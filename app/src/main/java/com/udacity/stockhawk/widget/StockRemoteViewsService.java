package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Binder;
import android.support.v4.content.CursorLoader;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;


/**
 * Created by FBrigati on 11/01/2017.
 */

public class StockRemoteViewsService extends RemoteViewsService {

    static final int INDEX_ID = 0;
    static final int INDEX_SYMBOL =1;
    static final int INDEX_PRICE = 2;
    static final int INDEX_ABSOLUTE_CHANGE=3;
    static final int INDEX_PERCENTAGE_CHANGE = 4;
    static final int INDEX_HISTORY = 5;
    static DecimalFormat dollarFormat;
    static DecimalFormat dollarFormatWithPlus;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
            }

            @Override
            public void onDataSetChanged() {
            if (data != null){
                data.close();
            }
                Timber.d("Inside onDataSetChanged.. getting cursor");
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(
                        Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS,
                        null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_item);

                Timber.d("Inside getViewAt.. cursor has items: " + data.getCount());

                views.setTextViewText(R.id.widget_symbol, data.getString(Contract.Quote.POSITION_SYMBOL));
                views.setTextViewText(R.id.widget_price, dollarFormat.format((data.getFloat(Contract.Quote.POSITION_PRICE))));

                float rawAbsoluteChange = data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE);

                views.setTextViewText(R.id.widget_change, dollarFormatWithPlus.format(data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE)));

                if (rawAbsoluteChange > 0) {
                    views.setInt(R.id.widget_change, "setTextColor", Color.GREEN);
                } else {
                    views.setInt(R.id.widget_change, "setTextColor", Color.RED);
                }

                //Fill intent for detail view...
                final Intent fillIntent = new Intent();

                Uri intentUri = Contract.Quote.makeUriForStock(data.getString(Contract.Quote.POSITION_SYMBOL));
                fillIntent.setData(intentUri);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getLong(Contract.Quote.POSITION_ID);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
