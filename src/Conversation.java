import java.io.*;
import java.util.ArrayList;

/**
 * Template class for all conversations created between a Seller and a Customer. Stores elementary details about
 * a conversation. Also stores a destination file's path where all Message(s) are stored in memory.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */
public class Conversation {

    /**
     * The conversationID of the conversation
     */
    private String conversationID;

    /**
     * The destination file path of the conversation
     */
    private final String FILE_NAME;

    /**
     * The participating Seller in the conversation
     */
    private final Seller SELLER;

    /**
     * The participating Customer in the conversation
     */
    private final Customer CUSTOMER;

    /**
     * The read status of the participating Seller in the conversation
     */
    private boolean sellerUnread;

    /**
     * The read status of the participating Customer in the conversation
     */
    private boolean customerUnread;

    /**
     * Parses a Conversation object's String and converts it into a Conversation object.
     * Used while reading Conversation(s) from memory. Inherently calls {@link User#User(String userString,
     * boolean hasDetails)}
     *
     * @param conversationString A Conversation String
     * @see Conversation#toString()
     */
    public Conversation(String conversationString) {
        // Strips the header from the Conversation object String to help in parsing
        String strippedMessage = conversationString.substring(conversationString.indexOf("<") + 1,
                conversationString.lastIndexOf(">"));
        String[] conversationDetails = strippedMessage.split(", ");

        this.conversationID = conversationDetails[0];
        this.FILE_NAME = conversationDetails[1];

        String sellerString = strippedMessage.substring(strippedMessage.indexOf("Seller<"),
                strippedMessage.indexOf("]>") + 2);
        this.SELLER = new Seller(sellerString, true, false);

        String customerString = strippedMessage.substring(strippedMessage.indexOf("Customer<"),
                strippedMessage.lastIndexOf("]>") + 2);
        this.CUSTOMER = new Customer(customerString, true);

        this.sellerUnread = Boolean.parseBoolean(conversationDetails[conversationDetails.length - 2]);
        this.customerUnread = Boolean.parseBoolean(conversationDetails[conversationDetails.length - 1]);
    }

    /**
     * Creates a new Conversation object with provided parameters. Used when creating a new Conversation before
     * appending or writing to a conversations.txt.
     *
     * @param conversationID The conversation's unique ID. Created from participants' usernames
     * @param fileName       The destination file's path that will contain all Message object Strings of Messages
     *                       in this Conversation. Created from participants' email IDs
     * @param seller         The seller in the conversation
     * @param customer       The customer in the conversation
     */
    public Conversation(String conversationID, String fileName, Seller seller, Customer customer) {
        this.conversationID = conversationID;
        this.FILE_NAME = fileName;
        this.SELLER = seller;
        this.CUSTOMER = customer;

        this.sellerUnread = false;
        this.customerUnread = false;
    }

    /**
     * Accessor method for String FILE_NAME
     *
     * @return Returns the conversation's file
     */
    public String getFileName() {
        return FILE_NAME;
    }

    /**
     * Accessor method for Seller SELLER
     *
     * @return Returns the conversation's seller
     */
    public Seller getSeller() {
        return SELLER;
    }

    /**
     * Accessor method for Customer CUSTOMER
     *
     * @return Returns the conversation's customer
     */
    public Customer getCustomer() {
        return CUSTOMER;
    }

    /**
     * Accessor method for String conversationID
     *
     * @return Returns the conversation's ID
     */
    public String getConversationID() {
        return conversationID;
    }


    /**
     * Accessor method for boolean sellerUnread
     *
     * @return Returns the read status of the conversation for the participating Seller.
     */
    public boolean isSellerUnread() {
        return sellerUnread;
    }

    /**
     * Accessor method for boolean customerUnread
     *
     * @return Returns the read status of the conversation for the participating Customer.
     */
    public boolean isCustomerUnread() {
        return customerUnread;
    }

    /**
     * Mutator method that changes the conversationID to the parameter conversationID.
     * Sends request to update conversations.txt for later retrieval.
     *
     * @param writer         The PrintWriter object to be used to send the network request
     * @param conversationID The new conversationID
     */
    public void setConversationID(PrintWriter writer, String conversationID) {
        String oldString = this.conversationID;
        this.conversationID = conversationID;
        String newString = this.conversationID;

        String updateConversationsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" +
                oldString + ";" + newString;
        MessengerClient.sendRequest(writer, updateConversationsRequest);
    }

