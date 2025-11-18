package org.andy.gui.iconHandler;

import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum MenuIcon {
    ACT("act.png"),
    EXIT("exit.png"),
    INFO("info.png");

	private static final String BASE = "/org/resources/icons/menu/";
    private final ImageIcon icon;
    
    MenuIcon(String file) {
        URL url = MenuIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.icon = new ImageIcon(url);
    }

    public ImageIcon icon() { return icon; }

    public static Icon byKey(String key) {
        return valueOf(key.toUpperCase(Locale.ROOT)).icon();
    }
}

