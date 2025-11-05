package org.andy.fx.code.main.overview.table;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;

public class LadeRechnung {
    
    private static BigDecimal sumOpen = BD.ZERO;
    private static BigDecimal sumPayed = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {
		
		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		sumOpen = BD.ZERO; sumPayed = BD.ZERO;
		
		RechnungRepository rechnungRepository = new RechnungRepository();
	    List<Rechnung> rechnungListe = new ArrayList<>();
		rechnungListe.addAll(rechnungRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [rechnungListe.size()][9];

		for (int i = 0; i < rechnungListe.size(); i++){
			Rechnung rechnung = rechnungListe.get(i);
			
			String status = switch(rechnung.getState()) {
				case 0 -> "storniert";
				case 1 -> "erstellt";
				case 11 -> "gedruckt";
				case 111 -> "bezahlt";
				case 112 -> "bez. Skonto 1";
				case 113 -> "bez. Skonto 2";
				case 211 -> "Zahlungserinnerung";
				case 311 -> "Mahnstufe 1";
				case 411 -> "Mahnstufe 2";
				default -> "-----";
			};
			
			LocalDate date = LocalDate.parse(rechnung.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(rechnung.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(rechnung.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(rechnung.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = rechnung.getIdNummer();
			sTemp[i][1] = status;
			sTemp[i][2] = datum;
			sTemp[i][3] = rechnung.getlZeitr();
			sTemp[i][4] = rechnung.getRef();
			sTemp[i][5] = LadeDatenHelper.searchKunde(rechnung.getIdKunde());
			sTemp[i][6] = netto;
			sTemp[i][7] = ust;
			sTemp[i][8] = brutto;
			
			if (rechnung.getState() > 0) { // nicht storniert
				switch (rechnung.getState()) {
				case 11 -> sumOpen = sumOpen.add(rechnung.getNetto());
				case 111 -> sumPayed = sumPayed.add(rechnung.getNetto());
				}
			}
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static BigDecimal getSumOpen() {
		return sumOpen;
	}

	public static BigDecimal getSumPayed() {
		return sumPayed;
	}
	
}
