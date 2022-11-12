import java.io.*;
import java.util.*;

public class AccountsMaster {
    private final String passwordFile;
    private final String conversationsFile;
    public static ArrayList<User> userArrayList = new ArrayList<>();
    public static ArrayList<Conversation> conversationList = new ArrayList<>();

    public AccountsMaster(String passwordFile, String conversationsFile) {
        this.passwordFile = passwordFile;
        this.conversationsFile = conversationsFile;

        try (BufferedReader bfr = new BufferedReader(new FileReader(passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.lastIndexOf(">"));
                String[] userDetails = strippedMessage.split(", ");
                userArrayList.add(new User(userDetails[0], userDetails[1], userDetails[2]));

                userString = bfr.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //TODO Handle un/blocking
    //TODO Handle in/visibility
    //TODO Open Conversations w/ w/o censoring as per user
    //TODO Edits to accounts should update all conversation Users

    //TODO Customer/Seller Dashboards
    //TODO Sorting the Dashboard
    public User fetchAccount(String usernameOrEmail) {
        User account = null;
        for (User user: userArrayList) {
            if (user.getUsername().equalsIgnoreCase(usernameOrEmail) ||
                    user.getEmail().equalsIgnoreCase(usernameOrEmail)) {
                account = user;
            }
        }
        return account;
    }
    public User createAccount(String username, String email, String password, String role) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(passwordFile, true))) {
            if (role.equalsIgnoreCase("SELLER")) {
                Seller newSeller = new Seller(username, email, password);
                userArrayList.add(newSeller);
                pw.println(newSeller);
                return newSeller;
            } else {
                Customer newCustomer = new Customer(username, email, password);
                userArrayList.add(newCustomer);
                pw.println(newCustomer);
                return newCustomer;
            }
        } catch (Exception e) {
            System.out.println("We hit an error! :|");
            return null;
        }
    }

    //TODO Deleting Accounts
    public boolean deleteAcc(User user) {
        ArrayList<String> temp = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(passwordFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true));
             BufferedWriter del = new BufferedWriter(new FileWriter(passwordFile, false))) {
            String line = br.readLine();
            while (line != null) {
                String[] lineArray = line.split(",");
                if (!user.getEmail().equalsIgnoreCase(lineArray[1]) && !user.getPassword().equals(lineArray[2])) {
                    temp.add(line);
                }
                line = br.readLine();
            }

            del.write("");

            for (String l : temp) {
                bw.write(l);
            }

            for (int i = 0; i < temp.size(); i++) {
                if (userArrayList.get(i).getEmail().equalsIgnoreCase(user.getEmail())) {
                    userArrayList.remove(i);
                    i++;
                }

            }
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int numUnreadConversations(User user) {
        int numUnreadConversations = 0;
        ArrayList<Conversation> userConversations = listConversations(user);
        for (Conversation conversation: userConversations) {
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
            for (Conversation conversation: conversationList) {
                if (conversation.getSeller().equals(user) && !conversation.getCustomer().getInvisibleUsers().contains(user)) {
                    if (conversation.isSellerUnread()) {
                        conversations.add(0, conversation);
                    } else {
                        conversations.add(conversation);
                    }
                }
            }
        } else {
            for (Conversation conversation: conversationList) {
                if (conversation.getCustomer().equals(user) && !conversation.getSeller().getInvisibleUsers().contains(user)) {
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

    //TODO Update For Buyer and Customer
    public boolean usernameAlreadyTaken(String username) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.indexOf(">"));
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

    //TODO Update For Buyer and Customer
    public boolean emailAlreadyRegistered(String email) {
        try (BufferedReader bfr = new BufferedReader(new FileReader(this.passwordFile))) {
            String userString = bfr.readLine();
            while (userString != null) {
                String strippedMessage = userString.substring(userString.indexOf("<") + 1, userString.indexOf(">"));
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
}