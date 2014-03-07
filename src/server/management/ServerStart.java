package server.management;

import javax.servlet.*;

/*
 * Classe avviata allo startup di JBoss
 */
public class ServerStart implements ServletContextListener{
	
    public void contextInitialized(ServletContextEvent se) {
         ServletContext application = se.getServletContext();
         String path = application.getRealPath("/");
         System.out.println(path);
         ServerThread server = ServerThread.getInstance(); //creiamo la prima istanza di ServerThread
         server.setCartellaDeploy(path);
         server.start();
    }
   
    public void contextDestroyed(ServletContextEvent arg0) {
    	
    }
}
