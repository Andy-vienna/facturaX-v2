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
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
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

public class LieferscheinPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(LieferscheinPanel.class);
	
	JPanel panel = new JPanel();
	private Border b;
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JTextField[] txtFieldsHead = new JTextField[1];
	private JTextField[] txtFieldsPos = new JTextField[12];
	private JTextField[] txtFieldsAnz = new JTextField[12];
	
	private JLabel lblState = null;
	private JComboBox<String> cmbState = null;

	private JButton[] btnFields = new JButton[2];
	
	private String[] sDatum = new String[2];
	private String id = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public LieferscheinPanel() {
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
	    String[] labelsCol = {"Nr:", "Position:", "Anzahl:"};
	    String[] labelsRow = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"};
		
	    // Label Arrays
	    JLabel[] lblFieldsTop = new JLabel[labelsTop.length];
	    JLabel[] lblFieldsCol = new JLabel[labelsCol.length];
	    JLabel[] lblFieldsRow = new JLabel[labelsRow.length];
		
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
    	
    	for (int r = 0; r < labelsRow.length; r++) {
	    	lblFieldsRow[r] = new JLabel(labelsRow[r]);
	    	lblFieldsRow[r].setBounds(10, 70 + r * 25, 40, 25);
	    	lblFieldsRow[r].setHorizontalAlignment(SwingConstants.CENTER);
	    	add(lblFieldsRow[r]);
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
	    	txtFieldsPos[r].setHorizontalAlignment(SwingConstants.LEFT);
	    	txtFieldsPos[r].setFocusable(false);
	    	txtFieldsAnz[r].setFocusable(false);
	    	add(txtFieldsPos[r]);
	    	add(txtFieldsAnz[r]); attachCommaToDot(txtFieldsAnz[r]);
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
		btnFields[0] = createButton("<html>update</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[1] = createButton("<html>Status<br>setzen</html>", ButtonIcon.SAVE.icon(), null);
		
		btnFields[0].setBounds(1625, 320, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[1].setBounds(1625, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnFields[1].setVisible(false);
		add(btnFields[0]); add(btnFields[1]);
		
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
	                btnFields[1].setEnabled(false);
	            } else {
	                btnFields[1].setEnabled(true);
	            }
	        }
	    });
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
	    
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				updateTable();
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
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
		}
		lblState.setVisible(false);
		cmbState.setVisible(false);
    }
    
	//###################################################################################################################################################
    
    private void updateTable() {
    	
    	int anzPos = 0;
    	String[] sPosText = new String[13];
    	BigDecimal[] bdAnzahl = new BigDecimal[this.txtFieldsPos.length];
    	
    	LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
        Lieferschein lieferschein = lieferscheinRepository.findById(id);
    	
    	for (int i = 0; i < this.txtFieldsPos.length; i++) {
    		if (!this.txtFieldsPos[i].getText().isEmpty()) {
				sPosText[i] = this.txtFieldsPos[i].getText();
				bdAnzahl[i] = parseStringToBigDecimalSafe(this.txtFieldsAnz[i].getText(), LocaleFormat.AUTO).setScale(2, RoundingMode.HALF_UP);
				anzPos = anzPos + 1; // Anzahl der Positionen
    		}
		}
    	
    	lieferschein.setDatum(datePicker[0].getDate());
    	lieferschein.setRef(txtFieldsHead[0].getText());
    	
    	lieferschein.setAnzPos(anzPos);
    	lieferschein.setArt01(sPosText[0]); lieferschein.setMenge01(bdAnzahl[0]);
    	lieferschein.setArt02(sPosText[1]); lieferschein.setMenge02(bdAnzahl[1]);
    	lieferschein.setArt03(sPosText[2]); lieferschein.setMenge03(bdAnzahl[2]);
    	lieferschein.setArt04(sPosText[3]); lieferschein.setMenge04(bdAnzahl[3]);
    	lieferschein.setArt05(sPosText[4]); lieferschein.setMenge05(bdAnzahl[4]);
    	lieferschein.setArt06(sPosText[5]); lieferschein.setMenge06(bdAnzahl[5]);
    	lieferschein.setArt07(sPosText[6]); lieferschein.setMenge07(bdAnzahl[6]);
    	lieferschein.setArt08(sPosText[7]); lieferschein.setMenge08(bdAnzahl[7]);
    	lieferschein.setArt09(sPosText[8]); lieferschein.setMenge09(bdAnzahl[8]);
    	lieferschein.setArt10(sPosText[9]); lieferschein.setMenge10(bdAnzahl[9]);
    	lieferschein.setArt11(sPosText[10]); lieferschein.setMenge11(bdAnzahl[10]);
    	lieferschein.setArt12(sPosText[11]); lieferschein.setMenge12(bdAnzahl[11]);
    	
    	lieferscheinRepository.update(lieferschein);
    	
    	HauptFenster.actScreen();
    }
    
    private void updateState() {
    	LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
        Lieferschein lieferschein = lieferscheinRepository.findById(id);
        
        switch (cmbState.getSelectedIndex()) {
		case 1: // storniert
			lieferschein.setState(0);
			break;
		case 2: // geliefert
			lieferschein.setState(51);
			break;
		default:
			break;
		}
        
        lieferscheinRepository.update(lieferschein);
        
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
    
    public void setTxtFields(String id) {
    	
    	ArrayList<String> pos = new ArrayList<>();
    	ArrayList<BigDecimal> anz = new ArrayList<>();
    	
    	LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
        Lieferschein lieferschein = lieferscheinRepository.findById(id);
        
    	if (id.isEmpty() || id == null) {
    		return;
    	}
    	
    	pos.add(lieferschein.getArt01()); pos.add(lieferschein.getArt02()); pos.add(lieferschein.getArt03()); pos.add(lieferschein.getArt04());
    	pos.add(lieferschein.getArt05()); pos.add(lieferschein.getArt06()); pos.add(lieferschein.getArt07()); pos.add(lieferschein.getArt08());
    	pos.add(lieferschein.getArt09()); pos.add(lieferschein.getArt10()); pos.add(lieferschein.getArt11()); pos.add(lieferschein.getArt12());
    	
    	anz.add(lieferschein.getMenge01()); anz.add(lieferschein.getMenge02()); anz.add(lieferschein.getMenge03()); anz.add(lieferschein.getMenge04());
    	anz.add(lieferschein.getMenge05()); anz.add(lieferschein.getMenge06()); anz.add(lieferschein.getMenge07()); anz.add(lieferschein.getMenge08());
    	anz.add(lieferschein.getMenge09()); anz.add(lieferschein.getMenge10()); anz.add(lieferschein.getMenge11()); anz.add(lieferschein.getMenge12());
    	
    	cmbState.setSelectedIndex(0);

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
		}
		txtFieldsFocusable(false);
		
    	this.datePicker[0].setDate(lieferschein.getDatum());
    	for (int i = 0; i < this.txtFieldsHead.length; i++) {
    		this.txtFieldsHead[i].setText(lieferschein.getRef());
    	}
    	for (int i = 0; i < lieferschein.getAnzPos(); i++) {
    		BigDecimal bdAnz = anz.get(i);
			
			this.txtFieldsPos[i].setText(pos.get(i));
			this.txtFieldsAnz[i].setText(bdAnz.toString());
			
		}
    	
    	switch(lieferschein.getState()) {
    	case 1:
    		txtFieldsFocusable(true);
    		btnFields[0].setEnabled(true);
    		btnFields[1].setVisible(false);
    		break;
    	case 11:
    		lblState.setVisible(true);
    		cmbState.setVisible(true);
    		btnFields[0].setEnabled(false);
    		btnFields[1].setVisible(true);
    		break;
    	case 51:
    		lblState.setVisible(false);
    		cmbState.setVisible(false);
    		btnFields[0].setEnabled(false);
    		btnFields[1].setVisible(false);
    		break;
    	default:
    		lblState.setVisible(false);
    		cmbState.setVisible(false);
    		btnFields[0].setEnabled(false);
    		btnFields[1].setVisible(false);
    		break;
    	}
    }
}
