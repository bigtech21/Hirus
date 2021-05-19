package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import Interface.GeoInterface;
import data.GeoVariableData;
import receiver.BootReceiver;
import service.BackgroundService;
import service.GpsTrackerService;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.snackbar.Snackbar;
import com.kcl.hirus.R;
import thread.SubThread;

import java.io.IOException;
import java.util.List;

public class LodingActivity extends AppCompatActivity implements GeoInterface , ActivityCompat.OnRequestPermissionsResultCallback{
    ImageView lodingImage;
    BitmapDrawable bitmap;
    Thread t;

    GpsTrackerService gpsTracker;
    Geocoder geocoder;
    Double latitude, longitude;
    SharedPreferences prefs;
    private BackgroundService backgroundService;
    private FusedLocationProviderClient mFusedLocationClient;
    LocationRequest locationRequest;
    public static String addstr = null;

     String str_Title = null;
     String addressArr = null;
     String do_;
     String si;

    boolean needRequest = false;

    private static final int PERMISSIONS_REQUEST_CODE = 100;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.INTERNET,Manifest.permission.ACCESS_BACKGROUND_LOCATION};

    private  boolean checkPermissions() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int hasInternetPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET);
        int hasBackgroundPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED&&
                hasInternetPermission == PackageManager.PERMISSION_GRANTED&&
                hasBackgroundPermission == PackageManager.PERMISSION_GRANTED
        ) {
            return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                startLocationUpdates();
                try {
                    if (prefs.getString("backsetlist", "") != null && (prefs.getString("backsetlist", "").equals("사용 안함"))) {

                    } else {
                        if (!BootReceiver.isServiceRunning(getApplicationContext(), backgroundService.getClass())) {
                            startService();
                        }
                    }
                }catch (NullPointerException npe){
                    startService();
                }
                initLocation();
                startLoding();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])
                 || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[3])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(findViewById(R.id.layout), "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(findViewById(R.id.lodingLayout), "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();
                }
            }

        }
    }
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
        }

    };

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);
            int hasBackgroundPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED||
                    hasBackgroundPermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }



            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

        }

    }

    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(LodingActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loding);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        backgroundService = new BackgroundService();
        t = new SubThread(getApplicationContext(),backgroundService);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if(checkPermissions()){


            if (!(prefs.getString("backsetlist", "").equals("사용 안함"))) {
                if(!BootReceiver.isServiceRunning(getApplicationContext(),backgroundService.getClass())) {
                    startService();
                }
            }
            initLocation();
            startLoding();
        }
        else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(findViewById(R.id.lodingLayout), "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions( LodingActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }
        }

        lodingImage = findViewById(R.id.Loding);

        Resources res = getResources();
        bitmap = (BitmapDrawable)res.getDrawable(R.drawable.um);
        int bitmapWidth = bitmap.getIntrinsicWidth();
        int bitmapHeight = bitmap.getIntrinsicHeight();

        lodingImage.setImageDrawable(bitmap);
        lodingImage.getLayoutParams().width = bitmapWidth;
        lodingImage.getLayoutParams().height = bitmapHeight;

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

    private void initLocation() {
        gpsTracker = new GpsTrackerService(this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        geocoder = new Geocoder(this);
        reverseCoding();
    }

    @Override
    public void reverseCoding() {
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

        } catch (IOException e) {
            reverseCoding();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                str_Title = "해당되는 주소 정보는 없습니다";
            } else {
                String cut[] = list.get(0).toString().split(" ");
                str_Title = cut[1] + " " + cut[2] + " " + cut[3];
                addressArr = cut[1] + " " + cut[2] + " " + cut[3];
                addstr = addressArr;
               do_ = cut[2];
               si = cut[3];
            }
        }
    }

}