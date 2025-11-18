package org.andy.code.misc;

import javax.swing.text.*;

public class CommaHelper {
    public static class CommaToDotFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String s, AttributeSet a) throws BadLocationException {
            if (s != null) s = s.replace(',', '.');
            super.insertString(fb, offset, s, a);
        }
        @Override
        public void replace(FilterBypass fb, int offset, int length, String s, AttributeSet a) throws BadLocationException {
            if (s != null) s = s.replace(',', '.');
            super.replace(fb, offset, length, s, a);
        }
    }
}
