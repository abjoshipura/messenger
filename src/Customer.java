/**
 * Template class (extends User) for Customers. Stores elementary details about a Customer. Holds the same set of
 * information in User.
 * <br>
 * instanceOf Customer is used to provide a Customer their customized permissions where required.
 *
 * @author Akshara Joshipura
 * @version 27 November 2022
 */

public class Customer extends User {
    /**
     * Parses a Customer object's String and converts it into a Customer object. Used while reading Customer(s)
     * from memory. Inherently calls {@link User#User(String userString, boolean hasDetails)} to instantiate inherited
     * fields to their values. <br> <br>
     * Possible customerString values dependent on memory: <br>
     * hasDetails == false => {@link User#toString()} <br>
     * hasDetails == true => {@link Customer#detailedToString()}
     *
     * @param customerString A Customer String
     * @param hasDetails     Whether the Customer String is detailed (i.e. it contains blocked user, invisible user,
     *                       and censored words)
     * @see User#toString()
     * @see Customer#detailedToString()
     */
    public Customer(String customerString, boolean hasDetails) {
        super(customerString, hasDetails);
    }

    /**
     * Creates a new Customer object with provided parameters. Inherently calls {@link User#User(String name,
     * String email, String password)} Used when creating a new Customer account.
     *
     * @param name     The name of the customer
     * @param email    The email of the customer
     * @param password The password of the customer
     */
    public Customer(String name, String email, String password) {
        super(name, email, password);
    }

    /**
     * Generates a formatted String of the Customer containing all details.
     * <br> <br>
     * General format: <br>
     * Customer&lt;username, email, password, this.getBlockedUsers(), this.getInvisibleUsers(),
     * this.getCensoredWords()&gt;
     *
     * @return Returns the Customer object's String
     */
    public String detailedToString() {
        return String.format("Customer<%s, %s, %s, %b, %s, %s, %s>", this.getUsername(), this.getEmail(),
                this.getPassword(), this.isRequestsCensorship(), this.getBlockedUsers(), this.getInvisibleUsers(),
                this.getCensoredWords());
    }

    /**
     * Generates a formatted String of the Customer as a User object String by inherently calling
     * {@link User#toString()}
     *
     * @return Returns the Customer object's User object String
     */
    @Override
    public String toString() {
        return super.toString();
    }
}
