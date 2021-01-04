package server.creatrue.evil;

import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Creature;

import java.util.LinkedList;

public class Snake extends EvilCreature {
    public Snake(Map map, LinkedList<Bullet> bullets, int id){
        super(map,bullets,id);
        this.name = "Snake";
    }

    @Override
    public void cure(){
        for(int i=position.getX()-1;i< position.getX()+1;i++){
            for(int j=position.getY()-1;j< position.getY()+1;j++){
                if(map.InMap(i,j)){
                    Creature creature = map.getCreature(i,j);
                    if(creature != null&&creature.getCamp()){
                        if(creature.getIsalive()&& creature.id!=8){
                            creature.beCured();
                        }
                    }
                }
            }
        }
    }

}
