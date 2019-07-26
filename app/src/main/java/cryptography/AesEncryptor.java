package cryptography;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Oggetto che cripta e decripta dati sulla base dell'algoritmo AES.
 * La chiave di cifratura è di 128 bit
 */
public class AesEncryptor implements Encryptor {

	/**
	 * Lunghezza della chiave richiesta dall'algoritmo espressa in byte
	 */
	public static final int REQUIRED_KEY_LENGTH = 16;
	public static final String ALGORITHM = "AES";
	
	private Cipher encryptor;
	private Cipher decryptor;

	public AesEncryptor(final SecretKey key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		this.encryptor = Cipher.getInstance(ALGORITHM);
		this.decryptor = Cipher.getInstance(ALGORITHM);
		this.encryptor.init(Cipher.ENCRYPT_MODE, key);
		this.decryptor.init(Cipher.DECRYPT_MODE, key);
	}

	@Override
	public String decrypt(final String s) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		byte[] dec = Base64.decode(s, Base64.NO_WRAP);
		byte[] utf8 = this.decryptor.doFinal(dec);
		return new String(utf8, "UTF8");
	}

	@Override
	public String encrypt(final String s) throws UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException {
		final byte[] utf8 = s.getBytes("UTF8");
		byte[] enc = this.encryptor.doFinal(utf8);
		return Base64.encodeToString(enc, Base64.NO_WRAP);
	}
}
