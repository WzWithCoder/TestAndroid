package com.example.wangzheng.album;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;

import com.example.wangzheng.common.Callable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangzheng on 2016/4/15 on 17:51.
 */
public class AlbumFactory {
    private Activity mContext = null;
    private static final int LOADER_ALL = 0;//加载图库全部图片
    private static final int LOADER_CATEGORY = 1;
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID};

    private Callable<List<FloderBean>, Boolean> callable = null;
    private List<FloderBean> mFloderList = new ArrayList<>();

    public AlbumFactory(Activity context) {
        this.mContext = context;
    }

    public void builder(Callable<List<FloderBean>, Boolean> callable) {
        this.callable = callable;
        mContext.getLoaderManager().restartLoader(LOADER_ALL, null, new LoaderCallbacksImpl());
    }

    // 本代码结构可以再优化
    private class LoaderCallbacksImpl implements LoaderManager.LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_ALL) {
                CursorLoader cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        MediaStore.Images.Media.SIZE + ">?", new String[]{"10000"}, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            } else if (id == LOADER_CATEGORY) {
                CursorLoader cursorLoader = new CursorLoader(mContext,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
                return cursorLoader;
            }
            return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data == null || data.getCount() <= 0) return;
            mFloderList.clear();
            data.moveToFirst();
            do {
                String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                File file = new File(path);
                if (!file.exists()){
                    continue;
                }

                PictureBean pictureBean = new PictureBean(path, System.currentTimeMillis());
                File imageFile = new File(path);
                File folderFile = imageFile.getParentFile();
                FloderBean folder = new FloderBean(folderFile.getName(), folderFile.getAbsolutePath());
                if (!mFloderList.contains(folder)) {
                    folder.addPicture(pictureBean);
                    mFloderList.add(folder);
                } else {
                    FloderBean album = mFloderList.get(mFloderList.indexOf(folder));
                    album.addPicture(pictureBean);
                }
            } while (data.moveToNext());
            if (callable != null) {
                callable.call(mFloderList);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }
    }
}
