package com.example.utente.progettomobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import database.DatabaseManager;
import io.fabric.sdk.android.Fabric;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import cryptography.MyEncryptor;
import database.DatabaseManagerImpl;
import model.ContoBancarioImpl;
import model.CredenzialeAccessoImpl;
import model.ElementMetadataImpl;
import model.SynchronizationService;
import model.interfaces.ContoBancario;
import model.interfaces.CredenzialeAccesso;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

public class MainActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "tdjFMck3MS6H4L4jo9UfM5V1w";
    private static final String TWITTER_SECRET = "Fs6D9IpyCKrQLaXKzjmIMDeZumSZUr5XxYWmabisSzdODzeHTD";


    public static final int SIGNUP_PASSWORD_REQUEST_CODE = 100;
    private ImageView img_home;
    private ImageView img_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        setContentView(R.layout.activity_main);

        final ImageButton enter = (ImageButton) findViewById(R.id.button3);
        img_home = (ImageView) findViewById(R.id.home_img);
        img_logo = (ImageView) findViewById(R.id.home_logo);

        final SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        String password = preferences.getString(getString(R.string.principal_password_preferences_entry), "");

        /* Se è la prima volta che l'utente usa l'app :
           - Creo database
           - Creo nuova chiave di crittografia
           - Apro activity per inserire la password principale dell'applicazione

           Altrimenti inizializzo l'encryptor con la chiave salvata
         */
        if(password.isEmpty()) {
            startActivityForResult(new Intent(this, SignUpActivity.class), SIGNUP_PASSWORD_REQUEST_CODE);
        } else {
            final byte[] encryptionKey = MyEncryptor.loadStoredKey(getApplicationContext());
            try {
                MyEncryptor.init(encryptionKey);
            } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeyException e) {
                e.printStackTrace();
            }
        }

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterButtonPressed();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // L'utente ha registrato la password principale, creo database e genero chiave di criptazione
        if (requestCode == SIGNUP_PASSWORD_REQUEST_CODE && resultCode == RESULT_OK) {
            final String password = data.getStringExtra("password");
            getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE)
                    .edit()
                    .putString(getString(R.string.principal_password_preferences_entry), password)
                    .commit();

            Log.d("applicazione", "Password principale settata : " + password);

            Log.d("applicazione", "Genero nuova chiave di crittografia");
            try {
                final SecretKey secretKey = MyEncryptor.generateKey();
                MyEncryptor.init(secretKey);
                MyEncryptor.storeKey(getApplicationContext());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DatabaseManagerImpl.getDatabaseManager(getApplicationContext()).getDatabase();
                }
            }).start();
        }
    }

    private void enterButtonPressed() {
        final String password = getSharedPreferences(getString(R.string.preferences_name),
                Context.MODE_PRIVATE).getString(getString(R.string.principal_password_preferences_entry), "");

        final String tryPassword = ((EditText)findViewById(R.id.tryPassword)).getText().toString();

        if(password.equals(tryPassword)) {
            /* Avvio animazione prima di lanciare l'altra activity */
            final Animation translate = AnimationUtils.loadAnimation(this,R.anim.translate_upper);
            final Animation rotate = AnimationUtils.loadAnimation(this,R.anim.rotate_around_center_point);

            translate.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    img_home.setVisibility(View.INVISIBLE);
                    final Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    intent.putExtra("category","main");
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    MainActivity.this.finish();
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {
                }
            });

            rotate.setAnimationListener(new Animation.AnimationListener() {
                public void onAnimationEnd(Animation animation) {
                    img_logo.setVisibility(View.INVISIBLE);
                    img_home.setImageDrawable(getResources().getDrawable(R.drawable.one_password_home_rotate));
                    img_home.startAnimation(translate);
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {}
            });

            img_logo.startAnimation(rotate);

        } else {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point);
            animation.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationEnd(Animation animation) {
                    img_logo.setImageDrawable(getResources().getDrawable(R.drawable.one_password_logo_error));
                    Toast.makeText(getApplicationContext(), "Password errata", Toast.LENGTH_LONG).show();
                    changeImage();
                }
                public void onAnimationRepeat(Animation animation) {}
                public void onAnimationStart(Animation animation) {}

            });
            img_logo.startAnimation(animation);
        }
    }

    private void changeImage() {
        /*Dopo 3 secondi di attesa rimetto il logo del colore normale. */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                img_logo.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        img_logo.setImageDrawable(getResources().getDrawable(R.drawable.one_password_logo));
                    }
                }, 3000);

            }
        });
    }

}
