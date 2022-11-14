import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class AccountsMaster {
    private final String passwordFile;
    private final String conversationsFile;

    // Contains all Seller objects that have been made.
    public static ArrayList<Seller> sellerArrayList = new ArrayList<>();

    // Contains all Customer objects that have been made.
    public static ArrayList<Customer> customerArrayList = new ArrayList<>();

    // Contains all Conversation objects of all users.
    public static ArrayList<Conversation> conversationArrayList = new ArrayList<>();

    // Takes two file Strings as inputs and read them, using the file lines to fill the three ArrayLists.
    // Throws errors if either file is not found, or if any ArrayList is not populated correctly.
    public AccountsMaster(String passwordFile, String conversationsFile) {
        this.passwordFile = passwordFile;
        this.conversationsFile = conversationsFile;

        try (BufferedReader bfr = new BufferedReader(new FileReader(passwordFile))) {
            String userString = bfr.readLine();

            // Reads line. Depending on what type of user is written, generates a new Seller/Customer object.
            // Adds newly generated object to its respective ArrayList
            // Catches exception when adding users is interrupted or the input file does not exist
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

        // Reads line. Creates and adds a new Conversation to conversationArrayList
        // Catches exception when adding conversations is interrupted or input file does not exist
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

    // Compares username parameter with existing Customers and Sellers' username attribute.
    // Returns true if the same username has been used by a previous user.
    public boolean usernameAlreadyTaken(String username) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
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

    // Compares email parameter with existing Customers and Sellers' email attribute.
    // Returns true if the same email has been used by a previous user.
    public boolean emailAlreadyRegistered(String email) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
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

    // Takes a String parameter. Searches for a user with the same email or username.
    // Returns that user with the matching username or email. If no user is found, returns null
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

    // Takes username, email, password, and role String parameters. Uses input to create a new Customer or Seller object
    public User createAccount(String username, String email, String password, String role) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(passwordFile, true))) {
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

    // Removes a user from their respective ArrayList.
    public void deleteAccount(User deletedUser) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(passwordFile, false))) {
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

    // Takes a user and finds all conversations they are a participant of.
    // Checks if conversations have new lines and returns the number of new lines.
    public int numUnreadConversations(User user) {
        int numUnreadConversations = 0;
        ArrayList<Conversation> userConversations = listConversations(user);

        for (Conversation conversation : userConversations) {
            if (user instanceof Seller && conversation.isSellerUnread()) {
                numUnreadConversations++;
            } else if (user instanceof Customer && conversation.isCustomerUnread()) {
                numUnreadConversations++;
            }
        }
        return numUnreadConversations;
    }

    // Takes user parameter, return ArrayList of all conversations the user is a participant of.
    public ArrayList<Conversation> listConversations(User user) {
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

    // Finds conversation in conversationArrayList that has both Customer and Seller inputs as participants.
    public Conversation fetchConversation(Customer customer, Seller seller) {
        for (Conversation conversation : conversationArrayList) {
            if (conversation.getCustomer().equals(customer) && conversation.getSeller().equals(seller)) {
                return conversation;
            }
        }
        return null;
    }

    // Takes Customer and Seller, creates a file path and ID and creates a new conversation and adds it to list.
    public Conversation createConversation(Customer customer, Seller seller) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(conversationsFile, true))) {
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

    // Takes a list of conversations. Creates and transfers message contents from each conversation file to a csv file.
    public boolean convertConversationsToCSV(ArrayList<Conversation> exportingConversations, String destinationPath)
            throws IOException {

        File dest = new File(destinationPath);
        dest.getParentFile().mkdirs();

        for (Conversation conv : exportingConversations) {
            File c = new File(dest, String.format("%s.txt", conv.getConversationID()));
            c.createNewFile();
            File act = new File(dest, String.format("%s.csv", conv.getConversationID()));

            PrintWriter bw = new PrintWriter(new FileWriter(c, true));

            ArrayList<Message> temp = conv.readFile();

            for (Message msg : temp) {
                bw.println(msg.csvToString());
            }
            bw.close();

            c.renameTo(act);
        }

        return true;
    }

    // Returns a list of all Customers for a Seller. Does not list a Customer if they are invisible to that Seller.
    public ArrayList<Customer> listCustomers(Seller seller) {
        ArrayList<Customer> customers = new ArrayList<>();
        for (Customer customer: customerArrayList) {
            if (!customer.getInvisibleUsers().contains(seller)) {
                customers.add(customer);
            }
        }
        System.out.println();
        return customers;
    }

    // Returns a list of all Stores for a Customer. Does not list a Store if the Seller of is invisible to Customer.
    public ArrayList<Store> listStores(Customer customer) {
        ArrayList<Store> stores = new ArrayList<>();
        for (Seller seller: sellerArrayList) {
            if (!seller.getInvisibleUsers().contains(customer)) {
                stores.addAll(seller.getStores());
            }
        }
        return stores;
    }

    // Takes a search String and finds all customers possessing that String in username or email.
    // Does not list a Customer if they are invisible to Seller.
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

    // Takes a search String and finds all sellers possessing that String in username or email.
    // Does not list a Seller if they are invisible to Customer.
    public ArrayList<Seller> fetchSellers(String searchKeyword, Customer customer) {
        ArrayList<Seller> searchResult = new ArrayList<>();
        for (Seller seller: sellerArrayList) {
            if (!seller.getInvisibleUsers().contains(customer) && (seller.getUsername().contains(searchKeyword) ||
                    seller.getEmail().contains(searchKeyword))) {
                searchResult.add(seller);
            }
        }
        return searchResult;
    }

    // Takes a file and replaces all instances of an old String with a new String.
    public static void replaceStringInFile(String filePath, String oldString, String newString) {
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