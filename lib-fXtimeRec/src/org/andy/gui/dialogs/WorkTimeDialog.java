package org.andy.gui.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import org.andy.code.dataStructure.entity.WorkTime;
import org.andy.code.dataStructure.entity.WorkTimeRaw;
import org.andy.code.dataStructure.repository.WorkTimeRawRepository;
import org.andy.code.dataStructure.repository.WorkTimeRepository;
import org.andy.code.misc.BD;
import org.andy.code.workTime.WorkTimeValidator;
import org.andy.gui.main.MainWindow;

public final class WorkTimeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private static String monat = null;
	private final JButton closeButton = new JButton("Schließen");
	
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

    public WorkTimeDialog(Window owner, Month m, int year, String user) {    	
        super(owner, "Arbeitstzeit-Rohdaten importieren für " + monat + "/" + year, ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent(m, year, user));
        pack();
        setMinimumSize(new Dimension(400, 100));
        setLocationRelativeTo(owner);
        getRootPane().setDefaultButton(closeButton);
        setIconImage(loadImage("/org/resources/icons/icon.png", 32, 32));
    }
    
    // Convenience
    public static void show(Window owner, Month m, int year, String user) {
    	monat = m.getDisplayName(TextStyle.FULL, Locale.GERMAN);
        new WorkTimeDialog(owner, m, year, user).setVisible(true);
    }

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private JPanel buildContent(Month m, int year, String user) {
        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBorder(new EmptyBorder(8, 5, 8, 5));
        
        ZoneId zone = ZoneId.systemDefault();
        int days = YearMonth.of(year, m).lengthOfMonth();
        LocalDate from = LocalDate.of(year, m, 1);
        LocalDate to   = LocalDate.of(year, m, days);
        
        OffsetDateTime start = from.atStartOfDay(zone).toOffsetDateTime(); // erster Tag des Zeitraums (Monat)
		OffsetDateTime end   = to.plusDays(1).atStartOfDay(zone).toOffsetDateTime(); //letzter tag des Zeitraums (Monat)

		WorkTimeRawRepository repo = new WorkTimeRawRepository();
		List<WorkTimeRaw> rows = repo.findDaysForUser(start, end, user);
		
		List<Long> listId = new ArrayList<>(); // ID's der geparsten Einträge merken
		for (int i = 0; i < rows.size(); i++) {
			listId.add(rows.get(i).getId());
		}
		
		List<WorkTimeValidator.Punch> dayPunches = WorkTimeValidator.toDayForUser(rows, user, from, zone); // erster Tag des Monats
		for (int i = 2; i < (days + 1); i++) {
			LocalDate day = LocalDate.of(year, m, i);
			List<WorkTimeValidator.Punch> dayPunch = WorkTimeValidator.toDayForUser(rows, user, day, zone); // 2. bis n-ter Tag des Monats
			dayPunches.addAll(dayPunch);
		}
		
		WorkTimeValidator.ValidationResult res = WorkTimeValidator.validateDay(dayPunches, zone);

		// Ergebnisse
		List<String> errors = res.errors();
		List<WorkTimeValidator.WorkSession> sessions = res.sessions();
		
		String meldung = null;
		if (sessions.size() == 0) {
			meldung = "<html><div style='font-size:10px;font-weight:bold;'>keine Rohdaten (Stempelungen) gefunden</div><br>";
		} else {
			meldung = "<html><div style='font-size:10px;font-weight:bold;'>keine Fehler beim parsen</div><br>";
			if (errors.size() > 0) {
				meldung = "<html><div style='font-size:10px;font-weight:bold;'>Fehler beim parsen:</div><br>";
				for (int n = 0; n < errors.size(); n++) {
					String zeile = errors.get(n).toString();
					meldung = meldung + "<div style='font-size:10px;font-weight:plain;color:red;'>" + zeile + "</div><br>";
				}
			}
		}
		meldung = meldung + "</html>";
		
        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 6, 10, 6);
        gc.gridx = 0; gc.weightx = 1; gc.fill = GridBagConstraints.HORIZONTAL;

		JLabel title = new JLabel(meldung);
        title.setForeground(new Color(20, 20, 20));
        gc.gridy = 0; right.add(title, gc);
        
        JSeparator sep2 = new JSeparator();
        gc.insets = new Insets(0, 6, 0, 6);
        gc.gridy = 1; right.add(sep2, gc);

        root.add(right, BorderLayout.CENTER);

        // Bottom: Buttonzeile
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        closeButton.addActionListener(_ -> { doWriteData(sessions, listId, user); dispose(); });
        buttons.add(closeButton);
        root.add(buttons, BorderLayout.SOUTH);

        return root;
    }
    
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

    private static Image loadImage(String path, int w, int h) {
        try (InputStream is = WorkTimeDialog.class.getResourceAsStream(path)) {
            if (is == null) return null;
            Image src = ImageIO.read(is);
            return src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }
    
    private void doWriteData(List<WorkTimeValidator.WorkSession> ses, List<Long> listId, String user) {
    	
    	if (ses.size() < 1) { dispose(); return; }
    	
    	WorkTimeRepository repoWT = new WorkTimeRepository();
    	WorkTimeRawRepository repoWTR = new WorkTimeRawRepository();
    	
    	for (int x = 0; x < ses.size(); x++) { // geparste Daten in die WorkTime Tabelle schreiben
    		WorkTime wt = new WorkTime();
    		WorkTimeValidator.WorkSession ws = ses.get(x);
    		
    		Duration pause = ws.breakTotal();
    		Duration work = ws.net();
    		
    		BigDecimal bdPause = BigDecimal.valueOf(pause.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    		BigDecimal bdWork = BigDecimal.valueOf(work.toMinutes()).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    		
    		wt.setUserName(user);
    		wt.setTsIn(ws.in());
    		wt.setTsOut(ws.out());
    		wt.setBreakTime(bdPause);
    		wt.setWorkTime(bdWork);
    		wt.setPlusMinus(BD.ZERO);
    		wt.setReason("");
    		
    		repoWT.save(wt);
    	}
    	
    	for (int n = 0; n < listId.size(); n++) { // Rohdaten aus WorkTimeRaw Tabelle enfernen
    		repoWTR.delete(listId.get(n));
    	}
    	
    	MainWindow.actScreen();
	}

}
