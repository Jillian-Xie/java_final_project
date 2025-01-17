package client.battle;

import annotations.ClassInfo;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import message.Message;

import java.util.ArrayList;

@ClassInfo(CompleteTime = "2020-12-29 11:35",Description = "For playback operation")
public class ReplayMap implements MapConfig, Runnable {
    public ArrayList<Boolean> creature_status = new ArrayList<>();
    public ArrayList<Integer> creture_position_X = new ArrayList<>();
    public ArrayList<Integer> creture_position_Y = new ArrayList<>();
    public ArrayList<Integer> creature_max_hp = new ArrayList<>();
    public ArrayList<Integer> creature_current_hp = new ArrayList<>();
    public ArrayList<Boolean> bullet_camp = new ArrayList<>();
    public ArrayList<Double> bullet_position_X = new ArrayList<>();
    public ArrayList<Double> bullet_position_Y = new ArrayList<>();
    public int skill1 = 0, skill2 = 0, skillId1 = -1, skillId2 = -1;
    public static Stage stage;
    public static Canvas canvas;
    public static GraphicsContext gc;
    public static Scene scene;

    public ReplayMap() {
    }

    public Image get_image(int i) {
        Image image = LocalImage.Dawa;
        switch (i) {
            case 0:
                break;
            case 1:
                image = LocalImage.Erwa;
                break;
            case 2:
                image = LocalImage.Sanwa;
                break;
            case 3:
                image = LocalImage.Siwa;
                break;
            case 4:
                image = LocalImage.Wuwa;
                break;
            case 5:
                image = LocalImage.Liuwa;
                break;
            case 6:
                image = LocalImage.Qiwa;
                break;
            case 7:
                image = LocalImage.Grandpa;
                break;
            case 8:
                image = LocalImage.Snake;
                break;
            case 9:
                image = LocalImage.Scorption;
                break;
            case 10:
                image = LocalImage.Toad;
                break;
            case 11:
                image = LocalImage.Toad;
                break;
            case 12:
                image = LocalImage.Centipede;
                break;
            case 13:
                image = LocalImage.Centipede;
                break;
            case 14:
                image = LocalImage.Bat;
                break;
            case 15:
                image = LocalImage.Bat;
                break;
        }
        return image;
    }

    public Image getBackground() {
        return LocalImage.backgroundImg;
    }

    @Override
    public void run() {
        while (true) {
            //背景
            gc.drawImage(getBackground(), 0, 0, MAPWIDTH, MAPHEIGHT);
            //划分Block

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(1);
            for (int i = 0; i <= 12; i++) {
                gc.strokeLine(0, i * 60, 16 * 60, i * 60);
            }
            for (int i = 0; i <= 16; i++) {
                gc.strokeLine(i * 60, 0, i * 60, 12 * 60);
            }
//画人物
            for (int k = 0; k < creature_status.size(); ++k) {
                if (creature_status.get(k)) {
                    int i = creture_position_X.get(k);
                    int j = creture_position_Y.get(k);
                    int maxHp = creature_max_hp.get(k); //TODO 生物最大血量
                    int curHp = creature_current_hp.get(k); //TODO 生物当前血量
                    int curHpLine = curHp * BLOCKSIZE / maxHp; //...注意整除  不能用cur/max * size
                    gc.drawImage(get_image(k), j * BLOCKSIZE, i * BLOCKSIZE, BLOCKSIZE - 5, BLOCKSIZE - 5);
                    //绘制当前血量
                    gc.setLineWidth(4);
                    if (curHp * BLOCKSIZE / maxHp > 0.5 * BLOCKSIZE) {
                        gc.setStroke(Color.LIGHTGREEN);
                    } else {
                        //血量报警
                        gc.setStroke(Color.DARKRED);
                    }
                    gc.strokeLine(j * BLOCKSIZE, i * BLOCKSIZE, j * BLOCKSIZE + curHpLine, i * BLOCKSIZE);
                    if (k == Globle.chosen_id) {
                        gc.setFill(Color.rgb(241, 158, 194, 0.6));
                        double x = j * BLOCKSIZE;
                        double y = i * BLOCKSIZE;
                        gc.fillOval(x + 5, y + 5, BLOCKSIZE - 10, BLOCKSIZE - 10);
                    }
                } else {
                    // TODO X Y 的对应可能有误
                    gc.drawImage(LocalImage.tombImg, creture_position_Y.get(k) * BLOCKSIZE, creture_position_X.get(k) * BLOCKSIZE, BLOCKSIZE - 5, BLOCKSIZE - 5);
                }
            }
//画子弹
            for (int k = 0; k < bullet_camp.size(); ++k) {
                if (bullet_camp.get(k)) {
                    gc.setFill(Color.LIGHTBLUE);
                } else {
                    gc.setFill(Color.LIGHTGREEN);
                }
                gc.fillOval(bullet_position_Y.get(k), bullet_position_X.get(k), 15, 15); //TODO 子弹坐标
            }

//画特技
            if(skill1 != 0){
                switch (skillId1){
                    case 1:
                        gc.drawImage(LocalImage.erwaSkill, (30-skill1)*10, 0, 300, 560);
                        break;
                    case 3:
                        gc.drawImage(LocalImage.siwaSkill, (30-skill1)*10, 0, 300, 560);
                        break;
                    case 4:
                        gc.drawImage(LocalImage.wuwaSkill, (30-skill1)*10, 0, 300, 560);
                        break;
                    case 6:
                        gc.drawImage(LocalImage.qiwaSkill, (30-skill1)*10, 0, 300, 560);
                        break;
                }
            }
            if(skill2 != 0) {
                gc.drawImage(LocalImage.scorptionSkill, 60+skill2*10, 0, 600, 720);
            }
            try {
                if (Globle.replay_queue.isEmpty()) break;
                Thread.sleep(65);
                Message message = Globle.replay_queue.removeFirst();
                creature_status = message.creature_status;
                creture_position_X = message.creture_position_X;
                creture_position_Y = message.creture_position_Y;
                creature_max_hp = message.creature_max_hp;
                creature_current_hp = message.creature_current_hp;
                bullet_camp = message.bullet_camp;
                bullet_position_X = message.bullet_position_X;
                bullet_position_Y = message.bullet_position_Y;
                skill1 = message.skill1;
                skill2 = message.skill2;
                skillId1 = message.skillId1;
                skillId2 = message.skillId2;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("回放完成");

        Platform.runLater(() -> {
            stage.hide();
        });
    }
}
