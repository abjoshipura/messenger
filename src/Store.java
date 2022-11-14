import java.util.Objects;

public class Store {
    private String storeName;
    private Seller seller;

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

    public Store(String storeName, Seller seller) {
        this.storeName = storeName;
        this.seller = seller;
    }

    public String getStoreName() {
        return this.storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;
        return Objects.equals(storeName, store.storeName) && Objects.equals(seller, store.seller);
    }

    public String toString() {
        return String.format("Store<%s, %s>", this.storeName, this.seller.detailedToStringWithoutStores());
    }
}