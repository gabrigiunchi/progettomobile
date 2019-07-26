package model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import model.interfaces.DataSynchronizer;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

import static model.interfaces.ElementMetadata.ElementState.ADDED;
import static model.interfaces.ElementMetadata.ElementState.REMOVED;
import static model.interfaces.ElementMetadata.ElementState.SYNCHRONIZED;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di DataSynchronizer che aggiorna i dati sul database quando effettua la sincronizzazione di due liste
 */
public class DatabaseSynchronizer implements DataSynchronizer {

    private final DatabaseManager databaseManager;
    private final List<ElementMetadata> localData;
    private final List<ElementMetadata> serverData;
    private List<ElementMetadata> incoming;
    private List<ElementMetadata> outcoming;
    private List<ElementMetadata> updatedList;

    public DatabaseSynchronizer(final DatabaseManager db, final List<ElementMetadata> localData, final List<ElementMetadata> serverData) {
        this.databaseManager = db;
        this.localData = localData;
        this.serverData = serverData;
    }

    private int findMaxElementId(final List<Element> list, final Category category) {
        int max = 0;

        for(Element e : list) {
            final int temp = e.getID();
            if(category == e.getCategory() && temp > max) {
                max = temp;
            }
        }

        return max;
    }

    private int findMaxSyncElementId(final List<ElementMetadata> list) {
        int max = 0;

        for(ElementMetadata e : list) {
            final int temp = e.getID();
            if (temp > max) {
                max = temp;
            }
        }

        return max;
    }

    private boolean duplicateID(final List<Element> list, final int id, final Category category) {
        int count = 0;

        for(Element e : list) {
            if(e.getCategory() == category && e.getID() == id) {
                count++;
            }
        }

        return count > 1;
    }

    private boolean duplicateID(final List<ElementMetadata> list, final int id) {
        int count = 0;

        for(ElementMetadata e : list) {
            if(e.getID() == id) {
                count++;
            }
        }

        return count > 1;
    }

    private void removeElements() {
        List<ElementMetadata> removed = new ArrayList<>();

        for(ElementMetadata e : this.localData) {
            if(e.getState() == REMOVED) {
                removed.add(e);
                this.databaseManager.removeElement(e.getElement());
                this.databaseManager.removeElementMetadata(e);
                Log.d("applicazione", "Rimosso elemento " + e.getID() + " del server: ");
            }
        }
        this.serverData.removeAll(removed);
        if(removed.isEmpty()) {
            Log.d("applicazione", "Nessun elemento da rimuovere nella lista del server");
        }
        this.localData.removeAll(removed);
        this.outcoming = new ArrayList<>(removed);
        removed.clear();
    }

    private void addNewElements() {
        final List<ElementMetadata> allElementMetadatas = new ArrayList<>(this.localData);
        allElementMetadatas.addAll(this.serverData);
        final List<Element> allElements = new ArrayList<>();
        for(ElementMetadata s : allElementMetadatas) {
            allElements.add(s.getElement());
        }

        // Si aggiungono gli elementi nuovi
        for(ElementMetadata e : this.localData) {
            if(e.getState() == ADDED) {
                Element element = e.getElement();
                final int oldSyncID = e.getID();
                final int oldElementID = element.getID();

                if(this.duplicateID(allElementMetadatas, e.getID())) {
                    final int maxSyncID = this.findMaxSyncElementId(allElementMetadatas);
                    System.out.println("Cambio id a elementMetadata " + e.getID() + ", nuovo id -> " + (maxSyncID + 1));
                    e.setID(maxSyncID + 1);
                }

                if(this.duplicateID(allElements, element.getID(), element.getCategory())) {
                    final int maxElementID = this.findMaxElementId(allElements, element.getCategory());
                    System.out.println("Cambio id a elemento " + e.getElement().getID() + " , nuovo id -> " + (maxElementID + 1));
                    e.getElement().setID(maxElementID + 1);
                }

                outcoming.add(new ElementMetadataImpl(e));
                e.setState(SYNCHRONIZED);
                updatedList.add(e);
                this.databaseManager.updateElementMetadataWithId(e, oldSyncID);
                this.databaseManager.updateElementWithId(element, oldElementID);

                Log.d("applicazione", "Aggiunto elemento " + oldSyncID + " da locale, nuovo id: " + e.getID());
            }
        }

        for(ElementMetadata e : this.serverData) {
            boolean miss = true;
            for(ElementMetadata e1 : this.localData) {
                if(e1.getID() == e.getID()) {
                    miss = false;
                }
            }

            // Elemento manca nella lista locale
            if(miss) {
                this.incoming.add(new ElementMetadataImpl(e));
                e.setState(SYNCHRONIZED);
                updatedList.add(e);

                this.databaseManager.insertElement(e.getElement());
                this.databaseManager.insertElementMetadata(e);

                Log.d("applicazione", "Aggiunto elemento " + e.getID() + " da server");
            }
        }
    }

