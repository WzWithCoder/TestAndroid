package com.example.wangzheng.common;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Build;
import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.graphics.Bitmap.CompressFormat.JPEG;

/**
 * Create by wangzheng on 2018/7/26
 */
public class BitmapKit {

    //Converts a drawable to a bitmap of specified width and height.
    public static Bitmap drawableToBitmap(
            Drawable drawable, int width, int height) {
        Bitmap bitmap = Bitmap.createBitmap(
                width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }


    public static void save(Bitmap bitmap, File file) throws Exception {
        FileOutputStream out = new FileOutputStream(file);
        if (bitmap.compress(JPEG, 80, out)) {
            out.flush();
            out.close();
        }
    }

    /**
     * 图片压缩
     *
     * @param path
     * @param edge 期望边长
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap compress(String path, int edge) throws IOException {
        Bitmap bitmap = null;
        //获取图片原始信息，但不加载图片到内存
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        //压缩图片
        opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = computeSampleSize(opts,edge,-1);
        opts.inDither = true;//不进行图片抖动处理
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            //使用匿名共享内存
            opts.inPurgeable = true;
            opts.inInputShareable = true;
        }
        opts.inTempStorage = new byte[12 * 1024];
        FileInputStream is = new FileInputStream(path);
        bitmap = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
        //处理图片角度
        bitmap = rotate(bitmap, getDegree(path));
        return bitmap;
    }

    public static Bitmap rotate(Bitmap bitmap, int degrees) {
        if (degrees == 0 || null == bitmap) return bitmap;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, bitmap.getWidth(), bitmap.getHeight());
        bitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return bitmap;
    }

    public static Bitmap fromBase64(String base64) {
        byte[] bytes = Base64.decode(base64, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(
                bytes, 0, bytes.length);
        return bitmap;
    }

    public static int getDegree(String path) {
        int degree = 0;
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        int orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        if (orientation == ExifInterface
                .ORIENTATION_ROTATE_90) {
            degree = 90;
        } else if (orientation == ExifInterface
                .ORIENTATION_ROTATE_180) {
            degree = 180;
        } else if (orientation == ExifInterface
                .ORIENTATION_ROTATE_270) {
            degree = 270;
        } else {
            degree = 0;
        }
        return degree;
    }

    public static Bitmap createThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, opts);

        opts.inSampleSize = computeSampleSize(opts, -1, 128 * 128);
        opts.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(filePath, opts);
        return bitmap;
    }

    /**
     * @param options
     * @param minSideLength  期望生成的缩略图的宽高中的较小的值
     * @param maxNumOfPixels 期望生成的缩量图的总像素
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options
            , int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    /**
     * @param options
     * @param minSideLength  期望生成的缩略图的宽高中的较小的值
     * @param maxNumOfPixels 期望生成的缩量图的总像素
     * @return
     */
    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 :
                (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 :
                (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    public static int sizeOf(Bitmap bitmap) {
        //4.4
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        }
        //3.1及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        //3.1之前的版本
        return bitmap.getRowBytes() * bitmap.getHeight();
    }


    public static boolean canUseForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // From Android 4.4 (KitKat) onward we can re-use
            // if the byte size of the new bitmap is smaller than
            // the reusable bitmap candidate
            // allocation byte count.
            int width = targetOptions.outWidth / targetOptions.inSampleSize;
            int height = targetOptions.outHeight / targetOptions.inSampleSize;
            int byteCount = width * height * getBytesPerPixel(candidate.getConfig());
            return byteCount <= candidate.getAllocationByteCount();
        }
        // On earlier versions,
        // the dimensions must match exactly and the inSampleSize must be 1
        return candidate.getWidth() == targetOptions.outWidth
                && candidate.getHeight() == targetOptions.outHeight
                && targetOptions.inSampleSize == 1;
    }

    public static int calculateInSampleSize(@NonNull BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width lower or equal to the requested height and width.
            while ((height / inSampleSize) > reqHeight || (width / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static float[] scaleBitmapOptions(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int width = options.outWidth;
        final int height = options.outHeight;

        float scale = 1f;
        float dx = 0, dy = 0;

        options.inScaled = true;
        if (width * reqHeight > reqWidth * height) {
            scale = reqHeight * 1f / height;
            dx = (width * scale - reqWidth) * 0.5f;
            options.inDensity = height;
            options.inTargetDensity = reqHeight;
        } else {
            scale = reqWidth * 1f / width;
            dy = (height * scale - reqHeight) * 0.5f;
            options.inDensity = width;
            options.inTargetDensity = reqWidth;
        }
        return new float[]{dx, dy};
    }

    private static int getBytesPerPixel(Bitmap.Config config) {
        if (config == Bitmap.Config.ARGB_8888) {
            return 4;
        } else if (config == Bitmap.Config.RGB_565) {
            return 2;
        } else if (config == Bitmap.Config.ARGB_4444) {
            return 2;
        } else if (config == Bitmap.Config.ALPHA_8) {
            return 1;
        }
        return 1;
    }
}
