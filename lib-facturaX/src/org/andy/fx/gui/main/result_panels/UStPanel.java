package org.andy.fx.gui.main.result_panels;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import org.andy.fx.code.main.Einstellungen;

import java.awt.*;
import java.text.NumberFormat;

public class UStPanel extends JPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;

    // Felder als Instanzvariablen
    private JFormattedTextField[][] txtFields = new JFormattedTextField[4][5]; // [row][col]
    private JFormattedTextField[] txtZahllast = new JFormattedTextField[5];    // Zahllast Q1-Q4, Jahr
    
    // Layout-Konstanten
    private final int iLeft = 10, iTop = 50, iWidth = 400, iHeight = 25;
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public UStPanel() {
        setLayout(null);
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	
    	// Überschriften und Feldbeschriftungen
	    String[] rowLabels = {
	        "Kz.000 - Gesamtbetrag der Bemessungsgrundlage (ohne USt.)",
	        "Kz.021 - Innergemeinschaftliche sonstige Leistungen (z.B. DE B2B)",
	        "Kz.022 - zu versteuern mit Normalsteuersatz 20%",
	        "Kz.060 - Gesamtbetrag der Vorsteuern"
	    };
	    String[] colLabels = {"Q1", "Q2", "Q3", "Q4", "U1"};

	    // Label Arrays
	    JLabel[] lblRows = new JLabel[rowLabels.length];
	    JLabel[] lblCols = new JLabel[colLabels.length];

	    // Positionierung
	    int left = iLeft, top = iTop;
	    int labelWidth = iWidth;
	    int cellWidth = 150, cellHeight = iHeight;
	    int labelStartX = left;
	    int cellStartX = left + 400;
	    int cellStepX = 160; // (cellWidth + 10)
	    int rows = rowLabels.length, cols = colLabels.length;

	    // Überschrift
	    JLabel lblTitle = new JLabel("Umsatzsteuer-Voranmeldung (UVA)");
	    lblTitle.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblTitle.setBounds(labelStartX, 20, labelWidth, cellHeight);
	    add(lblTitle);

	    // Zeilenlabels
	    for (int r = 0; r < rows; r++) {
	        lblRows[r] = new JLabel(rowLabels[r]);
	        lblRows[r].setBounds(labelStartX, top + r * cellHeight, labelWidth, cellHeight);
	        add(lblRows[r]);
	    }

	    // Spaltenlabels (Quartale + Jahr)
	    for (int c = 0; c < cols; c++) {
	        String colLabel = colLabels[c] + " - " + Einstellungen.getAppSettings().year;
	        lblCols[c] = new JLabel(colLabel);
	        lblCols[c].setFont(new Font("Tahoma", Font.BOLD, 11));
	        lblCols[c].setHorizontalAlignment(SwingConstants.CENTER);
	        lblCols[c].setBounds(cellStartX + c * cellStepX, 20, cellWidth, cellHeight);
	        add(lblCols[c]);
	    }
	    
	    // Zahllast-Label und Textfelder
	    JLabel lblZahllast = new JLabel("ermittelte Zahllast (neg. Beträge bedeuten eine Gutschrift)");
	    lblZahllast.setFont(new Font("Tahoma", Font.BOLD, 11));
	    lblZahllast.setForeground(Color.BLACK);
	    lblZahllast.setBounds(labelStartX, 215, labelWidth, cellHeight);
	    add(lblZahllast);

        // Textfelder pro Zelle
        for (int r = 0; r < txtFields.length; r++) {
            for (int c = 0; c < txtFields[r].length; c++) {
                txtFields[r][c] = makeField(iLeft + 400 + c * 160, iTop + r * iHeight, 150, iHeight, false, null);
                add(txtFields[r][c]);
            }
        }

        for (int c = 0; c < txtZahllast.length; c++) {
            txtZahllast[c] = makeField(iLeft + 400 + c * 160, iTop + 6 * iHeight + 10, 150, iHeight, true, null);
            txtZahllast[c].setForeground(Color.BLACK);
            add(txtZahllast[c]);
        }
        
     // Separatoren
        int[] vertSepX = {iLeft + 395, iLeft + 400 + 150 + 5, iLeft + 560 + 150 + 5, iLeft + 720 + 150 + 5, iLeft + 880 + 150 + 5};
        for (int v = 0; v < vertSepX.length; v++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(vertSepX[v], 25, 3, 6 * iHeight + 60);
            sep.setOrientation(SwingConstants.VERTICAL);
            add(sep);
        }
        int[] horSepY = {iTop - 5, iTop + 5 + (6 * iHeight)};
        for (int h = 0; h < horSepY.length; h++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(iLeft, horSepY[h], iLeft + 1040 + 150 - 5, 3);
            sep.setOrientation(SwingConstants.HORIZONTAL);
            add(sep);
        }

        setPreferredSize(new Dimension(iLeft + 1040 + 150 + 10, iTop + 6 * iHeight + 80));
    }
    
	//###################################################################################################################################################

    // Hilfsfunktion für Textfelder
    private JFormattedTextField makeField(int x, int y, int w, int h, boolean bold, Color bg) {
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getCurrencyInstance());
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        JFormattedTextField t = new JFormattedTextField(formatter);
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

    public void setFieldValue(int row, int col, double value) {
        txtFields[row][col].setValue(value);
    }
    
    public void setZahllast(int col, double value) {
        txtZahllast[col].setValue(value);
        if (value < 0) {
        	txtZahllast[col].setBackground(new Color(192, 255, 192));
        } else {
			txtZahllast[col].setBackground(new Color(255, 182, 193));
		}
    }

}

