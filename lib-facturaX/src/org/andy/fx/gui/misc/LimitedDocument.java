package org.andy.fx.gui.misc;

import javax.swing.text.*;

public class LimitedDocument extends PlainDocument {
	private static final long serialVersionUID = 1L;
	
	private final int max;

    public LimitedDocument(int max) {
        this.max = max;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) return;
        if (getLength() + str.length() <= max) {
            super.insertString(offs, str, a);
        }
    }
}
