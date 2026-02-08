package org.andy.gui.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.andy.code.dataStructure.entity.TimeAccount;
import org.andy.code.dataStructure.repository.EmployeeRepository;
import org.andy.code.dataStructure.repository.TimeAccountRepository;
import org.andy.code.main.Settings;
import org.andy.code.main.StartUp;
import org.andy.code.misc.App;
import org.andy.code.misc.BD;
import org.andy.gui.dialogs.InfoDialog;
import org.andy.gui.iconHandler.FrameIcon;
import org.andy.gui.iconHandler.MenuIcon;
import org.andy.gui.iconHandler.TabIcon;
import org.andy.gui.main.panels.EmployeePanel;
import org.andy.gui.main.panels.StatisticYearPanel;
import org.andy.gui.main.panels.TimeAccountPanel;
import org.andy.gui.main.panels.TimeRangePanelFactory;
import org.andy.gui.main.panels.WorkTimePanel;
import org.andy.gui.misc.RoundedBorder;
import org.andy.gui.misc.WrapLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MainWindow extends JFrame {
	
	private static volatile MainWindow instance; // Instanz bilden

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(MainWindow.class);
    
    private final static int BUTTONX = 130; private static final int BUTTONY = 50;
    private final String[] monthName = { "", "Januar", "Februar", "März", "April", "Mai", "Juni",
            "Juli", "August", "September", "Oktober", "November", "Dezember"};
    
    private final DateTimeFormatter fmt = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendPattern("[MMMM][MMM]") // voll oder kurz
            .toFormatter(Locale.GERMAN);
	
    private static App a = new App();
    EmployeeRepository emRepo = new EmployeeRepository();
    
    // UI
    private JPanel contentPane;
    private JTabbedPane tabPanel;
    private JPanel status, pageTravel, pageTR, pageWorkTime, pageWT, pageEmployee, pageEM;
    private JLabel lblState;
    private JComboBox<String> cmbMonthSP; private JComboBox<String> cmbUserSP;
    private JComboBox<String> cmbMonthWT; private JComboBox<String> cmbUserWT;
    private JComboBox<String> cmbEmployee;
    
    private List<String> userList = null;
    private int monthSP = LocalDate.now().getMonthValue();
    private int monthWT = LocalDate.now().getMonthValue();
    private int userWT = 0; private int userSP = 0;
    private int userEM = 0;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public static void loadGUI() {
        if (SwingUtilities.isEventDispatchThread()) {
            ensureInstanceEDT().setVisible(true);
        } else {
            EventQueue.invokeLater(() -> ensureInstanceEDT().setVisible(true));
        }
    }
    
    //###################################################################################################################################################
    
    public static void actScreen() {
        EventQueue.invokeLater(() -> ensureInstanceEDT().updScreen());
    }

	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    private static MainWindow ensureInstanceEDT() {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Aufruf muss auf dem EDT erfolgen");
        if (instance == null) instance = new MainWindow();
        return instance;
    }
    
    private MainWindow() {

        setIconImage(FrameIcon.ICON.image());
        setTitle(a.NAME + " (" + a.VERSION + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //---------------------------------------------------------------------------------------------------
        // 2 Monitor Betrieb - kann immer auf dem aktuellen Monitor maximiert werden
        setMinimumSize(new Dimension(1280, 1080));

	    // Funktion: MaxBounds für den aktuellen Monitor setzen
	    Runnable applyMaxBounds = () -> {
	        GraphicsConfiguration gc = getGraphicsConfiguration();
	        if (gc == null) return;
	        Rectangle b = gc.getBounds();
	        Insets in = Toolkit.getDefaultToolkit().getScreenInsets(gc);
	        Rectangle usable = new Rectangle(
	                b.x + in.left,
	                b.y + in.top,
	                b.width - in.left - in.right,
	                b.height - in.top - in.bottom
	        );
	        setMaximizedBounds(usable);
	    };
	    // initial anwenden
	    applyMaxBounds.run();
	
	    // Bei Move/Resize neu berechnen (Monitorwechsel durch Ziehen)
	    addComponentListener(new ComponentAdapter() {
	        @Override public void componentMoved(ComponentEvent e)  { applyMaxBounds.run(); }
	        @Override public void componentResized(ComponentEvent e){ applyMaxBounds.run(); }
	    });
	
	    // Bei Maximize sicherstellen, dass aktueller Monitor verwendet wird
	    addWindowStateListener((WindowStateListener) e -> {
	        if ((e.getNewState() & JFrame.MAXIMIZED_BOTH) != 0) {
	            applyMaxBounds.run();
	            SwingUtilities.invokeLater(() ->
	                setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH)
	            );
	        }
	    });
	    setExtendedState(JFrame.MAXIMIZED_BOTH); // startet direkt maximiert
	    //---------------------------------------------------------------------------------------------------
	    
        contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);
        
        userList = emRepo.findUsers();
        
        buildMenuBar();    // Menüzeile bauen
		buildTabs();   // Tabs aufbauen und anzeigen
        buildStatusBar();  // Statuszeile bauen
    }
    
    //###################################################################################################################################################
    // Menü

    private void buildMenuBar() {
        JMenu menu1 = new JMenu("Datei");
        JMenu menu2 = new JMenu("Ansicht");
        JMenu menu3 = new JMenu("Info");

        JMenuItem exit = new JMenuItem("Exit", MenuIcon.EXIT.icon());
        JMenuItem aktualisieren = new JMenuItem("Aktualisieren", MenuIcon.ACT.icon());
        JMenuItem info = new JMenuItem("Info", MenuIcon.INFO.icon());

        menu1.add(exit);
        menu2.add(aktualisieren);
        menu3.add(info);

        JMenuBar menuBar = new JMenuBar();
        menuBar.setBorderPainted(false);
        menuBar.add(menu1);
        menuBar.add(menu2);
        menuBar.add(menu3);
        setJMenuBar(menuBar);

        // Actions
        exit.addActionListener(_ -> StartUp.gracefulQuit(0));
        aktualisieren.addActionListener(_ -> updScreen());
        info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Component c = (Component) e.getSource();
	        	Window owner = SwingUtilities.getWindowAncestor(c);
	        	InfoDialog.show(owner, a);
			}
		});
        
    }
    
    //###################################################################################################################################################
    // Tabs

    private void buildTabs() {
        tabPanel = new JTabbedPane(JTabbedPane.TOP);
        tabPanel.setFont(new Font("Tahoma", Font.BOLD, 12));
        
        if (true) {
			try {
				doSpesenPanel();
			} catch (IOException e) {
				logger.error("error creating panel for travel expenses: " + e.getMessage());
			}
			tabPanel.addTab("Reisespesen", TabIcon.TRAVEL.icon(), pageTR);
		}
        if (true) {
        	doWorkTimePanel();
        	tabPanel.addTab("Arbeitszeit", TabIcon.OFFER.icon(), pageWT);
        }
		if (true) {
			doEmployeePanel();
			tabPanel.addTab("Mitarbeiter", TabIcon.INVOICE.icon(), pageEM);
		}
		
        contentPane.add(tabPanel, BorderLayout.CENTER);
    }
    
    //###################################################################################################################################################
    // Panels
    
    private void doSpesenPanel() throws IOException {

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageTR = new JPanel(new BorderLayout());
        pageTR.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lblMonth = new JLabel("Auswahl Monat für Spesenabrechnung:");
        JLabel lblUser = new JLabel("Benutzer:");
        lblMonth.setFont(new Font("Arial", Font.BOLD, 12));
        cmbMonthSP = new JComboBox<>(monthName);
        cmbMonthSP.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] tmp = userList.stream()
        		.map(String::trim)
                .toArray(String[]::new);
        String[] users = new String[tmp.length + 1];
        users[0] = ""; for (int i = 0; i < tmp.length; i++) { users[i + 1] = tmp[i]; }
        cmbUserSP = new JComboBox<>(users);
        cmbUserSP.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lblMonth); top.add(cmbMonthSP); top.add(lblUser); top.add(cmbUserSP);

        pageTravel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        pageTR.add(top, BorderLayout.NORTH);
        pageTR.add(new JScrollPane(pageTravel), BorderLayout.CENTER);
    	
        cmbMonthSP.addActionListener(_ -> changeDisplayedTravelExpenses());
        cmbUserSP.addActionListener(_ -> changeDisplayedTravelExpenses());
    	
        cmbMonthSP.setSelectedIndex(monthSP); // aktuellen Monat laden
    	cmbUserSP.setSelectedIndex(userSP);
    }
    
    private void doWorkTimePanel() {
 	
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageWT = new JPanel(new BorderLayout());
        pageWT.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lblMonth = new JLabel("Auswahl Monat für Arbeitszeit:");
        JLabel lblUser = new JLabel("Benutzer:");
        lblMonth.setFont(new Font("Arial", Font.BOLD, 12));
        cmbMonthWT = new JComboBox<>(monthName);
        cmbMonthWT.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] tmp = userList.stream()
        		.map(String::trim)
                .toArray(String[]::new);
        String[] users = new String[tmp.length + 1];
        users[0] = ""; for (int i = 0; i < tmp.length; i++) { users[i + 1] = tmp[i]; }
        cmbUserWT = new JComboBox<>(users);
        cmbUserWT.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lblMonth); top.add(cmbMonthWT); top.add(lblUser); top.add(cmbUserWT);

        pageWorkTime = new JPanel(new BorderLayout(5, 5));
        pageWT.add(top, BorderLayout.NORTH);
        pageWT.add(new JScrollPane(pageWorkTime), BorderLayout.CENTER);
    	
    	cmbMonthWT.addActionListener(_ -> changeDisplayedWorkTime());
    	cmbUserWT.addActionListener(_ -> changeDisplayedWorkTime());
    	
    	cmbMonthWT.setSelectedIndex(monthWT); // aktuellen Monat laden
    	cmbUserWT.setSelectedIndex(userWT);
    }
    
    private void doEmployeePanel() {
  	
    	TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageEM = new JPanel(new BorderLayout());
        pageEM.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lblUser = new JLabel("Auswahl Mitarbeiter zum bearbeiten (nichts wählen zur Neuanlage):");
        lblUser.setFont(new Font("Arial", Font.BOLD, 12));
        
        String[] tmp = userList.stream()
        		.map(String::trim)
                .toArray(String[]::new);
        String[] users = new String[tmp.length + 1];
        users[0] = ""; for (int i = 0; i < tmp.length; i++) { users[i + 1] = tmp[i]; }
        cmbEmployee = new JComboBox<>(users);
        cmbEmployee.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lblUser); top.add(cmbEmployee);

        pageEmployee = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        pageEM.add(top, BorderLayout.NORTH);
        pageEM.add(new JScrollPane(pageEmployee), BorderLayout.CENTER);
    	
        cmbEmployee.addActionListener(_ -> changeDisplayedEmployee());
        
        cmbEmployee.setSelectedIndex(userEM);
    }
    
    
    //###################################################################################################################################################
    // Statusbar

    private void buildStatusBar() {
    	var dateNow = java.time.LocalDate.now(java.time.ZoneId.systemDefault());
        var dfDate = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
        var dtNow = dateNow.format(dfDate);
        String sStatus = String.format("<html>"
                		+ "<b>%s</b>"											// Datum
                        + " | Daten-DB: <font color='blue'><b>%s</b></font>"	// DB-Name Produktiv-Datenbank
                        + " | DB-Server: <font color='red'><b>%s</b></font>"	// DB-Server
                        + "</html>",
					                dtNow,								// Datum
					                Settings.getSettings().dbData,	// DB-Name Produktiv-Datenbank
					                App.DB);							// DB-Server

        lblState = new JLabel(sStatus);
        lblState.setBorder(new RoundedBorder(10));
        lblState.setHorizontalAlignment(SwingConstants.LEFT);
        lblState.setOpaque(true);
        lblState.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblState.setBackground(new Color(152, 251, 152));

        status = new JPanel(new BorderLayout(1, 0));
        status.add(lblState, BorderLayout.CENTER);
        contentPane.add(status, BorderLayout.SOUTH);
    }
    
    //###################################################################################################################################################
    // Hilfsmethoden
    //###################################################################################################################################################
    
    private void changeDisplayedTravelExpenses() {
    	pageTravel.removeAll();
		if (cmbMonthSP.getSelectedIndex() == 0 || cmbUserSP.getSelectedIndex() == 0) {
			pageTravel.revalidate(); pageTravel.repaint();
			return;
		}
		
		String month = cmbMonthSP.getSelectedItem().toString();
		String user = cmbUserSP.getSelectedItem().toString();
		
        pageTravel.add(new TimeRangePanelFactory(month, user));
        
        Month m = Month.from(fmt.parse(month)); // z.B. "Februar", "März"
		monthSP = m.ordinal() + 1; userSP = cmbUserSP.getSelectedIndex();
        pageTravel.revalidate(); pageTravel.repaint();
    }
    
    private void changeDisplayedWorkTime() {
    	pageWorkTime.removeAll();
		if (cmbMonthWT.getSelectedIndex() == 0 || cmbUserWT.getSelectedIndex() == 0) {
			pageWorkTime.revalidate(); pageWorkTime.repaint();
			return;
		}
		String inhalt = cmbMonthWT.getSelectedItem().toString();
		String benutzer = cmbUserWT.getSelectedItem().toString();
		
		TimeAccountRepository taRepo = new TimeAccountRepository();
		TimeAccount ta = taRepo.findByUserAndYear(benutzer, Settings.getSettings().year);
		
		if (ta == null) {
        	TimeAccount h = new TimeAccount();
        	h.setTiPrinted(0);
        	h.setUserName(benutzer);
        	h.setContractHours(BD.ZERO);
        	h.setOverTime(BD.ZERO);
        	h.setYear(Settings.getSettings().year);
        	taRepo.save(h);
        	
        	ta = taRepo.findByUserAndYear(benutzer, Settings.getSettings().year);
        }
		
		BigDecimal hoursDay = ta.getContractHours().divide(BD.FIVE);
		
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		JPanel panelRe = new JPanel(new GridLayout(2, 1, 30, 5));
		
		panelRe.add(new TimeAccountPanel(inhalt, benutzer));
		panelRe.add(new StatisticYearPanel(Settings.getSettings().year, benutzer));
		panelRe.setPreferredSize(new Dimension(555, 750));
        
		panel.add(new WorkTimePanel(inhalt, benutzer, hoursDay));
        panel.add(panelRe, BorderLayout.EAST);
        pageWorkTime.add(panel);

		Month m = Month.from(fmt.parse(inhalt)); // z.B. "Februar", "März"
		monthWT = m.ordinal() + 1; userWT = cmbUserWT.getSelectedIndex();
		pageWorkTime.revalidate(); pageWorkTime.repaint();
    }
    
    private void changeDisplayedEmployee() {
    	pageEmployee.removeAll();
		String benutzer = cmbEmployee.getSelectedItem().toString();
		
		pageEmployee.add(new EmployeePanel(benutzer));
		userEM = cmbEmployee.getSelectedIndex();
		pageEmployee.revalidate(); pageEmployee.repaint();
    }
    
    private void updScreen() {
    	int oldIdx = tabPanel.getSelectedIndex(); // aktuell angewählten Tab sichern

        pageTR = null; pageWT = null; pageEM = null; 
        
        status.removeAll();
        this.remove(status);
        
        if (true) {
        	tabPanel.removeTabAt(tabPanel.indexOfTab("Reisespesen"));
			try {
				doSpesenPanel();
			} catch (IOException e) {
				logger.error("error creating panel for travel expenses: " + e.getMessage());
			}
			tabPanel.addTab("Reisespesen", TabIcon.TRAVEL.icon(), pageTR);
		}
        if (true) {
        	tabPanel.removeTabAt(tabPanel.indexOfTab("Arbeitszeit"));
        	doWorkTimePanel();
        	tabPanel.addTab("Arbeitszeit", TabIcon.OFFER.icon(), pageWT);
        }
		if (true) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Mitarbeiter"));
			doEmployeePanel();
			tabPanel.addTab("Mitarbeiter", TabIcon.INVOICE.icon(), pageEM);
		}
		
		buildStatusBar();
        contentPane.revalidate();
        contentPane.repaint();
        
        int idx = Math.min(Math.max(oldIdx, 0), tabPanel.getTabCount() - 1);
        tabPanel.setSelectedIndex(idx); // vorher aktiven Tab wieder aktivieren
    }
    
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	public static int getButtonx() {
		return BUTTONX;
	}

	public static int getButtony() {
		return BUTTONY;
	}
    
    
}
