package com.gospell.wildwolf.remoter;

import android.util.Log;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


class AppReserveClashEvent implements Serializable
{

    public static final String eventName = "event";
    public static final String eventType = "eventType";
    public static final String eventID = "eventID";
    public static final String JobIndex = "JobIndex";
    public static final String programIndex = "programIndex";

    public static final String Date = "Date";
    public static final String startTime = "startTime";
    public static final String endTime = "endTime";
    public static final String BookType = "BookType";

    public static final String select = "Select";
    public static final String conflict = "Conflict";

    public HashMap<String,String> selectEvent = null;
    public HashMap<String,String> conflictEvent = null;

    AppReserveClashEvent()
    {
        selectEvent = new HashMap<>();
        conflictEvent = new HashMap<>();
    }
}

class AppProgramsInfo implements Serializable {
    public static final String lcn = "LCN";
    public static final String serviceID = "service ID";
    public static final String name = "program name";
    public static final String proIndex = "program index";

    public static final String curProType = "program type";   //0 lcn ,1 service id ,2 program index
    public static final String curPlayPro = "program value"; //is playing program;

    public static final String keyProList = "program list";

    private HashMap<String,Object> programInfo = null;
    private ArrayList<HashMap<String, String>> programList = null;

    public int dataLen = 0;
    public int proNum = 0;

    public AppProgramsInfo() {
        programInfo = new HashMap<String,Object>();
        programList = new ArrayList<HashMap<String, String>>();

        programInfo.put(this.keyProList,programList);

    }

    public ArrayList<HashMap<String, String>> getProgramList() {
        if (null != programList) {
            return  programList;
        }
        return null;
    }

    public int getProgramNumber() {
        if (null != programList) {
            return  programList.size();
        }
        return 0;
    }

    public boolean addProgramInfo(HashMap<String, String> program) {
        if ((null != program) && (null != programList)) {
            programList.add(program);
            return true;
        }
        return false;
    }

    public boolean addProgramInfo(String name, String lcn, String serviceID,String proIndex) {
        if ((null != name) && (null != programList) && (null != lcn) && (null != serviceID)) {
            HashMap<String, String> hashMap = new HashMap<String, String>();
            hashMap.put(this.name, name);
            hashMap.put(this.lcn, lcn);
            hashMap.put(this.serviceID, serviceID);
            hashMap.put(this.proIndex, proIndex);
            programList.add(hashMap);
            return true;
        }
        return false;
    }

    public HashMap<String, String> getProgramInfo(int index) {
        if ((index < 0) || (null == programList)) {
            return null;
        }
        return programList.get(index);
    }

    public String getProgramLCN(int index) {
        if ((index < 0) || (null == programList)) {
            return null;
        }
        return programList.get(index).get(lcn);
    }

    public String getProgramName(int index) {
        if ((index < 0) || (null == programList)) {
            return null;
        }
        return programList.get(index).get(name);
    }

    public String getProgramServiceID(int index) {
        if ((index < 0) || (null == programList)) {
            return null;
        }
        return programList.get(index).get(serviceID);
    }

    public String getProgramIndex(int index) {
        if ((index < 0) || (null == programList)) {
            return null;
        }
        return programList.get(index).get(proIndex);
    }

    public boolean addCurPlayPro(String type,String value)
    {
        if((null != programInfo)&&(null != type) &&(null != value))
        {
            programInfo.put(this.curProType,type);
            programInfo.put(this.curPlayPro,value);
            return true;
        }

        return false;
    }

    public String getCurProType()
    {
        if((null != programInfo))
        {
            return (programInfo.get(curProType)).toString();
        }
        return null;
    }

    public String getCurPlayPro()
    {
        if((null != programInfo))
        {
            return (programInfo.get(curPlayPro)).toString();
        }
        return null;
    }

}

class AppEpgInfo implements Serializable {

    public static final String index = "index";

    public static final String name = "name";
    public static final String lcn = "lcn";
    public static final String serviceID = "service ID";
    public static final String dateList = "date list";

    public static final String date = "date";
    public static final String timeList = "time list";

    public static final String time = "time";
    public static final String startTime = "start time";
    public static final String endTime = "end time";
    public static final String event = "event";
    public static final String shortDescriptor = "short descriptor";
    public static final String longDescriptor = "long descriptor";

    public static final String bookState = "book state";//  0 表示没有预制  1表示  预制观看   2表示  预制录制
    public static final String eventID = "event ID";

    private ArrayList<HashMap<String,Object>> epgInfoList = null;

    public static int amount = 0;

    public AppEpgInfo() {
        epgInfoList = new ArrayList<HashMap<String,Object>>();

    }

