package com.stone.pulltorefresh.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.stone.pulltorefresh.R;
import com.stone.pulltorefresh.model.Photo;

import java.util.ArrayList;
import java.util.List;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/4/23 11 10
 */
public class ImgAdapter extends BaseAdapter {

    private final DisplayImageOptions options;
    private Context context;
    private List<Photo> photoList;

    public ImgAdapter(Context context, List<Photo> list) {
        this.context = context;
        this.photoList = list == null ? new ArrayList<Photo>() : list;

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_stub)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(20)).build();
    }

    @Override
    public int getCount() {
        return this.photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.photoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(this.context, R.layout.img_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.tvdesc.setText(this.photoList.get(position).getDesc());
        //使用imgloader 加载 网络图片
        ImageLoader.getInstance().displayImage(this.photoList.get(//
                        position).getUrl(), holder.ivimg, this.options,//
                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {
                        holder.pbprogress.setProgress(0);
                        holder.pbprogress.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                        holder.pbprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        holder.pbprogress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {
                        holder.pbprogress.setVisibility(View.GONE);
                    }
                },
        new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {
                        //通过url加载才有
                        System.out.println("加载----" + imageUri);
                        holder.pbprogress.setProgress(Math.round(100.0f * current / total));
                    }
                });

        return convertView;
    }


    public class ViewHolder {
        public final TextView tvdesc;
        public final ProgressBar pbprogress;
        public final ImageView ivimg;
        public final View root;

        public ViewHolder(View root) {
            tvdesc = (TextView) root.findViewById(R.id.tv_desc);
            pbprogress = (ProgressBar) root.findViewById(R.id.pb_progress);
            ivimg = (ImageView) root.findViewById(R.id.iv_img);
            this.root = root;
        }
    }
}
