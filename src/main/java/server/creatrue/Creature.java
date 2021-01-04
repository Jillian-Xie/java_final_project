package server.creatrue;
import server.battle.BattleController;
import server.battle.Map;
import server.bullet.Bullet;
import server.bullet.BulletFactory;
import server.creatrue.behaviour.Hit;
import server.creatrue.behaviour.Move;
import server.creatrue.behaviour.Skill;
import message.Message;
import server.game.Server;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.Runnable;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
/**
 * 生物类
 * @author fsq xlj
 * @version 1.0
 */


public abstract class Creature implements Runnable, Move, Hit, Skill, Serializable {
    // TODO: Design properties
    protected String name;
    public int id;
    protected int max_hp;
    protected int cur_hp;   //当前血量
    protected int rate;
    protected int atk;
    protected int defense;
    protected int count;
    protected boolean Isalive; //设置墓碑使用
    protected boolean camp; //阵营 1:Justice 0:Evil
    protected boolean IsPlayer; //判断是否为玩家操作
    protected Random random;
    protected Position position;
    protected Map map;      //地图对象,存储当前各类生物、所发子弹信息
    protected BulletFactory bulletFactory;
    protected LinkedList<Bullet> bullets;
    protected LinkedList<Direction> path;  //存储玩家移动方向
    protected LinkedList<Direction> waitbullets;  //存储当前待发射子弹

    public Creature(){}     //显示定义默认构造函数
    public Creature(Map map,LinkedList<Bullet>bullets){
        this.count = 0;
        this.Isalive = true;
        this.IsPlayer= false;
        this.map = map;
        this.bullets = bullets;
        this.random = new Random();
        this.position = new Position();
        this.path = new LinkedList<>();
        this.waitbullets = new LinkedList<>();
        this.bulletFactory = new BulletFactory();
    }

    public boolean getCamp(){
        return camp;
    }
    public boolean getIsalive() {
        return Isalive;
    }
    public int getMaxhp(){
        return max_hp;
    }
    public int getCurhp(){
        return cur_hp;
    }
    public void setIsPlayer(){
        IsPlayer = true;
    }    //设置玩家操作
    public boolean getIsPlayer() {
        return IsPlayer;
    }
    public void setCamp(){
        camp = !camp;
    }           //但是既然都这么多函数了,我也不介意再多一个
    //TODO: MOVE操作
    public void addPath(Direction direction){
        path.add(direction);
    }
    public void addBullets(Direction direction){
        waitbullets.add(direction);
    }
    public Position getPosition(){
        return this.position;
    }
    public void setPosition(int x,int y){
        position.setX(x);
        position.setY(y);
    }
    //電腦操控，自動移動
    @Override
    public void AutoMove(){
        //随机移动
        int d = random.nextInt(4); //0-4
        Direction direction;
        switch (d){
            case 0:{
                direction = Direction.UP;
                break;
            }
            case 1:{
                direction = Direction.DOWN;
                break;
            }
            case 2:{
                direction = Direction.LEFT;
                break;
            }
            default:{
                direction = Direction.RIGHT;
            }
        }
        addPath(direction);
        HumanMove();
    }

