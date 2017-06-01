
package codeu.chat.server;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.DriverManager;

import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class MessageDatabase {

	private Connection conn;
	private Statement stmt;
	private PreparedStatement pstmt;


	public MessageDatabase() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:message.db");
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
		}
	}

	public Connection getConnection() {
		return conn;
	}	

	public void closeConnection() {
		try { 
			conn.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
		}
	}

	public void createUserTable() {
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS USERS "  + 
						 "(ID            TEXT      NOT NULL," + 
						 " NAME          TEXT      NOT NULL," + 
						 " SALT          BLOB      NOT NULL," + 
						 " HASHPASS      BLOB      NOT NULL," +
						 " CREATIONTIME  INTEGER   NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}

	public void insertUser(String id, String name, byte[] salt, byte[] hashedpass, Long time) {
		String sql = "INSERT INTO USERS (ID, NAME, SALT, HASHPASS, CREATIONTIME) " + 
					 "VALUES (?, ?, ?, ?, ?);";
		try {		
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, name);
			pstmt.setBytes(3, salt);
			pstmt.setBytes(4, hashedpass);
			pstmt.setLong(5, time);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);			
		}
	}

	public void createConversationTable() {
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS CONVERSATION " + 
						 "(ID            TEXT      NOT NULL," + 
						 " TITLE         TEXT      NOT NULL," + 
						 " OWNER         TEXT      NOT NULL," + 
						 " CREATIONTIME  INTEGER      NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
    }

	public void insertConversationTable(String id, String title, String ownerID, Long time) {
		try {
			String sql = "INSERT INTO CONVERSATION (ID, TITLE, OWNER, CREATIONTIME)" + 
						 "VALUES (?, ?, ?, ?);";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, title);
			pstmt.setString(3, ownerID);
			pstmt.setLong(4, time);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);			
		}

	}

	public void createMessageTable() {
		try {
			String sql = "CREATE TABLE IF NOT EXISTS MESSAGE " + 
					 "(ID             TEXT      NOT NULL," + 
					 " AUTHOR         TEXT      NOT NULL," + 
					 " CONVERSATION   TEXT      NOT NULL," + 
					 " BODY           TEXT      NOT NULL," + 
					 " CREATIONTIME   INTEGER      NOT NULL)";
		stmt.executeUpdate(sql);
		stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
			System.exit(0);
		}
	}

	public void insertMessageTable(String id, String author, String convo, String body, Long time) {
		try {
			String sql = "INSERT INTO MESSAGE (ID, AUTHOR, CONVERSATION, BODY, CREATIONTIME)" + 
						"VALUES (?, ?, ?, ?, ?);" ; 
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, author);
			pstmt.setString(3, convo);
			pstmt.setString(4, body);
			pstmt.setLong(5, time);
			pstmt.executeUpdate();
			pstmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);			
		}						
	}

	public void deleteUsers() {
		try {
			stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS 'users'";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}
	
	public void deleteConversation() {
		try {
			stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS 'conversation'";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}

	public void deleteMessage() {
		try {
			stmt = conn.createStatement();
			String sql = "DROP TABLE IF EXISTS 'message'";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}


}