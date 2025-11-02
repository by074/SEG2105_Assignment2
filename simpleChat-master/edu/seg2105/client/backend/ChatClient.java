// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  private String loginId;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   * @param loginId the user's login id
   */
  
  public ChatClient(String loginId, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    // exercise 3:
    this.loginId = loginId;
    openConnection();
  }
  // Exercise 3b send "#login <loginId>" to the server.
  @Override
  protected void connectionEstablished() {
    try {
      sendToServer("#login " + loginId);
    } catch (IOException e) {
      clientUI.display("Could not send login command to server.");
    }
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
  }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  // Called by the framework when the connection has been closed. (exercise 1a)
  @Override
  protected void connectionClosed() {
    // server closed normally
    clientUI.display("The server has shut down. Client will terminate.");
    System.exit(0);
  }
  
  // exception (exercise 1a)
  @Override
  protected void connectionException(Exception exception) {
    clientUI.display("The server has shut down. Client will terminate.");
    System.exit(0);
  }
  
  //Exercise 2a: Disconnect from server
  public void logoff() {
	    try {
	      if (isConnected()) {
	        closeConnection();
	        clientUI.display("Connection closed.");
	      }
	    } catch (IOException e) {
	      clientUI.display("Error while logging off: " + e.getMessage());
	    }
  }
  
  //Exercise 2a: Reconnect to server.
  public void login() {
	    try {
	      if (!isConnected()) {
	        openConnection();
	        clientUI.display("You are now logged in.");
	      } else {
	        clientUI.display("Already connected to the server.");
	      }
	    } catch (IOException e) {
	      clientUI.display("Could not connect to server.");
	    }
	  }
  
  public String currentHost() {
	    return getHost();
	  }

	  public int currentPort() {
	    return getPort();
	  }
}
//End of ChatClient class
