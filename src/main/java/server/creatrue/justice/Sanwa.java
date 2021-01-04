package server.creatrue.justice;

import server.battle.Map;
import server.bullet.Bullet;

import java.util.LinkedList;

public class Sanwa extends JusticeCreature{
    public Sanwa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.name = "Sanwa";
        this.id = 2;
        this.atk = 75;
    }

    //三娃受别人一半的伤害
    @Override
    public void hit(int damage){
        int atk = (int)((damage - defense)/2);
        if(atk>=cur_hp){
            cur_hp = 0;
            Isalive = false;  //生物死亡,对应使用墓碑替换原生物
        }
        else{
            cur_hp -= atk;
        }
    }
}
