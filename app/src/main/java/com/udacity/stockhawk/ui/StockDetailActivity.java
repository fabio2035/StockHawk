package com.udacity.stockhawk.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.udacity.stockhawk.R;

/**
 * Created by FBrigati on 06/01/2017.
 */

public class StockDetailActivity extends AppCompatActivity{

@Override
    protected void onCreate(Bundle savedInstanceState){
    super.onCreate(savedInstanceState);

    setContentView(R.layout.detail_activity);

    if (savedInstanceState == null){
        Bundle arguments = new Bundle();
        arguments.putParcelable(StockDetailFragment.DETAIL_URI, getIntent().getData());

        StockDetailFragment fragment = new StockDetailFragment();
        fragment.setArguments(arguments);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.stock_detail_container, fragment)
                .commit();
    }
}

}
