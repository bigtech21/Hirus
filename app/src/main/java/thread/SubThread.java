package thread;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import fragment.SettingPreferenceFragment;
import service.BackgroundService;
import receiver.BootReceiver;

public class SubThread extends Thread{
    Context context;
    BackgroundService backgroundService;
    Intent serviceIntent;
    SharedPreferences prefs;
    public SubThread(){
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public SubThread(Context context, BackgroundService backgroundService){
        this.context = context;
        this.backgroundService = backgroundService;
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void run() {
        serviceInit();
        while(true) {
            try {
                Log.d("SubThread","run");
                sleep(10000);
                optioncheck();
                if(!(prefs.getString("backsetlist","").equals("사용 안함"))) {
                    serviceRun();
                }
            } catch (InterruptedException e) {
                Log.d("SubThread","intrrupted");
                e.printStackTrace();
            }

        }
    }

    public void serviceInit() {
        backgroundService = new BackgroundService(context);
        serviceIntent = new Intent(context, backgroundService.getClass());
    }

    public void serviceRun() {
            //실행중인지 확인
            if (!BootReceiver.isServiceRunning(context, backgroundService.getClass())) {
                context.startService(serviceIntent);
            }
    }

    public void optioncheck() {
        if((prefs.getString("backsetlist","").equals("사용 안함"))){
            Log.d("SubThread","disable");
            context.stopService(serviceIntent);
        }
        else{
            Log.d("SubThread","able");
            serviceRun();
        }
    }


}
