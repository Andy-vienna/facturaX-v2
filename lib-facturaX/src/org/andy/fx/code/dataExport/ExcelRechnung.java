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
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import javax.swing.JOptionPane;

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.eRechnung.XRechnungXML;
import org.andy.fx.code.eRechnung.ZUGFeRDpdf;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.ExportHelper;
import org.andy.fx.code.misc.Identified;
import org.andy.fx.code.qr.ZxingQR;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.zxing.WriterException;

public class ExcelRechnung implements Identified {

	public static final String CLASS_ID = ExcelRechnung.class.getSimpleName();
	private static final Logger logger = LogManager.getLogger(ExcelRechnung.class);

	private static final int START_ROW_OFFSET = 16;
	private static final int COLUMN_A = 0;
	private static final int COLUMN_B = 1;
	private static final int COLUMN_C = 2;
	private static final int COLUMN_D = 3;
	private static final int COLUMN_F = 5;

	private static final String ZUGFeRD = "ZUGFeRD";
	private static final String XRECHNUNG = "XRechnung";
	
	private static String taxNote = "";
	private static String[] sReTxt = new String[12];
	private static double[] dAnz = new double[12];
	private static double[] dEp = new double[12];
	
	private ExcelRechnung() {} // Instanzierung verhindern

	//###################################################################################################################################################
	// Rechnung erzeugen und als pdf exportieren
	//###################################################################################################################################################

