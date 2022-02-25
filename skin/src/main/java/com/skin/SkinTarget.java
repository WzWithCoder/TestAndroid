package com.skin;

import android.view.View;

import java.util.List;

/**
 * Created by wangzheng on 2017/12/20.
 */

class SkinTarget {
    public View view;
    public List<SkinAttr> attrs;

    public void apply() {
        for (SkinAttr attr : attrs) {
            AttrEnum attrEnum = AttrEnum.valueOf(attr.attrName);
            if (attrEnum != null) {
                attrEnum.apply(view, attr);
            }
        }
    }
}
