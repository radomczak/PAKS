package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class EmailContentController {

    private Stage stage;
    private ConnectionManager connection;

    @FXML
    private Button goBackButton;

    @FXML
    private Label title;

    @FXML
    private TextArea mailContent;

    public void passConnectionAndStage(ConnectionManager connection, Stage stage) {
        this.connection = connection;
        this.stage = stage;
    }
}
