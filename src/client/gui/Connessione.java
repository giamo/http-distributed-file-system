package client.gui;

import java.awt.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 * Finestra che permette di connettersi al server 
 */
public class Connessione extends JFrame {
	private static final long serialVersionUID = 1L;
	private static final JLabel s1=new JLabel("Indirizzo ip server:");
	private static final JLabel s2=new JLabel("Porta:");
	private static final JButton connetti = new JButton("Connetti");
	private static final ConnectionListener cListener = new ConnectionListener();
	protected static TextField t1=new TextField(15);
	protected static TextField t2=new TextField(5);
	
	public Connessione () {
		super("Connetti");
		connetti.addActionListener(cListener);
		connetti.setActionCommand(ConnectionListener.CONNETTI);
		connetti.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent event){ 
				setVisible(false);
			}
		});

		t1.setText("localhost");
		t2.setText("5000");
		add(s1);
		add(t1);
		add(s2);
		add(t2);
		add(connetti);
		getContentPane().setLayout(new FlowLayout());
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 100);
		setResizable(false);

		setLocation(500, 350);
		setVisible(false);
	}
	
	public static String getIp() {
		return t1.getText();
	}
	
	public static String getPort() {
		return t2.getText();
	}
}