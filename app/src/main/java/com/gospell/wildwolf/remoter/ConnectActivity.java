package com.gospell.wildwolf.remoter;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.gospell.wildwolf.remoter.qr.MainActivity;
import com.gospell.wildwolf.remoter.qr.MipcaActivityCapture;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wildwolf on 2017/5/23.
 */
public class ConnectActivity extends AppCompatActivity {
    private ListView listView;
    private Button connectButton;
    private String selectedIP;
    private int selectedItem = -1;
    private boolean connectFlag = false;
    private BaseAdapter adapter;
    private ConnectReceive connectReceive;
    private ArrayList<HashMap<String,String>> listcontent = new ArrayList<HashMap<String, String>>();
    public static String CONNECT_ACTION ="org.wildwolf.service.CONNECT_RECEIVE";
    public static final int command_add_device = 1;
    public static final int command_update_device = 2;
    public static final int command_delete_device = 3;
    public static final int command_online_device = 4;

    private static final int advertTime = 3;//advertisement setting time is 3 s
    private static final int whatAdvertTime = 0x123;

    private final static int SCANNIN_GREQUEST_CODE = 1;

    private final static int MENU_SCAN = 1;

    private Button btnAdvertClose;
    private Button btnAdvertTime;

    Intent intentNetService;

    Timer advertTimer = new Timer();
    Timer findDeviceTimer = new Timer();


