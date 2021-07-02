package Server;

import Model.Email;
import Server.Emails.EmailManager;
import Server.Exception.UserException;
import Server.Users.UserCredentialsManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

public class ClientHandler extends Thread {

    private final Socket socket;
    private final UserCredentialsManager credentialsManager;
    private final EmailManager emailManager;
    private BufferedReader in;
    private PrintWriter out;
    private String client = null;

    public ClientHandler(Socket socket,UserCredentialsManager credentialsManager, EmailManager emailManager) {
        this.socket = socket;
        this.credentialsManager = credentialsManager;
        this.emailManager = emailManager;
    }

    public void run() {


        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            while (true) {
                String request = in.readLine();
                if(request == null) {}
                else handleRequest(request);
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {}
        }
    }

    private void handleRequest(String request) {
        char requestType = request.charAt(0);
        switch (requestType) {
            case 'v': {         //verification request, subcategories l-login (check password), r-register
                handleVRequest(request.substring(1));
                break;
            }
            case 'r' : {            //email request r-receive emails(refresh button)
                handleRRequest(request.substring(1));
                break;
            }
            case 's' : {            //email request s-sent
                handleSRequest();
                break;
            }
            default: {
                break;
            }
        }
    }

    private void handleVRequest(String request) {
        if(request.startsWith("l"))
            login();
        else if (request.startsWith("r"))
            register();
    }

    private void login() {
        String userName;
        String pass;
        boolean condition = false;

        try {
            userName = in.readLine();
            pass = in.readLine();

            if(credentialsManager.checkPasswordForUser(userName,pass)) {
                condition = true;
                client = userName;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(condition) {
            out.println("isWelcome " + client);
        } else {
            out.println("ifInvalid login or password");
        }
    }

    private void register() {
        String userName;
        String pass;
        boolean condition = false;

        try {
            userName = in.readLine();
            pass = in.readLine();

            synchronized (credentialsManager) {
                if(!credentialsManager.getUsers().containsKey(userName)) {
                    credentialsManager.addUser(userName,pass);
                    condition = true;
                    client = userName;
                }
            }
        } catch (IOException | UserException e) {
            e.printStackTrace();
        }
        if(condition) {
            out.println("isWelcome our new user " + client);
        } else {
            out.println("ifUser with this login already exists");
        }
    }

    private void handleSRequest() {
        String emailAsText = null;
        String[] data;
        String receiver = null;
        try {
            emailAsText = in.readLine();
            data = emailAsText.split(";");
            receiver = data[1];
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(credentialsManager.getUsers().containsKey(receiver)) {
            synchronized (emailManager) {
                emailManager.sendEmail(emailAsText);
            }
            out.println("isMail was sent");
        } else {
            out.println("ifWe couldn't find user with name " + receiver);
        }
    }

    private void handleRRequest(String request) {
        receiveInboxMails();
        if (request.startsWith("s"))
            receiveSentMails();
    }

    private void receiveInboxMails() {
        Set<Email> emailsSet;
        synchronized (emailManager) {
            emailsSet = emailManager.getEmailsFor(client);
        }

        if(emailsSet.size()>0) {
            StringBuilder emailsAsText = new StringBuilder();
            for (Email e : emailsSet) {
                emailsAsText.append(e.textVersion());
                emailsAsText.append((char)30);
            }
            out.println("is"+emailsAsText.toString());
        } else {
            out.println("if");
        }
    }

    private void receiveSentMails() {
        Set<Email> emailsSet;
        synchronized (emailManager) {
            emailsSet = emailManager.getSentEmailsBy(client);
        }

        if(emailsSet.size()>0) {
            StringBuilder emailsAsText = new StringBuilder();
            for (Email e : emailsSet) {
                emailsAsText.append(e.textVersion());
                emailsAsText.append((char)30);
            }
            out.println("is"+emailsAsText.toString());
        } else {
            out.println("if");
        }
    }
}