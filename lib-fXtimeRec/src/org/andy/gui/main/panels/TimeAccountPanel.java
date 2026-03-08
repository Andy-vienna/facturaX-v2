package org.andy.gui.main.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.andy.code.dataStructure.entity.TimeAccount;
import org.andy.code.dataStructure.entity.WorkTimeSheet;
import org.andy.code.dataStructure.repository.TimeAccountRepository;
import org.andy.code.dataStructure.repository.WorkTimeSheetRepository;
import org.andy.code.main.Settings;
import org.andy.code.misc.BD;
import org.andy.code.misc.BankHoliday;

public class TimeAccountPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    
    private static JTextField[] account = new JTextField[10];
    
    private final DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("[MMMM][MMM]") // voll oder kurz
            .toFormatter(Locale.GERMAN);
    
    private final TimeAccountRepository taRepo = new TimeAccountRepository();
    private final WorkTimeSheetRepository tsRepo = new WorkTimeSheetRepository();
    private TimeAccount ta = null;
    private List<WorkTimeSheet> ts = null;
    
    private BigDecimal kumulOvertime = BD.ZERO;
    
    private static BigDecimal aktWorktime = BD.ZERO;
    private static BigDecimal aktOvertime = BD.ZERO;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public TimeAccountPanel(String month, String user) {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Kontenübersicht"
        );
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);

        int yearInt = Settings.getSettings().year;
        Month m = Month.from(fmt.parse(month)); // z.B. "Februar", "März"
      
        ta = new TimeAccount();
        ta = taRepo.findByUserAndYear(user, yearInt);
        if (ta == null) {
        	TimeAccount h = new TimeAccount();
        	h.setTiPrinted(0);
        	h.setUserName(user);
        	h.setContractHours(BD.ZERO);
        	h.setOverTime(BD.ZERO);
        	h.setYear(yearInt);
        	taRepo.save(h);
        }
        
        buildPanel(m, yearInt, user);
    }
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private void buildPanel(Month m, int year, String user) {
		Dimension size = new Dimension(0,0); int y = 30;
		
		JLabel[] label = new JLabel[10];
		String[] lbl = new String[] { "Stunden pro Woche lt. Vertrag:", "Stunden pro Tag entsprechend:", "", "Arbeitstage im Monat:",
				"Stunden im akt. Monat SOLL:", "", "Stunden im akt. Monat IST:", "Mehrstunden akt. Monat:", "", "Mehrstunden aus abgeschl. Monaten:"  };
		for (int i = 0; i < lbl.length; i++) {
			label[i] = new JLabel(lbl[i]);
			label[i].setHorizontalAlignment(SwingConstants.RIGHT);
			label[i].setBounds(10, y + (i * 25), 200, 25);
			add(label[i]);
			size.height = y + (i * 25);
		}
		size.width = 210;
		
		for (int i = 0; i < account.length; i++) {
			account[i] = new JTextField();
			account[i].setHorizontalAlignment(SwingConstants.RIGHT);
			account[i].setBounds(size.width + 5, y + (i * 25), 150, 25);
			account[i].getDocument().addDocumentListener(new DocumentListener() {
				  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
				  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
				  @Override public void changedUpdate(DocumentEvent e) { }
			});
			account[i].setFocusable(false);
			add(account[i]);
		}
		account[2].setVisible(false);
		account[4].setBackground(new Color(255,250,205)); account[4].setFont(new Font("Tahoma", Font.BOLD, 11));
		account[5].setVisible(false);
		account[6].setBackground(new Color(255,250,205)); account[6].setFont(new Font("Tahoma", Font.BOLD, 11)); account[6].setForeground(Color.BLUE);
		account[7].setFont(new Font("Tahoma", Font.BOLD, 11));
		account[8].setVisible(false);
		account[9].setFont(new Font("Tahoma", Font.BOLD, 11));
		size.width = 365;
		
		JSeparator sep1 = new JSeparator();
		sep1.setForeground(Color.DARK_GRAY);
        sep1.setBounds(10, 40 + (2 * 25), size.width - 10, 8);
        sep1.setOrientation(SwingConstants.HORIZONTAL);
        add(sep1);
		
		JSeparator sep2 = new JSeparator();
		sep2.setForeground(Color.DARK_GRAY);
        sep2.setBounds(10, 40 + (5 * 25), size.width - 10, 8);
        sep2.setOrientation(SwingConstants.HORIZONTAL);
        add(sep2);
        
        JSeparator sep3 = new JSeparator();
		sep3.setForeground(Color.DARK_GRAY);
        sep3.setBounds(10, 40 + (8 * 25), size.width - 10, 8);
        sep3.setOrientation(SwingConstants.HORIZONTAL);
        add(sep3);
        
        size.width = 375;
        size.height = size.height + 45;
		
		loadData(year, m, user);
		
		setPreferredSize(size);
    }
    
    private void loadData(int year, Month m, String user) {
    	ta = taRepo.findByUserAndYear(user, year); ts = tsRepo.findByUserYear(user, year);
    	kumulOvertime = BD.ZERO;
    	for (int n = 0; n < ts.size(); n++) {
    		WorkTimeSheet wts = ts.get(n);
    		kumulOvertime = kumulOvertime.add(wts.getOvertime());
    	}
    	aktWorktime = WorkTimePanel.getWorktime();
    	aktOvertime = WorkTimePanel.getOvertime();
    	
    	YearMonth ym = YearMonth.of(year, m);
		int workDays = BankHoliday.workdaysInMonth(ym, "at", "w"); // Anzahl Arbeitstage im Monat
		
		BigDecimal hoursDay = ta.getContractHours().divide(BD.FIVE).setScale(2, RoundingMode.HALF_UP);
		BigDecimal hoursMonth = hoursDay.multiply(new BigDecimal(workDays)).setScale(2, RoundingMode.HALF_UP);
		
		account[0].setText(ta.getContractHours().toString());
		account[1].setText(hoursDay.toString());
		account[3].setText(String.valueOf(workDays));
		account[4].setText(hoursMonth.toString());
		
		account[6].setText(aktWorktime.toString());
		account[7].setText(aktOvertime.toString());
		
		account[9].setText(kumulOvertime.toString());
		
		if (aktOvertime.compareTo(BD.ZERO) < 0) {
			account[7].setBackground(Color.PINK);
		} else {
			account[7].setBackground(new Color(144,238,144));
		}
		
		if (kumulOvertime.compareTo(BD.ZERO) < 0) {
			account[9].setBackground(Color.PINK);
		} else {
			account[9].setBackground(new Color(144,238,144));
		}
		
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
    
    private void onChange() {
    	
    }
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public BigDecimal getAktWorktime() {
		return aktWorktime;
	}

	public static void setAktWorktime(BigDecimal aktWorktime) {
		TimeAccountPanel.aktWorktime = aktWorktime;
		account[6].setText(aktWorktime.toString());
	}

	public BigDecimal getAktOvertime() {
		return aktOvertime;
	}

	public static void setAktOvertime(BigDecimal aktOvertime) {
		TimeAccountPanel.aktOvertime = aktOvertime;
		account[7].setText(aktOvertime.toString());
	}

}
