
package codeu.chat.server;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
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

	public void createUserTable() {
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS USERS " + 
						 "(ID            TEXT      NOT NULL," + 
						 " NAME          TEXT                  NOT NULL," + 
						 " CREATIONTIME  INTEGER            NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}

	public void insertUserTable(String id, String name, Long time) {
		try {
			stmt = conn.createStatement();
			String sql = "INSERT INTO USERS (ID,NAME,CREATIONTIME) " + 
						 "VALUES ( '" + id + "' , '" + name + "' , '" + time + "');" ;
			stmt.executeUpdate(sql);
			stmt.close();
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
						 "VALUES ( '" + id + "' , '" + title + "' , '" + ownerID + "' , '" + time + "');" ;
			stmt.executeUpdate(sql);
			stmt.close();
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
						"VALUES ( '" + id + "','" + author + "','" + 
						convo + "','" + body + "','" + time + "');" ; 
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);			
		}						
	}

}