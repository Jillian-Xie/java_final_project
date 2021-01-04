package server.creatrue.behaviour;

/** 生物被子弹击中
 * @author fsq
 */
/*
逻辑实现： 由BulletController判断是否击中地方生物,hit只进行对应生物血量减少
 */
public interface Hit {
    void hit(int atk); //生物被击中 实际扣血=子弹攻击力-防御力
}
