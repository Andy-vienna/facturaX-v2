package org.andy.fx.code.misc;

import java.util.Arrays;
import java.util.regex.Pattern;

import com.kosprov.jargon2.api.Jargon2;

public class Password {

	/*
	public static void main(String[] args) {
		byte[] pwd = new byte[] {'M','i','t','M','u','e','h','l','_','4','2'};
		//String storedHash = "";

		String hashedPassword = pwHash(pwd);

		System.out.println("Gehashtes Passwort: " + hashedPassword);

		//if (verify(pwd, storedHash)) {
		//	System.out.println("✅ Passwort ist korrekt!");
		//} else {
		//	System.out.println("❌ Falsches Passwort!");
		//}
	}
	 */

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static boolean checkComplexity(char[] password) {
		return checkSigns(password);
	}

	public static String hashPwd(char[] password) {
		return pwHash(password);
	}

	public static boolean verifyPwd(char[] password, String hash) {
		return verify(password, hash);
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private Password() {} // Instanzierung verhindern
	
	private static String pwHash(char[] password) {
		byte[] pwdBytes = toBytes(password);
		try {
			return Jargon2.jargon2Hasher()
					.type(Jargon2.Type.ARGON2id)
					.memoryCost(131072)
					.timeCost(12)
					.parallelism(2)   // Reduziert auf 2 Threads
					.saltLength(32)
					.hashLength(64)
					.password(pwdBytes)
					.encodedHash();
		} finally {
			Arrays.fill(pwdBytes, (byte) 0);  // Sichere Löschung
		}
	}

	private static boolean verify(char[] password, String hashedPassword) {
		byte[] pwdBytes = toBytes(password);
		try {
			return Jargon2.jargon2Verifier()
					.hash(hashedPassword)
					.password(pwdBytes)
					.verifyEncoded();
		} finally {
			Arrays.fill(pwdBytes, (byte) 0);  // Speicher überschreiben
		}
	}

	private static boolean checkSigns(char[] pwArray) {
		String password = String.valueOf(pwArray);
		boolean isValid = Pattern.compile("(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=\\-_!?\\.]).{8,}")
				.matcher(password)
				.matches();
		Arrays.fill(pwArray, ' ');  // Nach Prüfung löschen
		return isValid;
	}

	private static byte[] toBytes(char[] password) {
		if (password == null) {
			return null;
		}
		byte[] bytes = new byte[password.length];
		for (int i = 0; i < password.length; i++) {
			bytes[i] = (byte) password[i];
		}
		return bytes;
	}

}


