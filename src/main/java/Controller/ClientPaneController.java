package Controller;

import Model.Email;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

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

    List<Email> newInboxEmails = new ArrayList<>();
    List<Email> newSentEmails = new ArrayList<>();
    private final ObservableList<EmailRow> dataInbox = FXCollections.observableArrayList();
    private final ObservableList<EmailRow> dataSent = FXCollections.observableArrayList();

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

                stringEmailsInbox = serverResponse.substring(2).replace((char)29,'\r').split(regex);
                for (String email : stringEmailsInbox) {
                    Email e = createEmailFromString(email);
                    newInboxEmails.add(e);
                }
                newEmails = stringEmailsInbox.length;
                unreadCounter.setText("" + newEmails);
            } else {
                showServerResponse("You didn't receive any new emails");
            }
        });

        inbox.addEventHandler(ActionEvent.ACTION, actionEvent -> loadContentFor(newInboxEmails, "inbox"));

        sent.addEventHandler(ActionEvent.ACTION, actionEvent -> loadContentFor(newSentEmails, "sent"));

        Content.setRowFactory( tv -> {
            TableRow<EmailRow> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    EmailRow rowData = row.getItem();
                    showEmailContent(rowData);
                }
            });
            return row ;
        });

        write.addEventHandler(ActionEvent.ACTION, actionEvent -> writeMessage());
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
            clientTable.setText("From");
            for(Email e : emails) {
                dataInbox.add(new EmailRow(e,0));
            }
            Content.setItems(dataInbox);
            unreadCounter.setText("0");
        } else {
            clientTable.setText("Sent to");
            for(Email e : emails) {
                dataSent.add(new EmailRow(e,1));
            }
            Content.setItems(dataSent);
        }
        clearEmailList(emails);
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

            stringEmailsInbox = inbox.substring(2).replace((char)29,'\r').split(regex);
            for (String email : stringEmailsInbox) {
                Email e = createEmailFromString(email);
                newInboxEmails.add(e);
            }
            newEmails = stringEmailsInbox.length;
            unreadCounter.setText("" + newEmails);

            stringEmailsSent = sent.substring(2).split(regex);
            for (String email : stringEmailsSent) {
                Email e = createEmailFromString(email);
                newSentEmails.add(e);
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

    private void showEmailContent(EmailRow emailRow) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EmailContent.fxml"));
        final Stage emailContent = new Stage();
        emailContent.initModality(Modality.APPLICATION_MODAL);
        VBox emailContentPane = null;
        try {
            emailContentPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        HBox hBox = (HBox) emailContentPane.getChildren().get(0);
        Label labelClient = (Label) hBox.getChildren().get(0);
        if(emailRow.type == 0) {
            labelClient.setText("From: ");
        } else {
            labelClient.setText("To: ");
        }

        String client = emailRow.getClient();
        TextField textField = (TextField) hBox.getChildren().get(1);
        textField.setText(client);

        TextArea textArea = (TextArea) emailContentPane.getChildren().get(1);
        String content = emailRow.email.getMessage();
        textArea.setText(content.replace('\r','\n'));

        Scene emailContentScene = new Scene(emailContentPane);
        emailContent.setScene(emailContentScene);
        emailContent.show();
    }

    private void clearEmailList(List<Email> emailsToClear) {
        emailsToClear.clear();
    }

    private void writeMessage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SendEmail.fxml"));
        final Stage sendEmail = new Stage();
        sendEmail.initModality(Modality.APPLICATION_MODAL);
        VBox sendEmailPane = null;
        try {
            sendEmailPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SendEmailController controller = loader.getController();
        controller.passConnectionAndStage(connection, sendEmail);
        controller.setClientController(this);

        Scene sendEmailScene = new Scene(sendEmailPane);
        sendEmail.setScene(sendEmailScene);
        sendEmail.show();
    }

    public void addNewlySentMessage(Email email) {
        newSentEmails.add(email);
    }

    public class EmailRow {
        private SimpleStringProperty Client;
        private SimpleStringProperty Content;
        private Email email;
        private int type;

        public EmailRow(Email email, int type) {
            if(type == 0) //0 = inbox
                this.Client = new SimpleStringProperty(email.getSender());
            else    // 1 = sent
                this.Client = new SimpleStringProperty(email.getReceiver());

            String emailContent = email.getMessage().replace("\n"," ").replace("\r"," ");
            if(emailContent.length()>20)
                this.Content = new SimpleStringProperty(emailContent.substring(0,20).concat("..."));
            else
                this.Content = new SimpleStringProperty(emailContent);
            this.email = email;
            this.type = type;
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