    /**
     * Mutator method that changes the sellerUnread status to the parameter sellerUnread.
     * Sends request to update conversations.txt for later retrieval.
     *
     * @param writer       The PrintWriter object to be used to send the network request
     * @param sellerUnread The new read status of the conversation for the participating Seller
     */
    public void setSellerUnread(PrintWriter writer, boolean sellerUnread) {
        String oldString = this.toString();
        this.sellerUnread = sellerUnread;
        String newString = this.toString();

        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" +
                oldString + ";" + newString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Mutator method that changes the customerUnread status to the parameter customerUnread.
     * Sends request to update conversations.txt for later retrieval.
     *
     * @param writer         The PrintWriter object to be used to send the network request
     * @param customerUnread The new read status of the conversation for the participating Customer
     */
    public void setCustomerUnread(PrintWriter writer, boolean customerUnread) {
        String oldString = this.toString();
        this.customerUnread = customerUnread;
        String newString = this.toString();

        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" +
                oldString + ";" + newString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Reads this Conversation's destination file to compile an ArrayList&lt;Message&gt; of messages according to the
     * parameter user's deletions (i.e. Message.senderVisibility or Message.recipientVisibility). Inherently calls the
     * Message constructor {@link Message#Message(String messageString)}
     *
     * @param user The user viewing this Conversation
     * @return Returns an ArrayList&lt;Message&gt; of messages according to the parameter user's deletions
     */
    public ArrayList<Message> readFileAsPerUser(User user) {
        ArrayList<Message> readMessages = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.FILE_NAME))) {
            Message message = null;
            String messageLine = bfr.readLine();
            if (messageLine != null) {
                message = new Message(messageLine);
            }

            while (message != null) {
                if ((message.getSender().equals(user) && message.isSenderVisibility()) ||
                        (message.getRecipient().equals(user) && message.isRecipientVisibility())) {
                    readMessages.add(message);
                }
                messageLine = bfr.readLine();
                if (messageLine != null) {
                    message = new Message(messageLine);
                } else {
                    message = null;
                }
            }
            return readMessages;
        } catch (IOException e) {
            return readMessages;
        }
    }

    /**
     * Reads this Conversation's destination file to compile an ArrayList&lt;Message&gt; of messages. Inherently calls
     * the Message constructor {@link Message#Message(String messageString)}
     *
     * @return Returns an ArrayList&lt;Message&gt; of all messages in this Conversation's destination file
     */
    public ArrayList<Message> readFile() {
        ArrayList<Message> readMessages = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.FILE_NAME))) {
            Message message = null;
            String messageLine = bfr.readLine();
            if (messageLine != null) {
                message = new Message(messageLine);
            }
            while (message != null) {
                if ((message.isSenderVisibility()) || message.isRecipientVisibility()) {
                    readMessages.add(message);
                }
                messageLine = bfr.readLine();
                if (messageLine != null) {
                    message = new Message(messageLine);
                } else {
                    message = null;
                }
            }
            return readMessages;
        } catch (IOException e) {
            return readMessages;
        }
    }

    /**
     * Overwrites the all Message objects in parameter messages into the destination file ASIDE FROM those deleted by
     * both Seller and Customer
     *
     * @param messages The ArrayList&lt;Message&gt; of messages to write into the file
     * @return Returns whether the file was overwritten successfully
     */
    public boolean writeFile(ArrayList<Message> messages) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.FILE_NAME, false))) {
            for (Message message : messages) {
                if (message.isSenderVisibility() || message.isRecipientVisibility()) {
                    pw.println(message);
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Appends a new Message object String to the destination file. Inherently calls the Message constructor
     * {@link Message#Message(String messageString)}
     *
     * @param stringMessage The new message's content
     * @param sender        The sender of the new message
     * @param recipient     the recipient of the new message
     * @return Returns whether the new message was appended to the file successfully
     */
    public boolean appendToFile(String stringMessage, User sender, User recipient) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.FILE_NAME, true))) {
            Message message = new Message(stringMessage, sender, recipient);
            pw.println(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Reads the file at the parameter filePath to compile a String message and appends it to this Conversation's
     * destination file. Indirectly calls {@link Conversation#appendToFile(String stringMessage, User sender,
     * User recipient)}
     *
     * @param reader    The BufferedReader object to be used to read network responses
     * @param writer    The PrintWriter object to be used to send the network request
     * @param filePath  The path of the input .txt file to be read
     * @param sender    The sender (active user) of the message
     * @param recipient The recipient of the message. Taken from the Conversation depending on the active user's role
     * @return Returns whether the message was sent successfully
     */
    public boolean importTXT(BufferedReader reader, PrintWriter writer, String filePath, User sender, User recipient) {
        StringBuilder readContents = new StringBuilder();
        try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
            String readLine = bfr.readLine();
            while (readLine != null) {
                readContents.append(readLine).append(" ");
                readLine = bfr.readLine();
            }

            String appendRequest = "[FILE.APPEND]" + this + ";" + readContents + ";" + sender + ";" + recipient;
            MessengerClient.sendRequest(writer, appendRequest);
            return Boolean.parseBoolean(MessengerClient.readResponse(reader));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the equality of two Conversation objects. Compares all class fields ASIDE FROM boolean customerUnread and
     * boolean sellerUnread.
     *
     * @param o Object to be compared with
     * @return Returns whether the equality condition was met
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return conversationID.equals(that.conversationID) && FILE_NAME.equals(that.FILE_NAME) &&
                SELLER.equals(that.SELLER) && CUSTOMER.equals(that.CUSTOMER);
    }

    /**
     * Generates a formatted String of the Conversation containing all details.
     * <br> <br>
     * General format: <br>
     * Conversation&lt;conversationID, FILE_NAME, SELLER.detailedToStringWithoutStores(), customer.detailedToString(),
     * sellerUnread, customerUnread&gt;
     *
     * @return Returns the Message object's String
     */
    @Override
    public String toString() {
        return String.format("Conversation<%s, %s, %s, %s, %b, %b>", this.conversationID, this.FILE_NAME,
                this.SELLER.detailedToStringWithoutStores(), this.CUSTOMER.detailedToString(), this.sellerUnread,
                this.customerUnread);
    }
}