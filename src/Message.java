public class Message {
    private String message;
    private User sender;
    private User recepient;
    private String id;
    private boolean buyerVisibility;
    private boolean sellerVisibility;
    private boolean isDisappearing;

    public Message(String messageString) {
        //TODO implement
    }

    public Message(String message, User sender, User recepient, boolean isDisappearing, boolean buyerVisibility,
                   boolean sellerVisibility) {
        //TODO implement using generateID();
    }

    private String generateID() {
        return "";
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isBuyerVisibility() {
        return buyerVisibility;
    }

    public void setBuyerVisibility(boolean buyerVisibility) {
        this.buyerVisibility = buyerVisibility;
    }

    public boolean isSellerVisibility() {
        return sellerVisibility;
    }

    public void setSellerVisibility(boolean sellerVisibility) {
        this.sellerVisibility = sellerVisibility;
    }

    public String toString() {
        return id + ";" + sender.getName() + ";" + message;
    }
}
