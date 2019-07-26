package database;

/**
 * @author Gabriele Giunchi
 *
 * Classe che definisce la tabella element_metadata del database
 */
public class ElementMetadataSchema {

    private static final String TABLE_NAME = DatabaseTable.element_metadata.name();
    public static final String ID = "id";
    public static final String LAST_MODIFIED = "last_modified";
    public static final String ELEMENT = "element";
    public static final String STATE = "state";
    public static final String ELEMENT_CATEGORY = "element_category";

    public static final String SQL_CREATE_DATABASE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
            + " (" + ID + " INTEGER PRIMARY KEY, "
            +  STATE + " TEXT, "
            + ELEMENT + " INTEGER, "
            + LAST_MODIFIED + " LONG, "
            + ELEMENT_CATEGORY + " TEXT)";
}
