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

import org.andy.fx.code.dataStructure.entityJSON.JsonApp;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class QrCodePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(QrCodePanel.class);
	
	private JsonApp s = Einstellungen.getAppSettings();
	
    private static JButton btnQREdit = null, btnQROK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public QrCodePanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "SEPA OR-Code Einstellungen");
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
    	String[] sQRschema = s.qrScheme.split("/"); // Properties Eintrag zerlegen

		if (sQRschema.length != 10) {
			sQRschema = new String[] { "BCD","002","1","SCT","{BIC}","{KI}","{IBAN}","EUR{SUM}","","{RENR}" };
		}

		JLabel lbl01 = new JLabel("Servicekennung");
		JLabel lbl02 = new JLabel("Version");
		JLabel lbl03 = new JLabel("Kodierung");
		JLabel lbl04 = new JLabel("Funktion");
		JLabel lbl05 = new JLabel("BIC");
		JLabel lbl06 = new JLabel("Empfänger");
		JLabel lbl07 = new JLabel("IBAN");
		JLabel lbl08 = new JLabel("Währung | Betrag");
		JLabel lbl09 = new JLabel("Zweck");
		JLabel lbl10 = new JLabel("Referenz");
		JLabel lbl11 = new JLabel("Text");
		JLabel lbl12 = new JLabel("Anzeige");

		JTextField txtQRbcd = new JTextField(sQRschema[0]);
		JTextField textQRversion = new JTextField(sQRschema[1]);
		JTextField textQRcode = new JTextField(sQRschema[2]);
		JTextField textQRsct = new JTextField(sQRschema[3]);
		JTextField txtQRbic = new JTextField(sQRschema[4]);
		JTextField txtQRki = new JTextField(sQRschema[5]);
		JTextField txtQRiban = new JTextField(sQRschema[6]);
		JTextField txtQReursum = new JTextField(sQRschema[7]);
		JTextField textQRzweck = new JTextField(sQRschema[8]);
		JTextField textQRref = new JTextField(sQRschema[9]);
		JTextField textQRtext = new JTextField();
		JTextField textQRanzeige = new JTextField();

		lbl01.setBounds(10, 20, 90, 25);
		lbl02.setBounds(10, 45, 90, 25);
		lbl03.setBounds(10, 70, 90, 25);
		lbl04.setBounds(10, 95, 90, 25);
		lbl05.setBounds(10, 120, 90, 25);
		lbl06.setBounds(10, 155, 90, 25);
		lbl07.setBounds(10, 170, 90, 25);
		lbl08.setBounds(10, 195, 90, 25);
		lbl09.setBounds(10, 220, 90, 25);
		lbl10.setBounds(10, 245, 90, 25);
		lbl11.setBounds(10, 270, 90, 25);
		lbl12.setBounds(10, 295, 90, 25);

		txtQRbcd.setBounds(110, 20, 140, 25);
		textQRversion.setBounds(110, 45, 140, 25);
		textQRcode.setBounds(110, 70, 140, 25);
		textQRsct.setBounds(110, 95, 140, 25);
		txtQRbic.setBounds(110, 120, 140, 25);
		txtQRki.setBounds(110, 145, 140, 25);
		txtQRiban.setBounds(110, 170, 140, 25);
		txtQReursum.setBounds(110, 195, 140, 25);
		textQRzweck.setBounds(110, 220, 140, 25);
		textQRref.setBounds(110, 245, 140, 25);
		textQRtext.setBounds(110, 270, 140, 25);
		textQRanzeige.setBounds(110, 295, 140, 25);

		btnQREdit = createButton(null, ButtonIcon.EDIT.icon(), null);
		btnQROK = createButton(null, ButtonIcon.OK.icon(), null);
		
		btnQREdit.setEnabled(true);
		btnQREdit.setBounds(260, 220, HauptFenster.getButtonx(), HauptFenster.getButtony());
		btnQROK.setBounds(260, 270, HauptFenster.getButtonx(), HauptFenster.getButtony());

		add(lbl01);
		add(lbl02);
		add(lbl03);
		add(lbl04);
		add(lbl05);
		add(lbl06);
		add(lbl07);
		add(lbl08);
		add(lbl09);
		add(lbl10);
		add(lbl11);
		add(lbl12);

		add(txtQRbcd);
		add(textQRversion);
		add(textQRcode);
		add(textQRsct);
		add(txtQRbic);
		add(txtQRki);
		add(txtQRiban);
		add(txtQReursum);
		add(textQRzweck);
		add(textQRref);
		add(textQRtext);
		add(textQRanzeige);

		add(btnQREdit);
		add(btnQROK);

		txtQRbcd.setEnabled(false);
		textQRversion.setEnabled(false);
		textQRcode.setEnabled(false);
		textQRsct.setEnabled(false);
		txtQRbic.setEnabled(false);
		txtQRki.setEnabled(false);
		txtQRiban.setEnabled(false);
		txtQReursum.setEnabled(false);
		textQRzweck.setEditable(false);
		textQRzweck.setEnabled(false);
		textQRref.setEnabled(false);
		textQRtext.setEditable(false);
		textQRtext.setEnabled(false);
		textQRanzeige.setEditable(false);
		textQRanzeige.setEnabled(false);

		btnQREdit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				txtQRbcd.setEnabled(true);
				textQRversion.setEnabled(true);
				textQRcode.setEnabled(true);
				textQRsct.setEnabled(true);
				txtQRbic.setEnabled(true);
				txtQRki.setEnabled(true);
				txtQRiban.setEnabled(true);
				txtQReursum.setEnabled(true);
				textQRzweck.setEnabled(true);
				textQRref.setEnabled(true);
				textQRtext.setEnabled(true);
				textQRanzeige.setEnabled(true);
				btnQROK.setEnabled(true);
				btnQREdit.setEnabled(false);
			}
		});

		btnQROK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				String schema = txtQRbcd.getText() + "/" + textQRversion.getText() + "/" + textQRcode.getText()
				+ "/" + textQRsct.getText() + "/" + txtQRbic.getText() + "/" + txtQRki.getText() + "/"
				+ txtQRiban.getText() + "/" + txtQReursum.getText() + "/" + textQRzweck.getText() + "/"
				+ textQRref.getText() + "/" + textQRtext.getText() + "/" + textQRanzeige.getText();
				
	        	s.qrScheme = schema;
	        	try {
					JsonUtil.saveAPP(StartUp.getFileApp(), s);
				} catch (IOException e1) {
					logger.error("error writing app settings: " + e1.getMessage());
				}

				txtQRbcd.setEnabled(false);
				textQRversion.setEnabled(false);
				textQRcode.setEnabled(false);
				textQRsct.setEnabled(false);
				txtQRbic.setEnabled(false);
				txtQRki.setEnabled(false);
				txtQRiban.setEnabled(false);
				txtQReursum.setEnabled(false);
				textQRzweck.setEnabled(false);
				textQRref.setEnabled(false);
				textQRtext.setEnabled(false);
				textQRanzeige.setEnabled(false);
				btnQROK.setEnabled(false);
				btnQREdit.setEnabled(true);

			}
		});
		
		setPreferredSize(new Dimension(400, 340));
	}
}
