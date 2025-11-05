package org.andy.fx.gui.main.result_panels;

import javax.swing.*;
import org.andy.fx.code.main.Einstellungen;

import java.awt.*;

public class ZMeldungPanel extends JPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

    // Felder als Instanzvariablen
	private JTextField[][] txtFields = new JTextField[5][8]; // [row][col]
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public ZMeldungPanel() {
        setLayout(null);
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	
    	// Überschriften und Feldbeschriftungen
	    String[] rowLabels = {"Zusammenfassende Meldung Zeile 1", "Zusammenfassende Meldung Zeile 2",
	    		"Zusammenfassende Meldung Zeile 3", "Zusammenfassende Meldung Zeile 4", "Zusammenfassende Meldung Zeile 5"};
	    String[] colLabels = {"Q1", "Q2", "Q3", "Q4"};
	    String[] reasonLabels = {"USt.-ID", "Betrag", "USt.-ID", "Betrag", "USt.-ID", "Betrag", "USt.-ID", "Betrag"};

	    // Label Arrays
	    JLabel[] lblRows = new JLabel[rowLabels.length];
	    JLabel[] lblCols = new JLabel[colLabels.length];
	    JLabel[] lblReasons = new JLabel[reasonLabels.length];

	    int rows = rowLabels.length, cols = colLabels.length;

	    // Überschrift
	    JLabel lblTitle = new JLabel("Zusammenfassende Meldung ans Finanzamt");
	    lblTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblTitle.setBounds(10, 20, 400, 25);
	    add(lblTitle);

	    // Zeilenlabels
	    for (int r = 0; r < rows; r++) {
	        lblRows[r] = new JLabel(rowLabels[r]);
	        lblRows[r].setBounds(10, 75 + r * 25, 400, 25);
	        add(lblRows[r]);
	    }

	    // Spaltenlabels (Quartale + Jahr)
	    for (int c = 0; c < cols; c++) {
	        String colLabel = colLabels[c] + " - " + Einstellungen.getAppSettings().year;
	        lblCols[c] = new JLabel(colLabel);
	        lblCols[c].setFont(new Font("Tahoma", Font.BOLD, 11));
	        lblCols[c].setHorizontalAlignment(SwingConstants.CENTER);
	        lblCols[c].setBounds(405 + c * 320, 20, 320, 25);
	        add(lblCols[c]);
	    }
	    
	    // Feld-Legende
	    for (int c = 0; c < reasonLabels.length; c++) {
	    	lblReasons[c] = new JLabel(reasonLabels[c]);
	    	lblReasons[c].setFont(new Font("Tahoma", Font.BOLD, 11));
	    	lblReasons[c].setHorizontalAlignment(SwingConstants.CENTER);
            lblReasons[c].setBounds(410 + c * 160, 50, 150, 25);
            add(lblReasons[c]);
	    }
	    
        // Textfelder pro Zelle
        for (int r = 0; r < txtFields.length; r++) {
            for (int c = 0; c < txtFields[r].length; c++) {
            	txtFields[r][c] = makeTextField(410 + c * 160, 75 + r * 25, 150, 25, false, null);
            	if (c % 2 == 0) {
					// USt.-ID-Felder
					txtFields[r][c].setFocusable(true);
					txtFields[r][c].setFont(new Font("Tahoma", Font.BOLD, 11));
					txtFields[r][c].setHorizontalAlignment(SwingConstants.CENTER);
				} else {
					
				}
                add(txtFields[r][c]);
            }
        }
        
     // Separatoren
        int[] vertSepX = {405, 725, 1045, 1365};
        for (int v = 0; v < vertSepX.length; v++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(vertSepX[v], 25, 3, 7 * 25);
            sep.setOrientation(SwingConstants.VERTICAL);
            add(sep);
        }
        int[] horSepY = {45};
        for (int h = 0; h < horSepY.length; h++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(10, horSepY[h], 1685, 3);
            sep.setOrientation(SwingConstants.HORIZONTAL);
            add(sep);
        }

        setPreferredSize(new Dimension(1400, 260));
    }
    
	//###################################################################################################################################################

    // Hilfsfunktion für Textfelder
    private JTextField makeTextField(int x, int y, int w, int h, boolean bold, Color bg) {
        JTextField t = new JTextField();
        t.setBounds(x, y, w, h);
        t.setHorizontalAlignment(SwingConstants.RIGHT);
        t.setFocusable(false);
        if (bold) t.setFont(new Font("Tahoma", Font.BOLD, 11));
        if (bg != null) t.setBackground(bg);
        return t;
    }
    
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

    public void setTxtFields(int row, int col, String value) {
		txtFields[row][col].setText(value);
	}

}

