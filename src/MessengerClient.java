import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MessengerClient {

    private JFrame jFrame;

    private Container container;

    private CardLayout cardLayout = new CardLayout();

    private User loggedOnUser = null;
    /**
     * The constant password/account file path: passwords.txt
     */
    public static final String PASSWORD_FILE_PATH = "passwords.txt";

    /**
     * The constant conversations file path: conversations.txt
     */
    public static final String CONVERSATIONS_FILE_PATH = "conversations.txt";

    public MessengerClient() {
        jFrame = new JFrame("Messenger");
        jFrame.setSize(400, 200);
        jFrame.setLocationRelativeTo(null);
        container = jFrame.getContentPane();
        container.setLayout(cardLayout);
        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

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
    public static boolean convertConversationsToCSV(BufferedReader reader, PrintWriter writer, ArrayList<Conversation> exportingConversations, User loggedOnUser) throws IOException {

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
    public static ArrayList<Conversation> refreshVisibleConversations(BufferedReader reader, PrintWriter writer, User loggedOnUser) {
        ArrayList<Conversation> conversations = new ArrayList<>();

        String userString = (loggedOnUser instanceof Seller) ? ((Seller) loggedOnUser).detailedToString() : ((Customer) loggedOnUser).detailedToString();
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
    public static ArrayList<Message> refreshVisibleMessages(BufferedReader reader, PrintWriter writer, Conversation conversation, User loggedOnUser) {
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
    public static ArrayList<Customer> refreshSearchCustomers(BufferedReader reader, PrintWriter writer, User loggedOnUser, String searchKeyword) {
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
    public static ArrayList<Seller> refreshSearchSellers(BufferedReader reader, PrintWriter writer, User loggedOnUser, String searchKeyword) {
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

    public static void exitApplication(MessengerClient messengerClient, PrintWriter writer) {
        sendRequest(writer, "[LOGOUT]");

        JPanel holder = new JPanel(new GridLayout(3, 1));
        holder.add(new JPanel());

        JPanel titlePanel = new JPanel();
        JLabel title = new JLabel("Thank you for using Messenger! Goodbye!");
        title.setFont(new Font("Open Sans", Font.PLAIN, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);
        titlePanel.add(title);
        holder.add(titlePanel);

        holder.add(new JPanel());
        messengerClient.container.add("Goodbye", holder);
        messengerClient.cardLayout.show(messengerClient.container, "Goodbye");
    }

    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 8080);
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MessengerClient messengerClient = new MessengerClient();

            JPanel holder = new JPanel(new GridLayout(4, 1));
            holder.add(new JPanel());

            JPanel titlePanel = new JPanel();
            JLabel title = new JLabel("Welcome to Messenger!");
            title.setFont(new Font("Open Sans", Font.PLAIN, 16));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setVerticalAlignment(SwingConstants.CENTER);
            titlePanel.add(title);
            holder.add(titlePanel);

            holder.add(new JPanel());

            JPanel buttonPanel = new JPanel();
            JButton signUpButton = new JButton("Sign Up");
            signUpButton.setPreferredSize(new Dimension(150, 30));
            signUpButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Create Account");
                    messengerClient.jFrame.setSize(400, 250);
                    JPanel holder = new JPanel(new GridLayout(5, 1));

                    JPanel usernamePanel = new JPanel();
                    JLabel usernamePrompt = new JLabel("Create a Username:");
                    usernamePrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    usernamePanel.add(usernamePrompt);
                    JTextField usernameTextField = new JTextField(20);
                    usernamePanel.add(usernameTextField);
                    holder.add(usernamePanel);

                    JPanel emailPanel = new JPanel();
                    JLabel emailPrompt = new JLabel("Register an Email ID:");
                    emailPrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    emailPanel.add(emailPrompt);
                    JTextField emailTextField = new JTextField(20);
                    emailPanel.add(emailTextField);
                    holder.add(emailPanel);

                    JPanel passwordPanel = new JPanel();
                    JLabel passwordPrompt = new JLabel("Set a Password:");
                    passwordPrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    passwordPanel.add(passwordPrompt);
                    JTextField passwordTextField = new JTextField(20);
                    passwordPanel.add(passwordTextField);
                    holder.add(passwordPanel);

                    JPanel rolePanel = new JPanel();
                    JLabel rolePrompt = new JLabel("Choose a Role:");
                    rolePrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    rolePanel.add(rolePrompt);

                    ButtonGroup buttonGroup = new ButtonGroup();
                    JRadioButton sellerOption = new JRadioButton("Seller");
                    sellerOption.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    buttonGroup.add(sellerOption);
                    JRadioButton customerOption = new JRadioButton("Customer");
                    customerOption.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    buttonGroup.add(customerOption);

                    rolePanel.add(sellerOption);
                    rolePanel.add(customerOption);
                    holder.add(rolePanel);

                    JPanel buttonPanel = new JPanel();
                    JButton createButton = new JButton("Create");
                    createButton.setPreferredSize(new Dimension(150, 30));
                    createButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String username = usernameTextField.getText();
                            String email = emailTextField.getText();
                            String password = passwordTextField.getText();

                            if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                                    (!sellerOption.isSelected() && !customerOption.isSelected())) {
                                JOptionPane.showMessageDialog(null,
                                        "Cannot Submit Incomplete Form", "Messenger",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                String role = (sellerOption.isSelected()) ? "Seller" : "Customer";
                                String checkUsernameRequest = "[CHECK.USERNAME]" + usernameTextField.getText();
                                sendRequest(writer, checkUsernameRequest);
                                boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                                String checkEmailRequest = "[CHECK.EMAIL]" + emailTextField.getText();
                                sendRequest(writer, checkEmailRequest);
                                boolean isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

                                if (!(isUsernameTaken || isEmailRegistered)) {
                                    String createAccountRequest = String.format("[CREATE.USER]%s, %s, %s, %s",
                                            username, email, password, role);
                                    sendRequest(writer, createAccountRequest);
                                    String response = readResponse(reader);

                                    if (response != null && response.substring(0, response.indexOf("<"))
                                            .equalsIgnoreCase("Seller")) {
                                        messengerClient.loggedOnUser = new Seller(response, true,
                                                true);
                                    } else {
                                        messengerClient.loggedOnUser = new Customer(response, true);
                                    }

                                    JPanel holder = new JPanel(new GridLayout(4, 1));
                                    holder.add(new JPanel());

                                    JPanel usernamePanel = new JPanel();
                                    JLabel usernamePrompt = new JLabel("You're All Set!");
                                    usernamePrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                    usernamePanel.add(usernamePrompt);
                                    holder.add(usernamePanel);

                                    holder.add(new JPanel());

                                    showMainMenu(messengerClient, reader, writer);
                                } else if (isUsernameTaken) {
                                    JOptionPane.showMessageDialog(null,
                                            "Username already Taken", "Messenger",
                                            JOptionPane.ERROR_MESSAGE);
                                    usernameTextField.setText("");
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Email already Registered", "Messenger",
                                            JOptionPane.ERROR_MESSAGE);
                                    emailTextField.setText("");
                                }
                            }
                        }
                    });

                    JButton backButton = new JButton("Back");
                    backButton.setPreferredSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.cardLayout.show(messengerClient.container, "Onboard");
                        }
                    });

                    buttonPanel.add(backButton);
                    buttonPanel.add(createButton);
                    holder.add(buttonPanel);
                    messengerClient.container.add("Sign-up", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "Sign-up");
                }
            });

            JButton logInButton = new JButton("Log In");
            logInButton.setPreferredSize(new Dimension(150, 30));
            logInButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Log In");
                    messengerClient.jFrame.setSize(400, 250);
                    JPanel holder = new JPanel(new GridLayout(4, 1));
                    holder.add(new JPanel());

                    JPanel usernamePanel = new JPanel();
                    JLabel usernamePrompt = new JLabel("Username or Email ID:");
                    usernamePrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    usernamePanel.add(usernamePrompt);
                    JTextField usernameTextField = new JTextField(20);
                    usernamePanel.add(usernameTextField);
                    holder.add(usernamePanel);

                    JPanel passwordPanel = new JPanel();
                    JLabel passwordPrompt = new JLabel("Password:");
                    passwordPrompt.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    passwordPanel.add(passwordPrompt);
                    JTextField passwordTextField = new JTextField(20);
                    passwordPanel.add(passwordTextField);
                    holder.add(passwordPanel);

                    JPanel buttonPanel = new JPanel();
                    JButton logInButton = new JButton("Log In");
                    logInButton.setPreferredSize(new Dimension(150, 30));
                    logInButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String username = usernameTextField.getText();
                            String password = passwordTextField.getText();

                            if (username.isEmpty() || password.isEmpty()) {
                                JOptionPane.showMessageDialog(null,
                                        "Cannot Submit Incomplete Form", "Messenger",
                                        JOptionPane.ERROR_MESSAGE);
                            } else {
                                String fetchAccountRequest = "[FETCH.USER]" + username;
                                sendRequest(writer, fetchAccountRequest);
                                String fetchResponse = readResponse(reader);

                                if (fetchResponse != null && fetchResponse.substring(0, fetchResponse.indexOf("<"))
                                        .equalsIgnoreCase("Seller")) {
                                    messengerClient.loggedOnUser = new Seller(fetchResponse, true, true);
                                } else {
                                    messengerClient.loggedOnUser = new Customer(fetchResponse, true);
                                }

                                if (messengerClient.loggedOnUser.getPassword().equals(password)) {
                                    JOptionPane.showMessageDialog(null, String.format("Welcome back %s!",
                                            messengerClient.loggedOnUser.getUsername().toUpperCase()));

                                    String fetchNumUnreadRequest;
                                    if (messengerClient.loggedOnUser instanceof Seller) {
                                        fetchNumUnreadRequest = "[FETCH.UNREAD]" +
                                                ((Seller) messengerClient.loggedOnUser).detailedToString();
                                    } else {
                                        fetchNumUnreadRequest = "[FETCH.UNREAD]" +
                                                ((Customer) messengerClient.loggedOnUser).detailedToString();
                                    }
                                    sendRequest(writer, fetchNumUnreadRequest);
                                    fetchResponse = readResponse(reader);

                                    if (fetchResponse != null && Integer.parseInt(fetchResponse) > 0) {
                                        JOptionPane.showMessageDialog(null,
                                                String.format("You have %d new unread conversation%s",
                                                        Integer.parseInt(fetchResponse),
                                                        (Integer.parseInt(fetchResponse) != 1) ? "s" : ""));
                                    }
                                    showMainMenu(messengerClient, reader, writer);
                                } else {
                                    JOptionPane.showMessageDialog(null,
                                            "Incorrect Password", "Messenger",
                                            JOptionPane.ERROR_MESSAGE);
                                    exitApplication(messengerClient, writer);
                                }
                            }
                        }
                    });

                    JButton backButton = new JButton("Back");
                    backButton.setPreferredSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.cardLayout.show(messengerClient.container, "Onboard");
                        }
                    });

                    buttonPanel.add(backButton);
                    buttonPanel.add(logInButton);
                    holder.add(buttonPanel);
                    messengerClient.container.add("Sign-in", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "Sign-in");
                }
            });

            buttonPanel.add(signUpButton);
            buttonPanel.add(logInButton);
            holder.add(buttonPanel);

            messengerClient.container.add("Onboard", holder);
            messengerClient.cardLayout.show(messengerClient.container, "Onboard");
            messengerClient.jFrame.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void showMainMenu(MessengerClient messengerClient, BufferedReader reader, PrintWriter writer) {
        messengerClient.jFrame.setTitle("Messenger: Main");
        JPanel holder = new JPanel(new GridLayout(0, 3));
        holder.add(new JPanel());

        if (messengerClient.loggedOnUser instanceof Seller) {
            String[] options = {"Manage Stores", "View Conversations", "View All Customers", "Search Customers",
                    "Edit Account", "Log Out"};
            JList list = new JList(options);
            list.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int selectedOption = list.getSelectedIndex();
                    if (selectedOption == 0) {
                        JPanel storeMenu = new JPanel();
                        messengerClient.container.add(storeMenu, "StoreMenu");
                        storeMenu.setLayout(new CardLayout());
                        CardLayout card = (CardLayout) storeMenu.getLayout();

                        JPanel storeOption = new JPanel(new GridLayout(4,4));
                        JPanel createStore = new JPanel();
                        createStore.setAlignmentX(100);
                        createStore.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));

                        JPanel viewStore = new JPanel();
                        viewStore.setAlignmentX(100);
                        viewStore.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));

                        JPanel exitStore = new JPanel();
                        exitStore.setAlignmentX(100);
                        exitStore.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));

                        JButton create = new JButton("Create");
                        create.setSize(10,8);
                        JButton view = new JButton("View");
                        view.setSize(10,8);
                        JButton exit = new JButton("Exit");
                        exit.setSize(10,8);
                        createStore.add(create);
                        viewStore.add(view);
                        exitStore.add(exit);
                        storeOption.add(createStore);
                        storeOption.add(viewStore);
                        storeOption.add(exitStore);

                        create.addActionListener(creation -> {
                            JTextField createName = new JTextField("Please enter the name of your new store");

                            createName.setHorizontalAlignment(JTextField.CENTER);
                            createName.setEditable(false);
                            createName.setColumns(6);
                            createName.setSize(4,4);
                            Font font = new Font("Times New Roman", Font.PLAIN, 40);
                            JPanel extraPanel2 = new JPanel(new GridLayout(1,4));
                            extraPanel2.add(createName);

                            createStore.setFont(font);

                            JTextField enterStore = new JTextField();
                            JPanel extraPanel = new JPanel(new FlowLayout());
                            enterStore.setColumns(10);
                            extraPanel.add(enterStore);

                            JButton okCreate = new JButton("OK");
                            JButton cancelCreate = new JButton("Cancel");

                            JPanel okCancel = new JPanel(new FlowLayout());
                            okCancel.add(okCreate);
                            okCancel.add(cancelCreate);

                            okCreate.addActionListener(okStore ->{
                                String newStoreName = enterStore.getText();
                                if(newStoreName.isBlank()) {
                                    JOptionPane.showMessageDialog(null, "Please enter a name for your store.");
                                } else {
                                    //Store newStore = new Store(newStoreName);
                                    ((Seller) messengerClient.loggedOnUser).addStore(writer, new Store(newStoreName, (Seller)messengerClient.loggedOnUser));
                                    enterStore.setText("");
                                    okCreate.removeActionListener(okCreate.getActionListeners()[0]);
                                    cancelCreate.removeActionListener(cancelCreate.getActionListeners()[0]);
                                    card.show(storeMenu, "Options");
                                }
                            });

                            cancelCreate.addActionListener(cancel -> {
                                enterStore.setText("");
                                okCreate.removeActionListener(okCreate.getActionListeners()[0]);
                                cancelCreate.removeActionListener(cancelCreate.getActionListeners()[0]);
                                card.show(storeMenu, "Options");
                            });

                            JPanel createStoreMenu = new JPanel();
                            createStoreMenu.setLayout(new BoxLayout(createStoreMenu,BoxLayout.PAGE_AXIS));
                            createStoreMenu.add(extraPanel2);
                            createStoreMenu.add(extraPanel);
                            createStoreMenu.add(okCancel);
                            storeMenu.add(createStoreMenu, "Create");

                            card.show(storeMenu, "Create");

                        });

                        view.addActionListener(viewing -> {
                            ArrayList<Store> userStores = ((Seller) messengerClient.loggedOnUser).getStores();
                            String[] storeNames = new String[userStores.size()];

                            for(int i = 0; i < userStores.size(); i++) {
                                storeNames[i] = userStores.get(i).getStoreName();
                            }

                            JTextField blank = new JTextField("");

                            blank.setHorizontalAlignment(JTextField.CENTER);
                            blank.setEditable(false);
                            blank.setColumns(6);
                            blank.setSize(2,2);
                            JPanel blankPanel = new JPanel(new GridLayout(1,4));
                            blankPanel.add(blank);

                            JComboBox storeList = new JComboBox(storeNames);

                            JPanel storeSelection = new JPanel();
                            storeSelection.setLayout(new BoxLayout(storeSelection, BoxLayout.PAGE_AXIS));

                            JButton okSelection = new JButton("OK");
                            JButton cancelSelection = new JButton("Cancel");
                            JPanel buttons =  new JPanel(new FlowLayout());
                            buttons.setAlignmentX(100);
                            buttons.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));
                            buttons.add(okSelection);
                            buttons.add(cancelSelection);


                            JPanel storeListPanel = new JPanel();
                            storeListPanel.add(storeList);
                            storeSelection.add(blankPanel);
                            storeSelection.add(storeListPanel);
                            storeListPanel.add(buttons);

                            okSelection.addActionListener(selectStore -> {
                                int index = storeList.getSelectedIndex();
                                Store selectedStore = userStores.get(index);
                                JPanel storeEdit = new JPanel(new GridLayout(3,4));
                                storeEdit.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));

                                JButton rename = new JButton("Rename");
                                JButton delete = new JButton("Delete");
                                JButton cancel = new JButton("Cancel");

                                storeEdit.add(rename);
                                rename.addActionListener(renameStore -> {
                                    String newName = JOptionPane.showInputDialog(null, "Enter the store's new name");
                                    if(!newName.isBlank()) {
                                        selectedStore.setStoreName(newName);
                                    }
                                });

                                delete.addActionListener(deleteStore -> {
                                    int deleteConfirm = JOptionPane.showOptionDialog(null,
                                            "Are you sure you wish to delete this store",
                                            "Delete Store",JOptionPane.PLAIN_MESSAGE, JOptionPane.YES_NO_OPTION,
                                            null, null, null);

                                    if(deleteConfirm == 0) {
                                        ((Seller) messengerClient.loggedOnUser).removeStore(writer, selectedStore);
                                        try {
                                            cancel.removeActionListener(cancel.getActionListeners()[0]);
                                            rename.removeActionListener(rename.getActionListeners()[0]);
                                            delete.removeActionListener(delete.getActionListeners()[0]);
                                        } catch (ArrayIndexOutOfBoundsException exception) {
                                            System.out.println("Store Menu action listener removal: bad");
                                        }
                                        card.show(storeMenu, "StoreList");
                                    }

                                });

                                cancel.addActionListener(leaveStore -> {
                                    card.show(storeMenu, "Listing");
                                    try {
                                        cancel.removeActionListener(cancel.getActionListeners()[0]);
                                        rename.removeActionListener(rename.getActionListeners()[0]);
                                        delete.removeActionListener(delete.getActionListeners()[0]);
                                    } catch (ArrayIndexOutOfBoundsException exception) {
                                        System.out.println("Store Menu action listener removal: bad");
                                    }
                                });

                                storeEdit.add(delete);
                                storeEdit.add(cancel);

                                storeMenu.add(storeEdit, "Store");
                                card.show(storeMenu, "Store");
                            });
                            cancelSelection.addActionListener(leaveSelection -> {
                                okSelection.removeActionListener(okSelection.getActionListeners()[0]);
                                cancelSelection.removeActionListener(null);
                                card.show(storeMenu, "Options");
                            });

                            storeMenu.add(storeSelection, "StoreList");
                            card.show(storeMenu, "StoreList");
                        });

                        exit.addActionListener(exitStoreMenu -> {
                            storeMenu.removeAll();
                            messengerClient.cardLayout.show(messengerClient.container, "Main");

                        });
                        card.show(storeMenu, "Options");

                        storeMenu.add(storeOption, "Options");

                        messengerClient.cardLayout.show(messengerClient.container, "StoreMenu");
                        card.show(storeMenu, "Options");

                    } else if (selectedOption == 1) {
                        ArrayList<Conversation> conversations = refreshVisibleConversations(reader, writer, messengerClient.loggedOnUser);
                        if (conversations.size() < 1) {
                            JOptionPane.showMessageDialog(null, "You have no active conversations");
                        } else {
                            String[] conversationName = new String[conversations.size()];
                            for (int i = 0; i < conversations.size(); i++) {
                                conversationName[i] = conversations.get(i).getConversationID();
                            }
                            JList converseList = new JList(conversationName);


                            // Overall panel menu containing CardLayout
                            JPanel conversationMenu = new JPanel();
                            conversationMenu.setLayout(new CardLayout());
                            CardLayout card = (CardLayout) conversationMenu.getLayout();
                            messengerClient.container.add(conversationMenu, "ConversationMenu");

                            // Panel for conversation selection
                            JPanel viewConversationList = new JPanel();
                            viewConversationList.setLayout(new FlowLayout());
                            viewConversationList.setAlignmentX(100);
                            viewConversationList.setBorder(new EmptyBorder(new Insets(50, 200, 50, 200)));

                            JButton okButton = new JButton("Select");
                            JButton cancelButton = new JButton("Cancel");
                            JScrollPane selectList = new JScrollPane(converseList);
                            selectList.setBounds(200, 200, 200, 200);
                            viewConversationList.add(selectList);
                            JPanel panel = new JPanel(new GridLayout(1, 2));
                            panel.add(okButton);
                            panel.add(cancelButton);
                            viewConversationList.add(panel);

                            conversationMenu.add(viewConversationList, "Listing");
                            card.show(conversationMenu, "Listing");


                            JPanel messageOptions = new JPanel();
                            conversationMenu.add(messageOptions, "Options");

                            JButton sendMessage = new JButton("Send");
                            JButton editMessage = new JButton("Edit");
                            JButton deleteMessage = new JButton("Delete");
                            JButton exitMessage = new JButton("Exit");

                            JPanel flow = new JPanel(new FlowLayout());
                            flow.add(sendMessage);
                            flow.add(editMessage);
                            flow.add(deleteMessage);
                            flow.add(exitMessage);
                            messageOptions.add(flow);


                            DefaultListModel listModel = new DefaultListModel();
                            JList messageHistory = new JList(listModel);

                            okButton.addActionListener(ok -> {
                                try {
                                    int index = converseList.getSelectedIndex();
                                    Conversation selectedConversation = conversations.get(index);
                                    messageOptions.setLayout(new GridLayout(2, 9));
                                    ArrayList<Message> messageList = selectedConversation.readFileAsPerUser(messengerClient.loggedOnUser);

                                    // Populates an Array List with appropriate message displays
                                    ArrayList<String> messageDisplay = new ArrayList<>();
                                    for (int i = 0; i < messageList.size(); i++) {
                                        messageDisplay.add(messageList.get(i).getSender().getUsername() + ": " + messageList.get(i).getMessage());
                                    }

                                    DefaultListModel model = new DefaultListModel();
                                    for (int i = 0; i < messageList.size(); i++) {
                                        model.addElement(messageDisplay.get(i));
                                    }
                                    JList list = new JList(model);
                                    JScrollPane history = new JScrollPane(list);

                                    messageOptions.add(history);


                                    sendMessage.addActionListener(send -> {
                                        String sending = JOptionPane.showInputDialog(null, "Enter a new message",
                                                "Send Message", JOptionPane.PLAIN_MESSAGE);
                                        if (messengerClient.loggedOnUser instanceof Customer) {
                                            messengerClient.loggedOnUser.sendMessageToUser(reader, writer, sending, selectedConversation.getSeller());
                                        }
                                        if (messengerClient.loggedOnUser instanceof Seller) {
                                            messengerClient.loggedOnUser.sendMessageToUser(reader, writer, sending, selectedConversation.getCustomer());
                                        }
                                        sendMessage.removeActionListener(null);

                                    });

                                    editMessage.addActionListener(edit -> {
                                        try {
                                            int messageIndex = list.getSelectedIndex();
                                            Message selectedMessage = messageList.get(messageIndex);
                                            if (selectedMessage.getSender().equals(messengerClient.loggedOnUser)) {
                                                String replacement = JOptionPane.showInputDialog(null, "Edit Message", selectedMessage.getMessage());
                                                if (replacement != null) {
                                                    messengerClient.loggedOnUser.editMessage(reader, writer, selectedMessage, selectedConversation, replacement);
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You are not able to edit this message.");
                                            }
                                        } catch(IndexOutOfBoundsException exception) {
                                            JOptionPane.showMessageDialog(null, "Please select a message to edit.");
                                        }
                                        editMessage.removeActionListener(null);
                                    });

                                    deleteMessage.addActionListener(delete -> {
                                        try {
                                            int messageIndex = list.getSelectedIndex();
                                            Message selectedMessage = messageList.get(messageIndex);
                                            if (selectedMessage.getSender().equals(messengerClient.loggedOnUser)) {
                                                int deleteConfirm = JOptionPane.showOptionDialog(null,
                                                        "Are you sure you want to delete this message?", "",
                                                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                        null, null, null);
                                                if (deleteConfirm == 0) {
                                                    messengerClient.loggedOnUser.deleteMessage(reader, writer, selectedMessage, selectedConversation);
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You are not able to delete this message");
                                            }
                                        } catch (IndexOutOfBoundsException exception) {
                                            JOptionPane.showMessageDialog(null, "Please select a message to delete.");
                                        }
                                        deleteMessage.removeActionListener(null);
                                    });

                                    exitMessage.addActionListener(exit -> {
                                        for (int i = 0; i < model.getSize(); i++) {
                                            model.remove(i);
                                        }
                                        try {
                                            sendMessage.removeActionListener(sendMessage.getActionListeners()[0]);
                                            deleteMessage.removeActionListener(deleteMessage.getActionListeners()[0]);
                                            editMessage.removeActionListener(editMessage.getActionListeners()[0]);
                                            exitMessage.removeActionListener(exitMessage.getActionListeners()[0]);
                                            card.show(conversationMenu, "Listing");
                                            messageOptions.remove(history);
                                        } catch (ArrayIndexOutOfBoundsException exception) {
                                            System.out.println("Conversation Menu action listener removal: incorrect");
                                        }
                                    });
                                    card.show(conversationMenu, "Options");
                                } catch (IndexOutOfBoundsException indexException) {
                                    JOptionPane.showMessageDialog(null, "Please select a conversation");
                                }
                            });

                            cancelButton.addActionListener(cancel -> {
                                okButton.removeActionListener(okButton.getActionListeners()[0]);
                                cancelButton.removeActionListener(cancelButton.getActionListeners()[0]);
                                conversationMenu.removeAll();
                                messengerClient.cardLayout.show(messengerClient.container, "Main");
                            });
                            messengerClient.cardLayout.show(messengerClient.container, "ConversationMenu");

                        }
                    } else if (selectedOption == 2) {
                        ArrayList<Customer> customerSearchList = new ArrayList<>();
                        ArrayList<Store> storeSearchList = new ArrayList<>();


                        JPanel listMenu = new JPanel(new CardLayout());
                        CardLayout card = (CardLayout) listMenu.getLayout();
                        messengerClient.container.add(listMenu, "ListMenu");

                        DefaultListModel model = new DefaultListModel();

                        if(messengerClient.loggedOnUser instanceof Customer) {
                            storeSearchList = refreshStores(reader, writer, messengerClient.loggedOnUser);
                            for (int i = 0; i < storeSearchList.size(); i++) {
                                model.addElement(storeSearchList.get(i).getStoreName());
                            }
                        }
                        if(messengerClient.loggedOnUser instanceof Seller) {
                            customerSearchList = refreshCustomers(reader, writer,messengerClient.loggedOnUser);
                            for(int i = 0; i < customerSearchList.size(); i++) {
                                model.addElement(customerSearchList.get(i).getUsername());
                            }
                        }

                        JList selectUser = new JList(model);
                        JScrollPane selectUserDisplay = new JScrollPane(selectUser);

                        JPanel listDisplay = new JPanel(new FlowLayout());
                        listDisplay.setAlignmentX(100);
                        listDisplay.setBorder(new EmptyBorder(new Insets(30, 200, 30, 200)));
                        listDisplay.add(selectUserDisplay);

                        JButton okButton = new JButton("OK");
                        JButton cancelButton = new JButton("Cancel");
                        JPanel buttons = new JPanel(new GridLayout(1, 2));
                        buttons.add(okButton);
                        buttons.add(cancelButton);
                        listDisplay.add(buttons);


                        listMenu.add(listDisplay, "Display");

                        ArrayList<Store> secondStoreSearchList = storeSearchList;
                        ArrayList<Customer> secondCustomerSearchList = customerSearchList;
                        okButton.addActionListener(ok -> {
                            try {
                                int index = selectUser.getSelectedIndex();
                                User user = null;

                                if (messengerClient.loggedOnUser instanceof Customer) {
                                    user = secondStoreSearchList.get(index).getSeller();
                                }
                                if (messengerClient.loggedOnUser instanceof Seller) {
                                    user = secondCustomerSearchList.get(index);
                                }

                                JPanel msgBlockInvi = new JPanel();
                                msgBlockInvi.setLayout(new BoxLayout(msgBlockInvi, BoxLayout.PAGE_AXIS));

                                String name = "";
                                if (messengerClient.loggedOnUser instanceof Customer) {
                                    name = secondStoreSearchList.get(index).getStoreName();
                                }
                                if (messengerClient.loggedOnUser instanceof Seller) {
                                    name = user.getUsername();
                                }

                                JTextField usersName = new JTextField(name);
                                usersName.setHorizontalAlignment(JTextField.CENTER);
                                usersName.setEditable(false);
                                usersName.setColumns(6);
                                Font font = new Font("Times New Roman", Font.PLAIN, 20);
                                usersName.setFont(font);

                                msgBlockInvi.add(usersName);

                                JButton messageButton = new JButton("Message");
                                JButton blockButton = new JButton("Block");
                                JButton invisibleButton = new JButton("Invisible");
                                JButton exitButton = new JButton("Exit");
                                JPanel optionButtons = new JPanel(new GridLayout(4, 1));
                                optionButtons.setBorder(new EmptyBorder(new Insets(30, 200, 30, 200)));
                                optionButtons.add(messageButton);
                                optionButtons.add(blockButton);
                                optionButtons.add(invisibleButton);
                                optionButtons.add(exitButton);
                                msgBlockInvi.add(optionButtons);

                                listMenu.add(msgBlockInvi, "Options");
                                card.show(listMenu, "Options");

                                User restateUser = user;
                                messageButton.addActionListener(message -> {
                                    String msgToUser = JOptionPane.showInputDialog(null, "Enter your message");
                                    if (msgToUser != null) {
                                        messengerClient.loggedOnUser.sendMessageToUser(reader, writer, msgToUser, restateUser);
                                    }
                                    messageButton.removeActionListener(null);
                                });

                                blockButton.addActionListener(block -> {
                                    int confirmBlock = JOptionPane.showConfirmDialog(null,
                                            "Are you sure you wish to Block this user?", "Blocking", JOptionPane.YES_NO_OPTION);
                                    if (confirmBlock == 0) {
                                        messengerClient.loggedOnUser.addBlockedUser(writer, restateUser);
                                    }
                                    blockButton.removeActionListener(null);
                                });

                                invisibleButton.addActionListener(invisible -> {
                                    int confirmInvisible = JOptionPane.showConfirmDialog(null,
                                            "Are you sure you wish to become Invisible to this user?",
                                            "Invisiblility", JOptionPane.YES_NO_OPTION);
                                    if(confirmInvisible == 0) {
                                        messengerClient.loggedOnUser.addInvisibleUser(writer, restateUser);
                                    }
                                    invisibleButton.removeActionListener(null);
                                });

                                exitButton.addActionListener(exit -> {
                                    try {
                                        messageButton.removeActionListener(messageButton.getActionListeners()[0]);
                                        blockButton.removeActionListener(blockButton.getActionListeners()[0]);
                                        invisibleButton.removeActionListener(invisibleButton.getActionListeners()[0]);
                                        exitButton.removeActionListener(exitButton.getActionListeners()[0]);
                                        card.show(listMenu, "Display");
                                        msgBlockInvi.remove(usersName);
                                    } catch (ArrayIndexOutOfBoundsException exception) {
                                        System.out.println("List Menu action listener removal: incorrect");
                                    }
                                });

                            } catch (IndexOutOfBoundsException exception) {
                                JOptionPane.showMessageDialog(null, "Please select an option.");
                            } catch (NullPointerException exception) {
                                JOptionPane.showMessageDialog(null, "Something horribly wrong has occurred.");
                            }
                            okButton.removeActionListener(null);
                        });

                        cancelButton.addActionListener(cancel -> {
                            listDisplay.remove(selectUserDisplay);
                            cancelButton.removeActionListener(cancelButton.getActionListeners()[0]);
                            okButton.removeActionListener(okButton.getActionListeners()[0]);
                            listMenu.removeAll();
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        });

                        card.show(listMenu, "Display");
                        messengerClient.cardLayout.show(messengerClient.container, "ListMenu");

                    } else if (selectedOption == 3) {

                    } else if (selectedOption == 4) {

                    } else if (selectedOption == 5) {
                        exitApplication(messengerClient, writer);
                    } else {
                        JOptionPane.showMessageDialog(null, "Select an Option",
                                "Messenger", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            holder.add(list);
        } else {
            String[] options = {"View Conversations", "View All Stores", "Search Sellers", "Edit Account", "Log Out"};
            JList list = new JList(options);
            list.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    int selectedOption = list.getSelectedIndex();
                    if (selectedOption == 0) {

                    } else if (selectedOption == 1) {

                    } else if (selectedOption == 2) {

                    } else if (selectedOption == 3) {

                    } else if (selectedOption == 4) {
                        exitApplication(messengerClient, writer);
                    } else {
                        JOptionPane.showMessageDialog(null, "Select an Option",
                                "Messenger", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            holder.add(list);
        }

        messengerClient.container.add("Main", holder);
        messengerClient.cardLayout.show(messengerClient.container, "Main");
    }
}
