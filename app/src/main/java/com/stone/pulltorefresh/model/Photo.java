package com.stone.pulltorefresh.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/4/23 11 15
 */
public class Photo implements Parcelable {

    private String desc;
    private String url;

    public Photo() {

    }

    public Photo(String desc, String url) {
        this.desc = desc;
        this.url = url;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.desc);
        dest.writeString(this.url);
    }

    private Photo(Parcel in) {
        this.desc = in.readString();
        this.url = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel source) {
            return new Photo(source);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
