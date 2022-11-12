import java.util.*;

public class Message {
    private String timeStamp;
    private final UUID id;
    private String message;
    private final User sender;
    private final User recipient;
    private boolean senderVisibility;
    private boolean recipientVisibility;
    private boolean isDisappearing;

    public Message(String messageString) {
        String strippedMessage = messageString.substring(messageString.indexOf("<") + 1,
                messageString.lastIndexOf(">"));

        String[] messageDetails = strippedMessage.split(", ");
        this.timeStamp = messageDetails[0];
        this.id = UUID.fromString(messageDetails[1]);

        int elementsInUserString = 3;
        int indexOfNextElem = 2;
        StringBuilder senderString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            senderString.append(messageDetails[i]).append(", ");
        }
        this.sender = new User(senderString.toString());
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        StringBuilder recipientString = new StringBuilder();
        for (int i = indexOfNextElem; i < indexOfNextElem + elementsInUserString; i++) {
            recipientString.append(messageDetails[i]).append(", ");
        }
        this.recipient = new User(recipientString.toString());
        indexOfNextElem = indexOfNextElem + elementsInUserString;

        this.message = messageDetails[indexOfNextElem++];
        this.senderVisibility = Boolean.parseBoolean(messageDetails[indexOfNextElem++]);
        this.recipientVisibility = Boolean.parseBoolean(messageDetails[indexOfNextElem++]);
        this.isDisappearing = Boolean.parseBoolean(messageDetails[indexOfNextElem++]);
    }

    public Message(String timeStamp, String message, User sender, User recipient, boolean isDisappearing) {
        this.timeStamp = timeStamp;
        this.id = UUID.randomUUID();
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;

        this.senderVisibility = true;
        this.recipientVisibility = true;
        this.isDisappearing = isDisappearing;
    }

    public String toString() {
        return String.format("Message<%s, %s, %s, %s, %s, %b, %b, %b>", this.timeStamp, this.id, this.sender, this.recipient, this.message,
                this.senderVisibility, this.recipientVisibility, this.isDisappearing);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message message1)) return false;
        return senderVisibility == message1.senderVisibility && recipientVisibility == message1.recipientVisibility
                && isDisappearing == message1.isDisappearing && id.equals(message1.id) && message.equals(message1.message)
                && sender.equals(message1.sender) && recipient.equals(message1.recipient);
    }

    @Override
    public int hashCode() {
        return 0;
    }

    public UUID getID() {
        return this.id;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSenderVisibility() {
        return senderVisibility;
    }

    public void setSenderVisibility(boolean senderVisibility) {
        this.senderVisibility = senderVisibility;
    }

    public boolean isRecipientVisibility() {
        return recipientVisibility;
    }

    public void setRecipientVisibility(boolean recipientVisibility) {
        this.recipientVisibility = recipientVisibility;
    }

    public boolean isDisappearing() {
        return isDisappearing;
    }

    public void setDisappearing(boolean isDisappearing) {
        this.isDisappearing = isDisappearing;
    }
}
