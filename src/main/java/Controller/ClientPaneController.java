package Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientPaneController {

    private Stage stage;
    private ConnectionManager connection;

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

    public void initialize()
    {
        addEvents();
    }

    private void addEvents() {
        logout.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            loadLoginPaneView();
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

    public void passConnectionAndStage(ConnectionManager connection, Stage stage) {
        this.connection = connection;
        this.stage = stage;
    }
}
