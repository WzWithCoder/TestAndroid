package com.skin;

/**
 * android:textColor="@color/red"
 * @see com.skin.AttrEnum
 * <p>
 * Created by wangzheng on 2017/12/20.
 */

public class SkinAttr {
    /**
     * textColor
     */
    public String attrName;

    /**
     * red-->id
     */
    public int resId;

    /**
     * red
     */
    public String resName;

    /**
     * color
     */
    public String resType;

    public SkinAttr(String attrName, int resId, String resName, String resType) {
        this.attrName = attrName;
        this.resId = resId;
        this.resName = resName;
        this.resType = resType;
    }

    @Override
    public String toString() {
        return "SkinAttr{" +
                "attrName='" + attrName + '\'' +
                ", resId=" + resId +
                ", resName='" + resName + '\'' +
                ", resType='" + resType + '\'' +
                '}';
    }

    public static SkinAttr from(String attrName, int resId, String resName, String resType) {
        return new SkinAttr(attrName, resId, resName, resType);
    }
}
