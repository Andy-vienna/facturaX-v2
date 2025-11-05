package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.repositoryMaster.OwnerRepository;
import org.andy.fx.code.misc.CodeListen;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public class OwnerPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private CodeListen cl = new CodeListen();
	private JComboBox<String> cmbLand = new JComboBox<>();
	private JComboBox<String> cmbCurr = new JComboBox<>();
	private String iso2code; private String currency3code;
    
    private OwnerRepository ownerRepository = new OwnerRepository();
    private List<Owner> ownerListe = new ArrayList<>();

    private final JTextField[] txtFields = new JTextField[11];
    private final JButton[] btnFields = new JButton[2];
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    private final Consumer<JTextField[]>[] operations = createOperations();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public OwnerPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Eigentümerdaten");
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
    	int x = 10, y = 20; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
        String[] labels = { "Name", "Adresse", "PLZ", "Ort", "Land", "USt.-Id", "Kontakt Name", "Kontakt Telefon", "Kontakt E-Mail", "Währung", "Steuer-Id" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + i * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

        for (int i = 0; i < txtFields.length; i++) {
        	if (i == 4 || i == 9) {
        		txtFields[i] = makeField(x + 200, y + i * 25, 150, 25, false, null);
        		txtFields[i].setFocusable(false);
        	} else {
        		txtFields[i] = makeField(x, y + i * 25, 350, 25, false, null);
        	}
            add(txtFields[i]);
        }
        
        cmbLand = new JComboBox<>(cl.getCountries().toArray(new String[0]));
		cmbLand.setBounds(x, 120, 200, 25);
		cmbLand.addActionListener(_ -> doCountry());
		add(cmbLand);
		
		cmbCurr = new JComboBox<>(cl.getCurrencies().toArray(new String[0]));
		cmbCurr.setBounds(x, 245, 200, 25);
		cmbCurr.addActionListener(_ -> doCurrency());
		add(cmbCurr);

		x = 110; y = y + ((txtFields.length - 1) * 25);
		
        btnFields[0] = createButton("<html>Owner<br>anlegen</html>", ButtonIcon.NEW.icon(), null);
        btnFields[1] = createButton("<html>Owner<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);
        for (int i = 0; i < btnFields.length; i++) {
            btnFields[i].setBounds(x + i * (btnWidth + 10), y + 30, btnWidth, btnHeight);
            add(btnFields[i]);
        }
        for (int i = 0; i < btnFields.length; i++) {
            final int index = i;
            btnFields[i].addActionListener(_ -> {
                operations[index].accept(txtFields);
                rebuild();
            });
        }
        
        rebuild(); // Textfelder befüllen
        
        x = 10 + lblFields[labels.length - 1].getWidth() + txtFields[txtFields.length - 1].getWidth() + 10;
        y = btnFields[btnFields.length - 1].getY() + btnFields[btnFields.length - 1].getHeight() + 20;

        setPreferredSize(new Dimension(x, y));
    }

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
        ownerListe.clear();
        ownerListe.addAll(ownerRepository.findAll());
        if (ownerListe.size() < 1) {
        	btnFields[0].setEnabled(true);
            btnFields[1].setEnabled(false);
            return;
        } else {
        	btnFields[0].setEnabled(false);
            btnFields[1].setEnabled(true);
        }
        String[] ownerTexte = new String[11];
        ownerTexte[0] = ownerListe.get(0).getName();
        ownerTexte[1] = ownerListe.get(0).getAdresse();
        ownerTexte[2] = ownerListe.get(0).getPlz();
        ownerTexte[3] = ownerListe.get(0).getOrt();
        ownerTexte[4] = cl.getCountryFromCode(ownerListe.get(0).getLand());
        ownerTexte[5] = ownerListe.get(0).getUstid();
        ownerTexte[6] = ownerListe.get(0).getKontaktName();
        ownerTexte[7] = ownerListe.get(0).getKontaktTel();
        ownerTexte[8] = ownerListe.get(0).getKontaktMail();
        ownerTexte[9] = ownerListe.get(0).getCurrency();
        ownerTexte[10] = ownerListe.get(0).getTaxid();
        for (int i = 0; i < ownerTexte.length; i++) {
        	txtFields[i].setText(ownerTexte[i]);
        }
        for (int i = 0; i < cl.getCountries().size(); i++) {
        	if (cl.getCountries().get(i).contains(ownerListe.get(0).getLand())) {
        		cmbLand.setSelectedIndex(i);
        		break;
        	}
        }
        for (int i = 0; i < cl.getCurrencies().size(); i++) {
        	if (cl.getCurrencies().get(i).contains(ownerListe.get(0).getCurrency())) {
        		cmbCurr.setSelectedIndex(i);
        		break;
        	}
        }
        
        revalidate();
        repaint();
    }
    
  //###################################################################################################################################################

    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.LEFT);
        t.setFocusable(true);
        if (bold) t.setFont(font);
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
  //###################################################################################################################################################

    private Owner fromFields(JTextField[] fields) {
        Owner a = new Owner();
        a.setName(fields[0].getText().trim());
        a.setAdresse(fields[1].getText().trim());
        a.setPlz(fields[2].getText().trim());
        a.setOrt(fields[3].getText().trim());
        a.setLand(iso2code);
        a.setUstid(fields[5].getText().trim());
        a.setKontaktName(fields[6].getText().trim());
        a.setKontaktTel(fields[7].getText().trim());
        a.setKontaktMail(fields[8].getText().trim());
        a.setCurrency(fields[9].getText().trim());
        a.setTaxid(fields[10].getText().trim());
        return a;
    }
    
  //###################################################################################################################################################

    private boolean isValid(Owner a) {
        if (a.getName().isEmpty() || a.getAdresse().isEmpty() || a.getPlz().toString().isEmpty() || a.getOrt().isEmpty() || a.getLand().isEmpty() ||
        		a.getUstid().isEmpty() || a.getUstid().isEmpty() || a.getKontaktName().isEmpty() || a.getKontaktTel().isEmpty() ||
        		a.getKontaktMail().isEmpty() || a.getCurrency().isEmpty() || a.getTaxid().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alle Felder müssen befüllt sein", "Validierung", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
  //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
    }
    
    private void doCountry() {
    	if (cmbLand.getSelectedIndex() == 0) return;
    	iso2code = cmbLand.getSelectedItem().toString().substring(0,2);
    	String ctry = cl.getCountryFromCode(iso2code);
    	
    	this.txtFields[4].setText(ctry);
    }
    
    private void doCurrency() {
    	if (cmbCurr.getSelectedIndex() == 0) return;
    	currency3code = cmbCurr.getSelectedItem().toString().substring(0,3);
    	
    	this.txtFields[9].setText(currency3code);
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    @SuppressWarnings("unchecked")
    private Consumer<JTextField[]>[] createOperations() {
        Consumer<JTextField[]> insert = fields -> {
            Owner a = fromFields(fields);
            if (!isValid(a)) return;
            ownerRepository.insert(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> update = fields -> {
            Owner a = fromFields(fields);
            if (!isValid(a)) return;
            ownerRepository.update(a);
            clearFields(fields);
        };

        return new Consumer[] { insert, update };
    }

	public List<Owner> getOwnerListe() {
		return ownerListe;
	}
}
