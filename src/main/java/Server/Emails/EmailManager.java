package Server.Emails;

import Model.Email;
import Server.Exception.DataImportException;
import Server.Users.UserCredentialsManager;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EmailManager {
    private UserCredentialsManager manager;
    private Set<Email> emails = new HashSet<>();
    private Map<String, Set<Email>> emailsToSend = new HashMap<>();
    private final String FILE_NAME = "emails.csv";


    public EmailManager(UserCredentialsManager manager) {
        this.manager = manager;
        importEmails();
        initializeEmailsMap();
    }

    private void importEmails() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))) {
            bufferedReader.lines()
                    .forEach(x -> {
                        String[] emailData = getEmailDataFromString(x);
                        if(emailData[0]!= null && emailData[1] != null && emailData[2]!=null) {
                            Email email = createEmailUsingData(emailData);
                            emails.add(email);
                        }
                    });
        } catch (FileNotFoundException e) {
            throw new DataImportException("Brak pliku " + FILE_NAME);
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + FILE_NAME);
        }
    }



    private String[] getEmailDataFromString(String x) {
        String[] data = new String[3];
        StringBuilder sender = new StringBuilder();
        StringBuilder receiver = new StringBuilder();
        String message;

        int firstIndex = x.indexOf(";");
        for(int i = 0; i < firstIndex; i++) {
            char c = x.charAt(i);
            sender.append(c);
        }
        x = x.substring(firstIndex+1);

        int secondIndex = x.indexOf(";");
        for(int i = 0; i < secondIndex; i++) {
            char c = x.charAt(i);
            receiver.append(c);
        }
        message = x.substring(secondIndex+1);
        data[0] = sender.toString();
        data[1] = receiver.toString();
        data[2] = message;
        return data;
    }

    private Email createEmailUsingData(String[] data) {
        return new Email(data[0],data[1],data[2]);
    }

    private void initializeEmailsMap() {
        for (String user : manager.getUsers().keySet()) {
            emailsToSend.put(user,new HashSet<>());
        }
        for (Email email : emails) {
            emailsToSend.get(email.getReceiver()).add(email);
        }
    }

    public Set<Email> getEmailsFor(String user) {
        Set<Email> returnSet = emailsToSend.get(user);
        emailsToSend.get(user).clear();
        return returnSet;
    }

    public void sendEmail(String emailAsString) {
        Email email = createEmailUsingData(getEmailDataFromString(emailAsString));
        emailsToSend.get(email.getReceiver()).add(email);
        exportEmail(email);
    }

    private void exportEmail(Email email) {
        try (
                FileWriter fw = new FileWriter(FILE_NAME, true);
                BufferedWriter bw = new BufferedWriter(fw);
        ) {
            bw.newLine();
            bw.write(email.textVersion());
        } catch (IOException e) {}
    }

}
