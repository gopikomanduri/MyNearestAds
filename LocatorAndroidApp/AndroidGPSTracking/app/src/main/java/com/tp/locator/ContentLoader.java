package com.tp.locator;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

/**
 * Created by user on 8/1/2015.
 */
public class ContentLoader extends CursorLoader {
    @Override
    public Cursor loadInBackground() {
        return super.loadInBackground();
    }

    public ContentLoader(Context context) {
        super(context);
    }

    public ContentLoader(Context context, Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
    }
}
