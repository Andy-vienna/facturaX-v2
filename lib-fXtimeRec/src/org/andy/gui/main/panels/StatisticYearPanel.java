package org.andy.gui.main.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.andy.code.dataStructure.entity.TimeAccount;
import org.andy.code.dataStructure.entity.WorkTimeSheet;
import org.andy.code.dataStructure.repository.TimeAccountRepository;
import org.andy.code.dataStructure.repository.WorkTimeSheetRepository;
import org.andy.code.misc.BD;
import org.andy.gui.misc.MarkerProgressBar;

public class StatisticYearPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private final Font font = new Font("Tahoma", Font.BOLD, 14);
    private final Color titleColor = Color.BLUE;
    
    private final TimeAccountRepository taRepo = new TimeAccountRepository();
    private final WorkTimeSheetRepository tsRepo = new WorkTimeSheetRepository();
    private TimeAccount ta = null;
    private List<WorkTimeSheet> ts = null;
    
    private int year; private String user;
    private BigDecimal[] hM = new BigDecimal[12];

	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
    
	public StatisticYearPanel(int year, String user) {
		setLayout(null);
		this.year = year; this.user = user;
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Mehrstunden-Übersicht für " + year
        );
        border.setTitleFont(font);
        border.setTitleColor(titleColor);
        border.setTitleJustification(TitledBorder.LEFT);
        border.setTitlePosition(TitledBorder.TOP);
        setBorder(border);
        
        ta = new TimeAccount();
        ta = taRepo.findByUserAndYear(user, year);
        if (ta == null) {
        	TimeAccount h = new TimeAccount();
        	h.setTiPrinted(0);
        	h.setUserName(user);
        	h.setContractHours(BD.ZERO);
        	h.setOverTime(BD.ZERO);
        	h.setYear(year);
        	taRepo.save(h);
        }
        hM = getOvertimePerMonth();
        
        buildPanel(hM);
	}
	
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
	private void buildPanel(BigDecimal[] val) {
		
		String[] lbl = { "Jan", "Feb", "Mar", "Apr", "Mai", "Jun", "Jul", "Aug", "Sep", "Okt", "Nov", "Dez" };
		JLabel[] lblTxt = new JLabel[lbl.length];
		MarkerProgressBar[] proBar = new MarkerProgressBar[12];
		JTextField[] txt = new JTextField[lbl.length];
		
		for (int i = 0; i < proBar.length; i++) {
			lblTxt[i] = new JLabel(lbl[i]);
			lblTxt[i].setHorizontalAlignment(SwingConstants.CENTER);
			lblTxt[i].setBounds(10 + (i * 45), 20, 40, 25);
			
			proBar[i] = new MarkerProgressBar();
			proBar[i].setOrientation(SwingConstants.VERTICAL);
			proBar[i].setMinimum(-100);
			proBar[i].setMaximum(150);
			proBar[i].setBounds(10 + (i * 45), 50, 40, 200);
			proBar[i].setValue(val[i].intValue());
			proBar[i].setToolTipText(val[i].toString());
			
			txt[i] = new JTextField();
			txt[i].setFont(new Font("Tahoma", Font.BOLD, 10));
			if (i % 2 == 0) {
				txt[i].setBounds(5 + (i * 45), 250, 50, 25);
			} else {
				txt[i].setBounds(5 + (i * 45), 275, 50, 25);
			}
			txt[i].setOpaque(true); // Macht den Hintergrund sichtbar
			txt[i].setHorizontalAlignment(SwingConstants.CENTER);
			txt[i].setText(val[i].setScale(1, RoundingMode.HALF_UP).toString());
			
			if (val[i].intValue() < 0) {
				proBar[i].setMarkerColor(Color.RED);
				txt[i].setForeground(Color.RED);
			} else {
				proBar[i].setMarkerColor(Color.GREEN);
				txt[i].setForeground(Color.BLACK);
			}
			
			add(lblTxt[i]);
			add(proBar[i]);
			add(txt[i]);
		}
		
		setPreferredSize(new Dimension(555, 300));
	}
	
	//###################################################################################################################################################
	// Hilfsmethoden
	//###################################################################################################################################################
	
	private BigDecimal[] getOvertimePerMonth() {
		BigDecimal[] tmp = new BigDecimal[12];
		ts = tsRepo.findByUserYear(user, year);
		
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = BD.ZERO;
		}
		for (int n = 0; n < ts.size(); n++) {
    		WorkTimeSheet wts = ts.get(n);
    		tmp[wts.getMonat() - 1] = wts.getOvertime();
    	}
		return tmp;
	}
	
}
