package fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.kcl.hirus.R;


import activity.MainActivity;
import data.WordsData;



public class MinigameFragment extends Fragment implements MainActivity.OnBackpressedListener {
    WordsData wg = new WordsData();
    EditText submitAns;
    TextView problem;
    TextView scoreText;
    String submitStr;
    String answerStr;
    String scoreStr;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    int score;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_minigame, container, false);

        problem = rootView.findViewById(R.id.problem);
        submitAns = (EditText) rootView.findViewById(R.id.submitAns);
        scoreText = rootView.findViewById(R.id.score);
        prefs = getActivity().getSharedPreferences("score",Context.MODE_PRIVATE);
        score = prefs.getInt("scoreValue",0);

        scoreStr = "현재 점수 : ";
        scoreText.setText(scoreStr + score);

        setProblem();

        Button submit = rootView.findViewById(R.id.ansApply);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitStr = submitAns.getText().toString();
                try {
                    if (submitStr.equals(answerStr)) {
                        Toast.makeText(getContext(), "정답입니다!", Toast.LENGTH_SHORT).show();
                        score += 1;
                        editor = prefs.edit();
                        editor.putInt("scoreValue",score);
                        editor.commit();
                        scoreText.setText(scoreStr + score);
                        setProblem();
                        submitAns.setText("");

                    } else {
                        Toast.makeText(getContext(), "오답입니다...", Toast.LENGTH_SHORT).show();
                        setProblem();
                        submitAns.setText("");
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });

        return rootView;
    }

    public void setProblem(){
        int i = (int)(Math.random()*(wg.wordsAns.size()));
        problem.setText(wg.wordsAsk.get(i));;
        answerStr = wg.wordsAns.get(i);
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
        catch(Exception e){}
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        ((MainActivity)context).setOnBackPressedListener(this);
    }
}