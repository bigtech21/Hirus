package com.kcl.hirus;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


public class Etc extends Fragment implements MainActivity.OnBackpressedListener{



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_etc, container, false);
    }

    @Override
    public void onBack() {
        Log.e("etc","onBack()");
        MainActivity activity = (MainActivity)getActivity();
        activity.setOnBackPressedListener(null);
        activity.tabLayout.getTabAt(0).select();
        getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
        activity.toolbar_title.setText(activity.addressArr);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        Log.e("etc","onAttach()");
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}