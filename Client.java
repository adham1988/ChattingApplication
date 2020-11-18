/*
 * Main program on the Client side of the chat program.
 *
* AUTUMN SEMESTER 2020
* @author 20gr552 COMTEK 5, AAU
* @author G. Bonvang, M. Melgaard, A. Taha & J. Velgaard
*/
package client;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.Base64;
import java.util.Date;
import javax.crypto.Cipher;

public class Client extends Frame implements ActionListener {
  private static final long serialVersionUID = -1214315176582325743L;

  PrintWriter os = null;
  String username;
  String ip;

  // Boolean to signify connection
  public boolean done = false;

  // Methods to set username, server IP address, and connection status
  public void setusername(String name) {
    this.username = name;
  }

  public void setvalue(boolean connect) {
    this.done = connect;
  }

  public void ipaddress(String ip) {

    this.ip = ip;
  }

  // GUI initialisation
  Frame f = new Frame("SNAP CAT");
  Label l0, l1, l2, l3;
  TextArea area, area2;
  Button b1, b2, b3;
  TextField tf1, tf2;
  Color maroon = new Color(128, 0, 0);
  Color blue = new Color(0, 51, 153);
  Font font = new Font("Verdana", Font.BOLD, 12);
  Font a = new Font("TimesRoman", Font.BOLD, 12);
  Font b = new Font("Helvetica Neue", Font.BOLD, 14);
  Font c = new Font("Jazz LET", 15, 15);
  Font d = new Font("Helvetica Neue", Font.ITALIC, 15);
  Font e = new Font("Helvetica Neue", Font.PLAIN, 12);

