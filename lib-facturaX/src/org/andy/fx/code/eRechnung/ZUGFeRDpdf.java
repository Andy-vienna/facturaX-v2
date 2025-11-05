package org.andy.fx.code.eRechnung;

import static org.andy.fx.code.misc.FileSelect.chooseFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;

import javax.swing.JOptionPane;

import org.mustangproject.FileAttachment;
import org.mustangproject.Invoice;
import org.mustangproject.ZUGFeRD.Profiles;
import org.mustangproject.ZUGFeRD.ZUGFeRDExporterFromA3;
import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.App;

public class ZUGFeRDpdf {

	private static App a = new App();
	
	private static String[] SENDER;

	@SuppressWarnings({ "resource" })
	public static void generateZUGFeRDpdf(Rechnung rechnung, Bank bank, Kunde kunde, Owner owner, String sPdfInput, String sFeRDpdf) throws ParseException, IOException {

		String[] sAttachment = new String[10];

		Invoice i = RechnungsDaten.doInvoice(rechnung, bank, kunde, owner);
		SENDER = RechnungsDaten.getSENDER();

		int dialogButton = 0;
		dialogButton = JOptionPane.showConfirmDialog (null, "Soll eine Anlage angefügt werden ?","Attachment", dialogButton);
		if(dialogButton == JOptionPane.YES_OPTION) {
			sAttachment[0] = chooseFile(Einstellungen.getAppSettings().work);
			for(int num = 1; num < 10; num++) {
				dialogButton = JOptionPane.showConfirmDialog (null, num + "/10 Anlagen vorhanden, soll eine weitere angefügt werden ?","Attachment", dialogButton);
				if(dialogButton == JOptionPane.YES_OPTION) {
					sAttachment[num] = chooseFile(Einstellungen.getAppSettings().work);
				}
				if(dialogButton == JOptionPane.NO_OPTION) {
					break;
				}
			}
			ZUGFeRDExporterFromA3 ze = new ZUGFeRDExporterFromA3().load(sPdfInput)
					.setProfile(Profiles.getByName("EN16931"))
					.setCreatorTool(a.NAME + a.VERSION)
					.setProducer(a.NAME + a.VERSION)
					.setCreator(a.NAME + a.VERSION)
					.ignorePDFAErrors();
			int anz = 0;
			while(sAttachment[anz] != null) {
				String fileName = Paths.get(sAttachment[anz]).getFileName().toString();
				byte[] attachmentContents = Files.readAllBytes(Paths.get(sAttachment[anz]));
				String attachmentMime = Files.probeContentType(Paths.get(sAttachment[anz]));
				FileAttachment appendFile = new FileAttachment(fileName, attachmentMime, "Unspecified", attachmentContents).setDescription("Anlage: " + fileName);
				ze.attachFile(appendFile);
				i.embedFileInXML(appendFile);
				anz++;
				if(anz > 9) {
					break;
				}
			}
			ze.setCreatorTool(a.NAME);
			ze.setTransaction(i);
			ze.export(sFeRDpdf);
		} else if(dialogButton == JOptionPane.NO_OPTION) {
			ZUGFeRDExporterFromA3 ze = new ZUGFeRDExporterFromA3().load(sPdfInput)
					.setProfile(Profiles.getByName("EN16931"))
					.setCreatorTool(a.NAME + a.VERSION)
					.setProducer(a.NAME + a.VERSION)
					.setCreator(a.NAME + a.VERSION)
					.ignorePDFAErrors();
			ze.setCreatorTool(a.NAME);
			ze.setTransaction(i);
			ze.export(sFeRDpdf);
		}
	}

	/*
	private static String chooseFileNameAttachment() {

		JFileChooser tmpChooser = new JFileChooser();
		tmpChooser.setDialogTitle("Dateianlage auswählen");
		tmpChooser.setAcceptAllFileFilterUsed(false);
		tmpChooser.setFileFilter(new FileNameExtensionFilter("Dateiauswahl", "pdf", "jpg", "png", "xlsx", "csv"));
		tmpChooser.setCurrentDirectory(new File(LoadData.workPath));
		int choiceOf = tmpChooser.showOpenDialog(null);
		if(choiceOf == JFileChooser.APPROVE_OPTION) {
			File tempLoc = tmpChooser.getSelectedFile();
			String sFileName = tempLoc.getAbsolutePath();
			return sFileName;
		}
		return null;
	}
	 */

	/**
	 * @return the sENDER
	 */
	public static String[] getSENDER() {
		return SENDER;
	}

	/**
	 * @param sENDER the sENDER to set
	 */
	public static void setSENDER(String[] sENDER) {
		SENDER = sENDER;
	}
}
