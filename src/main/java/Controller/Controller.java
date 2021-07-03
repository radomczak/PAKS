package Controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class Controller {
    protected Stage stage;
    protected ConnectionManager connection;
    protected static String user;

    protected String getServerResponse() {
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

    protected void showServerResponse(String response) {
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
