package org.andy.fx.gui.main.result_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;

import org.andy.fx.code.dataExport.ExcelP109a;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.misc.BusyDialog;

public class SteuerPanel extends JPanel {

	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	// Felder als Instanzvariablen
    private JLabel[] lblP109aTexts = new JLabel[11];
    private JLabel lblGwbHinweis, lblVorGWB, lblGwbTotal;
    private JLabel[] lblGwbStufen = new JLabel[4];
    private JLabel lblE1Hinweis, lblE1VorSt, lblE1Text1, lblE1Text2, lblE1Summe;
    private JLabel[] lblE1Stufen = new JLabel[7];

    private JFormattedTextField[] txtP109aSVS = new JFormattedTextField[5]; // Quartale + Gesamt
    private JFormattedTextField txtP109aEin, txtP109aOeffiP, txtP109aAPausch, txtP109aExpenses, txtP109aGrundfrei, txtP109aErgebnis;
    private JFormattedTextField txtVorGWB, txtGwbTotal;
    private JFormattedTextField[] txtGwbStufen = new JFormattedTextField[4];
    private JFormattedTextField txtE1VorSt, txtE1Summe;
    private JFormattedTextField[] txtE1Stufen = new JFormattedTextField[7];
    private JFormattedTextField[] txtE1Tax = new JFormattedTextField[7];
    private JButton btnExportP109a;

    // Layout-Konstanten
    private final int iLeft = 10, iTop = 50, iWidth = 400, iHeight = 25;
    
