package utility;

import android.content.ContentValues;

import database.CartaCreditoSchema;
import database.CodiceFiscaleSchema;
import database.ContoBancarioSchema;
import database.CredenzialiAccessoSchema;
import database.DatabaseTable;
import database.ElementMetadataSchema;
import model.Category;
import model.interfaces.CartaCredito;
import model.interfaces.CodiceFiscale;
import model.interfaces.ContoBancario;
import model.interfaces.CredenzialeAccesso;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * @author Gabriele Giunchi
 *
 * Fornisce metodi statici per gestire alcuni aspetti riguardo al database
 */
public final class DatabaseUtilities {

    private DatabaseUtilities() { }

    public static DatabaseTable findTableByCategory(final Category category) {
        switch (category) {
            case cartaCredito: return DatabaseTable.carta_credito;
            case codiceFiscale: return DatabaseTable.codice_fiscale;
            case contoBancario:return DatabaseTable.conto_bancario;
            case credenzialeAccesso: return DatabaseTable.credenziale_accesso;
            default: return null;
        }
    }

    public static ContentValues getElementMetadataContentValues(final ElementMetadata elementMetadata) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ElementMetadataSchema.ELEMENT, elementMetadata.getElement().getID());
        contentValues.put(ElementMetadataSchema.ELEMENT_CATEGORY, elementMetadata.getElement().getCategory().name());
        contentValues.put(ElementMetadataSchema.ID, elementMetadata.getID());
        contentValues.put(ElementMetadataSchema.LAST_MODIFIED, elementMetadata.getLastModified());
        contentValues.put(ElementMetadataSchema.STATE, elementMetadata.getState().name());
        return contentValues;
    }

    public static ContentValues getElementContentValues(final Element element) {
        final Category category = element.getCategory();

        switch (category) {
            case cartaCredito: return getCartaCreditoContentValues((CartaCredito)element);
            case codiceFiscale: return getCodiceFiscaleContentValues((CodiceFiscale)element);
            case contoBancario: return getContoBancarioContentValues((ContoBancario)element);
            case credenzialeAccesso: return getCredenzialeAccessoContentValues((CredenzialeAccesso)element);
            default: return null;
        }
    }

    private static ContentValues getCartaCreditoContentValues(final CartaCredito cartaCredito) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(CartaCreditoSchema.CODICE_VERIFICA, cartaCredito.getCodiceVerifica());
        contentValues.put(CartaCreditoSchema.DATA_SCADENZA, cartaCredito.getDataScadenza());
        contentValues.put(CartaCreditoSchema.ID, cartaCredito.getID());
        contentValues.put(CartaCreditoSchema.INTESTATARIO, cartaCredito.getIntestatario());
        contentValues.put(CartaCreditoSchema.NUMERO, cartaCredito.getNumeroCarta());
        contentValues.put(CartaCreditoSchema.PIN, cartaCredito.getPin());
        contentValues.put(CartaCreditoSchema.TIPO, cartaCredito.getTipo());
        contentValues.put(CartaCreditoSchema.VALIDO_DA, cartaCredito.getValidità());
        contentValues.put(CartaCreditoSchema.NOTE, cartaCredito.getNote());
        return contentValues;
    }

    private static ContentValues getCredenzialeAccessoContentValues(final CredenzialeAccesso credenzialeAccesso) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(CredenzialiAccessoSchema.SITE, credenzialeAccesso.getSite());
        contentValues.put(CredenzialiAccessoSchema.USERNAME, credenzialeAccesso.getUsername());
        contentValues.put(CredenzialiAccessoSchema.PASSWORD, credenzialeAccesso.getPassword());
        contentValues.put(CredenzialiAccessoSchema.ID, credenzialeAccesso.getID());
        contentValues.put(CredenzialiAccessoSchema.NOTE, credenzialeAccesso.getNote());
        return contentValues;
    }

    private static ContentValues getContoBancarioContentValues(final ContoBancario contoBancario) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(ContoBancarioSchema.ID, contoBancario.getID());
        contentValues.put(ContoBancarioSchema.IBAN, contoBancario.getIBAN());
        contentValues.put(ContoBancarioSchema.NOME_BANCA, contoBancario.getNomeBanca());
        contentValues.put(ContoBancarioSchema.NOME_CONTO, contoBancario.getNomeConto());
        contentValues.put(ContoBancarioSchema.NUMERO, contoBancario.getNumeroConto());
        contentValues.put(ContoBancarioSchema.TIPO, contoBancario.getTipo());
        contentValues.put(ContoBancarioSchema.PIN, contoBancario.getPin());
        contentValues.put(ContoBancarioSchema.NOTE, contoBancario.getNote());
        return contentValues;
    }

    private static ContentValues getCodiceFiscaleContentValues(final CodiceFiscale codiceFiscale) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(CodiceFiscaleSchema.DATA_NASCITA, codiceFiscale.getDataNascita());
        contentValues.put(CodiceFiscaleSchema.ID, codiceFiscale.getID());
        contentValues.put(CodiceFiscaleSchema.NOME, codiceFiscale.getNome());
        contentValues.put(CodiceFiscaleSchema.NUMERO, codiceFiscale.getNumero());
        contentValues.put(CodiceFiscaleSchema.NOTE, codiceFiscale.getNote());
        return contentValues;
    }
}
