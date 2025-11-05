package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;
import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;

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
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityMaster.Artikel;
import org.andy.fx.code.dataStructure.repositoryMaster.ArtikelRepository;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public class ArtikelPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private ArtikelRepository artikelRepository = new ArtikelRepository();
    private List<Artikel> artikelListe = new ArrayList<>();
    private Artikel leer = new Artikel();

    private final JTextField[] txtFields = new JTextField[3];
    private final JButton[] btnFields = new JButton[3];
    private JComboBox<String> cmbSelect;
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    private final Consumer<JTextField[]>[] operations = createOperations();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public ArtikelPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Artikelverwaltung");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        leer.setId(""); leer.setText(""); leer.setWert(null); // Leeren Listeneintrag erzeugen

        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 45; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
        String[] labels = { "Artikelnummer", "Text", "Wert (EUR)" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        artikelListe.clear();
        artikelListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        artikelListe.addAll(artikelRepository.findAll());
        String[] artikelTexte = artikelListe.stream()
                .map(Artikel::getText)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(artikelTexte);
        cmbSelect.setBounds(10, 20, 1000, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);

        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + i * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

        for (int i = 0; i < txtFields.length; i++) {
            txtFields[i] = makeField(x, y + i * 25, 900, 25, false, null);
            add(txtFields[i]);
        }
        txtFields[0].setText(artikelRepository.findMaxNummer());
        txtFields[0].setEditable(false);
        attachCommaToDot(txtFields[2]);
        x = 600; y = y + ((txtFields.length - 1) * 25);

        btnFields[0] = createButton("<html>Artikel<br>anlegen</html>", ButtonIcon.NEW.icon(), null);
        btnFields[1] = createButton("<html>Artikel<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);
        btnFields[2] = createButton("<html>Artikel<br>loeschen</html>", ButtonIcon.DEL.icon(), null);
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
        btnFields[0].setEnabled(true);
        
        x = 10 + lblFields[labels.length - 1].getWidth() + txtFields[txtFields.length - 1].getWidth() + 10;
        y = btnFields[btnFields.length - 1].getY() + btnFields[btnFields.length - 1].getHeight() + 20;

        setPreferredSize(new Dimension(x, y));
    }

	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener actionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            Artikel artikel = artikelListe.get(idx);

            if (idx == 0) {
                // Leereintrag: Felder leeren, Buttons sperren etc.
                btnFields[0].setEnabled(true);
                btnFields[1].setEnabled(false);
                btnFields[2].setEnabled(false);
                clearFields(txtFields);
                txtFields[0].setText(artikelRepository.findMaxNummer());
                txtFields[0].setEditable(false);
            } else {
                btnFields[0].setEnabled(false);
                btnFields[1].setEnabled(true);
                btnFields[2].setEnabled(true);
                txtFields[0].setEditable(false);

                txtFields[0].setText(artikel.getId());
                txtFields[1].setText(artikel.getText());
                txtFields[2].setText(artikel.getWert() != null ? artikel.getWert().toString() : "");
            }
        }
    };

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
        remove(cmbSelect);
        artikelListe.clear();
        artikelListe.add(leer);
        artikelListe.addAll(artikelRepository.findAll());
        String[] artikelTexte = artikelListe.stream()
                .map(Artikel::getText)
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(artikelTexte);
        cmbSelect.setBounds(10, 20, 750, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);
        btnFields[0].setEnabled(true);
        btnFields[1].setEnabled(false);
        btnFields[2].setEnabled(false);
        txtFields[0].setText(artikelRepository.findMaxNummer());
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
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
    
  //###################################################################################################################################################

    private Artikel fromFields(JTextField[] fields) {
        Artikel a = new Artikel();
        a.setId(fields[0].getText().trim());
        a.setText(fields[1].getText().trim());
        a.setWert(parseStringToBigDecimalSafe(fields[2].getText(), LocaleFormat.AUTO));
        return a;
    }
    
  //###################################################################################################################################################

    private boolean isValid(Artikel a) {
        if (a.getId().isEmpty() || a.getText().isEmpty() || a.getWert().toString().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Alle Felder müssen befüllt sein", "Validierung", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
  //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    @SuppressWarnings("unchecked")
    private Consumer<JTextField[]>[] createOperations() {
        Consumer<JTextField[]> insert = fields -> {
            Artikel a = fromFields(fields);
            if (!isValid(a)) return;
            artikelRepository.insert(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> update = fields -> {
            Artikel a = fromFields(fields);
            if (!isValid(a)) return;
            artikelRepository.update(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> delete = fields -> {
            String id = fields[0].getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(null, "ID fehlt", "Löschen", JOptionPane.ERROR_MESSAGE);
                return;
            }
            artikelRepository.delete(id);
            clearFields(fields);
        };

        return new Consumer[] { insert, update, delete };
    }

	public List<Artikel> getArtikelListe() {
		return artikelListe;
	}
}
