package model;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import cryptography.MyEncryptor;
import model.interfaces.CredenzialeAccesso;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di CredenzialeAccesso
 */
public class CredenzialeAccessoImpl extends BaseElement implements CredenzialeAccesso {

	public static final Creator<CredenzialeAccesso> CREATOR = new Creator<CredenzialeAccesso>() {
		@Override
		public CredenzialeAccesso createFromParcel(Parcel source) {
			return new CredenzialeAccessoImpl(source);
		}

		@Override
		public CredenzialeAccesso[] newArray(int size) {
			return new CredenzialeAccesso[size];
		}
	};

	private static final String JSON_SITO = "sito";
	private static final String JSON_USERNAME = "username";
	private static final String JSON_PASSWORD = "password";
	private static final String JSON_NOTE = "note";

	private String site;
	private String username;
	private String password;
	private String note;
	
	public CredenzialeAccessoImpl() {
		super();
		this.site = "";
		this.username = "";
		this.password = "";
		this.note = "";
	}

	public CredenzialeAccessoImpl(final JSONObject json) throws JSONException {
		super(json);
		this.site = json.getString(JSON_SITO);
		this.username = json.getString(JSON_USERNAME);
		this.password = json.getString(JSON_PASSWORD);
		try {
			this.password = MyEncryptor.decrypt(this.password);
		} catch (Exception e) {
			this.password = "";
			e.printStackTrace();
		}
		this.note = json.getString(JSON_NOTE);
	}

	private CredenzialeAccessoImpl(final Parcel parcel) {
		super(parcel);
		this.username = parcel.readString();
		try {
			this.password = MyEncryptor.decrypt(parcel.readString());
		} catch (Exception e) {
			e.printStackTrace();
			this.password = "";
		}
		this.site = parcel.readString();
		this.note = parcel.readString();
	}

	@Override
	public void setSite (String site) { this.site = site; }

	@Override
	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String getSite() { return this.site; }

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getNote() {
		return this.note;
	}

	@Override
	public Category getCategory() {
		return Category.credenzialeAccesso;
	}

	@Override
	public String getTitle() { return this.site; }

	@Override
	public JSONObject generateJSON() throws JSONException{
		final JSONObject o =  super.generateJSON();

		String cryptedPassword = this.password;
		try {
			cryptedPassword = MyEncryptor.encrypt(cryptedPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

		o.put(JSON_USERNAME, this.username)
				.put(JSON_PASSWORD, cryptedPassword)
				.put(JSON_NOTE, this.note)
				.put(JSON_SITO, this.site)
				.put("category", Category.credenzialeAccesso.name());

		return o;
	}
	@Override
	public String toString() {
		return "ACCESSO: Sito->" + this.site + " , Username->" + this.username + " ,Password->" + this.password;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		String passwordCrypted = "";
		try {
			passwordCrypted = MyEncryptor.encrypt(this.password);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dest.writeString(this.username);
		dest.writeString(passwordCrypted);
		dest.writeString(this.site);
		dest.writeString(this.note);
	}
}
