package org.andy.gui.main.panels;

import static org.andy.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.andy.code.dataExport.ExcelWorkTimeSheet;
import org.andy.code.dataStructure.entity.TimeAccount;
import org.andy.code.dataStructure.entity.WorkTime;
import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.andy.code.dataStructure.entity.WorkTimeSheet;
import org.andy.code.dataStructure.repository.TimeAccountRepository;
import org.andy.code.dataStructure.repository.WorkTimeRawRepository;
import org.andy.code.dataStructure.repository.WorkTimeRepository;
import org.andy.code.dataStructure.repository.WorkTimeSheetRepository;
import org.andy.code.main.Settings;
import org.andy.code.misc.App;
import org.andy.code.misc.BD;
import org.andy.code.misc.FileSelect;
import org.andy.code.workTime.WorkTimeLoader;
import org.andy.gui.dialogs.WorkTimeDialog;
import org.andy.gui.iconHandler.ButtonIcon;
import org.andy.gui.iconHandler.FileIcon;
import org.andy.gui.main.MainWindow;
import org.andy.gui.main.panels.elements.WorkTimeElement;
import org.andy.gui.misc.BusyDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class WorkTimePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(WorkTimePanel.class);
	private static App a = new App();
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    
    private JPanel currentlySelectedPanel = null; // Zeiger für die aktuell angewählte Zeile
    private long panelId = 0; // Integer für das angewählte Panel

    private DayTimes times[] = null;
    private WorkTimeElement[] wtp = new WorkTimeElement[50];
    private JTextField stunden = new JTextField();
    private JTextField stundenPM = new JTextField();
    private static JButton[] btn = new JButton[5];
    private JLabel lblFileTyp = new JLabel();
    
    private final DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("[MMMM][MMM]") // voll oder kurz
            .toFormatter(Locale.GERMAN);
    private final String msg = "<html>"
			+ "<span style='font-size:12px;color:black;font-weight:plain;'>Nach dem Export sind keine weiteren Änderungen im</span><br>"
			+ "<span style='font-size:14px;color:blue;font-weight:bold;'>&#x2B9E;</span>"
			+ "<span style='font-size:14px;color:blue;font-weight:bold;'>  %s</span><br>"
			+ "<span style='font-size:12px;color:black;font-weight:plain;'>mehr möglich, bist du sicher ?</span>";
    private final Object[] opt = {"Ja", "Abbruch"};
    
    private final WorkTimeRepository wtRepo = new WorkTimeRepository();
    private final TimeAccountRepository taRepo = new TimeAccountRepository();
    private List<WorkTime> wt = null; private TimeAccount ta = null;
    private static long[] id; private static BigDecimal worktime = BD.ZERO; private static  BigDecimal overtime = BD.ZERO;
    private OffsetDateTime[] originalIn = null; private OffsetDateTime[] originalOut = null;
    private String user; private int monthIndex = 0; private String month = null; private BigDecimal hoursDay = null;
    private static boolean isCalc = false; private static boolean isAfter = false;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public WorkTimePanel(String month, String user, BigDecimal hoursDay) {
        setLayout(null);
        this.month = month; this.user = user; this.hoursDay = hoursDay;
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Arbeitszeit für " + month + " " + Settings.getSettings().year
        );
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);

        int yearInt = Settings.getSettings().year;
        Month m = Month.from(fmt.parse(month)); // z.B. "Februar", "März"
        int days = YearMonth.of(yearInt, m).lengthOfMonth();

        monthIndex = m.ordinal(); // 0..11
        isAfter = false;
        
        LocalDate from = LocalDate.of(yearInt, m, 1);
        LocalDate to   = LocalDate.of(yearInt, m, days);

        wt = new ArrayList<>(); ta = new TimeAccount();
        wt = wtRepo.findDaysForUser(from, to, user); ta = taRepo.findByUserAndYear(user, yearInt);
        if (ta == null) {
        	TimeAccount h = new TimeAccount();
        	h.setTiPrinted(0);
        	h.setUserName(user);
        	h.setContractHours(BD.ZERO);
        	h.setOverTime(BD.ZERO);
        	h.setYear(yearInt);
        	taRepo.save(h);
        }
        
        times = findRange(wt);
        YearMonth current = YearMonth.now();
		YearMonth given   = YearMonth.of(yearInt, m.getValue());
		isAfter = current.isAfter(given);
        
        buildPanel(m, days, yearInt, hoursDay);
    }
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
	private void buildPanel(Month m, int daysInMonth, int jahr, BigDecimal hoursDay) {
		Dimension size = new Dimension(0,0); int x = 0;

		// Listener, die wir an die Kind-Panels weitergeben
        PanelClickListener clickListener = new PanelClickListener();
        ChildFocusListener focusListener = new ChildFocusListener();
		
		JLabel[] header = new JLabel[7];
		String[] lbl = new String[] { "Datum", "Anfang", "Ende", "Pause (h)", "Stunden", "+/-", "Bemerkung" };
		for (int i = 0; i < lbl.length; i++) {
			header[i] = new JLabel(lbl[i]);
			header[i].setHorizontalAlignment(SwingConstants.CENTER);
			if (i < 3) header[i].setBounds(10 + (i * 150), 30, 150, 25);
			if (i > 2 && i < 6) header[i].setBounds(440 + ((i - 3) * 100), 30, 150, 25);
			if (i == 6) header[i].setBounds(740 + ((i - 5) * 200), 30, 150, 25);
			add(header[i]);
		}
		
		for (int i = 0; i < times.length; i++) wtp[i] = null;
		for (int i = 0; i < times.length; i++) {
			LocalDate d = LocalDate.of(jahr, m, i + 1);
			try { wtp[i] = new WorkTimeElement(); } catch (IOException e) { logger.error("error creating WorkTimePanel: ", e); }
			size = wtp[0].getPreferredSize();
			wtp[i].setDatum(d);
			wtp[i].setBounds(10, 60 + (i * 25), size.width, size.height);

			// 2. Verwende die neuen Hilfsmethoden, um die Listener hinzuzufügen
			wtp[i].addRecursiveMouseListener(clickListener);
			wtp[i].addRecursiveFocusListener(focusListener);

            // Speichere eine ID (optional, aber nützlich für das Label)
			wtp[i].putClientProperty("panelId", i + 1);
            
			add(wtp[i]);
			x = times.length;
		}
		
		JSeparator sep = new JSeparator();
		sep.setForeground(Color.DARK_GRAY);
        sep.setBounds(560, 60 + (x * 25), 200, 5);
        sep.setOrientation(SwingConstants.HORIZONTAL);
        add(sep);
        
        JLabel lblSum = new JLabel("Summen:");
		lblSum.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSum.setBounds(410, 65 + (x * 25), 150, 25);
		lblSum.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblSum);
		
		stunden.setFont(new Font("Tahoma", Font.BOLD, 12));
		stunden.setBounds(560, 65 + (x * 25), 100, 25);
		stunden.setHorizontalAlignment(SwingConstants.RIGHT);
		stunden.setFocusable(false);
		stunden.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(stunden);
		
		stundenPM.setFont(new Font("Tahoma", Font.BOLD, 12));
		stundenPM.setBounds(660, 65 + (x * 25), 100, 25);
		stundenPM.setHorizontalAlignment(SwingConstants.RIGHT);
		stundenPM.setFocusable(false);
		stundenPM.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(stundenPM);
		
		btn[0] = createButton("<html>berechnen<br>und speichern</html>", ButtonIcon.SAVE.icon(), null);
		btn[0].setBounds(970, (times.length * 25) + 85, Settings.getButtonX() + 30, Settings.getButtonY());
		btn[0].setEnabled(true);
		btn[0].addActionListener(_ -> doUpdate(m, jahr));
		add(btn[0]);
		
		btn[1] = createButton("<html>Monats-<br>abschluss</html>", ButtonIcon.EXPORT.icon(), null);
		btn[1].setBounds(1130, (times.length * 25) + 85, Settings.getButtonX(), Settings.getButtonY());
		btn[1].addActionListener(e -> doCloseMonth(e, daysInMonth, m, jahr, user));
		add(btn[1]);
		
		btn[2] = createButton("<html>Zeile<br>einfügen</html>", ButtonIcon.INSERT.icon(), null);
		btn[2].setBounds(170, (times.length * 25) + 85, Settings.getButtonX(), Settings.getButtonY());
		btn[2].setEnabled(true);
		btn[2].addActionListener(_ -> doInsertLine(daysInMonth, m, jahr));
		add(btn[2]);
		
		btn[3] = createButton("<html>Stepelungen<br>laden</html>", ButtonIcon.IMPORT.icon(), null);
		btn[3].setBounds(10, (times.length * 25) + 85, Settings.getButtonX() + 30, Settings.getButtonY());
		btn[3].setEnabled(true);
		btn[3].addActionListener(e -> doImportRawData(e, m, jahr, user));
		add(btn[3]);
		
		btn[4] = createButton("<html>Zeile<br>löschen</html>", ButtonIcon.DEL.icon(), null);
		btn[4].setBounds(300, (times.length * 25) + 85, Settings.getButtonX(), Settings.getButtonY());
		btn[4].setEnabled(true);
		btn[4].addActionListener(_ -> doDeleteLine(panelId));
		add(btn[4]);
		
		JLabel hinweis = new JLabel("<html>Arbeitszeit vom " + month + " bereits abgeschlossen, keine Änderungen mehr möglich ...<br>"
				+ "download durch Klick auf Dateisymbol</html>");
		hinweis.setFont(font); hinweis.setForeground(titleColor);
		hinweis.setBounds(10 + size.width - 655, (times.length * 25) + 95, 650, 50);
		hinweis.setHorizontalAlignment(SwingConstants.RIGHT); hinweis.setVisible(false);
		add(hinweis);
		
		// Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(10 + size.width - 655, (times.length * 25) + 100, 50, 40);
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					WorkTimeSheetRepository azRepo = new WorkTimeSheetRepository();
					String outputPath;
					outputPath = FileSelect.choosePath(Settings.getSettings().workpath);
					Path path = Paths.get(outputPath);
					if (outputPath.equals(FileSelect.getNotSelected())) {
						return; // nichts ausgewählt
					}
					try {
						azRepo.exportFile(user, jahr, monthIndex + 1, path);
					} catch (Exception e1) {
						logger.error("Fehler beim speichern der Datei " + outputPath + ": " + e1.getMessage());
					}
				}
			}
		});
		lblFileTyp.setVisible(false);
		add(lblFileTyp);
		int heightExtension = 155;
		
		btn[0].setVisible(true); btn[1].setVisible(true);
		ta = taRepo.findByUserAndYear(user, jahr);
		if (ta.getTiPrinted() > 0) {
			if (getBit(ta.getTiPrinted(), monthIndex)) {
				WorkTimeSheetRepository azRepo = new WorkTimeSheetRepository();
				WorkTimeSheet az = azRepo.findByUserYearMonth(user, jahr, monthIndex + 1);
				btn[0].setVisible(false); btn[1].setVisible(false); btn[2].setVisible(false); btn[3].setVisible(false); btn[4].setVisible(false);
				hinweis.setVisible(true);
				
				setIcon(az.getDateiname());
				lblFileTyp.setVisible(true); heightExtension = heightExtension + 0;
			}
		}
		
		doLoadData();
		BigDecimal[] val = doSummeStunden(times.length);
		stunden.setText(val[0].toString() + " h"); worktime = val[0];
		stundenPM.setText(val[1].toString() + " h"); overtime = val[1];
		
		if (times.length > 0) { setPreferredSize(new Dimension(size.width + 20, (times.length * size.height) + heightExtension)); return; }
		size.width = 1270; size.height = 155; setPreferredSize(size);
	}
	
	//###################################################################################################################################################
	// Datenbank Operationen
	//###################################################################################################################################################
	
	private record DayTimes(long id, LocalDate date, LocalTime wStart, LocalTime wEnd, BigDecimal breakTime, BigDecimal workTime, BigDecimal plusMinus, String reason) {}
	private DayTimes[] findRange(List<WorkTime> listWt) {
		WorkTime w = null;
		LocalDate wDay = null; LocalTime wStart = null; LocalTime wEnd = null;
		BigDecimal breakTime = BD.ZERO; BigDecimal workTime = BD.ZERO; BigDecimal plusMinus = BD.ZERO;
		String reason = ""; int num = 0;
		
		originalIn = new OffsetDateTime[listWt.size()]; originalOut = new OffsetDateTime[listWt.size()];
		DayTimes[] times = new DayTimes[listWt.size()]; // Anzahl der vollständigen Tage mit IN + OUT
		try {
			for (num = 0; num < listWt.size(); num++) {
				w = listWt.get(num);
				
				originalIn[num] = w.getTsIn(); originalOut[num] = w.getTsOut();
				LocalDate d = originalIn[num].toLocalDate();
				LocalTime tIn = originalIn[num].toLocalTime(); LocalTime tOut = originalOut[num].toLocalTime();
				
				if (w.getTsIn() != null) wDay = d;
				if (w.getTsIn() != null) { wStart = tIn; } else { wStart = LocalTime.of(0,  0); }
				if (w.getTsOut() != null) { wEnd = tOut; } else { wEnd = LocalTime.of(0,  0); }
				if (w.getReason() != null || !w.getReason().isBlank()) reason = w.getReason().trim();
				if (w.getBreakTime() != null) breakTime = w.getBreakTime();
				if (w.getWorkTime() != null) workTime = w.getWorkTime();
				if (w.getPlusMinus() != null) plusMinus = w.getPlusMinus();
				times[num] = new DayTimes(w.getId(), wDay, wStart, wEnd, breakTime, workTime, plusMinus, reason);
			}
		} catch (IndexOutOfBoundsException ex) {
			System.out.println("letzter Datensatz erreicht ...");
		}
		return times;
	}
	
	private void doLoadData(){
		id = new long[times.length];
		for (int i = 0; i < times.length; i++) {
			
			if (times == null) continue;
			id[i] = times[i].id;
			wtp[i].setDatum(times[i].date);
			wtp[i].setStart(times[i].wStart);
			wtp[i].setEnd(times[i].wEnd);
			wtp[i].setPause(times[i].breakTime);
			wtp[i].setStunden(times[i].workTime);
			wtp[i].setPlusMinus(times[i].plusMinus);
			wtp[i].setProjekt(times[i].reason());
			wtp[i].setOriginalIn(originalIn[i]);
			wtp[i].setOriginalOut(originalOut[i]);
			wtp[i].setHoursDay(hoursDay);
		}
	}
	
	private void doImportRawData(ActionEvent e, Month m, int year, String user) {
		Component c = (Component) e.getSource();
    	Window owner = SwingUtilities.getWindowAncestor(c);
    	
    	WorkTimeRawRepository repo = new WorkTimeRawRepository();
    	WorkTimeRaw wtr = new WorkTimeRaw();
    	
    	WorkTimeLoader wtl = new WorkTimeLoader();
    	List<String> events = wtl.syncEvents(user);
    	
    	for (String jsonEvent : events) {
    	    // Den String wieder zu einem Objekt machen
    	    JSONObject eventObj = new JSONObject(jsonEvent);
    	    
    	    // Einzelne Werte über ihre Keys abgreifen
    	    String event = eventObj.getString("event");
    	    String userName = eventObj.getString("username");
    	    String source = eventObj.getString("source");
    	    String deviceId = eventObj.getString("deviceid");
    	    String tz = eventObj.getString("tz");
    	    String sts = eventObj.getString("ts");
    	    
    	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	    LocalDateTime ldt = LocalDateTime.parse(sts, formatter);

    	    // In die Zelle "einbetten" und dann zu OffsetDateTime konvertieren
    	    OffsetDateTime ts = ldt.atZone(ZoneId.of(tz)).toOffsetDateTime();
    	    
    	    wtr.setId(null);
    	    wtr.setEvent(event);
    	    wtr.setUserName(userName);
    	    wtr.setSource(source);
    	    wtr.setDeviceId(deviceId);
    	    wtr.setTimeZoneId(tz);
    	    wtr.setTs(ts);
    	    
    	    repo.save(wtr);
    	    
    	}

    	WorkTimeDialog.show(owner, m, year, user);
	}
	
	private void doInsertLine(int daysInMonth, Month m, int year) {
		LocalTime lt = LocalTime.of(0, 0);
		LocalDate ld = LocalDate.of(year, m, daysInMonth);
		ZoneId zone = ZoneId.of("Europe/Vienna");
		OffsetDateTime odt = LocalDateTime.of(ld, lt).atZone(zone).toOffsetDateTime();
		
		WorkTime nw = new WorkTime();
		nw.setUserName(user);
		nw.setTsIn(odt);
		nw.setTsOut(odt);
		nw.setBreakTime(BD.ZERO);
		nw.setWorkTime(BD.ZERO);
		nw.setPlusMinus(BD.ZERO);
		nw.setReason("");
		wtRepo.save(nw); // neuen Datensatz in DB schreiben
		isCalc = false;
		MainWindow.actScreen(); // Übersicht aktualisieren
	}
	
	private void doUpdate(Month m, int year) {
		for (int i = 0; i < times.length; i++) {
			WorkTime w = wtRepo.findById(id[i]);
			
			OffsetDateTime IN = LocalDateTime.of(wtp[i].getDatum(), wtp[i].getStart()).atOffset(originalIn[i].getOffset());
			OffsetDateTime OUT = LocalDateTime.of(wtp[i].getDatum(), wtp[i].getEnd()).atOffset(originalOut[i].getOffset());
			
			w.setTsIn(IN);
			w.setTsOut(OUT);
			w.setReason(wtp[i].getProjekt());
			w.setBreakTime(wtp[i].getPause());
			w.setWorkTime(wtp[i].getStunden());
			w.setPlusMinus(wtp[i].getPlusMinus());
			
			wtRepo.update(w);
		}
		BigDecimal[] val = doSummeStunden(times.length);
		stunden.setText(val[0].toString() + " h");
		stundenPM.setText(val[1].toString() + " h");
		TimeAccountPanel.setAktWorktime(val[0]); TimeAccountPanel.setAktOvertime(val[1]);
		worktime = val[0]; overtime = val[1];
		isCalc = true;
		MainWindow.actScreen(); // Übersicht aktualisieren
	}
	
	private void doDeleteLine(long id) {
		if (id == 0) return;
		long data = WorkTimePanel.id[(int) (panelId - 1)];
		wtRepo.delete(data);
		panelId = 0; // unbedingt Panelzeiger zurück setzen
		
		MainWindow.actScreen(); // Übersicht aktualisieren
	}
	
	private void doCloseMonth(ActionEvent e, int daysInMonth, Month m, int year, String user) {
		String html = String.format(msg, this.month);
		Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
		int res = JOptionPane.showOptionDialog(null, html, "Bestätigung", // Sicherheitsabfrage
		        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
		        null, opt, opt[0] // ->Default: "Ja"
		);
		if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) return; // Abbruch oder X geklickt
	    BusyDialog.run(w,
	        "Bitte warten",
	        "Monat wird abgeschlossen …",
	        () -> {
	        	doWorkTime(daysInMonth, m, year, user);
	        	doSaveClosed(monthIndex); // Monat für Änderungen sperren
			},
	        MainWindow::actScreen // Übersicht aktualisieren
	    );
	}
	
	private void doWorkTime(int daysInMonth, Month m, int year, String user) {
		WorkTimeSheetRepository azRepo = new WorkTimeSheetRepository();
		WorkTimeSheet a = new WorkTimeSheet();
		try {
			ExcelWorkTimeSheet.wtExport(daysInMonth, m, year, user); // Excel und pdf erzeugen
		} catch (Exception e1) {
			logger.error("error exporting travel expenses to excel(pdf: ", e1);
			return;
		}
		int monat = m.ordinal() + 1; // 1..12
		String sExcelOut = ExcelWorkTimeSheet.getsExcelOut(); String sPdfOut = ExcelWorkTimeSheet.getsPdfOut();
		String name = Paths.get(sPdfOut).getFileName().toString(); // Dateiname aus Pfad extrahieren

		a.setJahr(year);
		a.setMonat(monat);
		a.setUserName(user);
		a.setDateiname(name);
		a.setOvertime(overtime);
		
		Path path = Paths.get(sPdfOut);
			try {
			a.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
		} catch (IOException e1) {
			logger.error("Fehler laden der Datei " + sPdfOut + ": " + e1.getMessage());
		}
		
		azRepo.save(a); // Ausgaben-Datensatz speichern*/
		
		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = Settings.isLocked(sPdfOut);
		boolean bLockedxlsx = Settings.isLocked(sExcelOut);
		while(bLockedpdf || bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		File pdFile = new File(sPdfOut);
		if(xlFile.delete() && pdFile.delete()) {

		}else {
			logger.error("spExport() - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}
	
	private void doSaveClosed(int index) {
		TimeAccount h = ta;
		int val = h.getTiPrinted() + calcValuePrinted(index);
		h.setTiPrinted(val);
		taRepo.update(h);
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
	
	public static void isButtonEnabled() {
		btn[1].setEnabled(isCalc && isAfter);
		isCalc = false;
	}
	
	private void setIcon(String fileName) {
		try {
			setFileIcon(lblFileTyp, fileName);
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}
	
	private void onChange() {
		if (stunden.getText().isBlank() || stunden.getText().equals("0.00 h")) return;
		stunden.setBackground(new Color(255,250,205)); // light yellow
		stundenPM.setBackground(new Color(255,250,205));
	}
	
	private BigDecimal[] doSummeStunden(int days) {
		BigDecimal sum = BD.ZERO; BigDecimal sumPM = BD.ZERO;
		for (int i = 0; i < days; i++) {
			wtp[i].getBtn().doClick(); // Taste im WorkTimeElement "klicken"
			sum = sum.add(wtp[i].getStunden());
			sumPM = sumPM.add(wtp[i].getPlusMinus());
		}
		BigDecimal val[] = { sum, sumPM };
		if (sumPM.compareTo(BD.ZERO) < 1) stundenPM.setForeground(Color.RED);
		if (sumPM.compareTo(BD.ZERO) >= 1) stundenPM.setForeground(Color.BLACK);
		return val;
	}
	
	private int calcValuePrinted(int ordinal) {
    	int val = 0;
    	for (int x = 0; x < 12; x++) {
    		if (x == ordinal) {
    			val = val + (1 << x);
    		}
    	}
    	return val;
    }
	
	boolean getBit(int x, int i) {
	    return ((x >>> i) & 1) == 1;
	}
	
	private int setFileIcon(JLabel lbl, String fileName) throws IOException {
		if(fileName.equals(FileSelect.getNotSelected()) || fileName == null || fileName.isEmpty()) {
			lbl.setIcon(null);
			return 0;
		}
		String typ = cutFromRight(fileName, '.');
		switch(typ) {
		case "pdf":
			lbl.setIcon(FileIcon.FILE_PDF.icon());
			return 1;
		default:
			lbl.setIcon(null);
			return 0;
		}
	}
	
	private String cutFromRight(String txt, char teil) throws IOException {
		int i = 0;
		int lang = txt.length();
		char[] backward = new char[lang];
		for (i = lang - 1; i > -1; i--) {
			backward[i] = txt.charAt(i);
			if (backward[i] == teil) {
				break;
			}
		}
		txt = txt.substring(i + 1);
		return txt;
	}
	
	//###################################################################################################################################################
	// Fokus-Methoden um das aktive Panel zu loggen
	//###################################################################################################################################################
	
    private void setActivePanel(JPanel panelToSelect) {
        if (panelToSelect == null || panelToSelect == currentlySelectedPanel) { return; }
        currentlySelectedPanel = panelToSelect;
        panelId = (int) panelToSelect.getClientProperty("panelId");
        if (a.DEBUG) System.out.println("Panel " + panelId + " ist ausgewählt. - Datensatz-Id: " + id[(int) (panelId - 1)]);
    }

    private class PanelClickListener extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            Object source = e.getSource();
            if (source instanceof JPanel) { setActivePanel((JPanel) source); }
        }
    }

    private class ChildFocusListener extends FocusAdapter {
        @Override
        public void focusGained(FocusEvent e) {
            Component childComponent = (Component) e.getSource();
            JPanel parentPanel = (JPanel) SwingUtilities.getAncestorOfClass(WorkTimeElement.class, childComponent);
            if (parentPanel != null) { setActivePanel(parentPanel); }
        }
    }
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static BigDecimal getWorktime() {
		return worktime;
	}

	public static BigDecimal getOvertime() {
		return overtime;
	}

	public static boolean isCalc() {
		return isCalc;
	}

	public static boolean isAfter() {
		return isAfter;
	}

}

