package com.hfad.zhyops.ops;

/**
 * Created by zhy on 2017/12/31.
 */

public class DiskInfo {
    private int diskId;       //磁带机ID
    private String diskHolder;  //磁带机占有者名称
    private int diskIsUse; // 磁带机是否被占用， 是则为 1， 没有被占用则为 0

    public void setDiskId(int diskId) {
        this.diskId = diskId;
    }

    public int getDiskId() {
        return diskId;
    }

    public void setDiskHolder(String diskHolder) {
        this.diskHolder = diskHolder;
    }

    public String getDiskHolder() {
        return diskHolder;
    }

    public void setDiskIsUse(int diskIsUse) {
        this.diskIsUse = diskIsUse;
    }

    public int getDiskIsUse() {
        return diskIsUse;
    }
}
