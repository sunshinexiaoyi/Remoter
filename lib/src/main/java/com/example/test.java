package com.example;

/**
 * Created by wuxy on 2017/6/15.
 */

public class test {
    public static void main(String[] argc)
    {
        byte[] data = new byte[]{84,1,1,1,-1};
        byte[] dataStr = new  byte[4];

        //无符号字符串 ==> int 类型
        int len =   (data[0]&0xff) +
                    ((data[1]&0xff)<<8)+
                    ((data[1]&0xff)<<16)+
                    ((data[1]&0xff)<<24);

        System.out.println("len:"+ len);

        // int 类型 ==> 无符号字符串
        dataStr[0] = (byte)(len&0xff);
        dataStr[1] = (byte)((len>>8)&0xff);
        dataStr[2] = (byte)((len>>16)&0xff);
        dataStr[3] = (byte)((len>>24)&0xff);

        for (byte a:dataStr
             ) {
            System.out.print(String.format(":%d\n",a));
        }


        //int left8 = (data[1]<<8)+data[0];
        /*for(int i=0;i<data.length;i++)
        {
            System.out.print(String.format("data[%d]:%d\n",i,data[i]));
        }*/
        System.out.println(" 1>>8:"+ (data[3]>>8));
        System.out.println(" 1<<8:"+ (data[3]<<8));

        System.out.println(" -1>>8:"+ (data[4]>>8));
        System.out.println(" -1>>>8:"+ (data[4]>>>1));

        System.out.println(" -1<<8:"+ (data[4]<<8));

        data[1] =(byte)255;
        System.out.println(" data[1]:"+ data[1]);
        System.out.println(" data[1]:"+ (data[1]<<1));
        System.out.println(" data[1]:"+ (data[1]>>1));

        System.out.println(" data[1]:"+ (data[1]&0xff));
        System.out.println("<<8:"+ ((data[1]&0xff)<<8));

        //System.out.println("<<8:"+(data[1]<<8));
        //System.out.println("<<8:"+(data[2]<<8));

        //System.out.println("<<8 :"+left8);
        //System.out.println("len:"+len);

    }
}
