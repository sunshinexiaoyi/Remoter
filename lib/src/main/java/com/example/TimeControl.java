package com.example;

import java.io.IOException;

/**
 * Created by wuxy on 2017/6/22.
 */

public class TimeControl {
    private long intervalTime = 0;//setting 1000ms is over time
    private long startTime = 0;
    private long endTime = 0;


    public TimeControl(float intervalTime){
       this((long)intervalTime);
    }

    public TimeControl(int intervalTime){
        this((long)intervalTime);
    }

    public TimeControl(long intervalTime){
        try{
            if(intervalTime < 0) {
                throw new IOException("interval time is must >= 0");
            }

            this.intervalTime = intervalTime;
        }catch(IOException e){
            e.printStackTrace();}
    }

    public boolean isOverTime()
    {
        endTime = System.currentTimeMillis( );
        long curInterval = endTime - startTime;

        if(intervalTime < curInterval)
        {
            startTime = endTime;
            return true;
        }

        return false;
    }

    public void setIntervalTime(long intervalTime){
        this.intervalTime = intervalTime;
    }


}
