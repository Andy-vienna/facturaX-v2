package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToIntSafe;
import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.NumberFormatter;

import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

public class RechnungPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(RechnungPanel.class);
	private static final Pattern P = Pattern.compile("^(\\d{2})\\.(\\d{2})\\.(\\d{4})-(\\d{2})\\.(\\d{2})\\.(\\d{4})$");
	private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsHead = new JTextField[2];
	private JTextField[] txtFieldsPos = new JTextField[12];
	private JTextField[] txtFieldsAnz = new JTextField[12];
	private JTextField[] txtFieldsEP = new JTextField[12];
	private JTextField[] txtFieldsGP = new JTextField[12];
	private JFormattedTextField[] txtFieldsSum = new JFormattedTextField[3];

	private JLabel lblState = null;
	private JComboBox<String> cmbState = null;
	
	private JLabel lblSkonto1, lblSkonto1a, lblSkonto2, lblSkonto2a;
	private JCheckBox chkSkonto1; private JTextField txtSkontoTage1; private JTextField txtSkontoWert1;
    private JCheckBox chkSkonto2; private JTextField txtSkontoTage2; private JTextField txtSkontoWert2;
	
	private JButton[] btnFields = new JButton[3];
	
	private String[] sDatum = new String[2];
	BigDecimal bdNetto = BD.ZERO, bdTax = BD.ZERO, bdBrutto = BD.ZERO;
	private String id = null;
	private BigDecimal bdTaxRate = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public RechnungPanel() {
        super("");
        initContent();
    }

	@Override
	public void initContent() {
		b = getBorder();
	    if (b instanceof TitledBorder) {
	        this.border = (TitledBorder) b;
	    } else {
	        logger.warn("Kein TitledBorder vorhanden – setsTitel() wird nicht funktionieren.");
	    }
	    
		buildPanel();
	}

	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void buildPanel() {
		
		String[] selectState = {"", "storniert", "bezahlt"};
		
		// Überschriften und Feldbeschriftungen
	    String[] labelsTop = {"Datum:", "Leistungszeitraum", "Referenz:"};
	    String[] labelsCol = {"Nr:", "Position:", "Anzahl:", "Einzel:", "Summe"};
	    String[] labelsRow = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
	    String[] labelsBtm = {"Netto:", "USt.:", "Brutto:"};
		
	    // Label Arrays
	    JLabel[] lblFieldsTop = new JLabel[labelsTop.length];
	    JLabel[] lblFieldsCol = new JLabel[labelsCol.length];
	    JLabel[] lblFieldsRow = new JLabel[labelsRow.length];
	    JLabel[] lblFieldsBtm = new JLabel[labelsBtm.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labelsTop.length; r++) {
	    	lblFieldsTop[r] = new JLabel(labelsTop[r]);
	    	add(lblFieldsTop[r]);
	    }
	    lblFieldsTop[0].setBounds(10, 20, 50, 25);
    	lblFieldsTop[1].setBounds(230, 20, 120, 25);
    	lblFieldsTop[2].setBounds(570, 20, 70, 25);
    	
	    for (int r = 0; r < labelsCol.length; r++) {
	    	lblFieldsCol[r] = new JLabel(labelsCol[r]);
	    	lblFieldsCol[r].setHorizontalAlignment(SwingConstants.CENTER);
	    	add(lblFieldsCol[r]);
	    }
	    lblFieldsCol[0].setBounds(10, 45, 40, 25);
    	lblFieldsCol[1].setBounds(50, 45, 800, 25);
    	lblFieldsCol[2].setBounds(850, 45, 75, 25);
    	lblFieldsCol[3].setBounds(925, 45, 200, 25);
    	lblFieldsCol[4].setBounds(1125, 45, 200, 25);
    	
    	for (int r = 0; r < labelsRow.length; r++) {
	    	lblFieldsRow[r] = new JLabel(labelsRow[r]);
	    	lblFieldsRow[r].setBounds(10, 70 + r * 25, 40, 25);
	    	lblFieldsRow[r].setHorizontalAlignment(SwingConstants.CENTER);
	    	add(lblFieldsRow[r]);
	    }
    	
    	for (int r = 0; r < labelsBtm.length; r++) {
	    	lblFieldsBtm[r] = new JLabel(labelsBtm[r]);
	    	lblFieldsBtm[r].setBounds(1345, 295 + r * 25, 100, 25);
	    	lblFieldsBtm[r].setFont(new Font("Tahoma", Font.BOLD, 11));
	    	add(lblFieldsBtm[r]);
	    }
		
	    // Datepicker für Belegdatum
	    for (int i = 0; i < panelDate.length; i++) {
	    	final int ii = i; // final für Lambda-Ausdruck
	    	panelDate[ii] = new DemoPanel();
		    dateSettings[ii] = new DatePickerSettings();
		    datePicker[ii] = new DatePicker(new DatePickerSettings());
			panelDate[ii].scrollPaneForButtons.setEnabled(false);
			dateSettings[ii].setWeekNumbersDisplayed(true, true);
			dateSettings[ii].setFormatForDatesCommonEra("dd.MM.yyyy");
			datePicker[ii] = new DatePicker(dateSettings[i]);
			datePicker[ii].getComponentDateTextField().setBorder(new RoundedBorder(10));
			datePicker[ii].addDateChangeListener(new DateChangeListener() {
				@Override
				public void dateChanged(DateChangeEvent arg0) {
					LocalDate selectedDate = datePicker[ii].getDate();
					if (selectedDate != null) {
						sDatum[ii] = selectedDate.format(StartUp.getDfdate());
					} else {
						sDatum[ii] = null;
					}
				}
			});
			datePicker[ii].setEnabled(false);
			add(datePicker[ii]);
	    }
		datePicker[0].setBounds(60, 20, 150, 25);
		
		// Textfelder
		txtFieldsHead[0] = makeField(640, 20, 1000, 25, true, null); // Referenz
		txtFieldsHead[1] = makeField(350, 20, 200, 25, true, null); // Leistungszeitraum
	    for (int r = 0; r < txtFieldsHead.length; r++) {
	    	txtFieldsHead[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsHead[r].setFocusable(false);
	    	add(txtFieldsHead[r]);
	    }
		JTextField tf = txtFieldsHead[1];
		tf.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void insertUpdate(DocumentEvent e) {	onChange();	}
			@Override public void removeUpdate(DocumentEvent e) {	onChange();	}
			@Override public void changedUpdate(DocumentEvent e) { }
			private void onChange() {
				tf.setOpaque(true); txtFieldsHead[0].setOpaque(true);
				tf.setBackground(tf.getText().contains("eintragen") ? Color.PINK : Color.WHITE);
				txtFieldsHead[0].setBackground(tf.getText().contains("eintragen") ? Color.PINK : Color.WHITE);
				tf.repaint(); txtFieldsHead[0].repaint();
			  }
		});
	    for (int r = 0; r < txtFieldsPos.length; r++) {
	    	txtFieldsPos[r] = makeField(50, 70 + r * 25, 800, 25, false, null);
	    	txtFieldsAnz[r] = makeField(850, 70 + r * 25, 75, 25, false, null);
	    	txtFieldsEP[r] = makeField(925, 70 + r * 25, 200, 25, false, null);
	    	txtFieldsGP[r] = makeField(1125, 70 + r * 25, 200, 25, false, null);
	    	txtFieldsPos[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsPos[r].setFocusable(false);
	    	txtFieldsAnz[r].setFocusable(false);
	    	txtFieldsEP[r].setFocusable(false);
	    	txtFieldsGP[r].setFocusable(false);
	    	add(txtFieldsPos[r]);
	    	add(txtFieldsAnz[r]); attachCommaToDot(txtFieldsAnz[r]);
	    	add(txtFieldsEP[r]); attachCommaToDot(txtFieldsEP[r]);
	    	add(txtFieldsGP[r]); attachCommaToDot(txtFieldsGP[r]);
	    }
	    for (int r = 0; r < txtFieldsSum.length; r++) {
	    	txtFieldsSum[r] = makeFormatField(1445, 295 + r * 25, 150, 25, true, null);
	    	txtFieldsSum[r].setFocusable(false);
	    	add(txtFieldsSum[r]);
	    }
	    
	    // Label für Status
	    lblState = new JLabel("Status:");
	    lblState.setBounds(1345, 70, 100, 25);
	    lblState.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblState.setVisible(false);
	    add(lblState);
	    // ComboBox für Status
	    cmbState = new JComboBox<String>(selectState);
	    cmbState.setBounds(1445, 70, 150, 25);
	    cmbState.setVisible(false);
	    add(cmbState);
	    
	    chkSkonto1 = new JCheckBox("Skonto 1"); chkSkonto1.setBounds(1345, 120, 80, 25); add(chkSkonto1);
        txtSkontoTage1 = new JTextField(); txtSkontoTage1.setBounds(1425, 120, 50, 25); add(txtSkontoTage1);
        lblSkonto1 = new JLabel("Tage"); lblSkonto1.setBounds(1475, 120, 50, 25); add(lblSkonto1);
        txtSkontoWert1 = new JTextField(); txtSkontoWert1.setBounds(1525, 120, 50, 25); add(txtSkontoWert1);
        lblSkonto1a = new JLabel("%"); lblSkonto1a.setBounds(1575, 120, 50, 25); add(lblSkonto1a);
        chkSkonto2 = new JCheckBox("Skonto 2"); chkSkonto2.setBounds(1345, 145, 80, 25); add(chkSkonto2);
        txtSkontoTage2 = new JTextField(); txtSkontoTage2.setBounds(1425, 145, 50, 25); add(txtSkontoTage2);
        lblSkonto2 = new JLabel("Tage"); lblSkonto2.setBounds(1475, 145, 50, 25); add(lblSkonto2);
        txtSkontoWert2 = new JTextField(); txtSkontoWert2.setBounds(1525, 145, 50, 25); add(txtSkontoWert2);
        lblSkonto2a = new JLabel("%"); lblSkonto2a.setBounds(1575, 145, 50, 25); add(lblSkonto2a);
        lblSkonto1.setVisible(false); lblSkonto1a.setVisible(false); lblSkonto2.setVisible(false); lblSkonto2a.setVisible(false);
        chkSkonto1.setVisible(false); txtSkontoTage1.setVisible(false); txtSkontoWert1.setVisible(false);
        chkSkonto2.setVisible(false); txtSkontoTage2.setVisible(false); txtSkontoWert2.setVisible(false);
        chkSkonto1.setEnabled(false); txtSkontoTage1.setEnabled(false); txtSkontoWert1.setEnabled(false);
        chkSkonto2.setEnabled(false); txtSkontoTage2.setEnabled(false); txtSkontoWert2.setEnabled(false);
	    
	    // Buttons
		btnFields[0] = createButton("<html>neu<br>berechnen</html>", ButtonIcon.CALC.icon(), null);
		btnFields[1] = createButton("<html>update</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[2] = createButton("<html>Status<br>setzen</html>", ButtonIcon.SAVE.icon(), null);
		
		btnFields[0].setBounds(1625, 260, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[1].setBounds(1625, 320, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setBounds(1625, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setVisible(false);
		add(btnFields[0]);
		add(btnFields[1]);
		add(btnFields[2]);
		
		setPreferredSize(new Dimension(1000, 70 + txtFieldsPos.length * 25 + 20));
		
		// ------------------------------------------------------------------------------
		// Action Listener für ComboBox
		// ------------------------------------------------------------------------------
		
		cmbState.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {
	            int idx = cmbState.getSelectedIndex();

	            if (idx == 0) {
	                // Leereintrag: Felder leeren, Buttons sperren etc.
	                btnFields[2].setEnabled(false);
	            } else {
	                btnFields[2].setEnabled(true);
	            }
	        }
	    });
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				try {
					calcValue();
					btnFields[1].setEnabled(true);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				updateTable();
 			}
 		});
	    
	    btnFields[2].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				updateState();
 			}
 		});
	    
	}
	
	//###################################################################################################################################################
	
	// Hilfsfunktion für Textfelder
    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(true);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private JFormattedTextField makeFormatField(int x, int y, int w, int h, boolean bold, Color bg) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getCurrencyInstance());
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        JFormattedTextField t = new JFormattedTextField(formatter);
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(false);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
    
    private void txtFieldsFocusable(boolean b) {
    	this.datePicker[0].setEnabled(b);
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
			this.txtFieldsHead[i].setFocusable(b);
		}
		for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsPos[i].setFocusable(b);
			this.txtFieldsAnz[i].setFocusable(b);
			this.txtFieldsEP[i].setFocusable(b);
			this.txtFieldsGP[i].setFocusable(false);
		}
		for (int i = 0; i < this.txtFieldsSum.length; i++) {
			this.txtFieldsSum[i].setFocusable(false);
		}
		lblState.setVisible(false);
		cmbState.setVisible(false);
		btnFields[0].setEnabled(b);
		btnFields[1].setEnabled(false);
		btnFields[2].setVisible(false);
		lblSkonto1.setVisible(b); lblSkonto1a.setVisible(b); lblSkonto2.setVisible(b); lblSkonto2a.setVisible(b);
        chkSkonto1.setVisible(b); txtSkontoTage1.setVisible(b); txtSkontoWert1.setVisible(b);
        chkSkonto2.setVisible(b); txtSkontoTage2.setVisible(b); txtSkontoWert2.setVisible(b);
        chkSkonto1.setEnabled(b); txtSkontoTage1.setEnabled(b); txtSkontoWert1.setEnabled(b);
        chkSkonto2.setEnabled(b); txtSkontoTage2.setEnabled(b); txtSkontoWert2.setEnabled(b);
    }
    
	//###################################################################################################################################################
    
    private void calcValue() throws ParseException {
    	BigDecimal bdEP = BD.ZERO; BigDecimal bdAnz = BD.ZERO; BigDecimal bdGP = BD.ZERO;
    	bdNetto = BD.ZERO;
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsGP[i].setText("");
		}
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
	    		if (this.txtFieldsAnz[i].getText().isEmpty() || this.txtFieldsEP[i].getText().isEmpty()) {
	    			JOptionPane.showMessageDialog(null, "Dateneingabe überprüfen ...", "Fehler", JOptionPane.INFORMATION_MESSAGE);
	    			return;
				}
	    		String anz = this.txtFieldsAnz[i].getText();
	    		String ep = this.txtFieldsEP[i].getText();
	    		
	    		bdAnz = parseStringToBigDecimalSafe(anz, LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
	    		bdEP = parseStringToBigDecimalSafe(ep, LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
	        	bdGP = bdAnz.multiply(bdEP).setScale(2, RoundingMode.HALF_UP);
	
	        	bdNetto = bdNetto.add(bdGP).setScale(2, RoundingMode.HALF_UP);
    			this.txtFieldsGP[i].setText(bdGP.toString());
    		}
		}
    	txtFieldsSum[0].setValue(Double.parseDouble(bdNetto.toString()));
    	bdTax = bdNetto.multiply(bdTaxRate.divide(BD.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[1].setValue(Double.parseDouble(bdTax.toString()));
    	bdBrutto = bdNetto.add(bdTax).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[2].setValue(Double.parseDouble(bdBrutto.toString()));	
    }
    
    private void updateTable() {
    	
        if (datePicker[0].getDate() == null) { info("Rechnungsdatum fehlt …"); return; }
        if (!isValidRange(txtFieldsHead[1].getText())) { info("Leistungszeitraum falsch …"); return; }
        if (isEmpty(txtFieldsHead[0])) { info("Kundenreferenz fehlt …"); return; }
    	
    	int anzPos = 0;
    	String[] sPosText = new String[13];
    	BigDecimal[] bdAnzahl = new BigDecimal[this.txtFieldsPos.length];
    	BigDecimal[] bdEinzel = new BigDecimal[this.txtFieldsPos.length];
    	
    	RechnungRepository rechnungRepository = new RechnungRepository();
        Rechnung rechnung = rechnungRepository.findById(id);
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = parseStringToBigDecimalSafe(this.txtFieldsAnz[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[i] = parseStringToBigDecimalSafe(this.txtFieldsEP[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				anzPos = anzPos + 1; // Anzahl der Positionen
    		}
		}
    	
    	rechnung.setDatum(datePicker[0].getDate());
    	rechnung.setRef(txtFieldsHead[0].getText()); rechnung.setlZeitr(txtFieldsHead[1].getText());
    	
    	rechnung.setAnzPos(anzPos);
    	rechnung.setArt01(sPosText[0]); rechnung.setMenge01(bdAnzahl[0]); rechnung.setePreis01(bdEinzel[0]);
    	rechnung.setArt02(sPosText[1]); rechnung.setMenge02(bdAnzahl[1]); rechnung.setePreis02(bdEinzel[1]);
    	rechnung.setArt03(sPosText[2]); rechnung.setMenge03(bdAnzahl[2]); rechnung.setePreis03(bdEinzel[2]);
    	rechnung.setArt04(sPosText[3]); rechnung.setMenge04(bdAnzahl[3]); rechnung.setePreis04(bdEinzel[3]);
    	rechnung.setArt05(sPosText[4]); rechnung.setMenge05(bdAnzahl[4]); rechnung.setePreis05(bdEinzel[4]);
    	rechnung.setArt06(sPosText[5]); rechnung.setMenge06(bdAnzahl[5]); rechnung.setePreis06(bdEinzel[5]);
    	rechnung.setArt07(sPosText[6]); rechnung.setMenge07(bdAnzahl[6]); rechnung.setePreis07(bdEinzel[6]);
    	rechnung.setArt08(sPosText[7]); rechnung.setMenge08(bdAnzahl[7]); rechnung.setePreis08(bdEinzel[7]);
    	rechnung.setArt09(sPosText[8]); rechnung.setMenge09(bdAnzahl[8]); rechnung.setePreis09(bdEinzel[8]);
    	rechnung.setArt10(sPosText[9]); rechnung.setMenge10(bdAnzahl[9]); rechnung.setePreis10(bdEinzel[9]);
    	rechnung.setArt11(sPosText[10]); rechnung.setMenge11(bdAnzahl[10]); rechnung.setePreis11(bdEinzel[10]);
    	rechnung.setArt12(sPosText[11]); rechnung.setMenge12(bdAnzahl[11]); rechnung.setePreis12(bdEinzel[11]);
    	
    	Number numberN = (Number) this.txtFieldsSum[0].getValue();
    	double netto = numberN.doubleValue();
    	rechnung.setNetto(BigDecimal.valueOf(netto));
    	
    	Number numberT = (Number) this.txtFieldsSum[1].getValue();
    	double tax = numberT.doubleValue();
    	rechnung.setUst(BigDecimal.valueOf(tax));
    	
    	Number numberB = (Number) this.txtFieldsSum[2].getValue();
    	double brutto = numberB.doubleValue();
    	rechnung.setBrutto(BigDecimal.valueOf(brutto));
    	
    	rechnung.setSkonto1(chkSkonto1.isSelected()?1:0);
    	rechnung.setSkonto1tage(txtSkontoTage1.getText().equals("")?0:parseStringToIntSafe(txtSkontoTage1.getText()));
    	rechnung.setSkonto1wert(txtSkontoTage1.getText().equals("")?BD.ZERO:parseStringToBigDecimalSafe(txtSkontoWert1.getText(), LocaleFormat.AUTO).divide(BD.HUNDRED));
    	rechnung.setSkonto2(chkSkonto2.isSelected()?1:0);
    	rechnung.setSkonto2tage(txtSkontoTage2.getText().equals("")?0:parseStringToIntSafe(txtSkontoTage2.getText()));
    	rechnung.setSkonto2wert(txtSkontoTage2.getText().equals("")?BD.ZERO:parseStringToBigDecimalSafe(txtSkontoWert2.getText(), LocaleFormat.AUTO).divide(BD.HUNDRED));
    	
    	rechnungRepository.update(rechnung); // Hibernate update
    	HauptFenster.actScreen();
    }
    
    private void updateState() {
    	RechnungRepository rechnungRepository = new RechnungRepository();
        Rechnung rechnung = rechnungRepository.findById(id);
        switch (cmbState.getSelectedIndex()) {
		case 1: // storniert
			rechnung.setNetto(BD.ZERO);
			rechnung.setUst(BD.ZERO);
			rechnung.setBrutto(BD.ZERO);
			rechnung.setState(0);
			break;
		case 2: // bezahlt
			rechnung.setState(111);
			break;
		case 3: // bezahlt mit Skonto 1
			updateSkonto(rechnung, 1);
			break;
		case 4: // bezahlt mit Skonto 2
			updateSkonto(rechnung, 2);
			break;
		default:
			break;
		}
        rechnungRepository.update(rechnung); // Hibernate update
        HauptFenster.actScreen();
    }
    
    private void updateSkonto(Rechnung r, int stufe) {
    	KundeRepository kundeRepository = new KundeRepository();
    	List<Kunde> kundeListe = kundeRepository.findAll();
    	BigDecimal oldNetto = BD.ZERO, skonto = BD.ZERO, taxRate = BD.ZERO;
    	BigDecimal newNetto = BD.ZERO, newUSt = BD.ZERO, newBrutto = BD.ZERO;
    	for (int i = 0; i < kundeListe.size(); i++) {
    		Kunde k = kundeListe.get(i);
    		if (k.getId().equals(r.getIdKunde())){
    			taxRate = parseStringToBigDecimalSafe(k.getTaxvalue(), LocaleFormat.AUTO).divide(BD.HUNDRED);
    		}
    	}
    	switch(stufe) {
    	case 1:
    		oldNetto = r.getNetto();
    		skonto = oldNetto.multiply(r.getSkonto1wert());
    		r.setState(112);
    		break;
    	case 2:
    		oldNetto = r.getNetto();
    		skonto = oldNetto.multiply(r.getSkonto2wert());
    		r.setState(113);
    		break;
    	}
    	newNetto = oldNetto.subtract(skonto); newUSt = newNetto.multiply(taxRate); newBrutto = newNetto.add(newUSt);
    	r.setNetto(newNetto); r.setUst(newUSt); r.setBrutto(newBrutto);
    }
    
    public static boolean isValidRange(String s) {
        Matcher m = P.matcher(s);
        if (!m.matches()) return false;
        try {
            LocalDate from = LocalDate.parse(m.group(1)+"."+m.group(2)+"."+m.group(3), F);
            LocalDate to   = LocalDate.parse(m.group(4)+"."+m.group(5)+"."+m.group(6), F);
            return !to.isBefore(from);
        } catch (DateTimeParseException ex) {
            return false; // z. B. 31.02.2025
        }
    }
    
    private static boolean isEmpty(JTextField t){ return t.getText()==null || t.getText().trim().isEmpty(); }
    
    private static void info(String msg){
        JOptionPane.showMessageDialog(null, msg, "Rechnung erstellen", JOptionPane.INFORMATION_MESSAGE);
    }
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################
    
    public void setsTitel(String sTitel) {
    	if (b instanceof TitledBorder) {
	        this.border = (TitledBorder) b;
	        this.border.setTitle(sTitel);
	    	this.border.setTitleColor(Color.BLUE);
	    	this.border.setTitleFont(new Font("Tahoma", Font.BOLD, 12));
	    	this.repaint();  // wichtig, damit es sichtbar wird
	    }
	}
    
    public void setTxtFields(String id, String TaxVal) {
    	
    	ArrayList<String> pos = new ArrayList<>();
    	ArrayList<BigDecimal> anz = new ArrayList<>();
    	ArrayList<BigDecimal> ep = new ArrayList<>();
    	    	
    	RechnungRepository rechnungRepository = new RechnungRepository();
        Rechnung rechnung = rechnungRepository.findById(id);
        
        if (id.isEmpty() || id == null) {
    		return;
    	}
        
        pos.add(rechnung.getArt01()); pos.add(rechnung.getArt02()); pos.add(rechnung.getArt03()); pos.add(rechnung.getArt04());
    	pos.add(rechnung.getArt05()); pos.add(rechnung.getArt06()); pos.add(rechnung.getArt07()); pos.add(rechnung.getArt08());
    	pos.add(rechnung.getArt09()); pos.add(rechnung.getArt10()); pos.add(rechnung.getArt11()); pos.add(rechnung.getArt12());
    	
    	anz.add(rechnung.getMenge01()); anz.add(rechnung.getMenge02()); anz.add(rechnung.getMenge03()); anz.add(rechnung.getMenge04());
    	anz.add(rechnung.getMenge05()); anz.add(rechnung.getMenge06()); anz.add(rechnung.getMenge07()); anz.add(rechnung.getMenge08());
    	anz.add(rechnung.getMenge09()); anz.add(rechnung.getMenge10()); anz.add(rechnung.getMenge11()); anz.add(rechnung.getMenge12());
    	
    	ep.add(rechnung.getePreis01()); ep.add(rechnung.getePreis02()); ep.add(rechnung.getePreis03()); ep.add(rechnung.getePreis04());
    	ep.add(rechnung.getePreis05()); ep.add(rechnung.getePreis06()); ep.add(rechnung.getePreis07()); ep.add(rechnung.getePreis08());
    	ep.add(rechnung.getePreis09()); ep.add(rechnung.getePreis10()); ep.add(rechnung.getePreis11()); ep.add(rechnung.getePreis12());
    	
    	
    	this.id = null; this.bdTaxRate = BD.ZERO;
    	bdNetto = BD.ZERO;
    	bdTaxRate = parseStringToBigDecimalSafe(TaxVal, LocaleFormat.AUTO);

    	if (id != null && !id.isEmpty()) {
			this.id = id;
		}
		this.datePicker[0].setDate(null);
		for (int i = 0; i < this.txtFieldsHead.length; i++) {
			this.txtFieldsHead[i].setText("");
		}
		for (int i = 0; i < this.txtFieldsPos.length; i++) {
			this.txtFieldsPos[i].setText("");
			this.txtFieldsAnz[i].setText("");
			this.txtFieldsEP[i].setText("");
			this.txtFieldsGP[i].setText("");
		}
		for (int i = 0; i < this.txtFieldsSum.length; i++) {
			this.txtFieldsSum[i].setValue(null);
		}
		btnFields[0].setEnabled(false);
		txtFieldsFocusable(false);
		
		this.datePicker[0].setDate(rechnung.getDatum());
		this.txtFieldsHead[0].setText(rechnung.getRef());
		this.txtFieldsHead[1].setText(rechnung.getlZeitr());
		for (int i = 0; i < rechnung.getAnzPos(); i++) {
    		BigDecimal bdAnz = anz.get(i);
    		BigDecimal bdEP = ep.get(i);
    		BigDecimal bdGP = bdAnz.multiply(bdEP).setScale(2, RoundingMode.HALF_UP);
    		bdNetto = bdNetto.add(bdGP).setScale(2, RoundingMode.HALF_UP);
			
			this.txtFieldsPos[i].setText(pos.get(i));
			this.txtFieldsAnz[i].setText(bdAnz.toString());
			this.txtFieldsEP[i].setText(bdEP.toString());
			this.txtFieldsGP[i].setText(bdGP.toString());
			
		}
		txtFieldsSum[0].setValue(Double.parseDouble(bdNetto.toString()));
    	bdTax = bdNetto.multiply(bdTaxRate.divide(BD.HUNDRED)).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[1].setValue(Double.parseDouble(bdTax.toString()));
    	bdBrutto = bdNetto.add(bdTax).setScale(2, RoundingMode.HALF_UP);
    	txtFieldsSum[2].setValue(Double.parseDouble(bdBrutto.toString()));
    	
    	if (rechnung.getSkonto1() == 1) {chkSkonto1.setSelected(true);} else {chkSkonto1.setSelected(false);}
    	txtSkontoTage1.setText(String.valueOf(rechnung.getSkonto1tage()));
    	txtSkontoWert1.setText(rechnung.getSkonto1wert().multiply(BD.HUNDRED).setScale(1, RoundingMode.HALF_UP).toString());
    	if (rechnung.getSkonto2() == 1) {chkSkonto2.setSelected(true);} else {chkSkonto2.setSelected(false);}
    	txtSkontoTage2.setText(String.valueOf(rechnung.getSkonto2tage()));
    	txtSkontoWert2.setText(rechnung.getSkonto2wert().multiply(BD.HUNDRED).setScale(1, RoundingMode.HALF_UP).toString());

    	switch(rechnung.getState()) {
    	case 1:
    		txtFieldsFocusable(true);
    		break;
    	case 11, 211, 311, 411:
    		String[] selectState = null;
    		if (rechnung.getSkonto1() == 1) {
    			selectState = new String[] {"", "storniert", "bezahlt", "bezahlt Skonto 1"};
    			if (rechnung.getSkonto2() == 1) {
    				selectState = new String[] {"", "storniert", "bezahlt", "bezahlt Skonto 1", "bezahlt Skonto 2"};
    			}
    			cmbState.setModel(new DefaultComboBoxModel<>(selectState));
    			cmbState.setSelectedIndex(0);
    		}
    		lblState.setVisible(true);
    		cmbState.setVisible(true);
    		btnFields[2].setVisible(true);
    		lblSkonto1.setVisible(true); lblSkonto1a.setVisible(true); lblSkonto2.setVisible(true); lblSkonto2a.setVisible(true);
    		chkSkonto1.setVisible(true); txtSkontoTage1.setVisible(true); txtSkontoWert1.setVisible(true);
            chkSkonto2.setVisible(true); txtSkontoTage2.setVisible(true); txtSkontoWert2.setVisible(true);
    	}
    }
}
