package fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.kcl.hirus.R;

import activity.MainActivity;

public class SearchFragment extends Fragment implements MainActivity.OnBackpressedListener{
    SearchView search;
    LinearLayout layout;
    private final int URL_CDC_FLAG = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("test","created");
        final View rootView = inflater.inflate(R.layout.fragment_inf_search, container, false);
        search = rootView.findViewById(R.id.searchView);
        layout = rootView.findViewById(R.id.inf_search_fr);

        try {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String s) {

                        WebFragment web = new WebFragment(URL_CDC_FLAG);
                        String searchText = s;
                        web.setUrlCode(searchText);
                        getFragmentManager().beginTransaction().replace(R.id.webAdd, web).commit();
                        Log.d("Web", "추가");

                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });
        }catch(Exception e){
            Toast.makeText(getContext(), "잘못된 접근입니다.",Toast.LENGTH_SHORT).show();
        }
        return rootView;
    }

    @Override
    public void onBack() {
        MainActivity activity = (MainActivity)getActivity();
        try {
            activity.setOnBackPressedListener(null);
            activity.tabLayout.getTabAt(0).select();
            activity.getSupportFragmentManager().beginTransaction().remove(this).commit();
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