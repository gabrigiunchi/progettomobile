package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Gabriele Giunchi
 */
public class DatabaseCreator extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "user_db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseCreator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        Log.d("applicazione", "Creo database");
        db.execSQL(CredenzialiAccessoSchema.SQL_CREATE_DATABASE);
        db.execSQL(ElementMetadataSchema.SQL_CREATE_DATABASE);
        db.execSQL(CodiceFiscaleSchema.SQL_CREATE_DATABASE);
        db.execSQL(CartaCreditoSchema.SQL_CREATE_DATABASE);
        db.execSQL(ContoBancarioSchema.SQL_CREATE_DATABASE);
        db.execSQL(FavoriteSchema.SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
