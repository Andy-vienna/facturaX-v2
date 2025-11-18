package org.andy.gui.main.panels;

import static org.andy.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.andy.code.dataStructure.entity.Employee;
import org.andy.code.dataStructure.repository.EmployeeRepository;
import org.andy.code.main.Settings;
import org.andy.gui.iconHandler.ButtonIcon;
import org.andy.gui.main.MainWindow;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class EmployeePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    
    private JTextField[] employee = new JTextField[9];
    private DatePicker datum = new DatePicker();
    private JButton[] btn = new JButton[3];
    
    private EmployeeRepository emRepo = new EmployeeRepository();
    Employee em = new Employee();
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public EmployeePanel(String user) {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Mitarbeiter anlegen/bearbeiten"
        );
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);

        em = emRepo.findByUser(user);
        if (em == null) em = new Employee();

        buildPanel(em);
    }
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private void buildPanel(Employee em) {
    	Dimension size = new Dimension(0,0); int y = 30;
		
		JLabel[] label = new JLabel[9];
		String[] lbl = new String[] { "Personalnummer", "Username (muss eindeutig sein):", "Vorname:", "Nachname:", "Straße und Hausnummer:",
				"PLZ:", "Ort:", "Geburtstag", "Sozialversicherungsnummer" };
		for (int i = 0; i < lbl.length; i++) {
			label[i] = new JLabel(lbl[i]);
			label[i].setHorizontalAlignment(SwingConstants.RIGHT);
			label[i].setBounds(10, y + (i * 25), 200, 25);
			add(label[i]);
		}
		
		for (int i = 0; i < employee.length; i++) {
			employee[i] = new JTextField();
			employee[i].setHorizontalAlignment(SwingConstants.LEFT);
			employee[i].setBounds(220, y + (i * 25), 400, 25);
			employee[i].getDocument().addDocumentListener(new DocumentListener() {
				  @Override public void insertUpdate(DocumentEvent e) { onChange(); }
				  @Override public void removeUpdate(DocumentEvent e) { onChange(); }
				  @Override public void changedUpdate(DocumentEvent e) { }
			});
			add(employee[i]);
		}
		employee[0].setFocusable(false); employee[7].setVisible(false);
    	employee[0].setFont(new Font("Arial", Font.BOLD, 12));
    	
    	DatePickerSettings d = new DatePickerSettings(Locale.GERMAN);
		d.setFormatForDatesCommonEra("dd.MM.yyyy");
		
		datum.setSettings(d); datum.setBounds(222, y + (7 * 25), 150, 25);
		JTextField dt = datum.getComponentDateTextField(); dt.setHorizontalAlignment(SwingConstants.CENTER);
		datum.setDateToToday();
		add(datum);
    	
		size.width = label[0].getX() + label[0].getWidth() + 10 + employee[0].getWidth() + 10;
		int btnX = size.width - (btn.length * Settings.getButtonX()) - 10;
		
		btn[0] = createButton("<html>neuer<br>MA</html>", ButtonIcon.SAVE.icon(), null);
		btn[0].setBounds(btnX, (label.length * 25) + 45, Settings.getButtonX(), Settings.getButtonY());
		btn[0].setEnabled(false);
		btn[0].addActionListener(_ -> doInsertEmployee(em));
		add(btn[0]);
		
		btn[1] = createButton("<html>MA<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);
		btn[1].setBounds(btn[0].getX() + Settings.getButtonX(), (label.length * 25) + 45, Settings.getButtonX(), Settings.getButtonY());
		btn[1].setEnabled(false);
		btn[1].addActionListener(_ -> doUpdateEmployee(em));
		add(btn[1]);
		
		btn[2] = createButton("<html>MA<br>löschen</html>", ButtonIcon.UPDATE.icon(), null);
		btn[2].setBounds(btn[1].getX() + Settings.getButtonX(), (label.length * 25) + 45, Settings.getButtonX(), Settings.getButtonY());
		btn[2].setEnabled(false);
		btn[2].addActionListener(_ -> doDeleteEmployee(em));
		add(btn[2]);
    	
		loadData(em); // Daten laden
		
    	size.height = btn[0].getY() + btn[0].getHeight() + 20;
    	setPreferredSize(size);
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void loadData(Employee em) {
    	if (em.getId() != null) employee[0].setText(em.getId().toString());
    	if (em.getUserName() != null) employee[1].setText(em.getUserName());
    	if (em.getFirstName() != null) employee[2].setText(em.getFirstName());
    	if (em.getLastName() != null) employee[3].setText(em.getLastName());
    	if (em.getAddress() != null) employee[4].setText(em.getAddress());
    	if (em.getZip() != null) employee[5].setText(em.getZip());
    	if (em.getTown() != null) employee[6].setText(em.getTown());
    	if (em.getBirthday() != null) datum.setDate(em.getBirthday());
    	if (em.getInsuranceNo() != null) employee[8].setText(em.getInsuranceNo());
    	
    	boolean enbl = false;
    	if (em.getId() != null) enbl = true;
    	
    	btn[0].setEnabled(!enbl);
    	btn[1].setEnabled(enbl);
    	btn[2].setEnabled(enbl);
    	
    }
    
    private void doInsertEmployee(Employee em) {
    	if (employee[1].getText().isBlank() || employee[1].getText().isEmpty()) return;
    	if (employee[2].getText().isBlank() || employee[2].getText().isEmpty()) return;
    	
    	em.setUserName(employee[1].getText());
    	em.setFirstName(employee[2].getText());
    	em.setLastName(employee[3].getText());
    	em.setAddress(employee[4].getText());
    	em.setZip(employee[5].getText());
    	em.setTown(employee[6].getText());
    	em.setBirthday(datum.getDate());
    	em.setInsuranceNo(employee[8].getText());
    	emRepo.save(em); // Datensatz anlegen
    	
    	MainWindow.actScreen();
    }
    
    private void doUpdateEmployee(Employee em) {
    	if (employee[1].getText().isBlank() || employee[1].getText().isEmpty()) return;
    	if (employee[2].getText().isBlank() || employee[2].getText().isEmpty()) return;
    	
    	em.setUserName(employee[1].getText());
    	em.setFirstName(employee[2].getText());
    	em.setLastName(employee[3].getText());
    	em.setAddress(employee[4].getText());
    	em.setZip(employee[5].getText());
    	em.setTown(employee[6].getText());
    	em.setBirthday(datum.getDate());
    	em.setInsuranceNo(employee[8].getText());
    	emRepo.update(em); // Datensatz aktualisieren
    	
    	MainWindow.actScreen();
    }
    
    private void doDeleteEmployee(Employee em) {
    	emRepo.delete(em.getId()); // Datensatz löschen
    	
    	MainWindow.actScreen();
    }
    
    private void onChange() {
    	
    }
}
