package com.kcl.hirus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;


public class WorldMap extends Fragment implements MainActivity.OnBackpressedListener{
    HorizontalScrollView scrollView;
    ImageView[] countries = new ImageView[200];
    //ImageView map;
    BitmapDrawable bitmap;
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
    static int CL = 17;//칠레 CHILE
    static int AG = 18;//아르헨티나 ARGENTINA
    static int UG = 19;//우루과이 URUGUAY
    static int GA = 20;//가이아나 GUYANA
    static int SN = 21;//수리남 SURINAME
    static int FGA = 22;//프랑스령 기아나 FRENCH GUYANA





    HashMap<ImageView, Integer> countryMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootview = inflater.inflate(R.layout.fragment_world_map,container,false);

        scrollView = rootview.findViewById(R.id.WhitemapView);

        scrollView.setHorizontalScrollBarEnabled(true);

        countries[CA] = rootview.findViewById(R.id.CA);
        countries[US] = rootview.findViewById(R.id.US);
        countries[GL] = rootview.findViewById(R.id.GL);
        countries[MX] = rootview.findViewById(R.id.MX);

        countries[CA].setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Toast.makeText(rootview.getContext(), "캐나다", Toast.LENGTH_SHORT).show();
                return false;
            }
        });


        return rootview;
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
}