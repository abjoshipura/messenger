import java.util.Objects;

/**
 * Template class for all stores that can be created, edited, or deleted by a Seller. Stores elementary details about
 * a store.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */

public class Store {

    /**
     * The name of the store
     */
    private String storeName;

    /**
     * The seller details of the owner of the store
     */
    private Seller seller;

    /**
     * Parses a Store object's String and converts it into a Store object. Used while reading User(s) and
     * Conversation(s) from memory. Inherently calls {@link Seller#Seller(String sellerString, boolean hasDetails,
     * boolean hasStores)}
     *
     * @param storeString A Store String
     * @see Store#toString() Store toString()
     */
    public Store(String storeString) {
        // Strips the header from the Store object String to help in parsing
        storeString = storeString.substring(storeString.indexOf("<") + 1, storeString.lastIndexOf(">"));
        String[] storeElements = storeString.split(", ");

        this.storeName = storeElements[0];

        // Parses storeElements[] to build a Seller object String that can be passed to the Seller constructor
        StringBuilder sellerString = new StringBuilder();
        for (int i = 1; i < storeElements.length; i++) {
            sellerString.append(storeElements[i]).append(", ");
        }
        this.seller = new Seller(sellerString.toString(), true, false);
    }

    /**
     * Creates a new Store object with provided parameters. Used when creating a new Store(s) before adding to
     * Seller strings in passwords.txt and conversations.txt.
     *
     * @param storeName The name of the Store object to be created
     * @param seller    The owner's Seller object
     */
    public Store(String storeName, Seller seller) {
        this.storeName = storeName;
        this.seller = seller;
    }

    /**
     * Accessor method for String storeName
     *
     * @return Returns the store's name
     */
    public String getStoreName() {
        return this.storeName;
    }

    /**
     * Mutator method for String storeName.
     *
     * @param storeName The new name for the store. Used during editing.
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * Accessor method for Seller seller
     *
     * @return Returns the store's owner
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * Mutator method for Seller seller.
     *
     * @param seller The new owner for the store.
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * Checks the equality of two Store objects. Compares all class fields.
     *
     * @param o Object to be compared with
     * @return Returns whether the equality condition was met
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storeName, store.storeName) && Objects.equals(seller, store.seller);
    }

    /**
     * Generates a formatted String of the Store containing all details.
     * <br> <br>
     * General format: <br>
     * Store&lt;storeName, sender.detailedToStringWithoutStores()&gt;
     *
     * @return Returns the Store object's String
     */
    @Override
    public String toString() {
        return String.format("Store<%s, %s>", this.storeName, this.seller.detailedToStringWithoutStores());
    }
}
