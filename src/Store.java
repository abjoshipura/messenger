import java.util.Objects;
/**
 * Store
 *
 * The Store class behaves as a template
 * for any stores that can be created, edited,
 * or deleted by a Seller. It holds the
 * elementary details about a store: store
 * name, the owner (a Seller object).
 *
 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class Store {
    private String storeName;
    private Seller seller;
    
/*
 * public Store(String storeString)
 * Constructor that converts a Store
 * object String into a Store object.
 * This is used while reading Users and
 * Conversations from memory.
 *
 * @param storeString the String to be converted into a Store object.
 */
    public Store(String storeString) {
        storeString = storeString.substring(storeString.indexOf("<") + 1, storeString.lastIndexOf(">"));
        String[] splitStoreString = storeString.split(", ");
        this.storeName = splitStoreString[0];

        StringBuilder sellerString = new StringBuilder();
        for (int i = 1; i < splitStoreString.length; i++) {
            sellerString.append(splitStoreString[i]).append(", ");
        }
        this.seller = new Seller(sellerString.toString(), true, false);
    }
    
/*
 * public Store(String storeName, Seller seller)
 * Constructor that creates a new Store object
 * based on input. This is used while creating
 * new Stores before adding to Seller strings
 * in passwords.txt and conversations.txt.
 * 
 * @param storeName the name of the Store object to be created
 * @param seller the user owning the store
 */
    public Store(String storeName, Seller seller) {
        this.storeName = storeName;
        this.seller = seller;
    }

    /**
     * public String getStoreName()
     * Getter for the store name.
     *
     * @return storeName
     */
    public String getStoreName() {
        return this.storeName;
    }

    /**
     * public void setStoreName(String storeName)
     * Sets the store name with the passed in String.
     *
     * @param storeName the String for the store's new name
     */
    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    /**
     * public Seller getSeller()
     * Returns the seller.
     *
     * @return seller
     */
    public Seller getSeller() {
        return seller;
    }

    /**
     * public void setSeller(Seller seller)
     * Sets seller as the Seller object passed in.
     *
     * @param seller the new seller
     */
    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    /**
     * public boolean equals(Object o)
     * Checks whether or not two sellers are equal based on the seller and their store names.
     *
     * @param o the object to be compared for equality
     * @return whether or not the two objects are equal
     */
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storeName, store.storeName) && Objects.equals(seller, store.seller);
    }

    /**
     * public String toString()
     * Returns the formatted String representing the store.
     * Includes its name and the owner, the seller.
     *
     * @return the formatted String containing store name and seller information
     */
    public String toString() {
        return String.format("Store<%s, %s>", this.storeName, this.seller.detailedToStringWithoutStores());
    }
}
