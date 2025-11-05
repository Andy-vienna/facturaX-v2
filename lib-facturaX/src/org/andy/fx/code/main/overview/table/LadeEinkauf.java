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

import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityProductive.Einkauf;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CodeListen;

public class LadeEinkauf {
	
	private static BigDecimal bdNetto = BD.ZERO; private static BigDecimal bdBrutto = BD.ZERO;
	private static BigDecimal[] bd10ProzQ = new BigDecimal[4]; private static BigDecimal[] bd20ProzQ = new BigDecimal[4];
	private static BigDecimal bdUstEU = BD.ZERO; private static BigDecimal bdUstEUnoEURO = BD.ZERO;
	private static BigDecimal bdUstNonEU = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadEinkaufsRechnung(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		bdNetto = BD.ZERO; bdBrutto = BD.ZERO;
		bdUstEU = BD.ZERO; bdUstEUnoEURO = BD.ZERO; bdUstNonEU = BD.ZERO;
		for (int n = 0; n < bd10ProzQ.length; n++) {
			bd10ProzQ[n] = BD.ZERO; bd20ProzQ[n] = BD.ZERO;
		}

		EinkaufRepository einkaufRepository = new EinkaufRepository();
	    List<Einkauf> einkaufListe = new ArrayList<>();
	    einkaufListe.addAll(einkaufRepository.findAllByJahr(Einstellungen.getAppSettings().year));
	    
	    LieferantRepository lieferantRepository = new LieferantRepository();
	    List<Lieferant> lieferantListe = new ArrayList<>();
	    lieferantListe.addAll(lieferantRepository.findAll());
		
		String[][] sTemp = new String [einkaufListe.size() + 1][11]; // 1 Zeile mehr für neuen Beleg
		
		for (int i = 0; i < einkaufListe.size(); i++){
			Lieferant lieferant = new Lieferant();
			Einkauf einkauf = einkaufListe.get(i);
			
			gefunden:
			for (int l = 0; l < lieferantListe.size(); l++) {
				lieferant = lieferantListe.get(l);
				if (einkauf.getLieferantId().equals(lieferant.getId())) {
					break gefunden;
				}
				lieferant = new Lieferant();
				lieferant.setName("none"); lieferant.setLand("none"); lieferant.setTaxvalue("0");
			}

			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
			LocalDate date = LocalDate.parse(einkauf.getReDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate dateZZ = LocalDate.parse(einkauf.getZahlungsziel().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        String datum = date.format(outputFormatter);
	        String datumZZ = dateZZ.format(outputFormatter);
	        
	        String netto = df.format(einkauf.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(einkauf.getUst()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(einkauf.getBrutto()) + " " + currency.getCurrencyCode();
	        
	        String land = lieferant.getLand();
	        String steuer = lieferant.getTaxvalue();
	        
	        String status = null;
	        switch(einkauf.getStatus()) {
	        case 0 -> status = "nein";
	        case 1 -> status = "angezahlt";
	        case 2 -> status = "ja";
	        case 3 -> status = "ja, Skonto 1";
	        case 4 -> status = "ja, Skonto 2";
	        default -> status = "unbekannt";
	        }

			sTemp[i][0] = datum;
			sTemp[i][1] = einkauf.getId();
			sTemp[i][2] = lieferant.getName();
			sTemp[i][3] = land;
			sTemp[i][4] = steuer;
			sTemp[i][5] = netto;
			sTemp[i][6] = ust;
			sTemp[i][7] = brutto;
			sTemp[i][8] = datumZZ;
			sTemp[i][9] = status;
			sTemp[i][10] = einkauf.getDateiname();
			
			bdNetto = bdNetto.add(einkauf.getNetto());
			bdBrutto = bdBrutto.add(einkauf.getBrutto());
			
			CodeListen cl = new CodeListen();
			boolean eu = cl.isEU(land); boolean euro = cl.isEurozone(land);
			
			if (eu) {
				if (land.equals("AT")) {
					
					int quartal = getQuartalFromString(einkauf.getReDatum().toString(), "yyyy-MM-dd") - 1;
					String sTax = steuer;
					BigDecimal bdVal = einkauf.getUst();
					if (quartal >= 0 && quartal < 4) {
						switch (sTax) {
						case "20" -> bd20ProzQ[quartal] = bd20ProzQ[quartal].add(bdVal);
						case "10" -> bd10ProzQ[quartal] = bd10ProzQ[quartal].add(bdVal);
						}
					}
					
				} else if (euro) {
					bdUstEU = bdUstEU.add(einkauf.getUst());
				} else {
					bdUstEUnoEURO = bdUstEUnoEURO.add(einkauf.getUst());
				}
			} else {
				bdUstNonEU = bdUstNonEU.add(einkauf.getUst());
			}
			
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static BigDecimal getBdNetto() {
		return bdNetto;
	}

	public static BigDecimal getBdBrutto() {
		return bdBrutto;
	}
	
	public static BigDecimal getUstEU() {
		return bdUstEU;
	}
	
	public static BigDecimal getUstNonEU() {
		return bdUstNonEU;
	}
	
	public static BigDecimal getBdUstEUnoEURO() {
		return bdUstEUnoEURO;
	}

	public static BigDecimal[] getBd10ProzQ() {
		return bd10ProzQ;
	}

	public static BigDecimal[] getBd20ProzQ() {
		return bd20ProzQ;
	}

}
