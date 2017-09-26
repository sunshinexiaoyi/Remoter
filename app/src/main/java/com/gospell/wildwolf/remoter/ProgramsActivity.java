package com.gospell.wildwolf.remoter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import com.example.*;

/**
 * Created by Administrator on 2017/5/22.
 */
public class ProgramsActivity extends Activity {

    private ListView listView;
    private String lcn;
    private String programeName;
    private String serviceId;
    private String proIndex;
    private programAdapter myAdapter = null;
    private ProgramsReceive programReceive;
    public static String PROGRAM_ACTION ="org.wildwolf.service.PROGRAM_RECEIVE";
    private  int progNumber = 0;
    private  String progArray[];
    private  String serviceArray[];
    private  String lcnArray[];
    private String indexArray[];

    private List<Map<String, Object>> listItem;
    public static final int command_add_program = 1;
    public static final int command_delete_program = 2;
    public static final int command_update_program = 3;
    private LinearLayout layoutEpg;
    private LinearLayout layoutMic;
    private GestureManager gestureManager = new GestureManager(new ProgramTouchCallback());

    private int curProPosition = 0;

    private TimeControl timeControl;

    public class ProgramsReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            if (null != data) {
                int command = data.getInt(getResources().getString(R.string.command));
                switch (command) {
                    case command_add_program:
                        AppProgramsInfo programsInfo = (AppProgramsInfo) data.getSerializable(getResources().getString(R.string.programs));
                        if (null != programsInfo) {
                            Toast.makeText(ProgramsActivity.this,R.string.prompt_update_program_list, Toast.LENGTH_SHORT).show();

                            int programNumber = programsInfo.getProgramNumber();
                            progNumber = programNumber;
                            progArray = new String[programNumber];
                            serviceArray = new String[programNumber];
                            lcnArray = new String[programNumber];
                            indexArray = new String[programNumber];
                            for (int index = 0; index < programNumber; index++) {
                                lcnArray[index] = programsInfo.getProgramLCN(index);
                                progArray[index] = programsInfo.getProgramName(index);
                                serviceArray[index] = programsInfo.getProgramServiceID(index);
                                indexArray[index] = programsInfo.getProgramIndex(index);
                                //TODO
                            }
                            setProgramListContent();
                            myAdapter.setnotifyDataSetChanged(listItem);
                        }
                        break;

                    case command_delete_program:
                        break;

                    case command_update_program:
                        break;

                    default:
                        break;
                }
            }
        }
    }

    private List<Map<String, Object>> setProgramListContent()
    {
        int num = progNumber;
        listItem = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < num; i++)
        {
            Map<String, Object> lineItem = new HashMap<String, Object>();
            lineItem.put(lcn, lcnArray[i]);
            lineItem.put(programeName, progArray[i]);
            lineItem.put(serviceId, serviceArray[i]);
            lineItem.put(proIndex,  indexArray[i]);

            listItem.add(lineItem);
        }
        return listItem;
    }

    private void setScheduleView() {
        myAdapter = new programAdapter(getApplicationContext(),listItem);
        listView.setAdapter(myAdapter);
    }

    private AdapterView.OnItemClickListener itemclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

            HashMap<String,String> map = (HashMap<String,String>)listView.getItemAtPosition(position);
            String proIndexValue = map.get(proIndex);
            String proName = map.get(programeName);
            curProPosition = position;

            listView.smoothScrollToPosition(position);
            myAdapter.curPosition = curProPosition;
            myAdapter.notifyDataSetChanged();

            Toast.makeText(ProgramsActivity.this,getResources().getString(R.string.prompt_switch_program)
                    +":"+proName, Toast.LENGTH_SHORT).show();

            if (null != proIndexValue) {
                int intProIndex = Integer.parseInt(proIndexValue);
                Intent intent = new Intent();
                intent.setAction(NetService.BROADCAST_ACTION);
                intent.putExtra(getResources().getString(R.string.command), NetService.command_switch_programs);
                intent.putExtra(getResources().getString(R.string.pro_index), intProIndex);
                sendBroadcast(intent);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.programs);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉状态栏

        AppManager.getAppManager().addActivity(this);

        layoutMic = (LinearLayout)findViewById(R.id.linear_mic2);
        layoutMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_romote = new Intent(ProgramsActivity.this, RemoterActivity.class);
                startActivity(intent_romote);
            }
        });
        layoutEpg = (LinearLayout)findViewById(R.id.linear_epg2);
        layoutEpg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_epg = new Intent(ProgramsActivity.this, EpgActivity.class);
                startActivity(intent_epg);
            }
        });

        //init time control ,about switch program interval time
        timeControl = new TimeControl(getResources().getInteger(R.integer.switch_pro_interval));


        
        listView = (ListView) findViewById(R.id.prog_list);
        lcn = getResources().getString(R.string.pro_lcn);
        programeName = getResources().getString(R.string.pro_name);
        serviceId = getResources().getString(R.string.pro_service_id);
        proIndex = getResources().getString(R.string.pro_index);

        listView.setOnTouchListener(gestureManager);

        setProgramListContent();
        setScheduleView();
        listView.setOnItemClickListener(itemclick);

        //register broadcast to receive data
        programReceive = new ProgramsReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PROGRAM_ACTION);
        registerReceiver(programReceive, filter);

        //create program activity and get programs information
        Intent intent = new Intent();
        intent.setAction(NetService.BROADCAST_ACTION);
        intent.putExtra(getResources().getString(R.string.command), NetService.command_get_programs);
        sendBroadcast(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        unregisterReceiver(programReceive);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if((KeyEvent.KEYCODE_VOLUME_DOWN == keyCode)||(KeyEvent.KEYCODE_VOLUME_UP == keyCode))
        {
            if(timeControl.isOverTime()) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        if ((listView.getCount() - 1) == curProPosition) {
                            curProPosition = 0;
                        } else {
                            curProPosition++;
                        }
                        break;
                    case KeyEvent.KEYCODE_VOLUME_UP:
                        if (0 == curProPosition) {
                            curProPosition = listView.getCount() - 1;
                        } else {
                            curProPosition--;
                        }
                        break;
                }

                clickListItem(R.id.prog_list, curProPosition);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * @param source 触发的ListView id
     * @param index 点击哪行 from 0 start
     */
    public void clickListItem(int source,int index)
    {
        ListView listView =  (ListView) findViewById(source);

        Log.i("test_epg","getcount:"+listView.getCount());
        if ((index +1) <= listView.getCount())// must set index < amount count
        {
            //模拟点击事件
            listView.performItemClick(listView.getAdapter().getView(index, null, null),
                    index,
                    listView.getItemIdAtPosition(index));
        }


    }

    class ProgramTouchCallback implements GestureCallback
    {
        @Override
        public void leftSlid(View v) {
            Intent intent_remoter = new Intent(ProgramsActivity.this, EpgActivity.class);
            startActivity(intent_remoter);
            overridePendingTransition(R.anim.slide_left, R.anim.fade);
        }

        @Override
        public void rightSlid(View v) {


            Intent intent_program = new Intent(ProgramsActivity.this, RemoterActivity.class);
            startActivity(intent_program);
            overridePendingTransition(R.anim.slide_right, R.anim.fade);
        }

        @Override
        public void click(View v) {

        }
    }
}


