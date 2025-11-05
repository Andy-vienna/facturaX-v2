package org.andy.fx.code.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.entityMaster.Text;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.OwnerRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.gui.main.HauptFenster;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportHelper {
	
	private static final Logger logger = LogManager.getLogger(ExportHelper.class);
	private static App a = new App();
	private static final String FOOTER_STYLE = "&\"Arial,Regular\"&9&K7F7F7F";
	
	private static String senderOwner;
	private static String footerLeft;
	private static String footerCenter;
	private static String kontaktName;
	private static String steuerNummer;
	
	private static Owner owner = new Owner();
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static Angebot loadAngebot(String AnNr) {
		return readAngebot(AnNr);
	}
	
	public static Rechnung loadRechnung(String ReNr) {
		return readRechnung(ReNr);
	}
	
	public static Bestellung loadBestellung(String BeNr) {
		return readBestellung(BeNr);
	}
	
	public static Lieferschein loadLieferschein(String LsNr) {
		return readLieferschein(LsNr);
	}
	
	public static Kunde kundeData(String KdNr) {
		return readKunde(KdNr);
	}
	
	public static String kundeAnschrift(String KdNr) {
		return formatKunde(KdNr);
	}
	
	public static Lieferant lieferantData(String LiNr) {
		return readLieferant(LiNr);
	}
	
	public static String lieferantAnschrift(String LiNr) {
		return formatLieferant(LiNr);
	}
	
	public static Bank bankData(int id) {
		return readBank(id);
	}
	
	public static ArrayList<String> ownerData(){
		return readOwner();
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private ExportHelper() {} // Instanzierung verhindern
	
	//###################################################################################################################################################
	
	private static Angebot readAngebot(String AnNr) {
		AngebotRepository angebotRepository = new AngebotRepository();
		return angebotRepository.findById(AnNr);
	}
	
	//###################################################################################################################################################
	
	private static Rechnung readRechnung(String ReNr) {
		RechnungRepository rechnungRepository = new RechnungRepository();
		return rechnungRepository.findById(ReNr);
	}
	
	//###################################################################################################################################################
	
	private static Bestellung readBestellung(String BeNr) {
		BestellungRepository bestellungRepository = new BestellungRepository();
		return bestellungRepository.findById(BeNr);
	}
	
	//###################################################################################################################################################
	
	private static Lieferschein readLieferschein(String LsNr) {
		LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
		return lieferscheinRepository.findById(LsNr);
	}
	
	//###################################################################################################################################################
	
	private static Kunde readKunde(String tmp){
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		for (int m = 0; m < kundeListe.size(); m++) {
			
			Kunde kunde = kundeListe.get(m);
			
			if (kunde.getId() != null && !kunde.getId().isEmpty() && kunde.getId().equals(tmp)) {
				return kunde;
			}
		}
		return null;
	}
	
	private static String formatKunde(String tmp) {
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		for (int m = 0; m < kundeListe.size(); m++) {
			
			Kunde kunde = kundeListe.get(m);
			
			if (kunde.getId() != null && !kunde.getId().isEmpty() && kunde.getId().equals(tmp)) {
				CodeListen cl = new CodeListen();
			    String land = cl.getCountryFromCode(kunde.getLand()).toUpperCase();
				return kunde.getName() + "\n" + kunde.getStrasse() + "\n" + kunde.getPlz() + " " +
						kunde.getOrt() + ", " + land;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static Lieferant readLieferant(String tmp){
		LieferantRepository lieferantRepository = new LieferantRepository();
		List<Lieferant> lieferantListe = new ArrayList<>();
		lieferantListe.addAll(lieferantRepository.findAll());
		
		for (int m = 0; m < lieferantListe.size(); m++) {
			
			Lieferant lieferant = lieferantListe.get(m);
			
			if (lieferant.getId() != null && !lieferant.getId().isEmpty() && lieferant.getId().equals(tmp)) {
				return lieferant;
			}
		}
		return null;
	}
	
	private static String formatLieferant(String tmp) {
		LieferantRepository lieferantRepository = new LieferantRepository();
		List<Lieferant> lieferantListe = new ArrayList<>();
		lieferantListe.addAll(lieferantRepository.findAll());
		
		for (int m = 0; m < lieferantListe.size(); m++) {
			
			Lieferant lieferant = lieferantListe.get(m);
			
			if (lieferant.getId() != null && !lieferant.getId().isEmpty() && lieferant.getId().equals(tmp)) {
				CodeListen cl = new CodeListen();
			    String land = cl.getCountryFromCode(lieferant.getLand()).toUpperCase();
				return lieferant.getName() + "\n" + lieferant.getStrasse() + "\n" + lieferant.getPlz() + " " +
						lieferant.getOrt() + ", " + land;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static Bank readBank(int id) {
		BankRepository bankRepository = new BankRepository();
	    List<Bank> bankListe = new ArrayList<>();
	    bankListe.addAll(bankRepository.findAll());

		for (int m = 0; m < bankListe.size(); m++) {
			
			Bank bank = bankListe.get(m);

			if (bank.getId() != 0 && bank.getId() == id) {
				return bank;
			}
		}
		return null;
	}
	
	//###################################################################################################################################################
	
	private static ArrayList<String> readOwner(){
		OwnerRepository ownerRepository = new OwnerRepository();
	    List<Owner> ownerListe = new ArrayList<>();
	    ownerListe.addAll(ownerRepository.findAll());
	    owner = ownerListe.get(0);
	    
	    CodeListen cl = new CodeListen();
	    String land = cl.getCountryFromCode(owner.getLand()).toUpperCase();
	    
	    ArrayList<String> owTmp = new ArrayList<>();

	    owTmp.add(owner.getName() + "\n");
	    owTmp.add(owner.getAdresse() + " | ");
	    owTmp.add(owner.getPlz() + " ");
	    owTmp.add(owner.getOrt() + " | ");
	    owTmp.add(land + "\n");
	    owTmp.add(owner.getUstid());
	    
	    senderOwner = owner.getName() + ", " + owner.getAdresse() + ", " + owner.getPlz() + " " + owner.getOrt();
	    footerLeft = owner.getName() + " | Bearbeiter: " + HauptFenster.getU();
		footerCenter = owner.getKontaktTel() + " | " + owner.getKontaktMail();
		kontaktName = owner.getKontaktName();
		steuerNummer = owner.getTaxid();
	    
	    return owTmp;
	}
	
	//###################################################################################################################################################
	
	public static String[][] findText(String forDocument) {
		
		String typ = null;
		TextRepository textRepository = new TextRepository();
		List<Text> textListe = new ArrayList<>();
	    textListe.addAll(textRepository.findAll());
	    
	    String[][] tmp = new String[textListe.size()][2];
	    
	    switch(forDocument) {
	    	case "ExcelAngebot"              -> typ = "Angebot";
	    	case "ExcelAngebotRevision"      -> typ = "AngebotRev";
	    	case "ExcelAuftragsbestaetigung" -> typ = "OrderConfirm";
	    	case "ExcelBestellung"           -> typ = "Bestellung";
	    	case "ExcelLieferschein"         -> typ = "Lieferschein";
	    	case "ExcelMahnstufe1"           -> typ = "MahnungStufe1";
	    	case "ExcelMahnstufe2"           -> typ = "MahnungStufe2";
	    	case "ExcelRechnung"             -> typ = "Rechnung";
	    	case "ExcelZahlungserinnerung"   -> typ = "ZahlErin";
	    }
	    
	    for(int i = 0; i < textListe.size(); i++) {
	    	Text tx = textListe.get(i);
	    	try {
	    		tmp[i][0] = (String) Text.class.getMethod("getVarText" + typ).invoke(tx);
	    		tmp[i][1] = (String) Text.class.getMethod("getText" + typ).invoke(tx);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				logger.error("error reading texts from db: " + e);
			}
	    }
	    return tmp;
	}
	
	//###################################################################################################################################################
	
	public static boolean replaceCellValue(XSSFWorkbook wb, Sheet ws, String placeholder, Object target) {
	    final boolean isNumeric; final boolean isXSSF;
	    double dval = 0d; String sval = null; XSSFRichTextString sXSSF = null;

	    if (placeholder == null || target == null) return false;
	    
	    if (target instanceof Number n) {
	        isNumeric = true; isXSSF = false;
	        dval = n.doubleValue();
	    } else if (target instanceof String s) {
	        isNumeric = false; isXSSF = false;
	        sval = s;
	    } else if (target instanceof XSSFRichTextString rt) {
	    	isNumeric = false; isXSSF = true;
	    	sXSSF = rt;
	    } else {
	        throw new IllegalArgumentException("Unerwarteter Typ: " + (target == null ? "null" : target.getClass()));
	    }

	    DataFormatter fmt = new DataFormatter();
	    FormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

	    CellReference start = new CellReference("A1");
	    CellReference end   = new CellReference("G60");

	    for (int r = start.getRow(); r <= end.getRow(); r++) {
	        Row row = ws.getRow(r);
	        for (int c = start.getCol(); c <= end.getCol(); c++) {
	            Cell cell = (row != null) ? row.getCell(c) : null;
	            String text = (cell != null) ? fmt.formatCellValue(cell, eval) : null;

	            if (placeholder.equals(text)) {
	                if (isNumeric && !isXSSF) {
	                    cell.setCellValue(dval);
	                } else if (!isNumeric && !isXSSF) {
	                    cell.setCellValue(sval);
	                } else {
	                	cell.setCellValue(sXSSF);
	                }
	                return true;
	            }
	        }
	    }
	    return false;
	}
	
	//###################################################################################################################################################
	
	public static void applyOwnerAndFooter(XSSFWorkbook wb, Sheet ws) {

        List<String> editOwner = readOwner();

        Footer footer = ws.getFooter();
        footer.setLeft(FOOTER_STYLE + ExportHelper.getFooterLeft());
        footer.setCenter(FOOTER_STYLE + ExportHelper.getFooterCenter());
        footer.setRight(FOOTER_STYLE + a.VERSION);

        // Fonts einmal anlegen
        XSSFFont ownerHead = font(wb, "Arial", (short)24, IndexedColors.GREY_50_PERCENT);
        XSSFFont ownerLine = font(wb, "Arial", (short)12, IndexedColors.GREY_50_PERCENT);
        XSSFFont senderFont = font(wb, "Arial", (short)7,  IndexedColors.GREY_50_PERCENT);

        // Angebotsinhaber (Zeile 0, Spalte colOwner)
        XSSFRichTextString ownerText = new XSSFRichTextString();
        for (int i = 0, n = Math.min(6, editOwner.size()); i < n; i++) {
            ownerText.append(editOwner.get(i), i == 0 ? ownerHead : ownerLine);
        }

        XSSFRichTextString ownerSender = new XSSFRichTextString(senderOwner);
        ownerSender.applyFont(senderFont);
        
        ExportHelper.replaceCellValue(wb, ws, "{Owner}", ownerText);
        ExportHelper.replaceCellValue(wb, ws, "{Sender}", ownerSender);
    }

    private static XSSFFont font(XSSFWorkbook wb, String name, short size, IndexedColors color) {
        XSSFFont f = wb.createFont();
        f.setFontName(name);
        f.setFontHeightInPoints(size);
        f.setColor(color.getIndex());
        return f;
    }
    
    //###################################################################################################################################################
    
    public static void placeQRinExcel(XSSFWorkbook wb, Sheet ws, String picQR) throws FileNotFoundException, IOException {
    	try (FileInputStream is = new FileInputStream(System.getProperty("user.dir") + "\\" + picQR)) {
			byte[] bytes = IOUtils.toByteArray(is);
			int pictureIdx = wb.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
			is.close();
			XSSFCreationHelper helper = wb.getCreationHelper();
			Drawing<?> drawing = ws.createDrawingPatriarch(); //POI Patriarch erstellen als Container, Bildelement hinzufügen
			XSSFClientAnchor anchor = helper.createClientAnchor();
			anchor.setCol1(4); //obere linke Ecke festlegen
			anchor.setRow1(36);
			Picture pict = drawing.createPicture(anchor, pictureIdx);
			pict.resize(0.9, 0.9); //Bild im Faktor 0,9x0,9 zoomen
		}
    }
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static Owner getOwner() {
		return owner;
	}
	
	public static String getSenderOwner() {
		return senderOwner;
	}

	public static String getFooterLeft() {
		return footerLeft;
	}

	public static String getFooterCenter() {
		return footerCenter;
	}

	public static String getKontaktName() {
		return kontaktName;
	}

	public static String getSteuerNummer() {
		return steuerNummer;
	}

}
