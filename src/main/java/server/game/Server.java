package server.game;

import server.battle.BattleController;
import server.battle.MapConfig;
import message.Message;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Server extends Application implements MapConfig {

    private static ServerSocket server;
    private Socket clientsocket;
    public static Hashtable ht_out = new Hashtable();
    public static Hashtable htPlayer = new Hashtable();
    protected ObjectInputStream inf;
    protected ObjectOutputStream out;
    public static int number = 0;
    public static int connection_num = 0;
    public static int period = 0;
    public static String KindDecided = null;
    public static Integer chosen_id1 = 0;
    public static Integer chosen_id2 = 8;
    public static ConcurrentLinkedQueue<Message> message_queue1 = new ConcurrentLinkedQueue<>();
    public static ConcurrentLinkedQueue<Message> message_queue2 = new ConcurrentLinkedQueue<>();
    public static ReadWriteLock rwlock = new ReentrantReadWriteLock();
    public static Lock rlock = rwlock.readLock();
    public static Lock wlock = rwlock.writeLock();

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
        server = new ServerSocket(8888);
        System.out.println("<---- 服务器已启动 ---->");
        while (true) {
            clientsocket = server.accept();
            System.out.println("<---- 收到连接：" + clientsocket.toString() + "---->");
            wlock.lock();
            connection_num++;
            wlock.unlock();
            inf = new ObjectInputStream(clientsocket.getInputStream());
            out = new ObjectOutputStream(clientsocket.getOutputStream());
            ht_out.put(clientsocket, out);
            System.out.println(ht_out);
            ServerThread serverThread = new ServerThread(clientsocket, ht_out, out, inf);
            Thread thread = new Thread(serverThread);
            thread.start();
            rlock.lock();
            try {
                if (connection_num == 2) {
                    System.out.println("connection_num == 2");
                    break;
                }
            } finally {
                rlock.unlock();
            }
        }
        while (true) {
            rlock.lock();
            try {
                if (Server.period == 1) {
                    break;
                }
            } finally {
                rlock.unlock();
            }
        }
        BattleController bat = new BattleController();
        bat.InitializeMap();
        while (true) {
            rlock.lock();
            try {
                if (Server.period == 0) {
                    System.out.println("period == 0");
                    break;
                }
            } finally {
                rlock.unlock();
            }
        }
        for (Iterator it = ht_out.keySet().iterator(); it.hasNext(); ) {
            Socket key = (Socket) it.next();
            if (!key.isClosed()) {
                try {
                    key.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.exit(0);
    }
}

class ServerThread extends Thread implements Runnable {
    private Socket clientsocket;
    private String player_name = null;
    private int camp = 2;
    private Hashtable ht_out;
    protected ObjectOutputStream out;
    protected ObjectInputStream inf;
    protected Message receivedMessage = new Message();
    protected static Message returnMessage = new Message();

    public ServerThread(Socket clientsocket, Hashtable ht_out, ObjectOutputStream out, ObjectInputStream inf) {
        this.clientsocket = clientsocket;
        this.ht_out = ht_out;
        this.out = out;
        this.inf = inf;
    }

    public void run() {
        try {
            while (true) {
                //获取发送来的消息
                synchronized (out) {
                    receivedMessage = (Message) inf.readObject();
                    System.out.println("get message from client");
                }

                //获得返回消息
                returnMessage = processMessage(receivedMessage, clientsocket);

                if (receivedMessage.MessageType == -1) continue;

                if (!Server.htPlayer.isEmpty()) {
                    //向其他玩家转发
                    synchronized (ht_out) {
                        for (Iterator it = ht_out.keySet().iterator(); it.hasNext(); ) {
                            System.out.println("sending to player");
                            Socket key = (Socket) it.next();
                            ObjectOutputStream outData = (ObjectOutputStream) ht_out.get(key);
                            InputStream inData = key.getInputStream();
                            synchronized (inData) {
                                outData.writeObject(returnMessage);
                                if (returnMessage.MessageType == 101) {
                                    Server.wlock.lock();
                                    try {
                                        Server.period = 1;
                                    } finally {
                                        Server.wlock.unlock();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (EOFException e) {
            System.out.print("GameOver!");
            try {
                this.clientsocket.close();
                for (Iterator it = ht_out.keySet().iterator(); it.hasNext(); ) {
                    Socket key = (Socket) it.next();
                    if (!key.isClosed()) {
                        Message message = new Message();
                        message.MessageType = 999;
                        if (this.camp == 1)
                            message.WinnerName = false;
                        else {
                            message.WinnerName = true;
                        }
                        ObjectOutputStream outData = (ObjectOutputStream) ht_out.get(key);
                        outData.writeObject(message);
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            Server.wlock.lock();
            try {
                Server.period = 0;
                System.out.println("Set period = 0 cause client close");
            } finally {
                Server.wlock.unlock();
            }
            System.exit(0);
        }
    }

    private Message processMessage(Message receivedMessage, Socket clientsocket) {
        Message returnMessage = new Message();
        switch (receivedMessage.MessageType) {
            case 0:
                returnMessage.MessageType = 100;
                if (Server.KindDecided == null) {
                    Random r = new Random();
                    int random_num = r.nextInt();
                    if (random_num % 2 == 0) {
                        returnMessage.KindName = receivedMessage.UserName;
                        Server.KindDecided = receivedMessage.UserName;
                        camp = 1;
                    } else {
                        returnMessage.KindName = "NotMe";
                        Server.KindDecided = "NotMe";
                    }
                } else if (Server.KindDecided.compareTo("NotMe") == 0) {
                    returnMessage.KindName = receivedMessage.UserName;
                    Server.KindDecided = receivedMessage.UserName;
                    camp = 1;
                } else {
                    returnMessage.KindName = Server.KindDecided;
                }
                Server.htPlayer.put(receivedMessage.UserName, clientsocket);
                System.out.println("Get User: " + receivedMessage.UserName);
                break;
            case 1:
                System.out.println("Ready signal received!");
                Server.number++;
                if (receivedMessage.srcId <= 7) {
                    Server.chosen_id1 = receivedMessage.srcId;
                } else {
                    Server.chosen_id2 = receivedMessage.srcId;
                }
                if (Server.number >= 2) {
                    returnMessage.MessageType = 101;
                }
                break;
            case 2:
                System.out.println("Keyboard event received!");
                if (camp == 1) {
                    Server.message_queue1.add(receivedMessage);
                    System.out.print("收到来自葫芦娃的键盘操作：");
                    System.out.println(receivedMessage.KEYCODE);
                } else {
                    Server.message_queue2.add(receivedMessage);
                    System.out.print("收到来自妖精的键盘操作：");
                    System.out.println(receivedMessage.KEYCODE);
                }
                break;
        }
        return returnMessage;
    }
}
