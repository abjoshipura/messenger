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
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i) instanceof Customer) {
                customerList.add((Customer) userList.get(i));
            }
        }
        return customerList;
    }

    public Customer searchCustomers(String searchString, ArrayList<User> userList) {
        for (int i = 0; i < userList.size(); i++) {
            boolean unblocked = true;
            User user = userList.get(i);
            if (user instanceof Customer && (user.getUsername().equalsIgnoreCase(searchString)
                    || user.getEmail().equalsIgnoreCase(searchString))) {
                ArrayList<User> invisible = user.getInvisibleUsers();
                for (int bl = 0; bl < invisible.size(); bl++) {
                    if (user.equals(invisible.get(bl))) {
                        unblocked = false;
                        break;
                    }
                }
                if (unblocked) {
                    return (Customer) user;
                }
            }
        }
        return null;
    }


}
