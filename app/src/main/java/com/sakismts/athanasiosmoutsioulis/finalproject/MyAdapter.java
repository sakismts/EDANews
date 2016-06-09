package com.sakismts.athanasiosmoutsioulis.finalproject;

/**
 * Created by AthanasiosMoutsioulis on 18/02/16.
 */
import java.sql.SQLException;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{
    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
       holder.itemView.clearAnimation();
    }
    public boolean load_more = false;
    public boolean favoritesDisplay = false;
    public String test;
    public int last_count=0;
    private Context context;
    private LruCache<String, Bitmap> mMemoryCache;
    private NewsListFragment.OnArticleItemClickedListener listener;
    private NewsListFragment.OnShareButtonClickedListener listenerShare;
    private NewsListFragment.OnFavoriteButtonClickedListener listenerFavorite;
    private final static int TYPE_1=1;
    private final static int TYPE_2=2;
    private NewsModel model = NewsModel.getInstance();
    private final static int FADE_DURATION = 800; // in milliseconds




    public MyAdapter(Context context) {
        super();
        this.context = context;

        if (context instanceof NewsListFragment.OnArticleItemClickedListener){
            listener = (NewsListFragment.OnArticleItemClickedListener) context;

        }
        if (context instanceof NewsListFragment.OnShareButtonClickedListener){
            listenerShare = (NewsListFragment.OnShareButtonClickedListener) context;

        }
        if (context instanceof NewsListFragment.OnFavoriteButtonClickedListener){
            listenerFavorite = (NewsListFragment.OnFavoriteButtonClickedListener) context;

        }



    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && favoritesDisplay == false) {
            return TYPE_1;
        }
        return TYPE_2;
    }

    public MyAdapter(){

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }




    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtHeader;
        public TextView txtDesc;
        public NetworkImageView thumbnail;
        private TextView date;
        private LinearLayout favorites;
        private LinearLayout share;
        private ImageView favorites_icon;

        public ViewHolder(View v) {
            super(v);
            txtHeader = (TextView) v.findViewById(R.id.firstLine);
            txtDesc = (TextView) v.findViewById(R.id.secondLine);
            thumbnail = (NetworkImageView) v.findViewById(R.id.icon);
            thumbnail.setDefaultImageResId(R.drawable.kent_offline);
            thumbnail.setErrorImageResId(R.drawable.kent_offline);
            date = (TextView) v.findViewById(R.id.txt_date);
            favorites = (LinearLayout) v.findViewById(R.id.btn_favorites);
            favorites_icon = (ImageView) v.findViewById(R.id.heart_icon);
            favorites.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenerFavorite != null) {
                        boolean tmp_isFavorite ;



                        if ((int)favorites_icon.getTag() == R.drawable.heart_full){
                            tmp_isFavorite = true;
                            favorites_icon.setImageResource(R.drawable.heart_empty);
                            favorites_icon.setTag(R.drawable.heart_empty);
                        }
                        else{
                            tmp_isFavorite=false;
                            favorites_icon.setImageResource(R.drawable.heart_full);
                            favorites_icon.setTag(R.drawable.heart_full);
                        }

                        listenerFavorite.onFavoriteButtonClickedListener(getAdapterPosition(),tmp_isFavorite);


                    }
                }
            });

            share = (LinearLayout) v.findViewById(R.id.btn_share);
            share.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listenerShare != null) {

                        listenerShare.onShareButtonClickedListener(getAdapterPosition());

                    }

                }
            });

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listener != null) {
                        listener.onArticleItemClicked(getAdapterPosition());

                    }
                }
            });
        }

        public void setData(NewsItem newsitems, int position){
            txtHeader.setText(newsitems.getTitle());
            date.setText(newsitems.getDate());
            try {
                model.opendb_read();
                if (model.check_favorites(newsitems.getRecord_id()) == true){
                    favorites_icon.setImageResource(R.drawable.heart_full);
                    favorites_icon.setTag(R.drawable.heart_full);

                }
                else{
                    favorites_icon.setImageResource(R.drawable.heart_empty);
                    favorites_icon.setTag(R.drawable.heart_empty);
                }
                model.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }



            if(position == 0 && favoritesDisplay == false) {
                thumbnail.setImageUrl("http://www.efstratiou.info/projects/newsfeed/timthumb.php?w=300&src=" + newsitems.getImage_url(), NewsApp.getInstance().getImageLoader());
            txtDesc.setText(newsitems.getShort_description());

            }else{

                thumbnail.setImageUrl("http://www.efstratiou.info/projects/newsfeed/timthumb.php?w=100&src=" + newsitems.getImage_url(), NewsApp.getInstance().getImageLoader());


            }

        }

    }



    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        View v;
        // create a new view
        if (viewType == 1) {
           v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewheader, parent, false);
        }else{
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewitem, parent, false);


        }
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        NewsItem newsitem = model.getNewsList().get(position);
        holder.setData(newsitem, position);

        //if the user load more data the animation isn't enable
        if (load_more == false){
            SharedPreferences mypref=PreferenceManager.getDefaultSharedPreferences(this.context);
            boolean animation = mypref.getBoolean("animation_list", true);
            Log.i("preferences", String.valueOf(animation));
            if(animation == true) // if the preference switch for animation is on
        setScaleAnimation(holder.itemView);

        }

        if( position == last_count){
            last_count = getItemCount();
            load_more = false;
        }

    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    private void setScaleAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }



    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return model.getNewsList().size();
    }





}

