package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.border.TitledBorder;

import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.main.settings_panels.ArtikelPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ArtikelNeuPanel extends EditPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(ArtikelNeuPanel.class);

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public ArtikelNeuPanel() {
    	super("Artikel anlegen oder bearbeiten");
    	if (!(getBorder() instanceof TitledBorder)) {
            logger.warn("Kein TitledBorder gesetzt.");
        }
        //setLayout(null); // beibehalten, um dein Layout nicht aufzubrechen
    	setLayout(new BorderLayout());
        buildPanel();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildPanel() {
    	ArtikelPanel ap = new ArtikelPanel();
    	ap.setBorder(null);
        add(ap, BorderLayout.CENTER);
        setPreferredSize(new Dimension(1000, 480));
    }

	@Override
	public void initContent() {
		// TODO Auto-generated method stub
		
	}
}
