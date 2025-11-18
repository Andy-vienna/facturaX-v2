package org.andy.code.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

import org.andy.code.dataStructure.entityJSON.JsonSettings;
import org.andy.code.dataStructure.entityJSON.JsonUtil;
import org.andy.code.misc.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

	private static final Logger logger = LogManager.getLogger(Settings.class);
	private static App a = new App();
	
	private final static String msSql = "jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=%s;trustServerCertificate=%s";
	private final static String pgSql = "jdbc:postgresql://%s:%s/%s?currentSchema=public&sslmode=disable";

	private static JsonSettings settings = new JsonSettings();
	private static String sData;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public static void LoadAllSettings(Path fileJson) {
		LoadSettings(fileJson);
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static void LoadSettings(Path file) {
		
		// ------------------------------------------------------------------------------
		// Einstellungen laden
		// ------------------------------------------------------------------------------
		try {
			settings = JsonUtil.loadSettings(file);
		} catch (IOException e) {
			logger.error("error loading settings: " + e.getMessage());
			StartUp.gracefulQuit(92);
		}
		
		// ------------------------------------------------------------------------------
		// Datenbank Connection strings für Hibernate
		// ------------------------------------------------------------------------------
		if (settings.dbType == null) {
			JOptionPane.showMessageDialog(null, "<html>settings.json - Inhalt unklar oder nicht lesbar<br>Anwendung wird beendet ...",
					"fXtimeRec", JOptionPane.ERROR_MESSAGE);
			StartUp.gracefulQuit(91);
		}
		switch(settings.dbType) {
    	case "mssql" -> sData = String.format(msSql, settings.dbHost, settings.dbPort, settings.dbData, settings.dbEncrypt, settings.dbCert);
    	case "postgre" -> sData = String.format(pgSql, settings.dbHost, settings.dbPort, settings.dbData);
    	}
		
		if (a.DEBUG) System.out.println(sData);
		
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	static boolean fileExist(String fileName) {
		File f = new File(fileName);
		return f.isFile() ? true : false;
	}
	
	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static JsonSettings getSettings() {
		return settings;
	}

	public static String getsData() {
		return sData;
	}

}
