package com.hfad.zhyops;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import com.hfad.zhyops.R;
import com.hfad.zhyops.Table.CustomAdapter;
import  com.hfad.zhyops.bean.Cell;
import  com.hfad.zhyops.bean.ColTitle;
import  com.hfad.zhyops.bean.RowTitle;
import com.hfad.zhyops.ops.DiskInfo;
import com.hfad.zhyops.ops.JobInfo;


import cn.zhouchaoyuan.excelpanel.ExcelPanel;


/**
 * Created by zhouchaoyuan on 2017/3/30.
 */

public class frag_storage extends Fragment  {

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd";
    public static final String TIME_FORMAT_PATTERN = "HH:mm";
    public static final long ONE_DAY = 24 * 3600 * 1000;
    public static final int PAGE_SIZE = 6;  //列
    public static final int ROW_SIZE = 8;  //行
    public static final int PAGE_SIZE_DISK = 4;  //列
    public static final int ROW_SIZE_DISK = 2;  //行
    public static final String[] READYQ_TABLE = {"作业名称", "开始时间", "已运行时间",
            "估计运行时间", "内存需要",  "优先级"};
    public static final String[] READYQ_TABLE_DISK = {"磁带机A", "磁带机B", "磁带机C",
            "磁带机D"};
    public static final String[][] DEFAULT_READYQ = {
            {"JOB1", "10:00", "25分钟", "15K", "2台", "3"},
            {"JOB2", "10:20", "30分钟", "60K", "1台", "1"},
            {"JOB3", "10:30", "10分钟", "50K", "3台", "4"},
            {"JOB4", "10:35", "20分钟", "10K", "2台", "2"},
            {"JOB5", "10:40", "15分钟", "30K", "2台", "5"},

            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
            {"", "", "", "", "", ""},
    };

    private ExcelPanel excelPanel;
    private ExcelPanel excelPanel2;
    private TextView testText;
    private ProgressBar progress;
    private ProgressBar progress_disk;
    private CustomAdapter adapter;
    private CustomAdapter adapter_disk;
    private List<RowTitle> rowTitles;
    private List<ColTitle> colTitles;
    private List<List<Cell>> cells;
    private SimpleDateFormat timeFormatPattern;
    private long moreStartTime;

