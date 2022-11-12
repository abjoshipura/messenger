import java.io.*;
import java.util.ArrayList;

public class Conversation {
    private String fileName;
    /*
    EMAIL1(buyer),EMAIL2(seller)
     */
    private Customer customer;
    private Seller seller;

    private boolean isDisappearing;

    private ArrayList<Message> msgList;

    public Conversation(String title, String fileName, Customer customer, Seller seller) {
        File convo = new File(fileName);
        this.fileName = fileName;
        this.customer = customer;
        this.seller = seller;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            convo.createNewFile();
            bw.write(String.format("%s,%s,%s", title, customer.getEmail(), seller.getEmail()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Conversation(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String[] lineArray = br.readLine().split(",");
            this.fileName = fileName;
            this.customer = Seller.searchCustomers(lineArray[0], AccountHandler.getUserArrayList());
            this.seller = Customer.searchSeller(lineArray[1], AccountHandler.getUserArrayList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<String> readFile(User user) {
        //TODO implement
        //TODO Individual messages should be labeled with the senders name in the conversation.
        ArrayList<String> ret = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line = br.readLine();
            while (line != null) {
                Message temp = new Message(line);
                if (user instanceof Customer && temp.isBuyerVisibility()) {
                    ret.add(line);
                } else if (user instanceof Seller && temp.isSellerVisibility()) {
                    ret.add(line);
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addMessage(Message msg) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true));
        bw.write(msg.toString());
        bw.close();
    }

    public String censorFile() {
        return null;
    }

    public void removeParticipant(User user) {
        //TODO Handle deleted user
    }

    public boolean isParticipant(User user) {
        for(int i = 0; i < customer.getBlockedUsers().size(); i++) {
            if(user.equals(customer.getBlockedUsers().get(i))) {
                return false;
            }
        }
        for(int i = 0; i < seller.getBlockedUsers().size(); i++) {
            if(user.equals(seller.getBlockedUsers().get(i))) {
                return false;
            }
        }

        return user.equals(customer) || user.equals(seller);
    }

    public String getFileName() {
        return fileName;
    }

    public Seller getSeller() {
        return seller;
    }

    public Customer getCustomer() {
        return customer;
    }
}