    private int deCode(Direction direction){
        int result = -1;
        switch (direction){
            case UP:
                result = 0;
                break;
            case DOWN:
                result = 1;
                break;
            case LEFT:
                result = 2;
                break;
            case RIGHT:
                result = 3;
                break;
        }
        return result;
    }
    //受到治疗回血
    public void beCured(){
        this.cur_hp = (this.cur_hp + 25)%this.max_hp;
    }
    //人工操控
    @Override
    public void HumanMove(){
        //获取移动后位置
        if(!path.isEmpty()){
            int curX = position.getX();
            int curY = position.getY();
            Direction curDirection = path.getFirst();
//            if(IsPlayer){
//                //System.out.println(curDirection);
//                System.out.println(curX);
//                System.out.println(curY);
//            }
            path.removeFirst();
            //注意，地图是从上到下，从左到右建立坐标轴
            switch (curDirection){
                case UP:{
                    curX--;
                    break;
                }
                case DOWN:{
                    curX++;
                    break;
                }
                case LEFT:{
                    curY--;
                    break;
                }
                case RIGHT:{
                    curY++;
                    break;
                }
            }
            if(map.InMap(curX,curY)&&map.IsEmpty(curX,curY)){
//                if(IsPlayer){
//                    System.out.println(curX);
//                    System.out.println(curY);
//                }

                map.RemoveBlocks(position.getX(),position.getY());
                map.setBlocks(curX,curY,this);
                position.setX(curX);
                position.setY(curY);
            }
        }
        try{
            Thread.sleep(60);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        //判断是否在地图内，与所移动格子有无生物，若满足条件，则移动
    }
    @Override
    public void hit(int damage){
        int atk = damage - defense;
        if(atk>=cur_hp){
            cur_hp = 0;
            Isalive = false;  //生物死亡,对应使用墓碑替换原生物
        }
        else{
            cur_hp -= atk;
            //System.out.println(cur_hp);
        }
    }

    public void handle(KeyCode keyCode, boolean clientCamp) throws IOException {
        switch (keyCode){
            case F:{
                if(Server.period==1){
                    //释放技能
                    this.skill();
                }
                break;
            }
            //移动操作
            case UP:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addPath(Direction.UP);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addPath(Direction.UP);
                    //System.out.println("UP");
                }
                break;
            }
            case DOWN:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addPath(Direction.DOWN);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addPath(Direction.DOWN);
                    //System.out.println("DOWN");
                }
                break;
            }
            case LEFT:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addPath(Direction.LEFT);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addPath(Direction.LEFT);
                    //System.out.println("LEFT");
                }
                break;
            }
            case RIGHT:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addPath(Direction.RIGHT);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addPath(Direction.RIGHT);
                    //System.out.println("RIGHT");
                }
                break;
            }
            //子弹发射
            case W:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addBullets(Direction.UP);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addBullets(Direction.UP);
                }
                break;
            }
            case A:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addBullets(Direction.LEFT);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addBullets(Direction.LEFT);
                }
                break;
            }
            case S:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addBullets(Direction.DOWN);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addBullets(Direction.DOWN);
                }
                break;
            }
            case D:{
                if(Server.period==1){
                    if(clientCamp)
                        BattleController.justiceCreatures.get(Server.chosen_id1).addBullets(Direction.RIGHT);
                    else
                        BattleController.evilCreatures.get(Server.chosen_id2-8).addBullets(Direction.RIGHT);
                }
                System.out.println("发射子弹");
                break;
            }
            //角色切换
            case Q:{
                if(Server.period==1){
                    synchronized (Server.KindDecided) {
                        if(clientCamp) {
                            Server.chosen_id1 = (Server.chosen_id1 + 1) % 8;  //0-7
                            while (!BattleController.justiceCreatures.get(Server.chosen_id1).getIsalive()) {
                                Server.chosen_id1 = (Server.chosen_id1 + 1) % 8;  //0-7
                            }
                            Message message = new Message();
                            message.srcId = Server.chosen_id1;
                            message.MessageType = 103;
                            synchronized (Server.ht_out) {
                                for (Iterator it = Server.ht_out.keySet().iterator(); it.hasNext(); ) {
                                    Socket key = (Socket) it.next();
                                    ObjectOutputStream outData = (ObjectOutputStream) Server.ht_out.get(key);
                                    InputStream inData = key.getInputStream();
                                    if (key.isClosed() || !key.isConnected()) {
                                        Server.ht_out.remove(key);
                                    } else {
                                        synchronized (inData) {
                                            outData.writeObject(message);
                                            outData.flush();
                                            System.out.println("Alter message send!");
                                        }
                                    }
                                }
                            }
                            System.out.print("切换后角色:");
                            System.out.println(Server.chosen_id1);
                        }
                        else{
                            Server.chosen_id2 = (Server.chosen_id2 + 1) % 8 + 8;//8-15
                            while (!BattleController.evilCreatures.get(Server.chosen_id2-8).getIsalive()) {
                                Server.chosen_id2 = (Server.chosen_id2 + 1) % 8 + 8;
                            }
                            Message message = new Message();
                            message.srcId = Server.chosen_id2;
                            message.MessageType = 103;
                            synchronized (Server.ht_out) {
                                for (Iterator it = Server.ht_out.keySet().iterator(); it.hasNext(); ) {
                                    Socket key = (Socket) it.next();
                                    ObjectOutputStream outData = (ObjectOutputStream) Server.ht_out.get(key);
                                    InputStream inData = key.getInputStream();
                                    if (key.isClosed() || !key.isConnected()) {
                                        Server.ht_out.remove(key);
                                    } else {
                                        synchronized (inData) {
                                            outData.writeObject(message);
                                            outData.flush();
                                            System.out.println("Alter message send!");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("切换人物->");
                Message message = new Message();
                break;
            }
            case E:{
                if(Server.period==1){
                    synchronized (Server.KindDecided) {
                        if(clientCamp) {
                            Server.chosen_id1 = (Server.chosen_id1 + 7) % 8;  //0-7
                            while (!BattleController.justiceCreatures.get(Server.chosen_id1).getIsalive()) {
                                Server.chosen_id1 = (Server.chosen_id1 + 7) % 8;  //0-7
                            }
                            Message message = new Message();
                            message.srcId = Server.chosen_id1;
                            message.MessageType = 103;
                            synchronized (Server.ht_out) {
                                for (Iterator it = Server.ht_out.keySet().iterator(); it.hasNext(); ) {
                                    Socket key = (Socket) it.next();
                                    ObjectOutputStream outData = (ObjectOutputStream) Server.ht_out.get(key);
                                    InputStream inData = key.getInputStream();
                                    if (key.isClosed() || !key.isConnected()) {
                                        Server.ht_out.remove(key);
                                    } else {
                                        synchronized (inData) {
                                            outData.writeObject(message);
                                            outData.flush();
                                            System.out.println("Alter message send!");
                                        }
                                    }
                                }
                            }
                            System.out.print("切换后角色:");
                            System.out.println(Server.chosen_id1);
                        }
                        else{
                            Server.chosen_id2 = (Server.chosen_id2 + 7) % 8 + 8;//8-15
                            while (!BattleController.evilCreatures.get(Server.chosen_id2-8).getIsalive()) {
                                Server.chosen_id2 = (Server.chosen_id2 + 7) % 8 + 8;
                            }
                            Message message = new Message();
                            message.srcId = Server.chosen_id2;
                            message.MessageType = 103;
                            synchronized (Server.ht_out) {
                                for (Iterator it = Server.ht_out.keySet().iterator(); it.hasNext(); ) {
                                    Socket key = (Socket) it.next();
                                    ObjectOutputStream outData = (ObjectOutputStream) Server.ht_out.get(key);
                                    InputStream inData = key.getInputStream();
                                    if (key.isClosed() || !key.isConnected()) {
                                        Server.ht_out.remove(key);
                                    } else {
                                        synchronized (inData) {
                                            outData.writeObject(message);
                                            outData.flush();
                                            System.out.println("Alter message send!");
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("切换人物<-");
                break;
            }
        }
    }
}
