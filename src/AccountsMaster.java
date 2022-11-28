import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Helper class to the server that handles CRUD operations on accounts and conversations
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */

public class AccountsMaster {

    /**
     * The constant password/account file path: passwords.txt
     */
    private final String PASSWORD_FILE;

    /**
     * The constant conversations file path: conversations.txt
     */
    private final String CONVERSATIONS_FILE;

    /**
     * The sellers registered on the application. Populated from passwords.txt in the constructor
     */
    public static ArrayList<Seller> sellerArrayList = new ArrayList<>();

    /**
     * The customers registered on the application. Populated from passwords.txt in the constructor
     */
    public static ArrayList<Customer> customerArrayList = new ArrayList<>();

    /**
     * The conversations created in the application. Populated from conversations.txt in the constructor
     */
    public static ArrayList<Conversation> conversationArrayList = new ArrayList<>();

    /**
     * Retrieves data from memory to update sellerArrayList, customerArrayList, and conversationArrayList for use in
     * the program. Inherently calls Seller, Customer, and Conversation constructors.
     *
     * @param passwordFile      The path to the passwords/accounts
     * @param conversationsFile The path to the conversations
     */
    public AccountsMaster(String passwordFile, String conversationsFile) {
        this.PASSWORD_FILE = passwordFile;
        this.CONVERSATIONS_FILE = conversationsFile;

        sellerArrayList.clear();
        customerArrayList.clear();
        conversationArrayList.clear();

        try (BufferedReader bfr = new BufferedReader(new FileReader(passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                if (userString.substring(0, userString.indexOf("<")).equalsIgnoreCase("Seller")) {
                    Seller seller = new Seller(userString, true, true);
                    sellerArrayList.add(seller);
                } else {
                    Customer customer = new Customer(userString, true);
                    customerArrayList.add(customer);
                }
                userString = bfr.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (Exception e) {
            System.out.println("Error: Users Did Not Populate");
        }

        try (BufferedReader bfr = new BufferedReader(new FileReader(conversationsFile))) {
            String conversationString = bfr.readLine();
            while (conversationString != null) {
                conversationArrayList.add(new Conversation(conversationString));
                conversationString = bfr.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Conversations File Not Found!");
        } catch (Exception e) {
            System.out.println("Error: Conversations Did Not Populate");
        }

    }

    /**
     * Checks if the parameter username has already been taken by other users
     *
     * @param username The proposed username
     * @return Returns whether the username has been taken
     */
    public boolean usernameAlreadyTaken(String username) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.PASSWORD_FILE))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1,
                        userString.lastIndexOf(">"));
                String[] userDetails = strippedMessage.split(", ");
                if (username.equalsIgnoreCase(userDetails[0])) {
                    return true;
                }
                userString = bfr.readLine();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Checks if the parameter email has already been registered
     *
     * @param email The proposed email
     * @return Returns whether the email is already registered
     */
    public boolean emailAlreadyRegistered(String email) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.PASSWORD_FILE))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1,
                        userString.lastIndexOf(">"));
                String[] userDetails = strippedMessage.split(", ");
                if (email.equalsIgnoreCase(userDetails[1])) {
                    return true;
                }
                userString = bfr.readLine();
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Fetches a user's account based on the username or email entered. Used when the user logs in.
     *
     * @param usernameOrEmail The username or email used to log in
     * @return Returns a User object with the matching username or email
     */
    public User fetchAccount(String usernameOrEmail) {
        for (Seller seller : sellerArrayList) {
            if (seller.getUsername().equalsIgnoreCase(usernameOrEmail) ||
                    seller.getEmail().equalsIgnoreCase(usernameOrEmail)) {
                return seller;
            }
        }
        for (Customer customer : customerArrayList) {
            if (customer.getUsername().equalsIgnoreCase(usernameOrEmail) ||
                    customer.getEmail().equalsIgnoreCase(usernameOrEmail)) {
                return customer;
            }
        }
        return null;
    }

    /**
     * Creates a new user account for the user based on user input. Used when creating a new account.
     *
     * @param username The username of the new user
     * @param email    The email of the new user
     * @param password The password set by the new user
     * @param role     The role of the new user
     * @return Returns the newly created User
     */
    public User createAccount(String username, String email, String password, String role) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PASSWORD_FILE, true))) {
            if (role.equalsIgnoreCase("SELLER")) {
                Seller newSeller = new Seller(username, email, password);
                sellerArrayList.add(newSeller);
                pw.println(newSeller.detailedToString());
                return newSeller;
            } else {
                Customer newCustomer = new Customer(username, email, password);
                customerArrayList.add(newCustomer);
                pw.println(newCustomer.detailedToString());
                return newCustomer;
            }
        } catch (Exception e) {
            System.out.println("We hit an error! :|");
            return null;
        }
    }

    /**
     * Deletes the user account by removing the parameter user from sellersArrayList / customersArrayList depending
     * on the role and rewrites passwords.txt without the parameter user's account.
     *
     * @param deletedUser The user to be deleted
     */
    public void deleteAccount(User deletedUser) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PASSWORD_FILE, false))) {
            if (deletedUser instanceof Seller) {
                sellerArrayList.remove(deletedUser);
            } else if (deletedUser instanceof Customer) {
                customerArrayList.remove(deletedUser);
            }

            for (Seller seller : sellerArrayList) {
                pw.println(seller.detailedToString());
            }
            for (Customer customer : customerArrayList) {
                pw.println(customer.detailedToString());
            }
        } catch (Exception e) {
            System.out.println("We hit an error! :|");
        }
    }

    /**
     * Calculates the number of unread conversations for the parameter user. Used when the user logs in.
     *
     * @param user The active user who logged in
     * @return Returns the number of unread messages for the parameter user
     */
    public int numUnreadConversations(User user) {
        int numUnreadConversations = 0;
        ArrayList<Conversation> userConversations = listVisibleConversations(user);

        for (Conversation conversation : userConversations) {
            if (user instanceof Seller && conversation.isSellerUnread()) {
                numUnreadConversations++;
            } else if (user instanceof Customer && conversation.isCustomerUnread()) {
                numUnreadConversations++;
            }
        }
        return numUnreadConversations;
    }

    /**
     * Compiles a list of Conversation objects dependent on the parameter user (e.g. the user will not be able to see
     * conversations if they are in the other user's invisibleUsers list).
     *
     * @param user The active user who logged in
     * @return Returns the list of conversations visible to the parameter user
     */
    public ArrayList<Conversation> listVisibleConversations(User user) {
        ArrayList<Conversation> conversations = new ArrayList<>();
        if (user instanceof Seller) {
            for (Conversation conversation : conversationArrayList) {
                if (conversation.getSeller().equals(user) &&
                        !conversation.getCustomer().getInvisibleUsers().contains(user)) {
                    if (conversation.isSellerUnread()) {
                        conversations.add(0, conversation);
                    } else {
                        conversations.add(conversation);
                    }
                }
            }
        } else {
            for (Conversation conversation : conversationArrayList) {
                if (conversation.getCustomer().equals(user) &&
                        !conversation.getSeller().getInvisibleUsers().contains(user)) {
                    if (conversation.isCustomerUnread()) {
                        conversations.add(0, conversation);
                    } else {
                        conversations.add(conversation);
                    }
                }
            }
        }
        return conversations;
    }

    /**
     * Accessor method for ArrayList&lt;Conversation&gt; conversationArrayList
     *
     * @return Returns the conversations created in the application
     */
    public ArrayList<Conversation> getConversationArrayList() {
        return AccountsMaster.conversationArrayList;
    }

    /**
     * Fetches a Conversation object that corresponds to the parameter seller and parameter customer.
     *
     * @param seller   The seller participating in the conversation
     * @param customer The customer participating in the conversation
     * @return Returns the Conversation corresponding to the parameter seller and parameter customer
     */
    public Conversation fetchConversation(Seller seller, Customer customer) {
        for (Conversation conversation : conversationArrayList) {
            if (conversation.getCustomer().equals(customer) && conversation.getSeller().equals(seller)) {
                return conversation;
            }
        }
        return null;
    }

    /**
     * Creates a new Conversation between the parameter seller and parameter customer. Used when sending messages to a
     * User sharing no conversation history.
     *
     * @param seller   The seller participating in the conversation
     * @param customer The customer participating in the conversation
     * @return Returns the newly created Conversation
     */
    public Conversation createConversation(Seller seller, Customer customer) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CONVERSATIONS_FILE, true))) {
            String conversationID = customer.getUsername() + "TO" + seller.getUsername();
            String conversationFilePath = customer.getEmail().replace(".", "") + "_" +
                    seller.getEmail().replace(".", "") + ".txt";

            Conversation conversation = new Conversation(conversationID, conversationFilePath, seller, customer);
            AccountsMaster.conversationArrayList.add(conversation);
            pw.println(conversation);
            return conversation;
        } catch (Exception e) {
            System.out.println("We hit an error! :|");
            return null;
        }
    }

    /**
     * Compiles a list of Customer objects dependent on the parameter seller (e.g. the seller will not be able to see
     * a Customer if they are in the Customer's invisibleUsers list).
     *
     * @param seller The active seller who logged in
     * @return Returns an ArrayList&lt;Customer&gt; of customers visible to the parameter seller
     */
    public ArrayList<Customer> listCustomers(Seller seller) {
        ArrayList<Customer> customers = new ArrayList<>();
        for (Customer customer : customerArrayList) {
            if (!customer.getInvisibleUsers().contains(seller)) {
                customers.add(customer);
            }
        }
        System.out.println();
        return customers;
    }

    /**
     * Compiles a list of Store objects dependent on the parameter customer (e.g. the customer will not be able to see
     * a Store if they are in the Store's Seller's invisibleUsers list).
     *
     * @param customer The active customer who logged in
     * @return Returns an ArrayList&lt;Store&gt; of stores visible to the parameter customer
     */
    public ArrayList<Store> listStores(Customer customer) {
        ArrayList<Store> stores = new ArrayList<>();
        for (Seller seller : sellerArrayList) {
            if (!seller.getInvisibleUsers().contains(customer)) {
                stores.addAll(seller.getStores());
            }
        }
        return stores;
    }

    /**
     * Compiles a list of Customer objects whose username or email String.contains() parameter searchKeyword. It is
     * still dependent on the parameter seller (e.g. the seller will not be able to see a Customer if they are in the
     * Customer's invisibleUsers list).
     *
     * @param searchKeyword The search keyword
     * @param seller        The active seller who logged in
     * @return Returns an ArrayList&lt;Customer&gt; of customers visible to the parameter seller based on the keyword
     */
    public ArrayList<Customer> fetchCustomers(String searchKeyword, Seller seller) {
        ArrayList<Customer> visibleCustomers = listCustomers(seller);
        ArrayList<Customer> searchResult = new ArrayList<>();
        for (Customer customer : visibleCustomers) {
            if (customer.getUsername().contains(searchKeyword) || customer.getEmail().contains(searchKeyword)) {
                searchResult.add(customer);
            }
        }
        return searchResult;
    }

    /**
     * Compiles a list of Seller objects whose username or email String.contains() parameter searchKeyword. It is
     * still dependent on the parameter customer (e.g. the customer will not be able to see a Seller if they are in the
     * Seller's invisibleUsers list).
     *
     * @param searchKeyword The search keyword
     * @param customer      The active customer who logged in
     * @return Returns an ArrayList&lt;Seller&gt; of sellers visible to the parameter customer based on the keyword
     */
    public ArrayList<Seller> fetchSellers(String searchKeyword, Customer customer) {
        ArrayList<Seller> searchResult = new ArrayList<>();
        for (Seller seller : sellerArrayList) {
            if (!seller.getInvisibleUsers().contains(customer) && (seller.getUsername().contains(searchKeyword) ||
                    seller.getEmail().contains(searchKeyword))) {
                searchResult.add(seller);
            }
        }
        return searchResult;
    }

    /**
     * Replaces an old User / Conversation / Message object String its updated String in the file at the provided path.
     * Indirectly used in multiple mutator methods to change values in files for later retrieval of information from
     * memory.
     *
     * @param filePath  The path to the file where changes are to be made
     * @param oldString The old object String
     * @param newString The new object String
     */
    public void replaceStringInFile(String filePath, String oldString, String newString) {
        ArrayList<String> strings = new ArrayList<>();
        try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
            String string = bfr.readLine();
            while (string != null) {
                string = string.replaceAll(Pattern.quote(oldString), newString);
                strings.add(string);
                string = bfr.readLine();
            }
        } catch (Exception e) {
            System.out.println("Error: Could Not Update Values in Files");
        }

        try (PrintWriter pw = new PrintWriter(new FileOutputStream(filePath, false))) {
            for (String string : strings) {
                pw.println(string);
            }
        } catch (Exception e) {
            System.out.println("Error: Could Not Update Values in Files");
        }
    }
}
