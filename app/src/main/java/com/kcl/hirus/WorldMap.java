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
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

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
    int RS = 0;
    int CA = 1;
    int US = 2;


    HashMap<ImageView, Integer> countryMap = new HashMap<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_world_map,container,false);

        scrollView = rootview.findViewById(R.id.WhitemapView);

        scrollView.setHorizontalScrollBarEnabled(true);


        //map = rootview.findViewById(R.id.map);
        countries[0] = rootview.findViewById(R.id.CA);
        countries[1] = rootview.findViewById(R.id.US);


        int j = 25;
        for(int i = 0; i < countries.length; i++){

            countryMap.put(countries[i],j);
            j = j + 25;
        }

        for(int i = 0; i< countryMap.size(); i++){
            if(countryMap.get(countries[i]) >= 25 & countryMap.get(countries[i]) <= 49){
                countries[0].setColorFilter(Color.parseColor("#0000FF"), PorterDuff.Mode.SRC_IN);
            }
            else if(countryMap.get(countries[i]) >= 50 & countryMap.get(countries[i]) <= 74){
                countries[1].setColorFilter(Color.parseColor("#FF0000"), PorterDuff.Mode.SRC_IN);
            }
        }

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