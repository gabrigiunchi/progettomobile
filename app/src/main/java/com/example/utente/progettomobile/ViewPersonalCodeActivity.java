package com.example.utente.progettomobile;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import model.interfaces.CodiceFiscale;

public class ViewPersonalCodeActivity extends BasicViewElementActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_personal_code);
        this.setTitle("Codice fiscale");

        final CodiceFiscale codiceFiscale = (CodiceFiscale)super.elementMetadata.getElement();

        final TextView textView1 = (TextView) findViewById(R.id.textView20);
        final TextView textView2 = (TextView) findViewById(R.id.textView15);
        final TextView textView3 = (TextView) findViewById(R.id.textView24);
        textView1.setText(codiceFiscale.getNome());
        textView2.setText(codiceFiscale.getDataNascita());
        textView3.setText(codiceFiscale.getNumero());

        super.modifyButton = (ImageButton) findViewById(R.id.imageButton7);
        super.removeButton = (Button) findViewById(R.id.button5);
        super.favoriteButton = (ImageButton) findViewById(R.id.imageButton14);

        if(elementIsInFavorites){
            super.favoriteButton.setImageResource(R.drawable.button_star_selected);
        }

        super.initButtonListeners();
    }

}
