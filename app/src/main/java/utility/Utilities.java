package utility;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import model.ElementMetadataImpl;
import model.interfaces.ElementMetadata;

/**
 * @author Gabriele Giunchi
 * Classe con metodi statici di utilità
 */
public final class Utilities {

	private Utilities() { }

	/**
	 * Converte un array json in una lista di ElementMetadata
	 * @param jsonArray
	 * @return
     */
	public static List<ElementMetadata> JsonArrayToElementMetadataList(final JSONArray jsonArray) {
		final List<ElementMetadata> list = new ArrayList<>();

		for(int i = 0; i < jsonArray.length(); i++) {
			try {
				ElementMetadata elementMetadata = new ElementMetadataImpl(jsonArray.getJSONObject(i));
				list.add(elementMetadata);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	/**
	 * Legge una stringa da uno stream di dati.
	 * @param inputStream
	 * @return
	 * @throws IOException
     */
	public static String readStringFromStream(final InputStream inputStream) throws IOException {
		int byteRead = 1;
		byte bytes[] = new byte[1024];
		final StringBuilder stringBuilder = new StringBuilder();

		while(byteRead > 0) {
			byteRead = inputStream.read(bytes, 0, bytes.length);

			if(byteRead > 0) {
				stringBuilder.append(new String(bytes, 0, byteRead));
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * Converte una lista di ElementMetadata in un array json
	 * @param list
	 * @return
	 * @throws JSONException
     */
	public static JSONArray elementMetadataListToJsonArray(final List<ElementMetadata> list) throws JSONException {
		final JSONArray array = new JSONArray();
		for(ElementMetadata s : list) {
			array.put(s.generateJSON());
		}
		return array;
	}

	/**
	 * Scrive una lista di ElementMetadata in uno stream di dati. La lista viene prima convertita in un array json
	 * @param list
	 * @param stream
	 * @throws IOException
	 * @throws JSONException
     */
	public static void writeListToStream(final List<ElementMetadata> list, final OutputStream stream) throws IOException, JSONException {
		stream.write(elementMetadataListToJsonArray(list).toString().getBytes());
	}

	/**
	 * Legge una lista di ElementMetadata da uno stream di dati.
	 * La funzione legge prima una stringa che viene poi convertita in array json.
	 * Se ben composto l'array json viene poi convertito nella lista.
	 * @param inputStream
	 * @return
	 * @throws IOException se si verifica un errore in lettura
	 * @throws JSONException se l'array json non corrisponde ad un array di ElementMetadata
     */
	public static List<ElementMetadata> getListFromStream(final InputStream inputStream) throws IOException, JSONException {
		final String json = readStringFromStream(inputStream);
		JSONArray jsonArray = new JSONArray(json);
		return JsonArrayToElementMetadataList(jsonArray);
	}
}
