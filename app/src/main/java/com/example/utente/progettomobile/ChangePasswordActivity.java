package com.example.utente.progettomobile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Utente on 27/05/2016.
 */
public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        final Button button1 = (Button) findViewById(R.id.button9);
        final Button button2 = (Button) findViewById(R.id.button10);
        final TextView textView1 = (TextView) findViewById(R.id.textView14);
        final TextView textView2 = (TextView) findViewById(R.id.textView18);
        final TableRow t = (TableRow) findViewById(R.id.tableRow2);
        final EditText password = (EditText) findViewById(R.id.editText22);
        final EditText conferma = (EditText) findViewById(R.id.editText23);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pass = getSharedPreferences(getString(R.string.preferences_name),
                        Context.MODE_PRIVATE).getString(getString(R.string.principal_password_preferences_entry), "");
                final String tryPassword = password.getText().toString();

                if(pass.equals(tryPassword)) {
                    password.setText("");
                    textView1.setVisibility(View.INVISIBLE);
                    textView2.setVisibility(View.VISIBLE);
                    t.setVisibility(View.VISIBLE);
                    button1.setVisibility(View.INVISIBLE);
                    button2.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(getApplicationContext(), "Password errata", Toast.LENGTH_LONG).show();
                }

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = password.getText().toString();
                String s2 = conferma.getText().toString();

                if (s1.length() > 2) {
                    if(s1.equals(s2)){
                        savePassword(s1);
                        onBackPressed();
                        finish();
                        Toast.makeText(getApplicationContext(),"Password modificata",Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),"Password diverse",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Password troppo corta",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void savePassword(final String password) {
        final SharedPreferences preferences = getSharedPreferences(getString(R.string.preferences_name), Context.MODE_PRIVATE);
        preferences.edit().putString(getString(R.string.principal_password_preferences_entry), password).apply();
    }
}
