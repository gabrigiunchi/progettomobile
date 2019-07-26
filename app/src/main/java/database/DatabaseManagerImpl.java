package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import model.CartaCreditoImpl;
import model.Category;
import model.CodiceFiscaleImpl;
import model.ContoBancarioImpl;
import model.CredenzialeAccessoImpl;
import model.ElementMetadataImpl;
import model.interfaces.CartaCredito;
import model.interfaces.CodiceFiscale;
import model.interfaces.ContoBancario;
import model.interfaces.CredenzialeAccesso;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;
import utility.DatabaseUtilities;

/**
 * Created by Gabri on 15/05/2016.
 */
public class DatabaseManagerImpl implements DatabaseManager {

    private final DatabaseCreator dbCreator;
    private static DatabaseManager databaseManager;

    private DatabaseManagerImpl(final Context context) {
        this.dbCreator = new DatabaseCreator(context);
    }

    public static DatabaseManager getDatabaseManager(final Context context) {
        if(databaseManager == null) {
            databaseManager = new DatabaseManagerImpl(context);
        }

        return databaseManager;
    }

    private void insertDatabaseObject(DatabaseTable tableName, ContentValues values) {
        final SQLiteDatabase db = this.dbCreator.getWritableDatabase();
        db.insert(tableName.name(), null, values);
        db.close();
    }

    private void updateDatabaseObject(DatabaseTable tableName, int id, ContentValues values) {
        this.getDatabase().update(tableName.name(), values, "id='" + id + "'", null);
    }

    private void removeDatabaseObject(DatabaseTable tableName, int id) {
        this.executeQuery("DELETE FROM " + tableName + " WHERE id='" + id + "'");
    }

    @Override
    public void insertElement(Element element) {
        final DatabaseTable table = DatabaseUtilities.findTableByCategory(element.getCategory());
        final ContentValues contentValues = DatabaseUtilities.getElementContentValues(element);
        this.insertDatabaseObject(table, contentValues);
    }

    @Override
    public void updateElement(Element element) {
        final DatabaseTable table = DatabaseUtilities.findTableByCategory(element.getCategory());
        final int id = element.getID();
        final ContentValues contentValues = DatabaseUtilities.getElementContentValues(element);
        this.updateDatabaseObject(table, id, contentValues);
    }

    @Override
    public void updateElementWithId(Element element, int id) {
        final DatabaseTable table = DatabaseUtilities.findTableByCategory(element.getCategory());
        final ContentValues contentValues = DatabaseUtilities.getElementContentValues(element);
        this.updateDatabaseObject(table, id, contentValues);
    }

    @Override
    public void removeElement(Element element) {
        this.removeDatabaseObject(DatabaseUtilities.findTableByCategory(element.getCategory()), element.getID());
    }

    @Override
    public void insertElementMetadata(ElementMetadata elementMetadata) {
        final ContentValues contentValues = DatabaseUtilities.getElementMetadataContentValues(elementMetadata);
        this.insertDatabaseObject(DatabaseTable.element_metadata, contentValues);
    }

    @Override
    public void updateElementMetadata(ElementMetadata elementMetadata) {
        final ContentValues contentValues = DatabaseUtilities.getElementMetadataContentValues(elementMetadata);
        final int id = elementMetadata.getID();
        this.updateDatabaseObject(DatabaseTable.element_metadata, id, contentValues);
    }

    @Override
    public void updateElementMetadataWithId(ElementMetadata elementMetadata, int id) {
        final ContentValues contentValues = DatabaseUtilities.getElementMetadataContentValues(elementMetadata);
        this.updateDatabaseObject(DatabaseTable.element_metadata, id, contentValues);
    }

    @Override
    public void removeElementMetadata(ElementMetadata elementMetadata) {
        this.removeDatabaseObject(DatabaseTable.element_metadata, elementMetadata.getID());
        Log.d("applicazioneDatabase", "Rimosso metadata " + elementMetadata.getID());
    }

    @Override
    public void clearDatabase() {
        executeQuery("DELETE FROM " + DatabaseTable.credenziale_accesso.name());
        executeQuery("DELETE FROM " + DatabaseTable.favorites.name());
        executeQuery("DELETE FROM " + DatabaseTable.element_metadata.name());
        executeQuery("DELETE FROM " + DatabaseTable.conto_bancario.name());
        executeQuery("DELETE FROM " + DatabaseTable.carta_credito.name());
        executeQuery("DELETE FROM " + DatabaseTable.codice_fiscale.name());
    }

    @Override
    public void executeQuery(String query) {
        final SQLiteDatabase db = this.getDatabase();
        db.execSQL(query);
        db.close();
    }

    @Override
    public void removeFromFavorites(Element element) {
        this.executeQuery("DELETE FROM " + DatabaseTable.favorites
                + " WHERE " + FavoriteSchema.ELEMENT_CATEGORY + "='" + element.getCategory() + "'"
                + " AND " + FavoriteSchema.ELEMENT_ID + "='" + element.getID() + "'"
        );
    }

