package server.creatrue.justice;

import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Creature;
import server.battle.MapConfig;

import java.util.LinkedList;

public class Qiwa extends JusticeCreature{
    private int skillCount;
    public Qiwa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.name = "Qiwa";
        this.id = 6;
        skillCount = 1;
    }
    @Override
    public void skill(){
        if(skillCount>0){
            skillCount -= 1;
            for (int i = 0; i < MapConfig.ROWS; i++) {
                for (int j = 0; j < MapConfig.COLUMNS; j++) {
                    Creature creature = map.getCreature(i,j);
                    if(creature!=null){
                        if(creature.id>=10&&creature.id<=15&&creature.getIsalive()){
                            creature.hit(Integer.MAX_VALUE);    //秒杀非蛇精和蝎子精以外生物
                            Map.skill1 = 30;
                            Map.skillId1 = this.id;
                            return;
                        }
                    }
                }
            }
        }
    }
}
