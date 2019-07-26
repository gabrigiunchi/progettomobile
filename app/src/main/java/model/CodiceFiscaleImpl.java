package model;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import model.interfaces.CodiceFiscale;


/**
 * @author Gabriele Giunchi
 *
 * Implementazione di CodiceFiscale
 */
public class CodiceFiscaleImpl extends BaseElement implements CodiceFiscale {

	public static final Creator<CodiceFiscale> CREATOR = new Creator<CodiceFiscale>() {
		@Override
		public CodiceFiscale createFromParcel(Parcel source) {
			return new CodiceFiscaleImpl(source);
		}

		@Override
		public CodiceFiscale[] newArray(int size) {
			return new CodiceFiscale[size];
		}
	};

	private static final String JSON_NOME = "nome";
	private static final String JSON_NUMERO = "numero";
	private static final String JSON_NASCITA = "data_nascita";
	private static final String JSON_NOTE = "note";
	
	private String nome;
	private String dataNascita;
	private String numero;
	private String note;

	public CodiceFiscaleImpl() {
		super();
		this.nome = "";
		this.numero = "";
		this.dataNascita = "";
		this.note = "";
	}

	public CodiceFiscaleImpl(final JSONObject json) throws JSONException{
		super(json);
		this.nome = json.getString(JSON_NOME);
		this.numero = json.getString(JSON_NUMERO);
		this.dataNascita = json.getString(JSON_NASCITA);
		this.note = json.getString(JSON_NOTE);
	}

	private CodiceFiscaleImpl(final Parcel parcel) {
		super(parcel);
		this.nome = parcel.readString();
		this.dataNascita = parcel.readString();
		this.numero = parcel.readString();
		this.note = parcel.readString();
	}

	@Override
	public Category getCategory() {
		return Category.codiceFiscale;
	}

	@Override
	public String getTitle() { return this.nome; }

	@Override
	public JSONObject generateJSON() throws JSONException{
		final JSONObject o =  super.generateJSON();
		o.put(JSON_NOME, this.nome)
				.put("category", Category.codiceFiscale.name())
				.put(JSON_NUMERO, this.numero)
				.put(JSON_NASCITA,this.dataNascita)
				.put(JSON_NOTE, this.note);
		return o;
	}

	@Override
	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public void setNumero(String numero) {
		this.numero = numero;
	}

	@Override
	public void setDataNascita (String dataNascita) { this.dataNascita = dataNascita; }

	@Override
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getNome() {
		return this.nome;
	}

	@Override
	public String getNumero() {
		return this.numero;
	}

	@Override
	public String getDataNascita () { return this.dataNascita; }

	@Override
	public String getNote() {
		return this.note;
	}

	@Override
	public String toString() {
		return "CODICE FISCALE: Nome->" + this.nome + " , codice->" + this.numero;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(this.nome);
		dest.writeString(this.dataNascita);
		dest.writeString(this.numero);
		dest.writeString(this.note);
	}
}
