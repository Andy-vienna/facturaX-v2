package org.andy.gui.misc;

import java.awt.*;

public class WrapLayout extends FlowLayout {

    private static final long serialVersionUID = 1L;

    public WrapLayout() { super(); }
    public WrapLayout(int align) { super(align); }
    public WrapLayout(int align, int hgap, int vgap) { super(align, hgap, vgap); }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    @Override
    public void layoutContainer(Container target) {
        synchronized (target.getTreeLock()) {
            Insets insets = target.getInsets();
            int maxWidth = Math.max(0,
                    target.getWidth() - (insets.left + insets.right + getHgap() * 2));
            int hgap = getHgap();
            int vgap = getVgap();

            boolean ltr = target.getComponentOrientation().isLeftToRight();

            int x = insets.left + hgap;
            int y = insets.top + vgap;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;

                Dimension d = m.getPreferredSize();

                // Wrap wenn nächste Komponente nicht mehr in die Zeile passt
                if (x > insets.left + hgap && x + d.width > insets.left + hgap + maxWidth) {
                    // neue Zeile
                    x = insets.left + hgap;
                    y += rowHeight + vgap;
                    rowHeight = 0;
                }

                int compX = ltr ? x : (target.getWidth() - insets.right - hgap - d.width);
                int compY = y; // WICHTIG: top-align, KEIN (rowHeight - d.height)/2

                m.setBounds(compX, compY, d.width, d.height);

                x += d.width + hgap;
                rowHeight = Math.max(rowHeight, d.height);
            }
        }
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getSize().width;
            if (targetWidth <= 0) targetWidth = Integer.MAX_VALUE;

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int maxWidth = targetWidth - (insets.left + insets.right + hgap * 2);

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (!m.isVisible()) continue;

                Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

                if (rowWidth > 0 && rowWidth + d.width > maxWidth) {
                    dim.width = Math.max(dim.width, rowWidth);
                    dim.height += rowHeight + vgap;
                    rowWidth = 0;
                    rowHeight = 0;
                }

                rowWidth += d.width + (rowWidth > 0 ? hgap : 0);
                rowHeight = Math.max(rowHeight, d.height);
            }

            dim.width = Math.max(dim.width, rowWidth);
            dim.height += rowHeight;

            dim.width += insets.left + insets.right + hgap * 2;
            dim.height += insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }
}
