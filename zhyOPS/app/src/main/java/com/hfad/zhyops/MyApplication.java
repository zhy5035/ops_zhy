package com.hfad.zhyops;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhy on 2017/12/24.
 */

public class MyApplication extends Application {
    private static MyApplication mInstance;
   /*
     获取Context
    */
   public static Context getInstance(){
       if(mInstance == null){
           mInstance = new MyApplication();
       }
       return  mInstance;
   }

}
