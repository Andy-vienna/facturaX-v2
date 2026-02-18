package org.andy.gui.main.panels.elements;

import static org.andy.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.andy.code.misc.BD;
import org.andy.code.misc.CommaHelper;
import org.andy.gui.iconHandler.ButtonIcon;
import org.andy.gui.misc.DateTimePickerSettings;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.optionalusertools.TimeChangeListener;

public final class WorkTimeElement extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private DateTimePickerSettings dtp = new DateTimePickerSettings();
	
	private int posx = 0; private int posy = 0;	private final int w = 150; private final int h = 25; private final int w2 = 100;
	private BigDecimal hours = BD.ZERO;
	private DatePicker datum = new DatePicker();
	private TimePicker[] zeit = new TimePicker[2];
	private JTextField[] ztf = new JTextField[2];
	private JTextField[] txtField = new JTextField[3];
	private JTextField projekt;
	private JButton btn = new JButton();
	private OffsetDateTime originalIn = null; private OffsetDateTime originalOut = null;
	private BigDecimal hoursDay = null;
	
	private ActionListener bal = _ -> doBtnAction();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public WorkTimeElement() throws IOException {
		
		setLayout(null);
		
		datum.setSettings(dtp.dpSettings()); datum.setBounds(posx, posy, w, h);
		JTextField dt = datum.getComponentDateTextField(); dt.setHorizontalAlignment(SwingConstants.CENTER);
		add(datum);
		posx = posx + w;
		
		for (int i = 0; i < zeit.length; i++) {
			zeit[i] = new TimePicker(dtp.tpSettings()); zeit[i].setBounds(posx + (i * w), posy, w, h);
			ztf[i] = zeit[i].getComponentTimeTextField(); ztf[i].setHorizontalAlignment(SwingConstants.CENTER);
			ztf[i].setOpaque(true);
			add(zeit[i]);
		}
		posx = posx + (zeit.length * w);
		
		for (int i = 0; i < txtField.length; i++) {
			final int x = i;
			txtField[i] = new JTextField(); txtField[i].setFocusable(false);
			txtField[i].setFont(new Font("Tahoma", Font.BOLD, 12)); txtField[i].setBounds(posx + (i * w2), posy, w2, h);
			txtField[i].setHorizontalAlignment(SwingConstants.RIGHT);
			txtField[i].getDocument().addDocumentListener(new DocumentListener() {
				  @Override public void insertUpdate(DocumentEvent e) { diffOnChange(txtField[x]); }
				  @Override public void removeUpdate(DocumentEvent e) { diffOnChange(txtField[x]); }
				  @Override public void changedUpdate(DocumentEvent e) { }
			});
			attachCommaToDot(txtField[i]);
			add(txtField[i]);
		}
		txtField[0].setFocusable(true); // Pausenzeit soll eingebbar sein
		posx = posx + (txtField.length * w2);
		
		projekt = new JTextField();
		projekt.setFont(new Font("Tahoma", Font.PLAIN, 12)); projekt.setBounds(posx, posy, 500, h);
		projekt.setHorizontalAlignment(SwingConstants.LEFT); projekt.setBackground(Color.PINK);
		projekt.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { projektOnChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { projektOnChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(projekt);
		posx = posx + 500;
		
		btn = createButton("", ButtonIcon.CALC16.icon(), null);
		btn.setBounds(posx, posy, 50, h); btn.setEnabled(true); btn.setVisible(false);
		btn.addActionListener(bal);
		add(btn);
		//posx = posx + 50;
		
		wireTimeListeners();

		setPreferredSize(new Dimension(posx, h));
	}

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void wireTimeListeners() {
	    TimeChangeListener l = _ -> SwingUtilities.invokeLater(this::timeOnChange);
	    for (int i = 0; i < zeit.length; i++) {
	    	zeit[i].addTimeChangeListener(l);
	    }
	    timeOnChange(); // initial
	}
	private void timeOnChange() {
		final Color GREEN   = new Color(144,238,144); final Color YELLOW = new Color(255,250,205);
		LocalTime t0 = zeit[0].getTime(); LocalTime t1 = zeit[1].getTime();
	    boolean gt0_0 = t0 != null && t0.toSecondOfDay() > 0; boolean gt0_1 = t1 != null && t1.toSecondOfDay() > 0;
	    ztf[0].setBackground(gt0_0 ? GREEN : YELLOW); ztf[1].setBackground(gt0_1 ? GREEN : YELLOW);
	}
	private void diffOnChange(JTextField txt) {
		if (txt.getText().isBlank()) return;
		BigDecimal val = new BigDecimal(txt.getText().replace(" h", ""));
		if (val.compareTo(BD.ZERO) == 0) { txt.setBackground(Color.WHITE); return; } // white
		if (val.compareTo(BD.ZERO) == -1) { txt.setBackground(Color.PINK); return; } // pink
		if (val.compareTo(BD.TWELVE) == 1) { txt.setBackground(Color.RED); return; } // red
		if (val.compareTo(BD.TEN) == 1) { txt.setBackground(new Color(255,250,205)); return; } // light yellow
		txt.setBackground(new Color(144,238,144)); // light green
	}
	private void projektOnChange() {
		if (projekt.getText().isBlank() || projekt.getText().equals("")) {
			projekt.setBackground(Color.PINK);
			return;
		}
		projekt.setBackground(new Color(144,238,144)); // light green
	}
	
	private void doBtnAction() {

		String tmp = "0.00";
		LocalTime wts = zeit[0].getTime(); LocalTime wte = zeit[1].getTime();
		BigDecimal hour = BigDecimal.valueOf(60);
		
		BigDecimal workMins = calcMinutes(wts, wte);
		BigDecimal breakMins = BD.ZERO;
		
		if (!txtField[0].getText().isEmpty() || !txtField[0].getText().isBlank()) {
			tmp = txtField[0].getText().replace(" h", "");
			breakMins = new BigDecimal(tmp).multiply(hour);
		}
		
		BigDecimal mins = workMins.subtract(breakMins);
		hours = mins.divide(hour, 2, RoundingMode.HALF_UP);
		BigDecimal hoursPM = BD.ZERO;
		hoursPM = hours.subtract(hoursDay);
		
		txtField[0].setText(tmp.toString() + " h");
		txtField[1].setText(hours.toString() + " h");
		txtField[2].setText(hoursPM.toString() + " h");
	}
	
	//###################################################################################################################################################
	
	private BigDecimal calcMinutes(LocalTime a, LocalTime b) {
		if (a.getHour() > 0 || a.getMinute() > 0) {
			if (Duration.between(a, b).toMinutes() > 0) {
				return BigDecimal.valueOf(Duration.between(a, b).toMinutes());
			}
		}
		return BD.ZERO;
	}
	
	private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public LocalDate getDatum() {
		return datum.getDate();
	}

	public void setDatum(LocalDate datum) {
		this.datum.setDate(datum);
	}
	
	public LocalTime getStart() {
		return zeit[0].getTime();
	}

	public void setStart(LocalTime zeit) {
		this.zeit[0].setTime(zeit);
	}
	
	public LocalTime getEnd() {
		return zeit[1].getTime();
	}

	public void setEnd(LocalTime zeit) {
		this.zeit[1].setTime(zeit);
	}
	
	public BigDecimal getStunden() {
		if (txtField[1].getText().isBlank()) return BD.ZERO;
		String text = txtField[1].getText().replace(" h", "").trim();
		return new BigDecimal(text);
	}

	public void setStunden(BigDecimal hours) {
		//if (hours.compareTo(BD.ZERO) == 0) { this.txtField[1].setText(""); return;	}
		this.txtField[1].setText(hours + " h");
	}
	
	public BigDecimal getPause() {
		if (txtField[0].getText().isBlank()) return BD.ZERO;
		String text = txtField[0].getText().replace(" h", "").trim();
		return new BigDecimal(text);
	}

	public void setPause(BigDecimal hours) {
		//if (hours.compareTo(BD.ZERO) == 0) { this.txtField[0].setText(""); return;	}
		this.txtField[0].setText(hours + " h");
	}
	
	public BigDecimal getPlusMinus() {
		if (txtField[2].getText().isBlank()) return BD.ZERO;
		String text = txtField[2].getText().replace(" h", "").trim();
		return new BigDecimal(text);
	}

	public void setPlusMinus(BigDecimal hours) {
		//if (hours.compareTo(BD.ZERO) == 0) { this.txtField[2].setText(""); return;	}
		this.txtField[2].setText(hours + " h");
	}
	
	public String getProjekt() {
		if (projekt.getText().isBlank()) return "";
		String text = projekt.getText().trim();
		return text;
	}

	public void setProjekt(String text) {
		this.projekt.setText(text);
	}

	public OffsetDateTime getOriginalIn() {
		return originalIn;
	}

	public void setOriginalIn(OffsetDateTime original) {
		this.originalIn = original;
	}
	
	public OffsetDateTime getOriginalOut() {
		return originalOut;
	}

	public void setOriginalOut(OffsetDateTime original) {
		this.originalOut = original;
	}
	
	public void setHoursDay(BigDecimal hoursDay) {
		this.hoursDay = hoursDay;
	}
	
	public JButton getBtn() {
		return btn;
	}
	
	//###################################################################################################################################################
	// Fokus-Methoden um das aktive Panel zu loggen
	//###################################################################################################################################################

	public void addRecursiveMouseListener(MouseListener listener) {
		this.addMouseListener(listener);
	}

	public void addRecursiveFocusListener(FocusListener listener) {
		// DatePicker
		datum.getComponentDateTextField().addFocusListener(listener);

		// TimePickers (ztf[] sind die Textfelder darin)
		for (int i = 0; i < zeit.length; i++) {
			if (ztf[i] != null) {
				ztf[i].addFocusListener(listener);
			}
			if (zeit[i] != null) {
				zeit[i].getComponentSpinnerPanel().addFocusListener(listener);
			}
		}

		// TextFields
		// txtField[1] ist laut Code 'setFocusable(false)'
		if (txtField[0] != null) {
			txtField[0].addFocusListener(listener);
		}
		if (projekt != null) {
			projekt.addFocusListener(listener);
		}

		// Button
		if (btn != null) {
			btn.addFocusListener(listener);
		}
	}

}
