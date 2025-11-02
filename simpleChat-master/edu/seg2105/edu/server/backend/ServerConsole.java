package edu.seg2105.edu.server.backend;

import java.util.Scanner;

import edu.seg2105.client.common.ChatIF;


public class ServerConsole implements ChatIF {

  private EchoServer server;
  private Scanner fromConsole;

  public ServerConsole(EchoServer server) {
    this.server = server;
    fromConsole = new Scanner(System.in);
  }


  public void accept() {
    try {
      String message;
      while (true) {
        message = fromConsole.nextLine();
        server.handleMessageFromServerUI(message);
      }
    } catch (Exception ex) {
      System.out.println("Unexpected error while reading from server console!");
    }
  }

  @Override
  public void display(String message) {
    System.out.println(message);
  }
}
