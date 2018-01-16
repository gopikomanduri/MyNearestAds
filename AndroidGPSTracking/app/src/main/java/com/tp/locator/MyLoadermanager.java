package com.tp.locator;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;

/**
 * Created by user on 8/1/2015.
 */
public class MyLoadermanager implements LoaderManager.LoaderCallbacks<Cursor>{
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

/*    public static Context;*/

}
