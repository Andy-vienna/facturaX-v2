package org.andy.gui.misc;

import javax.swing.JProgressBar;
import java.awt.*;
import javax.swing.SwingConstants; 

public class MarkerProgressBar extends JProgressBar {

	private static final long serialVersionUID = 1L;
	// Private Variable zur Speicherung der Marker-Farbe
    private Color markerColor = Color.BLUE; // Standardfarbe: Rot

    // Konstruktor beibehalten
    public MarkerProgressBar() {
        setOpaque(true);
    }

    /**
     * Setzt die Farbe für den einzelnen Marker-Strich und fordert eine Neuzeichnung an.
     * @param color Die neue Marker-Farbe
     */
    public void setMarkerColor(Color color) {
        this.markerColor = color;
        // Wichtig: repaint() aufrufen, damit die Komponente neu gezeichnet wird
        repaint(); 
    }
    
    /**
     * Gibt die aktuelle Marker-Farbe zurück.
     */
    public Color getMarkerColor() {
        return this.markerColor;
    }


    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // 1. Hintergrund zeichnen (Graue Füllung der Bar)
        int barWidth = getWidth();
        int barHeight = getHeight();
        g2d.setColor(new Color(224,224,224)); 
        g2d.fillRect(0, 0, barWidth, barHeight);

        int min = getMinimum();
        int max = getMaximum();
        int current = getValue();
        
        int markerThickness = 3; 

        if (max > min) {
            double percent = (double) (current - min) / (max - min);
            
            // !!! HIER: Verwenden der steuerbaren Farbe !!!
            g2d.setColor(this.markerColor); 

            if (getOrientation() == SwingConstants.HORIZONTAL) {
                // --- HORIZONTALE AUSRICHTUNG ---
                int markerX = (int) (barWidth * percent);
                g2d.fillRect(markerX - markerThickness / 2, 0, markerThickness, barHeight);
                
            } else if (getOrientation() == SwingConstants.VERTICAL) {
                // --- VERTIKALE AUSRICHTUNG ---
                int markerY = (int) (barHeight * (1.0 - percent));
                g2d.fillRect(0, markerY - markerThickness / 2, barWidth, markerThickness);
            }
        }
    }
}