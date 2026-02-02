package org.andy.fx.code.dataExport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ExportHelper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

public class WordLeistungsbeschreibung {
	
	private static ErzeugePDF doPdf = new ErzeugePDF();
	
	public static void doLeistungsbeschreibung(Angebot angebot, String sWordIn, String sWordOut, String sPdfOut) throws Exception {
		
		Map<String, String> ownerData = new HashMap<>();
		Map<String, String> data = new HashMap<>();
		
		ArrayList<String> owner = ExportHelper.ownerData();
		
		// Fill owner Data
		ownerData.put("${OWNER-NAME}", owner.get(0));
		ownerData.put("${OWNER-ADRESS}", owner.get(1) + owner.get(2) + owner.get(3) + owner.get(4));
		ownerData.put("${OWNER-VAT}", owner.get(5));
		
		String[] teile = angebot.getBeschreibungHtml().split("~");

		// fill description
		data.put("${HEADLINE}", teile[0]);
		data.put("${TEXTBLOCK}", teile[1]);
		
		// run word-file processor
		befuelleMarken(sWordIn, sWordOut, data, ownerData);
		
		//create pdf-file
		doPdf.wordToPDF(sWordOut, sPdfOut);
		doPdf.setPdfMetadata(angebot.getIdNummer(), "AN", sPdfOut);

		boolean bLockedXLSX = Einstellungen.isLocked(sWordOut);
		boolean bLockedPDF = Einstellungen.isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}
		
	}
	
	//###################################################################################################################################################
	// Leistungsbeschreibung erzeugen und pdf exportieren
	//###################################################################################################################################################

	private static void befuelleMarken(String vorlagePfad, String zielPfad, Map<String, String> daten, Map<String, String> ownerData) {
	    try (FileInputStream fis = new FileInputStream(vorlagePfad);
	         XWPFDocument doc = new XWPFDocument(fis)) {

	    	// 1. Kopfzeilen verarbeiten
	    	for (XWPFHeader header : doc.getHeaderList()) {
	    	    for (XWPFParagraph p : header.getParagraphs()) {
	    	        datenKopfzeile(p, ownerData); // Deine vorhandene Logik für Absätze
	    	    }
	    	}
	    	
	    	// Alle Absätze im Dokument durchlaufen
	    	for (XWPFParagraph p : doc.getParagraphs()) {
	    	    List<XWPFRun> runs = p.getRuns();
	    	    if (runs != null) {
	    	        for (XWPFRun r : runs) {
	    	            String text = r.getText(0);
	    	            if (text != null) {
	    	                for (Map.Entry<String, String> entry : daten.entrySet()) {
	    	                    if (text.contains(entry.getKey())) {
	    	                        String ersatzText = entry.getValue();

	    	                        // Prüfung: Enthält der Ersatztext Zeilenumbrüche?
	    	                        if (ersatzText != null && ersatzText.contains("\n")) {
	    	                            // Text am Umbruch splitten (deckt \n und \r\n ab)
	    	                            String[] zeilen = ersatzText.split("\\R");
	    	                            
	    	                            // Den Platzhalter im ersten Run mit der ersten Zeile ersetzen
	    	                            r.setText(text.replace(entry.getKey(), zeilen[0]), 0);
	    	                            
	    	                            // Für jede weitere Zeile einen Break und den Text hinzufügen
	    	                            for (int i = 1; i < zeilen.length; i++) {
	    	                                r.addBreak();      // Erzeugt den Zeilenumbruch in Word
	    	                                r.setText(zeilen[i]);
	    	                            }
	    	                        } else {
	    	                            // Standard-Fall: Kein Umbruch vorhanden
	    	                            text = text.replace(entry.getKey(), ersatzText != null ? ersatzText : "");
	    	                            r.setText(text, 0);
	    	                        }
	    	                    }
	    	                }
	    	            }
	    	        }
	    	    }
	    	}

	        // Speichern
	        try (FileOutputStream fos = new FileOutputStream(zielPfad)) {
	            doc.write(fos);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void datenKopfzeile(XWPFParagraph p, Map<String, String> daten) {
	    List<XWPFRun> runs = p.getRuns();
	    if (runs != null) {
	        for (XWPFRun r : runs) {
	            String text = r.getText(0);
	            if (text != null) {
	                for (Map.Entry<String, String> entry : daten.entrySet()) {
	                    if (text.contains(entry.getKey())) {
	                        text = text.replace(entry.getKey(), entry.getValue());
	                        r.setText(text, 0);
	                    }
	                }
	            }
	        }
	    }
	}
}
