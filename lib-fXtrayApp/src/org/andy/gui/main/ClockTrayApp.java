package org.andy.gui.main;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionListener;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.andy.code.dataStructure.repository.WorkTimeRawRepository;
import org.andy.code.main.StartUp;
import org.andy.code.misc.App;
import org.andy.code.misc.GetId;
import org.andy.gui.iconHandler.StateIcon;

public class ClockTrayApp {
	
	private static App a = new App();
	private static GetId id = new GetId();
	
	enum UiState { NEUTRAL, IN, BREAK }
	private final static String EVENT_IN = "IN";
	private final static String EVENT_BS = "BREAK_START";
	private final static String EVENT_BE = "BREAK_END";
	private final static String EVENT_OU = "OUT";
	private final static boolean[] STATE_IN = new boolean[]{false,true,false,true}; // IN, BREAK_START, BREAK_END, OUT
	private final static boolean[] STATE_BS = new boolean[]{false,false,true,false};
	private final static boolean[] STATE_BE = new boolean[]{false,true,false,true};
	private final static boolean[] STATE_OU = new boolean[]{true,false,false,false};
	private static WorkTimeRawRepository repo = new WorkTimeRawRepository();
	
	static PopupMenu menu = new PopupMenu();
	private static TrayIcon trayIcon;
	private static UiState state = UiState.NEUTRAL;
	
	private static String user = null;
	public static WorkTimeRaw wt = new WorkTimeRaw();
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static void runApp() throws Exception {
		System.setProperty("java.awt.headless", "false");
		var app = new ClockTrayApp(new WorkTimeRawRepository());
		app.start();
	}
	
	public static void actState() {
		setLastState(false);
	}
	
	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private ClockTrayApp(WorkTimeRawRepository repo) {
		ClockTrayApp.repo = repo; ClockTrayApp.user = id.userId();
		wt = repo.findLastEvent(user);
		if (wt == null) { wt = new WorkTimeRaw(); wt.setEvent("OUT"); }
	}

	private void start() throws Exception {
		if (!SystemTray.isSupported()) throw new IllegalStateException("SystemTray nicht verfügbar");

		// PopUp-Menü aufbauen
		addItem(menu, "Kommt",        _ -> writeEvent(EVENT_IN, UiState.IN));
		addItem(menu, "Pause Anfang", _ -> writeEvent(EVENT_BS, UiState.BREAK));
		addItem(menu, "Pause Ende",   _ -> writeEvent(EVENT_BE, UiState.IN));
		addItem(menu, "Geht",         _ -> writeEvent(EVENT_OU, UiState.NEUTRAL));
		menu.addSeparator();
		addItem(menu, "Beenden",      _ -> exit());

		// letzten Zustand lesen und App in diesen Zustand versetzen
		setLastState(true);
	}
	
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

	private void addItem(PopupMenu menu, String label, ActionListener al) {
		MenuItem it = new MenuItem(label);
		it.addActionListener(al);
		menu.add(it);
	}
	
	private static void setMenuState(boolean[] state) {
		for (int n = 0; n < state.length; n++) {
			menu.getItem(n).setEnabled(state[n]);
		}
	}
	
	private void writeEvent(String t, UiState next) { // PopUp-Menü wurde geklickt ...
		ZoneId tz = ZoneId.systemDefault();
		OffsetDateTime ts = OffsetDateTime.now();
		wt = repo.record(t, "DESKTOP", tz.toString(), ts, id.userId(), id.deviceId());
		setLastState(false);
	}
	
	private static void setLastState(boolean firstRun) {
		try {
			switch(wt.getEvent()) { // bei Start Zustand gemäß letztem Eintrag herstellen
				case EVENT_IN -> { setMenuState(STATE_IN); state = UiState.IN; }
				case EVENT_BS -> { setMenuState(STATE_BS); state = UiState.BREAK; }
				case EVENT_BE -> { setMenuState(STATE_BE); state = UiState.IN; }
				case EVENT_OU -> { setMenuState(STATE_OU); state = UiState.NEUTRAL; }
				default       -> { setMenuState(STATE_OU); state = UiState.NEUTRAL; }
			}
			// TrayIcon setzen und Meldugen einblenden
			if (firstRun) {
				trayIcon = new TrayIcon(iconFor(state), "Status: " + typeToText(wt.getEvent()), menu);
				trayIcon.setImageAutoSize(true);
				SystemTray.getSystemTray().add(trayIcon);
				trayIcon.displayMessage(a.NAME + " (" + a.VERSION + ")", "Zeiterfassung aktiv.", TrayIcon.MessageType.INFO);
				return;
			}
			setState(state, wt.getEvent());
			OffsetDateTime ts = wt.getTs();
			DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
			DateTimeFormatter fmtTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMANY);
			String datum = ts.toLocalDate().format(fmtDate);
			String zeit  = ts.toLocalTime().format(fmtTime);
			trayIcon.displayMessage("Erfasst " + typeToText(wt.getEvent()), datum + " | " + zeit, TrayIcon.MessageType.NONE);
		} catch (Exception ex) {
			trayIcon.displayMessage("Fehler", ex.getMessage(), TrayIcon.MessageType.ERROR);
		}
	}
	
	private static String typeToText(String t) {
		return switch (t) {
			case EVENT_IN -> "➜ anwesend";
			case EVENT_BS -> "➜ in Pause";
			case EVENT_BE -> "➜ anwesend";
			case EVENT_OU -> "➜ abwesend";
			default       -> "unbekannt";
		};
	}

	private static void setState(UiState s, String t) {
		OffsetDateTime ts = wt.getTs();
		DateTimeFormatter fmtDate = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMANY);
		DateTimeFormatter fmtTime = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.GERMANY);
		String datum = ts.toLocalDate().format(fmtDate);
		String zeit  = ts.toLocalTime().format(fmtTime);
		state = s;
		trayIcon.setImage(iconFor(s));
		trayIcon.setToolTip(typeToText(t) + " seit: " + datum + " " + zeit);
	}

	private static Image iconFor(UiState s) {
		Image out = switch (s) {
			case IN      -> StateIcon.PLAY.image();
			case BREAK   -> StateIcon.PAUSE.image();
			case NEUTRAL -> StateIcon.STOP.image();
			default      -> StateIcon.STOP.image();
		};
		return out;
	}

	private void exit() {
		SystemTray.getSystemTray().remove(trayIcon);
		StartUp.gracefulQuit(0);
	}

	public static String getUser() {
		return user;
	}

}
