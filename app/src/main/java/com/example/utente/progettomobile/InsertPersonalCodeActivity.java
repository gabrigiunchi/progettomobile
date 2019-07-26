package com.example.utente.progettomobile;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import model.CodiceFiscaleImpl;
import model.interfaces.CodiceFiscale;

/**
 * Created by Utente on 13/05/2016.
 */
public class InsertPersonalCodeActivity extends BasicInsertElementActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_code);
        this.setTitle("Nuovo codice fiscale");

        final CodiceFiscale codiceFiscale = super.elementMetadata == null ? new CodiceFiscaleImpl() :
                super.databaseManager.getCodiceFiscale(elementMetadata.getElement().getID());


        final EditText editText1 = (EditText) findViewById(R.id.editText14);
        editText1.setText(codiceFiscale.getNome());
        final EditText editText2 = (EditText) findViewById(R.id.editText15);
        editText2.setText(codiceFiscale.getDataNascita());
        final EditText editText3 = (EditText) findViewById(R.id.editText16);
        editText3.setText(codiceFiscale.getNumero());

        super.saveButton = (ImageButton) findViewById(R.id.imageButton5);
        super.saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                    final String nome = editText1.getText().toString();
                    final String data = editText2.getText().toString();
                    final String codice = editText3.getText().toString().toUpperCase();

                    codiceFiscale.setNome(nome);
                    codiceFiscale.setDataNascita(data);
                    codiceFiscale.setNumero(codice);

                    saveElement(codiceFiscale);
            }
        });
    }
}