    public int getProgramNumber() {
        if (null != epgInfoList) {
            return epgInfoList.size();
        }
        return 0;
    }

    public boolean addEpgInfo(HashMap<String, Object> epgInfo) {
        if ((null != epgInfo) && (null != epgInfoList)) {
            epgInfoList.add(epgInfo);
            return true;
        }
        return false;
    }

    public HashMap<String, Object> getEpgInfo(int index) {
        if (null != epgInfoList) {
            return epgInfoList.get(index);
        }
        return  null;
    }

    public boolean putEpgIndex(HashMap<String, Object> epgInfo, String index) {
        if ((null != epgInfo) && (null != index)) {
            epgInfo.put(this.index, index);
            return true;
        }
        return false;
    }
    public String getEpgIndex(HashMap<String, Object> epgInfo) {
        String index = null;
        if (null != epgInfo){
            index = (String)epgInfo.get(this.index);
            return index;
        }
        return null;
    }

    public boolean putEpgName(HashMap<String, Object> epgInfo, String name) {
        if ((null != epgInfo) && (null != name)) {
            epgInfo.put(this.name, name);
            return true;
        }
        return false;
    }


    public String getEpgName(HashMap<String, Object> epgInfo) {
        String name = null;
        if (null != epgInfo){
            name = (String)epgInfo.get(this.name);
            return name;
        }
        return null;
    }


    public boolean putEpgLCN(HashMap<String, Object> epgInfo, String lcn) {
        if ((null != epgInfo) && (null != lcn)) {
            epgInfo.put(this.lcn, lcn);
            return true;
        }
        return false;
    }

    public String getEpgLCN(HashMap<String, Object> epgInfo) {
        String lcn = null;
        if (null != epgInfo){
            lcn = (String)epgInfo.get(this.lcn);
            return lcn;
        }
        return null;
    }

    public boolean putEpgServiceID(HashMap<String, Object> epgInfo, String serviceID) {
        if ((null != epgInfo) && (null != serviceID)) {
            epgInfo.put(this.serviceID, serviceID);
            return true;
        }
        return false;
    }

    public String getEpgServiceID(HashMap<String, Object> epgInfo) {
        String serviceID = null;
        if (null != epgInfo) {
            serviceID = (String)epgInfo.get(this.serviceID);
            return serviceID;
        }
        return null;
    }

    public boolean putEpgDateList(HashMap<String, Object> epgInfo, ArrayList<HashMap<String, Object>> dateList) {
        if ((null != epgInfo) && (null != dateList)) {
            epgInfo.put(this.dateList, dateList);
            return true;
        }
        return false;
    }

    public ArrayList<HashMap<String, Object>> getEpgDateList(HashMap<String, Object> epgInfo) {
        ArrayList<HashMap<String, Object>> dateList = null;
        if (null != epgInfo) {
            dateList = (ArrayList<HashMap<String, Object>>)epgInfo.get(this.dateList);
            return dateList;
        }
        return null;
    }

    public boolean addEpgDayInfo(ArrayList<HashMap<String, Object>> dateList, HashMap<String, Object> dayInfo) {
        if ((null != dateList) && (null != dayInfo)) {
            dateList.add(dayInfo);
            return true;
        }
        return false;
    }

    public int getEpgDayInfoSize(ArrayList<HashMap<String, Object>> dateList) {
        if (null != dateList) {
            return dateList.size();
        }
        return 0;
    }

    public HashMap<String, Object> getEpgDayInfo(ArrayList<HashMap<String, Object>> dateList, int index) {
        HashMap<String, Object> dayInfo = null;
        if (null != dateList) {
            dayInfo = (HashMap<String, Object>)dateList.get(index);
            return dayInfo;
        }
        return null;
    }

    //epg date api
    public boolean putEpgDayInfoItemDate(HashMap<String, Object> dayInfo, String date) {
        if ((null != dayInfo) && (null != date)) {
            dayInfo.put(this.date, date);
            return true;
        }
        return false;
    }

    public String getEpgDayInfoItemDate(HashMap<String, Object> dayInfo) {
        String date = null;
        if (null != dayInfo) {
            date = (String)dayInfo.get(this.date);
            return date;
        }
        return null;
    }

    public boolean putEpgTimeList(HashMap<String, Object> dayInfo, ArrayList<HashMap<String, String>> timeList) {
        if ((null != dayInfo) && (null != timeList)) {
            dayInfo.put(this.timeList, timeList);
            return true;
        }
        return false;
    }

