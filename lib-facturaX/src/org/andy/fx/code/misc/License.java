package org.andy.fx.code.misc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JOptionPane;

public class License {

	private static final String FILE_REQUEST = System.getProperty("user.dir") + "\\getlic.lic";
	private static final int LICENSE_NONE = 0;
	private static final int LICENSE_DEMO = 1;
	private static final int LICENSE_FULL = 2;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static int getLicense(String file) throws NoSuchAlgorithmException, IOException {
		return handleLicense(file);
	}

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private License() {} // Instanzierung verhindern
	
	private static int handleLicense(String file) throws IOException, NoSuchAlgorithmException {
		String os = System.getProperty("os.name").toLowerCase();
		if (!os.contains("win")) {
			JOptionPane.showMessageDialog(null, "Unbekanntes Betriebssystem, Anwendung wird beendet!", "Fehler", JOptionPane.ERROR_MESSAGE);
			return LICENSE_NONE;
		}

		String sUUID = getWindowsUUID();
		if (Files.exists(Paths.get(file))) {
			String lic = readLicense(file);
			if (hash((sUUID + "-FULL").getBytes()).equals(lic)) {
				return LICENSE_FULL;
			}
			if (hash((sUUID + "-DEMO").getBytes()).equals(lic)) {
				return LICENSE_DEMO;
			}
		} else {
			setLicRequest(sUUID);
		}
		return LICENSE_NONE;
	}

	private static String getWindowsUUID() throws IOException {
		ProcessBuilder builder = new ProcessBuilder("wmic", "csproduct", "get", "UUID");
		builder.redirectErrorStream(true);
		Process process = builder.start();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
			reader.readLine(); // Erste Zeile (Überschrift "UUID") überspringen
			String uuid;
			while ((uuid = reader.readLine()) != null) {
				uuid = uuid.trim(); // Leerzeichen entfernen
				if (!uuid.isEmpty()) { // Falls nicht leer, ist es die UUID
					return uuid;
				}
			}
		}
		return "nothing"; // Falls keine gültige UUID gefunden wurde
	}

	private static boolean setLicRequest(String uuid) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_REQUEST))) {
			writer.write(uuid);
		}
		return true;
	}

	private static String readLicense(String file) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
			return reader.readLine();
		}
	}

	private static String hash(byte[] lic) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA3-512");
		byte[] digest = md.digest(lic);
		StringBuilder hexString = new StringBuilder();
		for (byte b : digest) {
			hexString.append(String.format("%02x", b));
		}
		return hexString.toString();
	}

}
