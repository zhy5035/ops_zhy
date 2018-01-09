package com.hfad.zhyops.ops;

/**
 * Created by zhy on 2017/12/31.
 */

public class JobInfo {

    public enum jobStatus{
        coming,
        waitInMemory,
        waitOutside,
        running,
        finshed
    }


    private String jobName; //作业名称
    private int arlTime; //提交时间 或 到达时间
    private int totalTime; //估计运行时间
    private int runTime; //已运行时间
    private int startTime; //开始时间
    private int endTime; //结束时间
    private int memoryNeed; //内存所需时间
    private int diskNeed; //磁带所需数量
    private int priority; //优先级
    private int timeSlice; //时间片
    private jobStatus status;


    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public int getArlTime() {
        return arlTime;
    }

    public void setArlTime(int arlTime) {
        this.arlTime = arlTime;
    }


    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public void setRunTime(int runTime) {
        this.runTime = runTime;
    }

    public int getMemoryNeed() {
        return memoryNeed;
    }

    public void setMemoryNeed(int memoryNeed) {
        this.memoryNeed = memoryNeed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getDiskNeed() {
        return diskNeed;
    }

    public void setDiskNeed(int diskNeed) {
        this.diskNeed = diskNeed;
    }

    public jobStatus getStatus() {
        return status;
    }

    public void setStatus(jobStatus status) {
        this.status = status;
    }

    public int getTimeSlice() {
        return timeSlice;
    }

    public void setTimeSlice(int timeSlice) {
        this.timeSlice = timeSlice;
    }
}
