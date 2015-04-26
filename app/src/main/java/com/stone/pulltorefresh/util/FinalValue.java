package com.stone.pulltorefresh.util;

import java.util.ArrayList;
import java.util.List;

/**
 * author : stone
 * email  : aa86799@163.com
 * time   : 15/4/22 11 43
 *
 * 常量
 */
public class FinalValue {

    private static final String qiniu_img_url = "http://7xi8ex.com1.z0.glb.clouddn.com/fengjing/";

    public static List<String> getImgUrls() {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= 102; i++) {
            list.add(qiniu_img_url + i + ".jpg");
        }
        return list;
    }

    public static class Config {
        public static final boolean DEVELOPER_MODE = false;
    }
}
