import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class User {
    private String username;
    private final String email;
    private final String password;
    private boolean requestsCensorship;
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;
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
            String blockedUsersString = userString.substring(userString.indexOf("[") + 1,
                    userString.indexOf("]"));
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
            String invisibleUsersString = userString.substring(userString.indexOf("[") + 1,
                    userString.indexOf("]"));
            if (invisibleUsersString.length() > 0) {
                while (!invisibleUsersString.isEmpty()) {
                    String singularUser = invisibleUsersString.substring(0, invisibleUsersString.indexOf(">") + 1);
                    int nextIndex = Math.min(invisibleUsersString.indexOf(">") + 3, invisibleUsersString.length());
                    invisibleUsersString = invisibleUsersString.substring(nextIndex);

                    this.invisibleUsers.add(new User(singularUser, false));
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
        if (this instanceof Customer && user instanceof Seller && !user.getBlockedUsers().contains(this)) {
            conversation = accountsMaster.fetchConversation((Customer) this, (Seller) user);
            if (conversation == null) {
                conversation = accountsMaster.createConversation((Customer) this, (Seller) user);
            }
            try {
                conversation.appendToFile(message, this, user);
            } catch (Exception e) {
                System.out.println("Could Not Send Message");
            }
            conversation.setSellerUnread(true);
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
            conversation.setCustomerUnread(true);
        } else {
            System.out.printf("Cannot Talk to other %ss\n", (this instanceof Seller) ? "Seller" : "Customer");
        }
    }

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
                    System.out.println("Error: Could not edit message!");
                }
            } else {
                System.out.println("You Cannot Edit this Message");
            }
        } catch (Exception e) {
            System.out.println("Error: Could not edit message!");
        }
    }

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
            }
        } catch (Exception e) {
            System.out.println("You cannot delete this message!");
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void updateUserFields() {
        ArrayList<String> detailedUserStrings = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(Main.passwordFilePath))) {
            String detailedUserString = bfr.readLine();

            while (detailedUserString != null) {
                String tempString = detailedUserString.substring(detailedUserString.indexOf("<") + 1,
                        detailedUserString.lastIndexOf(">"));
                String[] splitTempString = tempString.split(", ");
                if (this.email.equals(splitTempString[1])) {
                    if (this instanceof Seller) {
                        detailedUserString = ((Seller) this).detailedToString();
                    } else {
                        detailedUserString = ((Customer) this).detailedToString();
                    }
                }
                detailedUserStrings.add(detailedUserString);
                detailedUserString = bfr.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not update User");
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(Main.passwordFilePath, false))) {
            for (String detailedUserString : detailedUserStrings) {
                pw.println(detailedUserString);
            }
        } catch (Exception e) {
            System.out.println("Could not update User");
        }
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

    public void setBlockedUsers(ArrayList<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
        updateUserFields();
    }

    public void setInvisibleUsers(ArrayList<User> invisibleUsers) {
        this.invisibleUsers = invisibleUsers;
        updateUserFields();
    }

    public void setCensoredWords(ArrayList<String> censoredWords) {
        this.censoredWords = censoredWords;
        updateUserFields();
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
