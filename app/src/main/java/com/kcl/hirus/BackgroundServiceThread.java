package com.kcl.hirus;


import android.os.Handler;

public class BackgroundServiceThread extends Thread {
    Handler handler;
    boolean isRun = true;

    public BackgroundServiceThread(Handler handler) {
        this.handler = handler;
    }

    public void stopThread() {
        synchronized (this){
            this.isRun = false;
        }
    }

    public void run() {
        while(isRun){
            handler.sendEmptyMessage(0);
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){

            }
        }
    }
}
