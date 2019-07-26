package model.interfaces;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di un codice fiscale
 */
public interface CodiceFiscale extends Element {
	void setNome(String nome);
	void setNumero(String numero);
	void setDataNascita (String dataNascita);
	void setNote(String note);
	String getNome();
	String getNumero();
	String getDataNascita();
	String getNote();
}
