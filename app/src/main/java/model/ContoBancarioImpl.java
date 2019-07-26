package model;

import android.os.Parcel;
import org.json.JSONException;
import org.json.JSONObject;
import cryptography.MyEncryptor;
import model.interfaces.ContoBancario;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di ContoBancario
 */
public class ContoBancarioImpl extends BaseElement implements ContoBancario {

    public final static Creator<ContoBancario> CREATOR = new Creator<ContoBancario>() {
        @Override
        public ContoBancario createFromParcel(Parcel source) {
            return new ContoBancarioImpl(source);
        }

        @Override
        public ContoBancario[] newArray(int size) {
            return new ContoBancario[size];
        }
    };

    private static final String JSON_NOME_BANCA = "nome_banca";
    private static final String JSON_NOME_CONTO = "nome_conto";
    private static final String JSON_TIPO = "tipo";
    private static final String JSON_IBAN = "iban";
    private static final String JSON_NUMERO = "numero_conto";
    private static final String JSON_PIN = "pin";
    private static final String JSON_NOTE = "note";

    private String nomeBanca;
    private String nomeConto;
    private String tipo;
    private String numeroConto;
    private String iban;
    private String pin;
    private String note;

    public ContoBancarioImpl() {
        super();
        this.nomeBanca = "";
        this.nomeConto = "";
        this.tipo = "";
        this.numeroConto = "";
        this.iban = "";
        this.pin = "";
        this.note = "";
    }

    public ContoBancarioImpl(final JSONObject json) throws JSONException {
        super(json);
        this.nomeBanca = json.getString(JSON_NOME_BANCA);
        this.nomeConto = json.getString(JSON_NOME_CONTO);
        this.tipo = json.getString(JSON_TIPO);
        this.numeroConto = json.getString(JSON_NUMERO);
        this.iban = json.getString(JSON_IBAN);
        try {
            this.pin = MyEncryptor.decrypt(json.getString(JSON_PIN));
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.note = json.getString(JSON_NOTE);
    }

    private ContoBancarioImpl(final Parcel parcel) {
        super(parcel);
        this.nomeBanca = parcel.readString();
        this.nomeConto = parcel.readString();
        this.tipo = parcel.readString();
        this.numeroConto = parcel.readString();
        this.iban = parcel.readString();
        try {
            this.pin = MyEncryptor.decrypt(parcel.readString());
        } catch (Exception e) {
            e.printStackTrace();
            this.pin = "";
        }
        this.note = parcel.readString();
    }

    @Override
    public JSONObject generateJSON() throws JSONException {
        String pinCripted = "";
        try {
            pinCripted = MyEncryptor.encrypt(this.pin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final JSONObject o =  super.generateJSON();
                o.put(JSON_NOME_BANCA, this.nomeBanca)
                        .put("category", Category.contoBancario.name())
                        .put(JSON_NOME_CONTO, this.nomeConto)
                        .put(JSON_TIPO, this.tipo)
                        .put(JSON_NUMERO,this.numeroConto)
                        .put(JSON_IBAN, this.iban)
                        .put(JSON_PIN, pinCripted)
                        .put(JSON_NOTE, this.note);
        return o;
    }

    @Override
    public void setNomeBanca(String nome) { this.nomeBanca = nome; }

    @Override
    public void setNomeConto(String conto) { this.nomeConto = conto; }

    @Override
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public void setNumeroConto(String numero) { this.numeroConto = numero; }

    @Override
    public void setIBAN(String iban) { this.iban = iban; }

    @Override
    public void setPin(String pin) { this.pin = pin; }

    @Override
    public void setNote(String note) { this.note = note; }

    @Override
    public String getNomeBanca() { return this.nomeBanca; }

    @Override
    public String getNomeConto() { return this.nomeConto; }

    @Override
    public String getTipo() { return this.tipo; }

    @Override
    public String getNumeroConto() { return this.numeroConto; }

    @Override
    public String getIBAN() { return this.iban; }

    @Override
    public String getPin() { return this.pin; }

    @Override
    public String getNote() { return this.note; }

    @Override
    public Category getCategory() { return Category.contoBancario; }

    @Override
    public String getTitle() { return this.nomeConto; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        String pinCrypted = "";
        try {
            pinCrypted = MyEncryptor.encrypt(this.pin);
        } catch (Exception e) {
            e.printStackTrace();
        }

        dest.writeString(this.nomeBanca);
        dest.writeString(this.nomeConto);
        dest.writeString(this.tipo);
        dest.writeString(this.numeroConto);
        dest.writeString(this.iban);
        dest.writeString(pinCrypted);
        dest.writeString(this.note);
    }

    @Override
    public String toString() {
        return "CONTO BANCARIO: Nome Banca->" + this.nomeBanca + " , Nome Conto->" + this.nomeConto + " ,tipo->" + this.tipo;
    }

}