    private ArrayList<BigDecimal> dataExcel = new ArrayList<>();
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public SteuerPanel() {
        setLayout(null);
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	
    	int fieldWidth = 150;
	    int left1 = iLeft + 400;
	    int left2 = iLeft + 560;
	    int left3 = iLeft + 720;
	    int left4 = iLeft + 880;
	    int left5 = iLeft + 1040;
	    int left6 = iLeft + 1300;
	    int left7 = iLeft + 1460;

        // Labels
        String[] labels = {
                "E/A-Rechnung ($109a Mitteilung) - " + Einstellungen.getAppSettings().year,
                "Einkünfte aus selbstständiger Tätigkeit",
                "SVS Vorschreibung Q1/" + Einstellungen.getAppSettings().year,
                "SVS Vorschreibung Q2/" + Einstellungen.getAppSettings().year,
                "SVS Vorschreibung Q3/" + Einstellungen.getAppSettings().year,
                "SVS Vorschreibung Q4/" + Einstellungen.getAppSettings().year,
                "50% Öffi-Pauschale",
                "großes Arbeitsplatzpauschale",
                "Betriebsausgaben netto",
                "Grundfreibetrag",
                "Einnahmenüberschuss"
        };
        for (int i = 0; i < labels.length; i++) {
	        lblP109aTexts[i] = new JLabel(labels[i]);
	        int y = (i == 0) ? 20 : iTop + (i - 1) * iHeight;
	        int h = iHeight;
	        int x = iLeft;
	        if (i == 0) h = iHeight;
	        if (i == 10) { y = 290; }
	        lblP109aTexts[i].setBounds(x, y, iWidth, h);
	        if (i == 0) lblP109aTexts[i].setFont(new Font("Tahoma", Font.BOLD, 11));
	        if (i == 10) {
	            lblP109aTexts[i].setFont(new Font("Tahoma", Font.BOLD, 11));
	            lblP109aTexts[i].setForeground(Color.BLUE);
	        }
	        add(lblP109aTexts[i]);
	    }

        // Textfelder Hauptbereich
        txtP109aEin = makeField(left2, iTop + 0 * iHeight, fieldWidth, iHeight, false, null);
        add(txtP109aEin);

        for (int i = 1; i <= 4; i++) {
            txtP109aSVS[i] = makeField(left1, iTop + i * iHeight, fieldWidth, iHeight, false, new Color(192, 255, 192));
            add(txtP109aSVS[i]);
        }
        txtP109aSVS[0] = makeField(left2, iTop + 4 * iHeight, fieldWidth, iHeight, false, new Color(192, 255, 192)); // Summe SVS
        add(txtP109aSVS[0]);

        txtP109aOeffiP    = makeField(left2, iTop + 5 * iHeight, fieldWidth, iHeight, false, null); add(txtP109aOeffiP);
        txtP109aAPausch   = makeField(left2, iTop + 6 * iHeight, fieldWidth, iHeight, false, null); add(txtP109aAPausch);
        txtP109aExpenses  = makeField(left2, iTop + 7 * iHeight, fieldWidth, iHeight, false, null); add(txtP109aExpenses);

        txtP109aGrundfrei = makeField(left2, iTop + 8 * iHeight, fieldWidth, iHeight, false, new Color(173, 216, 230)); add(txtP109aGrundfrei);

        txtP109aErgebnis  = makeField(left2, 290, fieldWidth, iHeight, true, new Color(255, 255, 204)); add(txtP109aErgebnis);

        // GWB-Bereich
        lblGwbHinweis = new JLabel("Berechnung Grundfreibetrag");
        lblGwbHinweis.setBounds(left3, 20, iWidth, iHeight);
        lblGwbHinweis.setFont(new Font("Tahoma", Font.BOLD, 11));
        add(lblGwbHinweis);

        lblVorGWB = new JLabel("Gewinn vor GWB");
        lblVorGWB.setBounds(left3, iTop + 0 * iHeight, iWidth, iHeight);
        add(lblVorGWB);

        String[] gwbStufLabels = {"bis § [&%]", "weitere § [&%]", "weitere § [&%]", "weitere § [&%]"};
        for (int i = 0; i < gwbStufLabels.length; i++) {
            lblGwbStufen[i] = new JLabel(gwbStufLabels[i]);
            lblGwbStufen[i].setBounds(left3, iTop + (i + 1) * iHeight, iWidth, iHeight);
            add(lblGwbStufen[i]);
        }
        lblGwbTotal = new JLabel("Summe GWB", new ImageIcon(SteuerPanel.class.getResource("/icons/panels/linkspfeil.png")), JLabel.LEFT);
        lblGwbTotal.setBounds(left3, iTop + 8 * iHeight, iWidth, iHeight);
        lblGwbTotal.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblGwbTotal.setForeground(Color.BLUE);
        add(lblGwbTotal);

        txtVorGWB = makeField(left4, iTop + 0 * iHeight, fieldWidth, iHeight, false, null); add(txtVorGWB);
        for (int i = 0; i < gwbStufLabels.length; i++) {
            txtGwbStufen[i] = makeField(left4, iTop + (i + 1) * iHeight, fieldWidth, iHeight, false, null); add(txtGwbStufen[i]);
        }
        txtGwbTotal = makeField(left4, iTop + 8 * iHeight, fieldWidth, iHeight, true, new Color(173, 216, 230)); add(txtGwbTotal);

        // E1-Bereich
        lblE1Hinweis = new JLabel("Berechnung Einkommensteuer");
        lblE1Hinweis.setBounds(left5, 20, iWidth, iHeight);
        lblE1Hinweis.setFont(new Font("Tahoma", Font.BOLD, 11));
        add(lblE1Hinweis);

        lblE1VorSt = new JLabel("Gewinn vor Steuer", new ImageIcon(SteuerPanel.class.getResource("/icons/panels/rechtspfeil.png")), JLabel.LEFT);
        lblE1VorSt.setBounds(left5, iTop + 0 * iHeight, iWidth, iHeight);
        lblE1VorSt.setFont(new Font("Tahoma", Font.BOLD, 11));
        add(lblE1VorSt);

        lblE1Text1 = new JLabel("Steuerstufen");
        lblE1Text1.setBounds(left6, iTop + 1 * iHeight, fieldWidth, iHeight);
        lblE1Text1.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblE1Text1.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblE1Text1);

