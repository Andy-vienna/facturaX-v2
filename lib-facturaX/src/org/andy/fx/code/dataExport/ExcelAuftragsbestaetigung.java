package org.andy.fx.code.dataExport;

import static org.andy.fx.code.misc.TextFormatter.FormatIBAN;
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

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.andy.fx.code.qr.ZxingQR;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.zxing.WriterException;

public class ExcelAuftragsbestaetigung implements Identified {

	public static final String CLASS_ID = ExcelAuftragsbestaetigung.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelAuftragsbestaetigung.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_F = 5;

	private static String[] sAnTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];

	//###################################################################################################################################################
	// Angebotbestätigung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void abExport(String sNr, String confNr, String confDate, String startDate) throws Exception {
		String revNr = null;
		String sExcelIn = Einstellungen.getAppSettings().tplOfferConfirm;
		if(sNr.contains("/")) {
			revNr = sNr.replace("/", "rev");
		} else {
			revNr = sNr;
		}
		String sExcelOut = Einstellungen.getAppSettings().work + "Auftragsbestätigung_" + revNr.replace("AN", "AB") + ".xlsx";
		String sPdfOut = Einstellungen.getAppSettings().work + "Auftragsbestätigung_" + revNr.replace("AN", "AB") + ".pdf";

		final Cell abPos[] = new Cell[13];
		final Cell abText[] = new Cell[13];
		final Cell abAnz[] = new Cell[13];
		final Cell abEPreis[] = new Cell[13];
		final Cell abGPreis[] = new Cell[13];

		Angebot angebot = ExportHelper.loadAngebot(sNr);
		Kunde kunde = ExportHelper.kundeData(angebot.getIdKunde());
		String adressat = ExportHelper.kundeAnschrift(angebot.getIdKunde());
		Bank bank = ExportHelper.bankData(angebot.getIdBank());

		String[][] txtBaustein = ExportHelper.findText(CLASS_ID);

		//#######################################################################
		// Angebots-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Angebot");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);

			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			for(int i = 0; i < angebot.getAnzPos(); i++ ) { //Positionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				abPos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				abText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				abAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				abEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				abGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}

			//#######################################################################
			// Zellwerte beschreiben aus dem Array arrAnContent
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{abAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{abDatum}", StartUp.getDtNow());
			ExportHelper.replaceCellValue(wb, ws, "{abNummer}", angebot.getIdNummer().replace("AN", "AB"));
			ExportHelper.replaceCellValue(wb, ws, "{abRef}", angebot.getIdNummer());
			ExportHelper.replaceCellValue(wb, ws, "{abDuty}", kunde.getPronomen() + " " + kunde.getPerson());

			for(int i = 0; i < angebot.getAnzPos(); i++ ) {
				abPos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Angebot.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal menge = (BigDecimal) Angebot.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal ep = (BigDecimal) Angebot.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(angebot);
		            sAnTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            abText[i].setCellValue(art);
					abAnz[i].setCellValue(menge.doubleValue());
					abEPreis[i].setCellValue(ep.doubleValue());
					abGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			ExportHelper.replaceCellValue(wb, ws, "{abSumme}", angebot.getNetto().doubleValue());

			ExportHelper.replaceCellValue(wb, ws, "{Bank}", bank.getBankName());
			ExportHelper.replaceCellValue(wb, ws, "{IBAN}", FormatIBAN(bank.getIban()));
			ExportHelper.replaceCellValue(wb, ws, "{BIC}", bank.getBic());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (angebot.getPage2() == 0 && key != null && key.contains("{LBvorh}")) {
			        val = "";
			    } else if (val != null) {
			        val = val.replace("{AN}", angebot.getIdNummer());
			        val = val.replace("{Best-Nr}", confNr);
			        val = val.replace("{Datum}", confDate);
			        val = val.replace("{StartDatum}", startDate);
			        val = val.replace("{Tage}",     kunde.getZahlungsziel()); // ggf. String.valueOf(...)
			    }
			    txtBaustein[x][1] = val;
			    
			    ExportHelper.replaceCellValue(wb, ws, key, val); // Texte in Zellen schreiben

			    // QR Code erzeugen und im Anwendungsverzeichnis ablegen
			    if (key != null && key.contains("{QR}") && val != null && !val.isEmpty()) {
			        try {
			            ZxingQR.makeLinkQR(val);
			        } catch (WriterException e) {
			            logger.error("makeLinkQR error", e);
			        }
			    }
			}
			
			//#######################################################################
			// erzeugten QR Code als png-Datei einlesen
			//#######################################################################
			ExportHelper.placeQRinExcel(wb, ws, "link.png");
			
			//#######################################################################
			// WORKBOOK mit Daten befüllen und schließen
			//#######################################################################
			wb.write(fileOut); //Excel mit Daten befüllen
			wb.close(); //Excel workbook schließen
		} catch (FileNotFoundException e) {
			logger.error("abExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("abExport(String sNr) - " + e);
		}
		//#######################################################################
		// Datei link.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\link.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("reExport(String sNr) - link.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);
		ErzeugePDF.setPdfMetadata(sNr.replace("AN", "AB"), "AB", sPdfOut);

		boolean bLockedPDF = Einstellungen.isLocked(sPdfOut);
		while(bLockedPDF) {
			System.out.println("warte auf Datei ...");
		}

		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		String PdfName = PdfFn.getName();
		
		FileStoreRepository fileStoreRepository = new FileStoreRepository();
		FileStore fileStore = fileStoreRepository.findById(angebot.getIdNummer()); // Tabelleneintrag mit Hibernate lesen
		
		fileStore.setAbFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setAbPdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.update(fileStore); // Datei in DB speichern
		
		//#######################################################################
		// Status des Angebots ändern
		//#######################################################################
		
		angebot.setState(angebot.getState() + 100); // Zustand bestätigt setzen
		AngebotRepository angebotRepository = new AngebotRepository();
		angebotRepository.update(angebot);

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
			logger.error("reExport(String sNr) - xlsx-Datei konnte nicht gelöscht werden");
		}
	}

}

