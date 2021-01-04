package server.battle;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import server.bullet.Bullet;
import server.creatrue.Creature;
import message.Message;
import server.game.Server;

import java.lang.Runnable;

/**
 * 地图初步搭建
 *
 * @author fsq xlj
 * @version 0.1
 */

public class Map implements MapConfig, Runnable, Serializable {
    //TODO: Design map elements
    private Creature[][] blocks;       //存储生物，按照块划分地图
    private LinkedList<Bullet> bullets; //存储子弹

    public static int skill1 = 0, skill2 = 0;
    public static int skillId1 = -1, skillId2 = -1;

    public Map(LinkedList<Bullet> bullets) {
        this.bullets = bullets;
        blocks = new Creature[ROWS][COLUMNS];
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                blocks[i][j] = null;
            }
        }
    }

    public boolean InMap(int x, int y) {
        //判断是否在地图内
        return x >= 0 && x < ROWS && y >= 0 && y < COLUMNS;
    }

    public boolean IsEmpty(int x, int y) {
        //判断x,y上是否有生物
        return blocks[x][y] == null;
    }

    public void RemoveBlocks(int x, int y) {
        //把(x,y)上元素清空
        blocks[x][y] = null;
    }

    public void setBlocks(int x, int y, Creature creature) {
        //在(x,y)上放置生物
        blocks[x][y] = creature;
    }

    public Creature getCreature(int x, int y) {
        return blocks[x][y];
    }

    public boolean[] SearchEnemies(boolean camp, int x, int y) {
        //返回x,y方向上是否有敌方生物
        boolean[] arr = new boolean[]{false, false, false, false};  //上下左右
        for (int i = 0; i < ROWS; i++) {
            Creature c = blocks[i][y];
            if (c != null && c.getIsalive() && c.getCamp() != camp) {
                if (i < x) {
                    arr[0] = true;
                } else {
                    arr[1] = true;
                }
            }
        }
        for (int i = 0; i < 16; i++) {
            Creature c = blocks[x][i];
            if (c != null && c.getIsalive() && c.getCamp() != camp) {
                if (i < y) {
                    arr[2] = true;
                } else {
                    arr[3] = true;
                }
            }
        }
        return arr;
    }

    @Override
    public void run() {
        while (BattleController.IsStarted) {
            // TODO： 把下列代码改成先清空再填满，而不是每次重新新建
            ArrayList<Boolean> bullet_camp = new ArrayList<>();
            ArrayList<Double> bullet_position_X = new ArrayList<>();
            ArrayList<Double> bullet_position_Y = new ArrayList<>();

            boolean[] creature_status = new boolean[16];
            int[] creature_pos_X = new int[16];
            int[] creature_pos_Y = new int[16];
            int[] creature_max = new int[16];
            int[] creature_cur = new int[16];

            //生物
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    Creature c = getCreature(i, j);
                    if (c != null && c.getIsalive()) {
                        creature_status[c.id] = true;
                        creature_pos_X[c.id] = i;
                        creature_pos_Y[c.id] = j;
                        synchronized (c) {
                            int maxHp = c.getMaxhp();
                            int curHp = c.getCurhp();
                            creature_max[c.id] = maxHp;
                            creature_cur[c.id] = curHp;
                            int curHpLine = curHp * BLOCKSIZE / maxHp; //...注意整除  不能用cur/max * size
                        }
                    } else if (c != null && !c.getIsalive()) {
                        creature_status[c.id] = false;
                        creature_pos_X[c.id] = i;
                        creature_pos_Y[c.id] = j;
                    }
                }
            }

            //子弹
            //画子弹时不能添加新子弹
            for (Bullet bullet : bullets) {
                if (bullet.getCamp() == true) {
                    bullet_camp.add(true);
                    bullet_position_X.add(bullet.getX());
                    bullet_position_Y.add(bullet.getY());
                } else {
                    bullet_camp.add(false);
                    bullet_position_X.add(bullet.getX());
                    bullet_position_Y.add(bullet.getY());
                }
            }
            try {
                Thread.sleep(50);

                // TODO: 检查消息填写是否符合要求
                Message message = new Message();
                if (skill1 > 0) {
                    message.skill1 = skill1;
                    message.skillId1 = skillId1;
                    skill1 -= 1;
                }
                if (skill2 > 0) {
                    message.skill2 = skill2;
                    message.skillId2 = skillId2;
                    skill2 -= 1;
                }
                boolean justiceDead = true;
                boolean evilDead = true;
                for (int i = 0; i < 8; i++) {
                    if (creature_status[i]) {
                        justiceDead = false;
                    }
                    if (creature_status[i + 8]) {
                        evilDead = false;
                    }
                }
                if (justiceDead) {
                    message.MessageType = 999;
                    message.WinnerName = false;
                } else if (evilDead) {
                    message.MessageType = 999;
                    message.WinnerName = true;
                } else {
                    message.MessageType = 102;
                }
                message.bullet_position_Y = bullet_position_Y;
                message.bullet_position_X = bullet_position_X;
                message.bullet_camp = bullet_camp;
                for (int i = 0; i < 16; ++i) {
                    message.creature_status.add(creature_status[i]);
                    message.creture_position_X.add(creature_pos_X[i]);
                    message.creture_position_Y.add(creature_pos_Y[i]);
                    message.creature_current_hp.add(creature_cur[i]);
                    message.creature_max_hp.add(creature_max[i]);
                }
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
                                System.out.println("Map message send!");
                            }
                        }
                    }
                    if(message.MessageType == 999){ //MYEDIT
                            Server.wlock.lock();
                            try {
                                Server.period = 0;
                            } finally {
                                Server.wlock.unlock();
                            }
                    }
                }
            } catch (java.net.SocketException e) {
                System.out.println("ClientSocket has closed");
                break;
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
