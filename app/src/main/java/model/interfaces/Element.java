package model.interfaces;

import android.os.Parcelable;
import org.json.JSONException;
import org.json.JSONObject;

import model.Category;

/**
 * @author Gabriele Giunchi
 * 
 * Interfaccia basilare di un elemento gestito dall'applicazione.
 * Element possiede un id e una categoria di appartenenza (vedi enum Category)
 */
public interface Element extends Parcelable {
	void setID(int id);
	int getID();
	Category getCategory();
	JSONObject generateJSON() throws JSONException;
	String getTitle();
}
