package org.andy.fx.code.main.overview.result;

import static org.andy.fx.code.misc.ArithmeticHelper.parseBigDecimalToStringSafe;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFormattedTextField;

import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityProductive.Ausgaben;
import org.andy.fx.code.dataStructure.entityProductive.Einkauf;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AusgabenRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.EinkaufRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.gui.main.result_panels.UStPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class UStDaten {

	private static final Logger logger = LogManager.getLogger(UStDaten.class);
	
	public static JFormattedTextField[][] txtFields;     // [zeile][spalte] → z.B. [0][0] = txt000Q1
	public static JFormattedTextField[] txtZahllast;     // [spalte]        → z.B. [0] = Q1, [4] = Jahr
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void setValuesUVA(UStPanel panel) {
		setValues(panel);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(UStPanel panel) {

		// Pro Quartal: 0=Q1, 1=Q2, 2=Q3, 3=Q4
		BigDecimal[] bdKz000 = new BigDecimal[4], bdKz021 = new BigDecimal[4], bdKz022 = new BigDecimal[4], bdKz060 = new BigDecimal[4];
		BigDecimal[] zahlLast = new BigDecimal[4], tmp20 = new BigDecimal[4], tmp10 = new BigDecimal[4], tmp13 = new BigDecimal[4];
		BigDecimal[] tmpKz021 = new BigDecimal[4], tmpKz022 = new BigDecimal[4];
		BigDecimal[] tmpUst20 = new BigDecimal[5];
		
		LieferantRepository lieferantRepository = new LieferantRepository();
	    List<Lieferant> lieferantListe = new ArrayList<>();
	    lieferantListe.addAll(lieferantRepository.findAll());
		
		RechnungRepository rechnungRepository = new RechnungRepository();
	    List<Rechnung> rechnungListe = new ArrayList<>();
		rechnungListe.addAll(rechnungRepository.findAllByJahr(Einstellungen.getAppSettings().year)); // Rechnungen nach GJ laden
		
		EinkaufRepository einkaufRepository = new EinkaufRepository();
	    List<Einkauf> einkaufListe = new ArrayList<>();
	    einkaufListe.addAll(einkaufRepository.findAllByJahr(Einstellungen.getAppSettings().year)); // Einkäufe nach GJ laden
	    
	    AusgabenRepository ausgabenRepository = new AusgabenRepository();
	    List<Ausgaben> ausgabenListe = new ArrayList<>();
		ausgabenListe.addAll(ausgabenRepository.findAllByJahr(Einstellungen.getAppSettings().year)); // Betriebsausgebane nach GJ laden
		
		// Initialisieren
		for (int i = 0; i < 4; i++) {
			bdKz000[i] = bdKz021[i] = bdKz022[i] = bdKz060[i] = BD.ZERO;
			tmpKz021[i] = tmpKz022[i] = BD.ZERO;
			tmp20[i] = tmp10[i] = tmp13[i] = zahlLast[i] = BD.ZERO;
		}
		for (int i = 0; i < 5; i++) {
			tmpUst20[i] = BD.ZERO;
		}

		// Berechnung Bemessungsgrundlage (Ausgangsrechnungen Inland | Ausgangsrechnungen Ausland)
		try {
			for (int x = 0; x < rechnungListe.size(); x++) {
				Rechnung rechnung = rechnungListe.get(x);
				int quartal = getQuartalFromString(rechnung.getDatum().toString(), "yyyy-MM-dd") - 1;
				BigDecimal bdTax = rechnung.getUst();
				BigDecimal bdVal = rechnung.getNetto();
				if (quartal >= 0 && quartal < 4) {
					if (bdTax.compareTo(BD.ZERO) == 0) {
						tmpKz021[quartal] = tmpKz021[quartal].add(bdVal);
					} else {
						tmpKz022[quartal] = tmpKz022[quartal].add(bdVal);
					}
				}
			}
			for (int i = 0; i < 4; i++) {
				bdKz000[i] = tmpKz021[i].add(tmpKz022[i]); // Kz.000 = Summe der Bemessungsgrundlage
				bdKz021[i] = tmpKz021[i]; // Kz.021 = Innergemeinschaftliche sonstige Leistungen
				bdKz022[i] = tmpKz022[i]; // Kz.022 = zu versteuern mit Normalsteuersatz 20%
				tmpUst20[i] = tmpKz022[i].multiply(BD.DOT_TWO); // USt 20% auf Kz.022
			}
			
		} catch (NullPointerException e) {
			logger.error("error in calculating revenue sum - " + e);
		}
		
		// Berechnung der abziehbaren Vorsteuer (Eingangsrechnungen Inland mit Steuersatz 20%, 10% und 13%)
		try {
			for (int x = 0; x < einkaufListe.size(); x++) {
				Lieferant lieferant = new Lieferant();
				Einkauf einkauf = einkaufListe.get(x);
				int quartal = getQuartalFromString(einkauf.getReDatum().toString(), "yyyy-MM-dd") - 1;
				
				for (int l = 0; l < lieferantListe.size(); l++) {
					lieferant = lieferantListe.get(l);
					if (einkauf.getLieferantId().equals(lieferant.getId())) {
						break;
					}
				}
				
				String sTax = lieferant.getTaxvalue();
				BigDecimal bdVal = einkauf.getUst();
				if (quartal >= 0 && quartal < 4) {
					switch (sTax) {
					case "20" -> tmp20[quartal] = tmp20[quartal].add(bdVal);
					case "10" -> tmp10[quartal] = tmp10[quartal].add(bdVal);
					case "13" -> tmp13[quartal] = tmp13[quartal].add(bdVal);
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating inbound billing sum - " + e);
		}

		// Berechnung der abziehbaren Vorsteuer (Betriebsausgaben Inland mit Steuersatz 20%, 10% und 13%)
		try {
			for (int x = 0; x < ausgabenListe.size(); x++) {
				Ausgaben ausgaben = ausgabenListe.get(x);
				int quartal = getQuartalFromString(ausgaben.getDatum().toString(), "yyyy-MM-dd") - 1;
				String sTax = ausgaben.getSteuersatz();
				BigDecimal bdVal = ausgaben.getSteuer();
				if (quartal >= 0 && quartal < 4) {
					switch (sTax) {
					case "20" -> tmp20[quartal] = tmp20[quartal].add(bdVal);
					case "10" -> tmp10[quartal] = tmp10[quartal].add(bdVal);
					case "13" -> tmp13[quartal] = tmp13[quartal].add(bdVal);
					}
				}
			}
		} catch (NullPointerException e) {
			logger.error("error in calculating expenses sum - " + e);
		}
		
		// Gesamtbetrag der Vorsteuer (Kz.060) pro Quartal
		for (int i = 0; i < 4; i++) {
			bdKz060[i] = tmp20[i].add(tmp10[i]).add(tmp13[i]);
		}

		// Zahllast & Jahreswerte berechnen
		BigDecimal bdKz000year = BD.ZERO, bdKz021year = BD.ZERO, bdKz022year = BD.ZERO;
		BigDecimal bdKz060year = BD.ZERO;
		BigDecimal zahlLastYear = BD.ZERO;

		for (int i = 0; i < 4; i++) {
			zahlLast[i] = tmpUst20[i].subtract(bdKz060[i]);
			bdKz000year = bdKz000year.add(bdKz000[i]);
			bdKz021year = bdKz021year.add(bdKz021[i]);
			bdKz022year = bdKz022year.add(bdKz022[i]);
			bdKz060year = bdKz060year.add(bdKz060[i]);
		}
		tmpUst20[4] = bdKz022year.multiply(BD.DOT_TWO); // USt 20% auf Kz.022 für das Jahr
		zahlLastYear = tmpUst20[4].subtract(bdKz060year);

		// Ausgabe (je nach UI einfaches Array)
		setTxtFieldsQ(panel, bdKz000, bdKz021, bdKz022, bdKz060, zahlLast);
		setTxtFieldsYear(panel, bdKz000year, bdKz021year, bdKz022year, bdKz060year, zahlLastYear);
	}

	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
 
	private static void setTxtFieldsQ(UStPanel panel, BigDecimal[] Kz000, BigDecimal[] Kz021, BigDecimal[] Kz022,
			BigDecimal[] Kz060, BigDecimal[] zahlLast) {
		for (int i = 0; i < 4; i++) { // Q1-Q4
			panel.setFieldValue(0, i, Double.valueOf(parseBigDecimalToStringSafe(Kz000[i])));
			panel.setFieldValue(1, i, Double.valueOf(parseBigDecimalToStringSafe(Kz021[i])));
			panel.setFieldValue(2, i, Double.valueOf(parseBigDecimalToStringSafe(Kz022[i])));
			panel.setFieldValue(3, i, Double.valueOf(parseBigDecimalToStringSafe(Kz060[i])));
			panel.setZahllast(i, Double.valueOf(parseBigDecimalToStringSafe(zahlLast[i])));
		}
	}

	private static void setTxtFieldsYear(UStPanel panel, BigDecimal Kz000year, BigDecimal Kz021year, BigDecimal Kz022year, BigDecimal Kz060year,
			BigDecimal zahlLastYear) {
		panel.setFieldValue(0, 4, Double.valueOf(parseBigDecimalToStringSafe(Kz000year)));
		panel.setFieldValue(1, 4, Double.valueOf(parseBigDecimalToStringSafe(Kz021year)));
		panel.setFieldValue(2, 4, Double.valueOf(parseBigDecimalToStringSafe(Kz022year)));
		panel.setFieldValue(3, 4, Double.valueOf(parseBigDecimalToStringSafe(Kz060year)));
		panel.setZahllast(4, Double.valueOf(parseBigDecimalToStringSafe(zahlLastYear)));
	}

}
