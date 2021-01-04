package server.battle;

import server.bullet.Bullet;
import server.bullet.BulletController;
import server.creatrue.evil.*;
import server.creatrue.justice.Dawa;
import server.creatrue.justice.Erwa;
import server.creatrue.justice.Sanwa;
import server.creatrue.justice.Siwa;
import server.creatrue.justice.Wuwa;
import server.creatrue.justice.Liuwa;
import server.creatrue.justice.Qiwa;
import server.creatrue.justice.Grandpa;
import server.creatrue.justice.JusticeCreature;
import server.game.Server;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.stage.WindowEvent;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.LinkedList;

/**
 * @author fsq xlj
 */
//@Info(description = "use bullet to attack,field:position(x,y),damage,color,target and sender")

public class BattleController implements MapConfig {
    //TODO: Battle information
    LinkedList<Bullet> bullets = new LinkedList<>();
    Map map = new Map(bullets);
    BulletController bulletController = new BulletController(map, bullets);
    public static ArrayList<JusticeCreature> justiceCreatures = new ArrayList<>();
    public static ArrayList<EvilCreature> evilCreatures = new ArrayList<>();
    private int player;         //当前操控角色

    public static Stage stage;
    public static Canvas canvas;
    public static GraphicsContext gc;
    public static Scene scene;


    public BattleController() {}

    //TODO: 战斗状态Controller，暂停，继续，结束
    private StartController startController = new StartController();
    private EndController endController = new EndController();

    public static boolean IsStarted = false;      //判断游戏是否开始
    private static boolean clientCamp = true; //TODO: 当前操控阵营，等待服务器分配

    private ExecutorService cachedThreadPool; //可缓冲线程池

    public static boolean getCamp() {
        return clientCamp;
    }

    public static boolean Isstarted() {
        return IsStarted;
    }

    //TODO: 地图初始化
    public void InitializeMap() {
        InitializeCreature();   //生物初始化
        startController.control();
    }

    //TODO: 生物初始化
    public void InitializeCreature() {
        InitializeJustice();    //葫芦娃阵容初始化
        InitializeEvil();       //妖精阵容初始化
        justiceCreatures.get(Server.chosen_id1).setIsPlayer();
        evilCreatures.get(Server.chosen_id2 - 8).setIsPlayer();
    }

    public void InitializeJustice() {
        justiceCreatures.add(new Dawa(map, bullets));
        justiceCreatures.add(new Erwa(map, bullets));
        justiceCreatures.add(new Sanwa(map, bullets));
        justiceCreatures.add(new Siwa(map, bullets));
        justiceCreatures.add(new Wuwa(map, bullets));
        justiceCreatures.add(new Liuwa(map, bullets));
        justiceCreatures.add(new Qiwa(map, bullets));
        justiceCreatures.add(new Grandpa(map, bullets));
        //TODO: 阵型排布
        for (int i = 0; i < 8; i++) {
            justiceCreatures.get(i).setPosition(i, 5);
            map.setBlocks(i, 5, justiceCreatures.get(i));
        }
    }

    public void InitializeEvil() {
        evilCreatures.add(new Snake(map, bullets, 8));
        evilCreatures.add(new Scorption(map, bullets, 9));
        evilCreatures.add(new Toad(map, bullets, 10));
        evilCreatures.add(new Toad(map, bullets, 11));
        evilCreatures.add(new Centipede(map, bullets, 12));
        evilCreatures.add(new Centipede(map, bullets, 13));
        evilCreatures.add(new Bat(map, bullets, 14));
        evilCreatures.add(new Bat(map, bullets, 15));
        //TODO: 阵型排布
        for (int i = 0; i < 8; i++) {
            evilCreatures.get(i).setPosition(i, COLUMNS - 1 - 5);
            map.setBlocks(i, COLUMNS - 1 - 5, evilCreatures.get(i));
        }
    }

    //TODO: 游戏状态Controller实现
    class StartController {
        public void control() {
            IsStarted = true;
            GameStart();
        }
    }

    class EndController {
        EndController() {
        }

        public void control() {

        }
    }

    class CloseController implements EventHandler<WindowEvent> {
        @Override
        public void handle(WindowEvent event) {
            endController.control();
            System.exit(0);
        }
    }


    //TODO: 线程池完成游戏进行
    public void GameStart() {
        //InitializeMap();
        cachedThreadPool = Executors.newCachedThreadPool();
        cachedThreadPool.execute(map);
        for (JusticeCreature justiceCreature : justiceCreatures) {
            cachedThreadPool.execute(justiceCreature);
        }
        for (EvilCreature evilCreature : evilCreatures) {
            cachedThreadPool.execute(evilCreature);
        }
        cachedThreadPool.execute(bulletController);
    }

}
