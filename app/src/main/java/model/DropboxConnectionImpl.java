package model;

import android.os.Bundle;
import android.util.Log;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadUploader;
import org.json.JSONArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import cryptography.MyEncryptor;
import model.interfaces.DropboxConnection;
import model.interfaces.ElementMetadata;
import model.interfaces.ServerComunicationManager;
import utility.Utilities;

/**
 * @author Gabriele Giunchi
 *
 * Implementazione di ServerComunicationManager che utilizza una connessione verso un client Dropbox.
 * Questa classe implementa anche l'interfacca DropboxConnection, tuttavia i metodi di tale interfaccia non
 * vengono utilizzati all'interno dell'applicazione
 */
public class DropboxConnectionImpl implements DropboxConnection, ServerComunicationManager {

	private final DbxClientV2 client;

	public DropboxConnectionImpl(final String accessToken) {
		DbxRequestConfig config = new DbxRequestConfig("", "");
        this.client = new DbxClientV2(config, accessToken);
	}

	@Override
	public List<ElementMetadata> download(String path, final Bundle options) throws Exception {
		Log.d("dropbox", "Inizio download di " + path);

		final DbxDownloader<FileMetadata> downloader = this.client.files().download(path);

		// Leggo la stringa, la decripto e creo la lista
		final String encryptedData = Utilities.readStringFromStream(downloader.getInputStream());
		final String data = MyEncryptor.decrypt(encryptedData);
		final JSONArray jsonArray = new JSONArray(data);
		final List<ElementMetadata> res = Utilities.JsonArrayToElementMetadataList(jsonArray);

		Log.d("dropbox", "Download terminato");

		downloader.close();
		return res;
	}

	@Override
	public void upload(List<ElementMetadata> list, String destination, final Bundle options) throws Exception {
		Log.d("dropbox", "Inizio upload della lista verso " + destination);

		if(fileExists(destination)) {
			Log.d("dropbox", "File già esistente, sovrascrivo");
			client.files().delete(destination);
		}

		final UploadUploader uploader = client.files().upload(destination);
		final OutputStream stream = uploader.getOutputStream();

		// Creo il json e lo cripto con la chiave di criptazione prima di inviarlo
		final JSONArray jsonArray = Utilities.elementMetadataListToJsonArray(list);
		final String jsonString = jsonArray.toString();
		final String encryptedJson = MyEncryptor.encrypt(jsonString);
		stream.write(encryptedJson.getBytes());

		uploader.finish();
		uploader.close();

		Log.d("dropbox", "Upload terminato con successo");
	}

	@Override
	public void upload(final String source, final String destination) throws IOException, DbxException {
		Log.d("dropbox", "Inizio upload di " + source + " verso " + destination);
		
		final InputStream input = new FileInputStream(new File(source));
		if(fileExists(destination)) {
			Log.d("dropbox", "File già esistente, sovrascrivo");
			client.files().delete(destination);
		}
		
        final UploadUploader uploader = client.files().upload(destination);
        uploader.uploadAndFinish(input);

		Log.d("dropbox", "Upload terminato con successo");
	}

	@Override
	public boolean fileExists(String path) {
		try {
			this.client.files().getMetadata(path);
		} catch (DbxException e) {
			return false;
		}
		return true;
	}

	@Override
	public UploadUploader upload(String path) throws DbxException {
		return this.client.files().upload(path);
	}

	@Override
	public DbxDownloader<FileMetadata> download(String path) throws DbxException {
		return this.client.files().download(path);
	}

	@Override
	public void delete(String path) throws DbxException {
		this.client.files().delete(path);
		Log.d("dropbox", path + " eliminato");
	}
}
