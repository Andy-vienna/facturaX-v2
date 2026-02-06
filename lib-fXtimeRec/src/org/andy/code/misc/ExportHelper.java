package org.andy.code.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andy.code.dataStructure.entity.Owner;
import org.andy.code.dataStructure.repository.OwnerRepository;
import org.apache.commons.io.IOUtils;
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
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private ExportHelper() {} // Instanzierung verhindern
	
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
	    
	    footerLeft = owner.getName();
		footerCenter = owner.getKontaktTel() + " | " + owner.getKontaktMail();
	    
	    return owTmp;
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
	
	@SuppressWarnings("unused")
	public static void applyFooter(XSSFWorkbook wb, Sheet ws) {

        List<String> editOwner = readOwner();

        Footer footer = ws.getFooter();
        footer.setLeft(FOOTER_STYLE + ExportHelper.getFooterLeft());
        footer.setCenter(FOOTER_STYLE + ExportHelper.getFooterCenter());
        footer.setRight(FOOTER_STYLE + a.VERSION);

    }
	
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
