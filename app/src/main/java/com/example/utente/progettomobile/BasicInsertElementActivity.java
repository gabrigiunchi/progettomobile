package com.example.utente.progettomobile;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;

import database.DatabaseManager;
import database.DatabaseManagerImpl;
import database.DatabaseTable;
import model.ElementMetadataImpl;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;
import utility.DatabaseUtilities;

/**
 * Created by Gabri on 27/05/2016.
 * Acvitity di base per l'inserimento di un elemento.
 * Possiede il metodo saveElement che permette l'aggiunta di un elemento e l'aggiornamento dei suoi attributi.
 * Il metodo si preoccupa di aggiungere/aggiornare la tupla nel database
 */
public abstract class BasicInsertElementActivity extends AppCompatActivity {

    protected ElementMetadata elementMetadata;
    protected int metadataId;
    protected DatabaseManager databaseManager;
    protected ImageButton saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        this.databaseManager = DatabaseManagerImpl.getDatabaseManager(getApplicationContext());

        final Bundle bundle = intent.getParcelableExtra("extra");
        if(bundle != null) {
            this.elementMetadata = bundle.getParcelable("element");
        }
    }


    private void updateElement(final ElementMetadata elementMetadata, final Element element) {
        elementMetadata.modifyElement(element);
        databaseManager.updateElement(element);
        databaseManager.updateElementMetadata(elementMetadata);
        Log.d("applicazione", "Aggiornato metadata " + element.getID());
    }

    private int addElement(final Element element) {
        final DatabaseTable table = DatabaseUtilities.findTableByCategory(element.getCategory());
        final int id = databaseManager.getMaxID(table) + 1;
        element.setID(id);

        // Creo elementMetadata
        this.elementMetadata = new ElementMetadataImpl(element);
        elementMetadata.setID(databaseManager.getMaxID(DatabaseTable.element_metadata) + 1);

        // Aggiungo al database
        this.databaseManager.insertElementMetadata(elementMetadata);
        this.databaseManager.insertElement(element);

        Log.d("applicazione", "Aggiungo elemento " + id);

        return elementMetadata.getID();
    }

    protected void saveElement(final Element element) {
        if(this.elementMetadata == null) {
            this.addElement(element);
        } else {
            this.updateElement(elementMetadata, element);
        }

        // Richiamo l'activity
        Intent intent = new Intent();
        switch (element.getCategory()) {
            case cartaCredito: intent = new Intent(this, ViewCreditCardActivity.class); break;
            case codiceFiscale: intent = new Intent(this, ViewPersonalCodeActivity.class); break;
            case contoBancario: intent = new Intent(this, ViewBankAccountActivity.class); break;
            case credenzialeAccesso: intent = new Intent(this, ViewCredentialsActivity.class); break;
        }

        final Bundle bundle = new Bundle();
        bundle.putParcelable("element", this.elementMetadata);
        intent.putExtra("extra", bundle);
        startActivity(intent);
    }
}
