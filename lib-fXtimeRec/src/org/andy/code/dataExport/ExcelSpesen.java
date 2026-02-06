package org.andy.code.dataExport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.andy.code.dataStructure.entity.Spesen;
import org.andy.code.dataStructure.repository.SpesenRepository;
import org.andy.code.main.Settings;
import org.andy.code.misc.ExportHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSpesen {
	
	private static final Logger logger = LogManager.getLogger(ExcelSpesen.class);
	private static CreatePdf doPdf = new CreatePdf();
	private static final String HEADER_STYLE = "&\"Arial,Regular\"&11&K7F7F7F";
	private static final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static XSSFWorkbook wb = null;
	private static String sExcelOut = null;
	private static String sPdfOut = null;

	private static final int START_ROW_OFFSET = 2;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	private static final int COLUMN_F = 5;
	private static final int COLUMN_G = 6;
	
	private static final SpesenRepository repo = new SpesenRepository();
    private static List<Spesen> ls = null;
	
	private ExcelSpesen() {} // Instanzierung verhindern
	
	//###################################################################################################################################################
	// Angebot erzeugen und pdf exportieren
	//###################################################################################################################################################

	public static void spExport(int daysInMonth, Month m, int year, String stunden, String summe) throws Exception {
		String monat = m.getDisplayName(TextStyle.FULL, Locale.GERMAN);
		String jahr = String.valueOf(year);
		
		String sExcelIn = Settings.getSettings().tplSpesen;
		sExcelOut = Settings.getSettings().workpath + "Spesen_" + monat + "_" + jahr + ".xlsx";
		sPdfOut = Settings.getSettings().workpath + "Spesen_" + monat + "_" + jahr + ".pdf";

		final Cell spDatum[] = new Cell[31]; final Cell spVon[] = new Cell[31];	final Cell spBis[] = new Cell[31];
		final Cell spStunden[] = new Cell[31]; final Cell spBetrag[] = new Cell[31]; final Cell spLand[] = new Cell[31];
		final Cell spKommentar[] = new Cell[31]; Cell spGesStunden = null; Cell spGesSumme = null;
		
    	LocalDate from = LocalDate.of(year, m, 1);
    	LocalDate to = LocalDate.of(year, m, daysInMonth);
		
		ls = new ArrayList<>(); ls = repo.findByDateBetween(from, to);
		
		//#######################################################################
		// Angebots-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Monat");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			Header header = ws.getHeader();
	        header.setCenter(HEADER_STYLE + monat + " " + jahr);
			ExportHelper.applyOwnerAndFooter(wb, ws);
			
			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			for(int i = 0; i < daysInMonth; i++ ) { // Spesenzeilen füllen
				int j = i + START_ROW_OFFSET;
				spDatum[i] = ws.getRow(j).getCell(COLUMN_A);
				if (spDatum[i] == null) spDatum[i] = ws.getRow(j).createCell(COLUMN_A);
				spVon[i] = ws.getRow(j).getCell(COLUMN_B);
				if (spVon[i] == null) spVon[i] = ws.getRow(j).createCell(COLUMN_B);
				spBis[i] = ws.getRow(j).getCell(COLUMN_C);
				if (spBis[i] == null) spBis[i] = ws.getRow(j).createCell(COLUMN_C);
				spStunden[i] = ws.getRow(j).getCell(COLUMN_D);
				if (spStunden[i] == null) spStunden[i] = ws.getRow(j).createCell(COLUMN_D);
				spLand[i] = ws.getRow(j).getCell(COLUMN_E);
				if (spLand[i] == null) spLand[i] = ws.getRow(j).createCell(COLUMN_E);
				spBetrag[i] = ws.getRow(j).getCell(COLUMN_F);
				if (spBetrag[i] == null) spBetrag[i] = ws.getRow(j).createCell(COLUMN_F);
				spKommentar[i] = ws.getRow(j).getCell(COLUMN_G);
				if (spKommentar[i] == null) spKommentar[i] = ws.getRow(j).createCell(COLUMN_G);
				
				LocalDate date = LocalDate.parse(ls.get(i).getDate().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
		        String datum = date.format(outputFormatter);
				
				spDatum[i].setCellValue(datum);
				spVon[i].setCellValue(ls.get(i).getTimeStart().toString());
				spBis[i].setCellValue(ls.get(i).getTimeEnd().toString());
				spStunden[i].setCellValue(ls.get(i).getSumHours().setScale(2, RoundingMode.HALF_UP).doubleValue());
				spBetrag[i].setCellValue(ls.get(i).getAmount().setScale(2, RoundingMode.HALF_UP).doubleValue());
				spLand[i].setCellValue(ls.get(i).getCountry());
				spKommentar[i].setCellValue(ls.get(i).getComment());
			}
			spGesStunden = ws.getRow(33).getCell(COLUMN_D);
			if (spGesStunden == null) spGesStunden = ws.getRow(33).createCell(COLUMN_D);
			spGesSumme = ws.getRow(33).getCell(COLUMN_F);
			if (spGesSumme == null) spGesSumme = ws.getRow(33).createCell(COLUMN_F);
			spGesStunden.setCellValue(stunden.replace(".", ","));
			spGesSumme.setCellValue(summe.replace(".", ","));
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			
		} catch (FileNotFoundException e) {
			logger.error("anExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("anExport(String sNr) - " + e);
		} finally {
			wb.close(); //Excel workbook schließen
		}
		
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		doPdf.toPDF(sExcelOut, sPdfOut);
		doPdf.setPdfMetadata(monat, sPdfOut);

		boolean bLockedXLSX = Settings.isLocked(sExcelOut);
		boolean bLockedPDF = Settings.isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}
	}

	public static String getsExcelOut() {
		return sExcelOut;
	}

	public static String getsPdfOut() {
		return sPdfOut;
	}
}
