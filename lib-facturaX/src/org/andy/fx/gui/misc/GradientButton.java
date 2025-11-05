package org.andy.fx.gui.misc;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;

public class GradientButton extends JButton {
    
	private static final long serialVersionUID = 1L;
	
	private final float[] fractions;
    private final Color[] colors;
    private final boolean vertical;

    public GradientButton(String text, float[] fractions, Color[] colors, boolean vertical) {
        super(text);
        this.fractions = fractions;
        this.colors = colors;
        this.vertical = vertical;
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        int w = getWidth(), h = getHeight();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Verlauf: mehrere Farben möglich
        LinearGradientPaint lg = new LinearGradientPaint(
                0, 0, vertical ? 0 : w, vertical ? h : 0,
                fractions, colors);

        // leichte Zustandsabdunklung bei gedrückt/hover
        ButtonModel m = getModel();
        float stateMul = m.isPressed() ? 0.85f : (m.isRollover() ? 1.05f : 1.0f);
        Color[] adj = new Color[colors.length];
        for (int i = 0; i < colors.length; i++) {
            int r = Math.min(255, Math.round(colors[i].getRed() * stateMul));
            int gC = Math.min(255, Math.round(colors[i].getGreen() * stateMul));
            int b = Math.min(255, Math.round(colors[i].getBlue() * stateMul));
            adj[i] = new Color(r, gC, b);
        }
        lg = new LinearGradientPaint(0, 0, vertical ? 0 : w, vertical ? h : 0, fractions, adj);

        Shape bg = new RoundRectangle2D.Float(0, 0, w, h, 14, 14);
        g2.setPaint(lg);
        g2.fill(bg);

        // optional: dezenter Rand
        g2.setColor(new Color(0, 0, 0, 40));
        g2.draw(bg);

        // Text
        super.paintComponent(g2);
        g2.dispose();
    }

    @Override
    public void updateUI() {
        // verhindert Look&Feel-Übermalung
        setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }

}
