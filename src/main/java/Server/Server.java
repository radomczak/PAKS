package Server;

import Server.Emails.EmailManager;
import Server.Users.UserCredentialsManager;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.HashSet;


public class Server {


    private static final int PORT = 9001;

    private static HashSet<String> users = new HashSet<String>();
    private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
    private UserCredentialsManager userCredentialsManager = new UserCredentialsManager();

    public static void main(String[] args) throws Exception {
        UserCredentialsManager credentialsManager = new UserCredentialsManager();
        EmailManager emailManager = new EmailManager(credentialsManager);
        System.out.println("The mail server is running.");
        ServerSocket socket = new ServerSocket(PORT);
        try {
            while (true) {
                new ClientHandler(socket.accept(),credentialsManager,emailManager).start();
            }
        } finally {
            socket.close();
        }
    }
}