package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.code.misc.Password.checkComplexity;
import static org.andy.fx.code.misc.Password.hashPwd;
import static org.andy.fx.code.misc.Password.verifyPwd;
import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityMaster.User;
import org.andy.fx.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.table_panels.TabMask;

public class BenutzerPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private final boolean[] selUser = {true,true,true,true,true,false,false,false,false,false,false,false};
    private final boolean[] selSuser = {true,true,true,true,true,true,true,true,true,false,false,false};
    private final boolean[] selFuser = {true,false,false,false,false,true,true,true,true,false,false,false};
    private final boolean[] selAdmin = {false,false,false,false,false,false,false,false,false,true,true,true};
    private final boolean[] selDefault = {false,false,false,false,false,false,false,false,false,false,false,false};
    
    private static JButton btnShowPwd = null, btnPwdOK = null;
    
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
    private JComboBox<String> cmbSelect;
    private JTextField userExist = new JTextField();
    private JPasswordField[] passFields = new JPasswordField[3];
    private JTextField eMail = new JTextField();
    private JComboBox<String> cmbRoles;
    private JCheckBox[] chkConfig = new JCheckBox[12];
    
    private UserRepository userRepository = new UserRepository();
	private List<User> userListe = new ArrayList<>();
	private User storedUser = new User();
	private User leer = new User();
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public BenutzerPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Benutzerverwaltung");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        leer.setId(""); leer.setHash(""); leer.setRoles(""); // Leeren Listeneintrag erzeugen
        
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 20; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
    	String[] roles = {"", "user", "superuser", "financialuser", "admin"};
    	String[] label = {"vorh. User", "Username", "Kennwort alt", "Kennwort neu", "Kennw. wiederh.", "E-Mail", "Benutzerrolle"};
    	String[] config = { "Reisespesen", "Angebot", "Rechnung", "Bestellung", "Lieferschein", "Einkauf", "Betriebsausgaben",
    			"SV und Steuer", "Jahresergenis", "Einstellungen", "DB Migration", "Versuche" };
    	
    	JLabel[] lblFields = new JLabel[label.length];
    	for (int i= 0; i < label.length; i++) {
			lblFields[i] = new JLabel(label[i]);
			lblFields[i].setBounds(x, y + (i * 25), 90, 25);
			add(lblFields[i]);
		}
		x = 100;
    	
		userListe.clear();
		userListe.add(leer);
		userListe.addAll(userRepository.findAll());
        String[] userTexte = userListe.stream()
                .map(User::getId)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(userTexte);
		cmbSelect.setBounds(x, y, 220, 25);
		cmbSelect.addActionListener(cmbListener);
		add(cmbSelect);
		y = 45;
		
		userExist.setBounds(x, y, 220, 25);
		add(userExist);
		y = 70;
		
		for (int i = 0; i< passFields.length; i++) {
			passFields[i] = new JPasswordField();
			passFields[i].setBounds(x, y + (i * 25), 220, 25);
			passFields[i].setText("");
			passFields[i].setEchoChar('*');
			add(passFields[i]);
		}
		passFields[0].setEnabled(false);
		passFields[2].addKeyListener(passListener);
		y = 145;
		
		eMail.setBounds(x, y, 220, 25);
		add(eMail);
		y = 170;

		cmbRoles = new JComboBox<>(roles);
		cmbRoles.setBounds(x, y, 220, 25);
		cmbRoles.addActionListener(cmbRolesListener);
		add(cmbRoles);
		x = 370; y = 20;
		
		for (int i = 0; i < chkConfig.length; i++) {
			chkConfig[i] = new JCheckBox(config[i]);
			chkConfig[i].setBounds(x, y + (i * 25), 120, 25);
			chkConfig[i].setEnabled(false);
			add(chkConfig[i]);
		}
		y = chkConfig[chkConfig.length - 1].getY() + chkConfig[chkConfig.length - 1].getHeight();

		btnShowPwd = createButton("...", null, null);
		btnPwdOK = createButton(null, ButtonIcon.OK.icon(), null);
		
		btnShowPwd.addActionListener(btnShow);
		btnPwdOK.addActionListener(btnOK);
		btnShowPwd.setEnabled(true);
		btnPwdOK.setEnabled(true);
		btnShowPwd.setBounds(320, 70, 25, 75);
		btnPwdOK.setBounds(580, 195, btnWidth, btnHeight);
		add(btnShowPwd);
		add(btnPwdOK);
		
		setPreferredSize(new Dimension(720, y + 20));
	}
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild(int x, int y) {
        userListe.clear();
        userListe.add(leer);
        userListe.addAll(userRepository.findAll());
        String[] userTexte = userListe.stream()
                .map(User::getId)
                .toArray(String[]::new);
        cmbSelect.setModel(new DefaultComboBoxModel<>(userTexte));
        cmbSelect.setSelectedIndex(0);
        userExist.setText("");
		passFields[0].setText("");
		passFields[1].setText("");
		passFields[2].setText("");
		eMail.setText("");
		cmbRoles.setSelectedIndex(0);
		for (int i = 0; i < chkConfig.length; i++) {
			chkConfig[i].setSelected(false);
			chkConfig[i].setEnabled(false);
		}
    }
    
    private int calcValueConfig() {
    	int val = 0;
    	for (int x = 0; x < chkConfig.length; x++) {
    		if (chkConfig[x].isSelected()) {
    			val = val + (1 << x);
    		}
    	}
    	return val;
    }
    
    private void isSelectable(String role) {
    	boolean[] cbRole = null;
    	switch(role) {
    		case "user" -> cbRole = selUser;
    		case "superuser" -> cbRole = selSuser;
    		case "financialuser" -> cbRole = selFuser;
    		case "admin" -> cbRole = selAdmin;
    		default -> cbRole = selDefault;
    	}
    	for (int x = 0; x < chkConfig.length; x++) {
    		chkConfig[x].setEnabled(cbRole[x]);
    		if (!chkConfig[x].isEnabled()) chkConfig[x].setSelected(false);
    	}
    }
    
	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener cmbListener = new ActionListener() {
    	@Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            User user = userListe.get(idx);
            if (idx == 0) {
                userExist.setText("");
                for (int i = 0; i < passFields.length; i++) {
                	passFields[i].setText("");
                }
                userExist.setEnabled(true);
                passFields[0].setEnabled(false);
                cmbRoles.setSelectedIndex(0);
            } else {
                userExist.setText(user.getId());
                eMail.setText(user.getEmail());
                switch(user.getRoles().trim()) {
                	case "user" -> cmbRoles.setSelectedIndex(1);
                	case "superuser" -> cmbRoles.setSelectedIndex(2);
                	case "financialuser" -> cmbRoles.setSelectedIndex(3);
                	case "admin" -> cmbRoles.setSelectedIndex(4);
                	default -> cmbRoles.setSelectedIndex(0);
                }
                userExist.setEnabled(false);
                passFields[0].setEnabled(true);
                
                for (int x = 0; x < chkConfig.length; x++) {
        			chkConfig[x].setSelected(false);
        			chkConfig[x].setEnabled(true);
        		}
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.TRAVEL)) chkConfig[0].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.OFFER)) chkConfig[1].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.INVOICE)) chkConfig[2].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.ORDER)) chkConfig[3].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.DELIVERY)) chkConfig[4].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.PURCHASE)) chkConfig[5].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.EXPENSES)) chkConfig[6].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.TAX)) chkConfig[7].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.RESULT)) chkConfig[8].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.SETTINGS)) chkConfig[9].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.MIGRATION)) chkConfig[10].setSelected(true);
                if (TabMask.visible(user.getTabConfig(), TabMask.Tab.TRIALS)) chkConfig[11].setSelected(true);
            }
            String tmp = cmbRoles.getSelectedItem().toString();
            isSelectable(tmp);
        }
    };
    
    private final ActionListener cmbRolesListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			int idx = cmbRoles.getSelectedIndex();
			if (idx == 0) {
				for (int x = 0; x < chkConfig.length; x++) {
        			chkConfig[x].setSelected(false);
        			chkConfig[x].setEnabled(false);
        		}
			} else {
				String tmp = cmbRoles.getSelectedItem().toString();
	            isSelectable(tmp);
			}
		}
    };
    
    private final KeyListener passListener = new KeyListener() {
		@Override
		public void keyReleased(KeyEvent e) {
			if (Arrays.equals(passFields[1].getPassword(), passFields[2].getPassword())) {
				passFields[1].setBackground(Color.WHITE);
				passFields[2].setBackground(Color.WHITE);
			} else {
				passFields[1].setBackground(Color.PINK);
				passFields[2].setBackground(Color.PINK);
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
		}
	};
	
	private final ActionListener btnShow = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (passFields[0].getEchoChar() == '*') {
				passFields[0].setEchoChar((char) 0);
			} else {
				passFields[0].setEchoChar('*');
			}
			if (passFields[1].getEchoChar() == '*') {
				passFields[1].setEchoChar((char) 0);
			} else {
				passFields[1].setEchoChar('*');
			}
			if (passFields[2].getEchoChar() == '*') {
				passFields[2].setEchoChar((char) 0);
			} else {
				passFields[2].setEchoChar('*');
			}
		}
	};
	
	private final ActionListener btnOK = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {

			boolean bCheckComplexity = false, bCheckUser = false;

			int value = calcValueConfig();
			bCheckComplexity = checkComplexity(passFields[1].getPassword());
			
			for(int x = 0; x < userListe.size(); x++) {
				storedUser = userListe.get(x);
				if(storedUser.getId().trim().equals(userExist.getText())) {
					bCheckUser = true; // user exists
					break;
				}
			}

			if(!bCheckUser) { //neuer User
				if(!bCheckComplexity) {
					passFields[1].setText("");
					passFields[2].setText("");
					JOptionPane.showMessageDialog(null, "<html>Das Passwort entspricht nicht den Anforderungen ...<br>[>8 Zeichen, a-z, A-Z, 0-9, @#$%^&+=-_!?.]</html>",
							"Userverwaltung", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(Arrays.equals(passFields[1].getPassword(), passFields[2].getPassword())) {
					char[] passwordChars = passFields[1].getPassword();
					String newPass = hashPwd(passwordChars);
					
					String userRole = cmbRoles.getSelectedItem().toString();
					if(userRole.equals(" ")) {
						JOptionPane.showMessageDialog(null, "Bitte Benutzerrolle auswählen", "Userverwaltung", JOptionPane.ERROR_MESSAGE);
						return;
					}
					User newUser = new User();
					newUser.setEmail(eMail.getText().trim());
					newUser.setId(userExist.getText().trim());
					newUser.setHash(newPass);
					newUser.setRoles(userRole);
					newUser.setTabConfig(value);
					userRepository.insert(newUser);
					
					Arrays.fill(passwordChars, '\0');
					newPass = null;
				} else {
					JOptionPane.showMessageDialog(null, "Passwörter nicht gleich", "Userverwaltung", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}

			if(bCheckUser) { //bekannter User
				if (passFields[0].getPassword().length == 0 && passFields[1].getPassword().length == 0 && passFields[2].getPassword().length == 0) {
					storedUser.setEmail(eMail.getText());
					storedUser.setRoles(cmbRoles.getSelectedItem().toString());
					storedUser.setTabConfig(value);
					userRepository.update(storedUser);
					rebuild(100, 20);
					return;
				}
				if(!bCheckComplexity) {
					passFields[1].setText("");
					passFields[2].setText("");
					JOptionPane.showMessageDialog(null, "<html>Das Passwort entspricht nicht den Anforderungen ...<br>[>8 Zeichen, a-z, A-Z, 0-9, @#$%^&+=-_!?.]</html>",
							"Usermanagement", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if(passFields[0].getPassword().length != 0 && Arrays.equals(passFields[1].getPassword(), passFields[2].getPassword())) {
					char[] passwordChars = passFields[0].getPassword();
					boolean bCheckOld = verifyPwd(passwordChars, storedUser.getHash().trim());
					Arrays.fill(passwordChars, '\0');
					if(bCheckOld) {
						char[] newPasswordChars = passFields[1].getPassword();
						String changePass = hashPwd(newPasswordChars);

						storedUser.setHash(changePass);
						userRepository.update(storedUser);
						
						Arrays.fill(newPasswordChars, '\0');
						changePass = null;
					}else {
						JOptionPane.showMessageDialog(null, "altes Passwort nicht OK", "Usermanagement", JOptionPane.ERROR_MESSAGE);
						return;
					}
				} else {
					JOptionPane.showMessageDialog(null, "Passwörter nicht gleich", "Usermanagement", JOptionPane.ERROR_MESSAGE);
					return;
				}
			}
			rebuild(100, 20);
		}
	};
}
