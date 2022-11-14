import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.Test;
import java.io.*;

import static org.junit.Assert.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

public class MoreTestCases {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(MoreTestCases.TestCase.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {
            System.out.printf("Tests failed: %d.\n",result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.getMessage());
                System.out.println(failure.getTestHeader());
                System.out.println(failure.getDescription());
                System.out.println(failure);
            }
        }
    }

    @TestMethodOrder(OrderAnnotation.class)
    public static class TestCase {
        private final PrintStream originalOutput = System.out;
        private final InputStream originalSysin = System.in;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayInputStream testIn;

        @SuppressWarnings("FieldCanBeLocal")
        private ByteArrayOutputStream testOut;

        @Before
        public void outputStart() {
            testOut = new ByteArrayOutputStream();
            System.setOut(new PrintStream(testOut));
        }

        @BeforeClass
        public static void resetFiles() {
            // For testing purposes, erasing past runs of Main.main()
            File f1 = new File("../passwords.txt");
            if (f1.exists()) {
                f1.delete();
            }

            File f2 = new File("../conversations.txt");
            if (f2.exists()) {
                f2.delete();
            }
        }

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        @AfterClass
        public static void deleteFiles() {
            // Delete password and conversation files to reset after running
            File f1 = new File("../passwords.txt");
            if (f1.exists()) {
                f1.delete();
            }
            File f2 = new File("../conversations.txt");
            if (f2.exists()) {
                f2.delete();
            }
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }

        // Each of the correct outputs
        private static final String FILE_NOT_FOUND = "File not found!";
        public static final String NO_CONVERSATIONS_FILE = "Conversations File Not Found!";
        public static final String WELCOME = "Welcome to Messenger (not Facebook)!";
        public static final String WELCOME_BACK = "Welcome back";
        public static final String LOG_IN_SIGN_UP = "Log in (or Sign Up) with Username or Email:";
        public static final String NOT_SIGNED_UP = "Seems like you don't have an account yet!";
        public static final String MAKE_ACCOUNT_PROMPT = "Would you like to make an account? (Y/N)";
        public static final String SET_UP = "Great! Let's get you set up!";
        public static final String CREATE_USERNAME = "Create your new username (,<> change to _):";
        public static final String REGISTER_EMAIL = "Register with your email ID:";
        public static final String EMAIL_TAKEN = "Sorry! That email is already registered";
        public static final String ENTER_ROLE = "Enter your role (SELLER/CUSTOMER):";
        public static final String INVALID_ROLE = "Sorry! That is an invalid role";
        public static final String TRY_AGAIN = "Try again? (Y/N)";
        public static final String USERNAME_TAKEN = "Sorry! That username is already taken";
        public static final String CREATE_PASSWORD = "Create a password:";
        public static final String ENTER_PASSWORD = "Enter password:";
        public static final String INCORRECT_PASSWORD = "Incorrect Password";
        public static final String INVALID_PASSWORD = "Invalid Password";
        public static final String ALL_SET = "You're all set!";
        public static final String LINE_SEPARATOR_MAIN = "----Main----";
        public static final String MANAGE_STORES = "Manage Stores";
        public static final String YOUR_STORES = "----Your Stores----";
        public static final String ADD_STORE = "Add Store";
        public static final String DELETE_STORE = "Delete a Store";
        public static final String CONFIRM_CHOICE = "Are you sure? (Y/N)";
        public static final String ENTER_NEW_STORE = "Enter New Store Name:";
        public static final String STORE_DETAILS = "----Store Details----";
        public static final String LINE_SEPARATOR_CONVO = "----Your Conversations----";
        public static final String ENTER_CONTINUE = "[Hit Enter to Continue]";
        public static final String VIEW_CONVERSATIONS = "View Conversations";
        public static final String NO_CONVOS = "[No Conversations]";
        public static final String VIEW_ALL_STORES = "View All Stores";
        public static final String VIEW_ALL_CUSTOMERS = "View All Customers";
        public static final String SEARCH_CUSTOMERS = "Search Customers";
        public static final String LINE_SEPARATOR_STORES = "----All Stores----";
        public static final String LINE_SEPARATOR_CUST = "----All Customers----";
        public static final String SELECT_CUST = "[To Select a Customer, Enter their No.]";
        public static final String NO_STORES = "[No Stores]";
        public static final String SELECT_STORE = "**To Select, Enter Store No.**";
        public static final String SEE_STORE_DETAILS = "[To See its Details, Enter the Store Number]";
        public static final String SEARCH_SELLERS = "Search Sellers";
        public static final String EDIT_ACCOUNT = "Edit Account";
        public static final String CHANGE_USERNAME = "Change Username";
        public static final String ENTER_NEW_USERNAME = "Enter new username:";
        public static final String USERNAME_CHANGED = "Username Changed!";
        public static final String ENTER_CURRENT_PASSWORD = "Enter current password:";
        public static final String ENTER_NEW_PASSWORD = "Enter new password";
        public static final String CONFIRM_PASSWORD = "Confirm new password";
        public static final String PASSWORD_DIFF = "Did not match!";
        public static final String PASSWORD_CHANGED = "Password Changed!";
        public static final String CHANGE_PASSWORD = "Change Password";
        public static final String MANAGE_BLOCKED_USERS = "Manage Blocked Users";
        public static final String MANAGE_INVISIBLE_TO_USERS = "Manage Invisible to Users";
        public static final String EDIT_CENSORING = "Edit Censoring";
        public static final String DELETE_ACCOUNT = "Delete Account";
        public static final String LOG_OUT = "Log Out";
        public static final String LOGGING_OUT = "Logging Out...";
        public static final String BACK_TO_MAIN = "Back to Main Menu";
        public static final String INVALID_OPTION = "Invalid Option";
        public static final String ENTER_STORE_NUMBER = "Enter Store Number:";
        public static final String GOODBYE_NO_ACCOUNT = "Sorry to see you go :( Have a great day!";
        public static final String GOODBYE_ACCOUNT = "Goodbye!";
        // etc.

        @Test(timeout = 1000)
        @Order(1)
        public void testExpectedOne() { // Choose not to make an account
            // Set the input
            String input = "bob" + System.lineSeparator() +
                    "n" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = // FILE_NOT_FOUND + System.lineSeparator() +
                    // NO_CONVERSATIONS_FILE + System.lineSeparator() +
                    WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    NOT_SIGNED_UP + System.lineSeparator() +
                    MAKE_ACCOUNT_PROMPT + System.lineSeparator() +
                    GOODBYE_NO_ACCOUNT + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that users can choose not to create an account when prompted!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        @Order(2)
        public void testExpectedTwo() { // Create account
            // Set the input
            String input = "bob" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "bob" + System.lineSeparator() +
                    "bob@gmail.com" + System.lineSeparator() +
                    "buyer" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "customer" + System.lineSeparator() +
                    "password" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    System.lineSeparator() +
                    "0" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "f" + System.lineSeparator() +
                    "5" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = //FILE_NOT_FOUND + System.lineSeparator() +
                    //NO_CONVERSATIONS_FILE + System.lineSeparator() +
                    WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    NOT_SIGNED_UP + System.lineSeparator() +
                    MAKE_ACCOUNT_PROMPT + System.lineSeparator() +
                    SET_UP + System.lineSeparator() +
                    CREATE_USERNAME + System.lineSeparator() +
                    REGISTER_EMAIL + System.lineSeparator() +
                    ENTER_ROLE + System.lineSeparator() +
                    INVALID_ROLE + System.lineSeparator() +
                    TRY_AGAIN + System.lineSeparator() +
                    ENTER_ROLE + System.lineSeparator() +
                    CREATE_PASSWORD + System.lineSeparator() +
                    ALL_SET + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LINE_SEPARATOR_CONVO + System.lineSeparator() +
                    NO_CONVOS + System.lineSeparator() +
                    ENTER_CONTINUE + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    INVALID_OPTION + System.lineSeparator() +
                    LINE_SEPARATOR_STORES + System.lineSeparator() +
                    NO_STORES + System.lineSeparator() +
                    "1. " + BACK_TO_MAIN + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    INVALID_OPTION + System.lineSeparator() +
                    LOGGING_OUT + System.lineSeparator() +
                    GOODBYE_ACCOUNT + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that users can create accounts and enter invalid input without crashing!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        @Order(3)
        public void testExpectedThree() { // Wrong password when logging in
            // Set the input
            String input = "bob" + System.lineSeparator() +
                    "pass" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = // NO_CONVERSATIONS_FILE + System.lineSeparator() +
                    WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    ENTER_PASSWORD + System.lineSeparator() +
                    INCORRECT_PASSWORD + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that users can fail to sign in with the wrong password!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        @Order(4)
        public void testExpectedFour() { // Not allowed to create duplicate accounts (username/email)
            // Set the input
            String input = "bobjones" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "bob" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "bobjones" + System.lineSeparator() +
                    "bob@gmail.com" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "bobjones@yahoo.com" + System.lineSeparator() +
                    "seller" + System.lineSeparator() +
                    "password" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "5" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = // NO_CONVERSATIONS_FILE + System.lineSeparator() +
                    WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    NOT_SIGNED_UP + System.lineSeparator() +
                    MAKE_ACCOUNT_PROMPT + System.lineSeparator() +
                    SET_UP + System.lineSeparator() +
                    CREATE_USERNAME + System.lineSeparator() +
                    USERNAME_TAKEN + System.lineSeparator() +
                    TRY_AGAIN + System.lineSeparator() +
                    CREATE_USERNAME + System.lineSeparator() +
                    REGISTER_EMAIL + System.lineSeparator() +
                    EMAIL_TAKEN + System.lineSeparator() +
                    TRY_AGAIN + System.lineSeparator() +
                    REGISTER_EMAIL + System.lineSeparator() +
                    ENTER_ROLE + System.lineSeparator() +
                    CREATE_PASSWORD + System.lineSeparator() +
                    ALL_SET + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LINE_SEPARATOR_CONVO + System.lineSeparator() +
                    NO_CONVOS + System.lineSeparator() +
                    ENTER_CONTINUE + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LINE_SEPARATOR_CUST + System.lineSeparator() +
                    SELECT_CUST + System.lineSeparator() +
                    "1. bob | bob@gmail.com" + System.lineSeparator() +
                    "2. " + BACK_TO_MAIN + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LOGGING_OUT + System.lineSeparator() +
                    GOODBYE_ACCOUNT + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that users can't create duplicate accounts!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        @Order(5)
        public void testExpectedFive() { // Not allowed to create duplicate accounts (username/email)
            // Set the input
            String input = "bobjones" + System.lineSeparator() +
                    "password" + System.lineSeparator() +
                    "4" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "bobj" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "pass" + System.lineSeparator() +
                    "2" + System.lineSeparator() + // Invalid Password
                    "password" + System.lineSeparator() +
                    "abc123" + System.lineSeparator() +
                    "a" + System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "password" + System.lineSeparator() +
                    "abc123" + System.lineSeparator() +
                    "abc123" + System.lineSeparator() +
                    "7" + System.lineSeparator() +
                    "5" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = // NO_CONVERSATIONS_FILE + System.lineSeparator() +
                    WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    ENTER_PASSWORD + System.lineSeparator() +
                    WELCOME_BACK + " BOBJONES!" + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    "--------" + System.lineSeparator() +
                    "1. " + CHANGE_USERNAME + System.lineSeparator() +
                    "2. " + CHANGE_PASSWORD + System.lineSeparator() +
                    "3. " + MANAGE_BLOCKED_USERS + System.lineSeparator() +
                    "4. " + MANAGE_INVISIBLE_TO_USERS + System.lineSeparator() +
                    "5. " + EDIT_CENSORING + System.lineSeparator() +
                    "6. " + DELETE_ACCOUNT + System.lineSeparator() +
                    "7. " + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_NEW_USERNAME + System.lineSeparator() +
                    USERNAME_CHANGED + System.lineSeparator() + // TODO: Error: Could Not Update Values in Files
                    "--------" + System.lineSeparator() +
                    "1. " + CHANGE_USERNAME + System.lineSeparator() +
                    "2. " + CHANGE_PASSWORD + System.lineSeparator() +
                    "3. " + MANAGE_BLOCKED_USERS + System.lineSeparator() +
                    "4. " + MANAGE_INVISIBLE_TO_USERS + System.lineSeparator() +
                    "5. " + EDIT_CENSORING + System.lineSeparator() +
                    "6. " + DELETE_ACCOUNT + System.lineSeparator() +
                    "7. " + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_CURRENT_PASSWORD + System.lineSeparator() +
                    INVALID_PASSWORD + System.lineSeparator() +
                    "--------" + System.lineSeparator() +
                    "1. " + CHANGE_USERNAME + System.lineSeparator() +
                    "2. " + CHANGE_PASSWORD + System.lineSeparator() +
                    "3. " + MANAGE_BLOCKED_USERS + System.lineSeparator() +
                    "4. " + MANAGE_INVISIBLE_TO_USERS + System.lineSeparator() +
                    "5. " + EDIT_CENSORING + System.lineSeparator() +
                    "6. " + DELETE_ACCOUNT + System.lineSeparator() +
                    "7. " + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_CURRENT_PASSWORD + System.lineSeparator() +
                    ENTER_NEW_PASSWORD + System.lineSeparator() +
                    CONFIRM_PASSWORD + System.lineSeparator() +
                    PASSWORD_DIFF + System.lineSeparator() +
                    "--------" + System.lineSeparator() +
                    "1. " + CHANGE_USERNAME + System.lineSeparator() +
                    "2. " + CHANGE_PASSWORD + System.lineSeparator() +
                    "3. " + MANAGE_BLOCKED_USERS + System.lineSeparator() +
                    "4. " + MANAGE_INVISIBLE_TO_USERS + System.lineSeparator() +
                    "5. " + EDIT_CENSORING + System.lineSeparator() +
                    "6. " + DELETE_ACCOUNT + System.lineSeparator() +
                    "7. " + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_CURRENT_PASSWORD + System.lineSeparator() +
                    ENTER_NEW_PASSWORD + System.lineSeparator() +
                    CONFIRM_PASSWORD + System.lineSeparator() +
                    PASSWORD_CHANGED + System.lineSeparator() +
                    "--------" + System.lineSeparator() +
                    "1. " + CHANGE_USERNAME + System.lineSeparator() +
                    "2. " + CHANGE_PASSWORD + System.lineSeparator() +
                    "3. " + MANAGE_BLOCKED_USERS + System.lineSeparator() +
                    "4. " + MANAGE_INVISIBLE_TO_USERS + System.lineSeparator() +
                    "5. " + EDIT_CENSORING + System.lineSeparator() +
                    "6. " + DELETE_ACCOUNT + System.lineSeparator() +
                    "7. " + BACK_TO_MAIN + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LOGGING_OUT + System.lineSeparator() +
                    GOODBYE_ACCOUNT + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that users can edit their accounts (within reason)!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        @Order(6)
        public void testExpectedSix() { // Not allowed to create duplicate accounts (username/email)
            // Set the input
            String input = "bobj" + System.lineSeparator() +
                    "abc123" + System.lineSeparator() +
                    "0" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "Bob's Store" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    System.lineSeparator() +
                    "2" + System.lineSeparator() +
                    "Bob's Store 2" + System.lineSeparator() +
                    "4" + System.lineSeparator() +
                    "3" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "y" + System.lineSeparator() +
                    "4" + System.lineSeparator() +
                    "5" + System.lineSeparator();

            // Pair the input with the expected result
            // NO_CONVERSATIONS_FILE + System.lineSeparator() +
            String expected = WELCOME + System.lineSeparator() +
                    LOG_IN_SIGN_UP + System.lineSeparator() +
                    ENTER_PASSWORD + System.lineSeparator() +
                    WELCOME_BACK + " BOBJ!" + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    YOUR_STORES + System.lineSeparator() +
                    NO_STORES + System.lineSeparator() +
                    "1. " + ADD_STORE + System.lineSeparator() +
                    "2. " + DELETE_STORE + System.lineSeparator() +
                    "3. " + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_NEW_STORE + System.lineSeparator() +
                    YOUR_STORES + System.lineSeparator() +
                    SEE_STORE_DETAILS + System.lineSeparator() +
                    "1. Bob's Store" + System.lineSeparator() +
                    "2. " + ADD_STORE + System.lineSeparator() +
                    "3. " + DELETE_STORE + System.lineSeparator() +
                    "4." + BACK_TO_MAIN + System.lineSeparator() +
                    STORE_DETAILS + System.lineSeparator() +
                    "Name: Bob's Store" + System.lineSeparator() +
                    "Seller: bobj" + System.lineSeparator() +
                    ENTER_CONTINUE + System.lineSeparator() +
                    YOUR_STORES + System.lineSeparator() +
                    SEE_STORE_DETAILS + System.lineSeparator() +
                    "1. Bob's Store" + System.lineSeparator() +
                    "2. " + ADD_STORE + System.lineSeparator() +
                    "3. " + DELETE_STORE + System.lineSeparator() +
                    "4." + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_NEW_STORE + System.lineSeparator() +
                    YOUR_STORES + System.lineSeparator() +
                    SEE_STORE_DETAILS + System.lineSeparator() +
                    "1. Bob's Store" + System.lineSeparator() +
                    "2. Bob's Store 2" + System.lineSeparator() +
                    "3. " + ADD_STORE + System.lineSeparator() +
                    "4. " + DELETE_STORE + System.lineSeparator() +
                    "5." + BACK_TO_MAIN + System.lineSeparator() +
                    ENTER_STORE_NUMBER + System.lineSeparator() +
                    INVALID_OPTION + System.lineSeparator() +
                    CONFIRM_CHOICE + System.lineSeparator() +
                    YOUR_STORES + System.lineSeparator() +
                    SEE_STORE_DETAILS + System.lineSeparator() +
                    "1. Bob's Store 2" + System.lineSeparator() +
                    "2. " + ADD_STORE + System.lineSeparator() +
                    "3. " + DELETE_STORE + System.lineSeparator() +
                    "4." + BACK_TO_MAIN + System.lineSeparator() +
                    LINE_SEPARATOR_MAIN + System.lineSeparator() +
                    "0. " + MANAGE_STORES + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_CUSTOMERS + System.lineSeparator() +
                    "3. " + SEARCH_CUSTOMERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    LOGGING_OUT + System.lineSeparator() +
                    GOODBYE_ACCOUNT + System.lineSeparator();

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Make sure that sellers can create and view their stores!",
                    expected.trim(), output.trim());
        }
        // TODO: Upload rest of test cases
        // ISSUES:
        // - When trying to delete a store and you have no stores, stuck in infinite loop where
        // anything you type gives you "Invalid Option"
    }
}