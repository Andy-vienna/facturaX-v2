package org.andy.gui;

import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

import org.andy.code.dataStructure.entity.WorkTime;
import org.andy.code.dataStructure.repository.WorkTimeRepository;
import org.andy.code.main.StartUp;
import org.andy.code.misc.App;
import org.andy.code.misc.GetId;

public class ClockTrayApp {
	
	private static App a = new App();
	private static GetId id = new GetId();
	
	enum UiState { NEUTRAL, IN, BREAK }
	private final static String EVENT_IN = "IN";
	private final static String EVENT_BS = "BREAK_START";
	private final static String EVENT_BE = "BREAK_END";
	private final static String EVENT_OU = "OUT";
	private final boolean[] STATE_IN = new boolean[]{false,true,false,true}; // IN, BREAK_START, BREAK_END, OUT
	private final boolean[] STATE_BS = new boolean[]{false,false,true,false};
	private final boolean[] STATE_BE = new boolean[]{false,false,false,true};
	private final boolean[] STATE_OU = new boolean[]{true,false,false,false};
	private final WorkTimeRepository repo;
	
	PopupMenu menu = new PopupMenu();
	private TrayIcon trayIcon;
	private UiState state = UiState.NEUTRAL;
	private String note = null;
	
	private WorkTime wt = new WorkTime();
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static void runApp() throws Exception {
		System.setProperty("java.awt.headless", "false");
		String user = id.userId();
		String deviceId = id.deviceId();
		var app = new ClockTrayApp(new WorkTimeRepository(deviceId, user), user);
		app.start();
	}
	
	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private ClockTrayApp(WorkTimeRepository repo, String user) {
		this.repo = repo;
		wt = repo.findLastEvent(user);
		if (wt == null) { wt = new WorkTime(); wt.setLastEvent("OUT"); }
	}

	private void start() throws Exception {
		if (!SystemTray.isSupported()) throw new IllegalStateException("SystemTray nicht verfügbar");

		// PopUp-Menü aufbauen
		addItem(menu, "Kommt",        _ -> logAndNotify(EVENT_IN, UiState.IN));
		addItem(menu, "Pause Anfang", _ -> logAndNotify(EVENT_BS, UiState.BREAK));
		addItem(menu, "Pause Ende",   _ -> logAndNotify(EVENT_BE, UiState.IN));
		addItem(menu, "Geht",         _ -> logAndNotify(EVENT_OU, UiState.NEUTRAL));
		menu.addSeparator();
		addItem(menu, "Beenden",      _ -> exit());
		
		switch(wt.getLastEvent()) { // bei Start Zustand gemäß letztem Eintrag herstellen
			case EVENT_IN -> { setMenuState(STATE_IN); state = UiState.IN; note = wt.getNote(); }
			case EVENT_BS -> { setMenuState(STATE_BS);  state = UiState.BREAK; note = wt.getNote(); }
			case EVENT_BE -> { setMenuState(STATE_BE); state = UiState.IN; note = wt.getNote(); }
			case EVENT_OU -> { setMenuState(STATE_OU); state = UiState.NEUTRAL; }
			default       -> { setMenuState(STATE_OU); state = UiState.NEUTRAL; }
		}

		// App-Infos schreiben
		@SuppressWarnings("unused")	String[] tmp = id.appInfo();
		
		trayIcon = new TrayIcon(iconFor(state), "Status: " + typeToText(wt.getLastEvent()), menu);
		trayIcon.setImageAutoSize(true);
		SystemTray.getSystemTray().add(trayIcon);
		trayIcon.displayMessage(a.NAME + " (" + a.VERSION + ")", "Zeiterfassung aktiv.", TrayIcon.MessageType.INFO);
	}
	
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

	private void addItem(PopupMenu menu, String label, ActionListener al) {
		MenuItem it = new MenuItem(label);
		it.addActionListener(al);
		menu.add(it);
	}
	
	private void setMenuState(boolean[] state) {
		for (int n = 0; n < state.length; n++) {
			menu.getItem(n).setEnabled(state[n]);
		}
	}
	
	private void logAndNotify(String t, UiState next) { // PopUp-Menü wurde geklickt ...
		if (t.equals("IN")) { note = JOptionPane.showInputDialog(null, "Projekt eingeben ...", "fX-Zeiterfassung", JOptionPane.QUESTION_MESSAGE); }
		try {
			repo.record(t, note);
			setState(next, t);
			switch(t) {
				case EVENT_IN -> setMenuState(STATE_IN);
				case EVENT_BS -> setMenuState(STATE_BS);
				case EVENT_BE -> setMenuState(STATE_BE);
				case EVENT_OU -> setMenuState(STATE_OU);
			}
			trayIcon.displayMessage("Erfasst", typeToText(t), TrayIcon.MessageType.NONE);
		} catch (Exception ex) {
			trayIcon.displayMessage("Fehler", ex.getMessage(), TrayIcon.MessageType.ERROR);
		}
	}

	private static String typeToText(String t) {
		return switch (t) {
			case EVENT_IN -> "Arbeitszeit läuft ...";
			case EVENT_BS -> "Pause ist aktiv ...";
			case EVENT_BE -> "Pause Ende, Arbeitszeit läuft wieder ...";
			case EVENT_OU -> "Arbeitszeit beendet";
			default       -> "Arbeitszeit beendet";
		};
	}

	private void setState(UiState s, String t) {
		state = s;
		trayIcon.setImage(iconFor(s));
		trayIcon.setToolTip("Status: " + typeToText(t));
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

}
