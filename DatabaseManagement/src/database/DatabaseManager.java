package database;

import java.sql.*;

public class DatabaseManager {
	public static final String URL_SERVEUR = "tch054ora12c.logti.etsmtl.ca";
	public static final int PORT = 1521;
	public static final String SID = "TCH054";
	
	private String user;
	private String pass;
	
	private Connection connexion_JDBC;
	
	
	public DatabaseManager(String user, String pass) {
		this.user = user;
		this.pass = pass;
		
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
		} catch (ClassNotFoundException e) {
			System.out.print("\nImpossible d'initialiser database manager");
			e.printStackTrace();
		}
	}
	
	public Connection getConnection() {
		return connexion_JDBC;
	}
	
	public boolean createConnection() {
		boolean response = false;
		try {
			String uri = "jdbc:oracle:thin:@" + URL_SERVEUR + ":" + PORT + ":" + SID;
			System.out.println("\nURI: " + uri);
			connexion_JDBC = DriverManager.getConnection(uri, user, pass);
			response = true;
			
		} catch (SQLException e) {
			connexion_JDBC = null;
			e.printStackTrace();
		}
		return response;
	}
	
	public boolean terminateConnection() {
		boolean response = false;
		if (connexion_JDBC != null) {
			try {
				connexion_JDBC.close();
			} catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return response;
	}
}
