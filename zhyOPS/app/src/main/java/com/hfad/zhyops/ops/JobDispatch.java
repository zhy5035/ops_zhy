package com.hfad.zhyops.ops;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;



/**
 * Created by zhy on 2017/12/31.
 */

public class JobDispatch {
    private static final int RUNTIMESLICE = 1;

    public int MEMORY = 100;  //主存空间（KB）
    public int DISK = 4;  //磁带机总数
    public int NOW_TIME = 0; //当前时间

    public int RR_TIMESLICE = 0; //简单轮转时间片
    public int DPS_TIMESLICE = 0; //动态优先算法时间片

    public List<JobInfo> jobList;  //作业列表
    public List<DiskInfo> diskList;      //磁带机使用信息列表

    public void initLisk(){
        jobList = new ArrayList<>();
        diskList = new ArrayList<>();
        for(int i = 0; i < DISK; i++){
            DiskInfo disk = new DiskInfo();
            disk.setDiskId(i);
            disk.setDiskHolder("nobody");
            disk.setDiskIsUse(0);
            diskList.add(disk);
        }
    }

    public boolean addJob(String jname, int jarlTime, int jtotalTime,
                               int jMemory, int jDisk, int jPriority )
    {
        if(jarlTime < NOW_TIME || jtotalTime == 0)
            return false;

        JobInfo job = new JobInfo();
        job.setJobName(jname);
        job.setArlTime(jarlTime);
        job.setTotalTime(jtotalTime);
        job.setStartTime(0);
        job.setEndTime(0);
        job.setRunTime(0);
        job.setMemoryNeed(jMemory);
        job.setDiskNeed(jDisk);
        job.setPriority(jPriority);
        job.setTimeSlice(0);

        if(jarlTime <= NOW_TIME) // this guy has arrived
        {
            job.setStatus(JobInfo.jobStatus.waitOutside);
        }
        else // not arrived yet
            job.setStatus(JobInfo.jobStatus.coming);
        jobList.add(job);
        return true;
    }

    //判断作业是否能从外存调入内存，得到内存及磁带机资源
    public boolean enableEntMemory(JobInfo job_ent){

        if(job_ent.getStatus().equals(JobInfo.jobStatus.waitOutside)){
            if(job_ent.getMemoryNeed() <= MEMORY){
                if(updateDiskInfo(job_ent.getDiskNeed(), job_ent.getJobName()))
                    return true;
            }
            return false;
        }
        else
            return false;
    }

    //作业调度算法
    public void dispatch_job_fcfs(){
        JobInfo job_fcfs;
        sort_arlTime();
        Log.i("zhy", getMinSlice() + "       test");
        int minSlice = getMinSlice();
        for(int i = 0; i < jobList.size(); i++){
            job_fcfs = jobList.get(i);
            if(enableEntMemory(job_fcfs)){
                job_fcfs.setStatus(JobInfo.jobStatus.waitInMemory);

                job_fcfs.setTimeSlice(minSlice);
                MEMORY = MEMORY - job_fcfs.getMemoryNeed();
                DISK = DISK - job_fcfs.getDiskNeed();
            }
        }
    }

    public void dispatch_job_sjf(){
        JobInfo job_sjf;
        sort_totalTime();
        Log.i("zhy", "zhy find"+NOW_TIME + "disk:"+ DISK);
        for(int i = 0; i < jobList.size(); i++){
            Log.i("zhy", "zhy find every cycle  memory:"+MEMORY);
            job_sjf = jobList.get(i);
            if(enableEntMemory(job_sjf)){
                Log.i("zhy", "zhy find"+DISK);
                job_sjf.setTimeSlice(getMinSlice());
                job_sjf.setStatus(JobInfo.jobStatus.waitInMemory);
                MEMORY = MEMORY - job_sjf.getMemoryNeed();
                DISK = DISK - job_sjf.getDiskNeed();
            }
        }
    }

    public void dispatch_job_hrrn(){
        JobInfo job_hrrn;
        sort_hrrn();
        for(int i = 0; i < jobList.size(); i++){
            job_hrrn = jobList.get(i);
            if(enableEntMemory(job_hrrn)){
                job_hrrn.setTimeSlice(getMinSlice());
                job_hrrn.setStatus(JobInfo.jobStatus.waitInMemory);
                MEMORY = MEMORY - job_hrrn.getMemoryNeed();
                DISK = DISK - job_hrrn.getDiskNeed();
            }
        }
    }

