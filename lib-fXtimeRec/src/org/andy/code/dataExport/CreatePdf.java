package org.andy.code.dataExport;

import org.andy.code.misc.App;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import com.spire.pdf.PdfPageBase;
import com.spire.pdf.conversion.PdfStandardsConverter;
import com.spire.pdf.texts.PdfTextReplacer;
import com.spire.xls.PaperSizeType;
import com.spire.xls.Workbook;
import com.spire.xls.Worksheet;

public class CreatePdf {

	private App a = new App();
	
	private final String XLS = "Evaluation Warning : The document was created with Spire.XLS for Java.";

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

	public void setPdfMetadata(String sNr, String sPdf) throws Exception {
		//String[] tmp = SQLmasterData.getsArrOwner();

		String sTitel = "Arbeitszeit"; // Titel festlegen

		PdfDocument document = new PdfDocument(); // PDF-Dokument laden
		document.loadFromFile(sPdf);

		PdfDocumentInformation info = document.getDocumentInformation(); // Zugriff auf die Dokumentinformationen
		// Metadaten festlegen
		info.setAuthor("fXtimeRec");
		info.setTitle(sTitel + " " + sNr);
		info.setSubject(sTitel);
		info.setKeywords(sTitel + "," + sNr);
		info.setCreator(a.NAME);

		// PDF speichern
		document.saveToFile(sPdf);
		document.close();

	}

}
