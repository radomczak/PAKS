package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class LoginPaneController extends Controller{

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField loginField;

    public void initialize()
    {
        addEvents();
    }

    private void addEvents() {
        loginButton.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            String login = loginField.getText();
            String pass = passwordField.getText();

            connection.out.println("vl");
            connection.out.println(login);
            connection.out.println(pass);
            String result = getServerResponse();

            if(result.charAt(1)=='s') {
                loadClientPaneView();
            }
            showServerResponse(result.substring(2));
        });

        registerButton.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            String login = loginField.getText();
            String pass = passwordField.getText();
            connection.out.println("vr");
            connection.out.println(login);
            connection.out.println(pass);

            String result = getServerResponse();

            if(result.charAt(1)=='s') {
                loadClientPaneView();
            }
            showServerResponse(result.substring(2));
        });
    }

    private void loadClientPaneView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ClientPane.fxml"));
        BorderPane clientPane = null;
        try {
            clientPane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClientPaneController controller = loader.getController();
        controller.passConnectionAndStage(connection,stage);
        controller.firstLoad();

        Scene scene = new Scene(clientPane);
        stage.setScene(scene);
    }

}
