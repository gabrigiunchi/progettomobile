package com.example.utente.progettomobile;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import database.DatabaseManagerImpl;
import model.interfaces.ContoBancario;
import model.interfaces.Element;

public class ViewBankAccountActivity extends BasicViewElementActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bank_account);
        this.setTitle("Conto bancario");

        // Recupero dall'intent l'id dell'elemento selezionato, quindi lo leggo dal database
        final Intent intent = getIntent();
        final int id = intent.getIntExtra("id", -1);
        super.favoriteButton = (ImageButton)findViewById(R.id.imageButton16);
        super.databaseManager = DatabaseManagerImpl.getDatabaseManager(getApplicationContext());
        super.elementMetadata = databaseManager.getElementMetadata(id);

        final ContoBancario contoBancario = (ContoBancario) elementMetadata.getElement();

        final TextView textView1 = (TextView) findViewById(R.id.textView33);
        final TextView textView2 = (TextView) findViewById(R.id.textView21);
        final TextView textView3 = (TextView) findViewById(R.id.textView37);
        final TextView textView4 = (TextView) findViewById(R.id.textView38);
        final TextView textView5 = (TextView) findViewById(R.id.textView42);
        final TextView textView6 = (TextView) findViewById(R.id.textView44);

        textView2.setText(contoBancario.getNomeConto());
        textView1.setText(contoBancario.getNomeBanca());
        textView3.setText(contoBancario.getTipo());
        textView4.setText(contoBancario.getNumeroConto());
        textView5.setText(contoBancario.getIBAN());
        textView6.setText(contoBancario.getPin());

        textView6.setOnClickListener(new View.OnClickListener(){
            boolean show = true;
            @Override
            public void onClick(View v) {
                if(show){
                    textView6.setTransformationMethod(null);
                    show = false;
                } else {
                    textView6.setTransformationMethod(new PasswordTransformationMethod());
                    show = true;
                }
            }
        });

        super.modifyButton = (ImageButton) findViewById(R.id.imageButton9);
        super.removeButton = (Button) findViewById(R.id.button8);
        super.favoriteButton = (ImageButton) findViewById(R.id.imageButton17);

        final List<Element> preferiti = this.databaseManager.getFavoritesElements();
        super.elementIsInFavorites = preferiti.contains(contoBancario);
        if(elementIsInFavorites){
            super.favoriteButton.setImageResource(R.drawable.button_star_selected);
        }

        super.initButtonListeners();
    }

}
