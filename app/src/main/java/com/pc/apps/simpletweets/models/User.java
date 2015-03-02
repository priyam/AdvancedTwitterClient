package com.pc.apps.simpletweets.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

@Table(name = "User")
public class User extends Model implements Parcelable {
    //list the attributes

    @Column( name = "screenName", index=true , unique = true, onUniqueConflict = Column.ConflictAction.REPLACE)
    public String screenName;
    @Column( name = "name")
    public String name;
    @Column( name = "uid")
    public long uid;
    @Column( name = "profileImageUrl")
    public String profileImageUrl;
    @Column( name = "description")
    public String description;
    @Column( name = "followers")
    public int followers;
    @Column( name = "following")
    public int following;
    @Column( name = "tweetsCount")
    public int tweetsCount;
    @Column(name = "bannerImageURL")
    public String bannerImageURL;

    public String getScreenName() {
        return "@" + screenName;
    }

    //deserialize the user json => User
    public static User fromJSON(JSONObject jsonObject){
        User user = new User();
        try {
            user.screenName = jsonObject.getString("screen_name");
            user.name = jsonObject.getString("name");
            user.uid = jsonObject.getLong("id");
            user.profileImageUrl = jsonObject.optString("profile_image_url").replace("_normal", "_bigger");;
            user.bannerImageURL = jsonObject.optString("profile_banner_url");
            user.description = jsonObject.optString("description");
            user.followers = jsonObject.optInt("followers_count");
            user.following = jsonObject.optInt("following");
            user.tweetsCount = jsonObject.optInt("statuses_count");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        User userExists = new Select().from(User.class).where("screenName = ?",user.screenName).executeSingle();
        if(userExists == null){
            user.save();
        }
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.screenName);
        dest.writeString(this.name);
        dest.writeLong(this.uid);
        dest.writeString(this.profileImageUrl);
        dest.writeString(this.bannerImageURL);
        dest.writeString(this.description);
        dest.writeInt(this.followers);
        dest.writeInt(this.following);
        dest.writeInt(this.tweetsCount);
    }

    public User() {
    }

    private User(Parcel in) {
        this.screenName = in.readString();
        this.name = in.readString();
        this.uid = in.readLong();
        this.profileImageUrl = in.readString();
        this.bannerImageURL = in.readString();
        this.description = in.readString();
        this.followers = in.readInt();
        this.following = in.readInt();
        this.tweetsCount = in.readInt();
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
