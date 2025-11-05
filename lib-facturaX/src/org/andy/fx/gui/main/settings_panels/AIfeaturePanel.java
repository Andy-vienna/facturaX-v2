package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.file.Path;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonAI;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.googleServices.CheckEnvAI;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AIfeaturePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(AIfeaturePanel.class);
	
    private JsonAI s = CheckEnvAI.getSettingsAI();
    
    private JCheckBox[] enable = new JCheckBox[3];
    private JTextField[] value = new JTextField[5];
    private JButton[] btnFields = new JButton[1];
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public AIfeaturePanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "AI Feature Einstellungen (wirksam nach Neustart)");
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
    	
    	int x = 10; int y = 20;
    	
    	JLabel[] lbl = new JLabel[8];
    	String[] labels = {"feature enable", "OAuth2 filename", "feature enable", "Gemini api-key", "feature enable", "DocumentAI project-ID",
    			"DocumentAI location", "DocumentAI processor-ID"};
    	String[] chk = {"Google OAuth2 Login", "Gemini Agent", "Google Document AI"};
    	
    	enable[0] = new JCheckBox(chk[0]);
		enable[1] = new JCheckBox(chk[1]);
		enable[2] = new JCheckBox(chk[2]);
		
		value[0] = new JTextField(s.oauth2file);
		value[1] = new JTextField(s.geminiApiKey);
		value[2] = new JTextField(s.documentAIprojectID);
		value[3] = new JTextField(s.documentAIlocation);
		value[4] = new JTextField(s.documentAIprocessorId);
		
    	for (int n = 0; n < labels.length; n++) {
    		lbl[n] = new JLabel(labels[n]);
    		lbl[n].setBounds(x, y + (n * 25), 180, 25);
    		add(lbl[n]);
    	}
    	
    	enable[0].setBounds(x + 180, y + (0 * 25), 800, 25);
    	value[0].setBounds(x + 180, y + (1 * 25), 800, 25);
		enable[1].setBounds(x + 180, y + (2 * 25), 800, 25);
		value[1].setBounds(x + 180, y + (3 * 25), 800, 25);
		enable[2].setBounds(x + 180, y + (4 * 25), 800, 25);
		value[2].setBounds(x + 180, y + (5 * 25), 800, 25);
		value[3].setBounds(x + 180, y + (6 * 25), 800, 25);
		value[4].setBounds(x + 180, y + (7 * 25), 800, 25);
    	add(enable[0]); add(enable[1]); add(enable[2]);
    	add(value[0]); add(value[1]); add(value[2]); add(value[3]); add(value[4]);
    	x = value[value.length - 1].getX() + value[value.length - 1].getWidth();
    	y = value[value.length - 1].getY() + value[value.length - 1].getHeight() + 10;
    	
    	for (int n = 0; n < btnFields.length; n++) {
    		btnFields[n] = createButton("OK", ButtonIcon.OK.icon(), null);
    		btnFields[n].setBounds(x - HauptFenster.getButtonx(), y, HauptFenster.getButtonx(), HauptFenster.getButtony());
    		add(btnFields[n]);
    	}
    	btnFields[0].setEnabled(true);
    	x = x + 10;
    	y = btnFields[btnFields.length - 1].getY() + btnFields[btnFields.length - 1].getHeight() + 20;
    	
    	setPreferredSize(new Dimension(x, y));
    	
    	enable[0].setSelected(s.isOAuth2Login);
    	enable[1].setSelected(s.isGeminiAPI);
    	enable[2].setSelected(s.isDocumentAI);
 
		//###################################################################################################################################################
		// ActionListener
		//###################################################################################################################################################

		btnFields[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Path dir = Path.of(System.getProperty("user.dir"));
				Path fileName;
				
				s.isOAuth2Login = enable[0].isSelected();
				s.oauth2file = value[0].getText().trim();
				
				s.isGeminiAPI = enable[1].isSelected();
				s.geminiApiKey = value[1].getText().trim();
				
				s.isDocumentAI = enable[2].isSelected();
				s.documentAIprojectID = value[2].getText().trim();
				s.documentAIlocation = value[3].getText().trim();
				s.documentAIprocessorId = value[4].getText().trim();
				
				if (StartUp.getFileAI() == null) {
					fileName = dir.resolve("secrets\\settingsAI.json");
				} else {
					fileName = StartUp.getFileAI();
				}
				try {
					JsonUtil.saveAI(fileName, s);
				} catch (IOException e1) {
					logger.error("error writing db settings: " + e1.getMessage());
				}
			}
		});
	}
}