    public ArrayList<HashMap<String, String>> getEpgTimeList(HashMap<String, Object> dayInfo) {
        ArrayList<HashMap<String, String>> timeList = null;
        if (null != dayInfo) {
            timeList = (ArrayList<HashMap<String, String>>)dayInfo.get(this.timeList);
            return timeList;
        }
        return null;
    }

    public int getEpgTimeListSize(ArrayList<HashMap<String, String>> timeList) {
        if (null != timeList) {
            return timeList.size();
        }
        return 0;
    }

    public boolean addEpgTimeInfo(ArrayList<HashMap<String, String>> timeList, HashMap<String, String> timeInfo) {
        if ((null != timeList) && (null != timeInfo)) {
            timeList.add(timeInfo);
            return true;
        }
        return false;
    }

    public HashMap<String, String> getEpgTimeInfo(ArrayList<HashMap<String, String>> timeList, int index) {
        HashMap<String, String> timeInfo = null;
        if (null != timeList) {
            timeInfo = timeList.get(index);
            return timeInfo;
        }
        return null;
    }

    //epg time information
    public boolean putEpgTimeInfoItemTime(HashMap<String, String> timeInfo, String startTime, String endTime) {
        if ((null != timeInfo) && (null != startTime) && (null != endTime)) {
            timeInfo.put(this.startTime, startTime);
            timeInfo.put(this.endTime, endTime);

            timeInfo.put(this.time, startTime+"-"+endTime);
            return true;
        }
        return false;
    }

    public String getEpgTimeInfoItemStartTime(HashMap<String, String> timeInfo) {
        String startTime = null;
        if (null != timeInfo) {
            startTime = timeInfo.get(this.startTime);
            return startTime;
        }
        return null;
    }

    public String getEpgTimeInfoItemEndTime(HashMap<String, String> timeInfo) {
        String endTime = null;
        if (null != timeInfo) {
            endTime = timeInfo.get(this.endTime);
            return endTime;
        }
        return null;
    }

    //epg event information api
    public boolean putEpgTimeInfoItemEvent(HashMap<String, String> timeInfo, String event) {
        if ((null != timeInfo) && (null != event)) {
            timeInfo.put(this.event, event);
            return true;
        }
        return false;
    }

    public String getEpgTimeInfoItemEvent(HashMap<String, String> timeInfo) {
        String event = null;
        if (null != timeInfo) {
            event = timeInfo.get(this.event);
            return event;
        }
        return null;
    }

    //epg short descriptions api
    public boolean putEpgTimeInfoItemShortDes(HashMap<String, String> timeInfo, String shortDescriptor) {
        if ((null != timeInfo) && (null != shortDescriptor)) {
            timeInfo.put(this.shortDescriptor, shortDescriptor);
            return true;
        }
        return false;
    }

    public String getEpgTimeInfoItemShortDes(HashMap<String, String> timeInfo) {
        String shortDescriptor = null;
        if (null != timeInfo) {
            shortDescriptor = timeInfo.get(this.shortDescriptor);
            return shortDescriptor;
        }
        return null;
    }

    //epg long descriptions api
    public boolean putEpgTimeInfoItemLongDes(HashMap<String, String> timeInfo, String longDescriptor) {
        if ((null != timeInfo) && (null != longDescriptor)) {
            timeInfo.put(this.longDescriptor, longDescriptor);
            return true;
        }
        return false;
    }

    public String getEpgTimeInfoItemLongDes(HashMap<String, String> timeInfo) {
        String longDescriptor = null;
        if (null != timeInfo){
            longDescriptor = timeInfo.get(this.longDescriptor);
            return longDescriptor;
        }
        return null;
    }
    public boolean putEpgTimeInfoItemBookState(HashMap<String, String> timeInfo, String bookState) {
        if ((null != timeInfo) && (null != bookState)) {
            timeInfo.put(this.bookState, bookState);
            return true;
        }
        return false;
    }

    public boolean putEpgTimeInfoItemEventID(HashMap<String, String> timeInfo, String eventID) {
        if ((null != timeInfo) && (null != eventID)) {
            timeInfo.put(this.eventID, eventID);
            return true;
        }
        return false;
    }

    public void createEpgData()
    {
        //ArrayList<HashMap<String,Object>> epgInfoList = new ArrayList<HashMap<String,Object>>();

        int proNum = 1;
        int dateNum = 10;
        for(int i = 0; i < proNum;i++)
        {

            HashMap<String,Object> proItem = new HashMap<>();
            proItem.put(AppEpgInfo.name,String.format("CCTV%d",amount++));

            ArrayList<HashMap<String,Object>> dateList = new ArrayList<HashMap<String,Object>>();
            for(int j=0 ; j < dateNum;j++)
            {
                HashMap<String,Object> dateItem = new HashMap<>();
                dateItem.put(AppEpgInfo.date,String.format("2017-5-%d",j + amount));

                ArrayList<HashMap<String,Object>> timeList = getTimeList();

                dateItem.put(AppEpgInfo.timeList,timeList);

                dateList.add(dateItem);
            }

            proItem.put(AppEpgInfo.dateList,dateList);
            epgInfoList.add(proItem);
        }
    }

