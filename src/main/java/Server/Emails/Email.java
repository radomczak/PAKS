package Server.Emails;

public class Email {
    String client;
    String message;

    public Email(String client, String message) {
        this.client = client;
        this.message = message;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    
}
