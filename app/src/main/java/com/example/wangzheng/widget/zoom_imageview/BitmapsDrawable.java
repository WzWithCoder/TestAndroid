package com.example.wangzheng.widget.zoom_imageview;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LruCache;
import android.util.Log;

import com.example.wangzheng.common.TestKit;

import java.io.IOException;
import java.lang.ref.WeakReference;

/**
 * Create by wangzheng on 2018/8/8
 */
public class BitmapsDrawable extends Drawable {
    private final static String TAG = BitmapsDrawable.class.getSimpleName();
    private final static boolean DEBUG = true;
    private static final int DEFAULT_PAINT_FLAGS =
            Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG;

    private BitmapFactory.Options mOptions
            = new BitmapFactory.Options();
    private LruCache<String, WeakReference<Bitmap>> mLruCache;
    private BitmapRegionDecoder mRegionDecoder;

    private int mBitmapWidth, mBitmapHeight;
    private int mBlockWidth = 200, mBlockHeight = 300;
    private int mRows, mColumns;
    private Paint mPaint;

    public BitmapsDrawable(String filepath) throws IOException {
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filepath, mOptions);
        mRegionDecoder = BitmapRegionDecoder
                .newInstance(filepath, false);
        inflate();
    }


    public BitmapsDrawable(java.io.InputStream is) throws IOException {
        mOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, mOptions);
        mRegionDecoder = BitmapRegionDecoder
                .newInstance(is, false);
        inflate();
    }

    private void inflate() {
        mBitmapWidth = mOptions.outWidth;
        mBitmapHeight = mOptions.outHeight;
        mOptions.inJustDecodeBounds = false;

        int[] table = calculateTable(mBitmapWidth, mBitmapHeight);
        mRows = table[0];
        mColumns = table[1];

        mLruCache = new LruCache<>(5);

        mPaint = new Paint(DEFAULT_PAINT_FLAGS);
        if (DEBUG) {
            mPaint.setColor(Color.RED);
            mPaint.setStrokeWidth(1);
            mPaint.setTextSize(18);
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    private Bitmap decodeRegion(Rect region, float scale) {
        Bitmap bitmap = null;
        int inSampleSize = BitmapUtils.computeSampleSize(Math.round(scale));
        String key = inSampleSize + region.toString();
        WeakReference<Bitmap> reference = mLruCache.get(key);
        if (null != reference && null != (bitmap = reference.get())) {
            return bitmap;
        }
        mOptions.inBitmap = bitmap;
        mOptions.inMutable = true;
        mOptions.inSampleSize = Math.min(inSampleSize, 1);
        bitmap = mRegionDecoder.decodeRegion(region, mOptions);
        if (null != bitmap) {
            mLruCache.put(key, new WeakReference<>(bitmap));
        }
        Log.d(TAG, "decodeRegion-" + key);
        return bitmap;
    }

    private int[] calculateTable(int width, int height) {
        int rows = height / mBlockHeight;
        if (height % mBlockHeight > 0) ++rows;

        int columns = width / mBlockWidth;
        if (width % mBlockWidth > 0) ++columns;

        return new int[]{rows, columns};
    }

    private int fromRaw(float scrollY, float scale) {
        int row = (int) (scrollY / mBlockHeight /scale);
        return Math.min(row, mRows - 1);
    }

    private int fromColumn(float scrollX, float scale) {
        int column = (int) (scrollX / mBlockWidth / scale);
        return Math.min(column, mColumns - 1);
    }

    private void render(Canvas canvas) {
        final int vwidth = canvas.getWidth();
        final int vheight = canvas.getHeight();
        float baseScale = Math.max(
                vwidth / mBitmapWidth,
                vheight / mBitmapHeight);

        Matrix matrix = canvas.getMatrix();
        float scale = getScale(matrix);
        float realWidth = scale * mBitmapWidth;
        float realHeight = scale * mBitmapHeight;
        float transX = realWidth <= vwidth ? 0 : getTransX(matrix);
        float transY = realHeight <= vheight ? 0 : getTransY(matrix);

        int[] table = calculateTable(
                realWidth <= vwidth ? mBitmapWidth : (int) (vwidth / scale),
                realHeight <= vheight ? mBitmapHeight : (int) (vheight / scale));
        int maxSize = (int) Math.ceil(table[0] * table[1] * 1.3f);
        if (maxSize != mLruCache.maxSize()) {
            mLruCache.resize(maxSize);
        }
        Log.d(TAG, "screen_table-" + table[0] + "," + table[1]);

        int fromRaw = fromRaw(transY, scale);
        int toRaw = fromRaw(transY + vheight, scale);
        int fromColumn = fromColumn(transX, scale);
        int toColumn = fromColumn(transX + vwidth, scale);

        Log.d(TAG, "render-" + mRows + "," + mColumns + "======"
                + fromRaw + "," + toRaw + "==" + fromColumn + "," + toColumn);

        long start = System.currentTimeMillis();
        Bitmap bitmap = null;
        Rect region = new Rect();
        for (int i = fromRaw; i <= toRaw; i++) {
            region.top = i * mBlockHeight;
            region.bottom = Math.min(region.top + mBlockHeight, mBitmapHeight);
            for (int j = fromColumn; j <= toColumn; j++) {
                region.left = j * mBlockWidth;
                region.right = Math.min(region.left + mBlockWidth, mBitmapWidth);
                bitmap = decodeRegion(region, baseScale / scale);
                canvas.drawBitmap(bitmap, null, region, mPaint);
                if (DEBUG) {
                    Rect bounds = new Rect();
                    mPaint.getTextBounds("0", 0, 1, bounds);
                    canvas.drawText(i + "," + j + "-" + region.right + "," +
                            region.bottom, region.left, region.top + bounds.height(), mPaint);
                    canvas.drawRect(region, mPaint);
                }
            }
        }
        if (DEBUG) {
            Rect bounds = new Rect();
            mPaint.getTextBounds("Bitmap", 0, 1, bounds);
            bounds.bottom += 10;
            int unitHeight = (int) (transY / scale);
            float x = transX / scale;

            canvas.drawText("Bitmap:" + mBitmapWidth + "*" + mBitmapHeight,
                    x, unitHeight + bounds.height() * 1, mPaint);
            canvas.drawText("Screen:" + vwidth + "*" + vheight,
                    x, unitHeight + bounds.height() * 2, mPaint);
            canvas.drawText("Table:" + mRows + "*" + mColumns,
                    x, unitHeight + bounds.height() * 3, mPaint);
            canvas.drawText("Render:row:" + fromRaw + "-" + toRaw + ",column:" + fromColumn + "-" + toColumn,
                    x, unitHeight + bounds.height() * 4, mPaint);
            canvas.drawText("Cache:" + maxSize + "," + TestKit.calculateCache(mLruCache) * 1f / 1024 / 1024,
                    x, unitHeight + bounds.height() * 5, mPaint);
            canvas.drawText("FPS:" + 1000 / (System.currentTimeMillis() - start + 1),
                    x, unitHeight + bounds.height() * 6, mPaint);
        }
    }

    private final float[] mMatrixValues = new float[9];

    public float getTransY(Matrix matrix) {
        return Math.abs(getValue(matrix, Matrix.MTRANS_Y));
    }

    public float getTransX(Matrix matrix) {
        return Math.abs(getValue(matrix, Matrix.MTRANS_X));
    }

    public float getScale(Matrix matrix) {
        return (float) Math.sqrt((float) Math.pow(getValue(matrix, Matrix.MSCALE_X), 2)
                + (float) Math.pow(getValue(matrix, Matrix.MSKEW_Y), 2));
    }

    private float getValue(Matrix matrix, int whichValue) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[whichValue];
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int saveCount = canvas.save();
        render(canvas);
        canvas.restoreToCount(saveCount);
    }

    @Override
    public int getIntrinsicWidth() {
        return mBitmapWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return mBitmapHeight;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return mPaint.getAlpha() < 255 ?
                PixelFormat.TRANSLUCENT :
                PixelFormat.OPAQUE;
    }
}
