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

import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CodeListen;

public class LadeAusgaben {
	
	private static BigDecimal bdNetto = BD.ZERO; private static BigDecimal bdBrutto = BD.ZERO;
	private static BigDecimal[] bd10ProzQ = new BigDecimal[4]; private static BigDecimal[] bd20ProzQ = new BigDecimal[4];
	private static BigDecimal bdUstEU = BD.ZERO; private static BigDecimal bdUstEUnoEURO = BD.ZERO;
	private static BigDecimal bdUstNonEU = BD.ZERO;
	
	private static int[] belegID = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadAusgaben(boolean reRun) {
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

		AusgabenRepository ausgabenRepository = new AusgabenRepository();
	    List<Ausgaben> ausgabenListe = new ArrayList<>();
		ausgabenListe.addAll(ausgabenRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [ausgabenListe.size() + 1][8];
		belegID = new int[ausgabenListe.size()];
		
		for (int i = 0; i < ausgabenListe.size(); i++){
			Ausgaben ausgaben = ausgabenListe.get(i);

			LocalDate date = LocalDate.parse(ausgaben.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
	        String netto = df.format(ausgaben.getNetto()) + " " + currency.getCurrencyCode();
	        String ust = df.format(ausgaben.getSteuer()) + " " + currency.getCurrencyCode();
	        String brutto = df.format(ausgaben.getBrutto()) + " " + currency.getCurrencyCode();
	        
			sTemp[i][0] = datum;
			sTemp[i][1] = ausgaben.getArt().trim();
			sTemp[i][2] = ausgaben.getLand().trim();
			sTemp[i][3] = ausgaben.getSteuersatz().trim();
			sTemp[i][4] = netto;
			sTemp[i][5] = ust;
			sTemp[i][6] = brutto;
			sTemp[i][7] = ausgaben.getDateiname().trim();
			
			belegID[i] = ausgaben.getId();
			
			bdNetto = bdNetto.add(ausgaben.getNetto());
			bdBrutto = bdBrutto.add(ausgaben.getBrutto());
			
			CodeListen cl = new CodeListen();
			boolean eu = cl.isEU(ausgaben.getLand()); boolean euro = cl.isEurozone(ausgaben.getLand());
			
			if (eu) {
				if (ausgaben.getLand().equals("AT")) {
					
					int quartal = getQuartalFromString(ausgaben.getDatum().toString(), "yyyy-MM-dd") - 1;
					String sTax = ausgaben.getSteuersatz();
					BigDecimal bdVal = ausgaben.getSteuer();
					if (quartal >= 0 && quartal < 4) {
						switch (sTax) {
						case "20" -> bd20ProzQ[quartal] = bd20ProzQ[quartal].add(bdVal);
						case "10" -> bd10ProzQ[quartal] = bd10ProzQ[quartal].add(bdVal);
						}
					}
					
				} else if (euro) {
					bdUstEU = bdUstEU.add(ausgaben.getSteuer());
				} else {
					bdUstEUnoEURO = bdUstEUnoEURO.add(ausgaben.getSteuer());
				}
			} else {
				bdUstNonEU = bdUstNonEU.add(ausgaben.getSteuer());
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
	// Getter und Setter
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

	public static int[] getBelegID() {
		return belegID;
	}

	public static BigDecimal[] getBd10ProzQ() {
		return bd10ProzQ;
	}

	public static BigDecimal[] getBd20ProzQ() {
		return bd20ProzQ;
	}
	
}
