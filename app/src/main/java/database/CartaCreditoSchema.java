package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella carta_credito del database
 */
public class CartaCreditoSchema {

    private static final String TABLE_NAME = DatabaseTable.carta_credito.name();
    public static final String ID = "id";
    public static final String NUMERO = "numero";
    public static final String INTESTATARIO = "intestatario";
    public static final String TIPO = "tipo";
    public static final String CODICE_VERIFICA = "codice_verifica";
    public static final String DATA_SCADENZA = "data_scadenza";
    public static final String VALIDO_DA = "valido_da";
    public static final String PIN = "pin";
    public static final String NOTE = "note";


    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            + NUMERO + " TEXT, "
            +  INTESTATARIO + " TEXT, "
            + TIPO + " TEXT, "
            +  CODICE_VERIFICA + " TEXT, "
            +  DATA_SCADENZA + " TEXT, "
            +  VALIDO_DA + " TEXT, "
            +  PIN + " TEXT, "
            + NOTE + " TEXT)";
}
