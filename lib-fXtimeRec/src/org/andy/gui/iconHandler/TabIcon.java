package org.andy.gui.iconHandler;

import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum TabIcon {
    OFFER("offer.png"),
    INVOICE("invoice.png"),
    ORDER("bestellen.png"),
    DELIVERY_NOTE("lieferschein.png"),
    PURCHASE("purchase.png"),
    EXPENSES("expenses.png"),
    TRAVEL("travel.png"),
    TAX("tax.png"),
    RESULT("result.png"),
    TIME("zeiterfassung.png"),
    SETTINGS("config.png"),
	MIGRATION("migration.png");

    private static final String BASE = "/org/resources/icons/tabs/";
    private final ImageIcon icon;

    TabIcon(String file) {
        URL url = TabIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.icon = new ImageIcon(url);
    }

    public Icon icon() { return icon; }

    public static Icon byKey(String key) {
        return valueOf(key.toUpperCase(Locale.ROOT)).icon();
    }
}

