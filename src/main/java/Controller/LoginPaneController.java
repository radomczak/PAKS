package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginPaneController {

    private Stage stage;
    private ConnectionManager connection;

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

            if(result.charAt(1)=='t') {
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

            if(result.charAt(1)=='t') {
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

        Scene scene = new Scene(clientPane);
        stage.setScene(scene);
    }

    private String getServerResponse() {
        String result = null;
        while (result == null) {
            try {
                result = connection.in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void showServerResponse(String response) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        AnchorPane serverInfo = null;
        try {
            serverInfo = FXMLLoader.load(getClass().getResource("/fxml/ServerInfoWindow.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Label label = (Label) serverInfo.getChildren().get(1);
        label.setText(response);
        Scene dialogScene = new Scene(serverInfo);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    public void passConnectionAndStage(ConnectionManager connection, Stage stage) {
        this.connection = connection;
        this.stage = stage;
    }

}