    @Override
    public void addToFavorites(Element e) {
        final int id = e.getID();
        final String category = e.getCategory().name();
        final ContentValues contentValues = new ContentValues();
        contentValues.put(FavoriteSchema.ELEMENT_CATEGORY, category);
        contentValues.put(FavoriteSchema.ELEMENT_ID, id);
        contentValues.put(FavoriteSchema.ID, this.getMaxID(DatabaseTable.favorites) + 1);
        this.insertDatabaseObject(DatabaseTable.favorites, contentValues);
    }

    @Override
    public List<ContoBancario> getAllContoBancario() {
        final List<ContoBancario> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.conto_bancario.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                list.add(this.createContoBancario(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    @Override
    public List<CredenzialeAccesso> getAllCredenzialiAccesso() {
        final List<CredenzialeAccesso> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.credenziale_accesso.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                list.add(this.createCredenzialeAccesso(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();
        return list;
    }

    @Override
    public List<CartaCredito> getAllCartaCredito() {
        final List<CartaCredito> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.carta_credito.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                list.add(this.createCartaCredito(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();

        return list;
    }

    @Override
    public List<CodiceFiscale> getAllCodiceFiscale() {
        final List<CodiceFiscale> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.codice_fiscale.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                list.add(this.createCodiceFiscale(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();

        return list;
    }

    @Override
    public List<ElementMetadata> getAllElementMetadata() {
        final List<ElementMetadata> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.element_metadata.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                list.add(this.createSyncElement(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }

        db.close();

        return list;
    }

    @Override
    public CartaCredito getCartaCredito(int id) {
        return (CartaCredito)this.getElementInTable(DatabaseTable.carta_credito, id);
    }

    @Override
    public CodiceFiscale getCodiceFiscale(int id) {
        return (CodiceFiscale)this.getElementInTable(DatabaseTable.codice_fiscale, id);
    }

    @Override
    public CredenzialeAccesso getCredenzialeAccesso(int id) {
        return (CredenzialeAccesso)this.getElementInTable(DatabaseTable.credenziale_accesso, id);
    }

    @Override
    public ContoBancario getContoBancario(int id) {
        return (ContoBancario) this.getElementInTable(DatabaseTable.conto_bancario, id);
    }

    @Override
    public List<Element> getFavoritesElements() {
        final List<Element> list = new ArrayList<>();
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.favorites.name(), null);

        if(cursor != null && cursor.moveToFirst()) {
            for(int i = 0; i < cursor.getCount(); i++) {
                String categoryName = cursor.getString(cursor.getColumnIndex(FavoriteSchema.ELEMENT_CATEGORY));
                Category category = Category.valueOf(categoryName);
                DatabaseTable table = DatabaseUtilities.findTableByCategory(category);
                int id = cursor.getInt(cursor.getColumnIndex(FavoriteSchema.ELEMENT_ID));
                Element element = this.getElementInTable(table, id);
                if(element != null) {
                    list.add(element);
                }

                cursor.moveToNext();
            }
            cursor.close();
        }
        db.close();

        return list;
    }

    @Override
    public List<Element> getAllElements() {
        final List<Element> elements = new ArrayList<>();
        elements.addAll(databaseManager.getAllCartaCredito());
        elements.addAll(databaseManager.getAllContoBancario());
        elements.addAll(databaseManager.getAllCredenzialiAccesso());
        elements.addAll(databaseManager.getAllCodiceFiscale());
        return elements;
    }

    @Override
    public ElementMetadata getElementMetadata(int id) {
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseTable.element_metadata.name() + " WHERE id='" + id + "'", null);
        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        final ElementMetadata elementMetadata = this.createSyncElement(cursor);
        cursor.close();
        db.close();
        return elementMetadata;
    }

    private Element getElementInTable(final DatabaseTable table, final int id) {
        final SQLiteDatabase db = this.getDatabase();
        final Cursor cursor = db.rawQuery("SELECT * FROM " + table.name() + " WHERE id='" + id + "'", null);

        if(cursor == null || !cursor.moveToFirst()) {
            return null;
        }

        Element element = null;

        switch (table) {
            case carta_credito: element = this.createCartaCredito(cursor); break;
            case codice_fiscale: element = this.createCodiceFiscale(cursor); break;
            case conto_bancario: element = this.createContoBancario(cursor); break;
            case credenziale_accesso: element = this.createCredenzialeAccesso(cursor); break;
        }

        cursor.close();
        db.close();
        return element;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return this.dbCreator.getWritableDatabase();
    }


    private CredenzialeAccesso createCredenzialeAccesso(final Cursor cursor) {
        final CredenzialeAccesso c = new CredenzialeAccessoImpl();
        String password = cursor.getString(cursor.getColumnIndex(CredenzialiAccessoSchema.PASSWORD));
        c.setPassword(password);
        c.setUsername(cursor.getString(cursor.getColumnIndex(CredenzialiAccessoSchema.USERNAME)));
        c.setID(cursor.getInt(cursor.getColumnIndex(CredenzialiAccessoSchema.ID)));
        c.setSite(cursor.getString(cursor.getColumnIndex(CredenzialiAccessoSchema.SITE)));
        c.setNote(cursor.getString(cursor.getColumnIndex(CredenzialiAccessoSchema.NOTE)));
        return c;
    }

    private CartaCredito createCartaCredito(final Cursor cursor) {
        final CartaCredito cartaCredito = new CartaCreditoImpl();

        cartaCredito.setIntestatario(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.INTESTATARIO)));
        cartaCredito.setID(cursor.getInt(cursor.getColumnIndex(CartaCreditoSchema.ID)));
        cartaCredito.setCodiceVerifica(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.CODICE_VERIFICA)));
        cartaCredito.setPin(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.PIN)));
        cartaCredito.setDataScadenza(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.DATA_SCADENZA)));
        cartaCredito.setValidità(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.VALIDO_DA)));
        cartaCredito.setTipo(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.TIPO)));
        cartaCredito.setNumeroCarta(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.NUMERO)));
        cartaCredito.setNote(cursor.getString(cursor.getColumnIndex(CartaCreditoSchema.NOTE)));

        return cartaCredito;
    }

    private CodiceFiscale createCodiceFiscale(final Cursor cursor) {
        final CodiceFiscale codiceFiscale = new CodiceFiscaleImpl();

        codiceFiscale.setNumero(cursor.getString(cursor.getColumnIndex(CodiceFiscaleSchema.NUMERO)));
        codiceFiscale.setID(cursor.getInt(cursor.getColumnIndex(CodiceFiscaleSchema.ID)));
        codiceFiscale.setNome(cursor.getString(cursor.getColumnIndex(CodiceFiscaleSchema.NOME)));
        codiceFiscale.setDataNascita(cursor.getString(cursor.getColumnIndex(CodiceFiscaleSchema.DATA_NASCITA)));
        codiceFiscale.setNote(cursor.getString(cursor.getColumnIndex(CodiceFiscaleSchema.NOTE)));

        return codiceFiscale;
    }

    private ContoBancario createContoBancario(final Cursor cursor) {
        final ContoBancario contoBancario = new ContoBancarioImpl();

        contoBancario.setNomeBanca(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.NOME_BANCA)));
        contoBancario.setNomeConto(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.NOME_CONTO)));
        contoBancario.setNumeroConto(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.NUMERO)));
        contoBancario.setTipo(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.TIPO)));
        contoBancario.setPin(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.PIN)));
        contoBancario.setIBAN(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.IBAN)));
        contoBancario.setNote(cursor.getString(cursor.getColumnIndex(ContoBancarioSchema.NOTE)));
        contoBancario.setID(cursor.getInt(cursor.getColumnIndex(ContoBancarioSchema.ID)));

        return contoBancario;
    }

    private ElementMetadata createSyncElement(final Cursor cursor) {
        final int id = cursor.getInt(cursor.getColumnIndex(ElementMetadataSchema.ID));
        final ElementMetadata.ElementState state = ElementMetadata.ElementState.valueOf(cursor.getString(cursor.getColumnIndex(ElementMetadataSchema.STATE)));
        final long lastModified = cursor.getLong(cursor.getColumnIndex(ElementMetadataSchema.LAST_MODIFIED));
        final int elementID = cursor.getInt(cursor.getColumnIndex(ElementMetadataSchema.ELEMENT));
        final Category elementCategory = Category.valueOf(cursor.getString(cursor.getColumnIndex(ElementMetadataSchema.ELEMENT_CATEGORY)));

        Element element = null;

        switch (elementCategory) {
            case cartaCredito: element = this.getCartaCredito(elementID); break;
            case codiceFiscale: element = this.getCodiceFiscale(elementID); break;
            case contoBancario: element = this.getContoBancario(elementID); break;
            case credenzialeAccesso: element = this.getCredenzialeAccesso(elementID); break;
        }

        final ElementMetadata elementMetadata = new ElementMetadataImpl(element);
        elementMetadata.setID(id);
        elementMetadata.setState(state);
        elementMetadata.setLastModified(lastModified);

        return elementMetadata;
    }

    @Override
    public int getMaxID(DatabaseTable table){
        final String sql = "SELECT MAX(id) FROM " + table.name();
        final Cursor cursor = this.getDatabase().rawQuery(sql,null);
        if(cursor == null || !cursor.moveToFirst()){
            return -1;
        }
        final int id = cursor.getInt(0);
        cursor.close();
        return id;
    }
}
