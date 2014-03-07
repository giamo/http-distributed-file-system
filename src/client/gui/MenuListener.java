package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
	
/* 
 * Listener che gestisce gli eventi legati alla pressione di tasti del menu
 */
public class MenuListener implements ActionListener {
	public static final String ESCI = "1";
	public static final String ABOUT = "2";

	public void actionPerformed(ActionEvent ev) {
		Object m = ev.getActionCommand();
		if (m == ESCI) {
			int ret = JOptionPane.showConfirmDialog (null, "Sei sicuro di voler uscire?", "Chiusura applicazione", JOptionPane.YES_NO_OPTION);
            if (ret == JOptionPane.YES_OPTION)
                System.exit (0);
		}
		if(m == ABOUT) JOptionPane.showMessageDialog(null, "Università degli Studi di Roma \"La Sapienza\"\n" +
				"Progetto di laurea triennale in Ingegneria Informatica, A.A. 2009/2010\n" +
				"Amori Gianluca", "HTTP Distributed File System - Client", JOptionPane.PLAIN_MESSAGE);
	}
}