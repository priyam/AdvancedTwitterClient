package com.pc.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.activeandroid.query.Select;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pc.apps.simpletweets.R;
import com.pc.apps.simpletweets.TwitterApplication;
import com.pc.apps.simpletweets.Utilities.TwitterClient;
import com.pc.apps.simpletweets.Utilities.Utils;
import com.pc.apps.simpletweets.adapters.TweetsArrayAdapter;
import com.pc.apps.simpletweets.models.Tweet;
import android.util.Log;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public abstract class TweetsListFragment extends Fragment  {

    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected long MAX_ID = Long.MAX_VALUE;
    protected TwitterClient client;

    protected JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
        //Success
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
            Log.d("DEBUG", json.toString());
            addAll(Tweet.fromJSONArray(json));
        }
        //Failure
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Log.d("DEBUG", errorResponse.toString());
        }
    };

    //inflation logic
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tweets_list, parent, false);
        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        lvTweets = (ListView) v.findViewById(R.id.lvTweets);
        lvTweets.setAdapter(aTweets);

        return v;
    }

    public void addAll(List<Tweet> tweets){
        setMaxID(tweets);
        aTweets.addAll(tweets);
    }

    private void setMaxID(List<Tweet> tweets) {
        if(tweets != null && !tweets.isEmpty()){
            long mininumId= getMininumId(tweets);
            if(mininumId < MAX_ID ){
                MAX_ID = mininumId;
            }
        }
    }

    private long getMininumId(List<Tweet> tweets) {
        long min = tweets.get(0).uid;
        for (int i = 1; i < tweets.size(); i++){
            long tweetId = tweets.get(i).uid;
            if(min > tweetId){
                min = tweetId;
            }
        }
        return min;
    }

    //creation lifecycle even

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tweets = new ArrayList<>();
        aTweets = new TweetsArrayAdapter(getActivity(), tweets);
        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    public void populateTimeline(Tweet t){
        //TODO: load from DB for different fragments
        /*if(!Utils.isNetworkAvailable(getActivity().getApplicationContext())){
            List<Tweet> tweets  = new Select().from(Tweet.class).execute();
            if(tweets!= null && tweets.size() > 0 && aTweets.getCount() ==  0){
                aTweets.addAll(tweets);
            }
        }
        else */
        if (t != null){
            aTweets.insert(t, 0);
        }
        else   {
            makeNetworkApiRequest();

        }
    }

    public abstract void makeNetworkApiRequest();

}
