<html>
<head>
<title>HTTP DISTRIBUTED FILE SYSTEM</title>
<script src="/Dfilesystem/import/utils.js" type="text/javascript"></script> 
<link rel="stylesheet" href="/Dfilesystem/import/style.css" />
</head>

<body>
<%@ page import="java.net.*, java.io.*, java.util.*"%>
<%@ page import="server.management.ServerThread, server.management.MyThread, server.util.Util"%>

<% 
String carte = (String)application.getAttribute("cartella-attuale");
ServerThread ser = ServerThread.getInstance();
MyThread[] clients = ser.getThreads();
String host_id = (String)application.getAttribute("host-id");
int id;
boolean iniziale = false;
if (host_id != null && !host_id.equals("null"))
	id = Integer.parseInt(host_id);
else {
	id = -1;
	iniziale = true;
}
LinkedList<String> c = (LinkedList<String>)application.getAttribute("cartelle");
LinkedList<String> f = (LinkedList<String>)application.getAttribute("files");
String pathSeparator = (String)application.getAttribute("path-sep");
String comando = (String)application.getAttribute("comando");
String error = (String)application.getAttribute("error");
if (error == null)
	error = "0";
String prec = Util.inizioPath(carte, pathSeparator);
int ind, count = 0, elem;
LinkedList<String> pathElements = new LinkedList<String>();

boolean property = false;
String target = null, tipo = null;
long[] dati = null;
String[] os = null;
Date last_modified = null;

if (comando != null && comando.equals("proprieta")) {
	property = true;
	target = (String)application.getAttribute("target");
	tipo = (String)application.getAttribute("tipo");
	dati = (long[])application.getAttribute("dati");
	os = (String[])application.getAttribute("os");
	last_modified = null;
}
%>

