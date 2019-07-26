package com.example.utente.progettomobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import database.DatabaseManager;
import database.DatabaseManagerImpl;
import model.Category;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * Created by Gabri on 27/05/2016.
 * Activity di base per visualizzare un elemento ed effettuare operazioni di base sull'elemento stesso, in particolare:
 * - Modifica del file premendo l'apposito pulsante
 * - Rimozione ed aggiunta ai preferiti
 * - Eliminazione elemento
 */
public abstract class BasicViewElementActivity extends AppCompatActivity {

    protected DatabaseManager databaseManager;
    protected ElementMetadata elementMetadata;
    protected boolean elementIsInFavorites;
    protected ImageButton favoriteButton;
    protected ImageButton modifyButton;
    protected Button removeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.databaseManager = DatabaseManagerImpl.getDatabaseManager(getApplicationContext());
        final Bundle bundle = intent.getParcelableExtra("extra");
        this.elementMetadata = bundle.getParcelable("element");
        this.elementIsInFavorites = databaseManager.getFavoritesElements().contains(elementMetadata.getElement());
    }

    /**
     * Inizializza i listener per i bottoni di modifica, rimozione e preferiti.
     * N.B : chiamare questo metodo dopo aver inizializzato i bottoni
     */
    protected void initButtonListeners() {
        this.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeElement();
            }
        });

        this.modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyElement(elementMetadata.getElement().getCategory());
            }
        });

        this.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoritesButtonPressed();
            }
        });
    }

    protected void favoritesButtonPressed() {
        if(this.elementIsInFavorites) {
            this.databaseManager.removeFromFavorites(elementMetadata.getElement());
            this.elementIsInFavorites = false;
            this.favoriteButton.setImageResource(R.drawable.button_star_normal);
            Toast.makeText(getApplicationContext(),"Rimosso dai preferiti",Toast.LENGTH_SHORT).show();
        } else {
            this.databaseManager.addToFavorites(elementMetadata.getElement());
            this.elementIsInFavorites = true;
            this.favoriteButton.setImageResource(R.drawable.button_star_selected);
            Toast.makeText(getApplicationContext(),"Aggiunto ai preferiti!",Toast.LENGTH_SHORT).show();
        }
    }

    protected void modifyElement(final Category category) {
        Intent intent = new Intent();
        switch (category) {
            case cartaCredito: intent = new Intent(this, InsertCreditCardActivity.class); break;
            case codiceFiscale: intent = new Intent(this, InsertPersonalCodeActivity.class); break;
            case contoBancario: intent = new Intent(this, InsertBankAccountActivity.class); break;
            case credenzialeAccesso: intent = new Intent(this, InsertCredentialsActivity.class); break;
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable("element", elementMetadata);
        intent.putExtra("extra", bundle);
        startActivity(intent);
    }

    protected void removeElement() {
        final Element element = elementMetadata.getElement();
        this.databaseManager.removeFromFavorites(element);

        /*
            Se l'elemento è in stato ADDED posso rimuoverlo in modo definitivo poichè non c'è bisogno di sincronizzare
            l'informazione con altri dispositivi (in quanto gli altri dispositivi non tengono traccia di quel file)
         */
        if(elementMetadata.getState() == ElementMetadata.ElementState.ADDED) {
            databaseManager.removeElementMetadata(elementMetadata);
            this.databaseManager.removeElement(element);
        } else {
            elementMetadata.setState(ElementMetadata.ElementState.REMOVED);
            databaseManager.updateElementMetadata(elementMetadata);
        }

        Log.d("applicazione", "Eliminato elemento " + this.elementMetadata.getID());

        Intent intent = new Intent(this, MenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        try{
            if(getIntent().getStringExtra("onBackPressed").equals("preferiti")){
                Intent intent = new Intent(this, MenuActivity.class);
                intent.putExtra("categoria", "Pref");
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();

            final Intent intent = new Intent(this, MenuActivity.class);
            intent.putExtra("categoria", this.elementMetadata.getElement().getCategory().getCategoryFullName());
            startActivity(intent);
        }
    }
}
