package model.interfaces;

import android.os.Bundle;
import java.util.List;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di un oggetto che ha il compito effettuare lo scambio di dati tra l'applicazione e il server
 * eseguendo il download e l'upload degli elementi dell'utente.
 * Il server rappresenta il servizio di sincronizzazione scelto (vedi SynchronizationService.SynchronizationMethod)
 */
public interface ServerComunicationManager {
    List<ElementMetadata> download(String source, Bundle options) throws Exception;
    void upload(final List<ElementMetadata> outcomingData, String destination, Bundle options) throws Exception;
}
