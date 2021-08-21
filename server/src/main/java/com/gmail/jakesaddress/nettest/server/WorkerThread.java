package com.gmail.jakesaddress.nettest.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class WorkerThread extends Thread {

  private final Socket socket;
  private boolean disconnect = false;
  private boolean sendShutdown = false;

  WorkerThread(Socket socket) {
    super("WorkerThread");
    this.socket = socket;
  }

  public void run() {
    System.out.println("WorkerThread connected");
    try (
      PrintWriter netOut = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      String inputLine;
      while (!disconnect && ((inputLine = netIn.readLine()) != null)) {
        Main.printConsole(inputLine);
        switch (inputLine) {
          case "exit" -> {
            netOut.println("Exiting");
            netOut.println(".disconnect.");
            disconnect = true;
          }
          case "help" -> {
            netOut.println(showHelp());
          }
          case "shutdown" -> {
            netOut.println("Exiting and shutting down");
            netOut.println(".disconnect.");
            disconnect = true;
            sendShutdown = true;
          }
          default -> {
            netOut.println("Unknown command - " + inputLine);
            netOut.println(showHelp());
          }
        }
        netOut.println(".end.");
      }
      if (sendShutdown) {
        Main.exit();
      }
    } catch(java.io.IOException ex){
      System.err.println("Error during network I/O:");
      ex.printStackTrace();
    }
  }

  private String showHelp() {
    return """
      Possible commands are:
        help     - Show this text
        exit     - Disconnect and exit program.
        shutdown - Shutdown server and exit program.
    """;
  }

}
