package com.yutak.hotzone.event;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class Subscriber<T extends Event> {


    public final void register(Map<Integer,Subscriber> subscribers){
        subscribers.put(getIdentify(),this);
    }


    public abstract  void process(T event);

    public abstract int getIdentify();



    private BiConsumer<? super Void, ? super Throwable> action;

    public BiConsumer<? super Void, ? super Throwable> getAction() {
        return action;
    }

    public  void setAction(BiConsumer<? super Void, ? super Throwable> action){
        this.action = action;
    }
}
