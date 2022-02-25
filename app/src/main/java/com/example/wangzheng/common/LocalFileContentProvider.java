package com.example.wangzheng.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocalFileContentProvider extends ContentProvider {
	//这里的AUTHORITY就是我们在AndroidManifest.xml中配置的authorities
	private static final String AUTHORITY = "www.ipc.remote";
	private static final String URI_PREFIX = "content://" + AUTHORITY;
 
	public static final Uri buildUri(String key) {
		Uri uri = Uri.parse("content://" + AUTHORITY + key);
		return uri;
	}
 
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
		Log.e("xsxsxsxsxs","openFile:"+ uri.toString());
		File file = new File(uri.getPath());
		ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
		return parcel;
	}

	@Nullable
	@Override
	public AssetFileDescriptor openAssetFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
		String path = uri.getPath();
		Log.e("xsxsxsxsxs","openAssetFile:"+ uri.toString() + ":" + path);
		AssetManager am = getContext().getAssets();
		if (TextUtils.equals(path,"/local.html")) {
			try {
				return am.openFd("local.html");
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (TextUtils.equals(path,"/image")) {
			try {
				return am.openFd("larg.jpg");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return super.openAssetFile(uri, mode);
	}

	@Override
	public boolean onCreate() {
		return true;
	}
 
	@Override
	public int delete(Uri uri, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
 
	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
 
	@Override
	public Uri insert(Uri uri, ContentValues contentvalues) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
 
	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}
 
	@Override
	public int update(Uri uri, ContentValues contentvalues, String s, String[] as) {
		throw new UnsupportedOperationException("Not supported by this provider");
	}

	@Nullable
	@Override
	public Bundle call(@NonNull String method, @Nullable String arg, @Nullable Bundle extras) {
		Log.e("xsxsxsxsxs","call:"+ method);
		return super.call(method, arg, extras);
	}
}