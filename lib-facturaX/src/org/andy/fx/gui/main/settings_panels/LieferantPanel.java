package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.misc.CodeListen;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public class LieferantPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private CodeListen cl = new CodeListen();
	private JComboBox<String> cmbLand = new JComboBox<>();
	private String iso2code;
    
    private LieferantRepository lieferantRepository = new LieferantRepository();
    private List<Lieferant> lieferantListe = new ArrayList<>();
    private Lieferant leer = new Lieferant();

    private final JTextField[] txtFields = new JTextField[9];
    private final JButton[] btnFields = new JButton[3];
    private JComboBox<String> cmbSelect;
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    private final Consumer<JTextField[]>[] operations = createOperations();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public LieferantPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Lieferantenverwaltung");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        leer.setId(""); leer.setName(""); leer.setStrasse(""); leer.setPlz(""); leer.setOrt("");
        leer.setLand(""); leer.setUstid(""); leer.setTaxvalue(""); // Leeren Listeneintrag erzeugen
        
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 45; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
        String[] labels = { "Lieferant-Nr.", "Kundennummer", "Name", "Strasse", "PLZ", "Ort", "Land", "USt.-ID", "Steuersatz" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        lieferantListe.clear();
        lieferantListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        lieferantListe.addAll(lieferantRepository.findAll());
        String[] kundeTexte = lieferantListe.stream()
                .map(Lieferant::getName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(kundeTexte);
        cmbSelect.setBounds(10, 20, 500, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);

        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + i * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

        for (int i = 0; i < txtFields.length; i++) {
        	if (i == 6) {
        		txtFields[i] = makeField(x + 200, y + i * 25, 200, 25, false, null);
        	} else {
        		txtFields[i] = makeField(x, y + i * 25, 400, 25, false, null);
        	}
            add(txtFields[i]);
        }
        txtFields[0].setText(lieferantRepository.findMaxNummer());
        txtFields[0].setEditable(false);
        
        cmbLand = new JComboBox<>(cl.getCountries().toArray(new String[0]));
		cmbLand.setBounds(x, 195, 200, 25);
		cmbLand.addActionListener(_ -> doCountry());
		add(cmbLand);

		x = 510 + 10;
		y = txtFields[txtFields.length - 6].getY() - 10;

        btnFields[0] = createButton("<html>Lieferant<br>anlegen</html>", ButtonIcon.NEW.icon(), null);
        btnFields[1] = createButton("<html>Lieferant<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);
        btnFields[2] = createButton("<html>Lieferant<br>loeschen</html>", ButtonIcon.DEL.icon(), null);
        for (int i = 0; i < btnFields.length; i++) {
        	btnFields[i].setBounds(x, y + i * (btnHeight + 5), btnWidth, btnHeight);
            add(btnFields[i]);
        }
        for (int i = 0; i < btnFields.length; i++) {
            final int index = i;
            btnFields[i].addActionListener(_ -> {
                operations[index].accept(txtFields);
                rebuild();
            });
        }
        btnFields[0].setEnabled(true);
        
        x = 10 + ((lblFields[labels.length - 1].getWidth() + txtFields[txtFields.length - 1].getWidth())) + btnWidth + 20;
        y = txtFields[txtFields.length - 1].getY() + 45;
        
        setPreferredSize(new Dimension(x, y));
    }

	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            Lieferant lieferant = lieferantListe.get(idx);

            if (idx == 0) {
                // Leereintrag: Felder leeren, Buttons sperren etc.
                btnFields[0].setEnabled(true);
                btnFields[1].setEnabled(false);
                btnFields[2].setEnabled(false);
                clearFields(txtFields);
                txtFields[0].setText(lieferantRepository.findMaxNummer());
                txtFields[0].setEditable(false);
            } else {
                btnFields[0].setEnabled(false);
                btnFields[1].setEnabled(true);
                btnFields[2].setEnabled(true);
                txtFields[0].setEditable(false);

                txtFields[0].setText(lieferant.getId());
                txtFields[1].setText(lieferant.getKdnr());
                txtFields[2].setText(lieferant.getName());
                txtFields[3].setText(lieferant.getStrasse());
                txtFields[4].setText(lieferant.getPlz());
                txtFields[5].setText(lieferant.getOrt());
                txtFields[6].setText(cl.getCountryFromCode(lieferant.getLand()));
                txtFields[7].setText(lieferant.getUstid());
                txtFields[8].setText(lieferant.getTaxvalue());
                
                for (int i = 0; i < cl.getCountries().size(); i++) {
                	if (cl.getCountries().get(i).contains(lieferant.getLand())) {
                		cmbLand.setSelectedIndex(i);
                		break;
                	}
                }
            }
        }
    };

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
        remove(cmbSelect);
        lieferantListe.clear();
        lieferantListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        lieferantListe.addAll(lieferantRepository.findAll());
        String[] kundeTexte = lieferantListe.stream()
                .map(Lieferant::getName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(kundeTexte);
        cmbSelect.setBounds(10, 20, 500, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);
        btnFields[0].setEnabled(true);
        btnFields[1].setEnabled(false);
        btnFields[2].setEnabled(false);
        txtFields[0].setText(lieferantRepository.findMaxNummer());
        txtFields[0].setEditable(false);
        
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

    private Lieferant fromFields(JTextField[] fields) {
        Lieferant l = new Lieferant();
        l.setId(fields[0].getText().trim());
        l.setKdnr(fields[1].getText().trim());
        l.setName(fields[2].getText().trim());
        l.setStrasse(fields[3].getText().trim());
        l.setPlz(fields[4].getText().trim());
        l.setOrt(fields[5].getText().trim());
        l.setLand(iso2code);
        l.setUstid(fields[7].getText().trim());
        l.setTaxvalue(fields[8].getText().trim());
        return l;
    }
    
  //###################################################################################################################################################

    private boolean isValid(Lieferant l) {
        if (l.getId().isEmpty() || l.getName().isEmpty() || l.getStrasse().toString().isEmpty() || l.getPlz().isEmpty() || l.getOrt().isEmpty() ||
        	l.getLand().toString().isEmpty() || l.getUstid().toString().isEmpty() || l.getTaxvalue().toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alle Felder müssen befüllt sein", "Validierung", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
  //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
        cmbLand.setSelectedIndex(0);
    }
    
    private void doCountry() {
    	if (cmbLand.getSelectedIndex() == 0) return;
    	iso2code = cmbLand.getSelectedItem().toString().substring(0,2);
    	String ctry = cl.getCountryFromCode(iso2code);
    	
    	this.txtFields[6].setText(ctry);
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    @SuppressWarnings("unchecked")
    private Consumer<JTextField[]>[] createOperations() {
        Consumer<JTextField[]> insert = fields -> {
            Lieferant l = fromFields(fields);
            if (!isValid(l)) return;
            lieferantRepository.insert(l);
            clearFields(fields);
        };

        Consumer<JTextField[]> update = fields -> {
            Lieferant l = fromFields(fields);
            if (!isValid(l)) return;
            lieferantRepository.update(l);
            clearFields(fields);
        };

        Consumer<JTextField[]> delete = fields -> {
            String id = fields[0].getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID fehlt", "Löschen", JOptionPane.ERROR_MESSAGE);
                return;
            }
            lieferantRepository.delete(id);
            clearFields(fields);
        };

        return new Consumer[] { insert, update, delete };
    }
}