    //进程调度算法
    public void dispatch_pro_fcfs(){
        sort_arlTime();
        for(int i = 0; i < jobList.size(); i++){
            JobInfo pro_fcfs = jobList.get(i);
            if(pro_fcfs.getStatus().equals(JobInfo.jobStatus.waitInMemory)){
                Log.i("zhy", "zhy find pro_sps"+i);
                pro_fcfs = jobList.get(i);
                if(pro_fcfs.getRunTime() == 0){
                    pro_fcfs.setStartTime(NOW_TIME);
                }
                pro_fcfs.setStatus(JobInfo.jobStatus.running);
                int time = pro_fcfs.getRunTime() + RUNTIMESLICE;
                pro_fcfs.setRunTime(time);
                break;
            }

        }
    }

    //进程调度算法
    public void dispatch_pro_rr(){
        sort_arlTime();
        JobInfo pro_rr;
        if(exitRunningJob()){
            for(int i = 0; i < jobList.size(); i++){
                pro_rr = jobList.get(i);
                if(pro_rr.getStatus().equals(JobInfo.jobStatus.running)){
                    int time = pro_rr.getRunTime() + RUNTIMESLICE;
                    pro_rr.setRunTime(time);
                }
            }
        }
        else{
            sort_timeslice();
            for(int i = 0; i < jobList.size(); i++){
                pro_rr = jobList.get(i);
                if(pro_rr.getStatus().equals(JobInfo.jobStatus.waitInMemory)){
                    Log.i("zhy", "zhy find pro_sps"+i);
                    pro_rr = jobList.get(i);
                    if(pro_rr.getRunTime() == 0){
                        pro_rr.setStartTime(NOW_TIME);
                    }
                    pro_rr.setStatus(JobInfo.jobStatus.running);
                    int time = pro_rr.getRunTime() + RUNTIMESLICE;
                    pro_rr.setRunTime(time);
                    break;
                }

            }
        }

    }


    public void dispatch_pro_sps(){
        sort_priority();
        Log.i("zhy", "zhy find pro_sps here!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        for(int i = 0; i < jobList.size(); i++){
            JobInfo pro_sps = jobList.get(i);
            if(pro_sps.getStatus().equals(JobInfo.jobStatus.waitInMemory)){
                Log.i("zhy", "zhy find pro_sps"+i);
                pro_sps = jobList.get(i);
                if(pro_sps.getRunTime() == 0){
                    pro_sps.setStartTime(NOW_TIME);
                }
                pro_sps.setStatus(JobInfo.jobStatus.running);
                int time = pro_sps.getRunTime() + RUNTIMESLICE;
                pro_sps.setRunTime(time);
                break;
            }

        }
        //pro_sps.setRunTime(10);
    }

    public void dispatch_pro_dps(){
        sort_priority();
        JobInfo pro_sps;
        for(int i = 0; i < jobList.size(); i++){
            pro_sps = jobList.get(i);
            if(pro_sps.getStatus().equals(JobInfo.jobStatus.waitInMemory)){
                pro_sps = jobList.get(i);
                if(pro_sps.getRunTime() == 0){
                    pro_sps.setStartTime(NOW_TIME);
                }
                pro_sps.setStatus(JobInfo.jobStatus.running);
                int time = pro_sps.getRunTime() + RUNTIMESLICE;
                pro_sps.setRunTime(time);
                break;
            }

        }

//        if(exitRunningJob()){
//            for(int i = 0; i < jobList.size(); i++){
//                pro_sps = jobList.get(i);
//                if(pro_sps.getStatus().equals(JobInfo.jobStatus.running)){
//                    int time = pro_sps.getRunTime() + RUNTIMESLICE;
//                    pro_sps.setRunTime(time);
//                }
//            }
//        }
//        else{
//            for(int i = 0; i < jobList.size(); i++){
//                pro_sps = jobList.get(i);
//                if(pro_sps.getStatus().equals(JobInfo.jobStatus.waitInMemory)){
//                    pro_sps = jobList.get(i);
//                    if(pro_sps.getRunTime() == 0){
//                        pro_sps.setStartTime(NOW_TIME);
//                    }
//                    pro_sps.setStatus(JobInfo.jobStatus.running);
//                    int time = pro_sps.getRunTime() + RUNTIMESLICE;
//                    pro_sps.setRunTime(time);
//                    break;
//                }
//
//            }
//        }
    }

    //得到当前作业列表中，作业属性中的最小时间片
    public int getMinSlice(){
        sort_timeslice();
        int mintimeSlice = 100;
        JobInfo pro;
        for(int i = 0; i < jobList.size(); i++){
            pro = jobList.get(i);
            if(pro.getStatus().equals(JobInfo.jobStatus.waitInMemory)||
                    pro.getStatus().equals(JobInfo.jobStatus.running)){
                mintimeSlice = pro.getTimeSlice();
                break;
            }
        }
        return  mintimeSlice;
    }

