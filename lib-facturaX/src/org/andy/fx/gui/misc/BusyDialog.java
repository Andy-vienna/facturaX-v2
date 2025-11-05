package org.andy.fx.gui.misc;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class BusyDialog {
    private BusyDialog() {}

    public static void run(
    		Window parent,
    		String title,
    		String message,
            Runnable backgroundTask,
            Runnable onSuccess
        ) {
	        JProgressBar bar = new JProgressBar();
	        bar.setIndeterminate(true);
	        JPanel panel = new JPanel(new BorderLayout(10,10));
	        panel.add(new JLabel(message), BorderLayout.NORTH);
	        panel.add(bar, BorderLayout.CENTER);
	
	        JOptionPane pane = new JOptionPane(panel, JOptionPane.INFORMATION_MESSAGE,
	                JOptionPane.DEFAULT_OPTION, null, new Object[]{});
	        final JDialog dialog = pane.createDialog(parent, title);
	        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
	        dialog.setResizable(false);
	        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
	
	        AtomicReference<Throwable> error = new AtomicReference<>();
	
	        SwingWorker<Void,Void> worker = new SwingWorker<>() {
	            @Override protected Void doInBackground() {
	                try { backgroundTask.run(); } catch (Throwable t) { error.set(t); }
	                return null;
	            }
	            @Override protected void done() {
	                dialog.dispose();
	                Throwable t = error.get();
	                if (t != null) {
	                    JOptionPane.showMessageDialog(parent, "Fehler: " + t.getMessage(),
	                            "Aktion fehlgeschlagen", JOptionPane.ERROR_MESSAGE);
	                } else if (onSuccess != null) {
	                    onSuccess.run(); // läuft auf dem EDT
	                }
	            }
	        };
	
	        worker.execute();
	        dialog.setVisible(true);
    }
    
    public static <T> void runAI(
            Window parent,
            String title,
            String message,
            Supplier<T> backgroundTask,   // Worker-Thread
            Consumer<T> onSuccess         // EDT
    ) {
        JProgressBar bar = new JProgressBar();
        bar.setIndeterminate(true);
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(new JLabel(message), BorderLayout.NORTH);
        panel.add(bar, BorderLayout.CENTER);

        JOptionPane pane = new JOptionPane(
                panel, JOptionPane.INFORMATION_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, new Object[] {}
        );
        final JDialog dialog = pane.createDialog(parent, title);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

        AtomicReference<Throwable> error = new AtomicReference<>();

        SwingWorker<T, Void> worker = new SwingWorker<>() {
            @Override
            protected T doInBackground() {
                try {
                    return backgroundTask.get();
                } catch (Throwable t) {
                    error.set(t);
                    return null;
                }
            }
            @Override
            protected void done() {
                dialog.dispose();
                Throwable t = error.get();
                if (t != null) {
                    JOptionPane.showMessageDialog(
                            parent, "Fehler: " + t.getMessage(),
                            "Aktion fehlgeschlagen", JOptionPane.ERROR_MESSAGE
                    );
                    return;
                }
                if (onSuccess != null) {
                    try {
                        onSuccess.accept(get()); // Ergebnis an UI-Callback
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(
                                parent, "Fehler: " + ex.getMessage(),
                                "Aktion fehlgeschlagen", JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
            }
        };

        worker.execute();
        dialog.setVisible(true); // modal, EDT bleibt in Event-Loop aktiv
    }

    
}
