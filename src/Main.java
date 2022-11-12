import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    private static final String passwordFilePath = "passwords.txt";
    private static final String conversationsFilePath = "conversations.txt";

    private static void printMainMenu(User user) {
        System.out.println("1. View Dashboard");
        System.out.println("2. View Conversations");
        System.out.printf("3. View All %ss\n", (user instanceof Seller) ? "Customer" : "Store");
        System.out.printf("4. Search %ss\n", (user instanceof Seller) ? "Customer" : "Seller");
        System.out.println("5. Edit Account");
        System.out.println("6. Log Out");
    }

    private static void runMainMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        boolean deleteUser = false;
        while (true) {
            printMainMenu(loggedOnUser);
            int selectedOption = Integer.parseInt(scan.nextLine());

            if (selectedOption == 1) {
                //TODO Dashboard
                return;
            } else if (selectedOption == 2) {
                System.out.println("--------");
                ArrayList<Conversation> conversations = accountsMaster.listConversations(loggedOnUser);

                if (conversations.size() > 0) {
                    while (true) {
                        System.out.println("**To Open, Enter Conversation No.**");
                        for (int i = 0; i < conversations.size(); i++) {
                            System.out.printf("%d. %s\n", i + 1, conversations.get(i).getConversationID());
                        }
                        System.out.printf("%d. Export Conversations\n", conversations.size());
                        System.out.printf("%d. Back to Main Menu\n", conversations.size() + 1);
                        int conversationNumber = Integer.parseInt(scan.nextLine());

                        if (conversationNumber == conversations.size() + 1) {
                            break;
                        } else if (conversationNumber == conversations.size()) {
                            System.out.println("Enter Conversations to Export Separated by Commas (eg. 1,3,4):");
                            String exportingIndexes = scan.nextLine();

                            ArrayList<Conversation> exportingConversations = new ArrayList<>();
                            for (String index : exportingIndexes.split(",")) {
                                exportingConversations.add(conversations.get(Integer.parseInt(index)));
                            }

                            accountsMaster.convertConversationsToCSV(exportingConversations); //TODO implement censoring here as well
                        } else {
                            //TODO Shift to AccountsMaster to implement censoring
                            ArrayList<Message> messages = conversations.get(conversationNumber - 1).readFile(loggedOnUser);
                            int lowerLimit = Math.min(messages.size(), 20);
                            for (int i = messages.size() - lowerLimit; i < messages.size(); i++) {
                                System.out.printf("%s: %s", messages.get(i).getSender(), messages.get(i).getMessage());
                            }

                            System.out.println("1. Load More Messages");
                            System.out.println("2. Send Message");
                            System.out.println("3. Edit Message");
                            System.out.println("4. Delete Message");
                            System.out.println("5. Import .txt File");
                            System.out.println("6. Back to Main Menu");
                            int conversationAction = Integer.parseInt(scan.nextLine());

                            //TODO Messaging
//                            System.out.print("New Message: ");
//                            String newMessage = scan.nextLine();
//                            if (newMessage.equalsIgnoreCase("~exit~")) {
//                                break;
//                            } else {
//                                loggedOnUser.sendMessageToConversation(newMessage, conversations.get(conversationNumber - 1), );
                            if (loggedOnUser instanceof Seller) {
                                conversations.get(conversationNumber - 1).setCustomerUnread(true);
                            } else {
                                conversations.get(conversationNumber - 1).setSellerUnread(true);
                            }
//                            }
                        }
                    }
                } else {
                    System.out.println("No Conversations Found!");
                }
            } else if (selectedOption == 3) {
                if (loggedOnUser instanceof Seller) {
                    accountsMaster.listCustomers(loggedOnUser);
                    System.out.println("Choose one");
                    System.out.println("All of them");
                    System.out.println("exit");

                    System.out.println("Message User");
                    System.out.println("Block User");
                    System.out.println("Become Invisible to User");
//                        if chose one, create new conversation/open existing conversation
                } else {
                    accountsMaster.listStoresAndSellers(loggedOnUser); //returns a Map<Store, Seller>
                    System.out.println("Choose one");
                    System.out.println("All of them");
                    System.out.println("exit");

                    System.out.println("Message User");
                    System.out.println("Block User");
                    System.out.println("Become Invisible to User");
//                        if chose one, create new conversation with Seller/open existing conversation with Seller
                }
            } else if (selectedOption == 4) {
                System.out.println("Enter Username or Email:");
                String searchKeyword = scan.nextLine();
                if (loggedOnUser instanceof Seller) {
                    accountsMaster.fetchUsers(loggedOnUser); //uses .contains
                    System.out.println("Choose one");
                    System.out.println("All of them");
                    System.out.println("exit");

                    System.out.println("Message User");
                    System.out.println("Block User");
                    System.out.println("Become Invisible to User");
//                        if chose one, create new conversation/open existing conversation
                } else {
                    accountsMaster.listStoresAndSellers((Customer) loggedOnUser); //returns a Map<Store, Seller>
                    System.out.println("Choose one");
                    System.out.println("All of them");
                    System.out.println("exit");

                    System.out.println("Message User");
                    System.out.println("Block User");
                    System.out.println("Become Invisible to User");
//                        if chose one, create new conversation/open existing conversation
                }
            } else if (selectedOption == 5) {
                if (runAccountMenu(scan, accountsMaster, loggedOnUser)) {
                    break;
                }
            } else if (selectedOption == 6) {
                System.out.println("Logging Out... \nGoodbye!");
                break;
            } else {
                System.out.println("Invalid Option");
            }
        }
    }

    private static void printEditAccountMenu() {
        System.out.println("1. Change Username");
        System.out.println("2. Edit Censoring");
        System.out.println("3. Delete Account");
        System.out.println("4. Back to Main Menu");
    }

    private static void printCensoredWords(ArrayList<String> censoredWordPairs) {
        for (int i = 0; i < censoredWordPairs.size(); i++) {
            System.out.printf("%d. %s", i + 1, censoredWordPairs.get(i).replace(":", " >>> "));
        }
    }

    private static boolean runAccountMenu(Scanner scan, AccountsMaster accountsMaster, User loggedOnUser) {
        boolean deleteUser = false;
        System.out.println("--------");
        while (true) {
            printEditAccountMenu();
            int accountOption = Integer.parseInt(scan.nextLine());

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
                        loggedOnUser.setUsername(username); //TODO Change in conversations handled in User
                        break;
                    }
                }
            } else if (accountOption == 2) {
                ArrayList<String> censoredWords = loggedOnUser.getCensoredWords();

                if (censoredWords.size() > 0) {
                    System.out.println("**To Remove, Enter Censor Pair No.**");
                    printCensoredWords(censoredWords);
                }
                System.out.printf("%d. Add Censor Pair\n", censoredWords.size() + 1);
                int censorOption = Integer.parseInt(scan.nextLine());

                if (censorOption == censoredWords.size() + 1) {
                    System.out.println("Enter new pair as \"Word:Replacement\" or only \"Word\"");
                    String[] newCensor = scan.nextLine().split(":");
                    if (newCensor.length == 1) {
                        censoredWords.add(newCensor[0] + ":" + "****");
                    } else {
                        censoredWords.add(newCensor[0] + ":" + newCensor[1]);
                    }
                } else {
                    //TODO Handle IndexOutOfBounds
                    censoredWords.remove(censorOption - 1);
                }

            } else if (accountOption == 3) {
                System.out.println("Are you sure? (Y/N)");
                deleteUser = scan.nextLine().equalsIgnoreCase("Y");
                if (deleteUser) {
                    accountsMaster.deleteAcc(loggedOnUser);
                } else {
                    break;
                }
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
                    System.out.printf("You have %d new unread conversations", numUnreadConversations);
                }
            } else {
                System.out.println("Incorrect Password");
            }
        }

        if (loggedOnUser != null) {
            //TODO Handle Invalid Input
            runMainMenu(scan, accountsMaster, loggedOnUser);
            System.out.println("Goodbye!");
        } else {
            System.out.println("Sorry! We could not log you in!");
        }
    }
}
