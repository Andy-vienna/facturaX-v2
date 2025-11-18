package org.andy.gui.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(SettingsDialog.class);
	
	private final String hinweis = "<html>Zur Zertifikatserzeugung müssen folgende Informationen eingegeben werden:<br>"
			+ "<b>CommonName</b> - Vollständiger Name der Entität - dies können sein:<br>"
			+ "der FQDN des Servers oder der vollständige Name bei Personen,<br>"
			+ "<b>OrganizationalUnit</b> - die Organisationseinheit (IT, Software, etc.)<br>"
			+ "<b>Organization</b> - der offizielle Name der Organisation<br>"
			+ "<b>Locality</b> - Stadt/Gemeinde<br>"
			+ "<b>State or Province</b> - Bundesland, Bezirk<br>"
			+ "<b>Country</b> - Ländercode, ISO-3166-Alpha-2 (AT für Österreich)</html>";
	private final String[] lblTxt = { "CommonName (CN):", "OrganizationalUnit (OU):", "Organization (O):", "Locality (L):",
									  "State or Province (ST):", "Country (C):", "IP-Adresse:" };
	
	private JLabel text1 = new JLabel("Datei keystore.jks ist vorhanden ...");
	private JLabel text2 = new JLabel("Datei cert.crt ist in downloads vorhanden ...");
	private final JTextField[] txtField = new JTextField[7];
	private final JButton[] btn = new JButton[3];
	
	Path keystorePath = Path.of("keystore.jks");
	Path certPath = Path.of("downloads\\cert.crt");
	
	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

    public SettingsDialog() {
        super(null, "Einstellungen", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setContentPane(buildContent());
        pack();
        setLocationRelativeTo(null);
        getRootPane().setDefaultButton(btn[2]);
        bindEscToClose();
        setIconImage(loadImage("/org/resources/icons/icon.png", 32, 32));
    }
    
    // Convenience
    public static void show(Window owner) {
        new SettingsDialog().setVisible(true);
    }

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

    private JPanel buildContent() {
        JPanel root = new JPanel();
        root.setLayout(null);
        root.setPreferredSize(new Dimension(500, 440));
        root.setBorder(new EmptyBorder(8, 5, 8, 5));
        
        JLabel remark = new JLabel(hinweis);
        remark.setBounds(10, 10, 480, 125);
        root.add(remark);
        
        JLabel[] lbl = new JLabel[7];
        for (int i = 0; i < lbl.length; i++) {
        	lbl[i] = new JLabel(lblTxt[i]); txtField[i] = new JTextField();
        	lbl[i].setBounds(10, 150 + (i * 25), 150, 25); txtField[i].setBounds(180, 150 + (i * 25), 150, 25);
        	root.add(lbl[i]); root.add(txtField[i]);
        }
        
        text1.setBounds(10, 325, 400, 25); text1.setVisible(false);
        text1.setForeground(Color.RED); root.add(text1);

        text2.setBounds(10, 350, 400, 25); text2.setVisible(false);
        text2.setForeground(Color.RED); root.add(text2);
        
        btn[0] = new JButton("<html>Zertifikat<br>erzeugen</html>");
        btn[0].setBounds(10, 380, 100, 50);
        btn[0].addActionListener(_ -> doCreateCert());
        root.add(btn[0]);
        
        btn[1] = new JButton("<html>Zertifikat<br>export.</html>");
        btn[1].setBounds(120, 380, 100, 50);
        btn[1].addActionListener(_ -> doExportCert());
        root.add(btn[1]);
        
        btn[2] = new JButton("Schließen");
        btn[2].setBounds(380, 380, 100, 50);
        btn[2].addActionListener(_ -> dispose());
        root.add(btn[2]);
        
        if (Files.exists(keystorePath)) {
        	doStateTxtField(false);
        	btn[0].setEnabled(false);
        	text1.setVisible(true);
        }
        if (Files.exists(certPath)) {
        	btn[1].setEnabled(false);
        	text2.setVisible(true);
        }

        return root;
    }
    
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
    
    private void doCreateCert() {
    	
    	for (int i = 0; i < txtField.length; i++) {
    		if (txtField[i].getText().isEmpty() || txtField[i].getText().isBlank()) {
    			JOptionPane.showMessageDialog(null, "alle Felder müssen befüllt sein ...", "Zertifikat", JOptionPane.ERROR_MESSAGE, null);
    			return;
    		}
    	}
    	
    	String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();
    	
    	String dname = "CN=%s, OU=%s, O=%s, L=%s, ST=%s, C=%s";
    	String dName = String.format(dname, txtField[0].getText(), txtField[1].getText(), txtField[2].getText(), txtField[3].getText(),
    			txtField[4].getText(), txtField[5].getText());
    	String ipEntry = "SubjectAlternativeName=ip:%s";
    	String ext = String.format(ipEntry, txtField[6].getText());

        ProcessBuilder pb = new ProcessBuilder(
                keytool,
                "-genkeypair",
                "-alias", "timetracker-ca",
                "-keyalg", "RSA",
                "-keysize", "2048",
                "-validity", "3650",
                "-keystore", "keystore.jks",
                "-storepass", "password", // Achtung: Klartext-Passwort
                "-dname", dName,
                "-ext", "BasicConstraints=ca:true",
                "-ext", ext,
                "-ext", "KeyUsage=digitalSignature,keyEncipherment,keyCertSign,cRLSign"
        );

        // Im aktuellen Verzeichnis ausführen (optional anpassen):
        pb.directory(Path.of(".").toFile());

        // Ausgabe auf die Konsole durchreichen (zum Debuggen sinnvoll)
        pb.inheritIO();

        Process p;
		try {
			p = pb.start();
			int exitCode = p.waitFor();
			if (exitCode != 0) {
	            throw new RuntimeException("keytool returned exit code " + exitCode);
	        }
		} catch (IOException | InterruptedException e) {
			logger.error("error creating new certificate keysore " + e.getMessage());
		}
		
		doStateTxtField(false);
    	btn[0].setEnabled(false);
    	text1.setVisible(true);
    }
    
    private void doExportCert() {
    	
    	if (!Files.exists(keystorePath)) {
    		JOptionPane.showMessageDialog(null, "Zertifikat muss zuerst erzeugt werden ...", "Zertifikat", JOptionPane.ERROR_MESSAGE, null);
    		return;
    	}
    	
    	try {
			java.nio.file.Files.createDirectories(certPath.getParent());
		} catch (IOException e) {
			logger.error("error creating path for cert-download " + e.getMessage());
		}
    	
    	String keytool = Path.of(System.getProperty("java.home"), "bin", "keytool").toString();

    	ProcessBuilder pb = new ProcessBuilder(
                keytool,
                "-exportcert",
                "-alias", "timetracker-ca",
                "-keystore", "keystore.jks",
                "-storepass", "password",
                "-rfc",
                "-file", "downloads\\cert.crt"
        );

        // Im aktuellen Verzeichnis ausführen (optional anpassen):
        pb.directory(Path.of(".").toFile());

        // Ausgabe auf die Konsole durchreichen (zum Debuggen sinnvoll)
        pb.inheritIO();

        Process p;
		try {
			p = pb.start();
			int exitCode = p.waitFor();
			if (exitCode != 0) {
	            throw new RuntimeException("keytool returned exit code " + exitCode);
	        }
		} catch (IOException | InterruptedException e) {
			logger.error("error creating new certificate keysore " + e.getMessage());
		}
		
		doStateTxtField(false);
    	btn[1].setEnabled(false);
    	text2.setVisible(true);
    }
    
    private void doStateTxtField(boolean enbl) {
    	for (int i = 0; i < txtField.length; i++) {
    		txtField[i].setEnabled(enbl);
    	}
    }

    private void bindEscToClose() {
        JRootPane rp = getRootPane();
        InputMap im = rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = rp.getActionMap();
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "CLOSE");
        am.put("CLOSE", new AbstractAction() { @Override public void actionPerformed(ActionEvent e) { dispose(); }});
    }

    private static Image loadImage(String path, int w, int h) {
        try (InputStream is = SettingsDialog.class.getResourceAsStream(path)) {
            if (is == null) return null;
            Image src = ImageIO.read(is);
            return src.getScaledInstance(w, h, Image.SCALE_SMOOTH);
        } catch (Exception e) {
            return null;
        }
    }

}
