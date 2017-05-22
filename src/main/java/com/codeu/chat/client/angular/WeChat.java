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

    // private static final String PROMPT = ">>";

    private final static int PAGE_SIZE = 10;

    private static boolean alive = true;

    private final SecureRandom random = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    public static int globalInt = 0;



    // Needs to be instantiated from WebClientMain.java
    // It is bad practice for this field to be public, but there currently
    // seems to be no other way to instantiate this with inputs 
    // given to the WebClientMain while having something in place for
    // the reinstantiation of this class that happens every time 
    // the web client is accessed through GET and POST requests
    public static ClientContext clientContext;




    // Argument-less constructor
    public WeChat(){
        // this.clientContext = null;
        globalInt = globalInt + 1;
        System.out.println(globalInt);
        System.out.println(clientContext);
        System.out.println("no arg constructor");
    }

    // Constructor - sets up the Chat Application, so that any further instantiations will work properly
    public WeChat(Controller controller, View view) {
        this.clientContext = new ClientContext(controller, view);
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
    *
    **/
    // TODO: Casting is usually bad practice -- look into whether its possible to avoid casting
    private JSONObject stringToJson(String jsonAsString) throws ParseException {
        JSONParser parser = new JSONParser();

        Object jsonAsObject = parser.parse(jsonAsString);

        JSONObject jsonAsJSONObject = (JSONObject) jsonAsObject;

        return jsonAsJSONObject;
    }




    @POST
    @Path("adduser")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.TEXT_PLAIN)
    // Add a new user.
    //private void addUser(String name, String password) {

    // TODO: catch ParseException
    /* TODO: This recieving of String and converting to JSON means that fields 
        with characters like ':','{','}' could cause problems with the parsing 
        -- need to test to see if this is a problem, and if it is, either start 
        sending actual JSON, or disallow those specific characters */


    public String addUser(String jsonAsString) throws ParseException {
        
        JSONObject jsonObject = stringToJson(jsonAsString);

        String name = (String) jsonObject.get("username");
        String password = (String) jsonObject.get("password");

        clientContext.user.addUser(name, password);
        
        JSONObject obj = new JSONObject();
        obj.put("message", "User " + name +" added!" + " with password " + password);
        System.out.println("User " + name +" added!" + " with password " + password);
        return obj.toJSONString();

        //return "User " + name +" added!" + " with password " + password;
    }



    // //// example of simple json syntax
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







    @GET
    @Path("showallusers")
    @Produces(MediaType.TEXT_PLAIN)
    // Add a new user.
    //private void addUser(String name, String password) {
    public String showAllUsers() {

        StringBuilder toRet = new StringBuilder();

        clientContext.user.updateUsers();
        for (User u : clientContext.user.getUsers()) {
          toRet.append(u.name);
          toRet.append("\n");
        }

        // clientContext.user.showAllUsers();
        return toRet.toString();
    }











}


