package com.hfad.zhyops;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.Preference;



import android.app.FragmentTransaction;


import android.os.Bundle;

import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;

import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.hfad.zhyops.Table.ExcelFragment;
import com.hfad.zhyops.Utils.Bind;
import com.hfad.zhyops.Utils.Extras;
import com.hfad.zhyops.ops.JobDispatch;
import com.hfad.zhyops.ops.JobInfo;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.hfad.zhyops.R.*;


public class MainActivity extends BaseActivity implements View.OnClickListener,
        ViewPager.OnPageChangeListener
{

    private static final int RUNTIMESLICE = 1; //运行时间间隔


    private ViewPager mViewPager;
    private TextView tvSubmit;
    private TextView tvReadyq;
    private TextView tvResult;
    @Bind(id.run_bar)
    private FrameLayout runBar;
    private ImageView clock;

    private frag_submit fragSubmit;
    private frag_result fragResult;
    private frag_storage fragStorage;
    private frag_readyq fragReadyq;
    private ExcelFragment excelFragment;
    private MyFragmentAdapter adapter;
    //private  SettingsFragment setfragment;


    private JobDispatch jobDispatch;
    private TextView clock_text;
    private TextView resultTest;
    private TextView StoageTest;
    private TextView runningJob;
    private int now_time;

    private int fonftSize;


    private boolean isPlayFragmentShow = false;
    private MenuItem timerItem;

    private ViewPager mframecontent;
    private PageAdapter myFragmentAdapter;

    private BoomMenuButton bmb;
    private TextView aroundTime;
    private TextView weightedTime;

    //frasubmit 界面
    CharSequence pro_alg, job_alg, time_slice;
    JobInfo job_submit;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getSupportActionBar().hide();//隐藏掉整个ActionBar
        setContentView(layout.activity_main);

        //初始化视图
        setupView();
        setupData();
        parseIntent();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        fonftSize = sharedPreferences.getInt("preference_font_size",
                this.getResources().getInteger(R.integer.font_size_default_value));

        //按钮悬浮
        final BoomMenuButton bn = (BoomMenuButton) findViewById(id.bmb);
        bn.setOnTouchListener(new View.OnTouchListener() {
            int[] temp = new int[] { 0, 0 };

            public boolean onTouch(View v, MotionEvent event) {
                int eventaction = event.getAction();

                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                switch (eventaction) {

                    case MotionEvent.ACTION_DOWN: // touch down so check if the
                        temp[0] = (int) event.getX();
                        temp[1] = y - v.getTop();
                        break;

                    case MotionEvent.ACTION_MOVE: // touch drag with the ball
                        v.layout(x - temp[0], y - temp[1], x + v.getWidth()
                                - temp[0], y - temp[1] + v.getHeight());

                        // v.postInvalidate();
                        break;

                    case MotionEvent.ACTION_UP:
                        break;
                }

                return false;
            }

        });
        bmb = (BoomMenuButton)findViewById(R.id.bmb);
        bmb.setShadowEffect(false);

        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++) {
            TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder();

            switch (i) {
                case 0:
                    builder.normalImageRes(R.drawable.cat)
                            .normalText("提交");
                    break;
                case 1:
                    builder.normalImageRes(R.drawable.butterfly)
                            .normalText("运行");
                    break;
                case 2:
                    builder.normalImageRes(R.drawable.bear)
                            .normalText("连续运行");
                    break;
                case 3:
                    builder.normalImageRes(R.drawable.deer)
                            .normalText("暂停");
                    break;
                case 4:
                    builder.normalImageRes(R.drawable.bee)
                            .normalText("清空");
                    break;
                case 5:
                    builder.normalImageRes(R.drawable.bee)
                            .normalText("重置");
                    break;

            }
            builder.listener(new OnBMClickListener() {
                @Override
                public void onBoomButtonClick(int index) {
                     BoomButtonClick(index);
                }
            });

            bmb.addBuilder(builder);
        }
    }

    private void setupData(){
        jobDispatch = new JobDispatch();
        jobDispatch.initLisk();
        jobDispatch.addJob("JOB1", 0, 25, 15, 2, 3);
        jobDispatch.addJob("JOB2", 20, 30, 60, 1, 1);
        jobDispatch.addJob("JOB3", 30, 10, 50, 3, 4);
        jobDispatch.addJob("JOB4", 35, 20, 10, 2, 2);
        jobDispatch.addJob("JOB5", 40, 15, 30, 2, 5);
        Log.i("zhy", "start test null object");
        if(jobDispatch.jobList != null){
            Log.i("zhy", "jobDispatch.jobList is not null object");
            excelFragment.getList(jobDispatch.jobList, jobDispatch.diskList);
            fragReadyq.getList(jobDispatch.jobList, jobDispatch.diskList);
            //fragStorage.getList(jobDispatch.jobList, jobDispatch.diskList);
        }else{
            Log.i("zhy", "null object");
        }
    }

    private void updateLisk(){
        JobInfo job;
        job = fragSubmit.getJobData();
        jobDispatch.addJob(job.getJobName(), job.getArlTime(), job.getTotalTime(),
                job.getMemoryNeed(),job.getDiskNeed(), job.getPriority());
        jobDispatch.sort_arlTime();
        if(jobDispatch.jobList != null){
            Log.i("zhy", "jobDispatch.jobList is not null object");
            excelFragment.getList(jobDispatch.jobList, jobDispatch.diskList);
            fragReadyq.getList(jobDispatch.jobList, jobDispatch.diskList);
           // fragStorage.getList(jobDispatch.jobList, jobDispatch.diskList);
        }else{
            Log.i("zhy", "null object");
        }
    }


    private void setupView() {
        now_time = 0;
        runningJob = (TextView)findViewById(R.id.job_runningName);
        mViewPager = (ViewPager) findViewById(id.viewpager);
        tvSubmit = (TextView)findViewById(id.top_submit);
        tvReadyq = (TextView)findViewById(id.top_readyq);
        tvResult = (TextView)findViewById(id.top_result);
        clock_text = (TextView)findViewById(id.top_clock);
        clock_text.setText(now_time + "");
        Drawable icon = getDrawable(drawable.clock);
        icon.setBounds(0,0,40,44);
        // setup view pager
        fragSubmit = new frag_submit();
        excelFragment = new ExcelFragment();
        fragReadyq = new frag_readyq();
        //fragResult = new frag_result();
        ArrayList<Fragment> fralist = new ArrayList();
        fralist.add(fragSubmit);
        fralist.add(fragReadyq);
        fralist.add(excelFragment);
        adapter = new MyFragmentAdapter(getFragmentManager(), fralist);
        mViewPager.setAdapter(adapter);
        mViewPager.setOffscreenPageLimit(2); //预加载的大小，至关重要
        tvSubmit.setSelected(true);
//       adapter中getitem获取frament
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void setListener() {
        tvSubmit.setOnClickListener(this);
        tvReadyq.setOnClickListener(this);
        tvResult.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    public void BoomButtonClick(int index){
        JobInfo job;
        switch(index) {
            case 0:
                updateLisk();
                fragReadyq.updateCellData();
                break;
            case 1:
                runAll();
                break;
            case 2:
                handler.post(task);//立即调用
                break;
            case 3:
                handler.removeCallbacks(task);
                break;

            case 4:
                now_time = jobDispatch.NOW_TIME;
                clock_text.setText(now_time + "");
                runningJob.setText("noBody");
                jobDispatch.MEMORY = 100;
                jobDispatch.DISK = 4;
                jobDispatch.clearAll();
                if(fragStorage != null){
                    fragStorage.update();
                    fragStorage.clear();
                }
                excelFragment.clear();
                fragReadyq.clear();
                break;
            case 5:
                now_time = jobDispatch.NOW_TIME;
                clock_text.setText(now_time + "");
                runningJob.setText("noBody");
                jobDispatch.MEMORY = 100;
                jobDispatch.DISK = 4;
                jobDispatch.resetList();

                now_time = jobDispatch.NOW_TIME;
                clock_text.setText(now_time + "");
                if(fragStorage != null)
                    fragStorage.update();
                adapter.notifyDataSetChanged(); //更新 frament
                break;
        }
    }

    private Handler handler = new Handler();

    private Runnable task =new Runnable() {
        public void run() {
            // TODOAuto-generated method stub
            handler.postDelayed(this,1000);//设置延迟时间，此处是1秒
            runAll();
        }
    };

    public void runAll(){
        if(jobDispatch.isStop()){
            Toast.makeText(this, "list 为 空 ", Toast.LENGTH_SHORT ).show();
            return;
        }



        //只为不更新submit 界面， 暂时的办法
       if(fragSubmit.getJobData() != null)
           job_submit = fragSubmit.getJobData();

        Log.i("zhy", "test " + job_submit.getJobName() + "  " + job_submit.getMemoryNeed());
        Log.i("zhy", "runall before memory:" + job_submit.getMemoryNeed());
        pro_alg = fragSubmit.getProAlg();
        job_alg = fragSubmit.getJobAlg();
        time_slice = fragSubmit.getTimeSlice();



        String job = job_alg.toString();
        String pro = pro_alg.toString();
        int main_time_slice = Integer.parseInt(time_slice.toString());
        Log.i("zhy", "hhhhhhhh"+ main_time_slice);
        switch (pro){
            case "简单轮转算法(RR)":
                Log.i("zhy", "run pro_reset_rr hhhh");
                jobDispatch.reset_rr(main_time_slice);
                break;
            case "动态优先数优先(DPS)":
                Log.i("zhy", "run pro_reset_dps hhhh");
                jobDispatch.reset_dps(main_time_slice);
                break;
            default:
                Log.i("zhy", "run pro_reset hhhh");
                jobDispatch.reset();
                break;
        }
        switch (job){
            case "先来先服务算法(FCFS)":
                Log.i("zhy", "run job_alg hhhh");
                jobDispatch.dispatch_job_fcfs();
                break;
            case "最短作业优先(SJF)":
                jobDispatch.dispatch_job_sjf();
                break;
            case "响应比高者优先(HRRN)":
                jobDispatch.dispatch_job_hrrn();
                break;
        }
        switch (pro){
            case "先来先服务算法(FCFS)":
                jobDispatch.dispatch_pro_fcfs();
                break;
            case "简单轮转算法(RR)":
                Log.i("zhy", "run  pro_alg_rr hhhh");
                jobDispatch.dispatch_pro_rr();
                break;
            case "静态优先数优先(SPS)":
                jobDispatch.dispatch_pro_sps();
                break;
            case "动态优先数优先(DPS)":
                jobDispatch.dispatch_pro_dps();
                break;
        }
        jobDispatch.NOW_TIME = jobDispatch.NOW_TIME + RUNTIMESLICE;
        //实时更新界面
        //实时显示时钟
        now_time = jobDispatch.NOW_TIME;
        clock_text.setText(now_time + "");
        if(fragStorage != null)
            fragStorage.update();
        adapter.notifyDataSetChanged(); //更新 frament

        Log.i("zhy", "runall after memory:" + fragSubmit.getJobData().getMemoryNeed());


       // Log.i("zhy", "runall after memory:" + job_submit.getMemoryNeed());
        fragSubmit.setJobInfo(job_submit);
        fragSubmit.setTimeSlice(time_slice);
        Log.i("zhy", "11111 runall after alg_pro:" + fragSubmit.getProAlg().toString());
        fragSubmit.setJobAlg(job_alg);
        fragSubmit.setProAlg(pro_alg);
        Log.i("zhy", "22222runall after alg_pro:" + fragSubmit.getProAlg().toString());
        runningJob.setText(jobDispatch.returnRun().getJobName());
        aroundTime = (TextView) this.findViewById(id.result_time);
        weightedTime = (TextView) this.findViewById(id.result_weightedTime);
        if(aroundTime != null){
            aroundTime.setText(excelFragment.getAroundTime() + "");
            weightedTime.setText(excelFragment.getWeightedAroundTime() + "");
        }
        //aroundTime.setText(pro + "   "+  job + "");
    }

    @Override
    public void onClick(View v) {
        int flag = 0;
        switch (v.getId()) {
            case id.iv_search:
                //startActivity(new Intent(this, SearchMusicActivity.class));
                break;
            case id.top_submit:
                mViewPager.setCurrentItem(0);
                break;
            case id.top_readyq:
                mViewPager.setCurrentItem(1);
                break;
            case id.top_result:
                mViewPager.setCurrentItem(2);
                break;
            case id.run_bar:
                if(flag == 0){
                    showPlayingFragment();
                    flag = 1;
                }else if(flag == 1){
                    hidePlayingFragment();
                    flag = 0;
                }
                break;

        }
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            tvSubmit.setSelected(true);
            tvReadyq.setSelected(false);
            tvResult.setSelected(false);
        } else if(position == 1) {
            tvSubmit.setSelected(false);
            tvReadyq.setSelected(true);
            tvResult.setSelected(false);
        }else if(position == 2) {
            tvSubmit.setSelected(false);
            tvReadyq.setSelected(false);
            tvResult.setSelected(true);
        }
    }

    public void onPageScrollStateChanged(int state) {
    }


    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(animator.fragment_slide_up, 0);

        if (fragStorage == null) {
            fragStorage = new frag_storage();
            //赋值
            fragStorage.getList(jobDispatch.jobList, jobDispatch.diskList);
            ft.replace(android.R.id.content, fragStorage);
        } else {
            fragStorage.getList(jobDispatch.jobList, jobDispatch.diskList);
            ft.show(fragStorage);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, animator.fragment_slide_down);
        ft.hide(fragStorage);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(Extras.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }


    @Override
    public void onBackPressed() {
        if (fragStorage != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }

        super.onBackPressed();
    }

    //保存数据，防止软件退出后台，数据丢失
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        outState.putInt(Keys.VIEW_PAGER_INDEX, mViewPager.getCurrentItem());
//        fragSubmit.onSaveInstanceState(outState);
//        fragResult.onSaveInstanceState(outState);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(final Bundle savedInstanceState) {
//        mViewPager.post(new Runnable() {
//            @Override
//            public void run() {
//                mViewPager.setCurrentItem(savedInstanceState.getInt(Keys.VIEW_PAGER_INDEX), false);
//                fragSubmit.onRestoreInstanceState(savedInstanceState);
//                fragResult.onRestoreInstanceState(savedInstanceState);
//            }
//        });
//    }
}
