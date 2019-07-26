package model.interfaces;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di una credenziale d'accesso per siti internet, email ecc..
 */
public interface CredenzialeAccesso extends Element {
	void setSite(String site);
	void setUsername(String username);
	void setPassword(String password);
	void setNote(String note);
	String getSite();
	String getUsername();
	String getPassword();
	String getNote();
}
