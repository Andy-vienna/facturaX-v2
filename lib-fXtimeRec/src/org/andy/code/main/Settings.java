package org.andy.code.main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import javax.swing.JOptionPane;

import org.andy.code.dataStructure.entityJSON.JsonSettings;
import org.andy.code.dataStructure.entityJSON.JsonUtil;
import org.andy.code.misc.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Settings {

	private static final Logger logger = LogManager.getLogger(Settings.class);
	private App a = new App();
	
	private final String pgSql = "jdbc:postgresql://%s:%s/%s?currentSchema=public&sslmode=disable";

	private static JsonSettings settings = new JsonSettings();
	private static String sData;

	// ###################################################################################################################################################
	// public teil
	// ###################################################################################################################################################

	public void LoadProgSettings(Path fileJson) {
		LoadSettings(fileJson);
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private void LoadSettings(Path file) {
		
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
		if (settings.dbData == null) {
			JOptionPane.showMessageDialog(null, "<html>settings.json - Inhalt unklar oder nicht lesbar<br>Anwendung wird beendet ...",
					"fXtimeRec", JOptionPane.ERROR_MESSAGE);
			StartUp.gracefulQuit(91);
		}
		sData = String.format(pgSql, settings.dbHost, settings.dbPort, settings.dbData);
		
		if (a.DEBUG) System.out.println(sData);
		
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	static boolean fileExist(String fileName) {
		File f = new File(fileName);
		return f.isFile() ? true : false;
	}
	
	public static boolean isLocked(String fileName) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "rw");
				FileLock lock = randomAccessFile.getChannel().lock()) {
			return lock == null;
		} catch (IOException ex) {
			return true;
		}
	}
	
	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static int getButtonX() {
		return 130;
	}
	
	public static int getButtonY() {
		return 50;
	}
	
	public static JsonSettings getSettings() {
		return settings;
	}

	public static String getsData() {
		return sData;
	}
	
	public void setSettings(JsonSettings settings) {
		Settings.settings = settings;
	}

}