    private boolean exitRunningJob(){
        JobInfo runjob;
        for(int i = 0; i< jobList.size(); i++){
            runjob = jobList.get(i);
            if(runjob.getStatus().equals(JobInfo.jobStatus.running))
                return true;
        }
        return  false;
    }

    //重置算法
    public void reset(){
        JobInfo reset;
        for(int i = 0; i < jobList.size(); i++){
            reset = jobList.get(i);
            if(reset.getStatus() == JobInfo.jobStatus.running){
                if(reset.getRunTime() == reset.getTotalTime()){
                    reset.setEndTime(NOW_TIME);
                    reset.setStatus(JobInfo.jobStatus.finshed);
                    MEMORY = MEMORY + reset.getMemoryNeed();
                    DISK = DISK + reset.getDiskNeed();

                    //更新disk
                    DiskInfo disk_reset;
                    for(int j = 0; j < diskList.size(); j++){
                        disk_reset = diskList.get(j);
                        if(disk_reset.getDiskHolder().equals(reset.getJobName())){
                            disk_reset.setDiskIsUse(0);
                            disk_reset.setDiskHolder("nobody");
                        }
                    }
                }
                else
                    reset.setStatus(JobInfo.jobStatus.waitInMemory);
            }
            else if(reset.getStatus() == JobInfo.jobStatus.coming){
                if(reset.getArlTime() <= NOW_TIME){
                    reset.setStatus(JobInfo.jobStatus.waitOutside);
                }
            }
        }
    }


    public void reset_rr(int rr_timeslice){
        JobInfo reset_rr;
        for(int i = 0; i < jobList.size(); i++){
            reset_rr = jobList.get(i);
            if(reset_rr.getStatus() == JobInfo.jobStatus.running){
                if(reset_rr.getRunTime() == reset_rr.getTotalTime()){
                    reset_rr.setEndTime(NOW_TIME);
                    reset_rr.setStatus(JobInfo.jobStatus.finshed);
                    MEMORY = MEMORY + reset_rr.getMemoryNeed();
                    DISK = DISK + reset_rr.getDiskNeed();
                    RR_TIMESLICE = 0; //重置时间片

                    //更新disk
                    DiskInfo disk_reset;
                    for(int j = 0; j < diskList.size(); j++){
                        disk_reset = diskList.get(j);
                        if(disk_reset.getDiskHolder().equals(reset_rr.getJobName())){
                            disk_reset.setDiskIsUse(0);
                            disk_reset.setDiskHolder("nobody");
                        }
                    }
                }
                else
                {
                    //时间片加 1 ，若时间片达到轮转时间则 job 停止 run
                    RR_TIMESLICE = RR_TIMESLICE + RUNTIMESLICE;

                    if(RR_TIMESLICE == rr_timeslice){
                        RR_TIMESLICE = 0;

                        reset_rr.setTimeSlice(reset_rr.getTimeSlice() + 1);
                        reset_rr.setStatus(JobInfo.jobStatus.waitInMemory);
                    }


                }
            }
            else if(reset_rr.getStatus() == JobInfo.jobStatus.coming){
                if(reset_rr.getArlTime() <= NOW_TIME){
                    reset_rr.setStatus(JobInfo.jobStatus.waitOutside);
                }
            }
        }
    }


    public void reset_dps(int dps_timeslice){
        JobInfo reset_dps;
        for(int i = 0; i < jobList.size(); i++){
            reset_dps = jobList.get(i);
            if(reset_dps.getStatus() == JobInfo.jobStatus.running){
                if(reset_dps.getRunTime() == reset_dps.getTotalTime()){
                    reset_dps.setEndTime(NOW_TIME);
                    reset_dps.setStatus(JobInfo.jobStatus.finshed);
                    MEMORY = MEMORY + reset_dps.getMemoryNeed();
                    DISK = DISK + reset_dps.getDiskNeed();
                    DPS_TIMESLICE = 0;
                    //更新disk
                    DiskInfo disk_reset;
                    for(int j = 0; j < diskList.size(); j++){
                        disk_reset = diskList.get(j);
                        if(disk_reset.getDiskHolder().equals(reset_dps.getJobName())){
                            disk_reset.setDiskIsUse(0);
                            disk_reset.setDiskHolder("nobody");
                        }
                    }
                }
                else{
                    DPS_TIMESLICE = DPS_TIMESLICE + RUNTIMESLICE;
                    if(DPS_TIMESLICE == dps_timeslice){
                        DPS_TIMESLICE = 0;
                        int priority = reset_dps.getPriority();
                        if(priority != 0)
                            reset_dps.setPriority(priority - 1);

                        reset_dps.setTimeSlice(reset_dps.getTimeSlice() + 1);
                    }
                    reset_dps.setStatus(JobInfo.jobStatus.waitInMemory);
                }

            }
            else if(reset_dps.getStatus() == JobInfo.jobStatus.coming){
                if(reset_dps.getArlTime() <= NOW_TIME){
                    reset_dps.setStatus(JobInfo.jobStatus.waitOutside);
                }
            }
        }
    }




