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

import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;

public class LadeAngebot {
    
    private static BigDecimal sumOpen = BD.ZERO;
    private static BigDecimal sumOrdered = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAngebot(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		sumOpen = BD.ZERO; sumOrdered = BD.ZERO;
		
		AngebotRepository angebotRepository = new AngebotRepository();
	    List<Angebot> angebotListe = new ArrayList<>();
		angebotListe.addAll(angebotRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [angebotListe.size()][6];
		
		for (int i = 0; i < angebotListe.size(); i++){
			Angebot angebot = angebotListe.get(i);
			
			String status = switch(angebot.getState()) {
				case 0 -> "storniert";
				case 1 -> "erstellt";
				case 11 -> "gedruckt";
				case 12 -> "revisioniert";
				case 111 -> "bestellt";
				case 211 -> "bestätigt";
				default -> "-----";
				};
			
			LocalDate date = LocalDate.parse(angebot.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(angebot.getNetto()) + " " + currency.getCurrencyCode();
			
			sTemp[i][0] = angebot.getIdNummer();
			sTemp[i][1] = status;
			sTemp[i][2] = datum;
			sTemp[i][3] = angebot.getRef();
			sTemp[i][4] = LadeDatenHelper.searchKunde(angebot.getIdKunde());
			sTemp[i][5] = netto;
			
			if (angebot.getState() > 0) { // nicht storniert
				switch (angebot.getState()) {
				case 11 -> sumOpen = sumOpen.add(angebot.getNetto());
				case 111 -> sumOrdered = sumOrdered.add(angebot.getNetto());
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

	public static BigDecimal getSumOrdered() {
		return sumOrdered;
	}
	
}
