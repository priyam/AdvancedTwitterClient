package com.pc.apps.simpletweets.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.pc.apps.simpletweets.R;
import com.pc.apps.simpletweets.TwitterApplication;
import com.pc.apps.simpletweets.Utilities.TwitterClient;
import com.pc.apps.simpletweets.adapters.TweetsArrayAdapter;
import com.pc.apps.simpletweets.fragments.HomeTimelineFragment;
import com.pc.apps.simpletweets.fragments.MentionsTimelineFragment;
import com.pc.apps.simpletweets.fragments.TweetsListFragment;
import com.pc.apps.simpletweets.models.Tweet;
import com.pc.apps.simpletweets.models.User;
import org.apache.http.Header;
import org.json.JSONObject;

import java.util.List;


public class TimelineActivity extends ActionBarActivity implements ComposeTweetDialog.OnTweetPostListener  {

    private TweetsListFragment fragmentTweetsList;
    private ComposeTweetDialog composeTweetDialog;
    private User currentUser;
    private TwitterClient client;
    private OnTweetPostedListener listener;

    // Define the events that the fragment will use to communicate
    public interface OnTweetPostedListener {
        public void onTweetPosted(Tweet t);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);

        // Set a ToolBar to replace the ActionBar.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        // Get the viewpager
        ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        // Set the viewpager adapter for the pager
        vpPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));
        // find the sliding tabstrip
        PagerSlidingTabStrip tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        //Attach the tabstrip to viewpager
        tabStrip.setViewPager(vpPager);
        client = TwitterApplication.getRestClient();
        getLoggedInUser();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onProfileView(MenuItem item) {
        //Launch the profile view
        Intent i = new Intent(this, ProfileActivity.class);
        i.putExtra("user", currentUser);
        startActivity(i);
    }


    public void onComposeTweetAction(MenuItem item) {

        FragmentManager fm = getSupportFragmentManager();
       composeTweetDialog = ComposeTweetDialog.newInstance(currentUser);
       composeTweetDialog.show(fm,"fragment_compose_tweet");

    }
    private void getLoggedInUser() {
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json) {
                Log.d("DEBUG", json.toString());
                currentUser = User.fromJSON(json);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("DEBUG", errorResponse.toString());
            }
        });
    }

    @Override
    public void onTweetPostClicked(String text) {
        client.postTweet(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject json)  {

                Log.d("DEBUG", json.toString());

                Tweet t = Tweet.fromJSON(json);
               //FIRE EVENT THAT FRAGMENT IMPLEMENTS
                listener.onTweetPosted(t);
                composeTweetDialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getParent().getApplicationContext(), "Error occurred - couldnt post tweet", Toast.LENGTH_SHORT).show();
            }
        } , text);
    }


    //Return the order of the fragments in the view pager
    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        private String tabTitles[] = {"Home","Mentions"};

        public TweetsPagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0){
                HomeTimelineFragment fragmentHome = new HomeTimelineFragment();
                if(fragmentHome instanceof OnTweetPostedListener){
                    listener = (OnTweetPostedListener)fragmentHome;
                }
                else {
                    throw new ClassCastException(fragmentHome.toString()
                            + " must implement OnTweetPostedListener");
                }
                return fragmentHome;
            } else if(position == 1){
                return new MentionsTimelineFragment();
            } else{
                return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }
    }


}
