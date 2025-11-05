package org.andy.fx.gui.misc;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CreateButton {
	
	private static final Logger logger = LogManager.getLogger(CreateButton.class);

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static JButton createButton(String btnText, ImageIcon icon, Color col) {
		return doButton(btnText, icon, col);
	}
	
	public static GradientButton createGradientButton(String text, ImageIcon icon, float[] fractions, Color[] colors, boolean vertical) {
		return doGradientButton(text, icon, fractions, colors, vertical);
	}

	//###################################################################################################################################################
	// protected Teil
	//###################################################################################################################################################
	
	protected static JButton doButton(String text, ImageIcon icon, Color col) {
		JButton button = new JButton();

		if (text != null && !text.isEmpty()) {
			button.setText(text);
			button.setToolTipText(text);
		}

		if (icon != null) {
			try {
				ImageIcon buttonIcon = icon;
				if (buttonIcon.getImage() != null) {
					button.setIcon(buttonIcon);
				}
			} catch (Exception e) {
				logger.error("doButton: Icon nicht gefunden: " + icon);
			}
		}
		
		if (col != null) {
			button.setBackground(col);
		}

		button.setIconTextGap(10);
		button.setFont(new Font("Tahoma", Font.BOLD, 11));
		button.setPreferredSize(new Dimension(130, 50));
		button.setEnabled(false);

		return button;
	}
	
	protected static GradientButton doGradientButton(String text, ImageIcon icon, float[] fractions, Color[] colors, boolean vertical) {
		GradientButton gradient = new GradientButton(text,fractions,colors,vertical);
		
		if (text != null && !text.isEmpty()) {
			gradient.setText(text);
			gradient.setToolTipText(text);
		}
		
		if (icon != null) {
			try {
				ImageIcon buttonIcon = icon;
				if (buttonIcon.getImage() != null) {
					gradient.setIcon(buttonIcon);
				}
			} catch (Exception e) {
				logger.error("doGradientButton: Icon nicht gefunden: " + icon);
			}
		}
		
		gradient.setIconTextGap(10);
		gradient.setFont(new Font("Tahoma", Font.BOLD, 11));
		
		return gradient;
	}

}
