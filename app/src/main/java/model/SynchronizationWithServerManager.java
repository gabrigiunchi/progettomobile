package model;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.interfaces.ElementMetadata;
import model.interfaces.ServerComunicationManager;
import utility.Utilities;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di che comunica con i server dell'applicazione.
 */
public class SynchronizationWithServerManager implements ServerComunicationManager {

    /**
     * Variabili statiche utilizzato come chiave per ottenere il valore nel Bundle
     */
    public static final String USERNAME_ENTRY = "username";
    public static final String PASSWORD_ENTRY = "password";

    @Override
    public List<ElementMetadata> download(String source, final Bundle options) throws Exception {
        Log.d("synchronizationService", "Download da " + source);

        // Creo la connessione
        final URL url = new URL(source);
        final HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        // Creo i dati da inviare
        final Map<String, String> data = new HashMap<>();
        data.put(USERNAME_ENTRY, options.getString(USERNAME_ENTRY, ""));
        data.put(PASSWORD_ENTRY, options.getString(PASSWORD_ENTRY, ""));

        // Scrivo nello stream i dati
        final OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(this.getPostDataString(data).getBytes());
        outputStream.flush();
        outputStream.close();

        // Leggo la risposta
        if(httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            final InputStream inputStream = httpURLConnection.getInputStream();
            //final List<ElementMetadata> res = Utilities.getListFromStream(inputStream);*/

            final JSONObject response = new JSONObject(Utilities.readStringFromStream(inputStream));
            Log.d("synchronizationService", "Risposta dal server: " + response.toString());

            if(response.getInt("code") == 2) {
                final JSONArray serverData = response.getJSONObject("extra").getJSONArray("data");
                final List<ElementMetadata> res = Utilities.JsonArrayToElementMetadataList(serverData);

                inputStream.close();
                httpURLConnection.disconnect();

                return res;
            } else {
                inputStream.close();
                httpURLConnection.disconnect();

                throw new IllegalArgumentException(response.getString("message"));
            }
        }

        httpURLConnection.disconnect();

        return new ArrayList<>();
    }

    @Override
    public void upload(List<ElementMetadata> outcomingData, String destination, final Bundle options) throws Exception {
        Log.d("synchronizationService", "Upload verso " + destination);

        // Creo la connessione
        final URL url = new URL(destination);
        final HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        httpURLConnection.setDoInput(true);

        // Creo i dati da inviare
        final Map<String, String> data = new HashMap<>();
        data.put(USERNAME_ENTRY, options.getString(USERNAME_ENTRY, ""));
        data.put(PASSWORD_ENTRY, options.getString(PASSWORD_ENTRY, ""));
        data.put("data", Utilities.elementMetadataListToJsonArray(outcomingData).toString());

        // Invio i dati
        final OutputStream outputStream = httpURLConnection.getOutputStream();
        outputStream.write(this.getPostDataString(data).getBytes());
        outputStream.flush();
        outputStream.close();

        final String response = Utilities.readStringFromStream(httpURLConnection.getInputStream());

        Log.d("synchronizationService", "Risposta dal server: " + response);

        httpURLConnection.disconnect();
    }

    private String getPostDataString(Map<String, String> params) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            }
            else {
                result.append("&");
            }

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }
}