    private void checkModifiedElements() {
        this.updatedList = new ArrayList<>();
        this.incoming = new ArrayList<>();

        // Si confrontano elementi modificati
        for(ElementMetadata e : this.localData) {
            if(e.getState() != ADDED) {
                int index = this.serverData.lastIndexOf(e);

				/* Se index < 0 vuol dire che il file è stato rimosso dal server.
				 */
                if(index >= 0) {
                    ElementMetadata e1 = this.serverData.get(index);

                    if(e1.getLastModified() > e.getLastModified()) {
                        this.incoming.add(new ElementMetadataImpl(e1));
                        e1.setState(SYNCHRONIZED);
                        updatedList.add(e1);

                        // Aggiorno le tuple sul database
                        this.databaseManager.updateElementMetadata(e1);
                        this.databaseManager.updateElement(e1.getElement());

                        Log.d("applicazione", "Aggiornato file " + e1.getID() + " dal server");
                    } else if(e1.getLastModified() < e.getLastModified()){
                        this.outcoming.add(new ElementMetadataImpl(e));
                        e.setState(SYNCHRONIZED);
                        updatedList.add(e);

                        // Aggiorno le tuple sul database
                        this.databaseManager.updateElementMetadata(e);
                        this.databaseManager.updateElement(e.getElement());

                        Log.d("applicazione", "Aggiornato file " + e.getID() + " da locale");
                    } else {
                        Log.d("applicazione", "Aggiunto file " + e.getID() + " non modificato");
                        updatedList.add(e);
                    }
                } else {
                    final Element element = e.getElement();
                    this.databaseManager.removeElement(element);
                    this.databaseManager.removeElementMetadata(e);
                    Log.d("applicazione", "Rimosso elemento " + e.getID() + " in locale");
                }
            }
        }
    }

    private DataSynchronizer merge() {
        if(this.updatedList != null) {
            return this;
        }

        Log.d("applicazione", "INIZIO SINCRONIZZAZIONE");

       /* if(this.serverData.isEmpty()) {
            Log.d("applicazione", "Lista ricevuta dal server vuota");

            this.incoming = Collections.emptyList();
            this.outcoming = new ArrayList<>();

            for(ElementMetadata e : this.localData) {
                this.outcoming.add(new ElementMetadataImpl(e));
                e.setState(SYNCHRONIZED);
                this.databaseManager.updateElementMetadataWithId(e);
                Log.d("applicazione", "Aggiunto elemento " + e.getID() + " da locale");
            }

            this.updatedList = this.localData;
            return this;
        }
*/
        this.removeElements();
        this.checkModifiedElements();
        this.addNewElements();

        this.stampNewList();

        return this;
    }

    @Override
    public List<ElementMetadata> getIncomingChanges() throws IllegalStateException {
        if(this.incoming == null) {
            this.merge();
        }

        return this.incoming;
    }

    @Override
    public List<ElementMetadata> getOutcomingChanges()  {
        if(this.outcoming == null) {
            this.merge();
        }

        return this.outcoming;
    }

    @Override
    public List<ElementMetadata> getUpdatedList() {
        if(this.updatedList == null) {
            this.merge();
        }

        return this.updatedList;
    }

    private void stampNewList() {
        final StringBuilder stringBuilder = new StringBuilder("Lista aggiornata: [");
        for(int i = 0; i < this.updatedList.size(); i++) {
            stringBuilder.append(this.updatedList.get(i).getID());
            if(i != this.updatedList.size() - 1) {
                stringBuilder.append(" , ");
            } else {
                stringBuilder.append(" ]");
            }
        }

        Log.d("applicazione", stringBuilder.toString());
    }
}
