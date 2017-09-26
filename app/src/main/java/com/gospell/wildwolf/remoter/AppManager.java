package com.gospell.wildwolf.remoter;


import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;


public class AppManager {

    //private static AppManager instance;

    private AppManager(){}

    /**
     * 单例模式
     */
    private static class InnerClass{
        static AppManager instance = new AppManager();
        private static Stack<Activity> activityStack = new Stack<Activity>();
    }

    public static AppManager getAppManager(){
        return InnerClass.instance;
    }
    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity){
        if(InnerClass.activityStack==null){
            InnerClass.activityStack=new Stack<Activity>();
        }

        Log.i(LogTag.HEARTBEAT,"add:"+activity.getLocalClassName());
        InnerClass.activityStack.add(activity);
    }

    /**
     * 获取当前Activity（堆栈中最后一个压入的）
     */
    public Activity currentActivity(){
        Activity activity=InnerClass.activityStack.lastElement();
        return activity;
    }

    /**
     * 结束当前Activity（堆栈中最后一个压入的）
     */
    public void finishActivity(){
        Activity activity=InnerClass.activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity){
        if(activity!=null){
            InnerClass.activityStack.remove(activity);
            activity.finish();
            activity=null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls){
        for (Activity activity : InnerClass.activityStack) {
            if(activity.getClass().equals(cls) ){
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity(){

        while (!InnerClass.activityStack.empty()) {
            Activity activity = InnerClass.activityStack.pop();
            activity.finish();

            Log.i(LogTag.HEARTBEAT,"finish:"+activity.getLocalClassName());
        }

        InnerClass.activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit(Context context) {
        try {
            finishAllActivity();
            ActivityManager activityMgr= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {	}
    }
}


interface HeartbeatStop
{
    public void reStart();
}

class HeartbeatPacket implements HeartbeatStop
{
    HeartbeatStop heartbeatStop = null;

    final int setInterval;
    int curInterval = 0;

    private Timer timer;

    HeartbeatPacket(HeartbeatStop heartbeatStop, int curInterval)
    {
        this.heartbeatStop = heartbeatStop;
        this.curInterval = curInterval/1000;//ms -> s
        setInterval = this.curInterval;

        timer = new Timer() ;
    }

    @Override
    public void reStart() {
        if(null != heartbeatStop)
        {
            heartbeatStop.reStart();
        }
    }

    public void recover()
    {
        synchronized (this){
            curInterval = setInterval;
        }
    }

    public  void run(){
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
              synchronized (this){
                  if((curInterval--)<0)
                  {
                      reStart();
                      recover();

                      timer.cancel();
                  }
              }
            }
        }, 0,1000);

    }
}


class LogTag
{
    final static String HEARTBEAT = "heartbeat";

    final static String PROGRAM = "program";

    final static String EPG = "epg";

    final static String REMOTER = "remoter";

}



interface GestureCallback
{
    public void leftSlid(View v);
    public void rightSlid(View v);

    public void click(View v);
}


class GestureManager implements View.OnTouchListener
{

    public ModeEnum mode = ModeEnum.NONE;

    private GestureCallback gestureCallback = null;

    //缩放控制
    private Matrix matrix = new Matrix();
    private Matrix saveMatrix = new Matrix();

    // 定义第一个按下的点，两只接触点的重点，以及出事的两指按下的距离：
    private PointF startPoint = new PointF();
    private PointF midPoint = new PointF();

    private float oriDis = 1f;

    private float minDistance = 20f;//移动触发距离

    GestureManager(GestureCallback gestureCallback)
    {
        this.gestureCallback = gestureCallback;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //Log.i("onTouch","x:"+event.getX()+" y:"+event.getY());
        // ImageView imageView = (ImageView)v;

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
               /*
                MotionEvent.ACTION_DOWN：按下事件
                MotionEvent.ACTION_MOVE:移动事件
                MotionEvent.ACTION_UP:弹起事件
                MotionEvent.ACTION_POINTER_DOWN:当屏幕上已经有一个点被按住，此时再按下其他点时触发。
                MotionEvent.ACTION_POINTER_UP:当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）。
               */
            case MotionEvent.ACTION_DOWN:
                saveMatrix.set(matrix);
                startPoint.set(event.getX(),event.getY());
                // Log.i("ACTION_DOWN","x:"+event.getX()+" y:"+event.getY());
                mode = ModeEnum.DRAG;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:

                oriDis = distance(event);
                if(oriDis > 10f)
                {
                    mode = ModeEnum.ZOOM;
                    saveMatrix.set(matrix);
                    midPoint = middle(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(ModeEnum.DRAG == mode)
                {
                    float distance = event.getX()-startPoint.x;
                    float dX= (int) (event.getX()- startPoint.x);
                    float dY= (int) (event.getY()- startPoint.y);

                    if(Math.abs(dX) > minDistance || Math.abs(dY) > minDistance)
                    {
                        if(Math.abs(dX)>Math.abs(dY)){//左右滑动
                            if(distance >0)//right
                            {
                                mode = ModeEnum.RIGHT;
                                Log.i("ACTION_MOVE","右移");
                                if(null != gestureCallback)
                                {
                                    gestureCallback.rightSlid(v);
                                }
                            }
                            else
                            {
                                Log.i("ACTION_MOVE","左移");
                                mode = ModeEnum.LEFT;
                                if(null != gestureCallback)
                                {
                                    gestureCallback.leftSlid(v);
                                }
                            }
                            return true;
                        }

                    }else
                    {
                        mode = ModeEnum.CLICK;
                        if(null != gestureCallback)
                        {
                            gestureCallback.click(v);
                        }
                    }
                }

                Log.i("mode",""+mode);
                break;
            case MotionEvent.ACTION_POINTER_UP:
                mode = ModeEnum.NONE;
                break;
            case MotionEvent.ACTION_MOVE:
                //Log.i("ACTION_MOVE","x:"+event.getX()+" y:"+event.getY());
                if(ModeEnum.DRAG == mode)
                {
                    //Log.i("ACTION_MOVE","拖动");
                    matrix.set(saveMatrix);
                    matrix.postTranslate(event.getX()-startPoint.x,event.getY()-startPoint.y);

                }
                else if(ModeEnum.ZOOM == mode)
                {
                    Log.i("ACTION_MOVE","缩放");
                    float newDist = distance(event);
                    if(newDist > 10f)
                    {
                        matrix.set(saveMatrix);
                        float scale = newDist/oriDis;
                        matrix.postScale(scale,scale,midPoint.x,midPoint.y);
                    }
                }
                else
                {

                }
                break;

        }

        return false;
    }

    // 计算两个触摸点之间的距离
    private float distance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);

    }

    // 计算两个触摸点的中点
    private PointF middle(MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        return new PointF(x / 2, y / 2);
    }
}


enum ModeEnum
{
    DRAG,   //拖动
    ZOOM,   //缩放
    NONE,   //无

    LEFT,
    RIGHT,
    CLICK

}







