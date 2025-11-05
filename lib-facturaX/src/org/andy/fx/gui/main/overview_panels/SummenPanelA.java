package org.andy.fx.gui.main.overview_panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.text.NumberFormatter;

public class SummenPanelA extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private JLabel[] lblSum = new JLabel[2];
	private JLabel lblInfo = new JLabel();
	private JFormattedTextField[] txtSum = new JFormattedTextField[2];
	private JProgressBar progressBar = new JProgressBar();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	
    /** SummenPanel für 2 Summen und wahlweisen einem ProgessBar (rechts von den Summen)
     * @param labels
     * @param showBar
     */
    public SummenPanelA(String[] labels, boolean showBar) {
        setLayout(null);
        buildSumPanel(labels, showBar);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private void buildSumPanel(String[] labels, boolean showBar) {
    	if (labels[0] == null) return; // nichts anzeigen
    	
		// Labels und Textfelder erstellen
    	for (int i = 0; i < lblSum.length; i++) {
			lblSum[i] = new JLabel(labels[i]);
			lblSum[i].setBounds(5, 0 + (i * 25), 80, 25);
			add(lblSum[i]);
			
			txtSum[i] = makeField(85, 0 + (i * 25), 110, 25, true, Color.LIGHT_GRAY);
			add(txtSum[i]);
		}
		
		// Fortschrittsbalken
		if (showBar) {
			progressBar.setBounds(195, 2, 80, 46);
			progressBar.setStringPainted(false);
			progressBar.setOpaque(true);
			add(progressBar);
			
			lblInfo.setBounds(275, 0, 50, 50);
			lblInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
			lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
			lblInfo.setVerticalAlignment(SwingConstants.CENTER);
			add(lblInfo);
			
			setPreferredSize(new Dimension(325, 50));
		} else {
			setPreferredSize(new Dimension(195, 50));
		}
		
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
    
    public void setTxtSum(int idx, Double value) {
		this.txtSum[idx].setValue(value);
	}

	public void setProgressBar(int value) {
		this.progressBar.setValue(value);
		this.lblInfo.setText("<html>" + value + "%<br>offen</html>");
		if(value < 30) {
			progressBar.setForeground(Color.BLUE);
		}else {
			progressBar.setForeground(Color.RED);
		}
	}

}
