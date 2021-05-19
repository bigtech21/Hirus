package fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kcl.hirus.R;

import java.io.InputStream;
import java.util.Vector;

import Interface.ExcelInterface;
import activity.MainActivity;
import jxl.Sheet;
import jxl.Workbook;


public class WorldmapFragment extends Fragment implements MainActivity.OnBackpressedListener, ExcelInterface {

    HorizontalScrollView scrollView;
    AlertDialog.Builder a;
    AlertDialog ad[] = new AlertDialog[10];
    ScaleGestureDetector scaleGestureDetector;
    float mScaleFactor = 1.1f;
    float lastScaleFactor = 0f;
    float dx = 0f;
    float dy = 0f;

    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 2.0f;

    int zoomCnt = 0;

    int[] countriesID = {R.id.CA, R.id.US, R.id.GL, R.id.MX, R.id.GT, //~4
            R.id.HD, R.id.ELS, R.id.BLZ, R.id.NCG, R.id.CR, R.id.PNM, R.id.CB, R.id.VZ, R.id.ECD, R.id.PR, //~14
            R.id.BZ, R.id.BV, R.id.PRG, R.id.CL, R.id.AG, R.id.UG, R.id.GA, R.id.SN, R.id.FGA, R.id.FOL, //~24
            R.id.RS, R.id.IS, R.id.FL, R.id.SW, R.id.NW, R.id.KZH, R.id.MONG, R.id.CN, R.id.NK, R.id.KR, //~34
            R.id.ID, R.id.NP, R.id.BT, R.id.BGL, R.id.MY, R.id.TAI, R.id.RAOS, R.id.BIET, R.id.MAL, R.id.IDN, //~44
            R.id.PAP, R.id.AUS, R.id.NWZ, R.id.SOL, R.id.VNT, R.id.NVK, R.id.PIZ, R.id.KRG, R.id.TZK, R.id.UZB, //~54
            R.id.TRK, R.id.IRN, R.id.AFG, R.id.PAQ, R.id.IRK, R.id.SUA, R.id.YEM, R.id.OMAN, R.id.AEU, R.id.SIR, //~64
            R.id.TUR, R.id.GRG, R.id.AZB, R.id.ARM, R.id.YRD, R.id.ISR, R.id.LBN, R.id.IZT, R.id.RIB, R.id.SDN, //~74
            R.id.CHD, R.id.NZR, R.id.AZL, R.id.MRC, R.id.SSHR, R.id. MRT, R.id.MALI, R.id.SNG, R.id.GMB, R.id.BRC, //~84
            R.id.CRTB, R.id.GINI, R.id.GNBS, R.id.SRR, R.id.RAIB, R.id.GANA, R.id.TOGO, R.id.VNG, R.id.NIZ, R.id.CMR, //~94
            R.id.CAR, R.id.SSD, R.id.ETO, R.id.SMR, R.id.KNYA, R.id.UGD, R.id.CNGR, R.id.CNG, R.id.CGN, R.id.GBN, //~104
            R.id.AGL, R.id.ZBA, R.id.TZN, R.id.RWD, R.id.BRD, R.id.MLW, R.id.MZB, R.id.ZBW, R.id.BTW, R.id.NMB, //~114
            R.id.SAR, R.id.RST, R.id.EST, R.id.MDG, R.id.UCR, R.id.VLR, R.id.FLD, R.id.GER, R.id.FRNC, R.id.SPN, //~124
            R.id.ITA, R.id.SWS, R.id.AST, R.id.CHK, R.id.SLV, R.id.HGR, R.id.RMN, R.id.BGR, R.id.GRC, R.id.SRV, //~134
            R.id.SLVN, R.id.CRT, R.id.BSN, R.id.CSB, R.id.MTN, R.id.ABN, R.id.NMK, R.id.RTN, R.id.RTB, R.id.ESTN, //~144
            R.id.LSB, R.id.VGE, R.id.NDL, R.id.DMK, R.id.PRT, R.id.MDV, R.id.ENG, R.id.ISL}; //~152

