package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.gui.misc.CreateButton.createButton;
import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;

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
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.NumberFormatter;

import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CommaHelper;
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

public class BestellungPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(BestellungPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsHead = new JTextField[1];
	private JTextField[] txtFieldsPos = new JTextField[12];
	private JTextField[] txtFieldsAnz = new JTextField[12];
	private JTextField[] txtFieldsEP = new JTextField[12];
	private JTextField[] txtFieldsGP = new JTextField[12];
	private JFormattedTextField[] txtFieldsSum = new JFormattedTextField[3];
	
	private JLabel lblState = null;
	private JComboBox<String> cmbState = null;

	private JButton[] btnFields = new JButton[3];
	
	private String[] sDatum = new String[2];
	BigDecimal bdNetto = BD.ZERO, bdTax = BD.ZERO, bdBrutto = BD.ZERO;
	private String id = null;
	private BigDecimal bdTaxRate = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public BestellungPanel() {
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
		
		String[] selectState = {"", "storniert", "geliefert" };
		
		// Überschriften und Feldbeschriftungen
	    String[] labelsTop = {"Datum:", "Referenz:"};
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
    	lblFieldsTop[1].setBounds(230, 20, 70, 25);
    	
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
	    for (int r = 0; r < txtFieldsHead.length; r++) {
	    	txtFieldsHead[r] = makeField(300, 20, 1000, 25, true, null);
	    	txtFieldsHead[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsHead[r].setFocusable(false);
	    	add(txtFieldsHead[r]);
	    }
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
	    
	    // Buttons
		btnFields[0] = createButton("<html>neu<br>berechnen</html>", ButtonIcon.CALC.icon(), null);
		btnFields[1] = createButton("<html>update</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[2] = createButton("<html>Status<br>setzen</html>", ButtonIcon.SAVE.icon(), null);
		
		btnFields[0].setBounds(1625, 260, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[1].setBounds(1625, 320, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setBounds(1625, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setVisible(false);
		add(btnFields[0]); add(btnFields[1]); add(btnFields[2]);
		
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
    	
    	int anzPos = 0;
    	String[] sPosText = new String[13];
    	BigDecimal[] bdAnzahl = new BigDecimal[this.txtFieldsPos.length];
    	BigDecimal[] bdEinzel = new BigDecimal[this.txtFieldsPos.length];
    	
    	BestellungRepository bestellungRepository = new BestellungRepository();
        Bestellung bestellung = bestellungRepository.findById(id);
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = parseStringToBigDecimalSafe(this.txtFieldsAnz[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[i] = parseStringToBigDecimalSafe(this.txtFieldsEP[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				anzPos = anzPos + 1; // Anzahl der Positionen
    		}
		}
    	
    	bestellung.setDatum(datePicker[0].getDate());
    	bestellung.setRef(txtFieldsHead[0].getText());
    	
    	bestellung.setAnzPos(anzPos);
    	bestellung.setArt01(sPosText[0]); bestellung.setMenge01(bdAnzahl[0]); bestellung.setePreis01(bdEinzel[0]);
    	bestellung.setArt02(sPosText[1]); bestellung.setMenge02(bdAnzahl[1]); bestellung.setePreis02(bdEinzel[1]);
    	bestellung.setArt03(sPosText[2]); bestellung.setMenge03(bdAnzahl[2]); bestellung.setePreis03(bdEinzel[2]);
    	bestellung.setArt04(sPosText[3]); bestellung.setMenge04(bdAnzahl[3]); bestellung.setePreis04(bdEinzel[3]);
    	bestellung.setArt05(sPosText[4]); bestellung.setMenge05(bdAnzahl[4]); bestellung.setePreis05(bdEinzel[4]);
    	bestellung.setArt06(sPosText[5]); bestellung.setMenge06(bdAnzahl[5]); bestellung.setePreis06(bdEinzel[5]);
    	bestellung.setArt07(sPosText[6]); bestellung.setMenge07(bdAnzahl[6]); bestellung.setePreis07(bdEinzel[6]);
    	bestellung.setArt08(sPosText[7]); bestellung.setMenge08(bdAnzahl[7]); bestellung.setePreis08(bdEinzel[7]);
    	bestellung.setArt09(sPosText[8]); bestellung.setMenge09(bdAnzahl[8]); bestellung.setePreis09(bdEinzel[8]);
    	bestellung.setArt10(sPosText[9]); bestellung.setMenge10(bdAnzahl[9]); bestellung.setePreis10(bdEinzel[9]);
    	bestellung.setArt11(sPosText[10]); bestellung.setMenge11(bdAnzahl[10]); bestellung.setePreis11(bdEinzel[10]);
    	bestellung.setArt12(sPosText[11]); bestellung.setMenge12(bdAnzahl[11]); bestellung.setePreis12(bdEinzel[11]);
    	
    	Number numberN = (Number) this.txtFieldsSum[0].getValue();
    	double netto = numberN.doubleValue();
    	bestellung.setNetto(BigDecimal.valueOf(netto));
    	
    	Number numberT = (Number) this.txtFieldsSum[1].getValue();
    	double tax = numberT.doubleValue();
    	bestellung.setUst(BigDecimal.valueOf(tax));
    	
    	Number numberB = (Number) this.txtFieldsSum[2].getValue();
    	double brutto = numberB.doubleValue();
    	bestellung.setBrutto(BigDecimal.valueOf(brutto));
    	
    	bestellungRepository.update(bestellung);
    	
    	HauptFenster.actScreen();
    }
    
    private void updateState() {
    	BestellungRepository bestellungRepository = new BestellungRepository();
        Bestellung bestellung = bestellungRepository.findById(id);
        
        switch (cmbState.getSelectedIndex()) {
		case 1: // storniert
			bestellung.setNetto(BD.ZERO);
			bestellung.setUst(BD.ZERO);
			bestellung.setBrutto(BD.ZERO);
			bestellung.setState(0);
			break;
		case 2: // geliefert
			bestellung.setState(51);
			break;
		default:
			break;
		}
        
        bestellungRepository.update(bestellung);
        
        HauptFenster.actScreen();
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
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
    	
    	BestellungRepository bestellungRepository = new BestellungRepository();
        Bestellung bestellung = bestellungRepository.findById(id);
        
    	if (id.isEmpty() || id == null) {
    		return;
    	}
    	
    	pos.add(bestellung.getArt01()); pos.add(bestellung.getArt02()); pos.add(bestellung.getArt03()); pos.add(bestellung.getArt04());
    	pos.add(bestellung.getArt05()); pos.add(bestellung.getArt06()); pos.add(bestellung.getArt07()); pos.add(bestellung.getArt08());
    	pos.add(bestellung.getArt09()); pos.add(bestellung.getArt10()); pos.add(bestellung.getArt11()); pos.add(bestellung.getArt12());
    	
    	anz.add(bestellung.getMenge01()); anz.add(bestellung.getMenge02()); anz.add(bestellung.getMenge03()); anz.add(bestellung.getMenge04());
    	anz.add(bestellung.getMenge05()); anz.add(bestellung.getMenge06()); anz.add(bestellung.getMenge07()); anz.add(bestellung.getMenge08());
    	anz.add(bestellung.getMenge09()); anz.add(bestellung.getMenge10()); anz.add(bestellung.getMenge11()); anz.add(bestellung.getMenge12());
    	
    	ep.add(bestellung.getePreis01()); ep.add(bestellung.getePreis02()); ep.add(bestellung.getePreis03()); ep.add(bestellung.getePreis04());
    	ep.add(bestellung.getePreis05()); ep.add(bestellung.getePreis06()); ep.add(bestellung.getePreis07()); ep.add(bestellung.getePreis08());
    	ep.add(bestellung.getePreis09()); ep.add(bestellung.getePreis10()); ep.add(bestellung.getePreis11()); ep.add(bestellung.getePreis12());
    	
    	this.id = null; this.bdTaxRate = BD.ZERO;
    	cmbState.setSelectedIndex(0);
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
		
    	this.datePicker[0].setDate(bestellung.getDatum());
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
    		this.txtFieldsHead[i].setText(bestellung.getRef());
    	}
    	for (int i = 0; i < bestellung.getAnzPos(); i++) {
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
    	
    	switch(bestellung.getState()) {
    	case 1:
    		txtFieldsFocusable(true);
    		break;
    	case 11:
    		lblState.setVisible(true);
    		cmbState.setVisible(true);
    		btnFields[2].setVisible(true);
    	}
    }
}
