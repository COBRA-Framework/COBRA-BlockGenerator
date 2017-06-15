package be.uantwerpen.idlab.cobra.WCET;

import javax.swing.JFrame;

class WCET{
	public static void main(String[] args){
		
		WCETSettingBox AVR = new WCETSettingBox();
		AVR.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		AVR.setSize(500,600);
		AVR.setVisible(true);
		
		WCETSettings f =new WCETSettings();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(200,300);
		f.setVisible(true);
		
	}
	
	
}
