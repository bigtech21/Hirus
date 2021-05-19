package fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.kcl.hirus.R;

public class WebFragment extends Fragment {
    private WebView webview;
    private WebSettings webSettings;
    String urlCode = "a20101000000";
    int webFlag;

    public WebFragment(int flag){
        webFlag = flag;
    }


    public void setUrlCode(String url){
        urlCode = url;
    }
    String getUrlCode(){
        return urlCode;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_web, container, false);
        String url = null;
        if(webFlag == 0) {
            url = "http://www.kdca.go.kr/search/" +
                    "search.es?mid=a20101000000&termType=A&kwd=" + getUrlCode() +
                    "&category=TOTAL&reSrchFlag=false&pageNum=1&pageSize=10&detailSearch=false&srchFd=TOTAL&" +
                    "sort=d&date=TOTAL&startDate=&endDate=&fileExt=TOTAL&writer=&year=TOTAL&site=CDC&preKwd=%EC%BD%94%EB%A1%9C%EB%82%98";
        }
        else if(webFlag == 1){
            url = "http://www.kdca.go.kr/npt/biz/npp/portal/nppSumryMain.do?icdCd=" + getUrlCode() +"&icdSubgrpCd=";
        }
        webview = rootView.findViewById(R.id.webView);
        webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportMultipleWindows(false);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);
        Log.d("Web",url);

        webview.loadUrl(url);




        return rootView;
    }
}