package org.andy.fx.code.misc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CodeListen {
	
	private String[] countryCodes = Locale.getISOCountries(); // ISO 3166-1 alpha2 Codes in Array schreiben
	private Set<Currency> currencyCodes = Currency.getAvailableCurrencies();
    
	private List<String> countries = new ArrayList<>();
	private List<String> currencies = new ArrayList<>();
	
	// ISO 3166-1 alpha-2 Codes der EU (27 Mitglieder, Stand 2025)
    private final Set<String> EU = Set.of(
        "AT","BE","BG","HR","CY","CZ","DK","EE","FI","FR","DE","GR",
        "HU","IE","IT","LV","LT","LU","MT","NL","PL","PT","RO","SK","SI","ES","SE"
    );

    // Eurozone (20 Länder, Stand 2025)
    private final Set<String> EUROZONE = Set.of(
        "AT","BE","HR","CY","EE","FI","FR","DE","GR","IE","IT",
        "LV","LT","LU","MT","NL","PT","SK","SI","ES"
    );
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public boolean isEU(String iso2) {
        return EU.contains(iso2.toUpperCase(Locale.ROOT));
    }

    public boolean isEurozone(String iso2) {
        return EUROZONE.contains(iso2.toUpperCase(Locale.ROOT));
    }
    
    public boolean isCurrency(String code) {
    	List<String> c = new ArrayList<>();
		for (Currency curr : currencyCodes) {
            String entry = curr.getCurrencyCode();
            c.add(entry);
        }
    	for (int i = 0; i < c.size(); i++) {
    		if (code.equals(c.get(i))) return true;
    	}
    	return false;
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
	
	private void doCountryList() {
		List<String> c = new ArrayList<>();
	    for (String code : countryCodes) { // Liste für DropDown erstellen
	        Locale locale = new Locale.Builder().setRegion(code).build();
	        String entryCode = code;
	        String entryCountry = locale.getDisplayCountry(Locale.GERMAN);
	        String entry = entryCode + " - " + entryCountry;
	        c.add(entry);
	    }
	    c.sort(Comparator.comparing(s -> s.substring(5))); // alphabetisch nach Ländernamen sortieren
	    countries.add(" ");
	    countries.addAll(c); // vollständige Liste mit leerem ersten Eintrag
	}
	
	private void doCurrencyList() {
		List<String> c = new ArrayList<>();
		for (Currency curr : currencyCodes) {
            String entry = curr.getCurrencyCode() + " - " + curr.getDisplayName(Locale.GERMAN);
            c.add(entry);
        }
        c.sort(Comparator.comparing(s -> s.substring(6))); // sortieren nach Name
        currencies.add(" ");
        currencies.addAll(c); // vollständige Liste mit leerem ersten Eintrag
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################
	
	public String getCountryFromCode(String code) {
    	if (code.trim().length() < 2 || code.trim().length() > 2) return null;
    	Locale locale = new Locale.Builder().setRegion(code.trim()).build();
    	return locale.getDisplayCountry(Locale.GERMAN);
    }

	public List<String> getCountries() {
		doCountryList();
		return countries;
	}
	
	public List<String> getCurrencies() {
		doCurrencyList();
		return currencies;
	}

}
