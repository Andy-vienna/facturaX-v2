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

import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelBestellung implements Identified {

	public static final String CLASS_ID = ExcelBestellung.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelBestellung.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_F = 5;
	
	private static String[] sBeTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];

	//###################################################################################################################################################
	// Rechnung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void beExport(String sNr) throws Exception {
		String sExcelIn = Einstellungen.getAppSettings().tplOrder;
		String sExcelOut = Einstellungen.getAppSettings().work + "Bestellung_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getAppSettings().work + "Bestellung_" + sNr + ".pdf";

		final Cell bePos[] = new Cell[12];
		final Cell beText[] = new Cell[12];
		final Cell beAnz[] = new Cell[12];
		final Cell beEPreis[] = new Cell[12];
		final Cell beGPreis[] = new Cell[12];

		Bestellung bestellung = ExportHelper.loadBestellung(sNr);
		String adressat = ExportHelper.lieferantAnschrift(bestellung.getIdLieferant());
		
		String[][] txtBaustein = ExportHelper.findText(CLASS_ID);

		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Bestellung");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);

			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			for(int i = 0; i < bestellung.getAnzPos(); i++ ) { //Bestellpositionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				bePos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				beText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				beAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				beEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				beGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}

			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{beAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{beDatum}", bestellung.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			ExportHelper.replaceCellValue(wb, ws, "{beNummer}", bestellung.getIdNummer());
			ExportHelper.replaceCellValue(wb, ws, "{beRef}", bestellung.getRef());

			for(int i = 0; i < bestellung.getAnzPos(); i++ ) {
				bePos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Bestellung.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(bestellung);
		            BigDecimal menge = (BigDecimal) Bestellung.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(bestellung);
		            BigDecimal ep = (BigDecimal) Bestellung.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(bestellung);
		            sBeTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            beText[i].setCellValue(art);
					beAnz[i].setCellValue(menge.doubleValue());
					beEPreis[i].setCellValue(ep.doubleValue());
					beGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			ExportHelper.replaceCellValue(wb, ws, "{beSumme}", bestellung.getNetto().doubleValue());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (val != null) {
			        val = val.replace("{OwnerName}", ExportHelper.getKontaktName());
			    }
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
		
		fileStore.setIdNummer(bestellung.getIdNummer()); // Bestellnummer als Index für fileStore schreiben
		fileStore.setJahr(bestellung.getJahr()); // Jahr in fileStore schreiben
		
		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		
		String PdfName = PdfFn.getName();
		fileStore.setBeFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setBePdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.save(fileStore); // Datei(en) in DB speichern
		
		//#######################################################################
		// Status der Bestellung ändern
		//#######################################################################
		
		bestellung.setState(bestellung.getState() + 10); // Zustand gedruckt setzen
		BestellungRepository bestellungRepository = new BestellungRepository();
		bestellungRepository.update(bestellung);

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
			logger.error("anExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String[] getsReTxt() {
		return sBeTxt;
	}

	public static double[] getdAnz() {
		return dAnz;
	}

	public static double[] getdEp() {
		return dEp;
	}

}

