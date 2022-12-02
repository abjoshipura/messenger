import java.io.*;
import java.net.Socket;
import java.util.*;

public class MessengerClient {

    /**
     * The constant password/account file path: passwords.txt
     */
    public static final String PASSWORD_FILE_PATH = "passwords.txt";

    /**
     * The constant conversations file path: conversations.txt
     */
    public static final String CONVERSATIONS_FILE_PATH = "conversations.txt";

    /**
     * Sends a network request to the server
     *
     * @param writer  The PrintWriter object to be used to send the network request
     * @param request The request sent to the server
     */
    public static void sendRequest(PrintWriter writer, String request) {
        writer.write(request);
        writer.println();
        writer.flush();
    }

    /**
     * Reads a network response
     *
     * @param reader The BufferedReader object to be used to read network responses
     */
    public static String readResponse(BufferedReader reader) {
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.out.println("Network Error");
            return null;
        }
    }

    /**
     * Converts the chosen Conversation objects to exportable .csv files and exports them to the parameter
     * destinationPath
     *
     * @param exportingConversations The ArrayList&lt;Conversation&gt; of conversations to be exported
     * @param destinationPath        The destination of all .csv files
     * @return Returns whether the .csv conversion and export succeeded
     * @throws IOException in the case of a conversion failure
     */
    public static boolean convertConversationsToCSV(ArrayList<Conversation> exportingConversations, String destinationPath)
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

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Conversation&gt; to
     * implement live blocking, invisibility, and refreshing conversation titles
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Conversation&gt; conversations
     */
    public static ArrayList<Conversation> refreshVisibleConversations(BufferedReader reader, PrintWriter writer,
                                                                      User loggedOnUser) {
        ArrayList<Conversation> conversations = new ArrayList<>();

        String userString = (loggedOnUser instanceof Seller) ? ((Seller) loggedOnUser).detailedToString() :
                ((Customer) loggedOnUser).detailedToString();
        String listConversationsRequest = "[LIST.VISIBLE_CONVERSATIONS]" + userString;
        sendRequest(writer, listConversationsRequest);
        String stringListOfConversations = readResponse(reader);

        if (stringListOfConversations != null && stringListOfConversations.length() > 0) {
            String[] listOfConversationStrings = stringListOfConversations.split(";");
            for (String conversationString : listOfConversationStrings) {
                conversations.add(new Conversation(conversationString));
            }
        }

        return conversations;
    }

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Message&gt; to
     * implement refreshing
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param conversation The Conversation object of the currently opened conversation
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Message&gt; messages
     */
    public static ArrayList<Message> refreshVisibleMessages(BufferedReader reader, PrintWriter writer,
                                                            Conversation conversation, User loggedOnUser) {
        ArrayList<Message> messages = new ArrayList<>();

        String listMessagesRequest = "[LIST.VISIBLE_MESSAGES]" + conversation + ";" + loggedOnUser;
        sendRequest(writer, listMessagesRequest);
        String stringListOfMessages = readResponse(reader);

        if (stringListOfMessages != null && stringListOfMessages.length() > 0) {
            String[] listOfMessageStrings = stringListOfMessages.split(";");
            for (String messageString : listOfMessageStrings) {
                messages.add(new Message(messageString));
            }
        }

        return messages;
    }

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Customer&gt; to
     * implement live blocking and invisibility
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Customer&gt; customer
     */
    public static ArrayList<Customer> refreshCustomers(BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        ArrayList<Customer> customers = new ArrayList<>();

        String userString = ((Seller) loggedOnUser).detailedToString();
        String listCustomersRequest = "[LIST.VISIBLE_CUSTOMERS]" + userString;
        sendRequest(writer, listCustomersRequest);
        String stringListOfCustomers = readResponse(reader);

        if (stringListOfCustomers != null && stringListOfCustomers.length() > 0) {
            String[] listOfCustomerStrings = stringListOfCustomers.split(";");
            for (String customerString : listOfCustomerStrings) {
                customers.add(new Customer(customerString, true));
            }
        }

        return customers;
    }

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Store&gt; to
     * implement live blocking and invisibility
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Store&gt; stores
     */
    public static ArrayList<Store> refreshStores(BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        ArrayList<Store> stores = new ArrayList<>();

        String userString = ((Customer) loggedOnUser).detailedToString();
        String listStoresRequest = "[LIST.VISIBLE_STORES]" + userString;
        sendRequest(writer, listStoresRequest);
        String stringListOfStores = readResponse(reader);

        if (stringListOfStores != null && stringListOfStores.length() > 0) {
            String[] listOfStoreStrings = stringListOfStores.split(";");
            for (String storeString : listOfStoreStrings) {
                stores.add(new Store(storeString));
            }
        }

        return stores;
    }

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Customer&gt; to
     * implement live blocking and invisibility
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Customer&gt; customers
     */
    public static ArrayList<Customer> refreshSearchCustomers(BufferedReader reader, PrintWriter writer,
                                                             User loggedOnUser, String searchKeyword) {
        ArrayList<Customer> customers = new ArrayList<>();

        String userString = ((Seller) loggedOnUser).detailedToString();
        String listCustomersRequest = "[SEARCH.CUSTOMER]" + searchKeyword + ";" + userString;
        sendRequest(writer, listCustomersRequest);
        String stringListOfCustomers = readResponse(reader);

        if (stringListOfCustomers != null && stringListOfCustomers.length() > 0) {
            String[] listOfCustomerStrings = stringListOfCustomers.split(";");
            for (String customerString : listOfCustomerStrings) {
                customers.add(new Customer(customerString, true));
            }
        }

        return customers;
    }

    /**
     * Client sends a request to the server and refresh the local variable for the ArrayList&lt;Seller&gt; to
     * implement live blocking and invisibility
     *
     * @param reader       The BufferedReader object to be used to read network responses
     * @param writer       The PrintWriter object to be used to send the network request
     * @param loggedOnUser The active user
     * @return Returns the updated ArrayList&lt;Seller&gt; sellers
     */
    public static ArrayList<Seller> refreshSearchSeller(BufferedReader reader, PrintWriter writer, User loggedOnUser,
                                                        String searchKeyword) {
        ArrayList<Seller> sellers = new ArrayList<>();

        String userString = ((Customer) loggedOnUser).detailedToString();
        String listCustomersRequest = "[SEARCH.SELLER]" + searchKeyword + ";" + userString;
        sendRequest(writer, listCustomersRequest);
        String stringListOfSellers = readResponse(reader);

        if (stringListOfSellers != null && stringListOfSellers.length() > 0) {
            String[] listOfSellerStrings = stringListOfSellers.split(";");
            for (String sellerString : listOfSellerStrings) {
                sellers.add(new Seller(sellerString, true, true));
            }
        }

        return sellers;
    }

    public static void main(String[] args) {
        User loggedOnUser = null;
        Scanner scan = new Scanner(System.in);

        try (Socket socket = new Socket("localhost", 8080)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            System.out.println("Welcome to Messenger (not Facebook)!");
            System.out.println("Log in (or Sign Up) with Username or Email:");
            String usernameOrEmail = scan.nextLine();

            String checkUsernameRequest = "[CHECK.USERNAME]" + usernameOrEmail;
            sendRequest(writer, checkUsernameRequest);
            boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

            String checkEmailRequest = "[CHECK.EMAIL]" + usernameOrEmail;
            sendRequest(writer, checkEmailRequest);
            boolean isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

            if (!(isUsernameTaken || isEmailRegistered)) {
                System.out.println("Seems like you don't have an account yet!");
                System.out.println("Would you like to make an account? (Y/N)");
                boolean createAccount = scan.nextLine().equalsIgnoreCase("Y");

                if (createAccount) {
                    System.out.println("Great! Let's get you set up!");

                    String username;
                    while (true) {
                        System.out.println("Create your new username (,<>; change to _):");
                        username = scan.nextLine();

                        checkUsernameRequest = "[CHECK.USERNAME]" + username;
                        sendRequest(writer, checkUsernameRequest);
                        isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                        if (isUsernameTaken) {
                            System.out.println("Sorry! That username is already taken\nTry again? (Y/N)");
                            boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");

                            if (!tryAgain) {
                                username = null;
                                System.out.println("Sorry to see you go :( Have a great day!");
                                break;
                            }
                        } else {
                            username = username.replaceAll("[,<>;]", "_");
                            break;
                        }
                    }

                    if (username != null) {
                        String email;
                        while (true) {
                            System.out.println("Register with your email ID:");
                            email = scan.nextLine();

                            checkEmailRequest = "[CHECK.EMAIL]" + email;
                            sendRequest(writer, checkEmailRequest);
                            isEmailRegistered = Boolean.parseBoolean(readResponse(reader));
                            if (isEmailRegistered) {
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

                                if (!(role.equalsIgnoreCase("SELLER") ||
                                        role.equalsIgnoreCase("CUSTOMER"))) {
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

                                String createAccountRequest = String.format("[CREATE.USER]%s, %s, %s, %s",
                                        username, email, password, role);
                                sendRequest(writer, createAccountRequest);
                                String response = readResponse(reader);

                                if (response != null && response.substring(0, response.indexOf("<"))
                                        .equalsIgnoreCase("Seller")) {
                                    loggedOnUser = new Seller(response, true, true);
                                } else {
                                    loggedOnUser = new Customer(response, true);
                                }

                                System.out.println("You're all set!");
                            }
                        }
                    }
                } else {
                    System.out.println("Sorry to see you go :( Have a great day!");
                    sendRequest(writer, "[LOGOUT]");
                }

            } else {
                String fetchAccountRequest = "[FETCH.USER]" + usernameOrEmail;
                sendRequest(writer, fetchAccountRequest);
                String fetchResponse = readResponse(reader);

                if (fetchResponse != null && fetchResponse.substring(0, fetchResponse.indexOf("<"))
                        .equalsIgnoreCase("Seller")) {
                    loggedOnUser = new Seller(fetchResponse, true, true);
                } else {
                    loggedOnUser = new Customer(fetchResponse, true);
                }

                System.out.println("Enter password:");
                String password = scan.nextLine();

                if (password.equals(loggedOnUser.getPassword())) {
                    System.out.printf("Welcome back %s!\n", loggedOnUser.getUsername().toUpperCase());

                    String fetchNumUnreadRequest;
                    if (loggedOnUser instanceof Seller) {
                        fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Seller) loggedOnUser).detailedToString();
                    } else {
                        fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Customer) loggedOnUser).detailedToString();
                    }
                    sendRequest(writer, fetchNumUnreadRequest);
                    fetchResponse = readResponse(reader);

                    if (fetchResponse != null && Integer.parseInt(fetchResponse) > 0) {
                        System.out.printf("You have %d new unread conversation%s\n",
                                Integer.parseInt(fetchResponse), (Integer.parseInt(fetchResponse) != 1) ? "s" : "");
                    }

                } else {
                    loggedOnUser = null;
                    sendRequest(writer, "[LOGOUT]");
                    System.out.println("Incorrect Password");
                }
            }

            if (loggedOnUser != null) {
                runMainMenu(scan, reader, writer, loggedOnUser);
                sendRequest(writer, "[LOGOUT]");
                System.out.println("Goodbye!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMainMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        while (true) {
            printMainMenu(loggedOnUser);

            int selectedOption;
            while (true) {
                try {
                    selectedOption = Integer.parseInt(scan.nextLine());
                    if ((loggedOnUser instanceof Seller && selectedOption < 0) ||
                            (loggedOnUser instanceof Customer && selectedOption <= 0) || selectedOption > 5) {
                        System.out.println("Invalid Option");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Option");
                }
            }

            if (selectedOption == 0 && loggedOnUser instanceof Seller) {
                ArrayList<Store> stores = ((Seller) loggedOnUser).getStores();

                while (true) {
                    System.out.println("----Your Stores----");
                    if (stores.size() > 0) {
                        System.out.println("[To See its Details, Enter the Store Number]");
                    } else {
                        System.out.println("[No Stores]");
                    }
                    for (int i = 0; i < stores.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, stores.get(i).getStoreName());
                    }
                    System.out.printf("%d. Add Store\n", stores.size() + 1);
                    System.out.printf("%d. Delete a Store\n", stores.size() + 2);
                    System.out.printf("%d. Back to Main Menu\n", stores.size() + 3);

                    int storeOption;
                    while (true) {
                        try {
                            storeOption = Integer.parseInt(scan.nextLine());
                            if (storeOption <= 0 || storeOption > stores.size() + 3) {
                                System.out.println("Invalid Option");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (storeOption == stores.size() + 1) {
                        System.out.println("Enter New Store Name:");
                        String newStoreName = scan.nextLine();

                        ((Seller) loggedOnUser).addStore(writer, new Store(newStoreName, (Seller) loggedOnUser));
                    } else if (storeOption == stores.size() + 2) {
                        System.out.println("Enter Store Number:");
                        int storeNumber;
                        while (true) {
                            try {
                                storeNumber = Integer.parseInt(scan.nextLine());
                                if (storeNumber <= 0 || storeNumber > stores.size()) {
                                    System.out.println("Invalid Option");
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid Option");
                            }
                        }

                        System.out.println("Are you sure? (Y/N)");
                        boolean deleteStore = scan.nextLine().equalsIgnoreCase("Y");
                        if (deleteStore) {
                            ((Seller) loggedOnUser).removeStore(writer, stores.get(storeNumber - 1));
                        }
                    } else if (storeOption == stores.size() + 3) {
                        break;
                    } else if (storeOption <= stores.size()) {
                        System.out.println("----Store Details----");
                        System.out.println("Name: " + stores.get(storeOption - 1).getStoreName());
                        System.out.println("Seller: " + stores.get(storeOption - 1).getSeller().getUsername());
                        System.out.println("[Hit Enter to Continue]");
                        scan.nextLine();
                    } else {
                        System.out.println("Invalid Option");
                    }
                }
            } else if (selectedOption == 1) {
                runConversationsMenu(scan, reader, writer, loggedOnUser);
            } else if (selectedOption == 2) {
                runListMenu(scan, reader, writer, loggedOnUser);
            } else if (selectedOption == 3) {
                runSearchMenu(scan, reader, writer, loggedOnUser);
            } else if (selectedOption == 4) {
                if (runAccountMenu(scan, reader, writer, loggedOnUser)) {
                    break;
                }
            } else {
                System.out.println("Logging Out...");
                break;
            }
        }
    }

    private static void runConversationsMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        ArrayList<Conversation> conversations = refreshVisibleConversations(reader, writer, loggedOnUser);

        if (conversations.size() > 0) {
            while (true) {
                conversations = refreshVisibleConversations(reader, writer, loggedOnUser);

                if (conversations.size() == 0) {
                    System.out.println("----Your Conversations----");
                    System.out.println("[No Conversations]");
                    System.out.println("[Hit Enter to Continue]");
                    scan.nextLine();
                    break;
                }

                System.out.println("----Your Conversations----");
                if (conversations.size() > 0) {
                    System.out.println("[To Open, Enter Conversation No.]");
                    for (int i = 0; i < conversations.size(); i++) {
                        System.out.printf("%d. %s\n", i + 1, conversations.get(i).getConversationID());
                    }
                    System.out.printf("%d. Export Conversations\n", conversations.size() + 1);
                } else {
                    System.out.println("[No Conversations]");
                }
                System.out.printf("%d. Back to Main Menu\n", conversations.size() + 2);

                int conversationNumber;
                while (true) {
                    try {
                        conversationNumber = Integer.parseInt(scan.nextLine());
                        if (conversationNumber <= 0 || conversationNumber > conversations.size() + 2) {
                            System.out.println("Invalid Option");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (conversationNumber == conversations.size() + 2) {
                    break;
                } else if (conversationNumber == conversations.size() + 1) {
                    System.out.println("Enter Conversations to Export Separated by Commas (eg. 1,3,4):");
                    String exportingIndexes = scan.nextLine();
                    System.out.println("Enter .csv Destination (eg. folder/subfolder)");
                    String CSVDestination = scan.nextLine();

                    try {
                        ArrayList<Conversation> exportingConversations = new ArrayList<>();
                        for (String stringIndex : exportingIndexes.split(",")) {
                            int index = Integer.parseInt(stringIndex) - 1;
                            if (index >= 0 && index < conversations.size()) {
                                exportingConversations.add(conversations.get(index));
                            }
                        }

                        if (convertConversationsToCSV(exportingConversations, CSVDestination)) {
                            System.out.println("Successfully Converted to CSV!");
                        } else {
                            System.out.println("Conversion Failed");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Conversations");
                    } catch (NullPointerException e) {
                        System.out.println("Conversion Failed. No File Destination Inputted");
                    } catch (IOException e) {
                        System.out.println("Conversion Failed");
                    }

                } else if (conversationNumber <= conversations.size()) {
                    Conversation conversation = conversations.get(conversationNumber - 1);
                    if (loggedOnUser instanceof Seller) {
                        conversation.setSellerUnread(false, writer);
                    } else {
                        conversation.setCustomerUnread(writer, false);
                    }
                    runConversationActions(scan, reader, writer, loggedOnUser, conversation);
                }
            }
        } else {
            System.out.println("----Your Conversations----");
            System.out.println("[No Conversations]");
            System.out.println("[Hit Enter to Continue]");
            scan.nextLine();
        }
    }

    private static void runConversationActions(Scanner scan, BufferedReader reader, PrintWriter writer,
                                               User loggedOnUser, Conversation conversation) {
        ArrayList<Message> messages = refreshVisibleMessages(reader, writer, conversation, loggedOnUser);
        User recipient = (loggedOnUser instanceof Seller) ? conversation.getCustomer() :
                conversation.getSeller();

        String recipientActiveRequest = "[CHECK.RECIPIENT_ACTIVE]" + recipient;
        sendRequest(writer, recipientActiveRequest);
        boolean isRecipientActive = Boolean.parseBoolean(readResponse(reader));

        if (!isRecipientActive) {
            for (int i = 0; i < messages.size(); i++) {
                String message;
                if (loggedOnUser.isRequestsCensorship()) {
                    message = messages.get(i).getCensoredMessage(loggedOnUser);
                } else {
                    message = messages.get(i).getMessage();
                }
                System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(), message);
            }
            System.out.println("[Recipient has Deleted Their Account]");
            System.out.println("[Hit Enter to Continue]");
            scan.nextLine();
        } else {
            System.out.println("--------");
            System.out.println("0. Refresh");
            System.out.println("1. Load All Messages");
            System.out.println("2. Import .txt File");
            System.out.println("3. Send Message");
            System.out.println("4. Back to Conversation List");
            System.out.println("---");
            System.out.println("[To Execute the Below Actions, Enter MsgIndex.Action (eg. 3.5 to Edit Message 3)]");
            System.out.println("---");
            System.out.println("5. Edit Message");
            System.out.println("6. Delete Message");

            int lowerLimit = Math.min(messages.size(), 20);
            for (int i = messages.size() - lowerLimit; i < messages.size(); i++) {
                String message;
                if (loggedOnUser.isRequestsCensorship()) {
                    message = messages.get(i).getCensoredMessage(loggedOnUser);
                } else {
                    message = messages.get(i).getMessage();
                }
                System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(), message);
            }

            while (true) {
                String conversationAction = scan.nextLine();

                ArrayList<Conversation> refreshedConversations = refreshVisibleConversations(reader, writer, loggedOnUser);
                for (Conversation refreshedConversation : refreshedConversations) {
                    if (refreshedConversation.getFileName().equals(conversation.getFileName())) {
                        conversation = refreshedConversation;
                    }
                }
                recipient = (loggedOnUser instanceof Seller) ? conversation.getCustomer() :
                        conversation.getSeller();

                if (conversationAction.contains(".")) {
                    String[] messageActions = conversationAction.split("\\.");

                    int messageID = 0;
                    int action = 0;
                    try {
                        messageID = Integer.parseInt(messageActions[0]);
                        action = Integer.parseInt(messageActions[1]);
                        if (messageID > messages.size() - 1 || messageID < 0) {
                            System.out.println("Invalid Input. No Such Message Exists");
                            break;
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid Input");
                    }

                    if (action == 5) {
                        if (!recipient.getBlockedUsers().contains(loggedOnUser) &&
                                !loggedOnUser.getBlockedUsers().contains(recipient)) {
                            System.out.print("Updated Message: ");
                            String updatedMessage = scan.nextLine();
                            if (loggedOnUser.editMessage(reader, writer, messages.get(messageID), conversation,
                                    updatedMessage)) {
                                System.out.println("Edited Message");
                            } else {
                                System.out.println("You Cannot Edit this Message");
                            }
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (action == 6) {
                        if (loggedOnUser.deleteMessage(reader, writer, messages.get(messageID), conversation)) {
                            System.out.println("Deleted Message");
                        } else {
                            System.out.println("Sorry! Could not delete this message");
                        }
                    } else {
                        System.out.println("Invalid Option");
                    }
                } else {
                    int action;
                    while (true) {
                        try {
                            action = Integer.parseInt(conversationAction);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                            conversationAction = scan.nextLine();
                        }
                    }

                    if (action == 0) {
                        messages = refreshVisibleMessages(reader, writer, conversation, loggedOnUser);

                        lowerLimit = Math.min(messages.size(), 20);
                        for (int i = messages.size() - lowerLimit; i < messages.size(); i++) {
                            String message;
                            if (loggedOnUser.isRequestsCensorship()) {
                                message = messages.get(i).getCensoredMessage(loggedOnUser);
                            } else {
                                message = messages.get(i).getMessage();
                            }
                            System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(), message);
                        }
                    } else if (action == 1) {
                        for (int i = 0; i < messages.size(); i++) {
                            System.out.printf("[%d] %s: %s\n", i, messages.get(i).getSender().getUsername(),
                                    messages.get(i).getMessage());
                        }
                    } else if (action == 2) {
                        if (!recipient.getBlockedUsers().contains(loggedOnUser) &&
                                !loggedOnUser.getBlockedUsers().contains(recipient)) {
                            System.out.println("Enter path to .txt file:");
                            String txtPath = scan.nextLine();

                            if (conversation.importTXT(reader, writer, txtPath, loggedOnUser, recipient)) {
                                System.out.println("Message Sent Successfully!");
                            } else {
                                System.out.println("Error: Could not send message");
                            }
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (action == 3) {
                        if (!recipient.getBlockedUsers().contains(loggedOnUser) &&
                                !loggedOnUser.getBlockedUsers().contains(recipient)) {
                            System.out.print("Your Message: ");
                            String message = scan.nextLine();

                            if (loggedOnUser.sendMessageToUser(reader, writer, message, recipient)) {
                                System.out.println("Sent!");
                            } else {
                                System.out.println("Message Failed!");
                            }
                        } else {
                            System.out.println("Sorry! You cannot message this user.");
                        }
                    } else if (action == 4) {
                        break;
                    } else {
                        System.out.println("Invalid Option");
                    }
                }
            }
        }
    }

    private static void runListMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        if (loggedOnUser instanceof Seller) {
            while (true) {
                ArrayList<Customer> customers = refreshCustomers(reader, writer, loggedOnUser);

                System.out.println("----All Customers----");
                if (customers.size() > 0) {
                    System.out.println("[To Select a Customer, Enter their No.]");
                } else {
                    System.out.println("[No Customers]");
                }

                for (int i = 0; i < customers.size(); i++) {
                    String extraInformation = "";

                    if (loggedOnUser.getBlockedUsers().contains(customers.get(i))) {
                        extraInformation += "BLOCKED";
                    }
                    if (loggedOnUser.getInvisibleUsers().contains(customers.get(i))) {
                        if (extraInformation.length() > 0) {
                            extraInformation += " & ";
                        }
                        extraInformation += "INVISIBLE TO";
                    }

                    System.out.printf("%d. %s | %s%s\n", i + 1, customers.get(i).getUsername(),
                            customers.get(i).getEmail(), (extraInformation.length() > 0) ? " | Currently: " +
                                    extraInformation : "");
                }
                System.out.printf("%d. Back to Main Menu\n", customers.size() + 1);

                int customerNumber;
                while (true) {
                    try {
                        customerNumber = Integer.parseInt(scan.nextLine());
                        if (customerNumber <= 0 || customerNumber > customers.size() + 1) {
                            System.out.println("Invalid Option");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (customerNumber == customers.size() + 1) {
                    break;
                } else if (customerNumber <= customers.size()) {
                    Customer selectedCustomer = customers.get(customerNumber - 1);
                    while (true) {
                        printUserActionMenu(loggedOnUser, selectedCustomer);

                        int userChoice;
                        while (true) {
                            try {
                                userChoice = Integer.parseInt(scan.nextLine());
                                if (userChoice <= 0 || userChoice > 4) {
                                    System.out.println("Invalid Option");
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid Option");
                            }
                        }

                        customers = refreshCustomers(reader, writer, loggedOnUser);
                        for (Customer customer : customers) {
                            if (customer.equals(selectedCustomer)) {
                                selectedCustomer = customer;
                            }
                        }

                        if (userChoice == 1) {
                            if (!selectedCustomer.getBlockedUsers().contains(loggedOnUser) &&
                                    !loggedOnUser.getBlockedUsers().contains(selectedCustomer)) {
                                System.out.print("Your Message: ");
                                String message = scan.nextLine();

                                if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedCustomer)) {
                                    System.out.println("Sent!");
                                } else {
                                    System.out.println("Message Failed!");
                                }
                            } else {
                                System.out.println("Sorry! You cannot message this user.");
                            }
                        } else if (userChoice == 2) {
                            ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                            if (blockedUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeBlockedUser(writer, selectedCustomer);
                                System.out.println("Unblocked Customer");
                            } else {
                                loggedOnUser.addBlockedUser(writer, selectedCustomer);
                                System.out.println("Blocked Customer");
                            }
                            break;
                        } else if (userChoice == 3) {
                            ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                            if (invisibleUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeInvisibleUser(writer, selectedCustomer);
                                System.out.println("Now Visible to Customer");
                            } else {
                                loggedOnUser.addInvisibleUser(writer, selectedCustomer);
                                System.out.println("Now Invisible to Customer");
                            }
                            break;
                        } else {
                            break;
                        }
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        } else {
            while (true) {
                ArrayList<Store> stores = new ArrayList<>();

                String userString = ((Customer) loggedOnUser).detailedToString();
                String listStoresRequest = "[LIST.VISIBLE_STORES]" + userString;
                sendRequest(writer, listStoresRequest);
                String stringListOfStores = readResponse(reader);

                if (stringListOfStores != null && stringListOfStores.length() > 0) {
                    String[] listOfStoreStrings = stringListOfStores.split(";");
                    for (String storeString : listOfStoreStrings) {
                        stores.add(new Store(storeString));
                    }
                }

                System.out.println("----All Stores----");
                if (stores.size() > 0) {
                    System.out.println("[To Select a Store, Enter its No.]");
                } else {
                    System.out.println("[No Stores]");
                }
                for (int i = 0; i < stores.size(); i++) {
                    String extraInformation = "";

                    if (loggedOnUser.getBlockedUsers().contains(stores.get(i).getSeller())) {
                        extraInformation += "BLOCKED";
                    }
                    if (loggedOnUser.getInvisibleUsers().contains(stores.get(i).getSeller())) {
                        if (extraInformation.length() > 0) {
                            extraInformation += " & ";
                        }
                        extraInformation += "INVISIBLE TO";
                    }

                    System.out.printf("%d. %s by %s%s\n", i + 1, stores.get(i).getStoreName(),
                            stores.get(i).getSeller().getUsername(), (extraInformation.length() > 0) ? " | Currently: "
                                    + extraInformation : "");
                }
                System.out.printf("%d. Back to Main Menu\n", stores.size() + 1);

                int storeNumber;
                while (true) {
                    try {
                        storeNumber = Integer.parseInt(scan.nextLine());
                        if (storeNumber <= 0 || storeNumber > stores.size() + 1) {
                            System.out.println("Invalid Option");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (storeNumber == stores.size() + 1) {
                    break;
                } else if (storeNumber <= stores.size()) {
                    Seller selectedStoreSeller = stores.get(storeNumber - 1).getSeller();
                    if (selectedStoreSeller.getBlockedUsers().contains(loggedOnUser)) {
                        System.out.println("Sorry! You cannot view this store");
                    } else {
                        loggedOnUser.sendMessageToUser(reader, writer, loggedOnUser.getUsername() +
                                        " looked up the Store: " + stores.get(storeNumber - 1).getStoreName(),
                                selectedStoreSeller);

                        while (true) {
                            printUserActionMenu(loggedOnUser, selectedStoreSeller);

                            int userChoice;
                            while (true) {
                                try {
                                    userChoice = Integer.parseInt(scan.nextLine());
                                    if (userChoice <= 0 || userChoice > 4) {
                                        System.out.println("Invalid Option");
                                    } else {
                                        break;
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid Option");
                                }
                            }

                            stores = refreshStores(reader, writer, loggedOnUser);
                            for (Store store : stores) {
                                if (store.getSeller().equals(selectedStoreSeller)) {
                                    selectedStoreSeller = store.getSeller();
                                }
                            }

                            if (userChoice == 1) {
                                if (!selectedStoreSeller.getBlockedUsers().contains(loggedOnUser) &&
                                        !loggedOnUser.getBlockedUsers().contains(selectedStoreSeller)) {
                                    System.out.print("Your Message: ");
                                    String message = scan.nextLine();

                                    if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedStoreSeller)) {
                                        System.out.println("Sent!");
                                    } else {
                                        System.out.println("Message Failed!");
                                    }
                                } else {
                                    System.out.println("Sorry! You cannot message this user.");
                                }
                            } else if (userChoice == 2) {
                                ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                                if (blockedUsers.contains(selectedStoreSeller)) {
                                    loggedOnUser.removeBlockedUser(writer, selectedStoreSeller);
                                    System.out.println("Unblocked Store Owner");
                                } else {
                                    loggedOnUser.addBlockedUser(writer, selectedStoreSeller);
                                    System.out.println("Blocked Store Owner");
                                }
                                break;
                            } else if (userChoice == 3) {
                                ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                                if (invisibleUsers.contains(selectedStoreSeller)) {
                                    loggedOnUser.removeInvisibleUser(writer, selectedStoreSeller);
                                    System.out.println("Now Visible to Store Owner");
                                } else {
                                    loggedOnUser.addInvisibleUser(writer, selectedStoreSeller);
                                    System.out.println("Now Invisible Store Owner");
                                }
                                break;
                            } else {
                                break;
                            }
                        }
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        }
    }

    /*
     * private static void runSearchMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser)
     * Method that lets user search for a username or email
     * and lists corresponding users of the opposite role.
     */

    private static void runSearchMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        System.out.println("Search by Username or Email (full or part):");
        String searchKeyword = scan.nextLine();

        if (loggedOnUser instanceof Seller) {
            while (true) {
                ArrayList<Customer> customers = refreshSearchCustomers(reader, writer, loggedOnUser, searchKeyword);

                System.out.println("----Search Result----");
                if (customers.size() > 0) {
                    System.out.println("[To Select a Customer, Enter their No.]");
                } else {
                    System.out.println("[No Matching Search Results]");
                }

                for (int i = 0; i < customers.size(); i++) {
                    String extraInformation = "";

                    if (loggedOnUser.getBlockedUsers().contains(customers.get(i))) {
                        extraInformation += "BLOCKED";
                    }
                    if (loggedOnUser.getInvisibleUsers().contains(customers.get(i))) {
                        if (extraInformation.length() > 0) {
                            extraInformation += " & ";
                        }
                        extraInformation += "INVISIBLE TO";
                    }

                    System.out.printf("%d. %s | %s%s\n", i + 1, customers.get(i).getUsername(),
                            customers.get(i).getEmail(), (extraInformation.length() > 0) ? " | Currently: " +
                                    extraInformation : "");
                }
                System.out.printf("%d. Back to Main Menu\n", customers.size() + 1);

                int customerNumber;
                while (true) {
                    try {
                        customerNumber = Integer.parseInt(scan.nextLine());
                        if (customerNumber <= 0 || customerNumber > customers.size() + 1) {
                            System.out.println("Invalid Option");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (customerNumber == customers.size() + 1) {
                    break;
                } else if (customerNumber <= customers.size()) {
                    Customer selectedCustomer = customers.get(customerNumber - 1);
                    while (true) {
                        printUserActionMenu(loggedOnUser, selectedCustomer);

                        int userChoice;
                        while (true) {
                            try {
                                userChoice = Integer.parseInt(scan.nextLine());
                                if (userChoice <= 0 || userChoice > 4) {
                                    System.out.println("Invalid Option");
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid Option");
                            }
                        }

                        customers = refreshSearchCustomers(reader, writer, loggedOnUser, searchKeyword);
                        for (Customer customer : customers) {
                            if (customer.equals(selectedCustomer)) {
                                selectedCustomer = customer;
                            }
                        }

                        if (userChoice == 1) {
                            if (!selectedCustomer.getBlockedUsers().contains(loggedOnUser) &&
                                    !loggedOnUser.getBlockedUsers().contains(selectedCustomer)) {
                                System.out.print("Your Message: ");
                                String message = scan.nextLine();

                                if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedCustomer)) {
                                    System.out.println("Sent!");
                                } else {
                                    System.out.println("Message Failed!");
                                }
                            } else {
                                System.out.println("Sorry! You cannot message this user.");
                            }
                        } else if (userChoice == 2) {
                            ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                            if (blockedUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeBlockedUser(writer, selectedCustomer);
                                System.out.println("Unblocked Customer");
                            } else {
                                loggedOnUser.addBlockedUser(writer, selectedCustomer);
                                System.out.println("Blocked Customer");
                            }
                            break;
                        } else if (userChoice == 3) {
                            ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                            if (invisibleUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeInvisibleUser(writer, selectedCustomer);
                                System.out.println("Now Visible to Customer");
                            } else {
                                loggedOnUser.addInvisibleUser(writer, selectedCustomer);
                                System.out.println("Now Invisible to Customer");
                            }
                            break;
                        } else {
                            break;
                        }
                    }
                } else {
                    System.out.println("Invalid Option");
                }
            }
        } else {
            while (true) {
                ArrayList<Seller> sellers = refreshSearchSeller(reader, writer, loggedOnUser, searchKeyword);

                System.out.println("----Search Result----");
                if (sellers.size() > 0) {
                    System.out.println("[To Select a Seller, Enter their No.]");
                } else {
                    System.out.println("[No Matching Search Results]");
                }
                for (int i = 0; i < sellers.size(); i++) {
                    String extraInformation = "";

                    if (loggedOnUser.getBlockedUsers().contains(sellers.get(i))) {
                        extraInformation += "BLOCKED";
                    }
                    if (loggedOnUser.getInvisibleUsers().contains(sellers.get(i))) {
                        if (extraInformation.length() > 0) {
                            extraInformation += " & ";
                        }
                        extraInformation += "INVISIBLE TO";
                    }

                    System.out.printf("%d. %s | %s%s\n", i + 1, sellers.get(i).getUsername(), sellers.get(i).getEmail(),
                            (extraInformation.length() > 0) ? " | Currently:" + extraInformation : "");
                }
                System.out.printf("%d. Back to Main Menu\n", sellers.size() + 1);

                int sellerNumber;
                while (true) {
                    try {
                        sellerNumber = Integer.parseInt(scan.nextLine());
                        if (sellerNumber <= 0 || sellerNumber > sellers.size() + 1) {
                            System.out.println("Invalid Option");
                        } else {
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Option");
                    }
                }

                if (sellerNumber == sellers.size() + 1) {
                    break;
                } else if (sellerNumber <= sellers.size()) {
                    Seller selectedSeller = sellers.get(sellerNumber - 1);
                    while (true) {
                        printUserActionMenu(loggedOnUser, selectedSeller);

                        int userChoice;
                        while (true) {
                            try {
                                userChoice = Integer.parseInt(scan.nextLine());
                                if (userChoice <= 0 || userChoice > 4) {
                                    System.out.println("Invalid Option");
                                } else {
                                    break;
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid Option");
                            }
                        }

                        sellers = refreshSearchSeller(reader, writer, loggedOnUser, searchKeyword);
                        for (Seller seller : sellers) {
                            if (seller.equals(selectedSeller)) {
                                selectedSeller = seller;
                            }
                        }

                        if (userChoice == 1) {
                            if (!selectedSeller.getBlockedUsers().contains(loggedOnUser) &&
                                    !loggedOnUser.getBlockedUsers().contains(selectedSeller)) {
                                System.out.print("Your Message: ");
                                String message = scan.nextLine();

                                if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedSeller)) {
                                    System.out.println("Sent!");
                                } else {
                                    System.out.println("Message Failed!");
                                }
                            } else {
                                System.out.println("Sorry! You cannot message this user.");
                            }
                        } else if (userChoice == 2) {
                            ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                            if (blockedUsers.contains(selectedSeller)) {
                                loggedOnUser.removeBlockedUser(writer, selectedSeller);
                                System.out.println("Unblocked Seller");
                            } else {
                                loggedOnUser.addBlockedUser(writer, selectedSeller);
                                System.out.println("Blocked Seller");
                            }
                            break;
                        } else if (userChoice == 3) {
                            ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                            if (invisibleUsers.contains(selectedSeller)) {
                                loggedOnUser.removeInvisibleUser(writer, selectedSeller);
                                System.out.println("Now Visible to Seller");
                            } else {
                                loggedOnUser.addInvisibleUser(writer, selectedSeller);
                                System.out.println("Now Invisible Seller");
                            }
                            break;
                        } else {
                            break;
                        }
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
        System.out.println("2. Change Password");
        System.out.println("3. Manage Blocked Users");
        System.out.println("4. Manage Invisible to Users");
        System.out.println("5. Edit Censoring");
        System.out.println("6. Delete Account");
        System.out.println("7. Back to Main Menu");
    }

    private static void printCensoredWords(ArrayList<String> censoredWordPairs) {
        for (int i = 0; i < censoredWordPairs.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, censoredWordPairs.get(i).replace(":", " >>> "));
        }
    }

    private static void printUserActionMenu(User user, User selectedUser) {
        String keyword = (user instanceof Seller) ? "Customer" : "Seller";
        System.out.printf("----Selected: %s----\n", selectedUser.getUsername());
        System.out.printf("1. Message %s\n", keyword);
        System.out.printf("2. %s %s\n", (user.getBlockedUsers().contains(selectedUser)) ?
                "Unblock" : "Block", keyword);
        System.out.printf("3. Become %s to %s\n", (user.getInvisibleUsers().contains(selectedUser)) ?
                "Visible" : "Invisible", keyword);
        System.out.printf("4. Back to %s List\n", keyword);
    }

    private static boolean runAccountMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        boolean deleteUser = false;
        while (true) {
            printEditAccountMenu();
            int accountOption;
            while (true) {
                try {
                    accountOption = Integer.parseInt(scan.nextLine());
                    if (accountOption <= 0 || accountOption > 7) {
                        System.out.println("Invalid Option");
                    } else {
                        break;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid Option");
                }
            }

            if (accountOption == 1) {
                while (true) {
                    System.out.println("Enter new username:");
                    String username = scan.nextLine().replaceAll("[,<>;]", "_");

                    String checkUsernameRequest = "[CHECK.USERNAME]" + username;
                    sendRequest(writer, checkUsernameRequest);
                    boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                    if (isUsernameTaken) {
                        System.out.println("Sorry! That username is already taken\nTry again? (Y/N)");
                        boolean tryAgain = scan.nextLine().equalsIgnoreCase("Y");
                        if (!tryAgain) {
                            break;
                        }
                    } else {
                        loggedOnUser.setUsername(reader, writer, username);
                        System.out.println("Username Changed!");
                        break;
                    }
                }
            } else if (accountOption == 2) {
                System.out.println("Enter current password:");
                String currentPassword = scan.nextLine();
                if (currentPassword.equals(loggedOnUser.getPassword())) {
                    System.out.println("Enter new password:");
                    String newPassword = scan.nextLine();

                    System.out.println("Confirm new password:");
                    String confirmPassword = scan.nextLine();

                    if (newPassword.equals(confirmPassword)) {
                        loggedOnUser.setPassword(reader, writer, confirmPassword);
                        System.out.println("Password Changed!");
                    } else {
                        System.out.println("Did not match!");
                    }
                } else {
                    System.out.println("Invalid Password");
                }
            } else if (accountOption == 3) {
                ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                while (true) {
                    System.out.println("----Blocked Users----");
                    if (blockedUsers.size() > 0) {
                        System.out.println("[To Unblock a User, Enter their No.]");
                    } else {
                        System.out.println("[No Blocked Users]");
                    }
                    for (int i = 0; i < blockedUsers.size(); i++) {
                        System.out.printf("[%d] %s | %s\n", i + 1, blockedUsers.get(i).getUsername(),
                                blockedUsers.get(i).getEmail());
                    }
                    System.out.printf("%d. Back to Account Menu\n", blockedUsers.size() + 1);

                    int blockedOption;
                    while (true) {
                        try {
                            blockedOption = Integer.parseInt(scan.nextLine());
                            if (blockedOption <= 0 || blockedOption > blockedUsers.size() + 1) {
                                System.out.println("Invalid Option");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (blockedOption == blockedUsers.size() + 1) {
                        break;
                    } else {
                        loggedOnUser.removeBlockedUser(writer, blockedUsers.get(blockedOption - 1));
                        System.out.println("Unblocked User");
                    }
                }
            } else if (accountOption == 4) {
                ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                while (true) {
                    System.out.println("----Invisible to Users----");
                    if (invisibleUsers.size() > 0) {
                        System.out.println("[To Become Visible to a User, Enter their No.]");
                    } else {
                        System.out.println("[No Invisible to Users]");
                    }
                    for (int i = 0; i < invisibleUsers.size(); i++) {
                        System.out.printf("[%d] %s | %s\n", i + 1, invisibleUsers.get(i).getUsername(),
                                invisibleUsers.get(i).getEmail());
                    }
                    System.out.printf("%d. Back to Account Menu\n", invisibleUsers.size() + 1);

                    int visibleOption;
                    while (true) {
                        try {
                            visibleOption = Integer.parseInt(scan.nextLine());
                            if (visibleOption <= 0 || visibleOption > invisibleUsers.size() + 1) {
                                System.out.println("Invalid Option");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (visibleOption == invisibleUsers.size() + 1) {
                        break;
                    } else {
                        loggedOnUser.removeInvisibleUser(writer, invisibleUsers.get(visibleOption - 1));
                        System.out.println("Now Visible to User");
                    }
                }
            } else if (accountOption == 5) {
                ArrayList<String> censoredWords = loggedOnUser.getCensoredWords();
                while (true) {
                    System.out.println("----Censor Pairs----");
                    if (censoredWords.size() > 0) {
                        System.out.println("[To Delete, Enter Censor Pair No.]");
                        printCensoredWords(censoredWords);
                    } else {
                        System.out.println("[No Censor Pairs]");
                    }
                    System.out.printf("%d. Add Censor Pair\n", censoredWords.size() + 1);
                    System.out.printf("%d. Toggle Censoring (ON/OFF) | Currently: %s\n", censoredWords.size() + 2,
                            (loggedOnUser.isRequestsCensorship()) ? "ON" : "OFF");
                    System.out.printf("%d. Back to Account Menu\n", censoredWords.size() + 3);
                    int censorOption;
                    while (true) {
                        try {
                            censorOption = Integer.parseInt(scan.nextLine());
                            if (censorOption <= 0 || censorOption > censoredWords.size() + 3) {
                                System.out.println("Invalid Option");
                            } else {
                                break;
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid Option");
                        }
                    }

                    if (censorOption == censoredWords.size() + 1) {
                        System.out.println("Enter new pair as \"Word:Replacement\" or only \"Word\"");
                        String censorPair = scan.nextLine();
                        if (!(censorPair.contains(",") || censorPair.contains(">") || censorPair.contains("<"))) {
                            String[] newCensor = censorPair.split(":");
                            if (newCensor.length == 1) {
                                loggedOnUser.addCensoredWord(writer, newCensor[0] + ":" + "****");
                            } else {
                                loggedOnUser.addCensoredWord(writer, newCensor[0] + ":" + newCensor[1]);
                            }
                        } else {
                            System.out.println("You Cannot have Special Characters in Censoring!");
                        }
                    } else if (censorOption == censoredWords.size() + 2) {
                        loggedOnUser.toggleRequestsCensorship(writer);
                    } else if (censorOption == censoredWords.size() + 3) {
                        break;
                    } else {
                        loggedOnUser.removeCensoredWord(writer, censorOption - 1,
                                loggedOnUser.getCensoredWords().get(censorOption - 1));
                    }
                }
            } else if (accountOption == 6) {
                System.out.println("Are you sure? (Y/N)");
                deleteUser = scan.nextLine().equalsIgnoreCase("Y");
                if (deleteUser) {
                    String request = "[DELETE]" + ((loggedOnUser instanceof Seller) ?
                            ((Seller) loggedOnUser).detailedToString() : ((Customer) loggedOnUser).detailedToString());
                    sendRequest(writer, request);
                }
                break;
            } else {
                break;
            }
        }
        return deleteUser;
    }

    private static void printMainMenu(User user) {
        System.out.println("----Main----");
        if (user instanceof Seller) {
            System.out.println("0. Manage Stores");
        }
        System.out.println("1. View Conversations");
        System.out.printf("2. View All %ss\n", (user instanceof Seller) ? "Customer" : "Store");
        System.out.printf("3. Search %ss\n", (user instanceof Seller) ? "Customer" : "Seller");
        System.out.println("4. Edit Account");
        System.out.println("5. Log Out");
    }
}