package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

import timber.log.Timber;

/**
 * Created by FBrigati on 12/01/2017.
 */

public class StockWidgetIntentService extends IntentService {

    public StockWidgetIntentService() {
        super("StockWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                SingleStockWidgetProvider.class));

        Timber.d("Inside appWidget, items: " + appWidgetIds.length);

        for (int appWidgetId : appWidgetIds){

            RemoteViews views = new RemoteViews(getPackageName(),
                    R.layout.widget_detail);
            views.setTextViewText(R.id.widget_symbol, "GOOG");
            views.setTextViewText(R.id.widget_price, "100");

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

    }
}
