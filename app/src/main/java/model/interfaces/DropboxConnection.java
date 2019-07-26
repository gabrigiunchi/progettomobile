package model.interfaces;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadUploader;
import java.io.IOException;

/**
 * @author Gabriele Giunchi
 *
 * Interfaccia di una connessione a un client Dropbox.
 */
public interface DropboxConnection {

	void upload(String source, String destination) throws IOException, DbxException;
	void delete(String path) throws DbxException;
	boolean fileExists(String path);
	UploadUploader upload(String path) throws DbxException;
	DbxDownloader<FileMetadata> download(String path) throws  DbxException;
}
