package server.creatrue.justice;
import server.battle.Map;
import server.bullet.Bullet;

import java.util.LinkedList;

public class Dawa extends JusticeCreature{

    public Dawa(Map map, LinkedList<Bullet>bullets){
        super(map,bullets);
        this.name = "Dawa";
        this.id = 0;
        //TODO: Set Features
        this.atk = 200;
    }

}
