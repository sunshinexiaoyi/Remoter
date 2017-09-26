package com.gospell.wildwolf.remoter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class RemoterActivity extends Activity {
    private GestureManager gestureManager = new GestureManager(new RemoterGestureCallback());
    private LinearLayout layoutEpg;
    private LinearLayout layoutProglist;

    HashMap<Integer,Integer> keysMap = new HashMap();

    private android.support.v7.widget.AppCompatImageButton settingsRemote ;

    private final int WHAT_SETTING_REMOTE_CANCLE = 1;

    final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case WHAT_SETTING_REMOTE_CANCLE:
                    settingsRemote.setSelected(false);
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.remoter);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉状态栏

        layoutProglist = (LinearLayout)findViewById(R.id.linear_proglist1);
        layoutProglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_program = new Intent(RemoterActivity.this, ProgramsActivity.class);
                startActivity(intent_program);
            }
        });

        layoutEpg = (LinearLayout)findViewById(R.id.linear_epg1);
        layoutEpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_epg = new Intent(RemoterActivity.this, EpgActivity.class);
                startActivity(intent_epg);
            }
        });

        Button figureBn = (Button)findViewById(R.id.figure);
        figureBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFuncFrame();
            }
        });

        Button funcBn = (Button)findViewById(R.id.funcButton);
        funcBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFigureFrame();
            }
        });

        settingsRemote = (android.support.v7.widget.AppCompatImageButton)findViewById(R.id.settings_remote);

        initKeysMap();
        initKeyGesture();

        AppManager.getAppManager().addActivity(this);



        LinearLayout mainFrame = (LinearLayout)findViewById(R.id.mainFrame);
        mainFrame.setOnTouchListener(gestureManager);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    public void initKeysMap()
    {
        keysMap.put(R.id.power,KeyValue.KEYVALUE_POWER);
        keysMap.put(R.id.func1,KeyValue.KEYVALUE_FUNC1);
        keysMap.put(R.id.exit,KeyValue.KEYVALUE_EXIT);
        keysMap.put(R.id.menue,KeyValue.KEYVALUE_MENUE);
        keysMap.put(R.id.up,KeyValue.KEYVALUE_UP);
        keysMap.put(R.id.info,KeyValue.KEYVALUE_INFO);
        keysMap.put(R.id.back,KeyValue.KEYVALUE_BACK);
        keysMap.put(R.id.left,KeyValue.KEYVALUE_LEFT);



        keysMap.put(R.id.ok,KeyValue.KEYVALUE_OK);
        keysMap.put(R.id.right,KeyValue.KEYVALUE_RIGHT);
        keysMap.put(R.id.vol_add,KeyValue.KEYVALUE_VOL_ADD);
        keysMap.put(R.id.down,KeyValue.KEYVALUE_DOWN);
        keysMap.put(R.id.ch_add,KeyValue.KEYVALUE_CH_ADD);
        keysMap.put(R.id.vol_sub,KeyValue.KEYVALUE_VOL_SUB);
        keysMap.put(R.id.fav,KeyValue.KEYVALUE_FAV);


        keysMap.put(R.id.ch_sub,KeyValue.KEYVALUE_CH_SUB);
        keysMap.put(R.id.guide,KeyValue.KEYVALUE_GUIDE);

        keysMap.put(R.id.mute,KeyValue.KEYVALUE_MUTE);

        keysMap.put(R.id.pvr,KeyValue.KEYVALUE_PVR);

        //number
        keysMap.put(R.id.number0,KeyValue.KEYVALUE_0);
        keysMap.put(R.id.number1,KeyValue.KEYVALUE_1);
        keysMap.put(R.id.number2,KeyValue.KEYVALUE_2);
        keysMap.put(R.id.number3,KeyValue.KEYVALUE_3);
        keysMap.put(R.id.number4,KeyValue.KEYVALUE_4);
        keysMap.put(R.id.number5,KeyValue.KEYVALUE_5);
        keysMap.put(R.id.number6,KeyValue.KEYVALUE_6);
        keysMap.put(R.id.number7,KeyValue.KEYVALUE_7);
        keysMap.put(R.id.number8,KeyValue.KEYVALUE_8);
        keysMap.put(R.id.number9,KeyValue.KEYVALUE_9);


        keysMap.put(R.id.btn_red,KeyValue.KEYVALUE_RED);
        keysMap.put(R.id.btn_green,KeyValue.KEYVALUE_GREEN);
        keysMap.put(R.id.btn_yellow,KeyValue.KEYVALUE_YELLOW);
        keysMap.put(R.id.btn_blue,KeyValue.KEYVALUE_BLUE);

        keysMap.put(R.id.fast_rewind,KeyValue.KEYVALUE_REW);
        keysMap.put(R.id.play,KeyValue.KEYVALUE_PLAY);
        keysMap.put(R.id.pause,KeyValue.KEYVALUE_PAUSE);
        keysMap.put(R.id.fast_forward,KeyValue.KEYVALUE_FF);
        keysMap.put(R.id.skip_previous,KeyValue.KEYVALUE_PRE);
        keysMap.put(R.id.record,KeyValue.KEYVALUE_REC);
        keysMap.put(R.id.stop,KeyValue.KEYVALUE_STOP);
        keysMap.put(R.id.skip_next,KeyValue.KEYVALUE_NEXT);

        keysMap.put(R.id.set,KeyValue.KEYVALUE_SET);
        keysMap.put(R.id.excite,KeyValue.KEYVALUE_EXCITE);
        keysMap.put(R.id.help,KeyValue.KEYVALUE_HELP);



    }
    public void hideFuncFrame()
    {
        ViewGroup function = (ViewGroup) findViewById(R.id.function);
        ViewGroup digitFrame = (ViewGroup) findViewById(R.id.digit);
        function.setVisibility(View.GONE);
        digitFrame.setVisibility(View.VISIBLE);
    }

    public void hideFigureFrame()
    {
        ViewGroup function = (ViewGroup) findViewById(R.id.function);
        ViewGroup digitFrame = (ViewGroup) findViewById(R.id.digit);
        function.setVisibility(View.VISIBLE);
        digitFrame.setVisibility(View.GONE);
    }
    public void initKeyGesture()
    {
        for(Integer id : keysMap.keySet())
        {
           // Log.i("remoter"," "+id);
            ((View)findViewById(id)).setOnTouchListener(gestureManager);
        }
    }

    public void remoterFuncOnClick(View source) {
        int keyValue = -1;

        Integer key = new Integer(source.getId());
        if(keysMap.containsKey(key))
        {
            keyValue = keysMap.get(key);
        }
        Log.i("remoter",""+key);
        Log.i("remoter","keyValue:"+keyValue);


        if (-1 != keyValue) {

            Intent intent = new Intent();
            intent.setAction(NetService.BROADCAST_ACTION);
            intent.putExtra(getResources().getString(R.string.command), NetService.command_send_remoterKey);
            intent.putExtra(getResources().getString(R.string.key_value), keyValue);
            sendBroadcast(intent);

            settingsRemote.setSelected(true);
            handler.sendEmptyMessageDelayed(WHAT_SETTING_REMOTE_CANCLE,300);

        }
    }



    class RemoterGestureCallback implements GestureCallback
    {
        @Override
        public void leftSlid(View v) {

            Intent intent_remoter = new Intent(RemoterActivity.this, ProgramsActivity.class);
            startActivity(intent_remoter);
            overridePendingTransition(R.anim.slide_left, R.anim.fade);
        }

        @Override
        public void rightSlid(View v) {
            Intent intent_program = new Intent(RemoterActivity.this, EpgActivity.class);
            startActivity(intent_program);
            overridePendingTransition(R.anim.slide_right, R.anim.fade);
        }

        @Override
        public void click(View v) {

        }
    }
}
