package org.andy.fx.gui.main.overview_panels.edit_panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public abstract class EditPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JLabel label;

    public EditPanel(String titel) {
        this.setLayout(null);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                titel,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 12),
                Color.BLUE
            ));
    }

    // Optional: Methode für spätere Inhalte
    public abstract void initContent();
}

