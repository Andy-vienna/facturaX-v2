package org.andy.fx.gui.main;

import static org.andy.fx.gui.misc.CreateButton.createGradientButton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;
import javax.imageio.ImageIO;
import javax.swing.*;

import org.andy.fx.code.dataStructure.entityMaster.User;
import org.andy.fx.code.dataStructure.repositoryMaster.UserRepository;
import org.andy.fx.code.googleServices.CheckEnvAI;
import org.andy.fx.code.googleServices.GoogleOAuthDesktop;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.Password;
import org.andy.fx.gui.iconHandler.ButtonIcon;

public final class AnmeldeFenster {

    public interface AuthCallback {
        void onSuccess(User user);
        void onCancel();
    }

    private final JFrame frame = new JFrame("Anmeldung");
    private final JTextField userField = new JTextField(12);
    private final JPasswordField passField = new JPasswordField(12);
    private final JButton loginBtn = new JButton("OK");
    private final JButton cancelBtn = new JButton("Cancel");
    
    private JButton oAuth2Btn;

    private final UserRepository userRepository;
    private final AuthCallback callback;

    public AnmeldeFenster(UserRepository repo, AuthCallback cb) {
        this.userRepository = Objects.requireNonNull(repo);
        this.callback = Objects.requireNonNull(cb);

        BufferedImage bg = loadImage("/icons/hintergrund_450.jpg");
        JPanel root = new BackgroundPanel(bg);

        root.setLayout(new GridBagLayout());
        JPanel form = buildFormPanel();

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.anchor  = GridBagConstraints.SOUTH;
        c.fill    = GridBagConstraints.HORIZONTAL;
        c.insets  = new Insets(0, 0, 25, 0);
        root.add(form, c);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setContentPane(root);
        frame.setSize(450, 265);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        setAppIcon(frame, "/icons/frames/icon.png");
        JRootPane rootPane = frame.getRootPane();
        rootPane.setDefaultButton(loginBtn);
        wireActions(rootPane);
    }

    public void show() {
        frame.setVisible(true);
    }

