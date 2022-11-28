import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Template class (extends User) for Sellers. Stores elementary details about a Seller. Holds a list of Stores owned by
 * the Seller in addition to the set of information in User.
 * <br>
 * instanceOf Seller is used to provide a Seller their customized permissions where required.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */
public class Seller extends User {

    /**
     * The stores owned by the Seller
     */
    private ArrayList<Store> stores;

    /**
     * Parses a Seller object's String and converts it into a Seller object. Used while reading Seller(s)
     * from memory. Inherently calls {@link User#User(String userString, boolean hasDetails)} to instantiate inherited
     * fields to their values.
     * <br> <br>
     * Possible sellerString values dependent on memory: <br>
     * hasDetails == false => {@link User#toString()} <br>
     * hasDetails == true and hasStores == false => {@link Seller#detailedToStringWithoutStores()} <br>
     * hasDetails == true and hasStores == true => {@link Seller#detailedToString()}
     *
     * @param sellerString A Seller String
     * @param hasDetails   Whether the Seller String is detailed (i.e. it contains blocked user, invisible user,
     *                     and censored words)
     * @param hasStores    Whether the Seller String is detailed and has stores (e.g. in Store object strings Seller(s)
     *                     DO NOT contain stores)
     * @see User#toString()
     * @see Seller#detailedToStringWithoutStores()
     * @see Seller#detailedToString()
     */
    public Seller(String sellerString, boolean hasDetails, boolean hasStores) {
        super(sellerString, hasDetails);
        if (hasStores) {
            this.stores = new ArrayList<>();

            // Strips sellerString to a String containing only Store object Strings
            for (int i = 0; i < 3; i++) {
                sellerString = sellerString.substring(sellerString.indexOf("]") + 3);
            }
            String storesString = sellerString.substring(sellerString.indexOf("[") + 1,
                    sellerString.lastIndexOf("]"));

            if (storesString.length() > 0) {
                // Parses the Store object String to pass each individual String to the Store constructor
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

    /**
     * Creates a new Seller object with provided parameters. Inherently calls {@link User#User(String username,
     * String email, String password)} Used when creating a new Seller account.
     *
     * @param username The username of the seller
     * @param email    The email of the seller
     * @param password The password of the seller
     */
    public Seller(String username, String email, String password) {
        super(username, email, password);
        this.stores = new ArrayList<>();
    }

    /**
     * Accessor method for ArrayList&lt;Store&gt; stores
     *
     * @return Returns the seller's owned Store(s)
     */
    public ArrayList<Store> getStores() {
        return this.stores;
    }

    /**
     * Adds the parameter store to ArrayList&lt;Store&gt; stores.
     * Sends request to update passwords.txt for later retrieval.
     *
     * @param writer The PrintWriter object to be used to send the network request
     * @param store  The new store to be added
     */
    public void addStore(PrintWriter writer, Store store) {
        String oldUserString = this.detailedToString();
        this.stores.add(store);
        String newUserString = this.detailedToString();

        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.PASSWORD_FILE_PATH + ";" + oldUserString +
                ";" + newUserString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Removes the parameter store from ArrayList&lt;Store&gt; stores.
     * Sends request to update passwords.txt for later retrieval.
     *
     * @param writer The PrintWriter object to be used to send the network request
     * @param store  The new store to be removed
     */
    public void removeStore(PrintWriter writer, Store store) {
        String oldUserString;
        String newUserString;

        oldUserString = this.detailedToString();
        this.stores.remove(store);
        newUserString = this.detailedToString();

        String updateAccountsRequest = "[FILE.UPDATE]" + MessengerClient.PASSWORD_FILE_PATH + ";" + oldUserString +
                ";" + newUserString;
        MessengerClient.sendRequest(writer, updateAccountsRequest);
    }

    /**
     * Generates a formatted String of the Seller containing all details.
     * <br> <br>
     * General format: <br>
     * Seller&lt;username, email, password, this.getBlockedUsers(), this.getInvisibleUsers(), this.getCensoredWords(),
     * this.stores&gt;
     *
     * @return Returns the Seller object's String
     */
    public String detailedToString() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords(), this.stores);
    }

    /**
     * Generates a formatted String of the Seller containing all details ASIDE FROM ArrayList&lt;Store&gt; stores.
     * <br> <br>
     * General format: <br>
     * Seller&lt;username, email, password, this.getBlockedUsers(), this.getInvisibleUsers(),
     * this.getCensoredWords()&gt;
     *
     * @return Returns the Seller object's String
     */
    public String detailedToStringWithoutStores() {
        return String.format("Seller<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    /**
     * Generates a formatted String of the Seller as a User object String by inherently calling {@link User#toString()}
     *
     * @return Returns the Seller object's User object String
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
