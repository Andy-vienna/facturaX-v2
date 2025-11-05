package org.andy.versuche.gui;

import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.andy.fx.gui.main.HauptFenster;

public class VersuchPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Font font = new Font("Tahoma", Font.BOLD, 11);
	private final Color titleColor = Color.BLUE;
	
	private JButton[] btn = new JButton[3];
	private JLabel[] label = new JLabel[10];
	private JTextField[] txt = new JTextField[10];
	
	

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public VersuchPanel() {
		setLayout(null);
		TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY),
				"Versuche für kommende Funktionen");
		border.setTitleFont(font);
		border.setTitleColor(titleColor);
		border.setTitleJustification(TitledBorder.LEFT);
		border.setTitlePosition(TitledBorder.TOP);
		setBorder(border);

		buildPanel();
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private void buildPanel() {
		int x = 30, y = 20; // Variablen für automatische Positionierung
		int btnWidth = HauptFenster.getButtonx();
		int btnHeight = HauptFenster.getButtony();
		
		String[] lbl = { "ohne Funktion", "ohne Funktion", "ohne Funktion" };
		String[] txtlbl = { "txt[0]", "txt[1]", "txt[2]", "txt[3]", "txt[4]", "txt[5]", "txt[6]", "txt[7]", "txt[8]", "txt[9]" };
		
		for (int i = 0; i < btn.length; i++) {
			btn[i] = createButton(lbl[i], null, null);
			btn[i].setBounds(x, y + 25 + (i * 55), btnWidth, btnHeight);
			btn[i].setEnabled(true);
			add(btn[i]);
		}
		
		// -------------------------------------------------------------------------------------------
		btn[0].addActionListener(_ -> { doButton1(); });
		// -------------------------------------------------------------------------------------------
		btn[1].addActionListener(_ -> { doButton2(); });
		// -------------------------------------------------------------------------------------------
		btn[2].addActionListener(_ -> { doButton3(); });
		// -------------------------------------------------------------------------------------------
		
		for (int i = 0; i < label.length; i++) {
			label[i] = new JLabel(txtlbl[i]);
			label[i].setBounds(200, y + (i * 25), 100, 25);
			add(label[i]);
		}
		
		for (int i = 0; i < txt.length; i++) {
			txt[i] = new JTextField();
			txt[i].setBounds(300, y + (i * 25), 750, 25);
			add(txt[i]);
		}
		
	}
	
	// ###################################################################################################################################################
	
	void doButton1() {
		
	}
	
	void doButton2() {
		
	}

	void doButton3() {
	
	}
	
}
