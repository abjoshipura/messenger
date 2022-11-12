import java.util.ArrayList;
import java.util.Map;
import java.io.*;

public class User {
    private String username;
    private String email;
    private String password;
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;
    private boolean requestsCensorship;
    private ArrayList<String> censoredWords;
    private boolean censorWords;
    private int numUnreadMessages;

    public User(String userString) {
        String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
        String[] userDetails = strippedMessage.split(", ");
        this.username = userDetails[0];
        this.email = userDetails[1];
        this.password = userDetails[2];
    }

    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String toString() {
        return String.format("User<%s, %s, %s>", this.username, this.email, this.password);
    }

    public boolean isParticipantOf(Conversation conversation) {
        return this.equals(conversation.getSeller()) || this.equals(conversation.getCustomer());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return username.equals(user.username) && email.equals(user.email) && password.equals(user.password);
    }

    //TODO Shift to Customer and Seller add Time Stamp
//    public boolean sendMessageToUser(String message, User user, boolean isDisappearing) {
    //TODO Handle unique conversation
//        String uniqueConversationID = this.getUsername() + "_" + user.getUsername();
//        Conversation conversation = new Conversation(uniqueConversationID, uniqueConversationID + ".txt",
//                this, user);
//        AccountsMaster.conversationList.add(conversation);
//        try {
//            return conversation.appendToFile(message, this, user, isDisappearing);
//        } catch (Exception e) {
//            return false;
//        }
//    }

    public boolean sendMessageToConversation(String message, Conversation conversation, boolean isDisappearing) {

        try {
            FileWriter fileWriter = new FileWriter(conversation.getFileName(), true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(message);
            printWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMessage(Message message, Conversation conversation, String newMessage) {
        try {
            if (message.getSender().equals(this)) {
                ArrayList<Message> readMessages = conversation.readFile(this);
                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage)) {
                        readMessage.setMessage(newMessage);
                    }
                }
                conversation.writeFile(readMessages);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("You cannot edit this message!");
            return false;
        }
    }

    public boolean deleteMessage(Message message, Conversation conversation) {
        try {
            if (this.isParticipantOf(conversation)) {
                ArrayList<Message> readMessages = conversation.readFile(this);
                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage) && message.getSender().equals(this)) {
                        readMessage.setSenderVisibility(false);
                    } else {
                        readMessage.setRecipientVisibility(false);
                    }
                }
                conversation.writeFile(readMessages);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("You cannot delete this message!");
            return false;
        }
    }

    public void blockUser(User user) {
        blockedUsers.add(user);
    }

    public void becomeInvisibleToUser(User user) {
        invisibleUsers.add(user);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(ArrayList<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public ArrayList<User> getInvisibleUsers() {
        return invisibleUsers;
    }

    public void setInvisibleUsers(ArrayList<User> invisibleUsers) {
        this.invisibleUsers = invisibleUsers;
    }

    public boolean isRequestsCensorship() {
        return requestsCensorship;
    }

    public void setRequestsCensorship(boolean requestsCensorship) {
        this.requestsCensorship = requestsCensorship;
    }

    public ArrayList<String> getCensoredWords() {
        return censoredWords;
    }

    public void setCensoredWords(ArrayList<String> censoredWords) {
        this.censoredWords = censoredWords;
    }
}
