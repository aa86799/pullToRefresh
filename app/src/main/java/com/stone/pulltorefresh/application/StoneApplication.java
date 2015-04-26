package com.stone.pulltorefresh.application;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.StrictMode;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.stone.pulltorefresh.util.FinalValue;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/4/23 11 31
 */
public class StoneApplication extends Application {

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @Override
    public void onCreate() {
        if (FinalValue.Config.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        super.onCreate();

        initImageLoader(getApplicationContext());
    }

    public static void initImageLoader(Context context) {
        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
        config.threadPoolSize(5);
        config.threadPriority(Thread.NORM_PRIORITY - 2);
        config.diskCacheFileNameGenerator(new Md5FileNameGenerator());//磁盘缓存文件名生成器
        config.diskCacheSize(50 * 1024 * 1024); // 50 MiB  磁盘缓存最大容量
        config.denyCacheImageMultipleSizesInMemory();//拒绝内存缓存一张图片的多个尺寸
        config.memoryCache(new LruMemoryCache(1024 * 1024 * 8));//8mb 内存缓存
        config.memoryCacheExtraOptions(720, 1080);//内存缓存的图片最大宽高
        config.tasksProcessingOrder(QueueProcessingType.FIFO);//任务队列处理规则：LIFO、FIFO
        config.defaultDisplayImageOptions(DisplayImageOptions.createSimple());//加载图片的选项

        if (FinalValue.Config.DEVELOPER_MODE) {
            config.writeDebugLogs(); // Remove for release app
        }

        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config.build());
    }
}
