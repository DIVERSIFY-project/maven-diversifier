package utils;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

/**
 * Created by nicolas on 25/08/2015.
 */
public class Decryptor {

	private final String keyStr;

	private Key aesKey = null;
	private Cipher cipher = null;

	public Decryptor(String keyStr) {
		this.keyStr = keyStr;
	}

	private void init() throws Exception {
		if (keyStr == null || keyStr.length() != 16) {
			throw new Exception("bad aes key configured");
		}
		if (aesKey == null) {
			aesKey = new SecretKeySpec(keyStr.getBytes(), "AES");
			cipher = Cipher.getInstance("AES");
		}
	}

	synchronized public String decrypt(String text) {
		try {
			init();
			cipher.init(Cipher.DECRYPT_MODE, aesKey);
			return new String(cipher.doFinal(toByteArray(text)));
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	synchronized public byte decryptByte(String text) {
		return Byte.parseByte(decrypt(text));
	}

	synchronized public short decryptShort(String text) {
		return Byte.parseByte(decrypt(text));
	}

	synchronized public int decryptInteger(String text) {
		return Integer.parseInt(decrypt(text));
	}

	synchronized public long decryptLong(String text) {
		return Long.parseLong(decrypt(text));
	}

	synchronized public float decryptFloat(String text) {
		return Float.parseFloat(decrypt(text));
	}

	synchronized public double decryptDouble(String text) {
		return Double.parseDouble(decrypt(text));
	}

	synchronized public boolean decryptBoolean(String text) {
		return Boolean.parseBoolean(decrypt(text));
	}

	synchronized public char decryptChar(String text) {
		return decrypt(text).charAt(0);
	}

	public static byte[] toByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i +
					1), 16));
		}
		return data;
	}
}
