package server.management;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.io.*;
import java.util.*;
import server.util.Util;

/* 
 * Classe che rappresenta il client connesso, con le sue proprietà e i metodi per 
 * comunicare con esso
 */
public class MyThread extends Thread {
	private static ServerThread ser;
	private Socket sock;
	private int id;
	private String hostname;
	private String ip;
	private String osname;
	private String osversion;
	private String pathSeparator;
	
	public MyThread(ServerThread se, Socket s,  int i) {
		ser = se;
		sock = s;
		id = i;
		ip = s.getInetAddress().toString();
	}
	
	public void run() {
		try{
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			hostname = br.readLine();
			osname = br.readLine();
			osversion = br.readLine();
			pathSeparator = br.readLine();
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.run()");
			ioe.printStackTrace();
		}
	}
	
	public int getID() {
		return id;
	}
	
	public String getOS() {
		return osname + " " + osversion;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public String getIP() {
		return ip;
	}
	
	public String getPathSeparator() {
		return pathSeparator;
	}
	
	public boolean isFolder(String path) {
		return true;
	}
	
	/* Riceve la lista delle cartelle in condivisione sul client */
	public LinkedList<String> getCartelle() {
		LinkedList<String> l = new LinkedList<String>();
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("list-cart-iniz");
			ps.flush();
			String p;
			while ((p = br.readLine()) != null  && !p.equals("EOF")) {
				l.add(p);
			}
			ps.println("OK");
			ps.flush();
			if ((br.readLine()) == null) {
				ser.rimuoviClient(id);
				return null;
			}
			return l;
		}
		catch (IOException ioe) {
			System.out.println("IOException in MyThread.getCartelle()");
			ser.rimuoviClient(id);
			return null;
		}
		
	}
	
	/* Riceve e salva sul server un file, a seguito di una richiesta di download */
	public boolean riceviFile(String path, String tipo) {
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			ps.println("download-file");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			int sizeblock = 1;
		    int bytesLetti;
		    int current = 0;
		    
		    DataInputStream d = new DataInputStream(sock.getInputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		    int filesize = d.readInt();
		    FileOutputStream fos;

		    if (tipo.equals("cartella"))	
		    	fos = new FileOutputStream(ser.getCartellaDeploy()+"/download/"+Util.finePath(path, pathSeparator)+".zip");
		    else fos = new FileOutputStream(ser.getCartellaDeploy()+"/download/"+Util.finePath(path, pathSeparator));
		    BufferedOutputStream bos = new BufferedOutputStream(fos);

		    byte [] mybytearray  = new byte [filesize + sizeblock];
		    current = 0;

		    do {
		       bytesLetti = d.read(mybytearray, current, sizeblock);
		       if (bytesLetti >= 0) 
		    	   current += bytesLetti;
		    } 
		    while(bytesLetti > -1 && current < filesize);
		    
		    bos.write(mybytearray, 0 , current);
			bos.flush();
		    bos.close();
		    fos.close();
		    ps.println("OK");
		   	ps.flush();
		    String res = br.readLine();
		    if (res != null && res.equals("DOW-OK"))
		    	return true;
		    else return false;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.riceviFile()");
			return false;
		}
	}
	
	/* Invia un file al client a seguito di una richiesta di upload */
	public boolean inviaFile(String path, String name) {	
		try{
			File myFile=new File(ser.getCartellaDeploy()+"/upload/"+ name);
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("upload-file");
			ps.flush();
			ps.println(path+name);
			ps.flush();
			FileInputStream fis = new FileInputStream(myFile);
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    byte [] mybytearray  = new byte [(int)myFile.length()];
		    bis.read(mybytearray, 0, mybytearray.length);
		    DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
		    dos.writeInt(mybytearray.length);
		    dos.flush();
		    dos.write(mybytearray, 0, mybytearray.length);
		    dos.flush();
		    String res = br.readLine();
		    if (res.equals("UPL-OK"))
		    	return true;
		    else return false;
		}
		catch (SocketException se) {
			System.out.println("SocketException in MyThread.invioFile()");
			return false;
		 }
		catch(IOException ioe)  {
			System.out.println("IOException in MyThread.invioFile()");
			return false;
		}
	}
	
