package server.creatrue;

import java.io.Serializable;

/**
 * 葫芦娃站在某个位置，也可看作某个位置上有葫芦娃
 * 那么是否可以以Pos作为Creature,Bullet容器，Map作为Pos容器进行抽象呢?
 * @author fsq
 * @version 1.0
 */

public class Position implements Serializable {
    //此处还是暂定是葫芦娃拥有位置
    private int x;
    private int y;
    //Postion(int x,int y)
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
}
