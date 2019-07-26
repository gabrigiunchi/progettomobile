package model;

import com.example.utente.progettomobile.R;

/**
 * Created by Utente on 27/05/2016.
 */
public enum Setting {

    account("Account 1Password", R.drawable.account_icon),
    protezione("Protezione", R.drawable.protection_icon),
    sincro("Sincronizzazione", R.drawable.sync_icon);

    private final String categoryFullName;
    private final int imageID;

    private Setting(final String nome, final int id) {
        this.categoryFullName = nome;
        this.imageID = id;
    }

    /**
     * Restituisce il nome completo di una categoria, es: l'enum cartaCredito ha come nome completo "Carta di credito".
     * @return
     */
    public String getCategoryFullName() {
        return this.categoryFullName;
    }

    /**
     * Restituisce l'id dell'immagine associata all'enumerazione
     * @return
     */
    public int getImageID() {
        return this.imageID;
    }

}
