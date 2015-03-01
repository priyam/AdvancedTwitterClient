package com.pc.apps.simpletweets.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.pc.apps.simpletweets.R;
import com.pc.apps.simpletweets.Utilities.EndlessScrollListener;
import com.pc.apps.simpletweets.activities.ComposeTweetDialog;
import com.pc.apps.simpletweets.activities.TimelineActivity;
import com.pc.apps.simpletweets.adapters.TweetsArrayAdapter;
import com.pc.apps.simpletweets.models.Tweet;
import com.pc.apps.simpletweets.models.User;

import java.util.ArrayList;
import java.util.List;

public class TweetsListFragment extends Fragment {

    protected ArrayList<Tweet> tweets;
    protected TweetsArrayAdapter aTweets;
    protected ListView lvTweets;
    protected SwipeRefreshLayout swipeContainer;
    protected long MAX_ID = Long.MAX_VALUE;

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
        long min = tweets.get(0).getUid();
        for (int i = 1; i < tweets.size(); i++){
            long tweetId = tweets.get(i).getUid();
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
    }

}
