import java.util.ArrayList;
import java.util.Arrays;
/**
 * User
 *
 * The User class acts as the basic template for both Sellers and Customers.
 * It holds the elementary details about a user: username, email, and password.
 * It also holds lists for blocked users, invisible to users,
 * and censored words to implement BLOCKING and CENSORING.
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class User {
    private String username;
    private final String email;
    private String password;
    private boolean requestsCensorship;
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;
    private ArrayList<String> censoredWords;
/*
 * public User(String userString, boolean hasDetails) Constructor that
 * instantiates all User fields to their values by
 * parsing a deepToString() or toString() according to hasDetails.
 */
    public User(String userString, boolean hasDetails) {
        userString = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
        String[] splitUserString = userString.split(", ");
        this.username = splitUserString[0];
        this.email = splitUserString[1];
        this.password = splitUserString[2];

        if (hasDetails) {
            this.requestsCensorship = Boolean.parseBoolean(splitUserString[3]);

            this.blockedUsers = new ArrayList<>();
            String blockedUsersString = userString.substring(userString.indexOf("[") + 1, userString.indexOf("]"));
            if (blockedUsersString.length() > 0) {
                while (!blockedUsersString.isEmpty()) {
                    String singularUser = blockedUsersString.substring(0, blockedUsersString.indexOf(">") + 1);
                    int nextIndex = Math.min(blockedUsersString.indexOf(">") + 3, blockedUsersString.length());
                    blockedUsersString = blockedUsersString.substring(nextIndex);

                    this.blockedUsers.add(new User(singularUser, false));
                }
            }

            userString = userString.substring(userString.indexOf("]") + 3);
            this.invisibleUsers = new ArrayList<>();
            String invisibleUsersString = userString.substring(userString.indexOf("[") + 1, userString.indexOf("]"));
            if (invisibleUsersString.length() > 0) {
                while (!invisibleUsersString.isEmpty()) {
                    String singularUser = invisibleUsersString.substring(0, invisibleUsersString.indexOf(">") + 1);
                    int nextIndex = Math.min(invisibleUsersString.indexOf(">") + 3, invisibleUsersString.length());
                    invisibleUsersString = invisibleUsersString.substring(nextIndex);

                    this.invisibleUsers.add(new User(singularUser, false));
                }
            }

            userString = userString.substring(userString.indexOf("]") + 3);
            this.censoredWords = new ArrayList<>();
            String censoredWordPairs = userString.substring(userString.indexOf("[") + 1, userString.indexOf("]"));
            if (censoredWordPairs.length() > 0) {
                this.censoredWords = new ArrayList<>(Arrays.asList(censoredWordPairs.split(", ")));
            }
        } else {
            this.requestsCensorship = false;
            this.blockedUsers = new ArrayList<>();
            this.invisibleUsers = new ArrayList<>();
            this.censoredWords = new ArrayList<>();
        }
    }

