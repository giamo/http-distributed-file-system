package server.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import server.management.ServerThread;
 
/* Servlet chiamata alla richiesta di upload di un file */
public class UploadFile extends HttpServlet {
	private static final long serialVersionUID = 5L;
	private static final String temp_dir = "/tmp";
	private File temp;
	private static final String destination_dir ="/upload";
	private File destinazione;
 
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		temp = new File(temp_dir);
		if(!temp.isDirectory()) {
			throw new ServletException(temp_dir + " non è una cartella");
		}
		String realPath = getServletContext().getRealPath(destination_dir);
		destinazione = new File(realPath);
		if(!destinazione.isDirectory()) {
			throw new ServletException(destination_dir + " non è una cartella");
		}
 
	}
 
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean errore = false;
		ServerThread ser = ServerThread.getInstance();
		int id = Integer.parseInt((String)request.getParameter("id"));
		String path = (String)request.getParameter("path");
 
		DiskFileItemFactory  fileItemFactory = new DiskFileItemFactory ();
		fileItemFactory.setSizeThreshold(1024*1024);
		fileItemFactory.setRepository(temp);
 
		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			List<FileItem> items = uploadHandler.parseRequest(request);
			Iterator<FileItem> itr = items.iterator();
			String name = "";
			boolean res = true;
			while(itr.hasNext()) {
				FileItem item = itr.next();
				if(!item.isFormField()) {
					File file = new File(destinazione,item.getName());
					item.write(file);
					name = item.getName();
				}
			}
			
			if (ser.getThread(id) != null)
				res = ser.getThread(id).inviaFile(path + "/" , name);
			else errore = true;
			
			String address = "/Dfilesystem/server/servlet/RichiestaCartella?cartella="+path+"&id="+id;
			if (!errore) {
				if (res)
					response.sendRedirect(address);
				else response.sendRedirect("/Dfilesystem/error2.jsp");
			}
			else response.sendRedirect("/Dfilesystem/error.html");
			
			
		}
		catch(FileUploadException ex) {
			System.out.println("FileUploadException nella servlet UploadFile");
			response.sendRedirect("/Dfilesystem/error.html");
		} 
		catch(Exception ex) {
			System.out.println("Exception nella servlet UploadFile");
			response.sendRedirect("/Dfilesystem/error.html");
		}
	}
}