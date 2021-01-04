package message;

import javafx.scene.input.KeyCode;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    /*
    -1: null
    0: Login
    1: Press READY button
    2: Keyboard Event
    100: 登录确认
    101: 双方准备完成
    102：转发地图信息
    103: 角色切换消息
     */

    public int MessageType = -1;
    public int srcId = -1;
    public KeyCode KEYCODE;
    public String UserName = null;
    public String KindName = null;
    public boolean WinnerName = true;
    public ArrayList<Boolean> creature_status = new ArrayList<>();
    public ArrayList<Integer> creture_position_X = new ArrayList<>();
    public ArrayList<Integer> creture_position_Y = new ArrayList<>();
    public ArrayList<Integer> creature_max_hp = new ArrayList<>();
    public ArrayList<Integer> creature_current_hp = new ArrayList<>();
    public ArrayList<Boolean> bullet_camp = new ArrayList<>();
    public ArrayList<Double> bullet_position_X = new ArrayList<>();
    public ArrayList<Double> bullet_position_Y = new ArrayList<>();
    public int skill1 = 0;
    public int skill2 = 0;
    public int skillId1 = -1;
    public int skillId2 = -1;
}
