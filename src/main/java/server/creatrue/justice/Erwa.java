package server.creatrue.justice;

import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Creature;
import server.creatrue.Direction;
import server.creatrue.behaviour.Skill;
import server.battle.MapConfig;

import java.util.LinkedList;

public class Erwa extends JusticeCreature implements Skill {
    private int atkCount;
    private boolean skillCount;
    public Erwa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.name = "Erwa";
        this.id = 1;
        this.atkCount = 0;
        this.skillCount = false;
    }
    @Override
    public void skill(){
        if(skillCount){
            skillCount = false;
            //每次调用时,寻找蛇精或者蝎子精，对其进行攻击
            for (int i = 0; i < MapConfig.ROWS; i++) {
                for (int j = 0; j < MapConfig.COLUMNS; j++) {
                    Creature creature = map.getCreature(i,j);
                    if(creature!=null){
                        if((creature.id==8||creature.id==9)&&creature.getIsalive()){
                            creature.hit(atk);
                            Map.skill1 = 30;
                            Map.skillId1 = this.id;
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
            double x = position.getX() * MapConfig.BLOCKSIZE + MapConfig.BLOCKSIZE / 2;
            double y = position.getY() * MapConfig.BLOCKSIZE + MapConfig.BLOCKSIZE / 2;
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
        if(atkCount/5!=0){
            atkCount %= 5;
            skillCount = true;
        }
    }

}
