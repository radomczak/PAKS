package Server.Users;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserCredentialsManager {
    private Map<String,String> users = new HashMap<>();
    private final String FILE_NAME = "users.csv";


    public UserCredentialsManager() {
        importUsers();
    }

    private void importUsers() {
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_NAME))) {
            bufferedReader.lines()
                    .forEach(x -> {
                        String[] userData = x.split(";",-1);
                        if(userData[0]!= null && userData[1] != null) {
                            users.put(userData[0],userData[1]);
                        }
                    });
        } catch (FileNotFoundException e) {
            throw new DataImportException("Brak pliku " + FILE_NAME);
        } catch (IOException e) {
            throw new DataImportException("Błąd odczytu pliku " + FILE_NAME);
        }
    }

    private void exportNewUser(String user, String pass) {
        try (
        FileWriter fw = new FileWriter(FILE_NAME, true);
        BufferedWriter bw = new BufferedWriter(fw);
        ) {
            bw.newLine();
            bw.write(user.concat(";").concat(pass));
        } catch (IOException e) {}
    }


    public void addUser(String user, String pass) throws UserException {
        if(user!= null && pass != null) {
            if (!users.containsKey(user)) {
                users.put(user,pass);
                exportNewUser(user,pass);
            }
            else throw new UserException("Uzytkownik o tym loginie("+user+")jest już w bazie");
        }
    }

    public boolean checkPasswordForUser(String user, String pass) {
        if(!users.containsKey(user)) return false;
        else {
            if(users.get(user).equals(pass)) return true;
            else return false;
        }
    }

    public Map<String, String> getUsers() {
        return users;
    }
}
