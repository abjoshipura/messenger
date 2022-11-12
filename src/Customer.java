import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Customer extends User {
    private Map<Store, Integer> storeDetailsBySent;
    private Map<Store, ArrayList<Message>> storeDetailsByReceived;
    private SortOrder sortOrder;

    public Customer(String name, String email, String password) {
        super(name, email, password);
        //TODO implement
    }

    public ArrayList<Customer> listStores() {
        //TODO implement
        return null;
    }

    public static Seller searchSeller(String searchString, ArrayList<User> userList) {
        for (int i = 0 ; i < userList.size(); i++) {
            boolean unblocked = true;
            User user = userList.get(i);
            if(user instanceof Seller && (user.getName().equalsIgnoreCase(searchString)
                    || user.getEmail().equalsIgnoreCase(searchString))) {
                ArrayList<User> invisible = user.getInvisibleUsers();
                for(int bl = 0; bl < invisible.size(); bl++) {
                    if(user.equals(invisible.get(bl))) {
                        unblocked = false;
                        break;
                    }
                }
                if(unblocked) {
                    return (Seller) user;
                }
            }
        }
        return null;
    }

    public boolean sendMessageStore(Store store, String message) {
        ArrayList<Store> listOfStores = AccountHandler.getStoreArrayList();
        for(int i = 0; i < listOfStores.size(); i++) {
            if(store.equals(listOfStores.get(i))) {
                super.sendMessageToUser(message, store.getSeller());
                store.alreadyContacted(this);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Store> sortStores(SortOrder sortingStyle) {
        ArrayList<Store> sortStores = new ArrayList<Store>();
        ArrayList<Integer> numMessages = new ArrayList<Integer>();
        for(int i = 0; i < AccountHandler.getStoreArrayList().size(); i++) {
            Store storeSearch = AccountHandler.getStoreArrayList().get(i);
            ArrayList<Integer> numTimesContacted = storeSearch.getNumTimesContacted();
            for(int contact = 0; contact < storeSearch.getContacts().size(); contact++) {
                Customer messenger = storeSearch.getContacts().get(contact);
                if(this.getEmail().equals(messenger.getEmail())) {
                    sortStores.add(storeSearch);
                    numMessages.add(numTimesContacted.get(i));
                }
            }
        }

        switch(sortingStyle) {
            case ASCENDING:
                for(int i = 0; i < numMessages.size(); i++) {
                    for(int j = numMessages.size() - 1; j > i; j--) {
                        if(numMessages.get(i) < numMessages.get(i)) {
                            int tempInt = numMessages.get(i);
                            Store tempStore = sortStores.get(i);

                            numMessages.set(i, numMessages.get(j));
                            sortStores.set(i, sortStores.get(j));

                            numMessages.set(j, tempInt);
                            sortStores.set(j, tempStore);
                        }
                    }
                }
                return sortStores;

            case DESCENDING:
                for(int i = 0; i < numMessages.size(); i++) {
                    for(int j = numMessages.size() - 1; j > i; j--) {
                        if(numMessages.get(i) > numMessages.get(i)) {
                            int tempInt = numMessages.get(i);
                            Store tempStore = sortStores.get(i);

                            numMessages.set(i, numMessages.get(j));
                            sortStores.set(i, sortStores.get(j));

                            numMessages.set(j, tempInt);
                            sortStores.set(j, tempStore);
                        }
                    }
                }
                return sortStores;

            default:
                return null;
        }
    }

}
