package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Listener per la gestione degli eventi associati alla pressione dei pulsanti per la
 * connessione al server
 */
public class ConnectionListener implements ActionListener {
	protected static final String CONNETTI = "1";
	protected static final String DISCONNETTI = "2";
	protected static final String MOSTRA = "3";
	
	
	public void actionPerformed(ActionEvent ae) {
		String com=ae.getActionCommand();
		if (com == CONNETTI) {
			Finestra.connessione();
		}
		else if (com == DISCONNETTI) {
			Finestra.disconnessione();
			
		}
		else if (com == MOSTRA) {
			Finestra.connFrame.setVisible(true);
		}
	}
}
