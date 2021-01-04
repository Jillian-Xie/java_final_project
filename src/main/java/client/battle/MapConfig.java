package client.battle;

import annotations.ClassInfo;

@ClassInfo(CompleteTime = "2020-12-29 11:30",Description = "Store Map information")
public interface MapConfig {
    public int MAPWIDTH = 960;   //地图宽
    public int MAPHEIGHT= 720;   //地图高
    public int BLOCKSIZE = 60;   //正方形块长度
    public int ROWS = 12;        //行数
    public int COLUMNS = 16;     //列数
    //16*12个块儿, 960*720的地图大小
    public int CREATURESIZE = 55;//生物大小
}
