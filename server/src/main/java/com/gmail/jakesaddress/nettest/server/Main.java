package com.gmail.jakesaddress.nettest.server;

import java.net.ServerSocket;

public class Main {

  private static final String progName = "NetTest Server";
  private static final String progVer = "0.1";

  private static int port = 50505;

  private static boolean keepRunning = true;

  public static void main(String[] args) {

    System.out.println(progName + " v" + progVer + " starting");
    parseArgs(args);

    try (
      ServerSocket serverSocket = new ServerSocket(port)
    ) {
      System.out.println(progName + " bound to port " + port);
      while (keepRunning) {
        System.out.println("Setting up network listener");
        new WorkerThread(serverSocket.accept()).start();
      }
    } catch (java.net.BindException be) {
      System.err.println("Unable to bind to port " + port + "; verify that the port is not already in use.");
//      be.printStackTrace();
      System.exit(-1);
    } catch (java.io.IOException ex) {
      System.err.println("Error setting up network:");
      ex.printStackTrace();
      System.exit(-1);
    }
  }

  protected static void exit() {
    System.out.println("Shutting down");
    keepRunning = false;
    //TODO: Check that main loop has finished and all connections closed before exiting
    System.exit(0);
  }

  private static void parseArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case ("-port") -> {
          if ((i + 1) >= args.length) {
            System.out.println("-port parameter needs port number");
            showUsage();
          }
          try {
            port = Integer.parseInt(args[i + 1]);
          } catch (Exception ex) {
            System.out.println("Invalid port number '" + args[i + 1] + "'");
            showUsage();
          }
          System.out.println("  Set port to " + port);
          i++;
        }
        default -> {
          System.out.println("Unknown argument: " + args[i]);
          showUsage();
        }
      }
    }
  }

  protected static void printConsole(String message) {
    System.out.println(message);
  }

  private static void showUsage() {
    System.out.println("""
            Usage: java -jar server-<VERSION>.jar [options]
              where [options] are:
              
              -port <portnum>  - port to listen on
          """);
    System.exit(-1);
  }

}