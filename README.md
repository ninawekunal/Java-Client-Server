# Java-JDBC-Swings-and-Socket
## Final Project for the course CS-GY-9053 Java Programming

**Concepts used:**
* JDBC connection using SQLite3 database.
* Socket programming.
* Multithreading.
* Object Oriented concepts.
* Serialization.
* Swings and event handling for UI.
* Collections.

**Objectives:**
* The project is to build a client/server system that reads data from a DB into an object and sends the object to the server. 
* The server then writes that data into its DB. 
* There are two databases namely client and server containing a people table. I am using SQLite3 as the database.
* These two databases should interact with each other using networking.
* There are overall two main java files: *ClientInterface and ServerInterface*
* One instance of ServerInterface should be able to handle multiple client interface. (A multithreaded Server)
* Client should be able to load the database(*client.db*), open a *socket* connection to the server and send multiple *objects* across using the same connection.
* Server should be able to handle multiple clients using multithreading and receieve objects from all these clients over the socket connection and then enter those objects into its database and then view them in the text area.


**ClientInterface:**
* The Client UI has a menu that allows you to Exit and open a database, presumably the client.db database. 
* The JFileChooser is sufficient for selecting it, seen in the OpenDBListener inner class in ClientInterface.java
* The dropdown box is empty (with one entry, “Empty”) when not connected to the database. When it is connection to the database, it should be populated with all names whose value of “sent” in the People table is “0” (or false. Sqlite does not support Boolean types explicitly).
* The “open connection” button should open a connection to the server. The Server location should be “localhost” and the port should default to “8001.” The “Close Connection” button should close the connection. 
* You should be able to Open the connection, send data, close the connection, re-open the connection, and send data again without any errors.
* “Send Data” should get the entry in the people table that corresponds to what is selected in the dropdown box, create a Person object, and sent the person object to the server.
* If the client is not connected to the server, then you may raise an error, but the client should not terminate or otherwise fail to continue working.
* “Query DB Data” should show the current Contents of the People table in the text area, with Row names.

**ServerInterface:**
* The server should connect to server.db on startup and indicate such. 
* It should be listening on port 8001. 
* There should be a File menu with an Exit menu item that quits the application.
* The “Query DB” button should show the contents of the People table. 
* Any messages you want (status messages, indications that data has been received, success status, etc.) can appear in the JTextArea, and be appended over time. 
* You probably want to use a JScrollPane for this so that you can scroll through the messages.
* The server will run a thread that listens for connections. When it receives a connection from a client, it should spawn a thread to handle that connection from the client. i.e. A server should be multi-threaded and handle multiple clients.

**Note:** We are not handling concurrency issues in the database when multiple clients access the same data.
