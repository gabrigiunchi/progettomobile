package database;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

import model.interfaces.CartaCredito;
import model.interfaces.CodiceFiscale;
import model.interfaces.ContoBancario;
import model.interfaces.CredenzialeAccesso;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia del database con metodi per gestire gli elementi dell'applicazione
 */
public interface DatabaseManager{

    void insertElement(Element element);
    void updateElement(Element element);
    /**
     * Aggiorna gli attributi di un elemento con l'id dato.
     * I suoi attributi vengono sostituiti con quelli dell'elemento dato come parametro perciò
     * a seguito di questa operazione l'id dell'elemento può cambiare
     * @param element elemento con gli attributi aggiornati
     * @param id id dell'elemento da aggiornare
     */
    void updateElementWithId(Element element, int id);
    void removeElement(Element element);

    void insertElementMetadata(ElementMetadata elementMetadata);
    void updateElementMetadata(ElementMetadata elementMetadata);
    /**
     * Aggiorna gli attributi di un ElementMetadata avente l'id dato.
     * I suoi attributi vengono sostituiti con quelli dell'oggetto dato come parametro perciò
     * a seguito di questa operazione l'id dell'elemento può cambiare
     * @param elementMetadata
     * @param id
     */
    void updateElementMetadataWithId(ElementMetadata elementMetadata, int id);
    void removeElementMetadata(ElementMetadata elementMetadata);

    void clearDatabase();
    void executeQuery(String query);

    void removeFromFavorites(Element element);
    void addToFavorites(Element e);
    List<Element> getFavoritesElements();

    List<Element> getAllElements();
    List<ContoBancario> getAllContoBancario();
    List<CredenzialeAccesso> getAllCredenzialiAccesso();
    List<CartaCredito> getAllCartaCredito();
    List<CodiceFiscale> getAllCodiceFiscale();
    List<ElementMetadata> getAllElementMetadata();

    CartaCredito getCartaCredito(int id);
    CodiceFiscale getCodiceFiscale(int id);
    CredenzialeAccesso getCredenzialeAccesso(int id);
    ContoBancario getContoBancario(int id);
    ElementMetadata getElementMetadata(int id);

    SQLiteDatabase getDatabase();

    /**
     * Restituisce l'id massimo tra gli elementi nella tabella data in input
     * @param table su cui effettuare la query
     * @return id massimo, 0 se non presente
     */
    int getMaxID(DatabaseTable table);
}
