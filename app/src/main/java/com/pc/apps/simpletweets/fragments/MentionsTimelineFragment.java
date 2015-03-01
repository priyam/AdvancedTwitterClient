package com.pc.apps.simpletweets.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.pc.apps.simpletweets.TwitterApplication;
import com.pc.apps.simpletweets.Utilities.EndlessScrollListener;
import com.pc.apps.simpletweets.Utilities.TwitterClient;
import com.pc.apps.simpletweets.models.Tweet;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class MentionsTimelineFragment extends TweetsListFragment {

    private TwitterClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View v = super.onCreateView(inflater, parent, savedInstanceState);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                aTweets.clear();
                Tweet.setMentionsTimelineMaxId(Long.MAX_VALUE);
                populateTimeline(null);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        // Attach the listener to the AdapterView onCreate
        /*lvTweets.setOnScrollListener(new EndlessScrollListener(3) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                populateTimeline(null);
                // or customLoadMoreDataFromApi(totalItemsCount);
            }
        });*/
        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        client = TwitterApplication.getRestClient();
        populateTimeline(null);
    }

    //Send an API request to get the timeline json
    // Fill the listview by creating the tweet objects from json
    public void populateTimeline(Tweet t) {
        if (t == null) {
            final long maxId = Tweet.getMentionsTimelineMaxId();
            client.getMentionsTimeline(new JsonHttpResponseHandler() {
                //Success
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray json) {
                    Log.d("DEBUG", json.toString());

                    ArrayList<Tweet> moreTweets = Tweet.fromJSONArray(json);
                    if(maxId != Tweet.getMentionsTimelineMaxId()){
                        Log.d("DEBUG", "maxId is not same as new max");
                        addAll(moreTweets);
                    }
                }

                //Failure
                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("DEBUG", errorResponse.toString());
                }
            }, maxId == Long.MAX_VALUE ? Long.MAX_VALUE : maxId - 1);
        } else {
            aTweets.insert(t, 0);
        }
    }

}