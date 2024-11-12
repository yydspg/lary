package com.yutak.hotzone.client.core;

import com.yutak.hotzone.entry.YutakEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CollectComponent {

    private final List<YutakEntry> data_1 ;
    private final List<YutakEntry> data_2 ;
    private final ReentrantLock lock_1;
    private final ReentrantLock lock_2;
    private volatile int limit = 1024;
    private  volatile int _node = 1;

    public CollectComponent() {
        data_1 = new ArrayList<>(1024);
        data_2 = new ArrayList<>(1024);
        lock_2 = new ReentrantLock();
        lock_1 = new ReentrantLock();
    }
    public final  void send(YutakEntry entry) {
        if(_node == 1) {
            lock_1.lock();
            if(data_1.size() == limit) {
                // rejected
                lock_1.unlock();
                return;
            }
            data_1.add(entry);
            lock_1.unlock();
            return;
        }else{
            lock_2.lock();
            if(data_2.size() == limit) {
                lock_2.unlock();
                return;
            }
            data_2.add(entry);
            lock_2.unlock();
            return;
        }
    }
    public final List<YutakEntry> read() {
        List<YutakEntry> data;
        if(1 == _node) {
            data = data_2.stream().toList();
                data_2.clear();
        }else {
            data = data_1.stream().toList();
                data_1.clear();
        }
        doSwitch();
        return data;
    }
    private  void doSwitch(){
        if(_node == 1) {
            _node = 2;
        }else {
            _node = 1;
        }
    }
}
