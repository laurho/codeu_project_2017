
# CODEU CHAT | README

#
# Overview
This chat application was made as part of the Google CodeU 2017 Spring group. The chat has two ways of being used: through the commandline and through a web GUI. See the Design Doc and the group's personal summaries for more background information.

Note that the Morse Code translation is only available in the Commandline interface.


# Technologies Used
Jersey Framework (https://jersey.java.net/) 
(Allows Java to make and recieve http requests)

Apache Maven (https://maven.apache.org/) 
(Allows easier importing, updating, and managing of libraries)

Node.js (https://nodejs.org/en/)

npm (https://www.npmjs.com/)

Angular (https://angular.io/)

# Building and Running the code
Users will have to have to set up/install 2 things:
1. Maven (to build Java code)
2. Node.js and npm (to build and run Angular code)

Users have 1 option in terms of servers:
1. Running `Java Server`

Users have 2 options in terms of the clients that will connect to the Java Server:
1. Running `Java Web Client` and `Angular Web Client` together concurrently
2. Running `Java Commandline Client`

Instructions for each are below:

### Setting up Maven
1. Download Maven:
    1. `brew install maven` for Mac, 
    2. `sudo apt-get install maven` for Ubuntu, 
    3. or go to https://maven.apache.org/download.cgi)

### Building Java Code with Maven
1. In the terminal, navigate to the home directory of the project
To build the Java project, run the following in the terminal: 
(this step has to be done every time the Java code is changed)
     ```
      mvn clean
      mvn package
      ```

### Setting up Angular and Building Code
1. Download Node.js 6.9.x and npm 3.x.x or newer (https://nodejs.org/en/download/) 
    (Verify which version you have of each by running `node -v` and `npm -v`)
2. In the terminal, navigate to the home directory of the Angular project (`./codeu_angular_client/`)
3. Run the following in the terminal to download packages in `./codeu_angular_client/package.json`: (this has to be done only once)
    ```
    npm install
    ```


### Java Server
1. Follow directions under **Setting up Maven** if not done already.
2. Follow directions under **Building Java Code with Maven** if the code has been changed or if not done already.
2. To run the server, run the following in the terminal 
The arguments are `<Team Id>` , `<Team Secret>` , `<Port>` , `<Relay Address (optional)>`
**NOTE:** only the `<PORT>` argument is used at the moment and it is the port number that you would like the server to run on (any integers can be put in for the other arguments):
    ```
    mvn exec:java -Dexec.args="<Team Id> <Team Secret> <Port> <Relay Address (optional)>" -Dexec.mainClass="com.codeu.Main"
    ```
    Here is the same command with example arguments filled in:
    ```
    mvn exec:java -Dexec.args="1 1 2000" -Dexec.mainClass="com.codeu.Main"
    ```

### Java Web Client
1. Follow directions under **Setting up Maven** if not done already.
2. Follow directions under **Building Java Code with Maven** if the code has been changed or if not done already.
2. To run the web client, run the following in the terminal 
(the argument is: `localhost@<Java Server's Port>`):
    ```
     mvn exec:java -Dexec.args="localhost@<Java Server's Port>" -Dexec.mainClass="com.codeu.WebClientMain"
     ```
    Here is the same command with example arguments filled in:
    ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.WebClientMain"
     ```
     It should then start running on port specified, with additional resources running on port 8080 (this port number can be changed programatically, in `./codeu_angular_client/src/app/appSettings.ts` and `./src/main/java/com/codeu/chat/WebClientMain.java` )


### Angular Web Client
1. Follow directions under **Setting up Angular and Building Code** if not done already and make sure you are in the `./codeu_angular_client/` directory.
2. To run the Angular project, run the following in the terminal
    ```
    npm start
    ```
3. Once it starts, it should automatically direct you to: http://localhost:3000/ or something analogous if port 3000 is busy.

4. Navigate your browser to http://localhost:3001/sync-options and click on `disable all`. This will make it possible for you to open the same link of step 3 up in another tab or window and be able to sign in and chat as another user!



### Commandline Client
1. Follow directions under **Setting up Maven** if not done already.
2. Follow directions under **Building Java Code with Maven** if the code has been changed or if not done already.
2. To run the commandline client, run the following in the terminal
(the arguments are: `localhost@<Server's Port>`):
     ```
     mvn exec:java -Dexec.args="localhost@<Server's Port>" -Dexec.mainClass="com.codeu.CommandLineClientMain"
     ```
     Here is the same command with example arguments filled in:
     ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.CommandLineClientMain"
     ```
3. Type in `help` for a list of commands and explanations for each. 
Password security requires the form of `u-add <username>,<password>` if you were to make a new account. All messages are stored as they were typed, but if you would like the Morse Code translation, type the command `m-show <count>`, where count is the number of messages starting from the most recently sent. This decision was made since Morse Code takes time to listen to and so a user does not have to sit and wait for all messages to be translated with a command such as `m-list-all`. You may choose how many messages you would like to hear translated.


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
