<html>
<head>
<title>Error 2</title>
</head>
<body>
<%@ page import="java.net.*, java.io.*, java.util.*, server.management.ServerThread, server.management.MyThread, server.util.Util"%>

<% String carte = (String)application.getAttribute("cartella-attuale");
ServerThread ser = ServerThread.getInstance();
int id = Integer.parseInt((String)application.getAttribute("host-id"));%>

<b> The selected operation wasn't successful. </b>
</br>
</br>
Make sure the operation is legal and try again soon.<br/><br/>
<a href="server/servlet/RichiestaCartella?cartella=<%=carte%>&id=<%=id%>"> <img src="icons/arrow-left.png" title="indietro" align="absmiddle" border="0"/></a>&nbsp&nbsp
<a href="index.jsp"> <img src="icons/home.png" title="home" align="absmiddle" border="0"/> </a><br/><br/>
</body>
</html>
