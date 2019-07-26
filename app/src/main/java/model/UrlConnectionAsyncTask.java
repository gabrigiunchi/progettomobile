package model;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import utility.Utilities;

/**
 * @author Gabriele Giunchi
 *
 * Classe che implementa la connessione ad un URL.
 */
public class UrlConnectionAsyncTask extends AsyncTask<Bundle, Void, JSONObject> {
    private final UrlConnectionListener observer;
    private final Context context;
    private final URL url;

    /**
     * Interfaccia del listener che vuole ricevere la risposta del server e gestirla opportunamente
     */
    public interface UrlConnectionListener {
        int LOGIN_SUCCESS = 0;
        int LOGIN_FAILED = -2;
        int GENERIC_ERROR = -4;

        /**
         * Metodo per gestire la risposta del server.
         * L'oggetto JSONObject è la risposta del server,
         * mentre l'oggetto Bundle contiene le informazioni riguardante errori nel caso la connessione non sia andata a buon fine
         * @param response
         * @param extra
         */
        void handleResponse(JSONObject response, Bundle extra);
    }

    public UrlConnectionAsyncTask(final URL url, final UrlConnectionListener observer, final Context context) {
        this.observer = observer;
        this.context = context;
        this.url = url;
    }

    @Override
    protected JSONObject doInBackground(Bundle... params) {
        try {
            final Bundle data = params[0];

            Log.d("applicazione", "connetto a " + url.toString());

            // Creo la connessione
            final HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            // Scrivo nello stream i parametri
            final OutputStream outputStream = urlConnection.getOutputStream();
            outputStream.write(getPostDataString(data).getBytes());
            outputStream.flush();
            outputStream.close();

            // Leggo la risposta e creo il json
            if(urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                final InputStream inputStream = urlConnection.getInputStream();
                final String response = Utilities.readStringFromStream(inputStream);
                Log.d("applicazione", response);
                final JSONObject responseJson = new JSONObject(response);

                inputStream.close();
                urlConnection.disconnect();

                AccountManager.setError(false);
                return responseJson;
            } else {
                Log.d("applicazione", urlConnection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AccountManager.setError(true);
        return null;
    }

    protected void onPostExecute(JSONObject response) {
        if(response != null) {
            this.observer.handleResponse(response, Bundle.EMPTY);
        } else {
            final Bundle extra = new Bundle();
            extra.putInt("code", -1);
            this.observer.handleResponse(new JSONObject(), extra);
        }
    }

    private String getPostDataString(final Bundle params) throws UnsupportedEncodingException {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String key : params.keySet()) {
            if (first) {
                first = false;
            }
            else {
                result.append("&");
            }

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(params.getString(key), "UTF-8"));
        }
        return result.toString();
    }
}
