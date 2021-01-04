package server.creatrue.justice;
import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Creature;

import java.util.LinkedList;

public class Grandpa extends JusticeCreature {
    public Grandpa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.id = 7;
        this.name = "Grandpa";
    }
    @Override
    public void cure(){
        for(int i=position.getX()-1;i< position.getX()+1;i++){
            for(int j=position.getY()-1;j< position.getY()+1;j++){
                if(map.InMap(i,j)){
                    Creature creature = map.getCreature(i,j);
                    if(creature != null){
                        if(creature.getCamp()==this.camp&& creature.id!=7){
                            creature.beCured();
                        }
                    }
                }
            }
        }
    }
}
