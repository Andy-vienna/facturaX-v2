package org.andy.fx.code.misc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class GetId {
	
	public String deviceId() {
		try {
			Path dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "time");
			Files.createDirectories(dir);
			Path f = dir.resolve("device.id");
			if (Files.exists(f))
				return Files.readString(f, StandardCharsets.UTF_8).trim();
			String id = UUID.randomUUID().toString();
			Files.writeString(f, id, StandardCharsets.UTF_8);
			return id;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String[] appInfo(String app) {
		Path dir = null;
		switch(app){
		case "starter" -> dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "start");
		case "tray"  -> dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "time");
		case "html"  -> dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "runner");
		}
		try {
			Files.createDirectories(dir);
			Path f = dir.resolve("app.info");
			if (Files.exists(f)) {
	            // alle Zeilen lesen, trimmen, leere Zeilen entfernen
	            List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
	            return lines.stream().map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
	        }
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

}
