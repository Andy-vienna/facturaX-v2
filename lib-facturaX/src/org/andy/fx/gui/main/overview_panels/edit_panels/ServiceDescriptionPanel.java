package org.andy.fx.gui.main.overview_panels.edit_panels;

import static org.andy.fx.gui.misc.CreateButton.createButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.andy.fx.gui.iconHandler.ButtonIcon;

public class ServiceDescriptionPanel extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JPanel editorHost = new JPanel(new BorderLayout(5,0));
	
	private JTextField headLine = new JTextField();
	private JTextArea textBlock = new JTextArea();
	
	private String txtHeadline; private String txtTextblock;
	private String content;
	
    public ServiceDescriptionPanel() {
    	
    	super("Leistungsbeschreibungs Editor");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1500, 800);
        setLocationRelativeTo(null);
        
        editorHost.setBorder(new EmptyBorder(0, 5, 5, 5));
        editorHost.add(headLine, BorderLayout.NORTH);
        editorHost.add(textBlock, BorderLayout.CENTER);
        editorHost.setVisible(true);
        
        add(editorHost, BorderLayout.CENTER);
        
        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton btnGetHtml = createButton("<html>OK</html>", ButtonIcon.OK.icon(), null);
        btnGetHtml.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnGetHtml.setPreferredSize(new Dimension(130, 50));
        btnGetHtml.setEnabled(true);
        south.add(btnGetHtml);
        add(south, BorderLayout.SOUTH);
        
        btnGetHtml.addActionListener(_ -> {
        	txtHeadline = headLine.getText();
            txtTextblock = textBlock.getText();
            content = txtHeadline + "~" + txtTextblock;
            
            dispose(); // EDT -> ok
        });
    	
    }
    
    //###################################################################################################################################################
  	// Getter und Setter
  	//###################################################################################################################################################

  	public void setText(String content) {
  		String[] teile = content.split("~");
  		this.headLine.setText(teile[0]);
  		this.textBlock.setText(teile[1]);
	}

	public String getText() {
	    return content;
	}
	  
	public void setStartText(String headLine) {
		this.headLine.setText(headLine);
	}
}

