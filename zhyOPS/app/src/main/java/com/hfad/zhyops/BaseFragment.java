package com.hfad.zhyops;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;


import android.support.annotation.Nullable;
import android.app.Fragment;


/**
 * Created by zhy on 2017/12/23.
 */

public abstract class BaseFragment extends Fragment {
    private Activity activity;

    protected Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //ViewBinder.bind(this, getView());
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

    protected void setListener() {
    }

    public Context getContext(){
        if(activity == null){
            return MyApplication.getInstance();
        }
        return activity;
    }

    public void onAttach(Context context){
        super.onAttach(context);
        activity = getActivity();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
//    protected PlayService getPlayService() {
//        PlayService playService = AppCache.getPlayService();
//        if (playService == null) {
//            throw new NullPointerException("play service is null");
//        }
//        return playService;
//    }

 //   @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        PermissionReq.onRequestPermissionsResult(requestCode, permissions, grantResults);
//    }
}
