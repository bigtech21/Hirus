package com.kcl.hirus;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Vector;


public class Minigame extends Fragment implements MainActivity.OnBackpressedListener{
    WordGame wg = new WordGame();
    EditText submitAns;
    TextView problem;
    String submitString;
    String answerString;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_minigame, container, false);

        problem = rootView.findViewById(R.id.problem);
        submitAns = (EditText) rootView.findViewById(R.id.submitAns);
        int i = 0;
        problem.setText(wg.wordsAsk.get(i));

        answerString = wg.wordsAns.get(i);

        Button submit = rootView.findViewById(R.id.ansApply);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitString = submitAns.getText().toString();
                try {
                    if (submitString.equals(answerString)) {
                        Toast.makeText(getContext(), "정답입니다!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "오답입니다...", Toast.LENGTH_SHORT).show();
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        return rootView;
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