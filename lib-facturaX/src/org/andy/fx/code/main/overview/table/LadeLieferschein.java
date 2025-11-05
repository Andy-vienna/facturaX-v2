package org.andy.fx.code.main.overview.table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.BD;

public class LadeLieferschein {
    
    private static BigDecimal sumOpen = BD.ZERO;
    private static BigDecimal sumDelivered = BD.ZERO;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String[][] loadLieferschein(boolean reRun) {
		return loadData(reRun);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String[][] loadData(boolean reRun) {
			
		sumOpen = BD.ZERO; sumDelivered = BD.ZERO;
		
		LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();
	    List<Lieferschein> lieferscheinListe = new ArrayList<>();
	    lieferscheinListe.addAll(lieferscheinRepository.findAllByJahr(Einstellungen.getAppSettings().year));
		
		String[][] sTemp = new String [lieferscheinListe.size()][5];

		for (int i = 0; i < lieferscheinListe.size(); i++){
			Lieferschein lieferschein = lieferscheinListe.get(i);
			
			String status = switch(lieferschein.getState()) {
				case 0 -> "storniert";
				case 1 -> "erstellt";
				case 11 -> "gedruckt";
				case 51 -> "geliefert";
				default -> "-----";
			};
			
			LocalDate date = LocalDate.parse(lieferschein.getDatum().toString(), DateTimeFormatter.ISO_LOCAL_DATE);
	        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	        String datum = date.format(outputFormatter);
	        
			sTemp[i][0] = lieferschein.getIdNummer();
			sTemp[i][1] = status;
			sTemp[i][2] = datum;
			sTemp[i][3] = lieferschein.getRef();
			sTemp[i][4] = LadeDatenHelper.searchKunde(lieferschein.getIdKunde());
			
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
