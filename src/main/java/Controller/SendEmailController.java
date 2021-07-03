package Controller;

import Model.Email;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class SendEmailController extends Controller {


    @FXML
    private Button goBackButton;

    @FXML
    private TextField receiverTextField;

    @FXML
    private Button SendButton;

    @FXML
    private TextArea mailContent;


    ClientPaneController clientController;

    public void initialize() {
        addEvents();
    }

    private void addEvents() {
        goBackButton.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            stage.close();
        });

        SendButton.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            boolean mailIsOk = verifyMail();
            if (mailIsOk) {
                String sender = user;
                String receiver = receiverTextField.getText();
                String message = mailContent.getText();

                Email email = new Email(sender,receiver,message);
                connection.out.println("s");
                connection.out.println(email.textVersion());
                String serverResponse = getServerResponse();
                if(serverResponse.charAt(1) == 's') {
                    clientController.addNewlySentMessage(email);
                }
                stage.close();
                showServerResponse(serverResponse.substring(2));

            }
        });
    }

    private boolean verifyMail() {
        boolean verifyResult = false;
        if(receiverTextField.getText().length()==0) {
            showServerResponse("Enter receiver first");
        } else verifyResult = true;
        return verifyResult;
    }


    public void setClientController(ClientPaneController c) {
        this.clientController = c;
    }
}
