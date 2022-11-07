import java.util.ArrayList;

public class Conversation {
    private String title;
    private String fileName;
    private Customer customer;
    private Seller seller;

    private boolean isDisappearing;

    public Conversation(String title, String fileName, Customer customer, Seller seller) {

    }
    public ArrayList<String> readFile(User user) {
        //TODO implement
        //TODO Individual messages should be labeled with the senders name in the conversation.
        return null;
    }

    public String censorFile() {
        return null;
    }

    public void removeParticipant(User user) {
        //TODO Handle deleted user
    }

    public boolean isParticipant(User user) {
        return user.equals(customer) || user.equals(seller);
    }
    public String getFileName() {
        return fileName;
    }
}
