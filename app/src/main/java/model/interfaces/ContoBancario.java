package model.interfaces;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di un conto bancario
 */
public interface ContoBancario extends Element {
	void setNomeBanca(String nome);
	void setNomeConto(String conto);
	void setTipo(String tipo);
	void setNumeroConto(String numero);
	void setIBAN(String iban);
	void setPin(String pin);
	void setNote(String note);
	String getNomeBanca();
	String getNomeConto();
	String getTipo();
	String getNumeroConto();
	String getIBAN();
	String getPin();
	String getNote();
}
