package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by AthanasiosMoutsioulis on 23/02/16.
 */
public class NewsModel {


    private OnListUpdateListener listUpdateListener; //define var of the interface
    private OnSearchListUpdateListener SearchlistUpdateListener;
    private OnDetailsUpdateListener detailsUpdateListener; //define var of the interface
    private boolean error_json_encodeDetails = false;
    private boolean error_internet_connectionDetails = false;
    private boolean error_json_encode = false;
    private boolean error_internet_connection = false;
    private boolean error_json_encodeSearch = false;
    private boolean error_internet_connectionSearch = false;
    private NewsItem articleItem;

    public boolean isUpdateFavorites() {
        return updateFavorites;
    }

    public void setUpdateFavorites(boolean updateFavorites) {
        this.updateFavorites = updateFavorites;
    }

    private boolean updateFavorites = false;

    private int counter=0;

    //SQL
    private SQLiteDatabase database;
    private NewsDbHelper dbHelper;
    private Cursor dbCursor;



    //Constructor
    private NewsModel() {

    }

    public NewsModel(Context context) {
        ourInstance=this;
        dbHelper = new NewsDbHelper(NewsApp.getInstance());

    }
    ////////////
    private static NewsModel ourInstance;// = new NewsModel();

    public static NewsModel getInstance() {
        return ourInstance;
    }

    public ArrayList<String> getSearch_historyList() {
        return search_historyList;
    }

    public void setSearch_historyList(ArrayList<String> search_historyList) {
        this.search_historyList = search_historyList;
    }

    //search history
    private ArrayList<String> search_historyList = new ArrayList<String>();


    // Original List
    private ArrayList<NewsItem> original_newsList = new ArrayList<NewsItem>();
    public ArrayList<NewsItem> getOriginal_newsList() {
        return original_newsList;
    }

    public void setOriginal_newsList(ArrayList<NewsItem> original_newsList) {
        this.original_newsList = original_newsList;
    }
    //////////////

    // ArrayList NewsList
    private ArrayList<NewsItem> newsList = new ArrayList<NewsItem>();


    public void addNewsItem(String title, int record_id, String description, String date, String image_url,String weburl){
        newsList.add(new NewsItem(title,record_id,description,date,image_url,weburl));

    }

    public void setNewsList(ArrayList<NewsItem> newsList) {
        this.newsList = newsList;
    }

    public ArrayList<NewsItem> getNewsList() {

        return newsList;
    }
    ///////////

    //Data_loaded boolean
    private boolean Data_loaded = false;
    public boolean isData_loaded() {
        return Data_loaded;
    }

    public void setData_loaded(boolean data_loaded) {
        Data_loaded = data_loaded;
    }

    /// Loading List from the web
    public void loadData(int start){
        Log.i("Data Loading", "Loading web data");
        System.out.println("loading web data");

        JsonArrayRequest request = new JsonArrayRequest("http://www.efstratiou.info/projects/newsfeed/getList.php?start="+Integer.toString(start)+"&count=20",netListener,errorListener);
        NewsApp.getInstance().getRequestQueue().add(request);



    }

