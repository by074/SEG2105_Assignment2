package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginId, String host, int port) 
  {
    try 
    {
      client= new ChatClient(loginId, host, port, this);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error- Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        
        //Exercise 2a:
        if (message.startsWith("#")) {
        	handleCommand(message);
        } else {
        	client.handleMessageFromClientUI(message);
        }
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
  
  // Exercise 2a: handle client-side commands
  private void handleCommand(String cmdLine) {
	    String[] parts = cmdLine.split(" ");
	    String cmd = parts[0];

	    switch (cmd) {
	      // 1. #quit
	      case "#quit":
	        client.quit();
	        break;
	      // 2. #logoff  
	      case "#logoff":
	        client.logoff();
	        break;
	      // 3. #sethost<host>
	      case "#sethost":
	        if (parts.length < 2) {
	          display("Usage: #sethost <host>");
	          break;
	        }
	        if (client.isConnected()) {
	          display("Error: you must logoff before changing the host.");
	        } else {
	          client.setHost(parts[1]);
	          display("Host set to " + parts[1]);
	        }
	        break;
	      // 4. #setport <port>
	      case "#setport":
	        if (parts.length < 2) {
	          display("Usage: #setport <port>");
	          break;
	        }
	        if (client.isConnected()) {
	          display("Error: you must logoff before changing the port.");
	        } else {
	          try {
	            int p = Integer.parseInt(parts[1]);
	            client.setPort(p);
	            display("Port set to " + p);
	          } catch (NumberFormatException e) {
	            display("Invalid port number.");
	          }
	        }
	        break;
	      // 5. #login
	      case "#login":
	        if (client.isConnected()) {
	          display("Already connected.");
	        } else {
	          client.login();
	        }
	        break;
	      // 6. #gethost
	      case "#gethost":
	        display("Current host: " + client.currentHost());
	        break;
	      // 7. #getport
	      case "#getport":
	        display("Current port: " + client.currentPort());
	        break;

	      default:
	        display("Unknown command: " + cmdLine);
	    }
	  }

  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
	// args[0] = loginId (mandatory)
	// args[1] = host (optional)
	// args[2] = port (optional)
	if (args.length < 1) {
	      System.out.println("ERROR - No login ID specified.  Connection aborted");
	      System.exit(0);
	}
	String loginId = args[0];
	// exercise 1b : obtains the port number
    String host =  "localhost";
    int port = DEFAULT_PORT;
    
    if(args.length > 1) {
    	host = args[1];
    	}
    
    if (args.length > 2) {
        try {
          port = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
          System.out.println("Invalid port. Using default " + DEFAULT_PORT);
          port = DEFAULT_PORT;
        }
      }

      ClientConsole chat = new ClientConsole(loginId, host, port);
      chat.accept();
  }
}
//End of ConsoleChat class
