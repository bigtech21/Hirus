package activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import Interface.ExcelInterface;
import data.GeoVariableData;
import com.kcl.hirus.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import static java.lang.Math.abs;

public class CurMapActivity extends AppCompatActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, ExcelInterface {

    private GoogleMap mMap;

    private Marker currentMarker = null;
    private Marker newMarker = null;
    private FusedLocationProviderClient mFusedLocationClient;

    private static final String TAG = "현재위치";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int FATEST_UPDATE_INTERVAL_MS = 500;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    private long doubleClickTime;
    int arr[] = new int[67];
    int bestDesease;
    int secondDesease;
    int thirdDesease;
    String bestDeseaseName;


    String secondDeseaseName;
    String thirdDeseaseName;
    String[] deseases = new String[67];

    public static double curlatitude = 0;
    public static double curlongitude = 0;

    Workbook wb;
    InputStream is;

    String[] REQUIRED_PERMISSIONS  = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    Location mCurrentLocatiion;
    LatLng currentPosition;
    LatLng selectedPosition;

    private View mLayout;
    private Location location;
    GeoVariableData geoVariable;

    LocationRequest locationRequest;

    @Override
    public void arrinit() {
        for (int i = 0; i < arr.length; i++) { //초기화
            arr[i] = 0;
        }
        for (int i = 0; i < deseases.length; i++) { //초기화
            deseases[i] = "";
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geoVariable = new GeoVariableData();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_cur_map);

        mLayout = findViewById(R.id.layout_main);

        try {
            is = getApplicationContext().getResources().getAssets().open("database.xls");
            wb = Workbook.getWorkbook(is);
        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }


        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                //.setInterval(UPDATE_INTERVAL_MS)
                //.setFastestInterval(FATEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


       SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;
        setDefaultLocation();

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {

            startLocationUpdates();

        }else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions( CurMapActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();


            } else {
                ActivityCompat.requestPermissions( this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(getApplicationContext());
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(getApplicationContext());
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(getApplicationContext());
                snippet.setTextColor(Color.BLACK);
                snippet.setGravity(Gravity.LEFT);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {

               String str =  getCurrentAddress(latLng);
               String address[] = str.split(" ");
               getExcelData(address[1], address[2]);

                String markerTitle = (address[1]+" " + address[2] + "의 감염병 현황\n");
                String markerSnippet = bestDeseaseName+" : "+bestDesease+"\n"+ secondDeseaseName+" : "
                        +secondDesease+"\n"+ thirdDeseaseName+" : "+thirdDesease+"";

                addNewMarker(latLng, markerTitle, markerSnippet);
                Log.d( TAG, "onMapClick :");
            }
        });
    }

    @Override
    public  void getExcelData(String addr, String sii) {
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

                        if(si.contains(contents)){//구를 찾을 경우
                            addressPosition = i;
                            break;
                        }
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

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

                curlatitude = location.getLatitude();
                curlongitude = location.getLongitude();


                String markerTitle = getCurrentAddress(currentPosition);
                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                        + " 경도:" + String.valueOf(location.getLongitude());

                Log.d(TAG, "onLocationResult : " + markerSnippet);

                GeoVariableData.setAddress(markerTitle);
                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocatiion = location;
            }


        }

    };



    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);



            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }




    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }


    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        mMap.moveCamera(cameraUpdate);

    }

    public void addNewMarker(LatLng latLng, String markerTitle, String markerSnippet) {


        if (newMarker != null) newMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);


        newMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(cameraUpdate);

    }


    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";


        if (currentMarker != null) currentMarker.remove();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mMap.moveCamera(cameraUpdate);

    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);



        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED   ) {
            return true;
        }

        return false;

    }



    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
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

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {


                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                }else {


                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
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


    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CurMapActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case GPS_ENABLE_REQUEST_CODE:

                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {

                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");


                        needRequest = true;

                        return;
                    }
                }

                break;
        }
    }


}