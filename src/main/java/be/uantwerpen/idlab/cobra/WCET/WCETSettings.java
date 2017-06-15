package be.uantwerpen.idlab.cobra.WCET;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;


public class WCETSettings extends JFrame{
	private FlowLayout layout;
	private JFileChooser filechooser;
	private JButton open;

	
	public WCETSettings(){
		super("Cobra-WCET");
		layout = new FlowLayout();
		setLayout(layout);
		
		
		filechooser = new JFileChooser();
		filechooser.setCurrentDirectory(new java.io.File("."));
		filechooser.setDialogTitle("AAAA");
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		
		open = new JButton("Open");
		open.addActionListener(new ActionListener(){
			private int returnvalue;
			public void actionPerformed(ActionEvent event) {
				returnvalue = filechooser.showOpenDialog(null);
				
				if(returnvalue == JFileChooser.APPROVE_OPTION ){
					System.out.println(filechooser.getSelectedFile().getAbsolutePath());	
				}
				
			}
			
		});
		
		add(open,BorderLayout.CENTER);
	
		

	}







}
