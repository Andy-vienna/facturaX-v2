package org.andy.fx.code.dataExport;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.andy.fx.code.dataStructure.entityProductive.WorkTime;
import org.andy.fx.code.dataStructure.repositoryProductive.WorkTimeRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.ExportHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Header;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelArbeitszeit {
	
	private static final Logger logger = LogManager.getLogger(ExcelArbeitszeit.class);
	private static ErzeugePDF doPdf = new ErzeugePDF();
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
	
	private static final WorkTimeRepository repo = new WorkTimeRepository();
    private static List<WorkTime> wt = null;

	private ExcelArbeitszeit() {} // Instanzierung verhindern
	
	//###################################################################################################################################################
	// Angebot erzeugen und pdf exportieren
	//###################################################################################################################################################

	public static void wtExport(int daysInMonth, Month m, int year, String user) throws Exception {
		String monat = m.getDisplayName(TextStyle.FULL, Locale.GERMAN);
		String jahr = String.valueOf(year);
		
		String sExcelIn = Einstellungen.getAppSettings().tplArbeitszeit;
		sExcelOut = Einstellungen.getAppSettings().work + "Arbeitszeit_" + monat + "_" + jahr + ".xlsx";
		sPdfOut = Einstellungen.getAppSettings().work + "Arbeitszeit_" + monat + "_" + jahr + ".pdf";

		final Cell wtDatum[] = new Cell[31]; final Cell wtVon[] = new Cell[31];	final Cell wtBis[] = new Cell[31];
		final Cell wtPause[] = new Cell[31]; final Cell wtStunden[] = new Cell[31];
		final Cell wtKommentar[] = new Cell[31]; Cell wtGesStunden = null;
		
		BigDecimal sumHours = BD.ZERO;
		
    	LocalDate from = LocalDate.of(year, m, 1);
    	LocalDate to = LocalDate.of(year, m, daysInMonth);
		
		wt = new ArrayList<>(); wt = repo.findDaysForUser(from, to, user);
		
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
			for(int i = 0; i < wt.size(); i++ ) { // Arbeitszeit füllen
				int j = i + START_ROW_OFFSET;
				wtDatum[i] = ws.getRow(j).getCell(COLUMN_A);
				if (wtDatum[i] == null) wtDatum[i] = ws.getRow(j).createCell(COLUMN_A);
				wtVon[i] = ws.getRow(j).getCell(COLUMN_B);
				if (wtVon[i] == null) wtVon[i] = ws.getRow(j).createCell(COLUMN_B);
				wtBis[i] = ws.getRow(j).getCell(COLUMN_C);
				if (wtBis[i] == null) wtBis[i] = ws.getRow(j).createCell(COLUMN_C);
				wtPause[i] = ws.getRow(j).getCell(COLUMN_D);
				if (wtPause[i] == null) wtPause[i] = ws.getRow(j).createCell(COLUMN_D);
				wtStunden[i] = ws.getRow(j).getCell(COLUMN_E);
				if (wtStunden[i] == null) wtStunden[i] = ws.getRow(j).createCell(COLUMN_E);
				wtKommentar[i] = ws.getRow(j).getCell(COLUMN_F);
				if (wtKommentar[i] == null) wtKommentar[i] = ws.getRow(j).createCell(COLUMN_F);
				
				LocalDate date = LocalDate.parse(wt.get(i).getTsIn().toLocalDate().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
		        String datum = date.format(outputFormatter);
		        
		        wtDatum[i].setCellValue(datum);
				wtVon[i].setCellValue(wt.get(i).getTsIn().toLocalTime().toString());
				wtBis[i].setCellValue(wt.get(i).getTsOut().toLocalTime().toString());
				wtPause[i].setCellValue(wt.get(i).getBreakTime().doubleValue());
				wtStunden[i].setCellValue(wt.get(i).getWorkTime().setScale(2, RoundingMode.HALF_UP).doubleValue());
				wtKommentar[i].setCellValue(wt.get(i).getReason().trim());
				
				sumHours = sumHours.add(wt.get(i).getWorkTime());
			}
			wtGesStunden = ws.getRow(33).getCell(COLUMN_E);
			if (wtGesStunden == null) wtGesStunden = ws.getRow(33).createCell(COLUMN_E);
			wtGesStunden.setCellValue(sumHours.setScale(2, RoundingMode.HALF_UP).doubleValue());
			
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
		doPdf.setPdfMetadata(monat, "AZ", sPdfOut);

		boolean bLockedXLSX = Einstellungen.isLocked(sExcelOut);
		boolean bLockedPDF = Einstellungen.isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String getsExcelOut() {
		return sExcelOut;
	}

	public static String getsPdfOut() {
		return sPdfOut;
	}
}
