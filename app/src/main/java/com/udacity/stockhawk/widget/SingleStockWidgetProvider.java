package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;

import timber.log.Timber;

/**
 * Created by FBrigati on 09/01/2017.
 */
//@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SingleStockWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds){
        //perform this loop for every App Widget in this provider
        Timber.d("Inside Widget.. number of widgets: " + appWidgetIds.length);
        context.startService(new Intent(context, StockWidgetIntentService.class));
        }

}
