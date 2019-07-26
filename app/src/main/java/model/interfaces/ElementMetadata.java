package model.interfaces;

import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Gabriele Giunchi
 *
 * Un ElementMetadata contiene le informazioni riguardo lo stato di un elemento in modo da poter
 * gestire la sincronizzazione di collezione di elementi tra dispositivi diversi.
 * Ogni ElementMetadata contiene l'id dell'elemento associato, lo stato dell'elemento
 * (Aggiunto, rimosso, modificato, sincronizzato. Vedi ElementState) e la data di ultima modifica.
 * Con queste informazioni è possibile costruire un algoritmo di sincronizzazione simile a quelli implementati da
 * altri servizi come Dropbox, Mercurial, Git ecc..
 */
public interface ElementMetadata extends Comparable<ElementMetadata>, Parcelable {
	void setID(int id);
	void setLastModified(long lastModified);
	void setState(ElementState state);
	void modifyElement(Element element);
	Element getElement();
	ElementState getState();
	long getLastModified();
	int getID();
	JSONObject generateJSON() throws JSONException;


	/**
	 * @author Gabriele Giunchi
	 *
	 * Enumerazione che rappresenta lo stato attuale di un elemento (modificato, aggiornato, aggiunto o sincronizzato).
	 */
	public enum ElementState {
		ADDED,
		REMOVED,
		MODIFIED,
		SYNCHRONIZED
	}
}
