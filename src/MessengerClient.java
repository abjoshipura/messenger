import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            JOptionPane.showMessageDialog(null, "Network Error");
            return null;
        }
    }

    /**
     * Converts the chosen Conversation objects to exportable .csv files and exports them to the parameter
     * destinationPath
     *
     * @param exportingConversations The ArrayList&lt;Conversation&gt; of conversations to be exported
     * @return Returns whether the .csv conversion and export succeeded
     * @throws IOException in the case of a conversion failure
     */
    public static boolean convertConversationsToCSV(BufferedReader reader, PrintWriter writer,
                                                    ArrayList<Conversation> exportingConversations, User loggedOnUser)
            throws IOException {

        Files.createDirectories(Paths.get("src/exports")); // Creates the subfolder exports if it does not exist
        File destinationFile = new File("src/exports");

        for (Conversation conversation : exportingConversations) {
            File tempTXTFile = new File(destinationFile, String.format("%s.txt", conversation.getConversationID()));
            tempTXTFile.createNewFile(); // Creates a new .txt file for the Message object csv Strings
            PrintWriter pw = new PrintWriter(new FileWriter(tempTXTFile, true));

            ArrayList<Message> temp = refreshVisibleMessages(reader, writer, conversation, loggedOnUser);
            for (Message msg : temp) {
                pw.println(msg.csvToString()); // Writes each csv String into the file
            }
            pw.close();

            File csvFile = new File(destinationFile, String.format("%s.csv", conversation.getConversationID()));
            if (csvFile.exists()) {
                csvFile.delete(); // Deletes the existing file
            }
            tempTXTFile.renameTo(csvFile); // Converts new .txt file to a .csv file
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
    public static ArrayList<Seller> refreshSearchSellers(BufferedReader reader, PrintWriter writer, User loggedOnUser,
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
        Scanner scan = new Scanner(System.in); // TODO: Remove Scanner

        try (Socket socket = new Socket("localhost", 8080)) {
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            JOptionPane.showMessageDialog(null, "Welcome to Messenger (not Facebook)!");

            String usernameOrEmail = JOptionPane.showInputDialog(null, "Log in (or Sign Up) with Username or Email:");

            while (usernameOrEmail != null && usernameOrEmail.equals("")) {
                JOptionPane.showMessageDialog(null, "Username/Email Cannot Be Blank!");
                usernameOrEmail = JOptionPane.showInputDialog(null, "Log in (or Sign Up) with Username or Email:");
            }

            String checkUsernameRequest = "[CHECK.USERNAME]" + usernameOrEmail;
            sendRequest(writer, checkUsernameRequest);
            boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));
            String checkEmailRequest = "[CHECK.EMAIL]" + usernameOrEmail;
            sendRequest(writer, checkEmailRequest);
            boolean isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

            if (usernameOrEmail == null) { // User chooses to exit application
                JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
            } else if (!(isUsernameTaken || isEmailRegistered)) {
                JOptionPane.showMessageDialog(null, "Seems like you don't have an account yet!");
                int selectedOption = JOptionPane.showConfirmDialog(null, "Would you like to make an account?",
                        "Create New Account", JOptionPane.YES_NO_OPTION);
                boolean createAccount = selectedOption == JOptionPane.YES_OPTION;

                if (createAccount) {
                    JOptionPane.showMessageDialog(null, "Great! Let's get you set up!");

                    String username;
                    while (true) {
                        username = JOptionPane.showInputDialog(null, "Create your new username (,<>; change to _):");

                        while (username != null && username.equals("")) {
                            JOptionPane.showMessageDialog(null, "Username Cannot Be Blank!");
                            username = JOptionPane.showInputDialog(null, "Create your new username (,<>; change to _):");
                        }

                        if (username != null) {
                            username = username.replaceAll("[,<>;]", "_");
                        }

                        checkUsernameRequest = "[CHECK.USERNAME]" + username;
                        sendRequest(writer, checkUsernameRequest);
                        isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                        if (username == null) { // User chooses to exit application
                            JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                            break;
                        } else if (isUsernameTaken) {
                            selectedOption = JOptionPane.showConfirmDialog(null, "Sorry! That username is already" +
                                    " taken.\nTry again?", "Duplicate Username", JOptionPane.YES_NO_OPTION);
                            boolean tryAgain = selectedOption == JOptionPane.YES_OPTION;

                            if (!tryAgain) {
                                username = null;
                                JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                                break;
                            }
                        } else {
                            break;
                        }
                    }

                    if (username != null) {
                        String email;
                        while (true) {
                            email = JOptionPane.showInputDialog(null, "Register with your email ID:");

                            while (email != null && email.equals("")) {
                                JOptionPane.showMessageDialog(null, "Email Cannot Be Blank!");
                                email = JOptionPane.showInputDialog(null, "Register with your email ID:");
                            }

                            checkEmailRequest = "[CHECK.EMAIL]" + email;
                            sendRequest(writer, checkEmailRequest);
                            isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

                            if (email == null) {
                                JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                                break;
                            } else if (isEmailRegistered) {
                                selectedOption = JOptionPane.showConfirmDialog(null, "Sorry! That email is " +
                                        "already registered.\nTry again?", "Duplicate Email", JOptionPane.YES_NO_OPTION);
                                boolean tryAgain = selectedOption == JOptionPane.YES_OPTION;

                                if (!tryAgain) {
                                    email = null;
                                    JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                                    break;
                                }
                            } else {
                                break;
                            }
                        }

                        if (email != null) {
                            String[] options = {"Seller", "Buyer"};
                            String role = (String) JOptionPane.showInputDialog(null, "What is your role?", "Role " +
                                            "Selection",
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

                            if (role != null) {
                                String password = JOptionPane.showInputDialog(null, "Create a password:");

                                while (password != null && password.equals("")) {
                                    JOptionPane.showMessageDialog(null, "Password Cannot Be Blank!");
                                    password = JOptionPane.showInputDialog(null, "Create a password:");
                                }

                                if (password == null) {
                                    JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                                    sendRequest(writer, "[LOGOUT]");
                                } else {
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

                                    JOptionPane.showMessageDialog(null, "You're all set!");
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                                sendRequest(writer, "[LOGOUT]");
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Sorry to see you go :( Have a great day!");
                    sendRequest(writer, "[LOGOUT]");
                }
            } else { // User already exists
                String fetchAccountRequest = "[FETCH.USER]" + usernameOrEmail;
                sendRequest(writer, fetchAccountRequest);
                String fetchResponse = readResponse(reader);

                if (fetchResponse != null && fetchResponse.substring(0, fetchResponse.indexOf("<"))
                        .equalsIgnoreCase("Seller")) {
                    loggedOnUser = new Seller(fetchResponse, true, true);
                } else {
                    loggedOnUser = new Customer(fetchResponse, true);
                }

                String password = JOptionPane.showInputDialog(null, "Enter password:");

                if (password != null && password.equals(loggedOnUser.getPassword())) {
                    JOptionPane.showMessageDialog(null, String.format("Welcome back %s!",
                            loggedOnUser.getUsername().toUpperCase()));

                    String fetchNumUnreadRequest;
                    if (loggedOnUser instanceof Seller) {
                        fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Seller) loggedOnUser).detailedToString();
                    } else {
                        fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Customer) loggedOnUser).detailedToString();
                    }
                    sendRequest(writer, fetchNumUnreadRequest);
                    fetchResponse = readResponse(reader);

                    if (fetchResponse != null && Integer.parseInt(fetchResponse) > 0) {
                        JOptionPane.showMessageDialog(null, String.format("You have %d new unread conversation%s",
                                Integer.parseInt(fetchResponse), (Integer.parseInt(fetchResponse) != 1) ? "s" : ""));
                    }

                } else {
                    loggedOnUser = null;
                    sendRequest(writer, "[LOGOUT]");
                    JOptionPane.showMessageDialog(null, "Incorrect Password");
                }
            }

            if (loggedOnUser != null) {
                runMainMenu(scan, reader, writer, loggedOnUser);
                sendRequest(writer, "[LOGOUT]");
                JOptionPane.showMessageDialog(null, "Goodbye!");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void runMainMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        while (true) {
            int selectedOption = printMainMenu(loggedOnUser);

            if (selectedOption == 0 && loggedOnUser instanceof Seller) {
                ArrayList<Store> stores = ((Seller) loggedOnUser).getStores();
                while (true) {
                    String[] storeArray = new String[stores.size()];
                    for (int i = 0; i < stores.size(); i++) {
                        storeArray[i] = stores.get(i).getStoreName();
                    }

                    String[] storeOptions = {"Create Store", "View Stores", "Cancel"};
                    String[] editOptions = {"Rename", "Delete", "Cancel"};

                    int storeMenu = JOptionPane.showOptionDialog(null, "Select an Option",
                            "Manage Stores", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, storeOptions, storeOptions[0]);
                    if (storeMenu == 0) {
                        String newStore = JOptionPane.showInputDialog(null, "Enter the new Store's name", "Create Store", JOptionPane.PLAIN_MESSAGE);
                        ((Seller) loggedOnUser).addStore(new Store(newStore, (Seller) loggedOnUser));
                    } else if (storeMenu == 1) {
                        if (stores.size() < 1) {
                            JOptionPane.showMessageDialog(null, "No Stores Exist", "Warning: No Stores", JOptionPane.ERROR_MESSAGE);
                        } else {
                            String chooseStore = (String) JOptionPane.showInputDialog(null, "Choose a Store", "View Store", JOptionPane.PLAIN_MESSAGE, null, storeArray, storeArray[0]);
                            int chosenStore = 0;
                            for (int i = 0; i < stores.size();i++ ) {
                                if (stores.get(i).getStoreName().equals(chooseStore)) {
                                    chosenStore = i;
                                }
                            }
                            // Store not changing name's, check for this in final project
                            int editStore = JOptionPane.showOptionDialog(null, "Select an Option",
                                    stores.get(chosenStore).getStoreName(),JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.PLAIN_MESSAGE, null, editOptions, editOptions[0]);
                            if (editStore == 0) {
                                String newStoreName = JOptionPane.showInputDialog(null, "Enter The New Name", stores.get(chosenStore).getStoreName(), JOptionPane.PLAIN_MESSAGE);
                                stores.get(chosenStore).setStoreName(newStoreName);
                            } else if (editStore == 1) {
                                int deleteConfirm = JOptionPane.showOptionDialog(null,
                                        "Are you sure \n you want to delete this message?",
                                        stores.get(chosenStore).getStoreName(), JOptionPane.YES_NO_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                                if (deleteConfirm == 0) {
                                    ((Seller) loggedOnUser).removeStore(stores.get(chosenStore));
                                }
                            } else {
                                break;
                            }
                        }
                    } else if (storeMenu == 2) {
                        break;
                    }
                }
            } else if ((selectedOption == 0 && loggedOnUser instanceof Customer) || (selectedOption == 1 && loggedOnUser instanceof Seller)) {
                runConversationsMenu(scan, reader, writer, loggedOnUser);
            } else if ((selectedOption == 1 && loggedOnUser instanceof Customer) || (selectedOption == 2 && loggedOnUser instanceof Seller)) {
                runListMenu(scan, reader, writer, loggedOnUser);
            } else if ((selectedOption == 2 && loggedOnUser instanceof Customer) || (selectedOption == 3 && loggedOnUser instanceof Seller)) {
                runSearchMenu(reader, writer, loggedOnUser);
            } else if ((selectedOption == 3 && loggedOnUser instanceof Customer) || (selectedOption == 4 && loggedOnUser instanceof Seller)) {
                if (runAccountMenu(scan, reader, writer, loggedOnUser)) {
                    break;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Logging Out...");
                break;
            }
        }
    }

    private static void runConversationsMenu(Scanner scan, BufferedReader reader, PrintWriter writer, AccountMaster accountsMaster, User loggedOnUser) {
        ArrayList<Conversation> conversations = accountsMaster.listConversations(loggedOnUser);
        if (conversations.size() < 1) {
            JOptionPane.showMessageDialog(null, "You have no active conversations");
        } else {
            JFrame frame = new JFrame();

            ArrayList<Conversation> userConvo = accountsMaster.listConversations(loggedOnUser);
            Conversation[] conversationArray = new Conversation[userConvo.size()];
            String[] conversationName = new String[userConvo.size()];
            for (int i = 0; i < userConvo.size(); i++) {
                conversationArray[i] = userConvo.get(i);
                conversationName[i] = userConvo.get(i).getConversationID();
            }
            JList converseList = new JList(conversationName);

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);

            JPanel viewConversationList = new JPanel();
            viewConversationList.setLayout(new FlowLayout());
            viewConversationList.setAlignmentX(100);
            viewConversationList.setBorder(new EmptyBorder(new Insets(50, 300, 50, 300)));


            JScrollPane selectList = new JScrollPane(converseList);
            selectList.setBounds(200, 200, 200, 200);
            viewConversationList.add(selectList);
            JPanel panel = new JPanel(new GridLayout(1, 2));

            panel.add(okButton);
            panel.add(cancelButton);

            okButton.addActionListener(selectConversation -> {
                frame.dispose();
                int selectedIndex = converseList.getSelectedIndex();
                int secondIndexValueBecauseWhy = selectedIndex;
                if (secondIndexValueBecauseWhy < 0 || secondIndexValueBecauseWhy > userConvo.size() - 1) {
                    JOptionPane.showMessageDialog(null, "Please choose a conversation");
                } else {
                    Conversation selectedConvo = userConvo.get(secondIndexValueBecauseWhy);
                    System.out.println(secondIndexValueBecauseWhy);
                    String[] messageHistory = new String[selectedConvo.readFileAsPerUser(loggedOnUser).size()];
                    ArrayList<Message> messages = selectedConvo.readFileAsPerUser(loggedOnUser);
                    for (int i = 0; i < selectedConvo.readFileAsPerUser(loggedOnUser).size(); i++) {
                        messageHistory[i] = messages.get(i).getSender().getUsername() + ": " + messages.get(i).getMessage();
                    }
                    JFrame messageFrame = new JFrame();
                    messageFrame.setSize(400, 400);
                    messageFrame.setLocationRelativeTo(null);

                    JPanel messagePanel = new JPanel();
                    messagePanel.setLayout(new GridLayout(2, 9));


                    JList messageJList = new JList(messageHistory);
                    JScrollPane messageList = new JScrollPane(messageJList);
                    messageList.setBounds(1000, 1000, 1000, 1000);
                    messagePanel.add(messageList);

                    JButton editMessage = new JButton("Edit");
                    JButton deleteMessage = new JButton("Delete");
                    JButton sendMessage = new JButton("Send");
                    JButton cancelMessage = new JButton("Cancel");
                    JPanel flowMessage = new JPanel(new FlowLayout());
                    flowMessage.add(sendMessage);
                    flowMessage.add(editMessage);
                    flowMessage.add(deleteMessage);
                    flowMessage.add(cancelMessage);
                    messagePanel.add(flowMessage);

                    sendMessage.addActionListener(send -> {
                        String sending = JOptionPane.showInputDialog(null, "Enter a message",
                                "Send Message", JOptionPane.PLAIN_MESSAGE);
                        if (loggedOnUser instanceof Seller) {
                            loggedOnUser.sendMessageToUser(sending, selectedConvo.getCustomer(), accountsMaster);
                        }
                        if (loggedOnUser instanceof Customer) {
                            loggedOnUser.sendMessageToUser(sending, selectedConvo.getSeller(), accountsMaster);
                        }
                        String[] newMessageHistory = new String[selectedConvo.readFileAsPerUser(loggedOnUser).size()];
                        ArrayList<Message> addNewMessage = selectedConvo.readFileAsPerUser(loggedOnUser);
                        for (int i = 0; i < selectedConvo.readFileAsPerUser(loggedOnUser).size(); i++) {
                            newMessageHistory[i] = addNewMessage.get(i).getSender().getUsername() + ": " + addNewMessage.get(i).getMessage();
                        }
                        messageFrame.dispose();
                        messageBoard(scan, selectedConvo, newMessageHistory, addNewMessage, loggedOnUser, accountsMaster);

                    });

                    editMessage.addActionListener(edit -> {
                        String replacement = "";
                        try {
                            Message editingMessage = messages.get(messageJList.getSelectedIndex());
                            if (!editingMessage.getSender().equals(loggedOnUser)) {
                                JOptionPane.showMessageDialog(null, "You cannot edit this message");
                            } else {
                                replacement = JOptionPane.showInputDialog(null, "Enter your edited message", editingMessage.getMessage());
                                if (!(replacement == null)) {
                                    loggedOnUser.editMessage(messages.get(messageJList.getSelectedIndex()), selectedConvo, replacement);
                                }
                            }
                        } catch (IndexOutOfBoundsException exception) {
                            JOptionPane.showMessageDialog(null, "Please select a message");
                        }
                        messageFrame.repaint();
                    });

                    deleteMessage.addActionListener(delete -> {
                        try {
                            Message editingMessage = messages.get(messageJList.getSelectedIndex());
                            if (!editingMessage.getSender().equals(loggedOnUser)) {
                                JOptionPane.showMessageDialog(null, "You cannot delete this message");
                            } else {
                                int deleteOption = JOptionPane.showOptionDialog(null,
                                        "Are you sure you want to delete this message?",
                                        "Warning", JOptionPane.YES_NO_OPTION,
                                        JOptionPane.ERROR_MESSAGE, null, null, null);
                                if (deleteOption == 0) {
                                    loggedOnUser.deleteMessage(messages.get(messageJList.getSelectedIndex()), selectedConvo);
                                }
                            }
                        } catch (IndexOutOfBoundsException exception) {
                            JOptionPane.showMessageDialog(null, "Please select a message");
                        }
                        messageFrame.repaint();
                    });

                    cancelMessage.addActionListener(cancel -> {
                        messageFrame.remove(messageFrame);
                        messageFrame.dispose();
                        runConversationsMenu(scan, reader, writer, accountsMaster, loggedOnUser);
                    });
                    messageFrame.add(messagePanel);
                    messageFrame.setVisible(true);
                }
            });

            cancelButton.addActionListener(close -> {
                frame.dispose();
                //runMainMenu(scan, accountsMaster, loggedOnUser);
            });


            viewConversationList.add(panel);

            frame.add(viewConversationList);
            frame.setVisible(true);
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
                            JOptionPane.showMessageDialog(null, "Invalid Input. No Such Message Exists");
                            break;
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Invalid Input");
                    }

                    if (action == 5) {
                        if (!recipient.getBlockedUsers().contains(loggedOnUser) &&
                                !loggedOnUser.getBlockedUsers().contains(recipient)) {
                            String updatedMessage = JOptionPane.showInputDialog(null, "Updated Message: ");

                            while (updatedMessage != null && updatedMessage.equals("")) {
                                JOptionPane.showMessageDialog(null, "Message Cannot Be Blank!");
                                updatedMessage = JOptionPane.showInputDialog(null, "Updated Message: ");
                            }

                            if (updatedMessage != null) {
                                if (loggedOnUser.editMessage(reader, writer,
                                        messages.get(messageID),
                                        conversation,
                                        updatedMessage)) {
                                    JOptionPane.showMessageDialog(null, "Edited Message");
                                } else {
                                    JOptionPane.showMessageDialog(null, "You Cannot Edit this Message");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry! You cannot message this user.");
                        }
                    } else if (action == 6) {
                        if (loggedOnUser.deleteMessage(reader, writer, messages.get(messageID), conversation)) {
                            JOptionPane.showMessageDialog(null, "Deleted Message");
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry! Could not delete this message");
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
                            String txtPath = JOptionPane.showInputDialog(null, "Enter path to .txt file:");

                            if (conversation.importTXT(reader, writer, txtPath, loggedOnUser, recipient)) {
                                JOptionPane.showMessageDialog(null, "Message Sent Successfully!");
                            } else {
                                JOptionPane.showMessageDialog(null, "Error: Could not send message");
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry! You cannot message this user.");
                        }
                    } else if (action == 3) {
                        if (!recipient.getBlockedUsers().contains(loggedOnUser) &&
                                !loggedOnUser.getBlockedUsers().contains(recipient)) {
                            String message = JOptionPane.showInputDialog(null, "Your Message: ");

                            while (message != null && message.equals("")) {
                                JOptionPane.showMessageDialog(null, "Message Cannot Be Blank!");
                                message = JOptionPane.showInputDialog(null, "Your Message: ");
                            }

                            if (message != null) {
                                if (loggedOnUser.sendMessageToUser(reader, writer, message, recipient)) {
                                    JOptionPane.showMessageDialog(null, "Sent!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Message Failed!");
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Sorry! You cannot message this user.");
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

    private static void runListMenu(Scanner scan, BufferedReader reader, PrintWriter writer, AccountMaster accountsMaster, User loggedOnUser) {
        ArrayList<User> userSearchList = new ArrayList<User>();
        ArrayList<Store> storeSearchList = new ArrayList<Store>();
        JFrame frame = new JFrame();
        JList nameList = new JList();


        if (loggedOnUser instanceof Customer) {
            ArrayList <Store> storeListing = accountsMaster.listStores((Customer) loggedOnUser);
            for (int i = 0; i < storeListing.size(); i++){
                if (storeListing.get(i).getSeller().getInvisibleUsers().contains(loggedOnUser)) {
                    continue;
                }
                storeSearchList.add(storeListing.get(i));
            }
            Store[] storeArray = new Store[storeSearchList.size()];
            String[] storeStringArray = new String[storeSearchList.size()];
            for (int i = 0; i < storeSearchList.size(); i++) {
                storeArray[i] = storeSearchList.get(i);
                storeStringArray[i] = storeSearchList.get(i).getStoreName();
            }
            frame.setTitle("Store List");
            nameList = new JList(storeStringArray);
        }
        if (loggedOnUser instanceof Seller) {
            ArrayList <Customer> customerListing = accountsMaster.listCustomers((Seller) loggedOnUser);
            for (int i = 0; i < customerListing.size(); i++){
                if (customerListing.get(i).getInvisibleUsers().contains(loggedOnUser)) {
                    continue;
                }
                userSearchList.add(customerListing.get(i));
            }
            frame.setTitle("Customer List");
            User[] userArray = new User[userSearchList.size()];
            String[] userStringArray = new String[userSearchList.size()];
            for (int i = 0; i < userArray.length; i++) {
                userArray[i] = userSearchList.get(i);
                userStringArray[i] = userSearchList.get(i).getUsername();
            }
            frame.setTitle("Store List");
            nameList = new JList(userStringArray);
        }
        frame.setSize(600,400);
        frame.setLocationRelativeTo(null);

        JPanel viewUserList = new JPanel();
        viewUserList.setLayout(new FlowLayout());
        viewUserList.setAlignmentX(100);
        viewUserList.setBorder(new EmptyBorder(new Insets(50, 300, 50, 300)));


        JScrollPane selectList = new JScrollPane(nameList);
        selectList.setSize(200,300);
        viewUserList.add(selectList);
        JPanel panel = new JPanel(new GridLayout(1,2));

        panel.add(okButton);
        panel.add(cancelButton);
        okButton.putClientProperty("This logged in user", loggedOnUser);
        cancelButton.putClientProperty("This logged in user cancel", loggedOnUser);
        JList secNameList = nameList;

        okButton.addActionListener(e->{
            try {
                int index = secNameList.getSelectedIndex();
                frame.dispose();

                User tempSelectedUser = null;
                if (loggedOnUser instanceof Seller) {
                    tempSelectedUser = userSearchList.get(index);
                }
                if (loggedOnUser instanceof Customer) {
                    tempSelectedUser = storeSearchList.get(index).getSeller();
                }
                User selectedUser = tempSelectedUser;
                JFrame optionFrame = new JFrame();
                optionFrame.setSize(400, 400);
                optionFrame.setLocationRelativeTo(null);

                JPanel viewUserOp = new JPanel();
                viewUserOp.setLayout(new BoxLayout(viewUserOp, BoxLayout.PAGE_AXIS));
                viewUserOp.setBorder(new EmptyBorder(new Insets(100, 150, 100, 150)));

                JButton messageUser = new JButton("Message");
                JButton blockUser = new JButton("Block");
                JButton invisibleUser = new JButton("Be Invisible");
                invisibleUser.setSize(300,100);
                JButton goBack = new JButton("Go Back");

                messageUser.addActionListener(message -> {
                    String newMessage = JOptionPane.showInputDialog(null, "Enter a message");
                    loggedOnUser.sendMessageToUser(reader, writer, newMessage, loggedOnUser);
                });

                blockUser.addActionListener(blocking -> {
                    int confirm = JOptionPane.showOptionDialog(null,
                            "Are you sure you want to block this user?", "Blocking",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, null, null);
                    if (confirm == 0) {
                        loggedOnUser.addBlockedUser(selectedUser);
                    }
                });

                invisibleUser.addActionListener(invisibility -> {
                    int confirm = JOptionPane.showOptionDialog(null,
                            "Are you sure you want to become invisible to this user?", "Invisibility",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, null, null);
                    if (confirm == 0) {
                        loggedOnUser.addInvisibleUser(selectedUser);
                    }
                });

                goBack.addActionListener(goToPrev -> {
                    optionFrame.dispose();
                    runListMenu(scan, reader, writer, accountsMaster, loggedOnUser);
                });
                viewUserOp.add(messageUser);
                viewUserOp.add(blockUser);
                viewUserOp.add(invisibleUser);
                viewUserOp.add(goBack);

                viewUserOp.setAlignmentX(Component.CENTER_ALIGNMENT);

                optionFrame.add(viewUserOp);
                optionFrame.setVisible(true);
            } catch (IndexOutOfBoundsException a) {
                JOptionPane.showMessageDialog(null, "No option was selected");
                runListMenu(scan, reader, writer, accountsMaster, loggedOnUser);
            }
        });
        cancelButton.addActionListener(e -> {
            frame.dispose();
            runMainMenu(scan, accountsMaster, loggedOnUser);
        });
        viewUserList.add(panel);
        frame.add(viewUserList);
        frame.setVisible(true);
    }

    /*
     * private static void runSearchMenu(BufferedReader reader, PrintWriter writer, User loggedOnUser)
     * Method that lets user search for a username or email
     * and lists corresponding users of the opposite role.
     */
    private static void runSearchMenu(BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        String searchKeyword = JOptionPane.showInputDialog(null, "Search by Username or Email (full or part):");

        while (searchKeyword != null && searchKeyword.equals("")) {
            JOptionPane.showMessageDialog(null, "Search Keyword Cannot Be Blank!");
            searchKeyword = JOptionPane.showInputDialog(null, "Search by Username or Email (full or part):");
        }

        if (loggedOnUser instanceof Seller) {
            while (true) {
                ArrayList<Customer> customers = refreshSearchCustomers(reader, writer, loggedOnUser, searchKeyword);
                String[] options = new String[customers.size()];
                String selectedOption = "";

                if (customers.size() > 0) {
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

                        options[i] = String.format("%d. %s | %s%s\n", i + 1, customers.get(i).getUsername(),
                                customers.get(i).getEmail(), (extraInformation.length() > 0) ? " | Currently: " +
                                        extraInformation : "");
                    }
                    // options[customers.size()] = String.format("%d. Back to Main Menu", customers.size() + 1);

                    selectedOption = (String) JOptionPane.showInputDialog(null, "Select a customer:",
                            "Search " +
                                    "Results",
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                } else {
                    // options = new String[]{"Back to Main Menu"};
                    if (searchKeyword != null) {
                        JOptionPane.showMessageDialog(null, "[No Matching Search Results]", "Search Results",
                                JOptionPane.INFORMATION_MESSAGE);
                        /*
                        JOptionPane.showOptionDialog(null, "[No Matching Search Results]", "Search Results",
                                JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Back " +
                                        "to Main Menu"}, null);
                         */
                        /*
                        selectedOption = (String) JOptionPane.showInputDialog(null, "[No Matching Search Results]",
                                "Search Results",
                                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                         */
                    }
                }

                int customerNumber = customers.size() + 1;
                for (int i = 0; i < customers.size(); i++) {
                    if (selectedOption != null && selectedOption.equals(options[i])) {
                        customerNumber = i + 1;
                    }
                }

                if (customerNumber == customers.size() + 1) {
                    break;
                } else { // if (customerNumber <= customers.size())
                    Customer selectedCustomer = customers.get(customerNumber - 1);
                    while (true) {
                        int userChoice = printUserActionMenu(loggedOnUser, selectedCustomer);

                        customers = refreshSearchCustomers(reader, writer, loggedOnUser, searchKeyword);
                        for (Customer customer : customers) {
                            if (customer.equals(selectedCustomer)) {
                                selectedCustomer = customer;
                            }
                        }

                        if (userChoice == 0) {
                            if (!selectedCustomer.getBlockedUsers().contains(loggedOnUser) &&
                                    !loggedOnUser.getBlockedUsers().contains(selectedCustomer)) {
                                String message = JOptionPane.showInputDialog(null, "Your Message: ");

                                while (message != null && message.equals("")) {
                                    JOptionPane.showMessageDialog(null, "Message Cannot Be Blank!");
                                    message = JOptionPane.showInputDialog(null, "Your Message: ");
                                }

                                if (message != null) {
                                    if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedCustomer)) {
                                        JOptionPane.showMessageDialog(null, "Sent!");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Message Failed!");
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Sorry! You cannot message this user.");
                            }
                        } else if (userChoice == 1) {
                            ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                            if (blockedUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeBlockedUser(writer, selectedCustomer);
                                JOptionPane.showMessageDialog(null, "Unblocked Customer");
                            } else {
                                loggedOnUser.addBlockedUser(writer, selectedCustomer);
                                JOptionPane.showMessageDialog(null, "Blocked Customer");
                            }
                            break;
                        } else if (userChoice == 2) {
                            ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                            if (invisibleUsers.contains(selectedCustomer)) {
                                loggedOnUser.removeInvisibleUser(writer, selectedCustomer);
                                JOptionPane.showMessageDialog(null, "Now Visible to Customer");
                            } else {
                                loggedOnUser.addInvisibleUser(writer, selectedCustomer);
                                JOptionPane.showMessageDialog(null, "Now Invisible to Customer");
                            }
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        } else {
            while (true) {
                ArrayList<Seller> sellers = refreshSearchSellers(reader, writer, loggedOnUser, searchKeyword);
                String[] options = new String[sellers.size()];
                String selectedOption = "";

                if (sellers.size() > 0) {
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

                        options[i] = String.format("%d. %s | %s%s\n", i + 1, sellers.get(i).getUsername(), sellers.get(i).getEmail(),
                                (extraInformation.length() > 0) ? " | Currently:" + extraInformation : "");
                    }
                    // options[customers.size()] = String.format("%d. Back to Main Menu", customers.size() + 1);

                    selectedOption = (String) JOptionPane.showInputDialog(null, "Select a seller:",
                            "Search " +
                                    "Results",
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                } else {
                    options = new String[]{"1. Back to Main Menu"};
                    if (searchKeyword != null) {
                        JOptionPane.showMessageDialog(null, "[No Matching Search Results]", "Search Results",
                                JOptionPane.INFORMATION_MESSAGE);
                        /*
                        JOptionPane.showOptionDialog(null, "[No Matching Search Results]", "Search Results",
                                JOptionPane.OK_OPTION, JOptionPane.INFORMATION_MESSAGE, null, new String[]{"Back " +
                                        "to Main Menu"}, null);
                         */
                        /*
                        selectedOption = (String) JOptionPane.showInputDialog(null, "[No Matching Search Results]",
                                "Search Results",
                                JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
                         */
                    }
                }

                int sellerNumber = sellers.size() + 1;
                for (int i = 0; i < sellers.size(); i++) {
                    if (selectedOption != null && selectedOption.equals(options[i])) {
                        sellerNumber = i + 1;
                    }
                }
                if (sellerNumber == sellers.size() + 1) {
                    break;
                } else { // if (sellerNumber <= sellers.size())
                    Seller selectedSeller = sellers.get(sellerNumber - 1);
                    while (true) {
                        int userChoice = printUserActionMenu(loggedOnUser, selectedSeller);

                        sellers = refreshSearchSellers(reader, writer, loggedOnUser, searchKeyword);
                        for (Seller seller : sellers) {
                            if (seller.equals(selectedSeller)) {
                                selectedSeller = seller;
                            }
                        }

                        if (userChoice == 0) {
                            if (!selectedSeller.getBlockedUsers().contains(loggedOnUser) &&
                                    !loggedOnUser.getBlockedUsers().contains(selectedSeller)) {
                                String message = JOptionPane.showInputDialog(null, "Your Message: ");

                                while (message != null && message.equals("")) {
                                    JOptionPane.showMessageDialog(null, "Message Cannot Be Blank!");
                                    message = JOptionPane.showInputDialog(null, "Your Message: ");
                                }

                                if (message != null) {
                                    if (loggedOnUser.sendMessageToUser(reader, writer, message, selectedSeller)) {
                                        JOptionPane.showMessageDialog(null, "Sent!");
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Message Failed!");
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(null, "Sorry! You cannot message this user.");
                            }
                        } else if (userChoice == 1) {
                            ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                            if (blockedUsers.contains(selectedSeller)) {
                                loggedOnUser.removeBlockedUser(writer, selectedSeller);
                                JOptionPane.showMessageDialog(null, "Unblocked Seller");
                            } else {
                                loggedOnUser.addBlockedUser(writer, selectedSeller);
                                JOptionPane.showMessageDialog(null, "Blocked Seller");
                            }
                            break;
                        } else if (userChoice == 2) {
                            ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                            if (invisibleUsers.contains(selectedSeller)) {
                                loggedOnUser.removeInvisibleUser(writer, selectedSeller);
                                JOptionPane.showMessageDialog(null, "Now Visible to Seller");
                            } else {
                                loggedOnUser.addInvisibleUser(writer, selectedSeller);
                                JOptionPane.showMessageDialog(null, "Now Invisible to Seller");
                            }
                            break;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
    }

    // TODO: showOptionDialog --> showInputDialog. Too many buttons on one line
    private static int printEditAccountMenu() {
        String[] options = {"1. Change Username", "2. Change Password", "3. Manage Blocked Users", "4. Manage " +
                "Invisible to Users", "5. Edit Censoring", "6. Delete Account", "7. Back to Main Menu"};
        int selectedOption = JOptionPane.showOptionDialog(null, "Edit your account:", "Account Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        return selectedOption;
    }

    private static void printCensoredWords(ArrayList<String> censoredWordPairs) {
        for (int i = 0; i < censoredWordPairs.size(); i++) {
            System.out.printf("%d. %s\n", i + 1, censoredWordPairs.get(i).replace(":", " >>> "));
        }
    }

    private static int printUserActionMenu(User user, User selectedUser) {
        String keyword = (user instanceof Seller) ? "Customer" : "Seller";
        String[] options = {String.format("1. Message %s", keyword), String.format("2. %s %s", (user.getBlockedUsers().contains(selectedUser)) ?
                "Unblock" : "Block", keyword), String.format("3. Become %s to %s", (user.getInvisibleUsers().contains(selectedUser)) ?
                "Visible" : "Invisible", keyword), String.format("4. Back to %s List", keyword)};
        int selectedOption = JOptionPane.showOptionDialog(null, String.format("Selected: %s",
                        selectedUser.getUsername()), "User " +
                "Action Menu", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                options[0]);
        return selectedOption;
    }

    private static boolean runAccountMenu(Scanner scan, BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        boolean deleteUser = false;
        while (true) {
            int accountOption = printEditAccountMenu();
            if (accountOption == 0) {
                while (true) {
                    String username = JOptionPane.showInputDialog(null, "Enter new username:");

                    while (username != null && username.equals("")) {
                        JOptionPane.showMessageDialog(null, "Username Cannot Be Blank!");
                        username = JOptionPane.showInputDialog(null, "Enter new username:");
                    }

                    if (username != null) {
                        username = username.replaceAll("[,<>;]", "_");
                    }

                    String checkUsernameRequest = "[CHECK.USERNAME]" + username;
                    sendRequest(writer, checkUsernameRequest);
                    boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                    if (username == null) { // User goes back to Account Menu
                        break;
                    } else if (isUsernameTaken) {
                        int selectedOption = JOptionPane.showConfirmDialog(null, "Sorry! That username is already " +
                                "taken.\nTry again?", "Duplicate Username", JOptionPane.YES_NO_OPTION);

                        boolean tryAgain = selectedOption == JOptionPane.YES_OPTION;
                        if (!tryAgain) { // User goes back to Account Menu
                            break;
                        }
                    } else {
                        loggedOnUser.setUsername(reader, writer, username);
                        JOptionPane.showMessageDialog(null, "Username Changed!");
                        break;
                    }
                }
            } else if (accountOption == 1) {
                String currentPassword = JOptionPane.showInputDialog(null, "Enter current password:");

                if (currentPassword != null) { // Else, return to Account Menu
                    if (currentPassword.equals(loggedOnUser.getPassword())) {
                        String newPassword = JOptionPane.showInputDialog(null, "Enter new password:");

                        while (newPassword != null && newPassword.equals("")) {
                            JOptionPane.showMessageDialog(null, "Password Cannot Be Blank!");
                            newPassword = JOptionPane.showInputDialog(null, "Enter new password:");
                        }

                        if (newPassword != null) { // User did not cancel or exit
                            String confirmPassword = JOptionPane.showInputDialog(null, "Confirm new password:");

                            if (confirmPassword != null) { // User did not cancel or exit
                                if (newPassword.equals(confirmPassword)) {
                                    loggedOnUser.setPassword(reader, writer, confirmPassword);
                                    JOptionPane.showMessageDialog(null, "Password Changed!");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Did not match!");
                                }
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid Password");
                    }
                }
            } else if (accountOption == 2) {
                ArrayList<User> blockedUsers = loggedOnUser.getBlockedUsers();
                while (true) {
                    String selectedOption = "";
                    String[] options = new String[blockedUsers.size()];
                    if (blockedUsers.size() > 0) {
                        for (int i = 0; i < blockedUsers.size(); i++) {
                            options[i] = String.format("[%d] %s | %s\n", i + 1, blockedUsers.get(i).getUsername(),
                                    blockedUsers.get(i).getEmail());
                        }

                        selectedOption = (String) JOptionPane.showInputDialog(null, "Select a user to unblock:",
                                "Blocked Users",
                                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    } else {
                        JOptionPane.showMessageDialog(null, "[No Blocked Users]", "Blocked Users",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    int blockedOption = blockedUsers.size() + 1;
                    for (int i = 0; i < blockedUsers.size(); i++) {
                        if (selectedOption != null && selectedOption.equals(options[i])) {
                            blockedOption = i + 1;
                        }
                    }
                    if (blockedOption == blockedUsers.size() + 1) {
                        break;
                    } else {
                        loggedOnUser.removeBlockedUser(writer, blockedUsers.get(blockedOption - 1));
                        JOptionPane.showMessageDialog(null, "Unblocked User");
                    }
                }
            } else if (accountOption == 3) {
                ArrayList<User> invisibleUsers = loggedOnUser.getInvisibleUsers();
                while (true) {
                    String selectedOption = "";
                    String[] options = new String[invisibleUsers.size()];

                    if (invisibleUsers.size() > 0) {
                        for (int i = 0; i < invisibleUsers.size(); i++) {
                            options[i] = String.format("[%d] %s | %s\n", i + 1, invisibleUsers.get(i).getUsername(),
                                    invisibleUsers.get(i).getEmail());
                        }

                        selectedOption = (String) JOptionPane.showInputDialog(null, "Select a user to become visible " +
                                        "to:",
                                "Invisible to Users",
                                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
                    } else {
                        JOptionPane.showMessageDialog(null, "[No Invisible to Users]", "Invisible to Users",
                                JOptionPane.INFORMATION_MESSAGE);
                    }

                    int visibleOption = invisibleUsers.size() + 1;
                    for (int i = 0; i < invisibleUsers.size(); i++) {
                        if (selectedOption != null && selectedOption.equals(options[i])) {
                            visibleOption = i + 1;
                        }
                    }
                    if (visibleOption == invisibleUsers.size() + 1) {
                        break;
                    } else {
                        loggedOnUser.removeInvisibleUser(writer, invisibleUsers.get(visibleOption - 1));
                        JOptionPane.showMessageDialog(null, "Now Visible to User");
                    }
                }
            } else if (accountOption == 4) {
                // TODO: Edit Censoring
                ArrayList<String> censoredWords = loggedOnUser.getCensoredWords();
                while (true) {
                    System.out.println("----Censor Pairs----");
                    if (censoredWords.size() > 0) {
                        System.out.println("[To Delete, Enter Censor Pair No.]");
                        printCensoredWords(censoredWords);
                    } else {
                        JOptionPane.showMessageDialog(null, "[No Censor Pairs]", "Censor Pairs", JOptionPane.INFORMATION_MESSAGE);
                    }
                    System.out.printf("%d. Add Censor Pair\n", censoredWords.size() + 1);
                    System.out.printf("%d. Toggle Censoring (ON/OFF) | Currently: %s\n", censoredWords.size() + 2,
                            (loggedOnUser.isRequestsCensorship()) ? "ON" : "OFF");
                    System.out.printf("%d. Back to Account Menu\n", censoredWords.size() + 3);
                    int censorOption;
                    while (true) {
                            censorOption = Integer.parseInt(scan.nextLine());
                            if (censorOption <= 0 || censorOption > censoredWords.size() + 3) {
                                System.out.println("Invalid Option");
                            } else {
                                break;
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
                            JOptionPane.showMessageDialog(null, "You Cannot have Special Characters in Censoring!");
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
            } else if (accountOption == 5) {
                int selectedOption = JOptionPane.showConfirmDialog(null, "Are you sure?",
                        "Account " +
                                "Deletion Confirmation",
                        JOptionPane.YES_NO_OPTION);
                deleteUser = selectedOption == JOptionPane.YES_OPTION;
                if (deleteUser) {
                    String request = "[DELETE]" + ((loggedOnUser instanceof Seller) ?
                            ((Seller) loggedOnUser).detailedToString() : ((Customer) loggedOnUser).detailedToString());
                    sendRequest(writer, request);
                    break;
                }
            } else {
                break;
            }
        }
        return deleteUser;
    }

    // TODO: Make sure all titles are consistent with content (e.g., "Account Deletion Confirmation).
    private static int printMainMenu(User user) {
        String[] options;
        // TODO: Change/remove numbers?
        if (user instanceof Seller) {
            options = new String[]{"1. Manage Stores", "2. View Conversations", "3. View All Customers", "4. Search " +
                    "Customers", "5. Edit Account", "6. Log Out"};
        } else {
            options = new String[]{"1. View Conversations", "2. View All Stores", "3. Search " +
                    "Sellers", "4. Edit Account", "5. Log Out"};
        }

        int selectedOption = JOptionPane.showOptionDialog(null, "Select an action:", "Main Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options,
                options[0]);
        return selectedOption;
    }
}
