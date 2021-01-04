package server.creatrue.evil;

import server.battle.Map;
import server.battle.MapConfig;
import server.bullet.Bullet;
import server.bullet.BulletConfig;
import server.creatrue.Creature;
import server.creatrue.Direction;
import server.creatrue.behaviour.Cure;
import server.creatrue.behaviour.Shoot;
import message.Message;
import server.game.Server;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.util.LinkedList;

public class EvilCreature extends Creature implements Shoot, Cure, MapConfig, BulletConfig {
    public EvilCreature(Map map, LinkedList<Bullet> bullets,int id){
        super(map,bullets);
        //TODO: Set general properties(Special creatures adjust themselves
        this.id = id;
        this.max_hp = 500;  //随机设置，框架搭建阶段
        this.cur_hp = this.max_hp;
        this.atk = 110;
        this.defense = 67;      //普通葫芦娃打妖怪打15次
        this.rate = 2;
        this.camp = false;   //Evil
        this.Isalive = true;
        this.IsPlayer = false;
    }

    @Override
    public void AutoShoot() {
        //TODO: 寻找直线上的敌人发射子弹
        boolean[] enemies = map.SearchEnemies(camp,position.getX(),position.getY());
        Direction[] directions = new Direction[]{Direction.UP,Direction.DOWN,Direction.LEFT,Direction.RIGHT};
        for(int i=0;i<4;i++){
            if(enemies[i]){
                waitbullets.add(directions[i]);
            }
        }
        HumanShoot();
    }

    @Override
    public void HumanShoot() {
        if(!waitbullets.isEmpty()){
            Direction direction = waitbullets.getLast();  //get
            waitbullets.removeLast();
            double x = position.getX()*BLOCKSIZE + BLOCKSIZE/2;
            double y = position.getY()*BLOCKSIZE + BLOCKSIZE/2;
            Bullet bullet = null;
            try {
                bullet = bulletFactory.getBullet(x,y,direction,camp,atk,id);
            }
            catch (Exception e){
                e.printStackTrace();
            }
            //TODO:共享变量
            synchronized (bullets){
                bullets.add(bullet);
            }
        }
    }

    @Override
    public void skill(){

    }
    @Override
    public void cure(){

    }

    private Direction enCode(int i) {
        switch (i) {
            case 0:
                return Direction.UP;
            case 1:
                return Direction.DOWN;
            case 2:
                return Direction.LEFT;
            default:
                return Direction.RIGHT;
        }
    }

    @Override
    public void run() {
        while (true) {
            synchronized (Server.chosen_id2) {
                if (id == Server.chosen_id2){
                    IsPlayer = true;
                    synchronized (Server.message_queue2){
                        if (Server.message_queue2.isEmpty()==false){
                            Message message = Server.message_queue2.remove();
                            if(message.MessageType==2){
                                KeyCode keyCode = message.KEYCODE;
                                try {
                                    handle(keyCode,false);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                } else
                    IsPlayer = false;
            }
            if (IsPlayer && Isalive) {
                this.HumanMove();
                this.HumanShoot();
            } else if (Isalive) {
                try {
                    this.AutoMove();
                    this.AutoShoot();
                    this.skill();
                    Thread.sleep(1000 / rate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