    public void run(){
        if(isStop())
            return;
        dispatch_job_fcfs();
        dispatch_pro_rr();
        //dispatch_pro_rr();
        NOW_TIME = NOW_TIME +RUNTIMESLICE;
    }





    public boolean isStop(){
        for(int i = 0; i < jobList.size(); i++){
            if(jobList.get(i).getStatus() != JobInfo.jobStatus.finshed)
                return false;
        }
        return true;
    }

    private boolean lackOfDisk(int num){
        int rest = 0;
        if(num > diskList.size())
            return false;
        for(int i = 0; i < diskList.size(); i++){
            if(diskList.get(i).getDiskIsUse() == 0){
                rest++;
            }
        }
        if(rest < num)
            return false;
        else
            return true;
    }


    private boolean updateDiskInfo(int need_num, String holderName){
        DiskInfo disk_update;
        int num = 0;
        if(lackOfDisk(need_num)){
            for(int i = 0; i < diskList.size(); i++){
                disk_update = diskList.get(i);
                if(disk_update.getDiskIsUse() == 0 && num < need_num){
                    disk_update.setDiskIsUse(1);
                    disk_update.setDiskHolder(holderName);
                    num++;
                }
            }
            return true;
        }
        else
            return false;
    }


    public void sort_arlTime(){
        Collections.sort(jobList, new Comparator<JobInfo>() {
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                //前者大于后者
                if (o1.getArlTime() > o2.getArlTime()) {
                    return 1;
                }
                //小于同理
                if (o1.getArlTime() < o2.getArlTime()) {
                    return -1;
                }
                //如果返回0则认为前者与后者相等
                return 0;
            }
        });
    }

    private void sort_totalTime(){
        Collections.sort(jobList, new Comparator<JobInfo>() {
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                if (o1.getTotalTime() > o2.getTotalTime()) {
                    return 1;
                }
                if (o1.getTotalTime() < o2.getTotalTime()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private void sort_priority(){
        Collections.sort(jobList, new Comparator<JobInfo>() {
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                if (o1.getPriority() < o2.getPriority()) {
                    return 1;
                }
                if (o1.getPriority() > o2.getPriority()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    private void sort_hrrn(){
        Collections.sort(jobList, new Comparator<JobInfo>() {
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                int nt = NOW_TIME,
                        rsp_l = (nt - o1.getArlTime()) * o2.getTotalTime(),
                        rsp_r = (nt - o2.getArlTime()) * o1.getTotalTime();
                if(rsp_l < rsp_r)
                    return -1;
                if(rsp_l > rsp_r)
                    return 1;
                return 0;
            }
        });
    }

    private void sort_timeslice(){
        Collections.sort(jobList, new Comparator<JobInfo>() {
            @Override
            public int compare(JobInfo o1, JobInfo o2) {
                if (o1.getTimeSlice() > o2.getTimeSlice()) {
                    return 1;
                }
                if (o1.getTimeSlice() < o2.getTimeSlice()) {
                    return -1;
                }
                return 0;
            }
        });
    }

    public JobInfo returnRun(){
        JobInfo pro_sps = new JobInfo();
        for(int i = 0; i < jobList.size(); i++){
            pro_sps = jobList.get(i);
            if(pro_sps.getStatus().equals(JobInfo.jobStatus.running)){
                break;
            }

        }
        return pro_sps;
    }

    public boolean listEmpty(){
        int num = 0;
        for(int i = 0; i< jobList.size(); i++){
            JobInfo job = jobList.get(i);
            if(job.getStatus().equals(JobInfo.jobStatus.finshed))
                num++;
        }
        if(num == jobList.size())
            return true;
        else
            return false;
    }

    public void clearAll(){
        if(!listEmpty()){
            jobList.clear();
            diskList.clear();
        }
        initLisk();
        NOW_TIME = 0;
    }

    public void resetList(){
        if(!listEmpty()){
            for(int i = 0; i< jobList.size(); i++){
                JobInfo job = jobList.get(i);
                job.setRunTime(0);
                job.setStartTime(0);
                job.setEndTime(0);
                job.setStatus(JobInfo.jobStatus.coming);
                job.setTimeSlice(0);
            }
            diskList.clear();
        }
        diskList = new ArrayList<>();
        for(int i = 0; i < DISK; i++){
            DiskInfo disk = new DiskInfo();
            disk.setDiskId(i);
            disk.setDiskHolder("nobody");
            disk.setDiskIsUse(0);
            diskList.add(disk);
        }
        NOW_TIME = 0;
    }
}
