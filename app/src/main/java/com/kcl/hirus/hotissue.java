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
    String[] keyWords = new String[10];
    private final int URL_CDC_FLAG = 0;
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

        int i = 0;

        Blank = rootView.findViewById(R.id.blank);
        Blank.setText("");


        getWebsite();

        for(i = 0; i<10; i++) {
            issueses[i].setClickable(true);
            issueses[i].setOnClickListener(new Mylistener());
        }
        return rootView;
    }


    class Mylistener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String urlcode = "a20101000000";
            if(v.getId() == R.id.one){
                keyWords[0] = issueses[0].getText().toString();
                urlcode = keyWords[0].substring(2);
            }
            else if(v.getId() == R.id.two){
                keyWords[1] = issueses[1].getText().toString();
                urlcode = keyWords[1].substring(2);
            }
            else if(v.getId() == R.id.three){
                keyWords[2] = issueses[2].getText().toString();
                urlcode = keyWords[2].substring(2);
            }
            else if(v.getId() == R.id.four){
                keyWords[3] = issueses[3].getText().toString();
                urlcode = keyWords[3].substring(2);
            }
            else if(v.getId() == R.id.five){
                keyWords[4] = issueses[4].getText().toString();
                urlcode = keyWords[4].substring(2);
            }
            else if(v.getId() == R.id.six){
                keyWords[5] = issueses[5].getText().toString();
                urlcode = keyWords[5].substring(2);
            }
            else if(v.getId() == R.id.seven){
                keyWords[6] = issueses[6].getText().toString();
                urlcode = keyWords[6].substring(2);
            }
            else if(v.getId() == R.id.eight){
                keyWords[7] = issueses[7].getText().toString();
                urlcode = keyWords[7].substring(2);
            }
            else if(v.getId() == R.id.nine){
                keyWords[8] = issueses[8].getText().toString();
                urlcode = keyWords[8].substring(2);
            }
            else if(v.getId() == R.id.ten){
                keyWords[9] = issueses[9].getText().toString();
                urlcode = keyWords[9].substring(2);
            }
            Web web = new Web(URL_CDC_FLAG);
            web.setUrlCode(urlcode);
            getFragmentManager().beginTransaction().replace(R.id.layout, web).addToBackStack(null).commit();
        }
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