package client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

/*
 * Listener che gestisce la pressione dei pulsanti di aggiunta e rimozione di cartelle dalla
 * condivisione nella finestra principale
 */
public class SfogliaListener implements ActionListener{
	private String s;
	public static final String AGGIUNGI = "1";
	public static final String ELIMINA = "2";

	public void actionPerformed(ActionEvent ae) {
		JFileChooser chooser = new JFileChooser();
		JFrame frame=new JFrame();
		Object m = ae.getActionCommand();
		
		if (m == AGGIUNGI) {
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			
			/* determina quale pulsante è stato premuto per chiudere la finestra*/
			int result = chooser.showOpenDialog(frame);

			switch (result) {
				case JFileChooser.APPROVE_OPTION:
					/* è stato premuto OPEN */
					
					s = chooser.getSelectedFile().getAbsolutePath();
					Finestra.aggiungiElemento(s);
		          	break;
		          	
				case JFileChooser.CANCEL_OPTION:
					/* è stato premuto CANCEL o l'icona di chiusura della finestra */
		         
					break;
				
				case JFileChooser.ERROR_OPTION:
					/* il processo di selezione è terminato con un errore */
		          
					break;
			}
		}
		if (m == ELIMINA) {
			int i = Finestra.getList().getSelectedIndex();
			if (i >= 0)
				Finestra.eliminaElemento(i);
		}
	}
}
