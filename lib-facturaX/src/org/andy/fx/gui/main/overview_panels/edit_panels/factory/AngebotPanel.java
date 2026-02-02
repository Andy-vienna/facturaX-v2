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
import javax.swing.text.AbstractDocument;
import javax.swing.text.NumberFormatter;

import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.ServiceDescriptionPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

public class AngebotPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

	private static final Logger logger = LogManager.getLogger(AngebotPanel.class);
	
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
	private JCheckBox chkPage2;

	private JButton[] btnFields = new JButton[5];
	
	private String[] sDatum = new String[2];
	BigDecimal bdNetto = BD.ZERO, bdTax = BD.ZERO, bdBrutto = BD.ZERO;
	private String id = null;
	private BigDecimal bdTaxRate = BD.ZERO;
	
	private ServiceDescriptionPanel runEditor = new ServiceDescriptionPanel();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public AngebotPanel() {
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
		
		String[] selectState = {"", "storniert", "bestellt" };
		
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
	    
	    chkPage2 = new JCheckBox("<html>Beschreibung<br>hinzufügen/bearbeiten</html>)");
        chkPage2.setBounds(1440,140,150,50); chkPage2.setEnabled(false); add(chkPage2);
	    
	    // Buttons
		btnFields[0] = createButton("<html>neu<br>berechnen</html>", ButtonIcon.CALC.icon(), null);
		btnFields[1] = createButton("<html>update</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[2] = createButton("<html>Status<br>setzen</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[3] = createButton("<html>Revision<br>anlegen</html>", ButtonIcon.REV.icon(), null);
		btnFields[4] = createButton("<html>Editor</html>", ButtonIcon.EDIT.icon(), null);
		
		btnFields[0].setBounds(1625, 260, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[1].setBounds(1625, 320, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setBounds(1625, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[3].setBounds(1625, 200, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[4].setBounds(1625, 140, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[2].setVisible(false);
		btnFields[3].setEnabled(true); btnFields[3].setVisible(false);
		btnFields[4].setEnabled(true); btnFields[4].setVisible(false);
		add(btnFields[0]); add(btnFields[1]); add(btnFields[2]); add(btnFields[3]); add(btnFields[4]);
		
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
	    
	    btnFields[3].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				doRevision();
 			}
 		});
	    
	    btnFields[4].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				doText();
 			}
 		});
	    
	    chkPage2.addActionListener(_ -> btnFields[4].setVisible(chkPage2.isSelected()));
	    
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
		chkPage2.setEnabled(b);
		btnFields[0].setEnabled(b);
		btnFields[1].setEnabled(false);
		btnFields[2].setVisible(false);
		btnFields[3].setVisible(false);
		btnFields[4].setEnabled(b);
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
    	
    	AngebotRepository angebotRepository = new AngebotRepository();
        Angebot angebot = angebotRepository.findById(id);
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = parseStringToBigDecimalSafe(this.txtFieldsAnz[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				bdEinzel[i] = parseStringToBigDecimalSafe(this.txtFieldsEP[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				anzPos = anzPos + 1; // Anzahl der Positionen
    		}
		}
    	
    	angebot.setDatum(datePicker[0].getDate());
    	angebot.setRef(txtFieldsHead[0].getText());
    	
    	if (angebot.getPage2() == 1 || chkPage2.isSelected()) {
    		angebot.setPage2(1);
    		angebot.setBeschreibungHtml(runEditor.getText()); // Liefer- und Leistungsbeschreibung
    	}
    	
    	angebot.setAnzPos(anzPos);
    	angebot.setArt01(sPosText[0]); angebot.setMenge01(bdAnzahl[0]); angebot.setePreis01(bdEinzel[0]);
    	angebot.setArt02(sPosText[1]); angebot.setMenge02(bdAnzahl[1]); angebot.setePreis02(bdEinzel[1]);
    	angebot.setArt03(sPosText[2]); angebot.setMenge03(bdAnzahl[2]); angebot.setePreis03(bdEinzel[2]);
    	angebot.setArt04(sPosText[3]); angebot.setMenge04(bdAnzahl[3]); angebot.setePreis04(bdEinzel[3]);
    	angebot.setArt05(sPosText[4]); angebot.setMenge05(bdAnzahl[4]); angebot.setePreis05(bdEinzel[4]);
    	angebot.setArt06(sPosText[5]); angebot.setMenge06(bdAnzahl[5]); angebot.setePreis06(bdEinzel[5]);
    	angebot.setArt07(sPosText[6]); angebot.setMenge07(bdAnzahl[6]); angebot.setePreis07(bdEinzel[6]);
    	angebot.setArt08(sPosText[7]); angebot.setMenge08(bdAnzahl[7]); angebot.setePreis08(bdEinzel[7]);
    	angebot.setArt09(sPosText[8]); angebot.setMenge09(bdAnzahl[8]); angebot.setePreis09(bdEinzel[8]);
    	angebot.setArt10(sPosText[9]); angebot.setMenge10(bdAnzahl[9]); angebot.setePreis10(bdEinzel[9]);
    	angebot.setArt11(sPosText[10]); angebot.setMenge11(bdAnzahl[10]); angebot.setePreis11(bdEinzel[10]);
    	angebot.setArt12(sPosText[11]); angebot.setMenge12(bdAnzahl[11]); angebot.setePreis12(bdEinzel[11]);
    	
    	Number numberN = (Number) this.txtFieldsSum[0].getValue();
    	double netto = numberN.doubleValue();
    	angebot.setNetto(BigDecimal.valueOf(netto));
    	
    	angebotRepository.update(angebot);
    	
    	HauptFenster.actScreen();
    }
    
    private void updateState() {
    	AngebotRepository angebotRepository = new AngebotRepository();
        Angebot angebot = angebotRepository.findById(id);
        
        switch (cmbState.getSelectedIndex()) {
		case 1: // storniert
			angebot.setNetto(BD.ZERO);
			angebot.setUst(BD.ZERO);
			angebot.setBrutto(BD.ZERO);
			angebot.setState(0);
			break;
		case 2: // bestellt
			angebot.setState(111);
			break;
		default:
			break;
		}
        
        angebotRepository.update(angebot);
        
        HauptFenster.actScreen();
    }
    
    private void doRevision() {
    	AngebotRepository angebotRepository = new AngebotRepository();
    	Angebot angebot = angebotRepository.findById(id);
    	String revisionNr = null;
    	
    	String angebotNr = angebot.getIdNummer();
    	int slashIndex = angebotNr.lastIndexOf('/');
        if (slashIndex >= 0) {
            // Revision existiert -> Zahl inkrementieren
            String basis = angebotNr.substring(0, slashIndex);
            String revStr = angebotNr.substring(slashIndex + 1);
            int rev = Integer.parseInt(revStr);
            revisionNr = basis + "/" + (rev + 1);
        } else {
            // noch keine Revision -> /1 anhängen
        	revisionNr = angebotNr + "/1";
        }
        
        angebot.setState(12); // Status: revisioniert setzen
        angebotRepository.update(angebot);
    	
        angebot.setIdNummer(revisionNr);
        angebot.setState(1); // Status: erstellt
        angebotRepository.save(angebot);
        
        HauptFenster.actScreen();
    }
    
    private void doText() {
    	AngebotRepository angebotRepository = new AngebotRepository();
    	Angebot angebot = angebotRepository.findById(id);
    	if (angebot.getBeschreibungHtml() != null) {
    		runEditor.setText(angebot.getBeschreibungHtml());
    	} else {
    		runEditor.setText(null);
    	}
    	runEditor.setVisible(true);
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
    	
    	AngebotRepository angebotRepository = new AngebotRepository();
        Angebot angebot = angebotRepository.findById(id);
        
    	if (id.isEmpty() || id == null) {
    		return;
    	}
    	
    	pos.add(angebot.getArt01()); pos.add(angebot.getArt02()); pos.add(angebot.getArt03()); pos.add(angebot.getArt04());
    	pos.add(angebot.getArt05()); pos.add(angebot.getArt06()); pos.add(angebot.getArt07()); pos.add(angebot.getArt08());
    	pos.add(angebot.getArt09()); pos.add(angebot.getArt10()); pos.add(angebot.getArt11()); pos.add(angebot.getArt12());
    	
    	anz.add(angebot.getMenge01()); anz.add(angebot.getMenge02()); anz.add(angebot.getMenge03()); anz.add(angebot.getMenge04());
    	anz.add(angebot.getMenge05()); anz.add(angebot.getMenge06()); anz.add(angebot.getMenge07()); anz.add(angebot.getMenge08());
    	anz.add(angebot.getMenge09()); anz.add(angebot.getMenge10()); anz.add(angebot.getMenge11()); anz.add(angebot.getMenge12());
    	
    	ep.add(angebot.getePreis01()); ep.add(angebot.getePreis02()); ep.add(angebot.getePreis03()); ep.add(angebot.getePreis04());
    	ep.add(angebot.getePreis05()); ep.add(angebot.getePreis06()); ep.add(angebot.getePreis07()); ep.add(angebot.getePreis08());
    	ep.add(angebot.getePreis09()); ep.add(angebot.getePreis10()); ep.add(angebot.getePreis11()); ep.add(angebot.getePreis12());
    	
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
		btnFields[4].setVisible(false);
		txtFieldsFocusable(false);
		
    	this.datePicker[0].setDate(angebot.getDatum());
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
    		this.txtFieldsHead[i].setText(angebot.getRef());
    	}
    	for (int i = 0; i < angebot.getAnzPos(); i++) {
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
    	
    	if (angebot.getPage2() == 1) {
    		if (angebot.getBeschreibungHtml() != null) {
        		runEditor.setText(angebot.getBeschreibungHtml());
        	}
    		chkPage2.setSelected(true);
    		btnFields[4].setVisible(true);
    	} else {
    		chkPage2.setSelected(false);
    		btnFields[4].setVisible(false);
    	}
    	
    	switch(angebot.getState()) {
    	case 1:
    		txtFieldsFocusable(true);
    		break;
    	case 11:
    		lblState.setVisible(true);
    		cmbState.setVisible(true);
    		chkPage2.setEnabled(false);
    		btnFields[2].setVisible(true);
    		btnFields[3].setVisible(true);
    	}
    }
}
