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

        City.setText(Excel.copyExcelDataToDatabase(address, desease));

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