    Handler handler = new Handler(){
        int time = advertTime;
        
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what)
            {
                case whatAdvertTime:
                    btnAdvertTime.setText(time+"s");
                    if(0 == time)
                    {
                        closeAdvertisement();
                    }
                    time --;
                    break;
                default:
                    break;
            }
        }
    };

    private boolean checkDeviceIsExit(String id, String mac, String ip){
        for (int index = 0; index <listcontent.size(); index++) {
            HashMap<String, String> hmap = listcontent.get(index);
            if ((hmap.get(AppProtocol.id).equals(id)) || (hmap.get(AppProtocol.mac).equals(mac)) || (hmap.get(AppProtocol.ip).equals(ip))){
                return true;
            }
        }
        return false;
    }

    public class ConnectReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle data = intent.getExtras();
            if (null != data) {
                int command = data.getInt(getResources().getString(R.string.command));
                switch (command) {
                    case command_add_device:
                        findDeviceTimer.cancel();

                        String id = data.getString(AppProtocol.id);
                        String mac = data.getString(AppProtocol.mac);
                        String ip = data.getString(AppProtocol.ip);

                        if (!checkDeviceIsExit(id, mac, ip)) {
                            //add device item
                            HashMap<String, String> hmap = new HashMap<String, String>();
                            hmap.put(AppProtocol.id, id);
                            hmap.put(AppProtocol.mac, mac);
                            hmap.put(AppProtocol.ip, ip);
                            listcontent.add(hmap);
                            adapter.notifyDataSetChanged();
                        }
                        break;

                    case command_online_device:
                        break;

                    case command_delete_device:
                        break;

                    case command_update_device:
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

        setContentView(R.layout.connect);



        AppManager.getAppManager().addActivity(this);

        connectButton = (Button)findViewById(R.id.connect_button);
        listView =(ListView)findViewById(R.id.connect_list);

        btnAdvertClose = (Button)findViewById(R.id.btnAdvertClose);
        btnAdvertTime = (Button)findViewById(R.id.btnAdvertTime);


        adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return listcontent.size();
            }

            @Override
            public Object getItem(int position) {
                return listcontent.get(position);
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                CheckBox dev = new CheckBox(ConnectActivity.this);
                dev.setTextSize(12);
                dev.setText(AppProtocol.id + ":" + listcontent.get(position).get(AppProtocol.id) + "\n"
                        + AppProtocol.mac + ":" + listcontent.get(position).get(AppProtocol.mac) + "\n"
                        + AppProtocol.ip  + ":" + listcontent.get(position).get(AppProtocol.ip));

                dev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (-1 != selectedItem) {
                            for (int i= 0; i < listView.getCount(); i++) {
                                CheckBox checkBox = (CheckBox)listView.getChildAt(i);
                                if (checkBox.isChecked() && (i != selectedItem)) {

                                    CheckBox preSelectedBox = (CheckBox)listView.getChildAt(selectedItem);
                                    preSelectedBox.setChecked(false);
                                    selectedIP = listcontent.get(i).get(AppProtocol.ip);
                                    selectedItem = i;
                                }
                            }
                        }
                        else {
                            for (int i= 0; i < listView.getCount(); i++) {
                                CheckBox checkBox = (CheckBox) listView.getChildAt(i);
                                if (checkBox.isChecked()) {
                                    selectedIP = listcontent.get(i).get(AppProtocol.ip);
                                    selectedItem = i;
                                }
                            }
                        }
                    }
                });

                return dev;
            }
        };

       /* HashMap<String, String> hmap3 = new HashMap<String, String>();
        hmap3.put(AppProtocol.id, "0x12345600");
        hmap3.put(AppProtocol.mac, "08:00:20:0A:8C:6D");
        hmap3.put(AppProtocol.ip, "192.168.100.105");
        listcontent.add(hmap3);*/

        listView.setAdapter(adapter);

        //register broadcast to receive data from other activity or service
        connectReceive = new ConnectReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(CONNECT_ACTION);
        registerReceiver(connectReceive, filter);

        //start net service
        intentNetService = new Intent(ConnectActivity.this, NetService.class);
        intentNetService.setAction(NetService.SERVICE_ACTION);
        startService(intentNetService);

        //send message to net service to find devices

        findDeviceTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //send message to net service to find devices
                Intent findDeviceIntent = new Intent();
                findDeviceIntent.setAction(NetService.BROADCAST_ACTION);
                findDeviceIntent.putExtra(getResources().getString(R.string.command), NetService.command_find_device);
                sendBroadcast(findDeviceIntent);
                Log.i("test__send","send message to net service to find devices");
            }
        }, 0,3000);

       // Toast.makeText(ConnectActivity.this,R.string.prompt_find_device, Toast.LENGTH_SHORT).show();

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //must select device frist
                if (null == selectedIP) {
                    return;
                }

                //selected device to communication
                Intent intent = new Intent();
                intent.setAction(NetService.BROADCAST_ACTION);
                intent.putExtra(getResources().getString(R.string.command), NetService.command_connect_device);
                intent.putExtra(getResources().getString(R.string.ip), selectedIP);
                sendBroadcast(intent);

                //start remoter activity
                Intent intentRemoter = new Intent(ConnectActivity.this, RemoterActivity.class);
                startActivity(intentRemoter);
            }
        });

        btnAdvertClose.setOnClickListener(new ClickCloseAdvertisement());

        //使用定时器,每隔1秒让handler发送一个空信息
        advertTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0x123);

            }
        }, 0,1000);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().finishActivity(this);

        Log.i(LogTag.HEARTBEAT,"stopService");
        stopService(intentNetService);
        unregisterReceiver(connectReceive);

    }


    class ClickCloseAdvertisement implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            closeAdvertisement();
        }
    }

    void closeAdvertisement()
    {
        View dvertisemenFarme = (View)findViewById(R.id.advertisementFrame);
        dvertisemenFarme.setVisibility(View.GONE);
        advertTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras();

                    Log.i("result:",bundle.getString("result"));
                    //selected device to communication
                    Intent intent = new Intent();
                    intent.setAction(NetService.BROADCAST_ACTION);
                    intent.putExtra(getResources().getString(R.string.command), NetService.command_connect_device);
                    intent.putExtra(getResources().getString(R.string.ip), bundle.getString("result"));
                    sendBroadcast(intent);

                    //start remoter activity
                    Intent intentRemoter = new Intent(ConnectActivity.this, RemoterActivity.class);
                    startActivity(intentRemoter);

                    //显示扫描到的内容
                   // mTextView.setText(bundle.getString("result"));
                    //显示
                    //mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));
                }
                break;
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //运行时，参数Menu其实就是MenuBuilder对象
        Log.d("MainActivity", "menu--->" + menu);

        /*利用反射机制调用MenuBuilder的setOptionalIconsVisible方法设置mOptionalIconsVisible为true，
         * 给菜单设置图标时才可见
         */
        //setIconEnable(menu, true);

        MenuItem item1 = menu.add(0, MENU_SCAN, 0, "scan");
        //item1.setIcon(R.drawable.ic_scan);


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        // TODO Auto-generated method stub
        return super.onPrepareOptionsMenu(menu);
    }

    //enable为true时，菜单添加图标有效，enable为false时无效。4.0系统默认无效
    private void setIconEnable(Menu menu, boolean enable)
    {
        try
        {
            Class<?> clazz = Class.forName("com.android.internal.view.menu.MenuBuilder");
            Method m = clazz.getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            m.setAccessible(true);

            //MenuBuilder实现Menu接口，创建菜单时，传进来的menu其实就是MenuBuilder对象(java的多态特征)
            m.invoke(menu, enable);

        } catch (Exception e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case MENU_SCAN:

                //点击按钮跳转到二维码扫描界面，这里用的是startActivityForResult跳转
                //扫描完了之后调到该界面

                if (ContextCompat.checkSelfPermission(ConnectActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ConnectActivity.this,new String[]{Manifest.permission.CAMERA},1);
                }else {
                    Intent intent = new Intent();
                    intent.setClass(ConnectActivity.this,com.gospell.wildwolf.remoter.qr.MipcaActivityCapture.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(intent, SCANNIN_GREQUEST_CODE);
                    //startActivityForResult(new Intent(MainActivity.this, CaptureActivity.class),0);
                }

                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}