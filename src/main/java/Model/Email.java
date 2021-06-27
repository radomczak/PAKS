package Model;

public class Email {
    private String sender;
    private String receiver;
    private String message;

    public Email(String sender, String receiver, String message) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String textVersion() {
        StringBuilder builder = new StringBuilder();
        builder.append(sender).append(";").append(receiver).append(";").append(message);
        return builder.toString();
    }
}
