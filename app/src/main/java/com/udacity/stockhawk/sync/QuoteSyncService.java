package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import timber.log.Timber;


public class QuoteSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static QuoteSyncJob quoteJob = null;


    @Override
    public void onCreate(){
        Timber.d("Starting SyncService");
        synchronized (sSyncAdapterLock){
            if(quoteJob == null){
                quoteJob = new QuoteSyncJob(getApplicationContext(), true);
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return quoteJob.getSyncAdapterBinder();
    }
}
