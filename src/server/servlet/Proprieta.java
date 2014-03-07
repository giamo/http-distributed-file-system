package server.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import server.management.ServerThread;

/* 
 * Servlet chiamata alla richiesta di visualizzazione delle proprietà di una risorsa
 */
public class Proprieta extends HttpServlet {
	private static final long serialVersionUID = 3L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Inizio proprietà");
		boolean errore = false;
		ServletContext context = getServletContext();
		ServerThread ser = ServerThread.getInstance();
		String comando = (String)request.getParameter("comando");
		int id = Integer.parseInt((String)request.getParameter("id"));
		context.setAttribute("host-id", Integer.toString(id));
		String cartella_attuale = (String)request.getParameter("posizione");
		String target = (String)request.getParameter("target");
		String tipo = (String)request.getParameter("tipo");
		System.out.println("id: " + id + " - target: " + target + " - tipo: " + tipo);
		String ps = null;

		if (ser.getThread(id) != null)
			ps = ser.getThread(id).getPathSeparator();
		else errore = true;
		System.out.println("Entering");
		if (tipo.equals("utente")) {
			String[] os = new String[3];
			long[] dati = new long[3];
			if (ser.getThread(id) != null) {
				os[0] = ser.getThread(id).getHostname();
				os[1] = ser.getThread(id).getIP();
				os[2] = ser.getThread(id).getOS();
				dati = ser.getThread(id).proprietaUtente();
				context.setAttribute("dati", dati);
				context.setAttribute("os", os);
			}
			else errore = true;
		}
		else if (tipo.equals("cartella")) {
			System.out.println("Enter propr. cartella");
			long[] dati = new long[2];
			if (ser.getThread(id) != null) {
				try {
					dati = ser.getThread(id).proprietaCartella(target);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context.setAttribute("dati", dati);
			}
			else errore = true;
			System.out.println("Out propr. cartella");
		}
		else if (tipo.equals("file")){
			long[] dati = new long[2];
			if (ser.getThread(id) != null) {
				try {
					dati = ser.getThread(id).proprietaFile(target);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				context.setAttribute("dati", dati);
			}
			else errore = true;
		}
		
		String address = "/Dfilesystem/index.jsp";
		context.setAttribute("target", target);
		context.setAttribute("tipo", tipo);
		context.setAttribute("path-sep", ps);
		context.setAttribute("cartella-attuale", cartella_attuale);
		
		if (!errore) {
			context.setAttribute("comando", comando);
			context.setAttribute("error", "0");
		}
		else {
			context.setAttribute("comando", "browse");
			context.setAttribute("error", "1");
		}

		response.sendRedirect(address);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
