package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;
import com.udacity.stockhawk.ui.StockDetailActivity;

import timber.log.Timber;

/**
 * Created by FBrigati on 09/01/2017.
 */
//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StocksWidgetProvider extends AppWidgetProvider {

    public static final String WIDGET_ACTION = "com.udacity.stockhawk.ACTION_DATA_UPDATED";
    //"com.example.android.sunshine.app.ACTION_DATA_UPDATED"

    //@Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        //perform this loop for every App Widget in this provider
        Timber.d("Inside Widget.. number of widgets: " + appWidgetIds.length);
        for (int appWidgetId : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.widget_detail);

            //Intent to launch main activity..
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 , intent, 0 );
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Set up the intent that starts the ViewService, which will
            // provide the views for this collection
            //Todo: Add verion check for items
            views.setRemoteAdapter(R.id.widget_list, new Intent(context, StockRemoteViewsService.class));

            Intent clickIntentTemplate = new Intent(context, StockDetailActivity.class);
            PendingIntent clickPendingIntentTemplate = android.support.v4.app.TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, clickPendingIntentTemplate);

            //Set empty view
            views.setEmptyView(R.id.widget_list, R.id.widget_empty);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        Timber.d("Intent caught: " + intent.getAction().toString());
        if (QuoteSyncJob.ACTION_DATA_UPDATED.equals(intent.getAction())) {
            Timber.d("Inside onReceive.. supposedly updating data...");
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                    new ComponentName(context, getClass()));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
        }
    }
}
