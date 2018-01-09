package com.hfad.zhyops;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.hfad.zhyops.Utils.Preferences;
import com.hfad.zhyops.ops.JobInfo;

/**
 * Created by zhy on 2017/12/29.
 */

public  class frag_submit extends PreferenceFragment implements Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {
        private Preference preferenceCallback;
        private Preference preferenceCustomSummary;
        private Preference preferenceEditText;
        private Preference preferenceEditTexttest;
        private Preference preferenceJob;
        private Preference preferencePro;
        private Preference preferenceStore;
        private Preference preferenceTotal;
        private Preference preferenceArl;
        private ListPreference preferenceMemory;
        private Preference preferenceDisk;
        private Preference preferencePriority;
        private Preference preferenceTimeSlice;
        private Preference job_alg;
        private Preference pro_alg;

        private JobInfo jobInfo;
        private CharSequence pro_alg_char;
        private CharSequence job_alg_char;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            this.addPreferencesFromResource(R.xml.submit_preferences);

            preferenceEditText = this.findPreference("input_jobtest");
            preferenceEditText.setOnPreferenceChangeListener(this);

            preferenceTimeSlice = this.findPreference("preference_timeslice");
            job_alg = this.findPreference("submit_job");
            pro_alg = this.findPreference("submit_pro");

            preferenceCallback = this.findPreference("preference_priority");
            preferenceCallback.setOnPreferenceChangeListener(this);

