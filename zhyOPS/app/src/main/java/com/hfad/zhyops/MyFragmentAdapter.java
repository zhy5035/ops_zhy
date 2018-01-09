package com.hfad.zhyops;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by zhy on 2018/1/1.
 */

public class MyFragmentAdapter extends FragmentStatePagerAdapter {
    private int length ;

    private int tag = 0;

    private ArrayList<Fragment> mFragments;//碎片数组
    private int mCount;
    FragmentManager fm ;
    public MyFragmentAdapter(FragmentManager fm , ArrayList<Fragment> mFragments) {
        super(fm);
        this.fm = fm;
        this.length = mFragments.size();
        this.mFragments = mFragments;
        FragmentTransaction mTransaction = fm.beginTransaction();
    }

    @Override
    public int getItemPosition(Object object) {
//        if(tag == 1)
//            return POSITION_UNCHANGED; //这个是必须的
//        else
            return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {
//        if(position == 0)
//            tag = 1;
//        else
//            tag = 0;


        int size = mFragments.size();
        if(position >= size)
            return mFragments.get(size - 1);
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        if(mCount > 0) {
            return mCount;
        } else {
            return length;
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    public void setFragments(ArrayList<Fragment> fragments) {
        if(this.mFragments != null){
            FragmentTransaction ft = fm.beginTransaction();
            for(Fragment f:this.mFragments){
                ft.remove(f);
            }
            ft.commit();
            ft=null;
            fm.executePendingTransactions();
        }
        this.mFragments = fragments;
        notifyDataSetChanged();
    }

    public void setMaxCount(int count) {
        if(mCount < length) {
            if(mCount != count) {
                mCount = count;
                notifyDataSetChanged();
            }
        }
    }

    public void setmFragments(ArrayList<Fragment> mFragments) {
        this.mFragments = mFragments;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        
    }
}

