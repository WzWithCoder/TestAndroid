package com.example.wangzheng.widget;


/**
 * Create by wangzheng on 2018/12/4
 * https://blog.csdn.net/hnulwt/article/details/42787455
 */
public class RotateMatrix {

    public static float[] rotate(double angle, float... array) {
        return rotate(new Point(0f, 0f), angle, array);
    }

    /**
     * 旋转矩阵
     *
     * @param point 锚点
     * @param angle 角度
     * @param array 矩阵
     */
    public static float[] rotate(Point point, double angle, float... array) {
        int index = 0;
        float x, y;
        double cosTheta = Math.cos(angle);
        double sinTheta = Math.sin(angle);
        for (int i = 0; i < array.length / 2; i++) {
            index = 2 * i;
            //移动坐标系到锚点
            x = array[index] - point.x;
            y = array[index + 1] - point.y;
            //计算旋转后的坐标
            array[index] = (float) (
                    x * cosTheta - y * sinTheta);
            array[index + 1] = (float) (
                    y * cosTheta + x * sinTheta);
            //还原坐标系
            array[index] += point.x;
            array[index + 1] += point.y;
        }
        return array;
    }

    public static void main(String args[]) {
        double angle = -Math.toRadians(270);
        float[] array = {
                1.0f, -1.0f,
                -1.0f, -1.0f,
                1.0f, 1.0f,
                -1.0f, 1.0f};
        rotate(angle, array);
        print(array);

        Rect rect = new Rect(
                0.0f, 0.0f,
                200f, 100f);
        array = rect.getArray();
        Point point = new Point(rect.centerX(), rect.centerY());
        rotate(point, angle, array);
        System.out.println(rect.width() + "-" + rect.height());
        print(array);
    }

    public static void print(float[] array) {
        System.out.println();
        for (int i = 0; i < array.length / 2; i++) {
            int index = 2 * i;
            System.out.println(array[index] + "," + array[index + 1]);
        }
    }

    static class Rect {
        private float[] array = null;

        public Rect(float... array) {
            this.array = array;
        }

        public final float width() {
            return Math.abs(array[2] - array[0]);
        }

        public final float height() {
            return Math.abs(array[3] - array[1]);
        }

        public final float centerX() {
            return (array[0] + array[2]) * 0.5f;
        }

        public final float centerY() {
            return (array[1] + array[3]) * 0.5f;
        }

        public float[] getArray() {
            return array;
        }
    }

    public static class Point {
        public float x;
        public float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }
}
