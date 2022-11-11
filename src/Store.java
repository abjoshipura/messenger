public class Store {
    private Seller seller;
    public Store (Seller seller) {
        this.seller = seller;
        seller.addStore(this);
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {

        this.seller = seller;
    }
}
