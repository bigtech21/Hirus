package com.kcl.hirus;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOError;
import java.io.IOException;


public class hotissue extends Fragment implements MainActivity.OnBackpressedListener{
    TextView issueTitle,Blank;
    TextView[] issueses = new TextView[10];
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_hotissue, container, false);
        issueTitle = rootView.findViewById(R.id.issueTitle);
        issueses[0] = rootView.findViewById(R.id.one);
        issueses[1] = rootView.findViewById(R.id.two);
        issueses[2] = rootView.findViewById(R.id.three);
        issueses[3] = rootView.findViewById(R.id.four);
        issueses[4] = rootView.findViewById(R.id.five);
        issueses[5]= rootView.findViewById(R.id.six);
        issueses[6] = rootView.findViewById(R.id.seven);
        issueses[7] = rootView.findViewById(R.id.eight);
        issueses[8] = rootView.findViewById(R.id.nine);
        issueses[9] = rootView.findViewById(R.id.ten);
        Blank = rootView.findViewById(R.id.blank);
        Blank.setText("");
        getWebsite();

        return rootView;
    }

    private void getWebsite() {
        new Thread(new Runnable() {
            final StringBuilder builder = new StringBuilder();
        public void run() {
                try
                {
                    Document doc = Jsoup.connect("http://www.cdc.go.kr/search/search.es?mid=a20101000000")
                            .get();
                    Elements title = doc.select("article.box_keyword");

                    String[] issueKeyword = (title.text()).split(" ");
                    String strr = issueKeyword[0];
                    issueTitle.setText(strr);


                    for(int i = 0; i<10; i++) {
                        Log.d("Web", issueKeyword[i+1]);
                        String str = issueKeyword[i+1];

                       String cnt = Integer.toString(i+1);
                        String newStr = str.replaceFirst(cnt,cnt + ". ");
                        issueses[i].setText(newStr);
                    }


                }catch(IOException e)

                {
                    builder.append("error");
                }
        }
        }).start();

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