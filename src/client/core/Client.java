package client.core;

import java.net.*;
import java.io.*;

import javax.swing.JOptionPane;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import client.gui.*;

/*
 * Classe che si occupa della connessione con il server e contiene i metodi per la comunicazione
 * con esso
 */
public class Client extends Thread {
	private static Socket s = null;
	
	public void run() {
		boolean flag = true;
		try {
			String ip = Connessione.getIp();
			String p = Connessione.getPort();
			String hostname = InetAddress.getLocalHost().getHostName();
			int port = Integer.parseInt(p);
			s = new Socket(ip, port);

			String messaggio = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintStream ps = new PrintStream(s.getOutputStream());
			String risposta = br.readLine();
			if (risposta.equals("NO_CONN")) {
				chiudi(-5);
				System.exit(0);
			}
			String pathSeparator = File.separator;
			
			ps.println(hostname);
			ps.flush();
			ps.println(System.getProperty("os.name"));
			ps.flush();
			ps.println(System.getProperty("os.version"));
			ps.flush();
			ps.println(pathSeparator);
			ps.flush();
			
			/* in attesa di comandi dal server */
			while (true && flag) {
					messaggio = br.readLine();
					System.out.println("Comando ricevuto: " + messaggio);
					
					if (messaggio!= null && messaggio.equals("list-cart-iniz")) {
						String[] cart = Finestra.getCartelle();
						for (int i = 0; i < cart.length; i++) {
							ps.println(cart[i]);
							ps.flush();
						}
						ps.println("EOF");
						ps.flush();
						br.readLine();
						ps.println("OK");
						ps.flush();
					}
					else if (messaggio!= null && messaggio.equals("lista_file")) {
						String cartella=br.readLine();
						File f= new File(cartella);
						if (!f.exists()) {
							ps.println("LISTF_NO");
							ps.flush();
						}
						else {	
							ps.println("LISTF_YES");
							ps.flush();
							File[] ar = f.listFiles();
							for (int i = 0; ar != null && i < ar.length; i++)
								if (!ar[i].isDirectory()) {
									ps.println(ar[i]);
									ps.flush();
								}
							ps.println("EOF");
							ps.flush();
							br.readLine();
						}
					}
					else if (messaggio!= null && messaggio.equals("lista_cartelle")) {
						String cartella = br.readLine();
						File f = new File(cartella);
						if (!f.exists()) {
							ps.println("LISTC_NO");
							ps.flush();
						}
						else {
							ps.println("LISTC_YES");
							ps.flush();
							File[] ar=f.listFiles();
							for (int i = 0; ar != null && i < ar.length; i++)
								if (ar[i].isDirectory()) {
									ps.println(ar[i]);
									ps.flush();
								}
							ps.println("EOF");
							ps.flush();
							br.readLine();
						}
					}
					else if (messaggio!= null && messaggio.equals("download-file")) {
						String url = br.readLine();
						if (inviaFile(url)) {
							ps.println("DOW-OK");
							ps.flush();
						}
						else {
							ps.println("DOW-NO");
						    ps.flush();
						}
					}
					else if (messaggio!= null && messaggio.equals("upload-file")) {
						String url = br.readLine();
						File f = new File(url);
						if (!f.exists()) {
							ps.println("UPL-NO");
							ps.flush();
						}
						else if (riceviFile(url)) {
							ps.println("UPL-OK");
							ps.flush();
						}
						else {
							ps.println("UPL-NO");
						    ps.flush();
						}
					}
					else if (messaggio!= null && messaggio.equals("elimina")) {
						String url = br.readLine();
						File f = new File(url);
						if (!f.exists()) {
							ps.println("DEL_NO");
							ps.flush();
						}
						else if (f.isFile()){
							if (f.delete())
								ps.println("DEL_OK");
							else ps.println("DEL_NO");
							ps.flush();
						}
						else {
							if (eliminaCartella(url))
								ps.println("DEL_OK");
							else ps.println("DEL_NO");
							ps.flush();
						}
					}
					else if (messaggio!= null && messaggio.equals("rinomina")) {
						String url = br.readLine();
						String new_name = br.readLine();
						if(rinomina(url, new_name))
							ps.println("REN_OK");
						else ps.println("REN_NO");
						ps.flush();
					}
					else if (messaggio!= null && messaggio.equals("proprieta")) {
						String url = br.readLine();
						File f = new File(url);
						
						if (!f.exists()) {
							ps.println("PROP_NO");
							ps.flush();
						}
						else {
							if (f.isDirectory()) {
								//proprietaCartella(url);
								//ps.println("PROPC_OK");
								//ps.flush();
								
								long[] x = contaOggetti(f);
								ps.println(x[0]);
								ps.flush();
								ps.println(x[1]);
								ps.flush();
								ps.println(f.lastModified());
								ps.flush();
							}
							else {
								//proprietaFile(url);
								//ps.println("PROPF_OK");
								//ps.flush();
								ps.println(f.length());
								ps.flush();
								ps.println(f.lastModified());
								ps.flush();
							}
						}
						
					}
					else if (messaggio!= null && messaggio.equals("proprieta-all")) {
						proprietaUtente();
						br.readLine();
					}
					else if (messaggio!= null && messaggio.equals("crea-cartella")) {
						String path = br.readLine();
						File f = new File(path);
						if(f.mkdir())
							ps.println("CRE_OK");
						else ps.println("CRE_NO");
						ps.flush();
					}
					else if (messaggio!= null && messaggio.equals("cartella-valida")) {
						String url = br.readLine();
						if (valida(url))
							ps.println("VAL_OK");
						else ps.println("VAL_NO");
						ps.flush();
						br.readLine();
					}
					else if (messaggio!= null) {
						System.out.println("Comando non trovato: "+messaggio);
					}
					else {
						flag = false;
						chiudi(-4);
					}
				}			
		}
		 catch (SocketException se) {
			 flag = false;
			 chiudi(-1);

		 }
		 catch (UnknownHostException uhe) {
			 flag = false;
			 chiudi(-2);
		 }
		 catch (IOException ioe) {
			 flag = false;
			 chiudi(-3);
		 }
	}
	
