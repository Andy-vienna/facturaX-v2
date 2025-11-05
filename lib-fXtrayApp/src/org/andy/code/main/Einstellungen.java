package org.andy.code.main;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;

import org.andy.code.dataStructure.entityJSON.JsonDb;
import org.andy.code.dataStructure.entityJSON.JsonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Einstellungen {

	private static final Logger logger = LogManager.getLogger(Einstellungen.class);

	private static JsonDb dbSettings = new JsonDb();
	private static String sData;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public static void LoadProgSettings(Path fileDB) {
		LoadSettings(fileDB);
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static void LoadSettings(Path fileDB) {
		
		// ------------------------------------------------------------------------------
		// App- und DB-Einstellungen laden
		// ------------------------------------------------------------------------------
		try {
			dbSettings = JsonUtil.loadDB(fileDB);
		} catch (IOException e) {
			logger.error("error loading app or db settings: " + e.getMessage());
			StartUp.gracefulQuit(92);
		}
		
		// ------------------------------------------------------------------------------
		// Datenbank Connection strings für Hibernate
		// ------------------------------------------------------------------------------
		if (dbSettings.dbType == null) {
			JOptionPane.showMessageDialog(null, "<html>settingsDb.json - Inhalt unklar oder nicht lesbar<br>Anwendung wird beendet ...",
					"FacturaX v2", JOptionPane.ERROR_MESSAGE);
			StartUp.gracefulQuit(91);
		}
		switch(dbSettings.dbType) {
    	case "mssql" -> sData = "jdbc:sqlserver://" + dbSettings.dbHost + ":" + dbSettings.dbPort + ";databaseName="
    					+ dbSettings.dbData + ";encrypt=" + dbSettings.dbEncrypt + ";trustServerCertificate=" + dbSettings.dbCert;
    	case "postgre" -> sData = "jdbc:postgresql://" + dbSettings.dbHost + ":" + dbSettings.dbPort + "/"
    					+ dbSettings.dbData + "?currentSchema=public&sslmode=disable";
    	}
		
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

	public static JsonDb getDbSettings() {
		return dbSettings;
	}

	public static String getsData() {
		return sData;
	}

}
