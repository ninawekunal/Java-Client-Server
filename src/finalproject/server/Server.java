package finalproject.server;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.Date;
import javax.swing.*;

import finalproject.db.DBInterface;
import finalproject.entities.Person;

public class Server extends JFrame implements Runnable {

	public static final int DEFAULT_PORT = 8001;
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	final int AREA_ROWS = 10;
	final int AREA_COLUMNS = 40;
	
	Connection conn;
	
	// Number a client
	private int clientNo = 0;
	
	JPanel topPanel, labelPanel, buttonPanel, textAreaPanel;
	JLabel DB, serverName;
	JButton queryDbButton;
	JTextArea textArea;
	JScrollPane scrollPane;
	JMenuBar menuBar;
	JMenu menu;
	JMenuItem exitItem;
	

	public Server() throws IOException, SQLException {
		this(DEFAULT_PORT, "server.db");
	}
	
	public Server(String dbFile) throws IOException, SQLException {
		this(DEFAULT_PORT, dbFile);
	}

	public Server(int port, String dbFile) throws IOException, SQLException {

		this.setSize(Server.FRAME_WIDTH, Server.FRAME_HEIGHT);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		setAllComponents();
		serverName.setText("<"+dbFile+">");
		
		DBInterface dbCon = new DBInterface(dbFile);
		dbCon.setConnection();
		this.conn = dbCon.getConn();
		
		
		Thread t = new Thread(this);
	    t.start();
	}
	
	public void setAllComponents() {
		
		this.setLayout(new BorderLayout());
		
		labelPanel = new JPanel(new FlowLayout());
		buttonPanel = new JPanel(new FlowLayout());
		topPanel = new JPanel(new BorderLayout());
		textAreaPanel = new JPanel(new BorderLayout());
		
		DB = new JLabel("DB: ");
		serverName = new JLabel("<none>");
		queryDbButton = new JButton("Query DB");
		
		queryDbButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				getPeopleInTextArea();
			}
		});
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		
		menuBar = new JMenuBar();
		menu = new JMenu("File");
		exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		
		menu.add(exitItem);
		menuBar.add(menu);
		
		this.setJMenuBar(menuBar);
		
		
		labelPanel.add(DB);
		labelPanel.add(serverName);
		buttonPanel.add(queryDbButton);
		topPanel.add(labelPanel, BorderLayout.NORTH);
		topPanel.add(buttonPanel, BorderLayout.CENTER);
		
		
		textAreaPanel.add(scrollPane, BorderLayout.CENTER);
		
		this.add(topPanel, BorderLayout.NORTH);
		this.add(textAreaPanel, BorderLayout.CENTER);
		
	}
	
	public void getPeopleInTextArea() {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people");
			ResultSet rset = stmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			int numColumns = rsmd.getColumnCount();
			System.out.println("numcolumns is "+ numColumns);
			
			String underline = "";
			String rowString = "";
			for (int i=1;i<=numColumns;i++) {
				underline +="___________";
				rowString += rsmd.getColumnName(i) + "\t";
			}
			rowString += "\n";
			rowString +=underline+"\n";
			while (rset.next()) {
				for (int i=1;i<=numColumns;i++) {
					Object o = rset.getObject(i);
					
					rowString += o.toString() + "\t";
				}
				rowString += "\n";
			}
			//System.out.print("rowString  is  " + rowString);
			textArea.append("\nResults:\n"+rowString);
		}
		catch(Exception e) {
			System.err.println("Error: "+e);
		}
		
	}
	
	


	
	public int insertPerson(Person person) {
		String sql = "INSERT INTO people(first, last, age, city, sent, id) VALUES(?, ?, ?, ?, 0, ?)";
		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, person.getFirstName());
			ps.setString(2, person.getLastName());
			ps.setString(3, person.getAge());
			ps.setString(4, person.getCity());
			ps.setString(5, person.getId());
			
			int res = ps.executeUpdate();
			return res;
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 0;
		
	}

	@Override
	  public void run() {
		  try {
	        // Create a server socket
	        ServerSocket serverSocket = new ServerSocket(DEFAULT_PORT);
	        textArea.append("Server started at " + DEFAULT_PORT + '\n');
	    
	        while (true) {
	          // Listen for a new connection request
	          Socket socket = serverSocket.accept();
	    
	          // Increment clientNo
	          clientNo++;
	          
	          textArea.append("Starting thread for client " + clientNo +
	              " at " + new Date() + '\n');

	            // Find the client's host name, and IP address
	            InetAddress inetAddress = socket.getInetAddress();
	            textArea.append("Client " + clientNo + "'s host name is "
	              + inetAddress.getHostName() + "\n");
	            textArea.append("Client " + clientNo + "'s IP Address is "
	              + inetAddress.getHostAddress() + "\n");
	          
	          // Create and start a new thread for the connection
	          new Thread(new HandleAClient(socket, clientNo)).start();
	        }
	      }
	      catch(IOException ex) {
	        System.err.println(ex);
	      }
		    
	  }
	  
	  // Define the thread class for handling new connection
	  class HandleAClient implements Runnable {
	    private Socket socket; // A connected socket
	    private int clientNum;
	    
	    /** Construct a thread */
	    public HandleAClient(Socket socket, int clientNum) {
	      this.socket = socket;
	      this.clientNum = clientNum;
	    }

	    ObjectInputStream inputFromClient;
	    DataOutputStream outputToClient;
	    
	    /** Run a thread */
	    public void run() {
	      try {
	        // Create data input and output streams
	    	inputFromClient = new ObjectInputStream(
	          socket.getInputStream());
	    	outputToClient = new DataOutputStream(
	          socket.getOutputStream());
	        
	        
	        // Continuously serve the client
	        while (true) {
	          // Receive radius from the client
	          Object object = inputFromClient.readObject();
	          int result = 0;
	          if(object instanceof Person) {
	        	  Person person = (Person) object;
	        	  result = insertPerson(person);
	        	  if(result>0) {
	        		  // successfully inserted data
	        		  outputToClient.writeInt(1);
	        		  textArea.append("\n Received data from client: "+clientNum);
	        		  textArea.append("\n Received data: "+person);
	        	  }
	        	  else {
	        		  // show error message
	        		  outputToClient.writeInt(0);
	        	  }
	          }
	          else {
	        	  // show error message
	        	  outputToClient.writeInt(0);
	          }
	          
	        }
	        
	      }
	      catch(IOException ex) {
	        ex.printStackTrace();
	        try {
				outputToClient.writeChars("Failed\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
	    	  try {
				outputToClient.writeChars("Failed\n");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	    }
	  }
		public static void main(String[] args) {

			Server sv;
			try {
				sv = new Server("server.db");
				sv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				sv.setSize(FRAME_WIDTH, FRAME_HEIGHT);
				sv.setVisible(true);
			} catch (IOException | SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
