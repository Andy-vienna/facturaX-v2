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

import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;

public class LadeBestellung {
    
    private static BigDecimal sumOpen = BD.ZERO;
    private static BigDecimal sumDelivered = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadBestellung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {
		
		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		sumOpen = BD.ZERO; sumDelivered = BD.ZERO;
		
		BestellungRepository bestellungRepository = new BestellungRepository();
	    List<Bestellung> bestellungListe = new ArrayList<>();
	    bestellungListe.addAll(bestellungRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [bestellungListe.size()][9];

		for (int i = 0; i < bestellungListe.size(); i++){
			Bestellung bestellung = bestellungListe.get(i);
			
			String status = switch(bestellung.getState()) {
				case 0 -> "storniert";
				case 1 -> "erstellt";
				case 11 -> "gedruckt";
				case 51 -> "geliefert";
				default -> "-----";
			};
			
			LocalDate date = LocalDate.parse(bestellung.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(bestellung.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(bestellung.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(bestellung.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = bestellung.getIdNummer();
			sTemp[i][1] = status;
			sTemp[i][2] = datum;
			sTemp[i][3] = bestellung.getRef();
			sTemp[i][4] = LadeDatenHelper.searchLieferant(bestellung.getIdLieferant());
			sTemp[i][5] = netto;
			sTemp[i][6] = ust;
			sTemp[i][7] = brutto;
			
			if (bestellung.getState() > 0) { // nicht storniert
				switch (bestellung.getState()) {
				case 11 -> sumOpen = sumOpen.add(bestellung.getNetto());
				case 51 -> sumDelivered = sumDelivered.add(bestellung.getNetto());
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

	public static BigDecimal getSumDelivered() {
		return sumDelivered;
	}
	
}
