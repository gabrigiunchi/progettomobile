package com.example.utente.progettomobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import database.DatabaseManagerImpl;
import model.ContoBancarioImpl;
import model.interfaces.ContoBancario;
import model.interfaces.ElementMetadata;

/**
 * Created by Utente on 13/05/2016.
 */
public class InsertBankAccountActivity extends BasicInsertElementActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_account);
        this.setTitle("Nuovo conto bancario");

        final ContoBancario contoBancario = elementMetadata == null? new ContoBancarioImpl()
                : databaseManager.getContoBancario(elementMetadata.getElement().getID());

        final EditText editText1 = (EditText) findViewById(R.id.editText17);
        editText1.setText(contoBancario.getNomeConto());
        final EditText editText2 = (EditText) findViewById(R.id.editText18);
        editText2.setText(contoBancario.getNomeBanca());
        final EditText editText3 = (EditText) findViewById(R.id.editText19);
        editText3.setText(contoBancario.getNumeroConto());
        final EditText editText4 = (EditText) findViewById(R.id.editText20);
        editText4.setText(contoBancario.getIBAN());
        final EditText editText5 = (EditText) findViewById(R.id.editText21);
        editText5.setText(contoBancario.getPin());

        final Button button1 = (Button) findViewById(R.id.button4);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner2);

        super.saveButton = (ImageButton) findViewById(R.id.imageButton6);
        super.saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String nomeConto = editText1.getText().toString();
                final String nomeBanca = editText2.getText().toString();
                final String numeroConto = editText3.getText().toString();
                final String iban = editText4.getText().toString();
                final String PIN = editText5.getText().toString();
                final String tipo = spinner.getSelectedItem().toString();

                contoBancario.setNomeBanca(nomeBanca);
                contoBancario.setNomeConto(nomeConto);
                contoBancario.setNumeroConto(numeroConto);
                contoBancario.setIBAN(iban);
                contoBancario.setPin(PIN);
                contoBancario.setTipo(tipo);

                saveElement(contoBancario);
            }
        });

        button1.setOnClickListener(new View.OnClickListener(){
            boolean show = true;
            @Override
            public void onClick(View v) {
                if(show){
                    editText5.setTransformationMethod(null);
                    button1.setText("HIDE");
                    show = false;
                } else {
                    editText5.setTransformationMethod(new PasswordTransformationMethod());
                    button1.setText("SHOW");
                    show = true;
                }
            }
        });
    }

}
