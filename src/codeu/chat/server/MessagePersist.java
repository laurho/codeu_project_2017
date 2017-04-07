
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

	public void createTable() {
		try {
			stmt = conn.createStatement();
			String sql = "CREATE TABLE IF NOT EXISTS USERS " + 
						 "(ID            TEXT      NOT NULL," + 
						 " NAME          TEXT                  NOT NULL," + 
						 " CREATIONTIME  TEXT                  NOT NULL)";
			stmt.executeUpdate(sql);
			stmt.close();
		}
		catch (Exception e) {
			System.err.println( e.getClass().getName() + ": " + e.getMessage() );
    		System.exit(0);
    	}
	}

	public void insertTable(String id, String name, String time) {
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

}