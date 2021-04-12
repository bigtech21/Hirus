package activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.graphics.Color;
import android.location.Geocoder;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import fragment.AboutInfectionFragment;
import fragment.MinigameFragment;
import com.kcl.hirus.R;

import fragment.WorldmapFragment;
import fragment.HotissueFragment;
import fragment.SearchFragment;
import fragment.FakeFragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import pl.polidea.view.ZoomView;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    SearchFragment is = new SearchFragment();
    HotissueFragment hi = new HotissueFragment();
    MinigameFragment mg = new MinigameFragment();
    WorldmapFragment wm = new WorldmapFragment();
    AboutInfectionFragment ai = new AboutInfectionFragment();
    FakeFragment non = new FakeFragment();

    public TextView toolbar_title;
    TextView human1,human2, human3;
    TextView cur;
    TextView pcnt;

    ImageView face;

    private long pressedTime = 0;
    Double latitude, longitude;

    String do_ = null;
    String si = null;
    String bestDeseaseName, secondDeseaseName, thirdDeseaseName;
    public String addressArr = null;
    public static String addressstr = null;
    public String mainColor = null;
    String[] deseases = new String[67];

    public boolean fragmentOn = false;
    public TabLayout tabLayout;
    Geocoder geocoder;

    androidx.appcompat.widget.Toolbar toolbar;
    Bundle bundle;
    InputStream ist;
    Workbook wb;
    ActionBar actionBar;

    int bestDesease, secondDesease, thirdDesease;
    int patientcnt, bestcnt;
    int arr[] = new int[67];


    public interface OnBackpressedListener {
        void onBack();
    }

    private OnBackpressedListener mBackListener;

    public void setOnBackPressedListener(OnBackpressedListener listener){
        mBackListener = listener;
    }

    @Override
    public void onBackPressed(){
        if(mBackListener != null){
            mBackListener.onBack();
        }else{

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
                }
            }
        }
    }

    public  void arrinit() {
        for(int i = 0; i < arr.length; i++){ //초기화
            arr[i] = 0;
        }
        for(int i = 0; i < deseases.length; i++){ //초기화
            deseases[i] = "";
        }
    }

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

                        if(addr.contains(contents) || si.contains(contents)){//구를 찾을 경우
                            addressPosition = i;
                            Log.d("Main",contents);
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

    public  int selectDesease(String desease) { //AboutInfection 클래스에서 사용
        try {
            if(wb != null){

                Sheet sheet = wb.getSheet(0);

                if(sheet != null){
                    int colTotal = sheet.getColumns();    // 전체 컬럼

                    for(int i = 1; i < colTotal; i++){
                        String contents = sheet.getCell(i, 0).getContents();
                        if(desease.equals(contents)){

                            return i;
                        }
                    }
                }
            }
        }catch (Exception e){}
        return 0;
    }

    public void copyExcelDataToDatabase(TextView address, String desease) {

        String addresses = null;
        String newaddress[] = null;
        int positionD = selectDesease(desease);
        patientcnt = 0;

        try {

            addresses = address.getText().toString();//현주소
            newaddress = addresses.split(" ");//현주소자른, 0 = ~시, 1 = ~구,동
            String str1 = newaddress[0].replaceAll("\\P{Print}","");
            String str2 = newaddress[1].replaceAll("\\P{Print}","");


            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기
                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 0;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    for(int row=rowIndexStart;row<rowTotal;row++) {
                            String contents = sheet.getCell(0, row).getContents().replaceAll("\\P{Print}","");
                            if(contents.contains(str2)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                patientcnt = Integer.parseInt(iCnt.getContents());
                            }

                            else if(contents.contains(str1)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                patientcnt = Integer.parseInt(iCnt.getContents());
                            }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setColor(TextView tv) {
        String str = tv.getText().toString();
        copyExcelDataToDatabase(toolbar_title,str);

        int cnt = patientcnt;
        if(cnt >=1 && cnt <10){
            tv.setBackgroundColor(Color.parseColor("#800099ff"));
            tv.setTextColor(Color.parseColor("#000000"));}
        else if(cnt >=10 && cnt <50){
            tv.setBackgroundColor(Color.parseColor("#80FFff33"));
            tv.setTextColor(Color.parseColor("#000000"));
        }
        else if(cnt >=50 && cnt <100)tv.setBackgroundColor(Color.parseColor("#80FF9933"));
        else if(cnt >=100)tv.setBackgroundColor(Color.parseColor("#80ff0000"));
        else tv.setBackgroundColor(Color.parseColor("#8099FF99"));
    }

    public void setMainColor(TextView cur,TextView lo, TextView tv,  androidx.appcompat.widget.Toolbar toolbar, ImageView iv, TextView pcnt){
        String str = tv.getText().toString();
        copyExcelDataToDatabase(toolbar_title,str);
        bestcnt = patientcnt;
        pcnt.setText("현재 감염자 수 : " + bestcnt);
        if(bestcnt >=1 && bestcnt <10){
            tv.setBackgroundColor(Color.parseColor("#800099ff"));
            cur.setBackgroundColor(Color.parseColor("#800099ff"));
            lo.setBackgroundColor(Color.parseColor("#800099ff"));
            toolbar.setBackgroundColor(Color.parseColor("#800099ff"));
            tv.setTextColor(Color.parseColor("#000000"));
            mainColor = "800099ff";
            iv.setImageResource(R.drawable.smile);
        }
        else if(bestcnt >=10 && bestcnt <50){
            tv.setBackgroundColor(Color.parseColor("#80FFff33"));
            cur.setBackgroundColor(Color.parseColor("#80FFff33"));
            lo.setBackgroundColor(Color.parseColor("#80FFff33"));
            toolbar.setBackgroundColor(Color.parseColor("#80FFff33"));
            tv.setTextColor(Color.parseColor("#000000"));
            mainColor = "80FFff33";
            iv.setImageResource(R.drawable.nonsmile);
        }
        else if(bestcnt >=50 && bestcnt <100){
            tv.setBackgroundColor(Color.parseColor("#80FF9933"));
            cur.setBackgroundColor(Color.parseColor("#80FF9933"));
            lo.setBackgroundColor(Color.parseColor("#80FF9933"));
            toolbar.setBackgroundColor(Color.parseColor("#80FF9933"));
            mainColor = "80FF9933";
            iv.setImageResource(R.drawable.unsmile);
        }
        else if(bestcnt >=100){
            tv.setBackgroundColor(Color.parseColor("#ff8282"));
            cur.setBackgroundColor(Color.parseColor("#ff8282"));
            lo.setBackgroundColor(Color.parseColor("#ff8282"));
            toolbar.setBackgroundColor(Color.parseColor("#ff8282"));
            mainColor = "#ff8282";
            iv.setImageResource(R.drawable.die);
        }
        else{
            tv.setBackgroundColor(Color.parseColor("#8099FF99"));
            cur.setBackgroundColor(Color.parseColor("#8099FF99"));
            lo.setBackgroundColor(Color.parseColor("#8099FF99"));
            toolbar.setBackgroundColor(Color.parseColor("#8099FF99"));
            mainColor = "8099FF99";
            iv.setImageResource(R.drawable.ssmile);
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        longitude = getIntent().getExtras().getDouble("longitude");
        latitude = getIntent().getExtras().getDouble("latitude");
        do_ = getIntent().getExtras().getString("do_");
        si = getIntent().getExtras().getString("si");
        addressArr = getIntent().getExtras().getString("title");
        face = findViewById(R.id.faces);
        pcnt = findViewById(R.id.patientcnt);
        geocoder = new Geocoder(this);
        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.toolbar);
        toolbar_title= (TextView)findViewById(R.id.toolbar_title);
        cur = findViewById(R.id.current);

        Log.d("dd",addressArr + longitude + latitude + do_ + si);
        //gpsTracker = new GpsTracker(this);

        human1 = (TextView) findViewById(R.id.BestDesease);
        human2 = (TextView) findViewById(R.id.Human2);
        human3 = (TextView) findViewById(R.id.Human3);

        try {
            ist = getApplicationContext().getResources().getAssets().open("database.xls");
            wb = Workbook.getWorkbook(ist);

        }catch (Exception e){}

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.getTabAt(0).setIcon(R.drawable.home_icon);
        tabLayout.getTabAt(1).setIcon(R.drawable.search_icon);
        tabLayout.getTabAt(2).setIcon(R.drawable.issue_icon);
        tabLayout.getTabAt(3).setIcon(R.drawable.game_icon);
        tabLayout.getTabAt(4).setIcon(R.drawable.world_icon);

        bundle = new Bundle();

        human1.setOnClickListener(new MyClickListener());
        human2.setOnClickListener(new MyClickListener());
        human3.setOnClickListener(new MyClickListener());

    }

    public void reverseCoding() {
        List<Address> list = null;
        try {
            Log.e("test", latitude + longitude + "");
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
            } else {
                String cut[] = list.get(0).toString().split(" ");
                addressArr = cut[1] + " " + cut[2] + " " + cut[3];
                do_ = cut[2];
                si = cut[3];
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("!!!!","Resume1");

        toolbar_title.setText(addressArr);

        addressstr = addressArr;

        getExcelData(do_,si);

        human1.setText(bestDeseaseName);

        setMainColor(cur,toolbar_title,human1,toolbar,face,pcnt);

        human2.setText(secondDeseaseName);

        setColor(human2);

        human3.setText(thirdDeseaseName);

        setColor(human3);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            Fragment selected = null;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int id = tab.getPosition();
                Log.d("id", id+"");

                bundle.putString("color" , mainColor);
                int col = 0;

                if(id == 0) {
                    TextView titles =findViewById(R.id.toolbar_title);
                    Log.d("dd",addressArr);
                    titles.setText(addressArr);
                    selected = non;

                    if(bestcnt >=1 && bestcnt <10) col = Color.parseColor("#800099ff");
                    else if (bestcnt >=10 && bestcnt <50) col = Color.parseColor("#80FFff33");
                    else if(bestcnt >=50 && bestcnt <100) col = Color.parseColor("#80FF9933");
                    else if(bestcnt >=100) col = Color.parseColor("#80ff0000");
                    else col = Color.parseColor("#8099FF99");
                    toolbar.setBackgroundColor(col);
                }
                else if(id == 1) {
                    selected = is;
                    toolbar.setBackgroundColor(Color.parseColor("#dcf0fa"));
                }
                else if(id == 2){
                    selected = hi;
                    toolbar.setBackgroundColor(Color.parseColor("#dcf0fa"));
                }
                else if(id == 3){
                    selected = mg;
                    toolbar.setBackgroundColor(Color.parseColor("#dcf0fa"));
                }
                else if(id == 4){
                    selected = wm;

                    /*View v = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.fragment_world_map,null,false);
                    v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));

                    ZoomView zoomView = new ZoomView(getBaseContext());
                    zoomView.addView(v);
                    zoomView.setMaxZoom(3f);
                    ConstraintLayout container = findViewById(R.id.layout);
                    container.addView(zoomView);*/

                    toolbar.setBackgroundColor(Color.parseColor("#dcf0fa"));

                }
                if(selected != null)
                getSupportFragmentManager().beginTransaction().replace(R.id.layout, selected).commit();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if(selected != null)
                getSupportFragmentManager().beginTransaction().remove(selected).commit();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

       final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                //여기서 위치값이 갱신되면 이벤트가 발생한다.
                reverseCoding();
                getExcelData(do_, si);
                toolbar_title.setText(addressArr);
                human1.setText(bestDeseaseName);
                human2.setText(secondDeseaseName);
                human3.setText(thirdDeseaseName);
                Log.d("Main", "onLocationChanged, location:" + location + "address : "+do_ + " " + si);
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

        setSupportActionBar(toolbar);
         actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    public void onFragmentChanged(int id){
        Fragment lastFragment = non;
        if(id == 0){
            //toolbar.setBackgroundColor(human1);
            getSupportFragmentManager().beginTransaction().replace(R.id.layout, non).commit();
        }
        else if(id ==R.id.inf_search_fr){
            lastFragment = is;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout, is).commit();
        }
        else if (id == 2){
            lastFragment = hi;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout, hi).commit();
        }
        else if( id == 3){
            lastFragment = mg;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout, mg).commit();
        }
        else if(id == 4) {
            lastFragment = wm;
            getSupportFragmentManager().beginTransaction().replace(R.id.layout, wm).commit();
        }
        else if(id == R.id.AboutInfection_fr){
            lastFragment = ai;
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).replace(R.id.layout, ai).commit();
            fragmentOn = true;
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.map_open: {
                Intent intent = new Intent(getApplicationContext(), CurMapActivity.class);
                startActivityForResult(intent, 1001);
                break;
            }
            case R.id.menu_setting:{
                Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivityForResult(intent, 1002);
                break;
            }
        }
        return super. onOptionsItemSelected(item);
    }

    class MyClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int id = v.getId();
            String text;
            TextView titles =findViewById(R.id.toolbar_title);
            switch (id){
                case R.id.BestDesease :
                    text = human1.getText()+"";
                    Log.d("text",text);

                    titles.setText(human1.getText() + "현황");

                    bundle.putString("text" , text);
                    ai.setArguments(bundle);

                    onFragmentChanged(R.id.AboutInfection_fr);
                    break;
                case R.id.Human2 :
                    text = human2.getText()+"";
                    Log.d("text",text);

                    titles.setText(human2.getText() + "현황");

                    bundle.putString("text" , text);
                    ai.setArguments(bundle);

                    onFragmentChanged(R.id.AboutInfection_fr);
                    break;
                case R.id.Human3 :
                    text = human3.getText()+"";
                    Log.d("text",text);

                    titles.setText(human3.getText() + "현황");

                    bundle.putString("text" , text);
                    ai.setArguments(bundle);

                    onFragmentChanged(R.id.AboutInfection_fr);
                    break;

            }
        }
    }
}