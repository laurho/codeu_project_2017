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

import codeu.chat.util.RemoteAddress;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.ConnectionSource;


/**
 * This is the main place of operations for the Java Web Client.
 * Root resource (exposed at "wechat" path)
 * 
 * Based off of resources given at: 
 * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
 * 
 * TODO statements have been left in as signs of potential directions this project would go in the future
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
    // public static ClientContext clientContext;
    public static RemoteAddress address;

    // TODO: Remove
    // Counter of how many requests have been made overall
    public static int globalInt = 0;


    // Maps Uuid.toString()s to corresponding ConversationSummaries 
    // for easy lookup when selecting a conversation
    private static Map<String, ConversationSummary> allConvosByID = new HashMap<>();

    /* Since we want multiple web pages to be able to be used by different users simultaneously,
       We will be making a new ClientContext for each Angular client that connects to this client.
       While it would have been more ideal to create a new java client for each Angular client, 
       it did not seem possible to do that in a clean manner, specifically since not all of those 
       Angular clients would be able to use the same port to communicate with the java client, and 
       so how would we set their ports dynamically?
    */
    private static Integer clientContextCounter = 0;
    private static Map<String, ClientContext> allClientContexts = new HashMap<>();




    /** This argument-less constructor will be called everytime an http request 
     * is made to a method in this class. It is imperative that the 2-argument 
     * constructor is called at some point prior to any http requests being made,
     * because otherwise the static clientContext will never have been instantiated
     */
    public WeChat(){
        
        if (this.address == null) {
            System.out.println("ERROR: No clientContext has been instantiated. "
                + "Please call the 1-argument constructor before any other use "
                + "of this class to avoid errors");
        }

        // TODO: remove when not needed for debugging
        globalInt = globalInt + 1;
        System.out.println(globalInt);
        // System.out.println(clientContext);
        System.out.println("zero arg constructor called");
    }

    /** Constructor that sets up a static address 
     *  that will be used by any subsequent intantiations or uses of this class
     *  @param address - a RemoteAddress
     */
    public WeChat(RemoteAddress address) {
        this.address = address;
    }


    private ClientContext makeNewClientContext() {
        ConnectionSource source = new ClientConnectionSource(address.host, address.port);
        Controller controller = new Controller(source);
        View view = new View(source);

        return new ClientContext(controller, view);
    }

    /** 
     * Creates a new ClientContext and returns an 'id'
     * @return an 'id' that can be used to refer to the just-generated ClientContext later
     */
    @GET
    @Path("initclientcontext")
    @Produces(MediaType.APPLICATION_JSON)
    public String initClientContext() {

        ClientContext newClientContext = makeNewClientContext();
        
        // TODO this seems unsafe, because the web client could then just switch their 'clientContextId' and mess with other user accounts
        // TODO this is not threadsafe
        clientContextCounter++;
        String clientContextId = clientContextCounter.toString();

        allClientContexts.put(clientContextId, newClientContext);

        JSONObject obj = new JSONObject();
        obj.put("clientContextId", clientContextId);
        System.out.println(obj.toJSONString());
        return obj.toJSONString();
    }


    private ClientContext getCurrClientContext(String clientContextId) {
        ClientContext currClientContext = (allClientContexts.containsKey(clientContextId)) ? allClientContexts.get(clientContextId) : null;
        
        return currClientContext;
    }

    

    /**
     * Parses a string into JSON
     * Currently, assumes that the string is formatted as a JSON would be
     * @param jsonAsString - a string in json format to parse
     * @return the JSONObject resulting from parsing jsonAsString
     * @throws ParseException
     */
    // TODO: Casting is usually not ideal -- look into whether its possible to avoid casting
    private JSONObject stringToJson(String jsonAsString) throws ParseException {
        JSONParser parser = new JSONParser();

        Object jsonAsObject = parser.parse(jsonAsString);

        JSONObject jsonAsJSONObject = (JSONObject) jsonAsObject;

        return jsonAsJSONObject;
    }





    /**
     * Creates a new user account
     * @param jsonAsString - a string in json format with 'username', 'password', and 'clientContextId' fields 
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
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

            // Get current clientContext
            String clientContextId = (String) jsonObject.get("clientContextId");
            ClientContext clientContext = getCurrClientContext(clientContextId);

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
     * @param jsonAsString - a string in json format with 'username', 'password', and 'clientContextId' fields
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
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

            // Get current clientContext
            String clientContextId = (String) jsonObject.get("clientContextId");
            ClientContext clientContext = getCurrClientContext(clientContextId);

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
     * @param clientContextId - the id of the client's current clientContext
     * @return String such that each user's username is on a new line
     */
    @POST
    @Path("showallusers")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    public String showAllUsers(String clientContextId) {

        // Get current clientContext
        ClientContext clientContext = getCurrClientContext(clientContextId);


        StringBuilder toRet = new StringBuilder();

        clientContext.user.updateUsers();
        for (User u : clientContext.user.getUsers()) {
          toRet.append(u.name);
          toRet.append("\n");
        }

        return toRet.toString();
    }



    /**
     * @param clientContextId - the id of the client's current clientContext
     * @return All the details of all the open conversations as a JSON list 
     * that contains the title, uuid, owner name, and creation date for each chat
     */
    /* TODO: rather than reloading all the conversations every time this is called, 
       might be better to have a method that only sends previously unrequested 
       conversations (to avoid wasted duplicate work) */
    @POST
    @Path("showallconvos")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String showAllConvos(String clientContextId) {

        // Get current clientContext
        ClientContext clientContext;

        if (! clientContextId.equals("")) {
            clientContext = getCurrClientContext(clientContextId);
        } else {
            /* if clientContextId is null, this was called before 
            a client context was assigned.
            Since this will happen only once per use of the angular
            client, we will for now simply create a temporary 
            clientcontext that will be used for just this operation. */
            clientContext = makeNewClientContext();
        }

        /* TODO: Might be cleaner to always clear this, but in the case that the
           excecution of this method is interupted by the excecution of
           selectConvo and this IS cleared, runs into problems sometimes
           because the allConvosById is only partially filled, and so it causes 
           an error
           Should be fixed by locking the hashmap here and there
        */
        // allConvosByID.clear();

        
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
     * @param jsonAsString - A JSON object with two fields:
     *                      chosenConvo - the Uuid.toString() of the conversation to select
     *                      clientContextId - the id of the client's current clientContext
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    // TODO: might be a bad idea to send the actual uuid to the web client -- perhaps need to change this and showAllConvos slightly?
    @POST
    @Path("selectconvo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String selectConvo(String jsonAsString) {
        System.out.println("inside selectConvo");

        try {
            JSONObject jsonObject = stringToJson(jsonAsString);
            String chosenConvo = (String) jsonObject.get("chosenConvo");
            
            // Get current clientContext
            String clientContextId = (String) jsonObject.get("clientContextId");
            ClientContext clientContext = getCurrClientContext(clientContextId);

            System.out.println(clientContextId + ": " + clientContext);

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
                obj.put("error", "The current conversation was not changed, " 
                    + "either because there was an error, or because the " 
                    + "same conversation was selected!");
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
     * @param clientContextId - the id of the client's current clientContext
     * @return JSON list of all the messages of the currently selected conversation
     */
    /* TODO: rather than reloading all the messages every time this is called, 
       might be better to have a method that only sends previously unrequested 
       messages (to avoid wasted duplicate work) */
    @POST
    @Path("showcurrentmessages")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String showCurrentMessages(String clientContextId) {
        System.out.println("inside showCurrentMessages");

        // Get current clientContext
        ClientContext clientContext = getCurrClientContext(clientContextId);


        System.out.println(clientContextId + ": " + clientContext);

        JSONArray allMsgs = new JSONArray();

        if (!clientContext.conversation.hasCurrent()) {
            System.out.println("ERROR: No conversation selected.");

        } else if (clientContext.message.currentMessageCount() == 0) {
            System.out.println(" Current Conversation has no messages");

        } else {

            clientContext.message.updateMessages(false);

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
     * @param jsonAsString - A JSON object with two fields:
     *                      msgToSend - the message text to send
     *                      clientContextId - the id of the client's current clientContext
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @POST
    @Path("sendmsg")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String sendMsg(String jsonAsString) {
        System.out.println("inside sendMsg");

        try {
            JSONObject jsonObject = stringToJson(jsonAsString);
            String msgToSend = (String) jsonObject.get("msgToSend");
            
            // Get current clientContext
            String clientContextId = (String) jsonObject.get("clientContextId");
            ClientContext clientContext = getCurrClientContext(clientContextId);

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

        } catch (ParseException e) {
            e.printStackTrace();

            JSONObject obj = new JSONObject();
            obj.put("error", "There was some problem in processing the contents of your request. Try again with different answers, or contact the administrator.");
            System.out.println(obj.toJSONString());
            return obj.toJSONString();
        }
    }


    /**
     * Creates a new conversation
     * @param jsonAsString - A JSON object with two fields:
     *                      convoTitle - title of new conversation
     *                      clientContextId - the id of the client's current clientContext
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @POST
    @Path("createnewconvo")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String createNewConvo(String jsonAsString) {
        System.out.println("inside createNewConvo");


        try {
            JSONObject jsonObject = stringToJson(jsonAsString);
            String convoTitle = (String) jsonObject.get("convoTitle");
            
            // Get current clientContext
            String clientContextId = (String) jsonObject.get("clientContextId");
            ClientContext clientContext = getCurrClientContext(clientContextId);

        
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

        } catch (ParseException e) {
            e.printStackTrace();

            JSONObject obj = new JSONObject();
            obj.put("error", "There was some problem in processing the contents of your request. Try again with different answers, or contact the administrator.");
            System.out.println(obj.toJSONString());
            return obj.toJSONString();
        }
    }


    /**
     * Signs out the current user
     * @param clientContextId - the id of the client's current clientContext
     * @return JSON indicating either sucess (under a 'message' field) or failure (under an 'error' field)
     */
    @POST
    @Path("signoutuser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    public String signOutUser(String clientContextId) {
        
        // Get current clientContext
        ClientContext clientContext = getCurrClientContext(clientContextId);


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




}


