package org.andy.fx.code.googleServices;

import java.time.*;
import java.time.format.*;
import java.util.*;

public final class DateParser {
	
	private static final Locale DE = Locale.GERMAN;

	// Mehrere gängige Formate
	private static final List<DateTimeFormatter> F = List.of(DateTimeFormatter.ISO_LOCAL_DATE, // 2025-10-16
			DateTimeFormatter.ofPattern("d.M.yyyy"), // 16.10.2025 / 6.1.2025
			DateTimeFormatter.ofPattern("dd.MM.yyyy"), // 16.10.2025
			DateTimeFormatter.ofPattern("d. MMM yyyy", DE), // 16. Okt 2025
			DateTimeFormatter.ofPattern("d MMM yyyy", DE), // 16 Okt 2025
			DateTimeFormatter.ofPattern("d. MMMM yyyy", DE), // 16. Oktober 2025
			DateTimeFormatter.ofPattern("d MMMM yyyy", DE) // 16 Oktober 2025
	);

	// ###################################################################################################################################################
	// public Teil
	// ###################################################################################################################################################

	public static LocalDate parseOrDefault(String raw, LocalDate dflt) {
		LocalDate d = parseFlexible(raw); // deine Funktion
		return d != null ? d : dflt;
	}
	
	public static LocalDate parseFlexible(String raw) {
		if (raw == null)
			return null;
		String s = normalize(raw);
		for (DateTimeFormatter fmt : F) {
			try {
				return LocalDate.parse(s,
						new DateTimeFormatterBuilder().parseCaseInsensitive().append(fmt).toFormatter(DE));
			} catch (DateTimeParseException ignore) {
			}
		}
		return null;
	}

	public static LocalDate findFirstDate(String text) {
		if (text == null)
			return null;
		// Kandidaten per Regex ausschneiden
		var patterns = List.of("\\b\\d{4}-\\d{2}-\\d{2}\\b", // ISO
				"\\b\\d{1,2}[.]\\d{1,2}[.]\\d{4}\\b", // 16.10.2025
				"\\b\\d{1,2}[.]?\\s+[A-Za-zÄÖÜäöü\\.]+\\s+\\d{4}\\b" // 16 Oktober 2025 / 16. Okt. 2025
		);
		for (String re : patterns) {
			var m = java.util.regex.Pattern.compile(re).matcher(text);
			if (m.find()) {
				LocalDate d = parseFlexible(m.group());
				if (d != null)
					return d;
			}
		}
		return null;
	}

	// ###################################################################################################################################################
	// private Teil
	// ###################################################################################################################################################

	private static String normalize(String s) {
		String t = s.strip();
		// Punkt nach kurzem Monatsnamen optional entfernen: "Okt." -> "Okt"
		t = t.replaceAll("\\b(Jan|Feb|Mär|Mrz|Apr|Mai|Jun|Jul|Aug|Sep|Okt|Nov|Dez)\\.", "$1");
		// Mehrfache Spaces reduzieren
		t = t.replaceAll("\\s+", " ");
		return t;
	}
}
