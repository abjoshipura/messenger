import java.io.*;
import java.util.ArrayList;
/**
 * Conversation
 *
 * The Conversation class acts as the template for any conversation created between a Seller and a Customer.
 * It holds elementary details about a conversation: conversationID, seller, and customer.
 * It also holds a fileName for the destination file where all future Messages will be stored.
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class Conversation {
    private String conversationID;
    private final String fileName;
    private final Seller seller;
    private final Customer customer;
    private boolean sellerUnread;
    private boolean customerUnread;
    /*
    * public Conversation(String conversationString) Constructor that converts a
    * Conversation object String into a Conversation object.
    * This is used while reading Conversations from memory.
     */
    public Conversation(String conversationString) {
        String strippedMessage = conversationString.substring(conversationString.indexOf("<") + 1,
                conversationString.lastIndexOf(">"));

        String[] conversationDetails = strippedMessage.split(", ");
        this.conversationID = conversationDetails[0];
        this.fileName = conversationDetails[1];

        String sellerString = strippedMessage.substring(strippedMessage.indexOf("Seller<"),
                strippedMessage.indexOf("]>") + 2);
        String customerString = strippedMessage.substring(strippedMessage.indexOf("Customer<"),
                strippedMessage.lastIndexOf("]>") + 2);

        this.seller = new Seller(sellerString, true, false);
        this.customer = new Customer(customerString, true);

        this.sellerUnread = Boolean.parseBoolean(conversationDetails[conversationDetails.length - 2]);
        this.customerUnread = Boolean.parseBoolean(conversationDetails[conversationDetails.length - 1]);
    }

    /*
     * public Conversation(String conversationID,
     * String fileName, Seller seller, Customer customer)
     * Constructor that creates a new Conversation object based on indirect input.
     * This is used when creating a new Conversation before appending or writing to conversations.txt.
     */
    public Conversation(String conversationID, String fileName, Seller seller, Customer customer) {
        this.conversationID = conversationID;
        this.fileName = fileName;
        this.seller = seller;
        this.customer = customer;

        this.sellerUnread = false;
        this.customerUnread = false;
    }

    public String getFileName() {
        return fileName;
    }

    public Seller getSeller() {
        return seller;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getConversationID() {
        return conversationID;
    }

    public boolean isCustomerUnread() {
        return customerUnread;
    }
    public boolean isSellerUnread() {
        return sellerUnread;
    }

    public void setConversationID(String conversationID) {
        String oldString = this.toString();
        this.conversationID = conversationID;
        String newString = this.toString();

        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldString, newString);
    }

    public void setCustomerUnread(boolean customerUnread) {
        String oldString = this.toString();
        this.customerUnread = customerUnread;
        String newString = this.toString();

        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldString, newString);
    }

    public void setSellerUnread(boolean sellerUnread) {
        String oldString = this.toString();
        this.sellerUnread = sellerUnread;
        String newString = this.toString();

        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldString, newString);
    }

    public void updateConversationFields() {
        ArrayList<String> conversationStrings = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(Main.conversationsFilePath))) {
            String conversationString = bfr.readLine();
            while (conversationString != null) {
                String tempString = conversationString.substring(conversationString.indexOf("<") + 1,
                        conversationString.lastIndexOf(">"));
                String[] splitTempString = tempString.split(", ");
                if (this.fileName.equals(splitTempString[1])) {
                    conversationString = this.toString();
                }
                conversationStrings.add(conversationString);
                conversationString = bfr.readLine();
            }

        } catch (Exception e) {
            System.out.println("Could not update Conversation");
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(Main.conversationsFilePath, false))) {
            for (String conversationString : conversationStrings) {
                pw.println(conversationString);
            }
        } catch (Exception e) {
            System.out.println("Could not update Conversation");
        }
    }

    public ArrayList<Message> readFileAsPerUser(User user) {
        ArrayList<Message> readMessages = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.fileName))) {
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

    public ArrayList<Message> readFile() {
        ArrayList<Message> readMessages = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.fileName))) {
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

    public boolean writeFile(ArrayList<Message> messages) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.fileName, false))) {
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

    public void appendToFile(String stringMessage, User sender, User recipient) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.fileName, true))) {
            Message message = new Message(stringMessage, sender, recipient);
            pw.println(message);
        } catch (IOException e) {
            System.out.println("Error: Could not Send Message");
        }
    }

    public boolean importTXT(String filePath, User sender, User recipient) {
        StringBuilder readContents = new StringBuilder();
        try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
            String readLine = bfr.readLine();
            while (readLine != null) {
                readContents.append(readLine).append(" ");
                readLine = bfr.readLine();
            }
            appendToFile(readContents.toString(), sender, recipient);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return conversationID.equals(that.conversationID) && fileName.equals(that.fileName) &&
                seller.equals(that.seller) && customer.equals(that.customer);
    }

    @Override
    public String toString() {
        return String.format("Conversation<%s, %s, %s, %s, %b, %b>", this.conversationID, this.fileName,
                this.seller.detailedToStringWithoutStores(), this.customer.detailedToString(), this.sellerUnread,
                this.customerUnread);
    }
}