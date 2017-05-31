package codeu.chat.server;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet; 

import java.util.Collection;

import codeu.chat.common.BasicController;
import codeu.chat.common.Conversation;
import codeu.chat.common.Message;
import codeu.chat.common.RawController;
import codeu.chat.common.User;
import codeu.chat.util.Logger;
import codeu.chat.util.Time;
import codeu.chat.util.Uuid;

public final class RetrieveMessageData{

private MessageDatabase msgData;

	public RetrieveMessageData(MessageDatabase db) {
		msgData = db;
	}

	public void retrieveUser(Controller controller) {
		try {
			Statement stmt = msgData.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT * FROM USERS;");
			while (rs.next() ) {
				Uuid id = Uuid.fromString(rs.getString("id"));
				String name = rs.getString("name");
				byte[] salt = rs.getBytes("salt");
				byte[] hashpass = rs.getBytes("hashpass");
				Time time = Time.fromMs(rs.getLong("creationtime"));

				controller.loadUser(id, name, salt, hashpass, time);
			}
			rs.close();
			stmt.close();
		}
		catch ( Exception e ) {
      		System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      		System.exit(0);
   		}
   	}
	public void retrieveConversation(Controller controller) {
		try {
			Statement stmt = msgData.getConnection().createStatement();
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

	public void retrieveMessage(Controller controller) {
		try {
			Statement stmt = msgData.getConnection().createStatement();
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