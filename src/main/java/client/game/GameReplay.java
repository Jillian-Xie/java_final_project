package client.game;

import client.battle.MapConfig;
import client.battle.ReplayMap;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameReplay implements MapConfig {
    ReplayMap map = new ReplayMap();
    private ExecutorService cachedThreadPool; //可缓冲线程池
    public GameReplay(Stage primaryStage){
        String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
        primaryStage.getIcons().add(new Image(url));
        primaryStage.setTitle("观看回放文件");
        primaryStage.setResizable(false);
        Group root = new Group();
        Canvas canvas = new Canvas(MAPWIDTH,MAPHEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        ReplayMap.gc = gc;
        ReplayMap.canvas = canvas;
        ReplayMap.scene = scene;
        ReplayMap.stage = primaryStage;
        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(map);
    }
}
