package org.andy.fx.gui.main.overview_panels.edit_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
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

import org.andy.fx.code.dataExport.ExcelArbeitszeit;
import org.andy.fx.code.dataStructure.entityProductive.Arbeitszeit;
import org.andy.fx.code.dataStructure.entityProductive.Helper;
import org.andy.fx.code.dataStructure.entityProductive.WorkTime;
import org.andy.fx.code.dataStructure.repositoryProductive.ArbeitszeitRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.HelperRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.WorkTimeRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.dialogs.WorkTimeDialog;
import org.andy.fx.gui.misc.BusyDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkTimePanelFactory extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(WorkTimePanelFactory.class);
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    
    private DayTimes times[] = null;
    private WorkTimePanel[] wtp = new WorkTimePanel[50];
    private JTextField stunden = new JTextField();
    private JButton[] btn = new JButton[4];
    
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
    
    private final WorkTimeRepository repo = new WorkTimeRepository();
    private final HelperRepository hlpRepo = new HelperRepository();
    private List<WorkTime> wt = null; private List<Helper> hlp = null;
    private long[] id; private String user;
    private int monthIndex = 0; private String month = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public WorkTimePanelFactory(String month, String user) {
        setLayout(null);
        this.month = month; this.user = user;
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Arbeitszeit für " + month + " " + Einstellungen.getAppSettings().year
        );
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);

        int yearInt = Einstellungen.getAppSettings().year;
        Month m = Month.from(fmt.parse(month)); // z.B. "Februar", "März"
        int days = YearMonth.of(yearInt, m).lengthOfMonth();

        monthIndex = m.ordinal(); // 0..11
        
        LocalDate from = LocalDate.of(yearInt, m, 1);
        LocalDate to   = LocalDate.of(yearInt, m, days);

        wt = new ArrayList<>(); hlp = new ArrayList<>();
        wt = repo.findDaysForUser(from, to, user); hlp = hlpRepo.findAll();
        if (hlp.size() < 1) {
        	Helper h = new Helper();
        	h.setTiPrinted(0);
        	hlpRepo.save(h);
        }
        
        times = findRange(wt);
        
        buildPanel(m, days, yearInt);
    }
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
	private void buildPanel(Month m, int daysInMonth, int jahr) {
		Dimension size = new Dimension(0,0); int x = 0;
		
		JLabel[] header = new JLabel[6];
		String[] lbl = new String[] { "Datum", "Anfang", "Ende", "Pause (h)", "Stunden", "Bemerkung" };
		for (int i = 0; i < lbl.length; i++) {
			header[i] = new JLabel(lbl[i]);
			header[i].setHorizontalAlignment(SwingConstants.CENTER);
			header[i].setBounds(10 + (i * 150), 30, 150, 25);
			add(header[i]);
		}
		
		for (int i = 0; i < times.length; i++) wtp[i] = null;
		for (int i = 0; i < times.length; i++) {
			LocalDate d = LocalDate.of(jahr, m, i + 1);
			try { wtp[i] = new WorkTimePanel(); } catch (IOException e) { logger.error("error creating WorkTimePanel: ", e); }
			size = wtp[0].getPreferredSize();
			wtp[i].setDatum(d);
			wtp[i].setBounds(10, 60 + (i * 25), size.width, size.height);
			add(wtp[i]);
			x = times.length;
		}
		
		JSeparator sep = new JSeparator();
		sep.setForeground(Color.DARK_GRAY);
        sep.setBounds(610, 60 + (x * 25), 150, 5);
        sep.setOrientation(SwingConstants.HORIZONTAL);
        add(sep);
        
        JLabel lblSum = new JLabel("Summen:");
		lblSum.setFont(new Font("Tahoma", Font.BOLD, 12));
		lblSum.setBounds(460, 65 + (x * 25), 150, 25);
		lblSum.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lblSum);
		
		stunden.setFont(new Font("Tahoma", Font.BOLD, 12));
		stunden.setBounds(610, 65 + (x * 25), 150, 25);
		stunden.setHorizontalAlignment(SwingConstants.RIGHT);
		stunden.setFocusable(false);
		stunden.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(stunden);
		
		btn[0] = createButton("<html>Zeiten<br>speichern</html>", ButtonIcon.SAVE.icon(), null);
		btn[0].setBounds(1050, (times.length * 25) + 85, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btn[0].setEnabled(true);
		btn[0].addActionListener(_ -> doUpdate());
		add(btn[0]);
		
		btn[1] = createButton("<html>Monats-<br>abschluss</html>", ButtonIcon.EXPORT.icon(), null);
		btn[1].setBounds(1180, (times.length * 25) + 85, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btn[1].addActionListener(e -> doCloseMonth(e, daysInMonth, m, jahr, user));
		add(btn[1]);
		
		btn[2] = createButton("<html>Tag<br>einfügen</html>", ButtonIcon.INSERT.icon(), null);
		btn[2].setBounds(190, (times.length * 25) + 85, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btn[2].setEnabled(true);
		btn[2].addActionListener(_ -> doInsertDay(daysInMonth, m, jahr));
		add(btn[2]);
		
		btn[3] = createButton("<html>Stepelungen<br>laden</html>", ButtonIcon.IMPORT.icon(), null);
		btn[3].setBounds(10, (times.length * 25) + 85, HauptFenster.getButtonx() + 50, HauptFenster.getButtony());
		btn[3].setEnabled(true);
		btn[3].addActionListener(e -> doImportRawData(e, m, jahr, user));
		add(btn[3]);
		
		JLabel hinweis = new JLabel("Arbeitszeit für " + month + " bereits abgeschlossen, keine Änderungen mehr möglich ...");
		hinweis.setFont(font); hinweis.setForeground(titleColor);
		hinweis.setBounds(10 + size.width - 655, (times.length * 25) + 100, 650, 40);
		hinweis.setHorizontalAlignment(SwingConstants.RIGHT); hinweis.setVisible(false);
		add(hinweis);
		
		btn[0].setVisible(true); btn[1].setVisible(true);
		if (hlp.get(0).getTiPrinted() > 0 && getBit(hlp.get(0).getTiPrinted(), monthIndex)) {
			btn[0].setVisible(false); btn[1].setVisible(false); btn[2].setVisible(false);
			hinweis.setVisible(true);
			
			//--> hier Dateianzeige mit Downloadmöglichkeit einbauen (analog Betriebsausgaben)
		}
        
		doLoadData();
		stunden.setText(doSummeStunden(times.length) + " h");
		
		if (times.length > 0) {
			setPreferredSize(new Dimension(size.width + 20, (times.length * size.height) + 155));
		} else {
			setPreferredSize(new Dimension(1320, 155));
		}
		
	}
	
	//###################################################################################################################################################
	// Datenbank Operationen
	//###################################################################################################################################################
	
	private record DayTimes(long id, LocalDate date, LocalTime wStart, LocalTime wEnd, BigDecimal breakTime, BigDecimal workTime, String reason) {}
	private DayTimes[] findRange(List<WorkTime> listWt) {
		WorkTime w = null;
		LocalDate wDay = null; LocalTime wStart = null; LocalTime wEnd = null;
		BigDecimal breakTime = BD.ZERO; BigDecimal workTime = BD.ZERO; String reason = ""; int num = 0;
		
		DayTimes[] times = new DayTimes[listWt.size()]; // Anzahl der vollständigen Tage mit IN + OUT
		try {
			for (num = 0; num < listWt.size(); num++) {
				w = listWt.get(num);
				if (w.getTsIn() != null) wDay = w.getTsIn().toLocalDate();
				if (w.getTsIn() != null) { wStart = w.getTsIn().toLocalTime(); } else { wStart = LocalTime.of(0,  0); }
				if (w.getTsOut() != null) { wEnd = w.getTsOut().toLocalTime(); } else { wEnd = LocalTime.of(0,  0); }
				if (w.getReason() != null || !w.getReason().isBlank()) reason = w.getReason().trim();
				if (w.getBreakTime() != null) breakTime = w.getBreakTime();
				if (w.getWorkTime() != null) workTime = w.getWorkTime();
				times[num] = new DayTimes(w.getId(), wDay, wStart, wEnd, breakTime, workTime, reason);
			}
		} catch (IndexOutOfBoundsException ex) {
			System.out.println("da war nix mehr ...");
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
			wtp[i].setProjekt(times[i].reason());	
		}
	}
	
	private void doImportRawData(ActionEvent e, Month m, int year, String user) {
		
		Component c = (Component) e.getSource();
    	Window owner = SwingUtilities.getWindowAncestor(c);
    	WorkTimeDialog.show(owner, m, year, user);

	}
	
	private void doInsertDay(int daysInMonth, Month m, int year) {
		LocalDate ld = LocalDate.of(year, m, daysInMonth); LocalTime lt = LocalTime.of(0, 0);
		ZoneId zone = ZoneId.systemDefault();
		OffsetDateTime odt = LocalDateTime.of(ld, lt).atZone(zone).toOffsetDateTime();
		
		WorkTime nw = new WorkTime();
		nw.setUserName(user);
		nw.setTsIn(odt);
		nw.setTsOut(odt);
		nw.setBreakTime(BD.ZERO);
		nw.setWorkTime(BD.ZERO);
		nw.setReason("");
		repo.save(nw); // neuen Datensatz in DB schreiben
		
		HauptFenster.actScreen(); // Übersicht aktualisieren
	}
	
	private void doUpdate() {
		ZoneId zone = ZoneId.systemDefault();
		for (int i = 0; i < times.length; i++) {
			WorkTime w = repo.findById(id[i]);
			
			OffsetDateTime IN = LocalDateTime.of(wtp[i].getDatum(), wtp[i].getStart()).atZone(zone).toOffsetDateTime();
			OffsetDateTime OUT = LocalDateTime.of(wtp[i].getDatum(), wtp[i].getEnd()).atZone(zone).toOffsetDateTime();
			
			w.setTsIn(IN);
			w.setTsOut(OUT);
			w.setReason(wtp[i].getProjekt());
			w.setBreakTime(wtp[i].getPause());
			w.setWorkTime(wtp[i].getStunden());
			
			repo.update(w);
		}
		stunden.setText(doSummeStunden(times.length) + " h");
		btn[1].setEnabled(true);
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
	        HauptFenster::actScreen // Übersicht aktualisieren
	    );
	}
	
	private void doWorkTime(int daysInMonth, Month m, int year, String user) {
		ArbeitszeitRepository azRepo = new ArbeitszeitRepository();
		Arbeitszeit a = new Arbeitszeit();
		try {
			ExcelArbeitszeit.wtExport(daysInMonth, m, year, user); // Excel und pdf erzeugen
		} catch (Exception e1) {
			logger.error("error exporting travel expenses to excel(pdf: ", e1);
			return;
		}
		int monat = m.ordinal() + 1; // 1..12
		String sExcelOut = ExcelArbeitszeit.getsExcelOut(); String sPdfOut = ExcelArbeitszeit.getsPdfOut();
		String name = Paths.get(sPdfOut).getFileName().toString(); // Dateiname aus Pfad extrahieren

		a.setJahr(year);
		a.setMonat(monat);
		a.setUserName(user);
		a.setDateiname(name);
		
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
		boolean bLockedpdf = Einstellungen.isLocked(sPdfOut);
		boolean bLockedxlsx = Einstellungen.isLocked(sExcelOut);
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
		Helper h = hlp.get(0);
		int val = h.getTiPrinted() + calcValuePrinted(index);
		h.setTiPrinted(val);
		hlpRepo.update(h);
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
	
	private void onChange() {
		if (stunden.getText().isBlank() || stunden.getText().equals("0.00 h")) return;
		stunden.setBackground(new Color(255,250,205)); // light green
	}
	
	private BigDecimal doSummeStunden(int days) {
		BigDecimal sum = BD.ZERO;
		for (int i = 0; i < days; i++) {
			sum = sum.add(wtp[i].getStunden());
		}
		return sum;
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
	
}
