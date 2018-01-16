package com.tp.locator;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;


/**
 * Created by user on 8/1/2015.
 */
public class DataLoaderCallBack implements LoaderManager.LoaderCallbacks<Cursor> {
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
    public void fillData(Fragment fragment)
    {
        //fragment.getLoaderManager().initLoader(0,null,this);
    }
}
