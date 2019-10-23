package com.google.gps.model.events;

import org.greenrobot.eventbus.EventBus;

public class EventlBus {


    public static EventBus gBus;

    public static EventBus getgBus(){
        if (gBus==null){

            gBus= EventBus.getDefault();
        }
        return gBus;
    }

}
