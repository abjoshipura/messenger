import java.util.ArrayList;
import java.util.Map;

public class Customer extends User {
    private Map<Store, Integer> storeDetailsBySent;
    private Map<Store, ArrayList<Message>> storeDetailsByReceived;
    private SortOrder sortOrder;

    public Customer(String name, String email, String password) {
        super(name, email, password);
        //TODO implement
    }

    public ArrayList<Customer> listStores() {
        //TODO implement
        return null;
    }

    public Seller searchSeller(String searchString, ArrayList<User> userList) {
        for (int i = 0 ; i < userList.size(); i++) {
            boolean unblocked = true;
            User user = userList.get(i);
            if(user instanceof Seller && (user.getUsername().equalsIgnoreCase(searchString)
                    || user.getEmail().equalsIgnoreCase(searchString))) {
                ArrayList<User> invisible = user.getInvisibleUsers();
                for(int bl = 0; bl < invisible.size(); bl++) {
                    if(user.equals(invisible.get(bl))) {
                        unblocked = false;
                        break;
                    }
                }
                if(unblocked) {
                    return (Seller) user;
                }
            }
        }
        return null;
    }
}
