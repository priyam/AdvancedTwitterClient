package com.pc.apps.simpletweets.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.pc.apps.simpletweets.R;
import com.pc.apps.simpletweets.Utilities.Utils;
import com.pc.apps.simpletweets.activities.ProfileActivity;
import com.pc.apps.simpletweets.models.Tweet;
import com.squareup.picasso.Picasso;

import java.util.List;

// Take the tweet objects and turn into views displayed in the list
public class TweetsArrayAdapter extends ArrayAdapter<Tweet>{

    private static class ViewHolder{
        ImageView ivProfileImage;
        TextView tvUserName;
        TextView tvUserScreenName;
        TextView tvBody;
        TextView tvCreatedAtTime;
    }
    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, android.R.layout.simple_list_item_1 ,tweets);
    }

    // Override and setup custom template

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 1. Get the tweet
        final Tweet tweet = getItem(position);
        ViewHolder viewHolder;
        //2. find or inflate the template
        if(convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            //3. Find subviews to fill with data in the template
            convertView = inflater.inflate(R.layout.item_tweet, parent, false);
            viewHolder.ivProfileImage = (ImageView) convertView.findViewById(R.id.ivProfileImage);
            viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.tvBody = (TextView) convertView.findViewById(R.id.tvBody);
            viewHolder.tvUserScreenName = (TextView) convertView.findViewById(R.id.tvUserScreenName);
            viewHolder.tvCreatedAtTime = (TextView) convertView.findViewById(R.id.tvCreatedAtTime);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //4. populate data into subviews
        viewHolder.ivProfileImage.setImageResource(0);
        Picasso.with(getContext()).load(tweet.user.profileImageUrl).into(viewHolder.ivProfileImage);
        viewHolder.tvBody.setText(tweet.body);
        viewHolder.tvUserName.setText(tweet.user.name);
        viewHolder.tvUserScreenName.setText(tweet.user.screenName);
        viewHolder.tvCreatedAtTime.setText(Utils.getCreatedAtPrettyTime(tweet.createdAt));

        viewHolder.ivProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), ProfileActivity.class);
                i.putExtra("user", tweet.user);
                getContext().startActivity(i);
            }
        });

        //5. return the view to be inserted into the list
        return convertView;
    }
}
