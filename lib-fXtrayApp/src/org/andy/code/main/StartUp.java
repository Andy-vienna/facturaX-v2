package org.andy.code.main;

import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andy.code.httpsServer.ServerHttps;
import org.andy.gui.main.ClockTrayApp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class StartUp {

	private static final Logger logger = LogManager.getLogger(StartUp.class);

	private static java.nio.channels.FileChannel LOCK_CH;
	private static java.nio.channels.FileLock LOCK;
	private static java.nio.file.Path LOCK_PATH;

	private static Path fileDB;

	// ###################################################################################################################################################
	// Starten der Applikation
	// ###################################################################################################################################################

	public static void main(String[] args) {

		// 1) Logging konfigurieren
		System.setProperty("log4j.configurationFile", "classpath:log4j2.xml");
		logger.debug("fX-Zeiterfassung startet ..."); // zwingt Initialisierung
		Exit.init(); // Exit-Codes laden
		//-----------------------------------------------------------------------------------------------------------------------
		// 2) ShutdownHook initialisieren
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {	releaseSingleInstanceLock(); }));
		//-----------------------------------------------------------------------------------------------------------------------
		// 3) Einstellungs-Dateien festlegen und reagieren, wenn nicht da
		boolean db = Einstellungen.fileExist("settingsDb.json");
		if (!db) {
			JOptionPane.showMessageDialog(null,
					"<html>DB-Einstellungen nicht vorhanden<br>Anwendung wird beendet ...",
					"fXtrayApp", JOptionPane.ERROR_MESSAGE);
			StartUp.gracefulQuit(90);
		}
		Path dir = Path.of(System.getProperty("user.dir"));
		fileDB = dir.resolve("settingsDb.json"); // Dateiname anhängen
		//-----------------------------------------------------------------------------------------------------------------------
		// 4) Globale Fehlerbehandlung
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			logger.error("Uncaught in " + t.getName(), e);
			gracefulQuit(98);
		});
		//-----------------------------------------------------------------------------------------------------------------------
		// 5) Instanzprüfung
		if (!acquireSingleInstanceLock()) {
			JOptionPane.showMessageDialog(null, "Es läuft bereits eine Instanz von fX-Zeiterfassung", "FacturaX v2",
					JOptionPane.ERROR_MESSAGE);
			gracefulQuit(99);
		}
		//-----------------------------------------------------------------------------------------------------------------------
		// 6) Einstellungen laden
		Einstellungen.LoadProgSettings(fileDB);
		//-----------------------------------------------------------------------------------------------------------------------
		// 7) UI auf EDT starten
		SwingUtilities.invokeLater(() -> {
			//-------------------------------------------------------------------------------------------------------------------
			// 7.1) // UI-Manager initialisieren
			try {
				FlatIntelliJLaf.setup();
				UIManager.setLookAndFeel(new FlatIntelliJLaf());

				UIManager.put("Button.arc", 10);
				UIManager.put("Component.arc", 10);
				UIManager.put("TextComponent.arc", 10);

			} catch (Exception ex1) {
				logger.error("cannot load FlatIntelliJLaf theme", ex1);
			}
			//-------------------------------------------------------------------------------------------------------------------
			// 7.2) // HTTPS Server initialisieren und starten
			try {
				ServerHttps.startServer();
			} catch (Exception ex2) {
				logger.error("HTTPS Server kann nicht gestartet werden: " + ex2.getMessage());
			}
			//-------------------------------------------------------------------------------------------------------------------
			// 7.1) // App-UI starten
			try {
				ClockTrayApp.runApp();
			} catch (Exception ex3) {
				logger.error("fX-Zeiterfassung Tray-App kann nicht gestartet werden: " + ex3.getMessage());
			}
			//-------------------------------------------------------------------------------------------------------------------
		});
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

	public static void gracefulQuit(int code) {
		if (code > 0) logger.info("Shutdown. Exit-Code {}: {}", code, Exit.desc(code));
		Exit.exit(code);
	}

	private static boolean acquireSingleInstanceLock() {
		try {
			LOCK_PATH = java.nio.file.Paths.get(System.getProperty("user.home"), ".fxtray", "app.lock");
			java.nio.file.Files.createDirectories(LOCK_PATH.getParent());
			LOCK_CH = java.nio.channels.FileChannel.open(LOCK_PATH, java.nio.file.StandardOpenOption.CREATE,
					java.nio.file.StandardOpenOption.WRITE);
			LOCK = LOCK_CH.tryLock(); // entscheidend: OS-Lock
			return LOCK != null;
		} catch (Exception e) {
			return false;
		}
	}

	private static void releaseSingleInstanceLock() {
		try {
			if (LOCK != null && LOCK.isValid())
				LOCK.release();
		} catch (Exception ignore) {
		}
		try {
			if (LOCK_CH != null && LOCK_CH.isOpen())
				LOCK_CH.close();
		} catch (Exception ignore) {
		}
		try {
			if (LOCK_PATH != null)
				java.nio.file.Files.deleteIfExists(LOCK_PATH);
		} catch (Exception ignore) {
		}
	}

	// ###################################################################################################################################################
	// Getter und Setter
	// ###################################################################################################################################################

	public static Path getFileDB() {
		return fileDB;
	}
	
}
