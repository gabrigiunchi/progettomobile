package model;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import com.example.utente.progettomobile.R;

import org.json.JSONArray;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseManager;
import database.DatabaseManagerImpl;
import model.interfaces.DataSynchronizer;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;
import model.interfaces.ServerComunicationManager;
import model.interfaces.Utente;
import utility.Utilities;

import static model.SynchronizationService.SynchronizationMethod.ACCOUNT;
import static model.SynchronizationService.SynchronizationMethod.DISABLED;

/**
 * @author Gabriele Giunchi
 *
 * Service che si occupa della sincronizzazione dei dati in background.
 * I metodi di sincronizzazione disponibili sono riepilogati nell'enum SynchronizationMethod.
 * Dalle impostazione è possibile disattivare/limitare il lavoro di questo service, per esempio
 * si può impostare che la sincronizzazione background avvenga solo su rete wi-fi o disattivarla completamente
 */
public class SynchronizationService extends Service {

    public enum SynchronizationMethod {
        DROPBOX,
        ACCOUNT,
        DISABLED
    }

    private DatabaseManager databaseManager;
    private SynchronizationThread myThread;
    private MyBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        this.databaseManager = DatabaseManagerImpl.getDatabaseManager(getApplicationContext());
        Log.d("applicazione", "SynchronizationService creato");
    }

    @Override
    public void onDestroy() {
        Log.d("applicazione", "SynchronizationService distrutto");
        this.myThread.stopComputing();
        super.onDestroy();
    }

    @Override
    public boolean onUnbind(final Intent intent) {
        Log.d("applicazione", "SynchronizationService nella onUnbind");
        return false;
    }

    private boolean serviceCanStart() {
        if(this.myThread == null || !this.myThread.isRunning()) {
            return true;
        }

        return false;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startID) {
        Log.d("applicazione", "SynchrinizationService nella onStartCommand");

        // Viene effettuato un check sulle impostazioni per decidere se far partire il service o meno
        if(this.serviceCanStart()) {
            final SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);

            // Si preleva dalle SharedPreferences il metodo di sincronizzazione scelto
            final SynchronizationMethod synchronizationMethod = SynchronizationMethod
                    .valueOf(preferences.getString(getString(R.string.synchronization_method_preferences_entry), DISABLED.name()));

            Log.d("applicazione", "Metodo di sincronizzazione: " + synchronizationMethod.toString());

            // Se la sincronizzazione è disabilitata non proseguo
            if(synchronizationMethod != SynchronizationMethod.DISABLED) {
                final Bundle bundle = new Bundle();
                String source = "";
                String destination = "";

                /* In base al metodo di sincronizzazione scelto configuro gli indirizzi per l'upload e il download dei dati
                   così come altri parametri
                 */
                ServerComunicationManager serverComunicationManager = new SynchronizationWithServerManager();
                switch (synchronizationMethod) {
                    case DROPBOX:
                        source = preferences.getString(getString(R.string.dropbox_filepath_preferences_entry), "");
                        destination = source;
                        final String accessToken = preferences.getString(getString(R.string.dropbox_access_token_preferences_entry), "");
                        serverComunicationManager = new DropboxConnectionImpl(accessToken);
                        break;

                    case ACCOUNT:
                        source = getString(R.string.get_element_metadata_url);
                        destination = getString(R.string.sync_url);
                        final Utente loggedUser = AccountManager.getLoggedUser(getApplicationContext());
                        final String username = loggedUser == null? "" : loggedUser.getUsername();
                        final String password = loggedUser == null? "" : loggedUser.getPassword();
                        bundle.putString(SynchronizationWithServerManager.USERNAME_ENTRY, username);
                        bundle.putSerializable(SynchronizationWithServerManager.PASSWORD_ENTRY, password);
                        break;

                    default: break;
                }

                // Costruisco il bundle contenente le informazioni da passare al thread affidato alla sincronizzazione
                bundle.putString(SynchronizationThread.SOURCE__ENTRY, source);
                bundle.putString(SynchronizationThread.DESTINATION__ENTRY, destination);
                bundle.putString(SynchronizationThread.SYNCHONIZATION_METHOD_ENTRY, synchronizationMethod.name());

                this.myThread = new SynchronizationThread(serverComunicationManager, bundle);
                this.myThread.start();
            }
        }

        return Service.START_STICKY;
    }

    public void syncNow() {
        if(this.myThread != null) {
            this.myThread.syncNow();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("applicazione", "SynchronizationService nella onBind");
        if(this.binder == null) {
            this.binder = new MyBinder();
        }
        return this.binder;
    }

    /**
     * @author Gabriele Giunchi
     *
     * Thread usato da SynchronizationService per effettuare le operazioni di sincronizzazione long-running.
     *
     * So occupa di:
     *
     * - Fare il download dei dati dal server
     * - Effettuare la sincronizzazione con i dati locali
     * - Eseguire l'upload dei dati verso il server
     *
     * Per lavorare ha bisogno di alcuni parametri di configurazione:
     *
     * - Un oggetto di tipo ServerComunicationManager che userà per il download e l'upload dei dati
     * - Indirizzo di sorgente e destinazione dei dati (può essere un URL o l'indirizzo di un file su un server Dropbox, MEGA ecc)
     *   da inserire nel Bundle
     * - Il metodo di sincronizzazione usato, da inserire nel Bundle
     * Per conoscere le chiavi da utilizzare per inserire i parametri nel Bundle vedere le variabili statiche della classe
     *
     * Per non gravare troppo sulla batteria e sull'uso di rete cellulare implementa due meccanismi di controllo:
     *
     * - Se l'utente ha disattivato la sincronizzazione sotto la rete cellulare non viene eseguita alcuna operazione
     * - Quando non è possibile eseguire la sincronizzazione (caso sopra citato o se il dispositivo non è connesso ad internet)
     *   il thread entra in una modalità di risparmio energetico dove limita la sua frequenza di aggiornamento.
     *
     *   N.B : Non viene controllato se l'utente abbia disattivato o meno la sincronizzazione,
     *         si presuppone che tale controllo venga effettuato nei livelli superiori
     *
     *   Inoltre implementa un controllo per prevenire la perdita degli aggiornamenti:
     *   se durante l'upload dei dati si verifica un errore (perdita di connessione per esempio),
     *   i dati vengono salvati in un file locale e verrà tentato l'upload finchè non ha successo.
     *   Solo quando l'operazione viene eseguita correttamente si procede con la procedura normale di sincronizzazione
     */
    private class SynchronizationThread extends Thread {
        public static final String SOURCE__ENTRY = "source";
        public static final String DESTINATION__ENTRY = "destination";
        public static final String SYNCHONIZATION_METHOD_ENTRY = "synchronization_method";

        // Per salvare una lista di elementi da mandare al server nel caso di errore in upload
        private static final String SYNCHRONIZATION_CACHE = "synchronization_cache.bin";

        private static final int SYNC_PERIOD_DEFAULT = 10 * 1000;
        private static final int SLEEP_TIME_DEFAULT = 1000;
        /*
            Se non ci sono le condizioni per effettuare una sincronizzazione si entra in modalità risparmio energia
            dove viene aumentato il periodo di aggiornamento
         */
        private static final int SYNC_PERIOD_IN_LOW_ENERGY_MODE = 1000 * 60 * 1;

        private volatile boolean stop;
        private boolean isRunning;
        private final ServerComunicationManager serverComunicationManager;
        private final String source;
        private final String destination;
        private final SynchronizationMethod synchronizationMethod;
        private long lastUpdate ;
        private final Bundle options;
        private int syncFrequency; // Frequenza con la quale il thread effettua la sincronizzazione
        private int sleepTime; // Variabile per effettuare l'operazione di Thread.sleep()

        public SynchronizationThread(final ServerComunicationManager serverComunicationManager, final Bundle options) {
            this.serverComunicationManager = serverComunicationManager;
            this.source = options.getString(SOURCE__ENTRY);
            this.destination = options.getString(DESTINATION__ENTRY);
            this.synchronizationMethod = SynchronizationMethod.valueOf(options.getString(SYNCHONIZATION_METHOD_ENTRY
                    , DISABLED.name()));

            this.lastUpdate = 0;
            this.options = options;
            this.syncFrequency = SYNC_PERIOD_DEFAULT;
            this.sleepTime = SLEEP_TIME_DEFAULT;
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public void stopComputing() {
            Log.d("synchronizationService", "stop Thread");
            this.stop = true;
        }

        /**
         * Viene eseguita un'operazione di sincronizzazione immediatamente
         */
        public void syncNow() {
            this.lastUpdate = 0;
        }

        private boolean saveOutcomingData(final List<ElementMetadata> outcomingData) {
            try {
                final OutputStream outputStream = openFileOutput(SYNCHRONIZATION_CACHE, Context.MODE_PRIVATE);
                final JSONArray jsonArray = Utilities.elementMetadataListToJsonArray(outcomingData);
                outputStream.write(jsonArray.toString().getBytes());
                outputStream.close();
                return true;
            } catch (Exception e) {
                Log.e("synchronizationService", e.getMessage());
            }

            return false;
        }

        private List<ElementMetadata> getCachedOutcomingData() {
            try {
                final InputStream inputStream = openFileInput(SYNCHRONIZATION_CACHE);
                final String jsonString = Utilities.readStringFromStream(inputStream);
                final JSONArray jsonArray = new JSONArray(jsonString);
                final List<ElementMetadata> list = Utilities.JsonArrayToElementMetadataList(jsonArray);
                inputStream.close();
                return list;
            } catch (Exception e) {
                Log.e("synchronizationService", e.getMessage());
            }

            return new ArrayList<>();
        }

        private boolean clearCache() {
            return deleteFile(SYNCHRONIZATION_CACHE);
        }

        private boolean canWork() {
            // Nel caso non ci siano i pressupposti per eseguire una sincronizzazione entro in modalità di risparmio
            this.syncFrequency = SYNC_PERIOD_IN_LOW_ENERGY_MODE;

            /* Controllo la connessione per decidere se far lavorare o meno il thread
               Se la connessione non è attiva oppure l'utente ha disattivato la sincronizzazione
               sotto rete cellulare viene restituito false e il thread non effettua la comunicazione con il server
             */
            final ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnected();

            // Se activeNetwork == null significa che non c'è connessione
            if(activeNetwork == null || !isConnected) {
                Log.d("synchronizationService", "Error: non connesso");
                return false;
            }

            // Prelevo dalle SharedPreferences l'impostazione sulla sincronizzazione sotto rete cellulare
            final SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
            final boolean synchronizationWifiOnly = preferences.getBoolean(
                    getString(R.string.synchronizationWifiOnly), false);

            final int connectionType = activeNetwork.getType();

            /*
                Se l'utente ha disattivato la sincronizzazione sotto rete cellulare e attualmente
                il dispositivo non è connesso ad una rete wifi restituisco false
              */
            if(synchronizationWifiOnly && connectionType != ConnectivityManager.TYPE_WIFI) {
                Log.d("synchronizationService", "Error: utente ha disattivato sincronizzazione con rete cellulare");
                return false;
            }

            // I check sono andati a buon fine, esco dalla modalità di risparmio
            this.syncFrequency = SYNC_PERIOD_DEFAULT;

            return true;
        }

        @Override
        public void run() {
            this.isRunning = true;
            long actualTime;

            Log.d("synchronizationService", "SynchronizationThread: inizio computazione");
            while(!this.stop) {
                actualTime = System.currentTimeMillis();

                if((actualTime - lastUpdate) > this.syncFrequency) {
                    if(this.canWork()) {
                        /*
                            Se non ci sono dati che sono in sospeso e devono essere inviati procedo al download dei dati
                            dal server e inizio la procedura normale di sincronizzazione,
                            altrimenti provo ad inviare i dati in sospeso
                         */
                        if(this.getCachedOutcomingData().isEmpty()) {

                            // Download
                            final List<ElementMetadata> local = databaseManager.getAllElementMetadata();
                            List<ElementMetadata> server = new ArrayList<>();
                            try {
                                server = serverComunicationManager.download(source, this.options);
                            } catch (Exception e) {
                                Log.d("synchronizationService", e.getMessage());
                                e.printStackTrace();
                            }

                            Log.d("synchronizationService", "Ricevuti " + server.size() + " elementi dal server");

                            // Sincronizzo le due liste
                            final DataSynchronizer dataSynchronizer = new DatabaseSynchronizer(databaseManager, local, server);
                            List<ElementMetadata> outcomingData = synchronizationMethod == ACCOUNT?
                                    dataSynchronizer.getOutcomingChanges() : dataSynchronizer.getUpdatedList();

                            // Eseguo l'upload dei dati solo se c'è qualcosa da aggiornare sul server
                            if(dataSynchronizer.getOutcomingChanges().isEmpty()) {
                                Log.d("synchronizationService", "Nessun elemento da inviare al server");
                            } else {
                                Log.d("synchronizationService", "Upload di " + outcomingData.size() + " elementi");
                                try {
                                    serverComunicationManager.upload(outcomingData, destination, this.options);
                                    this.clearCache();
                                } catch (Exception e) {
                                    /* Se incontro un errore nell'upload dei dati salvo la lista che deve essere
                                       mandata al server localmente per poi essere inviata al prossimo tentativo
                                    */
                                    this.saveOutcomingData(outcomingData);
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            // Provo ad inviare i dati in sospeso
                            try {
                                final List<ElementMetadata> outcomingData = this.getCachedOutcomingData();
                                serverComunicationManager.upload(outcomingData, destination, this.options);
                                this.clearCache();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    lastUpdate = actualTime;
                } else {
                    int t = (int)((actualTime - lastUpdate) / 1000);
                    Log.d("synchronizationService", "SynchronizationService timesleep: " + t);
                }

                try {
                    sleep(this.sleepTime);
                } catch (InterruptedException e) {
                    Log.e("synchronizationService", e.getMessage());
                    e.printStackTrace();
                }
            }

            this.isRunning = false;
            Log.d("synchronizationService", "SynchronizationThread: termino computazione");
        }
    }

    public class MyBinder extends Binder {
        public SynchronizationService getService() {
            return SynchronizationService.this;
        }
    }
}