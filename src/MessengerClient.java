import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
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

    private JFrame jFrame;

    private Container container;

    private CardLayout cardLayout = new CardLayout();

    private User loggedOnUser = null;

    private ArrayList<Conversation> conversations = new ArrayList<>();

    private ArrayList<Message> messages = new ArrayList<>();

    private ArrayList<Customer> customers = new ArrayList<>();

    private ArrayList<Store> stores = new ArrayList<>();

    private ArrayList<Customer> searchCustomers = new ArrayList<>();

    private ArrayList<Seller> searchSellers = new ArrayList<>();

    public MessengerClient(PrintWriter writer) {
        jFrame = new JFrame("Messenger");
        jFrame.setSize(400, 200);
        jFrame.setLocationRelativeTo(null);
        container = jFrame.getContentPane();
        container.setLayout(cardLayout);
        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                sendRequest(writer, "[LOGOUT]");
            }
        });
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
     * @param conversation The conversation to be exported
     * @return Returns whether the .csv conversion and export succeeded
     * @throws IOException in the case of a conversion failure
     */
    public static boolean convertConversationToCSV(BufferedReader reader, PrintWriter writer, Conversation conversation, User loggedOnUser) throws IOException {

        Files.createDirectories(Paths.get("src/exports")); // Creates the subfolder exports if it does not exist
        File destinationFile = new File("src/exports");

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
            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            MessengerClient messengerClient = new MessengerClient(writer);

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

                            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || (!sellerOption.isSelected() && !customerOption.isSelected())) {
                                JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form", "Messenger", JOptionPane.ERROR_MESSAGE);
                            } else {
                                String role = (sellerOption.isSelected()) ? "Seller" : "Customer";
                                String checkUsernameRequest = "[CHECK.USERNAME]" + usernameTextField.getText();
                                sendRequest(writer, checkUsernameRequest);
                                boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                                String checkEmailRequest = "[CHECK.EMAIL]" + emailTextField.getText();
                                sendRequest(writer, checkEmailRequest);
                                boolean isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

                                if (!(isUsernameTaken || isEmailRegistered)) {
                                    String createAccountRequest = String.format("[CREATE.USER]%s, %s, %s, %s", username, email, password, role);
                                    sendRequest(writer, createAccountRequest);
                                    String response = readResponse(reader);

                                    if (response != null && response.substring(0, response.indexOf("<")).equalsIgnoreCase("Seller")) {
                                        messengerClient.loggedOnUser = new Seller(response, true, true);
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
                                    JOptionPane.showMessageDialog(null, "Username already Taken", "Messenger", JOptionPane.ERROR_MESSAGE);
                                    usernameTextField.setText("");
                                } else {
                                    JOptionPane.showMessageDialog(null, "Email already Registered", "Messenger", JOptionPane.ERROR_MESSAGE);
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
                            messengerClient.jFrame.setTitle("Messenger");
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
                    JPasswordField passwordTextField = new JPasswordField(20);
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
                                JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form", "Messenger", JOptionPane.ERROR_MESSAGE);
                            } else {
                                String checkUsernameRequest = "[CHECK.USERNAME]" + username;
                                sendRequest(writer, checkUsernameRequest);
                                boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                                String checkEmailRequest = "[CHECK.EMAIL]" + username;
                                sendRequest(writer, checkEmailRequest);
                                boolean isEmailRegistered = Boolean.parseBoolean(readResponse(reader));

                                if (isUsernameTaken || isEmailRegistered) {
                                    String fetchAccountRequest = "[FETCH.USER]" + username;
                                    sendRequest(writer, fetchAccountRequest);
                                    String fetchResponse = readResponse(reader);

                                    if (fetchResponse != null && fetchResponse.substring(0, fetchResponse.indexOf("<")).equalsIgnoreCase("Seller")) {
                                        messengerClient.loggedOnUser = new Seller(fetchResponse, true, true);
                                    } else {
                                        messengerClient.loggedOnUser = new Customer(fetchResponse, true);
                                    }

                                    if (messengerClient.loggedOnUser.getPassword().equals(password)) {
                                        JOptionPane.showMessageDialog(null, String.format("Welcome back %s!", messengerClient.loggedOnUser.getUsername().toUpperCase()));

                                        String fetchNumUnreadRequest;
                                        if (messengerClient.loggedOnUser instanceof Seller) {
                                            fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Seller) messengerClient.loggedOnUser).detailedToString();
                                        } else {
                                            fetchNumUnreadRequest = "[FETCH.UNREAD]" + ((Customer) messengerClient.loggedOnUser).detailedToString();
                                        }
                                        sendRequest(writer, fetchNumUnreadRequest);
                                        fetchResponse = readResponse(reader);

                                        if (fetchResponse != null && Integer.parseInt(fetchResponse) > 0) {
                                            JOptionPane.showMessageDialog(null, String.format("You have %d new unread conversation%s", Integer.parseInt(fetchResponse), (Integer.parseInt(fetchResponse) != 1) ? "s" : ""));
                                        }
                                        showMainMenu(messengerClient, reader, writer);
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Incorrect Password", "Messenger", JOptionPane.ERROR_MESSAGE);
                                        passwordTextField.setText("");
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Unregistered Account");
                                    usernameTextField.setText("");
                                    passwordTextField.setText("");
                                }
                            }
                        }
                    });

                    JButton backButton = new JButton("Back");
                    backButton.setPreferredSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setTitle("Messenger");
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
        messengerClient.jFrame.setSize(400, 300);
        JPanel holder = new JPanel(new GridLayout(0, 1));

        if (messengerClient.loggedOnUser instanceof Seller) {
            JPanel manageStoresPanel = new JPanel();
            JButton manageStoresButton = new JButton("Manage Stores");
            manageStoresButton.setPreferredSize(new Dimension(175, 30));
            manageStoresButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Messenger: Manage Stores");
                    JPanel holder = new JPanel(new BorderLayout());
                    JPanel storePanel = new JPanel(new GridLayout(0, 1, 0, 10));

                    if (((Seller) messengerClient.loggedOnUser).getStores().size() > 0) {
                        for (int i = 0; i < ((Seller) messengerClient.loggedOnUser).getStores().size(); i++) {
                            final int index = i;
                            final Store selectedStore = ((Seller) messengerClient.loggedOnUser).getStores().get(i);
                            String title = selectedStore.getStoreName();

                            JMenuItem storeElement = new JMenuItem(title);
                            storeElement.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    Object[] storeActions = {"Cancel", "Delete"};

                                    JPanel holder = new JPanel(new GridLayout(4, 1));
                                    holder.add(new JPanel());

                                    JLabel storeLabel = new JLabel("Selected: " + title);
                                    storeLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                    storeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                    holder.add(storeLabel);

                                    holder.add(new JPanel());

                                    int result = JOptionPane.showOptionDialog(null, holder, "Delete Store",
                                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                            null, storeActions, null);
                                    if (result == 1) {
                                        ((Seller) messengerClient.loggedOnUser).removeStore(writer, selectedStore);
                                        messengerClient.jFrame.setSize(400, 250);
                                        messengerClient.cardLayout.show(messengerClient.container, "Main");
                                    }
                                }
                            });
                            storePanel.add(storeElement);
                        }

                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setViewportView(storePanel);
                        holder.add(scrollPane);

                        messengerClient.jFrame.setSize(400,
                                Math.min(((Seller) messengerClient.loggedOnUser).getStores().size() * 60 + 40, 250));
                    } else {
                        messengerClient.jFrame.setSize(400, 120);
                        JLabel storeLabel = new JLabel("No Stores");
                        storeLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        storeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        holder.add(storeLabel);
                    }

                    JPanel buttonPanel = new JPanel();
                    JButton backButton = new JButton("Back");
                    backButton.setSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setSize(400, 300);
                            messengerClient.jFrame.setTitle("Messenger: Main");
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        }
                    });
                    buttonPanel.add(backButton);

                    JButton addButton = new JButton("Add");
                    addButton.setSize(new Dimension(150, 30));
                    addButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            Object[] storeOptions = {"Cancel", "Add"};

                            JPanel storeHolder = new JPanel(new GridLayout(2, 1));

                            JLabel storeLabel = new JLabel("New Store Name:");
                            storeLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                            storeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                            storeHolder.add(storeLabel);
                            JTextField storeTextField = new JTextField(20);
                            storeHolder.add(storeTextField);

                            int storeResult = JOptionPane.showOptionDialog(null, storeHolder, "Add Store",
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                    null, storeOptions, null);

                            String newStoreName = storeTextField.getText();
                            if (storeResult == 1) {
                                if (newStoreName.isEmpty()) {
                                    JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form",
                                            "Messenger", JOptionPane.ERROR_MESSAGE);
                                } else {
                                    ((Seller) messengerClient.loggedOnUser).addStore(writer,
                                            new Store(newStoreName, (Seller) messengerClient.loggedOnUser));
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.cardLayout.show(messengerClient.container, "Main");
                                }
                            } else {
                                messengerClient.jFrame.setSize(400, 300);
                                messengerClient.cardLayout.show(messengerClient.container, "Main");
                            }
                        }
                    });
                    buttonPanel.add(addButton);
                    holder.add(buttonPanel, BorderLayout.SOUTH);

                    messengerClient.container.add("Store Menu", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "Store Menu");
                }
            });
            manageStoresPanel.add(manageStoresButton);

            JPanel conversationsPanel = new JPanel();
            JButton conversationsButton = new JButton("View Conversations");
            conversationsButton.setPreferredSize(new Dimension(175, 30));
            conversationsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Messenger: View Conversations");
                    messengerClient.conversations = refreshVisibleConversations(reader, writer, messengerClient.loggedOnUser);
                    JPanel holder = new JPanel(new BorderLayout());

                    JPanel conversationsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                    if (messengerClient.conversations.size() > 0) {
                        for (int i = 0; i < messengerClient.conversations.size(); i++) {
                            final Conversation conversation = messengerClient.conversations.get(i);
                            String title = conversation.getConversationID();

                            JMenuItem conversationElement = new JMenuItem(title);
                            conversationElement.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    messengerClient.jFrame.setTitle(title);
                                    messengerClient.jFrame.setSize(600, 300);
                                    if (messengerClient.loggedOnUser instanceof Seller) {
                                        conversation.setSellerUnread(writer, false);
                                    } else {
                                        conversation.setCustomerUnread(writer, false);
                                    }
                                    JPanel holder = new JPanel(new BorderLayout());

                                    messengerClient.messages = refreshVisibleMessages(reader, writer, conversation,
                                            messengerClient.loggedOnUser);
                                    if (messengerClient.messages.size() > 0) {
                                        DefaultListModel messageModel = new DefaultListModel();
                                        for (int i = 0; i < messengerClient.messages.size(); i++) {
                                            String message = messengerClient.messages.get(i).getSender().getUsername()
                                                    + ": ";

                                            if (messengerClient.loggedOnUser.isRequestsCensorship()) {
                                                message += messengerClient.messages.get(i)
                                                        .getCensoredMessage(messengerClient.loggedOnUser);
                                            } else {
                                                message += messengerClient.messages.get(i).getMessage();
                                            }

                                            messageModel.addElement(message);
                                        }

                                        JList messageList = new JList(messageModel);
                                        messageList.addListSelectionListener(new ListSelectionListener() {
                                            @Override
                                            public void valueChanged(ListSelectionEvent e) {
                                                Message selectedMessage = messengerClient.messages.get(e.getLastIndex());

                                                Object[] userActions = {"Back", "Delete Message", "Edit Message"};

                                                JPanel holder = new JPanel(new GridLayout(4, 1));
                                                holder.add(new JPanel());

                                                JLabel messageLabel = new JLabel("Message Actions");
                                                messageLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                holder.add(messageLabel);

                                                holder.add(new JPanel());

                                                int result = JOptionPane.showOptionDialog(null, holder, "Message Actions",
                                                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                        null, userActions, null);

                                                if (result == 1) {
                                                    if (messengerClient.loggedOnUser.deleteMessage(reader, writer, selectedMessage, conversation)) {
                                                        JOptionPane.showMessageDialog(null, "Deleted Message!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Could Not Delete Message",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                } else if (result == 2) {
                                                    User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                            conversation.getCustomer() : conversation.getSeller();

                                                    if (selectedMessage.getSender().equals(messengerClient.loggedOnUser)) {
                                                        Object[] sendOptions = {"Cancel", "Edit & Send"};

                                                        JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                        sendHolder.add(new JPanel());

                                                        JLabel sendLabel = new JLabel("Edited Message:");
                                                        sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                        sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                        sendHolder.add(sendLabel);
                                                        JTextField sendTextField = new JTextField(20);
                                                        sendHolder.add(sendTextField);

                                                        sendHolder.add(new JPanel());

                                                        int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Edit a Message",
                                                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                                null, sendOptions, null);

                                                        if (sendResult == 1) {
                                                            String message = sendTextField.getText();
                                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                                if (message.isEmpty()) {
                                                                    JOptionPane.showMessageDialog(null, "Not Sent. Empty Message");
                                                                } else {
                                                                    if (messengerClient.loggedOnUser.editMessage(reader,
                                                                            writer, selectedMessage, conversation, message)) {
                                                                        JOptionPane.showMessageDialog(null, "Updated Message!");
                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Update Failed",
                                                                                "Messenger", JOptionPane.ERROR_MESSAGE);
                                                                    }
                                                                }
                                                            } else {
                                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                                            }
                                                        }
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "You cannot edit received messages!",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                }
                                            }
                                        });

                                        JScrollPane scrollPane = new JScrollPane();
                                        scrollPane.setViewportView(messageList);
                                        holder.add(scrollPane);
                                    } else {
                                        messengerClient.jFrame.setSize(400, 120);
                                        JLabel messageLabel = new JLabel("No Messages");
                                        messageLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                        holder.add(messageLabel);
                                    }

                                    JPanel buttonPanel = new JPanel();
                                    JButton backButton = new JButton("Back");
                                    backButton.setSize(new Dimension(150, 30));
                                    backButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            messengerClient.jFrame.setSize(400,
                                                    Math.min(messengerClient.conversations.size() * 60 + 40, 250));
                                            messengerClient.jFrame.setTitle("Messenger: View Conversations");
                                            messengerClient.cardLayout.show(messengerClient.container, "List Conversations");
                                        }
                                    });
                                    buttonPanel.add(backButton);

                                    JButton exportButton = new JButton("Export to CSV");
                                    exportButton.setSize(new Dimension(150, 30));
                                    exportButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            try {
                                                convertConversationToCSV(reader, writer, conversation, messengerClient.loggedOnUser);
                                                JOptionPane.showMessageDialog(null, "Exported to src/exports!");
                                            } catch (IOException ex) {
                                                JOptionPane.showMessageDialog(null, "Could Not Convert", "Messenger", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(exportButton);

                                    JButton importButton = new JButton("Import a TXT");
                                    importButton.setSize(new Dimension(150, 30));
                                    importButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                    conversation.getCustomer() : conversation.getSeller();

                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                Object[] sendOptions = {"Cancel", "Send"};

                                                JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                sendHolder.add(new JPanel());

                                                JLabel sendLabel = new JLabel(".TXT File Source:");
                                                sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                sendHolder.add(sendLabel);
                                                JTextField sendTextField = new JTextField(20);
                                                sendHolder.add(sendTextField);

                                                sendHolder.add(new JPanel());

                                                int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Import a .TXT",
                                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                        null, sendOptions, null);

                                                String filePath = sendTextField.getText();
                                                if (sendResult == 1 && !filePath.isEmpty()) {
                                                    if (conversation.importTXT(reader, writer, filePath,
                                                            messengerClient.loggedOnUser, recipient)) {
                                                        JOptionPane.showMessageDialog(null, "Sent!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Import Failed. Check the entered path.",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(importButton);

                                    JTextField sendTextField = new JTextField(20);
                                    buttonPanel.add(sendTextField);
                                    JButton sendButton = new JButton("Send");
                                    sendButton.setSize(new Dimension(150, 30));
                                    sendButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                    conversation.getCustomer() : conversation.getSeller();

                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                String message = sendTextField.getText().strip();
                                                if (message.isEmpty()) {
                                                    JOptionPane.showMessageDialog(null, "Not Sent. Empty Message");
                                                } else {
                                                    if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, recipient)) {
                                                        JOptionPane.showMessageDialog(null, "Sent!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Message Failed",
                                                                "Messenger", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                }
                                                sendTextField.setText("");
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(sendButton);
                                    holder.add(buttonPanel, BorderLayout.SOUTH);

                                    messengerClient.container.add("Messages View", holder);
                                    messengerClient.cardLayout.show(messengerClient.container, "Messages View");
                                }
                            });
                            conversationsPanel.add(conversationElement);
                        }

                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setViewportView(conversationsPanel);
                        holder.add(scrollPane);

                        messengerClient.jFrame.setSize(400,
                                Math.min(messengerClient.conversations.size() * 60 + 40, 250));
                    } else {
                        messengerClient.jFrame.setSize(400, 100);
                        JLabel conversationLabel = new JLabel("No Conversations");
                        conversationLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        conversationLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        holder.add(conversationLabel);
                    }

                    JButton backButton = new JButton("Back");
                    backButton.setSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setSize(400, 300);
                            messengerClient.jFrame.setTitle("Messenger: Main");
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        }
                    });
                    holder.add(backButton, BorderLayout.SOUTH);

                    messengerClient.container.add("List Conversations", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "List Conversations");
                }
            });
            conversationsPanel.add(conversationsButton);

            JPanel listCustomersPanel = new JPanel();
            JButton listCustomersButton = new JButton("View All Customers");
            listCustomersButton.setPreferredSize(new Dimension(175, 30));
            listCustomersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Messenger: View All Customers");
                    messengerClient.customers = refreshCustomers(reader, writer, messengerClient.loggedOnUser);
                    JPanel holder = new JPanel(new BorderLayout());

                    JPanel customersPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                    if (messengerClient.customers.size() > 0) {
                        for (int i = 0; i < messengerClient.customers.size(); i++) {
                            final Customer customer = messengerClient.customers.get(i);
                            String title = String.format("%s | %s", customer.getUsername(), customer.getEmail());

                            JMenuItem customerElement = new JMenuItem(title);
                            customerElement.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String blockedUserTitle = "Block";
                                    String invisibleUserTitle = "Become Invisible";
                                    if (messengerClient.loggedOnUser.getBlockedUsers().contains(customer)) {
                                        blockedUserTitle = "Unblock";
                                    }
                                    if (messengerClient.loggedOnUser.getInvisibleUsers().contains(customer)) {
                                        invisibleUserTitle = "Become Visible";
                                    }

                                    Object[] userActions = {"Message", blockedUserTitle, invisibleUserTitle};

                                    JPanel holder = new JPanel(new GridLayout(4, 1));
                                    holder.add(new JPanel());

                                    JLabel customerLabel = new JLabel("Selected: " + title);
                                    customerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                    customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                    holder.add(customerLabel);

                                    holder.add(new JPanel());

                                    int result = JOptionPane.showOptionDialog(null, holder, "User Actions",
                                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                            null, userActions, null);
                                    if (result == 0) {
                                        //TODO Update Customers
                                        messengerClient.customers = refreshCustomers(reader, writer, messengerClient.loggedOnUser);
                                        if (!customer.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                !messengerClient.loggedOnUser.getBlockedUsers().contains(customer)) {
                                            Object[] sendOptions = {"Cancel", "Send"};

                                            JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                            sendHolder.add(new JPanel());

                                            JLabel sendLabel = new JLabel("Your Message:");
                                            sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                            sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                            sendHolder.add(sendLabel);
                                            JTextField sendTextField = new JTextField(20);
                                            sendHolder.add(sendTextField);

                                            sendHolder.add(new JPanel());

                                            int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Send a Message",
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                    null, sendOptions, null);

                                            String message = sendTextField.getText();
                                            if (sendResult == 1 && !message.isEmpty()) {
                                                if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, customer)) {
                                                    JOptionPane.showMessageDialog(null, "Sent!");
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "Message Failed!");
                                                }
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                    "Messenger", JOptionPane.WARNING_MESSAGE);
                                        }
                                    } else if (result == 1) {
                                        ArrayList<User> blockedUsers = messengerClient.loggedOnUser.getBlockedUsers();
                                        if (blockedUsers.contains(customer)) {
                                            messengerClient.loggedOnUser.removeBlockedUser(writer, customer);
                                        } else {
                                            messengerClient.loggedOnUser.addBlockedUser(writer, customer);
                                        }
                                    } else if (result == 2) {
                                        ArrayList<User> invisibleUsers = messengerClient.loggedOnUser.getInvisibleUsers();
                                        if (invisibleUsers.contains(customer)) {
                                            messengerClient.loggedOnUser.removeInvisibleUser(writer, customer);
                                        } else {
                                            messengerClient.loggedOnUser.addInvisibleUser(writer, customer);
                                        }
                                    }
                                }
                            });
                            customersPanel.add(customerElement);
                        }

                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setViewportView(customersPanel);
                        holder.add(scrollPane);

                        messengerClient.jFrame.setSize(400, Math.min(messengerClient.customers.size() * 60 + 40, 250));
                    } else {
                        messengerClient.jFrame.setSize(400, 100);
                        JLabel customerLabel = new JLabel("No Customers");
                        customerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        holder.add(customerLabel);
                    }

                    JButton backButton = new JButton("Back");
                    backButton.setSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setSize(400, 300);
                            messengerClient.jFrame.setTitle("Messenger: Main");
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        }
                    });
                    holder.add(backButton, BorderLayout.SOUTH);

                    messengerClient.container.add("List Customers", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "List Customers");
                }
            });
            listCustomersPanel.add(listCustomersButton);

            JPanel searchCustomerPanel = new JPanel();
            JButton searchCustomerButton = new JButton("Search for a Customer");
            searchCustomerButton.setPreferredSize(new Dimension(175, 30));
            searchCustomerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object[] searchOptions = {"Cancel", "Search"};

                    JPanel searchHolder = new JPanel(new GridLayout(0, 1));
                    searchHolder.add(new JPanel());

                    JLabel searchLabel = new JLabel("Search Keyword:");
                    searchLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    searchLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    searchHolder.add(searchLabel);
                    JTextField searchTextField = new JTextField(20);
                    searchHolder.add(searchTextField);

                    searchHolder.add(new JPanel());

                    int searchResult = JOptionPane.showOptionDialog(null, searchHolder, "Search Customers",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, searchOptions, null);

                    String searchKeyword = searchTextField.getText();
                    if (searchResult == 1) {
                        if (searchKeyword.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Cannot Leave Empty",
                                    "Messenger", JOptionPane.ERROR_MESSAGE);
                        } else {
                            messengerClient.jFrame.setTitle("Messenger: Customer Search Results");
                            messengerClient.searchCustomers = refreshSearchCustomers(reader, writer,
                                    messengerClient.loggedOnUser, searchKeyword);
                            JPanel holder = new JPanel(new BorderLayout());

                            JPanel customersPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                            if (messengerClient.searchCustomers.size() > 0) {
                                for (int i = 0; i < messengerClient.searchCustomers.size(); i++) {
                                    final Customer customer = messengerClient.searchCustomers.get(i);
                                    String title = String.format("%s | %s", customer.getUsername(), customer.getEmail());

                                    JMenuItem customerElement = new JMenuItem(title);
                                    customerElement.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String blockedUserTitle = "Block";
                                            String invisibleUserTitle = "Become Invisible";
                                            if (messengerClient.loggedOnUser.getBlockedUsers().contains(customer)) {
                                                blockedUserTitle = "Unblock";
                                            }
                                            if (messengerClient.loggedOnUser.getInvisibleUsers().contains(customer)) {
                                                invisibleUserTitle = "Become Visible";
                                            }

                                            Object[] userActions = {"Message", blockedUserTitle, invisibleUserTitle};

                                            JPanel holder = new JPanel(new GridLayout(4, 1));
                                            holder.add(new JPanel());

                                            JLabel customerLabel = new JLabel("Selected: " + title);
                                            customerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                            customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                            holder.add(customerLabel);

                                            holder.add(new JPanel());

                                            int result = JOptionPane.showOptionDialog(null, holder, "User Actions",
                                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                    null, userActions, null);
                                            if (result == 0) {
                                                //TODO Update Customers
                                                messengerClient.searchCustomers = refreshSearchCustomers(reader, writer, messengerClient.loggedOnUser, searchKeyword);
                                                if (!customer.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                        !messengerClient.loggedOnUser.getBlockedUsers().contains(customer)) {
                                                    Object[] sendOptions = {"Cancel", "Send"};

                                                    JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                    sendHolder.add(new JPanel());

                                                    JLabel sendLabel = new JLabel("Your Message:");
                                                    sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                    sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                    sendHolder.add(sendLabel);
                                                    JTextField sendTextField = new JTextField(20);
                                                    sendHolder.add(sendTextField);

                                                    sendHolder.add(new JPanel());

                                                    int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Send a Message",
                                                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                            null, sendOptions, null);

                                                    String message = sendTextField.getText();
                                                    if (sendResult == 1 && !message.isEmpty()) {
                                                        if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, customer)) {
                                                            JOptionPane.showMessageDialog(null, "Sent!");
                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Message Failed!");
                                                        }
                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                            "Messenger", JOptionPane.WARNING_MESSAGE);
                                                }
                                            } else if (result == 1) {
                                                ArrayList<User> blockedUsers = messengerClient.loggedOnUser.getBlockedUsers();
                                                if (blockedUsers.contains(customer)) {
                                                    messengerClient.loggedOnUser.removeBlockedUser(writer, customer);
                                                } else {
                                                    messengerClient.loggedOnUser.addBlockedUser(writer, customer);
                                                }
                                            } else if (result == 2) {
                                                ArrayList<User> invisibleUsers = messengerClient.loggedOnUser.getInvisibleUsers();
                                                if (invisibleUsers.contains(customer)) {
                                                    messengerClient.loggedOnUser.removeInvisibleUser(writer, customer);
                                                } else {
                                                    messengerClient.loggedOnUser.addInvisibleUser(writer, customer);
                                                }
                                            }
                                        }
                                    });
                                    customersPanel.add(customerElement);
                                }

                                JScrollPane scrollPane = new JScrollPane();
                                scrollPane.setViewportView(customersPanel);
                                holder.add(scrollPane);

                                messengerClient.jFrame.setSize(400, Math.min(messengerClient.searchCustomers.size() * 60 + 40, 250));
                            } else {
                                messengerClient.jFrame.setSize(400, 100);
                                JLabel customerLabel = new JLabel("No Customers");
                                customerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                holder.add(customerLabel);
                            }

                            JButton backButton = new JButton("Back");
                            backButton.setSize(new Dimension(150, 30));
                            backButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.jFrame.setTitle("Messenger: Main");
                                    messengerClient.cardLayout.show(messengerClient.container, "Main");
                                }
                            });
                            holder.add(backButton, BorderLayout.SOUTH);

                            messengerClient.container.add("Search Customers", holder);
                            messengerClient.cardLayout.show(messengerClient.container, "Search Customers");
                        }
                    }
                }
            });
            searchCustomerPanel.add(searchCustomerButton);

            JPanel editAccountPanel = new JPanel();
            JButton editAccountButton = new JButton("Edit Account");
            editAccountButton.setPreferredSize(new Dimension(175, 30));
            editAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showEditAccountMenu(messengerClient, reader, writer);
                }
            });
            editAccountPanel.add(editAccountButton);

            JPanel logOutPanel = new JPanel();
            JButton logOutButton = new JButton("Log Out");
            logOutButton.setPreferredSize(new Dimension(175, 30));
            logOutButton.setBackground(new Color(235, 64, 52));
            logOutButton.setForeground(Color.black);
            logOutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exitApplication(messengerClient, writer);
                }
            });
            logOutPanel.add(logOutButton);

            holder.add(manageStoresPanel);
            holder.add(conversationsPanel);
            holder.add(listCustomersPanel);
            holder.add(searchCustomerPanel);
            holder.add(editAccountPanel);
            holder.add(logOutPanel);
        } else {
            JPanel conversationsPanel = new JPanel();
            JButton conversationsButton = new JButton("View Conversations");
            conversationsButton.setPreferredSize(new Dimension(175, 30));
            conversationsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Messenger: View Conversations");
                    messengerClient.conversations = refreshVisibleConversations(reader, writer, messengerClient.loggedOnUser);
                    JPanel holder = new JPanel(new BorderLayout());

                    JPanel conversationsPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                    if (messengerClient.conversations.size() > 0) {
                        for (int i = 0; i < messengerClient.conversations.size(); i++) {
                            final Conversation conversation = messengerClient.conversations.get(i);
                            String title = conversation.getConversationID();

                            JMenuItem conversationElement = new JMenuItem(title);
                            conversationElement.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    messengerClient.jFrame.setTitle(title);
                                    messengerClient.jFrame.setSize(600, 300);
                                    if (messengerClient.loggedOnUser instanceof Seller) {
                                        conversation.setSellerUnread(writer, false);
                                    } else {
                                        conversation.setCustomerUnread(writer, false);
                                    }
                                    JPanel holder = new JPanel(new BorderLayout());

                                    messengerClient.messages = refreshVisibleMessages(reader, writer, conversation,
                                            messengerClient.loggedOnUser);
                                    if (messengerClient.messages.size() > 0) {
                                        DefaultListModel messageModel = new DefaultListModel();
                                        for (int i = 0; i < messengerClient.messages.size(); i++) {
                                            String message = messengerClient.messages.get(i).getSender().getUsername()
                                                    + ": ";

                                            if (messengerClient.loggedOnUser.isRequestsCensorship()) {
                                                message += messengerClient.messages.get(i)
                                                        .getCensoredMessage(messengerClient.loggedOnUser);
                                            } else {
                                                message += messengerClient.messages.get(i).getMessage();
                                            }

                                            messageModel.addElement(message);
                                        }

                                        JList messageList = new JList(messageModel);
                                        messageList.addListSelectionListener(new ListSelectionListener() {
                                            @Override
                                            public void valueChanged(ListSelectionEvent e) {
                                                Message selectedMessage = messengerClient.messages.get(e.getLastIndex());

                                                Object[] userActions = {"Back", "Delete Message", "Edit Message"};

                                                JPanel holder = new JPanel(new GridLayout(4, 1));
                                                holder.add(new JPanel());

                                                JLabel messageLabel = new JLabel("Message Actions");
                                                messageLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                holder.add(messageLabel);

                                                holder.add(new JPanel());

                                                int result = JOptionPane.showOptionDialog(null, holder, "Message Actions",
                                                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                        null, userActions, null);

                                                if (result == 1) {
                                                    if (messengerClient.loggedOnUser.deleteMessage(reader, writer, selectedMessage, conversation)) {
                                                        JOptionPane.showMessageDialog(null, "Deleted Message!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Could Not Delete Message",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                } else if (result == 2) {
                                                    User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                            conversation.getCustomer() : conversation.getSeller();

                                                    if (selectedMessage.getSender().equals(messengerClient.loggedOnUser)) {
                                                        Object[] sendOptions = {"Cancel", "Edit & Send"};

                                                        JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                        sendHolder.add(new JPanel());

                                                        JLabel sendLabel = new JLabel("Edited Message:");
                                                        sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                        sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                        sendHolder.add(sendLabel);
                                                        JTextField sendTextField = new JTextField(20);
                                                        sendHolder.add(sendTextField);

                                                        sendHolder.add(new JPanel());

                                                        int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Edit a Message",
                                                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                                null, sendOptions, null);

                                                        if (sendResult == 1) {
                                                            String message = sendTextField.getText();
                                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                                if (message.isEmpty()) {
                                                                    JOptionPane.showMessageDialog(null, "Not Sent. Empty Message");
                                                                } else {
                                                                    if (messengerClient.loggedOnUser.editMessage(reader,
                                                                            writer, selectedMessage, conversation, message)) {
                                                                        JOptionPane.showMessageDialog(null, "Updated Message!");
                                                                    } else {
                                                                        JOptionPane.showMessageDialog(null, "Update Failed",
                                                                                "Messenger", JOptionPane.ERROR_MESSAGE);
                                                                    }
                                                                }
                                                            } else {
                                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                                            }
                                                        }
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "You cannot edit received messages!",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                }
                                            }
                                        });

                                        JScrollPane scrollPane = new JScrollPane();
                                        scrollPane.setViewportView(messageList);
                                        holder.add(scrollPane);
                                    } else {
                                        messengerClient.jFrame.setSize(400, 120);
                                        JLabel messageLabel = new JLabel("No Messages");
                                        messageLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                        holder.add(messageLabel);
                                    }

                                    JPanel buttonPanel = new JPanel();
                                    JButton backButton = new JButton("Back");
                                    backButton.setSize(new Dimension(150, 30));
                                    backButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            messengerClient.jFrame.setSize(400,
                                                    Math.min(messengerClient.conversations.size() * 60 + 40, 250));
                                            messengerClient.jFrame.setTitle("Messenger: View All Conversations");
                                            messengerClient.cardLayout.show(messengerClient.container, "List Conversations");
                                        }
                                    });
                                    buttonPanel.add(backButton);

                                    JButton exportButton = new JButton("Export to CSV");
                                    exportButton.setSize(new Dimension(150, 30));
                                    exportButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            try {
                                                convertConversationToCSV(reader, writer, conversation, messengerClient.loggedOnUser);
                                                JOptionPane.showMessageDialog(null, "Exported to src/exports!");
                                            } catch (IOException ex) {
                                                JOptionPane.showMessageDialog(null, "Could Not Convert", "Messenger", JOptionPane.ERROR_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(exportButton);

                                    JButton importButton = new JButton("Import a TXT");
                                    importButton.setSize(new Dimension(150, 30));
                                    importButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                    conversation.getCustomer() : conversation.getSeller();

                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                Object[] sendOptions = {"Cancel", "Send"};

                                                JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                sendHolder.add(new JPanel());

                                                JLabel sendLabel = new JLabel(".TXT File Source:");
                                                sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                sendHolder.add(sendLabel);
                                                JTextField sendTextField = new JTextField(20);
                                                sendHolder.add(sendTextField);

                                                sendHolder.add(new JPanel());

                                                int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Import a .TXT",
                                                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                        null, sendOptions, null);

                                                String filePath = sendTextField.getText();
                                                if (sendResult == 1 && !filePath.isEmpty()) {
                                                    if (conversation.importTXT(reader, writer, filePath,
                                                            messengerClient.loggedOnUser, recipient)) {
                                                        JOptionPane.showMessageDialog(null, "Sent!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Import Failed. Check the entered path.",
                                                                "Messenger", JOptionPane.WARNING_MESSAGE);
                                                    }
                                                }
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(importButton);

                                    JTextField sendTextField = new JTextField(20);
                                    buttonPanel.add(sendTextField);
                                    JButton sendButton = new JButton("Send");
                                    sendButton.setSize(new Dimension(150, 30));
                                    sendButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            User recipient = (messengerClient.loggedOnUser instanceof Seller) ?
                                                    conversation.getCustomer() : conversation.getSeller();

                                            if (!recipient.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                    !messengerClient.loggedOnUser.getBlockedUsers().contains(recipient)) {
                                                String message = sendTextField.getText().strip();
                                                if (message.isEmpty()) {
                                                    JOptionPane.showMessageDialog(null, "Not Sent. Empty Message");
                                                } else {
                                                    if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, recipient)) {
                                                        JOptionPane.showMessageDialog(null, "Sent!");
                                                    } else {
                                                        JOptionPane.showMessageDialog(null, "Message Failed",
                                                                "Messenger", JOptionPane.ERROR_MESSAGE);
                                                    }
                                                }
                                                sendTextField.setText("");
                                            } else {
                                                JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                        "Messenger", JOptionPane.WARNING_MESSAGE);
                                            }
                                        }
                                    });
                                    buttonPanel.add(sendButton);
                                    holder.add(buttonPanel, BorderLayout.SOUTH);

                                    messengerClient.container.add("Messages View", holder);
                                    messengerClient.cardLayout.show(messengerClient.container, "Messages View");
                                }
                            });
                            conversationsPanel.add(conversationElement);
                        }

                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setViewportView(conversationsPanel);
                        holder.add(scrollPane);

                        messengerClient.jFrame.setSize(400,
                                Math.min(messengerClient.conversations.size() * 60 + 40, 250));
                    } else {
                        messengerClient.jFrame.setSize(400, 100);
                        JLabel customerLabel = new JLabel("No Conversations");
                        customerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        customerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        holder.add(customerLabel);
                    }

                    JButton backButton = new JButton("Back");
                    backButton.setSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setSize(400, 300);
                            messengerClient.jFrame.setTitle("Messenger: Main");
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        }
                    });
                    holder.add(backButton, BorderLayout.SOUTH);

                    messengerClient.container.add("List Conversations", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "List Conversations");
                }
            });
            conversationsPanel.add(conversationsButton);

            JPanel listStoresPanel = new JPanel();
            JButton listStoresButton = new JButton("View All Stores");
            listStoresButton.setPreferredSize(new Dimension(175, 30));
            listStoresButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    messengerClient.jFrame.setTitle("Messenger: View All Stores");
                    messengerClient.stores = refreshStores(reader, writer, messengerClient.loggedOnUser);
                    JPanel holder = new JPanel(new BorderLayout());

                    JPanel storesPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                    if (messengerClient.stores.size() > 0) {
                        for (int i = 0; i < messengerClient.stores.size(); i++) {
                            final Store selectedStore = messengerClient.stores.get(i);
                            String title = String.format("%s by %s", selectedStore.getStoreName(), selectedStore.getSeller().getUsername());

                            JMenuItem storeElement = new JMenuItem(title);
                            storeElement.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    String blockedUserTitle = "Block";
                                    String invisibleUserTitle = "Become Invisible";
                                    if (messengerClient.loggedOnUser.getBlockedUsers().contains(selectedStore.getSeller())) {
                                        blockedUserTitle = "Unblock";
                                    }
                                    if (messengerClient.loggedOnUser.getInvisibleUsers().contains(selectedStore.getSeller())) {
                                        invisibleUserTitle = "Become Visible";
                                    }

                                    //TODO Update Store
                                    messengerClient.stores = refreshStores(reader, writer, messengerClient.loggedOnUser);
                                    messengerClient.loggedOnUser.sendMessageToUser(reader, writer,
                                            messengerClient.loggedOnUser.getUsername() +
                                                    " looked up the Store: " + selectedStore.getStoreName(),
                                            selectedStore.getSeller());

                                    Object[] userActions = {"Message", blockedUserTitle, invisibleUserTitle};

                                    JPanel holder = new JPanel(new GridLayout(4, 1));
                                    holder.add(new JPanel());
                                    JLabel storeLabel = new JLabel("Selected: " + title);
                                    storeLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                    storeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                    holder.add(storeLabel);
                                    holder.add(new JPanel());

                                    int result = JOptionPane.showOptionDialog(null, holder, "User Actions",
                                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                            null, userActions, null);
                                    if (result == 0) {
                                        //TODO Update Store
                                        messengerClient.stores = refreshStores(reader, writer, messengerClient.loggedOnUser);
                                        if (!selectedStore.getSeller().getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                !messengerClient.loggedOnUser.getBlockedUsers().contains(selectedStore.getSeller())) {
                                            Object[] sendOptions = {"Cancel", "Send"};

                                            JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                            sendHolder.add(new JPanel());

                                            JLabel sendLabel = new JLabel("Your Message:");
                                            sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                            sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                            sendHolder.add(sendLabel);
                                            JTextField sendTextField = new JTextField(20);
                                            sendHolder.add(sendTextField);

                                            sendHolder.add(new JPanel());

                                            int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Send a Message",
                                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                    null, sendOptions, null);

                                            String message = sendTextField.getText();
                                            if (sendResult == 1 && !message.isEmpty()) {
                                                if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, selectedStore.getSeller())) {
                                                    JOptionPane.showMessageDialog(null, "Sent!");
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "Message Failed!");
                                                }
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                    "Messenger", JOptionPane.WARNING_MESSAGE);
                                        }
                                    } else if (result == 1) {
                                        ArrayList<User> blockedUsers = messengerClient.loggedOnUser.getBlockedUsers();
                                        if (blockedUsers.contains(selectedStore.getSeller())) {
                                            messengerClient.loggedOnUser.removeBlockedUser(writer, selectedStore.getSeller());
                                        } else {
                                            messengerClient.loggedOnUser.addBlockedUser(writer, selectedStore.getSeller());
                                        }
                                    } else if (result == 2) {
                                        ArrayList<User> invisibleUsers = messengerClient.loggedOnUser.getInvisibleUsers();
                                        if (invisibleUsers.contains(selectedStore.getSeller())) {
                                            messengerClient.loggedOnUser.removeInvisibleUser(writer, selectedStore.getSeller());
                                        } else {
                                            messengerClient.loggedOnUser.addInvisibleUser(writer, selectedStore.getSeller());
                                        }
                                    }
                                    messengerClient.stores = refreshStores(reader, writer, messengerClient.loggedOnUser);
                                }
                            });
                            storesPanel.add(storeElement);
                        }

                        JScrollPane scrollPane = new JScrollPane();
                        scrollPane.setViewportView(storesPanel);
                        holder.add(scrollPane);

                        messengerClient.jFrame.setSize(400, Math.min(messengerClient.stores.size() * 60 + 40, 250));
                    } else {
                        messengerClient.jFrame.setSize(400, 100);
                        JLabel storeLabel = new JLabel("No Stores");
                        storeLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        storeLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        holder.add(storeLabel);
                    }

                    JButton backButton = new JButton("Back");
                    backButton.setSize(new Dimension(150, 30));
                    backButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            messengerClient.jFrame.setSize(400, 300);
                            messengerClient.jFrame.setTitle("Messenger: Main");
                            messengerClient.cardLayout.show(messengerClient.container, "Main");
                        }
                    });
                    holder.add(backButton, BorderLayout.SOUTH);

                    messengerClient.container.add("List Stores", holder);
                    messengerClient.cardLayout.show(messengerClient.container, "List Stores");
                }
            });
            listStoresPanel.add(listStoresButton);

            JPanel searchSellersPanel = new JPanel();
            JButton searchSellersButton = new JButton("Search for a Seller");
            searchSellersButton.setPreferredSize(new Dimension(175, 30));
            searchSellersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object[] searchOptions = {"Cancel", "Search"};

                    JPanel searchHolder = new JPanel(new GridLayout(0, 1));
                    searchHolder.add(new JPanel());

                    JLabel searchLabel = new JLabel("Search Keyword:");
                    searchLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    searchLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    searchHolder.add(searchLabel);
                    JTextField searchTextField = new JTextField(20);
                    searchHolder.add(searchTextField);

                    searchHolder.add(new JPanel());

                    int searchResult = JOptionPane.showOptionDialog(null, searchHolder, "Search Sellers",
                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                            null, searchOptions, null);

                    String searchKeyword = searchTextField.getText();
                    if (searchResult == 1) {
                        if (searchKeyword.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Cannot Leave Empty",
                                    "Messenger", JOptionPane.ERROR_MESSAGE);
                        } else {
                            messengerClient.jFrame.setTitle("Messenger: Seller Search Results");
                            messengerClient.searchSellers = refreshSearchSellers(reader, writer,
                                    messengerClient.loggedOnUser, searchKeyword);
                            JPanel holder = new JPanel(new BorderLayout());

                            JPanel sellersPanel = new JPanel(new GridLayout(0, 1, 0, 10));
                            if (messengerClient.searchSellers.size() > 0) {
                                for (int i = 0; i < messengerClient.searchSellers.size(); i++) {
                                    final Seller seller = messengerClient.searchSellers.get(i);
                                    String title = String.format("%s | %s", seller.getUsername(), seller.getEmail());

                                    JMenuItem sellerElement = new JMenuItem(title);
                                    sellerElement.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            String blockedUserTitle = "Block";
                                            String invisibleUserTitle = "Become Invisible";
                                            if (messengerClient.loggedOnUser.getBlockedUsers().contains(seller)) {
                                                blockedUserTitle = "Unblock";
                                            }
                                            if (messengerClient.loggedOnUser.getInvisibleUsers().contains(seller)) {
                                                invisibleUserTitle = "Become Visible";
                                            }

                                            Object[] userActions = {"Message", blockedUserTitle, invisibleUserTitle};

                                            JPanel holder = new JPanel(new GridLayout(4, 1));
                                            holder.add(new JPanel());

                                            JLabel sellerLabel = new JLabel("Selected: " + title);
                                            sellerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                            sellerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                            holder.add(sellerLabel);

                                            holder.add(new JPanel());

                                            int result = JOptionPane.showOptionDialog(null, holder, "User Actions",
                                                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                    null, userActions, null);
                                            if (result == 0) {
                                                //TODO Update Customers
                                                messengerClient.searchSellers = refreshSearchSellers(reader, writer, messengerClient.loggedOnUser, searchKeyword);
                                                if (!seller.getBlockedUsers().contains(messengerClient.loggedOnUser) &&
                                                        !messengerClient.loggedOnUser.getBlockedUsers().contains(seller)) {
                                                    Object[] sendOptions = {"Cancel", "Send"};

                                                    JPanel sendHolder = new JPanel(new GridLayout(0, 1));
                                                    sendHolder.add(new JPanel());

                                                    JLabel sendLabel = new JLabel("Your Message:");
                                                    sendLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                                    sendLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                                    sendHolder.add(sendLabel);
                                                    JTextField sendTextField = new JTextField(20);
                                                    sendHolder.add(sendTextField);

                                                    sendHolder.add(new JPanel());

                                                    int sendResult = JOptionPane.showOptionDialog(null, sendHolder, "Send a Message",
                                                            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                                            null, sendOptions, null);

                                                    String message = sendTextField.getText();
                                                    if (sendResult == 1 && !message.isEmpty()) {
                                                        if (messengerClient.loggedOnUser.sendMessageToUser(reader, writer, message, seller)) {
                                                            JOptionPane.showMessageDialog(null, "Sent!");
                                                        } else {
                                                            JOptionPane.showMessageDialog(null, "Message Failed!");
                                                        }
                                                    }
                                                } else {
                                                    JOptionPane.showMessageDialog(null, "You cannot message this user!",
                                                            "Messenger", JOptionPane.WARNING_MESSAGE);
                                                }
                                            } else if (result == 1) {
                                                ArrayList<User> blockedUsers = messengerClient.loggedOnUser.getBlockedUsers();
                                                if (blockedUsers.contains(seller)) {
                                                    messengerClient.loggedOnUser.removeBlockedUser(writer, seller);
                                                } else {
                                                    messengerClient.loggedOnUser.addBlockedUser(writer, seller);
                                                }
                                            } else if (result == 2) {
                                                ArrayList<User> invisibleUsers = messengerClient.loggedOnUser.getInvisibleUsers();
                                                if (invisibleUsers.contains(seller)) {
                                                    messengerClient.loggedOnUser.removeInvisibleUser(writer, seller);
                                                } else {
                                                    messengerClient.loggedOnUser.addInvisibleUser(writer, seller);
                                                }
                                            }
                                        }
                                    });
                                    sellersPanel.add(sellerElement);
                                }

                                JScrollPane scrollPane = new JScrollPane();
                                scrollPane.setViewportView(sellersPanel);
                                holder.add(scrollPane);

                                messengerClient.jFrame.setSize(400, Math.min(messengerClient.searchSellers.size() * 60 + 40, 250));
                            } else {
                                messengerClient.jFrame.setSize(400, 100);
                                JLabel sellerLabel = new JLabel("No Sellers");
                                sellerLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                sellerLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                holder.add(sellerLabel);
                            }

                            JButton backButton = new JButton("Back");
                            backButton.setSize(new Dimension(150, 30));
                            backButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.jFrame.setTitle("Messenger: Main");
                                    messengerClient.cardLayout.show(messengerClient.container, "Main");
                                }
                            });
                            holder.add(backButton, BorderLayout.SOUTH);

                            messengerClient.container.add("Search Sellers", holder);
                            messengerClient.cardLayout.show(messengerClient.container, "Search Sellers");
                        }
                    }
                }
            });
            searchSellersPanel.add(searchSellersButton);

            JPanel editAccountPanel = new JPanel();
            JButton editAccountButton = new JButton("Edit Account");
            editAccountButton.setPreferredSize(new Dimension(175, 30));
            editAccountButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showEditAccountMenu(messengerClient, reader, writer);
                }
            });
            editAccountPanel.add(editAccountButton);

            JPanel logOutPanel = new JPanel();
            JButton logOutButton = new JButton("Log Out");
            logOutButton.setPreferredSize(new Dimension(175, 30));
            logOutButton.setBackground(new Color(235, 64, 52));
            logOutButton.setForeground(Color.black);
            logOutButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    exitApplication(messengerClient, writer);
                }
            });
            logOutPanel.add(logOutButton);

            holder.add(conversationsPanel);
            holder.add(listStoresPanel);
            holder.add(searchSellersPanel);
            holder.add(editAccountPanel);
            holder.add(logOutPanel);
        }

        messengerClient.container.add("Main", holder);
        messengerClient.cardLayout.show(messengerClient.container, "Main");
    }

    public static void showEditAccountMenu(MessengerClient messengerClient, BufferedReader reader, PrintWriter writer) {
        messengerClient.jFrame.setTitle("Messenger: Account");
        messengerClient.jFrame.setSize(400, 300);
        JPanel holder = new JPanel(new GridLayout(0, 1));

        JPanel changeUsernamePanel = new JPanel();
        JButton changeUsernameButton = new JButton("Change Username");
        changeUsernameButton.setPreferredSize(new Dimension(200, 30));
        changeUsernameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] usernameOptions = {"Cancel", "Change"};

                JPanel usernameHolder = new JPanel(new GridLayout(0, 1));
                usernameHolder.add(new JPanel());

                JLabel usernameLabel = new JLabel("New Username:");
                usernameLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
                usernameHolder.add(usernameLabel);
                JTextField usernameTextField = new JTextField(20);
                usernameHolder.add(usernameTextField);

                usernameHolder.add(new JPanel());

                int usernameResult = JOptionPane.showOptionDialog(null, usernameHolder, "Change Username",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, usernameOptions, null);

                if (usernameResult == 1) {
                    String username = usernameTextField.getText();
                    if (username.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form",
                                "Messenger", JOptionPane.ERROR_MESSAGE);
                    } else {
                        String checkUsernameRequest = "[CHECK.USERNAME]" + username;
                        sendRequest(writer, checkUsernameRequest);
                        boolean isUsernameTaken = Boolean.parseBoolean(readResponse(reader));

                        if (isUsernameTaken) {
                            JOptionPane.showMessageDialog(null, "Username already Taken",
                                    "Messenger", JOptionPane.ERROR_MESSAGE);
                        } else {
                            messengerClient.loggedOnUser.setUsername(reader, writer, username);
                        }
                    }
                }
            }
        });
        changeUsernamePanel.add(changeUsernameButton);

        JPanel changePasswordPanel = new JPanel();
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(200, 30));
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object[] passwordOptions = {"Cancel", "Change"};

                JPanel passwordHolder = new JPanel(new GridLayout(3, 1));

                JLabel passwordLabel = new JLabel("Current Password:");
                passwordLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
                passwordHolder.add(passwordLabel);
                JPasswordField passwordTextField = new JPasswordField(20);
                passwordHolder.add(passwordTextField);

                JLabel newPasswordLabel = new JLabel("New Password:");
                newPasswordLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                newPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
                passwordHolder.add(newPasswordLabel);
                JPasswordField newPasswordTextField = new JPasswordField(20);
                passwordHolder.add(newPasswordTextField);

                JLabel confirmNewPasswordLabel = new JLabel("Confirm New Password:");
                confirmNewPasswordLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                confirmNewPasswordLabel.setHorizontalAlignment(SwingConstants.CENTER);
                passwordHolder.add(confirmNewPasswordLabel);
                JPasswordField confirmNewPasswordTextField = new JPasswordField(20);
                passwordHolder.add(confirmNewPasswordTextField);

                int passwordResult = JOptionPane.showOptionDialog(null, passwordHolder, "Change Password",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                        null, passwordOptions, null);

                String currentPassword = String.valueOf(passwordTextField.getPassword());
                String newPassword = String.valueOf(newPasswordTextField.getPassword());
                String confirmNewPassword = String.valueOf(confirmNewPasswordTextField.getPassword());

                if (passwordResult == 1) {

                    if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form",
                                "Messenger", JOptionPane.ERROR_MESSAGE);
                    } else {
                        if (!currentPassword.equals(messengerClient.loggedOnUser.getPassword())) {
                            JOptionPane.showMessageDialog(null, "Incorrect Password",
                                    "Messenger", JOptionPane.ERROR_MESSAGE);
                        } else if (!newPassword.equals(confirmNewPassword)) {
                            JOptionPane.showMessageDialog(null, "New Passwords Did Not Match",
                                    "Messenger", JOptionPane.ERROR_MESSAGE);
                        } else {
                            messengerClient.loggedOnUser.setPassword(reader, writer, confirmNewPassword);
                        }
                    }
                }
            }
        });
        changePasswordPanel.add(changePasswordButton);

        JPanel manageBlockedUsersPanel = new JPanel();
        JButton manageBlockedUsersButton = new JButton("Manage Blocked Users");
        manageBlockedUsersButton.setPreferredSize(new Dimension(200, 30));
        manageBlockedUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messengerClient.jFrame.setTitle("Manage Blocked Users");
                JPanel holder = new JPanel(new BorderLayout());
                JPanel userPanel = new JPanel(new GridLayout(0, 1, 0, 10));

                if (messengerClient.loggedOnUser.getBlockedUsers().size() > 0) {
                    for (int i = 0; i < messengerClient.loggedOnUser.getBlockedUsers().size(); i++) {
                        final User selectedUser = messengerClient.loggedOnUser.getBlockedUsers().get(i);
                        String title = String.format("%s | %s", selectedUser.getUsername(), selectedUser.getEmail());

                        JMenuItem userElement = new JMenuItem(title);
                        userElement.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Object[] userActions = {"Cancel", "Unblock"};

                                JPanel holder = new JPanel(new GridLayout(4, 1));
                                holder.add(new JPanel());

                                JLabel userLabel = new JLabel("Selected: " + title);
                                userLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                userLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                holder.add(userLabel);

                                holder.add(new JPanel());

                                int result = JOptionPane.showOptionDialog(null, holder, "Unblock User",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                        null, userActions, null);
                                if (result == 1) {
                                    messengerClient.loggedOnUser.removeBlockedUser(writer, selectedUser);
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                                }
                            }
                        });
                        userPanel.add(userElement);
                    }

                    JScrollPane scrollPane = new JScrollPane();
                    scrollPane.setViewportView(userPanel);
                    holder.add(scrollPane);

                    messengerClient.jFrame.setSize(400,
                            Math.min(messengerClient.loggedOnUser.getBlockedUsers().size() * 60 + 40, 250));
                } else {
                    messengerClient.jFrame.setSize(400, 100);
                    JLabel blockedLabel = new JLabel("No Blocked Users");
                    blockedLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    blockedLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    holder.add(blockedLabel);
                }

                JButton backButton = new JButton("Back");
                backButton.setSize(new Dimension(150, 30));
                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        messengerClient.jFrame.setSize(400, 300);
                        messengerClient.jFrame.setTitle("Messenger: Account");
                        messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                    }
                });
                holder.add(backButton, BorderLayout.SOUTH);

                messengerClient.container.add("Manage Blocked Users", holder);
                messengerClient.cardLayout.show(messengerClient.container, "Manage Blocked Users");
            }
        });
        manageBlockedUsersPanel.add(manageBlockedUsersButton);

        JPanel manageInvisibleUsersPanel = new JPanel();
        JButton manageInvisibleUsersButton = new JButton("Manage Invisible to Users");
        manageInvisibleUsersButton.setPreferredSize(new Dimension(200, 30));
        manageInvisibleUsersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messengerClient.jFrame.setTitle("Manage Invisible to Users");
                JPanel holder = new JPanel(new BorderLayout());
                JPanel userPanel = new JPanel(new GridLayout(0, 1, 0, 10));

                if (messengerClient.loggedOnUser.getInvisibleUsers().size() > 0) {
                    for (int i = 0; i < messengerClient.loggedOnUser.getInvisibleUsers().size(); i++) {
                        final User selectedUser = messengerClient.loggedOnUser.getInvisibleUsers().get(i);
                        String title = String.format("%s | %s", selectedUser.getUsername(), selectedUser.getEmail());

                        JMenuItem userElement = new JMenuItem(title);
                        userElement.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Object[] userActions = {"Cancel", "Become Visible"};

                                JPanel holder = new JPanel(new GridLayout(4, 1));
                                holder.add(new JPanel());

                                JLabel userLabel = new JLabel("Selected: " + title);
                                userLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                userLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                holder.add(userLabel);

                                holder.add(new JPanel());

                                int result = JOptionPane.showOptionDialog(null, holder, "Become Visible to User",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                        null, userActions, null);
                                if (result == 1) {
                                    messengerClient.loggedOnUser.removeInvisibleUser(writer, selectedUser);
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                                }
                            }
                        });
                        userPanel.add(userElement);
                    }

                    JScrollPane scrollPane = new JScrollPane();
                    scrollPane.setViewportView(userPanel);
                    holder.add(scrollPane);

                    messengerClient.jFrame.setSize(400,
                            Math.min(messengerClient.loggedOnUser.getInvisibleUsers().size() * 60 + 40, 250));
                } else {
                    messengerClient.jFrame.setSize(400, 100);
                    JLabel invisibleLabel = new JLabel("No Invisible to Users");
                    invisibleLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    invisibleLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    holder.add(invisibleLabel);
                }

                JButton backButton = new JButton("Back");
                backButton.setSize(new Dimension(150, 30));
                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        messengerClient.jFrame.setSize(400, 300);
                        messengerClient.jFrame.setTitle("Messenger: Account");
                        messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                    }
                });
                holder.add(backButton, BorderLayout.SOUTH);

                messengerClient.container.add("Manage Invisible to Users", holder);
                messengerClient.cardLayout.show(messengerClient.container, "Manage Invisible to Users");
            }
        });
        manageInvisibleUsersPanel.add(manageInvisibleUsersButton);

        JPanel censoringPanel = new JPanel();
        JButton censoringButton = new JButton("Edit Censoring");
        censoringButton.setPreferredSize(new Dimension(200, 30));
        censoringButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messengerClient.jFrame.setTitle("Censoring Options");
                JPanel holder = new JPanel(new BorderLayout());
                JPanel censorPanel = new JPanel(new GridLayout(0, 1, 0, 10));

                if (messengerClient.loggedOnUser.getCensoredWords().size() > 0) {
                    for (int i = 0; i < messengerClient.loggedOnUser.getCensoredWords().size(); i++) {
                        final int index = i;
                        final String selectedCensorPair = messengerClient.loggedOnUser.getCensoredWords().get(i);
                        String title = String.format("%s >>>> %s",
                                selectedCensorPair.split(":")[0], selectedCensorPair.split(":")[1]);

                        JMenuItem censorElement = new JMenuItem(title);
                        censorElement.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                Object[] censorActions = {"Cancel", "Delete Censor Pair"};

                                JPanel holder = new JPanel(new GridLayout(4, 1));
                                holder.add(new JPanel());

                                JLabel censorLabel = new JLabel("Selected: " + title);
                                censorLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                                censorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                holder.add(censorLabel);

                                holder.add(new JPanel());

                                int result = JOptionPane.showOptionDialog(null, holder, "Delete Censor Pair",
                                        JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE,
                                        null, censorActions, null);
                                if (result == 1) {
                                    messengerClient.loggedOnUser.removeCensoredWord(writer, index, selectedCensorPair);
                                    messengerClient.jFrame.setSize(400, 300);
                                    messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                                }
                            }
                        });
                        censorPanel.add(censorElement);
                    }

                    JScrollPane scrollPane = new JScrollPane();
                    scrollPane.setViewportView(censorPanel);
                    holder.add(scrollPane);

                    messengerClient.jFrame.setSize(400,
                            Math.min(messengerClient.loggedOnUser.getCensoredWords().size() * 60 + 40, 250));
                } else {
                    messengerClient.jFrame.setSize(400, 120);
                    JLabel censorPairLabel = new JLabel("No Censored Words");
                    censorPairLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                    censorPairLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    holder.add(censorPairLabel);
                }

                JPanel buttonPanel = new JPanel();
                JButton backButton = new JButton("Back");
                backButton.setSize(new Dimension(150, 30));
                backButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        messengerClient.jFrame.setSize(400, 300);
                        messengerClient.jFrame.setTitle("Messenger: Account");
                        messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                    }
                });
                buttonPanel.add(backButton);

                JButton addButton = new JButton("Add");
                addButton.setSize(new Dimension(150, 30));
                addButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        Object[] censorOptions = {"Cancel", "Add"};

                        JPanel censorHolder = new JPanel(new GridLayout(2, 1));

                        JLabel censorLabel = new JLabel("Censored Word:");
                        censorLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        censorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        censorHolder.add(censorLabel);
                        JTextField censorTextField = new JTextField(20);
                        censorHolder.add(censorTextField);

                        JLabel replacementLabel = new JLabel("Replacement:");
                        replacementLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                        replacementLabel.setHorizontalAlignment(SwingConstants.CENTER);
                        censorHolder.add(replacementLabel);
                        JTextField replacementTextField = new JTextField(20);
                        replacementTextField.setText("****");
                        censorHolder.add(replacementTextField);

                        int censorResult = JOptionPane.showOptionDialog(null, censorHolder, "Add Censor Pair",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, censorOptions, null);

                        String censoredWord = censorTextField.getText();
                        String replacement = replacementTextField.getText();
                        if (censorResult == 1) {
                            if (censoredWord.isEmpty() || replacement.isEmpty()) {
                                JOptionPane.showMessageDialog(null, "Cannot Submit Incomplete Form",
                                        "Messenger", JOptionPane.ERROR_MESSAGE);
                            } else {
                                messengerClient.loggedOnUser.addCensoredWord(writer, censoredWord + ":"
                                        + replacement);
                            }
                        }

                        messengerClient.jFrame.setSize(400, 300);
                        messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
                    }
                });
                buttonPanel.add(addButton);
                holder.add(buttonPanel, BorderLayout.SOUTH);

                JButton onoffButton = new JButton("On / Off");
                onoffButton.setSize(new Dimension(150, 30));
                onoffButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent se) {
                        messengerClient.loggedOnUser.toggleRequestsCensorship(writer);

                        String message = String.format("Censoring Switched %s!",
                                (messengerClient.loggedOnUser.isRequestsCensorship()) ? "ON" : "OFF");
                        JOptionPane.showMessageDialog(null, message);
                    }
                });
                buttonPanel.add(onoffButton);

                messengerClient.container.add("Censored Words", holder);
                messengerClient.cardLayout.show(messengerClient.container, "Censored Words");
            }
        });
        censoringPanel.add(censoringButton);

        JPanel deleteAccountPanel = new JPanel();
        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setPreferredSize(new Dimension(200, 30));
        deleteAccountButton.setBackground(new Color(235, 64, 52));
        deleteAccountButton.setForeground(Color.black);
        deleteAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel deleteHolder = new JPanel(new GridLayout(4, 1));
                deleteHolder.add(new JPanel());

                JLabel deleteLabel = new JLabel("Are you sure you want to delete this account?");
                deleteLabel.setFont(new Font("Open Sans", Font.PLAIN, 14));
                deleteLabel.setHorizontalAlignment(SwingConstants.CENTER);
                deleteHolder.add(deleteLabel);

                deleteHolder.add(new JPanel());
                String[] deleteOptions = {"Cancel", "Delete"};
                int result = JOptionPane.showOptionDialog(null, deleteHolder, "Delete Account",
                        JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                        null, deleteOptions, null);

                if (result == 1) {
                    String request = "[DELETE]" + ((messengerClient.loggedOnUser instanceof Seller) ?
                            ((Seller) messengerClient.loggedOnUser).detailedToString() :
                            ((Customer) messengerClient.loggedOnUser).detailedToString());
                    sendRequest(writer, request);
                    exitApplication(messengerClient, writer);
                }
            }
        });
        deleteAccountPanel.add(deleteAccountButton);

        JPanel backPanel = new JPanel();
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(200, 30));
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                messengerClient.jFrame.setTitle("Messenger: Main");
                messengerClient.cardLayout.show(messengerClient.container, "Main");
            }
        });
        backPanel.add(backButton);

        holder.add(changeUsernamePanel);
        holder.add(changePasswordPanel);
        holder.add(manageBlockedUsersPanel);
        holder.add(manageInvisibleUsersPanel);
        holder.add(censoringPanel);
        holder.add(deleteAccountPanel);
        holder.add(backPanel);

        messengerClient.container.add("Edit Account", holder);
        messengerClient.cardLayout.show(messengerClient.container, "Edit Account");
    }
}