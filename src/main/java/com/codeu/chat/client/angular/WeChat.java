package codeu.chat.client.angular;


import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

import codeu.chat.client.ClientContext;
import codeu.chat.client.Controller;
import codeu.chat.client.View;
import codeu.chat.common.ConversationSummary;
import codeu.chat.util.Logger;

import java.security.SecureRandom;
import javax.crypto.SecretKeyFactory;
import java.security.spec.InvalidKeySpecException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.spec.PBEKeySpec;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import codeu.chat.common.User;
import codeu.chat.common.Message;


// TODO; remove
import codeu.chat.util.Uuid;


/**
 * Root resource (exposed at "wechat" path)
 * 
 * Based off of resources given at: 
 * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
 *
 */
@Path("wechat")
public final class WeChat {
    private final static Logger.Log LOG = Logger.newLog(WeChat.class);

    private final static int PAGE_SIZE = 10;

    private static boolean alive = true;

    private final SecureRandom random = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    // Needs to be instantiated from WebClientMain.java
    public static ClientContext clientContext;

    // TODO: Remove
    // Counter of how many requests have been made overall
    public static int globalInt = 0;


    // Maps Uuid.toString()s to corresponding ConversationSummaries 
    // for easy lookup when selecting a conversation
    private static Map<String, ConversationSummary> allConvosByID = new HashMap<>();




    /** This argument-less constructor will be called everytime an http request 
     * is made to a method in this class. It is imperative that the 2-argument 
     * constructor is called at some point prior to any http requests being made,
     * because otherwise the static clientContext will never have been instantiated
     */
    public WeChat(){
        
        if (this.clientContext == null) {
            System.out.println("ERROR: No clientContext has been instantiated. "
                + "Please call the 2-argument constructor before any other use "
                + "of this class to avoid errors");
        }

        // TODO: remove when not needed for debugging
        globalInt = globalInt + 1;
        System.out.println(globalInt);
        System.out.println(clientContext);
        System.out.println("zero arg constructor called");
    }

    /** Constructor that sets up a static clientContext 
     *  that will be used by any subsequent intantiations or uses of this class
     *  @param controller - a new Controller
     *  @param view - a new View
     */
    public WeChat(Controller controller, View view) {
        this.clientContext = new ClientContext(controller, view);
    }