    public void deleteAllFromDB(){
        try {
            this.open();
            database.delete(dbHelper.TABLE_NAME, null, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void SearchData (String query){
        Log.i("Data Loading", "Loading Searchweb data");
        JsonArrayRequest request = new JsonArrayRequest("http://www.efstratiou.info/projects/newsfeed/getList.php?titleHas="+query,netSearchListener,errorSearchListener);
        NewsApp.getInstance().getRequestQueue().add(request);



    }

    //INTERFACE for loading list
    public interface  OnListUpdateListener{
        void onListUpdateListener(ArrayList<NewsItem> newslist, boolean error_json, boolean error_internet);

    }

    public void setListUpdateListener(OnListUpdateListener listUpdateListener) {
        this.listUpdateListener = listUpdateListener;
    }
    private void notifyListener(){


        if (listUpdateListener != null)

            listUpdateListener.onListUpdateListener(newsList, error_json_encode, error_internet_connection);

    }


    //response listener
    private Response.Listener<JSONArray> netListener = new Response.Listener<JSONArray>(){

        @Override
        public void onResponse(JSONArray response) {
                if(newsList.isEmpty()){
                    deleteAllFromDB();
                }

            try{
                for (int i=0; i< response.length();i++){
                    JSONObject objJson= response.getJSONObject(i);
                    String title = objJson.getString("title");
                    int record_id = objJson.getInt("record_id");
                    String s_description = objJson.getString("short_info");
                    String date = objJson.getString("date");
                    String image_url = objJson.getString("image_url");

                    String tmp_url = "http://www.eda.kent.ac.uk/school/news_article.aspx?aid="+Integer.toString(record_id);
                    NewsItem  tmpItem = new NewsItem(title, record_id, s_description, date, image_url,tmp_url);
                    //Insert to Sql
                    try{
                        open();
                        addNews(false,record_id,title,s_description,"",date,image_url,tmp_url);


                    }catch (SQLException e){
                        e.printStackTrace();

                    }
                    close();
                    newsList.add(tmpItem);
                    System.out.println("add");

                }
                error_internet_connection = false;
                error_json_encode = false;
            }catch (JSONException e){
                Log.e("Error in Json",e.toString());
                error_json_encode = true;
            }
            setOriginal_newsList(newsList);

            notifyListener();


        }

    };

    //Error Listener
    private Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error_internet_connection = true;
            Log.e("Error", error.toString());
            notifyListener();

        }
    };



    //private variable article item for storing the json data from the article
    public NewsItem getArticleItem() {
        return articleItem;
    }

    public void setArticleItem(NewsItem articleItem) {
        this.articleItem = articleItem;
    }

    // Listener for the Details of an Article

    public interface  OnDetailsUpdateListener{
        void onDetailsUpdateListener(NewsItem newsItem, boolean error_json, boolean error_internet);

    }

    public OnDetailsUpdateListener getDetailsUpdateListener() {
        return detailsUpdateListener;
    }

    public void setDetailsUpdateListener(OnDetailsUpdateListener detailsUpdateListener) {
        this.detailsUpdateListener = detailsUpdateListener;
    }

    /// Loading Details from the web
    public void loadDataDetails(int articleId){
        Log.i("Details Loading", "Loading web data Details");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,"http://www.efstratiou.info/projects/newsfeed/getItem.php?id="+Integer.toString(articleId),null,netListenerDetails,errorListenerDetails);
        NewsApp.getInstance().getRequestQueue().add(request);


    }

