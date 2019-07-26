package cryptography;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.example.utente.progettomobile.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Classe statica per la criptazione di dati che utilizza l'algoritmo AES a 128bit
 */
public class MyEncryptor {

	public static final String ALGORITHM = "AES";
	public static final int REQUIRED_KEY_LENGTH = AesEncryptor.REQUIRED_KEY_LENGTH;
	private static final String DEFAULT_PASSWORD = "yxc537y90wFEeQLz";
	private static Encryptor encryptor;
	private static byte[] key;

	private MyEncryptor() {
		try {
			final SecretKey secretKey = new SecretKeySpec(DEFAULT_PASSWORD.getBytes(), ALGORITHM);
			encryptor = new AesEncryptor(secretKey);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}

	public static void init(final SecretKey secretKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		encryptor = new AesEncryptor(secretKey);
		Log.d("applicazione", "MyEncriptor inizializzato");
		key = secretKey.getEncoded();
	}

	public static void init(final byte[] bytes) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		init(new SecretKeySpec(bytes, ALGORITHM));
	}
	
	public static String encrypt(final String s) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		return encryptor.encrypt(s);
	}

	public static String decrypt(final String s) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		return encryptor.decrypt(s);
	}

	public static SecretKey generateKey() {
		final KeyGenerator keyGenerator;
		try {
			keyGenerator = KeyGenerator.getInstance(ALGORITHM);
			keyGenerator.init(REQUIRED_KEY_LENGTH * 8);
			return keyGenerator.generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Salva la chiave con cui l'encryptor è inizializzato.
     */
	public static void storeKey(final Context context) {
		try {
			final OutputStream outputStream = context.openFileOutput(context
					.getString(R.string.encryption_key__preferences_entry), Context.MODE_PRIVATE);
			outputStream.write(key, 0, key.length);
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Restituisce la chiave salvata in precedenza con il metodo StoreKey.
	 * @return
     */
	public static byte[] loadStoredKey(final Context context) {
		try {
			final InputStream inputStream = context.openFileInput(context.getString(R.string.encryption_key__preferences_entry));
			final byte[] key = new byte[MyEncryptor.REQUIRED_KEY_LENGTH];
			inputStream.read(key, 0, key.length);
			inputStream.close();
			return key;
		} catch (IOException e) {
			return new byte[0];
		}
	}

	/**
	 * Converte una variabile di tipo long in una stringa che rappresenta una chiave di criptazione valida
	 * per l'algoritmo AES a 128bit
	 * @param value
	 * @return
     */
	public static String transformIntoKey(final long value) {
		final String valueString = Long.toString(value);
		try {
			/**
			 * Per trasformare il valore in una chiave cripto la rappresentazione in stringa del valore
			 * utilizzando una chiave costruita a partire dal valore stesso.
			 */
			final String tempKey = fixLenght(valueString, REQUIRED_KEY_LENGTH);
			final Encryptor encryptor = new AesEncryptor(new SecretKeySpec(tempKey.getBytes(), AesEncryptor.ALGORITHM));
			String key = encryptor.encrypt(valueString);

			return fixLenght(key, REQUIRED_KEY_LENGTH);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			return fixLenght(valueString, REQUIRED_KEY_LENGTH);
		}
	}

	private static String fixLenght(final String s, int length) {
		if(s.length() > length) {
			return s.substring(0, length);
		}

		final StringBuilder stringBuilder = new StringBuilder(s);
		for(int i = 0; i < MyEncryptor.REQUIRED_KEY_LENGTH - s.length(); i++) {
			stringBuilder.append("0");
		}
		return stringBuilder.toString();
	}
}