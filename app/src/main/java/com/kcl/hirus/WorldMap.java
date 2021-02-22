package com.kcl.hirus;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import jxl.Sheet;
import jxl.Workbook;


public class WorldMap extends Fragment implements MainActivity.OnBackpressedListener{
    HorizontalScrollView scrollView;
    AlertDialog.Builder a;
    AlertDialog ad[] = new AlertDialog[10];
    ImageView[] countries = new ImageView[200];
    int[] countriesID = {R.id.CA, R.id.US, R.id.GL, R.id.MX, R.id.GT,
            R.id.HD, R.id.ELS, R.id.BLZ, R.id.NCG, R.id.CR, R.id.PNM, R.id.CB, R.id.VZ, R.id.ECD, R.id.PR,
            R.id.BZ, R.id.BV, R.id.PRG, R.id.CL, R.id.AG, R.id.UG, R.id.GA, R.id.SN, R.id.FGA, R.id.FOL,
            R.id.RS, R.id.IS, R.id.FL, R.id.SW, R.id.NW, R.id.KZH, R.id.MONG, R.id.CN, R.id.NK, R.id.KR,
            R.id.ID, R.id.NP, R.id.BT, R.id.BGL, R.id.MY, R.id.TAI, R.id.RAOS, R.id.BIET, R.id.MAL, R.id.IDN,
            R.id.PAP, R.id.AUS, R.id.NWZ, R.id.SOL, R.id.VNT, R.id.NVK, R.id.PIZ, R.id.KRG, R.id.TZK, R.id.UZB,
            R.id.TRK, R.id.IRN, R.id.AFG, R.id.PAQ, R.id.IRK, R.id.SUA, R.id.YEM, R.id.OMAN, R.id.AEU, R.id.SIR,
            R.id.TUR, R.id.GRG, R.id.AZB, R.id.ARM, R.id.YRD, R.id.ISR, R.id.LBN, R.id.IZT, R.id.RIB, R.id.SDN,
            R.id.CHD, R.id.NZR, R.id.AZL, R.id.MRC, R.id.SSHR, R.id. MRT, R.id.MALI, R.id.SNG, R.id.GMB, R.id.BRC,
            R.id.CRTB, R.id.GINI, R.id.GNBS, R.id.SRR, R.id.RAIB, R.id.GANA, R.id.TOGO, R.id.VNG, R.id.NIZ, R.id.CMR,
            R.id.CAR, R.id.SSD, R.id.ETO, R.id.SMR, R.id.KNYA, R.id.UGD, R.id.CNGR, R.id.CNG, R.id.CGN, R.id.GBN,
            R.id.AGL, R.id.ZBA, R.id.TZN, R.id.RWD, R.id.BRD, R.id.MLW, R.id.MZB, R.id.ZBW, R.id.BTW, R.id.NMB,
            R.id.SAR, R.id.RST, R.id.EST, R.id.MDG};
    Vector<Integer> confirmedVector = new Vector<Integer>();
    Vector<String> nameVector = new Vector<String>();
    CountryTouchListener countryTouchListener;
    Fragment result;
    FragmentManager fm;
    int dialogcnt = 0;
    String nstr = "";
    boolean touch = false;

    static int CA = 0;//캐나다 CANADA
    static int US = 1;//미국 US
    static int GL = 2;//그린란드 GREENLAND
    static int MX = 3;//멕시코 MEXICO
    static int GT = 4;//과테말라 GUATEMALA
    static int HD = 5;//혼두라스 HONDURAS
    static int ELS = 6;//엘살바도르 EL SALVADOR
    static int BLZ = 7;//벨리제 BELIZE
    static int NCG = 8;//니카라구아 NICARAGUA
    static int CR = 9;//코스타리카 COSTA RICA
    static int PNM = 10;//파나마 PANAMA
    static int CB = 11;//콜롬비아 COLOMBIA
    static int VZ = 12;//베네수엘라 VENEZUELA
    static int ECD = 13;//에콰도르 ECUADOR
    static int PR = 14;//페루 PERU
    static int BZ = 15;//브라질 BRAZIL
    static int BV = 16;//볼리비아 BOLIVIA
    static int PRG = 17;//파라과이 PARAGUAY
    static int CL = 18;//칠레 CHILE
    static int AG = 19;//아르헨티나 ARGENTINA
    static int UG = 20;//우루과이 URUGUAY
    static int GA = 21;//가이아나 GUYANA
    static int SN = 22;//수리남 SURINAME
    static int FGA = 23;//프랑스령 기아나 FRENCH GUYANA

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootview = inflater.inflate(R.layout.fragment_world_map,container,false);

        scrollView = rootview.findViewById(R.id.WhitemapView);

        scrollView.setHorizontalScrollBarEnabled(true);
        countryTouchListener = new CountryTouchListener();
        result = getFragmentManager().findFragmentById(R.id.coronaLayout);

