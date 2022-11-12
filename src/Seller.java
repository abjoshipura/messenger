import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Seller extends User {
    private ArrayList<Store> stores;
    private Map<Customer, ArrayList<Message>> customerDetails;
    private SortOrder sortOrder;

    public Seller(String name, String email, String password) {
        super(name, email, password);
        //TODO implement
    }

    public ArrayList<Customer> listCustomers(ArrayList<User> userList) {
        ArrayList<Customer> customerList = new ArrayList<Customer>();
        for(int i = 0; i < userList.size(); i++) {
            if(userList.get(i) instanceof Customer) {
                customerList.add((Customer) userList.get(i));
            }
        }
        return customerList;
    }

    public static Customer searchCustomers(String searchString, ArrayList<User> userList) {
        for (int i = 0 ; i < userList.size(); i++) {
            boolean unblocked = true;
            User user = userList.get(i);
            if(user instanceof Customer && (user.getName().equalsIgnoreCase(searchString)
                    || user.getEmail().equalsIgnoreCase(searchString))) {
                ArrayList<User> invisible = user.getInvisibleUsers();
                for(int bl = 0; bl < invisible.size(); bl++) {
                    if(user.equals(invisible.get(bl))) {
                        unblocked = false;
                        break;
                    }
                }
                if(unblocked) {
                    return (Customer) user;
                }
            }
        }
        return null;
    }

    public ArrayList<Customer> sortCustomer(SortOrder sortingStyle) {
        ArrayList<Customer> sortCustomer = new ArrayList<Customer>();
        ArrayList<Integer> numMessages = new ArrayList<Integer>();
        for(int i = 0; i < AccountHandler.getConversationList().size(); i++) {
            Conversation conversation = AccountHandler.getConversationList().get(i);
            int numLines = 0;
            if(conversation.isParticipant(this)) {
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(conversation.getFileName()));
                    String line = reader.readLine();
                    while(line != null) {
                        Message msg = new Message(line);
                        if(msg.getSender().getEmail().equals(conversation.getCustomer().getEmail())) {
                            numLines++;
                        }
                        line = reader.readLine();
                    }
                    sortCustomer.add(conversation.getCustomer());
                    numMessages.add(numLines);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    System.out.println("File not found");
                    return null;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }

            }
        }

        switch(sortingStyle) {
            case ASCENDING:
                for(int i = 0; i < numMessages.size(); i++) {
                    for(int j = numMessages.size() - 1; j > i; j--) {
                        if(numMessages.get(i) < numMessages.get(i)) {
                            int tempInt = numMessages.get(i);
                            Customer tempCustomer = sortCustomer.get(i);

                            numMessages.set(i, numMessages.get(j));
                            sortCustomer.set(i, sortCustomer.get(j));

                            numMessages.set(j, tempInt);
                            sortCustomer.set(j, tempCustomer);
                        }
                    }
                }
                return sortCustomer;

            case DESCENDING:
                for(int i = 0; i < numMessages.size(); i++) {
                    for(int j = numMessages.size() - 1; j > i; j--) {
                        if(numMessages.get(i) > numMessages.get(i)) {
                            int tempInt = numMessages.get(i);
                            Customer tempCustomer = sortCustomer.get(i);

                            numMessages.set(i, numMessages.get(j));
                            sortCustomer.set(i, sortCustomer.get(j));

                            numMessages.set(j, tempInt);
                            sortCustomer.set(j, tempCustomer);
                        }
                    }
                }
                return sortCustomer;

            default:
                System.out.println("No sorting method provided");
                return null;
        }
    }

    public void addStore(Store store) {
        stores.add(store);
    }

}
