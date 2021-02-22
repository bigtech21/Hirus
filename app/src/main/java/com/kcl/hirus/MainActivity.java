package com.kcl.hirus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

import static com.kcl.hirus.GeoVariable.latitude;
import static com.kcl.hirus.GeoVariable.longitube;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawLayout;
    private Context context = this;
    inf_search is = new inf_search();
    hotissue hi = new hotissue();
    Minigame mg = new Minigame();
    WorldMap wm = new WorldMap();
    Setting st = new Setting();
    Etc etc = new Etc();
    AboutInfection ai = new AboutInfection();
    private long pressedTime = 0;
    private GpsTracker gpsTracker;
    private final static int GPS_ENABLE_REQUEST_CODE = 3001;
    private final static int PERMISSION_REQURST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
    Double latitude, longitude;
    Location location;;
    String[] deseases = new String[67];
    int arr[] = new int[67];
    int bestDesease;
    int secondDesease;
    int thirdDesease;
    String bestDeseaseName;
    String secondDeseaseName;
    String thirdDeseaseName;
    String do_ = null;
    String si = null;
    TextView human1;
    TextView human2;
    TextView human3;




    Geocoder geocoder;
    public static TextView toolbar_title;
    public static String addressArr = null;


    public interface OnBackpressedListener {
        public void onBack();
    }

    private OnBackpressedListener mBackListener;

    public void setOnBackPressedListener(OnBackpressedListener listener){
        mBackListener = listener;
    }

    @Override
    public void onBackPressed(){
        if(mBackListener != null){
            mBackListener.onBack();
            Log.e("!!!","Listener is not null");
        }else{
            Log.e("!!!","Listener is null");

            if(pressedTime == 0){
                Toast.makeText(this,"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                pressedTime = System.currentTimeMillis();
            }else{
                int second = (int)(System.currentTimeMillis() - pressedTime);

                if(second > 2000){
                    Toast.makeText(this,"한번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
                    pressedTime = 0;
                }else{
                    super.onBackPressed();
                    Log.e("!!!","OnBackPressed:Finished, killProcess");
                    finish();
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
        }
    }

    public void getExcelData(String addr, String si) {
        try {
            String str_do;
            //배열 초기화
            for(int i = 0; i < deseases.length ; i++){
                deseases[i] = "";
            }
            if(addr.length() > 3) { //충청북도 ~~

                str_do = addr.substring(0,4);
            }
            else { //강원도 ~~ 서울 ~~
                str_do = addr.substring(0, 2);
            }
            String str_si = si.substring(0,2);

            InputStream is = getBaseContext().getResources().getAssets().open("database.xls");
            Workbook wb = Workbook.getWorkbook(is);
            int key, k, l;
            int addressPosition = 0;
            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int rowTotal = sheet.getRows();
                    int colTotal = sheet.getColumns(); // 전체 컬럼

                    for(int i = 1; i < rowTotal; i++){
                        String contents = sheet.getCell(0, i).getContents();
                        Log.d(str_do+" "+str_si,contents);
                        if(str_do.equals(contents) || str_si.equals(contents)){//도 혹은 시/군을 찾을 경우
                            addressPosition = i;
                            break;
                        }
                    }

                    for(int j = 1; j < colTotal; j++){
                        Cell iCnt = sheet.getCell(j, addressPosition); //감염병 환자 수
                        Cell DName = sheet.getCell(j, 0); //감염병 이름
                        arr[j-1] = Integer.parseInt(iCnt.getContents());
                        deseases[j-1] = DName.getContents();
                        Log.d("de",deseases[j-1]);
                    }

                    for(k = 1; k< arr.length; k++) { //삽입 정렬
                        key = arr[k];
                        for(l = k-1; l>=0 && arr[l]>key; l--) {
                            arr[l + 1] = arr[l];
                        }
                        arr[l+1] = key;
                        Log.d("test","되니?");
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* //백그라운드 서비스 실행
        Intent serviceIntent = new Intent(getApplicationContext(),BackgroundService.class);
        startService(serviceIntent);*/


        gpsTracker = new GpsTracker(this);

        human1 = (TextView) findViewById(R.id.BestDesease);
        human2 = (TextView) findViewById(R.id.Human2);
        human3 = (TextView) findViewById(R.id.Human3);



        int permissionCheck1 = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        if(permissionCheck1 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET},1);

        int permissionCheck2 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permissionCheck2 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},1);

        int permissionCheck3 = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck3 == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1);




        mDrawLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                item.setChecked(true);
                mDrawLayout.closeDrawers();

                int id = item.getItemId();
                String title = item.getTitle().toString();

                if (id == R.id.inf_search) {
                    Toast.makeText(context, title + ":질병 정보 검색", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);

                } else if (id == R.id.hotissue) {
                    Toast.makeText(context, title + ":핫 이슈", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);

                } else if (id == R.id.minigame) {
                    Toast.makeText(context, title + ":미니게임", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);

                } else if (id == R.id.map) {
                    Toast.makeText(context, title + ":해외 현황", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);

                } else if (id == R.id.setting) {
                    Toast.makeText(context, title + ":환경설정", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);

                } else if (id == R.id.etc) {
                    Toast.makeText(context, title + ":기타사항 문의", Toast.LENGTH_SHORT).show();
                    TextView titles =findViewById(R.id.toolbar_title);
                    titles.setText(title);
                    onFragmentChanged(id);
                }

                return true;
            }
        });


        final Bundle bundle = new Bundle();
        final Intent intent = getIntent();

        human1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String text = human1.getText()+"";
                Log.d("text",text);

                TextView titles =findViewById(R.id.toolbar_title);
                titles.setText(human1.getText() + "현황");

                bundle.putString("text" , text);
                ai.setArguments(bundle);

                onFragmentChanged(R.id.AboutInfection_fr);
            }
        });

        human2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = human2.getText()+"";

                TextView titles =findViewById(R.id.toolbar_title);
                titles.setText(human2.getText() + "현황");

                bundle.putString("text" , text);
                ai.setArguments(bundle);

                onFragmentChanged(R.id.AboutInfection_fr);
            }
        });

        human3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = human3.getText()+"";

                TextView titles =findViewById(R.id.toolbar_title);
                titles.setText(human3.getText() + "현황");

                bundle.putString("text" , text);
                ai.setArguments(bundle);

                onFragmentChanged(R.id.AboutInfection_fr);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        latitude = gpsTracker.getLatitude(); // 위도 경도 클래스변수에서 가져옴
        Log.d("!!!!","위도 : "+latitude);

        longitude = gpsTracker.getLongitude();
        Log.d("!!!!","경도 : "+longitude);
        toolbar_title= (TextView)findViewById(R.id.toolbar_title);
        geocoder = new Geocoder(this);  // 역지오코딩 하기 위해
        reverseCoding();

        getExcelData(do_, si);
        human1.setText(bestDeseaseName);
        human2.setText(secondDeseaseName);
        human3.setText(thirdDeseaseName);

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                //값은 Location 형태로 리턴되며 좌표 출력 방법은 다음과 같다.

                Log.d("test", "onLocationChanged, location:" + location);


            }

            public void onProviderDisabled(String provider) {
                // Disabled시
                Log.d("test", "onProviderDisabled, provider:" + provider);
            }

            public void onProviderEnabled(String provider) {
                // Enabled시
                Log.d("test", "onProviderEnabled, provider:" + provider);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                // 변경시
                Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
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

        final androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
    }

    public void reverseCoding(){ // 위도 경도 넣어가지구 역지오코딩 주소값 뽑아낸다
       List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

       } catch (IOException e) {
           e.printStackTrace();
         Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
                toolbar_title.setText("해당되는 주소 정보는 없습니다");
            } else {
                // onWhere.setText(list.get(0).toString()); 원래 통으로 나오는 주소값 문자열

                // 문자열을 자르자!
                String cut[] = list.get(0).toString().split(" ");
                for(int i=0; i<cut.length; i++){
                    System.out.println("cut["+i+"] : " + cut[i]);
                } // cut[0] : Address[addressLines=[0:"대한민국
                // cut[1] : 서울특별시  cut[2] : 송파구  cut[3] : 오금동
                // cut[4] : cut[4] : 41-26"],feature=41-26,admin=null ~~~~
                toolbar_title.setText(cut[1] + " " + cut[2] + " " + cut[3]); // 내가 원하는 구의 값을 뽑아내 출력
                addressArr = cut[1] + " " + cut[2] + " " + cut[3];
                do_ = cut[1];
                si = cut[2];
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void onFragmentChanged(int id){
        if(id == R.id.inf_search){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, is).commit();
        }
        else if (id == R.id.hotissue){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, hi).commit();
        }
        else if( id == R.id.minigame){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, mg).commit();
        }
        else if(id == R.id.map) {
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, wm).commit();
        }
        else if(id == R.id.setting){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, st).commit();
        }
        else if(id == R.id.etc){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, etc).commit();
        }
        else if(id == R.id.AboutInfection_fr){
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.container, ai).commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home:{
                mDrawLayout.openDrawer(GravityCompat.START);
                break;
            }

            case R.id.map_open: {

                Intent intent = new Intent(getApplicationContext(),CurMap.class);
                startActivityForResult(intent, 1001);
                break;
            }

        }
        return super. onOptionsItemSelected(item);
    }
}