package com.kcl.hirus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.telephony.CarrierConfigManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.CharArrayWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

public class AboutInfection extends Fragment implements MainActivity.OnBackpressedListener {

    private DB db;
    private static final String TAG = "DB";
    Geocoder geocoder;
    GpsTracker gpsTracker;
    Double latitude, longitude;
    String addressArr;
    TextView address;
    TextView City;
    String desease;

    int EVOLA = 1;
    int MAVERG = 2;
    int LASSA = 3;
    int CCHF = 4;
    int AHF = 5;
    int RVF = 6;
    int VV = 7;
    int PEST = 8;
    int ANTHRAX = 9;
    int BOTULISM = 10;
    int TULAREMIA = 11;
    int ND = 12;
    int SARS = 13;
    int MERS = 14;
    int PAI = 15;
    int H1N1 = 16;
    int DPT = 17;
    int CKP = 18;
    int MSL = 19; //홍역
    int CHOLERA = 20;
    int TYPHOID = 21;
    int PTF = 22;
    int SGLS = 23;
    int EECI = 24;
    int HEPATITIS_A = 25;
    int WC = 26;
    int EP = 27;
    int RUBELLA = 28;
    int C_RUBELLA = 29;
    int A_RUBELLA = 30;
    int POLIO = 31;
    int NM = 32;
    int HI_B = 33;
    int PNEUMOCOCCOSIS = 34;
    int LEPROSY = 35;
    int SCARLET = 36;
    int VRSA = 37;
    int CRE = 38;
    int HEPATITIS_E = 39;
    int TETANUS = 40;
    int HEPATITIS_B = 41;
    int JE = 42;
    int HEPATITIS_C = 43;
    int MALARIA = 44;
    int LEGIONELLOSIS = 45;
    int VIBRIO = 46;
    int TF = 47;
    int R_TYPHI = 48;
    int TST = 49;
    int LEPTOSPIROSIS = 50;
    int BRUCELLOSIS = 51;
    int rabies = 52;
    int HFWRS = 53;
    int CJD = 54;
    int YF = 55;
    int DF = 56;
    int QF = 57;
    int WNF = 58;
    int LD = 59;
    int TBE = 60;
    int MELIOIDOSIS = 61;
    int CV = 62;
    int SFTS = 63;
    int ZV = 64;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_about_infection, container, false);

        gpsTracker = new GpsTracker(getContext());
        geocoder = new Geocoder(getContext());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();

        Bundle bundle = getArguments();
        desease = bundle.getString("text");


        address = rootView.findViewById(R.id.Infested);
        reverseCoding();
        address.setText(addressArr);

        Log.d(TAG,"DatabaseTest :: onCreate()");
        this.db = new DB(getContext());
        City = rootView.findViewById(R.id.city);

        copyExcelDataToDatabase();

        return rootView;
    }



    public void reverseCoding(){
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(latitude, longitude, 10); // 위도, 경도, 얻어올 값의 개수

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test", "입출력 오류 - 서버에서 주소변환시 에러발생");
        }
        if (list != null) {
            if (list.size()==0) {
               address.setText("주소를 알 수 없습니다.");
            } else {

                String cut[] = list.get(0).toString().split(" ");
                for(int i=0; i<cut.length; i++){
                    System.out.println("cut["+i+"] : " + cut[i]);
                }
                addressArr = cut[1] + "의 "+ desease+" 현황";
            }
        }
    }

    private int selectDesease() {
        try {
            InputStream is = getContext().getResources().getAssets().open("database.xls");
            Workbook wb = Workbook.getWorkbook(is);

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

    private void copyExcelDataToDatabase() {

        String addresses = null;
        String dataAddress = null;
        String newaddress = null;
        int positionD = selectDesease();
        try {
            InputStream is = getContext().getResources().getAssets().open("database.xls");
            Workbook wb = Workbook.getWorkbook(is);

            Log.d("주소", address.getText().toString());
            addresses = address.getText().toString();//현주소
            newaddress = addresses.substring(0, 2);//현주소자른

            if(wb != null) {
                Sheet sheet = wb.getSheet(0);   // 시트 불러오기

                if(sheet != null) {
                    int colTotal = sheet.getColumns();    // 전체 컬럼
                    int rowIndexStart = 0;                  // row 인덱스 시작
                    int rowTotal = sheet.getColumn(colTotal-1).length;

                    StringBuilder sb;
                    for(int row=rowIndexStart;row<rowTotal;row++) {
                        sb = new StringBuilder();
                        for(int col=0;col<colTotal;col++) {
                            String contents = sheet.getCell(col, row).getContents();
                            sb.append("col"+col+" : "+contents+" , ");
                            Log.d("newaddress", newaddress);

                            if(newaddress.equals(contents)){
                                Cell iCnt = sheet.getCell(positionD, row); //감염병 환자 수
                                Cell iName = sheet.getCell(positionD, 0); //감염병 이름

                                String result = "현재 감염된 "+iName.getContents() +"환자 수"+ " : "+iCnt.getContents() + "명";

                                String infName = iName.getContents();
                                City.setText(result);
                                break;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onBack() {
        Log.e("etc","onBack()");
        MainActivity activity = (MainActivity)getActivity();
        activity.setOnBackPressedListener(null);

        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).remove(this).commit();
        activity.fragmentOn = false;
        activity.toolbar_title.setText(activity.addressArr);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.e("etc","onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}

