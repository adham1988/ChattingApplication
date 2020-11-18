/* 
 * Server program that facilitates TCP connections and sends/receives messages and public keys. 
 * 
* AUTUMN SEMESTER 2020 
* @author 20gr552 COMTEK 5, AAU
* @author G. Bonvang, M. Melgaard, A. Taha & J. Velgaard
*/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
  private static Server instance = new Server();

  private Server() {
  }

  public static Server getInstance() {
    return instance;
  }

  ArrayList<ServerThread> clients = new ArrayList<>(); // array list to store all the sockets
  ArrayList<String> publicKeys = new ArrayList<>(); // array list to store all the sockets

  /**
   * Creates a new server class object and runs the connect() method.
   * 
   * @param args
   */
  public static void main(String args[]) {
    Server server = Server.getInstance();
    server.connect();// run the function connect
  }

  /**
   * Takes the message argument and passes them to the sendMessage() method for
   * every connected client via the ServerThread.
   * 
   * @param message      The message to be sent to all clients.
   * @param serverThread The thread used to send the messages.
   */
  public void handleMessage(String message, ServerThread serverThread) {
    for (ServerThread serverSocketThread : this.clients) { // loop over all clients
      System.out.println("Sending message...");
      serverSocketThread.sendMessage(message); // send to all other
    }
  }

  /**
   * Creates a new thread and socket every time a client attempts to connect.
   * 
   */
  @SuppressWarnings("resource")
  public void connect() {

    Socket socket = null;
    ServerSocket serverSocket = null;

    System.out.println("Listening......");
    try {
      serverSocket = new ServerSocket(1200); // // starts server and waits for a connection

    } catch (IOException e) { // Exception to ensure that the flow of the program doesn't break when an
      // exception occurs
      e.printStackTrace();
      System.out.println("Server error");
    }

    while (true) {
      try {
        socket = serverSocket.accept(); // creating socket and waiting for client connection
        System.out.println("connection Established");
        ServerThread serverThread = new ServerThread(socket, this, publicKeys); // handel every socket connection
        serverThread.start(); // start the thread
        clients.add(serverThread); // add the thread to socket list
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Connection Error");
      }
    }
  }
}

// ServerThread to make socket connection whenever a client connect to server socket
class ServerThread extends Thread {

  Server server;
  String line = null; // Using string line to read message from input stream coming from client
  BufferedReader inputStream = null; // returns an input stream for reading bytes from this socket
  PrintWriter outputStream = null; // returns an output stream for writing bytes to this socket (send data to
  // client)
  Socket socket = null;
  ArrayList<String> publicKeys;

  /**
   * Constructor for the ServerThread class.
   * 
   * @param socket     Socket thread
   * @param server     Server object
   * @param publicKeys The array list of received public keys.
   */
  public ServerThread(Socket socket, Server server, ArrayList<String> publicKeys) {

    this.socket = socket;
    this.server = server;
    this.publicKeys = publicKeys;

  }

  /**
   * Method to forward messages. It takes parameter message and then write it to
   * socket using outputStream.prinln
   * 
   * @param message The string to be sent.
   */
  public void sendMessage(String message) {
    // this.outputStream.print(message);

    outputStream.println(message);
    outputStream.flush(); // forces any buffered output bytes to be written out
    System.out.println("Message forwarded.");

  }

  /**
   * 
   *
   */
  public void run() {

    try {

      inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      outputStream = new PrintWriter(socket.getOutputStream());

    } catch (IOException e) {
      System.out.println("IO error in server thread");
    }

    try {
      line = inputStream.readLine(); // using string line to read message from input stream coming from client
      while (true) {// while line input inputStream different from "QUIT"
        String[] parts = line.split("--");

        // Compare the amount of parts in the string to distinguish between message and
        // public key.
        if (parts.length == 2) { // Public key received.
          System.out.println("Public key received!");
          publicKeys.add(line); // Adds to list of publicKeys
          if (publicKeys.size() == 2) {
            System.out.println("Both public keys have received! Sending...");
            String message = publicKeys.get(0) + "--" + publicKeys.get(1);
            this.server.handleMessage(message, this);
            outputStream.flush();
          } else {
            outputStream.flush();
            line = inputStream.readLine();
          }
        } else { // Sends message normally.
          System.out.println("A message has been received. Forwarding...");
          this.server.handleMessage(line, this);
          outputStream.flush();
          line = inputStream.readLine();
        }
      }
    } catch (Exception e) {
      line = this.getName(); // reused String line for getting thread name
      System.out.println("IO Error/ Client " + line + " terminated abruptly");

    } finally {
      try {
        System.out.println("Connection Closing..");
        if (inputStream != null) {
          inputStream.close();
          System.out.println(" Socket Input Stream Closed");
        }

        if (outputStream != null) {
          outputStream.close();
          System.out.println("Socket Out Closed");
        }

        if (socket != null) {
          socket.close();
          System.out.println("Socket Closed");
        }

      } catch (IOException ie) {
        System.out.println("Socket Close Error");
      }
    }
  }
}
