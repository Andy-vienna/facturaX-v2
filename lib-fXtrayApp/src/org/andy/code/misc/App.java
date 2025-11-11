package org.andy.code.misc;

import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.jar.Manifest;

import org.andy.code.main.StartUp;

public class App {
	
	public boolean DEBUG = false;
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
		String[] tmp = selectBuildInfo();
		TIME = tmp[0];
		JDK = tmp[1];
		VERSION = tmp[2];
		NAME = tmp[3];
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	public String[] selectBuildInfo() {
	    String[] tmp = new String[4];

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

	public static void setDB(String dB) {
		App.DB = dB;
	}
}
