package receiver;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import service.BackgroundService;

public class BootReceiver extends BroadcastReceiver {
    /*부팅 이벤트 받는 클래스*/
    private final static String TAG = BootReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();

        //액션 값이 부팅 완료일때 동작하는 조건문
        if(action.equals("android.intent.action.BOOT_COMPLETED")){
            //부팅 이후 처리할 동작들 작성
            Log.d(TAG, "ACTION : " + action);

            new Handler().postDelayed(new Runnable() {
                //3초 후에 실행
                @Override
                public void run() {
                    Intent serviceLuncher = new Intent(context, BackgroundService.class);
                    //기기 버전이 오레오 이상일 경우
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        context.startForegroundService(serviceLuncher);
                    } else {
                        context.startService(serviceLuncher);
                    }
                }
            },3000);
        }
    }

    public static boolean isServiceRunning(Context context, Class serviceClass){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i (TAG,"ServiceRunning? = "+true);
                return true;
            }
        }
        Log.i(TAG,"ServiceRunning? = "+ false);
        return false;
     }

}