    int[] contriesCnt = new int[countriesID.length+1];
    ImageView[] countries = new ImageView[countriesID.length+1];
    Vector<Integer> confirmedVector = new Vector<Integer>();
    Vector<String> nameVector = new Vector<String>();
    CountryTouchListener countryTouchListener;
    int dialogcnt = 0;
    int selected = 0; //0 코로나 1 테스트 2 테스트
    String nstr = "";
    boolean touch = false;
    LinearLayout layout;
    Button btn1;
    Button btn2;
    Button btn3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootview = inflater.inflate(R.layout.fragment_world_map,container,false);


        scaleGestureDetector = new ScaleGestureDetector(getContext(),new ScaleListener());

        layout = rootview.findViewById(R.id.worldMap_fr);

        btn1 = rootview.findViewById(R.id.deasease1);
        btn1.setOnClickListener(new BtnListener());

        btn2 = rootview.findViewById(R.id.deasease2);
        btn2.setOnClickListener(new BtnListener());

        btn3 = rootview.findViewById(R.id.deasease3);
        btn3.setOnClickListener(new BtnListener());

        scrollView = rootview.findViewById(R.id.WhitemapView);
        scrollView.setHorizontalScrollBarEnabled(true);
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                scaleGestureDetector.onTouchEvent(event);
                return false;
            }
        });


        countryTouchListener = new CountryTouchListener();

        for(int i = 0; i < countriesID.length; i++){
            contriesCnt[i] = i;
        }

        getData();


        /* 투명도 16진수 접두어
        * 100(%) FF
        * 95 F2
        * 90 E6
        * 85 D9
        * 80 CC
        * 75 BF
        * 70 B3
        * 65 A6
        * 60 99
        * 55 8C
        * 50 80
        * 45 73
        * 40 66
        * 35 59
        * 30 4D
        * 25 40
        * 20 33
        * 15 26
        * 10 1A
        * 5 0D
        * 0 00
        */
        for(int i = 0; i < countriesID.length; i++){
            countries[i] = rootview.findViewById(countriesID[i]);
            countries[i].setClickable(true);
            countries[i].setOnClickListener(countryTouchListener);
            if(confirmedVector.get(i) > 0 && confirmedVector.get(i) <= 100000){ //확진자 1명 이상 10만명 이하
                countries[i].setColorFilter(Color.parseColor("#4DFF0000"),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 30%
            }
            else if(confirmedVector.get(i) > 100000 && confirmedVector.get(i) <= 1000000){ //10만명~100만명
                countries[i].setColorFilter(Color.parseColor("#66FF0000"),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 40%
            }
            else if(confirmedVector.get(i) > 1000000 && confirmedVector.get(i) <= 3000000){ //300만명
                countries[i].setColorFilter(Color.parseColor("#80FF0000"),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 50%
            }
            else if(confirmedVector.get(i) > 3000000 && confirmedVector.get(i) <= 5000000){ //500만명
                countries[i].setColorFilter(Color.parseColor("#99FF0000"),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 60%
            }
            else if(confirmedVector.get(i) > 5000000 && confirmedVector.get(i) <= 10000000){ //1000만명
                countries[i].setColorFilter(Color.parseColor("#B3FF0000"),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 70%
            }
            else if(confirmedVector.get(i) > 10000000){ //1000만명이상
                countries[i].setColorFilter(Color.parseColor("#CCFF0000"),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 80%
            }else{ //정보없음 혹은 확진자 0
                countries[i].setColorFilter(Color.parseColor("#1AFF0000"),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 10%
            }


        }
        getActivity().findViewById(R.id.toolbar).setBackgroundColor(Color.parseColor("#c8f0fa"));
        return rootview;
    }

    @Override
    public void arrinit() {

    }

    @Override
    public void getExcelData(String addr, String addr2) {

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
        try {
            InputStream is = getResources().getAssets().open("corona.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int colTotal = sheet.getColumns();

                    for(int i = 0; i < colTotal; i++){
                        int patients = Integer.parseInt(sheet.getCell(i,selected).getContents());
                        String name = sheet.getCell(i,3).getContents();
                        confirmedVector.add(patients);
                        nameVector.add(name);
                    }

                }
            }

        }catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onBack() {
        MainActivity activity = (MainActivity)getActivity();
        try {
            ((MainActivity) getContext()).setOnBackPressedListener(null);
            activity.tabLayout.getTabAt(0).select();
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
            activity.toolbar_title.setText(activity.addressArr);
        }
        catch(Exception e){
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ((MainActivity)context).setOnBackPressedListener(this);
    }

    class CountryTouchListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

                    switch (v.getId()) {
                        case R.id.CA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[0]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[0]) + "명\n\n");
                            break;
                        case R.id.US:
                            nstr = nstr.concat(nameVector.get(contriesCnt[1]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[1]) + "명\n\n");
                            break;
                        case R.id.GL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[2]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[2]) + "명\n\n");
                            break;
                        case R.id.MX:
                            nstr = nstr.concat(nameVector.get(contriesCnt[3]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[3]) + "명\n\n");
                            break;
                        case R.id.GT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[4]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[4]) + "명\n\n");
                            break;
                        case R.id.HD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[5]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[5]) + "명\n\n");
                            break;
                        case R.id.ELS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[6]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[6]) + "명\n\n");
                            break;
                        case R.id.BLZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[7]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[7]) + "명\n\n");
                            break;
                        case R.id.NCG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[8]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[8]) + "명\n\n");
                            break;
                        case R.id.CR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[9]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[9]) + "명\n\n");
                            break;
                        case R.id.PNM:
                            nstr = nstr.concat(nameVector.get(contriesCnt[10]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[10]) + "명\n\n");
                            break;
                        case R.id.CB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[11]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[11]) + "명\n\n");
                            break;
                        case R.id.VZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[12]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[12]) + "명\n\n");
                            break;
                        case R.id.ECD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[13]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[13]) + "명\n\n");
                            break;
                        case R.id.PR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[14]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[14]) + "명\n\n");
                            break;
                        case R.id.BZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[15]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[15]) + "명\n\n");
                            break;
                        case R.id.BV:
                            nstr = nstr.concat(nameVector.get(contriesCnt[16]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[16]) + "명\n\n");
                            break;
                        case R.id.PRG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[17]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[17]) + "명\n\n");
                            break;
                        case R.id.CL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[18]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[18]) + "명\n\n");
                            break;
                        case R.id.AG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[19]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[19]) + "명\n\n");
                            break;
                        case R.id.UG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[20]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[20]) + "명\n\n");
                            break;
                        case R.id.GA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[21]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[21]) + "명\n\n");
                            break;
                        case R.id.SN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[22]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[22]) + "명\n\n");
                            break;
                        case R.id.FGA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[23]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[23]) + "명\n\n");
                            break;
                        case R.id.FOL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[24]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[24]) + "명\n\n");
                            break;
                        case R.id.RS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[25]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[25]) + "명\n\n");
                            break;
                        case R.id.IS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[26]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[26]) + "명\n\n");
                            break;
                        case R.id.FL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[27]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[27]) + "명\n\n");
                            break;
                        case R.id.SW:
                            nstr = nstr.concat(nameVector.get(contriesCnt[28]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[28]) + "명\n\n");
                            break;
                        case R.id.NW:
                            nstr = nstr.concat(nameVector.get(contriesCnt[29]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[29]) + "명\n\n");
                            break;
                        case R.id.KZH:
                            nstr = nstr.concat(nameVector.get(contriesCnt[30]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[30]) + "명\n\n");
                            break;
                        case R.id.MONG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[31]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[31]) + "명\n\n");
                            break;
                        case R.id.CN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[32]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[32]) + "명\n\n");
                            break;
                        case R.id.NK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[33]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[33]) + "명\n\n");
                            break;
                        case R.id.KR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[34]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[34]) + "명\n\n");
                            break;
                        case R.id.ID:
                            nstr = nstr.concat(nameVector.get(contriesCnt[35]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[35]) + "명\n\n");
                            break;
                        case R.id.NP:
                            nstr = nstr.concat(nameVector.get(contriesCnt[36]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[36]) + "명\n\n");
                            break;
                        case R.id.BT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[37]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[37]) + "명\n\n");
                            break;
                        case R.id.BGL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[38]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[38]) + "명\n\n");
                            break;
                        case R.id.MY:
                            nstr = nstr.concat(nameVector.get(contriesCnt[39]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[39]) + "명\n\n");
                            break;
                        case R.id.TAI:
                            nstr = nstr.concat(nameVector.get(contriesCnt[40]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[40]) + "명\n\n");
                            break;
                        case R.id.RAOS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[41]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[41]) + "명\n\n");
                            break;
                        case R.id.BIET:
                            nstr = nstr.concat(nameVector.get(contriesCnt[42]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[42]) + "명\n\n");
                            break;
                        case R.id.MAL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[43]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[43]) + "명\n\n");
                            break;
                        case R.id.IDN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[44]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[44]) + "명\n\n");
                            break;
                        case R.id.PAP:
                            nstr = nstr.concat(nameVector.get(contriesCnt[45]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[45]) + "명\n\n");
                            break;
                        case R.id.AUS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[46]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[46]) + "명\n\n");
                            break;
                        case R.id.NWZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[47]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[47]) + "명\n\n");
                            break;
                        case R.id.SOL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[48]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[48]) + "명\n\n");
                            break;
                        case R.id.VNT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[49]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[49]) + "명\n\n");
                            break;
                        case R.id.NVK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[50]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[50]) + "명\n\n");
                            break;
                        case R.id.PIZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[51]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[51]) + "명\n\n");
                            break;
                        case R.id.KRG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[52]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[52]) + "명\n\n");
                            break;
                        case R.id.TZK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[53]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[53]) + "명\n\n");
                            break;
                        case R.id.UZB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[54]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[54]) + "명\n\n");
                            break;
                        case R.id.TRK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[55]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[55]) + "명\n\n");
                            break;
                        case R.id.IRN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[56]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[56]) + "명\n\n");
                            break;
                        case R.id.AFG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[57]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[57]) + "명\n\n");
                            break;
                        case R.id.PAQ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[58]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[58]) + "명\n\n");
                            break;
                        case R.id.IRK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[59]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[59]) + "명\n\n");
                            break;
                        case R.id.SUA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[60]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[60]) + "명\n\n");
                            break;
                        case R.id.YEM:
                            nstr = nstr.concat(nameVector.get(contriesCnt[61]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[61]) + "명\n\n");
                            break;
                        case R.id.OMAN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[62]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[62]) + "명\n\n");
                            break;
                        case R.id.AEU:
                            nstr = nstr.concat(nameVector.get(contriesCnt[63]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[63]) + "명\n\n");
                            break;
                        case R.id.SIR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[64]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[64]) + "명\n\n");
                            break;
                        case R.id.TUR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[65]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[65]) + "명\n\n");
                            break;
                        case R.id.GRG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[66]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[66]) + "명\n\n");
                            break;
                        case R.id.AZB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[67]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[67]) + "명\n\n");
                            break;
                        case R.id.ARM:
                            nstr = nstr.concat(nameVector.get(contriesCnt[68]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[68]) + "명\n\n");
                            break;
                        case R.id.YRD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[69]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[69]) + "명\n\n");
                            break;
                        case R.id.ISR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[70]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[70]) + "명\n\n");
                            break;
                        case R.id.LBN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[71]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[71]) + "명\n\n");
                            break;
                        case R.id.IZT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[72]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[72]) + "명\n\n");
                            break;
                        case R.id.RIB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[73]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[73]) + "명\n\n");
                            break;
                        case R.id.SDN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[74]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[74]) + "명\n\n");
                            break;
                        case R.id.CHD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[75]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[75]) + "명\n\n");
                            break;
                        case R.id.NZR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[76]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[76]) + "명\n\n");
                            break;
                        case R.id.AZL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[77]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[77]) + "명\n\n");
                            break;
                        case R.id.MRC:
                            nstr = nstr.concat(nameVector.get(contriesCnt[78]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[78]) + "명\n\n");
                            break;
                        case R.id.SSHR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[79]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[79]) + "명\n\n");
                            break;
                        case R.id.MRT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[80]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[80]) + "명\n\n");
                            break;
                        case R.id.MALI:
                            nstr = nstr.concat(nameVector.get(contriesCnt[81]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[81]) + "명\n\n");
                            break;
                        case R.id.SNG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[82]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[82]) + "명\n\n");
                            break;
                        case R.id.GMB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[83]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[83]) + "명\n\n");
                            break;
                        case R.id.BRC:
                            nstr = nstr.concat(nameVector.get(contriesCnt[84]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[84]) + "명\n\n");
                            break;
                        case R.id.CRTB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[85]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[85]) + "명\n\n");
                            break;
                        case R.id.GINI:
                            nstr = nstr.concat(nameVector.get(contriesCnt[86]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[86]) + "명\n\n");
                            break;
                        case R.id.GNBS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[87]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[87]) + "명\n\n");
                            break;
                        case R.id.SRR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[88]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[88]) + "명\n\n");
                            break;
                        case R.id.RAIB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[89]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[89]) + "명\n\n");
                            break;
                        case R.id.GANA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[90]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[90]) + "명\n\n");
                            break;
                        case R.id.TOGO:
                            nstr = nstr.concat(nameVector.get(contriesCnt[91]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[91]) + "명\n\n");
                            break;
                        case R.id.VNG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[92]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[92]) + "명\n\n");
                            break;
                        case R.id.NIZ:
                            nstr = nstr.concat(nameVector.get(contriesCnt[93]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[93]) + "명\n\n");
                            break;
                        case R.id.CMR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[94]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[94]) + "명\n\n");
                            break;
                        case R.id.CAR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[95]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[95]) + "명\n\n");
                            break;
                        case R.id.SSD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[96]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[96]) + "명\n\n");
                            break;
                        case R.id.ETO:
                            nstr = nstr.concat(nameVector.get(contriesCnt[97]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[97]) + "명\n\n");
                            break;
                        case R.id.SMR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[98]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[98]) + "명\n\n");
                            break;
                        case R.id.KNYA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[99]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[99]) + "명\n\n");
                            break;
                        case R.id.UGD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[100]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[100]) + "명\n\n");
                            break;
                        case R.id.CNGR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[101]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[101]) + "명\n\n");
                            break;
                        case R.id.CNG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[102]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[102]) + "명\n\n");
                            break;
                        case R.id.CGN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[103]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[103]) + "명\n\n");
                            break;
                        case R.id.GBN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[104]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[104]) + "명\n\n");
                            break;
                        case R.id.AGL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[105]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[105]) + "명\n\n");
                            break;
                        case R.id.ZBA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[106]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[106]) + "명\n\n");
                            break;
                        case R.id.TZN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[107]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[107]) + "명\n\n");
                            break;
                        case R.id.RWD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[108]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[108]) + "명\n\n");
                            break;
                        case R.id.BRD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[109]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[109]) + "명\n\n");
                            break;
                        case R.id.MLW:
                            nstr = nstr.concat(nameVector.get(contriesCnt[110]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[110]) + "명\n\n");
                            break;
                        case R.id.MZB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[111]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[111]) + "명\n\n");
                            break;
                        case R.id.ZBW:
                            nstr = nstr.concat(nameVector.get(contriesCnt[112]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[112]) + "명\n\n");
                            break;
                        case R.id.BTW:
                            nstr = nstr.concat(nameVector.get(contriesCnt[113]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[113]) + "명\n\n");
                            break;
                        case R.id.NMB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[114]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[114]) + "명\n\n");
                            break;
                        case R.id.SAR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[115]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[115]) + "명\n\n");
                            break;
                        case R.id.RST:
                            nstr = nstr.concat(nameVector.get(contriesCnt[116]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[116]) + "명\n\n");
                            break;
                        case R.id.EST:
                            nstr = nstr.concat(nameVector.get(contriesCnt[117]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[117]) + "명\n\n");
                            break;
                        case R.id.MDG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[118]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[118]) + "명\n\n");
                            break;
                        case R.id.UCR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[119]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[119]) + "명\n\n");
                            break;
                        case R.id.VLR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[120]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[120]) + "명\n\n");
                            break;
                        case R.id.FLD:
                            nstr = nstr.concat(nameVector.get(contriesCnt[121]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[121]) + "명\n\n");
                            break;
                        case R.id.GER:
                            nstr = nstr.concat(nameVector.get(contriesCnt[122]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[122]) + "명\n\n");
                            break;
                        case R.id.FRNC:
                            nstr = nstr.concat(nameVector.get(contriesCnt[123]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[123]) + "명\n\n");
                            break;
                        case R.id.SPN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[124]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[124]) + "명\n\n");
                            break;
                        case R.id.ITA:
                            nstr = nstr.concat(nameVector.get(contriesCnt[125]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[125]) + "명\n\n");
                            break;
                        case R.id.SWS:
                            nstr = nstr.concat(nameVector.get(contriesCnt[126]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[126]) + "명\n\n");
                            break;
                        case R.id.AST:
                            nstr = nstr.concat(nameVector.get(contriesCnt[127]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[127]) + "명\n\n");
                            break;
                        case R.id.CHK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[128]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[128]) + "명\n\n");
                            break;
                        case R.id.SLV:
                            nstr = nstr.concat(nameVector.get(contriesCnt[129]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[129]) + "명\n\n");
                            break;
                        case R.id.HGR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[130]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[130]) + "명\n\n");
                            break;
                        case R.id.RMN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[131]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[131]) + "명\n\n");
                            break;
                        case R.id.BGR:
                            nstr = nstr.concat(nameVector.get(contriesCnt[132]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[132]) + "명\n\n");
                            break;
                        case R.id.GRC:
                            nstr = nstr.concat(nameVector.get(contriesCnt[133]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[133]) + "명\n\n");
                            break;
                        case R.id.SRV:
                            nstr = nstr.concat(nameVector.get(contriesCnt[134]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[134]) + "명\n\n");
                            break;
                        case R.id.SLVN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[135]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[135]) + "명\n\n");
                            break;
                        case R.id.CRT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[136]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[136]) + "명\n\n");
                            break;
                        case R.id.BSN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[137]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[137]) + "명\n\n");
                            break;
                        case R.id.CSB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[138]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[138]) + "명\n\n");
                            break;
                        case R.id.MTN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[139]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[139]) + "명\n\n");
                            break;
                        case R.id.ABN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[140]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[140]) + "명\n\n");
                            break;
                        case R.id.NMK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[141]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[141]) + "명\n\n");
                            break;
                        case R.id.RTN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[142]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[142]) + "명\n\n");
                            break;
                        case R.id.RTB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[143]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[143]) + "명\n\n");
                            break;
                        case R.id.ESTN:
                            nstr = nstr.concat(nameVector.get(contriesCnt[144]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[144]) + "명\n\n");
                            break;
                        case R.id.LSB:
                            nstr = nstr.concat(nameVector.get(contriesCnt[145]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[145]) + "명\n\n");
                            break;
                        case R.id.VGE:
                            nstr = nstr.concat(nameVector.get(contriesCnt[146]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[146]) + "명\n\n");
                            break;
                        case R.id.NDL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[147]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[147]) + "명\n\n");
                            break;
                        case R.id.DMK:
                            nstr = nstr.concat(nameVector.get(contriesCnt[148]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[148]) + "명\n\n");
                            break;
                        case R.id.PRT:
                            nstr = nstr.concat(nameVector.get(contriesCnt[149]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[149]) + "명\n\n");
                            break;
                        case R.id.MDV:
                            nstr = nstr.concat(nameVector.get(contriesCnt[150]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[150]) + "명\n\n");
                            break;
                        case R.id.ENG:
                            nstr = nstr.concat(nameVector.get(contriesCnt[151]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[151]) + "명\n\n");
                            break;
                        case R.id.ISL:
                            nstr = nstr.concat(nameVector.get(contriesCnt[152]) + "\n확진자 수 : " + confirmedVector.get(contriesCnt[152]) + "명\n\n");
                            break;
                    }

                    dialogcnt++;
                    touch = false;
                    String alertTitle = null;
            if (selected == 0) {
                alertTitle = "COVID-19 감염병 현황";
            }
            else if(selected == 1) {
                alertTitle = "테스트1 감염병 현황";
            }
            else if (selected == 2) {
                alertTitle = "테스트2 감염병 현황";
            }
                    a = new AlertDialog.Builder(getContext());

                        a.setTitle(alertTitle)
                                .setMessage(nstr)
                                .setNeutralButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogcnt = 0;
                                        nstr = "";
                                    }
                                });


                        ad[dialogcnt] = a.create();

                        if(dialogcnt  >1){
                        ad[dialogcnt-1].dismiss();
                    }
                        ad[dialogcnt].show();

        }
    }


    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) { }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
                zoomCnt++;
                if(zoomCnt > 1) {
                    zoomCnt = 0;
                    mScaleFactor *= scaleFactor;
                    mScaleFactor = Math.max(MIN_ZOOM, Math.min(mScaleFactor, MAX_ZOOM));
                    lastScaleFactor = scaleFactor;
                    applyScaleAndTranslation();
                }
            return true;
        }
    }

    public void applyScaleAndTranslation() {
        Log.d("scale",mScaleFactor+"");
        scrollView.setScaleX(mScaleFactor);
        scrollView.setScaleY(mScaleFactor);
    }

    public class BtnListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            String str = "FF0000";
            switch(v.getId()){
                case R.id.deasease1:{
                    selected = 0;
                    str = "FF0000";
                    break;
                }
                case R.id.deasease2:{
                    selected = 1;
                    str = "99FF99";
                    break;
                }
                case R.id.deasease3:{
                    selected = 2;
                    str = "6200EE";
                    break;
                }
            }
            confirmedVector.clear();
            nameVector.clear();
            getData();
            for(int i = 0; i < countriesID.length; i++){
                countries[i] = getActivity().findViewById(countriesID[i]);
                countries[i].setClickable(true);
                countries[i].setOnClickListener(countryTouchListener);
                if(confirmedVector.get(i) > 0 && confirmedVector.get(i) <= 100000){ //확진자 1명 이상 10만명 이하
                    countries[i].setColorFilter(Color.parseColor("#4D"+str),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 30%
                }
                else if(confirmedVector.get(i) > 100000 && confirmedVector.get(i) <= 1000000){ //10만명~100만명
                    countries[i].setColorFilter(Color.parseColor("#66"+str),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 40%
                }
                else if(confirmedVector.get(i) > 1000000 && confirmedVector.get(i) <= 3000000){ //300만명
                    countries[i].setColorFilter(Color.parseColor("#80"+str),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 50%
                }
                else if(confirmedVector.get(i) > 3000000 && confirmedVector.get(i) <= 5000000){ //500만명
                    countries[i].setColorFilter(Color.parseColor("#99"+str),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 60%
                }
                else if(confirmedVector.get(i) > 5000000 && confirmedVector.get(i) <= 10000000){ //1000만명
                    countries[i].setColorFilter(Color.parseColor("#B3"+str),PorterDuff.Mode.SRC_ATOP); // 빨강색, 투명도 70%
                }
                else if(confirmedVector.get(i) > 10000000){ //1000만명이상
                    countries[i].setColorFilter(Color.parseColor("#CC"+str),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 80%
                }else{ //정보없음 혹은 확진자 0
                    countries[i].setColorFilter(Color.parseColor("#1A"+str),PorterDuff.Mode.SRC_ATOP); //빨강색, 투명도 10%
                }


            }

        }
    }
}