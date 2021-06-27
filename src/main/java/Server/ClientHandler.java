package Server;

import Server.Users.UserCredentialsManager;
import Server.Users.UserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler extends Thread {

    private final Socket socket;
    private UserCredentialsManager credentialsManager;
    private BufferedReader in;
    private PrintWriter out;
    private String client = null;

    public ClientHandler(Socket socket,UserCredentialsManager credentialsManager) {
        this.socket = socket;
        this.credentialsManager = credentialsManager;
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
            case 'v': {
                handleVRequest(request.substring(1));
                break;
            }
            case 's' : {
                handleSRequest(request.substring(1));
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
            out.println("itWelcome " + client);
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
            out.println("itWelcome our new user " + client);
        } else {
            out.println("ifUser with this login already exists");
        }
    }

    private void handleSRequest(String request) {

    }
}