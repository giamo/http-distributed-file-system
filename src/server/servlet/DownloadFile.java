package server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import server.management.ServerThread;
import server.util.Util;

/* 
 * Servlet chiamata alla richiesta di download di una risorsa 
 */
public class DownloadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse resp) throws ServletException, IOException {
		boolean errore = false;
		ServletContext context = getServletContext();
		ServerThread ser = ServerThread.getInstance();
		String comando = (String)request.getParameter("comando");
		int id = Integer.parseInt((String)request.getParameter("id"));
		String path_file = ((String)request.getParameter("fileurl"));
		String tipo = (String)request.getParameter("tipo");
		String ps = null;
		System.out.println(path_file);
		boolean res = true;
		if (ser.getThread(id) != null)
			ps = ser.getThread(id).getPathSeparator();
		else errore = true;

		if (ser.getThread(id) != null) {
			res = ser.getThread(id).riceviFile(path_file, tipo);
			System.out.println(res);
		}
		else errore = true;

		if (!errore) {
			if(res) {
				File f = new File(context.getRealPath("/download/"+ Util.finePath(path_file, ps)));
				int length = 0;
				ServletOutputStream op = resp.getOutputStream();
				String mimetype = context.getMimeType(path_file);

				resp.setContentType( (mimetype != null) ? mimetype : "application/octet-stream" );
				resp.setContentLength( (int)f.length() );
				resp.setHeader( "Content-Disposition", "attachment; filename=\"" + Util.finePath(path_file, ps) + "\"" );
				
				byte[] bbuf = new byte[1024];
				DataInputStream in = new DataInputStream(new FileInputStream(f));

				while ((in != null) && ((length = in.read(bbuf)) != -1))
					op.write(bbuf,0,length);

				in.close();
				op.flush();
				op.close();
			}
			else System.out.println("file non ricevuto");
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
