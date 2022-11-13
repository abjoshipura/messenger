public class Customer extends User {
    public Customer(String customerString, boolean hasDetails) {
        super(customerString, hasDetails);
    }

    public Customer(String name, String email, String password) {
        super(name, email, password);
    }

    public String detailedToString() {
        return String.format("Customer<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    public String toString() {
        return super.toString();
    }
}
