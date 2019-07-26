package model.interfaces;

/**
 * @author Gabriele Giunchi
 *
 * Interfacca di una carta di credito con le sue principali caratteristiche: pin, intestatario, numero carta, scadenza ecc..
 */
public interface CartaCredito extends Element {
	void setNumeroCarta(String numero);
	void setIntestatario(String intestatario);
	void setTipo(String tipo);
	void setCodiceVerifica(String codice);
	void setDataScadenza(String data);
	void setValidità(String validoDa);
	void setPin(String pin);
	void setNote(String note);
	String getNumeroCarta();
	String getIntestatario();
	String getTipo();
	String getCodiceVerifica();
	String getDataScadenza();
	String getValidità();
	String getPin();
	String getNote();
}
