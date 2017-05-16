package com.codeu;

// import org.glassfish.grizzly.http.server.HttpServer;
// import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
// import org.glassfish.jersey.server.ResourceConfig;

// import java.io.IOException;
// import java.net.URI;


// import codeu.chat.server.Server;


// /**
//  * Main class.
//  * Used to create and start the backend server
//  * 
//  * Based off of resources given at: 
//  * https://jersey.java.net/documentation/latest/getting-started.html#new-from-archetype
//  * 
//  * 
//  */
// public class Main {
//     // Base URI the Grizzly HTTP server will listen on
//     public static final String BASE_URI = "http://localhost:8080/myapp/";

//     /**
//      * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
//      * @return Grizzly HTTP server.
//      */
//     public static HttpServer startServer() {
//         // create a resource config that scans for JAX-RS resources and providers
//         // in this com.codu package
//         final ResourceConfig rc = new ResourceConfig().packages("com.codeu");

//         // this line allows programs running on other ports to read what is 
//         // being sent from here (not good in the real world for cyber safety
//         // reasons, but impt while developing)
//         rc.register(new MyCORSFilter());

//         // create and start a new instance of grizzly http server
//         // exposing the Jersey application at BASE_URI
//         return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
//     }

//     /**
//      * Main method.
//      * @param args
//      * @throws IOException
//      */
//     public static void main(String[] args) throws IOException {
//         final HttpServer server = startServer();
//         System.out.println(String.format("Jersey app started with WADL available at "
//                 + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
//         System.in.read();
//         server.stop();
//     }
// }

// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//


import java.io.IOException;

import codeu.chat.common.Relay;
import codeu.chat.common.Secret;
import codeu.chat.server.NoOpRelay;
import codeu.chat.server.RemoteRelay;
import codeu.chat.server.Server;
import codeu.chat.util.Logger;
import codeu.chat.util.RemoteAddress;
import codeu.chat.util.Uuid;
import codeu.chat.util.connections.ClientConnectionSource;
import codeu.chat.util.connections.Connection;
import codeu.chat.util.connections.ConnectionSource;
import codeu.chat.util.connections.ServerConnectionSource;


import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;
// import com.codeu.MyResource;
// import com.codeu.MyCORSFilter;




final class Main {

  private static final Logger.Log LOG = Logger.newLog(Main.class);

  public static void main(String[] args) {

    Logger.enableConsoleOutput();

    try {
      Logger.enableFileOutput("chat_server_log.log");
    } catch (IOException ex) {
      LOG.error(ex, "Failed to set logger to write to file");
    }

    LOG.info("============================= START OF LOG =============================");

    final Uuid id = Uuid.fromString(args[0]);
    final byte[] secret = Secret.parse(args[1]);

    final int myPort = Integer.parseInt(args[2]);

    final RemoteAddress relayAddress = args.length > 3 ?
                                       RemoteAddress.parse(args[3]) :
                                       null;

    try (
        final ConnectionSource serverSource = ServerConnectionSource.forPort(myPort);
        final ConnectionSource relaySource = relayAddress == null ? null : new ClientConnectionSource(relayAddress.host, relayAddress.port)
    ) {

      LOG.info("Starting server...");
      runServer(id, secret, serverSource, relaySource);

    } catch (IOException ex) {

      LOG.error(ex, "Failed to establish connections");

    }
  }

  private static void runServer(Uuid id,
                                byte[] secret,
                                ConnectionSource serverSource,
                                ConnectionSource relaySource) {

    final Relay relay = relaySource == null ?
                        new NoOpRelay() :
                        new RemoteRelay(relaySource);

    final Server server = new Server(id, secret, relay);

    LOG.info("Created server.");



    String BASE_URI = "http://localhost:8080/myapp/";
    final ResourceConfig rc = new ResourceConfig().packages("com.codeu");  
    rc.register(new MyCORSFilter());
    HttpServer webserver = GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    System.out.println(String.format("Jersey app started with WADL available at "
            + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
    // System.in.read();
    // webserver.stop();



    while (true) {

      try {

        LOG.info("Established connection...");
        final Connection connection = serverSource.connect();
        LOG.info("Connection established.");

        server.handleConnection(connection);

      } catch (IOException ex) {
        LOG.error(ex, "Failed to establish connection.");
      }
    }
  }
}





