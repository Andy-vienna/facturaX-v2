package org.andy.fx.gui.main.settings_panels.text_panels;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToIntSafe;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.text.BadLocationException;

import org.andy.fx.code.dataStructure.entityMaster.Text;
import org.andy.fx.code.dataStructure.repositoryMaster.TextRepository;
import org.andy.fx.code.misc.TextHighlighting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextEditor extends TextPanel  {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(TextEditor.class);
	
	private String typ = null;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public TextEditor(String typ) {
        super("Textbausteine für " + typ + " bearbeiten");
        if (!(getBorder() instanceof TitledBorder)) {
            logger.warn("Kein TitledBorder gesetzt.");
        }
        this.typ = typ;
        buildUI();
    }

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildUI() {
    	
    	assert javax.swing.SwingUtilities.isEventDispatchThread() : "EDT required";

        final TextEditorStruktur tph = new TextEditorStruktur();

        final List<JButton> buttons = tph.getButtons();
        final List<JTextField> fields = tph.getPlaceholderList();
        final List<JTextPane> panes = tph.getTextAreas();
        final List<JLabel> labels = tph.getLabelList();

        final int count = Math.min(
            buttons != null ? buttons.size() : 0,
            Math.min(fields != null ? fields.size() : 0, panes != null ? panes.size() : 0)
        );

        for (int i = 0; i < count; i++) {
            final JButton b = buttons.get(i);
            final JTextField f = fields.get(i);
            final JTextPane p = panes.get(i);
            final JLabel l = labels.get(i);
            if (b == null || f == null || p == null) continue;

            // Initial deaktivieren. Aktivierung bei jeder Änderung.
            b.setEnabled(false);
            b.addActionListener(_ -> handleButtonClick(l, f, p, buttons));

            javax.swing.event.DocumentListener enabler = new javax.swing.event.DocumentListener() {
                private void enable() { b.setEnabled(true); }
                @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { enable(); }
                @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { enable(); }
                @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { enable(); }
            };
            f.getDocument().addDocumentListener(enabler);
            p.getDocument().addDocumentListener(enabler);
        }
        
        setLayout(new BorderLayout());
        add(tph, BorderLayout.CENTER);
		texte(labels, fields, panes, buttons); // Inhalte befüllen
		
		Dimension dim = tph.getSize();
		
    	setPreferredSize(dim); //new Dimension(1750, 700));
    }
    
	//###################################################################################################################################################
    
    private void texte(List<JLabel> labels, List<JTextField> fields, List<JTextPane> panes, List<JButton> buttons) {

    	String tmpVar = null; String tmpTxt = null;
    	
    	List<Text> textListe = new TextRepository().findAll();
    	if (textListe != null && !textListe.isEmpty()) {
    		int n = Math.min(panes.size(), fields.size());
        	for (int i = 0; i < n; i++) {
        	    Text tx = textListe.get(i);
        	    try {
        			tmpVar = (String) Text.class.getMethod("getVarText" + typ).invoke(tx);
        			tmpTxt = (String) Text.class.getMethod("getText" + typ).invoke(tx);
        		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        			logger.error("error reading texts from db: " + e);
        		}
        	    fields.get(i).setText(tmpVar);
        	    panes.get(i).setText(tmpTxt);
        	    labels.get(i).setText(String.valueOf(tx.getId()));
        	}
    		
    		for (int i = 0; i < panes.size(); i++) {
    			if (panes.get(i).getText().length() > 0) {
    				if (panes.get(i).getText().contains("{")) {
    					String text = panes.get(i).getText();
    					panes.get(i).setText(""); // Zurücksetzen, um doppeltes Styling zu vermeiden
    					try {
    						TextHighlighting.applyHighlighting(panes.get(i), text);
    					} catch (BadLocationException e) {
    						logger.error("error applying text highlighting - " + e);
    					}
    				}
    			}
    		}
    	}
    	
		for (int m = 0; m < buttons.size(); m++) {
			buttons.get(m).setEnabled(false);
		}

	}
    
	//###################################################################################################################################################
    
    private void handleButtonClick(JLabel label, JTextField txtVar, JTextPane txtPane, List<JButton> buttons) {
        int dataId = parseStringToIntSafe(label.getText());
        TextRepository repo = new TextRepository();
        Text tx = repo.findById(dataId);
		try {
			Method mVar = Text.class.getMethod("setVarText" + typ, String.class);
			Method mTxt = Text.class.getMethod("setText" + typ, String.class);
			mVar.invoke(tx, txtVar.getText());
			mTxt.invoke(tx, txtPane.getText());
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			logger.error("error writing texts to db: " + e);
		}
        repo.update(tx);
        for (int m = 0; m < buttons.size(); m++) {
			buttons.get(m).setEnabled(false);
		}
    }

}
