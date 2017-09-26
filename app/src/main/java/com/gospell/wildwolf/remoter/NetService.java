package com.gospell.wildwolf.remoter;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by wildwolf on 2017/5/24.
 */
public class NetService extends Service {
    private UdpUnicastSocket netSender = null;
    private UdpUnicastSocket netReceive = null;
    private Thread receiveThread;
    private String localIP;
    private String deviceIP;
    private NetReceive broadcastReceive;
    public static String BROADCAST_ACTION ="org.wildwolf.service.NET_RECEIVER";
    public static String SERVICE_ACTION ="org.wildwolf.service.NET_SERVICE";
    public static final int command_send_remoterKey = 1;
    public static final int command_switch_epg = 2;
    public static final int command_switch_programs = 3;
    public static final int command_get_programs = 4;
    public static final int command_find_device = 5;
    public static final int command_connect_device = 6;

    public static final int command_epg_reserve_set = 7;
    public static final int command_epg_reserve_clash = 8;

    SlaveNetTask slaveNetTask ;

    HeartbeatPacket heartbeatPacket = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class NetReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle data = intent.getExtras();
            Message msg = new Message();
            msg.setData(data);

            slaveNetTask.handler.sendMessage(msg);
        }
    }

    class SlaveNetTask extends Thread{
        public Handler handler;

        @Override
        public void run() {
            super.run();

            Looper.prepare();
            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);

                    Bundle data = msg.getData();
                    int command = data.getInt(getResources().getString(R.string.command));
                    switch (command) {
                        case command_send_remoterKey:
                            int keyValue = data.getInt(getResources().getString(R.string.key_value));
                            if (null != netSender) {
                                netSender.send(AppProtocol.sendRemoterKey(keyValue));
                            }
                            break;

                        case command_switch_programs:
                            //pro_index
                            int proIndex = data.getInt(getResources().getString(R.string.pro_index));
                            if (null != netSender) {
                                netSender.send(AppProtocol.switchProgramIndex(proIndex));
                            }

                            //service id
                            //int serviceID = data.getInt(getResources().getString(R.string.pro_service_id));
                            //if (null != netSender) {
                            //    netSender.send(AppProtocol.switchProgramByLCN(serviceID));
                            //}

                            break;

                        case command_find_device:
                            if (null == localIP)
                            {
                                Log.i(LogTag.HEARTBEAT,"initNet");
                                initNet();
                            }

                            UdpUnicastSocket broadSender = new UdpUnicastSocket("255.255.255.255", AppProtocol.sendPort, SocketType.SEND);
                            if (null != broadSender) {
                                broadSender.send(AppProtocol.findDevice());
                            }
                            break;

                        case command_connect_device:
                            deviceIP = data.getString(getResources().getString(R.string.ip));
                            if (null != deviceIP) {
                                netSender = new UdpUnicastSocket(deviceIP, AppProtocol.sendPort, SocketType.SEND);
                                if (null != netSender) {
                                    netSender.send(AppProtocol.connectDevice());
                                }
                            }
                            break;

                        case command_switch_epg:
                            if (null != netSender) {
                                //pro_index
                                int type = data.getInt(getResources().getString(R.string.pro_type));
                                int value = data.getInt(getResources().getString(R.string.pro_value));

                                netSender.send(AppProtocol.getSwitchEpgInfo(type,value));
                            }
                            break;

                        case command_get_programs:
                            if (null != netSender) {
                                netSender.send(AppProtocol.getProgramInfo());
                            }
                            break;

                        case command_epg_reserve_set:
                            if(null != netSender){
                                try{
                                    int eventType = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_event_type)));
                                    int eventId = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_event_id)));
                                    int programIndex = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_program_index)));
                                    Log.i("reserve","eventType:"+eventType+"\neventId:"+eventId+"\nprogramIndex:"+programIndex);
                                    netSender.send(AppProtocol.getEpgReserveSetInfo(eventType,programIndex,eventId));
                                }catch (NumberFormatException e){e.printStackTrace();}
                        }
                        break;

                        case command_epg_reserve_clash:
                            if(null != netSender){
                                try {
                                    int eventType = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_event_type)));
                                    int eventId = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_event_id)));
                                    int programIndex = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_program_index)));
                                    int jobIndex = Integer.parseInt(data.getString(getResources().getString(R.string.epg_reserve_job_index)));
                                    netSender.send(AppProtocol.getEpgReserveClashInfo(eventType, programIndex,eventId,  jobIndex));
                                }catch (NumberFormatException e){e.printStackTrace();}
                            }
                            break;

                        default:
                            break;
                    }

                    try{
                        Thread.sleep(100);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            };
            Looper.loop();
        }
    }

    private String getLocalIP() {
        WifiManager wifiMng = (WifiManager)(getApplicationContext().getSystemService(Context.WIFI_SERVICE));
        WifiInfo wifiInfor = wifiMng.getConnectionInfo();
        int ipAddress = wifiInfor.getIpAddress();

        if (0 == ipAddress) {
            return null;
        }

        return  ((ipAddress & 0xff)+"."+(ipAddress>>8 & 0xff)+"."
                +(ipAddress>>16 & 0xff)+"."+(ipAddress>>24 & 0xff));
    }

    private void initNet()
    {
        //get wifi IP address
        localIP = getLocalIP();

        if (null != localIP) {
            //create receive socket
            netReceive = new UdpUnicastSocket(localIP, AppProtocol.receivePort, SocketType.RECEIVE);

            //create thread to receive devices message and analysis message to distribute to activity
            receiveThread = new Thread() {
                @Override
                public void run() {
                    while (true) {

                        // lock.acquire();
                        byte data[] = netReceive.receive();
                        //lock.release();
                        Log.i("test_recv",String.format("command:%d",data[0]));
                        if (null != data) {
                            switch (data[0]) {
                                case AppProtocol.command_add_device:
                                    HashMap<String, String> hashMap = AppProtocol.receiveDeviceInfo(data);
                                    if (null != hashMap) {
                                        Intent intent = new Intent();
                                        intent.setAction(ConnectActivity.CONNECT_ACTION);
                                        intent.putExtra(getResources().getString(R.string.command), ConnectActivity.command_add_device);
                                        intent.putExtra(AppProtocol.id,  hashMap.get(AppProtocol.id));
                                        intent.putExtra(AppProtocol.mac, hashMap.get(AppProtocol.mac));
                                        intent.putExtra(AppProtocol.ip,  hashMap.get(AppProtocol.ip));
                                        sendBroadcast(intent);
                                    }
                                    break;

                                case AppProtocol.command_receive_programs_list:

                                    AppProgramsInfo programList = AppProtocol.receiveProgramsInfo(data);

                                    if (null != programList) {
                                        Intent intent = new Intent();
                                        intent.setAction(ProgramsActivity.PROGRAM_ACTION);
                                        Bundle programsListData = new Bundle();
                                        programsListData.putSerializable(getResources().getString(R.string.programs), programList);
                                        programsListData.putInt(getResources().getString(R.string.command), ProgramsActivity.command_add_program);
                                        intent.putExtras(programsListData);
                                        sendBroadcast(intent);

                                        intent = new Intent();
                                        intent.setAction(EpgActivity.EPG_ACTION);
                                        Bundle epgData = new Bundle();
                                        epgData.putInt("dataLen",programList.dataLen);
                                        epgData.putInt("proNum",programList.proNum);
                                        epgData.putSerializable(getResources().getString(R.string.programs), programList);
                                        epgData.putInt(getResources().getString(R.string.command),EpgActivity.command_add_programs_list_epg);// EpgActivity.);
                                        intent.putExtras(epgData);
                                        sendBroadcast(intent);
                                        Log.i("test__epg","send command_add_programs_list_epg");

                                        if (null != netSender) {
                                            byte respondData[] = new byte[2];
                                            respondData[0] = AppProtocol.command_receive_programs_list;
                                            respondData[1] = 0;
                                            netSender.send(AppProtocol.sendRespondMessage(respondData, true));
                                        }
                                    }
                                    break;

                                case AppProtocol.command_receive_program_epg:
                                    PackageIntactData packageData = new PackageIntactData();
                                    if(packageData.packageData(data)&&packageData.isIntact())
                                    {
                                        data =  packageData.getPackageData();
                                        AppEpgInfo epgInfo = AppProtocol.receiveEpgInfo(data);
                                        if (null != epgInfo) {
                                            Intent intent = new Intent();
                                            intent.setAction(EpgActivity.EPG_ACTION);
                                            Bundle epgData = new Bundle();
                                            epgData.putSerializable(getResources().getString(R.string.epg), epgInfo);
                                            epgData.putInt(getResources().getString(R.string.command),EpgActivity.command_add_new_epg);// EpgActivity.command_add_new_epg);
                                            intent.putExtras(epgData);
                                            sendBroadcast(intent);
                                        }
                                    }

                                    if (null != netSender) {
                                        byte respondData[] = new byte[2];
                                        respondData[0] = AppProtocol.command_receive_program_epg;
                                        respondData[1] = 0;
                                        netSender.send(AppProtocol.sendRespondMessage(respondData, true));
                                    }

                                    break;

                                case AppProtocol.command_heartbeat_packet:
                                    String deviceID = AppProtocol.receiveHeartBeatPacket(data);
                                    if (null != deviceID) {
                                        //recover heartbeat interval
                                        if(null != heartbeatPacket)
                                        {
                                            heartbeatPacket.recover();
                                        }

                                        Intent intent = new Intent();
                                        intent.setAction(ConnectActivity.CONNECT_ACTION);
                                        intent.putExtra(getResources().getString(R.string.command), ConnectActivity.command_online_device);
                                        intent.putExtra(AppProtocol.id,  deviceID);
                                        sendBroadcast(intent);

                                        if (null != netSender) {
                                            byte respondData[] = new byte[2];
                                            respondData[0] = AppProtocol.command_heartbeat_packet;
                                            respondData[1] = 0;
                                            netSender.send(AppProtocol.sendRespondMessage(respondData, true));
                                        }
                                    }
                                    break;

                                case AppProtocol.command_epg_reserve_clash:
                                    AppReserveClashEvent eventInfo = AppProtocol.receiveEpgReserveClash(data);
                                    if (null != eventInfo) {

                                        Intent intent = new Intent();
                                        intent.setAction(EpgActivity.EPG_ACTION);

                                        Bundle bundle = new Bundle();
                                        bundle.putInt(getResources().getString(R.string.command), EpgActivity.command_epg_reserve_clash);
                                        bundle.putSerializable(getResources().getString(R.string.epg_reserve_event_info),eventInfo);

                                        intent.putExtras(bundle);
                                        sendBroadcast(intent);

                                        if (null != netSender) {
                                            byte respondData[] = new byte[2];
                                            respondData[0] = AppProtocol.command_epg_reserve_clash;
                                            respondData[1] = 0;
                                            netSender.send(AppProtocol.sendRespondMessage(respondData, true));
                                        }
                                    }
                                    break;

                                case AppProtocol.command_respond:
                                    byte respondData[] = AppProtocol.receiveRespondMessage(data);
                                    Log.i("command_respond:",""+respondData[0]);
                                    if (null != respondData) {
                                        switch (respondData[0]) {
                                            case AppProtocol.command_connect_device:
                                                heartbeatPacket = new HeartbeatPacket(new HeatBeatStopCallback(),
                                                        getResources().getInteger(R.integer.headbeat_interval));
                                                heartbeatPacket.run();
                                                break;

                                            case AppProtocol.command_send_remoter_key:
                                                //TODO
                                                break;

                                            case AppProtocol.command_switch_program:
                                                //TODO
                                                break;

                                            case AppProtocol.command_epg_reserve_set:
                                                Log.i("reserve","command_epg_reserve_set success");
                                                break;
                                            default:break;
                                        }
                                    }
                                    break;

                                default:
                                    break;
                            }
                        }
                       /* try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/
                    }
                }
            };
            receiveThread.start();

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LogTag.HEARTBEAT,"close udp socket");
        if(null != netReceive)
        {
            netReceive.close();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //create task thread to recv main thread msg and send udp socket;
        slaveNetTask = new SlaveNetTask();
        slaveNetTask.start();

        //register broadcast to receive message from other activity or service
        broadcastReceive = new NetReceive();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_ACTION);
        registerReceiver(broadcastReceive, filter);

        Log.i("test__send","broadcastReceive");

        //WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //final WifiManager.MulticastLock  lock= manager.createMulticastLock("testwifi");



    }

    class HeatBeatStopCallback implements HeartbeatStop
    {
        @Override
        public void reStart() {
            Log.i("headbeat","reStart");
            AppManager.getAppManager().finishAllActivity();

            Intent intent = new Intent(NetService.this,ConnectActivity.class);
            startActivity(intent);

        }
    }
}
