package org.andy.fx.gui.main.dialogs;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import javax.swing.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.andy.fx.code.dataExport.ExcelMahnstufe1;
import org.andy.fx.code.dataExport.ExcelMahnstufe2;
import org.andy.fx.code.dataExport.ExcelZahlungserinnerung;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.iconHandler.FrameIcon;
import org.andy.fx.gui.main.HauptFenster;

public final class MahnstufeDialog extends JDialog {

    private static final Logger log = LogManager.getLogger(MahnstufeDialog.class);
    private static final long serialVersionUID = 1L;

    private final String sachId;
    private final Map<Stage, JRadioButton> radios = new EnumMap<>(Stage.class);
    private final ButtonGroup group = new ButtonGroup();
    
    private String stufe;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public static void open(Window owner, String sId, String state) {
        SwingUtilities.invokeLater(() -> {
            MahnstufeDialog d = new MahnstufeDialog(owner, sId, state);
            d.setVisible(true);
        });
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private MahnstufeDialog(Window owner, String sId, String state) {
        super(owner, "Mahnstufe einleiten", ModalityType.APPLICATION_MODAL);
        this.sachId = sId; this.stufe = state;

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconImage(FrameIcon.RUFZEICHEN.image());

        getContentPane().setLayout(new BorderLayout(12, 12));
        JPanel center = buildCenter();
        JPanel south = buildSouth();

        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(south, BorderLayout.SOUTH);

        pack();
        setMinimumSize(new Dimension(220, getHeight()));
        setLocationRelativeTo(owner);

        // ESC -> schließen, Enter -> Standardbutton auslösen
        installEscToClose();
        getRootPane().setDefaultButton((JButton) south.getClientProperty("defaultButton"));
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private JPanel buildCenter() {
        JPanel p = new JPanel();
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel header = new JLabel(sachId, SwingConstants.CENTER);
        header.setFont(header.getFont().deriveFont(Font.BOLD, 16f));
        header.setForeground(UIManager.getColor("Component.error.focusedBorderColor") != null
                ? UIManager.getColor("Component.error.focusedBorderColor")
                : java.awt.Color.RED);

        JPanel headerWrap = new JPanel(new BorderLayout());
        headerWrap.add(header, BorderLayout.CENTER);
        p.add(headerWrap);
        p.add(Box.createVerticalStrut(8));

        addRadio(p, Stage.S0, "Zahlungserinnerung");
        addRadio(p, Stage.S1, "Mahnstufe 1");
        addRadio(p, Stage.S2, "Mahnstufe 2");
        
        radios.get(Stage.S0).setEnabled(false); radios.get(Stage.S1).setEnabled(false); radios.get(Stage.S2).setEnabled(false);
        
        switch(stufe) {
        	case "gedruckt" -> {radios.get(Stage.S0).setEnabled(true); radios.get(Stage.S0).setSelected(true);}
        	case "Zahlungserinnerung" -> {radios.get(Stage.S1).setEnabled(true); radios.get(Stage.S1).setSelected(true);}
        	case "Mahnstufe 1" -> {radios.get(Stage.S2).setEnabled(true); radios.get(Stage.S2).setSelected(true);}
        }

        return p;
    }

    private void addRadio(JPanel parent, Stage stage, String text) {
        JRadioButton rb = new JRadioButton(text);
        group.add(rb);
        radios.put(stage, rb);

        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        wrap.add(rb);

        parent.add(wrap);
        parent.add(Box.createVerticalStrut(4));
    }

    private JPanel buildSouth() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));

        JButton printBtn;
        printBtn = createButton("<html>Mahnstufe drucken</html>", ButtonIcon.PRINT.icon(), null);
        printBtn.setEnabled(true);
        printBtn.addActionListener(_ -> onPrint());
        printBtn.setMnemonic(KeyEvent.VK_D);

        p.add(printBtn, BorderLayout.CENTER);
        p.putClientProperty("defaultButton", printBtn);
        return p;
    }

    private void onPrint() {
        Stage selected = radios.entrySet().stream()
                .filter(e -> e.getValue().isSelected())
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(Stage.S0);

        try {
            switch (selected) {
                case S0 -> ExcelZahlungserinnerung.reminderExport(sachId);
                case S1 -> ExcelMahnstufe1.mahnungExport(sachId, 1);
                case S2 -> ExcelMahnstufe2.mahnungExport(sachId, 2);
            }
        } catch (IOException io) {
            log.error("I/O-Fehler beim Erstellen der Mahnung: {}", io.getMessage(), io);
            showError("Dateifehler beim Erstellen der Mahnung.\nDetails siehe Log.");
            return;
        } catch (Exception ex) {
            log.error("Fehler beim Erstellen der Mahnung: {}", ex.getMessage(), ex);
            showError("Fehler beim Erstellen der Mahnung.\nDetails siehe Log.");
            return;
        }

        try {
            HauptFenster.actScreen();
        } catch (Exception ex) {
            log.warn("actScreen meldet Fehler: {}", ex.getMessage(), ex);
        }
        dispose();
    }

    private void installEscToClose() {
        Action close = new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { dispose(); }
        };
        JRootPane root = getRootPane();
        root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "esc");
        root.getActionMap().put("esc", close);
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Fehler", JOptionPane.ERROR_MESSAGE);
    }

    private enum Stage { S0, S1, S2 }
}
