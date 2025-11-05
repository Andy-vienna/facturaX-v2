package org.andy.fx.gui.main.table_panels;

public class TableHeader {
	
	private final String[] HEADER_AN = { "AN-Nummer", "Status", "Datum", "Referenz", "Kunde", "Netto" };
    private final String[] HEADER_RE = { "RE-Nummer", "Status", "Datum", "Leistungszeitraum", "Referenz", "Kunde", "Netto", "USt.", "Brutto" };
    private final String[] HEADER_BE = { "BE-Nummer", "Status", "Datum", "Referenz", "Lieferant", "Netto", "USt.", "Brutto" };
    private final String[] HEADER_LS = { "LS-Nummer", "Status", "Datum", "Referenz", "Empfänger" };
    private final String[] HEADER_PU = { "RE-Datum","RE-Nummer", "Lieferant", "Land", "Steuersatz", "Netto", "USt.", "Brutto", "Zahlungsziel", "bezahlt", "Dateiname" };
    private final String[] HEADER_EX = { "Datum", "Bezeichnung", "Land", "Steuersatz", "Netto (EUR)", "Steuer (EUR)", "Brutto (EUR)", "Dateiname" };
    private final String[] HEADER_ST = { "Datum", "Zahlungsempfänger", "Bezeichnung", "Betrag", "Fälligkeit", "Art", "Dateiname" };
    
    //###################################################################################################################################################
  	// Getter und Setter
  	//###################################################################################################################################################

	public String[] getHEADER_AN() {
		return HEADER_AN;
	}
	public String[] getHEADER_RE() {
		return HEADER_RE;
	}
	public String[] getHEADER_BE() {
		return HEADER_BE;
	}
	public String[] getHEADER_LS() {
		return HEADER_LS;
	}
	public String[] getHEADER_PU() {
		return HEADER_PU;
	}
	public String[] getHEADER_EX() {
		return HEADER_EX;
	}
	public String[] getHEADER_ST() {
		return HEADER_ST;
	}

}
