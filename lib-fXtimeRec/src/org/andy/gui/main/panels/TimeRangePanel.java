package org.andy.gui.main.panels;

import static org.andy.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Locale;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.andy.code.misc.BD;
import org.andy.code.misc.CodeListen;
import org.andy.gui.iconHandler.ButtonIcon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.optionalusertools.TimeChangeListener;

public final class TimeRangePanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final File file = Path.of("spesen.json").toFile();
	
	private CodeListen cl = new CodeListen();
	private int posx = 0; private int posy = 0;	private final int w = 150; private final int h = 25;
	private BigDecimal hours = BD.ZERO; BigDecimal wert = BD.ZERO;
	private String land = null;
	private DatePicker datum = new DatePicker();
	private TimePicker[] zeit = new TimePicker[2];
	private JTextField[] ztf = new JTextField[2];
	private JTextField diff; private final JTextField betrag; private JTextField grund;
	private JButton btn = new JButton();
	private JComboBox<String> cmb = new JComboBox<>();
	
	private DateChangeListener dcl = _ -> dateOnChange();
	private ActionListener cal = _ -> land = countryOnChange();
	private ActionListener bal = _ -> doBtnAction();
	
	private Map<String, BigDecimal> values = Map.of();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public TimeRangePanel() throws IOException {
		
		setLayout(null);
		values = loadIsoValues(file);
		
		DatePickerSettings d = new DatePickerSettings(Locale.GERMAN);
		d.setFormatForDatesCommonEra("dd.MM.yyyy");

		TimePickerSettings s = new TimePickerSettings(Locale.GERMAN);
		s.use24HourClockFormat();
		s.initialTime = LocalTime.of(0, 0);
		s.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
		s.setDisplaySpinnerButtons(true);
		
		datum.setSettings(d); datum.setBounds(posx, posy, w, h);
		JTextField dt = datum.getComponentDateTextField(); dt.setHorizontalAlignment(SwingConstants.CENTER);
		datum.addDateChangeListener(dcl);
		add(datum);
		posx = posx + w;
		
		for (int i = 0; i < zeit.length; i++) {
			zeit[i] = new TimePicker(s); zeit[i].setBounds(posx + (i * w), posy, w, h);
			ztf[i] = zeit[i].getComponentTimeTextField(); ztf[i].setHorizontalAlignment(SwingConstants.CENTER);
			ztf[i].setOpaque(true);
			zeit[i].setEnabled(false);
			add(zeit[i]);
		}
		wireTimeListeners();
		posx = posx + (zeit.length * w);
		
		cmb = new JComboBox<>(cl.getCountries().toArray(new String[0]));
		cmb.setBounds(posx, posy, w, h);
		cmb.addActionListener(cal);
		cmb.setEnabled(false);
		add(cmb);
		posx = posx + w;
		
		diff = new JTextField(); diff.setFocusable(false);
		diff.setFont(new Font("Tahoma", Font.BOLD, 12)); diff.setBounds(posx, posy, w, h); diff.setHorizontalAlignment(SwingConstants.RIGHT);
		diff.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { diffOnChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { diffOnChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(diff);
		posx = posx + w;
		
		betrag = new JTextField(); betrag.setFocusable(false);
		betrag.setFont(new Font("Tahoma", Font.BOLD, 12)); betrag.setBounds(posx, posy, w, h); betrag.setHorizontalAlignment(SwingConstants.RIGHT);
		betrag.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { betragOnChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { betragOnChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(betrag);
		posx = posx + w;
		
		grund = new JTextField(); grund.setFocusable(false);
		grund.setFont(new Font("Tahoma", Font.PLAIN, 12)); grund.setBounds(posx, posy, w * 3, h);
		grund.getDocument().addDocumentListener(new DocumentListener() {
			  @Override public void insertUpdate(DocumentEvent e) { grundOnChange(); }
			  @Override public void removeUpdate(DocumentEvent e) { grundOnChange(); }
			  @Override public void changedUpdate(DocumentEvent e) { }
		});
		add(grund);
		posx = posx + (w * 3);
		
		btn = createButton("", ButtonIcon.CALC16.icon(), null);
		btn.setBounds(posx, posy, 50, h);
		btn.addActionListener(bal);
		add(btn);
		posx = posx + 50;

		setPreferredSize(new Dimension(posx, h));
	}

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	public static Map<String, BigDecimal> loadIsoValues(File jsonFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonFile, new TypeReference<Map<String, BigDecimal>>() {});
    }
	
	//###################################################################################################################################################
	
	private void wireTimeListeners() {
	    TimeChangeListener l = _ -> SwingUtilities.invokeLater(this::timeOnChange);
	    zeit[0].addTimeChangeListener(l); zeit[1].addTimeChangeListener(l);
	    timeOnChange(); // initial
	}
	private void dateOnChange() {
		LocalDate date = datum.getDate();
		if (date != null) {
			zeit[0].setEnabled(true);
			zeit[1].setEnabled(true);
			cmb.setEnabled(true);
		} else {
			zeit[0].setEnabled(false);
			zeit[1].setEnabled(false);
			cmb.setEnabled(false);
		}
	}
	private void timeOnChange() {
		final Color GREEN   = new Color(144,238,144);
		final Color NORMAL0 = ztf[0].getBackground(); final Color NORMAL1 = ztf[1].getBackground();
		LocalTime t0 = zeit[0].getTime(); LocalTime t1 = zeit[1].getTime();
	    boolean gt0_0 = t0 != null && t0.toSecondOfDay() > 0; boolean gt0_1 = t1 != null && t1.toSecondOfDay() > 0;
	    ztf[0].setBackground(gt0_0 ? GREEN : NORMAL0); ztf[1].setBackground(gt0_1 ? GREEN : NORMAL1);
	}
	private String countryOnChange() {
		String selected = (String) cmb.getSelectedItem();
		if (selected != null && selected.length() >= 2) {
			land = selected.substring(0, 2);
			cmb.setToolTipText(cl.getCountryFromCode(land));
			cmb.setBackground(new Color(144,238,144));
		} else {
			cmb.setToolTipText("");
			cmb.setBackground(Color.WHITE);
		}
		grund.setFocusable(true);
		btn.setEnabled(true);
		return land;
	}
	private void diffOnChange() {
		if (diff.getText().isBlank() || diff.getText().equals("0.00 h")) return;
		diff.setBackground(new Color(144,238,144)); // light green
	}
	private void betragOnChange() {
		if (betrag.getText().isBlank() || betrag.getText().equals("0.00 EUR")) return;
		betrag.setBackground(new Color(144,238,144)); // light green
	}
	private void grundOnChange() {
		if (grund.getText().isBlank()) { grund.setBackground(Color.WHITE); return; }
		grund.setBackground(new Color(191,239,255)); // light blue
	}
	private void doBtnAction() {
		if (grund.getText().isBlank()) {
			JOptionPane.showMessageDialog(this,  "Bitte geben Sie einen Grund für die Spesen an.", "Fehlende Angabe", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		LocalTime a = zeit[0].getTime(); LocalTime b = zeit[1].getTime();
		BigDecimal hour = BigDecimal.valueOf(60);
		
		BigDecimal mins = calcMinutes(a, b);
		hours = mins.divide(hour, 2, RoundingMode.HALF_UP);
		wert = calcSpesen(land, hours);
		
		diff.setBackground(new Color(144,238,144)); // light green
		betrag.setBackground(new Color(144,238,144)); // light green
		diff.setText(hours.toString() + " h"); betrag.setText(wert.toString() + " EUR");
	}
	
	//###################################################################################################################################################
	
	private BigDecimal calcMinutes(LocalTime a, LocalTime b) {
		int minuten = 0;
		BigDecimal tagMinuten = BigDecimal.valueOf(1440);
		if (a.getHour() == 0 && a.getMinute() == 0 && b.getHour() == 0 && b.getMinute() == 0) {
			return tagMinuten; // für den ganzen Tag von 0-24 Uhr
		}
		if (a.getHour() > 0 || a.getMinute() > 0) {
			if (b.getHour() == 0 && b.getMinute() == 0) {
				minuten = (a.getHour() * 60) + a.getMinute();
				return tagMinuten.subtract(BigDecimal.valueOf(minuten));
			} else {
				if (Duration.between(a, b).toMinutes() > 0) {
					return BigDecimal.valueOf(Duration.between(a, b).toMinutes());
				}
			}
		}
		if (b.getHour() > 0 || b.getMinute() > 0) {
			if (a.getHour() == 0 && a.getMinute() == 0) {
				minuten = (b.getHour() * 60) + b.getMinute();
				return BigDecimal.valueOf(minuten);
			}
		}
		return BD.ZERO;
	}
	
	private BigDecimal calcSpesen(String land, BigDecimal stunden) {
		BigDecimal rate = BD.ZERO;
		rate = values.getOrDefault(land.toLowerCase(Locale.ROOT), BigDecimal.ZERO);
		BigDecimal div = rate.divide(BD.TWELVE, 6, RoundingMode.HALF_UP);
		
		if (stunden.compareTo(BD.THREE) < 0) return BD.ZERO;
		if (stunden.compareTo(BD.THREE) >= 0 && stunden.compareTo(BD.FOUR) < 0) return div.multiply(BD.FOUR).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.FOUR) >= 0 && stunden.compareTo(BD.FIVE) < 0) return div.multiply(BD.FIVE).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.FIVE) >= 0 && stunden.compareTo(BD.SIX) < 0) return div.multiply(BD.SIX).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.SIX) >= 0 && stunden.compareTo(BD.SEVEN) < 0) return div.multiply(BD.SEVEN).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.SEVEN) >= 0 && stunden.compareTo(BD.EIGHT) < 0) return div.multiply(BD.EIGHT).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.EIGHT) >= 0 && stunden.compareTo(BD.NINE) < 0) return div.multiply(BD.NINE).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.NINE) >= 0 && stunden.compareTo(BD.TEN) < 0) return div.multiply(BD.TEN).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.TEN) >= 0 && stunden.compareTo(BD.ELEVEN) < 0) return div.multiply(BD.ELEVEN).setScale(2, RoundingMode.HALF_UP);
		if (stunden.compareTo(BD.ELEVEN) >= 0) return rate.setScale(2, RoundingMode.HALF_UP);
		return BD.ZERO;
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public BigDecimal getBetrag() {
		if (betrag.getText().isBlank()) return BD.ZERO;
		String text = betrag.getText().replace(" EUR", "").trim();
		return new BigDecimal(text);
	}

	public void setBetrag(BigDecimal wert) {
		this.betrag.setText(wert + " EUR");
	}
	
	public LocalDate getDatum() {
		return datum.getDate();
	}

	public void setDatum(LocalDate datum) {
		this.datum.setDate(datum);
	}
	
	public LocalTime getEnd() {
		return zeit[1].getTime();
	}

	public void setEnd(LocalTime zeit) {
		this.zeit[1].setTime(zeit);
	}
	
	public String getGrund() {
		return grund.getText();
	}
	
	public void setGrund(String grund) {
		this.grund.setText(grund);
	}
	
	public String getLand() {
		return land;
	}

	public void setLand(String land) {
		if (land == null || land.isBlank()) return;
		for (int i = 0; i < cl.getCountries().toArray(new String[0]).length; i++) {
			if (cl.getCountries().toArray(new String[0])[i].startsWith(land)) {
				cmb.setSelectedIndex(i);
				break;
			}
		}
	}
	
	public LocalTime getStart() {
		return zeit[0].getTime();
	}

	public void setStart(LocalTime zeit) {
		this.zeit[0].setTime(zeit);
	}
	
	public BigDecimal getStunden() {
		if (diff.getText().isBlank()) return BD.ZERO;
		String text = diff.getText().replace(" h", "").trim();
		return new BigDecimal(text);
	}

	public void setStunden(BigDecimal hours) {
		this.diff.setText(hours + " h");
	}

}
