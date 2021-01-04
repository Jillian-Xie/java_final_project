package client.game;

import message.Message;
import client.battle.Globle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class GameHall {
    @FXML
    TextField tfDisplay;
    private AnchorPane root;
    public Stage gameHallStage;
    private final BackgroundImage dawa_bgi = new BackgroundImage(new Image("Pics/Dawa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage erwa_bgi = new BackgroundImage(new Image("Pics/Erwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage sanwa_bgi = new BackgroundImage(new Image("Pics/Sanwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage siwa_bgi = new BackgroundImage(new Image("Pics/Siwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage wuwa_bgi = new BackgroundImage(new Image("Pics/Wuwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage liuwa_bgi = new BackgroundImage(new Image("Pics/Liuwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage qiwa_bgi = new BackgroundImage(new Image("Pics/Qiwa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage grandpa_bgi = new BackgroundImage(new Image("Pics/Grandpa.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage snake_bgi = new BackgroundImage(new Image("Pics/Snake.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage scorpion_bgi = new BackgroundImage(new Image("Pics/Scorption.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage toad_bgi = new BackgroundImage(new Image("Pics/Toad.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage bat_bgi = new BackgroundImage(new Image("Pics/Bat.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);
    private final BackgroundImage centipede_bgi = new BackgroundImage(new Image("Pics/Centipede.png"),
            BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
            BackgroundSize.DEFAULT);


    public void handleReadyButtonAction(ActionEvent actionEvent){
        if(!Globle.kind){
            Globle.chosen_id += 8;
        }
        Message message = new Message();
        message.MessageType = 1;
        message.srcId = Globle.chosen_id;
        try {
            synchronized (Globle.socket.getInputStream()){
                Globle.client_out.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        StackPane sp = (StackPane)root.getChildren().get(0);
        Text role = (Text)root.getChildren().get(1);
        Text camp = (Text)root.getChildren().get(6);
        Text introduction = (Text)root.getChildren().get(7);

        introduction.setText("等待对方玩家准备中...");

        camp.setStyle("-fx-font-size: 20;");
        role.setStyle("-fx-font-size: 25;");
        introduction.setStyle("-fx-font-size: 25;");

        sp.setPrefWidth(200);
        sp.setPrefHeight(200);

        AnchorPane.setTopAnchor(sp, 150.0);
        AnchorPane.setLeftAnchor(sp, 300.0);
        AnchorPane.setRightAnchor(sp, 300.0);
        AnchorPane.setBottomAnchor(sp, 150.0);

        AnchorPane.setTopAnchor(camp, 20.0);
        AnchorPane.setLeftAnchor(camp, 20.0);
        AnchorPane.setTopAnchor(introduction, 100.0);
        AnchorPane.setLeftAnchor(introduction, 300.0);
        AnchorPane.setBottomAnchor(role, 82.0);
        AnchorPane.setLeftAnchor(role, 370.0);

        root.getChildren().remove(2);
        root.getChildren().remove(2);
        root.getChildren().remove(2);
        root.getChildren().remove(2);

        new Thread(() -> {
            Message receivedMessage = new Message();
            while (true) {
                //获取发送来的消息
                try {
                    synchronized (Globle.socket.getOutputStream()) {
                        receivedMessage = (Message) Globle.client_inf.readObject();
                        System.out.println(receivedMessage.MessageType);
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (receivedMessage.MessageType == 101)
                    break;
            }
            Platform.runLater(() -> {
                gameHallStage.hide();
                new Game(gameHallStage);
            });
        }).start();
    }

    public void handleReplayButtonAction(ActionEvent actionEvent){
        FileChooser fc = new FileChooser();
        fc.setTitle("选择回放文件");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("回放文件","*.hulu"),
                new FileChooser.ExtensionFilter("所有类型","*.*"));
        File file = fc.showOpenDialog(gameHallStage);
        if (file == null) return;
        try {
            FileInputStream in = new FileInputStream(file);
            ObjectInputStream objIn=new ObjectInputStream(in);
            Message temp;
            Globle.replay_queue.clear();
            while((temp = (message.Message) objIn.readObject()) != null){
                Globle.replay_queue.add(temp);
            }
            System.out.println("回放文件读取成功！");
            if (gameHallStage == null){
                gameHallStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            }
            new GameReplay(new Stage());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void exitGame(ActionEvent actionEvent){
        try {
            Globle.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    private void setEvil(int chosen_id) {
        StackPane sp = (StackPane) root.getChildren().get(0);
        HBox hbox = (HBox) sp.getChildren().get(0);
        Text role = (Text) root.getChildren().get(1);
        switch (Globle.chosen_id) {
            case 0:
                hbox.setBackground(new Background(snake_bgi));
                role.setText("蛇精");
                break;
            case 1:
                hbox.setBackground(new Background(scorpion_bgi));
                role.setText("蝎子精");
                break;
            case 2:
                hbox.setBackground(new Background(toad_bgi));
                role.setText("蟾蜍弟");
                break;
            case 3:
                hbox.setBackground(new Background(toad_bgi));
                role.setText("蟾蜍哥");
                break;
            case 4:
                hbox.setBackground(new Background(centipede_bgi));
                role.setText("蜈蚣弟");
                break;
            case 5:
                hbox.setBackground(new Background(centipede_bgi));
                role.setText("蜈蚣哥");
                break;
            case 6:
                hbox.setBackground(new Background(bat_bgi));
                role.setText("蝙蝠弟");
                break;
            case 7:
                hbox.setBackground(new Background(bat_bgi));
                role.setText("蝙蝠哥");
                break;
        }
    }

    private void setKind(int chosen_id){
        StackPane sp = (StackPane)root.getChildren().get(0);
        HBox hbox = (HBox)sp.getChildren().get(0);
        Text role = (Text)root.getChildren().get(1);
        switch (Globle.chosen_id){
            case 0:
                hbox.setBackground(new Background(dawa_bgi));
                role.setText("大娃");
                break;
            case 1:
                hbox.setBackground(new Background(erwa_bgi));
                role.setText("二娃");
                break;
            case 2:
                hbox.setBackground(new Background(sanwa_bgi));
                role.setText("三娃");
                break;
            case 3:
                hbox.setBackground(new Background(siwa_bgi));
                role.setText("四娃");
                break;
            case 4:
                hbox.setBackground(new Background(wuwa_bgi));
                role.setText("五娃");
                break;
            case 5:
                hbox.setBackground(new Background(liuwa_bgi));
                role.setText("六娃");
                break;
            case 6:
                hbox.setBackground(new Background(qiwa_bgi));
                role.setText("七娃");
                break;
            case 7:
                hbox.setBackground(new Background(grandpa_bgi));
                role.setText("爷爷");
                break;
        }
    }

    public void handleLastButtonAction(ActionEvent actionEvent){
        Globle.chosen_id = (Globle.chosen_id+7)%8;
        if (Globle.kind)
            setKind(Globle.chosen_id);
        else{
            setEvil(Globle.chosen_id);
        }
    }

    public void handleNextButtonAction(ActionEvent actionEvent){
        Globle.chosen_id = (Globle.chosen_id+1)%8;
        if (Globle.kind)
            setKind(Globle.chosen_id);
        else{
            setEvil(Globle.chosen_id);
        }
    }

    private void setGameHall(ActionEvent actionEvent){
        Text camp, role;
        Text introduction = new Text("单击左右箭头切换初始角色");
        if(Globle.kind) {
            camp = new Text("我的阵营：葫芦兄弟");
            role = new Text("大娃");
        }
        else {
            camp = new Text("我的阵营：葫芦山群妖");
            role = new Text("蛇精");
        }

        camp.setStyle("-fx-font-size: 20;");
        role.setStyle("-fx-font-size: 25;");
        introduction.setStyle("-fx-font-size: 15;");

        Button bu1 = new Button();
        Button bu2 = new Button();
        Button ready = new Button("准备好了");
        Button replay = new Button("");

        BackgroundImage bgi_last = new BackgroundImage(new Image("Pics/last.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bg_last = new Background(bgi_last);
        bu1.setPrefHeight(100);
        bu1.setPrefWidth(100);
        bu1.setBackground(bg_last);
        bu1.setOnAction(this::handleLastButtonAction);

        BackgroundImage bgi_next = new BackgroundImage(new Image("Pics/next.png"),
                BackgroundRepeat.REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT,
                BackgroundSize.DEFAULT);
        Background bg_next = new Background(bgi_next);
        bu2.setPrefHeight(100);
        bu2.setPrefWidth(100);
        bu2.setBackground(bg_next);
        bu2.setOnAction(this::handleNextButtonAction);

        ready.setStyle("-fx-pref-width: 200;"+
                "-fx-pref-height: 50;"+
                "-fx-background-color: #d2691e90;"+
                "-fx-background-radius: 10;"+
                "-fx-border-color: chocolate;"+
                "-fx-border-width: 2;"+
                "-fx-border-radius: 10;"+
                "-fx-text-fill: white;"+
                "-fx-font-family: Arial Narrow;"+
                "-fx-font-weight: bold;"+
                "-fx-font-size: 20;");
        ready.setOnAction(this::handleReadyButtonAction);

        replay.setStyle("-fx-pref-width: 200;"+
                "-fx-pref-height: 50;"+
                "-fx-background-color: #d2691e00;"+
                "-fx-background-radius: 10;"+
                "-fx-text-fill: white;"+
                "-fx-font-family: Arial Narrow;"+
                "-fx-font-weight: bold;"+
                "-fx-font-size: 20;");

        StackPane sp = new StackPane();
        sp.setPrefWidth(200);
        sp.setPrefHeight(200);

        HBox hbox = new HBox();
        if(Globle.kind)
            hbox.setBackground(new Background(dawa_bgi));
        else
            hbox.setBackground(new Background(snake_bgi));

        sp.getChildren().addAll(hbox);

        root = new AnchorPane();
        root.getChildren().addAll(sp, role, bu1, bu2, ready, replay, camp, introduction);
        root.setStyle("-fx-background-image: url(Pics/GameHall.jpg)");

        AnchorPane.setTopAnchor(sp, 140.0);
        AnchorPane.setLeftAnchor(sp, 100.0);
        AnchorPane.setRightAnchor(sp, 500.0);
        AnchorPane.setBottomAnchor(sp, 160.0);

        AnchorPane.setLeftAnchor(bu1, 50.0);
        AnchorPane.setTopAnchor(bu1, 350.0);
        AnchorPane.setTopAnchor(bu2, 350.0);
        AnchorPane.setLeftAnchor(bu2, 250.0);
        AnchorPane.setRightAnchor(ready, 100.0);
        AnchorPane.setTopAnchor(ready, 100.0);
        AnchorPane.setTopAnchor(replay, 200.0);
        AnchorPane.setRightAnchor(replay, 100.0);
        AnchorPane.setTopAnchor(camp, 20.0);
        AnchorPane.setLeftAnchor(camp, 20.0);
        AnchorPane.setBottomAnchor(introduction, 15.0);
        AnchorPane.setLeftAnchor(introduction, 120.0);
        AnchorPane.setBottomAnchor(role, 82.0);
        AnchorPane.setLeftAnchor(role, 175.0);

        Scene scene = new Scene(root);
        gameHallStage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        gameHallStage.setOnCloseRequest(arg0 -> {
            try {
                Globle.client_inf.close();
                Globle.client_out.close();
                Globle.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(-1);
        });
        gameHallStage.hide();
        gameHallStage.setScene(scene);
        gameHallStage.show();
    }

    private boolean setUserName(){
        String content = tfDisplay.getText();
        if (content.length() == 0){
            Alert alert = new Alert(Alert.AlertType.NONE, "请输入用户名！", ButtonType.CLOSE);
            alert.setTitle("错误的用户名");
            alert.show();
            return false;
        }
        Globle.name = content;
        return true;
    }

    @FXML
    public void handleSubmitButtonAction(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
        if (!setUserName()) return;
        connect();
        System.out.println("连接成功");
        Message message = new Message();
        while(message.MessageType != 100){
            message = (Message) Globle.client_inf.readObject();
        }
        System.out.println("登陆成功");
        if (message.KindName.compareTo(Globle.name) == 0) Globle.kind = true;
        setGameHall(actionEvent);
    }

    private boolean connect() throws IOException {
        Globle.socket = new Socket("192.168.43.210", 8888);
        Globle.client_out = new ObjectOutputStream(Globle.socket.getOutputStream());
        Globle.client_inf = new ObjectInputStream(Globle.socket.getInputStream());
        Message message = new Message();
        message.MessageType = 0;
        message.UserName = Globle.name;
        synchronized (Globle.client_inf){
            Globle.client_out.writeObject(message);
        }
        return true;
    }

    public void handleExitButtonAction(ActionEvent actionEvent) {
        System.exit(0);
    }

    public void saveReplay(ActionEvent actionEvent) {
        FileChooser fc = new FileChooser();
        fc.setTitle("选择回放文件存储路径");
        fc.setInitialFileName("replay");
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("回放文件","*.hulu"));
        File file = fc.showSaveDialog(gameHallStage);
        if (file == null) return;
        try {
            FileOutputStream out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            Message temp = new Message();
            while(!Globle.replay_queue.isEmpty()){
                temp = Globle.replay_queue.removeFirst();
                objOut.writeObject(temp);
            }
            objOut.writeObject(null);
            System.out.println("回放文件存储成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
