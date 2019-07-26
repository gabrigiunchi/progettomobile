package model;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import cryptography.MyEncryptor;
import model.interfaces.CartaCredito;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di CartaCredito
 */
public class CartaCreditoImpl extends BaseElement implements CartaCredito{

	public static final Creator<CartaCredito> CREATOR = new Creator<CartaCredito>() {
		@Override
		public CartaCredito createFromParcel(Parcel source) {
			return new CartaCreditoImpl(source);
		}

		@Override
		public CartaCredito[] newArray(int size) {
			return new CartaCredito[size];
		}
	};

	private static final String JSON_TITOLARE = "intestatario";
	private static final String JSON_NUMERO = "numero";
	private static final String JSON_CCV = "codice_verifica";
	private static final String JSON_TIPO = "tipo";
	private static final String JSON_SCADENZA = "data_scadenza";
	private static final String JSON_PIN = "pin";
	private static final String JSON_VALIDITA = "valido_da";
	private static final String JSON_NOTE = "note";

	private String titolare;
	private String numero;
	private String ccv;
	private String tipo;
	private String dataScadenza;
	private String pin;
	private String validità;
	private String note;
	
	public CartaCreditoImpl() {
		super();
		this.titolare = "";
		this.numero = "";
		this.ccv = "";
		this.tipo = "";
		this.dataScadenza = "";
		this.pin = "";
		this.validità = "";
		this.note = "";
	}

	public CartaCreditoImpl(final JSONObject json) throws JSONException{
		super(json);
		this.titolare = json.getString(JSON_TITOLARE);
		this.numero = json.getString(JSON_NUMERO);
		try {
			this.ccv = MyEncryptor.decrypt(json.getString(JSON_CCV));
		} catch (Exception e) {
			e.printStackTrace();
			this.ccv = "";
		}
		try {
			this.pin = MyEncryptor.decrypt(json.getString(JSON_PIN));
		} catch (Exception e) {
			e.printStackTrace();
			this.pin = "";
		}

		this.tipo = json.getString(JSON_TIPO);
		this.dataScadenza = json.getString(JSON_SCADENZA);
		this.validità = json.getString(JSON_VALIDITA);

		this.note = json.getString(JSON_NOTE);
	}

	private CartaCreditoImpl(final Parcel parcel) {
		super(parcel);
		this.titolare = parcel.readString();
		this.numero = parcel.readString();

		try {
			this.ccv  = MyEncryptor.decrypt(parcel.readString());
		} catch (Exception e) {
			e.printStackTrace();
			this.ccv = "";
		}
		this.tipo = parcel.readString();
		this.dataScadenza = parcel.readString();
		try {
			this.pin  = MyEncryptor.decrypt(parcel.readString());
		} catch (Exception e) {
			e.printStackTrace();
			this.pin = "";
		}
		this.validità = parcel.readString();
		this.note = parcel.readString();
 	}

	@Override
	public Category getCategory() {
		return Category.cartaCredito;
	}

	@Override
	public String getTitle() { return this.numero; }

	@Override
	public JSONObject generateJSON() throws JSONException {
		final JSONObject o = super.generateJSON();

		String ccvCripted = "";
		String pinCripted = "";
		try {
			ccvCripted = MyEncryptor.encrypt(this.ccv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pinCripted = MyEncryptor.encrypt(this.ccv);
		} catch (Exception e) {
			e.printStackTrace();
		}

		o.put(JSON_TITOLARE, this.titolare)
				.put(JSON_NUMERO, this.numero)
				.put(JSON_CCV, ccvCripted)
				.put(JSON_TIPO, this.tipo)
				.put(JSON_SCADENZA, this.dataScadenza)
				.put(JSON_VALIDITA, this.validità)
				.put(JSON_PIN, pinCripted)
				.put(JSON_NOTE, this.note)
				.put("category", Category.cartaCredito.name());

		return o;
	}

	public void setNumeroCarta(String numero) { this.numero = numero; }

	@Override
	public void setIntestatario(String intestatario) {
		this.titolare = intestatario;
	}

	@Override
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	@Override
	public void setCodiceVerifica(String codice) {
		this.ccv = codice;
	}

	@Override
	public void setDataScadenza(String data) {
		this.dataScadenza = data;
	}

	@Override
	public void setValidità(String validoDa) {
		this.validità = validoDa;
	}

	@Override
	public void setPin(String pin) {
		this.pin = pin;
	}

	@Override
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getNumeroCarta() {
		return this.numero;
	}

	@Override
	public String getIntestatario() {
		return this.titolare;
	}

	@Override
	public String getTipo() {
		return this.tipo;
	}

	@Override
	public String getCodiceVerifica() {
		return this.ccv;
	}

	@Override
	public String getDataScadenza() {
		return this.dataScadenza;
	}

	@Override
	public String getValidità() {
		return this.validità;
	}

	@Override
	public String getPin() {
		return this.pin;
	}

	@Override
	public String getNote() {
		return this.note;
	}

	@Override
	public String toString() {
		return "CARTA CREDITO: Intestatario->" + this.titolare + " , numero->" + this.numero + " ,tipo->" + this.tipo;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		String ccvCrypted = "";
		String pinCrypted = "";
		try {
			ccvCrypted = MyEncryptor.encrypt(this.ccv);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			pinCrypted = MyEncryptor.encrypt(this.pin);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dest.writeString(this.titolare);
		dest.writeString(this.numero);
		dest.writeString(ccvCrypted);
		dest.writeString(this.tipo);
		dest.writeString(this.dataScadenza);
		dest.writeString(pinCrypted);
		dest.writeString(this.validità);
		dest.writeString(this.note);
	}
}
