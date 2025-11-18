package org.andy.code.dataExport;

import org.andy.code.misc.App;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.ComThread;
import com.jacob.com.Dispatch;
import com.spire.pdf.PdfDocument;
import com.spire.pdf.PdfDocumentInformation;
import com.spire.pdf.conversion.PdfStandardsConverter;

public class CreatePdf {

	private final Logger logger = LogManager.getLogger(CreatePdf.class);
	private App a = new App();

	/** Excel als pdf exportieren
	 * @param sFileExcel
	 * @param sFilePDF
	 * @throws OwnException
	 */
	public void toPDF(String sFileExcel, String sFilePDF) {
		ActiveXComponent excel = new ActiveXComponent("Excel.Application");
		Dispatch workbook = null;
		try {
			excel.setProperty("Visible", false); // Excel im Hintergrund starten
			Dispatch workbooks = excel.getProperty("Workbooks").toDispatch();
			workbook = Dispatch.call(workbooks, "Open", sFileExcel).toDispatch();

			Dispatch.call(workbook, "ExportAsFixedFormat", 0, sFilePDF); // Exportieren als PDF

		} catch (Exception e) {
			logger.error("toPDF(String sFileExcel, String sFilePDF) - " + e);
		} finally {
			Dispatch.call(workbook, "Close", false); // Arbeitsmappe schließen
			excel.invoke("Quit"); // Excel beenden
			excel = null;
			System.gc();
			ComThread.Release();
		}
		PdfStandardsConverter converter = new PdfStandardsConverter(sFilePDF); // pdf zu pdf-A wandeln
		converter.toPdfA1A(sFilePDF);
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
