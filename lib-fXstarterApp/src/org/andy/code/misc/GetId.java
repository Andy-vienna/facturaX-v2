package org.andy.code.misc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

public class GetId {
	
	private App a = new App();
	private Path dir = Path.of(System.getProperty("user.home"), "AppData", "Roaming", "FacturaX", "start");
	
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
			
			List<String> values = List.of(version,built,jdk,name);
	        Files.write(f, values, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	        return values.toArray(String[]::new);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
