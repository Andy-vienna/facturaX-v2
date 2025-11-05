package org.andy.fx.gui.main.table_panels;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.andy.fx.gui.main.overview_panels.SummenPanelB;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;

public class ErzeugePanelB extends JPanel {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private JButton[] buttons;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public ErzeugePanelB(ErzeugeTabelle<Object> sPane, EditPanel editPanel, JButton[] buttons, SummenPanelB info) {
		this.buttons = buttons;
		buildPanel(sPane, editPanel, buttons, info);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void buildPanel(ErzeugeTabelle<Object> sPane, EditPanel editPanel, JButton[] buttons, SummenPanelB info) {

		// Hauptlayout für das Panel
	    this.setLayout(new GridBagLayout());

	    // 1. Tabelle
	    GridBagConstraints gbcTable = new GridBagConstraints();
	    gbcTable.gridx = 0;
	    gbcTable.gridy = 0;
	    gbcTable.gridwidth = 3;
	    gbcTable.weightx = 1.0;
	    gbcTable.weighty = 1.0;
	    gbcTable.fill = GridBagConstraints.BOTH;
	    gbcTable.insets = new Insets(5,5,5,5);
	    gbcTable.anchor = GridBagConstraints.NORTHWEST;
	    this.add(sPane, gbcTable);
	    
	    // 2. EditPanel
	    GridBagConstraints gbcEdit = new GridBagConstraints();
	    gbcEdit.gridx = 0;
	    gbcEdit.gridy = 1;
	    gbcEdit.gridwidth = 3;
	    gbcEdit.weightx = 1.0;
	    gbcEdit.weighty = 0.0;
	    gbcEdit.fill = GridBagConstraints.HORIZONTAL;
	    gbcEdit.insets = new Insets(5,5,5,5);
	    gbcEdit.anchor = GridBagConstraints.SOUTH;
	    this.add(editPanel, gbcEdit);

	    // 2. Dummy-Komponente (VerticalGlue)
	    GridBagConstraints gbcGlue = new GridBagConstraints();
	    gbcGlue.gridx = 0;
	    gbcGlue.gridy = 2;
	    gbcGlue.gridwidth = 3;
	    gbcGlue.weightx = 1.0;
	    gbcGlue.weighty = 1.0;
	    gbcGlue.fill = GridBagConstraints.VERTICAL;
	    gbcGlue.insets = new Insets(5,5,5,5);
	    this.add(Box.createVerticalGlue(), gbcGlue);

	    // 3. BottomPanel
	    GridBagConstraints gbcBottom = new GridBagConstraints();
	    gbcBottom.gridx = 0;
	    gbcBottom.gridy = 3;
	    gbcBottom.gridwidth = 3;
	    gbcBottom.weightx = 1.0;
	    gbcBottom.weighty = 0.0;
	    gbcBottom.fill = GridBagConstraints.HORIZONTAL;
	    gbcBottom.insets = new Insets(5,5,5,5);
	    gbcBottom.anchor = GridBagConstraints.SOUTH;
	    this.add(createBottomPanel(buttons, info), gbcBottom);
		
	}
	
	//###################################################################################################################################################
	
	private JPanel createBottomPanel(JButton[] buttons, SummenPanelB info) {
		int i = 0;

		// Buttons und InfoPanel in einem BottomPanel anordnen
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		
		if (buttons == null || buttons.length == 0) {
			gbc.gridx = i;
			gbc.weightx = 0;
			gbc.anchor = GridBagConstraints.EAST;
			panel.add(info, gbc);
			
			gbc.gridx = i + 1;
			gbc.weightx = 1.0;
			panel.add(Box.createHorizontalGlue(), gbc);
		} else {
			gbc.gridy = 0; gbc.weightx = 0; gbc.anchor = GridBagConstraints.WEST;
			for (i = 0; i < buttons.length; i++) {
				gbc.gridx = i;
				panel.add(buttons[i], gbc);
			}
					
			gbc.gridx = i + 1;
			gbc.weightx = 0;
			gbc.anchor = GridBagConstraints.EAST;
			panel.add(info, gbc);
			
			gbc.gridx = i + 2;
			gbc.weightx = 1.0;
			panel.add(Box.createHorizontalGlue(), gbc);
		}
		
		return panel;
	}
	
	public JButton[] getButtons() {
	    return buttons;
	}
	
}
