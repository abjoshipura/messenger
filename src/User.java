import java.util.ArrayList;
import java.util.Map;
import java.io.*;

public class User {
    private String name; 
    private String email; //TODO ensure uniqueness
    private String password;
    private ArrayList<User> blockedUsers;
    private ArrayList<User> invisibleUsers;

    private boolean requestsCensorship;
    private Map<String, String> censoredWords;

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public boolean sendMessageToUser(String message, User user) {
        try {
            String fileName = null;
            String title;
            if(user instanceof Customer) {
                fileName = user.name.replace(" ", "_") + "&" +
                        this.name.replace(" ", "_") + ".txt";
                title = user.name + " & " + this.name;
            } else if(user instanceof Seller) {
                fileName = this.name.replace(" ", "_") + "&" +
                        user.name.replace(" ", "_") + ".txt";
                title = this.name + " & " + user.name;
            }
            File newMessage = new File(fileName);

            FileWriter fileWriter = new FileWriter(newMessage);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            Message sentMessage = new Message(message, this, user, false, true, true);
            printWriter.println(sentMessage);
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
            printWriter.println(message);
            printWriter.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMessage(String messageID, String newMessage) {
        ArrayList<Conversation> tempList = AccountHandler.getConversationList();
        // This is probably all very inefficient, but it's how I did Project 3, and it worked there, so...
        try {

            for (int i = 0; i < tempList.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(tempList.get(i).getFileName()));
                FileWriter fileWriter = new FileWriter(tempList.get(i).getFileName(), false);
                if (tempList.get(i).isParticipant(this)) {
                    String line = reader.readLine();
                    ArrayList<String[]> addLines = new ArrayList<String[]>();
                    while (line != null) {
                        String[] messageComponents = line.split(";");
                        addLines.add(messageComponents);
                        line = reader.readLine();
                    }
                    String theWholeDamnFile = "";
                    for(int index = 0; index < addLines.size(); index++) {
                        if(addLines.get(index)[0].equals(messageID)) {
                            addLines.get(index)[1] = newMessage;
                        }
                        theWholeDamnFile += String.join(";", addLines.get(index)[0], addLines.get(index)[1], addLines.get(index)[2]) + "\n";
                        fileWriter.write(theWholeDamnFile);
                    }
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
                    String line = reader.readLine();
                    ArrayList<String[]> addLines = new ArrayList<String[]>();
                    while (line != null) {
                        String[] messageComponents = line.split(";");
                        addLines.add(messageComponents);
                        line = reader.readLine();
                    }
                    String theWholeDamnFile = "";
                    for(int index = 0; index < addLines.size(); index++) {
                        if(addLines.get(index)[0].equals(messageID)) {
                            continue;
                        }
                        theWholeDamnFile += String.join(";", addLines.get(index)[0], addLines.get(index)[1], addLines.get(index)[2]) + "\n";
                        fileWriter.write(theWholeDamnFile);
                    }
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
        //TODO implement
    }

    public void becomeInvisibleToUser(User user) {
        //TODO implement
    }

    public String getName() {
        return name;
    }
}
