package org.andy.gui.iconHandler;

import java.net.URL;
import java.util.Locale;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public enum FileIcon {
    FILE_PDF("pdf.png"),
    FILE_PNG("png.png"),
    FILE_CSV("csv.png"),
    FILE_JPG("jpg.png"),
    FILE_MSG("msg.png"),
    FILE_XML("xml.png"),
    FILE_XLSX("xlsx.png"),
    FILE_XLSM("xlsm.png"),
    FILE_RAR("rar.png"),
	FILE_ZIP("zip.png");

	private static final String BASE = "/org/resources/icons/files/";
    private final ImageIcon icon;
    
    FileIcon(String file) {
        URL url = FileIcon.class.getResource(BASE + file);
        if (url == null) throw new IllegalStateException("Icon fehlt: " + BASE + file);
        this.icon = new ImageIcon(url);
    }

    public Icon icon() { return icon; }

    public static Icon byKey(String key) {
        return valueOf(key.toUpperCase(Locale.ROOT)).icon();
    }
}

