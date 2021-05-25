package chat.client;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import tcp.Message;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Controller extends Window implements Initializable {
    private Network network;
    private File file = null;


    @FXML
    private TextField msgField;

    @FXML
    private TextArea mainArea;

    @FXML
    private ListView<String> listView;

    public TextArea getMainArea() {
        return mainArea;
    }

    @Override
    public void initialize(URL location, ResourceBundle resourceBundle) {
        network = new Network(args -> {
            mainArea.appendText((String) args[0]);
        });
    }

    public void sendMsgAction(ActionEvent actionEvent) {
        network.sendMessage(msgField.getText());
        msgField.clear();
        msgField.requestFocus();
    }

    public void exitAction() {
        network.close();
        Platform.exit();
    }

    @lombok.SneakyThrows
    @FXML
    public void hndlOpenFile(ActionEvent event) {
        List<File> filesInServer = new ArrayList<>();

        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setInitialDirectory(new File("C:\\"));//папка открытия по умолчанию

        fileChooser.setTitle("Open Document");//Заголовок диалога
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All Images", "*.*");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Documents", "*.html"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File chooseFile = fileChooser.showOpenDialog(this);//Указываем текущую сцену CodeNote.mainStage
        //List<File> chooseFiles = fileChooser.showOpenMultipleDialog(this);//выбор нескольких

        file = chooseFile;
        network.sendMessage(file);
        System.out.println("log: выбрали файл в Controller " + file);
        mainArea.appendText("Файл скопирован в директорию: ");

        List<File> fileList = new ArrayList<>();

        fileList.add(chooseFile);

        if (chooseFile != null) {
            /*for (int i = 0; i < fileList.size(); i++) {
                listView.getItems().add(fileList.get(i).getAbsolutePath());
            }*/
            listView.getItems().add(chooseFile.getAbsolutePath()); //Open
            System.out.println("Процесс открытия файла");

        }
    }


    public File getFile() {
        return file;
    }



    public void hndlDownloadFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();//Класс работы с диалогом выборки и сохранения
        fileChooser.setInitialDirectory(new File("server/src/main/FileSystem/"));//папка открытия по умолчанию

        fileChooser.setTitle("Open Document");//Заголовок диалога
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("All Images", "*.*");//Расширение
        fileChooser.getExtensionFilters().add(extFilter);
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("HTML Documents", "*.html"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        File chooseFile = fileChooser.showOpenDialog(this);//Указываем текущую сцену CodeNote.mainStage
    }

}
//
