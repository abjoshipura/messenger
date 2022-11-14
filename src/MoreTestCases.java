import org.junit.*;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
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
        public static void createFiles() {
            // For testing purposes, erasing past runs of Main.main()
            File f1 = new File("passwords.txt");
            if (f1.exists()) {
                f1.delete();
            }

            File f2 = new File("conversations.txt");
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
            File f1 = new File("passwords.txt");
            if (f1.exists()) {
                f1.delete();
            }
            File f2 = new File("conversations.txt");
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
        public static final String LOG_IN_SIGN_UP = "Log in (or Sign Up) with Username or Email:";
        public static final String NOT_SIGNED_UP = "Seems like you don't have an account yet!";
        public static final String MAKE_ACCOUNT_PROMPT = "Would you like to make an account? (Y/N)";
        public static final String SET_UP = "Great! Let's get you set up!";
        public static final String CREATE_USERNAME = "Create your new username:";
        public static final String REGISTER_EMAIL = "Register with your email ID:";
        public static final String ENTER_ROLE = "Enter your role (SELLER/CUSTOMER):";
        public static final String INVALID_ROLE = "Sorry! That is an invalid role";
        public static final String TRY_AGAIN = "Try again? (Y/N)";
        public static final String CREATE_PASSWORD = "Create a password:";
        public static final String ALL_SET = "You're all set!";
        public static final String LINE_SEPARATOR = "--------";
        public static final String VIEW_CONVERSATIONS = "View Conversations";
        public static final String NO_CONVERSATIONS_FOUND = "No Conversations Found!";
        public static final String VIEW_ALL_STORES = "View All Stores";
        public static final String SELECT_STORE = "**To Select, Enter Store No.**";
        public static final String SEARCH_SELLERS = "Search Sellers";
        public static final String EDIT_ACCOUNT = "Edit Account";
        public static final String LOG_OUT = "Log Out";
        public static final String LOGGING_OUT = "Logging Out...";
        public static final String BACK_TO_MAIN = "Back to Main Menu";
        public static final String INVALID_OPTION = "Invalid Option";
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
            String expected = FILE_NOT_FOUND + System.lineSeparator() +
                    NO_CONVERSATIONS_FILE + System.lineSeparator() +
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
                    "2" + System.lineSeparator() +
                    "0" + System.lineSeparator() +
                    "f" + System.lineSeparator() +
                    "1" + System.lineSeparator() +
                    "5" + System.lineSeparator();

            // Pair the input with the expected result
            String expected = FILE_NOT_FOUND + System.lineSeparator() +
                    NO_CONVERSATIONS_FILE + System.lineSeparator() +
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
                    LINE_SEPARATOR + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    NO_CONVERSATIONS_FOUND + System.lineSeparator() +
                    LINE_SEPARATOR + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
                    "4. " + EDIT_ACCOUNT + System.lineSeparator() +
                    "5. " + LOG_OUT + System.lineSeparator() +
                    SELECT_STORE + System.lineSeparator() +
                    "1. " + BACK_TO_MAIN + System.lineSeparator() +
                    INVALID_OPTION + System.lineSeparator() +
                    SELECT_STORE + System.lineSeparator() +
                    "1. " + BACK_TO_MAIN + System.lineSeparator() +
                    INVALID_OPTION + System.lineSeparator() +
                    LINE_SEPARATOR + System.lineSeparator() +
                    "1. " + VIEW_CONVERSATIONS + System.lineSeparator() +
                    "2. " + VIEW_ALL_STORES + System.lineSeparator() +
                    "3. " + SEARCH_SELLERS + System.lineSeparator() +
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
            assertEquals("Make sure that users can create accounts and enter invalid input without crashing!",
                    expected.trim(), output.trim());
        }

        // TODO: Upload rest of test cases
    }
}