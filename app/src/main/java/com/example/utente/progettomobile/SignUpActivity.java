package com.example.utente.progettomobile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Utente on 28/04/2016.
 */
public class SignUpActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });

    }

    private void checkPassword() {
        final EditText codice = (EditText) findViewById(R.id.editText2);
        final EditText conferma = (EditText) findViewById(R.id.editText3);

        final String password1 = codice.getText().toString();
        final String password2 = conferma.getText().toString();

        if(!password1.isEmpty() && password1.equals(password2)) {
            final Intent intent = new Intent();
            intent.putExtra("password", password1);
            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(SignUpActivity.this, "INSERISCI CODICI UGUALI", Toast.LENGTH_SHORT).show();
        }
    }
}
