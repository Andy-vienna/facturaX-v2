package org.andy.fx.gui.main;

import static org.andy.fx.gui.misc.CreateButton.createButton;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowStateListener;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.andy.datamigration.gui.MigrationPanel;
import org.andy.fx.code.dataExport.ExcelAngebot;
import org.andy.fx.code.dataExport.ExcelAngebotRevision;
import org.andy.fx.code.dataExport.ExcelBestellung;
import org.andy.fx.code.dataExport.ExcelLieferschein;
import org.andy.fx.code.dataExport.ExcelRechnung;
import org.andy.fx.code.dataStructure.entityJSON.JsonApp;
import org.andy.fx.code.dataStructure.entityJSON.JsonUtil;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.main.overview.result.SteuerDaten;
import org.andy.fx.code.main.overview.result.UStDaten;
import org.andy.fx.code.main.overview.result.ZMeldungDaten;
import org.andy.fx.code.main.overview.table.ErzeugeReVonAn;
import org.andy.fx.code.main.overview.table.LadeAngebot;
import org.andy.fx.code.main.overview.table.LadeAusgaben;
import org.andy.fx.code.main.overview.table.LadeBestellung;
import org.andy.fx.code.main.overview.table.LadeEinkauf;
import org.andy.fx.code.main.overview.table.LadeLieferschein;
import org.andy.fx.code.main.overview.table.LadeRechnung;
import org.andy.fx.code.main.overview.table.LadeSvTax;
import org.andy.fx.code.misc.App;
import org.andy.fx.code.misc.BD;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.iconHandler.FrameIcon;
import org.andy.fx.gui.iconHandler.MenuIcon;
import org.andy.fx.gui.iconHandler.TabIcon;
import org.andy.fx.gui.main.dialogs.ABDialog;
import org.andy.fx.gui.main.dialogs.DateianzeigeDialog;
import org.andy.fx.gui.main.dialogs.InfoDialog;
import org.andy.fx.gui.main.dialogs.MahnstufeDialog;
import org.andy.fx.gui.main.overview_panels.SummenPanelA;
import org.andy.fx.gui.main.overview_panels.SummenPanelB;
import org.andy.fx.gui.main.overview_panels.edit_panels.TimeRangePanelFactory;
import org.andy.fx.gui.main.overview_panels.edit_panels.WorkTimePanelFactory;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanelFactory;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.AngebotPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.AusgabenPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.BestellungPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.EinkaufPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.LieferscheinPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.RechnungPanel;
import org.andy.fx.gui.main.overview_panels.edit_panels.factory.SvTaxPanel;
import org.andy.fx.gui.main.result_panels.SteuerPanel;
import org.andy.fx.gui.main.result_panels.UStPanel;
import org.andy.fx.gui.main.result_panels.ZMeldungPanel;
import org.andy.fx.gui.main.settings_panels.AIfeaturePanel;
import org.andy.fx.gui.main.settings_panels.ArtikelPanel;
import org.andy.fx.gui.main.settings_panels.BankPanel;
import org.andy.fx.gui.main.settings_panels.BenutzerPanel;
import org.andy.fx.gui.main.settings_panels.DatenbankPanel;
import org.andy.fx.gui.main.settings_panels.GwbTabellePanel;
import org.andy.fx.gui.main.settings_panels.KundePanel;
import org.andy.fx.gui.main.settings_panels.LieferantPanel;
import org.andy.fx.gui.main.settings_panels.OwnerPanel;
import org.andy.fx.gui.main.settings_panels.PfadPanel;
import org.andy.fx.gui.main.settings_panels.QrCodePanel;
import org.andy.fx.gui.main.settings_panels.SteuertabellePanel;
import org.andy.fx.gui.main.settings_panels.text_panels.TextPanelFactory;
import org.andy.fx.gui.main.table_panels.ErzeugePanelA;
import org.andy.fx.gui.main.table_panels.ErzeugePanelB;
import org.andy.fx.gui.main.table_panels.ErzeugeTabelle;
import org.andy.fx.gui.main.table_panels.TabMask;
import org.andy.fx.gui.main.table_panels.TableHeader;
import org.andy.fx.gui.misc.BusyDialog;
import org.andy.fx.gui.misc.RoundedBorder;
import org.andy.fx.gui.misc.WrapLayout;
import org.andy.versuche.gui.VersuchPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HauptFenster extends JFrame {
	private static volatile HauptFenster instance; // Instanz bilden

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(HauptFenster.class);
    private static App a = new App();

    private TableHeader header = new TableHeader();
    private final static int BUTTONX = 130; private static final int BUTTONY = 50;
    
    // Rollen
    enum Role { NONE, USER, SUPERUSER, FINANCIALUSER, ADMIN }

    // Status/Session
    private String sLic;
    private int iLic;
    private Role role;

    // UI
    private JPanel contentPane;
    private JTabbedPane tabPanel;
    private JLabel lblState;
    // Seiten
    private final JPanel pageAN = new JPanel(new BorderLayout());
    private final JPanel pageRE = new JPanel(new BorderLayout());
    private final JPanel pageBE = new JPanel(new BorderLayout());
    private final JPanel pageLS = new JPanel(new BorderLayout());
    private final JPanel pagePU = new JPanel(new BorderLayout());
    private final JPanel pageEX = new JPanel(new BorderLayout());
    private final JPanel pageST = new JPanel(new BorderLayout());
    private JPanel status, pageTravel, pageTR, pageOv, pageErg, pageWorkTime, pageWT, pageAdmin, pageSetting, pageMigration, pageVersuch;

    // Panels, Tabellen, Summen
    private EditPanel offerPanel, billPanel, bestellungPanel, lieferscheinPanel, purchasePanel, svTaxPanel, expensesPanel;
    private SummenPanelA infoAN, infoRE, infoBE, infoLS;
	private SummenPanelB infoPU, infoEX, infoST;
	
    private UStPanel panelUSt;
    private ZMeldungPanel panelZM;
    private SteuerPanel panelP109a;
    private JScrollPane sPaneErg;
    private ErzeugeTabelle<Object> sPaneAN, sPaneRE, sPaneBE, sPaneLS, sPanePU, sPaneEX, sPaneST;
    private ErzeugePanelA panelOfferInfo, panelBillInfo, panelBEInfo, panelLSInfo;

    // Daten
    private String[][] sTempAN, sTempRE, sTempBE, sTempLS, sTempPU, sTempEX, sTempST;

    // Auswahl
    private String vZelleAN, vStateAN, vZelleRE, vStateRE, vZelleBE, vStateBE, vZelleLS, vStateLS;
    private int month = LocalDate.now().getMonthValue();

    private static String u, r, m; // user, Rolle, Mail
    private static int tc; // Tab-Config
    
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public static void loadGUI(String u, String m, String r, int tc) {
    	HauptFenster.u = u; HauptFenster.r = r; HauptFenster.m = m; HauptFenster.tc = tc;
    	
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
    
    private static HauptFenster ensureInstanceEDT() {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Aufruf muss auf dem EDT erfolgen");
        if (instance == null) instance = new HauptFenster();
        return instance;
    }

    private HauptFenster() {
        sLic = a.LICENSE;
        iLic = a.MODE;
        role = roleFromLogin(r);

        setIconImage(FrameIcon.ICON.image());
        setTitle(a.NAME + " (" + a.VERSION + ") - " + sLic);
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
        
        buildMenuBar();    // Menüzeile bauen
        loadData();        // Tabellen-Daten laden
        buildTabs(role);   // Tabs aufbauen und anzeigen
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

        // Lizenzzustand
        if (iLic == 0) { // nicht lizenziert
            menu1.setEnabled(false);
            menu2.setEnabled(false);
        } else if (iLic == 1) { // Demo
            aktualisieren.setEnabled(false);
        }

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

    private void buildTabs(Role role) {
        tabPanel = new JTabbedPane(JTabbedPane.TOP);
        tabPanel.setFont(new Font("Tahoma", Font.BOLD, 12));

        if (TabMask.visible(tc, TabMask.Tab.TIME)) {
			try {
				doWorkTimePanel(u);
			} catch (IOException e) {
				logger.error("error creating panel for work time: " + e.getMessage());
			}
			tabPanel.addTab("Arbeitszeit", TabIcon.TIME.icon(), pageWT);
		}
        if (TabMask.visible(tc, TabMask.Tab.TRAVEL)) {
			try {
				doSpesenPanel();
			} catch (IOException e) {
				logger.error("error creating panel for travel expenses: " + e.getMessage());
			}
			tabPanel.addTab("Reisespesen", TabIcon.TRAVEL.icon(), pageTR);
		}
        if (TabMask.visible(tc, TabMask.Tab.OFFER)) {
        	doAngebotPanel(0);
        	tabPanel.addTab("Angebote", TabIcon.OFFER.icon(), pageAN);
        }
		if (TabMask.visible(tc, TabMask.Tab.INVOICE)) {
			doRechnungPanel(0);
			tabPanel.addTab("Rechnungen", TabIcon.INVOICE.icon(), pageRE);
		}
		if (TabMask.visible(tc, TabMask.Tab.ORDER)) {
        	doBestellungPanel(0);
        	tabPanel.addTab("Bestellungen", TabIcon.ORDER.icon(), pageBE);
        }
		if (TabMask.visible(tc, TabMask.Tab.DELIVERY)) {
			doLieferscheinPanel(0);
			tabPanel.addTab("Lieferscheine", TabIcon.DELIVERY_NOTE.icon(), pageLS);
		}
		if (TabMask.visible(tc, TabMask.Tab.PURCHASE)) {
        	doEinkaufPanel(0);
        	tabPanel.addTab("Einkauf", TabIcon.PURCHASE.icon(), pagePU);
        }
		if (TabMask.visible(tc, TabMask.Tab.EXPENSES)) {
			doAusgabenPanel();
			tabPanel.addTab("Betriebsausgaben", TabIcon.EXPENSES.icon(), pageEX);
		}
		if (TabMask.visible(tc, TabMask.Tab.TAX)) {
			doSvsTaxPanel();
        	tabPanel.addTab("SV und Steuer", TabIcon.TAX.icon(), pageST);
        }
		if (TabMask.visible(tc, TabMask.Tab.RESULT)) {
			doJahresergebnis();
			tabPanel.addTab("Jahresergebnis", TabIcon.RESULT.icon(), pageOv);
		}
		if (TabMask.visible(tc, TabMask.Tab.SETTINGS)) {
			doEinstellungen();
			tabPanel.addTab("Einstellungen", TabIcon.SETTINGS.icon(), pageAdmin);
		}
		if (TabMask.visible(tc, TabMask.Tab.MIGRATION)) {
			doMigration();
			tabPanel.addTab("Datenmigration", TabIcon.MIGRATION.icon(), pageMigration);
		}
		if (TabMask.visible(tc,  TabMask.Tab.TRIALS)) {
			doVersuch();
			tabPanel.addTab("Versuche",  TabIcon.MIGRATION.icon(), pageVersuch);
		}
        contentPane.add(tabPanel, BorderLayout.CENTER);
    }

    //###################################################################################################################################################
    // Panels
    
    private void doWorkTimePanel(String user) throws IOException {
    	
    	final DateTimeFormatter fmt = new DateTimeFormatterBuilder()
                .parseCaseInsensitive()
                .appendPattern("[MMMM][MMM]") // voll oder kurz
                .toFormatter(Locale.GERMAN);
    	
    	String[] select = { "", "Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August", "September", "Oktober", "November", "Dezember"};

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageWT = new JPanel(new BorderLayout());
        pageWT.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lbl = new JLabel("Auswahl Monat für Arbeitszeit:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        JComboBox<String> cmbSelect = new JComboBox<>(select);
        cmbSelect.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lbl); top.add(cmbSelect);

        pageWorkTime = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        pageWT.add(top, BorderLayout.NORTH);
        pageWT.add(new JScrollPane(pageWorkTime), BorderLayout.CENTER);
    	
    	cmbSelect.addActionListener(_ -> {
    		pageWorkTime.removeAll();
    		if (cmbSelect.getSelectedIndex() == 0) {
    			pageWorkTime.revalidate(); pageWorkTime.repaint();
    			return;
    		}
    		pageWorkTime.add(new WorkTimePanelFactory(cmbSelect.getSelectedItem().toString(), user));
    		Month m = Month.from(fmt.parse(cmbSelect.getSelectedItem().toString())); // z.B. "Februar", "März"
    		month = m.ordinal() + 1; // 0..11
    		pageWorkTime.revalidate(); pageWorkTime.repaint();
        });
    	
    	cmbSelect.setSelectedIndex(month); // aktuellen Monat laden
    	
    }
    
    //###################################################################################################################################################
    
    private void doSpesenPanel() throws IOException {
    	
    	String[] select = { "", "Januar", "Februar", "März", "April", "Mai", "Juni",
                "Juli", "August", "September", "Oktober", "November", "Dezember"};

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageTR = new JPanel(new BorderLayout());
        pageTR.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lbl = new JLabel("Auswahl Monat für Spesenabrechnung:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        JComboBox<String> cmbSelect = new JComboBox<>(select);
        cmbSelect.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lbl); top.add(cmbSelect);

        pageTravel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        pageTR.add(top, BorderLayout.NORTH);
        pageTR.add(new JScrollPane(pageTravel), BorderLayout.CENTER);
    	
    	cmbSelect.addActionListener(_ -> {
    		pageTravel.removeAll();
    		if (cmbSelect.getSelectedIndex() == 0) {
    			pageTravel.revalidate(); pageTravel.repaint();
    			return;
    		}
            pageTravel.add(new TimeRangePanelFactory(cmbSelect.getSelectedItem().toString()));
            pageTravel.revalidate(); pageTravel.repaint();
        });
    }
    
    //###################################################################################################################################################

    private void doAngebotPanel(int use) {

        JButton[] btn = null;
        switch(use) {
        	case 0 -> {
        		btn = new JButton[6];
                offerPanel = EditPanelFactory.create("AN");
                btn[0] = createButton("<html>Kunde<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(168,168,168));
                btn[1] = createButton("<html>Artikel<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(159,182,205));
                btn[2] = createButton("<html>neues<br>Angebot</html>", ButtonIcon.NEW.icon(), new Color(191,239,255));
                btn[3] = createButton("<html>Angebot<br>drucken</html>", ButtonIcon.PRINT.icon(), null);
                btn[4] = createButton("<html>AB<br>drucken</html>", ButtonIcon.BEST.icon(), null);
                btn[5] = createButton("<html>Rechnung<br>erzeugen</html>", ButtonIcon.FORWARD.icon(), null);
                btn[1].setEnabled(true); btn[2].setEnabled(true);
        	}
        	case 1 -> {
        		btn = new JButton[1];
                offerPanel = EditPanelFactory.create("NA");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 2 -> {
        		btn = new JButton[1];
            	offerPanel = EditPanelFactory.create("NK");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 3 -> {
        		btn = new JButton[1];
            	offerPanel = EditPanelFactory.create("NArt");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        }
        btn[0].setEnabled(true);
        
        if ((use == 0) && role.name().equals("USER")) {	btn[0].setEnabled(false); btn[1].setEnabled(false); }

        sPaneAN = new ErzeugeTabelle<>(sTempAN, header.getHEADER_AN(), new TableANcr(this));
        sPaneAN.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickAN(sPaneAN.getTable(), e); }
        });
        sPaneAN.setColumnWidths(new int[] {120,120,120,750,300,200});
        sPaneAN.getTable().setAutoCreateRowSorter(true);

        infoAN = new SummenPanelA(new String[] {"Summe offen:", "Summe best.:"}, true);
        setSumAN();
        if (use == 1 || use == 2 || use == 3) infoAN.setVisible(false);

        panelOfferInfo = new ErzeugePanelA(sPaneAN, offerPanel, btn, infoAN);

        // Actions
        if (use == 1 || use == 2 || use == 3) {
        	btn[0].addActionListener(_ -> doAngebotPanel(0));
        }
        if (use == 0) {
        	btn[0].addActionListener(_ -> { if (use == 0) doAngebotPanel(2); else updScreen(); });
        	btn[1].addActionListener(_ -> { if (use == 0) doAngebotPanel(3); else updScreen(); });
        	btn[2].addActionListener(_ -> { if (use == 0) doAngebotPanel(1); else updScreen(); });
        	btn[3].addActionListener(e -> {
        	    if (vZelleAN == null) return;
        	    Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
        	    BusyDialog.run(w,
        	        "Bitte warten",
        	        "Angebot wird erzeugt …",
        	        () -> {
						try {
							if(vZelleAN.contains("/")) {
								ExcelAngebotRevision.anExport(vZelleAN); // Angebotsrevision
							} else {
								ExcelAngebot.anExport(vZelleAN); //Angebot
							}
						} catch (Exception ex) {
							logger.error("error exporting offer: ", ex);
						}
					},
        	        this::updScreen
        	    );
        	});
            btn[4].addActionListener(_ -> { if (vZelleAN != null) { try { ABDialog.showDialog(vZelleAN); updScreen(); } catch (Exception ex) { logger.error("AN AB", ex); } }});
            btn[5].addActionListener(_ -> { if (vZelleAN != null) { ErzeugeReVonAn.doReVonAn(vZelleAN); updScreen(); }});
        }

        pageAN.removeAll();
        pageAN.add(panelOfferInfo, BorderLayout.CENTER);
        pageAN.revalidate(); pageAN.repaint();
    }
    
    //###################################################################################################################################################

    private void doRechnungPanel(int use) {

        JButton[] btn = null;
        switch(use) {
        	case 0 -> {
        		btn = new JButton[5];
        		billPanel = EditPanelFactory.create("RE");
                btn[0] = createButton("<html>Kunde<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(168,168,168));
                btn[1] = createButton("<html>Artikel<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(159,182,205));
                btn[2] = createButton("<html>neue<br>Rechnung</html>", ButtonIcon.NEW.icon(), new Color(191,239,255));
                btn[3] = createButton("<html>Rechnung<br>drucken</html>", ButtonIcon.PRINT.icon(), null);
                btn[4] = createButton("<html>Mahn-<br>verfahren</html>", ButtonIcon.ALARM.icon(), null);
                btn[1].setEnabled(true); btn[2].setEnabled(true);
        	}
        	case 1 -> {
        		btn = new JButton[1];
        		billPanel = EditPanelFactory.create("NR");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 2 -> {
        		btn = new JButton[1];
        		billPanel = EditPanelFactory.create("NK");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 3 -> {
        		btn = new JButton[1];
        		billPanel = EditPanelFactory.create("NArt");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        }
        btn[0].setEnabled(true);
        
        if ((use == 0) && role.name().equals("USER")) {	btn[0].setEnabled(false); btn[1].setEnabled(false); }

        sPaneRE = new ErzeugeTabelle<>(sTempRE, header.getHEADER_RE(), new TableREcr(this));
        sPaneRE.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickRE(sPaneRE.getTable(), e); }
        });
        sPaneRE.setColumnWidths(new int[] {120,120,120,200,650,200,150,150,150});
        sPaneRE.getTable().setAutoCreateRowSorter(true);

        infoRE = new SummenPanelA(new String[] {"Summe offen:", "Summe bez.:"}, true);
        setSumRE();
        if (use == 1 || use == 2 || use == 3) infoRE.setVisible(false);

        panelBillInfo = new ErzeugePanelA(sPaneRE, billPanel, btn, infoRE);
        
        // Actions
        if (use == 1 || use == 2 || use == 3) {
        	btn[0].addActionListener(_ -> doRechnungPanel(0));
        }
        if (use == 0) {
        	btn[0].addActionListener(_ -> { if (use == 0) doRechnungPanel(2); else updScreen(); });
        	btn[1].addActionListener(_ -> { if (use == 0) doRechnungPanel(3); else updScreen(); });
        	btn[2].addActionListener(_ -> { if (use == 0) doRechnungPanel(1); else updScreen(); });
        	btn[3].addActionListener(e -> {
        		if (vZelleRE == null) return;
        	    Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
        	    BusyDialog.run(w,
        	        "Bitte warten",
        	        "Rechnung wird erzeugt …",
        	        () -> {
						try {
							ExcelRechnung.reExport(vZelleRE);
						} catch (Exception ex) {
							logger.error("error exporting bill: ", ex);
						}
					},
        	        this::updScreen
        	    );
        	});
            btn[4].addActionListener(_ -> { if (vZelleRE != null) { MahnstufeDialog.open(null, vZelleRE, vStateRE); updScreen(); }});
        }

        pageRE.removeAll();
        pageRE.add(panelBillInfo, BorderLayout.CENTER);
        pageRE.revalidate(); pageRE.repaint();
    }
    
    //###################################################################################################################################################
    
    private void doBestellungPanel(int use) {
        
        JButton[] btn = null;
        switch(use) {
        	case 0 -> {
        		btn = new JButton[4];
        		bestellungPanel = EditPanelFactory.create("BE");
        		btn[0] = createButton("<html>Lieferant<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(168,168,168));
                btn[1] = createButton("<html>Artikel<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(159,182,205));
                btn[2] = createButton("<html>neue<br>Bestellung</html>", ButtonIcon.NEW.icon(), new Color(191,239,255));
                btn[3] = createButton("<html>Bestellung<br>drucken</html>", ButtonIcon.PRINT.icon(), null);
                btn[1].setEnabled(true); btn[2].setEnabled(true);
        	}
        	case 1 -> {
        		btn = new JButton[1];
        		bestellungPanel = EditPanelFactory.create("NB");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 2 -> {
        		btn = new JButton[1];
        		bestellungPanel = EditPanelFactory.create("NL");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 3 -> {
        		btn = new JButton[1];
        		bestellungPanel = EditPanelFactory.create("NArt");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        }
        btn[0].setEnabled(true);
        
        if ((use == 0) && role.name().equals("USER")) {	btn[0].setEnabled(false); btn[1].setEnabled(false); }
        
        sPaneBE = new ErzeugeTabelle<>(sTempBE, header.getHEADER_BE(), new TableBEcr(this));
        sPaneBE.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickBE(sPaneBE.getTable(), e); }
        });
        sPaneBE.setColumnWidths(new int[] {120,120,120,500,500,100,100,100});
        sPaneBE.getTable().setAutoCreateRowSorter(true);
        
        infoBE = new SummenPanelA(new String[] {"Summe offen:", "Summe gel.:"}, false);
        setSumBE();
        if (use == 1) infoBE.setVisible(false);

        panelBEInfo = new ErzeugePanelA(sPaneBE, bestellungPanel, btn, infoBE);
        
        if (use == 1 || use == 2 || use == 3) {
        	btn[0].addActionListener(_ -> doBestellungPanel(0));
        }
        if (use == 0) {
        	btn[0].addActionListener(_ -> { if (use == 0) doBestellungPanel(2); else updScreen(); });
        	btn[1].addActionListener(_ -> { if (use == 0) doBestellungPanel(3); else updScreen(); });
        	btn[2].addActionListener(_ -> { if (use == 0) doBestellungPanel(1); else updScreen(); });
        	btn[3].addActionListener(e -> {
        		if (vZelleBE == null) return;
        	    Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
        	    BusyDialog.run(w,
        	        "Bitte warten",
        	        "Bestellung wird erzeugt …",
        	        () -> {
						try {
							ExcelBestellung.beExport(vZelleBE);
						} catch (Exception ex) {
							logger.error("error exporting order: ", ex);
						}
					},
        	        this::updScreen
        	    );
        	});
        }

        pageBE.removeAll();
        pageBE.add(panelBEInfo, BorderLayout.CENTER);
        pageBE.revalidate(); pageBE.repaint();
    	
    }
    
    //###################################################################################################################################################
    
    private void doLieferscheinPanel(int use) {
    	
    	JButton[] btn = null;
        switch(use) {
        	case 0 -> {
        		btn = new JButton[2];
        		lieferscheinPanel = EditPanelFactory.create("LS");
                btn[0] = createButton("<html>neuer<br>Lieferschein</html>", ButtonIcon.NEW.icon(), new Color(191,239,255));
                btn[1] = createButton("<html>Lieferschein<br>drucken</html>", ButtonIcon.PRINT.icon(), null);
        	}
        	case 1 -> {
        		btn = new JButton[1];
        		lieferscheinPanel = EditPanelFactory.create("nLS");
                btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        	case 2 -> {
        		
        	}
        	case 3 -> {
        		
        	}
        }
        btn[0].setEnabled(true);
        
        sPaneLS = new ErzeugeTabelle<>(sTempLS, header.getHEADER_LS(), new TableLScr(this));
        sPaneLS.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickLS(sPaneLS.getTable(), e); }
        });
        sPaneLS.setColumnWidths(new int[] {120,120,120,500,500});
        sPaneLS.getTable().setAutoCreateRowSorter(true);
        
        infoLS = new SummenPanelA(new String[] {null}, false);
        if (use == 1) infoLS.setVisible(false);

        panelLSInfo = new ErzeugePanelA(sPaneLS, lieferscheinPanel, btn, infoLS);
        
        if (use == 1 || use == 2 || use == 3) {
        	btn[0].addActionListener(_ -> doLieferscheinPanel(0));
        }
        if (use == 0) {
        	btn[0].addActionListener(_ -> { if (use == 0) doLieferscheinPanel(1); else updScreen(); });
        	btn[1].addActionListener(e -> {
        		if (vZelleLS == null) return;
        	    Window w = SwingUtilities.getWindowAncestor((Component) e.getSource());
        	    BusyDialog.run(w,
        	        "Bitte warten",
        	        "Lieferschein wird erzeugt …",
        	        () -> {
						try {
							ExcelLieferschein.lsExport(vZelleLS);
						} catch (Exception ex) {
							logger.error("error exporting delivery note: ", ex);
						}
					},
        	        this::updScreen
        	    );
        	});
        }

        pageLS.removeAll();
        pageLS.add(panelLSInfo, BorderLayout.CENTER);
        pageLS.revalidate(); pageLS.repaint();
    }
    
    //###################################################################################################################################################

    private void doEinkaufPanel(int use) {
        
        JButton[] btn = null;
        switch(use) {
        	case 0 -> {
        		btn = new JButton[1];
        		purchasePanel = EditPanelFactory.create("PU");
        		btn[0] = createButton("<html>Lieferant<br>neu/bearb.</html>", ButtonIcon.EDIT.icon(), new Color(168,168,168));
        	}
        	case 1 -> {
        		btn = new JButton[1];
        		purchasePanel = EditPanelFactory.create("NL");
        		btn[0] = createButton("zurück", ButtonIcon.ACT.icon(), null);
        	}
        }
        btn[0].setEnabled(true);
        
        if ((use == 0) && role.name().equals("USER")) {	btn[0].setEnabled(false); }
        
        if (purchasePanel instanceof EinkaufPanel pup) {
            pup.setsTitel("");
            pup.setBtnText(0, "..."); pup.setBtnText(1, "save");
        }

        sPanePU = new ErzeugeTabelle<>(sTempPU, header.getHEADER_PU(), new TablePUcr());
        sPanePU.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickPU(sPanePU.getTable(), e); }
        });
        sPanePU.setColumnWidths(new int[] {100,150,400,50,100,100,100,100,150,80,400});
        sPanePU.getTable().setAutoCreateRowSorter(true);

        infoPU = new SummenPanelB(13, new String[] {"Netto:", "Brutto:", "USt.AT 10% Q1", "USt.AT 20% Q1", "USt.AT 10% Q2", "USt.AT 20% Q2",
        		"USt.AT 10% Q3", "USt.AT 20% Q3", "USt.AT 10% Q4", "USt.AT 20% Q4", "USt.EU (EURO)", "USt.EU sonst.", "USt. Welt", "", },
				new boolean[] {true, true, true, true, true, true, true, true, true, true, true, false, false,});
        setSumPU();
        
        if (use == 1) infoPU.setVisible(false);
        
        ErzeugePanelB cp = new ErzeugePanelB(sPanePU, purchasePanel, btn, infoPU);
        
        // Actions
        if (use == 1) {
        	btn[0].addActionListener(_ -> { if (use == 1) doEinkaufPanel(0); else updScreen(); });
        }
        if (use == 0) {
        	btn[0].addActionListener(_ -> { if (use == 0) doEinkaufPanel(1); else updScreen(); });
        }

        pagePU.removeAll();
        pagePU.add(cp, BorderLayout.CENTER);
        pagePU.revalidate(); pagePU.repaint();
    }
    
    //###################################################################################################################################################

    private void doAusgabenPanel() {

        expensesPanel = EditPanelFactory.create("EX");
        if (expensesPanel instanceof AusgabenPanel ep) {
            ep.setsTitel("");
            ep.setBtnText(0, "..."); ep.setBtnText(1, "save");
        }

        sPaneEX = new ErzeugeTabelle<>(sTempEX, header.getHEADER_EX(), new TableEXcr());
        sPaneEX.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickEX(sPaneEX.getTable(), e); }
        });
        sPaneEX.setColumnWidths(new int[] {100,650,50,100,150,150,150,500});
        sPaneEX.getTable().setAutoCreateRowSorter(true);

        infoEX = new SummenPanelB(13, new String[] {"Netto:", "Brutto:", "USt.AT 10% Q1", "USt.AT 20% Q1", "USt.AT 10% Q2", "USt.AT 20% Q2",
        		"USt.AT 10% Q3", "USt.AT 20% Q3", "USt.AT 10% Q4", "USt.AT 20% Q4", "USt.EU (EURO)", "USt.EU sonst.", "USt. Welt", "", },
				new boolean[] {true, true, true, true, true, true, true, true, true, true, true, false, false,});
        setSumEX();

        ErzeugePanelB cp = new ErzeugePanelB(sPaneEX, expensesPanel, null, infoEX);
        pageEX.removeAll();
        pageEX.add(cp, BorderLayout.CENTER);
        pageEX.revalidate(); pageEX.repaint();
    }
    
    //###################################################################################################################################################

    private void doSvsTaxPanel() {

        svTaxPanel = EditPanelFactory.create("SVT");
        if (svTaxPanel instanceof SvTaxPanel svt) {
            svt.setsTitel("");
            svt.setBtnText(0, "..."); svt.setBtnText(1, "save");
        }

        sPaneST = new ErzeugeTabelle<>(sTempST, header.getHEADER_ST(), new TableSTcr());
        sPaneST.getTable().addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { actionClickST(sPaneST.getTable(), e); }
        });
        sPaneST.setColumnWidths(new int[] {120,450,450,120,120,80,500});
        sPaneST.getTable().setAutoCreateRowSorter(true);

        infoST = new SummenPanelB(4, new String[] {"SV Gesamt:", "SV offen:", "Steuer Gesamt:", "Steuer offen:"},
									new boolean[] {true, true, true, true});
        setSumST();

        ErzeugePanelB cp = new ErzeugePanelB(sPaneST, svTaxPanel, null, infoST);
        pageST.removeAll();
        pageST.add(cp, BorderLayout.CENTER);
        pageST.revalidate(); pageST.repaint();
    }
    
    //###################################################################################################################################################

    private void doJahresergebnis() {

        panelUSt = new UStPanel(); panelUSt.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panelZM = new ZMeldungPanel(); panelZM.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
        panelP109a = new SteuerPanel(); panelP109a.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        UStDaten.setValuesUVA(panelUSt);
        ZMeldungDaten.RecState(panelZM);
        SteuerDaten.setValuesTax(panelP109a);

        pageErg = new JPanel(new GridBagLayout());
        GridBagConstraints erg = new GridBagConstraints();
        erg.gridx = 0; erg.weightx = 1.0; erg.fill = GridBagConstraints.HORIZONTAL;

        erg.gridy = 0; pageErg.add(panelUSt, erg);
        erg.gridy = 1; pageErg.add(panelZM, erg);
        erg.gridy = 2; pageErg.add(panelP109a, erg);

        erg.gridy = 3; erg.weighty = 1.0; erg.fill = GridBagConstraints.VERTICAL;
        pageErg.add(Box.createVerticalGlue(), erg);

        sPaneErg = new JScrollPane(pageErg);
        pageOv = new JPanel(new BorderLayout());
        pageOv.add(sPaneErg, BorderLayout.CENTER);
        pageOv.revalidate(); pageOv.repaint();
    }
    
    //###################################################################################################################################################

    private void doEinstellungen() {

        String[] select = { "", "Eigentümerdaten", "Bankdaten", "Stammdatenverwaltung", "Pfadverwaltung", "Benutzerverwaltung",
                "Steuerdaten", "SEPA QR-Code", "Datenbank",
                "Angebotstexte (Textbausteine)", "Angebotsrevisionstexte (Textbausteine)",
                "Auftragsbestätigungstexte (Textbausteine)", "Rechnungstexte (Textbausteine)",
                "Zahlungserinnerungstexte (Textbausteine)", "Mahnungstexte Mahnstufe 1 (Textbausteine)",
                "Mahnungstexte Mahnstufe 2 (Textbausteine)", "Bestellungstexte (Textbausteine)",
                "Lieferscheintexte (Textbausteine)", "AI Features"};

        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageAdmin = new JPanel(new BorderLayout());
        pageAdmin.setBorder(border);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JLabel lbl = new JLabel("Auswahl der Einstellungen:");
        lbl.setFont(new Font("Arial", Font.BOLD, 12));
        JComboBox<String> cmbSelect = new JComboBox<>(select);
        cmbSelect.setFont(new Font("Arial", Font.BOLD, 12));
        top.add(lbl); top.add(cmbSelect);

        pageSetting = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        pageAdmin.add(top, BorderLayout.NORTH);
        pageAdmin.add(new JScrollPane(pageSetting), BorderLayout.CENTER);

        cmbSelect.addActionListener(_ -> {
            pageSetting.removeAll();
            switch (cmbSelect.getSelectedIndex()) {
                case 1 ->  pageSetting.add(new OwnerPanel());
                case 2 ->  pageSetting.add(new BankPanel());
                case 3 ->  { pageSetting.add(new KundePanel()); pageSetting.add(new LieferantPanel()); pageSetting.add(new ArtikelPanel()); }
                case 4 ->  pageSetting.add(new PfadPanel());
                case 5 ->  pageSetting.add(new BenutzerPanel());
                case 6 ->  { pageSetting.add(new SteuertabellePanel()); pageSetting.add(new GwbTabellePanel()); }
                case 7 ->  pageSetting.add(new QrCodePanel());
                case 8 ->  pageSetting.add(new DatenbankPanel());
                case 9 ->  pageSetting.add(TextPanelFactory.create("T1"));
                case 10 -> pageSetting.add(TextPanelFactory.create("T2"));
                case 11 -> pageSetting.add(TextPanelFactory.create("T3"));
                case 12 -> pageSetting.add(TextPanelFactory.create("T4"));
                case 13 -> pageSetting.add(TextPanelFactory.create("T5"));
                case 14 -> pageSetting.add(TextPanelFactory.create("T6"));
                case 15 -> pageSetting.add(TextPanelFactory.create("T7"));
                case 16 -> pageSetting.add(TextPanelFactory.create("T8"));
                case 17 -> pageSetting.add(TextPanelFactory.create("T9"));
                case 18 -> pageSetting.add(new AIfeaturePanel());
                default -> {}
            }
            pageSetting.revalidate(); pageSetting.repaint();
        });
    }
    
    //###################################################################################################################################################

    private void doMigration() {
    	
    	MigrationPanel migPanel = new MigrationPanel();
    	
    	TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageMigration = new JPanel(new BorderLayout());
        pageMigration.setBorder(border);
        pageMigration.add(new JScrollPane(migPanel), BorderLayout.CENTER);
    	
    }
    
    //###################################################################################################################################################

    private void doVersuch() {
    	
    	VersuchPanel testPanel = new VersuchPanel();
    	
    	TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), null);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);

        pageVersuch = new JPanel(new BorderLayout());
        pageVersuch.setBorder(border);
        pageVersuch.add(new JScrollPane(testPanel), BorderLayout.CENTER);
    	
    }
    
    //###################################################################################################################################################
    // Statusbar

    private void buildStatusBar() {
    	String myMail = m != null ? myMail = m : "no E-Mail provided";
    	var dateNow = java.time.LocalDate.now(java.time.ZoneId.systemDefault());
        var dfDate = java.time.format.DateTimeFormatter.ofPattern("dd.MM.yyyy");
        var dtNow = dateNow.format(dfDate);
        String sStatus = String.format("<html>"
                		+ "<b>%s</b>"														// Datum
                		+ " | %s"															// Lizenz
                		+ " | Angemeldeter Benutzer: <font color='blue'><b>%s</b></font>"	// Benutzer
                		+ " | Rolle: <font color='blue'>%s</font>"							// Rolle
                		+ " | E-Mail: <font color='blue'><b>%s</b></font>"					// E-Mail
                        + " | Master-DB: <font color='blue'><b>%s</b></font>"				// DB-Name Master-Datenbank
                        + " | Daten-DB: <font color='blue'><b>%s</b></font>"				// DB-Name Produktiv-Datenbank
                        + " | DB-Server: <font color='red'><b>%s</b></font>"					// DB-Server
                        + "</html>",
					                dtNow,									// Datum
					                sLic,									// Lizenz
					                u, role,								// Benutzer und Rolle
					                myMail,									// E-Mail
					                Einstellungen.getDbSettings().dbMaster,	// DB-Name Master-Datenbank
					                Einstellungen.getDbSettings().dbData,	// DB-Name Produktiv-Datenbank
					                App.DB);								// DB-Server

        lblState = new JLabel(sStatus);
        lblState.setBorder(new RoundedBorder(10));
        lblState.setHorizontalAlignment(SwingConstants.LEFT);
        lblState.setOpaque(true);
        lblState.setFont(new Font("Tahoma", Font.PLAIN, 12));
        lblState.setBackground(switch (iLic) {
            case 1 -> new Color(255, 246, 143);
            case 2 -> new Color(152, 251, 152);
            case 3 -> Color.YELLOW;
            default -> Color.PINK;
        });

        JButton btn = new JButton(String.valueOf(Einstellungen.getAppSettings().year));
    	btn.setBackground(new Color(176, 224, 230));
    	btn.setHorizontalAlignment(SwingConstants.CENTER);
    	btn.setFont(new Font("Tahoma", Font.BOLD, 12));
    	btn.setForeground(Color.BLACK);
    	btn.addActionListener(_ ->
	        SwingUtilities.invokeLater(() -> {
	        	String eingabe = null;
	        	do {
            	  eingabe = JOptionPane.showInputDialog(null, "Bitte gewünschtes Geschaäftsjahr eingeben", "Geschäftsjahr ändern", JOptionPane.QUESTION_MESSAGE);
            	  if (eingabe == null) return;
            	} while (eingabe == null || eingabe.length() < 4 || eingabe.length() > 4);
	        	try {
	                int y = Integer.parseInt(eingabe);
	                JsonApp s = Einstellungen.getAppSettings(); // Live-Objekt
	                s.year = y;
	                Einstellungen.setAppSettings(s);
	                JsonUtil.saveAPP(StartUp.getFileApp(), s);
	                updScreen(); // Gesamt-Update ausführen
	            } catch (NumberFormatException ex) {
	                logger.warn("error parsing string to integer: " + ex.getMessage());
	            } catch (IOException ex) {
	                logger.error("error writing app settings: " + ex.getMessage());
	            }
	        })
	    );
        
        status = new JPanel(new BorderLayout(1, 0));
        status.add(lblState, BorderLayout.CENTER);
        status.add(btn, BorderLayout.EAST);
        contentPane.add(status, BorderLayout.SOUTH);
    }
    
    //###################################################################################################################################################
    // Summen
    
    private void loadData() {
    	sTempAN = LadeAngebot.loadAngebot(false);
        if (sTempAN == null) sTempAN = new String[0][header.getHEADER_AN().length];
        sTempRE = LadeRechnung.loadRechnung(false);
        if (sTempRE == null) sTempRE = new String[0][header.getHEADER_RE().length];
        sTempBE = LadeBestellung.loadBestellung(false);
        if (sTempBE == null) sTempBE = new String[0][header.getHEADER_BE().length];
        sTempLS = LadeLieferschein.loadLieferschein(false);
        if (sTempLS == null) sTempLS = new String[0][header.getHEADER_LS().length];
    	sTempPU = LadeEinkauf.loadEinkaufsRechnung(false);
        if (sTempPU == null) sTempPU = new String[0][header.getHEADER_PU().length];
        sTempEX = LadeAusgaben.loadAusgaben(false);
        if (sTempEX == null) sTempEX = new String[0][header.getHEADER_EX().length];
        sTempST = LadeSvTax.loadSvTax(false);
        if (sTempST == null) sTempST = new String[0][header.getHEADER_ST().length];
    }

    private void setSumAN() {
        double dOpen = LadeAngebot.getSumOpen().doubleValue();
        double dOrdered = LadeAngebot.getSumOrdered().doubleValue();
        infoAN.setTxtSum(0, dOpen);
        infoAN.setTxtSum(1, dOrdered);
        infoAN.setProgressBar(prozent(LadeAngebot.getSumOpen(), LadeAngebot.getSumOrdered()));
    }

    private void setSumRE() {
        double dOpen = LadeRechnung.getSumOpen().doubleValue();
        double dPayed = LadeRechnung.getSumPayed().doubleValue();
        infoRE.setTxtSum(0, dOpen);
        infoRE.setTxtSum(1, dPayed);
        infoRE.setProgressBar(prozent(LadeRechnung.getSumOpen(), LadeRechnung.getSumPayed()));
    }
    
    private void setSumBE() {
    	double dOpen = LadeBestellung.getSumOpen().doubleValue();
        double dDelivered = LadeBestellung.getSumDelivered().doubleValue();
        infoBE.setTxtSum(0, dOpen);
        infoBE.setTxtSum(1, dDelivered);
    }
    
    private void setSumPU() {
    	BigDecimal[] tmp10Q = new BigDecimal[4]; BigDecimal[] tmp20Q = new BigDecimal[4];
    	double[] d10ProzQ = new double[4]; double[] d20ProzQ = new double[4];
    	double dNetto = LadeEinkauf.getBdNetto().doubleValue(); double dBrutto = LadeEinkauf.getBdBrutto().doubleValue();
    	tmp10Q = LadeEinkauf.getBd10ProzQ(); tmp20Q = LadeEinkauf.getBd20ProzQ();
        d10ProzQ[0] = tmp10Q[0].doubleValue(); d10ProzQ[1] = tmp10Q[1].doubleValue();
        d10ProzQ[2] = tmp10Q[2].doubleValue(); d10ProzQ[3] = tmp10Q[3].doubleValue();
        d20ProzQ[0] = tmp20Q[0].doubleValue(); d20ProzQ[1] = tmp20Q[1].doubleValue();
        d20ProzQ[2] = tmp20Q[2].doubleValue(); d20ProzQ[3] = tmp20Q[3].doubleValue();
        double dUstSonst = LadeEinkauf.getUstEU().doubleValue(); double dSonstEUnoEURO = LadeEinkauf.getBdUstEUnoEURO().doubleValue();
        double dUstWelt = LadeEinkauf.getUstNonEU().doubleValue();
        infoPU.setTxtSum(0, dNetto); infoPU.setTxtSum(1, dBrutto);
        infoPU.setTxtSum(2, d10ProzQ[0]); infoPU.setTxtSum(3, d20ProzQ[0]);
        infoPU.setTxtSum(4, d10ProzQ[1]); infoPU.setTxtSum(5, d20ProzQ[1]);
        infoPU.setTxtSum(6, d10ProzQ[2]); infoPU.setTxtSum(7, d20ProzQ[2]);
        infoPU.setTxtSum(8, d10ProzQ[3]); infoPU.setTxtSum(9, d20ProzQ[3]);
        infoPU.setTxtSum(10, dUstSonst); infoPU.setTxtSum(11, dSonstEUnoEURO);
        infoPU.setTxtSum(12, dUstWelt);
    }

    private void setSumEX() {
    	BigDecimal[] tmp10Q = new BigDecimal[4]; BigDecimal[] tmp20Q = new BigDecimal[4];
    	double[] d10ProzQ = new double[4]; double[] d20ProzQ = new double[4];
    	double dNetto = LadeAusgaben.getBdNetto().doubleValue(); double dBrutto = LadeAusgaben.getBdBrutto().doubleValue();
    	tmp10Q = LadeAusgaben.getBd10ProzQ(); tmp20Q = LadeAusgaben.getBd20ProzQ();
        d10ProzQ[0] = tmp10Q[0].doubleValue(); d10ProzQ[1] = tmp10Q[1].doubleValue();
        d10ProzQ[2] = tmp10Q[2].doubleValue(); d10ProzQ[3] = tmp10Q[3].doubleValue();
        d20ProzQ[0] = tmp20Q[0].doubleValue(); d20ProzQ[1] = tmp20Q[1].doubleValue();
        d20ProzQ[2] = tmp20Q[2].doubleValue(); d20ProzQ[3] = tmp20Q[3].doubleValue();
        double dUstSonst = LadeAusgaben.getUstEU().doubleValue(); double dSonstEUnoEURO = LadeAusgaben.getBdUstEUnoEURO().doubleValue();
        double dUstWelt = LadeAusgaben.getUstNonEU().doubleValue();
        infoEX.setTxtSum(0, dNetto); infoEX.setTxtSum(1, dBrutto);
        infoEX.setTxtSum(2, d10ProzQ[0]); infoEX.setTxtSum(3, d20ProzQ[0]);
        infoEX.setTxtSum(4, d10ProzQ[1]); infoEX.setTxtSum(5, d20ProzQ[1]);
        infoEX.setTxtSum(6, d10ProzQ[2]); infoEX.setTxtSum(7, d20ProzQ[2]);
        infoEX.setTxtSum(8, d10ProzQ[3]); infoEX.setTxtSum(9, d20ProzQ[3]);
        infoEX.setTxtSum(10, dUstSonst); infoEX.setTxtSum(11, dSonstEUnoEURO);
        infoEX.setTxtSum(12, dUstWelt);
    }

    private void setSumST() {
        double dSv = LadeSvTax.getBdSV().doubleValue(); double dSvOffen = LadeSvTax.getBdSVoffen().doubleValue();
        double dSt = LadeSvTax.getBdST().doubleValue(); double dStOffen = LadeSvTax.getBdSToffen().doubleValue();
        infoST.setTxtSum(0, dSv); infoST.setTxtSum(1, dSvOffen);
        infoST.setTxtSum(2, dSt); infoST.setTxtSum(3, dStOffen);
    }

    //###################################################################################################################################################
    // Click-Aktionen

    private void actionClickAN(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        if (isLeftSingle(e)) {
            e.consume();
            if (table.getValueAt(row, column) == null) {
                if (offerPanel instanceof AngebotPanel anp) {
                    anp.setsTitel("Angebotspositionen");
                    anp.setTxtFields(null, null);
                }
                return;
            }
            if (offerPanel instanceof AngebotPanel anp) {
                anp.setsTitel("Angebotspositionen (Angebots-Nr. = " + table.getValueAt(row, 0) + ")");
                Kunde kunde = searchKundeAll(table.getValueAt(row, 4).toString());
                anp.setTxtFields(table.getValueAt(row, 0).toString(), kunde != null ? kunde.getTaxvalue() : null);
                vZelleAN = table.getValueAt(row, 0).toString();
                vStateAN = table.getValueAt(row, 1).toString();
                if (panelOfferInfo instanceof ErzeugePanelA cp) {
                    JButton[] btn = cp.getButtons();
                    setOfferButtonsByState(vStateAN, btn);
                }
            }
        } else if (isLeftDouble(e)) {
            e.consume();
            if (table.getValueAt(row, column) != null) {
                String nr = table.getValueAt(row, 0).toString();
                Kunde kunde = searchKundeAll(table.getValueAt(row, 4).toString());
                actionFile(nr, kunde);
            }
        }
    }

    private void actionClickRE(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        if (isLeftSingle(e)) {
            e.consume();
            if (table.getValueAt(row, column) == null) {
                if (billPanel instanceof RechnungPanel rep) {
                    rep.setsTitel("Rechnungspositionen");
                    rep.setTxtFields(null, null);
                }
                return;
            }
            if (billPanel instanceof RechnungPanel rep) {
                rep.setsTitel("Rechnungspositionen (Rechnung-Nr. = " + table.getValueAt(row, 0) + ")");
                Kunde kunde = searchKundeAll(table.getValueAt(row, 5).toString());
                rep.setTxtFields(table.getValueAt(row, 0).toString(), kunde != null ? kunde.getTaxvalue() : null);
                vZelleRE = table.getValueAt(row, 0).toString();
                vStateRE = table.getValueAt(row, 1).toString();
                if (panelBillInfo instanceof ErzeugePanelA cp) {
                    JButton[] btn = cp.getButtons();
                    setBillButtonsByState(vStateRE, btn);
                }
            }
        } else if (isLeftDouble(e)) {
            e.consume();
            if (table.getValueAt(row, column) != null) {
                String nr = table.getValueAt(row, 0).toString();
                Kunde kunde = searchKundeAll(table.getValueAt(row, 5).toString());
                actionFile(nr, kunde);
            }
        }
    }
    
    private void actionClickBE(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        if (isLeftSingle(e)) {
            e.consume();
            if (table.getValueAt(row, column) == null) {
                if (bestellungPanel instanceof BestellungPanel bep) {
                    bep.setsTitel("Bestellungen");
                    bep.setTxtFields(null, null);
                }
                return;
            }
            if (bestellungPanel instanceof BestellungPanel bep) {
                bep.setsTitel("Bestellungen (Bestellung-Nr. = " + table.getValueAt(row, 0) + ")");
                Lieferant lieferant = searchLieferantAll(table.getValueAt(row, 4).toString());
                bep.setTxtFields(table.getValueAt(row, 0).toString(), lieferant != null ? lieferant.getTaxvalue() : null);
                vZelleBE = table.getValueAt(row, 0).toString();
                vStateBE = table.getValueAt(row, 1).toString();
                if (panelBEInfo instanceof ErzeugePanelA cp) {
                    JButton[] btn = cp.getButtons();
                    setOrderButtonsByState(vStateBE, btn);
                }
            }
        } else if (isLeftDouble(e)) {
            e.consume();
            if (table.getValueAt(row, column) != null) {
                String nr = table.getValueAt(row, 0).toString();
                Lieferant lieferant = searchLieferantAll(table.getValueAt(row, 4).toString());
                actionFileBE(nr, lieferant);
            }
        }
    }
    
    private void actionClickLS(JTable table, MouseEvent e) {
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        if (isLeftSingle(e)) {
            e.consume();
            if (table.getValueAt(row, column) == null) {
                if (lieferscheinPanel instanceof LieferscheinPanel lsp) {
                    lsp.setsTitel("Lieferscheine");
                    lsp.setTxtFields(null);
                }
                return;
            }
            if (lieferscheinPanel instanceof LieferscheinPanel lsp) {
            	lsp.setsTitel("Lieferschein (Lieferschein-Nr. = " + table.getValueAt(row, 0) + ")");
            	lsp.setTxtFields(table.getValueAt(row, 0).toString());
                vZelleLS = table.getValueAt(row, 0).toString();
                vStateLS = table.getValueAt(row, 1).toString();
                if (panelLSInfo instanceof ErzeugePanelA cp) {
                    JButton[] btn = cp.getButtons();
                    setDeliveryNoteButtonsByState(vStateLS, btn);
                }
            }
        } else if (isLeftDouble(e)) {
            e.consume();
            if (table.getValueAt(row, column) != null) {
                String nr = table.getValueAt(row, 0).toString();
                Kunde kunde = searchKundeAll(table.getValueAt(row, 4).toString());
                actionFile(nr, kunde);
            }
        }
    }

    private void actionClickPU(JTable table, MouseEvent e) {
        if (!isLeftSingle(e)) return;
        e.consume();
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        if (table.getValueAt(row, column) == null) {
            if (purchasePanel instanceof EinkaufPanel pup) {
                pup.setsTitel("neuen Einkaufsbeleg erfassen");
                pup.setBtnText(1, "save");
                pup.setTxtFields(null);
                pup.setIcon();
                pup.setFile(false);
            }
        } else {
            if (purchasePanel instanceof EinkaufPanel pup) {
                pup.setsTitel("vorhandenen Einkaufsbeleg (Rechnungs-Nr. = " + table.getValueAt(row, 1) + ") bearbeiten");
                pup.setBtnText(1, "<html>Status<br>setzen</html>");
                pup.setTxtFields(table.getValueAt(row, 1).toString());
                pup.setIcon();
                pup.setFile(false);
            }
        }
    }

    private void actionClickEX(JTable table, MouseEvent e) {
        if (!isLeftSingle(e)) return;
        e.consume();
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        int[] belegID = LadeAusgaben.getBelegID();
        if (table.getValueAt(row, column) == null) {
            if (expensesPanel instanceof AusgabenPanel ep) {
                ep.setsTitel("neuen Beleg erfassen");
                ep.setTxtFields(0);
                ep.setIcon();
                ep.setFile(false);
            }
        } else {
            if (expensesPanel instanceof AusgabenPanel ep) {
                ep.setsTitel("vorhandenen Beleg bearbeiten");
                ep.setTxtFields(belegID[row]);
                ep.setIcon();
                ep.setFile(false);
            }
        }
    }

    private void actionClickST(JTable table, MouseEvent e) {
        if (!isLeftSingle(e)) return;
        e.consume();
        int row = table.rowAtPoint(e.getPoint());
        int column = table.columnAtPoint(e.getPoint());
        if (row == -1 || column == -1) return;

        int[] belegID = LadeSvTax.getBelegID();
        if (table.getValueAt(row, column) == null) {
            if (svTaxPanel instanceof SvTaxPanel svp) {
                svp.setsTitel("neuen Beleg erfassen");
                svp.setBtnText(1, "save");
                svp.setTxtFields(0);
                svp.setIcon();
                svp.setFile(false);
            }
        } else {
            if (svTaxPanel instanceof SvTaxPanel svp) {
                svp.setsTitel("vorhandenen Beleg bearbeiten");
                svp.setBtnText(1, null);
                svp.setTxtFields(belegID[row]);
                svp.setIcon();
                svp.setFile(false);
            }
        }
    }
    
    //###################################################################################################################################################
    // Hilfsmethoden
    //###################################################################################################################################################
    
    private void updScreen() {
    	int oldIdx = tabPanel.getSelectedIndex(); // aktuell angewählten Tab sichern

    	sTempAN = sTempRE = sTempBE = sTempLS = sTempPU = sTempEX = sTempST = null;
        pageTR = null; pageOv = null; pageErg = null; pageWT = null;
        
        status.removeAll();
        this.remove(status);
        loadData(); // Daten neu laden
        
        if (TabMask.visible(tc, TabMask.Tab.TIME)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Arbeitszeit"));
			try {
				doWorkTimePanel(u);
			} catch (IOException e) {
				logger.error("error creating panel for work time: " + e.getMessage());
			}
			tabPanel.addTab("Arbeitszeit", TabIcon.TIME.icon(), pageWT);
		}
        if (TabMask.visible(tc, TabMask.Tab.TRAVEL)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Reisespesen"));
			try {
				doSpesenPanel();
			} catch (IOException e) {
				logger.error("error creating panel for travel expenses: " + e.getMessage());
			}
			tabPanel.addTab("Reisespesen", TabIcon.TRAVEL.icon(), pageTR);
		}
        if (TabMask.visible(tc, TabMask.Tab.OFFER)) {
        	pageAN.removeAll();
        	doAngebotPanel(0);
        	tabPanel.addTab("Angebote", TabIcon.OFFER.icon(), pageAN);
        }
		if (TabMask.visible(tc, TabMask.Tab.INVOICE)) {
			pageRE.removeAll();
			doRechnungPanel(0);
			tabPanel.addTab("Rechnungen", TabIcon.INVOICE.icon(), pageRE);
		}
		if (TabMask.visible(tc, TabMask.Tab.ORDER)) {
			pageBE.removeAll();
        	doBestellungPanel(0);
        	tabPanel.addTab("Bestellungen", TabIcon.ORDER.icon(), pageBE);
        }
		if (TabMask.visible(tc, TabMask.Tab.DELIVERY)) {
			pageLS.removeAll();
			doLieferscheinPanel(0);
			tabPanel.addTab("Lieferscheine", TabIcon.DELIVERY_NOTE.icon(), pageLS);
		}
		if (TabMask.visible(tc, TabMask.Tab.PURCHASE)) {
			pagePU.removeAll();
        	doEinkaufPanel(0);
        	tabPanel.addTab("Einkauf", TabIcon.PURCHASE.icon(), pagePU);
        }
		if (TabMask.visible(tc, TabMask.Tab.EXPENSES)) {
			pageEX.removeAll();
			doAusgabenPanel();
			tabPanel.addTab("Betriebsausgaben", TabIcon.EXPENSES.icon(), pageEX);
		}
		if (TabMask.visible(tc, TabMask.Tab.TAX)) {
			pageST.removeAll();
			doSvsTaxPanel();
        	tabPanel.addTab("SV und Steuer", TabIcon.TAX.icon(), pageST);
        }
		if (TabMask.visible(tc, TabMask.Tab.RESULT)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Jahresergebnis"));
			doJahresergebnis();
			tabPanel.addTab("Jahresergebnis", TabIcon.RESULT.icon(), pageOv);
		}
		if (TabMask.visible(tc, TabMask.Tab.SETTINGS)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Einstellungen"));
			doEinstellungen();
			tabPanel.addTab("Einstellungen", TabIcon.SETTINGS.icon(), pageAdmin);
		}
		if (TabMask.visible(tc, TabMask.Tab.MIGRATION)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Datenmigration"));
			doMigration();
			tabPanel.addTab("Datenmigration", TabIcon.MIGRATION.icon(), pageMigration);
		}
		if (TabMask.visible(tc,  TabMask.Tab.TRIALS)) {
			tabPanel.removeTabAt(tabPanel.indexOfTab("Versuche"));
			doVersuch();
			tabPanel.addTab("Versuche",  TabIcon.MIGRATION.icon(), pageVersuch);
		}
		
		buildStatusBar();
        contentPane.revalidate();
        contentPane.repaint();
        
        int idx = Math.min(Math.max(oldIdx, 0), tabPanel.getTabCount() - 1);
        tabPanel.setSelectedIndex(idx); // vorher aktiven Tab wieder aktivieren
    }
    
    private Role roleFromLogin(String r) {
        return switch (r) {
            case "user" -> Role.USER;
            case "superuser" -> Role.SUPERUSER;
            case "financialuser" -> Role.FINANCIALUSER;
            case "admin" -> Role.ADMIN;
            default -> Role.NONE;
        };
    }
    
    private void actionFile(String value, Kunde kunde) {
        if (value == null || kunde == null) return;
        DateianzeigeDialog.loadGUI(value, kunde);
    }
    
    private void actionFileBE(String value, Lieferant lieferant) {
        if (value == null || lieferant == null) return;
        DateianzeigeDialog.loadGUIBE(value, lieferant);
    }

    private boolean isLeftSingle(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1 && !e.isConsumed();
    }

    private boolean isLeftDouble(MouseEvent e) {
        return e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && !e.isConsumed();
    }

    private void setOfferButtonsByState(String state, JButton[] btn) {
        boolean print = "erstellt".equals(state);
        boolean confirm = "bestellt".equals(state);
        boolean confirmed = "bestätigt".equals(state);
        btn[3].setEnabled(print);
        btn[4].setEnabled(confirm);
        btn[5].setEnabled(confirmed);
    }

    private void setBillButtonsByState(String state, JButton[] btn) {
        boolean print = "erstellt".equals(state);
        boolean dunning = switch (state) {
            case "gedruckt", "Zahlungserinnerung", "Mahnstufe 1" -> true;
            default -> false;
        };
        btn[3].setEnabled(print);
        btn[4].setEnabled(dunning);
    }
    
    private void setOrderButtonsByState(String state, JButton[] btn) {
        boolean print = "erstellt".equals(state);
        btn[3].setEnabled(print);
    }
    
    private void setDeliveryNoteButtonsByState(String state, JButton[] btn) {
        boolean print = "erstellt".equals(state);
        btn[1].setEnabled(print);
    }

    private Kunde searchKundeAll(String name) {
        if (name == null || name.isBlank()) return null;
        return new KundeRepository().findAll().stream()
                .filter(k -> name.equals(k.getName()))
                .findFirst().orElse(null);
    }
    
    private Lieferant searchLieferantAll(String name) {
        if (name == null || name.isBlank()) return null;
        return new LieferantRepository().findAll().stream()
                .filter(k -> name.equals(k.getName()))
                .findFirst().orElse(null);
    }
    
    private int prozent(BigDecimal open, BigDecimal closed) {
        if (open == null) open = BD.ZERO;
        if (closed == null) closed = BD.ZERO;
        BigDecimal sum = open.add(closed);
        if (sum.signum() <= 0) return 0;
        return open.multiply(BigDecimal.valueOf(100))
                .divide(sum, 0, RoundingMode.DOWN)
                .intValue();
    }

    //###################################################################################################################################################
    // Renderer-Klassen ohne statischen Zugriff
    //###################################################################################################################################################

    static class TableANcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final HauptFenster ctx;
        TableANcr(HauptFenster ctx) { this.ctx = ctx; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column == 0 || column == 1 || column == 2) ? SwingConstants.CENTER
                    : (column == 5 ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);
            String[][] s = ctx.sTempAN;
            if (s != null && row < s.length && s[row][1] != null) {
                switch (s[row][1]) {
                    case "storniert" -> setBackground(Color.PINK);
                    case "gedruckt" -> setBackground(new Color(175,238,238));
                    case "revisioniert" -> setBackground(new Color(218,165,32));
                    case "bestellt" -> setBackground(new Color(37,204,196));
                    case "bestätigt" -> setBackground(new Color(152,251,152));
                }
            }
            return label;
        }
    }

    static class TableREcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final HauptFenster ctx;
        TableREcr(HauptFenster ctx) { this.ctx = ctx; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column <= 3) ? SwingConstants.CENTER
                    : ((column >= 6 && column <= 8) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);
            String[][] s = ctx.sTempRE;
            if (s != null && row < s.length && s[row][1] != null) {
                switch (s[row][1]) {
                    case "storniert" -> setBackground(Color.PINK);
                    case "gedruckt" -> setBackground(new Color(175,238,238));
                    case "Zahlungserinnerung" -> setBackground(Color.YELLOW);
                    case "Mahnstufe 1" -> setBackground(Color.MAGENTA);
                    case "Mahnstufe 2" -> setBackground(Color.RED);
                    case "bezahlt" -> setBackground(new Color(152,251,152));
                    case "bez. Skonto 1", "bez. Skonto 2" -> setBackground(new Color(155,205,155));
                }
            }
            return label;
        }
    }
    
    static class TableBEcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final HauptFenster ctx;
        TableBEcr(HauptFenster ctx) { this.ctx = ctx; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column <= 2) ? SwingConstants.CENTER
                    : ((column >= 5 && column <= 7) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);
            String[][] s = ctx.sTempBE;
            if (s != null && row < s.length && s[row][1] != null) {
                switch (s[row][1]) {
                    case "storniert" -> setBackground(Color.PINK);
                    case "gedruckt" -> setBackground(new Color(175,238,238));
                    case "geliefert" -> setBackground(Color.YELLOW);
                }
            }
            return label;
        }
    }
    
    static class TableLScr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private final HauptFenster ctx;
        TableLScr(HauptFenster ctx) { this.ctx = ctx; }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column <= 2) ? SwingConstants.CENTER
                    : ((column >= 5 && column <= 7) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);
            String[][] s = ctx.sTempLS;
            if (s != null && row < s.length && s[row][1] != null) {
                switch (s[row][1]) {
                    case "storniert" -> setBackground(Color.PINK);
                    case "gedruckt" -> setBackground(new Color(175,238,238));
                    case "geliefert" -> setBackground(Color.YELLOW);
                }
            }
            return label;
        }
    }

    static class TablePUcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final Logger logger = LogManager.getLogger(TablePUcr.class);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column == 0 || column == 1 || column == 3 || column == 4 || column == 8 || column == 9) ? SwingConstants.CENTER
                    : ((column == 5 || column == 6 || column == 7 || column == 10) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);

            Object v0 = table.getValueAt(row, 0);
            if (v0 != null) {
                try {
                    DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate dateNow = LocalDate.now();
                    LocalDate datePay = LocalDate.parse(table.getValueAt(row, 8).toString(), inputFormat);
                    long daysBetween = ChronoUnit.DAYS.between(dateNow, datePay);
                    int daysPayable = Math.toIntExact(daysBetween);

                    String s = String.valueOf(table.getValueAt(row, 9));
                    switch(s) {
                    case "nein" -> { if (daysPayable < 0) setBackground(Color.RED); else if (daysPayable < 3) setBackground(Color.PINK);}
                    case "angezahlt" -> setBackground(Color.CYAN);
                    case "ja" -> setBackground(new Color(152,251,152));
                    case "ja, Skonto 1", "ja, Skonto 2" -> setBackground(new Color(155,205,155));
                    }
                } catch (Exception e) {
                    logger.error("PU render date error", e);
                }
            } else {
                setBackground(new Color(238,210,238));
            }
            return label;
        }
    }

    static class TableEXcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final Logger logger = LogManager.getLogger(TableEXcr.class);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column == 0) || (column == 2) || (column == 3) ? SwingConstants.CENTER
                    : ((column >= 4 && column <= 7) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(176,226,255,100) : Color.WHITE);
            Object v0 = table.getValueAt(row, 0);
            if (v0 != null) {
                try {
                	if (table.getValueAt(row, 1).toString().contains("Diäten")) {
						setFont(new Font("Tahoma", Font.BOLD, 11));
					} else {
						setFont(new Font("Tahoma", Font.PLAIN, 11));
					}
		            if (table.getValueAt(row, 4).toString().contains("-")) {
		            	setForeground(Color.RED);
		            } else {
		            	setForeground(Color.BLACK);
		            }
                } catch (Exception e) {
                    logger.error("EX render date error", e);
                }
            } else {
                setBackground(new Color(238,210,238));
            }
            return label;
        }
    }

    static class TableSTcr extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;
        private static final Logger logger = LogManager.getLogger(TableSTcr.class);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment((column == 0 || column == 4 || column == 5) ? SwingConstants.CENTER
                    : ((column == 3 || column == 6) ? SwingConstants.RIGHT : SwingConstants.LEFT));
            setBackground(row % 2 < 1 ? new Color(10,10,10,10) : Color.WHITE);

            Object v0 = table.getValueAt(row, 0);
            if (v0 != null) {
            	int daysPayable = 0;
                try {
                    DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                    LocalDate dateNow = LocalDate.now();
                    if (table.getValueAt(row, 4).toString() != "") {
                    	LocalDate datePay = LocalDate.parse(table.getValueAt(row, 4).toString(), inputFormat);
                        daysPayable = Math.toIntExact(ChronoUnit.DAYS.between(dateNow, datePay));
                    }
                    
                    setForeground(Color.BLACK);
                    String s = String.valueOf(table.getValueAt(row, 5));
                    switch(s) {
                    	case "Forderung" -> { if (daysPayable < 0) setBackground(Color.PINK);
                    					else if (daysPayable < 3) setBackground(Color.ORANGE); }
                    	case "Zahlung" -> setBackground(new Color(152,251,152));
                    	case "Dateiablage" -> { setBackground(new Color(202,225,255)); setForeground(Color.BLUE); }
                    }
                } catch (Exception e) {
                    logger.error("ST render date error", e);
                }
            } else {
                setBackground(new Color(238,210,238));
            }
            return label;
        }
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

	public static String getU() {
		return u;
	}

}
