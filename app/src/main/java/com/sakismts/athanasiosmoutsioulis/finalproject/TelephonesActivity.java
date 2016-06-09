package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TelephonesActivity extends AppCompatActivity {
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    ExpandableListView listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telephones);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


       
        listview = (ExpandableListView) findViewById(R.id.expandableListView);

        //setGroupIndicatorToRight();

        prepareListData();
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
        listview.setAdapter(listAdapter);
        listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                Toast.makeText(
                        getApplicationContext(),
                        listDataHeader.get(groupPosition)
                                + " : "
                                + listDataChild.get(
                                listDataHeader.get(groupPosition)).get(
                                childPosition), Toast.LENGTH_SHORT)
                        .show();


                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listDataChild.get(
                            listDataHeader.get(groupPosition)).get(
                            childPosition)));
                    if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                                123);
                        return false;
                    }
                    startActivity(intent);


                return false;
            }
        });




    }
    private void setGroupIndicatorToRight() {
		/* Get the screen width */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;

        listview.setIndicatorBounds(width - getDipsFromPixel(35), width
                - getDipsFromPixel(5));
    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("General enquiries");
        listDataHeader.add("Undergraduate enquiries");
        listDataHeader.add("Postgraduate enquiries");
        listDataHeader.add("Industry enquiries");

        // Adding child data
        List<String> general_enquiries = new ArrayList<String>();
        general_enquiries.add("00441227823246");


        List<String> undergraduate_enquiries = new ArrayList<String>();
        undergraduate_enquiries.add("00441227823612");

        List<String> postgraduate_enquiries = new ArrayList<String>();
        postgraduate_enquiries.add("00441227827535");

        List<String> industry_enquiries = new ArrayList<String>();
        industry_enquiries.add("00441227823251");



        listDataChild.put(listDataHeader.get(0), general_enquiries); // Header, Child data
        listDataChild.put(listDataHeader.get(1), undergraduate_enquiries);
        listDataChild.put(listDataHeader.get(2), postgraduate_enquiries);
        listDataChild.put(listDataHeader.get(3), industry_enquiries);
        System.out.println(listDataChild);
    }




}
