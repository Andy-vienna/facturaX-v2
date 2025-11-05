package org.andy.fx.code.main.overview.result;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;
import org.andy.fx.gui.main.result_panels.ZMeldungPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZMeldungDaten {
	
	private static final Logger logger = LogManager.getLogger(UStDaten.class);
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static void RecState(ZMeldungPanel panel) {
		setValues(panel);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static void setValues(ZMeldungPanel panel) {
		NumberFormat nf = NumberFormat.getNumberInstance(Locale.GERMANY);
		nf.setMinimumFractionDigits(2);
		nf.setMaximumFractionDigits(2);
		
		KundeRepository kundeRepository = new KundeRepository();
		List<Kunde> kundeListe = new ArrayList<>();
		kundeListe.addAll(kundeRepository.findAll());
		
		RechnungRepository rechnungRepository = new RechnungRepository();
	    List<Rechnung> rechnungListe = new ArrayList<>();
		rechnungListe.addAll(rechnungRepository.findAllByJahr(Einstellungen.getAppSettings().year)); // Rechnungen nach GJ laden
		
	    List<Map<String, Statistik>> mapProQuartal = new ArrayList<>();
	    for (int i = 0; i < 4; i++) {
	        mapProQuartal.add(new HashMap<>());
	    }

	    try {
	        for (int x = 1; x < rechnungListe.size(); x++) {
	        	Rechnung rechnung = rechnungListe.get(x);
	            int quartal = getQuartalFromString(rechnung.getDatum().toString(), "yyyy-MM-dd") - 1;
	            String sKunde = rechnung.getIdKunde().trim();

	            for (int i = 0; i < kundeListe.size(); i++) {
	            	Kunde kunde = kundeListe.get(i);
	                if (kunde.getId().trim().equals(sKunde)) {
	                    if (!kunde.getLand().trim().equals("AT")) {
	                        String ustId = kunde.getUstid().trim();
	                        BigDecimal betrag = rechnung.getNetto();

	                        mapProQuartal.get(quartal)
	                            .computeIfAbsent(ustId, _ -> new Statistik())
	                            .add(betrag);
	                    }
	                }
	            }
	        }

	        // Übertrage in panel-Felder
	        for (int q = 0; q < 4; q++) {
	            int index = 0;
	            for (Map.Entry<String, Statistik> entry : mapProQuartal.get(q).entrySet()) {
	                panel.setTxtFields(index, q * 2, entry.getKey()); // USt-Id
	                String summeFormatted = nf.format(entry.getValue().summe);
	                panel.setTxtFields(index, q * 2 + 1, summeFormatted + " €");
	                index++;
	            }
	        }

	    } catch (NullPointerException e) {
	        logger.error("error in calculating revenue sum - " + e);
	    }
	}
	
	//###################################################################################################################################################
	
	private static int getQuartalFromString(String datumString, String fPattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(fPattern);
        LocalDate datum = LocalDate.parse(datumString, formatter);
        return (datum.getMonthValue() - 1) / 3 + 1;
    }
}

//###################################################################################################################################################
// interne Klasse
//###################################################################################################################################################

class Statistik {
	long anzahl = 0;
	BigDecimal summe = BD.ZERO;

	void add(BigDecimal betrag) {
		anzahl++;
		summe = summe.add(betrag);
	}

	@Override
	public String toString() {
		return "Anzahl: " + anzahl + ", Summe: " + summe;
    }
 }
