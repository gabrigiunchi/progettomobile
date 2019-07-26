package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella codice_fiscale del database
 */
public class CodiceFiscaleSchema {

    private static final String TABLE_NAME = DatabaseTable.codice_fiscale.name();
    public static final String ID = "id";
    public static final String NOME = "nome";
    public static final String NUMERO = "numero";
    public static final String DATA_NASCITA = "data_nascita";
    public static final String NOTE = "note";


    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            +  NOME + " TEXT, "
            + NUMERO + " TEXT, "
            + DATA_NASCITA + " TEXT, "
            + NOTE + " TEXT)";
}