  /**
   * Visualises and handles GUI interactions. First window handles choosing a
   * username and IP address to connect to.
   *
   * @param pubkeyHolder Object holding the public key of the opposing chat
   *                     client. Starts out as null which is used to check if
   *                     someone else is online.
   */
  Client(PubkeyHolder pubkeyHolder) {

    // Construct Label l0
    l0 = new Label("Enter a username:");
    l1 = new Label("Type your message below:");
    l2 = new Label("Enter server IP:");
    l3 = new Label("Local IP will be used if no IP is entered");

    // setBounds(int x-coordinate, int y-coordinate, int width, int height)
    l0.setBounds(30, 30, 300, 30);
    l0.setFont(d);
    l0.setForeground(blue);

    l1.setBounds(30, 365, 300, 30);
    l1.setVisible(false);
    l1.setFont(d);
    l1.setForeground(blue);

    l2.setBounds(340, 30, 300, 30);
    l2.setFont(d);
    l2.setForeground(blue);

    l3.setBounds(320, 120, 300, 60);
		l3.setFont(d);
		l3.setForeground(blue);

    area = new TextArea();
    area.setEditable(false);
    area.setBounds(30, 160, 460, 150);
    area.setFont(d);
    area.setForeground(maroon);
    area.setVisible(false);

    area2 = new TextArea();
    area2.setBounds(30, 400, 460, 100);
    area2.setVisible(false);
    area2.setFont(b);

    tf1 = new TextField();
    tf1.setBounds(30, 80, 80, 30);

    tf2 = new TextField();
    tf2.setBounds(340, 80, 90, 30);

    b1 = new Button("Connect");
    b1.setBounds(500, 80, 70, 50);

    b2 = new Button("Send");
		b2.setBounds(500, 425, 70, 50);
		b2.setVisible(false);

    b3 = new Button("Quit");
		b3.setBounds(500, 80, 70, 50);
		b3.setVisible(false);

    // Saves the username and IP inputs.
    b1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {

        String name = tf1.getText();
        String ip = tf2.getText();
        ipaddress(ip);
        setusername(name);

        boolean connect = true;
        setvalue(connect);

        l0.setText("You are connected as : " + name);

        b1.setVisible(false);
        tf1.setVisible(false);
        tf2.setVisible(false);
        l2.setVisible(false);
        l3.setVisible(false);
        area.setVisible(true);
        area2.setVisible(true);
        b2.setVisible(true);
        b3.setVisible(true);
        l1.setVisible(true);
      }
    });

    // Checks if there is another person online by the value of the public key
    // modulus in the pubkeyHolder. If it is null, chatting is disabled. If it
    // isn't, the client may chat.
    b2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (pubkeyHolder.getPubmod() == null) { // Checks if the opponent public key has been received
          area.append("No one is online. \n");
        } else {
          try { // Opponent public key is received and the user may chat

            String text = area2.getText();
            Date date = new Date();
            long time = date.getTime();
            Timestamp ts = new Timestamp(time);

            // Initialise encryption with opponent public key
            RSAPublicKeySpec spec = new RSAPublicKeySpec(pubkeyHolder.getPubmod(), pubkeyHolder.getPubexpo());
            KeyFactory factory = KeyFactory.getInstance("RSA");
            PublicKey pub = factory.generatePublic(spec);

            // Encrypt using opponent public key from pubkeyHolder and decode
            byte[] cipherText = CryptFunc.encrypt(text.trim(), pub);
            String encoded = Base64.getEncoder().encodeToString(cipherText);

            // Builds message and sends it
            MessageBuilder builder = new MessageBuilder(ts, username, encoded);
            os.println(builder.messsage_build());
            os.flush();
            area.append(DateFormat.getDateTimeInstance().format(ts) + ": " + username + " said: " + text.trim() + "\n");
            area2.setText("");

          } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("Socket read Error");
          }
        }
      }
    });

    b3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

    // this handler is to activate the close button of the panel
    f.addWindowListener(new WindowAdapter() {

      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    });

    // Frame container adds components
    f.add(l0);
    f.add(l1);
    f.add(l2);
    f.add(l3);
    f.add(b1);
    f.add(b2);
    f.add(b3);
    f.add(area);
    f.add(area2);
    f.add(tf1);
    f.add(tf2);

    // determine the size of frame
    f.setSize(600, 600);

    f.setLayout(null);
    f.setVisible(true);
  }

  /**
   * Activates the client program by intialising an empty pubkeyHolder and running
   * the client method with it. Once username and IP is entered in the GUI, the
   * connect method is run with the username and IP parameters.
   *
   * @param args
   * @throws Exception
   */
  public static void main(String args[]) throws Exception {
    PubkeyHolder pubkeyHolder = PubkeyHolder.getInstance();
    Client client = new Client(pubkeyHolder);

    while (!client.done) {
      System.out.println("");
    }
    System.out.println("");
    client.connect(client.username, client.ip, pubkeyHolder);
  }

  /**
   * Connects the user to the server socket.
   *
   * @param name         Username chosen in the GUI
   * @param ip           Server IP address chosen in the GUI
   * @param pubkeyHolder
   * @throws Exception
   */
  public void connect(String name, String ip, PubkeyHolder pubkeyHolder) throws Exception {

    // Sets IP address based on GUI input. Defaults to LocalHost.
    InetAddress address;
    if (ip != null) {
      address = InetAddress.getByName(ip);
    } else {
      address = InetAddress.getLocalHost();
    }

    // Initialize variables
    Socket socket = null;
    String line = null;
    BufferedReader br = null;
    BufferedReader is = null;
    PrivateKey privateKey = null; // Initialise empty private key
    PublicKey publicKey = null; // Initialise empty public key
    int KEYSIZE = 1024; // KEYSIZE for CryptFunc

    // Keypair generation, RSApublickey that can be split in modulus and exponent
    CryptFunc CryptFunc = new CryptFunc(privateKey, publicKey);
    CryptFunc cipherPair = CryptFunc.generateKeyPair(KEYSIZE);
    RSAPublicKey pub = (RSAPublicKey) cipherPair.getPubKey();

    // Connects to the server socket and initialises the reader and writer. Sends
    // public key modulus and exponent to server.
    try {
      socket = new Socket(address, 1200); // You can use static final constant PORT_NUM
      br = new BufferedReader(new InputStreamReader(System.in)); // buffer to take input from terminal
      is = new BufferedReader(new InputStreamReader(socket.getInputStream()));// getInputStream() returns an input
      os = new PrintWriter(socket.getOutputStream()); // create print to write to stream

      // Send public key parts to server
      os.println(pub.getModulus() + "--" + pub.getPublicExponent());
      os.flush();

    } catch (Exception e) {
      e.printStackTrace();
      System.err.print("IO Exception");
    }

    // Starts a reader thread with the socket inputstream.
    try {
      Thread reader = new Reader(is, this.area, cipherPair, pubkeyHolder);
      reader.start();
      line = br.readLine();

    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("Socket read Error");

    } finally {
      is.close();
      os.close();
      br.close();
      socket.close();
      System.out.println("Connection Closed");
    }
  }

  /*
   * Reader thread using the socket input stream.
   *
   * @param in is the inputstream reader.
   *
   * @param textArea is the window in the chatroom GUI showing messages.
   *
   * @param cipherPair is the keypair generated on this client.
   *
   * @param pubkeyHolder is the object used to pass the opponent client's public
   * key to.
   */
  class Reader extends Thread {

    private BufferedReader in;
    private TextArea textArea;
    private CryptFunc cipherPair;
    PubkeyHolder pubkeyHolder;

    public Reader(BufferedReader in, TextArea textArea, CryptFunc cipherPair, PubkeyHolder pubkeyHolder) {
      this.in = in;
      this.textArea = textArea;
      this.cipherPair = cipherPair;
      this.pubkeyHolder = pubkeyHolder;
    }

    /*
     * Runs the reader thread. Checks if the read inputstream contains a message or
     * public key, and handles them accordingly.
     *
     */
    public void run() {
      String userInput;
      try {
        while ((userInput = in.readLine()) != null) { // While the buffer contains data
          String[] parts = userInput.split("--"); // Split string to distinguish input

          if (parts.length == 4) { // Received public keys. Split them up to find the other client's public key,
                                   // then save it.

            // Public key 1
            String pubkey1Mod = parts[0];
            String pubkey1Expo = parts[1];

            // Public key 2
            String pubkey2Mod = parts[2];
            String pubkey2Expo = parts[3];

            // Split own public key in modulus and exponent
            RSAPublicKey pub = (RSAPublicKey) cipherPair.getPubKey();

            // Compare received public key modulus to own key modulus. If it's
            // not own, it's the opponent's public key. Save it to the pubkeyHolder.
            if (pub.getModulus().equals(new BigInteger(pubkey1Mod))) { // If Public Key 1 = own,
              pubkeyHolder.setPubmod(new BigInteger(pubkey2Mod)); // set public key 2 in holder.
              pubkeyHolder.setPubexpo(new BigInteger(pubkey2Expo));
              textArea.append("Welcome to the chat! \n"); // Signal that another user has connected, as we got their
                                                          // public key.

            } else { // Otherwise public key 2 is own, and we put public key 1 in holder.
              pubkeyHolder.setPubmod(new BigInteger(pubkey1Mod));
              pubkeyHolder.setPubexpo(new BigInteger(pubkey1Expo));
              textArea.append("Welcome to the chat! \n"); // Signal that another user has connected, as we got their
                                                          // public key.

            }
          } else { // Received message. Split, decode, decrypt and append to text area.
            String ts = parts[0];
            String name = parts[1];
            String text = parts[2];

            if (!username.equals(name)) { // Check if the message comes from the other person
              Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
              cipher.init(Cipher.DECRYPT_MODE, cipherPair.getPrivKey()); // Initialize in decryption mode

              // Decode from Base 64, then decrypt with own private key
              byte[] decoded = Base64.getDecoder().decode(text.getBytes());
              byte[] plainText = cipher.doFinal(decoded); // Decrypts the input to plainText

              // Add message to chat window
              textArea.append(ts + ": " + name + " said: " + new String(plainText) + "\n");
            }
          }
        }
      } catch (

      Exception e) {
        System.exit(1);
      }
    }
  }

  // Abstract void, necessary for the client GUI.
  public void actionPerformed(ActionEvent e) {
  }
}
