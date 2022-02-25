package com.example;

public class TestTransformer{



    /**
     * Processes one file, which may be either a class or a resource.
     *
     * @param name  {@code non-null;} name of the file
     * @param bytes {@code non-null;} contents of the file
     * @return whether processing was successful
     */
    public static void processClass(String name, byte[] bytes) {
        System.out.println("==" + name);
    }
}