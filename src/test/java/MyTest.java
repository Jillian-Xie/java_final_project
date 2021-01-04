import server.battle.Map;
import server.bullet.Bullet;
import server.bullet.BulletController;
import server.bullet.BulletFactory;
import server.creatrue.Creature;
import server.creatrue.Direction;
import server.creatrue.evil.Snake;
import server.creatrue.justice.Dawa;
import server.creatrue.justice.Erwa;
import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;


public class MyTest {

    @Test
    // TODO: 添加注解
    public void BulletMoveTest(){
        BulletFactory bulletFactory = new BulletFactory();
        Bullet bullet = bulletFactory.getBullet(50,50, Direction.RIGHT,true,10,0);
        assertEquals((int)bullet.getX(),(int)50);
        assertEquals((int)bullet.getY(),(int)50);
        bullet.move();
        assertEquals((int)bullet.getX(),(int)50);
        assertEquals((int)bullet.getY(),(int)51);
    }
    @Test
    public void CreatureMoveTest(){
        LinkedList<Bullet>bullets = new LinkedList<>();
        Map map = new Map(bullets);
        Creature creature = new Dawa(map,bullets);
        creature.setPosition(0,0);
        map.setBlocks(0,0,creature);
        Creature hinder = new Erwa(map,bullets);
        hinder.setPosition(2,0);
        map.setBlocks(2,0,hinder);
        assertEquals((int)(creature.getPosition().getX()),(int)0);
        assertEquals((int)(creature.getPosition().getY()),(int)0);
        LinkedList<Direction>path = new LinkedList<Direction>(){{
            add(Direction.UP);
            add(Direction.UP);
            add(Direction.DOWN);
            add(Direction.DOWN);
            add(Direction.DOWN);
            add(Direction.RIGHT);
            add(Direction.RIGHT);
            add(Direction.LEFT);
            add(Direction.UP);
        }};

        for (Direction direction: path){
            creature.addPath(direction);
        }
        creature.HumanMove();
        assertEquals((int)(creature.getPosition().getX()),(int)0);
        assertEquals((int)(creature.getPosition().getY()),(int)0);
    }

    @Test
    public void CreatureHitTest(){
        LinkedList<Bullet>bullets = new LinkedList<>();
        Map map = new Map(bullets);
        BulletController bulletController = new BulletController(map, bullets);
        Creature creature = new Snake(map,bullets,8);
        creature.setPosition(0,1);
        map.setBlocks(0,1,creature);
        Bullet bullet = new Bullet(30,50,Direction.RIGHT,true,100,1);
        bullets.add(bullet);
        for (int i =0;i<15;i++)
            bulletController.moveAllBullets();
        assertEquals((int)bullet.getY(),(int)60);
        assertEquals((boolean)(bullets.isEmpty()),true);
        assertEquals((int)(creature.getCurhp()),(int)467);
    }
}
