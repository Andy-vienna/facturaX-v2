package org.andy.fx.gui.main.settings_panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonApp;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.FileSelect;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PfadPanel extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(PfadPanel.class);
	
	JPanel panel = new JPanel();
	
	private JsonApp s = Einstellungen.getAppSettings();
	
	// Titel definieren
	String titel = "Pfadverwaltung";

	// Schrift konfigurieren
	Font font = new Font("Tahoma", Font.BOLD, 11);
	Color titleColor = Color.BLUE; // oder z. B. new Color(30, 60, 150);
	
	private JTextField[] txtFields = new JTextField[13];
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public PfadPanel() {
		
        setLayout(null);
        
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), titel);
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT); // optional: Ausrichtung links
        border.setTitlePosition(TitledBorder.TOP);       // optional: Position oben

        setBorder(border);
        
        buildPanel();
    }

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void buildPanel() {
		
		String labels[] = {
				"Angebot Vorlage (Excel-Vorlage *.xlsx)",
				"Angebotsrevision Vorlage (Excel-Vorlage *.xlsx)",
				"Leistungsbeschreibung Vorlage (Word-Vorlage *.docx)",
				"Auftragsbestätigung Vorlage (Excel-Vorlage *.xlsx)",
				"Rechnung Vorlage (Excel-Vorlage *.xlsx)",
				"Zahlungserinnerung Vorlage (Excel-Vorlage *.xlsx)",
				"Mahnung Vorlage (Excel-Vorlage *.xlsx",
				"Bestellung Vorlage (Excel-Vorlage *.xlsx)",
				"Lieferschein Vorlage (Excel-Vorlage *.xlsx)",
				"§109a (E/A-Rechnung) Vorlage (Excel-Vorlage *.xlsx",
				"Spesenabrechnung Vorlage (Excel-Vorlage *.xlsx)",
				"Arbeitszeit Vorlage (Excel-Vorlage *.xlsx)",
				"Arbeitsverzeichnis",
				};
		
		// Label Arrays
	    JLabel[] lblFields = new JLabel[labels.length];
		
	    // Zeilenlabels
	    for (int r = 0; r < labels.length; r++) {
	    	lblFields[r] = new JLabel(labels[r]);
	    	lblFields[r].setBounds(10, 20 + r * 25, 300, 25);
	    	add(lblFields[r]);
	    }
		
	    // Textfelder
	    for (int r = 0; r < txtFields.length; r++) {
	    	final int index = r;
	    	txtFields[r] = makeField(310, 20 + r * 25, 800, 25, false, null);
	    	txtFields[r].setText(getters[index].apply(null));
	        txtFields[r].addMouseListener(new MouseAdapter() {
	            @Override
	            public void mouseClicked(MouseEvent e) {
	            	String chosenPath = null;
	                String currentPath = txtFields[index].getText();
	                String defaultPath = currentPath.isEmpty() ? "C:\\" : getters[index].apply(null);

	                if (index < txtFields.length -1) {
	                	chosenPath = FileSelect.chooseFile(defaultPath);
	                } else {
	                	chosenPath = FileSelect.choosePath(defaultPath);
	                }
	                if (chosenPath != null) {
	                	
	                	s.tplOffer = txtFields[0].getText();
	                	s.tplOfferRev = txtFields[1].getText();
	                	s.tplDescription = txtFields[2].getText();
	                	s.tplOfferConfirm = txtFields[3].getText();
	                	s.tplBill = txtFields[4].getText();
	                	s.tplReminder = txtFields[5].getText();
	                	s.tplStrictReminder = txtFields[6].getText();
	                	s.tplOrder = txtFields[7].getText();
	                	s.tplDeliveryNote = txtFields[8].getText();
	                	s.tplP109a = txtFields[9].getText();
	                	s.tplSpesen = txtFields[10].getText();
	                	s.tplArbeitszeit = txtFields[11].getText();
	                	s.work = txtFields[12].getText();
	                	
	                	try {
							JsonUtil.saveAPP(StartUp.getFileApp(), s);
						} catch (IOException e1) {
							logger.error("error writing app settings: " + e1.getMessage());
						}
	                	
	                    txtFields[index].setText(chosenPath);
	                }
	            }
	        });
	    	add(txtFields[r]);
	    }
		
	    setPreferredSize(new Dimension(1120, 20 + labels.length * 25 + 20));
		
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
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
    
    // Get-Methoden (als Function<Void, String>)
    @SuppressWarnings("unchecked")
    Function<Void, String>[] getters = new Function[] {
        _ -> Einstellungen.getAppSettings().tplOffer,
        _ -> Einstellungen.getAppSettings().tplOfferRev,
        _ -> Einstellungen.getAppSettings().tplDescription,
        _ -> Einstellungen.getAppSettings().tplOfferConfirm,
        _ -> Einstellungen.getAppSettings().tplBill,
        _ -> Einstellungen.getAppSettings().tplReminder,
        _ -> Einstellungen.getAppSettings().tplStrictReminder,
        _ -> Einstellungen.getAppSettings().tplOrder,
        _ -> Einstellungen.getAppSettings().tplDeliveryNote,
        _ -> Einstellungen.getAppSettings().tplP109a,
        _ -> Einstellungen.getAppSettings().tplSpesen,
        _ -> Einstellungen.getAppSettings().tplArbeitszeit,
        _ -> Einstellungen.getAppSettings().work
    };

}
