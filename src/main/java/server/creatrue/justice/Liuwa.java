package server.creatrue.justice;

import server.battle.Map;
import server.bullet.Bullet;

import java.util.LinkedList;

public class Liuwa extends JusticeCreature{
    public Liuwa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.name = "Liuwa";
        this.id = 5;
        this.rate = 4;
    }
}
