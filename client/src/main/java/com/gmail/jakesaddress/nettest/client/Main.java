package com.gmail.jakesaddress.nettest.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Main {

  private static final String progName = "NetTest Client";
  private static final String progVer = "0.1";
  private static final String lineEnd = System.getProperty("line.separator");

  private static String host;
  private static int port = 50505;

  private static boolean keepRunning = true;

  public static void main(String[] args) {

    System.out.println(progName + " v" + progVer + " starting");
    parseArgs(args);

    if (host == null || host.isBlank()) {
      System.out.println("Error: -host argument required" + lineEnd);
      showUsage();
    }

    try (
      BufferedReader conIn = new BufferedReader(new InputStreamReader(System.in));
      Socket socket = new Socket(host, port);
      PrintWriter netOut = new PrintWriter(socket.getOutputStream(), true);
      BufferedReader netIn = new BufferedReader(new InputStreamReader(socket.getInputStream()))
    ) {
      System.out.println("Connected to " + host + ":" + port);
      String input;
      while (keepRunning) {
        System.out.print("nettest: ");
        input = conIn.readLine();
        netOut.println(input);
        String response;
        while ((response = netIn.readLine()) != null && !response.equals(".end.")) {
          if (response.equals(".disconnect.")) {
            keepRunning = false;
          } else {
            System.out.println(response);
          }
        }
      }
    } catch (java.net.UnknownHostException uh) {
      System.err.println("Could not connect to host '" + host + "', please verify the name and try again.");
//      uh.printStackTrace();
      System.exit(-1);
    } catch (java.io.IOException ex) {
      System.err.println("Error setting up network:");
      ex.printStackTrace();
      System.exit(-1);
    }

  }

  private static void parseArgs(String[] args) {
    for (int i = 0; i < args.length; i++) {
      switch (args[i]) {
        case ("-host") -> {
          if ((i + 1) >= args.length) {
            System.out.println("-host parameter needs hostname");
            showUsage();
          }
          host = args[i + 1];
          System.out.println("  Set host to '" + host + "'");
          i++;
        }
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

  private static void showUsage() {
    System.out.println("""
            Usage: java -jar client-<VERSION>.jar [options]
              where [options] are:
              
              -host <hostname> - hostname to connect to (required)
              -port <portnum>  - port to connect
          """);
    System.exit(-1);
  }

}
