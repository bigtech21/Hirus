package com.kcl.hirus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;

public class Setting extends Fragment implements MainActivity.OnBackpressedListener{
    String CHANNEL_ID = "channel1";
    String CHANNEL_NAME = "Channel1";

    String CHANNEL_ID2 = "channel2";
    String CHANNEL_NAME2 = "channel2";

    NotificationManager manager;
    NotificationCompat.Builder Nbuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        final ViewGroup rootview = (ViewGroup)inflater.inflate(R.layout.fragment_settings,container,false);

        final Switch SVSwitch = (Switch) rootview.findViewById(R.id.sound_vibe);
        SVSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Vibrator vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                if(isChecked){
                    if(Build.VERSION.SDK_INT >= 26){
                        vibrator.vibrate(VibrationEffect.createOneShot(1000,10));
                        Toast.makeText(getActivity(),"붕~",Toast.LENGTH_SHORT).show();
                    }else
                    {
                        vibrator.vibrate(1000);
                        Toast.makeText(getActivity(),"붕~",Toast.LENGTH_SHORT).show();
                    }
                    SVSwitch.setText("진동모드");
                }else{
                    SVSwitch.setText("소리모드");
                    Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone ringtone = RingtoneManager.getRingtone(getActivity().getApplicationContext(), uri);
                    ringtone.play();
                }
            }
        });

        final Button button = (Button) rootview.findViewById(R.id.button2);
        button.setOnClickListener(new myListener1());

        final Button button1 = (Button)rootview.findViewById(R.id.button3);
        button1.setOnClickListener(new myListener2());

        return rootview;
    }

    class myListener1 implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showNoti();
        }
    }

    class myListener2 implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            showNoti2();
        }
    }


    public void showNoti() {
    MainActivity m = new MainActivity();
        manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        //Nbuilder  = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(manager.getNotificationChannel(CHANNEL_ID) != null){
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                        ));

                Nbuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID);
            }
        }else {
            Nbuilder = new NotificationCompat.Builder(getContext());
        }

        Nbuilder.setContentTitle("Hirus Notification");
        Nbuilder.setContentText("Hirus");
        Nbuilder.setSmallIcon(android.R.drawable.ic_menu_view);
        Notification noti = Nbuilder.build();

        manager.notify(1,noti);
    }

    public void showNoti2() {
        manager = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        //Nbuilder = null;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            if(manager.getNotificationChannel(CHANNEL_ID2) != null){
                manager.createNotificationChannel(new NotificationChannel(
                        CHANNEL_ID2, CHANNEL_NAME2, NotificationManager.IMPORTANCE_DEFAULT
                ));

                Nbuilder = new NotificationCompat.Builder(getContext(), CHANNEL_ID2);
            }
        }
        else
            {
            Nbuilder = new NotificationCompat.Builder(getActivity());
        }

        Intent intent = new Intent(getActivity(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 101, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Nbuilder.setContentTitle("하이러스");
        Nbuilder.setContentText("하이러스");
        Nbuilder.setSmallIcon(android.R.drawable.ic_menu_view);
        Nbuilder.setAutoCancel(true);
        Nbuilder.setContentIntent(pendingIntent);

        Notification noti = Nbuilder.build();

        manager.notify(2,noti);

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