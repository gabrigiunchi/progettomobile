package com.example.utente.progettomobile;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import model.interfaces.CartaCredito;

public class ViewCreditCardActivity extends BasicViewElementActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_credit_card);
        this.setTitle("Carta di credito");

        final CartaCredito cartaCredito = (CartaCredito)super.elementMetadata.getElement();

        final TextView textView1 = (TextView) findViewById(R.id.textView33);
        final TextView textView2 = (TextView) findViewById(R.id.textView21);
        final TextView textView3 = (TextView) findViewById(R.id.textView37);
        final TextView textView4 = (TextView) findViewById(R.id.textView38);
        final TextView textView5 = (TextView) findViewById(R.id.textView42);
        final TextView textView6 = (TextView) findViewById(R.id.textView43);
        final TextView textView7 = (TextView) findViewById(R.id.textView44);
        textView2.setText(cartaCredito.getNumeroCarta());
        textView1.setText(cartaCredito.getIntestatario());
        textView3.setText(cartaCredito.getValidità());
        textView4.setText(cartaCredito.getDataScadenza());
        textView5.setText(cartaCredito.getTipo());
        textView6.setText(cartaCredito.getCodiceVerifica());
        textView7.setText(cartaCredito.getPin());


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

        textView7.setOnClickListener(new View.OnClickListener(){
            boolean show = true;
            @Override
            public void onClick(View v) {
                if(show){
                    textView7.setTransformationMethod(null);
                    show = false;
                } else {
                    textView7.setTransformationMethod(new PasswordTransformationMethod());
                    show = true;
                }
            }
        });

        super.modifyButton = (ImageButton) findViewById(R.id.imageButton9);
        super.removeButton = (Button) findViewById(R.id.button6);
        super.favoriteButton = (ImageButton) findViewById(R.id.imageButton15);
        if(elementIsInFavorites){
            super.favoriteButton.setImageResource(R.drawable.button_star_selected);
        }

        super.initButtonListeners();
    }

}