/*
 * public User(String username, String email, String password)
 * Constructor that creates a new User.
 * Inherently called by Seller and Customer only.
 */
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.requestsCensorship = false;
        this.blockedUsers = new ArrayList<>();
        this.invisibleUsers = new ArrayList<>();
        this.censoredWords = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public ArrayList<User> getBlockedUsers() {
        return blockedUsers;
    }

    public ArrayList<User> getInvisibleUsers() {
        return invisibleUsers;
    }

    public ArrayList<String> getCensoredWords() {
        return censoredWords;
    }

    public boolean isRequestsCensorship() {
        return requestsCensorship;
    }

    public void setUsername(String username) {
        username = username.replaceAll("[,<>]", "_");

        String oldUserID = this.username + ", " + this.email + ", " + this.password;
        String newUserID = username + ", " + this.email + ", " + this.password;
        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserID, newUserID);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserID, newUserID);

        for (Conversation conversation : AccountsMaster.conversationArrayList) {
            if (this instanceof Seller && conversation.getSeller().equals(this)) {
                String newConversationID = conversation.getCustomer().getUsername() + "TO" + username;
                conversation.setConversationID(newConversationID);
            } else if (this instanceof Customer && conversation.getCustomer().equals(this)) {
                String newConversationID = username + "TO" + conversation.getSeller().getUsername();
                conversation.setConversationID(newConversationID);
            }
            AccountsMaster.replaceStringInFile(conversation.getFileName(), oldUserID, newUserID);
        }

        this.username = username;
    }

    public void setPassword(String password) {
        String oldUserID = this.username + ", " + this.email + ", " + this.password;
        String newUserID = this.username + ", " + this.email + ", " + password;
        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserID, newUserID);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserID, newUserID);

        for (Conversation conversation : AccountsMaster.conversationArrayList) {
            if (this.isParticipantOf(conversation)) {
                AccountsMaster.replaceStringInFile(conversation.getFileName(), oldUserID, newUserID);
            }
        }

        this.password = password;
    }

    public void setRequestsCensorship(boolean requestsCensorship) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.requestsCensorship = requestsCensorship;
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.requestsCensorship = requestsCensorship;
            newUserString = ((Customer) this).detailedToString();
        }
        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void addBlockedUser(User blockedUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.blockedUsers.add(blockedUser);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.blockedUsers.add(blockedUser);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void removeBlockedUser(User blockedUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.blockedUsers.remove(blockedUser);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.blockedUsers.remove(blockedUser);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void addInvisibleUser(User invisibleUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.invisibleUsers.add(invisibleUser);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.invisibleUsers.add(invisibleUser);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void removeInvisibleUser(User invisibleUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.invisibleUsers.remove(invisibleUser);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.invisibleUsers.remove(invisibleUser);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void addCensoredWord(String censoredWord) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.censoredWords.add(censoredWord);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.censoredWords.add(censoredWord);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void removeCensoredWord(int index) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            this.censoredWords.remove(index);
            newUserString = ((Seller) this).detailedToString();
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.censoredWords.remove(index);
            newUserString = ((Customer) this).detailedToString();
        }

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }
/*
 * public boolean sendMessageToUser(String message, User user, AccountsMaster accountsMaster)
 * Method to send a message to User ONLY IF User has not blocked this User.
 * Appends a new Message object String to the Conversation file.
 * Sets the recipient's Conversation status to UNREAD.
 */
    public boolean sendMessageToUser(String message, User user, AccountsMaster accountsMaster) {
        Conversation conversation;
        if (this instanceof Customer && user instanceof Seller && !user.getBlockedUsers().contains(this)) {
            conversation = accountsMaster.fetchConversation((Customer) this, (Seller) user);
            if (conversation == null) {
                conversation = accountsMaster.createConversation((Customer) this, (Seller) user);
            }
            try {
                conversation.appendToFile(message, this, user);
            } catch (Exception e) {
                return false;
            }
            conversation.setSellerUnread(true);
            return true;
        } else if (this instanceof Seller && user instanceof Customer && !user.getBlockedUsers().contains(this)) {
            conversation = accountsMaster.fetchConversation((Customer) user, (Seller) this);
            if (conversation == null) {
                conversation = accountsMaster.createConversation((Customer) user, (Seller) this);
            }
            try {
                conversation.appendToFile(message, this, user);
            } catch (Exception e) {
                return false;
            }
            conversation.setCustomerUnread(true);
            return true;
        } else {
            return false;
        }
    }
/*
 * public void editMessage(Message message, Conversation conversation, String newMessage)
 * Method to edit a message in a Conversation ONLY IF this User is the sender of the Message.
 */
    public void editMessage(Message message, Conversation conversation, String newMessage) {
        try {
            if (message.getSender().equals(this)) {
                ArrayList<Message> readMessages = conversation.readFile();
                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage)) {
                        readMessage.setMessage(newMessage);
                    }
                }
                if (conversation.writeFile(readMessages)) {
                    System.out.println("Edited Message to: " + newMessage);
                } else {
                    System.out.println("Error: Could not Edit Message");
                }
            } else {
                System.out.println("You Cannot Edit this Message");
            }
        } catch (Exception e) {
            System.out.println("Error: Could not Edit Message");
        }
    }
/*
 public void deleteMessage(Message message, Conversation conversation)
 * Method to delete a message in a Conversation. Sets the Message's corresponding
 * senderVisibility or recipientVisibility to false.
 */
    public void deleteMessage(Message message, Conversation conversation) {
        try {
            if (this.isParticipantOf(conversation)) {
                ArrayList<Message> readMessages = conversation.readFile();
                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage) && message.getSender().equals(this)) {
                        readMessage.setSenderVisibility(false);
                    } else if (message.equals(readMessage) && message.getRecipient().equals(this)) {
                        readMessage.setRecipientVisibility(false);
                    }
                }
                conversation.writeFile(readMessages);
            } else {
                System.out.println("You Cannot Delete this Message");
            }
        } catch (Exception e) {
            System.out.println("Error: Could not Delete Message");
        }
    }

    public boolean isParticipantOf(Conversation conversation) {
        return this.equals(conversation.getSeller()) || this.equals(conversation.getCustomer());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return email.equals(user.email);
    }

    @Override
    public String toString() {
        return String.format("User<%s, %s, %s>", this.username, this.email, this.password);
    }
    
    public String csvToString() {
        return String.format("%s",this.email);
    }
}
