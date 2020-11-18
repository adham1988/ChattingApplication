/* 
 * Builder pattern class used to construct the message sent by the clients. 
 * Takes the timestamp, username and text, then generates a string which seperates
 * each part with --, to be delimited by the Client reader.
 * 
* AUTUMN SEMESTER 2020 
* @author 20gr552 COMTEK 5, AAU
* @author G. Bonvang, M. Melgaard, A. Taha & J. Velgaard
*/
package client;

import java.sql.Timestamp;
import java.text.DateFormat;

/**
 * Builds a string with the parameters, seperated by --.
 *
 * @param timestamp is the current time the message is created
 * @param username  is the name written in the GUI on the Client program
 * @param text      is the message to be sent between clients
 */

public class MessageBuilder {

  private Timestamp timestamp;
  private String username;
  private String text;

  public MessageBuilder(Timestamp timestamp, String username, String text) {
    this.timestamp = timestamp;
    this.username = username;
    this.text = text;
  }

  public String messsage_build() {

    return (DateFormat.getDateTimeInstance().format(timestamp) + "--" + username + "--" + text);
  }

}
