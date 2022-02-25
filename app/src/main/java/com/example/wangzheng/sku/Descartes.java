package com.example.wangzheng.sku;

import java.util.ArrayList;
import java.util.List;

/**
 * Create by wangzheng on 2019/8/27
 */
public class Descartes {
    public static void main(String[] args) {
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

        List<List<String>> result = new ArrayList<>();
        long start = System.currentTimeMillis();
        //descartes(skuGroup, result, 0, new ArrayList<String>());
        result = descartes(skuGroup);
        System.out.println("time:"+(System.currentTimeMillis() - start));
        printSkuGroup(result);
    }

    public static void printSkuGroup(List<List<String>> skus) {
        int i = 0;
        for (List<String> sku : skus) {
            for (String s : sku) {
                System.out.print(s);
            }
            System.out.println(++i);
        }
    }

    /**
     * 笛卡尔乘积 非递归算法实现
     *
     * @param dimvalue
     * @return
     */
    public static <T> List<List<T>> descartes(List<List<T>> dimvalue) {
        int[][] points = tablePoints(dimvalue);
        List<List<T>> result = new ArrayList<>();
        do {
            List<T> temp = new ArrayList<>();
            for (int i = 0; i < points.length; i++) {
                int pointer = points[i][0];
                T t = dimvalue.get(i).get(pointer);
                temp.add(t);
            }
            result.add(temp);
        } while (movePointer(points, 1));
        return result;
    }

    private static <T> int[][] tablePoints(List<List<T>> list) {
        int[][] points = new int[list.size()][2];
        for (int i = 0; i < points.length; i++) {
            points[i][0] = 0;
            points[i][1] = list.get(i).size();
        }
        return points;
    }

    /**
     * 指针加法器
     *
     * @param points 指针位图
     * @param value  指针位移
     * @return false:溢出
     */
    private static boolean movePointer(int[][] points, int value) {
        for (int i = points.length - 1; i >= 0; i--) {
            int currNum = points[i][0] + value;
            points[i][0] = currNum % points[i][1];
            value = currNum / points[i][1];
            if (value == 0) {
                break;
            }
        }
        return value == 0;
    }

    /**
     * 笛卡尔乘积算法
     *
     * @param dimvalue 原List
     * @param result   通过乘积转化后的数组
     * @param layer    中间参数
     * @param curList  中间参数
     */
    public static <T> void descartes(List<List<T>> dimvalue,
                                 List<List<T>> result,
                                 int layer,
                                 List<T> curList) {
        if (layer < dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                descartes(dimvalue, result, layer + 1, curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<T>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    descartes(dimvalue, result, layer + 1, list);
                }
            }
        } else if (layer == dimvalue.size() - 1) {
            if (dimvalue.get(layer).size() == 0) {
                result.add(curList);
            } else {
                for (int i = 0; i < dimvalue.get(layer).size(); i++) {
                    List<T> list = new ArrayList<T>(curList);
                    list.add(dimvalue.get(layer).get(i));
                    result.add(list);
                }
            }
        }
    }
}
