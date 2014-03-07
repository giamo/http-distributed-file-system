package server.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import server.management.ServerThread;

/* 
 * Servlet chiamata alla richiesta di visualizzazione di una cartella 
 */
public class RichiestaCartella extends HttpServlet {
	private static final long serialVersionUID = 4L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean errore = false;
		ServletContext context = getServletContext();
		ServerThread ser = ServerThread.getInstance();
		String comando = (String)request.getParameter("comando");
		int id = Integer.parseInt((String)request.getParameter("id"));
		context.setAttribute("host-id", Integer.toString(id));
		String cartella_richiesta = (String)request.getParameter("cartella");
		String err = (String)request.getParameter("error");
		LinkedList<String> cart = new LinkedList<String>();
		LinkedList<String> files = new LinkedList<String>();
		String ps = null;
		if (ser.getThread(id) != null) {
			ps = ser.getThread(id).getPathSeparator();
			context.setAttribute("path-sep", ps);
		}
		else errore = true;
		
		if (cartella_richiesta.equals("lista-iniziale")) {
			if (ser.getThread(id) != null) {
				cart = ser.getThread(id).getCartelle();
				if (cart == null)
					errore = true;
			}
			else errore = true;

			context.removeAttribute("files");
		}
		else {
			if (ser.getThread(id) == null)
				errore = true;
			else if (ser.getThread(id).cartellaValida(cartella_richiesta)) {
				if (ser.getThread(id) != null)
					cart = ser.getThread(id).visualizzaCartelle(cartella_richiesta);
				else errore = true;
				if (ser.getThread(id) != null)
					files = ser.getThread(id).visualizzaFiles(cartella_richiesta);
				else errore = true;
				if (files.size() != 0)
					context.setAttribute("files", files);
				else context.removeAttribute("files");
			}
			else errore = true;
		}
		
		context.setAttribute("cartelle", cart);
		context.setAttribute("cartella-attuale", cartella_richiesta);
		context.setAttribute("comando", comando);
		String address = "/Dfilesystem/index.jsp";
		
		if (!errore) {
			if (err != null && err.equals("2"))
				context.setAttribute("error", "2");
			else context.setAttribute("error", "0");
		}
		else
			context.setAttribute("error", "1");
		
		response.sendRedirect(address);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
