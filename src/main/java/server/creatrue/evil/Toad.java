package server.creatrue.evil;

import server.battle.Map;
import server.bullet.Bullet;

import java.util.LinkedList;

public class Toad extends EvilCreature{
    public Toad(Map map, LinkedList<Bullet> bullets, int id){
        super(map,bullets,id);
        this.name = "Toad";
    }
}
