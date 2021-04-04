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
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.kcl.hirus.R;

import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import activity.LodingActivity;
import activity.MainActivity;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class BackgroundService extends Service{
   private final static String TAG = BackgroundService.class.getSimpleName();

   private Context context = null;
   public int counter = 0;
   String beforeStr = null;
   String afterStr = null;
    String text = null;
    Workbook wb;
    int bestDesease;
    int secondDesease;
    int thirdDesease;
    String[] deseases = new String[67];
    int arr[] = new int[67];
    String bestDeseaseName;
    String secondDeseaseName;
    String thirdDeseaseName;
    SharedPreferences prefs;

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

    public void arrinit() {
        for(int i = 0; i < arr.length; i++){ //초기화
            arr[i] = 0;
        }
        for(int i = 0; i < deseases.length; i++){ //초기화
            deseases[i] = "";
        }
    }

    public void getExcelData(String addr, String sii) {
        try {
            String si = sii;
            arrinit();
            //배열 초기화
            for(int i = 0; i < deseases.length ; i++){
                deseases[i] = "";
            }

            int key, k, l;
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
                        Cell DName = sheet.getCell(j, 0); //감염병 이름
                        arr[j-1] = Integer.parseInt(iCnt.getContents());
                        deseases[j-1] = DName.getContents();
                    }

                    for(k = 1; k< arr.length; k++) { //삽입 정렬
                        key = arr[k];
                        for(l = k-1; l>=0 && arr[l]>key; l--) {
                            arr[l + 1] = arr[l];
                        }
                        arr[l+1] = key;
                    }
                    bestDesease = arr[arr.length-1];
                    secondDesease = arr[arr.length-2];
                    thirdDesease = arr[arr.length-3];

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
    public void onCreate() {
        super.onCreate();
        //최초 한번만 호출
        Log.d(TAG, "BackgroundService.onCreate");
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            this.context = context;
            InputStream is = context.getResources().getAssets().open("database.xls");
            wb = Workbook.getWorkbook(is);
        }catch (Exception e){}


        final String strId ="10" ;
        final String strTitle = "하이러스";//getString(R.string.app_name);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, strId)
                .setSmallIcon(R.mipmap.ic_hirus)
               .setContentText("감염병 정보를 탐색하고 있습니다.");
               // .setContentTitle("");

        Intent notiIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notiIntent,0);
        builder.setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            /*NotificationChannel channel = notificationManager.getNotificationChannel(strId);
            if (channel == null) {
                channel = new NotificationChannel(strId, strTitle, NotificationManager.IMPORTANCE_MIN);
                notificationManager.createNotificationChannel(channel);
            }*/
            notificationManager.createNotificationChannel(new NotificationChannel(strId,strTitle,NotificationManager.IMPORTANCE_MIN));
        }
        startForeground(1, builder.build());
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

    @Override
    public boolean stopService(Intent name) {
        Log.d("dddd","므엥엥");
        return super.stopService(name);
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        timer = new Timer();

        initializerTimerTask();

        timer.schedule(timerTask, 5000, 10000);
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
                    getExcelData(arr[1], arr[2]);
                }


               if(beforeStr != afterStr){
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
                            .setContentIntent(pendingIntent); // 사용자가 노티피케이션을 탭시 ResultActivity로 이동하도록 설정
                            //.setAutoCancel(true);
                    if(prefs.getString("backsetlist","").contains("소리")){
                        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
                    }
                   if(prefs.getString("backsetlist","").contains("진동")){
                       builder.setVibrate(new long[]{0, 500, 500, 500});
                   }

                    NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle(builder);
                   // inboxStyle.addLine(afterStr + "의 감염병 현황입니다.");
                   inboxStyle.addLine(bestDeseaseName);
                   inboxStyle.addLine(secondDeseaseName);
                   inboxStyle.addLine(thirdDeseaseName);

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
                   //soundset(builder);//소리 띠링
                   //setVibrate(builder); //붕
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
