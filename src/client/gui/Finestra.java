package client.gui;

import client.gui.SfogliaListener;

import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

import client.core.Client;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;

/* 
 * Finestra principale dell'applicativo client, che permette di selezionare le cartelle da
 * mandare in condivisione
 */
public class Finestra extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final JMenuBar mb=new JMenuBar();
	private static final JMenuItem esci=new JMenuItem("Esci");
	private static final JMenuItem connect=new JMenuItem("Connetti");
	private static final JMenuItem about=new JMenuItem("About");
	private static final JMenu file=new JMenu("File");
	private static final JMenu info=new JMenu("Info");
	private static final MenuListener ml=new MenuListener();
	private static final JButton connetti = new JButton("Connetti!");
	private static final ConnectionListener cListener = new ConnectionListener();
	private static final SfogliaListener sListener = new SfogliaListener();
	protected static JTextArea textarea = new JTextArea(50, 100);
	private static final JButton aggiungi=new JButton("Aggiungi cartella");
	private static final JButton elimina=new JButton("Rimuovi cartella selezionata");
	private static final JPanel north = new JPanel();
	private static final JPanel center = new JPanel();
	private static final JPanel south = new JPanel();
	private static final JPanel east = new JPanel();
	private static final JPanel cnorth=new JPanel();
	private static final JPanel ccenter=new JPanel();
	private static final JPanel csouth=new JPanel();
	private static final JPanel cwest=new JPanel();
	private static final JPanel ceast=new JPanel();
	protected static TextField t5=new TextField(2);
	private static DefaultListModel modello = new DefaultListModel();
	private static final JList lista = new JList(modello);
	private static final JLabel label1 = new JLabel("Attualmente non sei connesso. Scegli le cartelle da condividere e connettiti");
	protected static JFrame connFrame = new JFrame();
	
	public Finestra() {
		super("HTTP Distributed File System - Client");

		connFrame = new client.gui.Connessione();
		file.add(connect);
		file.add(esci);
		info.add(about);
		mb.add(file);
		mb.add(info);
		
		connetti.addActionListener(cListener);
		connetti.setActionCommand(ConnectionListener.MOSTRA);
		lista.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		aggiungi.addActionListener(sListener);
		aggiungi.setActionCommand(SfogliaListener.AGGIUNGI);
		elimina.addActionListener(sListener);
		elimina.setActionCommand(SfogliaListener.ELIMINA);
		elimina.setEnabled(false);
		north.add(label1);
		south.add(connetti);
		lista.addListSelectionListener(new ListSelectionListener() {
		      public void valueChanged(ListSelectionEvent evt) {
		        if (!evt.getValueIsAdjusting())
		        
                    elimina.setEnabled(true);
		      }
		    });
		JScrollPane jsp = new JScrollPane(lista);
		jsp.setPreferredSize(new Dimension(400, 250));
		cwest.add(jsp);
		ceast.add(aggiungi);
		ceast.add(elimina);
		
		esci.addActionListener(ml);
		esci.setActionCommand(MenuListener.ESCI);
		about.addActionListener(ml);
		about.setActionCommand(MenuListener.ABOUT);
		
		center.add(cnorth, BorderLayout.NORTH);
		center.add(ccenter, BorderLayout.CENTER);
		center.add(cwest, BorderLayout.WEST);
		center.add(ceast, BorderLayout.EAST);
		center.add(csouth, BorderLayout.SOUTH);
		ceast.setLayout(new GridLayout(2, 1));
		
		getContentPane().add (north, BorderLayout.NORTH);
		getContentPane().add (center, BorderLayout.CENTER);
		getContentPane().add (south, BorderLayout.SOUTH);
		getContentPane().add(east, BorderLayout.EAST);
		
		this.setJMenuBar(mb);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setSize(750, 450);
		setResizable(false);
		setLocation(300, 130);
		setVisible(true);
		addWindowListener (new WindowAdapter(){
			public void windowClosing (WindowEvent e) {
	            int ret = JOptionPane.showConfirmDialog (null, "Sei sicuro di voler uscire?", "Chiusura applicazione", JOptionPane.YES_NO_OPTION);
	            if (ret == JOptionPane.YES_OPTION)
	                System.exit (0);
	        }
		});
	}
	
	/* Lancia la classe client avviando la connessione */
	public static void connessione() {
			new Client().start();
			connetti.setVisible(false);
			connect.setEnabled(false);
			label1.setText("Sei connesso! Le cartelle sono in condivisione con il server (" + Connessione.getIp() + ")");	
	}
	
	public static void disconnessione() {
		connetti.setVisible(true);
		connect.setEnabled(true);
		label1.setText("Attualmente non sei connesso. Scegli le cartelle da condividere e connettiti");

	}
	
	public static JList getList() {
		return lista;
	}
	
	public static String[] getCartelle() {
		int size = modello.getSize();
		String[] c = new String[size];
		for (int i = 0; i < size; i++)
			c[i] = (String)modello.getElementAt(i);
		
		return c;
	}
	
	public static void aggiungiElemento(String s) {
		boolean flag = (elimina.isEnabled()) ? true : false; 
		modello.addElement(s);
		if (!flag)
			elimina.setEnabled(false);
			
	}
	
	public static void eliminaElemento(int i) {
		modello.remove(i);
		elimina.setEnabled(false);
	}

}