<div id="hover" onclick="close_property()"> </div>
	<div id="property-box">
		<div id="close" onclick="close_property()">X</div> 
		<%
		if (property) {
			if (tipo.equals("cartella"))
				last_modified = new Date(dati[2]);
			else if (tipo.equals("file"))
				last_modified = new Date(dati[1]);

			if ((dati == null && os == null || dati.length == 0))
				out.println("An error occured while visualizing the file/folder properties");
			else { 
				if (target != null) {%>
					<div id="property-bar" class="bar"><center> &laquo <%=Util.finePath(target, pathSeparator)%> &raquo properties</center></div>
					<b><i>Percorso completo sull'host di origine:</i></b> <%=target%> <br/>
				<%}
				else {%>
					<div align='center'><b><font size='5'> USER PROPERTIES </font></b></div><br/><br/>
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
			}
		}
		%>
</div> 

<div id="container">

	<div id="title-bar">
		<img src="icons/logo.png" align="absmiddle"/>&nbsp <font size="6"><b>HTTP DISTRIBUTED FILE SYSTEM</b></font>
	</div>
	<br/><br/>
	<div id="client-list">
		<div id="client-list-bar" class="bar">
			<center>Clients connected</center>
		</div>
		<% 
		for (ind = 0; ind < clients.length; ind++) {
			if (clients[ind] != null) {
				count++;
		%>
				<!--<img src="icons/star.png" align="absmiddle">-->
				<a href="server/servlet/RichiestaCartella?comando=browse&cartella=lista-iniziale&id=<%=ind%>"<% application.setAttribute("cartella-attuale", "home");%> > <center><%=clients[ind].getHostname()%></center></a>
				<!--&nbsp &nbsp(<a href="" onclick="window.open('server/servlet/Proprieta?id=<%=ind%>&tipo=utente', 'Proprieta', 'width=700, height=300');"><img src="icons/gear.png" title="propriet&agrave" border="0" align="absmiddle"></a>)--> <br/>
			<%
			} 
		}
		if (count == 0)
			out.println("<div class='text-italic'>No client connected</div>");%>
	</div>
	<div id="content">
		<div id="path-and-options">
			<%
			if (!iniziale) {
				if (ser.getThread(id) != null) {
					out.println("<div id='path'><u>"+clients[id].getHostname()+"</u>");
					if (carte.equals("lista-iniziale")) {
						out.println("</div>");
					}
					else {
						pathElements = server.util.Util.deconstructPath(server.util.Util.formatDirectory(carte, pathSeparator), pathSeparator);
						elem = pathElements.size()-1;
						while (ser.getThread(id) != null && ser.getThread(id).cartellaValida(pathElements.get(elem)))
							elem--;
							
						String[] sp = pathElements.get(elem).split(pathSeparator);
						String[] sp2;
						if (pathElements.get(pathElements.size()-1).startsWith(pathSeparator))
							sp2 = pathElements.get(pathElements.size()-1).substring(1).split(pathSeparator);
						else sp2 = pathElements.get(pathElements.size()-1).split(pathSeparator);
						
						boolean write = false;
						for (int i = 0; i < sp2.length; i++) {
							if (write && i != sp2.length-1)
								out.println(" > <a href='server/servlet/RichiestaCartella?comando=browse&cartella=" + URLEncoder.encode(pathElements.get(i), "UTF-8") + "&id=" + id + "'>" + sp2[i] + "</a>");
							else if (write)
								out.println(" > <b>" + sp2[i] + "</b>");
								
							if (sp2[i].equals(sp[sp.length-1]))
								write = true;
						}
						
						out.println("</div>");
					}
				}
				
				if (error.equals("2")) {%>
				<div id="error2" style="color:red">Impossible to fulfill the request, make sure the operation is legal</div>
				<%}
			}				
			%>
		</div>
		<table border="0" cellspacing="0" style="width:100%">
		<div id="content-bar" class="bar">
			<tr bgcolor="#ffce38">
				<td style="border-top-left-radius:10px; border-bottom-left-radius:10px; width:60%; font-weight:bold"><span id="content-bar-name">Name</span></td>
				<td style="width:20%; text-align:center; font-weight:bold"><span id="content-bar-type">Type</span></td>
				<td style="border-top-right-radius:10px; border-bottom-right-radius:10px; width:20%; text-align:center; font-weight:bold"><span id="content-bar-actions">Actions</span></td>
			</tr>
		</div>
		<div id="content-elements">
			
			<%
			
			if (error.equals("1")) {
				out.println("</table>");
				out.println("<div class='text-italic' style='padding-top:1em; color:red'>The requested resource is not available at the moment.");
				out.println("The remote host could have removed the resource from the sharing, or there may be connection problems</div>");
			}
			else if ((f == null || f.size() == 0) && (c == null || c.size() == 0)) {
				out.println("</table>");
				out.println("<div class='text-italic' style='padding-top:1em'>The folder is empty</div>");
			}
			else {
				if (c != null) {
					for(int i = 0; i < c.size(); i++) {
					%>
						<tr>
						<td style="margin-top: 0.7em"><img src="icons/folder.png" align="absmiddle"/> <a href="server/servlet/RichiestaCartella?comando=browse&cartella=<%=URLEncoder.encode(c.get(i), "UTF-8")%>&id=<%=id%>"> <%= Util.finePath(c.get(i), pathSeparator)%></a></td>
						<td style="margin-top: 0.7em; text-align:center"><div >Folder</div></td>
						<td style="margin-top: 0.7em; padding-top: 0.5em; vertical-alignment:middle">
							
								<form action="dummyvalue">
								<select name="newurl" onchange="menu_goto(this.form)">
								<option label="select" value="" selected="selected">--Select an action--</option>
								<option label="download" value="DownloadFile?comando=download&fileurl=<%=URLEncoder.encode(c.get(i), "UTF-8")%>&id=<%=id%>&tipo=cartella">Download</option>
								<option label="delete" value="ModificaCartella?comando=elimina&cartella=<%=URLEncoder.encode(c.get(i), "UTF-8")%>&id=<%=id%>">Delete</option>
								<option label="rename" value="ModificaCartella?comando=rinomina&cartella=<%=URLEncoder.encode(c.get(i), "UTF-8")%>&id=<%=id%>&nome=">Rename</option>
								<option label="property" value="Proprieta?comando=proprieta&posizione=<%=URLEncoder.encode(carte, "UTF-8")%>&target=<%=URLEncoder.encode(c.get(i), "UTF-8")%>&id=<%=id%>&tipo=cartella">Properties</option>
								</select>
								</form>
					
						</td></tr>
						<!--<td width="5%"><a href="server/servlet/DownloadFile?fileurl=<%=c.get(i)%>&id=<%=id%>&tipo=cartella" onClick="return alert(mostraAttesa('caricamento'));"> <img src="icons/arrow-down.png" title="download" align="absmiddle" border="0"/></a></td>-->
						<%
						if(!iniziale) {
						%>
						<!--	<td width="5%"><a href="server/servlet/ModificaCartella?cartella=<%=c.get(i)%>&id=<%=id%>&comando=elimina" onClick="return confirm('Sei sicuro di voler eliminare la cartella <%=Util.finePath(c.get(i), pathSeparator)%>?');"> <img src="icons/delete.png" title="elimina" align="absmiddle" border="0"/></a></td>-->
						<%
						}
						%>
						<%
						if(!iniziale) {
						%>
						<!--	<td width="5%"><a href="" onClick="var nome = prompt('Inserisci il nuovo nome'); if (!(nome == null)){ href='server/servlet/ModificaCartella?cartella=<%=c.get(i)%>&id=<%=id%>&comando=rinomina&nome='+nome; }"><img src="icons/pencil.png" title="rinomina" align="absmiddle" border="0"/></a></td>-->
						<%
						}
						%>
						<!--<td width="5%"><a href="" onclick="window.open('server/servlet/Proprieta?target=<%=c.get(i)%>&id=<%=id%>&tipo=cartella', 'Proprieta', 'width=700, height=300');"><img src="icons/gear.png" title="propriet&agrave" align="absmiddle" border="0"/></a></td></tr>-->
					<%
					}
				}
				
				if (f != null) {
					for(int i = 0; i< f.size(); i++) {
					%>
						<tr>
						<td style="padding-top: 0.7em"><img src="icons/file.png" align="absmiddle"/> <%= Util.finePath(f.get(i), pathSeparator)%></td>
						<td style="padding-top: 0.7em; text-align:center"><div >File</div></td>
						<td style="padding-top: 0.7em; text-align:center">
							<form id="actionform" action="dummyvalue">
								<select name="newurl" onchange="menu_goto(this.form)">
								<option label="select" value="" selected="selected">--Select an action--</option>
								<option label="download" value="DownloadFile?fileurl=<%=URLEncoder.encode(f.get(i), "UTF-8")%>&id=<%=id%>&tipo=file">Download</option>
								<option label="delete" value="ModificaCartella?cartella=<%=URLEncoder.encode(f.get(i), "UTF-8")%>&id=<%=id%>&comando=elimina">Delete</option>
								<option label="rename" value="ModificaCartella?cartella=<%=URLEncoder.encode(f.get(i), "UTF-8")%>&id=<%=id%>&comando=rinomina&nome=">Rename</option>
								<option label="property" value="Proprieta?comando=proprieta&posizione=<%=URLEncoder.encode(carte, "UTF-8")%>&target=<%=URLEncoder.encode(f.get(i), "UTF-8")%>&id=<%=id%>&tipo=file">Properties</option>
								</select>
							</form>
						</td></tr>
						<!--<td width="5%"><a href="server/servlet/" onClick="return "><img src="icons/arrow-down.png" title="download" align="absmiddle" border="0"/></a></td>
						<td width="5%"><a href="server/servlet/" <img src="icons/delete.png" title="elimina" align="absmiddle" border="0"/></a></td>
						<td width="5%"><a href="" onClick="var nome = prompt('Inserisci il nuovo nome'); if (!(nome == null)){ href='server/servlet/"><img src="icons/pencil.png" title="rinomina" align="absmiddle" border="0"/></a></td>
						<td width="5%"><a href="" onclick="window.open('server/servlet/Proprieta?target=<%=f.get(i)%>&id=<%=id%>&tipo=file', 'Proprieta', 'width=700, height=300');"><img src="icons/gear.png" title="propriet&agrave" align="absmiddle" border="0"/></a><br/></td></tr>-->
					<% 
					}
				}
			}
			out.println("</table>");
			%>
		</div>
	</div>
</div>

<%if (property) {%>
<script type="text/javascript">
	open_property();
</script>
<%}%>
</body>

</html>
