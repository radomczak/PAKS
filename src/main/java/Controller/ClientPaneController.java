package Controller;

import Model.Email;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientPaneController extends Controller {

    @FXML
    private Button write;

    @FXML
    private Button inbox;

    @FXML
    private Label unreadCounter;

    @FXML
    private Button sent;

    @FXML
    private Button refresh;

    @FXML
    private Button logout;

    @FXML
    private TableView<EmailRow> Content;

    @FXML
    private TableColumn<EmailRow, String> clientTable;

    @FXML
    private TableColumn<EmailRow, String> contentTable;

    List<Email> inboxEmails = new ArrayList<>();
    List<Email> sentEmails = new ArrayList<>();
    private final ObservableList<EmailRow> dataInbox = FXCollections.observableArrayList(
            new EmailRow("radek", "Whatever dude just send it to...."),
            new EmailRow("radek", "Thanks dude")
    );
    private final ObservableList<EmailRow> dataSent = FXCollections.observableArrayList(
            new EmailRow("Marlena", "Ok dude sending now")
    );

    public void initialize() {
        addEvents();
    }

    public void firstLoad() {
        loginRefresh();
    }

    private void addEvents() {
        logout.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadLoginPaneView();
        });

        refresh.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            connection.out.println("ri");
            String serverResponse = getServerResponse();

            String[] stringEmailsInbox = null;
            int newEmails = 0;

            if (serverResponse.charAt(1) == 's') {
                String regex = String.valueOf((char) 30);

                stringEmailsInbox = serverResponse.substring(2).split(regex);
                for (String email : stringEmailsInbox) {
                    Email e = createEmailFromString(email);
                    inboxEmails.add(e);
                }
                newEmails = stringEmailsInbox.length;
                unreadCounter.setText("" + newEmails);
            } else {
                showServerResponse("You didn't receive any new emails");
            }
        });

        inbox.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadContentFor(inboxEmails, "inbox");
        });

        sent.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadContentFor(sentEmails, "sent");
        });
    }

    private void loadLoginPaneView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginPane.fxml"));
        AnchorPane loginPane = null;
        try {
            loginPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoginPaneController controller = loader.getController();
        controller.passConnectionAndStage(connection, stage);

        Scene scene = new Scene(loginPane);
        stage.setScene(scene);
    }

    private void loadContentFor(List<Email> emails, String type) {
        clientTable.setCellValueFactory(new PropertyValueFactory<>("Client"));
        contentTable.setCellValueFactory(new PropertyValueFactory<>("Content"));
        if (type.equals("inbox")) {
            Content.setItems(dataInbox);
        } else {
            Content.setItems(dataSent);
        }
    }

    private void loginRefresh() {
        connection.out.println("rs");
        String inbox = getServerResponse();
        String sent = getServerResponse();
        String[] stringEmailsInbox = null;
        String[] stringEmailsSent = null;
        int newEmails = 0;

        if (inbox.charAt(1) == 's') {
            String regex = String.valueOf((char) 30);

            stringEmailsInbox = inbox.substring(2).split(regex);
            for (String email : stringEmailsInbox) {
                Email e = createEmailFromString(email);
                inboxEmails.add(e);
            }
            newEmails = stringEmailsInbox.length;
            unreadCounter.setText("" + newEmails);

            stringEmailsSent = sent.substring(2).split(regex);
            for (String email : stringEmailsSent) {
                Email e = createEmailFromString(email);
                sentEmails.add(e);
            }
        }
        showServerResponse("You have " + newEmails + " new Emails");
    }

    private Email createEmailFromString(String x) {
        String[] data = new String[3];
        StringBuilder sender = new StringBuilder();
        StringBuilder receiver = new StringBuilder();
        String message;

        int firstIndex = x.indexOf(";");
        for (int i = 0; i < firstIndex; i++) {
            char c = x.charAt(i);
            sender.append(c);
        }
        x = x.substring(firstIndex + 1);

        int secondIndex = x.indexOf(";");
        for (int i = 0; i < secondIndex; i++) {
            char c = x.charAt(i);
            receiver.append(c);
        }
        message = x.substring(secondIndex + 1);
        data[0] = sender.toString();
        data[1] = receiver.toString();
        data[2] = message;
        return new Email(data[0], data[1], data[2]);
    }

    public class EmailRow {
        private SimpleStringProperty Client;
        private SimpleStringProperty Content;

        public EmailRow(String client, String content) {
            this.Client = new SimpleStringProperty(client);
            this.Content = new SimpleStringProperty(content);
        }

        public String getClient() {
            return Client.get();
        }

        public String getContent() {
            return Content.get();
        }

        public void setClient(String client) {
            Client.set(client);
        }

        public void setContent(String content) {
            Content.set(content);
        }
    }
}