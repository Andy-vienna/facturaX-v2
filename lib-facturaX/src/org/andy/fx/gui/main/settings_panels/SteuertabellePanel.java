package org.andy.fx.gui.main.settings_panels;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToIntSafe;
import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityMaster.Tax;
import org.andy.fx.code.dataStructure.repositoryMaster.TaxRepository;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public class SteuertabellePanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
    private TaxRepository taxRepository = new TaxRepository();
    private List<Tax> taxListe = new ArrayList<>();
    private Tax leer = new Tax();
	
	private final Font font = new Font("Tahoma", Font.BOLD, 11);
    private final Color titleColor = Color.BLUE;
    
    private JComboBox<String> cmbSelect;
    private JTextField txtJahr = new JTextField();
    private final JTextField[] txtFields = new JTextField[21];
    private final JTextField[] txtFieldsP = new JTextField[2];
    private final JButton[] btnFields = new JButton[2];
	
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public SteuertabellePanel() {
        setLayout(null);
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Steuertabelle");
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
    	
    	JLabel lblJahr = new JLabel("Jahr");
    	lblJahr.setBounds(x, y, 120, 25);
    	lblJahr.setFont(font);
    	add(lblJahr);
    	x = lblJahr.getY() + lblJahr.getWidth();
    	y = 20;
    	
    	taxListe.clear();
        taxListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        taxListe.addAll(taxRepository.findAll());
        String[] taxYear = taxListe.stream()
        	    .map(t -> t.getYear() == 0 ? "" : String.valueOf(t.getYear()))
        	    .toArray(String[]::new);
        cmbSelect = new JComboBox<>(taxYear);
        cmbSelect.setBounds(x, y, 140, 25);
        cmbSelect.addActionListener(cmbListener);
        add(cmbSelect);
        x = 10;
        y = cmbSelect.getY() + cmbSelect.getHeight();
        
        txtJahr = makeField(310, 20, 130, 25, true, null);
        txtJahr.setVisible(false);
        add(txtJahr);
        
        JLabel lblSteuertabelle = new JLabel("Steuertabelle");
        lblSteuertabelle.setBounds(x, y, 120, 25);
        lblSteuertabelle.setFont(font);
    	add(lblSteuertabelle);
    	x = 10;
    	y = lblSteuertabelle.getY() + lblSteuertabelle.getHeight();
    	
        String[] labels = { "von", "bis", "Steuersatz" };
        JLabel[] lblFields = new JLabel[labels.length];
        
        for (int i = 0; i < labels.length; i++) {
            lblFields[i] = new JLabel(labels[i]);
            lblFields[i].setBounds(x + i * 100, y, 100, 25);
            lblFields[i].setHorizontalAlignment(SwingConstants.CENTER);
            add(lblFields[i]);
        }
        x = 10;
        y = lblFields[lblFields.length - 1].getY() + lblFields[lblFields.length - 1].getHeight();
        
        for (int i = 0; i < txtFields.length; i++) {
        	if (i < 3) {
        		txtFields[i] = makeField(x + i * 100, y, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 2 && i < 6) {
        		txtFields[i] = makeField(x + (i - 3) * 100, y + 25, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 5 && i < 9) {
        		txtFields[i] = makeField(x + (i - 6) * 100, y + 50, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 8 && i < 12) {
        		txtFields[i] = makeField(x + (i - 9) * 100, y + 75, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 11 && i < 15) {
        		txtFields[i] = makeField(x + (i - 12) * 100, y + 100, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 14 && i < 18) {
        		txtFields[i] = makeField(x + (i - 15) * 100, y + 125, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	if (i > 17) {
        		txtFields[i] = makeField(x + (i - 18) * 100, y + 150, 100, 25, false, null);
                add(txtFields[i]);
        	}
        	attachCommaToDot(txtFields[i]);
            
        }
        x = 10;
        y = txtFields[txtFields.length - 1].getY() + txtFields[txtFields.length - 1].getHeight();
        
        JLabel lblPauschalen = new JLabel("Pauschalen");
        lblPauschalen.setBounds(10, y, 120, 25);
        lblPauschalen.setFont(font);
    	add(lblPauschalen);
    	x = 10;
    	y = lblPauschalen.getY() + lblPauschalen.getHeight();
        
        String[] labelsP = { "Öffi-Pauschale", "großes Arbeitsplatzpausch." };
        JLabel[] lblFieldsP = new JLabel[labelsP.length];
        
        for (int i = 0; i < labelsP.length; i++) {
            lblFieldsP[i] = new JLabel(labelsP[i]);
            lblFieldsP[i].setBounds(x, y + i * 25, 200, 25);
            add(lblFieldsP[i]);
        }
        x = lblFieldsP[lblFieldsP.length - 1].getX() + lblFieldsP[lblFieldsP.length - 1].getWidth();
        y = lblPauschalen.getY() + lblPauschalen.getHeight();
        
        for (int i = 0; i < txtFieldsP.length; i++) {
        	txtFieldsP[i] = makeField(x, y + i * 25, 100, 25, false, null);
            add(txtFieldsP[i]);
            attachCommaToDot(txtFieldsP[i]);
        }
        x = txtFieldsP[txtFieldsP.length - 1].getX() + txtFieldsP[txtFieldsP.length - 1].getWidth();
        y = txtFieldsP[0].getY();
        
		btnFields[0] = createButton("<html>Jahr anlegen</html>", null, null);
		btnFields[1] = createButton("<html>Tabelle<br>updaten</html>", ButtonIcon.UPDATE.icon(), null);

        btnFields[0].setEnabled(true);
        btnFields[0].setVisible(false);
        btnFields[1].setEnabled(false);
		btnFields[0].setBounds(x, 45, btnWidth, btnHeight);
		btnFields[1].setBounds(x, y, btnWidth, btnHeight);
		btnFields[0].addActionListener(btn0Listener);
		btnFields[1].addActionListener(btn1Listener);
		add(btnFields[0]);
		add(btnFields[1]);
        
        x = btnFields[btnFields.length - 1].getX() + btnFields[btnFields.length - 1].getWidth() + 10;
        y = txtFieldsP[txtFieldsP.length - 1].getY() + txtFieldsP[txtFieldsP.length - 1].getHeight() + 20;

        setPreferredSize(new Dimension(x, y));
        
        noFocusFields(txtFields);
        noFocusFields(txtFieldsP);
    }
        
        
    //###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private final ActionListener cmbListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int idx = cmbSelect.getSelectedIndex();
            Tax tax = taxListe.get(idx);

            if (idx == 0) {
                txtJahr.setVisible(true);
                btnFields[0].setVisible(true);
                btnFields[1].setEnabled(false);
                clearFields(txtFields);
                clearFields(txtFieldsP);
                noFocusFields(txtFields);
                noFocusFields(txtFieldsP);
            } else {
            	txtJahr.setVisible(false);
                btnFields[0].setVisible(false);
                btnFields[1].setEnabled(true);
                txtFields[0].setText(tax.getVon_1().toString());
                txtFields[1].setText(tax.getBis_1().toString());
                txtFields[2].setText(tax.getTax_1().toString());
                txtFields[3].setText(tax.getVon_2().toString());
                txtFields[4].setText(tax.getBis_2().toString());
                txtFields[5].setText(tax.getTax_2().toString());
                txtFields[6].setText(tax.getVon_3().toString());
                txtFields[7].setText(tax.getBis_3().toString());
                txtFields[8].setText(tax.getTax_3().toString());
                txtFields[9].setText(tax.getVon_4().toString());
                txtFields[10].setText(tax.getBis_4().toString());
                txtFields[11].setText(tax.getTax_4().toString());
                txtFields[12].setText(tax.getVon_5().toString());
                txtFields[13].setText(tax.getBis_5().toString());
                txtFields[14].setText(tax.getTax_5().toString());
                txtFields[15].setText(tax.getVon_6().toString());
                txtFields[16].setText(tax.getBis_6().toString());
                txtFields[17].setText(tax.getTax_6().toString());
                txtFields[18].setText(tax.getVon_7().toString());
                txtFields[19].setText(tax.getBis_7().toString());
                txtFields[20].setText(tax.getTax_7().toString());
                txtFieldsP[0].setText(tax.getOeP().toString());
                txtFieldsP[1].setText(tax.getApP().toString());
                focusFields(txtFields);
                focusFields(txtFieldsP);
            }
        }
    };
    
    private final ActionListener btn0Listener = new ActionListener() { // neues Jahr Button
    	@Override
        public void actionPerformed(ActionEvent actionEvent) {
    		fillFields(txtFields);
    		fillFields(txtFieldsP);
    		Tax tax = new Tax();
    		tax.setYear(parseStringToIntSafe(txtJahr.getText()));
    		tax.setVon_1(parseStringToBigDecimalSafe(txtFields[0].getText(), LocaleFormat.AUTO));
    		tax.setBis_1(parseStringToBigDecimalSafe(txtFields[1].getText(), LocaleFormat.AUTO));
    		tax.setTax_1(parseStringToBigDecimalSafe(txtFields[2].getText(), LocaleFormat.AUTO));
    		tax.setVon_2(parseStringToBigDecimalSafe(txtFields[3].getText(), LocaleFormat.AUTO));
    		tax.setBis_2(parseStringToBigDecimalSafe(txtFields[4].getText(), LocaleFormat.AUTO));
    		tax.setTax_2(parseStringToBigDecimalSafe(txtFields[5].getText(), LocaleFormat.AUTO));
    		tax.setVon_3(parseStringToBigDecimalSafe(txtFields[6].getText(), LocaleFormat.AUTO));
    		tax.setBis_3(parseStringToBigDecimalSafe(txtFields[7].getText(), LocaleFormat.AUTO));
    		tax.setTax_3(parseStringToBigDecimalSafe(txtFields[8].getText(), LocaleFormat.AUTO));
    		tax.setVon_4(parseStringToBigDecimalSafe(txtFields[9].getText(), LocaleFormat.AUTO));
    		tax.setBis_4(parseStringToBigDecimalSafe(txtFields[10].getText(), LocaleFormat.AUTO));
    		tax.setTax_4(parseStringToBigDecimalSafe(txtFields[11].getText(), LocaleFormat.AUTO));
    		tax.setVon_5(parseStringToBigDecimalSafe(txtFields[12].getText(), LocaleFormat.AUTO));
    		tax.setBis_5(parseStringToBigDecimalSafe(txtFields[13].getText(), LocaleFormat.AUTO));
    		tax.setTax_5(parseStringToBigDecimalSafe(txtFields[14].getText(), LocaleFormat.AUTO));
    		tax.setVon_6(parseStringToBigDecimalSafe(txtFields[15].getText(), LocaleFormat.AUTO));
    		tax.setBis_6(parseStringToBigDecimalSafe(txtFields[16].getText(), LocaleFormat.AUTO));
    		tax.setTax_6(parseStringToBigDecimalSafe(txtFields[17].getText(), LocaleFormat.AUTO));
    		tax.setVon_7(parseStringToBigDecimalSafe(txtFields[18].getText(), LocaleFormat.AUTO));
    		tax.setBis_7(parseStringToBigDecimalSafe(txtFields[19].getText(), LocaleFormat.AUTO));
    		tax.setTax_7(parseStringToBigDecimalSafe(txtFields[20].getText(), LocaleFormat.AUTO));
    		tax.setOeP(parseStringToBigDecimalSafe(txtFieldsP[0].getText(), LocaleFormat.AUTO));
    		tax.setApP(parseStringToBigDecimalSafe(txtFieldsP[1].getText(), LocaleFormat.AUTO));
    		
    		taxRepository.insert(tax);
    		rebuild();
    	}
    };
    
    private final ActionListener btn1Listener = new ActionListener() { // Update-Button
    	@Override
        public void actionPerformed(ActionEvent actionEvent) {
    		Tax tax = new Tax();
    		tax.setYear(parseStringToIntSafe((String) cmbSelect.getSelectedItem()));
    		tax.setVon_1(parseStringToBigDecimalSafe(txtFields[0].getText(), LocaleFormat.AUTO));
    		tax.setBis_1(parseStringToBigDecimalSafe(txtFields[1].getText(), LocaleFormat.AUTO));
    		tax.setTax_1(parseStringToBigDecimalSafe(txtFields[2].getText(), LocaleFormat.AUTO));
    		tax.setVon_2(parseStringToBigDecimalSafe(txtFields[3].getText(), LocaleFormat.AUTO));
    		tax.setBis_2(parseStringToBigDecimalSafe(txtFields[4].getText(), LocaleFormat.AUTO));
    		tax.setTax_2(parseStringToBigDecimalSafe(txtFields[5].getText(), LocaleFormat.AUTO));
    		tax.setVon_3(parseStringToBigDecimalSafe(txtFields[6].getText(), LocaleFormat.AUTO));
    		tax.setBis_3(parseStringToBigDecimalSafe(txtFields[7].getText(), LocaleFormat.AUTO));
    		tax.setTax_3(parseStringToBigDecimalSafe(txtFields[8].getText(), LocaleFormat.AUTO));
    		tax.setVon_4(parseStringToBigDecimalSafe(txtFields[9].getText(), LocaleFormat.AUTO));
    		tax.setBis_4(parseStringToBigDecimalSafe(txtFields[10].getText(), LocaleFormat.AUTO));
    		tax.setTax_4(parseStringToBigDecimalSafe(txtFields[11].getText(), LocaleFormat.AUTO));
    		tax.setVon_5(parseStringToBigDecimalSafe(txtFields[12].getText(), LocaleFormat.AUTO));
    		tax.setBis_5(parseStringToBigDecimalSafe(txtFields[13].getText(), LocaleFormat.AUTO));
    		tax.setTax_5(parseStringToBigDecimalSafe(txtFields[14].getText(), LocaleFormat.AUTO));
    		tax.setVon_6(parseStringToBigDecimalSafe(txtFields[15].getText(), LocaleFormat.AUTO));
    		tax.setBis_6(parseStringToBigDecimalSafe(txtFields[16].getText(), LocaleFormat.AUTO));
    		tax.setTax_6(parseStringToBigDecimalSafe(txtFields[17].getText(), LocaleFormat.AUTO));
    		tax.setVon_7(parseStringToBigDecimalSafe(txtFields[18].getText(), LocaleFormat.AUTO));
    		tax.setBis_7(parseStringToBigDecimalSafe(txtFields[19].getText(), LocaleFormat.AUTO));
    		tax.setTax_7(parseStringToBigDecimalSafe(txtFields[20].getText(), LocaleFormat.AUTO));
    		tax.setOeP(parseStringToBigDecimalSafe(txtFieldsP[0].getText(), LocaleFormat.AUTO));
    		tax.setApP(parseStringToBigDecimalSafe(txtFieldsP[1].getText(), LocaleFormat.AUTO));
    		
    		taxRepository.update(tax);
    		rebuild();
    	}
    };
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private void rebuild() {
    	remove(cmbSelect);
    	taxListe.clear();
        taxListe.add(leer); // falls du immer einen Dummy-Eintrag vorne willst        
        taxListe.addAll(taxRepository.findAll());
        String[] taxYear = taxListe.stream()
        	    .map(t -> t.getYear() == 0 ? "" : String.valueOf(t.getYear()))
        	    .toArray(String[]::new);
        cmbSelect = new JComboBox<>(taxYear);
        cmbSelect.setBounds(130, 20, 140, 25);
        cmbSelect.addActionListener(cmbListener);
        add(cmbSelect);
        txtJahr.setVisible(false);
        btnFields[0].setVisible(false);
        btnFields[1].setEnabled(false);
        clearFields(txtFields);
        clearFields(txtFieldsP);
        noFocusFields(txtFields);
        noFocusFields(txtFieldsP);
        revalidate();
        repaint();
    }
    
    //###################################################################################################################################################

    private JTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(true);
        if (bold) t.setFont(font);
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }
        
    //###################################################################################################################################################

    private void clearFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("");
    }
    
    //###################################################################################################################################################

    private void fillFields(JTextField[] fields) {
        for (JTextField f : fields) f.setText("0.00");
    }
    
    //###################################################################################################################################################

    private void noFocusFields(JTextField[] fields) {
        for (JTextField f : fields) f.setFocusable(false);
    }
    
    //###################################################################################################################################################

    private void focusFields(JTextField[] fields) {
        for (JTextField f : fields) f.setFocusable(true);
    }

}
