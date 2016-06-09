package com.sakismts.athanasiosmoutsioulis.finalproject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NewsListFragment.OnArticleItemClickedListener} interface
 * to handle interaction events.
 */
public class NewsListFragment extends Fragment implements  NewsModel.OnListUpdateListener, NewsModel.OnSearchListUpdateListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    public ProgressDialog progress;
    TextView tv_favorites;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsModel model = NewsModel.getInstance();
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int article_index =0;
    private int previousTotal = 0;// The total number of items in the dataset after the last load
    private boolean loading = true;// True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 0;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean isFavoritesDispayed = false;
    private boolean InternetConnection = true;
    private boolean searchmode = false;
    private boolean searchmodeEda = false;



    private OnArticleItemClickedListener mListener;
    private OnShareButtonClickedListener mShareListener;
    private OnFavoriteButtonClickedListener mfavoriteListener;


    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        //Recucler view set up
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLayoutManager.scrollToPosition(0);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyAdapter(getActivity());
        mRecyclerView.setAdapter(mAdapter);

        //Create SWIPE AND REFRESH
        tv_favorites = (TextView) view.findViewById(R.id.textView_favorites);

        mSwipeRefreshLayout=  (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary_dark);
        // the user swipe the top of recycler view to refresh articles
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                //clear the array and load new items to it

                if (searchmodeEda == true) {
                    model.getNewsList().clear();
                    searchmodeEda = false;
                }
                if (isFavoritesDispayed == false) {
                    if (searchmode == false) {
                        model.getNewsList().clear();
                        model.getOriginal_newsList().clear();
                        //delete data from the database
                        //model.deleteAllFromDB();

                        article_index = 0;
                        model.loadData(article_index);
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }

            }
        });

        if (model.isUpdateFavorites())
            mAdapter.notifyDataSetChanged();
        //Progress Dialog
        progress = new ProgressDialog(getActivity());
        progress.setMessage("Loading web data...");
        progress.setIndeterminate(false);
        progress.setCancelable(true);

        model.setListUpdateListener(this);
        model.setSearchlistUpdateListener(this);
        // If the application is loading data for the first time then the article start
        // will be 0 and function loadData is called
        if (model.isData_loaded() == false){
            progress.show();
            article_index=0;
            model.loadData(article_index);
            model.setData_loaded(true);


        }

        //Detection of the end of scrolling
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                visibleItemCount = mRecyclerView.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();

                if (mLayoutManager.findLastCompletelyVisibleItemPosition() == model.getNewsList().size() - 1) {

                    //bottom of recycler view
                    if (isFavoritesDispayed == false) {
                        if (searchmode == false && searchmodeEda == false){
                        article_index += 20; // add +20 items for the next load more
                        ((MyAdapter) mAdapter).load_more = true;
                        progress.show();
                        model.loadData(article_index); //the loadData will load data for article_index+20
                         }

                    }
                }

                /*if (loading) {
                    if (totalItemCount > previousTotal) {
                        loading = false;
                        previousTotal = totalItemCount;
                    }
                }
                if (!loading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + visibleThreshold)) {
                    // End has been reached
                    article_index += 20;
                    model.loadData(article_index);
                    Log.i("Yaeye!", "end called");

                    // Do something

                    loading = true;
                }*/
            }
        });


        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event


    @Override
    public void onResume() {
        super.onResume();
       if (model.isUpdateFavorites()){

           mAdapter.notifyDataSetChanged();
            model.setUpdateFavorites(false);
       }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        try {
            mListener = (OnArticleItemClickedListener) activity;
            mShareListener = (OnShareButtonClickedListener) activity;
            mfavoriteListener = (OnFavoriteButtonClickedListener) activity;
            //model = (NewsModel.getInstance());

        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        mShareListener = null;
    }

    @Override
    public void onListUpdateListener(ArrayList<NewsItem> newslist, boolean error_json, boolean error_internet) {

        if (error_internet){
            InternetConnection = false;
            progress.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"You don't have internet connection", Toast.LENGTH_SHORT).show();
            try {
                model.opendb_read();
                model.read_db(false);
                model.close();
                mAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        else if (error_json){
            InternetConnection = false;
            progress.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"Error Loading Web Data", Toast.LENGTH_SHORT).show();
            try {
                model.opendb_read();
                model.read_db(false);
                model.close();
                mAdapter.notifyDataSetChanged();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else {
            if (mAdapter!= null){

                progress.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();
                // in table case display initial the first article
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager.findFragmentById(R.id.details_fragment) !=  null) {
                    ArticleDetailsFragment fragment = (ArticleDetailsFragment) fragmentManager.findFragmentById(R.id.details_fragment);
                    fragment.updateDetails(0);

                }


        }
        }
    }

    @Override
    public void onSearchListUpdateListener(ArrayList<NewsItem> newslist, boolean error_json, boolean error_internet) {

        if (error_internet){
            InternetConnection = false;
            progress.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"You don't have internet connection", Toast.LENGTH_SHORT).show();


        }
        else if (error_json){
            InternetConnection = false;
            progress.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"Error Loading Web Data", Toast.LENGTH_SHORT).show();

        }
        else {
            if (mAdapter!= null){
                searchmodeEda = true;
                if (isFavoritesDispayed==true){
                isFavoritesDispayed = false;
                tv_favorites.setVisibility(View.GONE);
                    ((MyAdapter) mAdapter).favoritesDisplay = false;
                }
                progress.dismiss();
                mSwipeRefreshLayout.setRefreshing(false);
                mAdapter.notifyDataSetChanged();

            }
        }

    }

   public void UpdateList(){

       mAdapter.notifyDataSetChanged();

   }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */


    public interface OnArticleItemClickedListener {
        // TODO: Update argument type and name
         void onArticleItemClicked(int position);

    }

    public interface OnShareButtonClickedListener {
        // TODO: Update argument type and name
        void onShareButtonClickedListener(int position);

    }

    public interface OnFavoriteButtonClickedListener {
        // TODO: Update argument type and name
        void onFavoriteButtonClickedListener(int position, boolean isfavorite);

    }

    public void SearchSubmit(String query)
    {
        searchmode = true;
        //Submit query in order to call the filter guntion to search the original list
        final ArrayList<NewsItem> filteredModelList;
        if (!isFavoritesDispayed){
        filteredModelList = filter(model.getOriginal_newsList(), query);
        }else{
            filteredModelList = filter(model.getFavoritesList(), query);

        }
        model.setNewsList(filteredModelList);

        mAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);

    }



    public void SearchMode(boolean searchmode){
      this.searchmode = searchmode;

    }

    private ArrayList<NewsItem> filter(ArrayList<NewsItem> models, String query) {
        query = query.toLowerCase();
        //check the original list if has title tha contains the query.
        final ArrayList<NewsItem> filteredModelList = new ArrayList<>();
        for (NewsItem model : models) {
            final String text = model.getTitle().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public void displayfavoriteList()
    {
        //Submit query in order to call the filter guntion to search the original list
        isFavoritesDispayed = true;
        tv_favorites.setVisibility(View.VISIBLE);

       // model.setNewsList(model.getFavoritesList());
        try {
            model.opendb_read();
            model.read_db(true);

            model.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        ((MyAdapter) mAdapter).favoritesDisplay = true;
        mAdapter.notifyDataSetChanged();

       // mRecyclerView.scrollToPosition(0);

        System.out.println("display favorites: "+model.getFavoritesList().size());
        System.out.println("display original: " + model.getOriginal_newsList().size());
        System.out.println("display newlist: " + model.getNewsList().size());


    }

    public void displayOriginalList()
    {
        //Submit query in order to call the filter guntion to search the original list
        isFavoritesDispayed = false;
        tv_favorites.setVisibility(View.GONE);

        model.setNewsList(model.getOriginal_newsList());

        ((MyAdapter) mAdapter).favoritesDisplay = false;
        mAdapter.notifyDataSetChanged();

        System.out.println("display favorites: " + model.getFavoritesList().size());
        System.out.println("display original: " + model.getOriginal_newsList().size());
        System.out.println("display newlist: " + model.getNewsList().size());

        //mRecyclerView.invalidate();
        // mRecyclerView.scrollToPosition(0);

       // mLayoutManager.scrollToPosition(0);

    }





}
