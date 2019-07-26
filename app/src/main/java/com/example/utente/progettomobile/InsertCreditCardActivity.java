package com.example.utente.progettomobile;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import model.CartaCreditoImpl;
import model.interfaces.CartaCredito;

/**
 * Created by Utente on 13/05/2016.
 */
public class InsertCreditCardActivity extends BasicInsertElementActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);
        this.setTitle("Nuova carta di credito");

        final CartaCredito cartaCredito = super.elementMetadata == null ? new CartaCreditoImpl() :
                super.databaseManager.getCartaCredito(super.elementMetadata.getElement().getID());

        final EditText editText1 = (EditText) findViewById(R.id.editText8);
        editText1.setText(cartaCredito.getIntestatario());
        final EditText editText2 = (EditText) findViewById(R.id.editText9);
        editText2.setText(cartaCredito.getNumeroCarta());
        final EditText editText3 = (EditText) findViewById(R.id.editText10);
        editText3.setText(cartaCredito.getCodiceVerifica());
        final EditText editText4 = (EditText) findViewById(R.id.editText11);
        editText4.setText(cartaCredito.getValidità());
        final EditText editText5 = (EditText) findViewById(R.id.editText12);
        editText5.setText(cartaCredito.getDataScadenza());
        final EditText editText6 = (EditText) findViewById(R.id.editText13);
        editText1.setText(cartaCredito.getPin());
        final Button button1 = (Button) findViewById(R.id.button4);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        button1.setOnClickListener(new View.OnClickListener(){
            boolean show = true;
            @Override
            public void onClick(View v) {
                if(show){
                    editText6.setTransformationMethod(null);
                    button1.setText("HIDE");
                    show = false;
                } else {
                    editText6.setTransformationMethod(new PasswordTransformationMethod());
                    button1.setText("SHOW");
                    show = true;
                }
            }
        });

        super.saveButton = (ImageButton) findViewById(R.id.imageButton4);
        super.saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String nome = editText1.getText().toString();
                final String numeroCarta = editText2.getText().toString();
                final String CCV = editText3.getText().toString();
                final String validità = editText4.getText().toString();
                final String scandenza = editText5.getText().toString();
                final String PIN = editText6.getText().toString();
                final String tipo = spinner.getSelectedItem().toString();

                cartaCredito.setIntestatario(nome);
                cartaCredito.setNumeroCarta(numeroCarta);
                cartaCredito.setCodiceVerifica(CCV);
                cartaCredito.setValidità(validità);
                cartaCredito.setDataScadenza(scandenza);
                cartaCredito.setPin(PIN);
                cartaCredito.setTipo(tipo);

                saveElement(cartaCredito);
            }
        });
    }
}
