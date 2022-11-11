import java.io.*;
import java.util.*;

public class AccountHandler {
    private static String passwordFile;
    /* where logins are stored in the format:
    name,email,password
    name,email,password
    name,email,password
     */
    private static ArrayList<User> userArrayList;
    private static ArrayList<Conversation> conversationList;
    public AccountHandler(String pf) {
        passwordFile = pf; //sets password file to input filename
        try (BufferedReader br = new BufferedReader(new FileReader(pf))) {
            //this adds users previously created into the arraylist
            String line = br.readLine();
            while (line != null) {
                String[] lineArray = line.split(",");
                userArrayList.add(new User(lineArray[0], lineArray[1], lineArray[2]));
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //TODO Handle unique conversation
    //TODO New Conversation
    //TODO Handle un/blocking
    //TODO Handle in/visibility
    //TODO List Conversations
    //TODO Open Conversations w/ w/o censoring as per user

    //TODO Customer/Seller Dashboards
    //TODO Sorting the Dashboard
    //TODO Disappearing Messages

    // Edit Account --> TODO: How to ensure that the user is editing/deleting their own account?
    public static boolean editName(String email, String password, String newName) throws IOException {
        if (login(email, password)) { // User is logged in?
            ArrayList<String> temp = new ArrayList<>(); // Stores file contents

            try (BufferedReader br = new BufferedReader(new FileReader(passwordFile));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true)); // Append mode
                 BufferedWriter del = new BufferedWriter(new FileWriter(passwordFile, false))) { // Overwrite mode
                int lineIndex = 0;
                String line;
                String userLine;
                while ((line = br.readLine()) != null) {
                    String[] lineArray = line.split(",");
                    if (!email.equalsIgnoreCase(lineArray[1])) { // Uniquely identifies user
                        temp.add(line);
                    } else { // Line corresponds to user
                        userLine = newName + "," + lineArray[1] + "," + lineArray[2]; // Change name
                        temp.add(userLine);
                        userArrayList.get(lineIndex).setEmail(newName);
                    }
                    lineIndex++;
                }
                
                del.write(""); // Empty file contents
                for (int i = 0; i < temp.size(); i++) {
                    bw.write(temp.get(i));
                    if (i != temp.size() - 1) { // Keep file format consistent
                        bw.newLine();
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    public static boolean editEmail(String email, String password, String newEmail) throws IOException {
        if (passwordFile.contains(newEmail)) {
            System.out.println("E-mail already in use!");
        } else {
            if (login(email, password)) { // User is logged in?
                ArrayList<String> temp = new ArrayList<>(); // Stores file contents
                
                try (BufferedReader br = new BufferedReader(new FileReader(passwordFile));
                     BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true)); // Append mode
                     BufferedWriter del = new BufferedWriter(new FileWriter(passwordFile, false))) { // Overwrite mode
                    int lineIndex = 0;
                    String line;
                    String userLine;
                    while ((line = br.readLine()) != null) {
                        String[] lineArray = line.split(",");
                        if (!email.equalsIgnoreCase(lineArray[1])) { // Uniquely identifies user
                            temp.add(line);
                        } else { // Line corresponds to user
                            userLine = lineArray[0] + "," + newEmail + "," + lineArray[2]; // Change e-mail
                            temp.add(userLine);
                            userArrayList.get(lineIndex).setEmail(newEmail);
                        }
                        lineIndex++;
                    }
                    
                    del.write(""); // Empty file contents
                    for (int i = 0; i < temp.size(); i++) {
                        bw.write(temp.get(i));
                        if (i != temp.size() - 1) { // Keep file format consistent
                            bw.newLine();
                        }
                    }
                    return true;
                } catch (FileNotFoundException e) {
                    System.out.println("File not found!");
                    return false;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }
    
    public static boolean editPassword(String email, String password, String newPassword) throws IOException {
        if (login(email, password)) { // User is logged in?
            ArrayList<String> temp = new ArrayList<>(); // Stores file contents
                
            try (BufferedReader br = new BufferedReader(new FileReader(passwordFile));
                 BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true)); // Append mode
                 BufferedWriter del = new BufferedWriter(new FileWriter(passwordFile, false))) { // Overwrite mode
                int lineIndex = 0;
                String line;
                String userLine;
                while ((line = br.readLine()) != null) {
                    String[] lineArray = line.split(",");
                    if (!email.equalsIgnoreCase(lineArray[1])) { // Uniquely identifies user
                        temp.add(line);
                    } else { // Line corresponds to user
                        userLine = lineArray[0] + "," + lineArray[1] + "," + newPassword; // Change password
                        temp.add(userLine);
                        userArrayList.get(lineIndex).setEmail(newPassword);
                    }
                    lineIndex++;
                }
                
                del.write(""); // Empty file contents
                for (int i = 0; i < temp.size(); i++) {
                    bw.write(temp.get(i));
                    if (i != temp.size() - 1) { // Keep file format consistent
                        bw.newLine();
                    }
                }
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("File not found!");
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }
    
    //TODO SignIn Accounts
    public static boolean login(String email, String password) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(passwordFile));

        // This block reads every line in the passwords file
        // Returns true if login and passwords match
        String line = br.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");
            if (email.equalsIgnoreCase(lineArray[1]) && email.equals(lineArray[2])) {
                return true;
            }
            line = br.readLine();
        }

        br.close();
        return false;
    }
    //TODO New Accounts
    public static boolean makeNewAcc(String name, String email, String password) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true))) {
            if (accountExists(email)) {
                System.out.println("Account already exists!");
                return false;
            }
            bw.write(String.format("%s,%s,%s", name, email, password)); //adds to the file
            userArrayList.add(new User(name, email, password)); //adds to the in-program arraylist
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
    public static boolean deleteAcc(String email, String password) {

        ArrayList<String> temp = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(passwordFile));
        BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true));
        BufferedWriter del = new BufferedWriter(new FileWriter(passwordFile, false))){

            String line = br.readLine();
            while (line != null) {
                String[] lineArray = line.split(",");
                if (!email.equalsIgnoreCase(lineArray[1]) && !password.equals(lineArray[2])) {
                    temp.add(line);
                }
                line = br.readLine();
            }

            del.write("");

            for (String l : temp) {
                bw.write(l);
            }

            for (int i = 0; i < temp.size(); i++) {
                if (userArrayList.get(i).getEmail().equalsIgnoreCase(email)) {
                    userArrayList.remove(i);
                    i++;
                }

            }
            return true;

        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean accountExists(String email) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(passwordFile));

        // This block reads every line in the passwords file
        // Returns true if email exists
        String line = br.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");
            if (email.equalsIgnoreCase(lineArray[1])) {
                return true;
            }
            line = br.readLine();
        }

        br.close();
        return false;
    }

    public static ArrayList<Conversation> getConversationList() {
        return conversationList;
    }
    
    public static ArrayList<User> getUserArrayList() {
        return userArrayList;
    }

    public static void addConversation(Conversation conversation) {
        conversationList.add(conversation);
    }
}
