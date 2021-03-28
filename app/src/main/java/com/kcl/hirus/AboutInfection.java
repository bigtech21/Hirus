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
import java.util.HashMap;
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
    HashMap<String, String> map;
    private final int URL_KDCA_FLAG = 1;

    public void initMap() {
        map = new HashMap<String, String>();

        map.put("에볼라바이러스병","NA0001&icdgrpCd=01");map.put("마버그열","NA0002&icdgrpCd=01");map.put("라싸열","NA0003&icdgrpCd=01");
        map.put("크리미안콩고출혈열","NA0004&icdgrpCd=01");map.put("남아메리카출혈열","NA0005&icdgrpCd=01");map.put("리프트밸리열","NA0006&icdgrpCd=01");
        map.put("두창","NA0007&icdgrpCd=01");map.put("패스트","NA0008&icdgrpCd=01");map.put("탄저","NA0009&icdgrpCd=01");
        map.put("보툴리눔독소증","NA0010&icdgrpCd=01");map.put("야토병","NA0011&icdgrpCd=01");map.put("신종감염병증후군","NA0012&icdgrpCd=01");
        map.put("중증급성호흡기증후군(SARS)","NA0013&icdgrpCd=01");map.put("중동호흡기증후군(MERS)","NA0014&icdgrpCd=01");map.put("동물인플루엔자 인체감염증","NA0015&icdgrpCd=01");
        map.put("신종인플루엔자","NA0016&icdgrpCd=01");map.put("디프테리아","NA0017&icdgrpCd=01"); //1급증후군

        map.put("결핵","NB0001&icdgrpCd=02");map.put("수두","NB0002&icdgrpCd=02");map.put("홍역","NB0003&icdgrpCd=02");
        map.put("콜레라","NB0004&icdgrpCd=02");map.put("장티푸스","NB0005&icdgrpCd=02");map.put("파라티푸스","NB0006&icdgrpCd=02");
        map.put("세균성이질","NB0007&icdgrpCd=02");map.put("장출혈성대장균감염증","NB0008&icdgrpCd=02");map.put("A형간염","NB0009&icdgrpCd=02");
        map.put("백일해","NB0010&icdgrpCd=02");map.put("유행성이하선염","NB0011&icdgrpCd=02");map.put("폴리오","NB0013&icdgrpCd=02");
        map.put("수막구균 감염증","NB0014&icdgrpCd=02");map.put("b형헤모필루스인플루엔자","NB0015&icdgrpCd=02");map.put("페렴구균 감염증","NB0016&icdgrpCd=02");
        map.put("한센병","NB0017&icdgrpCd=02");map.put("성홍열","NB0018&icdgrpCd=02");map.put("반코마이신내성황색포도알균(VRSA) 감염증","NB0019&icdgrpCd=02");
        map.put("카바페넴내성장내세균속균종(CRE) 감염증","NB0020&icdgrpCd=02");map.put("E형간염","NB0021&icdgrpCd=02"); //2급

        map.put("파상풍","NC0001&icdgrpCd=03");map.put("B형간염","NC0002&icdgrpCd=03");map.put("일본뇌염","NC0003&icdgrpCd=03");
        map.put("C형간염","NC0004&icdgrpCd=03");map.put("말라리아","NC0005&icdgrpCd=03");map.put("레지오넬라증","NC0006&icdgrpCd=03");
        map.put("비브리오패혈증","NC0007&icdgrpCd=03");map.put("발진티푸스","NC0008&icdgrpCd=03");map.put("발진열","NC0009&icdgrpCd=03");
        map.put("쯔쯔가무시증","NC0010&icdgrpCd=03");map.put("렙토스피라증","NC0011&icdgrpCd=03");map.put("브루셀라증","NC0012&icdgrpCd=03");
        map.put("공수병","NC0013&icdgrpCd=03");map.put("신증후군출혈열","NC0014&icdgrpCd=03");map.put("후천성면역결핍증(AIDS)","NC0015&icdgrpCd=03");
        map.put("크로이츠펠트-야콥병(CJD) 및 변종크로이츠펠트-야콥병(vCJD)","NC0016&icdgrpCd=03");map.put("황열","NC0017&icdgrpCd=03");map.put("뎅기열","NC0018&icdgrpCd=03");
        map.put("큐열","NC0019&icdgrpCd=03");map.put("웨스트나일열","NC0020&icdgrpCd=03");map.put("라임병","NC0021&icdgrpCd=03");
        map.put("진드기매개뇌염","NC0022&icdgrpCd=03");map.put("유비저","NC0023&icdgrpCd=03");map.put("치쿤구니야열","NC0024&icdgrpCd=03");
        map.put("중증열성혈소판감소증후군(NFTS)","NC0025&icdgrpCd=03");map.put("지카바이러스감염증","NC0026&icdgrpCd=03"); //3급

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_about_infection, container, false);

        gpsTracker = new GpsTracker(getContext());
        geocoder = new Geocoder(getContext());
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        initMap();

        Bundle bundle = getArguments();
        desease = bundle.getString("text");


        address = rootView.findViewById(R.id.Infested);
        reverseCoding();
        address.setText(addressArr);

        Log.d(TAG,"DatabaseTest :: onCreate()");
        this.db = new DB(getContext());
        City = rootView.findViewById(R.id.city);

        City.setText(Excel.copyExcelDataToDatabase(address, desease));
        Log.d("d",address.toString());

        String urlcode = map.get(desease);
        Web web = new Web(URL_KDCA_FLAG);
        web.setUrlCode(urlcode);
        getFragmentManager().beginTransaction().replace(R.id.AIWEB, web).addToBackStack(null).commit();

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
                addressArr = cut[2] +" "+cut[3]+" "+ desease+" 현황";
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

