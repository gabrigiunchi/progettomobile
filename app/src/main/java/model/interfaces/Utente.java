package model.interfaces;

import org.json.JSONObject;

import java.net.URL;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di un Utente autenticato con l'applicazione
 */
public interface Utente {

    void setNome(final String nome);
    void setCognome(final String cognome);
    void setUsername(final String username);
    void setPassword(final String password);
    void setPictureURL(final String url);
    String getNome();
    String getCognome();
    String getPassword();
    String getUsername();
    String getPictureURL();
    JSONObject generateJSon();
}
