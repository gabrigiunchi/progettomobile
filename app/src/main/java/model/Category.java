package model;

import com.example.utente.progettomobile.R;

/**
 * @author Gabriele Giunchi
 *
 * Categoria di un elemento. Un elemento appartiene ad una ed una sola categoria.
 */
public enum Category {
	cartaCredito("Carta di credito", R.drawable.credit_cards_icon),
	contoBancario("Conto bancario", R.drawable.bank_account_icon),
	codiceFiscale("Codice fiscale", R.drawable.personal_code_icon),
	credenzialeAccesso("Credenziale di accesso", R.drawable.lock_icon);

	private final String categoryFullName;
	private final int imageID;

	private Category(final String nome, final int id) {
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


