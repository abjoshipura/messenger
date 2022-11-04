import java.util.ArrayList;
import java.util.Map;

public class Seller extends User {
    private ArrayList<Store> stores;
    private Map<Customer, ArrayList<Message>> customerDetails;
    private SortOrder sortOrder;

    public Seller(String name, String email) {
        super(name, email);
        //TODO implement
    }

    public ArrayList<Customer> listCustomers() {
        //TODO implement
        return null;
    }

    public Customer searchCustomers(String searchString) {
        //TODO implement
        return null;
    }
}
