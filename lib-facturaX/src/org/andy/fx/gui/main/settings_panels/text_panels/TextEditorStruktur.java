package org.andy.fx.gui.main.settings_panels.text_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextEditorStruktur extends TextPanel {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TextEditorStruktur.class);
	
	private final String txtLabel = "Platzhalter | Zeilentext";
	private final List<JLabel> labelList = new ArrayList<>();
	private final List<JTextField> placeholderList = new ArrayList<>();
	private final List<JTextPane> textAreas = new ArrayList<>();
	private final List<JButton> updateButtons = new ArrayList<>();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public TextEditorStruktur() {
		super("");
		setBorder(BorderFactory.createEmptyBorder());
		setLayout(null);
		tp();
	}
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	private void tp() {
		setLayout(new GridBagLayout()); // Verwende GridBagLayout für flexible Anordnung
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.insets = new Insets(3, 3, 3, 3);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (int i = 0; i < 20; i++) {

			// ---- Labels hinzufügen (lbl = Beschriftungslabel, lblInf = unsichtbares Label für DB-Index)
			gbc.gridx = 0; // erste Spalte
			JLabel lbl = new JLabel(txtLabel);
			lbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
			lbl.setForeground(Color.BLACK);
			JLabel lblInf = new JLabel();
			lblInf.setVisible(false);
			labelList.add(lblInf); // Label zur Liste hinzufügen)
			gbc.weightx = 0.03;  // Label nimmt 6 % des Platzes
			gbc.weighty = 0;
			add(lbl, gbc);
			add(lblInf, gbc);
			
			// ---- TextFelder für Platzhaltertexte anlegen und darstellen
			gbc.gridx = 1; // Wechsel zur nächsten Spalte
			JTextField txtPlaceholder = new JTextField();
			txtPlaceholder.setBorder(new RoundedBorder(10));
			txtPlaceholder.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtPlaceholder.setColumns(12);
			txtPlaceholder.setForeground(Color.RED);
			
			placeholderList.add(txtPlaceholder); // TextField zur Liste hinzufügen
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.07; // Textfeld nimmt 89 % des Platzes
			gbc.weighty = 0;
			add(txtPlaceholder, gbc);
			
			// ---- TextPanes anlegen, in ScrollPanes einfügen und darstellen
			gbc.gridx = 2; // Wechsel zur nächsten Spalte
			JTextPane txtPane = new JTextPane(); // Verwende JTextPane statt JTextArea
			txtPane.setFont(new Font("Tahoma", Font.BOLD, 12));
			txtPane.setEditable(true);

			JScrollPane txtScroll = new JScrollPane(txtPane);
			txtScroll.setBorder(new RoundedBorder(10));
			textAreas.add(txtPane); // TextPane zur Liste hinzufügen
			gbc.fill = GridBagConstraints.BOTH;
			gbc.weightx = 0.85; // Textfeld nimmt 89 % des Platzes
			gbc.weighty = 0.2;
			add(txtScroll, gbc);

			// ---- Buttons anlegen und einfügen
			gbc.gridx = 3; // Wechsel zur nächsten Spalte
			JButton btnUpdateText = null;
			try {
				btnUpdateText = createButton("speichern", ButtonIcon.SAVE.icon(), null);
			} catch (RuntimeException e1) {
				logger.error("error creating button - " + e1);
			}
			updateButtons.add(btnUpdateText); // Button zur Liste hinzufügen
			gbc.weightx = 0.05; // Button nimmt 7 % des Platzes
			add(btnUpdateText, gbc);

			//------------------------------------------------------------------------------
			gbc.gridy++;   // Nächste Zeile
		}
		setSize(new Dimension(1850, 950));
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################
	
	public List<JButton> getButtons() {
	    return updateButtons;
	}

	public List<JTextField> getPlaceholderList() {
		return placeholderList;
	}

	public List<JTextPane> getTextAreas() {
		return textAreas;
	}

	public List<JLabel> getLabelList() {
		return labelList;
	}

}