    private ArrayList<HashMap<String, Object>> getTimeList()
    {
        int num = 10;

        ArrayList<HashMap<String, Object>> listitem = new ArrayList<HashMap<String, Object>>();

        for (int i = 0; i < num; i++) {
            HashMap<String, Object> showitem = new HashMap<String, Object>();
            showitem.put(AppEpgInfo.startTime, String.format("%d:00",9+i));
            showitem.put(AppEpgInfo.endTime, String.format("%d:00",9+i+1));
            showitem.put(AppEpgInfo.event, String.format("BBC-%d",i));

            String longStr = "长歌行_百度汉语\n" +
                    "作者：汉乐府\n" +
                    "青青园中葵，朝露待日晞。\n" +
                    "阳春布德泽，万物生光辉。\n" +
                    "常恐秋节至，焜黄华叶衰。\n" +
                    "百川东到海，何时复西归？\n" +
                    "少壮不努力，老大徒伤悲。";
            showitem.put("eventName", String.format("BBC-%d",i + amount));
            showitem.put(AppEpgInfo.shortDescriptor, String.format("shortDescriptor-\n%s",longStr));
            showitem.put(AppEpgInfo.longDescriptor, String.format("longDescriptor-%d",i));

            listitem.add(showitem);
        }

        return listitem;
    }
}

/**
 * Created by wildwolf on 2017/5/25.
 */
public class AppProtocol {
    //communicatin command
    public static final byte command_heartbeat_packet = 1;
    public static final byte command_find_device = 2;
    public static final byte command_add_device = 3;
    public static final byte command_connect_device = 4;
    public static final byte command_respond = 5;

    public static final byte command_send_remoter_key = 10;

    public static final byte command_get_programs_list = 20;
    public static final byte command_receive_programs_list = 21;
    public static final byte command_switch_program = 22;

    public static final byte command_switch_program_epg = 30;
    public static final byte command_receive_program_epg = 31;

    public static final byte command_epg_reserve_set = 32;   //设置epg预定事件
    public static final byte command_epg_reserve_clash = 33; //epg预定事件冲突


    //network socket port
    public static final int sendPort = 4321;
    public static final int receivePort = 1234;

    //connect info
    public final static String id =  "Device ID";
    public final static String mac = "MAC";
    public final static String ip =  "IP";

    public static byte[] findDevice() {
        byte data[] = new byte[128];

        //command
        data[0] = command_find_device;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 0;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        return data;
    }

    public static byte[] connectDevice() {
        byte data[] = new byte[128];

        //command
        data[0] = command_connect_device;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 0;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        return data;
    }

    public static byte[] sendRespondMessage(byte command[], boolean flag) {
        byte data[] = new byte[128];

        //command
        data[0] = command_respond;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 4;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        data[11] = command[0];
        data[12] = command[1];
        if (flag) {
            data[12] = 1;
        } else {
            data[12] = 0;
        }
        data[13] = 0;

        return data;
    }

    public static byte[] sendRemoterKey(int keyValue){
        byte data[] = new byte[128];

        //command
        data[0] = command_send_remoter_key;
        data[1] = 0; //reserved

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 4;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        //data
        data[11] = (byte)(keyValue & 0xFF);
        data[12] = (byte)((keyValue>>8) & 0xFF);
        data[13] = (byte)((keyValue>>16) & 0xFF);
        data[14] = (byte)((keyValue>>24) & 0xFF);

        return data;
    }

    public static byte[] switchProgramIndex(int proIndex){
        byte data[] = new byte[128];

        //command
        data[0] = command_switch_program;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 5;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        //data
        data[11] = 2;//0:lcn 1:service id 2program index
        data[12] = (byte)(proIndex & 0xFF);
        data[13] = (byte)((proIndex>>8) & 0xFF);
        data[14] = (byte)((proIndex>>16) & 0xFF);
        data[15] = (byte)((proIndex>>24) & 0xFF);

        return data;
    }

    public static byte[] switchProgramByLCN(int lcn){
        byte data[] = new byte[128];

        //command
        data[0] = command_switch_program;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 5;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        //data
        data[11] = 0;//0:lcn 1:service id
        data[12] = (byte)(lcn & 0xFF);
        data[13] = (byte)((lcn>>8) & 0xFF);
        data[14] = (byte)((lcn>>16) & 0xFF);
        data[15] = (byte)((lcn>>24) & 0xFF);

        return data;
    }

