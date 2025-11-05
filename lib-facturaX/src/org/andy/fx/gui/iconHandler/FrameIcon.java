package org.andy.fx.gui.iconHandler;

import java.awt.Image;
import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum FrameIcon {
	AIPFEIL("aipfeil.png"),
	EDIT("edit.png"),
    FILE("file.png"),
    ICON("icon.png"),
    IDEE("idee.png"),
    RUFZEICHEN("rufzeichen.png");

	private static final String BASE = "/org/resources/icons/frames/";
    private final ImageIcon icon;
    
    FrameIcon(String file) {
        URL url = FrameIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.icon = new ImageIcon(url);
    }

    public Icon icon() { return icon; }
    public Image image() { return icon.getImage(); }

    public static Icon byKey(String key) {
        return valueOf(key.toUpperCase(Locale.ROOT)).icon();
    }
}

