package client.battle;

import annotations.ClassInfo;
import message.Message;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@ClassInfo(CompleteTime = "2020-12-29 11:17",Description = "Store global variables")
public class Globle {
    public static int chosen_id = 0;
    public static boolean kind = false;
    public static String name = "";
    public static Socket socket;
    public static ObjectOutputStream client_out;
    public static ObjectInputStream client_inf;
    public static ConcurrentLinkedQueue<Message> message_queue = new ConcurrentLinkedQueue<Message>();
    public static Message map_message = new Message();
    public static boolean IsStarted = true;
    public static ReadWriteLock rwlock = new ReentrantReadWriteLock();
    public static Lock rlock = rwlock.readLock();
    public static Lock wlock = rwlock.writeLock();
    public static LinkedList<Message> replay_queue = new LinkedList<>();
}
