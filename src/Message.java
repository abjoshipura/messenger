import java.util.*;
import java.time.Instant;
import java.sql.Timestamp;

/**
 * Template class for all messages sent between two Users. Stores elementary details about a message.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */

public class Message {

    /**
     * The time stamp of the message
     */
    private final String TIMESTAMP;

    /**
     * The unique ID of a message
     */
    private final UUID ID;

    /**
     * The content of a message
     */
    private String message;

    /**
     * The sender details of a message
     */
    private final User SENDER;

    /**
     * The recipient details of a message
     */
    private final User RECIPIENT;

    /**
     * The sender's visibility of a message. Helps implement deleting
     */
    private boolean senderVisibility;

    /**
     * The recipient's visibility of a message. Helps implement deleting
     */
    private boolean recipientVisibility;

    /**
     * Parses a Message object's String and converts it into a Message object. Used while reading Message(s)
     * from memory. Inherently calls {@link User#User(String userString, boolean hasDetails)}
     *
     * @param messageString A Message String
     * @see Message#toString() Message toString()
     */
    public Message(String messageString) {
        // Strips the header from the Message object String to help in parsing
        String strippedMessage = messageString.substring(messageString.indexOf("<") + 1,
                messageString.lastIndexOf(">"));
        String[] messageElements = strippedMessage.split(", ");

        this.TIMESTAMP = messageElements[0];
        this.ID = UUID.fromString(messageElements[1]);

        final int NUM_USER_ELEMENTS = 3;  // A constant number that makes
        int indexOfNextElem = 2;  // Counter to keep track of the next element to parse

        // Parses messageElements[] to build a User object String that can be passed to the User constructor
        StringBuilder senderString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + NUM_USER_ELEMENTS; i++) {
            senderString.append(messageElements[i]).append(", ");
        }
        this.SENDER = new User(senderString.toString(), false);
        indexOfNextElem = indexOfNextElem + NUM_USER_ELEMENTS;

        // Parses messageElements[] to build a User object String that can be passed to the User constructor
        StringBuilder recipientString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + NUM_USER_ELEMENTS; i++) {
            recipientString.append(messageElements[i]).append(", ");
        }
        this.RECIPIENT = new User(recipientString.toString(), false);
        indexOfNextElem = indexOfNextElem + NUM_USER_ELEMENTS;

        this.message = messageElements[indexOfNextElem++];
        this.senderVisibility = Boolean.parseBoolean(messageElements[indexOfNextElem++]);
        this.recipientVisibility = Boolean.parseBoolean(messageElements[indexOfNextElem]);
    }

    /**
     * Creates a new Message object with provided parameters. Used when creating a new Message before appending
     * or writing to a conversation file.
     *
     * @param message   The content of the message. Taken from the active user's input
     * @param sender    The sender (active user) of the message
     * @param recipient The recipient of the message. Taken from the Conversation depending on the active user's role
     */
    public Message(String message, User sender, User recipient) {
        Timestamp instant = Timestamp.from(Instant.now());  // Fetches current date-time
        this.TIMESTAMP = instant.toString();

        this.ID = UUID.randomUUID();  // Generates a unique, random ID for each new Message
        this.SENDER = sender;
        this.RECIPIENT = recipient;
        this.message = message;

        this.senderVisibility = true;
        this.recipientVisibility = true;
    }

    /**
     * Accessor method for String TIMESTAMP
     *
     * @return Returns the message's timestamp
     */
    public String getTimeStamp() {
        return this.TIMESTAMP;
    }

    /**
     * Accessor method for UUID ID
     *
     * @return Returns the message's unique ID
     */
    public UUID getID() {
        return this.ID;
    }

    /**
     * Accessor method for User SENDER
     *
     * @return Returns the message's sender
     */
    public User getSender() {
        return SENDER;
    }

    /**
     * Accessor method for User RECIPIENT
     *
     * @return Returns the message's recipient
     */
    public User getRecipient() {
        return RECIPIENT;
    }

    /**
     * Accessor method for String message
     *
     * @return Returns the message's content
     */
    public String getMessage() {
        return message;
    }

    /**
     * Customized (as per parameter user) accessor method for String message to implement censoring.
     *
     * @param user The user viewing a Conversation
     * @return Returns the message's content according to the parameter user's censorship settings
     */
    public String getCensoredMessage(User user) {
        String tempMessage = this.message;
        ArrayList<String> censoredWords = user.getCensoredWords();
        for (String censoredWord : censoredWords) {
            // Replaces censored words with their given / automatically generated replacements
            tempMessage = tempMessage.replaceAll("(?i)\\b" + censoredWord.substring(0, censoredWord.indexOf(":"))
                    + "\\b", censoredWord.substring(censoredWord.indexOf(":") + 1));
        }
        return tempMessage;
    }

    /**
     * Mutator method for String message.
     *
     * @param message The new message content. Used during editing.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Accessor method for boolean senderVisibility
     *
     * @return Returns the visibility of the message by the sender.
     */
    public boolean isSenderVisibility() {
        return senderVisibility;
    }

    /**
     * Mutator method for boolean senderVisibility.
     *
     * @param senderVisibility The new visibility status. Used during deleting.
     */
    public void setSenderVisibility(boolean senderVisibility) {
        this.senderVisibility = senderVisibility;
    }

    /**
     * Accessor method for boolean recipientVisibility
     *
     * @return Returns the visibility of the message by the recipientVisibility.
     */
    public boolean isRecipientVisibility() {
        return recipientVisibility;
    }

    /**
     * Mutator method for boolean recipientVisibility.
     *
     * @param recipientVisibility The new visibility status. Used during deleting.
     */
    public void setRecipientVisibility(boolean recipientVisibility) {
        this.recipientVisibility = recipientVisibility;
    }

    /**
     * Checks the equality of two Message objects. Compares all class fields.
     *
     * @param o Object to be compared with
     * @return Returns whether the equality condition was met
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return senderVisibility == message.senderVisibility && recipientVisibility == message.recipientVisibility
                && ID.equals(message.ID) && this.message.equals(message.message) && TIMESTAMP.equals(message.TIMESTAMP)
                && SENDER.equals(message.SENDER) && RECIPIENT.equals(message.RECIPIENT);
    }

    /**
     * Generates a formatted String of the Message containing all details.
     * <br> <br>
     * General format: <br>
     * Message&lt;timeStamp, id, sender.toString(), recipient.toString(), message, senderVisibility,
     * recipientVisibility&gt;
     *
     * @return Returns the Message object's String
     */
    @Override
    public String toString() {
        return String.format("Message<%s, %s, %s, %s, %s, %b, %b>", this.TIMESTAMP, this.ID, this.SENDER.toString(),
                this.RECIPIENT.toString(), this.message, this.senderVisibility, this.recipientVisibility);
    }

    /**
     * Generates a formatted String of the Message containing only the required details for CSV conversion.
     * (i.e. does not contain sensitive information like passwords)
     * <br> <br>
     * General format: <br>
     * timeStamp, id, sender.csvToString(), recipient.csvToString(), message, senderVisibility, recipientVisibility
     *
     * @return Returns the Message object's String for CSV conversion
     */
    public String csvToString() {
        return String.format("%s, %s, %s, %s, %s, %b, %b", this.TIMESTAMP, this.ID, this.SENDER.csvToString(),
                this.RECIPIENT.csvToString(), this.message, this.senderVisibility, this.recipientVisibility);
    }
}