    public static byte[] switchProgramByServiceID(int serviceID){
        byte data[] = new byte[128];

        //command
        data[0] = command_switch_program;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 5;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        //data
        data[11] = 1; //0:lcn 1:service id
        data[12] = (byte)(serviceID & 0xFF);
        data[13] = (byte)((serviceID>>8) & 0xFF);
        data[14] = (byte)((serviceID>>16) & 0xFF);
        data[15] = (byte)((serviceID>>24) & 0xFF);

        return data;
    }


    public static byte[] getSwitchEpgInfo(int type ,int value) {
        byte data[] = new byte[128];

        //command
        data[0] = command_switch_program_epg;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;


        //data length
        data[7] = 5;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        //data
        data[11] = (byte)type;//0:lcn 1:service id 2program index
        data[12] = (byte)(value & 0xFF);
        data[13] = (byte)((value>>8) & 0xFF);
        data[14] = (byte)((value>>16) & 0xFF);
        data[15] = (byte)((value>>24) & 0xFF);

        return data;
    }




    public static byte[] getProgramInfo() {
        byte data[] = new byte[128];

        //command
        data[0] = command_get_programs_list;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;

        //data length
        data[7] = 0;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;

        return data;
    }


    public static byte[] getEpgReserveSetInfo(int eventType ,int proIndex,int eventId) {
        byte data[] = new byte[128];

        //command
        data[0] = command_epg_reserve_set;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;


        //data length
        data[7] = 9;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;


        //data
        int s = 11;

        data[s++] = (byte)eventType;//0：delete， 1：View， 2：View Series， 3： Record，4： Record Series

        data[s++] = (byte)( proIndex & 0xFF);
        data[s++] = (byte)((proIndex>>8) & 0xFF);
        data[s++] = (byte)((proIndex>>16) & 0xFF);
        data[s++] = (byte)((proIndex>>24) & 0xFF);

        data[s++] = (byte)( eventId & 0xFF);
        data[s++] = (byte)((eventId>>8) & 0xFF);
        data[s++] = (byte)((eventId>>16) & 0xFF);
        data[s++] = (byte)((eventId>>24) & 0xFF);


        return data;
    }

    public static byte[] getEpgReserveClashInfo(int eventType ,int index,int eventId,int jobIndex) {
        byte data[] = new byte[128];

        //command
        data[0] = command_epg_reserve_clash;
        data[1] = 0;

        //32bit crc
        data[2] = 8;
        data[3] = 0;
        data[4] = 9;
        data[5] = 0;

        //encryption 0 :no 1:yes
        data[6] = 0;


        //data length
        data[7] = 13;
        data[8] = 0;
        data[9] = 0;
        data[10] = 0;


        //data
        int s = 11;

        data[s++] = (byte)eventType;//0：delete， 1：View， 2：View Series， 3： Record，4： Record Series

        data[s++] = (byte)( index & 0xFF);
        data[s++] = (byte)((index>>8) & 0xFF);
        data[s++] = (byte)((index>>16) & 0xFF);
        data[s++] = (byte)((index>>24) & 0xFF);

        data[s++] = (byte)( eventId & 0xFF);
        data[s++] = (byte)((eventId>>8) & 0xFF);
        data[s++] = (byte)((eventId>>16) & 0xFF);
        data[s++] = (byte)((eventId>>24) & 0xFF);

        data[s++] = (byte)( jobIndex & 0xFF);
        data[s++] = (byte)((jobIndex>>8) & 0xFF);
        data[s++] = (byte)((jobIndex>>16) & 0xFF);
        data[s++] = (byte)((jobIndex>>24) & 0xFF);


        return data;
    }

