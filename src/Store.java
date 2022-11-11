import java.util.ArrayList;

public class Store {
    private String name;
    private Seller seller;
    private ArrayList<Customer> contacts;
    private ArrayList<Integer> numTimesContacted;
    public Store (String name, Seller seller) {
        this.seller = seller;
        this.name = name;
        seller.addStore(this);
        AccountHandler.addStore(this);
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getName() {
        return name;
    }

    public void addContact(Customer customer) {
        contacts.add(customer);
    }

    public boolean alreadyContacted(Customer customer) {
        for(int i = 0; i < contacts.size(); i++) {
            if(customer.getEmail().equals(contacts.get(i).getEmail())) {
                numTimesContacted.set(i, numTimesContacted.get(i) + 1);
                return true;
            }
        }
        contacts.add(customer);
        numTimesContacted.add(1);
        return false;
    }
}
