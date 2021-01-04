package server.bullet;

public interface BulletConfig {
    //子弹参数
    double STEP = 1;  //移动步长   TODO: 假如修改步长，需要修改测试的值
    int RADIUS = 15;  //子弹半径
    int REFRESH_RATE = 50;  //刷新频率
}
