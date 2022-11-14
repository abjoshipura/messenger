import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import java.io.*;
import static org.junit.Assert.*;

public class LoginTest {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(LoginTest.TestCase.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {
            System.out.printf("Tests failed: %d.\n", result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.getMessage());
                System.out.println(failure.getTestHeader());
                System.out.println(failure.getDescription());
                System.out.println(failure);
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
        public void testExpectedOne() {
            // Set the input
            String input = "Jesse\n" +
                    "Y\n" +
                    "Jesse James\n" +
                    "j@gmail.com\n" +
                    "Customer\n" +
                    "tree\n" +
                    "4\n" +
                    "1\n" +
                    "Walter White\n" +
                    "4\n" +
                    "5\n";
            // Pair the input with the expected result
            String expected = "File not found!\n" +
                    "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Seems like you don't have an account yet!\n" +
                    "Would you like to make an account? (Y/N)\n" +

                    "Great! Let's get you set up!\n" +
                    "Create your new username:\n" +

                    "Register with your email ID:\n" +

                    "Enter your role (SELLER/CUSTOMER):\n" +

                    "Create a password:\n" +

                    "You're all set!\n" +
                    "--------\n" +
                    "1. View Conversations\n" +
                    "2. View All Stores\n" +
                    "3. Search Sellers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "--------\n" +
                    "1. Change Username\n" +
                    "2. Edit Censoring\n" +
                    "3. Delete Account\n" +
                    "4. Back to Main Menu\n" +

                    "Enter new username:\n" +

                    "--------\n" +
                    "1. Change Username\n" +
                    "2. Edit Censoring\n" +
                    "3. Delete Account\n" +
                    "4. Back to Main Menu\n" +

                    "--------\n" +
                    "1. View Conversations\n" +
                    "2. View All Stores\n" +
                    "3. Search Sellers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Logging Out...\n" +
                    "Goodbye!\n";


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
        public void testExpectedTwo() {
            // Set the input
            String input = "new account\n" +
                    "Y\n" +
                    "Bob\n" +
                    "bob@gmail.com\n" +
                    "Seller\n" +
                    "pass\n" +
                    "6\n";
            // Pair the input with the expected result
            String expected ="Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Seems like you don't have an account yet!\n" +
                    "Would you like to make an account? (Y/N)\n" +

                    "Great! Let's get you set up!\n" +
                    "Create your new username:\n" +

                    "Register with your email ID:\n" +

                    "Enter your role (SELLER/CUSTOMER):\n" +

                    "Create a password:\n" +

                    "You're all set!\n" +
                    "--------\n" +
                    "1. View Conversations\n" +
                    "2. View All Customers\n" +
                    "3. Search Customers\n" +
                    "4. Manage Stores\n" +
                    "5. Edit Account\n" +
                    "6. Log Out\n" +

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

    }

}