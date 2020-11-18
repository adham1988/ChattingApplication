/* 
 * Class that generates an asymmetric keypair and enables encryption/decryption in the Client program.  
 * 
* AUTUMN SEMESTER 2020 
* @author 20gr552 COMTEK 5, AAU
* @author G. Bonvang, M. Melgaard, A. Taha & J. Velgaard
*/
package client;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

/**
 * 
 * @param privateKey used to decrypt incoming messages.
 * @param publicKey  used to send to the other client so they may encrypt
 *                   messages.
 */
public class CryptFunc {

  // Attributes
  private PrivateKey privateKey;
  private PublicKey publicKey;

  // Constructor
  public CryptFunc(PrivateKey priv, PublicKey pub) {
    this.privateKey = priv;
    this.publicKey = pub;
  }

  // Getter
  public PrivateKey getPrivKey() {
    return privateKey;
  }

  // Getter
  public PublicKey getPubKey() {
    return publicKey;
  }

  /**
   * Encrypt method. Takes a text string and returns a byte array. The input
   * string must not exceed 117 bytes.
   * 
   * @param input     the given text string to be encrypted
   * @param publicKey public key used to encrypt with
   * @return returns the encrypted text in a byte array
   * @throws Exception
   */
  public static byte[] encrypt(String input, PublicKey publicKey) throws Exception {
    try {

      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

      byte[] inputBytes = input.getBytes(); // Input to byte array
      cipher.init(Cipher.ENCRYPT_MODE, publicKey); // Initialize in encryption mode
      byte[] cipherText = cipher.doFinal(inputBytes); // Encrypts the input to cipherText
      return cipherText;

    } catch (

    NoSuchAlgorithmException e) {

      System.out.println("Exception thrown : " + e);
    }
    return null;
  }

  /**
   * Decrypt method. Takes an encrypted byte array and returns a string.
   * 
   * @param input      the given byte array to be decrypted
   * @param privatekey private key used to decrypt with
   * @return returns the decrypted text in a string
   * @throws Exception
   */
  public String decrypt(byte[] input, PrivateKey privatekey) throws Exception {
    try {

      Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");

      cipher.init(Cipher.DECRYPT_MODE, privatekey); // Initialize in decryption mode
      byte[] plainText = cipher.doFinal(input); // Decrypts the input to plainText
      return new String(plainText); // Returns as string

    } catch (NoSuchAlgorithmException e) {

      System.out.println("Exception thrown : " + e);
    }
    return null;
  }

  /**
   * Initialises and generates a keypair. This keypair is then split into the
   * private and public key. A new object cipherPair is constructed to contain the
   * two keys and the input argument.
   * 
   * @param KEYSIZE size for the generated keys
   * @param input   text string to give the object, meant for encryption
   * @return returns the CryptFunc object cipherPair
   * @throws Exception
   */
  public CryptFunc generateKeyPair(int KEYSIZE) throws Exception {
    try {

      // Initialization
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA"); // Generate keypair using RSA
      SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN"); // Generate secure random number
      // generator
      keyGen.initialize(KEYSIZE, random); // Initialize keygenerator

      // Generate keypair
      KeyPair pair = keyGen.generateKeyPair(); // Generate keypair "pair"
      CryptFunc cipherPair = new CryptFunc(pair.getPrivate(), pair.getPublic());
      return cipherPair;

    } catch (NoSuchAlgorithmException e) {

      System.out.println("Exception thrown : " + e);
    }
    return null;
  }

  // Main function for debugging
  public static void main(String[] argv) throws Exception {
    try {

      // Variables
      int KEYSIZE = 1024;
      String input = "E"; // Max 117 bytes
      PrivateKey privateKey = null; // Initialise empty private key
      PublicKey publicKey = null; // Initialise empty public key

      // Instantiates CryptFunc class
      CryptFunc CryptFunc = new CryptFunc(privateKey, publicKey);

      // Keypair generation
      CryptFunc cipherPair = CryptFunc.generateKeyPair(KEYSIZE);

      System.out.println("Private key is:" + cipherPair.privateKey + "\n" + "Public key is:" + cipherPair.publicKey);

      // Encryption
      byte[] cipherText = CryptFunc.encrypt(input, cipherPair.publicKey);

      System.out.println("cipherText content:" + new String(cipherText, "UTF8"));

      // Decryption
      String plainText = CryptFunc.decrypt(cipherText, cipherPair.privateKey);

      System.out.println("plainText content: " + plainText);
    }

    catch (NoSuchAlgorithmException e) {

      System.out.println("Exception thrown : " + e);
    }
  }
}
