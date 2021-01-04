package client.game;

import client.battle.ClientMap;
import client.battle.Globle;
import client.battle.MapConfig;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import message.Message;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
  服务器端工作： 绘图 + 键盘操作构造Msg发送到server端
 */

public class Game implements MapConfig {
    ClientMap map = new ClientMap();
    private ExecutorService cachedThreadPool; //可缓冲线程池
    public static boolean Isstarted;
    //TODO:
    public Game(Stage primaryStage){
        String url = Objects.requireNonNull(this.getClass().getClassLoader().getResource("Pics/Icon.png")).toString();
        primaryStage.getIcons().add(new Image(url));
        primaryStage.setTitle("葫芦娃，葫芦娃，一棵藤上七朵花");
        primaryStage.setResizable(false);
        //关闭窗口的处理
        primaryStage.setOnCloseRequest(arg0 -> {
            try {
                Globle.client_inf.close();
                Globle.client_out.close();
                Globle.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(-1);
        });
        Group root = new Group();
        Canvas canvas = new Canvas(MAPWIDTH,MAPHEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
        ClientMap.gc = gc;
        ClientMap.canvas = canvas;
        ClientMap.scene = scene;
        ClientMap.stage = primaryStage;
        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(map);


        new Thread(() -> {
            Message message = new Message();
            while (true) {
                if(Globle.socket.isClosed()) break;
                //获取发送来的消息
                try {
                    synchronized (Globle.client_out){
                        message = (Message) Globle.client_inf.readObject();
                    }
                } catch (java.net.SocketException e) {
                    System.out.println("ClientSocket has closed");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (message.MessageType == 102) {
                    Globle.map_message = message;
                }
                else if (message.MessageType == 103) {
                    if(Globle.chosen_id < 8 && message.srcId < 8){
                        Globle.chosen_id = message.srcId;
                        System.out.println("Alter role (kind)");
                    }
                    else if(Globle.chosen_id >= 8 && message.srcId >= 8){
                        Globle.chosen_id = message.srcId;
                        System.out.println("Alter role (evil)");
                    }
                }
                else if(message.MessageType == 999){
                    break;
                }
            }
            boolean WinnerName = message.WinnerName;
            Platform.runLater(() -> {
                primaryStage.hide();
                try {
                    new GameOver(primaryStage, WinnerName);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }).start();
    }
}
