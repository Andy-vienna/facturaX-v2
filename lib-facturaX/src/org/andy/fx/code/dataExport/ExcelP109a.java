package org.andy.fx.code.dataExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ExportHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelP109a {
	
	private static final Logger logger = LogManager.getLogger(ExcelP109a.class);
	private static ErzeugePDF doPdf = new ErzeugePDF();
	
	static String sExcelIn = null, sExcelOut = null, sPdfOut = null;
	
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_E = 4;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

    public static void ExportP109a(ArrayList<BigDecimal> listContent) {
        setData(listContent);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setData(ArrayList<BigDecimal> listContent) {
		
		sExcelIn = Einstellungen.getAppSettings().tplP109a;
		sExcelOut = Einstellungen.getAppSettings().work + "\\Mitteilung_nach_P109a_" + Einstellungen.getAppSettings().year + ".xlsx";
		sPdfOut = Einstellungen.getAppSettings().work + "\\Mitteilung_nach_P109a_" + Einstellungen.getAppSettings().year + ".pdf";
		
		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("EA-Rechnung");
		
			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			Cell taxID       = ws.getRow( 0).getCell(COLUMN_D); //Steuernummer
			Cell year        = ws.getRow( 7).getCell(COLUMN_E); //Wirtschaftsjahr
			
			Cell P109In      = ws.getRow(11).getCell(COLUMN_E); //Einkünfte aus selbstständiger Arbeit
			Cell P109SVSQ1   = ws.getRow(13).getCell(COLUMN_C); //SV-Beiträge 1. Quartal
			Cell P109SVSQ2   = ws.getRow(14).getCell(COLUMN_C); //SV-Beiträge 2. Quartal
			Cell P109SVSQ3   = ws.getRow(15).getCell(COLUMN_C); //SV-Beiträge 3. Quartal
			Cell P109SVSQ4   = ws.getRow(16).getCell(COLUMN_C); //SV-Beiträge 4. Quartal
			Cell P109SVTotal = ws.getRow(16).getCell(COLUMN_D); //SV-Beiträge Summe
			
			Cell P109OeffiP  = ws.getRow(17).getCell(COLUMN_D); //50% Öffi-Pauschale
			Cell P109APau    = ws.getRow(18).getCell(COLUMN_D); //großes Arbeitsplatzpauschale
			Cell P109Exp     = ws.getRow(19).getCell(COLUMN_D); //Betriebsausgaben
			
			Cell P109ZSumEx  = ws.getRow(20).getCell(COLUMN_E); //Zwischensumme Ausgaben
			Cell P109ZSum    = ws.getRow(21).getCell(COLUMN_E); //Zwischensumme
			
			Cell P109GWB     = ws.getRow(22).getCell(COLUMN_E); //Gewinnfreibetrag
			Cell P109Erg     = ws.getRow(23).getCell(COLUMN_E); //Ergebnis
			
			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);
			
			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			taxID.setCellValue("Steuernummer: " + ExportHelper.getSteuerNummer()); //Steuernummer
			year.setCellValue(Einstellungen.getAppSettings().year); //Wirtschaftsjahr
			
			P109In.setCellValue(listContent.get(0).doubleValue()); //Einkünfte aus selbstständiger Arbeit
			P109SVSQ1.setCellValue(listContent.get(1).doubleValue()); //SV-Beiträge 1. Quartal
			P109SVSQ2.setCellValue(listContent.get(2).doubleValue()); //SV-Beiträge 2. Quartal
			P109SVSQ3.setCellValue(listContent.get(3).doubleValue()); //SV-Beiträge 3. Quartal
			P109SVSQ4.setCellValue(listContent.get(4).doubleValue()); //SV-Beiträge 4. Quartal
			P109SVTotal.setCellValue(listContent.get(5).doubleValue()); //SV-Beiträge Summe
			
			P109OeffiP.setCellValue(listContent.get(6).doubleValue()); //50% Öffi-Pauschale
			P109APau.setCellValue(listContent.get(7).doubleValue()); //großes Arbeitsplatzpauschale
			P109Exp.setCellValue(listContent.get(8).doubleValue()); //Betriebsausgaben
			
			BigDecimal zwSum = listContent.get(5).add(listContent.get(6)).add(listContent.get(7)).add(listContent.get(8));
			
			P109ZSumEx.setCellValue(zwSum.doubleValue());
			P109ZSum.setCellValue(listContent.get(9).doubleValue()); //Zwischensumme
			
			P109GWB.setCellValue(listContent.get(10).doubleValue()); //Gewinnfreibetrag
			P109Erg.setCellValue(listContent.get(11).doubleValue()); //Ergebnis
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("reExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("reExport(String sNr) - " + e);
		}
		
		//#######################################################################
		// PDF-A1 Datei erzeugen
		//#######################################################################
		doPdf.toPDF(sExcelOut, sPdfOut);
		
		//#######################################################################
		// Ursprungs-Excel löschen
		//#######################################################################
		boolean bLockedxlsx = Einstellungen.isLocked(sExcelOut);
		while(bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		if(xlFile.delete()) {

		}else {
			logger.error("§109a Mitteilung - xlsx-Datei konnte nicht gelöscht werden");
		}
	}
}
