package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

import static android.R.attr.data;
import static android.R.attr.entries;

/**
 * Created by FBrigati on 06/01/2017.
 */

public class StockDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    //View references
    private String symbol;
    //private TextView mDivid;
    private TextView mPricetag;
    private TextView mVariation;
    private Toolbar mToolbar;
    private LineChart mChart;
    private Button mButton;


    public StockDetailFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        Bundle arguments = getArguments();
        if( arguments != null){
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail_ref, container, false);

        mPricetag = (TextView) rootView.findViewById(R.id.stock_price);

        mVariation = (TextView) rootView.findViewById(R.id.stock_variation);

        //mTitleText = (TextView) rootView.findViewById(R.id.stock_name);

        mToolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        mToolbar.setTitleTextColor(Color.WHITE);

        //Get chart view
        mChart = (LineChart) rootView.findViewById(R.id.chart);

        //mButton = (Button) rootView.findViewById(R.id.button_remove_stock);

        /*mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrefUtils.removeStock(getContext(), symbol);
                Toast.makeText(getContext(), "Stock Removed from list", Toast.LENGTH_LONG).show();
                mButton.setEnabled(false);
            }
        }); */

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(DETAIL_LOADER, null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri){
        return new CursorLoader(
                getActivity(),
                mUri,
                Contract.Quote.QUOTE_COLUMNS,
                null,
                null,
                null);
    }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            //here is where we fill chart data..

            String historyString = data.getString(Contract.Quote.POSITION_HISTORY);

            int i = 0;

            List<Entry> entries = new ArrayList<Entry>();

            final Map<Integer, String> valueMap = new HashMap<Integer, String>();

            //calculate quarterly values for compressed chart diagram...
            for (String retVal : historyString.split("\n") ){
                Timber.d("value for i=" + i + " is: " + retVal);
                i++;
                String[] something = retVal.split(", ");

                valueMap.put(i, millisToDate(Long.parseLong(something[0])));

                float xValue = (float) i;
                float yValue = (float) Double.parseDouble(something[1]);
                entries.add(new Entry(xValue,yValue));
            }

            Collections.sort(entries, new EntryXComparator());
            LineDataSet dataSet = new LineDataSet(entries, "Quote");
            LineData lineData = new LineData(dataSet);
            mChart.setData(lineData);

            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                @Override
                public String getFormattedValue(float value, AxisBase axis) {
                    Timber.d("Getting date value for: " + (Float) value);
                    return valueMap.get((int) value).toString();
                }
            };

            XAxis xAxis = mChart.getXAxis();
            xAxis.setGranularity(10);
            xAxis.setValueFormatter(formatter);
            mChart.setPinchZoom(true);
            mChart.fitScreen();

          //  mChart.setVisibleXRangeMaximum(30);
            mChart.invalidate(); //refresh

            //mTitleText.setText(data.getString(Contract.Quote.POSITION_SYMBOL));

            symbol = data.getString(Contract.Quote.POSITION_SYMBOL);

            mToolbar.setTitle(getString(R.string.stock_quote_toolbar_title, data.getString(Contract.Quote.POSITION_SYMBOL)));

            mPricetag.setText(data.getString(Contract.Quote.POSITION_PRICE));
            mPricetag.setContentDescription(getString(R.string.stock_price_label));

            mVariation.setText(Float.toString(data.getFloat(Contract.Quote.POSITION_ABSOLUTE_CHANGE)));
            mVariation.setContentDescription(getString(R.string.stock_variation));

//            mButton.setContentDescription(getString(R.string.button_remove_stock));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private String millisToDate(long millis){
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        return format1.format(cal.getTime());
    }

 }
