package com.tp.locator;

import android.content.Context;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Aruna on 01-01-2018.
 */

public class TypefaceCache {
    private static final Map<String, Typeface> CACHE = new HashMap<String, Typeface>();

    public static Typeface getTypeface(Context context, String font) {
        if (null == font) {
            return null;
        }
        Typeface typeface = CACHE.get(font);
        if (null == typeface) {

            typeface = Typeface.createFromAsset(context.getAssets(), font);
            CACHE.put(font, typeface);
        }
        return typeface;
    }
}
