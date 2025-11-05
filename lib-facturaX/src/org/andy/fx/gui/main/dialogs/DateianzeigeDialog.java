package org.andy.fx.gui.main.dialogs;

import static org.andy.fx.code.dataStructure.HibernateUtil.getSessionFactoryDb2;
import static org.andy.fx.code.misc.FileSelect.chooseFile;
import static org.andy.fx.code.misc.FileSelect.choosePath;
import static org.andy.fx.code.misc.FileSelect.getNotSelected;
import static org.andy.fx.code.misc.TextFormatter.*;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import org.andy.fx.code.dataStructure.entityMaster.Kunde;
import org.andy.fx.code.dataStructure.entityMaster.Lieferant;
import org.andy.fx.code.dataStructure.entityProductive.FileStore;
import org.andy.fx.code.dataStructure.repositoryProductive.FileStoreRepository;
import org.andy.fx.code.main.Einstellungen;
import org.andy.fx.code.misc.App;
import org.andy.fx.gui.iconHandler.ButtonIcon;
import org.andy.fx.gui.iconHandler.FileIcon;
import org.andy.fx.gui.iconHandler.FrameIcon;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;

public class DateianzeigeDialog extends JFrame {

	private static final Logger logger = LogManager.getLogger(DateianzeigeDialog.class);
	private static final long serialVersionUID = 1L;
	private static App a = new App();

	private JPanel contentPane = new JPanel();

	private static final String UPLOAD = "upload", DOWNLOAD = "download", UPDATE = "update", DELETE = "delete", SEND = "senden";
	private static final String CSV = "csv", JPG = "jpg", MSG = "msg", PDF = "pdf", PNG = "png", RAR = "rar", XLSM = "xlsm", XLSX = "xlsx", XML = "xml", ZIP = "zip";
	private static final String UCSV = "CSV", UJPG = "JPG", UMSG = "MSG", UPDF = "PDF", UPNG = "PNG", URAR = "RAR", UXLSM = "XLSM", UXLSX = "XLSX", UXML = "XML", UZIP = "ZIP";

	private static JLabel lblContentName = new JLabel("");
	
	private static final Font FONT_PLAIN = new Font("Tahoma", Font.PLAIN, 11);
	private static final Font FONT_BOLD  = new Font("Tahoma", Font.BOLD, 11);
	private static final String[] TYPES  = {"AN", "AB", "BE", "RE", "ZE", "M1", "M2", "01", "02", "03", "LS"};

	// Titeltexte (linke Spalte)
	private static final String[] TITLE_TEXTS = {
	    "Angebot:", "Auftragsbestätigung:", "Bestellung:", "Rechnung:",
	    "Zahlungserinnerung:", "Mahnstufe 1:", "Mahnstufe 2",
	    "zus. Datei 1:", "zus. Datei 2:", "zus. Datei 3:", "Lieferschein"
	};

	// Labels (linke Titel, Dateiname, Dateityp-Icon)
	private final JLabel[] lblTitle = new JLabel[11];
	private static final JLabel[] lblFileName = new JLabel[11];
	private static final JLabel[] lblFileTyp = new JLabel[11];

	// Buttons
	private static final JButton[] btnDownload = new JButton[11];
	private static final JButton[] btnUpload   = new JButton[11];
	private static final JButton[] btnUpdate   = new JButton[11];
	private static final JButton[] btnDelete   = new JButton[11];
	private static final JButton[] btnSendMail = new JButton[11];

	private static String sNummer = null;
	private static Kunde lKunde = null;
	@SuppressWarnings("unused")
	private static Lieferant lLieferant = null;
	private static int isFile = 0;
	
	private static FileStoreRepository fileStoreRepository = new FileStoreRepository();
	private static FileStore fileStore = null;
	

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################