	/* Metodo che elimina una cartella e tutto il suo contenuto dal disco */
	public static boolean eliminaCartella(String url) {
		boolean res = true;
		File cartella = new File(url); 
		String[] lista = cartella.list(); 
		if (lista.length == 0) {
			return cartella.delete();
		}
		File f = null;
		for(int i = 0; i < lista.length; i++) { 
			f = new File(cartella, lista[i]); 
			if (f.isDirectory()) {
				String filePath = f.getPath(); 
				res = eliminaCartella(filePath); 
			}
			else res = f.delete();
		}
		res = cartella.delete();
		return res;
	}
	
	/* Metodo che invia al server un file, che sarà poi scaricato dal richiedente tramite HTTP */
	public static boolean inviaFile(String url) {	
		try{
			File myFile = null;
			File f = new File (url);
			if (f.isDirectory()) {
				try { 
					ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(url+".zip")); 
					zipDir(url, zos, finePath(url)); 
					zos.close(); 
					url+=".zip";
					myFile = new File(url);
				} 
				catch(IOException ioe) { 
					return false;
				} 
			}
			else {
				myFile = f;
			}
		    FileInputStream fis = new FileInputStream(myFile);
		    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
		    BufferedInputStream bis = new BufferedInputStream(fis);
		    byte [] mybytearray  = new byte [(int)myFile.length()];
		    bis.read(mybytearray, 0, mybytearray.length);
		    DataOutputStream dos = new DataOutputStream(s.getOutputStream());
		    dos.writeInt(mybytearray.length);
		    dos.flush();
		    
		    dos.write(mybytearray, 0, mybytearray.length);
		    dos.flush();
		    
		    if (f.isDirectory()) {
		    	myFile.delete();
		    }
		    br.readLine();
		    return true;
		 }
		 catch (SocketException se) {
			 return false;
		 }
		 catch(IOException ioe)  {
			 return false;
		 }
	}
	
	/* Metodo che riceve un file dal server e lo aggiunge nel proprio file system */
	public static boolean riceviFile(String path) {
		try {			
			int sizeblock = 1024;
		    int bytesLetti;
		    int current = 0;
		    DataInputStream d = new DataInputStream(s.getInputStream());
		    int filesize = d.readInt();
		    FileOutputStream fos = new FileOutputStream(path);
		    BufferedOutputStream bos = new BufferedOutputStream(fos);	    
		    byte [] mybytearray  = new byte [filesize+sizeblock];
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
		    return true;
		}
		catch(IOException ioe) {
			return false;
		}
	}
	
	/* Metodo che invia al server le proprietà di un file */
	public static void proprietaFile(String path) {
		try {
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			File f = new File(path);
			dos.writeLong(f.length());
			dos.flush();
			dos.writeLong(f.lastModified());
			dos.flush();

		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/* Metodo che invia al server le proprietà del client */
	public static void proprietaUtente() {
		try{
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			String[] cart = Finestra.getCartelle();
			long[] count = new long[3];
			long[] temp = new long[2];
			count[0] = 2;
			for (int i = 0; i < cart.length; i++) {
				temp = contaOggetti(new File(cart[i]));
				count[1] += temp[0];
				count[2] += temp[1];
			}
			dos.writeLong(count[0]);
			dos.flush();
			dos.writeLong(count[1]);
			dos.flush();
			dos.writeLong(count[2]);
			dos.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/* Metodo che invia al server le proprietà di una cartella */
	public static void proprietaCartella(String path) {
		try {
			System.out.println("Porpr. cartella lato client");
			DataOutputStream dos = new DataOutputStream(s.getOutputStream());
			File f = new File(path);
			
			long[] x = contaOggetti(f);
			System.out.println("Scrivo primo");
			dos.writeLong(x[0]);
			dos.flush();
			System.out.println("Scrivo secondo");
			dos.writeLong(x[1]);
			dos.flush();
			System.out.println("Scrivo terzo");
			dos.writeLong(f.lastModified());
			dos.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	/* Metodo di supporto per i metodi che recuperano le proprietà, conta numero di oggetti
	 * (e loro dimensione) di una cartella 
	 */
	public static long[] contaOggetti(File f) {
		long[] n = new long[2];
		if (!f.isDirectory()) {
			n[0] = 1;
			n[1] = f.length();
		}
		else {
			String[] list = f.list();
			for (int i = 0; i < list.length; i++) {
				File f1 = new File(f.getAbsolutePath() + "/" + list[i]);
				if (!f1.isDirectory()) {
					n[0]++;
					n[1] += f1.length();
				}
				else {
					n[0] += 1 + contaOggetti(f1)[0];
					n[1] += contaOggetti(f1)[1];
				}
			}
			return n;
		}
		return null;
	}
	
	/* Metodo che rinomina una risorsa nel file system */
	public static boolean rinomina(String path, String newname) {
		boolean errore = true;
		File f;
		File f2 = new File(newname);
		if ((f = new File(path)) == null)
			errore = false;
		else {
			if(f2.exists())
				errore = false;
			else if (!f.renameTo(f2))
				errore = false;
		}
		return errore;
	}
	
	/* Metodo chiamato a seguito di una disconnessione, che visualizza l'origine dell'errore */
	public static void chiudi(int error) {
		String message;
		switch(error) {
			case -1: message = "Si è verificato un errore durante il tentativo di accesso alla socket (SocketException)";
				break;
			case -2: message = "Hostname sconosciuto o indirizzo ip non valido (UnknownHostException)";
				break;
			case -3: message = "Si è verificato un errore in lettura o scrittura sulla socket (IOException)";
				break;
			case -4: message = "La connessione si è interrotta, il server potrebbe essere non più raggiungibile";
				break;
			case -5: message = "Il server è già connesso con il numero massimo di client, riprovare più tardi";
				break;
			default: message = "Si è verificato un errore";
				break;
		}
		Finestra.disconnessione();
		JOptionPane.showMessageDialog(null, message, "ERRORE DI CONNESSIONE", JOptionPane.ERROR_MESSAGE);
	}
	
	/* Metodo che controlla la validità di una risorsa */
	public static boolean valida(String path) {
		String[] cart = Finestra.getCartelle();
		for (int i = 0; i < cart.length; i++)
			if (path.startsWith(cart[i]) || path.concat("/").startsWith(cart[i]))
				return true;
		return false;
	}
	
	/* Metodo che crea un nuovo archivio zip con il contenuto di una cartella */
	public static boolean zipDir(String dir2zip, ZipOutputStream zos, String base) { 
		try { 
			File zipDir = new File(dir2zip); 
			String[] lista = zipDir.list(); 
			byte[] readBuffer = new byte[1024]; 
			int bytesIn = 0;
			File f = null;
			FileInputStream fis = null;
			int length = lista.length;
			if (length == 0)
				return false;
			
			for(int i = 0; i < length; i++) { 
				f = new File(zipDir, lista[i]); 
				if(f.isDirectory()) { 
			        String filePath = f.getPath(); 
			        zipDir(filePath, zos, base); 
			        continue; 
				}
				else {
					fis = new FileInputStream(f); 
					ZipEntry entry = new ZipEntry(parse(f.getPath(), base)); 
					zos.putNextEntry(entry); 
					while((bytesIn = fis.read(readBuffer)) != -1) {
						zos.write(readBuffer, 0, bytesIn); 
					} 
					fis.close();
				}
			}
			return true;
		} 
		catch(IOException ioe)	{ 
			return false;
		} 
	}
	
	public static String finePath(String path) {
		String ris = "";
		int l = path.length();
		for (int i = l-1; i >=0; i--)
			if (path.charAt(i) == '/') {
				ris = path.substring(i+1);
				break;
			}
		return ris;		
	}
	
	/* Metodo di supporto per il metodo zipDir */
	public static String parse(String path, String start) {
		int l1 = path.length();
		int l = start.length();

		for (int i = 0; i < l1-l+1; i++)
			if (path.substring(i, i+l).equals(start))
				return path.substring(i);
		return null;
	}
}
