package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.BaseAdapter;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.kcl.hirus.R;

public class SettingPreferenceFragment extends PreferenceFragment {
    SharedPreferences prefs;

    ListPreference backPreference;
    PreferenceScreen backserviceset;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_preference);
        backPreference = (ListPreference) findPreference("backsetlist");
        backserviceset = (PreferenceScreen) findPreference("backgroundserviceset");

        prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        backPreference.setSummary(prefs.getString("backsetlist",""));

        if(!prefs.getString("backsetlist","").equals("")){
            backserviceset.setSummary(prefs.getString("backsetlist",""));
        }
        if(!prefs.getString("backgroundserviceset","").equals("")){
            backserviceset.setSummary(prefs.getString("backgroundserviceset",""));
        }

        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener prefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if(key.equals("backsetlist")){
                backserviceset.setSummary(prefs.getString("backsetlist",""));
                backPreference.setSummary(prefs.getString("backsetlist",""));
            }
            ((BaseAdapter)getPreferenceScreen().getRootAdapter()).notifyDataSetChanged();
        }
    };


}