    private JPanel buildFormPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 6, 1, 6);
        gc.anchor = GridBagConstraints.SOUTH;
        gc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Benutzeranmeldung", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        title.setForeground(Color.WHITE);

        gc.gridx = 0; gc.gridy = 0; gc.gridwidth = 2;
        p.add(title, gc);

        gc.gridwidth = 1;

        gc.gridy = 1; gc.gridx = 0;
        gc.gridx = 1; p.add(userField, gc);

        gc.gridy = 2; gc.gridx = 0;
        gc.gridx = 1; p.add(passField, gc);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttons.setOpaque(false);
        buttons.add(loginBtn);
        buttons.add(cancelBtn);

        gc.gridy = 3; gc.gridx = 0; gc.gridwidth = 2;
        p.add(buttons, gc);
        
        JPanel btnGoogle = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        btnGoogle.setOpaque(false);
        
        oAuth2Btn = createGradientButton(
        	"Google Login",
        	ButtonIcon.GOOGLE.icon(),
        	new float[]{0f, 0.33f, 0.66f, 1f},
        	new Color[]{new Color(66, 133, 244), new Color(52, 168, 83), new Color(251, 188, 5), new Color(234, 67, 53)},
        	false);
        oAuth2Btn.setPreferredSize(new Dimension(155, 21));
        oAuth2Btn.setVisible(false);
        if (CheckEnvAI.getSettingsAI().isOAuth2Login) oAuth2Btn.setVisible(true); // nur nach Freischaltung sichtbar
        btnGoogle.add(oAuth2Btn);
        
        gc.gridy = 4; gc.gridx = 0; gc.gridwidth = 2;
        p.add(btnGoogle, gc);

        return p;
    }

    private void wireActions(JRootPane root) {
        // Enter = Login, Esc = Cancel
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();
        im.put(KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), "CANCEL");
        am.put("CANCEL", new AbstractAction() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) { doCancel(); }
        });

        loginBtn.addActionListener(_ -> doLogin());
        cancelBtn.addActionListener(_ -> doCancel());
        oAuth2Btn.addActionListener(_ -> doOAuth2Login());
    }

    private void doLogin() {
    	userField.setEnabled(false); passField.setEnabled(false);
    	loginBtn.setEnabled(false); cancelBtn.setEnabled(false); oAuth2Btn.setEnabled(false);

        final String userId = userField.getText().trim();
        final char[] pwd = passField.getPassword();

        new SwingWorker<User, Void>() {
            @Override protected User doInBackground() {
                try {
                    User u = userRepository.findById(userId);
                    if (u == null) return null;
                    String hash = u.getHash();
                    boolean ok = Password.verifyPwd(pwd, hash);
                    return ok ? u : null;
                } finally {
                    Arrays.fill(pwd, '\0');
                }
            }
            @Override protected void done() {
                try {
                    User u = get();
                    if (u != null) {
                        frame.dispose();
                        callback.onSuccess(u);
                    } else {
                        JOptionPane.showMessageDialog(frame,
                                "Benutzer oder Passwort falsch.",
                                "Anmeldung",
                                JOptionPane.ERROR_MESSAGE);
                        passField.setText("");
                        loginBtn.setEnabled(true);
                        cancelBtn.setEnabled(true);
                        passField.requestFocusInWindow();
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame,
                            "Fehler beim Start der Anwendung.",
                            "Anmeldung",
                            JOptionPane.ERROR_MESSAGE);
                    passField.setText("");
                    loginBtn.setEnabled(true);
                    cancelBtn.setEnabled(true);
                    passField.requestFocusInWindow();
                    callback.onCancel();
                }
            }
        }.execute();
    }

    private void doOAuth2Login() {
    	userField.setEnabled(false); passField.setEnabled(false);
    	loginBtn.setEnabled(false); cancelBtn.setEnabled(false); oAuth2Btn.setEnabled(false);
    
        new SwingWorker<User, Void>() {
        	@Override
			protected User doInBackground() throws Exception {
        		User u = null; boolean ok;
        		try {
                    var r = GoogleOAuthDesktop.login(frame, CheckEnvAI.getCs().clientId(), CheckEnvAI.getCs().clientSecret());
                    u = userRepository.findByEmail(r.email); // user per Mail-Adresse finden 
                    ok = u != null ? ok = true : false;
                } catch (Exception ex) {
                	String message = "<html>" +
                			"<span style='font-size:10px; font-weight:bold; color:black;'>Google Login fehlgeschlagen:</span><br>" +
                			"<span style='font-size:10px; font-weight:bold; color:red;'>E-Mail Adresse für Nutzer nicht hinterlegt ...</span><br>" +
                			"</html>";
                    JOptionPane.showMessageDialog(frame, message, "Anmeldung", JOptionPane.ERROR_MESSAGE);
                    ok = false;
                    StartUp.gracefulQuit(10);
                }
        		return ok ? u : null;
			}
        	@Override protected void done() {
        		try {
        			User u = get();
                    if (u != null) {
                        frame.dispose();
                        callback.onSuccess(u);
                    }
        		} catch (Exception ex) {
        			String message = "<html>" +
                			"<span style='font-size:10px; font-weight:bold; color:black;'>Google Login fehlgeschlagen:</span><br>" +
                			"<span style='font-size:10px; font-weight:bold; color:red;'>kein Nutzer für E-Mail Adresse gefunden ...</span><br>" +
                			"</html>";
                    JOptionPane.showMessageDialog(frame, message, "Anmeldung", JOptionPane.ERROR_MESSAGE);
                    StartUp.gracefulQuit(10);
        		}
        	}
        }.execute();
    }

    private void doCancel() {
        frame.dispose();
        callback.onCancel();
    }

    private static void setAppIcon(Window w, String path) {
        BufferedImage img = loadImage(path);
        if (img != null) w.setIconImage(img);
    }

    private static BufferedImage loadImage(String path) {
        try (InputStream is = AnmeldeFenster.class.getResourceAsStream(path)) {
            return is != null ? ImageIO.read(is) : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    // Panel mit skaliertem Hintergrund
    private static final class BackgroundPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final Image bg;
        BackgroundPanel(Image bg) { this.bg = bg; }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bg != null) {
                int w = getWidth(), h = getHeight();
                g.drawImage(bg, 0, 0, w, h, this);
            }
        }
    }
}
