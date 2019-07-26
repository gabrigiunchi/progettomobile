package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella del database per gli elementi preferiti
 */
public class FavoriteSchema {

    private static final String TABLE_NAME = DatabaseTable.favorites.name();
    public static final String ID = "id";
    public static final String ELEMENT_ID = "element_id";
    public static final String ELEMENT_CATEGORY = "element_category";

    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            + ELEMENT_ID + " TEXT, "
            + ELEMENT_CATEGORY + " TEXT)";
}
