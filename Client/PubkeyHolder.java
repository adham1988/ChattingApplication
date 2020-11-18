/* 
 * Class that holds the public key modulus and exponent.
 * Used in the Client program to hold the public key of the other person,
 * which is necessary for asymmetric encryption.
 * 
* AUTUMN SEMESTER 2020 
* @author 20gr552 COMTEK 5, AAU
* @author G. Bonvang, M. Melgaard, A. Taha & J. Velgaard
*/
package client;

import java.math.BigInteger;

/**
 * Stores the public key from the other client as the modulus and exponent, used
 * to recreate the public key and encrypt sent text.
 *
 * @param pubMod  Modulus of the public key
 * @param pubExpo Exponent of the public key
 */
public class PubkeyHolder {

  // Attributes
  private static BigInteger pubMod;
  private static BigInteger pubExpo;

  private static PubkeyHolder pubkeyHolder = new PubkeyHolder(pubMod, pubExpo);

  // Constructor
  private PubkeyHolder(BigInteger pubMod, BigInteger pubExpo) {
    PubkeyHolder.pubMod = pubMod; // Modulus of the public key
    PubkeyHolder.pubExpo = pubExpo;
  }

  // Methods
  public static PubkeyHolder getInstance() {
    return pubkeyHolder;
  }

  public synchronized BigInteger getPubmod() {
    return pubMod;
  }

  public synchronized void setPubmod(BigInteger pubMod) {
    PubkeyHolder.pubMod = pubMod;
  }

  public synchronized BigInteger getPubexpo() {
    return pubExpo;
  }

  public synchronized void setPubexpo(BigInteger pubExpo) {
    PubkeyHolder.pubExpo = pubExpo;
  }
}
