import java.util.ArrayList;

public class Seller extends User {
    private ArrayList<Store> stores;

    public Seller(String sellerString, boolean hasDetails) {
        super(sellerString, hasDetails);
        if (hasDetails) {
            this.stores = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                sellerString = sellerString.substring(sellerString.indexOf("]") + 3);
            }
            String[] storesArray = sellerString.substring(sellerString.indexOf("[") + 1,
                    sellerString.indexOf("]")).split(", ");
            if (storesArray.length > 1) {
                for (String storeString : storesArray) {
                    this.stores.add(new Store(storeString));
                }
            }
        } else {
            this.stores = new ArrayList<>();
        }
    }

    public Seller(String name, String email, String password) {
        super(name, email, password);
        this.stores = new ArrayList<>();
    }

    public String detailedToString() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords(), this.stores);
    }

    public String toString() {
        return super.toString();
    }

    public ArrayList<Store> getStores() {
        return this.stores;
    }
}
