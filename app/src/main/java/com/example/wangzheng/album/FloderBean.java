package com.example.wangzheng.album;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzheng on 2016/4/15 on 17:12.
 */
public class FloderBean {
    private String floderName = null;
    public String floderTag = null;
    private List<PictureBean> pictures = null;
    public int selectedCount = 0;

    public FloderBean(String floderName, String floderTag) {
        this.floderName = floderName;
        this.floderTag = floderTag;
    }

    public String getFloderName() {
        return floderName + "";
    }

    public List<PictureBean> getPictures() {
        if (pictures == null) {
            pictures = new ArrayList<>();
        }
        return pictures;
    }

    public String getFloderCover() {
        String path = "";
        if (!getPictures().isEmpty()) {
            path = getPictures().get(0).path + "";
        }
        return path;
    }

    public void addPicture(PictureBean pictureBean) {
        getPictures().add(pictureBean);
    }

    @Override
    public int hashCode() {
        if (floderTag == null) {
            return super.hashCode();
        } else {
            return floderTag.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && (o instanceof FloderBean)) {
            return TextUtils.equals(floderTag, ((FloderBean) o).floderTag);
        }
        return false;
    }

}
