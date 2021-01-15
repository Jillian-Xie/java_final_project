package client.game;

import client.battle.Globle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class GameOver {
    public GameOver(Stage primaryStage, boolean WinnerName) throws IOException {
        if(WinnerName && Globle.chosen_id < 8){
            Parent root = FXMLLoader.load(getClass().getResource("HuluWin.fxml"));
            Scene scene = new Scene(root, 960, 720);
            primaryStage.setTitle("游戏结束");
            primaryStage.setScene(scene);
            String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
            primaryStage.getIcons().add(new Image(url));
            primaryStage.show();
        }
        else if(WinnerName && Globle.chosen_id > 7){
            Parent root = FXMLLoader.load(getClass().getResource("MonsterLose.fxml"));
            Scene scene = new Scene(root, 960, 720);
            primaryStage.setTitle("游戏结束");
            primaryStage.setScene(scene);
            String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
            primaryStage.getIcons().add(new Image(url));
            primaryStage.show();
        }
        else if((!WinnerName) && Globle.chosen_id < 8){
            Parent root = FXMLLoader.load(getClass().getResource("HuluLose.fxml"));
            Scene scene = new Scene(root, 960, 720);
            primaryStage.setTitle("游戏结束");
            primaryStage.setScene(scene);
            String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
            primaryStage.getIcons().add(new Image(url));
            primaryStage.show();
        }
        else if((!WinnerName) && Globle.chosen_id > 7){
            Parent root = FXMLLoader.load(getClass().getResource("MonsterWin.fxml"));
            Scene scene = new Scene(root, 960, 720);
            primaryStage.setTitle("游戏结束");
            primaryStage.setScene(scene);
            String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
            primaryStage.getIcons().add(new Image(url));
            primaryStage.show();
        }
    }
}


