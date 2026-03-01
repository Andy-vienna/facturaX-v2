package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityJSON.JsonApp;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.Crypto;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import jakarta.mail.Message;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailServerPanel extends JPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(MailServerPanel.class);

	JPanel panel = new JPanel();

	private JsonApp s = Einstellungen.getAppSettings();

	// Titel definieren
	String titel = "Mailserver-Einstellungen";

	// Schrift konfigurieren
	Font font = new Font("Tahoma", Font.BOLD, 11);
	Color titleColor = Color.BLUE; // oder z. B. new Color(30, 60, 150);

	private JTextField[] txtFields = new JTextField[3];
	private JPasswordField passField = new JPasswordField();
	private final JButton[] btnFields = new JButton[2];

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public MailServerPanel() {

		setLayout(null);

		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), titel);
		border.setTitleFont(font);
		border.setTitleColor(titleColor);
		border.setTitleJustification(TitledBorder.LEFT); // optional: Ausrichtung links
		border.setTitlePosition(TitledBorder.TOP); // optional: Position oben

		setBorder(border);

		buildPanel();
		loadSettings(); // Daten beim Start laden
	}

	private void buildPanel() {
		int btnWidth = HauptFenster.getButtonx();
		int btnHeight = HauptFenster.getButtony();

		String labels[] = { "Mailserver (SMTP-Host)", "Port", "Username/Mail-Adresse", "Passwort" };

		// Textfelder
		for (int r = 0; r < labels.length; r++) {
			JLabel lbl = new JLabel(labels[r]);
			lbl.setBounds(10, 20 + r * 25, 200, 25); // Breite etwas reduziert
			add(lbl);

			if (r < 3) {
				// Die ersten 3 sind normale Textfelder
				txtFields[r] = makeField(220, 20 + r * 25, 400, 25, false, null);
				add(txtFields[r]);
			} else {
				// Das letzte ist das Passwortfeld
				passField = new JPasswordField();
				passField.setBounds(220, 20 + r * 25, 400, 25);
				add(passField);
			}
		}

		btnFields[0] = createButton("<html>speichern</html>", ButtonIcon.SAVE.icon(), null);
		btnFields[1] = createButton("<html>Testmail</html>", ButtonIcon.MAIL.icon(), null);
		btnFields[0].setBounds(490, 125, btnWidth, btnHeight);
		btnFields[1].setBounds(350, 125, btnWidth, btnHeight);
		add(btnFields[0]);
		add(btnFields[1]);

		btnFields[0].setEnabled(true);
		btnFields[1].setEnabled(true);
		btnFields[0].addActionListener(_ -> {
			saveSettings();
		});
		btnFields[1].addActionListener(_ -> {
			sendTestMail();
		});

		setPreferredSize(new Dimension(650, 20 + labels.length * 25 + btnHeight + 20));

	}

	// ###################################################################################################################################################

	// Hilfsfunktion für Textfelder
	private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
		JTextField t = new JTextField();
		t.setBounds(x, y, w, h);
		t.setHorizontalAlignment(SwingConstants.LEFT);
		t.setFocusable(true);
		if (bold)
			t.setFont(new Font("Tahoma", Font.BOLD, 11));
		if (bg != null)
			t.setBackground(bg);
		return t;
	}

	private void loadSettings() {
		try {
			txtFields[0].setText(s.smtpHost);
			txtFields[1].setText(String.valueOf(s.smtpPort));
			txtFields[2].setText(s.smtpUser);

			// Passwort entschlüsseln, falls vorhanden
			if (s.smtpPass != null && !s.smtpPass.isEmpty()) {
				passField.setText(Crypto.decrypt(s.smtpPass));
			}
		} catch (Exception e) {
			logger.error("Fehler beim Laden der Mail-Einstellungen: " + e.getMessage());
		}
	}

	public void saveSettings() {
		try {
			s.smtpHost = txtFields[0].getText();
			s.smtpPort = Integer.parseInt(txtFields[1].getText());
			s.smtpUser = txtFields[2].getText();

			// Passwort verschlüsselt in das JSON-Objekt schreiben
			String rawPass = new String(passField.getPassword());
			s.smtpPass = Crypto.encrypt(rawPass);

			JsonUtil.saveAPP(StartUp.getFileApp(), s);

		} catch (Exception e) {
			logger.error("Fehler beim Speichern: " + e.getMessage());
		}
	}

	private void sendTestMail() {
		// 1. Aktuelle Werte aus den Feldern auslesen (noch nicht gespeichert!)
		String host = txtFields[0].getText();
		String portStr = txtFields[1].getText();
		String user = txtFields[2].getText();
		String pass = new String(passField.getPassword());

		if (host.isEmpty() || user.isEmpty() || pass.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Bitte fülle alle Felder aus!", "Hinweis", JOptionPane.WARNING_MESSAGE);
			return;
		}

		// 2. Testversand in einem eigenen Thread (damit die GUI nicht einfriert)
		new Thread(() -> {
			btnFields[1].setEnabled(false);
			btnFields[1].setText("sende...");

			try {
				// Wir nutzen eine leicht abgewandelte Logik deiner sendMail Methode
				// Hier ohne Anhang, nur zum Testen der Verbindung
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.smtp.starttls.enable", "true");
				props.put("mail.smtp.host", host);
				props.put("mail.smtp.port", portStr);
				props.put("mail.smtp.timeout", "10000");

				final String finalPassword = pass;
				jakarta.mail.Session session = jakarta.mail.Session.getInstance(props,
						new jakarta.mail.Authenticator() {
							@Override
							protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
								return new jakarta.mail.PasswordAuthentication(user, finalPassword);
							}
						});

				jakarta.mail.Message message = new MimeMessage(session);
				message.setFrom(new InternetAddress(user)); // Absender = Username
				message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(user));
				message.setSubject("Testmail aus facturaX v2: Mailserver Einstellungen");
				message.setText("Hallo,\n\ndie SMTP-Verbindung funktioniert einwandfrei!");

				Transport.send(message);

				JOptionPane.showMessageDialog(this, "Testmail erfolgreich an " + user + " gesendet!", "Erfolg",
						JOptionPane.INFORMATION_MESSAGE);

			} catch (Exception ex) {
				logger.error("Testmail fehlgeschlagen: " + ex.getMessage());
				JOptionPane.showMessageDialog(this, "Fehler: " + ex.getMessage(), "Versand fehlgeschlagen",
						JOptionPane.ERROR_MESSAGE);
			} finally {
				btnFields[1].setEnabled(true);
				btnFields[1].setText("Testmail");
			}
		}).start();
	}
}
