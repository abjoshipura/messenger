import java.util.ArrayList;

public class Seller extends User {
    // List of all stores that the Seller owns
    private ArrayList<Store> stores;

    // Creates a preexisting seller based on a formatted String.
    public Seller(String sellerString, boolean hasDetails, boolean hasStores) {
        super(sellerString, hasDetails);
        if (hasStores) {
            for (int i = 0; i < 3; i++) {
                sellerString = sellerString.substring(sellerString.indexOf("]") + 3);
            }
            this.stores = new ArrayList<>();
            String storesString = sellerString.substring(sellerString.indexOf("[") + 1,
                    sellerString.lastIndexOf("]"));
            if (storesString.length() > 0) {
                while (!storesString.isEmpty()) {
                    String singularStore = storesString.substring(0, storesString.indexOf("]>>") + 3);
                    int nextIndex = Math.min(storesString.indexOf("]>>") + 5, storesString.length());
                    storesString = storesString.substring(nextIndex);

                    this.stores.add(new Store(singularStore));
                }
            }
        } else {
            this.stores = new ArrayList<>();
        }
    }

    // Creates a new Seller object
    public Seller(String name, String email, String password) {
        super(name, email, password);
        this.stores = new ArrayList<>();
    }

    public ArrayList<Store> getStores() {
        return this.stores;
    }

    public void addStore(Store store) {
        String oldUserString;
        String newUserString;

        oldUserString = this.detailedToString();
        this.stores.add(store);
        newUserString = this.detailedToString();

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public void removeStore(Store store) {
        String oldUserString;
        String newUserString;

        oldUserString = this.detailedToString();
        this.stores.remove(store);
        newUserString = this.detailedToString();

        AccountsMaster.replaceStringInFile(Main.passwordFilePath, oldUserString, newUserString);
        AccountsMaster.replaceStringInFile(Main.conversationsFilePath, oldUserString, newUserString);
    }

    public String detailedToString() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords(), this.stores);
    }

    public String detailedToStringWithoutStores() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    public String toString() {
        return super.toString();
    }
}
