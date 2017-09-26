package com.gospell.wildwolf.remoter;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.app.AppCompatActivity;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/5/22.
 */
public class EpgActivity extends Activity {

    ListView proListView;
    ListView scheduleView;
    private LinearLayout layoutProglist;
    private LinearLayout layoutMic;

    ArrayList<HashMap<String, String>> programList = new ArrayList<>();
    ArrayList<HashMap<String, Object>>  dateList = new  ArrayList<>();
    ArrayList<HashMap<String, String>> scheduleList = new ArrayList<>() ;

    SimpleAdapter proAdapter = null;
    SimpleAdapter dateAdapter = null;
    EpgTimeAdapter epgTimeAdapter = null;

    String curProIndex = null; //记录epg返回的节目index

    AlertDialog dialog = null; //egp 弹出框
    ShowTypeEnum type;    //listView显示的类型:proType timeType

    GestureManager GestureManager = new GestureManager(new epgTouchCallback());
    int selectProgramIndex = -1;


    private EpgActivity.EpgReceive epgReceive = null;
    public static String EPG_ACTION ="org.wildwolf.service.EPG_RECEIVE";
    public static final int command_add_new_epg = 1;
    public static final int command_update_epg = 2;
    public static final int command_delete_epg = 3;

    public static final int command_add_programs_list_epg = 4;

    public static final int command_epg_reserve_clash = 5;


    //program type
    public static final int program_type_lcn = 0;
    public static final int program_type_service_id = 1;
    public static final int program_type_index = 2;


    public static int num = 0;
    public static final int[] epgCommand = new int[]{command_add_new_epg,command_delete_epg};



