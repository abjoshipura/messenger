import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class AccountsMaster {
    private final String passwordFile;
    private final String conversationsFile;
    public static ArrayList<Seller> sellerArrayList = new ArrayList<>();
    public static ArrayList<Customer> customerArrayList = new ArrayList<>();
    public static ArrayList<Conversation> conversationArrayList = new ArrayList<>();

    public AccountsMaster(String passwordFile, String conversationsFile) {
        this.passwordFile = passwordFile;
        this.conversationsFile = conversationsFile;

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


    public Conversation fetchConversation(Customer customer, Seller seller) {
        for (Conversation conversation : conversationArrayList) {
            if (conversation.getCustomer().equals(customer) && conversation.getSeller().equals(seller)) {
                return conversation;
            }
        }
        return null;
    }

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

    public ArrayList<Store> listStores(Customer customer) {
        ArrayList<Store> stores = new ArrayList<>();
        for (Seller seller: sellerArrayList) {
            if (!seller.getInvisibleUsers().contains(customer)) {
                stores.addAll(seller.getStores());
            }
        }
        return stores;
    }

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