    public static HashMap<String, String> receiveDeviceInfo(byte data[]){
        HashMap<String, String> deviceInfo = new HashMap<String, String>();
        int startIndex = 0;
        int endIndex = 0;

        //check comand
        if ((command_add_device != data[0]) || (0 != data[1])) {
            Log.i("test_recv","check comand");
            return null;
        }


        //check crc
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {

            return null;
        }

        //encryption 0 :no 1:yes
        data[6] = 0;

        //total length
        int infoLen = data[7] + (data[8]<<8) + (data[9]<<16) + (data[10]<<24);

        byte deviceIDLen = data[11];
        byte MACLen = data[12];
        byte IPLen = data[13];
        Log.i("test_name",String.format("deviceIDLen:%d",deviceIDLen));
        Log.i("test_name",String.format("MACLen:%d",MACLen));
        Log.i("test_name",String.format("IPLen:%d",IPLen));
        //device id
        startIndex = 14;
        endIndex = startIndex + deviceIDLen;
        deviceInfo.put(id, new String(Arrays.copyOfRange(data, startIndex, endIndex)));



        //MAC address
        startIndex += deviceIDLen;
        endIndex += MACLen;
        deviceInfo.put(mac, new String(Arrays.copyOfRange(data, startIndex, endIndex)));

        byte[] byteName = Arrays.copyOfRange(data, startIndex, endIndex);
        String name =  new String(byteName);


        //IP address
        startIndex += MACLen;
        endIndex += IPLen;
        deviceInfo.put(ip, new String(Arrays.copyOfRange(data, startIndex, endIndex)));

        return deviceInfo;
    }

    public static AppProgramsInfo receiveProgramsInfo(byte data[]) {
        AppProgramsInfo programsInfo = new AppProgramsInfo();
        int startIndex = 0;
        int endIndex = 0;

        //check comand
        if ((command_receive_programs_list != data[0]) || (0 != data[1])) {
            return null;
        }

        //check crc
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {
            return null;
        }

        //encryption 0 :no 1:yes
        data[6] = 0;

        //total length
        int infoLen = (data[7]&0x00ff) + ((data[8]&0x00ff)<<8) + ((data[9]&0x00ff)<<16) + ((data[10]&0x00ff)<<24);

        //programs number
        int programsNumber = (data[11]&0x00ff) + ((data[12]&0x00ff)<<8) + ((data[13]&0x00ff)<<16) + ((data[14]&0x00ff)<<24);

        //
        programsInfo.dataLen = infoLen;
        programsInfo.proNum = programsNumber;

        //1 byte program type
        byte curProType = data[15];
        //4 byte playing program value
        int curPlayProgram = (data[16]&0x00ff) + ((data[17]&0x00ff)<<8) + ((data[18]&0x00ff)<<16) + ((data[19]&0x00ff)<<24);

        programsInfo.addCurPlayPro(String.valueOf(curProType),String.valueOf(curPlayProgram));

        ////program num+cur type+cur play idx + (program name + lcn + service id + idx) * program num	//type: 0 lcn 1 svid 2 index
        startIndex = 11 + 4 + 4 +1;
        endIndex = startIndex;
        for (int index = 0; index < programsNumber; index++) {
            //64 byte program name
            endIndex += 64;
            String name = new String(Arrays.copyOfRange(data, startIndex, endIndex));

            //4 byte
            int lcn = (data[endIndex++]&0x00ff) + ((data[endIndex++]&0x00ff)<<8) + ((data[endIndex++]&0x00ff)<<16) + ((data[endIndex++]&0x00ff)<<24);
            String lcnStr= String.valueOf(lcn);

            //4 byte
            int serviceID = (data[endIndex++]&0x00ff) + ((data[endIndex++]&0x00ff)<<8) + ((data[endIndex++]&0x00ff)<<16) + ((data[endIndex++]&0x00ff)<<24);
            String serviceIDStr = String.valueOf(serviceID);

            //4 byte
            int proIndex = (data[endIndex++]&0x00ff) + ((data[endIndex++]&0x00ff)<<8) + ((data[endIndex++]&0x00ff)<<16) + ((data[endIndex++]&0x00ff)<<24);
            String proIndexStr = String.valueOf(proIndex);

            programsInfo.addProgramInfo(name, lcnStr, serviceIDStr,proIndexStr);
            startIndex = endIndex;
        }

        return programsInfo;
    }

