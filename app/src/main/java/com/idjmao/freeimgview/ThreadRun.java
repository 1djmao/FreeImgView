package com.idjmao.freeimgview;

import android.app.Activity;

public class ThreadRun {

    Activity context;
    Runnable newThreadRun;
    Runnable mainThreadRun;

    public ThreadRun(Runnable newThreadRun, Runnable mainThreadRun, Activity activity) {
        this.newThreadRun = newThreadRun;
        this.mainThreadRun = mainThreadRun;
        context=activity;
    }

    public void start(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                newThreadRun.run();
                context.runOnUiThread(mainThreadRun);
            }
        }).start();
    }
}
