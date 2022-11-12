import java.io.*;
import java.util.ArrayList;

public class Conversation {
    private String conversationID;
    private final String fileName;
    private final Seller seller;
    private final Customer customer;
    private boolean sellerUnread;
    private boolean customerUnread;

    public Conversation(String conversationID, String fileName, Seller seller, Customer customer) {
        this.conversationID = conversationID;
        this.fileName = fileName;
        this.seller = seller;
        this.customer = customer;

        this.sellerUnread = false;
        this.customerUnread = false;
    }
    
    public Conversation(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] lineArray = br.readLine().split(",");
            this.fileName = fileName;
            this.customer = Seller.searchCustomers(lineArray[0], AccountHandler.getUserArrayList());
            this.seller = Customer.searchSeller(lineArray[1], AccountHandler.getUserArrayList());
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void setCustomerUnread(boolean customerUnread) {
        this.customerUnread = customerUnread;
    }

    public void setSellerUnread(boolean sellerUnread) {
        this.sellerUnread = sellerUnread;
    }

    public ArrayList<Message> readFile(User user) {
        ArrayList<Message> readMessages = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.fileName))) {
            Message message = null;

            bfr.readLine();
            String messageLine = bfr.readLine();

            if (messageLine != null) {
                message = new Message(messageLine);
            }

            while (message != null && ((message.getSender().equals(user) && message.isSenderVisibility()) || (message.getRecipient().equals(user) && message.isRecipientVisibility()))) {
                readMessages.add(message);
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
        //TODO Individual messages should be labeled with the senders name in the conversation.
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

    public boolean appendToFile(String stringMessage, User sender, User recipient, boolean isDisappearing) {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(this.fileName, true))) {
            Message message = new Message(stringMessage, sender, recipient, isDisappearing);
            pw.println(message);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public void toCSV() {
        //TODO Implement
    }

    public void importTXT() {
        //TODO Implement
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
}
