package chat.client;

import auth.Authent;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Window;

import java.awt.*;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerCreateUser extends Window implements Initializable {
    @FXML
    private TextArea mainArea;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    private Network network;

    @FXML
    private TextField nicknameField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        network = new Network(args -> {
            mainArea.appendText((String) args[0]);
        });
    }

    public void exitAction() {
        network.close();
        Platform.exit();
    }

    public void registerOnAction(ActionEvent actionEvent) {
        Authent newUser = new Authent();
        newUser.add(loginField.getText(),passwordField.getText(), nicknameField.getText());
    }
}
