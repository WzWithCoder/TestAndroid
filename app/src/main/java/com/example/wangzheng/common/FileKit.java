package com.example.wangzheng.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

/**
 * Create by wangzheng on 2018/7/25
 */
public class FileKit {

    public static long size(File file) {
        long size = 0;
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                size += size(fileList[i]);
            }
        } else {
            size += file.length();
        }
        return size;
    }

    public static boolean delete(File file) {
        boolean result = true;
        if (file == null || !file.exists()) {
            return result;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                result &= delete(f);
            }
        }
        result &= file.delete();
        return result;
    }

    public static void copy(String src, String dst) {
        File source = new File(src);
        File target = new File(dst);
        copy(source, target);
    }

    public static void copy(File source, File target) {
        File parent = target.getParentFile();
        if (!parent.exists()) {
            parent.mkdirs();
        }
        FileInputStream fis = null;
        FileOutputStream fos = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(target);
            in = fis.getChannel();
            out = fos.getChannel();
            //连接两个通道，并且从in通道读取，然后写入out通道
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (fos != null) {
                    fos.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void append(String path, String content) {
        try {
            RandomAccessFile randomFile =
                    new RandomAccessFile(path, "rw");
            long length = randomFile.length();
            randomFile.seek(length);
            randomFile.write((content + "\r\n").getBytes());
            randomFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件编码格式
     *
     * @param fileName
     * @return charset
     * @throws Exception
     */
    public static String charset(String fileName) throws Exception {
        BufferedInputStream bis = new BufferedInputStream(
                new FileInputStream(fileName));
        int p = (bis.read() << 8) + bis.read();
        String code = null;
        switch (p) {
            case 0xefbb:
                code = "UTF-8";
                break;
            case 0xfffe:
                code = "Unicode";
                break;
            case 0xfeff:
                code = "UTF-16BE";
                break;
            default:
                code = "GBK";
        }
        return code;
    }
}
