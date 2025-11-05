package org.andy.fx.code.main;

import static org.andy.fx.code.misc.Password.checkComplexity;
import static org.andy.fx.code.misc.Password.hashPwd;

import java.awt.Color;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.andy.fx.code.dataStructure.entityJSON.JsonDb;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.dataStructure.entityMaster.User;
import org.andy.fx.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.AnmeldeFenster;
import org.andy.fx.gui.misc.MyFlatTabbedPaneUI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class StartUp {

	private static final Logger logger = LogManager.getLogger(StartUp.class);

	private static java.nio.channels.FileChannel LOCK_CH;
	private static java.nio.channels.FileLock LOCK;
	private static java.nio.file.Path LOCK_PATH;

	private static Path fileApp;
	private static Path fileDB;
	private static Path fileAI;

	private static LocalDate dateNow;
	private static String dtNow;
	private static final DateTimeFormatter dfDate = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	// ###################################################################################################################################################
	// Starten der Applikation
	// ###################################################################################################################################################

	public static void main(String[] args) {

		// 1) Logging konfigurieren
		System.setProperty("log4j.configurationFile", "classpath:log4j2.xml");
		logger.debug("FacturaX startet ..."); // zwingt Initialisierung
		Exit.init(); // Exit-Codes laden
		//-----------------------------------------------------------------------------------------------------------------------
		// 2) ShutdownHook initialisieren
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {	releaseSingleInstanceLock(); }));
		//-----------------------------------------------------------------------------------------------------------------------
		// 3) Einstellungs-Dateien festlegen und reagieren, wenn nicht da
		boolean app = Einstellungen.fileExist("settingsApp.json");
		boolean db = Einstellungen.fileExist("settingsDb.json");
		boolean ai = Einstellungen.fileExist("secrets\\settingsAI.json");
		if (!app || !db) {
			JOptionPane.showMessageDialog(null,
					"<html>Anwendungs- und/oder DB-Einstellungen nicht vorhanden<br>Anwendung wird beendet ...",
					"FacturaX v2", JOptionPane.ERROR_MESSAGE);
			StartUp.gracefulQuit(90);
		}
		Path dir = Path.of(System.getProperty("user.dir"));
		fileApp = dir.resolve("settingsApp.json"); // Dateiname anhängen
		fileDB = dir.resolve("settingsDb.json"); // Dateiname anhängen
		if (ai) fileAI = dir.resolve("secrets\\settingsAI.json");
		//-----------------------------------------------------------------------------------------------------------------------
		// 4) Globale Fehlerbehandlung
		Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
			logger.error("Uncaught in " + t.getName(), e);
			gracefulQuit(98);
		});
		//-----------------------------------------------------------------------------------------------------------------------
		// 5) Instanzprüfung
		if (!acquireSingleInstanceLock()) {
			JOptionPane.showMessageDialog(null, "Es läuft bereits eine Instanz von FacturaX v2", "FacturaX v2",
					JOptionPane.ERROR_MESSAGE);
			gracefulQuit(99);
		}
		//-----------------------------------------------------------------------------------------------------------------------
		// 6) aktuelles Datum setzen
		dateNow = LocalDate.now();
		dtNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		//-----------------------------------------------------------------------------------------------------------------------
		// 7) Einstellungen laden
		Einstellungen.LoadProgSettings(fileApp, fileDB, fileAI);
		//-----------------------------------------------------------------------------------------------------------------------
		// 8) UI auf EDT starten
		SwingUtilities.invokeLater(() -> {
			try {
				FlatIntelliJLaf.setup();
				UIManager.setLookAndFeel(new FlatIntelliJLaf());

				UIManager.put("Button.arc", 10);
				UIManager.put("Component.arc", 10);
				UIManager.put("TextComponent.arc", 10);
				UIManager.put("ProgressBar.arc", 10);
				UIManager.put("TabbedPaneUI", MyFlatTabbedPaneUI.class.getName());
				UIManager.put("TabbedPane.tabType", "card");
				UIManager.put("TabbedPane.cardTabSelectionHeight", 0);
				UIManager.put("MenuBar.selectionBackground", Color.LIGHT_GRAY);
				UIManager.put("MenuBar.hoverBackground", Color.LIGHT_GRAY);
				UIManager.put("MenuBar.underlineSelectionColor", Color.LIGHT_GRAY);
				UIManager.put("MenuBar.underlineSelectionBackground", Color.LIGHT_GRAY);
				UIManager.put("MenuItem.selectionBackground", Color.LIGHT_GRAY);
				UIManager.put("MenuItem.hoverBackground", Color.LIGHT_GRAY);
				UIManager.put("MenuItem.underlineSelectionColor", Color.LIGHT_GRAY);
				UIManager.put("MenuItem.underlineSelectionBackground", Color.LIGHT_GRAY);
				UIManager.put("TableHeader.background", new Color(255, 248, 220));
				UIManager.put("TableHeader.foreground", Color.BLACK);

			} catch (Exception ex) {
				logger.error("cannot load FlatIntelliJLaf theme", ex);
			}
			// **************************************************************************************
			// wenn die Einstellung 'mode' auf "create" steht (Neu-Erzeugung aller Datentabellen)
			if (Einstellungen.getDbSettings().dbMode.equals("create")) {
				String eingabe;
				String hinweis = "<html>"
						+ "<span style='font-size:10px; font-weight:bold; color:black;'>Bitte ein Passwort für den Administrator-Zugang erstellen:</span><br>"
						+ "<span style='font-size:10px; font-weight:bold; color:blue ;'>******** user: admin | Rolle: admin ********</span><br>"
						+ "<span style='font-size:10px; font-weight:bold; color:black;'>Dieses Passwort muss den Anforderungen entsprechen:</span><br>"
						+ "<span style='font-size:10px; font-weight:bold; color:red  ;'>[größer 8 Zeichen, a-z, A-Z, 0-9, @#$%^&+=-_!?.]</span></html>";
				UserRepository userRep = new UserRepository();
				User u = new User();
				do {
					eingabe = JOptionPane.showInputDialog(null, hinweis, "Passwort erstellen", JOptionPane.QUESTION_MESSAGE);
					if (eingabe == null) gracefulQuit(55); // wenn nichts eingegeben, dann Ende
				} while (!checkComplexity(eingabe.toCharArray()));
				boolean bCheckComplexity = checkComplexity(eingabe.toCharArray());
				if (bCheckComplexity) {
					char[] passwordChars = eingabe.toCharArray();
					u.setId("admin");
					u.setHash(hashPwd(passwordChars));
					u.setRoles("admin");
					u.setTabConfig(256);
					userRep.insert(u);
				}
				HauptFenster.loadGUI("admin", "no E-Mail", "admin", 768);
			// **************************************************************************************
			} else {
				// ansonsten 'normaler' Start
				new AnmeldeFenster(new UserRepository(), new AnmeldeFenster.AuthCallback() {
					@Override
					public void onSuccess(User u) {
						HauptFenster.loadGUI(u.getId(), u.getEmail(), u.getRoles(), u.getTabConfig());
					}

					public void onCancel() {
						gracefulQuit(1);
					}
				}).show();
			}
		});
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

	public static void gracefulQuit(int code) {
		if (code != 90 && code != 98 && code != 99) {
			try {
				JsonDb sDB = Einstellungen.getDbSettings();
				sDB.dbMode = "none";
				JsonUtil.saveAPP(fileApp, Einstellungen.getAppSettings());
				JsonUtil.saveDB(fileDB, sDB);
			} catch (IOException e) {
				logger.error("saving settings on exit failed", e);
			}
		}
		if (code > 0) logger.info("Shutdown. Exit-Code {}: {}", code, Exit.desc(code));
		Exit.exit(code);
	}

	private static boolean acquireSingleInstanceLock() {
		try {
			LOCK_PATH = java.nio.file.Paths.get(System.getProperty("user.home"), ".facturax", "app.lock");
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

	public static String getDtNow() {
		return dtNow;
	}

	public static final LocalDate getDateNow() {
		return dateNow;
	}

	public static final DateTimeFormatter getDfdate() {
		return dfDate;
	}

	public static Path getFileApp() {
		return fileApp;
	}

	public static Path getFileDB() {
		return fileDB;
	}

	public static Path getFileAI() {
		return fileAI;
	}

}
