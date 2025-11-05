package org.andy.fx.gui.main.overview_panels.edit_panels.factory;

import static org.andy.fx.code.misc.ArithmeticHelper.parseStringToBigDecimalSafe;
import static org.andy.fx.code.misc.TextFormatter.FormatIBAN;
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
import javax.swing.JCheckBox;
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
import org.andy.fx.code.dataStructure.entityMaster.Bank;
import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityProductive.Angebot;
import org.andy.fx.code.dataStructure.repositoryMaster.ArtikelRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.BankRepository;
import org.andy.fx.code.dataStructure.repositoryMaster.KundeRepository;
import org.andy.fx.code.dataStructure.repositoryProductive.AngebotRepository;
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

public class AngebotNeuPanel extends EditPanel {
	
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LogManager.getLogger(AngebotNeuPanel.class);

    private static final int POS_COUNT = 12;

    // Datenquellen
    private final KundeRepository kundeRepository = new KundeRepository();
    private final BankRepository bankRepository = new BankRepository();
    private final ArtikelRepository artikelRepository = new ArtikelRepository();
    private final AngebotRepository angebotRepository = new AngebotRepository();

    // Daten
    private final List<Kunde> kunden = new ArrayList<>();
    private final List<Bank> banken = new ArrayList<>();
    private final List<Artikel> artikel = new ArrayList<>();

    // UI Felder
    private JComboBox<String> cmbKunde;
    private JTextField[] txtKd = new JTextField[12];
    private JCheckBox chkRevCharge;

    private JComboBox<String> cmbBank;
    private JTextField txtBank, txtIBAN, txtBIC;

    private JTextField txtNummer, txtReferenz;
    private JCheckBox chkPage2; private JLabel lblHinweis;
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
    private boolean kundeGewählt = false;
    private boolean bankGewählt = false;
    private boolean mind1ArtikelGewählt = false;
    
    private String htmlText = null;

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
    public AngebotNeuPanel() {
        super("neues Angebot erstellen");
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
        							 "%","Rabattschlüssel","%","Zahlungsziel","Tage","Bank","IBAN","BIC" };
        
        final int[][] leftBounds = {{10,55},{10,80},{10,105},{10,130},{10,155},{10,180},{10,205},{10,230},{10,255},{10,280},  {10,305},  {10,330},
                																								  {155,280}, {155,305}, {155,330},
                																								 {1010,280},{1010,305},{1010,330}};
        
        final String preFlightLabel = "<html>Bei Erstellung des Angebots wird ein Standardtext hinterlegt. Quelle ist die Datei:<br>"
				   					+ "<font color='blue'><b>%s</b></font><br>";
        
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

        JLabel lbl25=new JLabel("Angebotsnummer:"); lbl25.setBounds(1010,55,125,25); add(lbl25);
        JLabel lbl26=new JLabel("Angebotsdatum:");  lbl26.setBounds(1010,80,125,25); add(lbl26);
        JLabel lbl29=new JLabel("Referenz");        lbl29.setBounds(1010,105,60,25);  add(lbl29);
        
        lblHinweis = new JLabel(String.format(preFlightLabel, Einstellungen.getAppSettings().tplDescriptionBase));
        lblHinweis.setBounds(1130,155,700,75); lblHinweis.setVisible(false); add(lblHinweis);

        // Combos/Textfelder links
        cmbKunde = new JComboBox<>(kunden.stream().map(k -> nullToEmpty(k.getName())).toArray(String[]::new));
        cmbKunde.setBounds(10,30,300,25); add(cmbKunde);

        for (int ii=0;ii<txtKd.length;ii++) {
        	txtKd[ii]=setRO(110, 55+(ii*25));
        	add(txtKd[ii]);
        }

        chkRevCharge = new JCheckBox("ReverseCharge-Hinweis"); chkRevCharge.setBounds(110,355,110,25); chkRevCharge.setVisible(false); add(chkRevCharge);

        cmbBank = new JComboBox<>(banken.stream().map(b -> nullToEmpty(b.getBankName())).toArray(String[]::new));
        cmbBank.setBounds(1010,255,300,25); add(cmbBank);

        txtBank=setRO(1110,280); txtIBAN=setRO(1110,305); txtBIC=setRO(1110,330);
        add(txtBank); add(txtIBAN); add(txtBIC);

        txtNummer = new JTextField(nextAnNummer());
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
        
        chkPage2 = new JCheckBox("Angebot mit Anlage (Beschreibung aus Seite 2 hinzufügen)");
        chkPage2.setBounds(1130,130,390,25); add(chkPage2);

        JButton btnDoExport = createButton("<html>Angebot<br>erstellen</html>", ButtonIcon.EDIT.icon(), null);
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
        cmbKunde.addActionListener(_ -> onKundeChanged());
        cmbBank.addActionListener(_ -> onBankChanged());

