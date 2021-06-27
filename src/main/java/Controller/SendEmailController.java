package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class SendEmailController {

    private Stage stage;
    private ConnectionManager connection;

    @FXML
    private Button goBackButton;

    @FXML
    private Button SendButton;

    @FXML
    private TextArea mailContent;

    public void passConnectionAndStage(ConnectionManager connection, Stage stage) {
        this.connection = connection;
        this.stage = stage;
    }
}
