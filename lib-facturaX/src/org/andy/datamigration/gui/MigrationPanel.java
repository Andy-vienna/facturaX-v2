package org.andy.datamigration.gui;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.andy.datamigration.code.dataStructure.entityMaster.BankMig;
import org.andy.datamigration.code.dataStructure.entityProductive.AusgabenMig;
import org.andy.datamigration.code.dataStructure.entityProductive.SVSteuerMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.ArtikelRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.BankRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.GwbRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.KundeRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.LieferantRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.OwnerRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.TaxRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.TextRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryMaster.UserRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.AngebotRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.AusgabenRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.BestellungRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.EinkaufRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.FileStoreRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.LieferscheinRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.RechnungRepositoryMig;
import org.andy.datamigration.code.dataStructure.repositoryProductive.SVSteuerRepositoryMig;
import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.andy.fx.code.dataStructure.repositoryMaster.ArtikelRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.GwbRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.OwnerRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.TaxRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.SVSteuerRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.gui.main.HauptFenster;

public class MigrationPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Font font = new Font("Tahoma", Font.BOLD, 11);
	private final Color titleColor = Color.BLUE;

	private static String migMasterJDBC = null;
	private static String migWorkJDBC = null;

	private final JTextField[] txtFields = new JTextField[4];
	private final JTextField[] typeFields = new JTextField[4];
	private final JButton[] btnFieldsM = new JButton[9];
	private final JButton[] btnFieldsW = new JButton[8];

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public MigrationPanel() {
		setLayout(null);
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
				"Datenmigration");
		border.setTitleFont(font);
		border.setTitleColor(titleColor);
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		setBorder(border);

		buildPanel();
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private void buildPanel() {
		int x = 30, y = 20; // Variablen für automatische Positionierung
		int btnWidth = HauptFenster.getButtonx();
		int btnHeight = HauptFenster.getButtony();

		String text1 = "Panel zur Datenmigration von Datenbank zu Datenbank";
		String text2 = "Diese Funktion sollte nur durch versierte Administratoren verwendet werden. - Bitte die Dokumentation beachten ... !";
		String text3 = "Die Connection-Strings sind in direkter JDBC Notation einzutragen. User und Passwort für die Ziel-DB muss gleich der Quell-DB sein.";
		String text4 = "aktueller user: " + Einstellungen.getDbSettings().dbUser + " | aktuelles Passwort: "
				+ Einstellungen.getDbSettings().dbPass;

		String description = "<html>" + "<span style='font-size:24px; font-weight:bold; color:black;'>" + text1
				+ "</span><br>" + "<span style='font-size:16px; font-weight:bold; color:red  ;'>" + text2
				+ "</span><br>" + "<span style='font-size:8px ; font-weight:bold; color:black;'></span><br>"
				+ "<span style='font-size:10px; font-weight:bold; color:blue ;'>" + text3 + "</span><br>"
				+ "<span style='font-size:10px; font-weight:bold; color:blue ;'>" + text4 + "</span>" + "</html>";

		String[] labels = { "Quell-Datenbank MASTER", "Quell-Datenbank WORK", "Ziel-Datenbank MASTER",
				"Ziel-Datenbank WORK" };
		JLabel[] lblFields = new JLabel[labels.length];

		String[] btnLblMaster = { "<html>Owner<br>übertragen</html>", "<html>User<br>übertragen</html>",
				"<html>Bank<br>übertragen</html>", "<html>Kunde<br>übertragen</html>",
				"<html>Lieferant<br>übertragen</html>", "<html>Artikel<br>übertragen</html>",
				"<html>Texte<br>übertragen</html>", "<html>Tax<br>übertragen</html>",
				"<html>GWB<br>übertragen</html>" };
		String[] btnLblWork = { "<html>Angebote<br>übertragen</html>", "<html>Rechnungen<br>übertragen</html>",
				"<html>Bestellungen<br>übertragen</html>", "<html>Lieferscheine<br>übertragen</html>",
				"<html>Dateien<br>übertragen</html>", "<html>Ausgaben<br>übertragen</html>",
				"<html>Einkäufe<br>übertragen</html>", "<html>Steuer/SV<br>übertragen</html>" };

		JLabel lblDescription = new JLabel(description);
		lblDescription.setBounds(x, y, 1200, 125);
		add(lblDescription);
		y = 170;

		for (int i = 0; i < labels.length; i++) {
			lblFields[i] = new JLabel(labels[i]);
			lblFields[i].setBounds(x, y + i * 25, 200, 25);
			add(lblFields[i]);
		}
		x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

		for (int ii = 0; ii < txtFields.length; ii++) {
			final int i = ii;
			txtFields[i] = makeField(x, y + i * 25, 1200, 25, false, null);
			txtFields[i].getDocument().addDocumentListener(docChanged(() -> onTextChanged(i)));
			add(txtFields[i]);
		}
		txtFields[0].setEditable(false); txtFields[1].setEditable(false);
		txtFields[0].setFocusable(false); txtFields[1].setFocusable(false);

		x = txtFields[txtFields.length - 1].getX() + txtFields[txtFields.length - 1].getWidth() + 10;

		for (int i = 0; i < typeFields.length; i++) {
			typeFields[i] = makeField(x, y + i * 25, 120, 25, true, null);
			typeFields[i].setFocusable(false); typeFields[i].setHorizontalAlignment(JTextField.CENTER);
			add(typeFields[i]);
		}
		
		x = 30;
		y = y + ((txtFields.length - 1) * 25) + 50;

		JLabel lblMaster = new JLabel("Tabellen der MASTER-Datenbank");
		lblMaster.setBounds(x, y, 200, 25);
		add(lblMaster);
		y = lblMaster.getY() + lblMaster.getHeight() + 10;


		for (int i = 0; i < btnFieldsM.length; i++) {
			btnFieldsM[i] = createButton(btnLblMaster[i], null, null);
			btnFieldsM[i].setBounds(x + i * (btnWidth + 10), y, btnWidth, btnHeight);
			btnFieldsM[i].setEnabled(true);
			add(btnFieldsM[i]);
		}
		y = btnFieldsM[0].getY() + btnFieldsM[0].getHeight() + 50;

		JLabel lblWork = new JLabel("Tabellen der WORK-Datenbank");
		lblWork.setBounds(x, y, 200, 25);
		add(lblWork);
		y = lblWork.getY() + lblWork.getHeight() + 10;

		for (int i = 0; i < btnFieldsW.length; i++) {
			btnFieldsW[i] = createButton(btnLblWork[i], null, null);
			btnFieldsW[i].setBounds(x + i * (btnWidth + 10), y, btnWidth, btnHeight);
			btnFieldsW[i].setEnabled(true);
			add(btnFieldsW[i]);
		}
		y = btnFieldsW[0].getY() + btnFieldsW[0].getHeight() + 50;
		
		txtFields[0].setText(Einstellungen.getsMasterData());
		txtFields[1].setText(Einstellungen.getsProductiveData());

		// Achtions binden
		wireActionsMaster();
		wireActionsWork();

		setPreferredSize(new Dimension(x, y));
	}

	// ###################################################################################################################################################
	// ActionListener
	// ###################################################################################################################################################

	private void wireActionsMaster() {
		// Ziel-Connection aus Textfeld holen, falls benötigt:
		// migMasterJDBC wird intern von *RepositoryMig* genutzt
		Runnable setTargetConn = () -> migMasterJDBC = txtFields[2].getText();

		// Owner
		onClick(btnFieldsM[0], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			OwnerRepository src = new OwnerRepository();
			OwnerRepositoryMig dst = new OwnerRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[0]);
		});
		
		// User
		onClick(btnFieldsM[1], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			UserRepository src = new UserRepository();
			UserRepositoryMig dst = new UserRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[1]);
		});
				
		// Bank (Sonderbehandlung)
		onClick(btnFieldsM[2], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			BankRepository src = new BankRepository();
			BankRepositoryMig dst = new BankRepositoryMig();
			List<Bank> bList = src.findAll(); Bank b = new Bank(); BankMig bMig = new BankMig();
			dst.deleteAllData(); // Ziel-Tabelle löschen und Index zurücksetzen
			for (int x = 0; x < bList.size(); x++) {
				b = bList.get(x);
				bMig.setBankName(b.getBankName());
				bMig.setBic(b.getBic());
				bMig.setIban(b.getIban());
				bMig.setKtoName(b.getKtoName());
				dst.insert(bMig);
			}
			btnFieldsM[2].setBackground(Color.GREEN);
		});

		// Kunde
		onClick(btnFieldsM[3], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			KundeRepository src = new KundeRepository();
			KundeRepositoryMig dst = new KundeRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[3]);
		});
		
		// Lieferant
		onClick(btnFieldsM[4], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			LieferantRepository src = new LieferantRepository();
			LieferantRepositoryMig dst = new LieferantRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[4]);
		});

		// Artikel
		onClick(btnFieldsM[5], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			ArtikelRepository src = new ArtikelRepository();
			ArtikelRepositoryMig dst = new ArtikelRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[5]);
		});

		// Texte
		onClick(btnFieldsM[6], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			TextRepository src = new TextRepository();
			TextRepositoryMig dst = new TextRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[6]);
		});
		
		// Tax
		onClick(btnFieldsM[7], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			TaxRepository src = new TaxRepository();
			TaxRepositoryMig dst = new TaxRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[7]);
		});
		
		// GWB
		onClick(btnFieldsM[8], () -> {
			if (txtFields[2].getText().isBlank() || txtFields[2].getText().isEmpty()) return;
			setTargetConn.run();
			GwbRepository src = new GwbRepository();
			GwbRepositoryMig dst = new GwbRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::insert, btnFieldsM[8]);
		});

	}
	
	private void wireActionsWork() {
		// Ziel-Connection aus Textfeld holen, falls benötigt:
		// migMasterJDBC wird intern von *RepositoryMig* genutzt
		Runnable setTargetConn = () -> migWorkJDBC = txtFields[3].getText();

		// Angebote
		onClick(btnFieldsW[0], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			AngebotRepository src = new AngebotRepository();
			AngebotRepositoryMig dst = new AngebotRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[0]);
		});
		
		// Rechnungen
		onClick(btnFieldsW[1], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			RechnungRepository src = new RechnungRepository();
			RechnungRepositoryMig dst = new RechnungRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[1]);
		});
		
		// Bestellungen
		onClick(btnFieldsW[2], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			BestellungRepository src = new BestellungRepository();
			BestellungRepositoryMig dst = new BestellungRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[2]);
		});
		
		// Lieferscheine
		onClick(btnFieldsW[3], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			LieferscheinRepository src = new LieferscheinRepository();
			LieferscheinRepositoryMig dst = new LieferscheinRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[3]);
		});
		
		// Dateien
		onClick(btnFieldsW[4], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			FileStoreRepository src = new FileStoreRepository();
			FileStoreRepositoryMig dst = new FileStoreRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[4]);
		});
		
		// Ausgaben (Sonderbehandlung)
		onClick(btnFieldsW[5], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			AusgabenRepository src = new AusgabenRepository();
			AusgabenRepositoryMig dst = new AusgabenRepositoryMig();
			List<Ausgaben> aList = src.findAll(); Ausgaben a = new Ausgaben(); AusgabenMig aMig = new AusgabenMig();
			dst.deleteAllData(); // Ziel-Tabelle löschen und Index zurücksetzen
			for (int x = 0; x < aList.size(); x++) {
				a = aList.get(x);
				aMig.setArt(a.getArt());
				aMig.setBrutto(a.getBrutto());
				aMig.setDatei(a.getDatei());
				aMig.setDateiname(a.getDateiname());
				aMig.setDatum(a.getDatum());
				aMig.setJahr(a.getJahr());
				aMig.setLand(a.getLand());
				aMig.setNetto(a.getNetto());
				aMig.setSteuer(a.getSteuer());
				aMig.setSteuersatz(a.getSteuersatz());
				aMig.setWaehrung(a.getWaehrung());
				dst.save(aMig);
			}
			btnFieldsW[5].setBackground(Color.GREEN);
		});
		
		// Einkäufe
		onClick(btnFieldsW[6], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			EinkaufRepository src = new EinkaufRepository();
			EinkaufRepositoryMig dst = new EinkaufRepositoryMig();
			migrateAsync(src::findAll, dst::deleteAllData, dst::save, btnFieldsW[6]);
		});
		
		// Steuer und Sozialversicherung (Sonderbehandlung)
		onClick(btnFieldsW[7], () -> {
			if (txtFields[3].getText().isBlank() || txtFields[3].getText().isEmpty()) return;
			setTargetConn.run();
			SVSteuerRepository src = new SVSteuerRepository();
			SVSteuerRepositoryMig dst = new SVSteuerRepositoryMig();
			List<SVSteuer> sList = src.findAll(); SVSteuer s = new SVSteuer(); SVSteuerMig sMig = new SVSteuerMig();
			dst.deleteAllData(); // Ziel-Tabelle löschen und Index zurücksetzen
			for (int x = 0; x < sList.size(); x++) {
				s = sList.get(x);
				sMig.setBezeichnung(s.getBezeichnung());
				sMig.setDatei(s.getDatei());
				sMig.setDateiname(s.getDateiname());
				sMig.setDatum(s.getDatum());
				sMig.setJahr(s.getJahr());
				sMig.setOrganisation(s.getOrganisation());
				sMig.setStatus(s.getStatus());
				sMig.setZahllast(s.getZahllast());
				sMig.setZahlungsziel(s.getZahlungsziel());
				dst.save(sMig);
			}
			btnFieldsW[7].setBackground(Color.GREEN);
		});
	}

	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################
	
	private static DocumentListener docChanged(Runnable r){
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e){ r.run(); }
            @Override public void removeUpdate(DocumentEvent e){ r.run(); }
            @Override public void changedUpdate(DocumentEvent e){ r.run(); }
        };
    }
	
	private void onTextChanged(int i) {
		if (txtFields[i].getText().contains("jdbc:postgresql")) {
			typeFields[i].setText("PostgreSQL"); typeFields[i].setForeground(Color.BLUE);
		} else { 
			typeFields[i].setText("Microsoft SQL"); typeFields[i].setForeground(Color.PINK);
		}
	}
	
	// Generic: 1 Button-Handler 
	private <T> void migrateAsync(Supplier<List<T>> srcFetch, // Quelle: findAll()
			Runnable targetClear, // Ziel leeren: deleteAllData()
			Consumer<T> targetInsert, // Ziel befüllen: insert(entity)
			JButton trigger // UI-Button für Feedback
	) {
		trigger.setEnabled(false);
		trigger.setBackground(null);

		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				targetClear.run();
				for (T e : srcFetch.get()) {
					targetInsert.accept(e);
				}
				return null;
			}

			@Override
			protected void done() {
				trigger.setEnabled(true);
				trigger.setBackground(Color.GREEN);
			}
		}.execute();
	}

	private static void onClick(JButton b, Runnable r) {
		b.addActionListener(_ -> r.run());
	}

	// ###################################################################################################################################################

	private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
		JTextField t = new JTextField();
		t.setBounds(x, y, w, h);
		t.setHorizontalAlignment(SwingConstants.LEFT);
		t.setFocusable(true);
		if (bold)
			t.setFont(font);
		if (bg != null)
			t.setBackground(bg);
		return t;
	}

	// ###################################################################################################################################################
	// Getter und Setter für Felder
	// ###################################################################################################################################################

	public static String getMigMasterJDBC() {
		return migMasterJDBC;
	}

	public static String getMigWorkJDBC() {
		return migWorkJDBC;
	}
}
