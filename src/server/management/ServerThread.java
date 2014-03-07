package server.management;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.io.*;

/*
 * Classe che rappresenta il server centrale, che gestisce le connessioni die client
 */
public class ServerThread extends Thread {
	private static final int MAX_CONN = 100;
	private static final int port = 5000;
	private static MyThread[] threads = new MyThread[MAX_CONN];
	private static String cartella_deploy;
	private static ServerThread instance = null;
	
	private ServerThread() {
		
	}
	
	public void run() {
		try{
			Socket s;
			ServerSocket ss;
			ss = new ServerSocket(port);
			MyThread t;
			int id;
			PrintStream ps = null;
			
			/* All'arrivo delle connessioni dei client crea un nuovo thread e lo lancia */
			while(true) {
				s = ss.accept();
				ps = new PrintStream(s.getOutputStream());
				id = ottieniID();
				if (id != -1) {
					t = new MyThread(this, s, id);
					addThread(t, id);
					t.start();
					ps.print("YES_CONN\r\n");
					ps.flush();
				}
				else {
					ps.print("NO_CONN\r\n");
					ps.flush();
				}
			}
		}
		catch (SocketException se) {
			System.out.println("SocketException in ServetThread.run()");
			se.printStackTrace();
			System.exit(1);
		}
		catch (IOException ioe) {
			System.out.println("IOException in ServetThread.run()");
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
	
	public MyThread getThread(int i) {
		if (threads[i] != null)
			return threads[i];
		else return null;
	}
	
	public static MyThread[] getThreads() {
		return threads;
	}
	
	public static int ottieniID() {
		for (int i = 0; i < threads.length; i++)
			if (threads[i] == null) {
				return i;
			}
		return -1;
	}
	
	public static void addThread(MyThread t, int id) {
		threads[id] = t;
	}
	
	public void rimuoviClient(int id) {
		threads[id] = null;
	}	
	
	public String getCartellaDeploy() {
		return cartella_deploy;
	}
	
	public void setCartellaDeploy(String path) {
		cartella_deploy = path;
	}
	
	public static ServerThread getInstance() {
		if (instance == null)
			instance = new ServerThread();
		return instance;
	}
}
