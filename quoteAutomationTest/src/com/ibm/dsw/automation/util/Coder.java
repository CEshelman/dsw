package com.ibm.dsw.automation.util;

import java.io.IOException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 */
public class Coder {

	private static final String ALGORITHM_DES = "DES";

	//private static final String SEED = "";
	private static final String key = "QKePFuDIolc=";

	//private static final String key = "abcdefg";

	/**
	 * @param byte[] key
	 * @return String 
	 */
	private static String encryptBASE64(byte[] key) {
		return (new BASE64Encoder()).encodeBuffer(key);
	}

	/**
	 * @param String key
	 * @return byte[] 
	 */
	private static byte[] decryptBASE64(String key) throws IOException{
		return (new BASE64Decoder()).decodeBuffer(key);
	}

	/**
	 * @param String seed
	 * @return String 
	 */
//	public static String initKey(String seed) throws Exception{
//		SecureRandom secureRandom = null;
//
//		if (seed != null) {
//			secureRandom = new SecureRandom(decryptBASE64(seed));
//		} else {
//			secureRandom = new SecureRandom();
//		}
//
//		KeyGenerator kg = KeyGenerator.getInstance(ALGORITHM_DES);
//		kg.init(secureRandom);
//
//		SecretKey secretKey = kg.generateKey();
//
//		return encryptBASE64(secretKey.getEncoded());
//	}


	private static Key toKey(byte[] key) throws Exception {
		DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM_DES);
		SecretKey secretKey = keyFactory.generateSecret(dks);
		return secretKey;
	}


	private static byte[] desEncrypt(byte[] data, String key) throws Exception{
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
		cipher.init(Cipher.ENCRYPT_MODE, k);
		return cipher.doFinal(data);
	}


	private static byte[] desDecrypt(byte[] data, String key) throws Exception {
		Key k = toKey(decryptBASE64(key));
		Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
		cipher.init(Cipher.DECRYPT_MODE, k);
		return cipher.doFinal(data);
	}


	public static String encrypt(String data) throws Exception{
		byte[] encrypted = desEncrypt(data.getBytes(), key);
		String str = encryptBASE64(encrypted);
//		str = str.substring(0, str.length() - 1);
		return str;
	}

	public static String decrypt(String data) throws Exception{
//		byte[] decrypted = desDecrypt(decryptBASE64(data + "\n"), key);
		byte[] decrypted = desDecrypt(decryptBASE64(data), key);
		return new String(decrypted);
	}
}
