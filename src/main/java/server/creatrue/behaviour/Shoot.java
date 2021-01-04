package server.creatrue.behaviour;

/** 射击行为
 * author: fsq
 */
public interface Shoot {
    void HumanShoot(); //按照waitbullets列表发射子弹
    void AutoShoot();  //需要制定子弹发射策略
}
