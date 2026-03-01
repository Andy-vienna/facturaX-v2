package org.andy.code.main;

import java.io.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;

import org.andy.code.misc.App;
import org.andy.code.misc.GetId;

public class StartUp {

	@SuppressWarnings("unused")
	private App a = new App(); private static GetId id = new GetId();
    private static final Pattern VERSIONED_JAR_PATTERN = Pattern.compile(".*-(\\d+)\\.(\\d+)\\.(\\d+)\\.(\\d+)\\.jar$");

	// ###################################################################################################################################################
	// Starten der Applikation
	// ###################################################################################################################################################
    
    public static void main(String[] args) throws IOException {
    	
    	// App-Infos schreiben
    	@SuppressWarnings("unused")	String[] tmp = id.appInfo();
    	
        String folderPath = System.getProperty("user.dir");

        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("Ungültiger Ordner: " + folder.getAbsolutePath());
            return;
        }

        File[] jarFiles = folder.listFiles((_, name) -> name.toLowerCase().endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            System.out.println("Keine JAR-Dateien gefunden.");
            return;
        }

        List<File> sortedJars = Arrays.stream(jarFiles)
        	    .filter(f -> VERSIONED_JAR_PATTERN.matcher(f.getName()).matches())
        	    .sorted((f1, f2) -> compareVersions(extractVersion(f2), extractVersion(f1))) // höchste Version zuerst
        	    .collect(Collectors.toList());

        for (File jar : sortedJars) {
            System.out.println("Versuche zu starten: " + jar.getName());
            try {
            	ProcessBuilder pb = new ProcessBuilder(
            		"java",
            		"--enable-native-access=ALL-UNNAMED",
            		"-jar",
            		jar.getAbsolutePath()
            	);
                //ProcessBuilder pb = new ProcessBuilder("java", "-jar", jar.getAbsolutePath());
                pb.inheritIO();
                Process process = pb.start();
                int exitCode = process.waitFor();
                System.out.println("Beendet mit Code: " + exitCode);
                if (exitCode == 0) {
                    break;
                } else {
                    System.out.println("Fehlgeschlagen, versuche ältere Version...");
                }
            } catch (Exception e) {
                System.out.println("Fehler beim Start von " + jar.getName() + ": " + e.getMessage());
            }
        }

        //System.out.println("Keine startbare JAR-Datei gefunden.");
    }
    
	// ###################################################################################################################################################
	// Hilfsmethoden
	// ###################################################################################################################################################

    // Versionsnummer als Liste extrahieren
    private static List<Integer> extractVersion(File f) {
        Matcher m = VERSIONED_JAR_PATTERN.matcher(f.getName());
        if (m.matches()) {
            return Arrays.asList(
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)),
                    Integer.parseInt(m.group(3))
            );
        }
        return Arrays.asList(0, 0, 0);
    }
    
    private static int compareVersions(List<Integer> v1, List<Integer> v2) {
        for (int i = 0; i < Math.min(v1.size(), v2.size()); i++) {
            int cmp = Integer.compare(v1.get(i), v2.get(i));
            if (cmp != 0) return cmp;
        }
        return 0; // gleich
    }
    
}
