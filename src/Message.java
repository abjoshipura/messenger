import java.util.*;
import java.time.Instant;
import java.sql.Timestamp;


/**
 * Message
 *
 * The Message class behaves as a template for
 * any message sent between Users. It holds
 * elementary details about a message: time
 * stamp, a UUID identifier, the message, the
 * sender (a User object) and the recipient (a User object).
 * It also possesses two attributes: senderVisibility and
 * recipientVisibility to implement DELETING.
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class Message {
    private final String timeStamp;
    private final UUID id;
    private String message;
    private final User sender;
    private final User recipient;
    private boolean senderVisibility;
    private boolean recipientVisibility;

    /*
     * public Message(String messageString) Constructor
     * that converts a Message object String into a Message
     * object. This is used while reading Messages from memory.
     */
    public Message(String messageString) {
        String strippedMessage = messageString.substring(messageString.indexOf("<") + 1,
                messageString.lastIndexOf(">"));

        String[] messageDetails = strippedMessage.split(", ");
        this.timeStamp = messageDetails[0];
        this.id = UUID.fromString(messageDetails[1]);

        int elementsInUserString = 3;
        int indexOfNextElem = 2;
        StringBuilder senderString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            senderString.append(messageDetails[i]).append(", ");
        }
        this.sender = new User(senderString.toString(), false);
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        StringBuilder recipientString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            recipientString.append(messageDetails[i]).append(", ");
        }
        this.recipient = new User(recipientString.toString(), false);
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        this.message = messageDetails[indexOfNextElem++];
        this.senderVisibility = Boolean.parseBoolean(messageDetails[indexOfNextElem++]);
        this.recipientVisibility = Boolean.parseBoolean(messageDetails[indexOfNextElem]);
    }

    /*
     * public Message(String message, User sender, User recipient)
     * Constructor that creates a new Message object based on input.
     * This is used when creating a new Messages before appending
     * or writing to a conversation file.
     */
    public Message(String message, User sender, User recipient) {
        Timestamp instant = Timestamp.from(Instant.now());
        this.timeStamp = instant.toString();

        this.id = UUID.randomUUID();
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;

        this.senderVisibility = true;
        this.recipientVisibility = true;
    }

    public UUID getID() {
        return this.id;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    /*
     * public String getCensoredMessage(User user)
     * Method that returns the message censored according
     * to the user's settings.
     */
    public String getCensoredMessage(User user) {
        String tempMessage = this.message;
        ArrayList<String> censoredWords = user.getCensoredWords();
        for (String censoredWord : censoredWords) {
            tempMessage = tempMessage.replaceAll("(?i)\\b" + censoredWord.substring(0,
                    censoredWord.indexOf(":")) + "\\b", censoredWord.substring(
                    censoredWord.indexOf(":") + 1));
        }
        return tempMessage;
    }
    
    /*
     * Sets message equal to the new message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /*
     * Returns the visibility of the message of the sender.
     */
     
    public boolean isSenderVisibility() {
        return senderVisibility;
    }

    /*
     * Sets the sender visibility status of the message.
     */
    public void setSenderVisibility(boolean senderVisibility) {
        this.senderVisibility = senderVisibility;
    }
    /*
     * Returns the visibility of the message of the recipient.
     */
    public boolean isRecipientVisibility() {
        return recipientVisibility;
    }
    /*
     * Sets the recipient visibility status of the message.
     */
    public void setRecipientVisibility(boolean recipientVisibility) {
        this.recipientVisibility = recipientVisibility;
    }

    /*
     * Checks whether two messages are equal to one another.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return senderVisibility == message1.senderVisibility && recipientVisibility == message1.recipientVisibility
                && id.equals(message1.id) && message.equals(message1.message) && timeStamp.equals(message1.timeStamp)
                && sender.equals(message1.sender) && recipient.equals(message1.recipient);
    }
    
    /*
     * Formats a message as a String when a Message object is printed.
     */
    @Override
    public String toString() {
        return String.format("Message<%s, %s, %s, %s, %s, %b, %b>", this.timeStamp, this.id,
                this.sender.toString(), this.recipient.toString(), this.message, this.senderVisibility, this.recipientVisibility);
    }
    
    /*
     *Formats a message as a String when importing it to a csv file.
     */
    public String csvToString() {
        return String.format("%s, %s, %s, %s, %s, %b, %b", this.timeStamp, this.id,
                this.sender.csvToString(), this.recipient.csvToString(), this.message, this.senderVisibility, this.recipientVisibility);
    }
}
