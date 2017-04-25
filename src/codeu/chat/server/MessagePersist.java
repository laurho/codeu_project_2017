
package codeu.chat.server;

import java.sql.*;
import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class MessagePersist {

	private Connection conn;
	private Statement stmt;

	public MessagePersist() {
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:message.db");
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
		}
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

	public void retrieveUser(Controller controller) {
		try {
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;");
			while (rs.next() ) {
				Uuid id = Uuid.fromString(rs.getString("id"));
				String name = rs.getString("name");
				Time time = Time.fromMs(rs.getLong("creationtime"));

				controller.newUser(id, name, time);
			}
			rs.close();
			stmt.close();
		}
		catch ( Exception e ) {
      		System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);
   		}
	}

	public void createConversationTable() {
		try {
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

	public void retrieveConversation(Controller controller) {
		try {
			ResultSet rs = stmt.executeQuery( "SELECT * FROM CONVERSATION;");
			while (rs.next() ) {
				Uuid id = Uuid.fromString(rs.getString("id"));
				String title = rs.getString("title");
				Uuid owner = Uuid.fromString(rs.getString("owner"));
				Time time = Time.fromMs(rs.getLong("creationtime"));

				controller.newConversation(id, title, owner, time);
			}
			rs.close();
			stmt.close();
		}
		catch ( Exception e ) {
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

	public void retrieveMessage(Controller controller) {
		try {
			ResultSet rs = stmt.executeQuery( "SELECT * FROM MESSAGE;");
			while (rs.next() ) {
				Uuid id = Uuid.fromString(rs.getString("id"));
				Uuid author = Uuid.fromString(rs.getString("author"));
				Uuid conversation = Uuid.fromString(rs.getString("conversation"));
				String body = rs.getString("body");
				Time time = Time.fromMs(rs.getLong("creationtime"));

				controller.newMessage(id, author, conversation, body, time);
			}
			rs.close();
			stmt.close();
		}
		catch ( Exception e ) {
      		System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);
   		}
	}

}