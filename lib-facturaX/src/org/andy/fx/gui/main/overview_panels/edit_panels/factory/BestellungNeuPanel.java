package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityProductive.Bestellung;
import org.andy.fx.code.dataStructure.repositoryMaster.ArtikelRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.LieferantRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.BestellungRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.main.StartUp;
import org.andy.fx.code.misc.ArithmeticHelper.LocaleFormat;
import org.andy.fx.code.misc.BD;
import org.andy.fx.code.misc.CommaHelper;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.main.HauptFenster;
import org.andy.fx.gui.main.overview_panels.edit_panels.EditPanel;
import org.andy.fx.gui.misc.RoundedBorder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class BestellungNeuPanel extends EditPanel {
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(BestellungNeuPanel.class);

    private static final int POS_COUNT = 12;

    // Datenquellen
    private final LieferantRepository lieferantRepository = new LieferantRepository();
    private final ArtikelRepository artikelRepository = new ArtikelRepository();
    private final BestellungRepository bestellungRepository = new BestellungRepository();

    // Daten
    private final List<Lieferant> lieferant = new ArrayList<>();
    private final List<Artikel> artikel = new ArrayList<>();

    // UI Felder
    private JComboBox<String> cmbLieferant;
    private JTextField[] txtLi = new JTextField[9];

    private JTextField txtNummer, txtReferenz;
    private DatePicker datePicker;

    private final JLabel[] lblPos = new JLabel[POS_COUNT];
    @SuppressWarnings("unchecked")
	private final JComboBox<String>[] cbPos = new JComboBox[POS_COUNT];
    private final JTextField[] txtAnz = new JTextField[POS_COUNT];
    private final JTextField[] txtEP  = new JTextField[POS_COUNT];
    private final JTextField[] txtGP  = new JTextField[POS_COUNT];

    private final BigDecimal[] bdAnzahl = new BigDecimal[POS_COUNT];
    private final BigDecimal[] bdEinzel = new BigDecimal[POS_COUNT];
    private final BigDecimal[] bdSumme  = new BigDecimal[POS_COUNT];
    private final String[] sPosText = new String[POS_COUNT];

    // Zustände
    private boolean lieferantGewählt = false;
    private boolean mind1ArtikelGewählt = false;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public BestellungNeuPanel() {
        super("neue Bestellung erstellen");
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
        final String[] leftLabels = { "Lieferanten-Nr.", "Kundennummer", "Name","Strasse","PLZ","Ort","Land","UID","USt.-Satz" };
        final int[][] leftBounds = { {10,55},{10,80},{10,105},{10,130},{10,155},{10,180},{10,205},{10,230},{10,255} };
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
        JLabel lbl23=new JLabel("Einzel"); lbl23.setBounds(855,30,70,25);
        JLabel lbl24=new JLabel("Summe"); lbl24.setBounds(925,30,70,25);
        for (JLabel x : new JLabel[]{lbl20,lbl21,lbl22,lbl23,lbl24}) {
            x.setHorizontalAlignment(SwingConstants.CENTER);
            add(x);
        }

        JLabel lbl25=new JLabel("Bestellnummer:"); lbl25.setBounds(1010,55,125,25); add(lbl25);
        JLabel lbl26=new JLabel("Bestelldatum:");  lbl26.setBounds(1010,80,125,25); add(lbl26);
        JLabel lbl29=new JLabel("Referenz");        lbl29.setBounds(1010,105,60,25);  add(lbl29);

        // Combos/Textfelder links
        cmbLieferant = new JComboBox<>(lieferant.stream().map(k -> nullToEmpty(k.getName())).toArray(String[]::new));
        cmbLieferant.setBounds(10,30,300,25); add(cmbLieferant);

        for (int ii=0;ii<txtLi.length;ii++) {
        	txtLi[ii]=setRO(110, 55+(ii*25));
        	add(txtLi[ii]);
        }

        txtNummer = new JTextField(nextBeNummer());
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
        
        JButton btnDoExport = createButton("<html>Bestellung<br>erstellen</html>", ButtonIcon.EDIT.icon(), null);
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
            txtEP[i] =centeredField(855,y,70); txtEP[i].setEditable(false); add(txtEP[i]); attachCommaToDot(txtEP[i]);
            txtGP[i] =centeredField(925,y,70); txtGP[i].setEditable(false); add(txtGP[i]);

            // Listener je Zeile
            cbPos[i].addActionListener(_ -> onArtikelChosen(i));
            txtEP[i].getDocument().addDocumentListener(docChanged(() -> onEPChanged(i)));
            txtAnz[i].getDocument().addDocumentListener(docChanged(() -> onQtyOrEPChanged(i)));
        }
        
        // Trenner
        JSeparator s1=new JSeparator(JSeparator.VERTICAL); s1.setBounds(315,10,2,370); add(s1);
        JSeparator s2=new JSeparator(JSeparator.VERTICAL); s2.setBounds(1000,10,2,370); add(s2);

        // Aktionen
        cmbLieferant.addActionListener(_ -> onLieferantChanged());

        btnDoExport.addActionListener(_ -> doSave());

        setPreferredSize(new Dimension(1000, 390));
    }
    
	//###################################################################################################################################################
	// ActionListener
	//###################################################################################################################################################

    private void onLieferantChanged() {
        int idx = cmbLieferant.getSelectedIndex();
        if (idx <= 0) {
            clearLieferant();
            lieferantGewählt = false;
            return;
        }
        Lieferant l = lieferant.get(idx);
        txtLi[0].setText(l.getId());
        txtLi[1].setText(l.getKdnr());
        txtLi[2].setText(l.getName());
        txtLi[3].setText(l.getStrasse());
        txtLi[4].setText(l.getPlz());
        txtLi[5].setText(l.getOrt());
        txtLi[6].setText(l.getLand());
        txtLi[7].setText(l.getUstid());
        txtLi[8].setText(l.getTaxvalue());
        lieferantGewählt = true;
    }

    private void onArtikelChosen(int i) {
        int idx = cbPos[i].getSelectedIndex();
        if (idx <= 0) {
            txtAnz[i].setText(""); txtEP[i].setText(""); txtGP[i].setText("");
            txtAnz[i].setEnabled(false); txtEP[i].setEditable(false);
            txtAnz[i].setBackground(Color.WHITE); txtEP[i].setBackground(Color.WHITE);
            bdAnzahl[i]=bdEinzel[i]=bdSumme[i]=null; sPosText[i]=null;
            recomputeMind1Artikel();
            return;
        }
        Artikel a = artikel.get(idx);
        sPosText[i] = a.getText();
        bdEinzel[i] = a.getWert();
        txtEP[i].setText(bdEinzel[i].toString());
        txtAnz[i].setEnabled(true);
        txtAnz[i].setBackground(Color.PINK);
        recomputeMind1Artikel();
        onQtyOrEPChanged(i); // initial Summe
    }

    private void onEPChanged(int i) {
        String s = txtEP[i].getText().trim();
        if (s.isEmpty()) { bdEinzel[i]=null; txtGP[i].setText(""); return; }
        try {
            bdEinzel[i] = parseStringToBigDecimalSafe(s, LocaleFormat.AUTO);
            onQtyOrEPChanged(i);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Eingabe inkorrekt …", "Angebot", JOptionPane.ERROR_MESSAGE);
            txtEP[i].setText("");
        }
    }

    private void onQtyOrEPChanged(int i) {
        if (isEmpty(txtEP[i]) || isEmpty(txtAnz[i])) { txtAnz[i].setBackground(Color.PINK); txtGP[i].setText(""); return; }
        try {
            bdAnzahl[i] = parseStringToBigDecimalSafe(txtAnz[i].getText(), LocaleFormat.AUTO);
            if (bdEinzel[i] == null) bdEinzel[i] = parseStringToBigDecimalSafe(txtEP[i].getText(), LocaleFormat.AUTO);
            bdSumme[i]  = bdEinzel[i].multiply(bdAnzahl[i]).setScale(2, RoundingMode.HALF_UP);
            txtGP[i].setText(bdSumme[i].toString());
            txtAnz[i].setBackground(Color.WHITE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Eingabe inkorrekt …", "Angebot", JOptionPane.ERROR_MESSAGE);
            txtAnz[i].setText("");
            txtGP[i].setText("");
        }
    }

    private void doSave() {
        if (!lieferantGewählt) { info("Lieferant nicht ausgewählt …"); return; }
        if (!mind1ArtikelGewählt) { info("keine Artikel ausgewählt …"); return; }
        if (isEmpty(txtReferenz)) { info("Kundenreferenz fehlt …"); return; }

        Bestellung b = new Bestellung();
        b.setIdNummer(nextBeNummer());
        b.setJahr(Einstellungen.getAppSettings().year);
        b.setDatum(dateOrToday(datePicker));
        Lieferant l = lieferant.get(cmbLieferant.getSelectedIndex());
        b.setIdLieferant(Objects.toString(l.getId(),""));
        b.setRef(txtReferenz.getText().trim());
        
        int posCount = countFilledPositions();
        b.setAnzPos(posCount);

        BigDecimal ustFaktor = parseStringToBigDecimalSafe(l.getTaxvalue(), LocaleFormat.AUTO).divide(BD.HUNDRED);
        BigDecimal netto = BD.ZERO; BigDecimal ust = BD.ZERO; BigDecimal brutto = BD.ZERO;
        for (int i=0;i<POS_COUNT;i++) {
            if (sPosText[i]==null || bdAnzahl[i]==null || bdEinzel[i]==null) continue;
            setBestellungPosition(b, i, sPosText[i], bdAnzahl[i], bdEinzel[i]);
            netto = netto.add(bdEinzel[i].multiply(bdAnzahl[i]));
        }
        b.setNetto(netto.setScale(2, RoundingMode.HALF_UP));
        ust = netto.multiply(ustFaktor);
        b.setUst(ust);
        brutto = netto.add(ust);
        b.setBrutto(brutto);
        b.setState(1); // erstellt

        bestellungRepository.save(b);
        HauptFenster.actScreen();
    }

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
    
    private void loadData() {
        lieferant.clear();
        artikel.clear();

        // Dummy an Position 0
        lieferant.add(new Lieferant());
        lieferant.addAll(lieferantRepository.findAll());

        artikel.add(new Artikel());
        artikel.addAll(artikelRepository.findAll());
    }

    private void setBestellungPosition(Bestellung b, int idx0, String text, BigDecimal menge, BigDecimal ep) {
        switch (idx0) {
            case 0 -> { b.setArt01(text); b.setMenge01(menge); b.setePreis01(ep); }
            case 1 -> { b.setArt02(text); b.setMenge02(menge); b.setePreis02(ep); }
            case 2 -> { b.setArt03(text); b.setMenge03(menge); b.setePreis03(ep); }
            case 3 -> { b.setArt04(text); b.setMenge04(menge); b.setePreis04(ep); }
            case 4 -> { b.setArt05(text); b.setMenge05(menge); b.setePreis05(ep); }
            case 5 -> { b.setArt06(text); b.setMenge06(menge); b.setePreis06(ep); }
            case 6 -> { b.setArt07(text); b.setMenge07(menge); b.setePreis07(ep); }
            case 7 -> { b.setArt08(text); b.setMenge08(menge); b.setePreis08(ep); }
            case 8 -> { b.setArt09(text); b.setMenge09(menge); b.setePreis09(ep); }
            case 9 -> { b.setArt10(text); b.setMenge10(menge); b.setePreis10(ep); }
            case 10 -> { b.setArt11(text); b.setMenge11(menge); b.setePreis11(ep); }
            case 11 -> { b.setArt12(text); b.setMenge12(menge); b.setePreis12(ep); }
            default -> {}
        }
    }

    private int countFilledPositions() {
        int n=0;
        for (int i=0;i<POS_COUNT;i++) {
            if (sPosText[i]!=null && bdAnzahl[i]!=null && bdEinzel[i]!=null) n++;
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

    private void clearLieferant(){
        for (JTextField t : txtLi) {
            t.setText("");
        }
    }

    private String nextBeNummer() {
        int max = bestellungRepository.findMaxNummerByJahr(Einstellungen.getAppSettings().year);
        return "BE-" + Einstellungen.getAppSettings().year + "-" + String.format("%04d", max + 1);
    }

    private static void info(String msg){
        JOptionPane.showMessageDialog(null, msg, "Angebot erstellen", JOptionPane.INFORMATION_MESSAGE);
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
