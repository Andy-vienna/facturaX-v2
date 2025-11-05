package org.andy.fx.code.main.overview.result;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.andy.fx.code.dataStructure.entityMaster.Gwb;
import org.andy.fx.code.dataStructure.entityMaster.Tax;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.GwbRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.TaxRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.overview.table.LadeAusgaben;
import org.andy.fx.code.main.overview.table.LadeEinkauf;
import org.andy.fx.code.main.overview.table.LadeSvTax;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.gui.main.result_panels.SteuerPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SteuerDaten {
	
	private static final Logger logger = LogManager.getLogger(SteuerDaten.class);
	
	private static NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.GERMANY);

	private static BigDecimal bdSVYear = BD.ZERO;
	
	private static String[][] arrTaxValues = new String[2][25];
	private static String[][] arrGwbValues = new String[2][10];
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void setValuesTax(SteuerPanel panel) {	
		setValues(panel);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(SteuerPanel panel) {
		
		bdSVYear = BD.ZERO; // Variable neu initialisieren
		getDBData(); // Steuergrenzen und Gewinnfreibetragsgrenzen aus DB lesen
		
		List<BigDecimal> GwbStufe = new ArrayList<>();
		List<BigDecimal> TaxStufe = new ArrayList<>();
		
		BigDecimal netto = BD.ZERO;
		BigDecimal bdVorGwb = BD.ZERO;
		BigDecimal bdErgYear = BD.ZERO;
		BigDecimal bdOeffiP = parseStringToBigDecimalSafe(arrTaxValues[1][23], LocaleFormat.AUTO).multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdAPausch = parseStringToBigDecimalSafe(arrTaxValues[1][24], LocaleFormat.AUTO).multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP);
		BigDecimal bdExpenses = BD.ZERO;
		BigDecimal bdGwbTotal = BD.ZERO;
		BigDecimal bdGwbTotalNeg = BD.ZERO;
		BigDecimal bdTaxTotal = BD.ZERO;
		BigDecimal[] bdSVQ = new BigDecimal[4];
		bdSVQ = LadeSvTax.getBdSVQ(); // Quartalswerte der SV-Vorschreibungen
		
		for (int i = 0; i < bdSVQ.length; i++) {
			bdSVYear = bdSVYear.add(bdSVQ[i].multiply(BD.M_ONE));
		}
		
		RechnungRepository rechnungRepository = new RechnungRepository();
	    List<Rechnung> rechnungListe = new ArrayList<>();
		rechnungListe.addAll(rechnungRepository.findAllByJahr(Einstellungen.getAppSettings().year)); // Rechnungen nach GJ laden
		
		try {
			for(int x = 0; x < rechnungListe.size(); x++) {
				Rechnung rechnung = rechnungListe.get(x);
				if(rechnung.getState() > 1) { // Rechnung wurde gedruckt
					netto = netto.add(rechnung.getNetto());
				}
			}
			
			BigDecimal bdBA = LadeEinkauf.getBdNetto().add(LadeAusgaben.getBdNetto()); // Betriebsausgaben netto komplett
			bdExpenses = bdBA.multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP); // Betriebsausgaben netto negativ
			
			bdVorGwb = netto.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses); // VorGWB wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale und Ausgaben netto berechnet
			
			GwbStufe = calcGWB(panel, bdVorGwb); // Berechnung der GWB-Stufen
			
			bdGwbTotal = GwbStufe.stream().reduce(BD.ZERO, BigDecimal::add); // Summe der GWB-Stufen
			bdGwbTotalNeg = bdGwbTotal.multiply(BD.M_ONE).setScale(2, RoundingMode.HALF_UP); // GWB negativ
			
			bdErgYear = netto.add(bdSVYear).add(bdOeffiP).add(bdAPausch).add(bdExpenses).add(bdGwbTotalNeg); // Ergebnis wird aus der Summe der Einnahmen, SV, öffentlicher Pauschale, APauschale, Ausgaben netto und GWB negativ berechnet

			//bdErgYear = new BigDecimal("98456.23");
			
			TaxStufe = calcTAX(panel, bdErgYear); // Berechnung der Steuerstufen
			
			bdTaxTotal = TaxStufe.get(7).add(TaxStufe.get(8)).add(TaxStufe.get(9)).add(TaxStufe.get(10)).add(TaxStufe.get(11))
				.add(TaxStufe.get(12)).add(TaxStufe.get(13)); // Summe der errechneten Steuern bilden
			
			
		} catch (NullPointerException e1){
			logger.error("error in calculating revenue sum - " + e1);
		}
		
		panel.setTxtP109aEin(Double.valueOf(netto.toString()));
		panel.setTxtP109aSVS(0, Double.valueOf(bdSVYear.toString()));
		for (int i= 0; i < bdSVQ.length; i++) {
			panel.setTxtP109aSVS(i + 1, Double.valueOf(bdSVQ[i].toString()));
		}
		panel.setTxtP109aOeffiP(Double.valueOf(bdOeffiP.toString()));
		panel.setTxtP109aAPausch(Double.valueOf(bdAPausch.toString()));
		panel.setTxtP109aExpenses(Double.valueOf(bdExpenses.toString()));

		panel.setTxtVorGWB(Double.valueOf(bdVorGwb.toString()));
		panel.setTxtGwbStufen(0, Double.valueOf(GwbStufe.get(0).toString()));
		panel.setTxtGwbStufen(1, Double.valueOf(GwbStufe.get(1).toString()));
		panel.setTxtGwbStufen(2, Double.valueOf(GwbStufe.get(2).toString()));
		panel.setTxtGwbStufen(3, Double.valueOf(GwbStufe.get(3).toString()));
		panel.setTxtGwbTotal(Double.valueOf(bdGwbTotal.toString()));
		
		panel.setTxtP109aGrundfrei(Double.valueOf(bdGwbTotalNeg.toString()));
		panel.setTxtP109aErgebnis(Double.valueOf(bdErgYear.toString()));
		
		panel.setTxtE1VorSt(Double.valueOf(bdErgYear.toString())); // Gewinn vor Steuer für E1 wird aus dem Ergebnis der P109a übernommen
		panel.setTxtE1Stufen(0, Double.valueOf(TaxStufe.get(0).toString())); // Summe Stufe 1
		panel.setTxtE1Stufen(1, Double.valueOf(TaxStufe.get(1).toString())); // Summe Stufe 2
		panel.setTxtE1Stufen(2, Double.valueOf(TaxStufe.get(2).toString())); // Summe Stufe 3
		panel.setTxtE1Stufen(3, Double.valueOf(TaxStufe.get(3).toString())); // Summe Stufe 4
		panel.setTxtE1Stufen(4, Double.valueOf(TaxStufe.get(4).toString())); // Summe Stufe 5
		panel.setTxtE1Stufen(5, Double.valueOf(TaxStufe.get(5).toString())); // Summe Stufe 6
		panel.setTxtE1Stufen(6, Double.valueOf(TaxStufe.get(6).toString())); // Summe Stufe 7
		
		panel.setTxtE1Tax(0, Double.valueOf(TaxStufe.get(7).toString())); // voraussichtliche Steuer in Stufe 1
		panel.setTxtE1Tax(1, Double.valueOf(TaxStufe.get(8).toString())); // voraussichtliche Steuer in Stufe 2
		panel.setTxtE1Tax(2, Double.valueOf(TaxStufe.get(9).toString())); // voraussichtliche Steuer in Stufe 3
		panel.setTxtE1Tax(3, Double.valueOf(TaxStufe.get(10).toString())); // voraussichtliche Steuer in Stufe 4
		panel.setTxtE1Tax(4, Double.valueOf(TaxStufe.get(11).toString())); // voraussichtliche Steuer in Stufe 5
		panel.setTxtE1Tax(5, Double.valueOf(TaxStufe.get(12).toString())); // voraussichtliche Steuer in Stufe 6
		panel.setTxtE1Tax(6, Double.valueOf(TaxStufe.get(13).toString())); // voraussichtliche Steuer in Stufe 7
		panel.setTxtE1Summe(Double.valueOf(bdTaxTotal.toString())); // voraussichtliche Einkommensteuer gesamt
		
		ArrayList<BigDecimal> tmpListe = new ArrayList<>(); // Liste für §109a Formular erzeugen und in Setter schreiben
		tmpListe.add(netto); // Einnahmen
		tmpListe.add(bdSVQ[0]); // SV Q1
		tmpListe.add(bdSVQ[1]); // SV Q2
		tmpListe.add(bdSVQ[2]); // SV Q3
		tmpListe.add(bdSVQ[3]); // SV Q4
		tmpListe.add(bdSVYear); // SV Jahr
		tmpListe.add(bdOeffiP); // Öffi-Pauschale
		tmpListe.add(bdAPausch); // Arbeitsplatzpauschale
		tmpListe.add(bdExpenses); // Betriebsausgaben netto
		tmpListe.add(bdVorGwb); // Zwischensumme
		tmpListe.add(bdGwbTotalNeg); // Gewinnfreibetrag
		tmpListe.add(bdErgYear); // Ergebnis
		
		panel.setDataExcel(tmpListe); // Liste für P109a in Setter schreiben
		
	}
	
	private static List<BigDecimal> calcGWB(SteuerPanel panel, BigDecimal bdVorGwb) {

		BigDecimal rest1 = BD.ZERO;
		BigDecimal tmp1 = BD.ZERO;
		BigDecimal rest2 = BD.ZERO;
		BigDecimal tmp2 = BD.ZERO;
		BigDecimal rest3 = BD.ZERO;
		BigDecimal tmp3 = BD.ZERO;
		BigDecimal tmp4 = BD.ZERO;
		
		BigDecimal bdGwbTmp1 = parseStringToBigDecimalSafe(arrGwbValues[1][2], LocaleFormat.AUTO);
		BigDecimal bdGwbVal1 = parseStringToBigDecimalSafe(arrGwbValues[1][3], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp2 = parseStringToBigDecimalSafe(arrGwbValues[1][4], LocaleFormat.AUTO);
		BigDecimal bdGwbVal2 = parseStringToBigDecimalSafe(arrGwbValues[1][5], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp3 = parseStringToBigDecimalSafe(arrGwbValues[1][6], LocaleFormat.AUTO);
		BigDecimal bdGwbVal3 = parseStringToBigDecimalSafe(arrGwbValues[1][7], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdGwbTmp4 = parseStringToBigDecimalSafe(arrGwbValues[1][8], LocaleFormat.AUTO);
		BigDecimal bdGwbVal4 = parseStringToBigDecimalSafe(arrGwbValues[1][9], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		
		Double dTmp1 = Double.valueOf(bdGwbTmp1.toString());
		Double dTmp2 = Double.valueOf(bdGwbTmp2.toString());
		Double dTmp3 = Double.valueOf(bdGwbTmp3.toString());
		Double dTmp4 = Double.valueOf(bdGwbTmp4.toString());
		
		String sTmpd1 = currencyFormat.format(dTmp1);
		String sTmpd2 = currencyFormat.format(dTmp2);
		String sTmpd3 = currencyFormat.format(dTmp3);
		String sTmpd4 = currencyFormat.format(dTmp4);
		
		String sTmp1 = panel.getLblGwbStufen(0).replace("§", sTmpd1).replace("&", bdGwbVal1.toString());
		String sTmp2 = panel.getLblGwbStufen(1).replace("§", sTmpd2).replace("&", bdGwbVal2.toString());
		String sTmp3 = panel.getLblGwbStufen(2).replace("§", sTmpd3).replace("&", bdGwbVal3.toString());
		String sTmp4 = panel.getLblGwbStufen(3).replace("§", sTmpd4).replace("&", bdGwbVal4.toString());
		
		panel.setLblGwbStufen(0, sTmp1); // Texte für GWB Stufen anpassen
		panel.setLblGwbStufen(1, sTmp2);
		panel.setLblGwbStufen(2, sTmp3);
		panel.setLblGwbStufen(3, sTmp4);
		
		List<BigDecimal> liste = new ArrayList<>();
		
		if(bdVorGwb.compareTo(bdGwbTmp1) >= 0) { // wenn VorGWB größer oder gleich GWB Stufe 1
			rest1 = bdVorGwb.subtract(bdGwbTmp1);
			tmp1 = bdGwbTmp1.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][3], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest1 = BD.ZERO;
			tmp1 = bdVorGwb.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][3], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest1.compareTo(bdGwbTmp2) >= 0) { // wenn Rest größer oder gleich GWB Stufe 2
			rest2 = rest1.subtract(bdGwbTmp2);
			tmp2 = bdGwbTmp2.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][5], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest2 = BD.ZERO;
			tmp2 = rest1.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][5], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest2.compareTo(bdGwbTmp3) >= 0) { // wenn Rest größer oder gleich GWB Stufe 3
			rest3 = rest2.subtract(bdGwbTmp3);
			tmp3 = bdGwbTmp3.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][7], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		} else {
			rest3 = BD.ZERO;
			tmp3 = rest2.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][7], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		}
		
		if(rest3.compareTo(bdGwbTmp4) >= 0) { // wenn Rest größer oder gleich GWB Stufe 4
			tmp4 = bdGwbTmp4.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][9], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		} else {
			tmp4 = rest3.multiply(parseStringToBigDecimalSafe(arrGwbValues[1][9], LocaleFormat.AUTO)).setScale(2, RoundingMode.HALF_UP);
		}
		
		liste.add(tmp1); // GWB Stufe 1
		liste.add(tmp2); // GWB Stufe 2
		liste.add(tmp3); // GWB Stufe 3
		liste.add(tmp4); // GWB Stufe 4
		
		return liste;
		
	}
	
	private static List<BigDecimal> calcTAX(SteuerPanel panel, BigDecimal bdVorTax){

		BigDecimal rest1 = BD.ZERO;
		BigDecimal rest2 = BD.ZERO;
		BigDecimal rest3 = BD.ZERO;
		BigDecimal rest4 = BD.ZERO;
		BigDecimal rest5 = BD.ZERO;
		BigDecimal rest6 = BD.ZERO;
		BigDecimal rest7 = BD.ZERO;
		
		BigDecimal bdTaxVon1 = parseStringToBigDecimalSafe(arrTaxValues[1][2], LocaleFormat.AUTO);
		BigDecimal bdTaxBis1 = parseStringToBigDecimalSafe(arrTaxValues[1][3], LocaleFormat.AUTO);
		BigDecimal bdTaxVal1 = parseStringToBigDecimalSafe(arrTaxValues[1][4], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon2 = parseStringToBigDecimalSafe(arrTaxValues[1][5], LocaleFormat.AUTO);
		BigDecimal bdTaxBis2 = parseStringToBigDecimalSafe(arrTaxValues[1][6], LocaleFormat.AUTO);
		BigDecimal bdTaxVal2 = parseStringToBigDecimalSafe(arrTaxValues[1][7], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon3 = parseStringToBigDecimalSafe(arrTaxValues[1][8], LocaleFormat.AUTO);
		BigDecimal bdTaxBis3 = parseStringToBigDecimalSafe(arrTaxValues[1][9], LocaleFormat.AUTO);
		BigDecimal bdTaxVal3 = parseStringToBigDecimalSafe(arrTaxValues[1][10], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon4 = parseStringToBigDecimalSafe(arrTaxValues[1][11], LocaleFormat.AUTO);
		BigDecimal bdTaxBis4 = parseStringToBigDecimalSafe(arrTaxValues[1][12], LocaleFormat.AUTO);
		BigDecimal bdTaxVal4 = parseStringToBigDecimalSafe(arrTaxValues[1][13], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon5 = parseStringToBigDecimalSafe(arrTaxValues[1][14], LocaleFormat.AUTO);
		BigDecimal bdTaxBis5 = parseStringToBigDecimalSafe(arrTaxValues[1][15], LocaleFormat.AUTO);
		BigDecimal bdTaxVal5 = parseStringToBigDecimalSafe(arrTaxValues[1][16], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon6 = parseStringToBigDecimalSafe(arrTaxValues[1][17], LocaleFormat.AUTO);
		BigDecimal bdTaxBis6 = parseStringToBigDecimalSafe(arrTaxValues[1][18], LocaleFormat.AUTO);
		BigDecimal bdTaxVal6 = parseStringToBigDecimalSafe(arrTaxValues[1][19], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);
		BigDecimal bdTaxVon7 = parseStringToBigDecimalSafe(arrTaxValues[1][20], LocaleFormat.AUTO);
		BigDecimal bdTaxBis7 = parseStringToBigDecimalSafe(arrTaxValues[1][21], LocaleFormat.AUTO);
		BigDecimal bdTaxVal7 = parseStringToBigDecimalSafe(arrTaxValues[1][22], LocaleFormat.AUTO).multiply(BD.HUNDRED).setScale(0, RoundingMode.HALF_UP);

		Double dTmp1 = Double.valueOf(bdTaxVon1.toString());
		Double dTmp2 = Double.valueOf(bdTaxBis1.toString());
		Double dTmp3 = Double.valueOf(bdTaxVon2.toString());
		Double dTmp4 = Double.valueOf(bdTaxBis2.toString());
		Double dTmp5 = Double.valueOf(bdTaxVon3.toString());
		Double dTmp6 = Double.valueOf(bdTaxBis3.toString());
		Double dTmp7 = Double.valueOf(bdTaxVon4.toString());
		Double dTmp8 = Double.valueOf(bdTaxBis4.toString());
		Double dTmp9 = Double.valueOf(bdTaxVon5.toString());
		Double dTmp10 = Double.valueOf(bdTaxBis5.toString());
		Double dTmp11 = Double.valueOf(bdTaxVon6.toString());
		Double dTmp12 = Double.valueOf(bdTaxBis6.toString());
		Double dTmp13 = Double.valueOf(bdTaxVon7.toString());
		Double dTmp14 = Double.valueOf(bdTaxBis7.toString());
		
		String sTmpd1 = currencyFormat.format(dTmp1), sTmpd2 = currencyFormat.format(dTmp2);
		String sTmpd3 = currencyFormat.format(dTmp3), sTmpd4 = currencyFormat.format(dTmp4);
		String sTmpd5 = currencyFormat.format(dTmp5), sTmpd6 = currencyFormat.format(dTmp6);
		String sTmpd7 = currencyFormat.format(dTmp7), sTmpd8 = currencyFormat.format(dTmp8);
		String sTmpd9 = currencyFormat.format(dTmp9), sTmpd10 = currencyFormat.format(dTmp10);
		String sTmpd11 = currencyFormat.format(dTmp11), sTmpd12 = currencyFormat.format(dTmp12);
		String sTmpd13 = currencyFormat.format(dTmp13), sTmpd14 = currencyFormat.format(dTmp14);
		
		String sTmp1 = panel.getLblE1Stufen(0).replace("$", sTmpd1).replace("§", sTmpd2).replace("&", bdTaxVal1.toString());
		String sTmp2 = panel.getLblE1Stufen(1).replace("$", sTmpd3).replace("§", sTmpd4).replace("&", bdTaxVal2.toString());
		String sTmp3 = panel.getLblE1Stufen(2).replace("$", sTmpd5).replace("§", sTmpd6).replace("&", bdTaxVal3.toString());
		String sTmp4 = panel.getLblE1Stufen(3).replace("$", sTmpd7).replace("§", sTmpd8).replace("&", bdTaxVal4.toString());
		String sTmp5 = panel.getLblE1Stufen(4).replace("$", sTmpd9).replace("§", sTmpd10).replace("&", bdTaxVal5.toString());
		String sTmp6 = panel.getLblE1Stufen(5).replace("$", sTmpd11).replace("§", sTmpd12).replace("&", bdTaxVal6.toString());
		String sTmp7 = panel.getLblE1Stufen(6).replace("$", sTmpd13).replace("§", sTmpd14).replace("&", bdTaxVal7.toString());
		
		panel.setLblE1Stufen(0, sTmp1); // Texte für E1 Stufen anpassen
		panel.setLblE1Stufen(1, sTmp2);
		panel.setLblE1Stufen(2, sTmp3);
		panel.setLblE1Stufen(3, sTmp4);
		panel.setLblE1Stufen(4, sTmp5);
		panel.setLblE1Stufen(5, sTmp6);
		panel.setLblE1Stufen(6, sTmp7);

		List<BigDecimal> liste = new ArrayList<>();
		
		// Steuerstufen berechnen

		if(bdVorTax.compareTo(bdTaxVon7) >= 1) { // wenn VorTax größer oder gleich Stufe 7 von
			rest7 = bdVorTax.subtract(bdTaxVon7);
			rest6 = bdTaxBis6.subtract(bdTaxVon6);
			rest5 = bdTaxBis5.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon6) >= 1) { // wenn VorTax größer oder gleich Stufe 6 von
			rest7 = BD.ZERO;
			rest6 = bdVorTax.subtract(bdTaxVon6);
			rest5 = bdTaxBis5.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon5) >= 1) { // wenn VorTax größer oder gleich Stufe 5 von
			rest7 = BD.ZERO;
			rest6 = BD.ZERO;
			rest5 = bdVorTax.subtract(bdTaxVon5);
			rest4 = bdTaxBis4.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon4) >= 1) { // wenn VorTax größer oder gleich Stufe 4 von
			rest7 = BD.ZERO;
			rest6 = BD.ZERO;
			rest5 = BD.ZERO;
			rest4 = bdVorTax.subtract(bdTaxVon4);
			rest3 = bdTaxBis3.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon3) >= 1) { // wenn VorTax größer oder gleich Stufe 3 von
			rest7 = BD.ZERO;
			rest6 = BD.ZERO;
			rest5 = BD.ZERO;
			rest4 = BD.ZERO;
			rest3 = bdVorTax.subtract(bdTaxVon3);
			rest2 = bdTaxBis2.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon2) >= 1) { // wenn VorTax größer oder gleich Stufe 2 von
			rest7 = BD.ZERO;
			rest6 = BD.ZERO;
			rest5 = BD.ZERO;
			rest4 = BD.ZERO;
			rest3 = BD.ZERO;
			rest2 = bdVorTax.subtract(bdTaxVon2);
			rest1 = bdTaxBis1.subtract(bdTaxVon1);
		} else if(bdVorTax.compareTo(bdTaxVon1) >= 1) { // wenn VorTax größer oder gleich Stufe 1 von
			rest7 = BD.ZERO;
			rest6 = BD.ZERO;
			rest5 = BD.ZERO;
			rest4 = BD.ZERO;
			rest3 = BD.ZERO;
			rest2 = BD.ZERO;
			rest1 = bdVorTax.subtract(bdTaxVon1);
		}
		
		liste.add(rest1); // Wert Stufe 1
		liste.add(rest2); // Wert Stufe 2
		liste.add(rest3); // Wert Stufe 3
		liste.add(rest4); // Wert Stufe 4
		liste.add(rest5); // Wert Stufe 5
		liste.add(rest6); // Wert Stufe 6
		liste.add(rest7); // Wert Stufe 7
		liste.add(rest1.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][4], LocaleFormat.AUTO))); // Steuer Stufe 1
		liste.add(rest2.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][7], LocaleFormat.AUTO))); // Steuer Stufe 2
		liste.add(rest3.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][10], LocaleFormat.AUTO))); // Steuer Stufe 3
		liste.add(rest4.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][13], LocaleFormat.AUTO))); // Steuer Stufe 4
		liste.add(rest5.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][16], LocaleFormat.AUTO))); // Steuer Stufe 5
		liste.add(rest6.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][19], LocaleFormat.AUTO))); // Steuer Stufe 6
		liste.add(rest7.multiply(parseStringToBigDecimalSafe(arrTaxValues[1][22], LocaleFormat.AUTO))); // Steuer Stufe 7
		
		return liste;
		
	}
	
	//###################################################################################################################################################
	
	private static void getDBData() {
		
		TaxRepository taxRepository = new TaxRepository();
		GwbRepository gwbRepository = new GwbRepository();
	    List<Tax> taxListe = new ArrayList<>();
	    List<Gwb> gwbListe = new ArrayList<>();
	    
	    taxListe.addAll(taxRepository.findAll());
	    gwbListe.addAll(gwbRepository.findAll());
	    
	    Arrays.stream(arrTaxValues).forEach(a -> Arrays.fill(a, null));
		Arrays.stream(arrGwbValues).forEach(a -> Arrays.fill(a, null));
		
		for (int x = 0; x < taxListe.size(); x++) {
			Tax tax = taxListe.get(x);
			if (tax.getYear() == Einstellungen.getAppSettings().year) {
				arrTaxValues[1][2] = tax.getVon_1().toString();
				arrTaxValues[1][3] = tax.getBis_1().toString();
				arrTaxValues[1][4] = tax.getTax_1().toString();
				arrTaxValues[1][5] = tax.getVon_2().toString();
				arrTaxValues[1][6] = tax.getBis_2().toString();
				arrTaxValues[1][7] = tax.getTax_2().toString();
				arrTaxValues[1][8] = tax.getVon_3().toString();
				arrTaxValues[1][9] = tax.getBis_3().toString();
				arrTaxValues[1][10] = tax.getTax_3().toString();
				arrTaxValues[1][11] = tax.getVon_4().toString();
				arrTaxValues[1][12] = tax.getBis_4().toString();
				arrTaxValues[1][13] = tax.getTax_4().toString();
				arrTaxValues[1][14] = tax.getVon_5().toString();
				arrTaxValues[1][15] = tax.getBis_5().toString();
				arrTaxValues[1][16] = tax.getTax_5().toString();
				arrTaxValues[1][17] = tax.getVon_6().toString();
				arrTaxValues[1][18] = tax.getBis_6().toString();
				arrTaxValues[1][19] = tax.getTax_6().toString();
				arrTaxValues[1][20] = tax.getVon_7().toString();
				arrTaxValues[1][21] = tax.getBis_7().toString();
				arrTaxValues[1][22] = tax.getTax_7().toString();
				arrTaxValues[1][23] = tax.getOeP().toString();
				arrTaxValues[1][24] = tax.getApP().toString();
			}
		}
		
		for ( int x = 0; x < gwbListe.size(); x++) {
			Gwb gwb = gwbListe.get(x);
			if (gwb.getYear() == Einstellungen.getAppSettings().year) {
				arrGwbValues[1][2] = gwb.getBis_1().toString();
				arrGwbValues[1][3] = gwb.getVal_1().toString();
				arrGwbValues[1][4] = gwb.getWeitere_2().toString();
				arrGwbValues[1][5] = gwb.getVal_2().toString();
				arrGwbValues[1][6] = gwb.getWeitere_3().toString();
				arrGwbValues[1][7] = gwb.getVal_3().toString();
				arrGwbValues[1][8] = gwb.getWeitere_4().toString();
				arrGwbValues[1][9] = gwb.getVal_4().toString();
			}
		}
	}
	
	//###################################################################################################################################################
	// Getter und Setter für Felder
	//###################################################################################################################################################
	
}
