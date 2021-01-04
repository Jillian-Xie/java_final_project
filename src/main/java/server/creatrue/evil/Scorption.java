package server.creatrue.evil;

import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Creature;
import server.creatrue.Direction;

import java.util.LinkedList;

/*
*蝎子精
 */
public class Scorption extends EvilCreature{
    private int skillCount;
    private int atkCount;
    public Scorption(Map map, LinkedList<Bullet> bullets, int id){
        super(map,bullets,id);
        this.name = "Scorption";
        this.skillCount = 0;
        this.atkCount = 0;
    }
    @Override
    public void skill(){
        if(skillCount>0){
            skillCount -= 1;
            //每次调用时,寻找所有葫芦娃生物
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLUMNS; j++) {
                    Creature creature = map.getCreature(i,j);
                    if(creature!=null){
                        if(creature.id<7&&creature.getIsalive()){
                            creature.hit(atk);
                            Map.skill2 = 30;
                            Map.skillId2 = this.id;
                            return;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void HumanShoot() {
        if (!waitbullets.isEmpty()) {
            Direction direction = waitbullets.getLast();  //get
            waitbullets.removeLast();
            double x = position.getX() * BLOCKSIZE + BLOCKSIZE / 2;
            double y = position.getY() * BLOCKSIZE + BLOCKSIZE / 2;
            Bullet bullet = null;
            try {
                bullet = bulletFactory.getBullet(x, y, direction, camp, atk, id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //TODO:共享变量
            synchronized (bullets) {
                bullets.add(bullet);
            }
        }
        this.atkCount+=1;
        if(atkCount/10!=0){
            atkCount %= 10;
            skillCount +=1;
        }
    }

}
