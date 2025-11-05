package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.FileSelect.chooseFile;
import static org.andy.fx.code.misc.FileSelect.choosePath;
import static org.andy.fx.code.misc.FileSelect.getNotSelected;
import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.andy.fx.code.dataStructure.repositoryProductive.SVSteuerRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.dialogs.DateianzeigeDialog;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.zinternaltools.DemoPanel;

public class SvTaxPanel extends EditPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SvTaxPanel.class);
	
	private static final int FORDERUNG = 0;
	private static final int ZAHLUNG = 10;
	private static final int DATEI = 55;
	
	JPanel panel = new JPanel();
	private Border b;
	
	private TitledBorder border;
	private DemoPanel[] panelDate = new DemoPanel[2];
	private DatePickerSettings[] dateSettings = new DatePickerSettings[2];
	private DatePicker[] datePicker = new DatePicker[2];
	private JComboBox<String> cmbOrganisation;
	private JComboBox<String> cmbBezeichnung;
	private JTextField[] txtFields = new JTextField[2];
	private JTextField txtFile = new JTextField();
	private JLabel lblFileTyp = new JLabel();
	private JButton[] btnFields = new JButton[2];
	private JRadioButton rbZahllast = new JRadioButton("Forderung");
	private JRadioButton rbZahlung  = new JRadioButton("Zahlung");
	private JRadioButton rbDatei  = new JRadioButton("Dateiablage");
	private ButtonGroup grp = new ButtonGroup();
	
	private int id = 0;
	private boolean file = false;
	private boolean neuBeleg = false;
	
	private SVSteuerRepository svsteuerRepository = new SVSteuerRepository();
	private SVSteuer svsteuer = new SVSteuer();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public SvTaxPanel() {
        super("Steuer und Sozialversicherung");
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
		
		grp.add(rbZahllast); grp.add(rbZahlung); grp.add(rbDatei);
		
		// Überschriften und Feldbeschriftungen
	    String[] labels = {"Eingangsdatum:", "Organisation:", "Bezeichnung:", "Betrag:", "Zahlungsziel:", "Dateianhang:"};
	    String[] orga = {"", "Finanzamt Österreich", "Sozialversicherungsanstalt der Selbstständigen"};
	    String[] art = {"", "freie Texteingabe", "Beitragsveroschreibung Q1", "Beitragsveroschreibung Q2", "Beitragsveroschreibung Q3", "Beitragsveroschreibung Q4",
	    		"Zahlungserinnerung Beitragsvorschreibung"};
		
	    // Label Arrays
	    JLabel[] lblFields = new JLabel[labels.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labels.length; r++) {
	    	lblFields[r] = new JLabel(labels[r]);
	    	lblFields[r].setBounds(10, 20 + r * 25, 200, 25);
	    	add(lblFields[r]);
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
			add(datePicker[ii]);
	    }
		datePicker[0].setBounds(212, 20, 180, 25);
		
		// Textfelder
	    
	    txtFields[0] = makeField(210, 70, 400, 25, false, null);
	    txtFields[1] = makeField(210, 95, 180, 25, false, null);
	    for (int r = 0; r < txtFields.length; r++) {
	    	add(txtFields[r]);
	    }
	    txtFields[0].setVisible(false);
	    attachCommaToDot(txtFields[1]);
	    
	    txtFields[1].getDocument().addDocumentListener(new DocumentListener() {
	        private void onChange() {
	        	String txt = txtFields[1].getText();
	            if (txt.length() > 0) {
	            	rbZahllast.setEnabled(true);
	            	rbZahlung.setEnabled(true);
	            	rbDatei.setEnabled(true);
	            }
	        }
	        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { onChange(); }
	        @Override public void removeUpdate(javax.swing.event.DocumentEvent e)  { onChange(); }
	        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { /* bei JTextField meist nie */ }
	    });
	    
	    rbZahllast.setBounds(400, 95, 100, 25); rbZahlung.setBounds(500, 95, 100, 25); rbDatei.setBounds(400, 120, 100, 25);
	    add(rbZahllast); add(rbZahlung); add(rbDatei);
	    ItemListener l = _ -> {
	        if (rbZahlung.isSelected()) {
	            BigDecimal tmp = new BigDecimal(txtFields[1].getText().trim());
	            if (tmp.compareTo(BD.ZERO)== 1) {
	            	txtFields[1].setText(tmp.multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP).toString());
	            }
	        } else if (rbZahllast.isSelected()) {
	        	BigDecimal tmp = new BigDecimal(txtFields[1].getText().trim());
	            if (tmp.compareTo(BD.ZERO)== -1) {
	            	txtFields[1].setText(tmp.multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP).toString());
	            }
	        } else if (rbDatei.isSelected()) {
	        	txtFields[1].setText(BD.ZERO.setScale(2, RoundingMode.HALF_UP).toString());
	        }
	    };
	    rbZahlung.addItemListener(l);
	    rbZahllast.addItemListener(l);
	    rbDatei.addItemListener(l);
		
		cmbOrganisation = new JComboBox<>(orga);
		cmbOrganisation.setBounds(210, 45, 400, 25);
		cmbOrganisation.setSelectedIndex(0);
		add(cmbOrganisation);
		
		cmbBezeichnung = new JComboBox<>(art);
		cmbBezeichnung.setBounds(210, 70, 400,25);
		cmbBezeichnung.setSelectedIndex(0);
		cmbBezeichnung.setVisible(true);
		add(cmbBezeichnung);
		
		cmbOrganisation.addActionListener(orgaListener);
		cmbBezeichnung.addActionListener(artListener);
	    
	    datePicker[1].setBounds(212, 120, 180, 25);
	    
	    txtFile = makeField(210, 145, 400, 25, false, null);
	    txtFile.setFocusable(false);
	    add(txtFile);
	    
	    // Anzeige Filetyp
	    lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileTyp.setBounds(660, 45, 50, 40);
		add(lblFileTyp);
	    
	    btnFields[0] = new JButton();
	    btnFields[0].setToolTipText("");
	    btnFields[0].setBounds(145, 145, 65, 25);
	    add(btnFields[0]);

		btnFields[1] = createButton("", ButtonIcon.SAVE.icon(), null);
		btnFields[1].setEnabled(true);
		btnFields[1].setBounds(660, 120, HauptFenster.getButtonx(), HauptFenster.getButtony());
		add(btnFields[1]);
		
		txtFieldsFocusable(false);
		datePicker[0].setEnabled(false); datePicker[1].setEnabled(false);
		cmbOrganisation.setEnabled(false); cmbBezeichnung.setEnabled(false);
		rbZahllast.setEnabled(false); rbZahlung.setEnabled(false); rbDatei.setEnabled(false);
		setPreferredSize(new Dimension(1000, 20 + 5 * 25 + 50));
	    
	    // ------------------------------------------------------------------------------
 		// Action Listener für Buttons
 		// ------------------------------------------------------------------------------
		lblFileTyp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(lblFileTyp.getIcon() != null) {
					String outputPath;
					outputPath = choosePath(Einstellungen.getAppSettings().work);
					Path path = Paths.get(outputPath);
					if (outputPath.equals(getNotSelected())) {
						return; // nichts ausgewählt
					}
					try {
						svsteuerRepository.exportFileById(svsteuer.getId(), path);
					} catch (Exception e1) {
						logger.error("Fehler beim speichern der Datei " + outputPath + ": " + e1.getMessage());
					}
				}
			}
		});
				
	    btnFields[0].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				String FileNamePath = chooseFile(Einstellungen.getAppSettings().work);
 				File fn = new File(FileNamePath);
 				String FileName = fn.getName();
 				txtFile.setText(FileName);
 				svsteuer.setDateiname(FileName);
 				Path path = Paths.get(FileNamePath);
 				try {
					svsteuer.setDatei(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
				} catch (IOException e1) {
					logger.error("Fehler laden der Datei " + FileName + ": " + e1.getMessage());
				}
 				file = true;
 			}
 		});
	    
	    btnFields[1].addActionListener(new ActionListener() {
 			@Override
 			public void actionPerformed(ActionEvent e) {
 				if (neuBeleg) {
 					
 					svsteuer.setJahr(Einstellungen.getAppSettings().year);
 	 				
 	 				boolean bResult = checkInput();
 	 				if (!bResult) {
 	 					JOptionPane.showMessageDialog(null, "Eingaben unvollständig, Beleg kann nicht gespeichert werden", "Belegeingabe", JOptionPane.INFORMATION_MESSAGE);
 	 					return;
 	 				}
 					
 					svsteuer.setDatum(datePicker[0].getDate());
 					svsteuer.setOrganisation(cmbOrganisation.getSelectedItem().toString());
 					if (cmbBezeichnung.getSelectedIndex() < 2) {
 						svsteuer.setBezeichnung(txtFields[0].getText());
 					} else {
 						svsteuer.setBezeichnung(cmbBezeichnung.getSelectedItem().toString());
 					}
 					svsteuer.setZahllast(parseStringToBigDecimalSafe(txtFields[1].getText(), LocaleFormat.AUTO));
 					svsteuer.setZahlungsziel(datePicker[1].getDate());
 					if (rbZahllast.isSelected()) svsteuer.setStatus(FORDERUNG);
 					if (rbZahlung.isSelected()) svsteuer.setStatus(ZAHLUNG);
 					if (rbDatei.isSelected()) svsteuer.setStatus(DATEI);
 					
 					svsteuerRepository.save(svsteuer);
 				} else {
 					svsteuer = svsteuerRepository.findById(id);
 					svsteuer.setStatus(1);
 					svsteuerRepository.update(svsteuer);
 				}
 				neuBeleg = false;
 				HauptFenster.actScreen();
 			}
 		});
	}
	
	//###################################################################################################################################################
	
	// Hilfsfunktion für Textfelder
    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.LEFT);
        t.setFocusable(true);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
    
    private void txtFieldsFocusable(boolean b) {
    	for (int i = 0; i < this.btnFields.length; i++) {
			this.btnFields[i].setEnabled(b);
		}
    	for (int i = 0; i < this.txtFields.length; i++) {
			this.txtFields[i].setFocusable(b);
		}
    	this.txtFile.setFocusable(false);
    }
    
    private boolean checkInput() {
    	if (datePicker[0].getDate() == null) return false;
    	if (cmbOrganisation.getSelectedIndex() < 1) return false;
    	if (cmbOrganisation.getSelectedIndex() > 0 && cmbBezeichnung.getSelectedIndex() == 0) {
    		for (int i = 0; i < txtFields.length - 1; i++) {
        		if (txtFields[i].getText() == null || txtFields[i].getText().equals("")) return false;
        	}
    	} else {
    		if (cmbBezeichnung.getSelectedIndex() < 1) return false;
    		if (txtFields[1].getText() == null || txtFields[1].getText().equals("")) return false;
    	}
    	if (txtFile.getText() == null || txtFile.getText().equals("")) return false;
    	if (!rbZahllast.isSelected() && !rbZahlung.isSelected() && !rbDatei.isSelected()) return false;
    	if (file == false) return false;
    	return true;
    }
    
	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################
    
    private final ActionListener orgaListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbOrganisation.getSelectedIndex();
            switch (idx) {
            case 2: // Sozialversicherung ausgewählt
            	txtFields[0].setVisible(false);
            	cmbBezeichnung.setVisible(true);
            	cmbBezeichnung.setSelectedIndex(0);
            	break;
            default:
            	txtFields[0].setVisible(true);
            	cmbBezeichnung.setVisible(false);
            	break;
            }
            revalidate();
        	repaint();
        }
    };
    
    private final ActionListener artListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbBezeichnung.getSelectedIndex();
            switch (idx) {
            case 1: // freie Texteingabe ausgewählt
            	txtFields[0].setVisible(true);
            	cmbBezeichnung.setVisible(false);
            	break;
            default:
            	txtFields[0].setVisible(false);
            	cmbBezeichnung.setVisible(true);
            	break;
            }
            revalidate();
        	repaint();
        }
    };
    
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
    
    public void setTxtFields(int id) {
    	if (id > 0) {
    		this.id = id;
		} else {
    		this.datePicker[0].setDate(null);
    		this.datePicker[0].setEnabled(true);
    		for (int i = 0; i < this.txtFields.length; i++) {
				this.txtFields[i].setText("");
			}
    		this.datePicker[1].setDate(null);
    		this.datePicker[1].setEnabled(true);
    		this.cmbOrganisation.setSelectedIndex(0);
    		this.txtFile.setText("");
    		txtFieldsFocusable(true);
    		txtFields[0].setVisible(false);
    		cmbOrganisation.setEnabled(true);
        	cmbBezeichnung.setEnabled(true); cmbBezeichnung.setVisible(true);
        	rbZahllast.setSelected(false); rbZahlung.setSelected(false); rbDatei.setSelected(false);
        	rbDatei.setEnabled(true);
    		svsteuer = new SVSteuer();
    		neuBeleg = true;
			return;
		}
    	
    	svsteuer = svsteuerRepository.findById(id);
    	
    	this.datePicker[0].setDate(svsteuer.getDatum());
    	this.datePicker[0].setEnabled(false);
    	
    	switch (svsteuer.getOrganisation().trim()) {
    		case "Finanzamt Österreich" -> this.cmbOrganisation.setSelectedIndex(1);
    		case "Sozialversicherungsanstalt der Selbstständigen" -> this.cmbOrganisation.setSelectedIndex(2);
    	}
    	this.cmbBezeichnung.setVisible(true);
    	switch (svsteuer.getBezeichnung().trim()) {
    	case "Beitragsvorschreibung Q1" -> this.cmbBezeichnung.setSelectedIndex(2);
    	case "Beitragsvorschreibung Q2" -> this.cmbBezeichnung.setSelectedIndex(3);
    	case "Beitragsvorschreibung Q3" -> this.cmbBezeichnung.setSelectedIndex(4);
    	case "Beitragsvorschreibung Q4" -> this.cmbBezeichnung.setSelectedIndex(5);
    	case "Zahlungserinnerung Beitragsvorschreibung" -> this.cmbBezeichnung.setSelectedIndex(6);
    	default -> cmbBezeichnung.setVisible(false);
    	}
    	if (cmbBezeichnung.isVisible()) {
    		this.txtFields[0].setVisible(false);
    		this.txtFields[0].setText("");
    	} else {
    		this.txtFields[0].setVisible(true);
    		this.txtFields[0].setText(svsteuer.getBezeichnung());
    		this.cmbBezeichnung.setVisible(true);
    	}
    	this.txtFields[0].setText(svsteuer.getBezeichnung());
    	this.txtFields[1].setText(svsteuer.getZahllast().toString());
    	
    	switch(svsteuer.getStatus()) {
    		case FORDERUNG -> rbZahllast.setSelected(true);
    		case ZAHLUNG -> rbZahlung.setSelected(true);
    		case DATEI -> rbDatei.setSelected(true);
    	}

    	this.datePicker[1].setDate(svsteuer.getZahlungsziel());
    	this.datePicker[1].setEnabled(false);
    	
    	this.txtFile.setText(svsteuer.getDateiname());
    	
    	txtFieldsFocusable(false);
    	cmbOrganisation.setEnabled(false);
    	cmbBezeichnung.setEnabled(false);
    	this.btnFields[0].setEnabled(false);
    	neuBeleg = false;
		if (svsteuer.getStatus() == 0) {
			this.btnFields[1].setEnabled(true);
		} else {
			this.btnFields[1].setEnabled(false);
		}
    }

	public void setBtnText(int col, String value) {
		if (value == null) {
			this.btnFields[col].setVisible(false);
			return;
		}
		this.btnFields[col].setText(value);
	}
	
	public void setIcon() {
		try {
			DateianzeigeDialog.setFileIcon(lblFileTyp, txtFile.getText());
			lblFileTyp.setHorizontalAlignment(SwingConstants.CENTER);
		} catch (IOException e) {
			logger.error("setIcon() - " + e);
		}
	}

	public void setFile(boolean file) {
		this.file = false;
		this.file = file;
	}

}
