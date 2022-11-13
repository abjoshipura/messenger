import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static final String passwordFilePath = "passwords.txt";
    public static final String conversationsFilePath = "conversations.txt";

    private static void printMainMenu(User user) {
        System.out.println("--------");
        System.out.println("1. View Conversations");
        System.out.printf("2. View All %ss\n", (user instanceof Seller) ? "Customer" : "Store");
        System.out.printf("3. Search %ss\n", (user instanceof Seller) ? "Customer" : "Seller");
        System.out.println("4. Edit Account");
        System.out.println("5. Log Out");
    }

    private static void printUserActionMenu(User user) {
        String keyword = (user instanceof Seller) ? "Customer" : "Seller";
        System.out.println("--------");
        System.out.printf("1. Message %s\n", keyword);
        System.out.printf("2. Block %s\n", keyword);
        System.out.printf("3. Become Invisible to %s\n", keyword);
        System.out.println("4. Back to Main Menu");
    }

    private static void runMainMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        while (true) {
            printMainMenu(loggedOnUser);
            int selectedOption;
            while (true) {
                try {
                    selectedOption = Integer.parseInt(scan.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Option");
                }
            }

            if (selectedOption == 1) {
                runConversationsMenu(scan, accountsMaster, loggedOnUser);
            } else if (selectedOption == 2) {
                runListMenu(scan, accountsMaster, loggedOnUser);
            } else if (selectedOption == 3) {
                runSearchMenu(scan, accountsMaster, loggedOnUser);
            } else if (selectedOption == 4) {
                if (runAccountMenu(scan, accountsMaster, loggedOnUser)) {
                    break;
                }
            } else if (selectedOption == 5) {
                System.out.println("Logging Out...");
                break;
            } else {
                System.out.println("Invalid Option");
            }
        }
    }

    private static void runConversationsMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        ArrayList<Conversation> conversations = accountsMaster.listConversations(loggedOnUser);
        if (conversations.size() > 0) {
            while (true) {
                System.out.println("--------");
                System.out.println("**To Open, Enter Conversation No.**");
                for (int i = 0; i < conversations.size(); i++) {
                    System.out.printf("%d. %s\n", i + 1, conversations.get(i).getConversationID());
                }
                System.out.printf("%d. Export Conversations\n", conversations.size() + 1);
                System.out.printf("%d. Back to Main Menu\n", conversations.size() + 2);
                int conversationNumber;
                while (true) {
                    try {
                        conversationNumber = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (conversationNumber == conversations.size() + 2) {
                    break;
                } else if (conversationNumber == conversations.size() + 1) {
                    System.out.println("Enter Conversations to Export Separated by Commas (eg. 1,3,4):");
                    String exportingIndexes = scan.nextLine();
                    System.out.println("Enter .csv File Destination (eg. folder\\fileName.txt)"); //TODO Confirm this format works
                    String CSVDestination = scan.nextLine();

                    try {
                        ArrayList<Conversation> exportingConversations = new ArrayList<>();
                        for (String stringIndex : exportingIndexes.split(",")) {
                            int index = Integer.parseInt(stringIndex) - 1;
                            System.out.println(conversations.size());
                            if (index >= 0 && index < conversations.size()) {
                                exportingConversations.add(conversations.get(index));
                            }
                        }

                        if (accountsMaster.convertConversationsToCSV(exportingConversations, CSVDestination)) {
                            System.out.println("Successfully Converted to CSV!");
                        } else {
                            System.out.println("Conversion Failed");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Conversations");
                    }

                } else if (conversationNumber > 0 && conversationNumber <= conversations.size()) {
                    Conversation conversation = conversations.get(conversationNumber - 1);
                    if (loggedOnUser instanceof Seller) {
                        conversation.setSellerUnread(false);
                    } else {
                        conversation.setCustomerUnread(false);
                    }
                    runConversationActions(scan, accountsMaster, loggedOnUser, conversation);
                } else {
                    System.out.println("Invalid Option");
                }
            }
        } else {
            System.out.println("No Conversations Found!");
        }
    }

    private static void runConversationActions(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser,
                                               Conversation conversation) {
        System.out.println("--------");
        System.out.println("1. Load All Messages");
        System.out.println("2. Import .txt File");
        System.out.println("3. Send Message");
        System.out.println("4. Back to Conversation List");
        System.out.println("---\n**To Execute the Below Actions, Enter MsgIndex.Action " +
                "(eg. 3.5 to Edit Message 3)**\n---");
        System.out.println("5. Edit Message");
        System.out.println("6. Delete Message");

        User recipient = (loggedOnUser instanceof Seller) ? conversation.getCustomer() :
                conversation.getSeller();

        ArrayList<Message> messages = conversation.readFileAsPerUser(loggedOnUser);
        int lowerLimit = Math.min(messages.size(), 20);
        for (int i = messages.size() - lowerLimit; i < messages.size(); i++) {
            System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(),
                    messages.get(i).getCensoredMessage(loggedOnUser));
        }

        while (true) {
            String conversationAction = scan.nextLine();
            if (conversationAction.contains(".")) {
                String[] messageActions = conversationAction.split("\\.");

                int messageID = 0;
                int action = 0;
                try {
                    messageID = Integer.parseInt(messageActions[0]);
                    action = Integer.parseInt(messageActions[1]);
                    if (messageID > messages.size() - 1 || messageID < 0) {
                        System.out.println("Invaid Input. No Such Conversation Exists");
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid Input");
                }

                if (action == 5) {
                    if (messages.get(messageID).getSender().equals(loggedOnUser)) {
                        System.out.print("Updated Message: ");
                        String updatedMessage = scan.nextLine();
                        loggedOnUser.editMessage(messages.get(messageID), conversation, updatedMessage);
                    } else {
                        loggedOnUser.editMessage(messages.get(messageID), conversation, "");
                    }
                } else if (action == 6) {
                    loggedOnUser.deleteMessage(messages.get(messageID), conversation);
                    System.out.println("Deleted Message");
                } else {
                    System.out.println("Invalid Option");
                }
            } else {
                int action = 0;
                while (true) {
                    try {
                        action = Integer.parseInt(conversationAction);
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                        conversationAction = scan.nextLine();
                    }
                }
                
                if (action == 1) {
                    for (int i = 0; i < messages.size(); i++) {
                        System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(),
                                messages.get(i).getMessage());
                    }
                } else if (action == 2) {
                    System.out.println("Enter path to .txt file:");
                    String txtPath = scan.nextLine();
                    if (conversation.importTXT(txtPath, loggedOnUser, recipient)) {
                        System.out.println("Message Sent Successfully!");
                    } else {
                        System.out.println("Error: Could not send message");
                    }
                } else if (action == 3) {
                    System.out.print("Your Message: ");
                    String message = scan.nextLine();
                    loggedOnUser.sendMessageToUser(message, recipient, accountsMaster);
                    System.out.println("Sent!");
                } else if (action == 4) {
                    break;
                } else {
                    System.out.println("Invalid Option");
                }
            }
        }
    }

    private static void runListMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        if (loggedOnUser instanceof Seller) {
            while (true) {
                ArrayList<Customer> customers = accountsMaster.listCustomers((Seller) loggedOnUser);
                
                int customerNumber = 0;
                while (true) {
                    try {
                        System.out.println("--------");
                        System.out.println("**To Select, Enter Customer No.**");
                        for (int i = 0; i < customers.size(); i++) {
                            System.out.printf("%d. %s | %s\n", i + 1, customers.get(i).getUsername(),
                                    customers.get(i).getEmail());
                        }
                        System.out.printf("%d. Back to Main Menu\n", customers.size() + 1);
                        customerNumber = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option.");
                    }
                }

                if (customerNumber == customers.size() + 1) {
                    break;
                } else if (customerNumber > 0 && customerNumber <= customers.size()) {
                    Customer selectedCustomer = customers.get(customerNumber - 1);
                    printUserActionMenu(loggedOnUser);
                    int userChoice = 0;
                    while (true) {
                        try {
                            userChoice = Integer.parseInt(scan.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (userChoice == 1) {
                        if (!selectedCustomer.getBlockedUsers().contains(loggedOnUser)) {
                            System.out.print("Your Message: ");
                            String message = scan.nextLine();
                            loggedOnUser.sendMessageToUser(message, selectedCustomer, accountsMaster);
                            System.out.println("Sent!");
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (userChoice == 2) {
                        ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                        blockedUsers.add(selectedCustomer);
                        loggedOnUser.setBlockedUsers(blockedUsers);
                        System.out.println("Blocked Customer");
                    } else if (userChoice == 3) {
                        ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                        invisibleUsers.add(selectedCustomer);
                        loggedOnUser.setInvisibleUsers(invisibleUsers);
                        System.out.println("Now Invisible to Customer");
                    } else if (userChoice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid Option");
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        } else {
            while (true) {
                ArrayList<Store> stores = accountsMaster.listStores((Customer) loggedOnUser);
                System.out.println("**To Select, Enter Store No.**");
                for (int i = 0; i < stores.size(); i++) {
                    System.out.printf("%d. %s by %s\n", i + 1, stores.get(i).getStoreName(),
                            stores.get(i).getSeller().getUsername());
                }
                System.out.printf("%d. Back to Main Menu\n", stores.size() + 1);
                
                int storeNumber = 0;
                while (true) {
                    try {
                        storeNumber = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (storeNumber == stores.size() + 1) {
                    break;
                } else if (storeNumber > 0 && storeNumber <= stores.size()) {
                    Seller selectedStoreSeller = stores.get(storeNumber - 1).getSeller();
                    loggedOnUser.sendMessageToUser(loggedOnUser.getUsername() + "looked up you store: "
                            + stores.get(storeNumber - 1).getStoreName(), selectedStoreSeller, accountsMaster);

                    printUserActionMenu(loggedOnUser);
                    int userChoice = 0;
                    while (true) {
                        try {
                            userChoice = Integer.parseInt(scan.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (userChoice == 1) {
                        if (!selectedStoreSeller.getBlockedUsers().contains(loggedOnUser)) {
                            System.out.print("Your Message: ");
                            String message = scan.nextLine();
                            loggedOnUser.sendMessageToUser(message, selectedStoreSeller, accountsMaster);
                            System.out.println("Sent!");
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (userChoice == 2) {
                        ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                        blockedUsers.add(selectedStoreSeller);
                        loggedOnUser.setBlockedUsers(blockedUsers);
                        System.out.println("Blocked Store Owner");
                    } else if (userChoice == 3) {
                        ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                        invisibleUsers.add(selectedStoreSeller);
                        loggedOnUser.setInvisibleUsers(invisibleUsers);
                        System.out.println("Now Invisible to Store Owner");
                    } else if (userChoice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid Option");
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        }
    }

    private static void runSearchMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        System.out.println("Search by Username or Email (full or part):");
        String searchKeyword = scan.nextLine();
        if (loggedOnUser instanceof Seller) {
            while (true) {
                ArrayList<Customer> customers = accountsMaster.fetchCustomers(searchKeyword, (Seller) loggedOnUser);
                System.out.println("**To Select, Enter Customer No.**");
                for (int i = 0; i < customers.size(); i++) {
                    System.out.printf("%d. %s | %s\n", i + 1, customers.get(i).getUsername(), customers.get(i).getEmail());
                }
                if (customers.size() == 0) {
                    System.out.println("**No Customers Found**");
                }
                System.out.printf("%d. Back to Main Menu\n", customers.size() + 1);
                int customerNumber = 0;
                while (true) {
                    try {
                        customerNumber = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (customerNumber == customers.size() + 1) {
                    break;
                } else if (customerNumber > 0 && customerNumber <= customers.size()) {
                    Customer selectedCustomer = customers.get(customerNumber - 1);
                    printUserActionMenu(loggedOnUser);

                    int userChoice = 0;
                    while (true) {
                        try {
                            userChoice = Integer.parseInt(scan.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (userChoice == 1) {
                        if (!selectedCustomer.getBlockedUsers().contains(loggedOnUser)) {
                            System.out.print("Your Message: ");
                            String message = scan.nextLine();
                            loggedOnUser.sendMessageToUser(message, selectedCustomer, accountsMaster);
                            System.out.println("Sent!");
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (userChoice == 2) {
                        ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                        blockedUsers.add(selectedCustomer);
                        loggedOnUser.setBlockedUsers(blockedUsers);
                        System.out.println("Blocked Customer");
                    } else if (userChoice == 3) {
                        ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                        invisibleUsers.add(selectedCustomer);
                        loggedOnUser.setInvisibleUsers(invisibleUsers);
                        System.out.println("Now Invisible to Customer");
                    } else if (userChoice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid Option");
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        } else {
            while (true) {
                ArrayList<Seller> sellers = accountsMaster.fetchSellers(searchKeyword, (Customer) loggedOnUser);
                System.out.println("--------");
                System.out.println("**To Select, Enter Seller No.**");
                for (int i = 0; i < sellers.size(); i++) {
                    System.out.printf("%d. %s | %s\n", i + 1, sellers.get(i).getUsername(), sellers.get(i).getEmail());
                }
                if (sellers.size() == 0) {
                    System.out.println("**No Sellers Found**");
                }
                System.out.printf("%d. Back to Main Menu\n", sellers.size() + 1);
                
                int sellerNumber = 0;
                while (true) {
                    try {
                        sellerNumber = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (sellerNumber == sellers.size() + 1) {
                    break;
                } else if (sellerNumber > 0 && sellerNumber <= sellers.size()) {
                    Seller selectedSeller = sellers.get(sellerNumber - 1);
                    printUserActionMenu(loggedOnUser);
                    
                    int userChoice = 0;
                    while (true) {
                        try {
                            userChoice = Integer.parseInt(scan.nextLine());
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (userChoice == 1) {
                        if (!selectedSeller.getBlockedUsers().contains(loggedOnUser)) {
                            System.out.print("Your Message: ");
                            String message = scan.nextLine();
                            loggedOnUser.sendMessageToUser(message, selectedSeller, accountsMaster);
                            System.out.println("Sent!");
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (userChoice == 2) {
                        ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                        blockedUsers.add(selectedSeller);
                        loggedOnUser.setBlockedUsers(blockedUsers);
                        System.out.println("Blocked Seller");
                    } else if (userChoice == 3) {
                        ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                        invisibleUsers.add(selectedSeller);
                        loggedOnUser.setInvisibleUsers(invisibleUsers);
                        System.out.println("Now Invisible to Seller");
                    } else if (userChoice == 4) {
                        break;
                    } else {
                        System.out.println("Invalid Option");
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        }
    }

    private static void printEditAccountMenu() {
        System.out.println("--------");
        System.out.println("1. Change Username");
        System.out.println("2. Edit Censoring");
        System.out.println("3. Delete Account");
        System.out.println("4. Back to Main Menu");
    }

    private static void printCensoredWords(ArrayList<String> censoredWordPairs) {
        for (int i = 1; i < censoredWordPairs.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, censoredWordPairs.get(i).replace(":", " >>> "));
        }
    }

    private static boolean runAccountMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        boolean deleteUser = false;
        while (true) {
            printEditAccountMenu();
            int accountOption = 0;
            while (true) {
                try {
                    accountOption = Integer.parseInt(scan.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Option");
                }
            }

            if (accountOption == 1) {
                while (true) {
                    System.out.println("Enter new username:");
                    String username = scan.nextLine();
                    if (accountsMaster.usernameAlreadyTaken(username)) {
                        System.out.println("Sorry! That username is already taken\nTry again? (Y/N)");
                        boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");
                        if (!tryAgain) {
                            break;
                        }
                    } else {
                        loggedOnUser.setUsername(username); //TODO Change in conversations and password handled in User
                        break;
                    }
                }
            } else if (accountOption == 2) {
                ArrayList<String> censoredWords = loggedOnUser.getCensoredWords();

                if (censoredWords.size() > 0) {
                    System.out.println("**To Remove, Enter Censor Pair No.**");
                    printCensoredWords(censoredWords);
                }
                System.out.printf("%d. Add Censor Pair\n", censoredWords.size());
                System.out.printf("%d. Toggle Censoring (ON/OFF) | Currently: %s\n", censoredWords.size() + 1,
                        (loggedOnUser.isRequestsCensorship()) ? "ON" : "OFF");
                int censorOption = 0;
                while (true) {
                    try {
                        censorOption = Integer.parseInt(scan.nextLine());
                        break;
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (censorOption == censoredWords.size()) {
                    System.out.println("Enter new pair as \"Word:Replacement\" or only \"Word\"");
                    String[] newCensor = scan.nextLine().split(":");
                    if (newCensor.length == 1) {
                        censoredWords.add(newCensor[0] + ":" + "****"); //TODO Update Passwords.txt
                    } else {
                        censoredWords.add(newCensor[0] + ":" + newCensor[1]); //TODO Update Passwords.txt
                    }
                } else if (censorOption == censoredWords.size() + 1) {
                    loggedOnUser.setRequestsCensorship(!loggedOnUser.isRequestsCensorship()); //TODO Update Passwords.txt
                } else if (censorOption > 0 && censorOption <= censoredWords.size()) {
                    censoredWords.remove(censorOption - 1); //TODO Update Passwords.txt
                } else {
                    System.out.println("Invalid Option");
                }

            } else if (accountOption == 3) {
                System.out.println("Are you sure? (Y/N)");
                deleteUser = scan.nextLine().equalsIgnoreCase("Y");
                if (deleteUser) {
                    accountsMaster.deleteAccount(loggedOnUser); //TODO update Conversations.txt
                }
                break;
            } else if (accountOption == 4) {
                break;
            } else {
                System.out.println("Invalid Option");
            }
        }
        return deleteUser;
    }

    public static void main(String[] args) {
        User loggedOnUser = null;
        Scanner scan = new Scanner(System.in);
        AccountsMaster accountsMaster = new AccountsMaster(passwordFilePath, conversationsFilePath);

        System.out.println("Welcome to Messenger (not Facebook)!");
        System.out.println("Log in (or Sign Up) with Username or Email:");
        String usernameOrEmail = scan.nextLine();

        if (!(accountsMaster.usernameAlreadyTaken(usernameOrEmail) || accountsMaster.emailAlreadyRegistered(usernameOrEmail))) {
            System.out.println("Seems like you don't have an account yet!");
            System.out.println("Would you like to make an account? (Y/N)");
            boolean createAccount = scan.nextLine().equalsIgnoreCase("Y");

            if (createAccount) {
                System.out.println("Great! Let's get you set up!");

                String username;
                while (true) {
                    System.out.println("Create your new username:");
                    username = scan.nextLine();
                    if (accountsMaster.usernameAlreadyTaken(username)) {
                        System.out.println("Sorry! That username is already taken\nTry again? (Y/N)");
                        boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");
                        if (!tryAgain) {
                            username = null;
                            System.out.println("Sorry to see you go :( Have a great day!");
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if (username != null) {
                    String email;
                    while (true) {
                        System.out.println("Register with your email ID:");
                        email = scan.nextLine();
                        if (accountsMaster.emailAlreadyRegistered(email)) {
                            System.out.println("Sorry! That email is already registered\nTry again? (Y/N)");
                            boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");
                            if (!tryAgain) {
                                email = null;
                                System.out.println("Sorry to see you go :( Have a great day!");
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (email != null) {
                        String role;
                        while (true) {
                            System.out.println("Enter your role (SELLER/CUSTOMER):");
                            role = scan.nextLine();
                            if (!(role.equalsIgnoreCase("SELLER") || role.equalsIgnoreCase("CUSTOMER"))) {
                                System.out.println("Sorry! That is an invalid role\nTry again? (Y/N)");
                                boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");
                                if (!tryAgain) {
                                    role = null;
                                    System.out.println("Sorry to see you go :( Have a great day!");
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        if (role != null) {
                            System.out.println("Create a password:");
                            String password = scan.nextLine();
                            loggedOnUser = accountsMaster.createAccount(username, email, password, role);
                            System.out.println("You're all set!");
                        }
                    }
                }
            } else {
                System.out.println("Sorry to see you go :( Have a great day!");
            }
        } else {
            loggedOnUser = accountsMaster.fetchAccount(usernameOrEmail);
            System.out.println("Enter password:");
            String password = scan.nextLine();

            if (password.equals(loggedOnUser.getPassword())) {
                System.out.printf("Welcome back %s!\n", loggedOnUser.getUsername().toUpperCase());
                int numUnreadConversations = accountsMaster.numUnreadConversations(loggedOnUser);
                if (numUnreadConversations > 0) {
                    System.out.printf("You have %d new unread conversation%s\n", numUnreadConversations,
                            (numUnreadConversations != 1) ? "s" : "");
                }
            } else {
                loggedOnUser = null;
                System.out.println("Incorrect Password");
            }
        }
        if (loggedOnUser != null) {
            runMainMenu(scan, accountsMaster, loggedOnUser);
            System.out.println("Goodbye!");
        }
    }
}
