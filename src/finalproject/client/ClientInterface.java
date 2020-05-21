package finalproject.client;

import java.sql.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import finalproject.client.ClientInterface.ComboBoxItem;
import finalproject.db.DBInterface;
import finalproject.entities.Person;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ClientInterface extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_PORT = 8001;
	
	private static final int FRAME_WIDTH = 600;
	private static final int FRAME_HEIGHT = 400;
	final int AREA_ROWS = 10;
	final int AREA_COLUMNS = 40;

	
	Socket socket;
	int port;
	
	Connection conn;
	
	int sent=0;
	
	
	JPanel row1Panel, row2Panel, comboBoxPanel, connectionButtonsPanel, dataButtonsPanel, textAreaPanel, mainPanel;
	JLabel activeDb, dbName, activeCon, conName;
	JComboBox<String> peopleSelect;
	JFileChooser jFileChooser;
	JButton openConButton, closeConButton, sendDataButton, queryDbDataButton;
	JTextArea textArea;
	JScrollPane scrollPane;
	
	JMenuBar menuBar;
	JMenu menu;
	
	public ClientInterface() {
		this(DEFAULT_PORT);
		setAllComponents();
	}
	
	public ClientInterface(int port) {
		this.port = port;
		setAllComponents();
		
	}
	
	@SuppressWarnings("rawtypes")
	public void setAllComponents() {
		this.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		row1Panel = new JPanel(new FlowLayout());
		row2Panel = new JPanel(new FlowLayout());
		comboBoxPanel = new JPanel(new FlowLayout());
		connectionButtonsPanel = new JPanel(new GridLayout(1,1));
		dataButtonsPanel = new JPanel(new GridLayout(1,1));
		textAreaPanel = new JPanel(new BorderLayout());
		
		
		activeDb = new JLabel("Active DB: ");
		dbName = new JLabel("<none>");
		
		row1Panel.add(activeDb);
		row1Panel.add(dbName);
		
		activeCon = new JLabel("Active Connection: ");
		conName = new JLabel("<none>");
		
		row2Panel.add(activeCon);
		row2Panel.add(conName);
		
		String[] empty = {"Empty"};
		peopleSelect = new JComboBox<String>(empty);
		comboBoxPanel.add(peopleSelect);
		
		openConButton = new JButton("Open Connection");
		openConButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				openConnection();
				sent=0;
			}
		});
		
		closeConButton = new JButton("Close Connection");
		closeConButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				closeConnection();
				sent=0;
			}
		});
		closeConButton.setEnabled(false);
		
		connectionButtonsPanel.add(openConButton);
		connectionButtonsPanel.add(closeConButton);
		
		sendDataButton = new JButton("Send Data");
		sendDataButton.setEnabled(false);
		sendDataButton.addActionListener(new SendButtonListener());
		queryDbDataButton = new JButton("Query DB Data");
		queryDbDataButton.setEnabled(false);
		queryDbDataButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				queryData();
			}
		});
		dataButtonsPanel.add(sendDataButton);
		dataButtonsPanel.add(queryDbDataButton);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane = new JScrollPane(textArea);
		
		String currentDirectory = System.getProperty("user.dir");
		jFileChooser = new JFileChooser(currentDirectory);
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Database files", "db", "db");
		jFileChooser.setFileFilter(filter);
		
		mainPanel = new JPanel(new GridLayout(6,1));
		mainPanel.add(row1Panel);
		mainPanel.add(row2Panel);
		mainPanel.add(comboBoxPanel);
		mainPanel.add(connectionButtonsPanel);
		mainPanel.add(dataButtonsPanel);
		
		menuBar = new JMenuBar();
		menu = createFileMenu();
		menuBar.add(menu);
		this.setJMenuBar(menuBar);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel, BorderLayout.NORTH);
		this.add(scrollPane, BorderLayout.CENTER);
		
	}
	
	public void toggleButtons(boolean openCon, boolean closeCon, boolean sendData) {
		openConButton.setEnabled(openCon);
		sendDataButton.setEnabled(sendData);
		closeConButton.setEnabled(closeCon);
	}
	
	public void openConnection() {
		try {
			socket = new Socket("localhost", DEFAULT_PORT);
			textArea.append("\nconnected");
			conName.setText("<localhost Server>");
			toggleButtons(false, true, true);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			textArea.append("\nconnection Failure");
		}
	}
	
	public void closeConnection() {
		try { 
			socket.close(); 
			textArea.append("\nconnection closed");
			conName.setText("<none>");
			toggleButtons(true, false, false);
		} catch (Exception e1) {
			System.err.println("error"); 
		}
	}
	
	public void queryData() {
		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT * FROM people WHERE sent=0");
			ResultSet rset = stmt.executeQuery();
			ResultSetMetaData rsmd = rset.getMetaData();
			int numColumns = rsmd.getColumnCount();
			System.out.println("\n numcolumns is "+ numColumns);
			
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
	

   public JMenu createFileMenu()
   {
      JMenu menu = new JMenu("File");
      menu.add(createFileOpenItem());
      menu.add(createFileExitItem());
      return menu;
   }
   
   
   private void fillComboBox() throws SQLException {
	   
	   List<ComboBoxItem> l = getNames();
	   peopleSelect.setModel(new DefaultComboBoxModel(l.toArray()));
	   for(ComboBoxItem i : l) {
		   System.out.println(i.name);
	   }
	   
   }
   
   public void clearComboBox() {
	   peopleSelect.removeAll();
	   System.out.println("Removed");
   }
   
   private JMenuItem createFileExitItem() {
	   JMenuItem item = new JMenuItem("Exit");
	   item.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			System.exit(0);
		}
	});
	   return item;
   }
   
   private JMenuItem createFileOpenItem() {
	   JMenuItem item = new JMenuItem("Open DB");
	   
	   item.addActionListener(new OpenDBListener());
	   return item;
   }
   
   // USED FOR APPENDING DATA.
   public class AppendingObjectOutputStream extends ObjectOutputStream {

	   public AppendingObjectOutputStream(OutputStream out) throws IOException {
	     super(out);
	   }

	   @Override
	   protected void writeStreamHeader() throws IOException {
	     // do not write a header, but reset:
	     // this line added after another question
	     // showed a problem with the original
	     reset();
	   }

	 }

   
   DataInputStream fromServer;
   ObjectOutputStream os;
   
   AppendingObjectOutputStream aos;
   

   
   
	class SendButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {

	        try {
				
	        	// responses are going to come over the input as text, and that's tricky,
	        	// which is why I've done that for you:
	        	fromServer = new DataInputStream(socket.getInputStream());
				
				// now, get the person on the object dropdownbox we've selected
				ComboBoxItem personEntry = (ComboBoxItem)peopleSelect.getSelectedItem();
				
				// That's tricky which is why I have included the code. the personEntry
				// contains an ID and a name. You want to get a "Person" object out of that
				// which is stored in the database
				
				System.out.println("Person Entry: " + personEntry.id);
				
				int id = personEntry.id;
				
				Person person = new Person();
				
				String sql = "SELECT * FROM people WHERE id=?";
				
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				while(rs.next()) {
					person.setFirstName(rs.getString(1));
					person.setLastName(rs.getString(2));
					person.setAge(rs.getString(3));
					person.setCity(rs.getString(4));
					person.setId(rs.getString(6));
				}
				
				
				if(sent==0) {
					// Means no data is sent yet.
					os = new ObjectOutputStream(socket.getOutputStream());
					os.writeObject(person);
					sent=1;
				}
				else {
					// atleast one data has been sent yet.
					// Then instantiate appendable output stream and use that.
					aos = new AppendingObjectOutputStream(socket.getOutputStream());
					aos.writeObject(person);
					sent=1;
				}
				
				
				
				
				
				
				// Send the person object here over an output stream that you got from the socket.
				
				int response = fromServer.readInt();
				System.out.println(response);
				if (response==1) {
					System.out.println("Success");
					// what do you do after we know that the server has successfully
					// received the data and written it to its own database?
					// you will have to write the code for that.
					
					String sql1 = "UPDATE people SET sent=1 WHERE id=?";
					PreparedStatement ps1 = conn.prepareStatement(sql1);
					ps1.setInt(1, id);
					int res = ps1.executeUpdate();
					if(res>0) {
						textArea.append("\n Server response: SUCCESSFULLY RECEIVED");
						clearComboBox();
						fillComboBox();
					}
					else {
						textArea.append("\n Server response: FAILED");
					}
					
				} else {
					System.out.println("Failed");
					textArea.append("\n Server response: FAILED TO RECEIVE");
				}
				
			} 
	        catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	        
			
		}
		
	}
	
   private List<ComboBoxItem> getNames() throws SQLException {
	   
	   List<ComboBoxItem> names = new ArrayList<ComboBoxItem>();
	   
	   String sql = "SELECT * FROM people WHERE sent='0'";
	   try {
		   PreparedStatement ps = conn.prepareStatement(sql);
		   ResultSet rs = ps.executeQuery();
		   while(rs.next()) {
			   names.add(new ComboBoxItem(rs.getInt(6), rs.getString(1)));
		   }
		   return names;
	   }
	   catch(Exception ex) {
		   System.err.println("Error is: "+ex);
	   }
	   
	   return null;
   }
	
	// a JComboBox will take a bunch of objects and use the "toString()" method
	// of those objects to print out what's in there. 
	// So I have provided to you an object to put people's names and ids in
	// and the combo box will print out their names. 
	// now you will want to get the ComboBoxItem object that is selected in the combo box
	// and get the corresponding row in the People table and make a person object out of that.
	class ComboBoxItem {
		private int id;
		private String name;
		
		public ComboBoxItem(int id, String name) {
			this.id = id;
			this.name = name;
		}
		
		public int getId() {
			return this.id;
		}
		
		public String getName() {
			return this.name;
		}
		
		public String toString() {
			return this.name;
		}
	}
	
	/* the "open db" menu item in the client should use this ActionListener */
	   class OpenDBListener implements ActionListener
	      {
			   
	         public void actionPerformed(ActionEvent event)
	         {
	 			int returnVal = jFileChooser.showOpenDialog(getParent());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose to open this file: " + jFileChooser.getSelectedFile().getAbsolutePath());
					String dbFileName = jFileChooser.getSelectedFile().getAbsolutePath();
					System.out.println("You chose to open this file: " + dbFileName);
					
					try {
						DBInterface dbCon = new DBInterface(dbFileName);
						dbCon.setConnection();
						conn = dbCon.getConn();
						dbName.setText( jFileChooser.getSelectedFile().getName());
						//queryButtonListener.setConnection(conn);
						clearComboBox();
						fillComboBox();
						queryDbDataButton.setEnabled(true);
						
					} catch (Exception e ) {
						System.err.println("error connection to db: "+ e.getMessage());
						e.printStackTrace();
						dbName.setText("<None>");
					}
					
				}
	         }
	      }
	
	public static void main(String[] args) {
		ClientInterface ci = new ClientInterface();
		ci.setSize(FRAME_WIDTH, FRAME_HEIGHT);
		ci.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ci.setVisible(true);
	}
}
