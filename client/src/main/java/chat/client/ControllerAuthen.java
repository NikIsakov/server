package chat.client;


import auth.Authent;
import auth.Authent2;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerAuthen extends Window implements Initializable {

    @FXML
    private TextArea mainArea;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passwordField;
    private Network network;

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

    public void comeOnAction(ActionEvent actionEvent) throws IOException {
        Authent2 authent = new Authent2();
        String mayBeNickname = authent.findNicknameLoginAndPassword(loginField.getText(), passwordField.getText());
        try{
            if (!mayBeNickname.isEmpty()){
                Stage primaryStage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/client.fxml"));
                Parent root = fxmlLoader.load();
                Controller controller = fxmlLoader.getController();
                primaryStage.setOnCloseRequest(event -> controller.exitAction());
                primaryStage.setTitle("Storage client");
                primaryStage.setScene(new Scene(root, 400, 400));
                primaryStage.show();
            }
        }catch (Exception e){
            mainArea.appendText("Неверный логин или пароль");
        }
    }

    public void registerOnAction(ActionEvent actionEvent) throws IOException {
        Stage primaryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/createUser.fxml"));
        Parent root = fxmlLoader.load();
        ControllerCreateUser controllerCreateUser = fxmlLoader.getController();
        primaryStage.setOnCloseRequest(event -> controllerCreateUser.exitAction());
        primaryStage.setTitle("Storage client");
        primaryStage.setScene(new Scene(root, 400, 400));
        primaryStage.show();
    }
}
