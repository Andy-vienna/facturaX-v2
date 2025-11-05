package org.andy.fx.code.dataExport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelLieferschein implements Identified {

	public static final String CLASS_ID = ExcelLieferschein.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelLieferschein.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_F = 5;
	
	private static String[] sLsTxt = new String[12];
	private static double[] dAnz = new double[12];

	//###################################################################################################################################################
	// Rechnung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void lsExport(String sNr) throws Exception {
		String sExcelIn = Einstellungen.getAppSettings().tplDeliveryNote;
		String sExcelOut = Einstellungen.getAppSettings().work + "Lieferschein_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getAppSettings().work + "Lieferschein_" + sNr + ".pdf";

		final Cell lsPos[] = new Cell[12];
		final Cell lsText[] = new Cell[12];
		final Cell lsAnz[] = new Cell[12];

		Lieferschein lieferschein = ExportHelper.loadLieferschein(sNr);
		String adressat = ExportHelper.kundeAnschrift(lieferschein.getIdKunde());
		
		String[][] txtBaustein = ExportHelper.findText(CLASS_ID);

		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Lieferschein");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);

			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			for(int i = 0; i < lieferschein.getAnzPos(); i++ ) { //Positionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				lsPos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				lsText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				lsAnz[i] = ws.getRow(j).getCell(COLUMN_F); //Menge
			}

			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{lsAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{lsDatum}", lieferschein.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			ExportHelper.replaceCellValue(wb, ws, "{lsNummer}", lieferschein.getIdNummer());

			for(int i = 0; i < lieferschein.getAnzPos(); i++ ) {
				lsPos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Lieferschein.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(lieferschein);
		            BigDecimal menge = (BigDecimal) Lieferschein.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(lieferschein);
		            sLsTxt[i] = art; dAnz[i] = menge.doubleValue();
		            lsText[i].setCellValue(art);
					lsAnz[i].setCellValue(menge.doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    txtBaustein[x][1] = val;
			    
			    ExportHelper.replaceCellValue(wb, ws, key, val); // Texte in Zellen schreiben
			    
			}
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("reExport(...) - " + e);
		} catch (IOException e) {
			logger.error("reExport(...) - " + e);
		}

		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);
		ErzeugePDF.setPdfMetadata(sNr, "BE", sPdfOut);

		boolean bLockedXLSX = Einstellungen.isLocked(sExcelOut);
		boolean bLockedPDF = Einstellungen.isLocked(sPdfOut);
		while(bLockedXLSX || bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}
		
		//#######################################################################
		// FileStore Entität instanzieren
		//#######################################################################
		FileStoreRepository fileStoreRepository = new FileStoreRepository();
		FileStore fileStore = new FileStore();
		
		fileStore.setIdNummer(lieferschein.getIdNummer()); // Bestellnummer als Index für fileStore schreiben
		fileStore.setJahr(lieferschein.getJahr()); // Jahr in fileStore schreiben
		
		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		
		String PdfName = PdfFn.getName();
		fileStore.setLsFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setLsPdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.save(fileStore); // Datei(en) in DB speichern
		
		//#######################################################################
		// Status der Bestellung ändern
		//#######################################################################
		
		lieferschein.setState(lieferschein.getState() + 10); // Zustand gedruckt setzen
		LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
		lieferscheinRepository.update(lieferschein);

		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = Einstellungen.isLocked(sPdfOut);
		boolean bLockedxlsx = Einstellungen.isLocked(sExcelOut);
		while(bLockedpdf || bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		File pdFile = new File(sPdfOut);
		if(xlFile.delete() && pdFile.delete()) {

		}else {
			logger.error("lsExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String[] getsReTxt() {
		return sLsTxt;
	}

	public static double[] getdAnz() {
		return dAnz;
	}

}

