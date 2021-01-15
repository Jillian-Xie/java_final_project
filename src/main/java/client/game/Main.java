package client.game;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
        Scene scene = new Scene(root, 900, 455);
        primaryStage.setTitle("葫芦娃战群妖");
        primaryStage.setScene(scene);
        String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
        primaryStage.getIcons().add(new Image(url));
        primaryStage.show();
    }
}