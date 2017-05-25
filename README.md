
# CODEU CHAT SERVER | README



#########################################

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

##### Building Java Code with Maven
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

##### Building Angular Code
1. Download Node.js 6.9.x and npm 3.x.x or newer (https://nodejs.org/en/download/) 
    (Verify which version you have of each by running `node -v` and `npm -v`)
2. In the terminal, navigate to the home directory of the project (`./codeu_angular_client/`)
3. Run the following in the terminal to download packages in `./codeu_angular_client/package.json`:
    ```
    npm install
    ```

At the time of writing, Ruban is unsure whether other steps need to be followed, so if that does not work, you can try going here for other things to try before (https://angular.io/docs/ts/latest/cli-quickstart.html)


##### Java Server
1. Install Maven if not done already.
2. Build the Java code using Maven if not done already.
3. To run the server, run the following in the terminal 
(the arguments are `<Team Id>` , `<Team Secret>` , `<Port>` , `<Relay Address (optional)>`:
    ```
    mvn exec:java -Dexec.args="1 1 2000" -Dexec.mainClass="com.codeu.Main"
    ```

##### Java Web Client
1. Build the Java code using Maven if not done already.
2. To run the web client, run the following in the terminal 
(the arguments are: 'localhost@<Server's Port>'):
    ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.WebClientMain"
     ```
     It should then start running on port specified, with additional resources running on port 8080 (this port number can be changed programatically, but if it is be sure to also change it in `./codeu_angular_client/src/app/appSettings.ts` )

3. Quit with 'control - C' in the terminal when done

##### Angular Web Client
1. Install Node.js and npm if not done already.
2. To run the Angular2 project, run the following in the terminal
    ```
    npm start
    ```
3. Once it starts, it should automatically direct you to: `http://localhost:3000/`

    Navigate to http://localhost:3001/sync-options to disable syncing of clicks, scrolls, and submissions across tabs, if you want to try using two different accounts on two different browser tabs

4. Quit with 'control - C' in the terminal when done
  

##### Commandline Client
1. Build the Java code using Maven if not done already.
2. To run the commandline client, run the following in the terminal
(the arguments are: `localhost@<Server's Port>`):
     ```
     mvn exec:java -Dexec.args="localhost@2000" -Dexec.mainClass="com.codeu.CommandLineClientMain"
     ```

3. Quit with 'control - C' in the terminal when done


# Potentially Helpful Tutorials
For Jersey Framework's JAX-RS APIs: 
http://www.vogella.com/tutorials/REST/article.html


#########################################





## DISCLAIMER

CODEU is a program created by Google to develop the skills of future software
engineers. This project is not an offical Google Product. This project is a
playground for those looking to develop their coding and software engineering
skills.


## ENVIRONMENT

All instructions here are relative to a LINUX environment. There will be some
differences if you are working on a non-LINUX system. We will not support any
other development environment.

This project was built using JAVA 7. It is recommended that you install
JAVA&nbsp;7 when working with this project.


## GETTING STARTED

  There is a single python script build.py that is used to build and run
  the project. There are many ways to run a python script. The syntax
  below should work almost anywhere if the script is in your current directory.

  1. To build the project:
       ```
       $ python build.py clean
       $ python build.py build
       ```

  1. To test the project:
       ```
       $ python build.py run codeu.chat.TestRunner
       ```

  1. To run the project you will need to run both the client and the server. Run
     the following two commands in separate shells:

       ```
       $ python build.py run codeu.chat.ServerMain <team_id> <team_secret> <port> <persistent-dir>
       $ python build.py run codeu.chat.ClientMain "<host>@<port>"
       ```

     You must provide the following startup arguments to `ServerMain`:
     + `<team_id>` and `<team_secret>`: a numeric id for your team, and a secret
       code, which are used to authenticate your server with the Relay server.
       You can specify any integer value for `<team_id>`, and a value expressed
       in hexadecimal format (using numbers `0-9` and letters in the range
       `A-F`) for `<team_secret>` when you launch the server in your local setup
       since it will not connect to the Relay server.
     + `<port>`: the TCP port that your Server will listen on for connections
       from the Client. You can use any value between 1024 and 65535, as long as
       there is no other service currently listening on that port in your
       system. The server will return an error:

         ```
         java.net.BindException: Address already in use (Bind failed)
         ```

       if the port is already in use.
     + `<persistent-dir>`: the path where you want the server to save data between
       runs. This directory must exist when you start the server.

     The startup argument for running `ClientMain` is `<host>@<port>`:
     + `<host>`: the hostname or IP address of the computer on which the server
       is listening. If you are running server and client on the same computer,
       you can use `localhost` here.
     + `<port>`: the port on which your server is listening. Must be the same
       port number you specified when you launched `ServerMain`.
     + The "@" between `<host>` and `<port>` is mandatory.

All running images write informational and exceptional events to log files.
The default setting for log messages is "INFO". You may change this to get
more or fewer messages, and you are encouraged to add more LOG statements
to the code. The logging is implemented in `codeu.chat.util.Logger.java`,
which is built on top of `java.util.logging.Logger`, which you can refer to
for more information.

In addition to your team's client and server, the project also includes a
Relay Server. This is not needed to get your project started. You can start
it locally using `python build.py run codeu.chat.RelayMain <args>`. Look in
`RelayMain.Java` for information about arguments.


## Finding your way around the project

All the source files (except test-related source files) are in
`./src/codeu/chat`.  The test source files are in `./test/codeu/chat`. If you
use the supplied scripts to build the project, the `.class` files will be placed
in `./bin`. There is a `./third_party` directory that holds the jar files for
JUnit (a Java testing framework). Your environment may or may not already have
this installed. The supplied scripts use the version in `./third_party`.

Finally, there are some high-level design documents in the project Wiki. Please
review them as they can help you find your way around the sources.



## Source Directories

The major project components have been separated into their own packages. The
main packages/directories under `src/codeu/chat` are:

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

Some basic infrastructure classes used throughout the project.