    //response listener
    private Response.Listener<JSONObject> netListenerDetails = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {

            try{
                    String title = response.getString("title");
                    int record_id = response.getInt("record_id");
                    String short_description = response.getString("short_info");
                    String date = response.getString("date");
                    String image_url = response.getString("image_url");
                    String description = response.getString("contents");
                    String webUrl = response.getString("web_page");


                articleItem = new NewsItem(title,short_description,date,image_url,record_id,description,webUrl);
                error_internet_connectionDetails = false;
                error_json_encodeDetails = false;
            }catch (JSONException e){
                Log.e("Error in Json",e.toString());
                error_json_encodeDetails = true;
            }
            notifyListenerDetails();

        }
    };

    //Error Listener
    private Response.ErrorListener errorListenerDetails = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error_internet_connectionDetails = true;
            Log.e("Error", error.toString());
            notifyListenerDetails();

        }
    };

    private void notifyListenerDetails(){

        if (detailsUpdateListener != null)

            detailsUpdateListener.onDetailsUpdateListener(articleItem, error_json_encodeDetails, error_internet_connectionDetails);

    }

    //INTERFACE for searching list
    public interface  OnSearchListUpdateListener{
        void onSearchListUpdateListener(ArrayList<NewsItem> newslist, boolean error_json, boolean error_internet);

    }
    public OnSearchListUpdateListener getSearchlistUpdateListener() {
        return SearchlistUpdateListener;
    }

    public void setSearchlistUpdateListener(OnSearchListUpdateListener searchlistUpdateListener) {
        SearchlistUpdateListener = searchlistUpdateListener;
    }

    private void notifyListenerSearch(){


        if (SearchlistUpdateListener != null)

            SearchlistUpdateListener.onSearchListUpdateListener(newsList, error_json_encodeSearch, error_internet_connectionSearch);

    }


    //response listener
    private Response.Listener<JSONArray> netSearchListener = new Response.Listener<JSONArray>(){

        @Override
        public void onResponse(JSONArray response) {
            // newsList.clear();
           /* try{
                open();
                database.delete(NewsDbHelper.TABLE_NAME,null,null);
                close();

            }catch (SQLException e){
            e.printStackTrace();

            }*/
            ArrayList<NewsItem> tmpList = new ArrayList<NewsItem>();
            try{
                for (int i=0; i< response.length();i++){
                    JSONObject objJson= response.getJSONObject(i);
                    String title = objJson.getString("title");
                    int record_id = objJson.getInt("record_id");
                    String s_description = objJson.getString("short_info");
                    String date = objJson.getString("date");
                    String image_url = objJson.getString("image_url");

                    String tmp_url = "http://www.eda.kent.ac.uk/school/news_article.aspx?aid="+Integer.toString(record_id);
                    NewsItem  tmpItem = new NewsItem(title, record_id, s_description, date, image_url,tmp_url);

                    tmpList.add(tmpItem);
                    System.out.println("add searc");

                }
                error_internet_connectionSearch = false;
                error_json_encodeSearch = false;
            }catch (JSONException e){
                Log.e("Error in Json",e.toString());
                error_json_encodeSearch = true;
            }

            setNewsList(tmpList);
            setOriginal_newsList(tmpList);
            notifyListenerSearch();


        }

    };

    //Error Listener
    private Response.ErrorListener errorSearchListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            error_internet_connectionSearch = true;
            Log.e("Error", error.toString());
            notifyListenerSearch();

        }
    };



    //////

    public ArrayList<NewsItem> getFavoritesList() {
        return favoritesList;
    }

    public void setFavoritesList(ArrayList<NewsItem> favoritesList) {
        this.favoritesList = favoritesList;
    }

    // favorites array
    private ArrayList<NewsItem>  favoritesList = new ArrayList<NewsItem>();

    //SQL Commands
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();

    }
    public void opendb_read() throws SQLException {
        database = dbHelper.getReadableDatabase();

    }

    public void close(){
        database.close();

    }

    public void addNews(boolean favorites,int id, String title,  String sdesc, String desc,String date,String imgurl,String weburl){
        ContentValues values = new ContentValues();
        values.put(NewsDbHelper.COLUMN_RECORD_ID, id);
        values.put(NewsDbHelper.COLUMN_TITLE, title);
        values.put(NewsDbHelper.COLUMN_SDESC, sdesc);
        values.put(NewsDbHelper.COLUMN_DESC, desc);
        values.put(NewsDbHelper.COLUMN_DATE, date);
        values.put(NewsDbHelper.COLUMN_IMGURL, imgurl);
        values.put(NewsDbHelper.COLUMN_URL, weburl);

        if (favorites == false){
            long insertId = database.insert(NewsDbHelper.TABLE_NAME, null, values);
        }
        else{
            long insertId = database.insert(NewsDbHelper.TABLE_NAME_FAVORITES, null, values);
        }
        Log.i("Database", "insert" + counter++);


    }


    public Cursor getnews(){
        String[] allColumns = {
                NewsDbHelper.COLUMN_ID,
                NewsDbHelper.COLUMN_RECORD_ID,
                NewsDbHelper.COLUMN_SDESC,
                NewsDbHelper.COLUMN_DATE,
                NewsDbHelper.COLUMN_IMGURL,
                NewsDbHelper.COLUMN_URL

        };
        dbCursor = database.query(NewsDbHelper.TABLE_NAME,allColumns,null,null,null,null,null,null);

        return dbCursor;

    }

    public boolean check_favorites(int record_id){
        int recordId = -1;
        String[] allColumns = {
                NewsDbHelper.COLUMN_ID,
                NewsDbHelper.COLUMN_RECORD_ID,
                NewsDbHelper.COLUMN_TITLE,
                NewsDbHelper.COLUMN_SDESC,
                NewsDbHelper.COLUMN_DATE,
                NewsDbHelper.COLUMN_IMGURL,
                NewsDbHelper.COLUMN_URL

        };

            dbCursor = database.query(NewsDbHelper.TABLE_NAME_FAVORITES,allColumns,NewsDbHelper.COLUMN_RECORD_ID+" = ?",new String[] { Integer.toString(record_id) },null,null,null,null);


        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {
            recordId = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_RECORD_ID));
            Log.i("DBCheck",Integer.toString(recordId));

        }

        if (recordId==-1)
            return false;
        else
            return true;
    }

    public void delete_from_favorites(int record_id){
        try {
            open();
            database.delete(NewsDbHelper.TABLE_NAME_FAVORITES, NewsDbHelper.COLUMN_RECORD_ID + " = ?", new String[]{Integer.toString(record_id)});
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //dbCursor = database.query(NewsDbHelper.TABLE_NAME_FAVORITES,allColumns,NewsDbHelper.COLUMN_RECORD_ID+" = ?",new String[] { Integer.toString(record_id) },null,null,null,null);



    }

    public void updateNews(boolean favorites,int id, String title,  String sdesc, String desc,String date,String imgUrl,String url){
        ContentValues newvalues = new ContentValues();
        newvalues.put(NewsDbHelper.COLUMN_RECORD_ID, id);
        newvalues.put(NewsDbHelper.COLUMN_TITLE, title);
        newvalues.put(NewsDbHelper.COLUMN_SDESC, sdesc);
        newvalues.put(NewsDbHelper.COLUMN_DESC, desc);
        newvalues.put(NewsDbHelper.COLUMN_DATE, date);
        newvalues.put(NewsDbHelper.COLUMN_IMGURL, imgUrl);
        newvalues.put(NewsDbHelper.COLUMN_URL, url);

        if (favorites == false){
            database.update(NewsDbHelper.TABLE_NAME, newvalues, NewsDbHelper.COLUMN_RECORD_ID+ " = "+Integer.toString(id), null);
        }
        else{
            database.update(NewsDbHelper.TABLE_NAME_FAVORITES, newvalues, NewsDbHelper.COLUMN_RECORD_ID +" = "+Integer.toString(id), null);
        }
        Log.i("Database", "insert" + counter++);


    }

    public NewsItem readDetails(int record_id){
        NewsItem  tmpItem = null;
        String[] allColumns = {
                NewsDbHelper.COLUMN_ID,
                NewsDbHelper.COLUMN_RECORD_ID,
                NewsDbHelper.COLUMN_TITLE,
                NewsDbHelper.COLUMN_SDESC,
                NewsDbHelper.COLUMN_DESC,
                NewsDbHelper.COLUMN_DATE,
                NewsDbHelper.COLUMN_URL

        };

        dbCursor = database.query(NewsDbHelper.TABLE_NAME,allColumns,NewsDbHelper.COLUMN_RECORD_ID+" = ?",new String[] { Integer.toString(record_id) },null,null,null,null);

        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {

            int recordId = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_RECORD_ID));
            String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_TITLE));
            String SDesc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_SDESC));
            String Desc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DESC));
            String date = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DATE));
            String url = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_URL));
            tmpItem = new NewsItem(title,SDesc,date,"",recordId,Desc,url);


        }

        return tmpItem;



    }

    public void read_db(boolean favorites){
        ArrayList<NewsItem>  tmpList = new ArrayList<NewsItem>();
        String[] allColumns = {
                NewsDbHelper.COLUMN_ID,
                NewsDbHelper.COLUMN_RECORD_ID,
                NewsDbHelper.COLUMN_TITLE,
                NewsDbHelper.COLUMN_SDESC,
                NewsDbHelper.COLUMN_DESC,
                NewsDbHelper.COLUMN_DATE,
                NewsDbHelper.COLUMN_IMGURL,
                NewsDbHelper.COLUMN_URL

        };
        if(favorites == false){
            dbCursor = database.query(NewsDbHelper.TABLE_NAME,allColumns,null,null,null,null,null,null);
        }else{
            dbCursor = database.query(NewsDbHelper.TABLE_NAME_FAVORITES,allColumns,null,null,null,null,null,null);
        }

        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {
            int recordId = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_RECORD_ID));
            String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_TITLE));
            String SDesc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_SDESC));
            String Desc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DESC));
            String date = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DATE));
            String url = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_URL));
            String imgurl = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_IMGURL));
            NewsItem  tmpItem = new NewsItem(title, recordId, SDesc, date,imgurl, url);
            tmpList.add(tmpItem);
            Log.i("DB",Integer.toString(recordId));


        }
        if(favorites == true){
            setFavoritesList(tmpList);

        }else{
            setOriginal_newsList(tmpList);
        }
        setNewsList(tmpList);


    }

    public void reload_favorites(){
        ArrayList<NewsItem>  tmpList = new ArrayList<NewsItem>();
        String[] allColumns = {
                NewsDbHelper.COLUMN_ID,
                NewsDbHelper.COLUMN_RECORD_ID,
                NewsDbHelper.COLUMN_TITLE,
                NewsDbHelper.COLUMN_SDESC,
                NewsDbHelper.COLUMN_DESC,
                NewsDbHelper.COLUMN_DATE,
                NewsDbHelper.COLUMN_IMGURL,
                NewsDbHelper.COLUMN_URL

        };

            dbCursor = database.query(NewsDbHelper.TABLE_NAME_FAVORITES,allColumns,null,null,null,null,null,null);


        //dbCursor.moveToFirst();
        while (dbCursor.moveToNext()) {
            int recordId = dbCursor.getInt(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_RECORD_ID));
            String title = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_TITLE));
            String SDesc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_SDESC));
            String Desc = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DESC));
            String date = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_DATE));
            String url = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_URL));
            String imgurl = dbCursor.getString(dbCursor.getColumnIndexOrThrow(NewsDbHelper.COLUMN_IMGURL));
            NewsItem  tmpItem = new NewsItem(title, recordId, SDesc, date,imgurl, url);
            tmpList.add(tmpItem);
            Log.i("DB",Integer.toString(recordId));


        }

            setFavoritesList(tmpList);


    }


    public boolean isEmpty(){
        dbCursor = getnews();
        long size = dbCursor.getCount();
        dbCursor.close();
        return (size==0);

    }



}
