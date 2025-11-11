package org.andy.fx.code.misc;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Manifest;

import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;

public class App {
	
	public static boolean DEBUG = false;
	public static String DB = null;
	public String JDK = null;
	public String LICENSE = null;
	public int MODE = 0;
	public String NAME = null;
	public String TIME = null;
	public String VERSION = null;

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################
	
	public App() {
		DEBUG = Boolean.getBoolean("app.debug");
		LicMode lm = selectLicense(DEBUG);
		String[] tmp = selectBuildInfo(DEBUG);
		TIME = tmp[0];
		JDK = tmp[1];
		VERSION = tmp[2];
		NAME = tmp[3];
		LICENSE = lm.lic();
		MODE = lm.mode();
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	public static String[] selectBuildInfo(boolean debug) {
	    String[] tmp = new String[4];
	    if (debug) return new String[]{"date not relevant for debug", "debug-mode", "debug", "FacturaX v2"};

	    try (InputStream is = StartUp.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
	        if (is == null) return new String[]{"no build date", "no Java version", null, null};
	        Manifest mf = new Manifest(is);
	        
	        String build = mf.getMainAttributes().getValue("Built-Date");
	        Instant instant = Instant.parse(build); // Formattierer für Date-and-Time
    	    ZonedDateTime local = instant.atZone(ZoneId.systemDefault());
    	    tmp[0] = local.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
	        tmp[1] = mf.getMainAttributes().getValue("Build-Jdk-Spec");
	        tmp[2] = mf.getMainAttributes().getValue("Version");
	        tmp[3] = mf.getMainAttributes().getValue("Name");
	        
	        return tmp;
	    } catch (Exception e) {
	        return new String[]{"--.--.----", "xx", "-.-.-", null};
	    }
	}
	
	private record LicMode(String lic, int mode) {}
	private LicMode selectLicense(boolean debug) {
		String lic = null; int mode = 0;
		try {
			if (debug) {
				mode = 3;
			} else {
				mode = License.getLicense(Einstellungen.getFileLicense());
			}
		} catch (NoSuchAlgorithmException | IOException e) {
			mode = 0;
		}
		lic = switch (mode) {
		case 1 -> "Lizenz DEMO";
		case 2 -> "Lizenz OK";
		case 3 -> "DebugMode aktiv";
		default -> "unlizensiertes Produkt";
		};
		return new LicMode(lic, mode);
	}

	public static void setDB(String dB) {
		App.DB = dB;
	}
}
