package org.andy.gui;

import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;

public enum StateIcon {
    PLAY("play.png"),
    PAUSE("pause.png"),
    STOP("stop.png");

	private static final String BASE = "/org/resources/icons/";
	private final Image image;

    StateIcon(String file) {
        URL url = StateIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.image = new ImageIcon(url).getImage();   // hier umstellen
    }

    // statt ImageIcon zurückgeben:
    public Image image() { 
        return image; 
    }

}
