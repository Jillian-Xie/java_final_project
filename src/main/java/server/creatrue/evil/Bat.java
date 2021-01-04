package server.creatrue.evil;

import server.battle.Map;
import server.bullet.Bullet;

import java.util.LinkedList;

public class Bat extends EvilCreature{
    public Bat(Map map, LinkedList<Bullet> bullets, int id){
        super(map,bullets,id);
        this.name = "Bat";
    }
}
