package server.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.management.ServerThread;
import server.util.Util;

/*
 * Servlet chiamata alla richiesta di creazione, eliminazione o rinominazione di risorse
 */
public class ModificaCartella extends HttpServlet {
	private static final long serialVersionUID = 2L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean errore = false;
		ServletContext context = getServletContext();
		ServerThread ser = ServerThread.getInstance();
		int id = Integer.parseInt((String)request.getParameter("id"));
		String cartella_richiesta = (String)request.getParameter("cartella");
		String comando = (String)request.getParameter("comando");
		String newname = (String)request.getParameter("nome");
		String ps = null;
		boolean res = true;
		if (ser.getThread(id) != null)
			ps = ser.getThread(id).getPathSeparator();
		else errore = true;
		String genitore = Util.inizioPath(cartella_richiesta, ps);
		String address = "/Dfilesystem/server/servlet/RichiestaCartella?comando=browse&cartella=" + genitore + "&id=" + id;	
		
		if (comando.equals("elimina")) {
			if (ser.getThread(id) != null)
				res = ser.getThread(id).elimina(cartella_richiesta);
			else errore = true;
		}
		else if (comando.equals("rinomina")){
			if (ser.getThread(id) != null)
				res = ser.getThread(id).rinomina(cartella_richiesta, newname);
			else errore = true;
		}
		else if (comando.equals("crea")){
			if (ser.getThread(id) != null) {
				res = ser.getThread(id).crea(cartella_richiesta, newname);
			}
			else errore = true;
		}
		else errore = true;
		
		if (!errore) {
			if (res)
				//response.sendRedirect(address);
				context.setAttribute("error", "0");
			else //response.sendRedirect("/Dfilesystem/error2.jsp");
				address += "&error=2";
		}
		else //response.sendRedirect("/Dfilesystem/error.html");
			context.setAttribute("error", "1");
		
		response.sendRedirect(address);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
