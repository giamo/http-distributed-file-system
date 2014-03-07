<!-- prova.jsp -->
<html>
<head>

<%@ page import="java.net.*, java.io.*, java.util.*, java.util.Date, server.util.Util, server.management.ServerThread, server.management.MyThread"%>

<% String target = (String)application.getAttribute("target");
String tipo = (String)application.getAttribute("tipo");
ServerThread ser = ServerThread.getInstance();
int id = Integer.parseInt((String)application.getAttribute("host-id"));
String pathSeparator = (String)application.getAttribute("path-sep");
long[] dati = (long[])application.getAttribute("dati");
String[] os = (String[])application.getAttribute("os");
Date last_modified = null;

if (tipo.equals("cartella")) {
	last_modified = new Date(dati[2]);
}
else if (tipo.equals("file")) {
	last_modified = new Date(dati[1]);
}

if ((dati == null && os == null || dati.length == 0))
	out.println("A problem occurred while visualizing the file/folder properties");
else { %>

<%if (target != null) {%>
<div align='center'><b><font size='5'>&laquo <%=Util.finePath(target, pathSeparator)%> &raquo PROPERTIES</font></b></div><br/><br/>
<b><i>Full path on the remote host's file system:</i></b> <%=target%> <br/>
<%}
else {%>
<div align='center'><b><font size='5'>USER PROPERTIES </font></b></div><br/><br/>
<%}
if (tipo.equals("utente")) {%>
<b><i>Hostname:</i></b> <%=os[0]%> <br/>
<b><i>IP address:</i></b> <%=os[1]%> <br/>
<b><i>Operating system:</i></b> <%=os[2]%> <br/>
<b><i>Number of folders:</i></b> <%=dati[0]%> <br/>
<b><i>Total number of objects:</i></b> <%=dati[1]%> <br/>
<b><i>Total size:</i></b> <%=Util.formatSize(dati[2])%> (<%=dati[2]%> bytes) <br/>
<%}
else if (tipo.equals("cartella")) {%>
<b><i>Type:</i></b> folder <br/>
<b><i>Number of objects:</i></b> <%=dati[0]%> <br/>
<b><i>Total size:</i></b> <%=Util.formatSize(dati[1])%> (<%=dati[1]%> bytes) <br/>
<b><i>Last modified:</i></b> <%=last_modified%> <br/>
<%}
else if (tipo.equals("file")) {%>
<b><i>Type:</i></b> file <br/>
<b><i>Size:</i></b> <%=Util.formatSize(dati[0])%> (<%=dati[0]%> bytes) <br/>
<b><i>Last modified:</i></b> <%=last_modified%> <br/>
<%}
}%>
<br/><br/>
<div align="center"> <a href="" onClick="window.close()"> Close window </a> </div>
</body>
</html>

