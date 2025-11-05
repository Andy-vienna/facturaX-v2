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
import java.time.format.DateTimeFormatter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.zxing.WriterException;

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.andy.fx.code.qr.ZxingQR;

public class ExcelAngebot implements Identified {

	public static final String CLASS_ID = ExcelAngebot.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelAngebot.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_F = 5;
	
	private static String[] sAnTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];
	
	private ExcelAngebot() {} // Instanzierung verhindern
	
	//###################################################################################################################################################
	// Angebot erzeugen und pdf exportieren
	//###################################################################################################################################################

	public static void anExport(String sNr) throws Exception {
		String sExcelIn = Einstellungen.getAppSettings().tplOffer;
		String sExcelOut = Einstellungen.getAppSettings().work + "Angebot_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getAppSettings().work + "Angebot_" + sNr + ".pdf";
		String sPdfDesc = Einstellungen.getAppSettings().work + "Leistungsbeschreibung_Angebot_" + sNr + ".pdf";

		final Cell anPos[] = new Cell[13];
		final Cell anText[] = new Cell[13];
		final Cell anAnz[] = new Cell[13];
		final Cell anEPreis[] = new Cell[13];
		final Cell anGPreis[] = new Cell[13];

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
			for(int i = 0; i < angebot.getAnzPos(); i++ ) { //Angebotspositionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				anPos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				anText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				anAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				anEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				anGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}
			
			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{anAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{anDatum}", angebot.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			ExportHelper.replaceCellValue(wb, ws, "{anNummer}", angebot.getIdNummer());
			ExportHelper.replaceCellValue(wb, ws, "{anRef}", angebot.getRef());
			ExportHelper.replaceCellValue(wb, ws, "{anDuty}", kunde.getPronomen() + " " + kunde.getPerson());
			
			for(int i = 0; i < angebot.getAnzPos(); i++ ) {
				anPos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Angebot.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal menge = (BigDecimal) Angebot.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(angebot);
		            BigDecimal ep = (BigDecimal) Angebot.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(angebot);
		            sAnTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            anText[i].setCellValue(art);
					anAnz[i].setCellValue(menge.doubleValue());
					anEPreis[i].setCellValue(ep.doubleValue());
					anGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			ExportHelper.replaceCellValue(wb, ws, "{anSumme}", angebot.getNetto().doubleValue());
			
			ExportHelper.replaceCellValue(wb, ws, "{Bank}", bank.getBankName());
			ExportHelper.replaceCellValue(wb, ws, "{IBAN}", FormatIBAN(bank.getIban()));
			ExportHelper.replaceCellValue(wb, ws, "{BIC}", bank.getBic());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (angebot.getPage2() == 0 && key != null && key.contains("{LBvorh}")) {
			        val = "";
			    } else if (val != null) {
			        val = val.replace("{OwnerName}", ExportHelper.getKontaktName());
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
			logger.error("anExport(String sNr) - " + e);
		} catch (IOException e) {
			logger.error("anExport(String sNr) - " + e);
		}
		//#######################################################################
		// Datei link.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\link.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("anExport(String sNr) - link.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// Datei als pdf speichern
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);
		ErzeugePDF.setPdfMetadata(sNr, "AN", sPdfOut);

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
		
		fileStore.setIdNummer(angebot.getIdNummer()); // Angebotsnummer als Index für fileStore schreiben
		fileStore.setJahr(angebot.getJahr()); // Jahr in fileStore schreiben
		
		//#######################################################################
		// wenn erforderlich Leistungsbeschreibung.pdf erzeugen
		//#######################################################################
		if (angebot.getPage2() == 1) {
			ErzeugeLeistungsbeschreibung.doLeistungsbeschreibung(angebot, sPdfDesc);
			String DescNamePath = sPdfDesc;
			File DescFn = new File(DescNamePath);
			
			String DescName = DescFn.getName();
			fileStore.setAddFileName01(DescName); // Dateiname übergeben
			
			Path DescPath = Paths.get(DescNamePath);
			fileStore.setAddFile01(Files.readAllBytes(DescPath)); // ByteArray für Dateiinhalt
		}
		
		//#######################################################################
		// Datei in DB speichern
		//#######################################################################
		String PdfNamePath = sPdfOut;
		File PdfFn = new File(PdfNamePath);
		
		String PdfName = PdfFn.getName();
		fileStore.setAnFileName(PdfName);
		
		Path PdfPath = Paths.get(PdfNamePath);
		fileStore.setAnPdfFile(Files.readAllBytes(PdfPath)); // ByteArray für Dateiinhalt
		
		fileStoreRepository.save(fileStore); // Datei(en) in DB speichern
		
		//#######################################################################
		// Status des Angebots ändern
		//#######################################################################
		
		angebot.setState(angebot.getState() + 10); // Zustand gedruckt setzen
		AngebotRepository angebotRepository = new AngebotRepository();
		angebotRepository.update(angebot);

		//#######################################################################
		// Ursprungs-Excel und -pdf löschen
		//#######################################################################
		boolean bLockedpdf = Einstellungen.isLocked(sPdfOut);
		boolean bLockedDesc = Einstellungen.isLocked(sPdfDesc);
		boolean bLockedxlsx = Einstellungen.isLocked(sExcelOut);
		while(bLockedpdf || bLockedDesc || bLockedxlsx) {
			System.out.println("warte auf Dateien ...");
		}
		File xlFile = new File(sExcelOut);
		File pdFile = new File(sPdfOut);
		File descFile = new File(sPdfDesc);
		if(xlFile.delete() && pdFile.delete() && descFile.delete()) {

		}else {
			logger.error("anExport(String sNr) - xlsx- und pdf-Datei konnte nicht gelöscht werden");
		}
	}

}

