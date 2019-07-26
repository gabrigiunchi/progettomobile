package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella credenziali_accesso del database
 */
public class CredenzialiAccessoSchema {

    private static final String TABLE_NAME = DatabaseTable.credenziale_accesso.name();
    public static final String ID = "id";
    public static final String SITE = "sito";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String NOTE = "note";

    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            +  USERNAME + " TEXT, "
            + PASSWORD + " TEXT, "
            + SITE + " TEXT, "
            + NOTE + " TEXT)";
}
