package org.andy.fx.code.dataExport;

import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.conversion.PdfStandardsConverter;
import com.spire.pdf.texts.PdfTextReplacer;
import com.spire.xls.*;
import com.spire.doc.*;

import org.andy.fx.code.misc.App;
import org.andy.fx.code.misc.ExportHelper;

public class ErzeugePDF {

	private App a = new App();

	private final String OFFER = "Angebot";
	private final String CONFIRMATION = "Auftragsbestätigung";
	private final String BILL = "Rechnung";
	private final String ORDER = "Bestellung";
	private final String REMINDER = "Zahlungserinnerung";
	private final String TRAVEL = "Spesenabrechnung";
	private final String WORKTIME = "Arbeitszeit";
	private final String UNKNOWN = "unknown";
	
	private final String XLS = "Evaluation Warning : The document was created with Spire.XLS for Java.";
	private final String DOC = "Evaluation Warning : The document was created with Spire.Doc for Java.";
	

	/** Excel als pdf exportieren
	 * @param sFileExcel
	 * @param sFilePDF
	 */
	public void toPDF(String sFileExcel, String sFilePDF) {
        Workbook workbook = new Workbook();
        workbook.loadFromFile(sFileExcel); // load the workbook
        Worksheet sheet = workbook.getWorksheets().get(0); // select the first worksheet out of the loaded workbook

        sheet.getPageSetup().setPaperSize(PaperSizeType.PaperA4); // select layout for A4 pages
        
        sheet.getPageSetup().setFitToPagesWide(1); // shrink to fit on one page
        sheet.getPageSetup().setFitToPagesTall(1);

        sheet.saveToPdf(sFilePDF); // save as pdf
        
        removeWatermark(sFilePDF, XLS); // remove watermark if exist
		
		PdfStandardsConverter converter = new PdfStandardsConverter(sFilePDF); // create pdf-A from pdf
		converter.toPdfA1A(sFilePDF);
	}
	
	public void wordToPDF(String sFileWord, String sFilePDF) {
        Document document = new Document();
        document.loadFromFile(sFileWord); // load the document

        ToPdfParameterList parameters = new ToPdfParameterList();
        parameters.isEmbeddedAllFonts(true); // use all fonts from source file

        document.saveToFile(sFilePDF, parameters); // save as pdf

        removeWatermark(sFilePDF, DOC); // remove watermark if exist

	    PdfStandardsConverter converter = new PdfStandardsConverter(sFilePDF); // create pdf-A from pdf
	    converter.toPdfA1A(sFilePDF);
	}

	public void setPdfMetadata(String sNr, String sTyp, String sPdf) throws Exception {
		String sTitel = decodeTyp(sTyp); // create the title of pdf

		PdfDocument document = new PdfDocument(); // load the pdf document
		document.loadFromFile(sPdf);

		PdfDocumentInformation info = document.getDocumentInformation(); // read the existing document properties
		// create metadata
		info.setAuthor(ExportHelper.getKontaktName());
		info.setTitle(sTitel + " " + sNr);
		info.setSubject(sTitel);
		info.setKeywords(sTitel + "," + sNr + "," + ExportHelper.getKontaktName());
		info.setCreator(a.NAME);

		document.saveToFile(sPdf); // save the pdf
		document.close();

	}

	private String decodeTyp(String sTyp) throws Exception {
		switch(sTyp) {
			case "AN": return OFFER;
			case "AB": return CONFIRMATION;
			case "RE": return BILL;
			case "BE": return ORDER;
			case "ZE": return REMINDER;
			case "SP": return TRAVEL;
			case "AZ": return WORKTIME;
			default  : return UNKNOWN;
		}
	}
	
	private void removeWatermark(String filePath, String watermark) {
        PdfDocument pdf = new PdfDocument();
        pdf.loadFromFile(filePath); // load the generated pdf

        // if the watermark exist, replace it with an empty string
        for (Object pageObj : pdf.getPages()) {
            PdfPageBase page = (PdfPageBase) pageObj;

            PdfTextReplacer replacer = new PdfTextReplacer(page);
            replacer.replaceAllText(watermark, " ");
        }

        pdf.saveToFile(filePath); // save and close the pdf
        pdf.close();
    }
	
}