        getCOVIDData();
        Log.d("length",countriesID.length+"");



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
            countries[i].setOnTouchListener(countryTouchListener);
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
        return rootview;
    }

    public void getCOVIDData() {
        try {
            InputStream is = getResources().getAssets().open("corona.xls");
            Workbook wb = Workbook.getWorkbook(is);

            if(wb != null){
                Sheet sheet = wb.getSheet(0);
                if(sheet != null){
                    int rowTotal = sheet.getRows();
                    int colTotal = sheet.getColumns();

                    for(int i = 0; i < colTotal; i++){
                        int patients = Integer.parseInt(sheet.getCell(i,0).getContents());
                        String name = sheet.getCell(i,2).getContents();
                        confirmedVector.add(patients);
                        nameVector.add(name);
                        Log.d("test",colTotal+"");
                    }

                }
            }

        }catch (Exception e){e.printStackTrace();};


    }




    @Override
    public void onBack() {
        Log.e("etc","onBack()");
        MainActivity activity = (MainActivity)getActivity();
        activity.setOnBackPressedListener(null);

        getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right).remove(this).commit();
        activity.toolbar_title.setText(activity.addressArr);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.e("etc","onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }



    class CountryTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    touch = true;
                    Log.d("event","down");
                    switch (v.getId()) {
                        case R.id.CA:
                            nstr = nstr.concat(nameVector.get(CA) + "\n확진자 수 : " + confirmedVector.get(CA) + "명\n\n");
                            break;
                        case R.id.US:
                            nstr = nstr.concat(nameVector.get(US) + "\n확진자 수 : " + confirmedVector.get(US) + "명\n\n");
                            break;
                        case R.id.GL:
                            Log.d("GL", nameVector.get(GL) + "" + confirmedVector.get(GL));
                            break;
                        case R.id.MX:
                            Log.d("MX", nameVector.get(MX) + "" + confirmedVector.get(MX));
                            break;
                        case R.id.GT:
                            Log.d("GT", nameVector.get(GT) + "" + confirmedVector.get(GT));
                            break;
                        case R.id.HD:
                            Log.d("HD", nameVector.get(HD) + "" + confirmedVector.get(HD));
                            break;
                        case R.id.ELS:
                            Log.d("ELS", nameVector.get(ELS) + "" + confirmedVector.get(ELS));
                            break;
                        case R.id.BLZ:
                            Log.d("BLZ", nameVector.get(BLZ) + "" + confirmedVector.get(BLZ));
                            break;
                        case R.id.NCG:
                            Log.d("NCG", nameVector.get(NCG) + "" + confirmedVector.get(NCG));
                            break;
                        case R.id.CR:
                            Log.d("CR", nameVector.get(CR) + "" + confirmedVector.get(CR));
                            break;
                        case R.id.PNM:
                            Log.d("PNM", nameVector.get(PNM) + "" + confirmedVector.get(PNM));
                            break;
                        case R.id.CB:
                            Log.d("CB", nameVector.get(CB) + "" + confirmedVector.get(CB));
                            break;
                        case R.id.VZ:
                            Log.d("GL", nameVector.get(GL) + "" + confirmedVector.get(GL));
                            break;
                        case R.id.ECD:
                            Log.d("MX", nameVector.get(MX) + "" + confirmedVector.get(MX));
                            break;
                        case R.id.PR:
                            Log.d("PR", nameVector.get(GT) + "" + confirmedVector.get(GT));
                            break;
                        case R.id.BZ:
                            Log.d("BZ", nameVector.get(BZ) + "" + confirmedVector.get(HD));
                            break;
                        case R.id.BV:
                            Log.d("BV", nameVector.get(BV) + "" + confirmedVector.get(ELS));
                            break;
                        case R.id.PRG:
                            Log.d("PRG", nameVector.get(PRG) + "" + confirmedVector.get(BLZ));
                            break;
                        case R.id.CL:
                            Log.d("CL", nameVector.get(CL) + "" + confirmedVector.get(NCG));
                            break;
                        case R.id.UG:
                            Log.d("UG", nameVector.get(UG) + "" + confirmedVector.get(CR));
                            break;
                        case R.id.GA:
                            Log.d("GA", nameVector.get(GA) + "" + confirmedVector.get(CR));
                            break;
                        case R.id.SN:
                            Log.d("SN", nameVector.get(SN) + "" + confirmedVector.get(CR));
                            break;
                        case R.id.FGA:
                            Log.d("FGA", nameVector.get(FGA) + "" + confirmedVector.get(CR));
                            break;
                        case R.id.AG:
                            Log.d("AG", nameVector.get(AG) + "" + confirmedVector.get(CR));
                            break;
                    }
                    Log.d("event",nstr);
                    dialogcnt++;
                    Log.d("do",dialogcnt+"");
                    touch = false;
                    a = new AlertDialog.Builder(getContext());

                        a.setTitle("선택한 구역의 국가들의 감염병 현황")
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
                        break;

                }
            }
            return false;

        }
    }
}