package com.hfad.zhyops;




import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhy on 2017/12/23.
 */

public class FragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragments = new ArrayList<>();
    FragmentManager fm;

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
    }



    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
    }


    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return null;
    }

    //    @Override
//    public int getItemPosition(Object object) {
//        return POSITION_NONE;
//    }

//


//
//    //private FragmentManager fm;
//
//    public FragmentAdapter(FragmentManager fm) {
//        super(fm);
//    }

//
//    @Override
//    /*
//     * 获取页面
//     */
//    public int getCount() {
//        return 3;
//    }
//
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//
//        if (position == 0)
//            removeFragment(container,position);
//        return super.instantiateItem(container, position);
//    }
//
//    private void removeFragment(ViewGroup container,int index) {
//        String tag = getFragmentTag(container.getId(), index);
//
//        Fragment fragment = fm.findFragmentByTag(tag);
//        if (fragment == null)
//            return;
//        FragmentTransaction ft = fm.beginTransaction();
//        ft.remove(fragment);
//        ft.commit();
//        ft = null;
//        fm.executePendingTransactions();
//    }
//
//    private String getFragmentTag(int viewId, int index) {
//        try {
//            Class<FragmentPagerAdapter> cls = FragmentPagerAdapter.class;
//            Class<?>[] parameterTypes = { int.class, long.class };
//            Method method = cls.getDeclaredMethod("makeFragmentName",
//                    parameterTypes);
//            method.setAccessible(true);
//            String tag = (String) method.invoke(this, viewId, index);
//            return tag;
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "";
//        }
//    }
//
//    public int getItemPosition(Object object) {
//        // TODO Auto-generated method stub
//        return POSITION_NONE;
//    }



}
