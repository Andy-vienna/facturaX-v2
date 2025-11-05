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

import org.andy.fx.code.dataStructure.entityProductive.SVSteuer;
import org.andy.fx.code.dataStructure.repositoryProductive.SVSteuerRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;

public class LadeSvTax {
	
	private static BigDecimal bdSV = BD.ZERO; private static BigDecimal bdSVoffen = BD.ZERO;
	private static BigDecimal bdST = BD.ZERO; private static BigDecimal bdSToffen = BD.ZERO;
	
	private static BigDecimal[] bdSVQ = new BigDecimal[4];
	
	private static int[] belegID = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadSvTax(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {
		
		BigDecimal bdSvEing ,bdStEing;

		Currency currency = Currency.getInstance("EUR");
		DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.GERMANY);
		DecimalFormat df = new DecimalFormat("#,##0.00", symbols);
		
		bdSV = BD.ZERO; bdSVoffen = BD.ZERO; bdST = BD.ZERO; bdSToffen = BD.ZERO; bdSvEing = BD.ZERO; bdStEing = BD.ZERO;
		for (int a = 0; a < bdSVQ.length; a++) {
			 bdSVQ[a] = BD.ZERO;
		}
		
		SVSteuerRepository svsteuerRepository = new SVSteuerRepository();
	    List<SVSteuer> svsteuerListe = new ArrayList<>();
	    svsteuerListe.addAll(svsteuerRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [svsteuerListe.size() + 1][7];
		belegID = new int[svsteuerListe.size()];
		
		for (int i = 0; i < svsteuerListe.size(); i++){
			SVSteuer svsteuer = svsteuerListe.get(i);
			
			LocalDate date = LocalDate.parse(svsteuer.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
			LocalDate dateF = LocalDate.parse(svsteuer.getZahlungsziel().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        String datumF = dateF.format(outputFormatter);
	        
	        String status = null;
	        switch(svsteuer.getStatus()) {
	        	case 0 -> status = "Forderung";
	        	case 10 -> status = "Zahlung";
	        	case 55 -> status = "Dateiablage";
	        }
	        
	        String zahllast = df.format(svsteuer.getZahllast()) + " " + currency.getCurrencyCode();
	        if (status.equals("Dateiablage")) { zahllast = ""; datumF = ""; }
		
	        sTemp[i][0] = datum;
	        sTemp[i][1] = svsteuer.getOrganisation();
	        sTemp[i][2] = svsteuer.getBezeichnung();
	        sTemp[i][3] = zahllast;
	        sTemp[i][4] = datumF;
	        sTemp[i][5] = status;
	        sTemp[i][6] = svsteuer.getDateiname();
	        
	        belegID[i] = svsteuer.getId();
			
	        if (svsteuer.getOrganisation().contains("Sozialversicherung")) {
	        	if (status.equals("Forderung")) bdSV = bdSV.add(svsteuer.getZahllast());
	        	for (int x = 0; x < 4; x++) {
	        	    String token = "Q" + (x + 1);
	        	    if (svsteuer.getBezeichnung().contains(token) && status.equals("Forderung")) {
	        	        bdSVQ[x] = bdSVQ[x].add(svsteuer.getZahllast());
	        	    }
	        	}
	        	if (status.equals("Zahlung")) bdSvEing = bdSvEing.add(svsteuer.getZahllast()); 
	        	bdSVoffen = bdSV.add(bdSvEing);
	        }
	        if (svsteuer.getOrganisation().contains("Finanzamt")) {
	        	if (status.equals("Forderung")) bdST = bdST.add(svsteuer.getZahllast());
	        	if (status.equals("Zahlung")) bdStEing = bdStEing.add(svsteuer.getZahllast());
	        	bdSToffen = bdST.add(bdStEing);
	        }
		}
		return sTemp;
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################

	public static BigDecimal getBdSV() {
		return bdSV;
	}
	
	public static BigDecimal getBdSVoffen() {
		return bdSVoffen;
	}

	public static BigDecimal getBdST() {
		return bdST;
	}
	
	public static BigDecimal getBdSToffen() {
		return bdSToffen;
	}

	public static int[] getBelegID() {
		return belegID;
	}

	public static BigDecimal[] getBdSVQ() {
		return bdSVQ;
	}

}
