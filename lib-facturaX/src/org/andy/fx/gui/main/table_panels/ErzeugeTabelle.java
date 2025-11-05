package org.andy.fx.gui.main.table_panels;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

public class ErzeugeTabelle<renderer> extends JScrollPane {
	
	// Serialisierungs-ID für die Klasse
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	
	//###################################################################################################################################################
	// public Teil
	//###################################################################################################################################################
	
    public ErzeugeTabelle(Object[][] rowData, Object[] columnNames, TableCellRenderer renderer) {
        table = new JTable(rowData, columnNames);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setRowSelectionAllowed(false);
        table.setDefaultEditor(Object.class, null);
        table.setDefaultRenderer(Object.class, renderer);
        // Weitere Einstellungen...

        setViewportView(table);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
    
	//###################################################################################################################################################
	// private Teil
	//###################################################################################################################################################
    
    public void setColumnWidths(int[] widths) {
    	TableColumnModel columnModel = table.getColumnModel();
    	for (int i = 0; i < widths.length; i++) {
    	    columnModel.getColumn(i).setPreferredWidth(widths[i]);
    	}
	}
    
    //###################################################################################################################################################
  	// Getter und Setter
  	//###################################################################################################################################################

    public JTable getTable() {
        return table;
    }
}
	
