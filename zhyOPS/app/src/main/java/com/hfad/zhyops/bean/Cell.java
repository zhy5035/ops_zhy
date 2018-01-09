package com.hfad.zhyops.bean;

/**
 * Created by zhouchaoyuan on 2017/1/14.
 */

public class Cell {

    private int status;// 0没信息 ，3表示预定，2表示入住，1表示离店
    private String bookingName;//预定人姓名

    private String jobName; //作业名称
    private int arlTime; //提交时间 或 到达时间
    private int totalTime; //估计运行时间
    private int runTime; //已运行时间
    private int endTime; //结束时间
    private int memoryNeed; //内存所需时间
    private int diskNeed; //磁带所需数量
    private int priority; //优先级

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getBookingName() {
        return bookingName;
    }

    public void setBookingName(String bookingName) {
        this.bookingName = bookingName;
    }

    //zhy_ops

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getArlTime() {
        return arlTime;
    }

    public void setArlTime(Integer arlTime) {
        this.arlTime = arlTime;
    }


    public Integer getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getRunTime() {
        return runTime;
    }

    public void setRunTime(Integer runTime) {
        this.runTime = runTime;
    }

    public Integer getMemoryNeed() {
        return memoryNeed;
    }

    public void setMemoryNeed(Integer memoryNeed) {
        this.memoryNeed = memoryNeed;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getDiskNeed() {
        return diskNeed;
    }

    public void setDiskNeed(Integer diskNeed) {
        this.diskNeed = diskNeed;
    }
}
