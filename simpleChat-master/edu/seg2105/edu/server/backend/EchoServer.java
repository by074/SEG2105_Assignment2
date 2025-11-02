package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import ocsf.server.*;
import java.io.IOException;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  
  //Exercise 3c: Now also handles "#login <id>" 
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  String message = msg.toString();

	    // 1) check if it is a login command from client
	    if (message.startsWith("#login")) {
	      handleLoginFromClient(message, client);
	      return;
	    }

	    // 2) for any other message, the client MUST have logged in first
	    String loginId = (String) client.getInfo("loginId");
	    if (loginId == null) {
	      // client didn't send #login first → error and disconnect
	      try {
	        client.sendToClient("ERROR - You must login first.");
	      } catch (IOException e) {
	        // ignore
	      }
	      try {
	        client.close();
	      } catch (IOException e) {
	        // ignore
	      }
	      System.out.println("Client did not login first. Connection closed.");
	      return;
	    }

	    // 3) normal message → broadcast with prefix
	    System.out.println("Message received from " + loginId + ": " + message);
	    sendToAllClients(loginId + "> " + message);
  }
  
  private void handleLoginFromClient(String message, ConnectionToClient client) {
	  	// message format: "#login alice"
	    String[] parts = message.split(" ", 2);
	    if (parts.length < 2) {
	      // no id given
	      try {
	        client.sendToClient("ERROR - Login id missing.");
	        client.close();
	      } catch (IOException e) {}
	      return;
	    }

	    String newId = parts[1].trim();

	    // have we already got a login id for this client?
	    String existingId = (String) client.getInfo("loginId");
	    if (existingId != null) {
	      // #login was sent again → not allowed
	      try {
	        client.sendToClient("ERROR - You are already logged in.");
	        client.close();
	      } catch (IOException e) {}
	      System.out.println("Client tried to login again. Closed.");
	      return;
	    }

	    System.out.println("Message received: " + message + " from null.");
	    client.setInfo("loginId", newId);
	    System.out.println(newId + " has logged on.");
	    try {
	        client.sendToClient(newId + " has logged on.");
	    } catch (IOException e) { }

  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  // Exercise 1c: clients connect or disconnect
  @Override
  protected void clientConnected(ConnectionToClient client) {
    System.out.println("Client connected: " + client);
  }

  @Override
  synchronized protected void clientDisconnected(ConnectionToClient client) {
	  Object idObj = client.getInfo("loginId");
	  String loginId = (idObj != null) ? idObj.toString() : "Unknown";
	  
	  System.out.println(loginId + " has disconnected");
  }
  
  //Exercise 2b,c handle messages
  public void handleMessageFromServerUI(String message) {
	  if (message.startsWith("#")) {
	      handleServerCommand(message);
	    } else {
	      // broadcast to clients
	      System.out.println("SERVER MSG> " + message);
	      sendToAllClients("SERVER MSG> " + message);
	    }
  }
  
  private void handleServerCommand(String cmdLine) {
	  String[] parts = cmdLine.split(" ");
	    String cmd = parts[0];

	    switch (cmd) {
	      case "#quit":
	        // close all and exit
	        try {
	          close();
	        } catch (IOException e) {
	          // ignore
	        }
	        System.out.println("Server quitting.");
	        System.exit(0);
	        break;

	      case "#stop":
	        stopListening();
	        System.out.println("Server stopped listening for new clients.");
	        break;

	      case "#close":
	        try {
	          close();
	          System.out.println("Server closed and all clients disconnected.");
	        } catch (IOException e) {
	          System.out.println("Error closing the server: " + e.getMessage());
	        }
	        break;

	      case "#setport":
	        if (parts.length < 2) {
	          System.out.println("Usage: #setport <port>");
	          break;
	        }
	        if (isListening() || getNumberOfClients() > 0) {
	          System.out.println("Cannot set port while server is open or clients are connected.");
	        } else {
	          try {
	            int p = Integer.parseInt(parts[1]);
	            setPort(p);
	            System.out.println("Port set to " + p);
	          } catch (NumberFormatException e) {
	            System.out.println("Invalid port number.");
	          }
	        }
	        break;

	      case "#start":
	        if (!isListening()) {
	          try {
	            listen();
	            System.out.println("Server started listening.");
	          } catch (IOException e) {
	            System.out.println("Error starting server: " + e.getMessage());
	          }
	        } else {
	          System.out.println("Server is already listening.");
	        }
	        break;

	      case "#getport":
	        System.out.println("Current port: " + getPort());
	        break;

	      default:
	        System.out.println("Unknown server command: " + cmdLine);
	    }
  }
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
    
    // start server console (blocking loop)
    ServerConsole console = new ServerConsole(sv);
    console.accept();
  }
}
//End of EchoServer class
