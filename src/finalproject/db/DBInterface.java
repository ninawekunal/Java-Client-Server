package finalproject.db;
import java.sql.*;
import java.util.ArrayList;

import finalproject.entities.Person;

public class DBInterface {

	/* implementing or using this class isn't strictly required, but
	 * you might want to abstract some of the interactions with and queries
	 * to the database to a separate class.
	 */
	
	private String dbName;
	
	Connection conn;
	
	public DBInterface() {
		dbName = "server.db";
	}
	
	public DBInterface(String dbName) {
		this.dbName = dbName;
	}
	
	public Connection getConn() {
		return this.conn;
	}
	
	public void setConnection() throws SQLException {
		try {
			this.conn = DriverManager.getConnection("jdbc:sqlite:"+dbName);
			
		}
		catch(Exception e) {
			System.err.println("Connection Error: " + e);
		}
	}
	
}
