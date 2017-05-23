package codeu.chat.client.angular;


import java.util.Scanner;

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
     *  controller - a new Controller
     *  view - a new View
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
     * jsonAsString - a string in json format to parse
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
     * jsonAsString - a string in json format with 'username' and 'password' fields 
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
     * jsonAsString - a string in json format with 'username' and 'password' fields 
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


