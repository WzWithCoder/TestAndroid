package com.example.wangzheng.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class PropertieKit {
    private File file;

    public static PropertieKit of(File file) {
        return new PropertieKit(file);
    }

    public static PropertieKit getDefault() {
        File dir = Storage.getStorageDirectory("config");
        File file = new File(dir, "default.properties");
        return new PropertieKit(file);
    }

    public PropertieKit(File file) {
        this.file = file;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String read(String key) {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(is);
            return properties.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IoKit.close(is);
        }
        return "";
    }

    public boolean write(String key, String value) {
        return write(key, value, "");
    }

    public boolean write(String key, String value, String comments) {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(file);
            os = new FileOutputStream(file);
            Properties properties = new Properties();
            properties.load(is);
            properties.setProperty(key, value);
            properties.store(os, comments);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoKit.close(is);
            IoKit.close(os);
        }
        return true;
    }
}