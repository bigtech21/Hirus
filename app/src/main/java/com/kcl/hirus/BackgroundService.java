package com.kcl.hirus;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class BackgroundService extends Service implements LocationListener {
    private String TAG = "Service";
    private String sPackageName = "com.kcl.hirus";

    private final IBinder mBinder = new LocalBinder();
    int iLoopValue = 0;

    int iThreadInterval = 25000;
    boolean bThreadGo = true;

    LocationManager locationManager;
    String sBestGpsProvider = "";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("service", "oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("service", "start");
        super.onStartCommand(intent, flags, startId);

        bThreadGo = true;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        sBestGpsProvider = LocationManager.GPS_PROVIDER;

        setGpsPosition(); //기기에 가지고 있는 마지막 위치정보로 현재위치를 초기설정
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_NOT_STICKY;
        }
        locationManager.requestLocationUpdates(sBestGpsProvider, 10000, 0, this);
        setGpsPosition(); //기기에 가지고 있는 마지막 위치정보로 현재위치를 초기설정

        new Thread(mRun).start();
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
       /* try {
            Log.d(TAG, "DESTROY");
            bThreadGo = false;

            if (this != null && locationManager != null) {
                locationManager.removeUpdates(this);
            }

            TAG = null;
            locationManager = null;
            mRun = null;
        } catch (Exception e) {

        }*/
        super.onDestroy();
    }

    public void onLocationChanged(Location location) {
        try {
            Log.d(TAG, "위치변경");

            positionSaveProc();
        } catch (Exception e) {
        }
    }


    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }
    }

    public interface ICallback {
        ;
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
    }

    Runnable mRun = new Runnable() {
        public void run() {
            try {
                while (bThreadGo) {
                    Log.i(TAG, ">mRun");

                    iLoopValue++;
                    Thread.sleep(iThreadInterval);
                    if (iLoopValue > 100000)
                        iLoopValue = 0;

                    // 위치를 저장
                    positionSaveProc();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


    public synchronized void positionSaveProc() {
        try {
            Log.i(TAG, ">positionSaveProc : 변경된 위치 저장");

            double dLatitude = 0;
            double dLongitude = 0;
            if (sBestGpsProvider != null && locationManager != null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location lcPosition = locationManager.getLastKnownLocation(sBestGpsProvider);
                if (lcPosition != null) {
                    dLatitude = lcPosition.getLatitude();
                    dLongitude = lcPosition.getLongitude();
                    Log.i(TAG, ">positionSaveProc : lat(" + dLatitude + "), lot(" + dLongitude + ")");
                    if (dLatitude != 0 && dLongitude != 0) {
                        setSharePreferenceFloatValue("dUserContactLatitude", (float) dLatitude);
                        setSharePreferenceFloatValue("dUserContactLongitude", (float) dLongitude);

                        setLocationProvider("GPS");        //값을 가져오니 위성으로 설정
                    } else {
                        setLocationProvider("NETWORK");    //실내이어서 위성으로 못가져올 가능성이 커서 네트워크로 설정
                    }
                } else {
                    Log.i(TAG, ">positionSaveProc : 널이어서 위치값이 없는 경우");
                    setLocationProvider("NETWORK");        //실내이어서 위성으로 못가져올 가능성이 커서 네트워크로 설정
                }
            }
        } catch (Exception e) {
            Log.i(TAG, ">positionSaveProc : " + e.toString());
        }
    }


    public synchronized void setLocationProvider(String parmOption) {
        if (locationManager == null)
            return;
        if (parmOption.equals("NETWORK")) {
            Log.i(TAG, ">setLocationProvider sBestGpsProvider : " + sBestGpsProvider);
            setGpsPosition(); // 기기에 가지고 있는 마지막 위치정보로 현재위치를 초기 설정
            sBestGpsProvider = LocationManager.NETWORK_PROVIDER; // 강제로 네트워크로 지정
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            locationManager.requestLocationUpdates(sBestGpsProvider, 10000, 0, this);// 10초마다체크(1000),0미터,리스너위치
            setGpsPosition(); // 기기에 가지고 있는 마지막 위치정보로 현재위치를 초기 설정
        } else if (parmOption.equals("GPS")) {
            Log.i(TAG, ">setLocationProvider sBestGpsProvider : " + sBestGpsProvider);
            sBestGpsProvider = LocationManager.GPS_PROVIDER; // 강제로 위성으로 지정
            locationManager.requestLocationUpdates(sBestGpsProvider, 10000, 0, this);// 10초마다체크(1000),0미터,리스너위치
        }
    }

    public synchronized void setGpsPosition() {
        try {
            Log.i(TAG, ">setGpsPosition : 위치 셋팅");
            if (locationManager == null)
                return;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Location lcPosition = locationManager.getLastKnownLocation(sBestGpsProvider);
            if (lcPosition != null) {
                Log.i(TAG, ">setGpsPosition : lat(" + lcPosition.getLatitude() + "), lot(" + lcPosition.getLongitude() + ")");
                setSharePreferenceFloatValue("dUserContactLatitude", (float) lcPosition.getLatitude());
                setSharePreferenceFloatValue("dUserContactLongitude",(float) lcPosition.getLongitude());
            } else {
                Log.i(TAG, ">setGpsPosition : 널이어서 위치값이 없는 경우");
            }
        } catch (Exception e) {
            Log.i(TAG, ">setGpsPosition : error : " + e.toString());
        }
    }

    public synchronized void locationChangedProc(Location location) {
        try {
            Log.i(TAG, ">locationChangedProc : 위치가 변경되었을 경우");
            if (location == null) {
                return;
            }
            double lat = location.getLatitude();
            double lon = location.getLongitude();
            if (lat > 1 && lon > 1) {
                setSharePreferenceFloatValue("dUserContactLatitude", (float) lat);
                setSharePreferenceFloatValue("dUserContactLongitude", (float) lon);
                Log.i(TAG, ">locationChangedProc : 위도 : " + lat);
                Log.i(TAG, ">locationChangedProc : 경도 : " + lon);

                // 서비스에서 액티비티로 데이터를 보내는 부분
            } else {
                Log.i(TAG, ">locationChangedProc : gps 오류 : " + lon);
            }
        } catch (Exception e) {
            Log.i(TAG, ">locationChangedProc : " + e.toString());
        }
    }

    public synchronized void setSharePreferenceStringValue(String parmName,
                                                           String parmValue) {
        try {
            SharedPreferences spSvc = getApplicationContext()
                    .getSharedPreferences(sPackageName, MODE_PRIVATE);
            SharedPreferences.Editor ed = spSvc.edit();
            ed.putString(parmName, parmValue);
            ed.commit();
            spSvc = null;
        } catch (Exception e) {
            Log.i(TAG, ">setSharePreferenceStringValue error : " + e.toString());
        }
    }

    public synchronized String getSharePreferenceStringValue(String parmName) {
        try {
            SharedPreferences spSvc = getApplicationContext()
                    .getSharedPreferences(sPackageName, MODE_PRIVATE);
            String sReturn = spSvc.getString(parmName, "");
            spSvc = null;
            return sReturn;
        } catch (Exception e) {
            Log.i(TAG, ">getSharePreferenceStringValue error : " + e.toString());
            return "";
        }
    }

    public synchronized void setSharePreferenceFloatValue(String parmName,
                                                          float parmValue) {
        try {
            SharedPreferences spSvc = getApplicationContext()
                    .getSharedPreferences(sPackageName, MODE_PRIVATE);
            SharedPreferences.Editor ed = spSvc.edit();
            ed.putFloat(parmName, parmValue);
            ed.commit();
            spSvc = null;
        } catch (Exception e) {
            Log.i(TAG, ">setSharePreferenceStringValue error : " + e.toString());
        }
    }

    public synchronized float getSharePreferenceFloatValue(String parmName) {
        try {
            SharedPreferences spSvc = getApplicationContext()
                    .getSharedPreferences(sPackageName, MODE_PRIVATE);
            float sReturn = spSvc.getFloat(parmName, 0);
            spSvc = null;
            return sReturn;
        } catch (Exception e) {
            Log.i(TAG, ">getSharePreferenceStringValue error : " + e.toString());
            return 0;
        }
    }

}
