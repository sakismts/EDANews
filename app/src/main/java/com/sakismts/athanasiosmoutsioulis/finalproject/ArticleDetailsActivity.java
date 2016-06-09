package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ArticleDetailsActivity extends AppCompatActivity implements ArticleDetailsFragment.OnUpdateList{
    private boolean isupdated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_article_details);
        Intent intent = getIntent();
        int itemId = intent.getIntExtra("ITEM_ID", 0);



        //Get fragment
        FragmentManager fragmentManager = getFragmentManager();
        ArticleDetailsFragment fragment = (ArticleDetailsFragment) fragmentManager.findFragmentById(R.id.details_fragment);
        fragment.updateDetails(itemId);
        fragment.mListenerUpdate = this;
    }


    @Override
    public void onUpdateList(boolean update) {
       //If the user make the article favourite the app should update the recycler view with all the news
        isupdated = update;
    }




}
