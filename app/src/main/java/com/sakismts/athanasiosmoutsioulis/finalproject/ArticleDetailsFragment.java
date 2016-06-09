package com.sakismts.athanasiosmoutsioulis.finalproject;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;

import java.sql.SQLException;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleDetailsFragment extends Fragment implements NewsModel.OnDetailsUpdateListener{


    public OnUpdateList mListenerUpdate;
    public interface OnUpdateList {
        // TODO: Update argument type and name
        void onUpdateList(boolean update);

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mListenerUpdate = (OnUpdateList) activity;

            //model = (NewsModel.getInstance());

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListenerUpdate = null;

    }


    public TextView title;
    public TextView date;
    public TextView description;
    public TextView article_counter;
    public Button share_btn;
    public Button favorite_btn;
    public NetworkImageView image;
    public TextView eda;
    final NewsModel model = NewsModel.getInstance();
    private ProgressDialog progress;
    private int recordId;
    private int initial_article_id;
    private ScrollView myScrollView;
    View view;
    LinearLayout mylinearLayout;
    private String Share_link = "";
    private boolean isFavorite;
    int swipe =0;
    private int item_pos;
    private NewsItem record_item;


    //swipe left and right
    private Animation inFromRightAnimation() {

        Animation inFromRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromRight.setDuration(500);
        inFromRight.setInterpolator(new AccelerateInterpolator());
        return inFromRight;
    }


    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }



    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(500);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }



    private Animation outToRightAnimation() {
        Animation outtoRight = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoRight.setDuration(500);
        outtoRight.setInterpolator(new AccelerateInterpolator());
        return outtoRight;
    }



    final GestureDetector gesture = new GestureDetector(getActivity(),
            new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                       float velocityY) {

                    final int SWIPE_MIN_DISTANCE = 120;
                    final int SWIPE_MAX_OFF_PATH = 250;
                    final int SWIPE_THRESHOLD_VELOCITY = 200;
                    try {

                        if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                            return false;
                        if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE //check if the two points have the minimum distance
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i("Swipe", "Right to Left");
                           if (initial_article_id+1<  NewsModel.getInstance().getNewsList().size()){

                                NewsItem article = NewsModel.getInstance().getNewsList().get(initial_article_id+1);
                                recordId = NewsModel.getInstance().getNewsList().get(initial_article_id+1).getRecord_id();
                                initial_article_id= initial_article_id+1;
                               swipe = 1;


                                progress.show();
                               //load the new article
                                model.loadDataDetails(article.getRecord_id());
                           }else{
                               Snackbar.make(view, "This is the last article", Snackbar.LENGTH_LONG)
                                       .setAction("Action", null).show();

                           }

                        } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE
                                && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                            Log.i("Swipe", "Left to Right");
                            if (initial_article_id-1>=  0){

                                NewsItem article = NewsModel.getInstance().getNewsList().get(initial_article_id-1);
                                recordId = NewsModel.getInstance().getNewsList().get(initial_article_id-1).getRecord_id();
                                initial_article_id= initial_article_id-1;
                                swipe = -1;

                                progress.show();
                                //load the new article
                                model.loadDataDetails(article.getRecord_id());
                            }else{
                                Snackbar.make(view, "This is the first article", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                        }
                    } catch (Exception e) {
                        // nothing
                    }
                    return super.onFling(e1, e2, velocityX, velocityY);
                }
            });



    public ArticleDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.details_view, container, false);

        title = (TextView) view.findViewById(R.id.detail_title);
        date = (TextView) view.findViewById(R.id.detail_date);
        description = (TextView) view.findViewById(R.id.detail_description);
        image = (NetworkImageView) view.findViewById(R.id.detail_image);
        share_btn = (Button) view.findViewById(R.id.detail_share);
        favorite_btn = (Button) view.findViewById(R.id.detail_favorite);
        image.setDefaultImageResId(R.drawable.kent_offline);
        image.setErrorImageResId(R.drawable.kent_offline);
        eda = (TextView) view.findViewById(R.id.txt_eda);
        article_counter = (TextView) view.findViewById(R.id.article_count);
        mylinearLayout = (LinearLayout) view.findViewById(R.id.details_ll);
        myScrollView = (ScrollView) view.findViewById(R.id.scrollView);
        //on share button click
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent share = new Intent(Intent.ACTION_SEND);
                String shareBody = Share_link;
                share.setType("text/plain"); // might be text, sound, whatever
                share.putExtra(Intent.EXTRA_SUBJECT, "Subject here...");
                share.putExtra(Intent.EXTRA_TEXT, shareBody);

                startActivity(Intent.createChooser(share, "share"));
            }
        });
        //on favourite button click
        favorite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setUpdateFavorites(true);
                mListenerUpdate.onUpdateList(true);
                if (isFavorite == false) {
                    NewsItem tmp = model.getNewsList().get(initial_article_id);
                    String tmp_url = Share_link;
                    favorite_btn.setBackgroundResource(R.drawable.heart_full);
                    model.getFavoritesList().add(tmp);
                    try {
                        model.open();
                        model.addNews(true, record_item.getRecord_id(), record_item.getTitle(), record_item.getShort_description(), "", record_item.getDate(), record_item.getImage_url(),record_item.getWebUrl());
                        model.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    isFavorite = true;
                    // System.out.println(model.getFavoritesList());

                } else {
                    System.out.println("The size of favorites is : " + model.getFavoritesList().size());

                    favorite_btn.setBackgroundResource(R.drawable.heart_empty);
                    model.delete_from_favorites(record_item.getRecord_id());
                    //delete the record item from favorites list
                    Iterator<NewsItem> it = model.getFavoritesList().iterator();
                    while (it.hasNext()) {
                        if (it.next().getRecord_id()== record_item.getRecord_id()) {
                            it.remove();
                            // If you know it's unique, you could `break;` here
                        }
                    }
                    //model.getFavoritesList().remove(model.getFavoritesList().indexOf(record_item));
                    System.out.println("The size of favorites is : " + model.getFavoritesList().size());
                    isFavorite = false;


                }
            }
        });
        share_btn.setVisibility(View.GONE);
        favorite_btn.setVisibility(View.GONE);

        title.setVisibility(View.GONE);
        date.setVisibility(View.GONE);
        image.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        eda.setVisibility(View.GONE);
        swipe=0;

        model.setDetailsUpdateListener(this);
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading Details...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);
        progress.getWindow().setGravity(Gravity.BOTTOM);
       

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                myScrollView.requestDisallowInterceptTouchEvent(true);
                return gesture.onTouchEvent(event);
            }
        });

        return view;

    }



    public void updateDetails(int id)
    {
        //load the article tha tas selected
        initial_article_id = id;
        article_counter.setText("< "+Integer.toString(initial_article_id)+" of "+ Integer.toString(NewsModel.getInstance().getNewsList().size())+" >");
        NewsItem article = NewsModel.getInstance().getNewsList().get(id);
        Share_link = NewsModel.getInstance().getNewsList().get(id).getWebUrl();
        recordId = NewsModel.getInstance().getNewsList().get(id).getRecord_id();
        progress.show();
        model.loadDataDetails(article.getRecord_id());


    }



    @Override
    public void onDetailsUpdateListener(NewsItem newsItem, boolean error_json, boolean error_internet) {
        record_item = newsItem;
        if (error_internet){
            //in case there isn't internet connection load the article form the database
            try {
                model.opendb_read();
                newsItem=model.readDetails(recordId);
                record_item = newsItem;
                model.close();
                //load data to views
                progress.dismiss();
                title.setText(newsItem.getTitle());
                date.setText(newsItem.getDate());
                //check for favorites
                try {
                    model.opendb_read();
                    if (model.check_favorites(newsItem.getRecord_id()) == true){
                        isFavorite = true;
                        favorite_btn.setBackgroundResource(R.drawable.heart_full);

                    }
                    else{
                        isFavorite = false;
                        favorite_btn.setBackgroundResource(R.drawable.heart_empty);

                    }
                    model.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }




                if (newsItem.getDescription().length()==0)
                    description.setText(newsItem.getShort_description()+"...");
                else
                description.setText(newsItem.getDescription());
                article_counter.setText("<  " + Integer.toString(initial_article_id+1) + " of " + Integer.toString(NewsModel.getInstance().getNewsList().size()) + "  >");

                if (swipe == 1){
                    mylinearLayout.startAnimation(inFromRightAnimation());
                    title.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    eda.setVisibility(View.VISIBLE);
                    share_btn.setVisibility(View.VISIBLE);
                    favorite_btn.setVisibility(View.VISIBLE);


                }else if (swipe == -1){

                    mylinearLayout.startAnimation(inFromLeftAnimation());
                    title.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    eda.setVisibility(View.VISIBLE);
                    share_btn.setVisibility(View.VISIBLE);
                    favorite_btn.setVisibility(View.VISIBLE);

                }else{
                    title.setVisibility(View.VISIBLE);
                    date.setVisibility(View.VISIBLE);
                    image.setVisibility(View.VISIBLE);
                    description.setVisibility(View.VISIBLE);
                    eda.setVisibility(View.VISIBLE);
                    share_btn.setVisibility(View.VISIBLE);
                    favorite_btn.setVisibility(View.VISIBLE);
                }


            } catch (SQLException e) {
                e.printStackTrace();
            }
            Toast.makeText(getActivity(), "You don't have internet connection", Toast.LENGTH_SHORT).show();
        }
        else if (error_json){
           // progress.dismiss();
            Toast.makeText(getActivity(),"Error Loading Web Data", Toast.LENGTH_SHORT).show();
        }
        else{
            //load the article
            progress.dismiss();
            title.setText(newsItem.getTitle());
            date.setText(newsItem.getDate());
            description.setText(newsItem.getDescription());
            image.setImageUrl("http://www.efstratiou.info/projects/newsfeed/timthumb.php?h=400&src=" + newsItem.getImage_url(), NewsApp.getInstance().getImageLoader());
            try {
                model.opendb_read();
                if (model.check_favorites(newsItem.getRecord_id()) == true){
                    isFavorite = true;
                    favorite_btn.setBackgroundResource(R.drawable.heart_full);
                    favorite_btn.setBackgroundResource(R.drawable.heart_full);

                }
                else{
                    isFavorite = false;
                    favorite_btn.setBackgroundResource(R.drawable.heart_empty);
                    favorite_btn.setBackgroundResource(R.drawable.heart_empty);
                }
                model.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }



            article_counter.setText("<  " + Integer.toString(initial_article_id + 1) + " of " + Integer.toString(NewsModel.getInstance().getNewsList().size()) + "  >");


            if (swipe == 1){
                mylinearLayout.startAnimation(inFromRightAnimation());
                title.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                eda.setVisibility(View.VISIBLE);
                share_btn.setVisibility(View.VISIBLE);
                favorite_btn.setVisibility(View.VISIBLE);


            }else if (swipe == -1){

                mylinearLayout.startAnimation(inFromLeftAnimation());
                title.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                eda.setVisibility(View.VISIBLE);
                share_btn.setVisibility(View.VISIBLE);
                favorite_btn.setVisibility(View.VISIBLE);

            }else{
                title.setVisibility(View.VISIBLE);
                date.setVisibility(View.VISIBLE);
                image.setVisibility(View.VISIBLE);
                description.setVisibility(View.VISIBLE);
                eda.setVisibility(View.VISIBLE);
                share_btn.setVisibility(View.VISIBLE);
                favorite_btn.setVisibility(View.VISIBLE);
            }
            try {
                //if there is an internet connection update the article with the full description
                model.open();
                model.updateNews(false, newsItem.getRecord_id(), newsItem.getTitle(), newsItem.getShort_description(), newsItem.getDescription(), newsItem.getDate(), newsItem.getImage_url(),newsItem.getWebUrl());
                model.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