	/* Recupera le sottocartelle contenute in una cartella */
	public LinkedList<String> visualizzaCartelle(String path) {
		LinkedList<String> ris = new LinkedList<String>();
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));

			ps.println("lista_cartelle");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			String list;
			if ((list = br.readLine()).equals("LISTC_NO"))
				return null;
			while ((list = br.readLine()) != null  && !list.equals("EOF"))
				ris.add(list);
			
			ps.println("OK");
			ps.flush();
			
			ris = ordina(ris);
		}
		catch(InterruptedIOException iioe) {
			System.out.println("InterruptedException in MyThread.visuaizzaCartelle()");
			return null;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.visualizzaCartelle()");
			return null;
		}
		
		return ris;
	}
	
	/* Recupera i file contenuti in una cartella */
	public LinkedList<String> visualizzaFiles(String path) {
		LinkedList<String> ris = new LinkedList<String>();
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("lista_file");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			String list;
			if ((list = br.readLine()).equals("LISTF_NO"))
				return null;
			
			while ((list = br.readLine()) != null  && !list.equals("EOF"))
				ris.add(list);
			
			ps.println("OK");
			ps.flush();
			ris = ordina(ris);
			return ris;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.visualizzaFiles()");
			return null;
		}
	}
	
	/* Invia il comando per l'eliminazione di una risorsa */
	public boolean elimina(String path) {
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("elimina");
			ps.flush();
			ps.println(path);
			ps.flush();
			String res = br.readLine();
			if (res.equals("DEL_NO"))
				return false;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.elimina()");
			return false;
		}
		return true;
	}
	
	/* Invia il comando per la rinominazione di una risorsa */
	public boolean rinomina(String path, String nome) {
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("rinomina");
			ps.flush();
			ps.println(path);
			ps.flush();
			ps.println(Util.inizioPath(path, pathSeparator)+"/"+nome);
			ps.flush();
			String res = br.readLine();
			if (res.equals("REN_NO"))
				return false;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.rinomina()");
			return false;
		}
		return true;
	}
	
	/* Invia il comando per la creazione di una cartella */
	public boolean crea(String path, String name) {
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			String total_path = path + "/" + name;
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("crea-cartella");
			ps.flush();
			ps.println(total_path);
			ps.flush();
			String res = br.readLine();
			if (res.equals("CRE_NO"))
				return false;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.crea()");
			return false;
		}
		return true;
	}
	
	/* Recupera le proprietà del client connesso */
	public long[] proprietaUtente() {
		long[] ris = new long[3];
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			DataInputStream d = new DataInputStream(sock.getInputStream());
			ps.println("proprieta-all");
			ps.flush();
			
			for (int i = 0; i < 3; i++)
				ris[i] = d.readLong();
			
			ps.println("OK");
			ps.flush();
			
			return ris;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.proprietaUtente()");
			return null;
		}
	}
	
	/* Recupera le proprietà di una cartella */
	public long[] proprietaCartella(String path) throws InterruptedException {
		long[] ris = new long[3];
		try {
			/*System.out.println("proprietaCartella");
			DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
			DataInputStream d = new DataInputStream(sock.getInputStream());
						dos.writeBytes("proprieta\r\n");
			dos.flush();
			dos.writeBytes(path+"\r\n");
			dos.flush();
			
			System.out.println("Lettura dati");
			for (int i = 0; i < 3; i++) {
				ris[i] = d.readLong();
				System.out.println(ris[i]);//Thread.sleep(100);
			}
			
			System.out.println("Invio 'ok'");
			dos.writeBytes("OK\r\n");
			dos.flush();
			
			return ris;*/
			
			PrintStream ps = new PrintStream(sock.getOutputStream());
			DataInputStream d = new DataInputStream(sock.getInputStream());
			ps.println("proprieta");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			for (int i = 0; i < 3; i++) {
				ris[i] = d.readLong();
				System.out.println(ris[i]);//Thread.sleep(100);
			}
			
			return ris;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.proprietaCartella()");
			return null;
		}
	}
	
	/* Recupera le proprietà di un file */
	public long[] proprietaFile(String path) throws InterruptedException {
		long[] ris = new long[2];
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			DataInputStream d = new DataInputStream(sock.getInputStream());
			ps.println("proprieta");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			for (int i = 0; i < 2; i++) {
				ris[i] = d.readLong();
				System.out.println(ris[i]);//Thread.sleep(100);
			}
			
			return ris;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.proprietaFile()");
			return null;
		}
	}
	
	/* Invia la richiesta di controllo della validità di una risorsa */
	public boolean cartellaValida(String path) {
		boolean res = false;
		try {
			PrintStream ps = new PrintStream(sock.getOutputStream());
			BufferedReader br = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			ps.println("cartella-valida");
			ps.flush();
			ps.println(path);
			ps.flush();
			
			String list = br.readLine();
			if (list != null && list.equals("VAL_OK"))
				res = true;
			else res = false;
			
			ps.println("OK");
			ps.flush();
			
			return res;
		}
		catch(IOException ioe) {
			System.out.println("IOException in MyThread.cartellaValida()");
			return false;
		}
	}
	
	/* Data una lista di stringhe, ritorna una lista con le stesse stringhe ordinate in 
	 * ordine alfabetico 
	 */
	public static LinkedList<String> ordina(LinkedList<String> lista){
		String[] ord= lista.toArray(new String[0]);
		LinkedList<String> ris = new LinkedList<String>();
		Arrays.sort(ord);
		for (int i=0; i<ord.length; i++)
		   ris.add(ord[i]);
		return ris;
	}
}
