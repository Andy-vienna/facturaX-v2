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

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public class BankPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private BankRepository bankRepository = new BankRepository();
    private List<Bank> bankListe = new ArrayList<>();
    private Bank leer = new Bank();
    private int iId = 0;

    private final JTextField[] txtFields = new JTextField[4];
    private final JButton[] btnFields = new JButton[3];
    private JComboBox<String> cmbSelect;
    private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    private final Consumer<JTextField[]>[] operations = createOperations();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public BankPanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Bankdaten");
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        leer.setBankName(""); leer.setIban(""); leer.setBic(""); leer.setKtoName(""); // Leeren Listeneintrag erzeugen

        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	int x = 10, y = 45; // Variablen für automatische Positionierung
    	int btnWidth = HauptFenster.getButtonx();
    	int btnHeight = HauptFenster.getButtony();
    	
        String[] labels = { "Bankname", "IBAN", "BIC", "Kontoinhaber" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        bankListe.clear();
        bankListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        bankListe.addAll(bankRepository.findAll());
        String[] bankTexte = bankListe.stream()
                .map(Bank::getBankName)   // oder .getId(), oder beliebiges Feld
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(bankTexte);
        cmbSelect.setBounds(10, 20, 450, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);

        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x, y + i * 25, 100, 25);
            add(lblFields[i]);
        }
        x = lblFields[labels.length - 1].getX() + lblFields[labels.length - 1].getWidth();

        for (int i = 0; i < txtFields.length; i++) {
            txtFields[i] = makeField(x, y + i * 25, 350, 25, false, null);
            add(txtFields[i]);
        }
        x = 10; y = y + ((txtFields.length - 1) * 25);

        btnFields[0] = createButton("<html>Bank<br>anlegen</html>", ButtonIcon.NEW.icon(), null);
        btnFields[1] = createButton("<html>Bank<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);
        btnFields[2] = createButton("<html>Bank<br>loeschen</html>", ButtonIcon.DEL.icon(), null);
        for (int i = 0; i < btnFields.length; i++) {
            final int index = i;
            btnFields[i].setBounds(x + i * (btnWidth + 10), y + 30, btnWidth, btnHeight);
            btnFields[i].addActionListener(_ -> {
                operations[index].accept(txtFields);
                rebuild();
            });
            add(btnFields[i]);
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
            Bank bank = bankListe.get(idx);

            if (idx == 0) {
                // Leereintrag: Felder leeren, Buttons sperren etc.
                btnFields[0].setEnabled(true);
                btnFields[1].setEnabled(false);
                btnFields[2].setEnabled(false);
                clearFields(txtFields);
            } else {
                btnFields[0].setEnabled(false);
                btnFields[1].setEnabled(true);
                btnFields[2].setEnabled(true);
                
                iId = bank.getId();
                txtFields[0].setText(bank.getBankName());
                txtFields[1].setText(bank.getIban());
                txtFields[2].setText(bank.getBic());
                txtFields[3].setText(bank.getKtoName());
            }
        }
    };

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
        remove(cmbSelect);
        bankListe.clear();
        bankListe.add(leer);
        bankListe.addAll(bankRepository.findAll());
        String[] bankTexte = bankListe.stream()
                .map(Bank::getBankName)
                .toArray(String[]::new);
        cmbSelect = new JComboBox<>(bankTexte);
        cmbSelect.setBounds(10, 20, 450, 25);
        cmbSelect.addActionListener(actionListener);
        add(cmbSelect);
        btnFields[0].setEnabled(true);
        btnFields[1].setEnabled(false);
        btnFields[2].setEnabled(false);
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

    private Bank fromFields(JTextField[] fields) {
        Bank a = new Bank();
        a.setBankName(fields[0].getText().trim());
        a.setIban(fields[1].getText().trim());
        a.setBic(fields[2].getText().trim());
        a.setKtoName(fields[3].getText().trim());
        return a;
    }
    
    //###################################################################################################################################################

    private boolean isValid(Bank a) {
        if (a.getBankName().isEmpty() || a.getIban().isEmpty() || a.getBic().isEmpty() || a.getKtoName().isEmpty()) {
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
            Bank a = fromFields(fields);
            if (!isValid(a)) return;
            bankRepository.insert(a);
            clearFields(fields);
        };

        Consumer<JTextField[]> update = fields -> {
            Bank a = fromFields(fields);
            if (!isValid(a)) return;
            bankRepository.update(a, iId);
            clearFields(fields);
        };

        Consumer<JTextField[]> delete = fields -> {
            bankRepository.delete(iId);
            clearFields(fields);
        };

        return new Consumer[] { insert, update, delete };
    }

	public List<Bank> getBankListe() {
		return bankListe;
	}
}
