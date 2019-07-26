package com.example.utente.progettomobile;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import model.AccountManager;
import model.UrlConnectionAsyncTask;

/**
 * Created by Utente on 02/06/2016.
 */
public class AddAccountActivity extends AppCompatActivity implements UrlConnectionAsyncTask.UrlConnectionListener {

    private static final int USER_ADDED_SUCCESS = 1;
    private static final int USER_ADDED_FAIL = -5;

    private ImageView imgView;
    private String imgDecodableString = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        final Button button = (Button) findViewById(R.id.button);
        final ImageButton loadImage = (ImageButton) findViewById(R.id.loadImage);
        final EditText accountName = (EditText) findViewById(R.id.accountName);
        final EditText accountSurname = (EditText) findViewById(R.id.accountSurname);
        final EditText accountUsername = (EditText) findViewById(R.id.accountUsername);
        final EditText accountPassword = (EditText) findViewById(R.id.accountPassword);
        imgView = (ImageView) findViewById(R.id.imgProfile);


        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    URL url = new URL(getString(R.string.add_user_user));

                    final Bundle data = new Bundle();
                    data.putString("username", accountUsername.getText().toString());
                    data.putString("password", accountPassword.getText().toString());
                    data.putString("nome", accountName.getText().toString());
                    data.putString("cognome", accountSurname.getText().toString());
                    data.putString("imageURL", imgDecodableString);
                    data.putString("email", "email");
                    data.putString("facebook_login", "false");

                    new UrlConnectionAsyncTask(url, AddAccountActivity.this, getApplicationContext()).execute(data);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, 1);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1 && resultCode == Activity.RESULT_OK
                    && null != data) {

                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                this.imgView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));

            } else {
                Toast.makeText(this, "Errore di caricamento",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Errore di caricamento", Toast.LENGTH_LONG)
                    .show();
        }

    }

    @Override
    public void handleResponse(JSONObject response, Bundle extra) {
        try {
            final int code = response.getInt("code");

            if(code == USER_ADDED_SUCCESS) {
                this.finish();
            } else if(code == USER_ADDED_FAIL) {
                Toast.makeText(getApplicationContext(),"Username già presente",Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(),"Errore imprevisto, riprovare",Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(),"Errore imprevisto, riprovare",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
