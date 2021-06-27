package Client;

import Controller.ConnectionManager;
import Controller.LoginPaneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Client extends Application {

    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException {
        ConnectionManager connection = new ConnectionManager();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginPane.fxml"));
        AnchorPane loginPane = loader.load();
        Scene scene = new Scene(loginPane);
        stage.setScene(scene);
        stage.setTitle("Mail app by Radosław Popielarski");
        LoginPaneController controller = loader.getController();
        controller.passConnectionAndStage(connection,stage);
        stage.show();
    }
}
