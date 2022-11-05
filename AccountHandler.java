import java.io.*;
import java.util.*;

public class AccountHandler {
    private static String passwordFile;
    /* where logins are stored in the format:
    login,password
    login,password
    login,password
     */
    //TODO Handle unique conversation
    //TODO New Conversation
    //TODO Handle un/blocking
    //TODO Handle in/visibility
    //TODO List Conversations
    //TODO Open Conversations w/ w/o censoring as per user

    //TODO Customer/Seller Dashboards
    //TODO Sorting the Dashboard
    //TODO Disappearing Messages

    //TODO Edit Accounts
    //TODO SignIn Accounts
    //TODO New Accounts
    public static boolean makeNewAcc(String login, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true))) {
            if (accountExists(login)) {
                System.out.println("Account already exists!");
                return false;
            }
            bw.write(String.format("%s,%s", login, password));
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //TODO Deleting Accounts
    //TODO check if account already exists
    public static boolean accountExists(String login) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(passwordFile));

        // This block reads every line in the passwords section
        // Returns true if login exists
        String line = br.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");
            if (login.equalsIgnoreCase(lineArray[0])) {
                return true;
            }
            line = br.readLine();
        }

        br.close();
        return false;
    }
}