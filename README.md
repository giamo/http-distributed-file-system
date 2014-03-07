HTTP Distributed File System
=============================

File system distributed over a network, built on top of HTTP and accessible through web browser.

This was the final project for my Bachelor degree in Computer Engineering at Sapienza University of Rome (academic year 2009/2010).


How to install & run
--------------------

From command line type:

- _ant war_ to generate the web application WAR file (in the build/ folder)

- _ant jar_ to generate the jar client file (in the build/ folder)

To deploy the application, copy the generated WAR file in your application server (Tomcat, JBoss, Glassfish, etc.) deploy folder.

Run the application client on the machines which participate to the HTTP file system and select the folders to be shared.
