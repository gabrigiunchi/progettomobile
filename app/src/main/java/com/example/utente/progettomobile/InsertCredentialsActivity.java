package com.example.utente.progettomobile;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import model.CredenzialeAccessoImpl;
import model.interfaces.CredenzialeAccesso;

/**
 * Created by Utente on 13/05/2016.
 */
public class InsertCredentialsActivity extends BasicInsertElementActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);
        this.setTitle("Nuova credenziale d'accesso");

        final CredenzialeAccesso credenzialeAccesso = elementMetadata == null? new CredenzialeAccessoImpl()
                : databaseManager.getCredenzialeAccesso(elementMetadata.getElement().getID());

        final EditText editText1 = (EditText) findViewById(R.id.editText4);
        editText1.setText(credenzialeAccesso.getSite());

        final EditText editText2 = (EditText) findViewById(R.id.editText5);
        editText2.setText(credenzialeAccesso.getUsername());

        final EditText editText3 = (EditText) findViewById(R.id.editText6);
        editText3.setText(credenzialeAccesso.getPassword());

        final EditText editText4 = (EditText) findViewById(R.id.editText7);
        editText4.setKeyListener(null);
        editText4.setFocusable(false);

        final ProgressBar mProgress = (ProgressBar) findViewById(R.id.progressBar);
        mProgress.setProgress(0);
        mProgress.setMax(4);

        editText3.addTextChangedListener(new TextWatcher() {
            boolean flag1 = false;
            boolean flag2 = false;
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                editText4.setText(s);
                if(editText3.getText().length()==0){
                    mProgress.setProgress(0);
                }
                if(editText3.getText().length()>0 && editText3.getText().length()<7) {
                    mProgress.setProgress(1);
                }
                if(editText3.getText().length()>8) {
                    mProgress.setProgress(2);
                }
                for(int i=0;i<editText3.getText().length();i++){
                    if((int)editText3.getText().charAt(i)>47&&(int)editText3.getText().charAt(i)<58){
                        flag1=true;
                        break;
                    }
                }
                for(int i=0;i<editText3.getText().length();i++){
                    if((int)editText3.getText().charAt(i)>32 &&(int)editText3.getText().charAt(i)<48){
                        flag2=true;
                        break;
                    }
                }
                if(editText3.getText().length()>8 && (flag1 || flag2)) {
                    mProgress.setProgress(3);
                }
                if(editText3.getText().length()>8 && flag1 && flag2) {
                    mProgress.setProgress(4);
                }
            }
        });

        super.saveButton = (ImageButton) findViewById(R.id.imageButton3);
        super.saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(editText3.getText().length()>3){
                    String sito = editText1.getText().toString();
                    String username = editText2.getText().toString();
                    String password = editText3.getText().toString();

                    credenzialeAccesso.setSite(sito);
                    credenzialeAccesso.setUsername(username);
                    credenzialeAccesso.setPassword(password);

                    saveElement(credenzialeAccesso);

                } else {
                    Toast.makeText(getApplicationContext(),"Inserisci correttamente tutti i campi", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}

