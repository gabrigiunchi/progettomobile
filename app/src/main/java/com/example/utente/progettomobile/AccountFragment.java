package com.example.utente.progettomobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import cryptography.MyEncryptor;
import database.DatabaseManager;
import database.DatabaseManagerImpl;
import model.AccountManager;
import model.SynchronizationService;
import model.UrlConnectionAsyncTask;
import model.UtenteImpl;
import model.interfaces.Element;
import model.interfaces.Utente;

/**
 * Created by Utente on 27/05/2016.
 */
public class AccountFragment extends Fragment implements UrlConnectionAsyncTask.UrlConnectionListener {

    private static final int FACEBOOK_LOGIN = 1;
    private static final int TWITTER_LOGIN = 2;
    private static final int NORMAL_LOGIN = 3;

    private CallbackManager callbackManager;
    private LoginButton facebookLoginButton;
    private TwitterLoginButton loginButton;
    private ImageView profilePicture;
    private View v;
    private RelativeLayout layoutFb;
    private RelativeLayout layoutNonFb;
    private TextView userFb;
    private TextView userNonFb;
    private Button logout;
    private TableRow tableRow;
    private TableRow tableRow2;
    private int login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        v = view;
        layoutFb = (RelativeLayout) view.findViewById(R.id.loggato);
        layoutNonFb = (RelativeLayout) view.findViewById(R.id.loggatoNonFB);
        userFb = (TextView) view.findViewById(R.id.textView46);
        userNonFb = (TextView) view.findViewById(R.id.textView47);
        logout = (Button) view.findViewById(R.id.logout_button);

        this.callbackManager = CallbackManager.Factory.create();
        this.facebookLoginButton = (LoginButton) view.findViewById(R.id.facebook_login_button);
        facebookLoginButton.setFragment(this);

        facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                final AccessToken accessToken = loginResult.getAccessToken();
                getFacebookProfile(accessToken);
            }

            @Override
            public void onCancel() { }

            @Override
            public void onError(FacebookException exception) {
                exception.printStackTrace();
            }
        });

        loginButton = (TwitterLoginButton) view.findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                final long id = result.data.getUserId();
                final String username = result.data.getUserName();
                Log.d("twitter", id + " " + username);
                final Utente utente = new UtenteImpl();
                utente.setUsername(id + "");
                utente.setPassword(id + "");
                utente.setNome(username);
                utente.setCognome("");
                loginWithTwitter(utente);
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

        //Verifica se sei già loggato con Facebook all'apertura del fragment
        final Profile currentProfile = Profile.getCurrentProfile();
        /*if(currentProfile!=null){
            try {
                URL pictureURL = new URL(currentProfile.getProfilePictureUri(40,40).toString());
                accessoEffettuato(currentProfile.getFirstName(),pictureURL);
            } catch (MalformedURLException e) {
                accessoEffettuato(currentProfile.getFirstName(),null);
                e.printStackTrace();
            }
        }*/
        //Verifica se fai il logout di Facebook
        AccessTokenTracker fbTracker;
        fbTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken accessToken, AccessToken accessToken2) {
                if (accessToken2 == null) {
                    accessoTerminato();
                }
            }
        };

        //Implementazione per l'aggiunta di un account 1Password
        tableRow = (TableRow) v.findViewById(R.id.tableRow);
        tableRow.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onCreateDialog();
            }
        });

        final Utente utente = AccountManager.getLoggedUser(getActivity().getApplicationContext());
        TextView nomeUtente = (TextView) v.findViewById(R.id.nomeUtente);
        tableRow2 = (TableRow) v.findViewById(R.id.tableRow2);
        tableRow2.setClickable(false);

        if(utente != null){
            accessoEffettuatoSenzaFacebook(utente,"");
            /*tableRow2.setVisibility(View.VISIBLE);
            tableRow2.setClickable(true);
            nomeUtente.setText("Accedi con 1Password");
            tableRow2.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    accessoEffettuatoSenzaFacebook(utente.getUsername(),"");
                }
            });*/
        }

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccountManager.reset(getActivity().getApplicationContext());
                accessoTerminatoSenzaFacebook();
            }
        });

        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10){//se ho aggiunto un account 1Password
            if (resultCode == Activity.RESULT_OK) {
                //salvo localmente username e foto
                saveUsername(data.getStringExtra("name"),data.getStringExtra("image"));
                AccountManager.setFlag(true);
                //Refresho il framgment
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this).attach(this).commit();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                //
            }
        } else {
            //se faccio il login con Facebook o Twitter
            loginButton.onActivityResult(requestCode, resultCode, data);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getFacebookProfile(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(accessToken,
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        loginWithFacebook(object);
                    }
                });
        final Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,email,cover,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void loginWithTwitter(final Utente utente) {
        this.login = TWITTER_LOGIN;
        final Bundle data = new Bundle();
        data.putString("username", utente.getUsername());
        data.putString("password", utente.getPassword());
        data.putString("first_name", utente.getNome());
        data.putString("last_name", utente.getCognome());
        data.putString("facebook_login", "true");

        try {
            new UrlConnectionAsyncTask(new URL(getString(R.string.login_url)), this, getActivity().getApplicationContext())
                    .execute(data);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void loginWithFacebook(final JSONObject jsonObject) {
        this.login = FACEBOOK_LOGIN;
        try {
            final String id = jsonObject.getString("id");

            final Utente utente = new UtenteImpl();
            utente.setUsername(id);
            utente.setPassword(id);
            utente.setNome(jsonObject.getString("first_name"));
            utente.setCognome(jsonObject.getString("last_name"));
            utente.setPictureURL(jsonObject.getJSONObject("picture").getJSONObject("data").getString("url"));

            final Bundle data = new Bundle();
            data.putString("username", id);
            data.putString("facebook_login", "true");
            data.putString("password", id);
            data.putString("extra", jsonObject.toString());

            new UrlConnectionAsyncTask(new URL(getString(R.string.login_url)), this, getActivity().getApplicationContext())
                    .execute(data);
        } catch (JSONException |MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void encryptionKeyChanged(final String decryptionKey) {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
        }).start();
    }

    @Override
    public void handleResponse(JSONObject response, Bundle extra) {
        if(response.length() != 0) {
            Log.d("applicazione", response.toString());
            try {
                final int code = response.getInt("code");

                if(code == LOGIN_SUCCESS) {
                    // Recupero dalla risposta del server la chiave di criptazione.
                    String decryptionKey = response.getJSONObject("extra").getString("hash_key");
                    if(decryptionKey.length() > MyEncryptor.REQUIRED_KEY_LENGTH) {
                        decryptionKey = decryptionKey.substring(0, MyEncryptor.REQUIRED_KEY_LENGTH);
                    }

                    // Controllo se è cambiata la chiave
                    final String lastKey = new String(MyEncryptor.loadStoredKey(getActivity().getApplicationContext()));
                    if(!decryptionKey.equals(lastKey)) {
                        // Cambio la chiave e aggiorno i dati sul database
                        this.encryptionKeyChanged(decryptionKey);
                    }

                    final Utente utente = new UtenteImpl(response.getJSONObject("extra").getJSONObject("utente"));

                    switch(this.login) {
                        case NORMAL_LOGIN: this.accessoEffettuatoSenzaFacebook(utente, ""); break;
                        case FACEBOOK_LOGIN:
                            URL url = null;
                            try {
                                url = new URL(utente.getPictureURL());
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            }
                            this.accessoEffettuato(utente.getNome(), url);
                            break;
                        case TWITTER_LOGIN:
                            final String nome = utente.getNome() == null || utente.getNome().isEmpty()? utente.getUsername() : utente.getNome();
                            this.accessoEffettuato(nome, null);
                        default: break;
                    }
                    AccountManager.saveUser(utente, getActivity().getApplicationContext());
                } else if(code == LOGIN_FAILED) {
                    Toast.makeText(getActivity().getApplicationContext(), "Username e/o password errati", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Errore sconosciuto, riprovare", Toast.LENGTH_LONG).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getActivity().getApplicationContext(), "Errore durante il login", Toast.LENGTH_LONG).show();
            }

        } else {
            Log.d("applicazione", "Errore durante il login");
            Toast.makeText(getActivity().getApplicationContext(), "Errore durante il login", Toast.LENGTH_LONG).show();
        }
    }

    private void accessoEffettuato(String firstName, URL pictureURL) {
        layoutFb.setVisibility(View.VISIBLE);
        userFb.setText("Ciao, "+firstName+"!");
        if(pictureURL != null){
            new DownloadPicture().execute(pictureURL);
        }
        this.facebookLoginButton = (LoginButton) v.findViewById(R.id.facebook_login_button2);
        tableRow.setClickable(false);
        tableRow2.setClickable(false);
    }

    private void accessoTerminato() {
        layoutFb.setVisibility(View.INVISIBLE);
        this.facebookLoginButton = (LoginButton) v.findViewById(R.id.facebook_login_button);
        tableRow.setClickable(true);
        tableRow2.setClickable(true);
    }

    private void accessoEffettuatoSenzaFacebook(final Utente utente, String image) {
        layoutNonFb.setVisibility(View.VISIBLE);
        final String name = utente.getNome() == "" | utente.getNome() == null? utente.getUsername() : utente.getNome();
        userNonFb.setText("Ciao, "+name+"!");
        if(image != null){
            profilePicture = (ImageView) v.findViewById(R.id.profilePicture2);
            profilePicture.setImageBitmap(BitmapFactory
                    .decodeFile(image));
        }
        tableRow.setClickable(false);
        tableRow2.setClickable(false);
        AccountManager.setFlag(true);
    }

    private void accessoTerminatoSenzaFacebook() {
        layoutNonFb.setVisibility(View.INVISIBLE);
        tableRow.setClickable(true);
        tableRow2.setClickable(true);
        AccountManager.setFlag(false);

        final SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.preferences_name),
                Context.MODE_PRIVATE);


        // Se il metodo di sincronizzazione è settato su ACCOUNT devo disattivare la sincronizzazione
        final SynchronizationService.SynchronizationMethod synchronizationMethod = SynchronizationService.SynchronizationMethod.valueOf(
                preferences.getString(getString(R.string.synchronization_method_preferences_entry)
                        , SynchronizationService.SynchronizationMethod.DISABLED.name()));

        if(synchronizationMethod == SynchronizationService.SynchronizationMethod.ACCOUNT) {
            getActivity().stopService(new Intent(getActivity(), SynchronizationService.class));
            preferences.edit().putString(getString(R.string.synchronization_method_preferences_entry),
                    SynchronizationService.SynchronizationMethod.DISABLED.name()).commit();
        }
    }

    private void saveUsername(final String nome, final String image) {
        final SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        preferences.edit().putString("username_account", nome).apply();
        preferences.edit().putString("image_account", image).apply();
    }

    public void onCreateDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.dialog_signin, null))
                // Add action buttons
                .setPositiveButton("Accedi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog d = (Dialog) dialog;
                        EditText user = (EditText) d.findViewById(R.id.loginUsername);
                        EditText pass = (EditText) d.findViewById(R.id.loginPassword);
                        String username = user.getText().toString();
                        String password = pass.getText().toString();

                        checkLogin(username, password);
                    }
                })
                .setNegativeButton("Registrati", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getActivity(),AddAccountActivity.class);
                        startActivityForResult(intent,10);
                    }
                });
        builder.create().show();
    }

    private void checkLogin(String username, String password) {
        this.login = NORMAL_LOGIN;
        final Bundle data = new Bundle();
        data.putString("username", username);
        data.putString("password", password);
        try {
            new UrlConnectionAsyncTask(new URL(getString(R.string.login_url)), this, getActivity().getApplicationContext()).execute(data);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private class DownloadPicture extends AsyncTask<URL, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(URL... params) {
            try {
                return getFacebookProfilePicture(params[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Bitmap result) {
            profilePicture = (ImageView) v.findViewById(R.id.profilePicture);
            profilePicture.setImageBitmap(result);
        }

        private Bitmap getFacebookProfilePicture(URL url) throws IOException {
            Bitmap bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            return bitmap;
        }
    }
}
