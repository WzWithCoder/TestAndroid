package com.example.wangzheng.sku;



import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wangzheng on 2022/1/6
 */
public class DescartesTest {

    @Test
    public void descartes() {

        List<List<String>> skuGroup = new ArrayList<>();
        List<String> sku1 = new ArrayList<>();
        sku1.add("红色");
        sku1.add("绿色");
        sku1.add("蓝色");
        skuGroup.add(sku1);

        List<String> sku2 = new ArrayList<>();
        sku2.add("S");
        sku2.add("M");
        sku2.add("XL");
        sku2.add("XXL");
        sku2.add("XXXL");
        skuGroup.add(sku2);

        List<String> sku3 = new ArrayList<>();
        sku3.add("丝绸");
        sku3.add("塑料");
        sku3.add("粗布");
        skuGroup.add(sku3);

        List<List<String>> result = Descartes.descartes(skuGroup);
        Descartes.printSkuGroup(result);
    }
}