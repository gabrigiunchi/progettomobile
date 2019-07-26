package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella conto_bancario del database
 */
public class ContoBancarioSchema {

    private static final String TABLE_NAME = DatabaseTable.conto_bancario.name();
    public static final String ID = "id";
    public static final String NOME_BANCA = "nome_banca";
    public static final String NOME_CONTO = "nome_conto";
    public static final String TIPO = "tipo";
    public static final String NUMERO = "numero";
    public static final String IBAN = "iban";
    public static final String PIN = "pin";
    public static final String NOTE = "note";


    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            + NOME_BANCA + " TEXT, "
            + NOME_CONTO + " TEXT, "
            + TIPO + " TEXT, "
            + NUMERO + " TEXT, "
            + IBAN + " TEXT, "
            + PIN + " TEXT, "
            + NOTE + " TEXT)";
}
