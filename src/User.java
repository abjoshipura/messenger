import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String username;
    private final String email;
    private final String password;
    private boolean requestsCensorship;
    private ArrayList<User> blockedUsers; //TODO: WHENEVER ADDED TO, MAKE SURE YOU ADD AS USER NOT CUSTOMER OR SELLER
    private ArrayList<User> invisibleUsers; //TODO: WHENEVER ADDED TO, MAKE SURE YOU ADD AS USER NOT CUSTOMER OR SELLER
    private ArrayList<String> censoredWords;

    public User(String userString, boolean hasDetails) {
        userString = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
        String[] splitTest = userString.split(", ");
        this.username = splitTest[0];
        this.email = splitTest[1];
        this.password = splitTest[2];

        if (hasDetails) {
            this.requestsCensorship = Boolean.parseBoolean(splitTest[3]);

            this.blockedUsers = new ArrayList<>();
            String[] blockedUsersStringArray = userString.substring(userString.indexOf("[") + 1,
                    userString.indexOf("]")).split(", ");
            if (blockedUsersStringArray.length > 1) {
                for (String blockedUserString : blockedUsersStringArray) {
                    this.blockedUsers.add(new User(blockedUserString, false));
                }
            }

            userString = userString.substring(userString.indexOf("]") + 3);
            this.invisibleUsers = new ArrayList<>();
            String[] invisibleUsersStringArray = userString.substring(userString.indexOf("[") + 1,
                    userString.indexOf("]")).split(", ");
            if (invisibleUsersStringArray.length > 1) {
                for (String invisibleUserString : invisibleUsersStringArray) {
                    this.blockedUsers.add(new User(invisibleUserString, false));
                }
            }

            userString = userString.substring(userString.indexOf("]") + 3);
            String censoredWordPairs = userString.substring(userString.indexOf("[") + 1, userString.indexOf("]"));
            this.censoredWords = new ArrayList<>(Arrays.asList(censoredWordPairs.split(", ")));
        } else {
            this.blockedUsers = new ArrayList<>();
            this.invisibleUsers = new ArrayList<>();
            this.censoredWords = new ArrayList<>();
            this.requestsCensorship = false;
        }
    }


    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;

        this.blockedUsers = new ArrayList<>();
        this.invisibleUsers = new ArrayList<>();
        this.censoredWords = new ArrayList<>();
        this.requestsCensorship = false;
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
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return username.equals(user.username) && email.equals(user.email) && password.equals(user.password);
    }
    public void sendMessageToUser(String message, User user, AccountsMaster accountsMaster) {
        Conversation conversation;
        if (this instanceof Customer && user instanceof Seller) {
            conversation = accountsMaster.fetchConversation((Customer) this, (Seller) user);
            if (conversation == null) {
                conversation = accountsMaster.createConversation((Customer) this, (Seller) user);
            }

            try {
                conversation.appendToFile(message, this, user);
            } catch (Exception e) {
                System.out.println("Could Not Send Message");
            }

        } else if (this instanceof Seller && user instanceof Customer) {
            conversation = accountsMaster.fetchConversation((Customer) user, (Seller) this);
            if (conversation == null) {
                conversation = accountsMaster.createConversation((Customer) user, (Seller) this);
            }

            try {
                conversation.appendToFile(message, this, user);
            } catch (Exception e) {
                System.out.println("Could Not Send Message");
            }
        } else {
            System.out.printf("Cannot Talk to other %ss\n", (this instanceof Seller) ? "Seller" : "Customer");
        }
    }

    public void editMessage(Message message, Conversation conversation, String newMessage) {
        try {
            if (message.getSender().equals(this)) {
                ArrayList<Message> readMessages = conversation.readFile(this);
                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage)) {
                        readMessage.setMessage(newMessage);
                    }
                }
                if (conversation.writeFile(readMessages)) {
                    System.out.println("Edited Message to: " + newMessage);
                } else {
                    System.out.println("Error: Could not edit message!");
                }
            } else {
                System.out.println("You Cannot Edit this Message");
            }
        } catch (Exception e) {
            System.out.println("Error: Could not edit message!");
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        //Use Conversation.setConversationID()
        this.username = username;
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

    public void setRequestsCensorship(boolean requestsCensorship) {
        this.requestsCensorship = requestsCensorship;
    }
}
