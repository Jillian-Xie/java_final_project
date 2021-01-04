package server.bullet;
import java.util.LinkedList;
import server.battle.BattleController;
import server.battle.Map;
import server.battle.MapConfig;
import server.creatrue.Creature;

import java.util.ListIterator;
import java.util.concurrent.TimeUnit;


public class BulletController implements Runnable, MapConfig,BulletConfig {
    //子弹线程控制
    private LinkedList<Bullet> bullets;
    private Map map;
    private boolean OutOfMap(Bullet bullet){
        //以圆心是否出界为判断标准
        return (bullet.getX()<0.1||bullet.getY()<0.1||bullet.getX()>MAPHEIGHT-0.1||bullet.getY()>MAPWIDTH-0.1);
    }
    public BulletController(Map map, LinkedList<Bullet> bullets){
        this.map = map;
        this.bullets = bullets;
    }
    public void moveAllBullets(){
        //子弹移动逻辑：{击中or出界：移除},{else:正常移动}
        //击中 remove --> else: 移动 --> 出界:remove
        //移动中上锁,不可写
        synchronized (bullets){
            ListIterator<Bullet> iter = bullets.listIterator();
            while(iter.hasNext()) {
                boolean isHit = false;    //判断子弹有无击中生物
                Bullet bullet = iter.next();
                double bulletX = bullet.getX(); //圆心X
                double bulletY = bullet.getY(); //圆心Y
                int blockX = (int)(bulletX/BLOCKSIZE); //计算子弹所在方块
                int blockY = (int)(bulletY/BLOCKSIZE);
                System.out.println(bulletY);
                System.out.println(blockY);
                synchronized (map){
                    Creature c = map.getCreature(blockX,blockY);
                    if(c != null){
                        synchronized (c){
                            if((c.getCamp()!=bullet.getCamp())&&c.getIsalive()){
                                //TODO: 生物受伤害函数待写
                                System.out.println(bullet.getDamage());
                                c.hit(bullet.getDamage());
                                isHit = true;   //子弹消失
                                System.out.println("Hit");
                            }
                        }
                    }
                }
                if(!isHit){
                    try{
                        bullet.move();
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                else {
                    //子弹打中生物,移出队列
                    iter.remove();
                }
                //TODO: 出界判断
                if(OutOfMap(bullet)){
                    iter.remove();
                    System.out.println("?");
                }
            }
        }
    }

    @Override
    public void run(){
        while(BattleController.Isstarted()){
            try{
            moveAllBullets();
            TimeUnit.MILLISECONDS.sleep(5);
            //TimeUnit.MICROSECONDS.sleep(1000);
            }catch (InterruptedException e){
            }
        }
        System.out.println("Game Over, bullets thread ended");
    }
}
