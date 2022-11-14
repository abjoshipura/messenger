import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.MethodSorters;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConvoTest {

    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(ConvoTest.TestCase2.class);
        System.out.printf("Test Count: %d.\n", result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("Excellent - all local tests ran successfully.");
        } else {
            System.out.printf("Tests failed: %d.\n",result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.getMessage());
                System.out.println(failure.getTestHeader());
                System.out.println(failure.getDescription());
                System.out.println(failure.getException());
            }
        }
    }

    public static class TestCase2 {
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
            String input = "Raymond\n" +
                    "lkjhg\n";

            // Pair the input with the expected result
            String expected = "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Enter password:\n" +

                    "Incorrect Password";
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

        @Test
        public void B() {
            // Set the input
            String input = "Raymond\n" +
                    "lkjhgf\n" +
                    "3\n" +
                    "Gus\n" +
                    "1\n" +
                    "1\n" +
                    "Got the good stuff? The 99.1% pure?\n" +
                    "2\n" +
                    "5\n";

            // Pair the input with the expected result
            String expected = "Conversations File Not Found!\n" +
                    "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Enter password:\n" +

                    "Welcome back RAYMOND!\n" +
                    "--------\n" +
                    "1. View Conversations\n" +
                    "2. View All Stores\n" +
                    "3. Search Sellers\n" +
                    "4. Edit Account\n" +
                    "5. Log Out\n" +

                    "Search by Username or Email (full or part):\n" +

                    "--------\n" +
                    "**To Select, Enter Seller No.**\n" +
                    "1. Gus Fring | lospolloshermanos@gmail.com\n" +
                    "2. Back to Main Menu\n" +

                    "--------\n" +
                    "1. Message Seller\n" +
                    "2. Block Seller\n" +
                    "3. Become Invisible to Seller\n" +
                    "4. Back to Main Menu\n" +

                    "Your Message: " +
                    "Sent!\n" +
                    "--------\n" +
                    "**To Select, Enter Seller No.**\n" +
                    "1. Gus Fring | lospolloshermanos@gmail.com\n" +
                    "2. Back to Main Menu\n" +

                    "--------\n" +
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

        @Test
        public void C() {
            // Set the input
            String input = "Gus Fring\n" +
                    "crystal\n" +
                    "1\n" +
                    "1\n" +
                    "Yes\n" +
                    "4\n" +
                    "3\n" +
                    "6\n";
            // Pair the input with the expected result
            String expected = "Welcome to Messenger (not Facebook)!\n" +
                    "Log in (or Sign Up) with Username or Email:\n" +

                    "Enter password:\n" +

                    "Welcome back GUS FRING!\n" +
                    "You have 1 new unread conversation\n" +
                    "--------\n" +
                    "1. View Conversations\n" +
                    "2. View All Customers\n" +
                    "3. Search Customers\n" +
                    "4. Manage Stores\n" +
                    "5. Edit Account\n" +
                    "6. Log Out\n" +

                    "--------\n" +
                    "**To Open, Enter Conversation No.**\n" +
                    "1. RaymondTOGus Fring\n" +
                    "2. Export Conversations\n" +
                    "3. Back to Main Menu\n" +

                    "--------\n" +
                    "1. Load All Messages\n" +
                    "2. Import .txt File\n" +
                    "3. Send Message\n" +
                    "4. Back to Conversation List\n" +
                    "---\n" +
                    "**To Execute the Below Actions, Enter MsgIndex.Action (eg. 3.5 to Edit Message 3)**\n" +
                    "---\n" +
                    "5. Edit Message\n" +
                    "6. Delete Message\n" +
                    "[0] Raymond: Got the good stuff? The 99.1% pure?\n" +
                    "3\n" +
                    "Your Message: " +
                    "Sent!\n" +

                    "--------\n" +
                    "**To Open, Enter Conversation No.**\n" +
                    "1. RaymondTOGus Fring\n" +
                    "2. Export Conversations\n" +
                    "3. Back to Main Menu\n" +

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
            expected = expected.replaceAll("\r\n","\n");
            output = output.replaceAll("\r\n","\n");
            assertEquals("Account Creation Assertion Error",
                    expected.trim(), output.trim());
        }
    }
}