    public static AppEpgInfo receiveEpgInfo(byte data[])
    {
        AppEpgInfo appEpgInfo = new AppEpgInfo();
        HashMap<String, Object> program = new HashMap<String, Object>();
        ArrayList<HashMap<String, Object>> dateList = new ArrayList<HashMap<String, Object>>();

        //check comand(2 byte)
        if ((command_receive_program_epg != data[0]) || (0 != data[1])) {
            return null;
        }

        //check crc(4 byte)
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {
            return null;
        }

        //encryption 0 :no 1:yes(1 byte)
        data[6] = 0;

        //total length(4 byte)
        int infoLen = (data[7]&0x00ff) + ((data[8]&0x00ff)<<8) + ((data[9]&0x00ff)<<16) + ((data[10]&0x00ff)<<24);


        String jsonStr = (new String(data)).substring(11);//get json String

        try{
            JSONObject programObj = new JSONObject(jsonStr);
            appEpgInfo.putEpgLCN(program,programObj.getString("lcn"));
            appEpgInfo.putEpgServiceID(program,programObj.getString("id"));
            appEpgInfo.putEpgName(program,programObj.getString("name"));
            appEpgInfo.putEpgIndex(program,programObj.getString("index"));


            Log.i("test_epg","name:"+programObj.getString("name"));
            JSONArray dateArray = programObj.getJSONArray("dateArray");
            for(int i=0 ; i<dateArray.length() ; i++)
            {
                JSONObject dateObj = dateArray.optJSONObject(i);

                HashMap<String, Object> dayInfo = new HashMap<String, Object>();
                appEpgInfo.putEpgDayInfoItemDate(dayInfo,dateObj.getString("date"));
                //Log.i("test_epg","date:"+dateObj.getString("date"));

                JSONArray timeArray = dateObj.getJSONArray("timeArray");
                ArrayList<HashMap<String, String>> timeList = new ArrayList<HashMap<String, String>>();
                //Log.i("test_epg","timeArray len:"+timeArray.length());
                for(int j=0 ; j<timeArray.length() ; j++)
                {

                    JSONObject timeObj = timeArray.optJSONObject(j);
                    HashMap<String, String> timeInfo = new HashMap<String, String>();
                    appEpgInfo.putEpgTimeInfoItemTime(timeInfo, timeObj.getString("startTime"), timeObj.getString("endTime"));

                    appEpgInfo.putEpgTimeInfoItemEvent(timeInfo, timeObj.getString("event"));
                    appEpgInfo.putEpgTimeInfoItemShortDes(timeInfo, timeObj.getString("shortDes"));
                    appEpgInfo.putEpgTimeInfoItemBookState(timeInfo,timeObj.getString("bookState"));
                    appEpgInfo.putEpgTimeInfoItemEventID(timeInfo,timeObj.getString("eventID"));

                    appEpgInfo.addEpgTimeInfo(timeList, timeInfo);

                    //Log.i("test_epg","startTime"+timeObj.getString("startTime"));
                }
                appEpgInfo.putEpgTimeList(dayInfo, timeList);
                appEpgInfo.addEpgDayInfo(dateList, dayInfo);

            }

        appEpgInfo.putEpgDateList(program, dateList);
        appEpgInfo.addEpgInfo(program);


        }catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return appEpgInfo;
    }

    public static AppReserveClashEvent receiveEpgReserveClash(byte data[])
    {

        Log.i("clash","receiveEpgReserveClash");
        AppReserveClashEvent eventInfo = new AppReserveClashEvent();

        //check comand(2 byte)
        if ((command_epg_reserve_clash != data[0]) || (0 != data[1])) {
            return null;
        }

        //check crc(4 byte)
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {
            return null;
        }

        //encryption 0 :no 1:yes(1 byte)
        data[6] = 0;

        //total length(4 byte)
        int infoLen = (data[7]&0x00ff) + ((data[8]&0x00ff)<<8) + ((data[9]&0x00ff)<<16) + ((data[10]&0x00ff)<<24);


        String jsonStr = (new String(data)).substring(11);//get json String

        try{
            JSONObject eventObj = new JSONObject(jsonStr);
            try {
                JSONArray eventArray = eventObj.getJSONArray("EventArray");
                Log.i("clash","length:"+eventArray.length());
                if(eventArray.length() == 2)
                {
                    for(int i = 0;i<eventArray.length();i++)
                    {
                        JSONObject obj   = eventArray.getJSONObject(i);
                        String eventType = obj.getString(eventInfo.eventType);
                        Log.i("clash","eventType:"+eventType);
                        if(eventType.equals(eventInfo.conflict ))
                        {
                            eventInfo.conflictEvent.put(eventInfo.eventName,obj.getString(eventInfo.eventName));
                            eventInfo.conflictEvent.put(eventInfo.Date,obj.getString(eventInfo.Date));
                            eventInfo.conflictEvent.put(eventInfo.startTime,obj.getString(eventInfo.startTime));
                            eventInfo.conflictEvent.put(eventInfo.endTime,obj.getString(eventInfo.endTime));
                            eventInfo.conflictEvent.put(eventInfo.JobIndex,obj.getString(eventInfo.JobIndex));

                            Log.i("clash","JobIndex:"+obj.getString(eventInfo.JobIndex));
                        }
                        else if(eventType.equals(eventInfo.select))
                        {
                            eventInfo.selectEvent.put(eventInfo.eventName,obj.getString(eventInfo.eventName));
                            eventInfo.selectEvent.put(eventInfo.Date,obj.getString(eventInfo.Date));
                            eventInfo.selectEvent.put(eventInfo.startTime,obj.getString(eventInfo.startTime));
                            eventInfo.selectEvent.put(eventInfo.endTime,obj.getString(eventInfo.endTime));

                            eventInfo.selectEvent.put(eventInfo.BookType,obj.getString(eventInfo.BookType));

                            eventInfo.selectEvent.put(eventInfo.programIndex,obj.getString(eventInfo.programIndex));
                            eventInfo.selectEvent.put(eventInfo.eventID,obj.getString(eventInfo.eventID));
                        }
                    }
                }
            }catch (JSONException e){
                e.printStackTrace();
                return null;
            }


        }catch (JSONException e){
            e.printStackTrace();
            return null;
        }

        return eventInfo;
    }




