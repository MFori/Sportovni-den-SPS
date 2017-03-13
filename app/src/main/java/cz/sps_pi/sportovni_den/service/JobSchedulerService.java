package cz.sps_pi.sportovni_den.service;

import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import cz.sps_pi.sportovni_den.util.ConnectionManager;

/**
 * Created by Martin Forejt on 14.01.2017.
 * forejt.martin97@gmail.com
 */

@TargetApi(21)
public class JobSchedulerService extends JobService {

    public static final int UPLOAD_JOB_ID = 1;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        if (ConnectionManager.isOnline(this))
            startService(new Intent(JobSchedulerService.this, UploadDataService.class));
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
