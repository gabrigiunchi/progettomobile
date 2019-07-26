package model;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import database.DatabaseManager;
import database.DatabaseManagerImpl;
import model.interfaces.Element;
import model.interfaces.ElementMetadata;

/**
 * Classe singleton che contiene la lista dei file gestiti dall'applicazione
 */
public class MyList {
	
	private static List<ElementMetadata> list;
	private static List<Element> prefList;
	
	public static List<ElementMetadata> getList(final Context context) {

		final DatabaseManager databaseManager = DatabaseManagerImpl.getDatabaseManager(context);
		list =  databaseManager.getAllElementMetadata();

		List<ElementMetadata> list2 = new ArrayList<>();
		for(ElementMetadata s: list){
			if(s.getState() != ElementMetadata.ElementState.REMOVED){
				list2.add(s);
			}
		}

		return list2;
	}
}