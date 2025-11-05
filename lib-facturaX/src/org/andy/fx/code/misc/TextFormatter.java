package org.andy.fx.code.misc;

import java.io.IOException;
import java.util.Locale;

public class TextFormatter {
	
	public static String cutFront(String txt, String teil, int number) throws IOException {
		for (int i = 0; i < number; i++) {
			txt = txt.substring(txt.indexOf(teil) + 1, txt.length());
		}
		return txt;

	}

	public static String cutBack(String txt, String teil, int number) throws IOException {
		for (int i = 0; i < number; i++) {
			txt = txt.substring(0, txt.lastIndexOf(teil));
		}
		return txt;
	}

	public static String cutBack2(String txt, String teil, int number) throws IOException {
		for (int i = 0; i < number; i++) {
			txt = txt.substring(0, txt.indexOf(teil));
		}
		return txt;
	}

	public static String cutFromRight(String txt, char teil) throws IOException {
		int i = 0;
		int lang = txt.length();
		char[] backward = new char[lang];
		for (i = lang - 1; i > -1; i--) {
			backward[i] = txt.charAt(i);
			if (backward[i] == teil) {
				break;
			}
		}
		txt = txt.substring(i + 1);
		return txt;
	}
	
	public static String FormatIBAN(String in) {
	    if (in == null) return null;
	    String s = in.replaceAll("\\s+", "").toUpperCase(Locale.ROOT);
	    StringBuilder sb = new StringBuilder(s.length() + s.length()/4);
	    for (int i = 0; i < s.length(); i++) {
	        if (i > 0 && (i % 4) == 0) sb.append(' ');
	        sb.append(s.charAt(i));
	    }
	    return sb.toString();
	}

}
