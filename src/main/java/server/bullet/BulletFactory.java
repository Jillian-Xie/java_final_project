package server.bullet;
import server.creatrue.Direction;

import java.io.Serializable;

public class BulletFactory implements Serializable {
    //简单工厂模式
    //发射者传入当前自身坐标，阵营，根据键盘输入确定
    public Bullet getBullet(double x, double y, Direction direction, boolean camp, int damage, int send_id){
        return new Bullet(x,y,direction,camp,damage,send_id);
    }
}
