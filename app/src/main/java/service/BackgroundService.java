package service;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kcl.hirus.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import Interface.ExcelInterface;
import Interface.GeoInterface;
import activity.LodingActivity;
import activity.MainActivity;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class BackgroundService extends Service implements ExcelInterface, GeoInterface {
   private final static String TAG = BackgroundService.class.getSimpleName();

   private Context context = null;
  String beforeStr = null;
   String afterStr = null;

    Workbook wb;
    int bestDesease;
    int secondDesease;
    int thirdDesease;
    Double latitude = 0.0;
    Double longitude = 0.0;
    Geocoder geocoder;

    String bestDeseaseName;
    String secondDeseaseName;
    String thirdDeseaseName;
    SharedPreferences prefs;
    GpsTrackerService gpsTrackerService;
    public String addressArr = null;
    public String do_ = null;
    public String si = null;

    //notiThread notiThread = new notiThread();

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
    public void getExcelData(String addr, String sii) {
        try {
            String si = sii;
            ArrayList<Integer> arrl = new ArrayList<>();
            int addressPosition = 0;
            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int rowTotal = sheet.getRows();
                    int colTotal = sheet.getColumns(); // 전체 컬럼

                    for(int i = 1; i < rowTotal; i++){
                        String contents = sheet.getCell(0, i).getContents().replaceAll("\\P{Print}","").trim();
                        /*데이터 양이 많아지니 유니코드 문제로 equls가 false나옴, replace구문으로 해결, 유니코드 문자 제거하는 구문*/

                        if(addr.contains(contents)||si.contains(contents)){//구를 찾을 경우
                            addressPosition = i;
                            break;
                        }
                        System.gc();
                    }

                    for(int j = 1; j < colTotal; j++){
                        Cell iCnt = sheet.getCell(j, addressPosition); //감염병 환자 수
                        arrl.add(Integer.parseInt(iCnt.getContents()));
                    }

                    Collections.sort(arrl);
                    Collections.reverse(arrl);
                    bestDesease = arrl.remove(0);
                    secondDesease = arrl.remove(0);
                    thirdDesease = arrl.remove(0);

                    for(int i = 1; i < colTotal; i++){
                        Cell iCnt = sheet.getCell(i, addressPosition);
                        if(bestDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            bestDeseaseName = D.getContents();
                        }
                        if(secondDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            secondDeseaseName = D.getContents();
                        }
                        if(thirdDesease == Integer.parseInt(iCnt.getContents())){
                            Cell D = sheet.getCell(i, 0);
                            thirdDeseaseName = D.getContents();
                        }
                    }
                }
            }
        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public int selectDesease(String desease) {
        return 0;
    }

    @Override
    public String copyExcelDataToDatabase(TextView address, String desease) {
        return null;
    }

    @Override
    public void getData() {

    }

    @Override
    public void reverseCoding() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        //최초 한번만 호출
        Log.d(TAG, "BackgroundService.onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            this.context = getApplicationContext();
            InputStream is = context.getResources().getAssets().open("database.xls");
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){e.printStackTrace();}


        final String strId ="10" ;
        final String strTitle = "하이러스";

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, strId)
               .setContentText("감염병 정보를 탐색하고 있습니다.")
                .setSmallIcon(R.drawable.hicon);

        Intent notiIntent = new Intent(getApplicationContext(), LodingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent,0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(strId,strTitle,NotificationManager.IMPORTANCE_HIGH));
        }
        startForeground(1, builder.build());


        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                GpsTrackerService gpsTracker = new GpsTrackerService(getApplicationContext());
                latitude = gpsTracker.getLatitude();
                longitude = gpsTracker.getLongitude();
                geocoder = new Geocoder(getApplicationContext());
                //Log.d("backGT",latitude + " +" + longitude);
                if(beforeStr==null){
                    beforeStr = LodingActivity.addstr;
                }
                List<Address> list = null;
                try {
                    list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수
                    //Log.d("backGT",list+" ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (list != null) {
                    if (list.size()==0) {
                    } else {
                        String cut[] = list.get(0).toString().split(" ");
                        afterStr = cut[1] + " " + cut[2] + " " + cut[3];
                        do_ = cut[2];
                        si = cut[3];
                    }
                }
                Log.d("backGT1",LodingActivity.addstr+" "+beforeStr+" "+afterStr);
                getExcelData(do_,si);

                if (!(beforeStr.trim().equals(afterStr.trim()))) {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(getApplicationContext(), LodingActivity.class);
                    notificationIntent.putExtra("notificationId", 0); //전달할 값
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.hicon)) //BitMap 이미지 요구
                            .setContentTitle("해당 지역의 감염병 정보입니다.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 MainActivity로 이동하도록 설정

                    Log.d("back", prefs.getString("backsetlist", ""));
                    if (prefs.getString("backsetlist", "").contains("소리")) {
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    }
                    if (prefs.getString("backsetlist", "").contains("진동")) {
                        builder.setVibrate(new long[]{0, 500, 500, 500});
                    }

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                    inboxStyle.addLine(bestDeseaseName);
                    inboxStyle.addLine(secondDeseaseName);
                    inboxStyle.addLine(thirdDeseaseName);

                    //OREO API 26 이상에서는 채널 필요
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                        CharSequence channelName = "해당 지역의 감염병 정보입니다.";
                        String description = "테스트1";
                        int importance = NotificationManager.IMPORTANCE_HIGH;

                        NotificationChannel channel = new NotificationChannel("1", channelName, importance);
                        channel.setDescription(description);

                        // 노티피케이션 채널을 시스템에 등록
                        assert notificationManager != null;
                        notificationManager.createNotificationChannel(channel);

                    } else
                        builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

                    assert notificationManager != null;
                    notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
                }



                beforeStr = afterStr;
            }

            public void onProviderDisabled(String provider) {

            }

            public void onProviderEnabled(String provider) {

            }

            public void onStatusChanged(String provider, int status, Bundle extras) {

            }
        };

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "BackgroundService.onStartCommand");
       super.onStartCommand(intent, flags, startId);
       //notiThread.start();
        //startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {

        //종료될때 실행
        Log.d(TAG, "BackgroundService.onDestroy");
        Intent broadcastIntent = new Intent("com.kcl.hirus.RestartService");
        sendBroadcast(broadcastIntent);
        //notiThread.interrupt();
        //stopTimerTask();
    }

    @Override
    public boolean stopService(Intent name) {
        Log.d("background","stopService");
        return super.stopService(name);
    }

    /*private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();

        initializerTimerTask();

        timer.schedule(timerTask, 2000, 5000);
    }*/


    /*public void initializerTimerTask() {
        timerTask = new TimerTask() {
            @Override
            public void run() {

            }
        };
    }*/


   /* public void stopTimerTask(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }*/

    @Override
    public void onTaskRemoved(Intent rootIntent) {

        Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime()+5000, pendingIntent);
        Log.d(TAG, "BackgroundService.onTaskRemoved");
        super.onTaskRemoved(rootIntent);
    }

    /*class notiThread extends Thread{
        @Override
        public void run() {
            while(true) {
                GpsTrackerService gpsTrackerService = new GpsTrackerService(context);
                geocoder = new Geocoder(context);

                Log.d("back", "reverse");
                List<Address> list = null;
                try {
                    latitude = gpsTrackerService.getLatitude();
                    longitude = gpsTrackerService.getLongitude();
                    Log.e("test", String.valueOf(longitude) + latitude);
                    list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
                }
                if (list != null) {
                    if (list.size() == 0) {
                    } else {
                        String cut[] = list.get(0).toString().split(" ");
                        do_ = cut[1] + " " + cut[2];
                        addressArr = cut[1] + " " + cut[2] + " " + cut[3];
                        Log.e("test", addressArr);
                    }
                }

                afterStr = addressArr;
                if (beforeStr == null) {
                    beforeStr = afterStr;
                }

                if (afterStr != null) {
                    String arr[] = afterStr.split(" ");
                    getExcelData(arr[1], arr[2]);
                }


                Log.d("background", beforeStr + " : " + afterStr);
                if (!(beforeStr.trim().equals(afterStr.trim()))) {

                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                    Intent notificationIntent = new Intent(getApplicationContext(), LodingActivity.class);
                    notificationIntent.putExtra("notificationId", 0); //전달할 값
                    notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.hicon)) //BitMap 이미지 요구
                            .setContentTitle("해당 지역의 감염병 정보입니다.")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 MainActivity로 이동하도록 설정

                    Log.d("back", prefs.getString("backsetlist", ""));
                    if (prefs.getString("backsetlist", "").contains("소리")) {
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    }
                    if (prefs.getString("backsetlist", "").contains("진동")) {
                        builder.setVibrate(new long[]{0, 500, 500, 500});
                    }

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                    inboxStyle.addLine(bestDeseaseName);
                    inboxStyle.addLine(secondDeseaseName);
                    inboxStyle.addLine(thirdDeseaseName);

                    //OREO API 26 이상에서는 채널 필요
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                        builder.setSmallIcon(R.drawable.ic_launcher_foreground); //mipmap 사용시 Oreo 이상에서 시스템 UI 에러남
                        CharSequence channelName = "해당 지역의 감염병 정보입니다.";
                        String description = "테스트1";
                        int importance = NotificationManager.IMPORTANCE_DEFAULT;

                        NotificationChannel channel = new NotificationChannel("1", channelName, importance);
                        channel.setDescription(description);

                        // 노티피케이션 채널을 시스템에 등록
                        assert notificationManager != null;
                        notificationManager.createNotificationChannel(channel);

                    } else
                        builder.setSmallIcon(R.mipmap.ic_launcher); // Oreo 이하에서 mipmap 사용하지 않으면 Couldn't create icon: StatusBarIcon 에러남

                    assert notificationManager != null;
                    notificationManager.notify(1234, builder.build()); // 고유숫자로 노티피케이션 동작시킴
                }

                beforeStr = afterStr;
                afterStr = null;
                try {
                    sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/


}
