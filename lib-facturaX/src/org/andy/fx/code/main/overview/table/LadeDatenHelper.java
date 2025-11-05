package org.andy.fx.code.main.overview.table;

import java.util.ArrayList;
import java.util.List;

import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;

public class LadeDatenHelper {
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
	public static String searchKunde(String sKdNr) {
		return kundeName(sKdNr);
	}
	
	public static String searchLieferant(String sLieferantNr) {
		return lieferantName(sLieferantNr);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private static String kundeName(String sKdNr) {
		KundeRepository kundeRepository = new KundeRepository();
	    List<Kunde> kundeListe = new ArrayList<>();
	    kundeListe.addAll(kundeRepository.findAll());

		// Prüfen, ob die Kundenliste null ist
		if (kundeListe.size() == 0) {
			return sKdNr; // Falls die Liste leer oder null ist, gib die ursprüngliche Kundennummer zurück.
		}

		for (int kd = 0; kd < kundeListe.size(); kd++) {
			Kunde kunde = kundeListe.get(kd);
			String id = kunde.getId().trim();
			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (id == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (id.equals(sKdNr)) {
				return kunde.getName(); // Gib den Kundennamen zurück
			}
		}
		return sKdNr; // Falls keine Übereinstimmung gefunden wurde, gib die Nummer zurück
	}
	
	private static String lieferantName(String sLieferantNr) {
		LieferantRepository lieferantRepository = new LieferantRepository();
	    List<Lieferant> lieferantListe = new ArrayList<>();
	    lieferantListe.addAll(lieferantRepository.findAll());

		// Prüfen, ob die Kundenliste null ist
		if (lieferantListe.size() == 0) {
			return sLieferantNr; // Falls die Liste leer oder null ist, gib die ursprüngliche Kundennummer zurück.
		}

		for (int kd = 0; kd < lieferantListe.size(); kd++) {
			Lieferant lieferant = lieferantListe.get(kd);
			String id = lieferant.getId().trim();
			// Prüfen, ob die Kunde-Liste null oder zu kurz ist
			if (id == null) {
				continue; // Überspringe ungültige Einträge
			}

			if (id.equals(sLieferantNr)) {
				return lieferant.getName(); // Gib den Kundennamen zurück
			}
		}
		return sLieferantNr; // Falls keine Übereinstimmung gefunden wurde, gib die Nummer zurück
	}

}
