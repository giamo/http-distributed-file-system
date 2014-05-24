HTTP Distributed File System
=============================

This application allows to visualize and access the file system resources shared by the hosts in the network. The allowed operations on files and folders include manipulation, properties visualization and download/upload through HTTP.

Server side:
- a multithread server communicates with client programs and handles concurrent accesses to the resources
- a servlet/JSP web interface communicates with the server and allows users to request and access information through a web browser

Client side: java application realized using the Java Swing graphical interface which is to be executed to connect to the server and register the machine to start sharing file system resources.

This was the final project for my Bachelor degree in Computer Engineering at Sapienza University of Rome (academic year 2009/2010).


How to install & run
--------------------

From command line type:

- _ant war_ to generate the web application WAR file (in the build/ folder)

- _ant jar_ to generate the jar client file (in the build/ folder)

To deploy the application, copy the generated WAR file in your application server (Tomcat, JBoss, Glassfish, etc.) deploy folder.

Run the application client on the machines which participate to the HTTP file system and select the folders to be shared.
