package com.udacity.stockhawk.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.sync.QuoteSyncJob;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import timber.log.Timber;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

public final class PrefUtils {

    private PrefUtils() {
    }

    public static Set<String> getStocks(Context context) {
        String stocksKey = context.getString(R.string.pref_stocks_key);
        String initializedKey = context.getString(R.string.pref_stocks_initialized_key);
        String[] defaultStocksList = context.getResources().getStringArray(R.array.default_stocks);

        HashSet<String> defaultStocks = new HashSet<>(Arrays.asList(defaultStocksList));
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        boolean initialized = prefs.getBoolean(initializedKey, false);

        if (!initialized) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(initializedKey, true);
            editor.putStringSet(stocksKey, defaultStocks);
            editor.apply();
            return defaultStocks;
        }
        return prefs.getStringSet(stocksKey, new HashSet<String>());
    }

    private static void editStockPref(Context context, String symbol, Boolean add) {
        String key = context.getString(R.string.pref_stocks_key);
        Set<String> stocks = getStocks(context);
        Timber.d("Stock set: " + stocks.toString());
        if (add) {
            stocks.add(symbol);
        } else {
            stocks.remove(symbol);
            Timber.d("Stock symbol removed: " + symbol);
        }

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, stocks);
        editor.apply();
    }



    public static void addStock(Context context, String symbol) {
        editStockPref(context, symbol, true);
    }

    public static void removeStock(Context context, String symbol) {
        editStockPref(context, symbol, false);
    }

    public static String getDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String defaultValue = context.getString(R.string.pref_display_mode_default);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(key, defaultValue);
    }

    public static void toggleDisplayMode(Context context) {
        String key = context.getString(R.string.pref_display_mode_key);
        String absoluteKey = context.getString(R.string.pref_display_mode_absolute_key);
        String percentageKey = context.getString(R.string.pref_display_mode_percentage_key);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String displayMode = getDisplayMode(context);

        SharedPreferences.Editor editor = prefs.edit();

        if (displayMode.equals(absoluteKey)) {
            editor.putString(key, percentageKey);
        } else {
            editor.putString(key, absoluteKey);
        }
        editor.apply();
    }

    //todo: check why this method returns error when StockStatus isn't commented
    @SuppressWarnings("ResoureType")
    static public //@QuoteSyncJob.StockStatus
    int getStockStatus(Context c){
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        return sp.getInt(c.getString(R.string.pref_stock_status_key), QuoteSyncJob.STOCK_STATUS_UNKNOWN);
    }

    static public void setStockStatus(Context c, @QuoteSyncJob.StockStatus int stockStatus){
        Timber.d("Setting stock status" + stockStatus);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor spe = sp.edit();
        spe.putInt(c.getString(R.string.pref_stock_status_key), stockStatus);
        spe.commit();
        //Toast.makeText("Chosen stock does not exist!",)
    }

}
