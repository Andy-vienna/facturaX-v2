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
    	
    	JLabel[] lbl = new JLabel[6];
    	String[] labels = {"computer", "port", "database for MasterData", "database for ProductiveData", "user", "pass"};
    	
    	for (int n = 0; n < labels.length; n++) {
    		lbl[n] = new JLabel(labels[n]);
    		lbl[n].setBounds(10, 20 + (n * 25), 180, 25);
    		add(lbl[n]);
    	}
    	
		JTextField textDBcomputer = new JTextField(s.dbHost);
		JTextField textDBport = new JTextField(s.dbPort);
		JTextField textDBnameSource = new JTextField(s.dbMaster);
		JTextField textDBnameDest = new JTextField(s.dbData);
		JTextField textDBuser = new JTextField(s.dbUser);
		JTextField textDBpass = new JTextField(s.dbPass);

		textDBcomputer.setBounds(190, 20, 215, 25);
		textDBport.setBounds(190, 45, 215, 25);
		textDBnameSource.setBounds(190, 70, 215, 25);
		textDBnameDest.setBounds(190, 95, 215, 25);
		textDBuser.setBounds(190, 120, 215, 25);
		textDBpass.setBounds(190, 145, 215, 25);

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
		
		add(btnDBEdit);
		add(btnDBOK);

		textDBcomputer.setEnabled(false);
		textDBport.setEnabled(false);
		textDBnameSource.setEnabled(false);
		textDBnameDest.setEnabled(false);
		textDBuser.setEnabled(false);
		textDBpass.setEnabled(false);
		btnDBOK.setEnabled(false);
		
		//###################################################################################################################################################
		// ActionListener
		//###################################################################################################################################################
		
		btnDBEdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				textDBcomputer.setEnabled(true);
				textDBport.setEnabled(true);
				textDBnameSource.setEnabled(true);
				textDBnameDest.setEnabled(true);
				textDBuser.setEnabled(true);
				textDBpass.setEnabled(true);
				btnDBEdit.setEnabled(false);
				btnDBOK.setEnabled(true);
			}
		});

		btnDBOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				s.dbHost = textDBcomputer.getText();
				s.dbPort = textDBport.getText();
				s.dbMaster = textDBnameSource.getText();
				s.dbData = textDBnameDest.getText();
				s.dbUser = textDBuser.getText();
				s.dbPass = textDBpass.getText();

				textDBcomputer.setEnabled(false);
				textDBport.setEnabled(false);
				textDBnameSource.setEnabled(false);
				textDBnameDest.setEnabled(false);
				textDBuser.setEnabled(false);
				textDBpass.setEnabled(false);
				btnDBEdit.setEnabled(true);
				btnDBOK.setEnabled(false);
				
				try {
					JsonUtil.saveDB(StartUp.getFileDB(), s);
				} catch (IOException e1) {
					logger.error("error writing db settings: " + e1.getMessage());
				}
			}
		});
		
		setPreferredSize(new Dimension(550, 190));
	}
}
