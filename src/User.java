import java.util.ArrayList;
import java.util.Map;
import java.io.*;
import java.util.Objects;

public class User {
    private String name; 
    private String email; //TODO ensure uniqueness
    private String password;
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;

    private boolean requestsCensorship;
    private Map<String, String> censoredWords;

    private boolean hasNewMessages;



    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        blockedUsers = new ArrayList<>();
        invisibleUsers = new ArrayList<>();
        requestsCensorship = false;

        hasNewMessages = false;
    }

    public boolean sendMessageToUser(String message, User user) {
        try {
            String fileName = null;
            String title = "";
            if(user instanceof Customer) {
                fileName = user.name.replace(" ", "_") + "&" +
                        this.name.replace(" ", "_") + ".txt";
                title = user.name + " & " + this.name;
            } else {
                fileName = this.name.replace(" ", "_") + "&" +
                        user.name.replace(" ", "_") + ".txt";
                title = this.name + " & " + user.name;
            }
            File newMessage = new File(fileName);
            newMessage.createNewFile();

            FileWriter fileWriter = new FileWriter(fileName);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            boolean convoAlreadyExists = false;
            for(int i = 0; i < AccountHandler.getConversationList().size(); i++) {
                if(AccountHandler.getConversationList().get(i).getTitle().equals(title)) {
                    break;
                }
            }
            if(!convoAlreadyExists) {
                if (this instanceof Customer) {
                    Conversation conversation = new Conversation(title, fileName, (Customer) this, (Seller) user);
                } else if (this instanceof Seller) {
                    Conversation conversation = new Conversation(title, fileName, (Customer) user, (Seller) this);
                }
            }
            Message sentMessage = new Message(message); //Creates new message. String parameter message must be parseable by the constructor
            printWriter.println(sentMessage + "\n");
            printWriter.close();

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendMessageToConversation(String message, Conversation conversation) {
        try {
            FileWriter fileWriter = new FileWriter(conversation.getFileName(), true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.println(message + "\n");
            printWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMessage(String messageID, String newMessage) {
        ArrayList<Conversation> tempList = AccountHandler.getConversationList();
        try {
            Message replace = new Message(newMessage);
            for (int i = 0; i < tempList.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(tempList.get(i).getFileName()));
                FileWriter fileWriter = new FileWriter(tempList.get(i).getFileName(), false);
                if (tempList.get(i).isParticipant(this)) {
                    String title = reader.readLine();
                    String line = reader.readLine();
                    ArrayList<Message> addMessage = new ArrayList<Message>();
                    while (line != null) {
                        // Create message object that is parsed and generated within constructor
                        Message msg = new Message(line);
                        addMessage.add(msg);
                        line = reader.readLine();
                    }
                    String theWholeFile = title + "\n";
                    for(int index = 0; index < addMessage.size(); index++) {
                        if(addMessage.get(index).getId().equals(messageID)) {
                            addMessage.set(index, replace);
                        }
                        theWholeFile += addMessage.get(index).toString() + "\n";

                    }
                    fileWriter.write(theWholeFile);
                }
                reader.close();
                fileWriter.flush();
                fileWriter.close();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteMessage(String messageID) {
        ArrayList<Conversation> tempList = AccountHandler.getConversationList();
        try {
            for (int i = 0; i < tempList.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(tempList.get(i).getFileName()));
                FileWriter fileWriter = new FileWriter(tempList.get(i).getFileName(), false);
                if (tempList.get(i).isParticipant(this)) {
                    String title = reader.readLine();
                    String line = reader.readLine();
                    ArrayList<Message> addMessage = new ArrayList<Message>();
                    while (line != null) {
                        Message msg = new Message(line);
                        addMessage.add(msg);
                        line = reader.readLine();
                    }
                    String theWholeFile = title + "\n";
                    for(int index = 0; index < addMessage.size(); index++) {
                        if(addMessage.get(index).getId().equals(messageID)) {
                            addMessage.remove(index);
                            index--;
                            continue;
                        }
                        theWholeFile += addMessage.get(index).toString() + "\n";

                    }
                    fileWriter.write(theWholeFile);
                }
                reader.close();
                fileWriter.flush();
                fileWriter.close();
            }
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void blockUser(User user) {
        blockedUsers.add(user);
    }

    public void becomeInvisibleToUser(User user) {
        invisibleUsers.add(user);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(ArrayList<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public ArrayList<User> getInvisibleUsers() {
        return invisibleUsers;
    }

    public void setInvisibleUsers(ArrayList<User> invisibleUsers) {
        this.invisibleUsers = invisibleUsers;
    }

    public boolean isRequestsCensorship() {
        return requestsCensorship;
    }

    public void setRequestsCensorship(boolean requestsCensorship) {
        this.requestsCensorship = requestsCensorship;
    }

    public Map<String, String> getCensoredWords() {
        return censoredWords;
    }

    public void setCensoredWords(Map<String, String> censoredWords) {
        this.censoredWords = censoredWords;
    }
    public boolean isHasNewMessages() {
        return hasNewMessages;
    }

    public void setHasNewMessages(boolean hasNewMessages) {
        this.hasNewMessages = hasNewMessages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return isRequestsCensorship() == user.isRequestsCensorship() && isHasNewMessages() == user.isHasNewMessages() && getName().equals(user.getName()) && getEmail().equals(user.getEmail()) && getPassword().equals(user.getPassword()) && Objects.equals(getBlockedUsers(), user.getBlockedUsers()) && Objects.equals(getInvisibleUsers(), user.getInvisibleUsers()) && Objects.equals(getCensoredWords(), user.getCensoredWords());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getEmail(), getPassword(), getBlockedUsers(), getInvisibleUsers(), isRequestsCensorship(), getCensoredWords(), isHasNewMessages());
    }
}
