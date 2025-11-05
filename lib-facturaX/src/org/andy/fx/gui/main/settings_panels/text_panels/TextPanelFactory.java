package org.andy.fx.gui.main.settings_panels.text_panels;

public class TextPanelFactory {
	
    public static TextPanel create(String sTyp) {
        switch (sTyp) {
            case "T1": return new TextEditor("Angebot");
            case "T2": return new TextEditor("AngebotRev");
            case "T3": return new TextEditor("OrderConfirm");
            case "T4": return new TextEditor("Rechnung");
            case "T5": return new TextEditor("ZahlErin");
            case "T6": return new TextEditor("MahnungStufe1");
            case "T7": return new TextEditor("MahnungStufe2");
            case "T8": return new TextEditor("Bestellung");
            case "T9": return new TextEditor("Lieferschein");
            default:    return null; 
        }
    }
}
