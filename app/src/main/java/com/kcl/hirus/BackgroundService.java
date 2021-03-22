package com.kcl.hirus;


import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Binder;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.util.Timer;
import java.util.TimerTask;

public class BackgroundService extends Service{
   private final static String TAG = BackgroundService.class.getSimpleName();

   private Context context = null;
   public int counter = 0;
   String beforeStr = null;
   String afterStr = null;
    String text = null;

   public BackgroundService(){}

   public BackgroundService(Context applicationContext){
       super();
       context = applicationContext;
   }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //최초 한번만 호출
        Log.d(TAG, "BackgroundService.onCreate");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final String strId ="0" ;
            final String strTitle = getString(R.string.app_name);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = notificationManager.getNotificationChannel(strId);
            if (channel == null) {
                channel = new NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_HIGH);
                notificationManager.createNotificationChannel(channel);
            }

            Notification notification = new NotificationCompat.Builder(this, strId).build();
            startForeground(1, notification);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BackgroundService.onStartCommand");
       super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //종료될때 실행
        Log.d(TAG, "BackgroundService.onDestroy");
        Intent broadcastIntent = new Intent("com.kcl.hirus.RestartService");
        sendBroadcast(broadcastIntent);
        stopTimerTask();
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        timer = new Timer();

        initializerTimerTask();

        timer.schedule(timerTask, 3, 10000);
    }

    public void initializerTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {
                afterStr = MainActivity.addressstr;
                if(beforeStr == null){
                    beforeStr = afterStr;
                }

                if(afterStr !=null) {
                    String arr[] = afterStr.split(" ");
                    Excel.getExcelData(arr[1], arr[2]);
                  //  text = afterStr + "의 감염병 현황입니다.\r" + Excel.bestDeseaseName + "\r" + Excel.secondDeseaseName + "\r" + Excel.thirdDeseaseName;
                }


               if(beforeStr == afterStr){
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(getApplicationContext(), LodingActivity.class);
                    notificationIntent.putExtra("notificationId", 0); //전달할 값
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK) ;
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,  PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground)) //BitMap 이미지 요구
                            .setContentTitle("해당 지역의 감염병 정보입니다.")
                           // .setContentText(text)
                            // 더 많은 내용이라서 일부만 보여줘야 하는 경우 아래 주석을 제거하면 setContentText에 있는 문자열 대신 아래 문자열을 보여줌
                            //.setStyle(new NotificationCompat.BigTextStyle().bigText("더 많은 내용을 보여줘야 하는 경우..."))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent) // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                            .setAutoCancel(true);

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                   // inboxStyle.addLine(afterStr + "의 감염병 현황입니다.");
                   inboxStyle.addLine(Excel.bestDeseaseName);
                   inboxStyle.addLine(Excel.secondDeseaseName);
                   inboxStyle.addLine(Excel.thirdDeseaseName);

                    //OREO API 26 이상에서는 채널 필요
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                        CharSequence channelName  = "해당 지역의 감염병 정보입니다.";
                        String description = "테스트1";
                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        NotificationChannel channel = new NotificationChannel("1", channelName , importance);
                        channel.setDescription(description);

                        // 노티피케이션 채널을 시스템에 등록
                        assert notificationManager != null;
                        notificationManager.createNotificationChannel(channel);

                    }else builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

                    assert notificationManager != null;
                    notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
                }

                beforeStr = afterStr;
                afterStr = null;

            }
        };
    }

    public void stopTimerTask(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+5000, pendingIntent);
        Log.d(TAG, "BackgroundService.onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }
}