    /**
     * @return true if the chat is still active, false if it is not
     */
    public boolean chatActive() {
        return this.alive;
    }


    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Path("testtext")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "This text is being sent from java backend!";
    }


    /**
     * Method handling HTTP GET request to exit the chat (?).
     * 
     * @return String notifying client of completion.
     */
    @GET
    @Path("exit")
    @Produces(MediaType.TEXT_PLAIN)
    public String exit() {
        alive = false;
        return "exiting app";
    }



    /**
     * Parses a string into JSON
     * Currently, assumes that the string is formatted as a JSON would be
     * @param jsonAsString - a string in json format to parse
     * @return the JSONObject resulting from parsing jsonAsString
     * @throws ParseException
     */
    // TODO: Casting is usually bad practice -- look into whether its possible to avoid casting
    private JSONObject stringToJson(String jsonAsString) throws ParseException {
        JSONParser parser = new JSONParser();

        Object jsonAsObject = parser.parse(jsonAsString);

        JSONObject jsonAsJSONObject = (JSONObject) jsonAsObject;

        return jsonAsJSONObject;
    }





    /**
     * Creates a new user account
     * @param jsonAsString - a string in json format with 'username' and 'password' fields 
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    // TODO: catch ParseException
    /* TODO: This recieving of String and converting to JSON means that fields 
        with characters like ':','{','}' could cause problems with the parsing 
        -- need to test to see if this is a problem, and if it is, either start 
        sending actual JSON, or disallow those specific characters */
    // TODO: check if user already exists?
    // TODO: test and modify to handle jsonAsString with wrong json fields without crashing
    @POST
    @Path("adduser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    // Add a new user.
    public String addUser(String jsonAsString) {
        try {

            JSONObject jsonObject = stringToJson(jsonAsString);
            String name = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");

            clientContext.user.addUser(name, password);
            
            JSONObject obj = new JSONObject();
            obj.put("message", "Account " + name +" was successfully created! Please sign in to confirm and start chatting!");
            System.out.println(obj.toJSONString());
            return obj.toJSONString();

        } catch (ParseException e) {
            e.printStackTrace();

            JSONObject obj = new JSONObject();
            obj.put("error", "There was some problem in processing the contents of your request. Try again with different answers, or contact the administrator.");
            System.out.println(obj.toJSONString());
            return obj.toJSONString();
        }
    }


    /**
     * Signs into a user account
     * @param jsonAsString - a string in json format with 'username' and 'password' fields 
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    // TODO: catch ParseException
    /* TODO: This recieving of String and converting to JSON means that fields 
        with characters like ':','{','}' could cause problems with the parsing 
        -- need to test to see if this is a problem, and if it is, either start 
        sending actual JSON, or disallow those specific characters */
    @POST
    @Path("signinuser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String signInUser(String jsonAsString) {
        try {
            JSONObject jsonObject = stringToJson(jsonAsString);
            String name = (String) jsonObject.get("username");
            String password = (String) jsonObject.get("password");

            JSONObject obj = new JSONObject();
            if (clientContext.user.signInUser(name, password)) {
                // Successfully signed in
                obj.put("message", "User " + name +" signed in!");
            } else {
                // Error while signing in
                obj.put("error", "Signing into " + name +" was unsuccessful... \n Recheck the username and password, or create a new account");
            }

            System.out.println(obj.toJSONString());
            return obj.toJSONString();

        } catch (ParseException e) {
            e.printStackTrace();

            JSONObject obj = new JSONObject();
            obj.put("error", "There was some problem in processing the contents of your request. Try again with different answers, or contact the administrator.");
            System.out.println(obj.toJSONString());
            return obj.toJSONString();
        }
    }



    /**
     * Shows all the availiable users
     * @return String such that each user's username is on a new line
     */
    @GET
    @Path("showallusers")
    @Produces(MediaType.TEXT_PLAIN)
    public String showAllUsers() {
        StringBuilder toRet = new StringBuilder();

        clientContext.user.updateUsers();
        for (User u : clientContext.user.getUsers()) {
          toRet.append(u.name);
          toRet.append("\n");
        }

        return toRet.toString();
    }



    /**
     * 
     * @return All the details of all the open conversations as a JSON list 
     * that contains the title, uuid, owner name, and creation date for each chat
     */
    /* TODO: rather than reloading all the conversations every time this is called, 
       might be better to have a method that only sends previously unrequested 
       conversations (to avoid wasted duplicate work) */
    @GET
    @Path("showallconvos")
    @Produces(MediaType.APPLICATION_JSON)
    public String showAllConvos() {


        allConvosByID.clear();

        
        // Update all conversations
        clientContext.conversation.updateAllConversations(false);

        JSONArray allConvos = new JSONArray();



        // Clears and fills the usersById hashmap
        /* Why does this have to be called separately?! There seems to be 
           no documentation as to why and is annoying behavior for ClientUser.lookup 
           to not work without it >_< */
        clientContext.user.updateUsers();

        for (final ConversationSummary conv : clientContext.conversation.getConversationSummaries()) {
            

            allConvosByID.put(conv.id.toString(), conv);
            
            String ownerName = clientContext.user.lookup(conv.owner).name;

            JSONObject jsonConv = new JSONObject();
            jsonConv.put("title", conv.title);
            jsonConv.put("id", conv.id.toString());
            jsonConv.put("owner", ownerName);
            jsonConv.put("creation", conv.creation.inMs());

            allConvos.add(jsonConv);
        }

        return allConvos.toJSONString();
    }



    /**
     * Selects a conversation
     * @param chosenConvo - the Uuid.toString() of the conversation to select
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    // TODO: might be a bad idea to send the actual uuid to the web client -- perhaps need to change this and showAllConvos slightly?
    @POST
    @Path("selectconvo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String selectConvo(String chosenConvo) {

        System.out.println("inside selectConvo");

        JSONObject obj = new JSONObject();

        // Update, save previous, and determine the new selection by looking up chosenConvo
        clientContext.conversation.updateAllConversations(false);
        final ConversationSummary previous = clientContext.conversation.getCurrent();
        ConversationSummary newCurrent = (allConvosByID.containsKey(chosenConvo)) ? allConvosByID.get(chosenConvo) : null;

        if (newCurrent != previous && newCurrent != null) {
            clientContext.conversation.setCurrent(newCurrent);
            clientContext.conversation.updateAllConversations(true);

            obj.put("message", newCurrent.title + " conversation selected");

        } else {
            obj.put("error", "There was some error in selecting the conversation");
        }

        System.out.println(obj.toJSONString());
        return obj.toJSONString();
    }




    /**
     * @return JSON list of all the messages of the currently selected conversation
     */
    /* TODO: rather than reloading all the messages every time this is called, 
       might be better to have a method that only sends previously unrequested 
       messages (to avoid wasted duplicate work) */
    @GET
    @Path("showcurrentmessages")
    @Produces(MediaType.APPLICATION_JSON)
    public String showCurrentMessages() {

        JSONArray allMsgs = new JSONArray();

        if (!clientContext.conversation.hasCurrent()) {
            System.out.println("ERROR: No conversation selected.");

        } else if (clientContext.message.currentMessageCount() == 0) {
            System.out.println(" Current Conversation has no messages");

        } else {

            for (final Message m : clientContext.message.conversationContents) {
                
                // Update users and extract the author's name
                clientContext.user.updateUsers();
                String authorName = clientContext.user.lookup(m.author).name;

                // This can be used to check whether the author is the same user or not for the purposes of placement
                String currUsername = (clientContext.user.hasCurrent()) ? clientContext.user.getCurrent().name : "";
                
                // Make message JSON
                JSONObject jsonMsg = new JSONObject();
                jsonMsg.put("author", authorName);
                jsonMsg.put("currUser", currUsername);
                // jsonMsg.put("id", m.id);
                jsonMsg.put("content", m.content);
                jsonMsg.put("creation", m.creation.inMs());

                // Add message to list of messages
                allMsgs.add(jsonMsg);
            }
        }

        return allMsgs.toJSONString();

    }


    /**
     * Sends a message to the currently selected conversation
     * @param msgToSend - the message text to send
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @POST
    @Path("sendmsg")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String sendMsg(String msgToSend) {

        System.out.println("inside sendMsg");

        JSONObject obj = new JSONObject();

        if (!clientContext.user.hasCurrent()) {
            // There is no 'current' user
            obj.put("error", "Not signed in.");
        } else if (!clientContext.conversation.hasCurrent()) {
            // There is no 'current' conversation
            obj.put("error", "No conversation has been selected.");
        } else {
            clientContext.message.addMessage(clientContext.user.getCurrent().id,
                clientContext.conversation.getCurrentId(), msgToSend);

            obj.put("message", "Message sent.");
        }

        System.out.println(obj.toJSONString());
        return obj.toJSONString();
    }


    /**
     * Creates a new conversation
     * @param convoTitle - title of new conversation
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @POST
    @Path("createnewconvo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String createNewConvo(String convoTitle) {

        System.out.println("inside createNewConvo");
        
        JSONObject obj = new JSONObject();

        if (!clientContext.user.hasCurrent()) {
            // There is no 'current' user
            obj.put("error", "Not signed in.");
        } else {
            clientContext.conversation.startConversation(convoTitle, clientContext.user.getCurrent().id);
            obj.put("message", "New conversation " + convoTitle + " created!");
        }

        System.out.println(obj.toJSONString());
        return obj.toJSONString();
    }


    /**
     * Signs out the current user
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @GET
    @Path("signoutuser")
    @Produces(MediaType.APPLICATION_JSON)
    public String signOutUser() {
        
        JSONObject obj = new JSONObject();

        if (clientContext.user.signOutUser()) {
            // Successfully signed out
            obj.put("message", "Successfully signed out!");
        } else {
            // Error while signing in
            obj.put("error", "Sign out failed... (Perhaps no one was signed in?)");
        }

        System.out.println(obj.toJSONString());
        return obj.toJSONString();

    }






// for (String pers: allConvosByID.keySet()){
        //         // String key =name.toString();
        //         // String value = example.get(name).toString();  
        //         // System.out.println(key + " " + value); 
        //     System.out.println(pers);
        //     System.out.println(allConvosByID.get(pers).title);
        // }




// if (!clientContext.user.signOutUser()) {
// System.out.println("Error: sign out failed (not signed in?)");
// }


// else if (token.equals("sign-out")) {

//       if (!clientContext.user.hasCurrent()) {
//         System.out.println("ERROR: Not signed in.");
//       } else {
//         signOutUser();
//       }









// } else if (token.equals("m-add")) {

//       if (!clientContext.user.hasCurrent()) {
//         System.out.println("ERROR: Not signed in.");
//       } else if (!clientContext.conversation.hasCurrent()) {
//         System.out.println("ERROR: No conversation selected.");
//       } else {
//         if (!tokenScanner.hasNext()) {
//           System.out.println("ERROR: Message body not supplied.");
//         } else {
//           clientContext.message.addMessage(clientContext.user.getCurrent().id,
//               clientContext.conversation.getCurrentId(),
//               tokenScanner.nextLine().trim());
//         }
//       }

//     }

// value.id);
//       Uuid.SERIALIZER.write(out, value.next);
//       Uuid.SERIALIZER.write(out, value.previous);
//       Time.SERIALIZER.write(out, value.creation);
//       Uuid.SERIALIZER.write(out, value.author);
//       Serializers.STRING.write(out, value.content);



// public void showAllMessages() {
//     if (conversationContents.size() == 0) {
//       System.out.println(" Current Conversation has no messages");
//     } else {
//       for (final Message m : conversationContents) {
//         printMessage(m, userContext);
//       }
//     }
//   }

//  // Print Message.  User context is used to map from author UUID to name.
//   public static void printMessage(Message m, ClientUser userContext) {
//     if (m == null) {
//       System.out.println("Null message.");
//     } else {

//       // Display author name if available.  Otherwise display the author UUID.
//       final String authorName = (userContext == null) ? null : userContext.getName(m.author);

//       System.out.format(" Author: %s   Id: %s created: %s\n   Body: %s\n",
//           (authorName == null) ? m.author : authorName, m.id, m.creation, m.content);
//     }
//   }

// usersById.clear();
//     usersByName = new Store<>(String.CASE_INSENSITIVE_ORDER);

//     for (final User user : view.getUsersExcluding(EMPTY)) {
//       usersById.put(user.id, user);




//     public void showAllConversations() {
//     updateAllConversations(false);

//     for (final ConversationSummary c : summariesByUuid.values()) {
//       printConversation(c, userContext);
//     }
//   }


//     public void selectConversation(Scanner lineScanner) {

//     clientContext.conversation.updateAllConversations(false);
//     final int selectionSize = clientContext.conversation.conversationsCount();
//     System.out.format("Selection contains %d entries.\n", selectionSize);

//     final ConversationSummary previous = clientContext.conversation.getCurrent();
//     ConversationSummary newCurrent = null;

//     if (selectionSize == 0) {
//       System.out.println("Nothing to select.");
//     } else {
//       final ListNavigator<ConversationSummary> navigator =
//           new ListNavigator<ConversationSummary>(
//               clientContext.conversation.getConversationSummaries(),
//               lineScanner, PAGE_SIZE);
//       if (navigator.chooseFromList()) {
//         newCurrent = navigator.getSelectedChoice();
//         clientContext.message.resetCurrent(newCurrent != previous);
//         System.out.format("OK. Conversation \"%s\" selected.\n", newCurrent.title);
//       } else {
//         System.out.println("OK. Current Conversation is unchanged.");
//       }
//     }
//     if (newCurrent != previous) {
//       clientContext.conversation.setCurrent(newCurrent);
//       clientContext.conversation.updateAllConversations(true);
//     }
//   }



  //   clientContext.conversation.showAllConversations();



  //   // Populate ListModel - updates display objects.
  // private void getAllConversations(DefaultListModel<String> convDisplayList) {

  //   clientContext.conversation.updateAllConversations(false);
  //   convDisplayList.clear();

  //   for (final ConversationSummary conv : clientContext.conversation.getConversationSummaries()) {
  //     convDisplayList.addElement(conv.title);
  //   }
  // }











    // //// TODO: remove example of simple json syntax after done
    // JSONObject obj = new JSONObject();
    // obj.put("name", "mkyong.com");
    // obj.put("age", new Integer(100));

    // JSONArray list = new JSONArray();
    // list.add("msg 1");
    // list.add("msg 2");
    // list.add("msg 3");

    // obj.put("messages", list);

    // obj.toJSONString();


    // JSONParser parser = new JSONParser();

    //     try {

    //         Object obj = parser.parse(new FileReader("f:\\test.json"));

    //         JSONObject jsonObject = (JSONObject) obj;
    //         System.out.println(jsonObject);

    //         String name = (String) jsonObject.get("name");
    //         System.out.println(name);

    //         long age = (Long) jsonObject.get("age");
    //         System.out.println(age);

    //         // loop array
    //         JSONArray msg = (JSONArray) jsonObject.get("messages");
    //         Iterator<String> iterator = msg.iterator();
    //         while (iterator.hasNext()) {
    //             System.out.println(iterator.next());
    //         }

    //     } catch (FileNotFoundException e) {
    //         e.printStackTrace();
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     } catch (ParseException e) {
    //         e.printStackTrace();
    //     }






    










}


