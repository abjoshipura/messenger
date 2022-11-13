import java.io.*;
import java.util.ArrayList;

public class Conversation {
    private String conversationID;
    private final String fileName;
    private final Seller seller;
    private final Customer customer;
    private boolean sellerUnread;
    private boolean customerUnread;

    public Conversation(String conversationString) {
        String strippedMessage = conversationString.substring(conversationString.indexOf("<") + 1,
                conversationString.lastIndexOf(">"));

        String[] conversationDetails = strippedMessage.split(", ");
        this.conversationID = conversationDetails[0];
        this.fileName = conversationDetails[1];

        int elementsInUserString = 3;
        int indexOfNextElem = 2;
        //TODO Update for Customer and Seller
        StringBuilder sellerString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            sellerString.append(conversationDetails[i]).append(", ");
        }
        this.seller = new Seller(sellerString.toString(), false);
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        StringBuilder customerString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            customerString.append(conversationDetails[i]).append(", ");
        }
        this.customer = new Customer(customerString.toString(), false);
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        this.sellerUnread = Boolean.parseBoolean(conversationDetails[indexOfNextElem++]);
        this.customerUnread = Boolean.parseBoolean(conversationDetails[indexOfNextElem]);
    }

    public Conversation(String conversationID, String fileName, Seller seller, Customer customer) {
        this.conversationID = conversationID;
        this.fileName = fileName;
        this.seller = seller;
        this.customer = customer;

        this.sellerUnread = false;
        this.customerUnread = false;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        for (Conversation conversation : AccountsMaster.conversationArrayList) {
            if (conversation.equals(this)) {
                conversation.setConversationID(conversationID);
            }
        }

        ArrayList<String> conversationStrings = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader("conversations.txt"))) {
            String conversationString = bfr.readLine();
            while (conversationString != null) {
                if (conversationString.equals(this.toString())) {
                    this.conversationID = conversationID;
                    conversationString = this.toString();
                }
                conversationStrings.add(conversationString);
                conversationString = bfr.readLine();
            }
        } catch (Exception e) {
            System.out.println("Could Not Update Conversation ID");
        }

        try (PrintWriter pw = new PrintWriter(new FileWriter("conversations.txt", true))) {
            for (String conversationString : conversationStrings) {
                pw.println(conversationString);
            }
        } catch (Exception e) {
            System.out.println("Could Not Update Conversation ID");
        }
    }

    public boolean isCustomerUnread() {
        return customerUnread;
    }

    public void setCustomerUnread(boolean customerUnread) {
        this.customerUnread = customerUnread;
    }

    public boolean isSellerUnread() {
        return sellerUnread;
    }

    public void setSellerUnread(boolean sellerUnread) {
        this.sellerUnread = sellerUnread;
    }

    public boolean importTXT(String filePath, User sender, User recipient) {
        StringBuilder readContents = new StringBuilder();
        try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
            String readLine = bfr.readLine();
            while (readLine != null) {
                readContents.append(readLine).append("\n");
                readLine = bfr.readLine();
            }
            appendToFile(readContents.toString(), sender, recipient);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String censorFile() {
        return null;
    }

    public void removeParticipant(User user) {
        //TODO Handle deleted user
    }

    public String getFileName() {
        return fileName;
    }

    public User getSeller() {
        return seller;
    }

    public User getCustomer() {
        return customer;
    }

    public ArrayList<Message> readFile(User user) {
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

    public boolean appendToFile(String stringMessage, User sender, User recipient) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.fileName, true))) {
            Message message = new Message(stringMessage, sender, recipient);
            pw.println(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conversation that = (Conversation) o;
        return conversationID.equals(that.conversationID) && fileName.equals(that.fileName) &&
                seller.equals(that.seller) && customer.equals(that.customer);
    }

    public String toString() {
        return String.format("Conversation<%s, %s, %s, %s, %b, %b>", this.conversationID, this.fileName,
                this.seller, this.customer, this.sellerUnread, this.customerUnread);
    }
}
