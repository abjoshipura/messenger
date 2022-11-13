public class Store {
    private String storeName;
    private Seller seller;

    public Store(String storeString) {
        storeString = storeString.substring(storeString.indexOf("<") + 1, storeString.lastIndexOf(">"));
        String[] splitTest = storeString.split(", ");
        this.storeName = splitTest[0];
        this.seller = new Seller(splitTest[1], false);
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

    public String toString() {
        return String.format("Store<%s, %s>", this.storeName, this.seller);
    }
}