        chkPage2.addActionListener(_ -> onPage2Activated());
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
        Kunde k = kunden.get(idx);
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
        chkRevCharge.setVisible("0".equals(txtKd[9].getText()));
        kundeGewählt = true;
    }

    private void onBankChanged() {
        int idx = cmbBank.getSelectedIndex();
        if (idx <= 0) {
            txtBank.setText(""); txtIBAN.setText(""); txtBIC.setText("");
            bankGewählt = false;
            return;
        }
        Bank b = banken.get(idx);
        txtBank.setText(b.getBankName());
        txtIBAN.setText(FormatIBAN(b.getIban()));
        txtBIC.setText(b.getBic());
        bankGewählt = true;
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
    
    private void onPage2Activated() {
    	if (chkPage2.isSelected()) {
    		htmlText = Einstellungen.getHtmlBaseText();
        	lblHinweis.setVisible(true);
    	} else {
    		htmlText = null;
        	lblHinweis.setVisible(false);
    	}
    }
    
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
    
    private void doSave() {
        if (!kundeGewählt) { info("Kunde nicht ausgewählt …"); return; }
        if (!mind1ArtikelGewählt) { info("keine Artikel ausgewählt …"); return; }
        if (datePicker.getDate() == null) { info("Angebotsdatum fehlt …"); return; }
        if (isEmpty(txtReferenz)) { info("Kundenreferenz fehlt …"); return; }
        if (!bankGewählt)  { info("Bank nicht ausgewählt …");  return; }

        Angebot a = new Angebot();
        a.setIdNummer(nextAnNummer());
        a.setJahr(Einstellungen.getAppSettings().year);
        a.setDatum(dateOrToday(datePicker));
        Kunde k = kunden.get(cmbKunde.getSelectedIndex());
        Bank  b = banken.get(cmbBank.getSelectedIndex());
        a.setIdKunde(Objects.toString(k.getId(),""));
        a.setIdBank(b.getId());
        a.setRef(txtReferenz.getText().trim());
        a.setRevCharge(chkRevCharge.isSelected()?1:0);
        a.setPage2(chkPage2.isSelected()?1:0);
        a.setBeschreibungHtml(htmlText); // Liefer- und Leistungsbeschreibung
        
        int posCount = countFilledPositions();
        a.setAnzPos(posCount);

        BigDecimal netto = BD.ZERO;
        for (int i=0;i<POS_COUNT;i++) {
            if (sPosText[i]==null || bdAnzahl[i]==null || bdEinzel[i]==null) continue;
            setAngebotPosition(a, i, sPosText[i], bdAnzahl[i], bdEinzel[i]);
            netto = netto.add(bdEinzel[i].multiply(bdAnzahl[i]));
        }
        a.setNetto(netto.setScale(2, RoundingMode.HALF_UP));
        a.setUst(BD.ZERO);
        a.setBrutto(BD.ZERO);
        a.setlZeitr(" ");
        a.setState(1); // erstellt

        angebotRepository.save(a);
        HauptFenster.actScreen();
    }
    
    private void loadData() {
        kunden.clear();
        banken.clear();
        artikel.clear();

        // Dummy an Position 0
        kunden.add(new Kunde());
        kunden.addAll(kundeRepository.findAll());

        banken.add(new Bank());
        banken.addAll(bankRepository.findAll());

        artikel.add(new Artikel());
        artikel.addAll(artikelRepository.findAll());
    }

    private void setAngebotPosition(Angebot a, int idx0, String text, BigDecimal menge, BigDecimal ep) {
        switch (idx0) {
            case 0 -> { a.setArt01(text); a.setMenge01(menge); a.setePreis01(ep); }
            case 1 -> { a.setArt02(text); a.setMenge02(menge); a.setePreis02(ep); }
            case 2 -> { a.setArt03(text); a.setMenge03(menge); a.setePreis03(ep); }
            case 3 -> { a.setArt04(text); a.setMenge04(menge); a.setePreis04(ep); }
            case 4 -> { a.setArt05(text); a.setMenge05(menge); a.setePreis05(ep); }
            case 5 -> { a.setArt06(text); a.setMenge06(menge); a.setePreis06(ep); }
            case 6 -> { a.setArt07(text); a.setMenge07(menge); a.setePreis07(ep); }
            case 7 -> { a.setArt08(text); a.setMenge08(menge); a.setePreis08(ep); }
            case 8 -> { a.setArt09(text); a.setMenge09(menge); a.setePreis09(ep); }
            case 9 -> { a.setArt10(text); a.setMenge10(menge); a.setePreis10(ep); }
            case 10 -> { a.setArt11(text); a.setMenge11(menge); a.setePreis11(ep); }
            case 11 -> { a.setArt12(text); a.setMenge12(menge); a.setePreis12(ep); }
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

    private void clearKunde(){
        for (JTextField t : txtKd) {
            t.setText("");
        }
        chkRevCharge.setVisible(false);
    }

    private String nextAnNummer() {
        int max = angebotRepository.findMaxNummerByJahr(Einstellungen.getAppSettings().year);
        return "AN-" + Einstellungen.getAppSettings().year + "-" + String.format("%04d", max + 1);
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
