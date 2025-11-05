package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonDb;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatenbankPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(DatenbankPanel.class);
	
    private JsonDb s = Einstellungen.getDbSettings();
    
    private static JButton btnDBEdit = null, btnDBOK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public DatenbankPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Datenbank Einstellungen (wirksam nach Neustart)");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	
    	JLabel[] lbl = new JLabel[7];
    	String[] labels = {"database type", "computer", "port", "database for MasterData", "database for ProductiveData", "user", "pass"};
    	String[] cmb = {"Microsoft SQL", "PostgreSQL"};
    	
    	for (int n = 0; n < labels.length; n++) {
    		lbl[n] = new JLabel(labels[n]);
    		lbl[n].setBounds(10, 20 + (n * 25), 180, 25);
    		add(lbl[n]);
    	}
    	
    	JComboBox<String> cmbDBtyp = new JComboBox<>(cmb);
    	cmbDBtyp.setBounds(190, 20, 215, 25);
    	add(cmbDBtyp);
    	
		JTextField textDBcomputer = new JTextField(s.dbHost);
		JTextField textDBport = new JTextField(s.dbPort);
		JTextField textDBnameSource = new JTextField(s.dbMaster);
		JTextField textDBnameDest = new JTextField(s.dbData);
		JTextField textDBuser = new JTextField(s.dbUser);
		JTextField textDBpass = new JTextField(s.dbPass);
		JCheckBox chkEncryption = new JCheckBox("encrypt database");
		JCheckBox chkServerCert = new JCheckBox("trust server certificate");

		textDBcomputer.setBounds(190, 45, 215, 25);
		textDBport.setBounds(190, 70, 215, 25);
		textDBnameSource.setBounds(190, 95, 215, 25);
		textDBnameDest.setBounds(190, 120, 215, 25);
		textDBuser.setBounds(190, 145, 215, 25);
		textDBpass.setBounds(190, 170, 215, 25);
		chkEncryption.setBounds(190, 195, 155, 25);
		chkServerCert.setBounds(190, 220, 155, 25);

		btnDBEdit = createButton(null, ButtonIcon.EDIT.icon(), null);
		btnDBOK = createButton(null, ButtonIcon.OK.icon(), null);
		
		btnDBEdit.setEnabled(true);
		btnDBEdit.setBounds(410, 20, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnDBOK.setBounds(410, 70, HauptFenster.getButtonx(), HauptFenster.getButtony());

		add(textDBcomputer);
		add(textDBport);
		add(textDBnameSource);
		add(textDBnameDest);
		add(textDBuser);
		add(textDBpass);
		add(chkEncryption);
		add(chkServerCert);
		
		add(btnDBEdit);
		add(btnDBOK);
		
		switch(s.dbType) {
			case "mssql" -> {cmbDBtyp.setSelectedIndex(0); chkEncryption.setVisible(true); chkServerCert.setVisible(true);}
			case "postgre" -> {cmbDBtyp.setSelectedIndex(1); chkEncryption.setVisible(false); chkServerCert.setVisible(false);}
			default -> cmbDBtyp.setSelectedIndex(-1);
		}

		cmbDBtyp.setEnabled(false);
		textDBcomputer.setEnabled(false);
		textDBport.setEnabled(false);
		textDBnameSource.setEnabled(false);
		textDBnameDest.setEnabled(false);
		textDBuser.setEnabled(false);
		textDBpass.setEnabled(false);
		chkEncryption.setEnabled(false);
		chkServerCert.setEnabled(false);
		btnDBOK.setEnabled(false);

		chkEncryption.setSelected(s.dbEncrypt);
		chkServerCert.setSelected(s.dbCert);
		
		//###################################################################################################################################################
		// ActionListener
		//###################################################################################################################################################

		cmbDBtyp.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {
	        	int idx = cmbDBtyp.getSelectedIndex();
	        	if (idx > 0) {
	        		chkEncryption.setVisible(false);
	        		chkServerCert.setVisible(false);
	        	} else {
	        		chkEncryption.setVisible(true);
	        		chkServerCert.setVisible(true);
	        	}
	        }
		});
		
		btnDBEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cmbDBtyp.setEnabled(true);
				textDBcomputer.setEnabled(true);
				textDBport.setEnabled(true);
				textDBnameSource.setEnabled(true);
				textDBnameDest.setEnabled(true);
				textDBuser.setEnabled(true);
				textDBpass.setEnabled(true);
				chkEncryption.setEnabled(true);
				chkServerCert.setEnabled(true);
				btnDBEdit.setEnabled(false);
				btnDBOK.setEnabled(true);
			}
		});

		btnDBOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				switch(cmbDBtyp.getSelectedIndex()) {
					case 0 -> s.dbType = "mssql";
					case 1 -> s.dbType = "postgre";
				}
				
				s.dbHost = textDBcomputer.getText();
				s.dbPort = textDBport.getText();
				s.dbMaster = textDBnameSource.getText();
				s.dbData = textDBnameDest.getText();
				s.dbUser = textDBuser.getText();
				s.dbPass = textDBpass.getText();
				
				s.dbEncrypt = chkEncryption.isSelected();
				s.dbCert = chkServerCert.isSelected();

				cmbDBtyp.setEnabled(false);
				textDBcomputer.setEnabled(false);
				textDBport.setEnabled(false);
				textDBnameSource.setEnabled(false);
				textDBnameDest.setEnabled(false);
				textDBuser.setEnabled(false);
				textDBpass.setEnabled(false);
				chkEncryption.setEnabled(false);
				chkServerCert.setEnabled(false);
				btnDBEdit.setEnabled(true);
				btnDBOK.setEnabled(false);
				
				try {
					JsonUtil.saveDB(StartUp.getFileDB(), s);
				} catch (IOException e1) {
					logger.error("error writing db settings: " + e1.getMessage());
				}
			}
		});
		
		setPreferredSize(new Dimension(550, 265));
	}
}
