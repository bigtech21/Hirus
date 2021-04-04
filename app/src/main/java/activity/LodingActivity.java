package activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;

import receiver.BootReceiver;
import service.BackgroundService;
import service.GpsTracker;
import com.kcl.hirus.R;
import thread.SubThread;

import java.io.IOException;
import java.util.List;

public class LodingActivity extends AppCompatActivity {
    ImageView iv;
    BitmapDrawable bitmap;
    Thread t;

    GpsTracker gpsTracker;
    Geocoder geocoder;
    Double latitude, longitude;
    SharedPreferences prefs;
    private BackgroundService backgroundService;

     String str_Title = null;
     String addressArr = null;
     String do_;
     String si;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        backgroundService = new BackgroundService();
        t = new SubThread(getApplicationContext(),backgroundService);
        ExcelInit();
        checkPermissions();
        //Log.d("ddd",prefs.getString("backsetlist",""));
        if ((prefs.getString("backsetlist", "").equals("사용 안함"))) {

        } else {
            if(!BootReceiver.isServiceRunning(getApplicationContext(),backgroundService.getClass())) {
                startService();
            }
        }

        initLocation();
        startLoding();

        iv = findViewById(R.id.Loding);

        Resources res = getResources();
        bitmap = (BitmapDrawable)res.getDrawable(R.drawable.um);
        int bitmapWidth = bitmap.getIntrinsicWidth();
        int bitmapHeight = bitmap.getIntrinsicHeight();

        iv.setImageDrawable(bitmap);
        iv.getLayoutParams().width = bitmapWidth;
        iv.getLayoutParams().height = bitmapHeight;

    }

    private void startLoding() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("title",str_Title);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("addressArr",addressArr);
                intent.putExtra("do_",do_);
                intent.putExtra("si",si);

                startActivity(intent);
                Log.d("load","메인화면");
                finish();
            }
        }, 1000);
    }

    private void startService() {
        //백그라운드 서비스 실행
        t.start();
    }

    private void ExcelInit() {
        //excel = new Excel(getBaseContext());
    }

    private void initLocation() {
        gpsTracker = new GpsTracker(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        geocoder = new Geocoder(this);
        reverseCoding();
    }

    public void reverseCoding() {
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                str_Title = "해당되는 주소 정보는 없습니다";
            } else {
                String cut[] = list.get(0).toString().split(" ");
                str_Title = cut[1] + " " + cut[2] + " " + cut[3];
                addressArr = cut[1] + " " + cut[2] + " " + cut[3];
               do_ = cut[2];
               si = cut[3];
            }
        }
    }


    private  void checkPermissions() {
        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if(permissionCheck1 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},1);

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck2 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},1);

        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck3 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);
    }
}