    /**
     * click program listener event
     */
    class ClickProgramListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            initProView(programList);
            hideMainFrame();
        }
    }

    /**
     *click date listener event
     */
    class ClickDateListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            initDateView(dateList);
            hideMainFrame();
        }
    }

    public class EpgReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            if (null != data) {
                int command = data.getInt(getResources().getString(R.string.command));
                Log.i("test__epg","recv command:"+command);
                switch (command) {
                    case command_add_new_epg:
                        AppEpgInfo recvEpgInfo = (AppEpgInfo) data.getSerializable(getResources().getString(R.string.epg));
                        if (null != recvEpgInfo) {
                            int programNumber = recvEpgInfo.getProgramNumber();
                            Log.i("test__epg","programNumber:"+programNumber);
                            HashMap<String, Object> program = recvEpgInfo.getEpgInfo(0);
                            if(null != program)
                            {
                                //Toast.makeText(EpgActivity.this,R.string.prompt_update_epg_info, Toast.LENGTH_SHORT).show();
                                ArrayList<HashMap<String, Object>>  getDateList =  recvEpgInfo.getEpgDateList(program);
                                curProIndex = recvEpgInfo.getEpgIndex(program);

                                if(null != getDateList)
                                {
                                    dateList.clear();
                                    dateList.addAll(getDateList);
                                    if(initDateView(dateList))
                                    {
                                        clickListItem(R.id.programView,0);
                                    }
                                }
                            }

                        }
                        break;

                    case command_delete_epg:
                        clearEpgView();
                        break;

                    case command_update_epg:
                        getPrograms();
                        break;
                    case command_add_programs_list_epg:
                        AppProgramsInfo programsInfo = (AppProgramsInfo) data.getSerializable(getResources().getString(R.string.programs));
                        if (null != programsInfo) {
                            ArrayList<HashMap<String, String>> getProgramList = programsInfo.getProgramList();
                            if(null != getProgramList)
                            {
                                //Toast.makeText(EpgActivity.this,R.string.prompt_update_program_list, Toast.LENGTH_SHORT).show();

                                programList.clear();
                                programList.addAll(getProgramList);
                                if(initProView(programList))
                                {
                                    clickListItem(R.id.programView,0);
                                }
                            }

                        }
                        break;

                    case command_epg_reserve_clash:
                       // Toast.makeText(EpgActivity.this, "command_epg_reserve_clash", Toast.LENGTH_SHORT).show();
                        AppReserveClashEvent eventInfo = (AppReserveClashEvent) data.getSerializable(getResources().getString(R.string.epg_reserve_event_info));
                        if(null != eventInfo)
                        {
                            new ClashDialog(eventInfo);

                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppManager.getAppManager().addActivity(this);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.epg);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
               WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉状态栏

        layoutMic = (LinearLayout)findViewById(R.id.linear_mic3);
        layoutMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_romote = new Intent(EpgActivity.this, RemoterActivity.class);
                startActivity(intent_romote);
            }
        });

        layoutProglist = (LinearLayout)findViewById(R.id.linear_proglist3);
        layoutProglist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_romote = new Intent(EpgActivity.this, ProgramsActivity.class);
                startActivity(intent_romote);
            }
        });

        listenerInit();

        listenerInit();


        //set recv
        epgReceive = new EpgReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(EPG_ACTION);
        registerReceiver(epgReceive, filter);

        //create epg activity and get epg information
        getPrograms();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);
        unregisterReceiver(epgReceive);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 初始化时间列表
     */
    private void initScheduleView(ListView listView) {

        epgTimeAdapter = new EpgTimeAdapter(getApplicationContext());
        epgTimeAdapter.setMenuCallback(new SendReserveSet());

        listView.setAdapter(epgTimeAdapter);
        //epgTimeAdapter.setNotifyDataSetChanged(testGetTimeList());


        Log.i("reserve","getCount:"+epgTimeAdapter.getCount());
    }

    private ArrayList<HashMap<String,String>> testGetTimeList()
    {
        ArrayList<HashMap<String,String>>  timeList = new ArrayList<>();
        for(int i = 0; i < 5;i++)
        {
            HashMap<String,String> item = new HashMap<>();
            item.put(AppEpgInfo.date,"6-26");
            item.put(AppEpgInfo.time,"9-10");
            item.put(AppEpgInfo.event,"bbc");
            item.put(AppEpgInfo.bookState,"0");
            item.put(AppEpgInfo.eventID,"22");
            item.put(AppEpgInfo.shortDescriptor,"shortDescriptor");

            timeList.add(item);
        }

        return timeList;
    }

    /**
     * 初始化监听设置
     */
    protected void listenerInit()
    {
        scheduleView = (ListView)findViewById(R.id.scheduleView);

        /*监听手势*/
        scheduleView.setOnTouchListener(GestureManager);

        initScheduleView(scheduleView);
        scheduleView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("setOnItemClickListener","setOnItemClickListener");
                if(null != dialog)
                {
                    dialog.dismiss();
                    dialog = null;
                }

                HashMap<String,String> lineData = scheduleList.get(position);
                LinearLayout epgDetailForm = (LinearLayout)viewBingDataByMap(lineData,
                        R.layout.epg_dialog,
                        new String[]{ AppEpgInfo.name,AppEpgInfo.date, AppEpgInfo.startTime,AppEpgInfo.endTime,
                                AppEpgInfo.event,AppEpgInfo.shortDescriptor },
                        new int[]{R.id.dialogProName,R.id.dialogDate,R.id.dialogStartTime,R.id.dialogEndTime,R.id.dialogEventName,R.id.dialogShortDescriptor});

                ImageButton closeBtn = (ImageButton)epgDetailForm.findViewById(R.id.dialogClose);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        dialog = null;

                    }
                });

                AlertDialog.Builder builder  = new AlertDialog.Builder(EpgActivity.this,R.style.AlertDialogCustom);
                builder.setView(epgDetailForm).create();

                dialog = builder.show();

            }
        });


        /*监听节目,日期项的点击*/
        proListView =  (ListView) findViewById(R.id.programView);
        proListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //根据类型触发不同事件
                //case后面的枚举常量名只能使用unqualified name，switch后已经指定了枚举的类型，case后无需使用全名，而且enum也不存在继承关系
                switch (type)
                {
                    case PROGRAM:
                        clearAboutDateView();

                        TextView proText = (TextView)findViewById(R.id.programName);
                        String name = programList.get(position).get(AppProgramsInfo.name);
                        if(null !=name)
                        {
                            proText.setText(programList.get(position).get(AppProgramsInfo.name));
                            setTextMarquee(proText);//设置内容超出时，跑马灯效果
                        }

                        selectProgramIndex = Integer.parseInt(programList.get(position).get(AppProgramsInfo.proIndex)) ;
                        getSwitchEpgInfoByIndex(selectProgramIndex);

                        break;
                    case DATE:
                        TextView timeText = (TextView)findViewById(R.id.timeText);
                        timeText.setText((String)dateList.get(position).get(AppEpgInfo.date));
                        setTextMarquee(timeText);//设置内容超出时，跑马灯效果

                        setScheduleView(position);

                        break;
                    default:
                        break;

                }

                showMainFrame();
            }
        });

        /*监听节目按钮,显示节目列表*/
        ImageButton proBtn = (ImageButton)findViewById(R.id.programBtn);
        proBtn.setOnClickListener(new ClickProgramListener());

        TextView programName = (TextView)findViewById(R.id.programName);
        programName.setOnClickListener(new ClickProgramListener());

        /*监听时间按钮,显示节目列表*/
        ImageButton timeBtn = (ImageButton)findViewById(R.id.timeBtn);
        timeBtn.setOnClickListener(new ClickDateListener());

        TextView timeText = (TextView)findViewById(R.id.timeText);
        timeText.setOnClickListener(new ClickDateListener());


        /*监听退出按钮,隐藏节目列表*/
        ImageButton exitProBtn = (ImageButton)findViewById(R.id.programViewExit);
        exitProBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainFrame();
            }
        });

    }

    // send Broadcast
    public void getSwitchEpgInfoByIndex(int value)
    {
        getSwitchEpgInfo(program_type_index,value);
    }

    public void getSwitchEpgInfo(int type,int value)
    {
        Log.i("epg___getEpgInfo","getSwitchEpgInfo");
        Intent intent = new Intent();
        intent.setAction(NetService.BROADCAST_ACTION);
        intent.putExtra(getResources().getString(R.string.pro_type),type);
        intent.putExtra(getResources().getString(R.string.pro_value),value);
        intent.putExtra(getResources().getString(R.string.command),NetService.command_switch_epg);
        sendBroadcast(intent);
    }

    public void getPrograms()
    {
        Log.i("epg___getEpgInfo","getPrograms");
        sendBroadcastCommand(NetService.command_get_programs);
    }

    public void sendBroadcastCommand(int command)
    {
        Intent intent = new Intent();
        intent.setAction(NetService.BROADCAST_ACTION);
        intent.putExtra(getResources().getString(R.string.command),command);
        sendBroadcast(intent);
    }


    /**
     * @param source 触发的ListView id
     * @param index 点击哪行 from 0 start
     */
    public void clickListItem(int source,int index)
    {
        ListView listView =  (ListView) findViewById(source);

        //Log.i("test_epg","getcount:"+listView.getCount());
        if ((index +1) <= listView.getCount())// must set index < amount count
        {
            //模拟点击事件
            listView.performItemClick(listView.getAdapter().getView(index, null, null),
                    index,
                    listView.getItemIdAtPosition(index));
        }


    }

    public void hideMainFrame()
    {
        ViewGroup main = (ViewGroup) findViewById(R.id.contentFrame);
        ViewGroup proFrame = (ViewGroup) findViewById(R.id.programFrame);

        main.setVisibility(View.GONE);
        proFrame.setVisibility(View.VISIBLE);

    }

    public void showMainFrame()
    {
        ViewGroup main = (ViewGroup) findViewById(R.id.contentFrame);
        ViewGroup proFrame = (ViewGroup) findViewById(R.id.programFrame);

        main.setVisibility(View.VISIBLE);
        proFrame.setVisibility(View.GONE);
    }

    public void clearEpgView()
    {
        proListView.setAdapter(null);

        scheduleView.setAdapter(null);
        dateList.clear();
        programList.clear();

        TextView proText = (TextView)findViewById(R.id.programName);
        proText.setText("");

        TextView timeText = (TextView)findViewById(R.id.timeText);
        timeText.setText("");
    }

    public void clearAboutDateView()
    {

        scheduleList.clear();
        epgTimeAdapter.setNotifyDataSetChanged(scheduleList);

        dateList.clear();

        TextView proText = (TextView)findViewById(R.id.programName);
        proText.setText("");

        TextView timeText = (TextView)findViewById(R.id.timeText);
        timeText.setText("");
    }


    public boolean initProView(ArrayList<HashMap<String, String>> programList)
    {
        /*Toast.makeText(EpgActivity.this, "更新节目列表："+programList.size(),
                Toast.LENGTH_SHORT).show();*/
        type = ShowTypeEnum.PROGRAM;
        TextView title = (TextView)findViewById(R.id.showTitle);
        title.setText(R.string.epg_program_title);

        if( null != programList )
        {
            if(null == proAdapter)
            {

                proAdapter  = new SimpleAdapter(this,
                        programList,
                        R.layout.my_simple_list_item,
                        new String[]{AppProgramsInfo.name},new int[]{android.R.id.text1});

                proListView.setAdapter(proAdapter);
            }
            else
            {
                proListView.setAdapter(proAdapter);
               // proAdapter.notifyDataSetChanged();
            }
            return true;
        }

        return false;
    }


    public boolean initDateView(ArrayList<HashMap<String, Object>> dateList)
    {
        type = ShowTypeEnum.DATE;

        TextView title = (TextView)findViewById(R.id.showTitle);
        title.setText(R.string.epg_time_title);

        if( null != dateList)
        {
            if(null == dateAdapter)
            {
                dateAdapter  = new SimpleAdapter(this,
                        dateList,
                        R.layout.my_simple_list_item,
                        new String[]{AppEpgInfo.date},new int[]{android.R.id.text1});
                proListView.setAdapter(dateAdapter);
            }
            else
            {
               // dateAdapter.notifyDataSetChanged();
                proListView.setAdapter(dateAdapter);
            }

            return true;
        }
        return false;
    }


    public void setScheduleView(int position)
    {
        scheduleList = getScheduleDate(position);
        Log.i("reserve",""+scheduleList.size());

        epgTimeAdapter.setNotifyDataSetChanged(scheduleList);

    }

    private ArrayList<HashMap<String, String>> getScheduleDate(int position)
    {
        ArrayList<HashMap<String, String>> timeList = null;

        if(null != dateList) {
            HashMap<String, Object> dayInfo = dateList.get(position);

            String date =(String)dayInfo.get(AppEpgInfo.date);

            String name = ((TextView)findViewById(R.id.programName)).getText().toString();

            timeList = (ArrayList<HashMap<String, String>>) dayInfo.get(AppEpgInfo.timeList);
            for (int i = 0; i < timeList .size(); i++) {

                timeList.get(i).put(AppEpgInfo.date, date);
                timeList.get(i).put(AppEpgInfo.name, name);

                timeList.get(i).put(AppEpgInfo.index, curProIndex);

                //Log.i("reserve",""+timeList.get(i).get(AppEpgInfo.time));
            }

        }
        return timeList;
    }


    /**
     * @param data  seeting Map
     * @param from  setting Map key
     * @param to    setting view id
     */
    private View viewBingDataByMap(Map<String,?>data, @LayoutRes int resource, String[] from, int[] to)
    {
        View epgDetailForm = (LinearLayout)getLayoutInflater().inflate(resource,null);

        for(int i = 0 ; i<from.length ; i++)
        {
            TextView textView = (TextView)epgDetailForm.findViewById(to[i]);
            if(null != textView)
            {
               // Log.i(from[i],data.get(from[i]).toString());
               textView.setText(data.get(from[i]).toString());
            }
            else
            {
                Log.e("error","null == textView,"+from[i].toString()+" not set");
            }

        }
        return epgDetailForm;
    }


    /**
     * 设置textView跑马灯效果
     * @param textView
     */
    public static void setTextMarquee(TextView textView) {
        if (textView != null) {
            textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            textView.setSingleLine(true);
            textView.setSelected(true);
            textView.setFocusable(true);
            textView.setFocusableInTouchMode(true);
        }
    }

    class epgTouchCallback implements GestureCallback
    {
        @Override
        public void leftSlid(View v) {


            Intent intent_remoter = new Intent(EpgActivity.this, RemoterActivity.class);
            startActivity(intent_remoter);
            overridePendingTransition(R.anim.slide_left, R.anim.fade);
        }

        @Override
        public void rightSlid(View v) {
            Intent intent_program = new Intent(EpgActivity.this, ProgramsActivity.class);
            startActivity(intent_program);
            overridePendingTransition(R.anim.slide_right, R.anim.fade);
        }

        @Override
        public void click(View v) {

        }
    }

    class SendReserveSet implements ReserveSetCallback
    {
        @Override
        public void sendSetInfo(HashMap<String, String> setInfo) {
            Intent intent = new Intent();
            intent.setAction(NetService.BROADCAST_ACTION);
            intent.putExtra(getResources().getString(R.string.command),NetService.command_epg_reserve_set);

            Bundle bundle = new Bundle();
            bundle.putString(getResources().getString(R.string.epg_reserve_event_type),setInfo.get(AppEpgInfo.bookState));
            bundle.putString(getResources().getString(R.string.epg_reserve_event_id),setInfo.get(AppEpgInfo.eventID));
            bundle.putString(getResources().getString(R.string.epg_reserve_program_index),setInfo.get(AppEpgInfo.index));

            intent.putExtras(bundle);
            sendBroadcast(intent);

            try{
                Thread.sleep(200);
                if(-1 != selectProgramIndex)
                {
                    getSwitchEpgInfoByIndex(selectProgramIndex);
                }

            }catch(InterruptedException e){
                e.printStackTrace();}

            //Toast.makeText(EpgActivity.this, "eventType:"+setInfo.get(AppEpgInfo.bookState)+"\neventId:"+setInfo.get(AppEpgInfo.eventID)+"\nprogramIndex:"+setInfo.get(AppEpgInfo.index), Toast.LENGTH_SHORT).show();
        }
    }


    class ClashDialog {

        //dialog
        AlertDialog alert = null;
        AlertDialog.Builder builder = null;

        int select = 0;

        AppReserveClashEvent eventInfo;

        ClashDialog(AppReserveClashEvent eventInfo) {
            this.eventInfo = eventInfo;
            String conflictEvent = eventInfo.conflictEvent.get(AppReserveClashEvent.eventName) + "\n" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.Date) + "\n" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.startTime) + "->" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.endTime);

            String selectEvent = eventInfo.selectEvent.get(AppReserveClashEvent.eventName) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.Date) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.startTime) + "->" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.endTime);
            final String[] fruits = new String[]{conflictEvent, selectEvent};


            alert = null;
            builder = new AlertDialog.Builder(EpgActivity.this);
            alert = builder.setIcon(R.mipmap.ic_launcher)
                    .setTitle(getResources().getString(R.string.epg_clash_dialog_title))
                    .setSingleChoiceItems(fruits, 0, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            select = which;
                            //Toast.makeText(getApplicationContext(), "你选择了" + fruits[which], Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton(getResources().getString(R.string.epg_clash_dialog_cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton(getResources().getString(R.string.epg_clash_dialog_confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (1 == select) {
                                sendClashInfo();
                            }
                            //Toast.makeText(getApplicationContext(), "你选择了" + select, Toast.LENGTH_SHORT).show();
                        }
                    }).create();
            alert.show();
        }


        void sendClashInfo() {
            Intent intent = new Intent();
            intent.setAction(NetService.BROADCAST_ACTION);
            intent.putExtra(getResources().getString(R.string.command), NetService.command_epg_reserve_clash);

            Bundle bundle = new Bundle();
            bundle.putString(getResources().getString(R.string.epg_reserve_event_type), eventInfo.selectEvent.get(AppReserveClashEvent.BookType));
            bundle.putString(getResources().getString(R.string.epg_reserve_event_id), eventInfo.selectEvent.get(AppReserveClashEvent.eventID));
            bundle.putString(getResources().getString(R.string.epg_reserve_program_index), eventInfo.selectEvent.get(AppReserveClashEvent.programIndex));
            bundle.putString(getResources().getString(R.string.epg_reserve_job_index), eventInfo.conflictEvent.get(AppReserveClashEvent.JobIndex));
            String selectEvent = eventInfo.selectEvent.get(AppReserveClashEvent.BookType) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.eventID) + "\n" +
                    eventInfo.selectEvent.get(AppReserveClashEvent.programIndex) + "->" +
                    eventInfo.conflictEvent.get(AppReserveClashEvent.JobIndex);
            Log.i("ss",selectEvent);
            intent.putExtras(bundle);
            sendBroadcast(intent);

        }
    }
}


/**
 *  enum type
 *  click icon type program or date
 */
enum ShowTypeEnum
{
    PROGRAM,
    DATE,
    RESERVE
}



