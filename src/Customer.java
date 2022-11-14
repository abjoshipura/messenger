/**
 * Customer
 *
 * The Customer class extends User to behave as a
 * specific template for customers. It holds the same
 * set of information as User. instanceOf Customer is
 * used to provide a Customer their customized permissions
 * where required.

 * @author Akshara Joshipura, Raymond Wang, Kevin Tang, Yejin Oh
 *
 * @version 11/14/22
 *
 */
public class Customer extends User {
    /*
     * public Customer(String customerString, boolean hasDetails)
     * Constructor that inherently calls
     * super(String userString, boolean hasDetails) to
     * instantiate Customer fields to their values.
     * It parses the deepToString() or super.toString()
     * version of the class written in memory according to hasDetails.
     *
     * @param customerString string for customer
     * @param hasDetails whether or not there should be details
     */
    public Customer(String customerString, boolean hasDetails) {
        super(customerString, hasDetails);
    }

    /*
     * public Customer(String name, String email, String password)
     * Constructor that inherently calls
     * super(String name, String email, String password)
     * to create a new Customer.
     *
     * @param name
     * @param email
     * @param password
     */
    public Customer(String name, String email, String password) {
        super(name, email, password);
    }

    /*
     * public String detailedToString()
     * Method that returns a String containing:
     * username, email, password, requestsCensorship,
     * blockedUsers, InvisibleUsers, and censoredWords.
     * This is used in writing into passwords.txt to
     * store censoring toggle, blocked users,
     * invisible to users, and censored word
     * pairs for the next log-in.
     *
     * @return the detailed string
     */
    public String detailedToString() {
        return String.format("Customer<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    /**
     * public String toString()
     * Returns a formatted String for the customer.
     *
     * @return the formatted String object
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
