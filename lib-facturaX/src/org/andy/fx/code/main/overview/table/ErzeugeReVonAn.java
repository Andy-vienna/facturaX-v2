package org.andy.fx.code.main.overview.table;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.entityProductive.Rechnung;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.RechnungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ErzeugeReVonAn {
	
	private static final Logger logger = LogManager.getLogger(ErzeugeReVonAn.class);
	
	// Datenquellen
    private final static KundeRepository kundeRepository = new KundeRepository();
    private final static AngebotRepository angebotRepository = new AngebotRepository();
    private final static RechnungRepository rechnungRepository = new RechnungRepository();

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public static void doReVonAn(String anNummer) {
    	erzeugeRechnung(anNummer);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private static void erzeugeRechnung(String anNummer) {
    	
    	Angebot a = angebotRepository.findById(anNummer); // Angebot laden
    	Kunde k = kundeRepository.findById(a.getIdKunde()); // Kunde raussuchen
    	
    	Rechnung r = new Rechnung(); // neue Rechnung aufmachen
    	
    	r.setIdNummer(nextReNummer());
        r.setJahr(Einstellungen.getAppSettings().year);
        r.setDatum(StartUp.getDateNow());
        
        r.setIdKunde(a.getIdKunde());
        r.setIdBank(a.getIdBank());
        r.setRef(a.getRef());
        r.setRevCharge(a.getRevCharge());
        r.setPage2(0);
        r.setSkonto1(0);
        r.setSkonto1tage(0);
        r.setSkonto1wert(BD.ZERO);
        r.setSkonto2(0);
        r.setSkonto2tage(0);
        r.setSkonto2wert(BD.ZERO);

        r.setAnzPos(a.getAnzPos()); // Anzahl Angebotspositionen
        
        for(int i = 0; i < a.getAnzPos(); i++ ) {
			try {
				int idx = i + 1;
				String suf = String.format("%02d", idx);

				// Getter
				String art   	 = (String) Angebot.class.getMethod("getArt" + suf).invoke(a);
				BigDecimal menge = (BigDecimal) Angebot.class.getMethod("getMenge" + suf).invoke(a);
				BigDecimal ep    = (BigDecimal) Angebot.class.getMethod("getePreis" + suf).invoke(a); // Prüfe Groß/Kleinschreibung

				// Setter
				Rechnung.class.getMethod("setArt"   + suf, String.class)     .invoke(r, art);
				Rechnung.class.getMethod("setMenge" + suf, BigDecimal.class) .invoke(r, menge);
				Rechnung.class.getMethod("setePreis"+ suf, BigDecimal.class) .invoke(r, ep);
	            
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				System.out.println(e.getMessage());
				logger.error("error creating bill from offer " + e.getMessage());
			}
		}
        
        BigDecimal ustFaktor = parseStringToBigDecimalSafe(k.getTaxvalue(), LocaleFormat.AUTO).divide(BD.HUNDRED);
        
        r.setNetto(a.getNetto());
        BigDecimal ust = a.getNetto().multiply(ustFaktor);
        r.setUst(ust);
        r.setBrutto(a.getNetto().add(ust));
        
        r.setlZeitr("LZ bitte eintragen ...");
        r.setState(1); // erstellt

        rechnungRepository.save(r); // Rechnung anlegen
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
    
    private static String nextReNummer() {
        int max = rechnungRepository.findMaxNummerByJahr(Einstellungen.getAppSettings().year);
        return "RE-" + Einstellungen.getAppSettings().year + "-" + String.format("%04d", max + 1);
    }
    
}
