package com.example.wangzheng;

/**
 * Create by wangzheng on 2018/11/9
 */
public class I {
    static String hello = "1";

    static {
        hello = "2";
    }

    public I(String s) {
        hello = s;
    }

    {
        hello = "3";
    }

    public void test() {
        char c = 'a';
        try {
            Integer i = 4;
            i++;
            throw  new I_E1();
        } catch (I_E1 | I_E2 | NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            return;
            //e.printStackTrace();
        } finally {
            System.out.println("xxxxxxxxxxxxxxx");
        }
        String str = "123";
    }

    public static void main(String[] args) {
        print();
    }

    public static void print() {
        if ("a" instanceof String) {
            System.out.print('a');
        } else {
            System.out.print('b');
        }
    }

    class I_E1 extends RuntimeException {
        @Override
        public String toString() {
            return super.toString();
        }
    }

}

class I_E2 extends RuntimeException {

}