        lblE1Text2 = new JLabel("Steuer aus Stufe");
        lblE1Text2.setBounds(left7, iTop + 1 * iHeight, fieldWidth, iHeight);
        lblE1Text2.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblE1Text2.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblE1Text2);

        String[] e1StufLabels = {
                "von $ bis § [&%]", "von $ bis § [&%]", "von $ bis § [&%]",
                "von $ bis § [&%]", "von $ bis § [&%]", "von $ bis § [&%]", "von $ bis § [&%]"
        };
        for (int i = 0; i < e1StufLabels.length; i++) {
            lblE1Stufen[i] = new JLabel(e1StufLabels[i]);
            lblE1Stufen[i].setBounds(left5, iTop + (i + 2) * iHeight, iWidth, iHeight);
            add(lblE1Stufen[i]);
        }

        lblE1Summe = new JLabel("vorauss. Einkommensteuer für das Jahr " + Einstellungen.getAppSettings().year,
        		new ImageIcon(SteuerPanel.class.getResource("/icons/panels/stern.png")), JLabel.LEFT);
        lblE1Summe.setBounds(left5, 290, iWidth, iHeight);
        lblE1Summe.setFont(new Font("Tahoma", Font.BOLD, 11));
        lblE1Summe.setForeground(Color.BLUE);
        add(lblE1Summe);

        txtE1VorSt = makeField(left6, iTop + 0 * iHeight, fieldWidth, iHeight, true, new Color(255, 255, 204)); add(txtE1VorSt);
        for (int i = 0; i < 7; i++) {
            txtE1Stufen[i] = makeField(left6, iTop + (i + 2) * iHeight, fieldWidth, iHeight, false, null); add(txtE1Stufen[i]);
            txtE1Tax[i]    = makeField(left7, iTop + (i + 2) * iHeight, fieldWidth, iHeight, false, null); add(txtE1Tax[i]);
        }
        txtE1Summe = makeField(left7, 290, fieldWidth, iHeight, true, null); add(txtE1Summe);

        // Separatoren
        int[] vertSepX = {iLeft + 395, left2 + fieldWidth + 5, left4 + fieldWidth + 5, left7 + fieldWidth + 5};
        for (int v = 0; v < vertSepX.length; v++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(vertSepX[v], 20, 3, 350);
            sep.setOrientation(SwingConstants.VERTICAL);
            add(sep);
        }
        int[] horSepY = {45, 280};
        for (int h = 0; h < horSepY.length; h++) {
        	JSeparator sep = new JSeparator();
        	sep.setForeground(Color.DARK_GRAY);
            sep.setBounds(iLeft, horSepY[h], left7 + fieldWidth + 5 - iLeft, 3);
            sep.setOrientation(SwingConstants.HORIZONTAL);
            add(sep);
        }

        // Export-Button
        btnExportP109a = createButton("<html>Export<br>§109a</html>", ButtonIcon.EXPORT.icon(), null);
        btnExportP109a.setEnabled(true);
        btnExportP109a.setBounds(4 * 130 + 60, 320, 130, 50);
        add(btnExportP109a);
        
        btnExportP109a.addActionListener(e -> {
    	    Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
    	    BusyDialog.run(w,
    	        "Bitte warten",
    	        "§109a Mitteilung wird erzeugt …",
    	        () -> {
    	        	btnExportP109a.setEnabled(false);
    				ExcelP109a.ExportP109a(dataExcel); // Daten an ExcelP109a übergeben
    				btnExportP109a.setEnabled(true);
				},
    	        HauptFenster::actScreen // Übersicht aktualisieren
    	    );
    	});

        setPreferredSize(new Dimension(left7 + fieldWidth + 30, 385));
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

	public void setTxtP109aSVS(int idx, Double value) {
		txtP109aSVS[idx].setValue(value);
	}

	public void setTxtP109aEin(Double value) {
		txtP109aEin.setValue(value);
	}

	public void setTxtP109aOeffiP(Double value) {
		txtP109aOeffiP.setValue(value);
	}

	public void setTxtP109aAPausch(Double value) {
		txtP109aAPausch.setValue(value);
	}

	public void setTxtP109aExpenses(Double value) {
		txtP109aExpenses.setValue(value);
	}

	public void setTxtP109aGrundfrei(Double value) {
		txtP109aGrundfrei.setValue(value);
	}

	public void setTxtP109aErgebnis(Double value) {
		txtP109aErgebnis.setValue(value);
	}

	public void setTxtVorGWB(Double value) {
		txtVorGWB.setValue(value);
	}

	public void setTxtGwbTotal(Double value) {
		txtGwbTotal.setValue(value);
	}

	public void setTxtGwbStufen(int idx, Double value) {
		txtGwbStufen[idx].setValue(value);
	}

	public void setTxtE1VorSt(Double value) {
		txtE1VorSt.setValue(value);
	}

	public void setTxtE1Summe(Double value) {
		txtE1Summe.setValue(value);
		if (value >= 0) {
			txtE1Summe.setBackground(new Color(192, 255, 192));
        } else {
        	txtE1Summe.setBackground(new Color(255, 182, 193));
		}
	}

	public void setTxtE1Stufen(int idx, Double value) {
		txtE1Stufen[idx].setValue(value);
	}

	public void setTxtE1Tax(int idx, Double value) {
		txtE1Tax[idx].setValue(value);
	}

	public String getLblGwbStufen(int idx) {
		return lblGwbStufen[idx].getText();
	}

	public void setLblGwbStufen(int idx, String text) {
		lblGwbStufen[idx].setText(text);
	}

	public String getLblE1Stufen(int idx) {
		return lblE1Stufen[idx].getText();
	}

	public void setLblE1Stufen(int idx, String text) {
		lblE1Stufen[idx].setText(text);
	}

	public void setDataExcel(ArrayList<BigDecimal> dataExcel) {
		this.dataExcel = dataExcel;
	}
    
}
