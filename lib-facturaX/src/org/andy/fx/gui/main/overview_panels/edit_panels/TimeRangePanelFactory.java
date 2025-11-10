package org.andy.fx.gui.main.overview_panels.edit_panels;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
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
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.TextStyle;
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

import org.andy.fx.code.dataExport.ExcelSpesen;
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.entityProductive.Helper;
import org.andy.fx.code.dataStructure.entityProductive.Spesen;
import org.andy.fx.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.HelperRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.SpesenRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.misc.BusyDialog;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimeRangePanelFactory extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TimeRangePanelFactory.class);
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    private TimeRangePanel[] trp = new TimeRangePanel[31];
    private JTextField stunden = new JTextField();
    private JTextField summe = new JTextField();
    private JButton[] btn = new JButton[2];
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
    
    private final SpesenRepository repo = new SpesenRepository();
    private final HelperRepository hlpRepo = new HelperRepository();
    private List<Spesen> ls = null; private Helper hlp = null;
    private int monthIndex = 0; private String month = null;
    private String user;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public TimeRangePanelFactory(String month, String user) {
        setLayout(null);
        this.month = month; this.user = user;
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Spesenabrechnung " + month + " " + Einstellungen.getAppSettings().year
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

        ls = new ArrayList<>(); hlp = new Helper();
        ls = repo.findByDateBetween(from, to); hlp = hlpRepo.findByUser(user);
        if (hlp == null) {
        	Helper h = new Helper();
        	h.setSpPrinted(0);
        	h.setTiPrinted(0);
        	h.setUserName(user);
        	hlpRepo.save(h);
        }
        buildPanel(m, days, yearInt);
    }
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
	private void buildPanel(Month m, int daysInMonth, int jahr) {
		Dimension size = new Dimension(0,0); int x = 0;
		
		for (int i = 0; i < trp.length; i++) trp[i] = null;
		for (int i = 0; i < daysInMonth; i++) {
			LocalDate d = LocalDate.of(Einstellungen.getAppSettings().year, m, i + 1);
			try { trp[i] = new TimeRangePanel(); } catch (IOException e) { logger.error("error creating TimeRangePanel: ", e); }
			size = trp[0].getPreferredSize();
			trp[i].setDatum(d);
			trp[i].setBounds(10, 30 + (i * 25), size.width, size.height);
			add(trp[i]);
			x = i;
		}
		
		JSeparator sep = new JSeparator();
		sep.setForeground(Color.DARK_GRAY);
        sep.setBounds(610, 60 + (x * 25), 300, 5);
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
		
		summe.setFont(new Font("Tahoma", Font.BOLD, 12));
		summe.setBounds(760, 65 + (x * 25), 150, 25);
		summe.setHorizontalAlignment(SwingConstants.RIGHT);
		summe.setFocusable(false);
		summe.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(summe);
		
		btn[0] = createButton("<html>Spesen<br>speichern</html>", ButtonIcon.SAVE.icon(), null);
		btn[0].setBounds(1150, (daysInMonth * 25) + 60, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btn[0].setEnabled(true);
		add(btn[0]);
		
		btn[1] = createButton("<html>Spesen<br>export.</html>", ButtonIcon.EXPORT.icon(), null);
		btn[1].setBounds(1280, (daysInMonth * 25) + 60, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btn[1].addActionListener(e -> doWriteExpenses(e, daysInMonth, m, jahr, stunden.getText(), summe.getText()));
		add(btn[1]);
		
		JLabel hinweis = new JLabel("Spesenabrechnung für " + month + " bereits exportiert, keine Änderungen mehr möglich ...");
		hinweis.setFont(font); hinweis.setForeground(titleColor);
		hinweis.setBounds(10 + size.width - 655, 95 + (x * 25), 650, 40);
		hinweis.setHorizontalAlignment(SwingConstants.RIGHT); hinweis.setVisible(false);
		add(hinweis);
		
		btn[0].setVisible(true); btn[1].setVisible(true);
		hlp = hlpRepo.findByUser(user);
		if (hlp.getSpPrinted() > 0 && getBit(hlp.getSpPrinted(), monthIndex)) {
			btn[0].setVisible(false);
			btn[1].setVisible(false);
			hinweis.setVisible(true);
		}
		
		doLoadData(daysInMonth);
		stunden.setText(doSummeStunden(daysInMonth) + " h");
		summe.setText(doSummeBetrag(daysInMonth) + " EUR");
		
		setPreferredSize(new Dimension(size.width + 20, (daysInMonth * size.height) + 130));
	}
	
	//###################################################################################################################################################
	// Datenbank Operationen
	//###################################################################################################################################################
	
	private void doLoadData(int days){
		btn[0].removeActionListener(_ -> doSave(0, days));
		if (ls.size() == 0 || ls.size() != days) {
			btn[0].addActionListener(_ -> doSave(1, days));
			return;
		}
		for (int i = 0; i < ls.size(); i++) {
			trp[i].setStart(ls.get(i).getTimeStart());
			trp[i].setEnd(ls.get(i).getTimeEnd());
			trp[i].setLand(ls.get(i).getCountry());
			trp[i].setStunden(ls.get(i).getSumHours());
			trp[i].setBetrag(ls.get(i).getAmount());
			trp[i].setGrund(ls.get(i).getComment());
		}
		btn[0].addActionListener(_ -> doSave(2, days));
	}
	
	private void doSave(int reason, int days) {
		if (reason == 0) return;
		if (reason == 1) { // Neuanlage Datensatz
			for (int i = 0; i < days; i++) {
				Spesen s = new Spesen();
				s.setDate(trp[i].getDatum());
				s.setTimeStart(trp[i].getStart());
				s.setTimeEnd(trp[i].getEnd());
				s.setCountry(trp[i].getLand());
				s.setSumHours(trp[i].getStunden());
				s.setAmount(trp[i].getBetrag());
				s.setComment(trp[i].getGrund());
				repo.save(s);
			}
		}
		if (reason == 2) { // Update Datensatz
			for (int i = 0; i < days; i++) {
				Spesen s = ls.get(i);
				s.setDate(trp[i].getDatum());
				s.setTimeStart(trp[i].getStart());
				s.setTimeEnd(trp[i].getEnd());
				s.setCountry(trp[i].getLand());
				s.setSumHours(trp[i].getStunden());
				s.setAmount(trp[i].getBetrag());
				s.setComment(trp[i].getGrund());
				repo.update(s);
			}
		}
		stunden.setText(doSummeStunden(days) + " h");
		summe.setText(doSummeBetrag(days) + " EUR");
		btn[1].setEnabled(true);
	}
	
	private void doWriteExpenses(ActionEvent e, int daysInMonth, Month m, int jahr, String stunden, String summe) {
		String html = String.format(msg, this.month);
		Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
		int res = JOptionPane.showOptionDialog(null, html, "Bestätigung", // Sicherheitsabfrage
		        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
		        null, opt, opt[0] // ->Default: "Ja"
		);
		if (res == JOptionPane.NO_OPTION || res == JOptionPane.CLOSED_OPTION) return; // Abbruch oder X geklickt
	    BusyDialog.run(w,
	        "Bitte warten",
	        "Spesenliste wird geschrieben …",
	        () -> {
	        	doExpenses(daysInMonth, m, jahr, stunden, summe);
	        	doSavePrinted(monthIndex); // Monat für Änderungen sperren
			},
	        HauptFenster::actScreen // Übersicht aktualisieren
	    );
	}
	
	private void doExpenses(int daysInMonth, Month m, int jahr, String stunden, String summe) {
		AusgabenRepository ausgabenRepo = new AusgabenRepository();
		Ausgaben a = new Ausgaben();
		try {
			ExcelSpesen.spExport(daysInMonth, m, jahr, stunden, summe); // Excel und pdf erzeugen
		} catch (Exception e1) {
			logger.error("error exporting travel expenses to excel(pdf: ", e1);
			return;
		}
		String sExcelOut = ExcelSpesen.getsExcelOut(); String sPdfOut = ExcelSpesen.getsPdfOut();
		String name = Paths.get(sPdfOut).getFileName().toString(); // Dateiname aus Pfad extrahieren
		String sumClean = summe.replace(" EUR", "").trim(); // "1.234,56 EUR" -> "1.234,56"
		
		a.setJahr(Einstellungen.getAppSettings().year);
		a.setDatum(LocalDate.of(Einstellungen.getAppSettings().year, m, daysInMonth));
		a.setArt("Diäten für Dienstreisen " + m.getDisplayName(TextStyle.FULL, Locale.GERMAN) + " " + jahr);
		a.setLand(ExportHelper.getOwner().getLand());
		a.setWaehrung(ExportHelper.getOwner().getCurrency());
		a.setSteuersatz("0");
		a.setNetto(parseStringToBigDecimalSafe(sumClean, LocaleFormat.AUTO));
		a.setSteuer(BD.ZERO);
		a.setBrutto(parseStringToBigDecimalSafe(sumClean, LocaleFormat.AUTO));
		a.setDateiname(name);
		Path path = Paths.get(sPdfOut);
			try {
			a.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
		} catch (IOException e1) {
			logger.error("Fehler laden der Datei " + sPdfOut + ": " + e1.getMessage());
		}
		
		ausgabenRepo.save(a); // Ausgaben-Datensatz speichern
		
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
	
	private void doSavePrinted(int index) {
		Helper h = hlp;
		int val = h.getSpPrinted() + calcValuePrinted(index);
		h.setSpPrinted(val);
		hlpRepo.update(h);
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
	
	private void onChange() {
		if (stunden.getText().isBlank() || stunden.getText().equals("0.00 h")) return;
		stunden.setBackground(new Color(255,250,205)); // light green
		if (summe.getText().isBlank() || summe.getText().equals("0.00 EUR")) return;
		summe.setBackground(new Color(255,250,205)); // light green
	}
	
	private BigDecimal doSummeStunden(int days) {
		BigDecimal sum = BD.ZERO;
		for (int i = 0; i < days; i++) {
			sum = sum.add(trp[i].getStunden());
		}
		return sum;
	}
	
	private BigDecimal doSummeBetrag(int days) {
		BigDecimal sum = BD.ZERO;
		for (int i = 0; i < days; i++) {
			sum = sum.add(trp[i].getBetrag());
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
