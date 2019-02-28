package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Calculator");
        Scene scene = new Scene(root, 280, 420);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(0, "sample/css/my.css");
        primaryStage.show();

        primaryStage.setResizable(false);




    }


    public static void main(String[] args) {
        launch(args);
    }
}
