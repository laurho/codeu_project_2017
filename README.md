
# CODEU CHAT SERVER | README

# Overview
  This chat application was made as part of the Google CodeU 2017 Spring group. The chat has two options of viewing: commandline and GUI using Angular. Due to the GUI not being in Java, the persistance of user account, conversations, and messages as well as Morse Code translation is not implemented in it. The CommandLine option lacks the GUI but contains all of the other features.

  The persistance of information is stored within message.db. Password security requires the form of `u-add <username>,<password>` if you were to make a new account. All messages are stored as they were typed, but if you would like the Morse Code translation, type the command `m-show <count>`, where count is the number of messages starting from the most recently sent. This decision was made since Morse Code takes time to listen to and so a user does not have to sit and wait for all messages to be translated with a command such as `m-list-all`. You may choose how many messages you would like to hear translated.

# Technologies Used
Jersey Framework (https://jersey.java.net/) 
(Allows Java to make and recieve http requests)

Apache Maven (https://maven.apache.org/) 
(Allows easier importing, updating, and managing of libraries)

# Building and Running the code
Users will have to have to set up/install 2 things:
1. Build Java code using Maven
2. Build and run Angular code using node.js

Users have 1 option in terms of servers:
1. Run `Java Server`

Users have 2 options in terms of the clients that will connect to the Java Server:
1. Run `Java Web Client` and `Angular Web Client` together
2. Run `Java Commandline Client`

Instructions for each are below:

### Building Java Code with Maven
Download Maven:
1. `brew install maven` for Mac, 
2. `sudo apt-get install maven` for Ubuntu, 
3. or go to https://maven.apache.org/download.cgi)

In the terminal, navigate to the home directory of the project
To build the project, run the following in the terminal:
  ```
  mvn clean
  mvn package
  ```

### Building Angular Code
1. Download Node.js 6.9.x and npm 3.x.x or newer (https://nodejs.org/en/download/) 
    (Verify which version you have of each by running `node -v` and `npm -v`)
2. In the terminal, navigate to the home directory of the project (`./codeu_angular_client/`)
3. Run the following in the terminal to download packages in `./codeu_angular_client/package.json`:
    ```
    npm install
    ```

At the time of writing, Ruban is unsure whether other steps need to be followed, so if that does not work, you can try going here for other things to try before (https://angular.io/docs/ts/latest/cli-quickstart.html)


### Java Server
1. Install Maven if not done already.
2. Build the Java code using Maven if not done already.
3. To run the server, run the following in the terminal 
(the arguments are `<Team Id>` , `<Team Secret>` , `<Port>` , `<Relay Address (optional)>`:
    ```
    mvn exec:java -Dexec.args="1 1 2000" -Dexec.mainClass="com.codeu.Main"
    ```

### Java Web Client
1. Build the Java code using Maven if not done already.
2. To run the web client, run the following in the terminal 
(the arguments are: 'localhost@<Server's Port>'):
    ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.WebClientMain"
     ```
     It should then start running on port specified, with additional resources running on port 8080 (this port number can be changed programatically, but if it is be sure to also change it in `./codeu_angular_client/src/app/appSettings.ts` )

3. Quit with 'control - C' in the terminal when done

### Angular Web Client
1. Install Node.js and npm if not done already.
2. To run the Angular2 project, run the following in the terminal
    ```
    npm start
    ```
3. Once it starts, it should automatically direct you to: `http://localhost:3000/`

    Navigate to http://localhost:3001/sync-options to disable syncing of clicks, scrolls, and submissions across tabs, if you want to try using two different accounts on two different browser tabs

4. Quit with 'control - C' in the terminal when done
  

### Commandline Client
1. Build the Java code using Maven if not done already.
2. To run the commandline client, run the following in the terminal
(the arguments are: `localhost@<Server's Port>`):
     ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.CommandLineClientMain"
     ```

3. Quit with 'control - C' in the terminal when done


## Potentially Helpful Tutorials
For Jersey Framework's JAX-RS APIs: 
http://www.vogella.com/tutorials/REST/article.html

## Testing
  Testing is done through Maven. To test, run the following: 
  ```
  mvn clean package
  ```
  You should be alerted upon the status of the tests. To build the project without the tests, please run the following code:
  ```
  mvn package -Dmaven.test.skip=true
  ```

## Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/main/java/com/codeu/chat` are:

### codeu.chat.client

Classes for building a simple client (`codeu.chat.ClientMain`).

### codeu.chat.server

Classes for building the server (`codeu.chat.ServerMain`).

### codeu.chat.relay

Classes for building the Relay Server (`codeu.chat.RelayMain`). The Relay Server
is not needed to get started.

### codeu.chat.common

Classes that are shared by the clients and servers.

### codeu.chat.util

Some basic infrastructure classes used throughout the project. Includes the password protection and morse code translator features.
