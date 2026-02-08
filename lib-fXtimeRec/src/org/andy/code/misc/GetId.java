package org.andy.code.misc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class GetId {
	
	private App a = new App();
	private Path dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "time");
	
	public String deviceId() {
		try {
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
	
	public String userId() {
		try {
			Files.createDirectories(dir);
			Path f = dir.resolve("user.id");
			if (Files.exists(f)) return Files.readString(f, StandardCharsets.UTF_8).trim();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}
	
	public String[] appInfo() {
		String[] tmp;
		try {
			Files.createDirectories(dir);
			Path f = dir.resolve("app.info");
			if (Files.exists(f)) {
	            // alle Zeilen lesen, trimmen, leere Zeilen entfernen
	            List<String> lines = Files.readAllLines(f, StandardCharsets.UTF_8);
	            tmp = lines.stream().map(String::trim).filter(s -> !s.isEmpty()).toArray(String[]::new);
	            if (tmp[0].equals(a.VERSION)) return tmp;
	        }
			
			String version = a.VERSION;
			String built = a.TIME;
			String jdk = a.JDK;
			String name = a.NAME;
			String db = App.DB;
			
			List<String> values = List.of(version,built,jdk,name,db);
	        Files.write(f, values, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	        return values.toArray(String[]::new);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public LocalDateTime loadTs() {
		try {
			Files.createDirectories(dir);
			Path f = dir.resolve("loadTs.id");
			if (Files.exists(f)) {
				String val = Files.readString(f, StandardCharsets.UTF_8).trim();
				return LocalDateTime.parse(val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return LocalDateTime.MIN; // falls kein vorheriger Stand
	}

	public long loadId() {
		try {
			Files.createDirectories(dir);
			Path f = dir.resolve("loadId.id");
			if (Files.exists(f)) {
				String val = Files.readString(f, StandardCharsets.UTF_8).trim();
				return Long.parseLong(val);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return 0L; // falls kein vorheriger Stand
	}
	
	public void saveWatermark(LocalDateTime ts, long id) {
		try {
			Files.createDirectories(dir);
			Path fTs = dir.resolve("loadTs.id");
			Files.writeString(fTs, ts.toString(), StandardCharsets.UTF_8);
			
			Path fId = dir.resolve("loadId.id");
			Files.writeString(fId, String.valueOf(id), StandardCharsets.UTF_8);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
