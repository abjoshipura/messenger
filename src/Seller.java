import java.util.ArrayList;
/**
 * Seller
 *
 * The Seller class extends User to behave as a
 * specific template for customers. It holds a list of
 * Stores owned by the Seller in addition to the
 * set of information in User. instanceOf Seller
 * is used to provide a Seller their customized
 * permissions where required.
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class Seller extends User {
    private ArrayList<Store> stores;
/*
 * public Seller(String sellerString, boolean hasDetails,
 * boolean hasStores) Constructor that inherently calls
 * super(String userString, boolean hasDetails) to instantiate
 * Seller fields to their values. It parses the deepToString()
 * or super.toString() version of the class written in memory
 * according to hasDetails. Additionally, if hasStores, it
 * updates this.stores by calling the Store constructor
 * public Store(String storeString).
 */
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
/*
 * public Seller(String name, String email, String password)
 * Constructor that inherently calls super(String name,
 * String email, String password) to create a new Seller.
 */
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
/*
 * public String detailedToString() Method that returns
 * a String containing: username, email, password, requestsCensorship,
 * blockedUsers, InvisibleUsers, censoredWords, and stores.
 * This is used in writing into a Seller object String
 * in passwords.txt to store censoring toggle, blocked users,
 * invisible to users, censored word pairs, and owned stores
 * for the next log-in.
 */
    public String detailedToString() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords(), this.stores);
    }
/*
 * public String detailedToStringWithoutStores() Method
 * that returns a String containing ONLY: username,
 * email, password, requestsCensorship, blockedUsers,
 * InvisibleUsers, and censoredWords. This is used in
 * writing into a Store object String in passwords.txt
 * to prevent an infinite nested loop of detailedToString() calls.
 */
    public String detailedToStringWithoutStores() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    public String toString() {
        return super.toString();
    }
}
