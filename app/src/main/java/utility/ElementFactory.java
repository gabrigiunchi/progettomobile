package utility;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

import model.CartaCreditoImpl;
import model.Category;
import model.CodiceFiscaleImpl;
import model.ContoBancarioImpl;
import model.CredenzialeAccessoImpl;
import model.interfaces.Element;

/**
 * @author Gabriele Giunchi
 * Classe che aderisce al pattern static factory per la creazione di oggetti di tipo Element
 * a partire da Parcel e Json
 */
public final class ElementFactory {

	/**
	 * Crea un oggetto di una sottoclasse di Element, dato un oggetto Parcel.
	 * @param parcel
	 * @return
     */
	public static Element create(final Parcel parcel) {
		final Category category = Category.valueOf(parcel.readString());

		switch (category) {
			case cartaCredito: return CartaCreditoImpl.CREATOR.createFromParcel(parcel);
			case codiceFiscale: return CodiceFiscaleImpl.CREATOR.createFromParcel(parcel);
			case contoBancario: return ContoBancarioImpl.CREATOR.createFromParcel(parcel);
			case credenzialeAccesso: return CredenzialeAccessoImpl.CREATOR.createFromParcel(parcel);
		}

		return null;
	}

	/**
	 * Restituisce un oggetto sottoclasse di Element dato un oggetto Json
	 * @param json
	 * @return
	 * @throws JSONException
     */
	public static Element create(final JSONObject json) throws JSONException{
		final Category category;

		try {
			category = Category.valueOf(json.getString("category"));
		} catch (JSONException e) {
			e.printStackTrace();
			return null;
		}

		switch(category) {
			case cartaCredito: return new CartaCreditoImpl(json);
			case contoBancario: return new ContoBancarioImpl(json);
			case credenzialeAccesso: return new CredenzialeAccessoImpl(json);
			case codiceFiscale: return new CodiceFiscaleImpl(json);
			default: return null;
		}
	}
}
