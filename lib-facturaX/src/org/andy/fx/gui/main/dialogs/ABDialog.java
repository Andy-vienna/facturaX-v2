package org.andy.fx.gui.main.dialogs;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.KeyboardFocusManager;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import org.andy.fx.code.dataExport.ExcelAuftragsbestaetigung;
import org.andy.fx.code.misc.App;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;

public final class ABDialog extends JDialog {

    private static final Logger log = LogManager.getLogger(ABDialog.class);
    private static final long serialVersionUID = 1L;
    private static App a = new App();

    private final JTextField txtConfNr = new JTextField(12);
    private final DatePicker dpConfDatum = new DatePicker(new DatePickerSettings());
    private final DatePicker dpConfStart = new DatePicker(new DatePickerSettings());

    private final String vZelleA;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public static void showDialog(String vZelleA) {
        EventQueue.invokeLater(() -> {
            Window owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
            new ABDialog(owner, vZelleA).setVisible(true);
        });
    }

    public ABDialog(Window owner, String vZelleA) {
        super(owner, "Dateneingabe Auftragsbestätigung", Dialog.ModalityType.APPLICATION_MODAL);
        this.vZelleA = Objects.requireNonNull(vZelleA, "vZelleA");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setIconSafe("/icons/frames/edit_color.png");

        initPickers();
        setContentPane(buildContent());
        pack();
        setLocationRelativeTo(owner);

        installEscToClose();
        getRootPane().setDefaultButton(findOkButton(getContentPane()));
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private JPanel buildContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel lNr = new JLabel("Bestellnummer:");
        JLabel lDatum = new JLabel("Bestelldatum:");
        JLabel lStart = new JLabel("Startdatum:");

        JButton ok = createOkButton();

        JPanel form = new JPanel();
        GroupLayout gl = new GroupLayout(form);
        form.setLayout(gl);
        gl.setAutoCreateGaps(true);
        gl.setAutoCreateContainerGaps(true);

        gl.setHorizontalGroup(gl.createParallelGroup()
            .addGroup(gl.createSequentialGroup()
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(lNr).addComponent(lDatum).addComponent(lStart))
                .addGroup(gl.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(txtConfNr)
                    .addComponent(dpConfDatum)
                    .addComponent(dpConfStart)))
            .addGroup(gl.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(ok))
        );

        gl.setVerticalGroup(gl.createSequentialGroup()
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lNr).addComponent(txtConfNr))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lDatum).addComponent(dpConfDatum))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(lStart).addComponent(dpConfStart))
            .addGroup(gl.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(ok))
        );

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

    private JButton createOkButton() {
        JButton ok = createButton("OK", ButtonIcon.OK.icon(), null);
        ok.setEnabled(true);
        ok.setMnemonic(KeyEvent.VK_O);
        ok.addActionListener(_ -> onOk());
        return ok;
    }

    private void initPickers() {
        configurePicker(dpConfDatum.getSettings());
        configurePicker(dpConfStart.getSettings());
    }

    private static void configurePicker(DatePickerSettings s) {
        s.setAllowEmptyDates(false);
        s.setWeekNumbersDisplayed(true, true);
        s.setFormatForDatesCommonEra("dd.MM.yyyy");
    }

    private void onOk() {
        String confNr = txtConfNr.getText() != null ? txtConfNr.getText().trim() : "";
        LocalDate dDatum = dpConfDatum.getDate(); LocalDate dStart = dpConfStart.getDate();
        
        LocalDate cDate = LocalDate.parse(dDatum.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate sDate = LocalDate.parse(dStart.toString(), DateTimeFormatter.ISO_LOCAL_DATE);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String confDate = cDate.format(outputFormatter); String startDate = sDate.format(outputFormatter);

        if (confNr.isEmpty() || dDatum == null || dStart == null) {
            JOptionPane.showMessageDialog(this,
                    "Dateneingabe unvollständig.",
                    a.NAME,
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        try {
            ExcelAuftragsbestaetigung.abExport(vZelleA, confNr, confDate, startDate);
        } catch (IOException ex) {
            log.error("I/O bei abExport: {}", ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Dateifehler beim Export. Details siehe Log.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        } catch (Exception ex) {
            log.error("Fehler bei abExport: {}", ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Fehler beim Export. Details siehe Log.", "Fehler", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            HauptFenster.actScreen();
        } catch (Exception ex) {
            log.warn("actScreen meldet: {}", ex.getMessage(), ex);
        }
        dispose();
    }

    private void setIconSafe(String resourcePath) {
        try (InputStream is = ABDialog.class.getResourceAsStream(resourcePath)) {
            if (is != null) setIconImage(ImageIO.read(is));
        } catch (IOException e) {
            log.debug("Icon nicht ladbar: {}", resourcePath, e);
        }
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

    private static JButton findOkButton(Component c) {
        if (c instanceof JButton b && "OK".equalsIgnoreCase(b.getText())) return b;
        if (c instanceof Container ct) {
            for (Component child : ct.getComponents()) {
                JButton found = findOkButton(child);
                if (found != null) return found;
            }
        }
        return null;
    }
    
}
