package model.interfaces;

import java.util.List;

/**
 * @author Gabriele Giunchi
 *
 * Un oggetto di tipo DataSynchronizer effettua la sincronizzazione tra due liste di ElementMetadata
 * e fornisce come risultato la lista aggiornata.
 *
 */
public interface DataSynchronizer {

	/**
	 * Restituisce la lista aggiornata ottenuta come sincronizzazione di due liste distinte di ElementMetadata.
	 * Esempio:
	 *
	 * list1 = {1(A), 2(A), 3(M)}
	 * list2 = {3(M), 4(A), 5(A)}.
	 *
	 * A e M sono lo stato dell'elemento (Aggiunto e modificato), gli altri stati sono S(sincronizzato) e R(rimosso)
	 *
	 * La lista risultante è = {1(S), 2(S), 3(S), 4(S), 5(S)}.
	 * Sono stati aggiunti gli elementi 1,2,4,5 e l'elemento 3 è stato aggiornato.
	 *
	 * Esempio 2:
	 *
	 * list1 = {1(S), 2(A), 3(R)}
	 * list2 = {1(S), 3(S), 4(A)}
	 *
	 * Lista risultato = {1(S), 2(A), 4(S)}.
	 * In questo caso l'elemento 3 è stato rimosso
	 *
	 * @return
     */
	List<ElementMetadata> getUpdatedList();

	/**
	 * Supponendo di avere due liste distinte list1 e list2, questo metodo fornisce la lista di elementi
	 * che list1 non possedeva al momento della sincronizzazione (o non possedeva la versione aggiornata dell'elemento).
	 * In questa lista possono rientrano anche gli elementi rimossi
	 *
	 * Esempio:
	 * list1 = {1(A), 2(A), 3(A)}
	 * list2 = {4(A), 5(A), 6(A)}
	 * Dove la lettera tra parentesi corrisponde allo stato dell'elemento (A -> aggiunto
	 * 																	   R -> rimosso
	 * 																	   S -> sincronizzato
	 * 																	   M -> modificato)
	 *
	 * Gli elementi incoming sono {4, 5, 6}
	 *
	 * Nel caso di elementi con id uguale si confronta la data di ultima modifica, scegliendo quello più recente.
	 *
	 * Esempio:
	 * list1 = {1(M), 2(M), 3(M), 4(S)}
	 * list2 = {1(M), 2(M), 3(M), 4(R)}
	 *
	 * Se l'elemento 1 di list2 è stato modificato più recentemente del rispettivo in list1 allora viene aggiunto
	 * alla lista degli elementi entranti.
	 * Questo ragionamento viene ripetuto per tutti gli altri elementi coinvolti nella sincronizzazione
	 * L'elemento 4 è stato rimosso e viene aggiunto alla lista degli elementi entranti poichè
	 * bisogna tener traccia di questa informazione
	 *
	 * @return
     */
	List<ElementMetadata> getIncomingChanges();

	/**
	 * Supponendo di avere due liste distinte list1 e list2, questo metodo fornisce la lista di elementi
	 * che list1 non possedeva al momento della sincronizzazione (o non possedeva la versione aggiornata dell'elemento).
	 * In questa lista possono rientrano anche gli elementi rimossi
	 *
	 * Esempio:
	 * list1 = {1(A), 2(A), 3(A)}
	 * list2 = {4(A), 5(A), 6(A)}
	 * Dove la lettera tra parentesi corrisponde allo stato dell'elemento (A -> aggiunto
	 * 																	   R -> rimosso
	 * 																	   S -> sincronizzato
	 * 																	   M -> modificato)
	 *
	 * Gli elementi outcoming sono {1, 2, 3}
	 *
	 * Nel caso di elementi con id uguale si confronta la data di ultima modifica, scegliendo quello più recente.
	 *
	 * Esempio:
	 * list1 = {1(M), 2(M), 3(M), 4(M)}
	 * list2 = {1(M), 2(M), 3(M), 4(S)}
	 *
	 * Se l'elemento 1 di list1 è stato modificato più recentemente del rispettivo in list2 allora viene aggiunto
	 * alla lista degli elementi uscenti.
	 * Questo ragionamento viene ripetuto per tutti gli altri elementi coinvolti nella sincronizzazione
	 * L'elemento 4 è stato rimosso e viene aggiunto alla lista degli elementi uscenti
	 *
	 * @return
	 */
	List<ElementMetadata> getOutcomingChanges();
}
