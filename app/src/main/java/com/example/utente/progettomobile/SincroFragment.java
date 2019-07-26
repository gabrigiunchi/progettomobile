package com.example.utente.progettomobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import com.dropbox.chooser.android.DbxChooser;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import cryptography.MyEncryptor;
import database.DatabaseManager;
import database.DatabaseManagerImpl;
import model.AccountManager;
import model.SynchronizationService;
import model.interfaces.Element;

/**
 * Created by Utente on 27/05/2016.
 */
public class SincroFragment extends Fragment {

    public static final int DBX_CHOOSER_REQUEST = 1;
    private static final int PENDING_REQUEST_DROPBOX = 1;
    private static final int PENDING_REQUEST_ACCOUNT = 2;

    private RadioGroup account;
    private int previousRadio;
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private DbxChooser mChooser;
    private SharedPreferences preferences;
    private Switch backgroundSyncSwitch;
    private Switch wifiOnlySwitch;
    private int pendingRequest;
    private Button changeDropboxFileButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_sincro, container, false);

        this.preferences = getActivity().getSharedPreferences(getString(R.string.preferences_name),
                Context.MODE_PRIVATE);

        this.wifiOnlySwitch = (Switch)view.findViewById(R.id.wifi_only_sincronization_switch);
        this.wifiOnlySwitch.setChecked(preferences.getBoolean(getString(R.string.synchronizationWifiOnly), false));
        this.wifiOnlySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(getString(R.string.synchronizationWifiOnly), isChecked).commit();
            }
        });

        this.backgroundSyncSwitch = (Switch)view.findViewById(R.id.backgroud_sync_switch);
        this.backgroundSyncSwitch.setChecked(preferences.getBoolean(getString(R.string.synchronizationInBackground), true));
        this.backgroundSyncSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                preferences.edit().putBoolean(getString(R.string.synchronizationInBackground), isChecked).commit();
            }
        });

        final SynchronizationService.SynchronizationMethod synchronizationMethod =
                SynchronizationService.SynchronizationMethod.valueOf(
                        preferences.getString(getString(R.string.synchronization_method_preferences_entry),
                                SynchronizationService.SynchronizationMethod.DISABLED.name()));

        // Nascondo il tasto "Cambia file su dropbox" se il metodo di sincronizzazione non è settato su DROPBOX
        this.changeDropboxFileButton = (Button)view.findViewById(R.id.changeDropboxFileButton);
        changeDropboxFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseFileOnDropbox();
            }
        });
        if(synchronizationMethod != SynchronizationService.SynchronizationMethod.DROPBOX) {
            changeDropboxFileButton.setVisibility(View.INVISIBLE);
        }

        this.account = (RadioGroup) view.findViewById(R.id.accountRadio);
        this.initRadioGroup(synchronizationMethod);
        account.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                final RadioButton radioButton = (RadioButton)view.findViewById(checkedId);
                // Controllo per risolvere problemi di bouncing
                if(previousRadio != checkedId && radioButton.isChecked()) {
                    switch (checkedId){
                        case (R.id.accountButton):
                            if(previousRadio != R.id.disabled) {
                                pendingRequest = PENDING_REQUEST_ACCOUNT;
                                confirmDIsableSynchronization();
                            } else {
                                loginWithAccount();
                            }
                            break;

                        case (R.id.dropboxButton):
                            if(previousRadio != R.id.disabled) {
                                pendingRequest = PENDING_REQUEST_DROPBOX;
                                confirmDIsableSynchronization();
                            } else {
                                loginWithDropbox();
                            }
                            break;

                        case (R.id.disabled):
                            confirmDIsableSynchronization();
                            break;

                        default: break;
                    }
                }
            }
        });

        return view;
    }

    private void encryptionKeyChanged(final String decryptionKey) {
        Log.d("applicazione", "chiave di criptazione cambiata");
        final DatabaseManager databaseManager = DatabaseManagerImpl.getDatabaseManager(getActivity().getApplicationContext());

        // Carico in memoria tutti gli elementi
        final List<Element> elements = databaseManager.getAllElements();

        // Cambio la chiave di crittografia
        try {
            String key = decryptionKey;
            if(key.length() > MyEncryptor.REQUIRED_KEY_LENGTH) {
                key = key.substring(0, MyEncryptor.REQUIRED_KEY_LENGTH);
            }
            MyEncryptor.init(key.getBytes());
            MyEncryptor.storeKey(getActivity().getApplicationContext());
        } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        // Aggiorno gli elementi del database utilizzando la nuova chiave di crittografia
        for(Element e : elements) {
            databaseManager.updateElement(e);
        }
    }

    private void initRadioGroup(final SynchronizationService.SynchronizationMethod synchronizationMethod) {
        switch (synchronizationMethod) {
            case ACCOUNT: this.previousRadio = R.id.accountButton; break;
            case DISABLED: this.previousRadio = R.id.disabled; break;
            case DROPBOX: this.previousRadio = R.id.dropboxButton; break;
        }
        this.initRadioGroup(this.previousRadio);
    }


    private void initRadioGroup(final int id) {
        this.previousRadio = id;
        this.account.check(id);

        if(id == R.id.dropboxButton) {
            this.changeDropboxFileButton.setVisibility(View.VISIBLE);
        } else {
            this.changeDropboxFileButton.setVisibility(View.INVISIBLE);
        }
    }

    private void loginWithAccount() {
        if(AccountManager.getLoggedUser(getActivity().getApplicationContext()) == null) {
            Toast.makeText(getActivity().getApplicationContext(), "Devi prima effettuare il login con un account!",
                    Toast.LENGTH_LONG).show();

            this.initRadioGroup(R.id.disabled);
        } else {
            this.synchronizationMethodSet(SynchronizationService.SynchronizationMethod.ACCOUNT);
        }
    }

    private void loginWithDropbox() {
        final String appKey = getString(R.string.dropbox_app_key);
        final String appSecret = getString(R.string.dropbox_app_secret);
        AppKeyPair appKeys = new AppKeyPair(appKey, appSecret);
        AndroidAuthSession session = new AndroidAuthSession(appKeys);
        this. mDBApi = new DropboxAPI<>(session);
        mDBApi.getSession().startOAuth2Authentication(getActivity());
    }

    public void onResume() {
        super.onResume();

        if (this.mDBApi != null && this.mDBApi.getSession().authenticationSuccessful()) {
            try {
                mDBApi.getSession().finishAuthentication();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // Memorizzo l'accesso token
                            final String accessToken = mDBApi.getSession().getOAuth2AccessToken();
                            final long id = mDBApi.accountInfo().uid;
                            final String key = MyEncryptor.transformIntoKey(id);
                            final String oldKey = new String(MyEncryptor.loadStoredKey(getActivity().getApplicationContext()));

                            if(!key.equals(oldKey)) {
                                encryptionKeyChanged(key);
                            }

                            preferences.edit().putString(getString(R.string.dropbox_access_token_preferences_entry), accessToken).commit();
                            Log.d("applicazione", "Login con dropbox effettuato, accesso token : " + accessToken);
                            mDBApi = null;
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chooseFileOnDropbox();
                                }
                            });
                        } catch (DropboxException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                // Chiedo all'utente di scegliere il file su dropbox
                //chooseFileOnDropbox();
            } catch (IllegalStateException e) {
                this.initRadioGroup(this.previousRadio);
                Log.d("applicazione", "Error authenticating", e);
            }
        } else {
            this.initRadioGroup(this.previousRadio);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DBX_CHOOSER_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                DbxChooser.Result result = new DbxChooser.Result(data);

                final String path = result.getLink().getPath();
                Log.d("applicazione", "Scelto file su dropbox: " + path + " " + result.getName());
                preferences.edit()
                        .putString(getString(R.string.dropbox_filepath_preferences_entry), path)
                        .commit();

                this.synchronizationMethodSet(SynchronizationService.SynchronizationMethod.DROPBOX);
            } else {
                this.initRadioGroup(this.previousRadio);
                Log.d("applicazione", "Operazione dropboxChooser annullata");
            }
        }
    }

    private void chooseFileOnDropbox() {
        this.mChooser = new DbxChooser(getString(R.string.dropbox_app_key));
        mChooser.forResultType(DbxChooser.ResultType.PREVIEW_LINK).launch(getActivity(), DBX_CHOOSER_REQUEST);
    }

    private void synchronizationMethodSet(final SynchronizationService.SynchronizationMethod synchronizationMethod) {
        this.preferences.edit()
                .putString(getString(R.string.synchronization_method_preferences_entry), synchronizationMethod.name())
                .commit();

        this.initRadioGroup(synchronizationMethod);

        if(synchronizationMethod == SynchronizationService.SynchronizationMethod.DISABLED) {
            getActivity().stopService(new Intent(getActivity(), SynchronizationService.class));
        } else {
            getActivity().startService(new Intent(getActivity(), SynchronizationService.class));
        }

        Log.d("applicazione", "Scelto metodo di sincronizzazione: " + synchronizationMethod.name());
    }


    public void confirmDIsableSynchronization() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Sei sicuro di voler disabilitare la sincronizzazione?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        synchronizationMethodSet(SynchronizationService.SynchronizationMethod.DISABLED);
                        switch (pendingRequest) {
                            case PENDING_REQUEST_ACCOUNT : loginWithAccount(); break;
                            case PENDING_REQUEST_DROPBOX: loginWithDropbox(); break;
                            default: break;
                        }
                        pendingRequest = 0;
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        initRadioGroup(previousRadio);
                    }
                });

        builder.create().show();
    }
}
