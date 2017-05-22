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


    // @XmlRootElement
    // public static class MyJaxBean {
    //     @XmlElement public String name;
    //     @XmlElement public String password;
    // }


    // @POST
    // @Path("adduser")
    // @Produces(MediaType.TEXT_PLAIN)
    // @Consumes("application/json")
    // // Add a new user.
    // //private void addUser(String name, String password) {
    // public String addUser(MyJaxBean input) {

    //     // TODO: remove
    //     System.out.println(input.name + "   " + input.password);


    //     //String name1 = "Bobby" + globalInt; String password1 = "goat";
    //     clientContext.user.addUser(input.name, input.password);
    //     return "User " + input.name +" added!";
    // }


    @POST
    @Path("adduser")
    @Produces(MediaType.TEXT_PLAIN)
    @Consumes(MediaType.TEXT_PLAIN)
    // Add a new user.
    //private void addUser(String name, String password) {
    public String addUser(String input) {

        // TODO: remove
        System.out.println(input);

        String[] data = input.split("\\|\\|\\|");

        for (String dat : data) {
            System.out.println(dat);
        }
        String name = data[0];
        String password = data[1];


        // String name1 = "Bobby" + globalInt; String password1 = "goat";
        clientContext.user.addUser(name, password);
        return "User " + name +" added!" + " with password " + password;
    }


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


