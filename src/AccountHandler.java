import javax.swing.*;
import java.io.*;
import java.util.*;

public class AccountHandler {
    private static String passwordFile;
    /* where logins are stored in the format:
    name,email,password,boolean(true if seller)
    name,email,password,boolean
    name,email,password,boolean
     */
    private static ArrayList<User> userArrayList;
    private static ArrayList<Store> storeArrayList;
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

	// Handle unique conversation
	public static boolean conversationExists(User customer, User seller) {
        for (Conversation conversation : conversationList) {
            String fileName = conversation.getFileName();
            String[] fileNameArr = fileName.split(",");
            if (fileNameArr[1].equals(customer.getEmail()) && fileNameArr[2].equals(seller.getEmail())) {
                return true;
            }
        }
		return false;
	}

	public static User getConversationPartner(User user, int listNumber) { // Case: Searching
	// Scanner for user input... Choosing which one
	// conversationPartners are numbered from 1 to n --> listNumber
	// int index = listNumber - 1
		// Buyer (Customer):
			// Search owner (seller) name --> (a) No results (b) One result (c) Multiple results, choose one
				// ArrayList<Seller> sellerList = user.searchSeller(searchString, userArrayList);
		// Seller:
			// Search buyer (customer) name --> (a) No results (b) One result (c) Multiple results, choose one
				// ArrayList<Customer> custList = user.searchCustomers(searchString, userArrayList);
		// return null;
	}

	public static User getConversationPartner(User user, int listNumber) { // Case: Choosing from list
		// Index to get conversationPartner from is (listNumber - 1)
		// In case of invalid input? (e.g., -1) --> Prompt user again?
		// Buyer: Choose from list of stores --> Use listStores()
		// Seller: Choose from list of customers --> Use listCustomers()
		// Unless list is empty? Cannot return null
	}

	// New Conversation
	public static boolean createConversation(String email, User conversationPartner) {
		// Is the user is already logged in? How to identify (currentUser)?
		User user = null;
		for (int i = 0; i < userArrayList.size(); i++) {
			if (userArrayList.get(i).getEmail().equals(email)) {
				user = userArrayList.get(i);
			}
		}

		ArrayList<User> tempList = getConversationPartner();
		if (conversationPartner == null) {
			System.out.println("Conversation partner must exist!");
			return false;
		}

		Conversation conversation;
		String title;
		String fileName;
		if (user instanceof Customer) { // conversationPartner is Seller
			if (!conversationExists(user, conversationPartner)) {
				title = String.format( "Conversation between %s (Buyer) and %s (Seller)", user.getName(), conversationPartner.getName());
				fileName = String.format("%s,%s,%s", title, user.getEmail(), conversationPartner.getEmail());
				conversation = new Conversation(title, fileName, (Customer) user, (Seller) conversationPartner);
			} else {
				System.out.println("Conversation already exists!");
				return false;
			}
		} else { // instanceof Seller, conversationPartner is Customer
			if (!conversationExists(conversationPartner, user)) {
				title = String.format("Conversation between %s (Seller) and %s (Buyer)", user.getName(), conversationPartner.getName());
				fileName = String.format("%s,%s,%s", title, conversationPartner.getEmail(), user.getEmail());
				conversation = new Conversation(title, fileName, (Customer) conversationPartner, (Seller) user);
			} else {
				System.out.println("Conversation already exists!");
				return false;
			}
		}
		return true;
	}

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
                        userArrayList.get(lineIndex).setName(newName);
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
        if (login(email, password)) { // User is logged in?

            if (passwordFile.contains(newEmail)) {
                System.out.println("E-mail already in use!");
            } else {

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
                            userLine = lineArray[0] + "," + newEmail + "," + lineArray[2]; // Change password
                            temp.add(userLine);

                            userArrayList.get(lineIndex).setPassword(newEmail);
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

                        userArrayList.get(lineIndex).setPassword(newPassword);
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
    public static boolean makeNewAcc(String name, String email, String password, boolean isSeller) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(passwordFile, true))) {
            if (accountExists(email)) {
                System.out.println("Account already exists!");
                return false;
            }
            bw.write(String.format("%s,%s,%s,%b", name, email, password, isSeller)); //adds to the file
            if (isSeller) {
                userArrayList.add(new Seller(name, email, password)); //adds to the in-program arraylist
            } else {
                userArrayList.add(new Customer(name, email, password));
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

    public static boolean userType(String email) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(passwordFile));

        String line = br.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");
            if (email.equalsIgnoreCase(lineArray[1])) {
                return Boolean.parseBoolean(lineArray[3]);
            }
            line = br.readLine();
        }

        br.close();
        return false;
    }

    public static User findUser(String email) {
        for (User u : userArrayList) {
            if (u.getEmail().equalsIgnoreCase(email)) {
                return u;
            }
        }
        return null;
    }

    public static Conversation findConvo(User user) {
        for (Conversation c : conversationList) {
            if (user instanceof Seller) {
                if (c.getSeller().equals(user)) {
                    return c;
                }
            } else {
                if (c.getCustomer().equals(user)) {
                    return c;
                }
            }
        }
        return null;
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

    public static void addStore(Store store) {
        storeArrayList.add(store);
    }

    public static ArrayList<Store> getStoreArrayList() {
        return storeArrayList;
    }
}
