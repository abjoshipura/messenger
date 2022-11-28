import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Server class running multiple threads (implements Runnable). Allows multiple clients to connect simultaneously while
 * accessing and modifying shared data independent of the client's location.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */
public class MessengerServer implements Runnable {

    /**
     * The constant password/account file path: passwords.txt
     */
    private static final String PASSWORD_FILE_PATH = "passwords.txt";

    /**
     * The constant conversations file path: conversations.txt
     */
    private static final String CONVERSATIONS_FILE_PATH = "conversations.txt";

    /**
     * The constant Socket connection with a client for a MessengerServer thread
     */
    private final Socket clientSocket;

    /**
     * Constructs a thread of the MessengerServer class
     *
     * @param clientSocket The Socket connection with a client
     */
    public MessengerServer(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    /**
     * Sends a network response to a client's network request
     *
     * @param writer   The PrintWriter object to be used to send the network response
     * @param response The response sent to the client
     */
    public static void sendResponse(PrintWriter writer, String response) {
        writer.write(response);
        writer.println();
        writer.flush();
    }

    /**
     * Parses a network request to fetch the request header
     *
     * @param request The network request received from a client
     * @return Returns the request header
     */
    public static String getRequestHeader(String request) {
        return request.substring(request.indexOf("[") + 1, request.indexOf("]"));
    }

    /**
     * Parses a network request to fetch the request body
     *
     * @param request The network request received from a client
     * @return Returns the request body
     */
    public static String getRequestBody(String request) {
        return request.substring(request.indexOf("]") + 1);
    }

    /**
     * Parses the request body of a FILE.UPDATE request to call
     * {@link AccountsMaster#replaceStringInFile(String filePath, String oldString, String newString)}
     *
     * @param requestBody The body of the request
     */
    private void updateFile(String requestBody) {
        AccountsMaster accountsMaster = new AccountsMaster(PASSWORD_FILE_PATH, CONVERSATIONS_FILE_PATH);
        String[] spiltRequest = requestBody.split(";");

        String filePath = spiltRequest[0];
        String oldUserString = spiltRequest[1];
        String newUserString = spiltRequest[2];

        accountsMaster.replaceStringInFile(filePath, oldUserString, newUserString);
    }

    /**
     * Parses the request body of a FILE.APPEND request to call
     * {@link Conversation#appendToFile(String message, User sender, User recipient)}
     *
     * @param writer      The PrintWriter object to be used to send the network response
     * @param requestBody The body of the request
     */
    private void appendToFile(PrintWriter writer, String requestBody) {
        String[] splitRequest = requestBody.split(";");

        Conversation conversation = new Conversation(splitRequest[0]);
        String message = splitRequest[1];
        User sender = new User(splitRequest[2], false);
        User recipient = new User(splitRequest[3], false);

        sendResponse(writer, Boolean.toString(conversation.appendToFile(message, sender, recipient)));
    }

    /**
     * Parses the request body of a FILE.REWRITE request to call
     * {@link Conversation#writeFile(ArrayList messages)}
     *
     * @param writer      The PrintWriter object to be used to send the network response
     * @param requestBody The body of the request
     */
    private void rewriteFile(PrintWriter writer, String requestBody) {
        String[] splitRequest = requestBody.split(";");

        Conversation conversation = new Conversation(splitRequest[0]);
        if (!requestBody.isEmpty()) {
            ArrayList<Message> messages = new ArrayList<>();
            for (int i = 1; i < splitRequest.length; i++) {
                messages.add(new Message(splitRequest[i]));
            }

            sendResponse(writer, Boolean.toString(conversation.writeFile(messages)));
        }
    }

    /**
     * Parses the request body of a CHECK.USERNAME request to call
     * {@link AccountsMaster#usernameAlreadyTaken(String username)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void checkUsername(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        sendResponse(writer, Boolean.toString(accountsMaster.usernameAlreadyTaken(requestBody)));
    }

    /**
     * Parses the request body of a CHECK.EMAIL request to call
     * {@link AccountsMaster#emailAlreadyRegistered(String email)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void checkEmail(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        sendResponse(writer, Boolean.toString(accountsMaster.emailAlreadyRegistered(requestBody)));
    }

    /**
     * Parses the request body of a CHECK.RECIPIENT_ACTIVE request to call
     * {@link AccountsMaster#sellerArrayList} and {@link AccountsMaster#customerArrayList}
     *
     * @param writer      The PrintWriter object to be used to send the network response
     * @param requestBody The body of the request
     */
    private void checkRecipientActive(PrintWriter writer, String requestBody) {
        User recipient = new User(requestBody, false);
        boolean isRecipientActive = AccountsMaster.sellerArrayList.contains(recipient) ||
                AccountsMaster.customerArrayList.contains(recipient);
        sendResponse(writer, Boolean.toString(isRecipientActive));
    }

    /**
     * Parses the request body of a CREATE.USER request to call
     * {@link AccountsMaster#createAccount(String username, String email, String password, String role)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void createUser(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        String[] newAccountDetails = requestBody.split(", ");

        String username = newAccountDetails[0];
        String email = newAccountDetails[1];
        String password = newAccountDetails[2];
        String role = newAccountDetails[3];

        User newUser = accountsMaster.createAccount(username, email, password, role);
        if (newUser instanceof Seller) {
            sendResponse(writer, ((Seller) newUser).detailedToString());
        } else {
            sendResponse(writer, ((Customer) newUser).detailedToString());
        }
    }

    /**
     * Parses the request body of a CREATE.CONVERSATION request to call
     * {@link AccountsMaster#createConversation(Seller seller, Customer cutsomer)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void createConversation(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        String[] splitRequest = requestBody.split(";");
        Customer customer = new Customer(splitRequest[0], true);
        Seller seller = new Seller(splitRequest[1], true, true);

        Conversation conversation = accountsMaster.createConversation(seller, customer);
        sendResponse(writer, conversation.toString());
    }

    /**
     * Parses the request body of a FETCH.USER request to call
     * {@link AccountsMaster#fetchAccount(String usernameOrEmail)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void fetchUser(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        User newUser = accountsMaster.fetchAccount(requestBody);
        if (newUser instanceof Seller) {
            sendResponse(writer, ((Seller) newUser).detailedToString());
        } else {
            sendResponse(writer, ((Customer) newUser).detailedToString());
        }
    }

    /**
     * Parses the request body of a FETCH.UNREAD request to call
     * {@link AccountsMaster#numUnreadConversations(User user)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     * @param isSeller       Whether the request is related to a Seller
     */
    private void fetchUnread(PrintWriter writer, AccountsMaster accountsMaster, String requestBody, boolean isSeller) {
        int numUnreadConversations;
        if (isSeller) {
            Seller loggedOnSeller = new Seller(requestBody, true, true);
            numUnreadConversations = accountsMaster.numUnreadConversations(loggedOnSeller);
        } else {
            Customer loggedOnCustomer = new Customer(requestBody, true);
            numUnreadConversations = accountsMaster.numUnreadConversations(loggedOnCustomer);
        }
        sendResponse(writer, Integer.toString(numUnreadConversations));
    }

    /**
     * Parses the request body of a FETCH.CONVERSATION request to call
     * {@link AccountsMaster#fetchConversation(Seller seller, Customer customer)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void fetchConversation(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        String[] splitRequest = requestBody.split(";");
        Customer customer = new Customer(splitRequest[0], true);
        Seller seller = new Seller(splitRequest[1], true, true);

        Conversation conversation = accountsMaster.fetchConversation(seller, customer);
        if (conversation != null) {
            sendResponse(writer, conversation.toString());
        } else {
            sendResponse(writer, "");
        }
    }

    /**
     * Parses the request body of a LIST.VISIBLE_MESSAGES request to call
     * {@link Conversation#readFileAsPerUser(User user)}
     *
     * @param writer      The PrintWriter object to be used to send the network response
     * @param requestBody The body of the request
     */
    private void listVisibleMessages(PrintWriter writer, String requestBody) {
        String[] splitRequest = requestBody.split(";");
        Conversation conversation = new Conversation(splitRequest[0]);
        User loggedOnUser = new User(splitRequest[1], false);

        ArrayList<Message> messages = conversation.readFileAsPerUser(loggedOnUser);

        StringBuilder response = new StringBuilder();
        for (Message message : messages) {
            response.append(message.toString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a LIST.VISIBLE_CONVERSATIONS request to call
     * {@link AccountsMaster#listVisibleConversations(User user)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     * @param isSeller       Whether the request is related to a Seller
     */
    private void listVisibleConversations(PrintWriter writer, AccountsMaster accountsMaster, String requestBody,
                                          boolean isSeller) {
        ArrayList<Conversation> conversations;
        if (isSeller) {
            Seller loggedOnSeller = new Seller(requestBody, true, true);
            conversations = accountsMaster.listVisibleConversations(loggedOnSeller);
        } else {
            Customer loggedOnCustomer = new Customer(requestBody, true);
            conversations = accountsMaster.listVisibleConversations(loggedOnCustomer);
        }

        StringBuilder response = new StringBuilder();
        for (Conversation conversation : conversations) {
            response.append(conversation.toString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a LIST.VISIBLE_STORES request to call
     * {@link AccountsMaster#listStores(Customer customer)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void listVisibleStores(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        Customer loggedOnCustomer = new Customer(requestBody, true);
        ArrayList<Store> stores = accountsMaster.listStores(loggedOnCustomer);

        StringBuilder response = new StringBuilder();
        for (Store store : stores) {
            response.append(store.toString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a LIST.VISIBLE_CUSTOMERS request to call
     * {@link AccountsMaster#listCustomers(Seller seller)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void listVisibleCustomers(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        Seller loggedOnSeller = new Seller(requestBody, true, true);
        ArrayList<Customer> customers = accountsMaster.listCustomers(loggedOnSeller);

        StringBuilder response = new StringBuilder();
        for (Customer customer : customers) {
            response.append(customer.detailedToString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a LIST.MESSAGES request to call
     * {@link Conversation#readFile()}
     *
     * @param writer      The PrintWriter object to be used to send the network response
     * @param requestBody The body of the request
     */
    private void listAllMessages(PrintWriter writer, String requestBody) {
        Conversation conversation = new Conversation(requestBody);
        ArrayList<Message> messages = conversation.readFile();

        StringBuilder response = new StringBuilder();
        for (Message message : messages) {
            response.append(message.toString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a LIST.CONVERSATIONS request to call
     * {@link AccountsMaster#getConversationArrayList()}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     */
    private void listAllConversations(PrintWriter writer, AccountsMaster accountsMaster) {
        ArrayList<Conversation> conversations = accountsMaster.getConversationArrayList();
        StringBuilder response = new StringBuilder();
        for (Conversation conversation : conversations) {
            response.append(conversation.toString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a SEARCH.SELLER request to call
     * {@link AccountsMaster#fetchSellers(String searchKeyword, Customer customer)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void searchSeller(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        String[] splitRequest = requestBody.split(";");
        String searchKeyword = splitRequest[0];
        Customer loggedOnCustomer = new Customer(splitRequest[1], true);

        ArrayList<Seller> sellers = accountsMaster.fetchSellers(searchKeyword, loggedOnCustomer);

        StringBuilder response = new StringBuilder();
        for (Seller seller : sellers) {
            response.append(seller.detailedToString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Parses the request body of a SEARCH.CUSTOMER request to call
     * {@link AccountsMaster#fetchCustomers(String searchKeyword, Seller seller)}
     *
     * @param writer         The PrintWriter object to be used to send the network response
     * @param accountsMaster The AccountsMaster object to be used to process the request
     * @param requestBody    The body of the request
     */
    private void searchCustomer(PrintWriter writer, AccountsMaster accountsMaster, String requestBody) {
        String[] splitRequest = requestBody.split(";");
        String searchKeyword = splitRequest[0];
        Seller loggedOnSeller = new Seller(splitRequest[1], true, true);

        ArrayList<Customer> customers = accountsMaster.fetchCustomers(searchKeyword, loggedOnSeller);

        StringBuilder response = new StringBuilder();
        for (Customer customer : customers) {
            response.append(customer.detailedToString()).append(";");
        }
        if (response.length() > 0) {
            response = new StringBuilder(response.substring(0, response.length() - 1));
        }
        sendResponse(writer, response.toString());
    }

    /**
     * Runs the interface between a thread of the server and a connected client.
     */
    public void run() {
        AccountsMaster accountsMaster = new AccountsMaster(PASSWORD_FILE_PATH, CONVERSATIONS_FILE_PATH);

        try {
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            while (true) {
                String request = reader.readLine();
                String requestHeader = getRequestHeader(request);
                String requestBody = getRequestBody(request);

                boolean isSeller = false;
                if (requestBody.contains("<")) {
                    isSeller = requestBody.substring(0, requestBody.indexOf("<")).equals("Seller");
                }

                if (!requestHeader.isEmpty()) {
                    if (requestHeader.equals("LOGOUT")) {
                        break;
                    } else if (requestHeader.equals("DELETE")) {
                        if (isSeller) {
                            Seller loggedOnSeller = new Seller(requestBody, true, true);
                            accountsMaster.deleteAccount(loggedOnSeller);
                        } else {
                            Customer loggedOnCustomer = new Customer(requestBody, true);
                            accountsMaster.deleteAccount(loggedOnCustomer);
                        }
                    } else {
                        String[] requestHeaderTags = requestHeader.split("\\.");
                        String primaryTag = requestHeaderTags[0];
                        switch (primaryTag) {
                            case "FILE":
                                switch (requestHeaderTags[1]) {
                                    case "UPDATE" -> updateFile(requestBody);
                                    case "APPEND" -> appendToFile(writer, requestBody);
                                    case "REWRITE" -> rewriteFile(writer, requestBody);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            case "CHECK":
                                switch (requestHeaderTags[1]) {
                                    case "USERNAME" -> checkUsername(writer, accountsMaster, requestBody);
                                    case "EMAIL" -> checkEmail(writer, accountsMaster, requestBody);
                                    case "RECIPIENT_ACTIVE" -> checkRecipientActive(writer, requestBody);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            case "CREATE":
                                switch (requestHeaderTags[1]) {
                                    case "USER" -> createUser(writer, accountsMaster, requestBody);
                                    case "CONVERSATION" -> createConversation(writer, accountsMaster, requestBody);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            case "FETCH":
                                switch (requestHeaderTags[1]) {
                                    case "USER" -> fetchUser(writer, accountsMaster, requestBody);
                                    case "UNREAD" -> fetchUnread(writer, accountsMaster, requestBody, isSeller);
                                    case "CONVERSATION" -> fetchConversation(writer, accountsMaster, requestBody);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            case "LIST":
                                switch (requestHeaderTags[1]) {
                                    case "VISIBLE_MESSAGES" -> listVisibleMessages(writer, requestBody);
                                    case "VISIBLE_CONVERSATIONS" ->
                                            listVisibleConversations(writer, accountsMaster, requestBody, isSeller);
                                    case "VISIBLE_STORES" -> listVisibleStores(writer, accountsMaster, requestBody);
                                    case "VISIBLE_CUSTOMERS" ->
                                            listVisibleCustomers(writer, accountsMaster, requestBody);
                                    case "MESSAGES" -> listAllMessages(writer, requestBody);
                                    case "CONVERSATIONS" -> listAllConversations(writer, accountsMaster);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            case "SEARCH":
                                switch (requestHeaderTags[1]) {
                                    case "SELLER" -> searchSeller(writer, accountsMaster, requestBody);
                                    case "CUSTOMER" -> searchCustomer(writer, accountsMaster, requestBody);
                                    default -> System.out.println("Error: Invalid Request");
                                }
                                break;
                            default:
                                System.out.println("Error: Invalid Request");
                        }
                    }
                } else {
                    System.out.println("Error: Invalid Request");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Runs the server on a forever loop. Accepts client connections and creates and runs a thread for each connection
     *
     * @param args [UNUSED] Variable arguments
     */
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(8080)) {
            File passwordFile = new File(PASSWORD_FILE_PATH);
            passwordFile.createNewFile();
            File conversationFile = new File(CONVERSATIONS_FILE_PATH);
            conversationFile.createNewFile();

            while (true) {
                Socket clientSocket = serverSocket.accept();
                MessengerServer messengerServer = new MessengerServer(clientSocket);
                new Thread(messengerServer).start();
            }
        } catch (IOException ioException) {
            System.out.println("Error: Could not Connect to Server");
        }
    }
}
