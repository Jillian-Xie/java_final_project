package server.bullet;


import server.creatrue.Direction;

import java.io.Serializable;

public class Bullet implements BulletConfig, Serializable {
    double x;
    double y;
    int sender_id;
    protected int damage;
    protected boolean camp;   //记录子弹所属阵营,防止队友伤害
    protected Direction direction; //子弹发射方向
    public Bullet(){}
    private int deCode(Direction direction){
        int result = -1;
        switch (direction){
            case UP:
                result = 0;
                break;
            case DOWN:
                result = 1;
                break;
            case LEFT:
                result = 2;
                break;
            case RIGHT:
                result = 3;
                break;
        }
        return result;
    }
    public Bullet(double x,double y,Direction direction,boolean camp,int damage,int sender_id){
        this.x = x;
        this.y = y;
        this.camp = camp;
        this.damage = damage;
        this.direction = direction;
        this.sender_id = sender_id;
    }
    public int getDamage(){
        return damage;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;}
    public boolean getCamp(){
        return camp;
    }

    public void move(){
        switch (direction){
            case UP:{
                this.x -= STEP;
                break;
            }
            case DOWN:{
                this.x += STEP;
                break;
            }
            case LEFT:{
                this.y -= STEP;
                break;
            }
            case RIGHT:{
                this.y += STEP;
                break;
            }
        }
    }

}
