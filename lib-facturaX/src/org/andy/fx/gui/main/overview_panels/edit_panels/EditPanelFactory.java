package org.andy.fx.gui.main.overview_panels.edit_panels;

import javax.swing.JLabel;

import org.andy.fx.gui.main.overview_panels.edit_panels.factory.RechnungNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.RechnungPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.AngebotNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.AusgabenPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.BestellungNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.BestellungPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.AngebotPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.ArtikelNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.EinkaufPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.KundeNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.LieferantNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.LieferscheinNeuPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.LieferscheinPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.SvTaxPanel;

public class EditPanelFactory {
	
    public static EditPanel create(String sTyp) {
        switch (sTyp) {
            case "AN":  return new AngebotPanel();
            case "NA":  return new AngebotNeuPanel();
            case "NK":  return new KundeNeuPanel();
            case "NArt":  return new ArtikelNeuPanel();
            case "RE":  return new RechnungPanel();
            case "NR":  return new RechnungNeuPanel();
            case "BE":  return new BestellungPanel();
            case "NB":  return new BestellungNeuPanel();
            case "LS":  return new LieferscheinPanel();
            case "nLS": return new LieferscheinNeuPanel();
            case "PU":  return new EinkaufPanel();
            case "NL":  return new LieferantNeuPanel();
            case "EX":  return new AusgabenPanel();
            case "SVT": return new SvTaxPanel();
            default:    return new EditPanel("Unbekannt") {
            	private static final long serialVersionUID = 1L;
				@Override public void initContent() {
                    this.add(new JLabel("Kein Inhalt verfügbar."));
                }
            };
        }
    }
}
