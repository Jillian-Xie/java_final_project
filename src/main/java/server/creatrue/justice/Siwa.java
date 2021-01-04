package server.creatrue.justice;

import server.battle.Map;
import server.bullet.Bullet;
import server.creatrue.Direction;
import server.battle.MapConfig;

import java.util.LinkedList;

public class Siwa extends JusticeCreature{
    private int atkCount;
    private boolean skillCount;
    public Siwa(Map map, LinkedList<Bullet> bullets){
        super(map,bullets);
        this.name = "Siwa";
        this.id = 3;
        this.atkCount = 0;
        this.skillCount = false;
    }
    @Override
    public void skill(){
        if(skillCount){
            skillCount = false;
            for(int i = 0; i< MapConfig.COLUMNS; i++){
                Bullet bullet = bulletFactory.getBullet(MapConfig.BLOCKSIZE/2,i* MapConfig.BLOCKSIZE+ MapConfig.BLOCKSIZE/2, Direction.RIGHT,camp,atk,id);
                bullets.add(bullet);
            }
            Map.skill1 = 30;
            Map.skillId1 = this.id;
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