            preferencePro = this.findPreference(getString(R.string.submit_pro_alg));
            preferenceJob = this.findPreference(getString(R.string.submit_job_alg));
            preferenceStore = this.findPreference("memory_rq");
            preferenceStore.setOnPreferenceChangeListener(this);
            preferencePro.setOnPreferenceChangeListener(this);
            preferenceJob.setOnPreferenceChangeListener(this);


//            preferencePro.setSummary(getSummary(Preferences.getFilterSize(), R.array.filter_size_entries, R.array.filter_size_entry_values));
//            preferenceJob.setSummary(getSummary(Preferences.getFilterTime(), R.array.filter_time_entries, R.array.filter_time_entry_values));
        }

        @Override
        public boolean onPreferenceChange(final Preference preference, final Object newValue) {
            if (preference.equals(preferenceCallback)) {
                final int value = (int) newValue;
                Toast.makeText(getActivity(), "New value is " + value, Toast.LENGTH_SHORT).show();
                return true;
            }
            if (preference.equals(preferencePro)) {
                //把preference这个Preference强制转化为ListPreference类型
                ListPreference preferencePro=(ListPreference)preference;
                //获取ListPreference中的实体内容
                CharSequence[] entries=preferencePro.getEntries();
                //获取ListPreference中的实体内容的下标值
                int index=preferencePro.findIndexOfValue((String)newValue);
                //把listPreference中的摘要显示为当前ListPreference的实体内容中选择的那个项目
                preferencePro.setSummary(entries[index]);

                return true;
            } else if (preference.equals(preferenceJob)) {
                ListPreference preferenceJob=(ListPreference)preference;
                CharSequence[] entries=preferenceJob.getEntries();
                int index=preferenceJob.findIndexOfValue((String)newValue);
                preferenceJob.setSummary(entries[index]);
                return true;
            }else if(preference.equals(preferenceEditText)){

                //动态改变summary的值
                if(((String) newValue).trim().equals(""))
                    preference.setSummary(R.string.Job_name);
                else
                    preference.setSummary(getResources().getString(R.string.testEdit)+newValue);
            }else if (preference.equals(preferenceStore)) {
                ListPreference preferenceStore = (ListPreference) preference;
                CharSequence[] entries = preferenceStore.getEntries();
                int index = preferenceStore.findIndexOfValue((String) newValue);
                preferenceStore.setSummary(entries[index]);
                return true;
            }
            return false;
        }

        private String getSummary(String value, int entries, int entryValues) {
            String[] entryArray = getResources().getStringArray(entries);
            String[] entryValueArray = getResources().getStringArray(entryValues);
            for (int i = 0; i < entryValueArray.length; i++) {
                String v = entryValueArray[i];
                if (TextUtils.equals(v, value)) {
                    return entryArray[i];
                }
            }
            return entryArray[0];
        }





        public JobInfo getJobData(){
            if(isAdded()){
                preferenceArl = this.findPreference("preference_arltime");
                preferenceTotal = this.findPreference("preference_totaltime");
                preferenceMemory = (ListPreference) this.findPreference("memory_rq");
                preferenceDisk = this.findPreference("disk_rq");
                preferencePriority = this.findPreference("preference_priority");


                int i = 0, num = 0;
                CharSequence[] entries=preferenceMemory.getEntries();
                String[] items = getResources().getStringArray(R.array.memory_require_entries);
                CharSequence[] entryValues=preferenceMemory.getEntryValues();
                for(; i < entries.length; i++){
                    if(items[i].equals(preferenceMemory.getSummary())){
                        num = i;
                    }
                }
                //新建JobInfo对象
                jobInfo = new JobInfo();
                jobInfo.setJobName(preferenceEditText.getSummary().toString());
                jobInfo.setArlTime(Integer.valueOf(preferenceArl.getSummary().toString()));
                jobInfo.setTotalTime(Integer.valueOf(preferenceTotal.getSummary().toString()));
                jobInfo.setMemoryNeed(Integer.valueOf(entryValues[num].toString()));
                Log.i("zhy", "memory:" + entryValues[num].toString());
                jobInfo.setDiskNeed(Integer.valueOf(preferenceDisk.getSummary().toString()));
                jobInfo.setPriority(Integer.valueOf(preferencePriority.getSummary().toString()));

                //preferenceTotal.getSummary().toString();
                return jobInfo;
            }
            return null;
        }

        public void getAlgData(String job, String pro){
            Log.i("zhy", "run getalgdata function");
            job = job_alg.getSummary().toString();
            pro = pro_alg.getSummary().toString();
            Log.i("zhy", "find out "+ job + " + "+ pro);
        }

        public void setJobInfo(JobInfo jobInfo){
            Log.i("zhy", "setjobinfo after memory:" + jobInfo.getMemoryNeed());

            preferenceEditText.setSummary(jobInfo.getJobName());
            preferenceArl.setSummary(jobInfo.getArlTime() + "");
            preferenceTotal.setSummary(jobInfo.getTotalTime() + "");
            preferenceMemory.setSummary(jobInfo.getMemoryNeed() + "KB");

            preferenceDisk.setSummary(jobInfo.getDiskNeed() + "");
            preferencePriority.setSummary(jobInfo.getPriority() + "");
        }

        public CharSequence getTimeSlice(){
            return preferenceTimeSlice.getSummary();
        }

        public CharSequence getProAlg(){
            pro_alg_char = pro_alg.getSummary();
            Log.i("zhy", "pro:   "+ pro_alg_char);
            return pro_alg_char;
        }

        public CharSequence getJobAlg(){
            job_alg_char = job_alg.getSummary();
            return job_alg_char;
        }

        public void setTimeSlice(CharSequence timeslice){
            job_alg.setSummary(timeslice);
        }

        public void setProAlg(CharSequence pro){
            pro_alg.setSummary(pro);
        }

        public void setJobAlg(CharSequence job){
            job_alg.setSummary(job);
        }



        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener( this );
//            preferenceMemory.setSummary("60hhhh");
        }

        @Override
        public void onPause() {
            super.onPause();
           // preferenceMemory.setSummary("60hhhh");
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener( this );
        }


        @Override
        public void onSharedPreferenceChanged(final SharedPreferences sharedPreferences, final String key) {
            if (preferenceCustomSummary != null && key.equals(preferenceCustomSummary.getKey())) {
                final int value = sharedPreferences.getInt(key, 0);
                preferenceCustomSummary.setSummary("My custom summary text. Value is " + value);
            }
            else if(preferenceJob != null && key.equals(preferenceJob.getKey())){
                //preferenceJob.setSummary("Current value is " + dialog.getText();
                //preferenceJob.setSummary(getResources().getString(R.string.download_Task_summary)+newValue);
            }

//            Preference pref = findPreference(key);
//            if (pref instanceof EditTextPreference) {
//                EditTextPreference etp = (EditTextPreference) pref;
//                pref.setSummary(etp.getText());
//            }
        }
    }