	public static void loadGUI(String sID, Kunde kunde) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					sNummer = sID;
					lKunde = kunde;
					fileStore = fileStoreRepository.findById(sID); // Tabelleneintrag mit Hibernate lesen
					DateianzeigeDialog frame = new DateianzeigeDialog();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI(String sID) fehlgeschlagen - " + e);
					Runtime.getRuntime().gc();
				}
			}
		});
	}
	
	public static void loadGUIBE(String sID, Lieferant lieferant) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					sNummer = sID;
					lLieferant = lieferant;
					fileStore = fileStoreRepository.findById(sID); // Tabelleneintrag mit Hibernate lesen
					DateianzeigeDialog frame = new DateianzeigeDialog();
					frame.setVisible(true);
				} catch (Exception e) {
					logger.fatal("loadGUI(String sID) fehlgeschlagen - " + e);
					Runtime.getRuntime().gc();
				}
			}
		});
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################

	private DateianzeigeDialog() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				destroyWindow();
			}
		});
		
		setIconImage(FrameIcon.FILE.image());
		setResizable(false);
		setTitle("Dateihandling - " + a.NAME + " " + " (" + a.VERSION + ")");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 785, 650);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(null);

		lblContentName.setFont(new Font("Arial", Font.BOLD, 16));
		lblContentName.setHorizontalAlignment(SwingConstants.CENTER);
		lblContentName.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		lblContentName.setBounds(5, 10, 760, 30);
		contentPane.add(lblContentName);

		// Positionierung
		int baseY = 50;
		int rowH  = 50;

		for (int i = 0; i < lblTitle.length; i++) {
		    int y = baseY + i * rowH;

		    // Titel-Label
		    lblTitle[i] = new JLabel(TITLE_TEXTS[i]);
		    lblTitle[i].setBounds(10, y, 120, 40);
		    contentPane.add(lblTitle[i]);

		    // Dateityp-Icon-Label
		    lblFileTyp[i] = new JLabel();
		    lblFileTyp[i].setBounds(140, y, 50, 40);
		    lblFileTyp[i].setHorizontalAlignment(SwingConstants.CENTER);
		    contentPane.add(lblFileTyp[i]);

		    // Dateiname-Label
		    lblFileName[i] = new JLabel();
		    lblFileName[i].setBounds(200, y, 350, 40);
		    contentPane.add(lblFileName[i]);

		    // Buttons
		    btnDownload[i] = createButton(DOWNLOAD, 560, y);
		    btnUpload[i]   = createButton(UPLOAD,   560, y);
		    btnUpdate[i]   = createButton(UPDATE,   610, y);
		    btnDelete[i]   = createButton(DELETE,   660, y);
		    btnSendMail[i] = createButton(SEND,     710, y);

		    contentPane.add(btnDownload[i]);
		    contentPane.add(btnUpload[i]);
		    contentPane.add(btnUpdate[i]);
		    contentPane.add(btnDelete[i]);
		    contentPane.add(btnSendMail[i]);
		}


		for (int i = 0; i < btnDownload.length; i++) {
		    final int idx = i;
		    final String typ = TYPES[i];

		    // Download (Speichern + Öffnen)
		    btnDownload[i].addActionListener(_ -> {
		        String fn = saveFile(typ, sNummer);
		        if (fn != null && !fn.equals(getNotSelected())) {
		            try { Desktop.getDesktop().open(new File(fn)); }
		            catch (IOException ex) { logger.error("error opening file for view - " + ex); }
		        }
		    });

		    // Upload (insert/update via upsert)
		    btnUpload[i].addActionListener(_ -> loadFile(typ, sNummer));

		    // Update
		    btnUpdate[i].addActionListener(_ -> updateFile(typ, sNummer));

		    // Delete (nur gewählte Spalte auf null)
		    btnDelete[i].addActionListener(_ -> deleteFile(typ, sNummer));

		    // Senden (Mail-Template abhängig vom Typ/Index)
		    btnSendMail[i].addActionListener(_ -> {
		        String completeFileName = getFileForMail(typ, sNummer, Einstellungen.getAppSettings().work);
		        String fileName = completeFileName;
		        try { fileName = cutFromRight(completeFileName, '\\'); } 
		        catch (IOException ex) { logger.error("error cutting filename", ex); }

		        String text = switch (idx) {
		            case 0 -> "gerne sende ich Ihnen das Angebot (" + fileName + ") zu Ihrer Anfrage.";
		            case 1 -> "gerne sende ich Ihnen die Auftragsbestätigung (" + fileName + ") zu Ihrer Bestellung.";
		            case 3 -> "in der Anlage sende ich Ihnen meine Rechung (" + fileName + ") zur erfolgten Dienstleistung.";
		            case 4 -> "in der Hektik des Geschäftsalltags kann es passieren, dass etwas untergeht.\n"
		                    + "Ich sende Ihnen meine Zahlungserinnerung (" + fileName + "), da die zugehörige Rechnung noch offen ist.";
		            case 5 -> "leider konnte ich trotz bereits zugesendeter Zahlungserinnerung keinen Zahlungseingang feststellen.\n"
		                    + "Anbei die 1. Mahnung (" + fileName + ") zur Begleichung der offenen Rechnung.";
		            case 6 -> "leider konnte ich trotz bereits zugesendeter 1. Mahnung keinen Zahlungseingang feststellen.\n"
		                    + "Anbei die 2. Mahnung (" + fileName + ") zur Begleichung der offenen Rechnung.";
		            default -> null; // BE/sonstige ohne Versandtext
		        };

		        if (text != null) {
		            String[] zeilen = {
		                "Sehr geehrte(r) " + lKunde.getPronomen() + " " + lKunde.getPerson() + ",",
		                text, "", "Mit freundlichen Grüßen", "", "Andreas Fischer"
		            };
		            String body = String.join(System.lineSeparator(), zeilen);
		            sendMail(lKunde.geteBillMail(), fileName, body);
		        }
		    });
		}

		actualizeWindow();
	}

	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################

	private void sendMail(String sAdress, String sSubject, String sBody) {

		ActiveXComponent outlook = new ActiveXComponent("Outlook.Application");

		try {
			Dispatch mail = Dispatch.call(outlook, "CreateItem", 0).toDispatch();
			Dispatch.put(mail, "To", sAdress);
			Dispatch.put(mail, "Subject", sSubject);
			Dispatch.put(mail, "Body", sBody);

			// Datei als Anhang hinzufügen
			Dispatch attachments = Dispatch.get(mail, "Attachments").toDispatch();
			Dispatch.call(attachments, "Add", Einstellungen.getAppSettings().work + "\\" + sSubject);

			// E-Mail senden
			Dispatch.call(mail, "Send");

			JOptionPane.showMessageDialog(null, "E-Mail an [" + sAdress + "] erfolgreich versendet", "E-Mail Versand", JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e1) {
			logger.error("error sending email - " + e1);
		} finally {
			outlook.safeRelease();
		}

		boolean bLocked = Einstellungen.isLocked(Einstellungen.getAppSettings().work + "\\" + sSubject);
		while(bLocked) {
			System.out.println("warte auf Dateien ...");
		}
		File MailFile = new File(Einstellungen.getAppSettings().work + "\\" + sSubject);
		if(MailFile.delete()) {

		}else {
			logger.error("error deleting mail attachment from folder ...");
		}

	}
	
	//###################################################################################################################################################

	private void destroyWindow() {
	    lblContentName.setText("");

	    // Labels resetten
	    for (int i = 0; i < lblFileName.length; i++) {
	        lblFileName[i].setText(getNotSelected());
	        lblFileName[i].setFont(new Font("Tahoma", Font.PLAIN, 11));
	        lblFileTyp[i].setIcon(null);
	    }

	    // Alle Buttons ausblenden
	    for (int i = 0; i < btnDownload.length; i++) {
	        btnDownload[i].setVisible(false);
	        btnUpload[i].setVisible(false);
	        btnUpdate[i].setVisible(false);
	        btnDelete[i].setVisible(false);
	        btnSendMail[i].setVisible(false);
	    }

	    isFile = 0; // falls verwendet, wieder neutral setzen
	    // kein explizites GC nötig
	}
	
	//###################################################################################################################################################

	private void actualizeWindow() {
	    lblContentName.setText(sNummer);
	    fileStore = fileStoreRepository.findById(sNummer); // Tabelleneintrag mit Hibernate lesen
	    String[] fileNames = queryFileNames(sNummer);
	    setIcons(fileNames);
	    enableButtons(fileNames);
	    contentPane.revalidate();
	    contentPane.repaint();
	}
	
	//###################################################################################################################################################

	private static JButton createButton(String btnText, int xPos, int yPos) {
		JButton button = new JButton();
		button.setToolTipText(btnText);
		button.setIconTextGap(10);
		button.setBounds(xPos, yPos, 50, 40);
		button.setFont(new Font("Tahoma", Font.BOLD, 11));
		switch(btnText) {
		case UPLOAD:
			button.setIcon(ButtonIcon.UP.icon());
			break;
		case DOWNLOAD:
			button.setIcon(ButtonIcon.DOWN.icon());
			break;
		case UPDATE:
			button.setIcon(ButtonIcon.UPDATE.icon());
			break;
		case DELETE:
			button.setIcon(ButtonIcon.DEL.icon());
			break;
		case SEND:
			button.setIcon(ButtonIcon.MAIL.icon());
			break;
		}
		return button;
	}
	
	//###################################################################################################################################################

	private static String[] queryFileNames(String sID) {
	    String[] fileNames = new String[TYPES.length];

	    // Falls noch kein FileStore geladen wurde
	    if (fileStore == null) {
	        for (int i = 0; i < TYPES.length; i++) {
	            fileNames[i] = getNotSelected();
	            lblFileName[i].setText(fileNames[i]);
	            lblFileName[i].setFont(FONT_PLAIN);
	        }
	        return fileNames;
	    }

	    FileStore fs = fileStore;

	    for (int i = 0; i < TYPES.length; i++) {
	        String name = getName(fs, TYPES[i]); // AN/AB/BE/RE/01/02/03
	        boolean hasName = name != null && !name.trim().isEmpty();

	        fileNames[i] = hasName ? name : getNotSelected();
	        lblFileName[i].setText(fileNames[i]);
	        lblFileName[i].setFont(hasName ? FONT_BOLD : FONT_PLAIN);
	    }

	    return fileNames;
	}
	
	//###################################################################################################################################################

	private static int setIcons(String[] fileNames) {
	    int sum = 0;

	    for (int i = 0; i < lblFileTyp.length; i++) {
	        JLabel lbl = lblFileTyp[i];
	        lbl.setHorizontalAlignment(SwingConstants.CENTER);

	        String name = (i < fileNames.length && fileNames[i] != null) ? fileNames[i] : "";
	        try {
	            sum += setFileIcon(lbl, name); // deine bestehende Methode
	        } catch (IOException ex) {
	            logger.warn("setFileIcon failed for index {} ({})", i, name, ex);
	            lbl.setIcon(null);
	        }
	    }

	    isFile = sum;
	    return sum;
	}
	
	//###################################################################################################################################################

	public static int setFileIcon(JLabel lbl, String fileName) throws IOException {
		if(fileName.equals(getNotSelected()) || fileName == null || fileName.isEmpty()) {
			lbl.setIcon(null);
			return 0;
		}
		String typ = cutFromRight(fileName, '.');
		switch(typ) {
		case PDF:
			lbl.setIcon(FileIcon.FILE_PDF.icon());
			return 1;
		case PNG:
			lbl.setIcon(FileIcon.FILE_PNG.icon());
			return 1;
		case JPG:
			lbl.setIcon(FileIcon.FILE_JPG.icon());
			return 1;
		case CSV:
			lbl.setIcon(FileIcon.FILE_CSV.icon());
			return 1;
		case MSG:
			lbl.setIcon(FileIcon.FILE_MSG.icon());
			return 1;
		case XML:
			lbl.setIcon(FileIcon.FILE_XML.icon());
			return 1;
		case XLSX:
			lbl.setIcon(FileIcon.FILE_XLSX.icon());
			return 1;
		case XLSM:
			lbl.setIcon(FileIcon.FILE_XLSM.icon());
			return 1;
		case RAR:
			lbl.setIcon(FileIcon.FILE_RAR.icon());
			return 1;
		case ZIP:
			lbl.setIcon(FileIcon.FILE_ZIP.icon());
			return 1;
		case UPDF:
			lbl.setIcon(FileIcon.FILE_PDF.icon());
			return 1;
		case UPNG:
			lbl.setIcon(FileIcon.FILE_PNG.icon());
			return 1;
		case UJPG:
			lbl.setIcon(FileIcon.FILE_JPG.icon());
			return 1;
		case UCSV:
			lbl.setIcon(FileIcon.FILE_CSV.icon());
			return 1;
		case UMSG:
			lbl.setIcon(FileIcon.FILE_MSG.icon());
			return 1;
		case UXML:
			lbl.setIcon(FileIcon.FILE_XML.icon());
			return 1;
		case UXLSX:
			lbl.setIcon(FileIcon.FILE_XLSX.icon());
			return 1;
		case UXLSM:
			lbl.setIcon(FileIcon.FILE_XLSM.icon());
			return 1;
		case URAR:
			lbl.setIcon(FileIcon.FILE_RAR.icon());
			return 1;
		case UZIP:
			lbl.setIcon(FileIcon.FILE_ZIP.icon());
			return 1;
		default:
			lbl.setIcon(null);
			return 0;
		}
	}
	
	//###################################################################################################################################################

	private static void enableButtons(String[] fileNames) {
	    for (int i = 0; i < btnDownload.length; i++) {
	        boolean hasFile = fileNames[i] != null && !fileNames[i].isBlank() && !getNotSelected().equals(fileNames[i]);

	        // Grundsichtbarkeit
	        btnDownload[i].setVisible(hasFile);
	        btnUpload[i].setVisible(!hasFile);
	        btnUpdate[i].setVisible(hasFile);
	        btnDelete[i].setVisible(hasFile);
	        btnSendMail[i].setVisible(false); // Default

	        // Mail nur bei bestimmten Typen/Dateinamen
	        if (hasFile) {
	            switch (i) {
	                case 0, 1, 3, 4, 5, 6 -> btnSendMail[i].setVisible(true); // AN, AB, RE, ZE, M1, M2 immer erlauben
	                default -> { /* BE ohne Send */ }
	            }
	        }
	    }
	}

	//###################################################################################################################################################

	private String saveFile(String typ, String id) { 
	    try { return extractFileFromDB(typ, id); } 
	    catch (IOException ex) { logger.error("export failed", ex); return getNotSelected(); }
	}
	
	//###################################################################################################################################################

	private void loadFile(String typ, String id) {
		if(isFile == 0) {
			insertFileIntoDB(typ, id);
		}else {
			updateFileIntoDB(typ, id);
		}
		actualizeWindow();
	}
	
	//###################################################################################################################################################

	private void updateFile(String typ, String id) {
		updateFileIntoDB(typ, id);
		actualizeWindow();
	}
	
	//###################################################################################################################################################

	private void deleteFile(String typ, String id) {
		deleteFileFromDB(typ, id);
		actualizeWindow();
	}
	
	//###################################################################################################################################################

	private String getFileForMail(String typ, String id, String sPath) {
		String CompleteFileName = null;
		try {
			CompleteFileName = extractFileForMail(typ, id, sPath);
		} catch (IOException e) {
			Thread.currentThread().interrupt();
			logger.error("error reading file from database - " + e);
		}
		actualizeWindow();
		return CompleteFileName;
	}

	//###################################################################################################################################################

	public static String queryFileDB(String sTyp, String sID) {
	    try (Session s = getSessionFactoryDb2().openSession()) {
	        FileStore fs = s.find(FileStore.class, sID);
	        if (fs == null) return null;
	        return getName(fs, sTyp);
	    }
	}
	
	//###################################################################################################################################################

	private static void upsertFileHibernate(String typ, String id) {
	    String filePath = chooseFile(Einstellungen.getAppSettings().work);
	    if (filePath.equals(getNotSelected())) return;

	    File f = new File(filePath);
	    byte[] bytes;
	    try {
	        bytes = Files.readAllBytes(f.toPath());
	    } catch (IOException e) {
	        logger.error("Datei kann nicht gelesen werden: {}", filePath, e);
	        return;
	    }
	    String name = f.getName();

	    Transaction tx = null;
	    try (Session s = getSessionFactoryDb2().openSession()) {
	        tx = s.beginTransaction();
	        FileStore fs = s.find(FileStore.class, id);
	        if (fs == null) {
	            fs = new FileStore();
	            fs.setIdNummer(id);
	            fs.setJahr(Einstellungen.getAppSettings().year);
	        }
	        setNameAndData(fs, typ, name, bytes);
	        s.merge(fs);
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null && tx.getStatus().canRollback()) try { tx.rollback(); } catch (Exception ignore) {}
	        logger.error("upsertFileHibernate fehlgeschlagen (typ={}, id={})", typ, id, ex);
	    }
	}

	// alte Aufrufer ersetzen:
	private static void insertFileIntoDB(String sTyp, String sID) { upsertFileHibernate(sTyp, sID); }
	private static void updateFileIntoDB(String sTyp, String sID) { upsertFileHibernate(sTyp, sID); }

	
	//###################################################################################################################################################

	private static void deleteFileFromDB(String sTyp, String sId) {
	    Transaction tx = null;
	    try (Session s = getSessionFactoryDb2().openSession()) {
	        tx = s.beginTransaction();
	        FileStore fs = s.find(FileStore.class, sId);
	        if (fs != null) {
	            // nur die gewählte Spalte auf null setzen, Rest behalten
	            setNameAndData(fs, sTyp, null, null);
	            s.merge(fs);
	        }
	        tx.commit();
	    } catch (Exception ex) {
	        if (tx != null && tx.getStatus().canRollback()) try { tx.rollback(); } catch (Exception ignore) {}
	        logger.error("deleteFileFromDB fehlgeschlagen (typ={}, id={})", sTyp, sId, ex);
	    }
	}

	
	//###################################################################################################################################################

	private static String exportFileHibernate(String typ, String id, String targetDirOrNull) throws IOException {
	    // Zielpfad bestimmen
	    String dir = targetDirOrNull != null ? targetDirOrNull : choosePath(Einstellungen.getAppSettings().work);
	    if (dir == null || dir.equals(getNotSelected())) return getNotSelected();
	    Path outDir = Paths.get(dir);

	    try (Session s = getSessionFactoryDb2().openSession()) {
	        FileStore fs = s.find(FileStore.class, id);
	        if (fs == null) return getNotSelected();

	        String fileName = getName(fs, typ);
	        byte[] data = getData(fs, typ);

	        if (data == null || data.length == 0) return getNotSelected();
	        if (fileName == null || fileName.isBlank()) fileName = (typ + "-" + id + ".bin");

	        Files.createDirectories(outDir);
	        Path out = outDir.resolve(fileName);
	        Files.write(out, data);
	        return out.toString();
	    }
	}

	// alte Aufrufer ersetzen:
	private static String extractFileFromDB(String sTyp, String sId) throws IOException {
	    return exportFileHibernate(sTyp, sId, null);
	}
	private static String extractFileForMail(String sTyp, String sId, String sPath) throws IOException {
	    return exportFileHibernate(sTyp, sId, sPath);
	}

	//###################################################################################################################################################
	// Getter und Setter
	//###################################################################################################################################################

	private static void setNameAndData(FileStore fs, String typ, String name, byte[] data) {
	    switch (typ) {
	        case "AN" -> { fs.setAnFileName(name); fs.setAnPdfFile(data); }
	        case "AB" -> { fs.setAbFileName(name); fs.setAbPdfFile(data); }
	        case "BE" -> { fs.setBeFileName(name); fs.setBePdfFile(data); }
	        case "LS" -> { fs.setLsFileName(name); fs.setLsPdfFile(data); }
	        case "RE" -> { fs.setReFileName(name); fs.setRePdfFile(data); }
	        case "ZE" -> { fs.setZeFileName(name); fs.setZePdfFile(data); }
	        case "M1" -> { fs.setM1FileName(name); fs.setM1PdfFile(data); }
	        case "M2" -> { fs.setM2FileName(name); fs.setM2PdfFile(data); }
	        case "01" -> { fs.setAddFileName01(name); fs.setAddFile01(data); }
	        case "02" -> { fs.setAddFileName02(name); fs.setAddFile02(data); }
	        case "03" -> { fs.setAddFileName03(name); fs.setAddFile03(data); }
	        default -> throw new IllegalArgumentException("Unbekannter Typ: " + typ);
	    }
	}

	private static String getName(FileStore fs, String typ) {
	    return switch (typ) {
	        case "AN" -> fs.getAnFileName();
	        case "AB" -> fs.getAbFileName();
	        case "BE" -> fs.getBeFileName();
	        case "LS" -> fs.getLsFileName();
	        case "RE" -> fs.getReFileName();
	        case "ZE" -> fs.getZeFileName();
	        case "M1" -> fs.getM1FileName();
	        case "M2" -> fs.getM2FileName();
	        case "01" -> fs.getAddFileName01();
	        case "02" -> fs.getAddFileName02();
	        case "03" -> fs.getAddFileName03();
	        default -> null;
	    };
	}

	private static byte[] getData(FileStore fs, String typ) {
	    return switch (typ) {
	        case "AN" -> fs.getAnPdfFile();
	        case "AB" -> fs.getAbPdfFile();
	        case "BE" -> fs.getBePdfFile();
	        case "LS" -> fs.getLsPdfFile();
	        case "RE" -> fs.getRePdfFile();
	        case "ZE" -> fs.getZePdfFile();
	        case "M1" -> fs.getM1PdfFile();
	        case "M2" -> fs.getM2PdfFile();
	        case "01" -> fs.getAddFile01();
	        case "02" -> fs.getAddFile02();
	        case "03" -> fs.getAddFile03();
	        default -> null;
	    };
	}

}
