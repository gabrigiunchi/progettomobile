package com.example.utente.progettomobile;

import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import model.interfaces.CredenzialeAccesso;

public class ViewCredentialsActivity extends BasicViewElementActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_credentials);
        this.setTitle("Credenziale di accesso");

        final CredenzialeAccesso credenzialeAccesso = (CredenzialeAccesso) elementMetadata.getElement();

        final TextView textView1 = (TextView) findViewById(R.id.textView28);
        final TextView textView2 = (TextView) findViewById(R.id.textView29);
        final TextView textView3 = (TextView) findViewById(R.id.textView31);
        textView1.setText(credenzialeAccesso.getSite());
        textView2.setText(credenzialeAccesso.getUsername());
        textView3.setText(credenzialeAccesso.getPassword());

        textView3.setOnClickListener(new View.OnClickListener(){
            boolean show = true;
            @Override
            public void onClick(View v) {
                if(show){
                    textView3.setTransformationMethod(null);
                    show = false;
                } else {
                    textView3.setTransformationMethod(new PasswordTransformationMethod());
                    show = true;
                }
            }
        });

        super.modifyButton = (ImageButton) findViewById(R.id.imageButton8);
        super.removeButton = (Button) findViewById(R.id.button7);
        super.favoriteButton = (ImageButton) findViewById(R.id.imageButton16);

        if(elementIsInFavorites){
            super.favoriteButton.setImageResource(R.drawable.button_star_selected);
        }

        super.initButtonListeners();
    }

}
