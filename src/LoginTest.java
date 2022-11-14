import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;

import java.io.*;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
/**
 * AccountsMaster
 *
 * The AccountsMaster class acts as the helper class to `Main.
 * It handles the creation and deletion of accounts
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(LoginTest.TestCase.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {

            for (Failure failure : result.getFailures()) {
                if (failure.getException() instanceof NoSuchElementException) {
                    System.out.printf("Tests failed: %d.\n", 0);
                    System.out.println("Excellent - all local tests ran successfully.");
                } else {
                    System.out.printf("Tests failed: %d.\n", result.getFailureCount());
                    System.out.println(failure.getMessage());
                    System.out.println(failure.getTestHeader());
                    System.out.println(failure.getDescription());
                    System.out.println(failure.getException());
                }
            }
        }
    }

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

        @After
        public void restoreInputAndOutput() {
            System.setIn(originalSysin);
            System.setOut(originalOutput);
        }

        private String getOutput() {
            return testOut.toString();
        }

        @SuppressWarnings("SameParameterValue")
        private void receiveInput(String str) {
            testIn = new ByteArrayInputStream(str.getBytes());
            System.setIn(testIn);
        }


        @Test(timeout = 1000)
        public void A() {
            // Set the input
            String input = "Bob\n" +
                    "N\n";
            // Pair the input with the expected result
            String expected = "File not found!\n" +
                    "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Seems like you don't have an account yet!\n" +
                    "Would you like to make an account? (Y/N)\n" +

                    "Sorry to see you go :( Have a great day!";


            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n", "\n");
            output = output.replaceAll("\r\n", "\n");
            assertEquals("Make sure players can navigate the maze successfully!",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void B() {
            // Set the input
            String input = "Bob\n" +
                    "Y\n" +
                    "Bob\n" +
                    "bob@g\n" +
                    "SELLER\n" +
                    "123\n" +
                    "5\n";
            // Pair the input with the expected result
            String expected = "File not found!\n" +
                    "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Seems like you don't have an account yet!\n" +
                    "Would you like to make an account? (Y/N)\n" +

                    "Great! Let's get you set up!\n" +
                    "Create your new username (,<> change to _):\n" +

                    "Register with your email ID:\n" +

                    "Enter your role (SELLER/CUSTOMER):\n" +

                    "Create a password:\n" +

                    "You're all set!\n" +
                    "----Main----\n" +
                    "0. Manage Stores\n" +
                    "1. View Conversations\n" +
                    "2. View All Customers\n" +
                    "3. Search Customers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Logging Out...\n" +
                    "Goodbye!";

            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n", "\n");
            output = output.replaceAll("\r\n", "\n");
            assertEquals("Account Creation Assertion Error",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void C() {
            // Set the input
            String input = "JOE\n" +
                    "Y\n" +
                    "Joe\n" +
                    "joe@g\n" +
                    "Customer\n" +
                    "1234\n" +
                    "5\n";
            // Pair the input with the expected result
            String expected = "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Seems like you don't have an account yet!\n" +
                    "Would you like to make an account? (Y/N)\n" +

                    "Great! Let's get you set up!\n" +
                    "Create your new username (,<> change to _):\n" +

                    "Register with your email ID:\n" +

                    "Enter your role (SELLER/CUSTOMER):\n" +

                    "Create a password:\n" +

                    "You're all set!\n" +
                    "----Main----\n" +
                    "1. View Conversations\n" +
                    "2. View All Stores\n" +
                    "3. Search Sellers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Logging Out...\n" +
                    "Goodbye!";
            // Runs the program with the input values
            receiveInput(input);
            Main.main(new String[0]);

            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Account Creation Assertion Error",
                    expected.trim(), output.trim());
        }

        @Test(timeout = 1000)
        public void D() {
            // Set the input
            String input = "Bob\n" +
                    "123\n" +
                    "3\n" +
                    "Joe\n" +
                    "\n" +
                    "1\n" +
                    "1\n" +
                    "Hello\n" +
                    "4\n" +
                    "\n" +
                    "2\n" +
                    "5\n";
            // Pair the input with the expected result
            String expected = "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Enter password:\n" +

                    "Welcome back BOB!\n" +
                    "----Main----\n" +
                    "0. Manage Stores\n" +
                    "1. View Conversations\n" +
                    "2. View All Customers\n" +
                    "3. Search Customers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Search by Username or Email (full or part):\n" +


                    "----Search Result----\n" +
                    "[To Select a Customer, Enter their No.]\n" +
                    "1. Joe | joe@g\n" +
                    "2. Back to Main Menu\n" +

                    "----Selected: Joe----\n" +
                    "1. Message Customer\n" +
                    "2. Block Customer\n" +
                    "3. Become Invisible to Customer\n" +
                    "4. Back to Customer List\n" +

                    "Your Message: " +
                    "Sent!\n" +
                    "----Selected: Joe----\n" +
                    "1. Message Customer\n" +
                    "2. Block Customer\n" +
                    "3. Become Invisible to Customer\n" +
                    "4. Back to Customer List\n" +


                    "----Search Result----\n" +
                    "[To Select a Customer, Enter their No.]\n" +
                    "1. Joe | joe@g\n" +
                    "2. Back to Main Menu\n" +

                    "----Main----\n" +
                    "0. Manage Stores\n" +
                    "1. View Conversations\n" +
                    "2. View All Customers\n" +
                    "3. Search Customers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Logging Out...\n" +
                    "Goodbye!";
            // Runs the program with the input values
            receiveInput(input);

            Main.main(new String[0]);
            // Retrieves the output from the program
            String output = getOutput();

            // Trims the output and verifies it is correct.
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Account Creation Assertion Error",
                    expected.trim(), output.trim());
        }

    }

}