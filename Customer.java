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

    public Customer searchSeller(String searchString) {
        //TODO implement
        return null;
    }
}