    public List<JobInfo> _jobList;  //作业列表
    public List<DiskInfo> _diskList;      //磁带机使用信息列表
    private TextView disk_one;
    private TextView disk_two;
    private TextView disk_three;
    private TextView disk_four;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_frag_storage, container, false);
        excelPanel = (ExcelPanel) root.findViewById(R.id.content_memory);
        progress = (ProgressBar)root.findViewById(R.id.progress_readyq);
      //  testText = (TextView)root.findViewById(R.id.storage_test);
        disk_one = (TextView)root.findViewById(R.id.disk_one);
        disk_two = (TextView)root.findViewById(R.id.disk_two);
        disk_three = (TextView)root.findViewById(R.id.disk_three);
        disk_four = (TextView)root.findViewById(R.id.disk_four);



        //自定义 customAdapter  这里数据传到customAdapter,然后显示在界面上了，值得思考getActivity();
        adapter = new CustomAdapter(getActivity(), blockListener);
        adapter.disableFooter();// load more
        adapter.disableHeader();// load history
        excelPanel.setAdapter(adapter);

        //disk_table

        initData();

        return root;
    }

    private View.OnClickListener blockListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Cell cell = (Cell) view.getTag();
            if (cell != null) {
//                if (cell.getStatus() == 0) {
//                    Toast.makeText(getActivity(), "空房", Toast.LENGTH_SHORT).show();
//                } else if (cell.getStatus() == 1) {
//                    Toast.makeText(getActivity(), "已离店，离店人：" + cell.getBookingName(), Toast.LENGTH_SHORT).show();
//                } else if (cell.getStatus() == 2) {
//                    Toast.makeText(getActivity(), "入住中，离店人：" + cell.getBookingName(), Toast.LENGTH_SHORT).show();
//                } else if (cell.getStatus() == 3) {
//                    Toast.makeText(getActivity(), "预定中，离店人：" + cell.getBookingName(), Toast.LENGTH_SHORT).show();
//                }
            }
        }
    };

    private void initData() {
        moreStartTime = Calendar.getInstance().getTimeInMillis();
        timeFormatPattern = new SimpleDateFormat(TIME_FORMAT_PATTERN);
        //以上不需要

        //disk
        setDiskData();

        //创建列表
        rowTitles = new ArrayList<>();
        colTitles = new ArrayList<>();
        cells = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            cells.add(new ArrayList<Cell>());
        }
        loadData(moreStartTime, false);
    }




    private void loadData(long startTime, final boolean history) {
        //模拟网络加载
        Message message = new Message();
        message.arg1 = history ? 1 : 2;
        message.obj = new Long(startTime);
        loadDataHandler.sendMessageDelayed(message, 1);
    }

    private Handler loadDataHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            long startTime = (Long) msg.obj;
            List<RowTitle> rowTitles1 = genRowData(startTime);
            List<List<Cell>> cells1 = genCellData();
            if (msg.arg1 == 1) {//history
                rowTitles.addAll(0, rowTitles1);
                for (int i = 0; i < cells1.size(); i++) {
                    cells.get(i).addAll(0, cells1.get(i));
                }

                //加载了数据之后偏移到上一个位置去
                if (excelPanel != null) {
                    excelPanel.addHistorySize(PAGE_SIZE);
                }
            } else {
                moreStartTime += ONE_DAY * PAGE_SIZE;
                rowTitles.addAll(rowTitles1);
                for (int i = 0; i < cells1.size(); i++) {
                    cells.get(i).addAll(cells1.get(i));
                }
            }
            if (colTitles.size() == 0) {
                colTitles.addAll(genColData());
            }
            progress.setVisibility(View.GONE);
            adapter.setAllData(colTitles, rowTitles, cells);
            adapter.enableFooter();
            adapter.enableHeader();
        }
    };

    //====================================模拟生成数据==========================================
    private List<RowTitle> genRowData(long startTime) {
        List<RowTitle> rowTitles = new ArrayList<>();
        //Random random = new Random();
        for (int i = 0; i < PAGE_SIZE; i++) {
            RowTitle rowTitle = new RowTitle();
            //rowTitle.setDateString(timeFormatPattern.format(startTime + i * ONE_DAY));
            //rowTitle.setWeekString(weekFormatPattern.format(startTime + i * ONE_DAY));
            rowTitle.setWeekString(READYQ_TABLE[i]);
            rowTitles.add(rowTitle);
        }
        return rowTitles;
    }

    private List<ColTitle> genColData() {
        List<ColTitle> colTitles = new ArrayList<>();
        for (int i = 0; i < ROW_SIZE; i++) {
            ColTitle colTitle = new ColTitle();
            if (i < 20) {
                colTitle.setRoomNumber("" + i);
            } else {
                colTitle.setRoomNumber("" + (i - 10));
            }
            colTitles.add(colTitle);
        }
        return colTitles;
    }

    public List<List<Cell>> genCellData() {
        List<List<Cell>> cells = new ArrayList<>();
        if(_jobList.isEmpty()){
            //testText.setText("_jobList is empty now");
            Log.i("zhy", "_jobList is empty now");
        }
        else{
            for(int i = 0; i < _jobList.size(); i++){
                JobInfo job = _jobList.get(i);
                //此处为   waitInMemory
                if(job.getStatus() == JobInfo.jobStatus.waitInMemory){
                    List<Cell> cellList = new ArrayList<>();
                    cells.add(cellList);
                    //进行每一行赋值
                    Cell cell0 = new Cell();
                    cell0.setBookingName(job.getJobName());
                    cellList.add(cell0);

                    Cell cell1 = new Cell();
                    cell1.setBookingName(job.getStartTime() + "");
                    cellList.add(cell1);

                    Cell cell2 = new Cell();
                    cell2.setBookingName(job.getRunTime() + "");
                    cellList.add(cell2);

                    Cell cell3 = new Cell();
                    cell3.setBookingName(job.getTotalTime() + "");
                    cellList.add(cell3);

                    Cell cell4 = new Cell();
                    cell4.setBookingName(job.getMemoryNeed() + "K");
                    cellList.add(cell4);

                    Cell cell5 = new Cell();
                    cell5.setBookingName(job.getPriority() + "");
                    cellList.add(cell5);
                }
            }
        }
        return cells;
    }

    public void getList(List<JobInfo> jobList, List<DiskInfo> diskList){
        Log.i("zhy", "what is null object");
        _jobList = new ArrayList<>(jobList);
        _diskList = new ArrayList<>(diskList);
    }
    //disk_table zhy
    public void setDiskData(){
        if(!_diskList.isEmpty()){
            disk_one.setText(_diskList.get(0).getDiskHolder());
            disk_two.setText(_diskList.get(1).getDiskHolder());
            disk_three.setText(_diskList.get(2).getDiskHolder());
            disk_four.setText(_diskList.get(3).getDiskHolder());
        }
    }

    public void update(){
        setDiskData();
        cells.clear();
        for (int i = 0; i < ROW_SIZE; i++) {
            cells.add(new ArrayList<Cell>());
        }
        loadData(moreStartTime, false);
    }

    public void clear(){
        _jobList.clear();
        cells.clear();
        for (int i = 0; i < ROW_SIZE; i++) {
            cells.add(new ArrayList<Cell>());
        }
        loadData(moreStartTime, false);
        disk_one.setText("nothing");
        disk_two.setText("nothing");
        disk_three.setText("nothing");
        disk_four.setText("nothing");
    }
}
