package org.andy.fx.gui.main.overview_panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

public class SummenPanelB extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private JLabel[] lblSum = null;
	private JFormattedTextField[] txtSum = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	
    /** SummenPanel für n Summen (werden im Parameter 'anz' vorgegeben)
     * @param labels
     * 
     */
    public SummenPanelB(int anz, String[] labels, boolean[] sym) {
        setLayout(null);
        buildSumPanel(anz, labels, sym);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private void buildSumPanel(int anz, String[] labels, boolean[] sym) {
    	
    	int spaltenBreite = 200; int labelBreite = 80; int fieldBreite = 110; int feldHoehe = 25;
    	lblSum = new JLabel[anz]; txtSum = new JFormattedTextField[anz];
    	
    	if (labels.length < anz) { // Prüfung, ob genügend Label-Texte vorgegeben werden
    	    String[] tmp = new String[anz];
    	    System.arraycopy(labels, 0, tmp, 0, labels.length); // alte Labels übernehmen
    	    for (int i = labels.length; i < anz; i++) {
    	        tmp[i] = "no Text"; // fehlende Einträge mit Defaultwert füllen
    	    }
    	    labels = tmp;
    	}
				
		// Labels und Textfelder erstellen
    	for (int i = 0; i < lblSum.length; i++) {
    	    int spalte = i / 2;       // 0,1,2,...
    	    int zeile  = i % 2;       // 0 oder 1

    	    int xLabel = spalte * spaltenBreite + 5;
    	    int xField = spalte * spaltenBreite + 85;
    	    int y      = zeile * feldHoehe;

    	    lblSum[i] = new JLabel(labels[i]);
    	    lblSum[i].setBounds(xLabel, y, labelBreite, feldHoehe);
    	    add(lblSum[i]);

    	    txtSum[i] = makeField(xField, y, fieldBreite, feldHoehe, true, Color.LIGHT_GRAY, sym[i]);
    	    add(txtSum[i]);
    	}
		
		setPreferredSize(new Dimension(200 * ((anz + 1) / 2), 50));
		
	}
    
	//###################################################################################################################################################

    // Hilfsfunktion für Textfelder
    private JFormattedTextField makeField(int x, int y, int w, int h, boolean bold, Color bg, boolean sym) {
    	JFormattedTextField t = null;
        NumberFormatter formatter = new NumberFormatter(NumberFormat.getCurrencyInstance());
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        
        DecimalFormat df = new DecimalFormat("#0.00");
        df.setGroupingUsed(true); // Tausendertrennzeichen
        NumberFormatter nf = new NumberFormatter(df);
        nf.setValueClass(Double.class);
        nf.setAllowsInvalid(false); // nur gültige Eingaben zulassen

        if (sym) {
        	t = new JFormattedTextField(formatter);
        } else {
        	t = new JFormattedTextField(nf);
        }
        
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
    
    public void setTxtSum(int idx, Double value) {
		this.txtSum[idx].setValue(value);
	}

}
