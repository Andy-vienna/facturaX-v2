package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import org.andy.fx.code.dataStructure.entityMaster.Artikel;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Lieferschein;
import org.andy.fx.code.dataStructure.repositoryMaster.ArtikelRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.LieferscheinRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class LieferscheinNeuPanel extends EditPanel {
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(LieferscheinNeuPanel.class);

    private static final int POS_COUNT = 12;

    // Datenquellen
    private final KundeRepository kundeRepository = new KundeRepository();
    private final ArtikelRepository artikelRepository = new ArtikelRepository();
    private final LieferscheinRepository lieferscheinRepository = new LieferscheinRepository();

    // Daten
    private final List<Kunde> kunde = new ArrayList<>();
    private final List<Artikel> artikel = new ArrayList<>();

    // UI Felder
    private JComboBox<String> cmbKunde;
    private JTextField[] txtKd = new JTextField[12];

    private JTextField txtNummer, txtReferenz;
    private DatePicker datePicker;

    private final JLabel[] lblPos = new JLabel[POS_COUNT];
    @SuppressWarnings("unchecked")
	private final JComboBox<String>[] cbPos = new JComboBox[POS_COUNT];
    private final JTextField[] txtAnz = new JTextField[POS_COUNT];

    private final BigDecimal[] bdAnzahl = new BigDecimal[POS_COUNT];
    private final String[] sPosText = new String[POS_COUNT];

    // Zustände
    private boolean kundeGewählt = false;
    private boolean mind1ArtikelGewählt = false;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public LieferscheinNeuPanel() {
        super("neuen Lieferschein erstellen");
        if (!(getBorder() instanceof TitledBorder)) {
            logger.warn("Kein TitledBorder gesetzt.");
        }
        setLayout(null); // beibehalten, um dein Layout nicht aufzubrechen
        loadData();
        buildUI();
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

    private void buildUI() {
    	
        // Labels links
    	final String[] leftLabels = {"Kundennummer","Kundenname","Strasse","PLZ","Ort","Land","Anrede","Ansprechpartner","UID","USt.-Satz",
				 "%","Rabattschlüssel","%","Zahlungsziel","Tage" };

    	final int[][] leftBounds = {{10,55},{10,80},{10,105},{10,130},{10,155},{10,180},{10,205},{10,230},{10,255},{10,280},  {10,305},  {10,330},
																							  {155,280}, {155,305}, {155,330}};
    	
        List<JLabel> left = new ArrayList<>();
        for (int i=0;i<leftLabels.length;i++){
            JLabel l=new JLabel(leftLabels[i]);
            l.setBounds(leftBounds[i][0], leftBounds[i][1], (i==10||i==12||i==14)?30:110, 25);
            l.setForeground(Color.GRAY);
            if (i==10||i==12||i==14) l.setHorizontalAlignment(SwingConstants.CENTER);
            add(l);
            left.add(l);
        }

        JLabel lbl20=new JLabel("Nr.");  lbl20.setBounds(320,30,25,25);
        JLabel lbl21=new JLabel("Position"); lbl21.setBounds(345,30,440,25);
        JLabel lbl22=new JLabel("Anz."); lbl22.setBounds(785,30,70,25);
        for (JLabel x : new JLabel[]{lbl20,lbl21,lbl22}) {
            x.setHorizontalAlignment(SwingConstants.CENTER);
            add(x);
        }

        JLabel lbl25=new JLabel("Lieferscheinnummer:"); lbl25.setBounds(1010,55,125,25); add(lbl25);
        JLabel lbl26=new JLabel("Datum:");  lbl26.setBounds(1010,80,125,25); add(lbl26);
        JLabel lbl29=new JLabel("Referenz");        lbl29.setBounds(1010,105,60,25);  add(lbl29);

        // Combos/Textfelder links
        cmbKunde = new JComboBox<>(kunde.stream().map(k -> nullToEmpty(k.getName())).toArray(String[]::new));
        cmbKunde.setBounds(10,30,300,25); add(cmbKunde);

        for (int ii=0;ii<txtKd.length;ii++) {
        	txtKd[ii]=setRO(110, 55+(ii*25));
        	add(txtKd[ii]);
        }

        txtNummer = new JTextField(nextLsNummer());
        txtNummer.setBounds(1130,55,140,25);
        txtNummer.setForeground(Color.BLUE);
        txtNummer.setFont(new Font("Tahoma", Font.BOLD, 14));
        txtNummer.setHorizontalAlignment(SwingConstants.CENTER);
        txtNummer.setEditable(false);
        add(txtNummer);

        datePicker = makeDatePicker(1132,80); add(datePicker);

        txtReferenz = new JTextField();
        txtReferenz.setBounds(1130,105,390,25);
        txtReferenz.setForeground(Color.BLUE);
        txtReferenz.setBackground(Color.PINK);
        txtReferenz.setFont(new Font("Tahoma", Font.BOLD, 11));
        add(txtReferenz);
        txtReferenz.getDocument().addDocumentListener(bgFlipOnNonEmpty(txtReferenz));
        
        JButton btnDoExport = createButton("<html>Lieferschein<br>erstellen</html>", ButtonIcon.EDIT.icon(), null);
        btnDoExport.setBounds(1545,305, HauptFenster.getButtonx(), HauptFenster.getButtony());
        btnDoExport.setEnabled(true);
        add(btnDoExport);
        
        // Positionszeilen
        final String[] artikelTexte = artikel.stream().map(a -> nullToEmpty(a.getText())).toArray(String[]::new);
        for (int ii=0;ii<POS_COUNT;ii++) {
        	final int i = ii;
            final int y = 55 + i*25;
            lblPos[i]=new JLabel(String.valueOf(i+1));
            lblPos[i].setHorizontalAlignment(SwingConstants.CENTER);
            lblPos[i].setBounds(320,y,20,25); add(lblPos[i]);

            cbPos[i]=new JComboBox<>(artikelTexte);
            cbPos[i].setBounds(345,y,440,25); add(cbPos[i]);

            txtAnz[i]=centeredField(785,y,70); txtAnz[i].setEnabled(false); add(txtAnz[i]); attachCommaToDot(txtAnz[i]);
            
            // Listener je Zeile
            cbPos[i].addActionListener(_ -> onArtikelChosen(i));
            txtAnz[i].getDocument().addDocumentListener(docChanged(() -> onQtyOrEPChanged(i)));
        }
        
        // Trenner
        JSeparator s1=new JSeparator(JSeparator.VERTICAL); s1.setBounds(315,10,2,370); add(s1);
        JSeparator s2=new JSeparator(JSeparator.VERTICAL); s2.setBounds(1000,10,2,370); add(s2);

        // Aktionen
        cmbKunde.addActionListener(_ -> onKundeChanged());

        btnDoExport.addActionListener(_ -> doSave());

        setPreferredSize(new Dimension(1000, 390));
    }
    
	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private void onKundeChanged() {
        int idx = cmbKunde.getSelectedIndex();
        if (idx <= 0) {
            clearKunde();
            kundeGewählt = false;
            return;
        }
        Kunde k = kunde.get(idx);
        txtKd[0].setText(k.getId());
        txtKd[1].setText(k.getName());
        txtKd[2].setText(k.getStrasse());
        txtKd[3].setText(k.getPlz());
        txtKd[4].setText(k.getOrt());
        txtKd[5].setText(k.getLand());
        txtKd[6].setText(k.getPronomen());
        txtKd[7].setText(k.getPerson());
        txtKd[8].setText(k.getUstid());
        txtKd[9].setText(k.getTaxvalue());
        txtKd[10].setText(k.getDeposit());
        txtKd[11].setText(k.getZahlungsziel());
        kundeGewählt = true;
    }

    private void onArtikelChosen(int i) {
        int idx = cbPos[i].getSelectedIndex();
        if (idx <= 0) {
            txtAnz[i].setText("");
            txtAnz[i].setEnabled(false);
            txtAnz[i].setBackground(Color.WHITE);
            bdAnzahl[i]=null; sPosText[i]=null;
            recomputeMind1Artikel();
            return;
        }
        Artikel a = artikel.get(idx);
        sPosText[i] = a.getText();
        txtAnz[i].setEnabled(true);
        txtAnz[i].setBackground(Color.PINK);
        recomputeMind1Artikel();
        onQtyOrEPChanged(i); // initial Summe
    }

    private void onQtyOrEPChanged(int i) {
        if (isEmpty(txtAnz[i])) { txtAnz[i].setBackground(Color.PINK); return; }
        try {
            bdAnzahl[i] = parseStringToBigDecimalSafe(txtAnz[i].getText(), LocaleFormat.AUTO);
            txtAnz[i].setBackground(Color.WHITE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Eingabe inkorrekt …", "Angebot", JOptionPane.ERROR_MESSAGE);
            txtAnz[i].setText("");
        }
    }

    private void doSave() {
        if (!kundeGewählt) { info("Kunde nicht ausgewählt …"); return; }
        if (!mind1ArtikelGewählt) { info("keine Artikel ausgewählt …"); return; }
        if (isEmpty(txtReferenz)) { info("Kundenreferenz fehlt …"); return; }

        Lieferschein l = new Lieferschein();
        l.setIdNummer(nextLsNummer());
        l.setJahr(Einstellungen.getAppSettings().year);
        l.setDatum(dateOrToday(datePicker));
        Kunde k = kunde.get(cmbKunde.getSelectedIndex());
        l.setIdKunde(Objects.toString(k.getId(),""));
        l.setRef(txtReferenz.getText().trim());
        
        int posCount = countFilledPositions();
        l.setAnzPos(posCount);

        for (int i=0;i<POS_COUNT;i++) {
            if (sPosText[i]==null || bdAnzahl[i]==null) continue;
            setLieferscheinPosition(l, i, sPosText[i], bdAnzahl[i]);
        }
        l.setState(1); // erstellt

        lieferscheinRepository.save(l);
        HauptFenster.actScreen();
    }

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
    
    private void loadData() {
        kunde.clear();
        artikel.clear();

        // Dummy an Position 0
        kunde.add(new Kunde());
        kunde.addAll(kundeRepository.findAll());

        artikel.add(new Artikel());
        artikel.addAll(artikelRepository.findAll());
    }

    private void setLieferscheinPosition(Lieferschein l, int idx0, String text, BigDecimal menge) {
        switch (idx0) {
            case 0 -> { l.setArt01(text); l.setMenge01(menge); }
            case 1 -> { l.setArt02(text); l.setMenge02(menge); }
            case 2 -> { l.setArt03(text); l.setMenge03(menge); }
            case 3 -> { l.setArt04(text); l.setMenge04(menge); }
            case 4 -> { l.setArt05(text); l.setMenge05(menge); }
            case 5 -> { l.setArt06(text); l.setMenge06(menge); }
            case 6 -> { l.setArt07(text); l.setMenge07(menge); }
            case 7 -> { l.setArt08(text); l.setMenge08(menge); }
            case 8 -> { l.setArt09(text); l.setMenge09(menge); }
            case 9 -> { l.setArt10(text); l.setMenge10(menge); }
            case 10 -> { l.setArt11(text); l.setMenge11(menge); }
            case 11 -> { l.setArt12(text); l.setMenge12(menge); }
            default -> {}
        }
    }

    private int countFilledPositions() {
        int n=0;
        for (int i=0;i<POS_COUNT;i++) {
            if (sPosText[i]!=null && bdAnzahl[i]!=null) n++;
        }
        return n;
    }

    private void recomputeMind1Artikel() {
        mind1ArtikelGewählt = false;
        for (int i=0;i<POS_COUNT;i++) if (cbPos[i].getSelectedIndex()>0) { mind1ArtikelGewählt=true; break; }
    }

    private static JTextField setRO(int x, int y) { return setRO(x, y, 200); }
    private static JTextField setRO(int x, int y, int w) {
        JTextField t=new JTextField();
        t.setBounds(x,y,w,25);
        t.setFont(new Font("Tahoma", Font.BOLD, 11));
        t.setEditable(false);
        return t;
    }

    private static JTextField centeredField(int x,int y,int w){
        JTextField t=new JTextField();
        t.setHorizontalAlignment(SwingConstants.CENTER);
        t.setBounds(x,y,w,25);
        return t;
    }

    private DatePicker makeDatePicker(int x,int y){
        DatePickerSettings st = new DatePickerSettings();
        st.setWeekNumbersDisplayed(true, true);
        st.setFormatForDatesCommonEra("dd.MM.yyyy");
        DatePicker dp = new DatePicker(st);
        dp.setDate(StartUp.getDateNow());
        dp.getComponentDateTextField().setBorder(new RoundedBorder(10));
        dp.getComponentDateTextField().setFont(new Font("Tahoma", Font.BOLD, 14));
        dp.getComponentDateTextField().setForeground(Color.BLUE);
        dp.getComponentDateTextField().setHorizontalAlignment(SwingConstants.CENTER);
        dp.setBounds(x,y,140,25);
        return dp;
    }

    private static LocalDate dateOrToday(DatePicker dp){
        return dp.getDate()!=null ? dp.getDate() : StartUp.getDateNow();
    }

    private static String nullToEmpty(String s){ return s==null ? "" : s; }

    private static boolean isEmpty(JTextField t){ return t.getText()==null || t.getText().trim().isEmpty(); }

    private static DocumentListener docChanged(Runnable r){
        return new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e){ r.run(); }
            @Override public void removeUpdate(DocumentEvent e){ r.run(); }
            @Override public void changedUpdate(DocumentEvent e){ r.run(); }
        };
    }

    private static DocumentListener bgFlipOnNonEmpty(JTextField f){
        return docChanged(() -> f.setBackground(f.getText().trim().isEmpty()? Color.PINK : Color.WHITE));
    }

    private void clearKunde(){
        for (JTextField t : txtKd) {
            t.setText("");
        }
    }

    private String nextLsNummer() {
        int max = lieferscheinRepository.findMaxNummerByJahr(Einstellungen.getAppSettings().year);
        return "LS-" + Einstellungen.getAppSettings().year + "-" + String.format("%04d", max + 1);
    }

    private static void info(String msg){
        JOptionPane.showMessageDialog(null, msg, "Lieferschein erstellen", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void attachCommaToDot(JTextField field) {
        ((AbstractDocument) field.getDocument()).setDocumentFilter(new CommaHelper.CommaToDotFilter());
    }

	@Override
	public void initContent() {
		// TODO Auto-generated method stub
		
	}
	
	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

}
