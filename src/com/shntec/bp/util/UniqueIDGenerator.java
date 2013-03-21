/**
 * Generate unique printable character based ID
 */
package com.shntec.bp.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author 1
 *
 */
public class UniqueIDGenerator {
	

	public static String generate() {
		
		String id = null;
		
		// Get a 1024 bit random bytes
		SecureRandom random = new SecureRandom();
		byte randomBytes[] = new byte[128];
		random.nextBytes(randomBytes);
		
		// calculate md5 of random bytes
		MessageDigest md = null;
		
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			ShntecLogger.logger.error("Digest algorithm MD5 is not supported.");
			ShntecLogger.logger.error(e.getMessage());
			return null;
		}
		
		byte[] randomBytesMd5 = md.digest(randomBytes);
		
		// Convert to base64 string
		id = NumberConverter.getInstance().convert(new BigInteger(randomBytesMd5), 8*randomBytesMd5.length);
		
		return id;
		
	}

}
