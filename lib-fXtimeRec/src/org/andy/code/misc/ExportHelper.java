package org.andy.code.misc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.andy.code.dataStructure.entity.Owner;
import org.andy.code.dataStructure.repository.OwnerRepository;
import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExportHelper {
	
	private static App a = new App();
	private static final String FOOTER_STYLE = "&\"Arial,Regular\"&9&K7F7F7F";
	
	private static String footerLeft;
	private static String footerCenter;

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
	
	@SuppressWarnings("unused")
	public static void applyFooter(XSSFWorkbook wb, Sheet ws) {

        List<String> editOwner = readOwner();

        Footer footer = ws.getFooter();
        footer.setLeft(FOOTER_STYLE + ExportHelper.getFooterLeft());
        footer.setCenter(FOOTER_STYLE + ExportHelper.getFooterCenter());
        footer.setRight(FOOTER_STYLE + a.VERSION);

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
	
	public static String getFooterLeft() {
		return footerLeft;
	}

	public static String getFooterCenter() {
		return footerCenter;
	}

}
