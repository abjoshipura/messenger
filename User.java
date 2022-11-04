import java.util.ArrayList;
import java.util.Map;

public class User {
    private String name;
    private String email; //TODO ensure uniqueness
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;

    private boolean requestsCensorship;
    private Map<String, String> censoredWords;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public boolean sendMessageToUser(String message, User user) {
        //TODO implement
        //TODO No user may start a message to a user that does not exist.
        return false;
    }

    public boolean sendMessageToConversation(String message, Conversation conversation) {
        //TODO implement
        return false;
    }

    public boolean editMessage(String messageID, String newMessage) {
        //TODO implement
        return false;
    }

    public boolean deleteMessage(String messageID) {
        //TODO implement
        return false;
    }

    public void blockUser(User user) {
        //TODO implement
    }

    public void becomeInvisibleToUser(User user) {
        //TODO implement
    }
}
