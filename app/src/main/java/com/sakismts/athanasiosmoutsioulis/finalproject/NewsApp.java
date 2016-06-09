package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by AthanasiosMoutsioulis on 07/03/16.
 */
public class NewsApp extends Application {




    private ImageLoader imageLoader;
    static private NewsApp instance;
    private RequestQueue requestQueue;

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public static NewsApp getInstance() {
        return instance;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        requestQueue = Volley.newRequestQueue(this);
        int cacheSize = 4 * 1024 *1024;
        imageLoader = new ImageLoader(requestQueue, new LruBitmapCache(cacheSize));




    }
}
