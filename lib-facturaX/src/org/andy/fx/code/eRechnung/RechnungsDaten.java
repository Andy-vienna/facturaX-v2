package org.andy.fx.code.eRechnung;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.TextFormatter.*;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.andy.fx.code.dataExport.ExcelRechnung;
import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Owner;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mustangproject.BankDetails;
import org.mustangproject.CashDiscount;
import org.mustangproject.Contact;
import org.mustangproject.Invoice;
import org.mustangproject.Item;
import org.mustangproject.Product;
import org.mustangproject.TradeParty;

public class RechnungsDaten {

	private static final Logger logger = LogManager.getLogger(RechnungsDaten.class);

	private static String[] SENDER = new String[11];
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static Invoice doInvoice(Rechnung rechnung, Bank bank, Kunde kunde, Owner owner) {
		try {
			return setInvoice(rechnung, bank, kunde, owner);
		} catch (ParseException | IOException e) {
			logger.error("doInvoice(...) - " + e);
		}
		return null;
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private static Invoice setInvoice(Rechnung rechnung, Bank bank, Kunde kunde, Owner owner) throws ParseException, IOException {

		SENDER[0] = owner.getName();
		SENDER[1] = owner.getAdresse();
		SENDER[2] = owner.getPlz();
		SENDER[3] = owner.getOrt();
		SENDER[4] = owner.getLand();
		SENDER[5] = owner.getUstid();
		SENDER[6] = owner.getKontaktName();
		SENDER[7] = owner.getKontaktTel();
		SENDER[8] = owner.getKontaktMail();
		SENDER[9] = owner.getCurrency();
		SENDER[10] = owner.getTaxid();
		
		String SEND_COUNTRY = null;
		switch(SENDER[4].toUpperCase()) {
		case "DE":
			SEND_COUNTRY = "DE";
			break;
		case "AT":
			SEND_COUNTRY = "AT";
			break;
		}

		long issue = 0;
		long due = 0;
		long start = 0;
		long end = 0;
		Item[] position = new Item[12];
		String RECV_COUNTRY = null;
		String RECV_NAME = kunde.getName();
		String RECV_ADRESS = kunde.getStrasse();
		String RECV_ZIP = kunde.getPlz();
		String RECV_TOWN = kunde.getOrt();
		switch(kunde.getLand().toUpperCase()) {
		case "DE":
			RECV_COUNTRY = "DE";
			break;
		case "AT":
			RECV_COUNTRY = "AT";
			break;
		}
		String RECV_DUTY = kunde.getPerson();
		String RECV_VAT = kunde.getUstid();
		String RECV_TAX = kunde.getTaxvalue();
		String LEITWEG_ID = kunde.getLeitwegId();
		String RECV_MAIL = kunde.geteBillMail();
		String RECV_PHONE = kunde.geteBillPhone();
		String RE_NR = rechnung.getIdNummer();
		String TAX_NOTE = ExcelRechnung.getTaxNote();
		String BANK_IBAN = bank.getIban();
		String BANK_BIC = bank.getBic();
		String BANK_HOLDER = bank.getKtoName();

		BankDetails bDetail = new BankDetails(BANK_IBAN, BANK_BIC).setAccountName(BANK_HOLDER);
		Contact cSend = new Contact(SENDER[6], SENDER[7], SENDER[8]);
		Contact cRecv = new Contact(RECV_DUTY, RECV_PHONE, RECV_MAIL);
		TradeParty sender = new TradeParty(SENDER[0], SENDER[1], SENDER[2], SENDER[3], SEND_COUNTRY).setVATID(SENDER[5])
				.setContact(cSend).addBankDetails(bDetail).setEmail(SENDER[8]);
		TradeParty recipient = new TradeParty(RECV_NAME, RECV_ADRESS, RECV_ZIP, RECV_TOWN, RECV_COUNTRY).setVATID(RECV_VAT)
				.setContact(cRecv).setEmail(RECV_MAIL);

		// Leisutngszeitraum zerlegen
		String LZvon = cutBack(rechnung.getlZeitr(), "-", 1);
		String LZbis = cutFront(rechnung.getlZeitr(), "-", 1);
		// Konvertierung von Datums
		Date reDate = new SimpleDateFormat("yyyy-MM-dd").parse(rechnung.getDatum().toString());
		Date vonDate = new SimpleDateFormat("dd.MM.yyyy").parse(LZvon);
		Date bisDate = new SimpleDateFormat("dd.MM.yyyy").parse(LZbis);
		
		int ziel = Integer.valueOf(kunde.getZahlungsziel());
		issue = dateInMilis(reDate); // Rechnungsdatum
		due = addDaysInMilis(reDate, ziel); // Fälligkeit

		start = dateInMilis(vonDate); // Lieferdatum (aus Leistungszeitraum von)
		end = dateInMilis(bisDate); // Lieferdatum (aus Leistungszeitraum von)

		int iAnz = rechnung.getAnzPos();
		String[] posText = ExcelRechnung.getsReTxt(); double[] posAnz = ExcelRechnung.getdAnz(); double[] posEp = ExcelRechnung.getdEp();
		for(int x = 0; x < iAnz; x++) {
			//new Item(new Product("Artikeltext", "Artikelbeschreibung", "C62", new BigDecimal(Steuersatz), new BigDecimal(E-Preis),  new BigDecimal(Menge))
			position[x] = new Item(new Product(posText[x], "", "C62", parseStringToBigDecimalSafe(RECV_TAX, LocaleFormat.AUTO)), BigDecimal.valueOf(posEp[x]), BigDecimal.valueOf(posAnz[x]));
		}
		Invoice iInv = new Invoice()
				.setNumber(RE_NR) // Rechnungsnummer
				.setIssueDate(new Date(issue)) // Rechnungsdatum
				.setDueDate(new Date(due)) // Fälligkeit
				.setDeliveryDate(new Date(start)) // Liefertermin od. Leistungszeitraum
				.setSender(sender) // Rechnungssteller
				.setRecipient(recipient) //Rechnungsempfänger
				.setBuyerOrderReferencedDocumentID(rechnung.getRef()) // Kundenreferenz
				.setDetailedDeliveryPeriod(new Date(start), new Date(end)) // Leistungszeitraum
				.setCurrency(SENDER[9]) // Währung
				.setReferenceNumber(LEITWEG_ID); // Leitweg-ID
		if(RECV_COUNTRY != "AT") {
			iInv.addTaxNote(TAX_NOTE);
		}
		for(int i = 0; i < iAnz; i++) {
			iInv.addItem(position[i]);
		}
		if(rechnung.getSkonto1() == 1 && rechnung.getSkonto2() == 0) { // Rechnung mit Skonto 1
			BigDecimal skonto1 = rechnung.getSkonto1wert().multiply(BD.HUNDRED).setScale(2, RoundingMode.HALF_UP);
			iInv.addCashDiscount(new CashDiscount(skonto1,rechnung.getSkonto1tage())); // Skonto 1
		}
		if(rechnung.getSkonto1() == 1 && rechnung.getSkonto2() == 1) { // Rechnung mit Skonto 2
			BigDecimal skonto1 = rechnung.getSkonto1wert().multiply(BD.HUNDRED).setScale(2, RoundingMode.HALF_UP);
			BigDecimal skonto2 = rechnung.getSkonto2wert().multiply(BD.HUNDRED).setScale(2, RoundingMode.HALF_UP);
			iInv.addCashDiscount(new CashDiscount(skonto1,rechnung.getSkonto1tage())); // Skonto 1
			iInv.addCashDiscount(new CashDiscount(skonto2,rechnung.getSkonto2tage())); // Skonto 2
		}
		return iInv;
	}
	
	//###################################################################################################################################################

	private static long dateInMilis(Date d) throws ParseException {
		long timestamp = d.getTime();
		return timestamp;
	}
	
	//###################################################################################################################################################

	private static long addDaysInMilis(Date d, int add) throws ParseException {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(d);
		calendar.add(Calendar.DAY_OF_MONTH, add); // add n days to calendar instance
		Date future = calendar.getTime(); // get the date instance
		long timestamp = future.getTime();
		return timestamp;
	}

	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static String[] getSENDER() {
		return SENDER;
	}

}
