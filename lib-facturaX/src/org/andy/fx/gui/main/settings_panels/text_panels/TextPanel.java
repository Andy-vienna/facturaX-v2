package org.andy.fx.gui.main.settings_panels.text_panels;

import java.awt.Color;
import java.awt.Font;

import javax.swing.*;
import javax.swing.border.TitledBorder;

public abstract class TextPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	protected JLabel label;

    public TextPanel(String titel) {
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

}

