package Controller;

import Model.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientPaneController extends Controller{

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
    private TableView<?> Content;

    List<Email> inboxEmails = new ArrayList<>();
    List<Email> sentEmails = new ArrayList<>();


    public void initialize()
    {
        addEvents();
    }

    private void addEvents() {
        logout.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadLoginPaneView();
        });

        refresh.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            connection.out.println("r");
            String serverResponse = getServerResponse();
            if(serverResponse.charAt(1)=='s') {
                int i = checkNumberOfEmailsFromString(serverResponse.substring(2));
                unreadCounter.setText(""+i);
            } else {
                showServerResponse("You didn't receive any emails");
            }
        });

        inbox.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            unreadCounter.setText("0");
            loadContentFor(inboxEmails);
        });

        sent.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadContentFor(sentEmails);
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
        controller.passConnectionAndStage(connection,stage);

        Scene scene = new Scene(loginPane);
        stage.setScene(scene);
    }

    private int checkNumberOfEmailsFromString(String emails) {
        return 2;
    }

    private void loadContentFor(List<Email> emails) {

    }
}