	public static void reExport(String sNr) throws Exception {
		
		String sExcelIn = Einstellungen.getAppSettings().tplBill;
		String sExcelOut = Einstellungen.getAppSettings().work + "Rechnung_" + sNr + ".xlsx";
		String sPdfOut = Einstellungen.getAppSettings().work + "Rechnung_" + sNr + ".pdf";

		DecimalFormat formatter = new DecimalFormat("#.##");

		final Cell rePos[] = new Cell[12];
		final Cell reText[] = new Cell[12];
		final Cell reAnz[] = new Cell[12];
		final Cell reEPreis[] = new Cell[12];
		final Cell reGPreis[] = new Cell[12];

		Rechnung rechnung = ExportHelper.loadRechnung(sNr);
		Kunde kunde = ExportHelper.kundeData(rechnung.getIdKunde());
		String adressat = ExportHelper.kundeAnschrift(rechnung.getIdKunde());
		Bank bank = ExportHelper.bankData(rechnung.getIdBank());
		
		String[][] txtBaustein = ExportHelper.findText(CLASS_ID);

		//#######################################################################
		// Rechnungs-Excel erzeugen
		//#######################################################################
		try (FileInputStream inputStream = new FileInputStream(sExcelIn);
				OutputStream fileOut = new FileOutputStream(sExcelOut)) {

			XSSFWorkbook wb = new XSSFWorkbook(inputStream);
			Sheet ws = wb.getSheet("Rechnung");

			//#######################################################################
			// Owner-Informationen in die Excel-Datei schreiben
			//#######################################################################
			ExportHelper.applyOwnerAndFooter(wb, ws);
			
			//#######################################################################
			// Zellen in Tabelle Enummerieren
			//#######################################################################
			for(int i = 0; i < rechnung.getAnzPos(); i++ ) { //Rechnungspositionen B, C, D, F Zeile 17-28
				int j = i + START_ROW_OFFSET;
				rePos[i] = ws.getRow(j).getCell(COLUMN_A); //Position
				reText[i] = ws.getRow(j).getCell(COLUMN_B); //Text
				reAnz[i] = ws.getRow(j).getCell(COLUMN_C); //Menge
				reEPreis[i] = ws.getRow(j).getCell(COLUMN_D); //E-Preis
				reGPreis[i] = ws.getRow(j).getCell(COLUMN_F); //G-Preis
			}

			//#######################################################################
			// Zellwerte beschreiben
			//#######################################################################
			ExportHelper.replaceCellValue(wb, ws, "{reAdresse}", adressat);
			ExportHelper.replaceCellValue(wb, ws, "{reDatum}", rechnung.getDatum().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
			ExportHelper.replaceCellValue(wb, ws, "{reNummer}", rechnung.getIdNummer());
			ExportHelper.replaceCellValue(wb, ws, "{reLZ}", rechnung.getlZeitr());
			ExportHelper.replaceCellValue(wb, ws, "{reKdUID}", kunde.getUstid());
			ExportHelper.replaceCellValue(wb, ws, "{reDuty}", kunde.getPronomen() + " " + kunde.getPerson());
			ExportHelper.replaceCellValue(wb, ws, "{reRef}", rechnung.getRef());
			
			for(int i = 0; i < rechnung.getAnzPos(); i++ ) {
				rePos[i].setCellValue(String.valueOf(i + 1));
				try {
					String art = (String) Rechnung.class.getMethod("getArt" + String.format("%02d", i + 1)).invoke(rechnung);
		            BigDecimal menge = (BigDecimal) Rechnung.class.getMethod("getMenge" + String.format("%02d", i + 1)).invoke(rechnung);
		            BigDecimal ep = (BigDecimal) Rechnung.class.getMethod("getePreis" + String.format("%02d", i + 1)).invoke(rechnung);
		            sReTxt[i] = art; dAnz[i] = menge.doubleValue(); dEp[i] = ep.doubleValue();
		            reText[i].setCellValue(art);
					reAnz[i].setCellValue(menge.doubleValue());
					reEPreis[i].setCellValue(ep.doubleValue());
					reGPreis[i].setCellValue(menge.multiply(ep).doubleValue());
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					System.out.println(e.getMessage());
				}
			}
			ExportHelper.replaceCellValue(wb, ws, "{reNetto}", rechnung.getNetto().doubleValue());
			ExportHelper.replaceCellValue(wb, ws, "{reSteuersatz}", kunde.getTaxvalue() + "%");
			ExportHelper.replaceCellValue(wb, ws, "{reUSt}", rechnung.getUst().doubleValue());
			ExportHelper.replaceCellValue(wb, ws, "{reSumme}", rechnung.getBrutto().doubleValue());
			
			ExportHelper.replaceCellValue(wb, ws, "{Bank}", bank.getBankName());
			ExportHelper.replaceCellValue(wb, ws, "{IBAN}", FormatIBAN(bank.getIban()));
			ExportHelper.replaceCellValue(wb, ws, "{BIC}", bank.getBic());
			
			for (int x = 0; x < txtBaustein.length; x++) {
			    String key = txtBaustein[x][0]; String val = txtBaustein[x][1];

			    if (val != null) {
			    	val = val.replace("{Tage}", kunde.getZahlungsziel());
			    	val = val.replace("{Skontowert-1}", rechnung.getSkonto1wert().multiply(BD.HUNDRED).setScale(1, RoundingMode.HALF_UP).toString());
					val = val.replace("{Skontotage-1}", String.valueOf(rechnung.getSkonto1tage()));
					val = val.replace("{Skontowert-2}", rechnung.getSkonto2wert().multiply(BD.HUNDRED).setScale(1, RoundingMode.HALF_UP).toString());
					val = val.replace("{Skontotage-2}", String.valueOf(rechnung.getSkonto2tage()));
			    }
			    
			    if (kunde.getTaxvalue().equals("0")) {
			    	if (key.contains("{steuerfrei}") && rechnung.getRevCharge() == 1) val = ""; // Steuerfrei-Hinweis ausblenden
				    if (key.contains("{RevCharge}") && rechnung.getRevCharge() == 0) val = ""; // Steuerfrei-Hinweis ausblenden
			    } else {
			    	if (key.contains("{steuerfrei}")) val = "";
				    if (key.contains("{RevCharge}")) val = "";
			    }
			    if (key.contains("{steuerfrei}") && !val.isEmpty()) { // Steuerhinweis für eRechnung
			    	taxNote = val;
			    } else if (key.contains("{RevCharge}") && !val.isEmpty()) {
			    	taxNote = val;
			    }
			    
			    if(kunde.getZahlungsziel().equals("0")) {
			    	if (key.contains("{ZahlZiel}")) val = " "; // Zahlungsziel x Tage ausblenden
				} else {
					if (key.contains("{sofort}")) val = " "; // Zahlungsziel
				}
			    
			    if(rechnung.getSkonto1() == 1) { // Skontovereinbarung, nur Skonto 1
			    	if (rechnung.getSkonto2() == 0 && key.contains("{Skonto2}")) val = " ";
			    } else {
			    	if (key.contains("{SkKopf}")) val = " ";
			    	if (key.contains("{Skonto1}")) val = " ";
			    	if (key.contains("{Skonto2}")) val = " ";
			    }
			    
			    txtBaustein[x][1] = val;
			    
			    ExportHelper.replaceCellValue(wb, ws, key, val); // Texte in Zellen schreiben

			}

			//#######################################################################
			// QR Code erzeugen und im Anwendungsverzeichnis ablegen
			//#######################################################################
			String sBrutto = formatter.format(rechnung.getBrutto());
			try {
				ZxingQR.makeQR(Einstellungen.getAppSettings().qrScheme, bank.getKtoName(), bank.getIban(), bank.getBic(), sBrutto.replace(",", "."), sNr);
			} catch (WriterException e) {
				logger.error("makeQR(...) - " + e);
			} catch (IOException e) {
				logger.error("makeQR(...) - " + e);
			}
			//#######################################################################
			// erzeugten QR Code als png-Datei einlesen
			//#######################################################################
			ExportHelper.placeQRinExcel(wb, ws, "qr.png");
			
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
		// Datei qr.png wieder löschen
		//#######################################################################
		File qrFile = new File(System.getProperty("user.dir") + "\\qr.png");
		if(qrFile.delete())
		{

		}else {
			logger.error("reExport(String sNr) - qr.png konnte nicht gelöscht werden");
		}
		//#######################################################################
		// PDF-A1 Datei erzeugen
		//#######################################################################
		ErzeugePDF.toPDF(sExcelOut, sPdfOut);

		//#######################################################################
		// eRechnung erstellen nach hinterlegtem Format (ZUGFeRD oder XRechnung)
		//#######################################################################
		String sFile = null; boolean bResult = false;
		
		switch(kunde.geteBillTyp()) {
		case ZUGFeRD:
			sFile = Einstellungen.getAppSettings().work + "Rechnung_" + sNr + "_ZUGFeRD.pdf";

			try {
				ZUGFeRDpdf.generateZUGFeRDpdf(rechnung, bank, kunde, ExportHelper.getOwner(), sPdfOut, sFile);
			} catch (ParseException | IOException e) {
				logger.error("error generating zugferd - " + e);
			}
			bResult = true;
			break;

		case XRECHNUNG:
			sFile = Einstellungen.getAppSettings().work + "Rechnung_" + sNr + "_XRechnung.xml";

			try {
				XRechnungXML.generateXRechnungXML(rechnung, bank, kunde, ExportHelper.getOwner(), sFile);
			} catch (ParseException | IOException e) {
				logger.error("error generating xrechnung - " + e);
			}
			bResult = true;
			break;

		default:
			return;
		}
		
		if (bResult) {
			boolean bLockedoutput = Einstellungen.isLocked(sFile);
			while(bLockedoutput) {
				System.out.println("warte auf Datei ...");
			}
			
			//#######################################################################
			// Datei in DB speichern
			//#######################################################################
			
			String FileNamePath = sFile;
			File fn = new File(FileNamePath);
			String FileName = fn.getName();
			
			FileStoreRepository fileStoreRepository = new FileStoreRepository();
			FileStore fileStore = new FileStore();
			
			fileStore.setIdNummer(rechnung.getIdNummer());
			fileStore.setJahr(rechnung.getJahr());
			fileStore.setReFileName(FileName);
			
			Path path = Paths.get(FileNamePath);
			fileStore.setRePdfFile(Files.readAllBytes(path)); // ByteArray für Dateiinhalt
			
			fileStoreRepository.save(fileStore); // Datei in DB speichern
			
			//#######################################################################
			// Status der Rechnung ändern
			//#######################################################################
			
			rechnung.setState(rechnung.getState() + 10); // Zustand gedruckt setzen
			RechnungRepository rechnungRepository = new RechnungRepository();
			rechnungRepository.update(rechnung);
			
			//#######################################################################
			// Ursprungs-Excel und -pdf löschen
			//#######################################################################
			boolean bLockedpdf = Einstellungen.isLocked(sPdfOut);
			boolean bLockedxlsx = Einstellungen.isLocked(sExcelOut);
			boolean bLockedout = Einstellungen.isLocked(sFile);
			while(bLockedpdf || bLockedxlsx || bLockedout) {
				System.out.println("warte auf Dateien ...");
			}
			File xlFile = new File(sExcelOut);
			File pdFile = new File(sPdfOut);
			File outFile = new File(sFile);
			if(xlFile.delete() && pdFile.delete() && outFile.delete()) {

			}else {
				logger.error("reExport(...) - Dateien konnten nicht gelöscht werden");
			}
		} else {
			JOptionPane.showMessageDialog(null, "Problem beim Drucken der Rechnung", "Rechnung drucken", JOptionPane.INFORMATION_MESSAGE);
			logger.error("Rechnung nicht gedruckt !");
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String[] getsReTxt() {
		return sReTxt;
	}

	public static double[] getdAnz() {
		return dAnz;
	}

	public static double[] getdEp() {
		return dEp;
	}

	public static String getTaxNote() {
		return taxNote;
	}

}

