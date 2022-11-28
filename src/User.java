import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Template class for all Users (both Sellers and Customers). Stores elementary details about a user.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */
public class User {

    /**
     * The username of the User
     */
    private String username;

    /**
     * The email of the User
     */
    private final String EMAIL;

    /**
     * The password of the User
     */
    private String password;

    /**
     * The status of whether the user wants to censor messages
     */
    private boolean requestsCensorship;

    /**
     * The ArrayList&lt;User&gt; of users blocked by the User
     */
    private ArrayList<User> blockedUsers;

    /**
     * The ArrayList&lt;User&gt; of users this User is invisible to
     */
    private ArrayList<User> invisibleUsers;

    /**
     * The ArrayList&lt;String&gt; of censor word pairs for the User
     */
    private ArrayList<String> censoredWords;

    /**
     * Parses a User object's String and instantiates User fields to their values.
     * <br> <br>
     * Possible userString values dependent on memory: <br>
     * hasDetails == false => {@link User#toString()} <br>
     * hasDetails == true => {@link Seller#detailedToString()} / {@link Customer#detailedToString()}
     *
     * @param userString A User String
     * @param hasDetails Whether the User String is detailed (i.e. it contains blocked user, invisible user,
     *                   and censored words)
     * @see User#toString()
     * @see Seller#detailedToString()
     * @see Seller#detailedToStringWithoutStores()
     * @see Customer#detailedToString()
     */
    public User(String userString, boolean hasDetails) {
        // Strips userString to a String containing only User object details
        String strippedUserString = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
        String[] splitUserString = strippedUserString.split(", ");
        this.username = splitUserString[0];
        this.EMAIL = splitUserString[1];
        this.password = splitUserString[2];

        if (hasDetails) {
            this.requestsCensorship = Boolean.parseBoolean(splitUserString[3]);

            // Parses the User object details to compile a list of blocked users
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

            // Parses the User object details to compile a list of users this User is invisible to
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

            // Parses the User object details to compile a list of censored word pairs
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

    /**
     * Creates a new User object with provided parameters.
     *
     * @param username The name of the seller
     * @param email    The email of the seller
     * @param password The password of the seller
     */
    public User(String username, String email, String password) {
        this.username = username;
        this.EMAIL = email;
        this.password = password;

        this.requestsCensorship = false;
        this.blockedUsers = new ArrayList<>();
        this.invisibleUsers = new ArrayList<>();
        this.censoredWords = new ArrayList<>();
    }

    /**
     * Accessor method for String username
     *
     * @return Returns the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Accessor method for String email
     *
     * @return Returns the user's email
     */
    public String getEmail() {
        return EMAIL;
    }

    /**
     * Accessor method for String password
     *
     * @return Returns the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Accessor method for ArrayList&lt;User&gt; blockedUsers
     *
     * @return Returns the user's list of blocked users
     */
    public ArrayList<User> getBlockedUsers() {
        return blockedUsers;
    }

    /**
     * Accessor method for ArrayList&lt;User&gt; invisibleUsers
     *
     * @return Returns the user's list of invisible to users
     */
    public ArrayList<User> getInvisibleUsers() {
        return invisibleUsers;
    }

    /**
     * Accessor method for ArrayList&lt;String&gt; censoredWords
     *
     * @return Returns the user's censored word pairs
     */
    public ArrayList<String> getCensoredWords() {
        return censoredWords;
    }

    /**
     * Accessor method for boolean requestsCensorship
     *
     * @return Returns the status of whether the User wants to censor messages
     */
    public boolean isRequestsCensorship() {
        return requestsCensorship;
    }

    /**
     * Checks whether this user is a participant of the parameter conversation
     *
     * @param conversation The conversation of which this user is or is not a participant of
     * @return Returns the whether this user is a participant of the parameter conversation independent of the role
     */
    public boolean isParticipantOf(Conversation conversation) {
        return this.equals(conversation.getSeller()) || this.equals(conversation.getCustomer());
    }

    /**
     * Sends network request to update passwords.txt
     *
     * @param writer    The PrintWriter object to be used to send the network request
     * @param oldString The old String that is to be replaced by newString
     * @param newString The new String that is to replace oldString
     */
    public void updateAccountsFile(PrintWriter writer, String oldString, String newString) {
        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.PASSWORD_FILE_PATH + ";" + oldString
                + ";" + newString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Sends network request to update conversations.txt
     *
     * @param writer    The PrintWriter object to be used to send the network request
     * @param oldString The old String that is to be replaced by newString
     * @param newString The new String that is to replace oldString
     */
    public void updateConversationsFile(PrintWriter writer, String oldString, String newString) {
        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" + oldString
                + ";" + newString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Mutator method that changes the username to the parameter username.
     * Sends request to update all files for later retrieval.
     *
     * @param reader   The BufferedReader object to be used to read network responses
     * @param writer   The PrintWriter object to be used to send the network request
     * @param username The new username
     */
    public void setUsername(BufferedReader reader, PrintWriter writer, String username) {
        String oldUserID = this.username + ", " + this.EMAIL + ", " + this.password;
        String newUserID = username + ", " + this.EMAIL + ", " + this.password;

        ArrayList<Conversation> conversations = new ArrayList<>();
        String listConversationsRequest = "[LIST.CONVERSATIONS]";
        MessengerClient.sendRequest(writer, listConversationsRequest);
        String stringListOfConversations = MessengerClient.readResponse(reader);

        if (stringListOfConversations != null && stringListOfConversations.length() > 0) {
            String[] listOfConversationStrings = stringListOfConversations.split(";");
            for (String conversationString : listOfConversationStrings) {
                conversations.add(new Conversation(conversationString));
            }
        }

        for (Conversation conversation : conversations) {
            // Updates all Conversation object Strings' conversationIDs in conversations.txt
            if (this instanceof Seller && conversation.getSeller().equals(this)) {
                String newConversationID = conversation.getCustomer().getUsername() + "TO" + username;
                conversation.setConversationID(writer, newConversationID);
            } else if (this instanceof Customer && conversation.getCustomer().equals(this)) {
                String newConversationID = username + "TO" + conversation.getSeller().getUsername();
                conversation.setConversationID(writer, newConversationID);
            }

            // Updates all Message object Strings in each conversation's file
            String updateConversationsRequest = "[FILE.UPDATE]" + conversation.getFileName() + ";" + oldUserID +
                    ";" + newUserID;
            MessengerClient.sendRequest(writer, updateConversationsRequest);
        }

        // Updates all Seller/Customer object Strings in passwords.txt
        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.PASSWORD_FILE_PATH + ";" + oldUserID +
                ";" + newUserID;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
        // Updates all Conversation object Strings in conversations.txt
        String updateConversationsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" +
                oldUserID + ";" + newUserID;
        MessengerClient.sendRequest(writer, updateConversationsRequest);

        this.username = username;
    }

    /**
     * Mutator method that changes the password to the parameter password.
     * Sends request to update all files for later retrieval.
     *
     * @param reader   The BufferedReader object to be used to read network responses
     * @param writer   The PrintWriter object to be used to send the network request
     * @param password The new password
     */
    public void setPassword(BufferedReader reader, PrintWriter writer, String password) {
        String oldUserID = this.username + ", " + this.EMAIL + ", " + this.password;
        String newUserID = this.username + ", " + this.EMAIL + ", " + password;

        ArrayList<Conversation> conversations = new ArrayList<>();
        String listConversationsRequest = "[LIST.CONVERSATIONS]";
        MessengerClient.sendRequest(writer, listConversationsRequest);
        String stringListOfConversations = MessengerClient.readResponse(reader);

        if (stringListOfConversations != null && stringListOfConversations.length() > 0) {
            String[] listOfConversationStrings = stringListOfConversations.split(";");
            for (String conversationString : listOfConversationStrings) {
                conversations.add(new Conversation(conversationString));
            }
        }

        for (Conversation conversation : conversations) {
            // Updates all Message object Strings in conversation's file
            if (this.isParticipantOf(conversation)) {
                String updateConversationsRequest = "[FILE.UPDATE]" + conversation.getFileName() + ";" + oldUserID
                        + ";" + newUserID;
                MessengerClient.sendRequest(writer, updateConversationsRequest);
            }
        }

        // Updates all Seller/Customer object Strings in passwords.txt
        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.PASSWORD_FILE_PATH + ";" + oldUserID
                + ";" + newUserID;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
        // Updates all Conversation object Strings in conversations.txt
        String updateConversationsRequest = "[FILE.UPDATE]" + MessengerClient.CONVERSATIONS_FILE_PATH + ";" +
                oldUserID + ";" + newUserID;
        MessengerClient.sendRequest(writer, updateConversationsRequest);

        this.password = password;
    }

    /**
     * Mutator method that toggles the status of whether the user wants to censor messages.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer The PrintWriter object to be used to send the network request
     */
    public void toggleRequestsCensorship(PrintWriter writer) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.requestsCensorship = !this.requestsCensorship;
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().toggleRequestsCensorship(writer);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else {
            oldUserString = ((Customer) this).detailedToString();
            this.requestsCensorship = !this.requestsCensorship;
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that adds a new User to this user's list of blocked users.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer      The PrintWriter object to be used to send the network request
     * @param blockedUser The new User to be added to this user's list of blocked users
     */
    public void addBlockedUser(PrintWriter writer, User blockedUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && !this.blockedUsers.contains(blockedUser)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.blockedUsers.add(blockedUser);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().addBlockedUser(writer, blockedUser);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.blockedUsers.add(blockedUser);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that removes the parameter User from this user's list of blocked users.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer      The PrintWriter object to be used to send the network request
     * @param blockedUser The User to be removed from this user's list of blocked users
     */
    public void removeBlockedUser(PrintWriter writer, User blockedUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && this.blockedUsers.contains(blockedUser)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.blockedUsers.remove(blockedUser);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().removeBlockedUser(writer, blockedUser);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.blockedUsers.remove(blockedUser);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that adds a new User to this user's list of users this user is invisible to.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer        The PrintWriter object to be used to send the network request
     * @param invisibleUser The new User to be added to this user's list of users this User is invisible to
     */
    public void addInvisibleUser(PrintWriter writer, User invisibleUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && !this.invisibleUsers.contains(invisibleUser)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.invisibleUsers.add(invisibleUser);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().addInvisibleUser(writer, invisibleUser);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.invisibleUsers.add(invisibleUser);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that removes the parameter User from this user's list of users this user is invisible to.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer        The PrintWriter object to be used to send the network request
     * @param invisibleUser The User to be removed from this user's list of users this User is invisible to
     */
    public void removeInvisibleUser(PrintWriter writer, User invisibleUser) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && this.invisibleUsers.contains(invisibleUser)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.invisibleUsers.remove(invisibleUser);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().removeInvisibleUser(writer, invisibleUser);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.invisibleUsers.remove(invisibleUser);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that adds a new censored word pair to this user's list of censored word pairs.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer       The PrintWriter object to be used to send the network request
     * @param censoredWord The new censored word pair to be added to this user's list of censored word pairs
     */
    public void addCensoredWord(PrintWriter writer, String censoredWord) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && !this.censoredWords.contains(censoredWord)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.censoredWords.add(censoredWord);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().addCensoredWord(writer, censoredWord);
            }

            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.censoredWords.add(censoredWord);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Mutator method that removes the parameter censored word pair from this user's list of censored word pairs.
     * Sends request to update passwords.txt and conversations.txt for later retrieval.
     *
     * @param writer       The PrintWriter object to be used to send the network request
     * @param index        The index of the censored word pair to be removed in the user's list of censored word pairs
     * @param censoredWord The censored word pair to be removed from this user's list of censored word pairs
     */
    public void removeCensoredWord(PrintWriter writer, int index, String censoredWord) {
        String oldUserString;
        String newUserString;
        if (this instanceof Seller && this.censoredWords.get(index).equals(censoredWord)) {
            oldUserString = ((Seller) this).detailedToString();
            String oldShortSellerString = ((Seller) this).detailedToStringWithoutStores();
            this.censoredWords.remove(index);
            newUserString = ((Seller) this).detailedToString();
            String newShortSellerString = ((Seller) this).detailedToStringWithoutStores();

            updateAccountsFile(writer, oldUserString, newUserString);
            for (Store store : ((Seller) this).getStores()) {
                store.getSeller().removeCensoredWord(writer, index, censoredWord);
            }
            updateAccountsFile(writer, oldShortSellerString, newShortSellerString);
            updateConversationsFile(writer, oldShortSellerString, newShortSellerString);
        } else if (this instanceof Customer) {
            oldUserString = ((Customer) this).detailedToString();
            this.censoredWords.remove(index);
            newUserString = ((Customer) this).detailedToString();

            updateAccountsFile(writer, oldUserString, newUserString);
            updateConversationsFile(writer, oldUserString, newUserString);
        }
    }

    /**
     * Sends a message to the parameter user ONLY IF this user is not blocked by the parameter user. Creates a new
     * conversation if one does not already exist.
     *
     * @param reader  The BufferedReader object to be used to read network responses
     * @param writer  The PrintWriter object to be used to send the network request
     * @param message The message content of the message to be sent
     * @param user    The user to which the message is being sent
     * @return Returns whether the message was sent successfully
     */
    public boolean sendMessageToUser(BufferedReader reader, PrintWriter writer, String message, User user) {
        if (this instanceof Customer && user instanceof Seller && !user.getBlockedUsers().contains(this)) {
            Conversation conversation = null;
            String fetchRequest = "[FETCH.CONVERSATION]" + ((Customer) this).detailedToString() + ";" +
                    ((Seller) user).detailedToString();
            MessengerClient.sendRequest(writer, fetchRequest);
            String conversationString = MessengerClient.readResponse(reader);

            if (conversationString != null) {
                if (conversationString.isEmpty()) {
                    String createRequest = "[CREATE.CONVERSATION]" + ((Customer) this).detailedToString() + ";" +
                            ((Seller) user).detailedToString();
                    MessengerClient.sendRequest(writer, createRequest);
                    String newConversationString = MessengerClient.readResponse(reader);
                    if (newConversationString != null) {
                        conversation = new Conversation(newConversationString);
                    }
                } else {
                    conversation = new Conversation(conversationString);
                }
            } else {
                return false;
            }

            try {
                if (conversation != null) {
                    String appendRequest = "[FILE.APPEND]" + conversation + ";" + message + ";" + this + ";" + user;
                    MessengerClient.sendRequest(writer, appendRequest);
                    if (!Boolean.parseBoolean(MessengerClient.readResponse(reader))) {
                        return false;
                    }
                    conversation.setSellerUnread(true, writer);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else if (this instanceof Seller && user instanceof Customer && !user.getBlockedUsers().contains(this)) {
            Conversation conversation = null;
            String fetchRequest = "[FETCH.CONVERSATION]" + ((Customer) user).detailedToString() + ";" +
                    ((Seller) this).detailedToString();
            MessengerClient.sendRequest(writer, fetchRequest);
            String conversationString = MessengerClient.readResponse(reader);

            if (conversationString != null) {
                if (conversationString.isEmpty()) {
                    String createRequest = "[CREATE.CONVERSATION]" + ((Customer) user).detailedToString() + ";" +
                            ((Seller) this).detailedToString();
                    MessengerClient.sendRequest(writer, createRequest);
                    String newConversationString = MessengerClient.readResponse(reader);
                    if (newConversationString != null) {
                        conversation = new Conversation(newConversationString);
                    }
                } else {
                    conversation = new Conversation(conversationString);
                }
            } else {
                return false;
            }

            try {
                if (conversation != null) {
                    String appendRequest = "[FILE.APPEND]" + conversation + ";" + message + ";" + this + ";" + user;
                    MessengerClient.sendRequest(writer, appendRequest);
                    if (!Boolean.parseBoolean(MessengerClient.readResponse(reader))) {
                        return false;
                    }
                    conversation.setCustomerUnread(writer, true);
                }
            } catch (Exception e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Edits a message in the parameter conversation and changes the message content to the parameter newMessage
     * ONLY IF the sender of the message is this user.
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param message      The Message object that is to be edited (message content changes)
     * @param conversation The Conversation object whose file is read and rewritten with the edited message
     * @param newMessage   The new edited message content
     * @return Returns whether the message was edited successfully
     */
    public boolean editMessage(BufferedReader reader, PrintWriter writer, Message message, Conversation conversation,
                               String newMessage) {
        try {
            if (message.getSender().equals(this)) {
                ArrayList<Message> readMessages = new ArrayList<>();
                String listMessagesRequest = "[LIST.MESSAGES]" + conversation;
                MessengerClient.sendRequest(writer, listMessagesRequest);
                String stringListOfMessages = MessengerClient.readResponse(reader);

                if (stringListOfMessages != null && stringListOfMessages.length() > 0) {
                    String[] listOfMessageStrings = stringListOfMessages.split(";");
                    for (String messageString : listOfMessageStrings) {
                        readMessages.add(new Message(messageString));
                    }
                }

                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage)) {
                        readMessage.setMessage(newMessage);
                    }
                }

                StringBuilder rewriteFileRequest = new StringBuilder("[FILE.REWRITE]" + conversation + ";");
                for (Message readMessage : readMessages) {
                    rewriteFileRequest.append(readMessage.toString()).append(";");
                }
                if (rewriteFileRequest.length() > 0) {
                    rewriteFileRequest = new StringBuilder(rewriteFileRequest.substring(0,
                            rewriteFileRequest.length() - 1));
                }
                MessengerClient.sendRequest(writer, rewriteFileRequest.toString());

                return Boolean.parseBoolean(MessengerClient.readResponse(reader));
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Deletes a message in the parameter conversation by changing the message's sender visibility status or recipient
     * visibility status depending on this user and the parameter conversation.
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param message      The Message object that is to be deleted (message visibility changes according to the user)
     * @param conversation The Conversation object whose file is read and rewritten with the edited message
     * @return Returns whether the message was deleted successfully
     */
    public boolean deleteMessage(BufferedReader reader, PrintWriter writer, Message message,
                                 Conversation conversation) {
        try {
            if (this.isParticipantOf(conversation)) {
                ArrayList<Message> readMessages = new ArrayList<>();
                String listMessagesRequest = "[LIST.MESSAGES]" + conversation;
                MessengerClient.sendRequest(writer, listMessagesRequest);
                String stringListOfMessages = MessengerClient.readResponse(reader);

                if (stringListOfMessages != null && stringListOfMessages.length() > 0) {
                    String[] listOfMessageStrings = stringListOfMessages.split(";");
                    for (String messageString : listOfMessageStrings) {
                        readMessages.add(new Message(messageString));
                    }
                }

                for (Message readMessage : readMessages) {
                    if (message.equals(readMessage) && message.getSender().equals(this)) {
                        readMessage.setSenderVisibility(false);
                    } else if (message.equals(readMessage) && message.getRecipient().equals(this)) {
                        readMessage.setRecipientVisibility(false);
                    }
                }

                StringBuilder rewriteFileRequest = new StringBuilder("[FILE.REWRITE]" + conversation + ";");
                for (Message readMessage : readMessages) {
                    rewriteFileRequest.append(readMessage.toString()).append(";");
                }
                if (rewriteFileRequest.length() > 0) {
                    rewriteFileRequest = new StringBuilder(rewriteFileRequest.substring(0,
                            rewriteFileRequest.length() - 1));
                }
                MessengerClient.sendRequest(writer, rewriteFileRequest.toString());
                return Boolean.parseBoolean(MessengerClient.readResponse(reader));
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Checks the equality of two User objects. Compares only EMAIL since it is the only constant.
     *
     * @param o Object to be compared with
     * @return Returns whether the equality condition was met
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return EMAIL.equals(((User) o).getEmail());
    }

    /**
     * Generates a formatted String of the User containing elementary identification details.
     * <br> <br>
     * General format: <br>
     * User&lt;username, EMAIL, password&gt;
     *
     * @return Returns the User object's String
     */
    @Override
    public String toString() {
        return String.format("User<%s, %s, %s>", this.username, this.EMAIL, this.password);
    }

    /**
     * Generates a formatted String of the User containing only the required details for CSV conversion.
     * (i.e. does not contain sensitive information like passwords)
     * <br> <br>
     * General format: <br>
     * EMAIL
     *
     * @return Returns the User object's String for CSV conversion
     */
    public String csvToString() {
        return String.format("%s", this.EMAIL);
    }
}