    public static byte[] receiveRespondMessage(byte data[]) {
        byte respondData[] = new byte[4];

        //check comand
        if ((command_respond != data[0]) || (0 != data[1])) {
            return null;
        }

        //check crc
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {
            return null;
        }

        //encryption 0 :no 1:yes
        data[6] = 0;

        //total length
        int totalLen = (data[7]&0x00ff) + ((data[8]&0x00ff)<<8) + ((data[9]&0x00ff)<<16) + ((data[10]&0x00ff)<<24);

        if (4 == totalLen) {
            respondData[0] = data[11];
            respondData[1] = data[12];
            respondData[2] = data[13];
            respondData[3] = data[14];

            return respondData;
        }

        return null;
    }

    public static String receiveHeartBeatPacket(byte data[]) {
        String deviceID = null;
        int startIndex = 0;
        int endIndex = 0;

        //check comand
        if ((command_heartbeat_packet != data[0]) || (0 != data[1])) {
            return null;
        }

        //check crc
        if ((data[2] != 8) || (data[3] != 0) || (data[4] != 9) || (data[5] != 0)) {
            return null;
        }

        //encryption 0 :no 1:yes
        data[6] = 0;

        //total length
        int totalLen = (data[7]&0x00ff) + ((data[8]&0x00ff)<<8) + ((data[9]&0x00ff)<<16) + ((data[10]&0x00ff)<<24);

        byte deviceIDLen = data[11];
        startIndex = 12;
        endIndex = startIndex + deviceIDLen;
        deviceID = new String(Arrays.copyOfRange(data, startIndex, endIndex));

        return deviceID;
    }
}


class  PracticalTool
{
    public static String toUtf8(String str) {
        String result = null;
        try {
            result = new String(str.getBytes("UTF-8"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return result;
    }
}

class PackageIntactData
{
    static int amountPackageNum =0;
    static int curPackageNum =0;       //from 0 start
    static byte[] packageData = null;

    private boolean status = false;


    public boolean packageData(byte[] data)
    {

        int headLen = 11;
        int position = 11;

        byte amountNum = data[position++];
        byte curNum = data[position++];

        byte[] head = Arrays.copyOfRange(data,0,headLen);

        byte[] loaderData = Arrays.copyOfRange(data,position,data.length);

        Log.i("test__epg","curNum:"+ curNum);

        this.status = false;

        if(0 == curNum)//package start
        {
            this.amountPackageNum = amountNum;
            this.curPackageNum = curNum;

            this.packageData = combineArray(head,loaderData);

            Log.i("test__epg","amountPackageNum:"+ amountPackageNum);

        }
        else
        {
            if((null == packageData)||(curNum != curPackageNum+1)||(amountNum != amountPackageNum))//data loser
            {
                this.amountPackageNum = 0;
                this.curPackageNum = 0;

                return false;
            }

            this.packageData = combineArray(this.packageData,loaderData);
            this.curPackageNum = curNum;
        }

        if(this.amountPackageNum == (this.curPackageNum +1))
        {
            this.status = true;

            Log.i("this.status",""+this.status);
        }

        return true;
    }

    public boolean isIntact()
    {
        return this.status;
    }

    public byte[] getPackageData()
    {
        Log.i("test__epg","packageData:"+this.packageData.length);
        return this.packageData;
    }

    public byte[] combineArray(byte[] arrayStart,byte[] arrayEnd )
    {
        if(null == arrayStart || null == arrayEnd)
        {
            return null;
        }

        int arrayStartLength = arrayStart.length;
        int arrayEndLength = arrayEnd.length;
        //Log.i("test__epg","arryStartLength:"+ arrayStartLength);
        //Log.i("test__epg","arrayEndLength:"+ arrayEndLength);
        byte[] arrayNew = Arrays.copyOf(arrayStart, arrayStartLength+arrayEndLength);//数组扩容
        System.arraycopy(arrayEnd, 0, arrayNew, arrayStartLength, arrayEndLength);


        return arrayNew;
    }


}
