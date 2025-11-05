package org.andy.fx.code.misc;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class TextHighlighting {
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void applyHighlighting(JTextPane textPane, String text) throws BadLocationException {
		applyHighlightText(textPane, text);
	}
	
	//###################################################################################################################################################
	// protected Teil
	//###################################################################################################################################################

	protected static void applyHighlightText(JTextPane textPane, String text) throws BadLocationException {

		StyledDocument doc = textPane.getStyledDocument();
		SimpleAttributeSet normalAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(normalAttr, Color.BLACK);

		SimpleAttributeSet highlightAttr = new SimpleAttributeSet();
		StyleConstants.setForeground(highlightAttr, Color.MAGENTA);

		int start = 0;
		while (start < text.length()) {
			int openBrace = text.indexOf('{', start);
			int closeBrace = text.indexOf('}', openBrace);

			if (openBrace == -1 || closeBrace == -1) {
				doc.insertString(doc.getLength(), text.substring(start), normalAttr);
				break;
			}

			// Normalen Text bis zur öffnenden Klammer einfügen
			if (openBrace > start) {
				doc.insertString(doc.getLength(), text.substring(start, openBrace), normalAttr);
			}

			// Markierten Text einfügen
			doc.insertString(doc.getLength(), text.substring(openBrace, closeBrace + 1), highlightAttr);

			// Startpunkt für nächste Suche setzen
			start = closeBrace + 1;
		}
	}

}
