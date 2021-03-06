package com.quartzodev.app;

import android.app.Application;

import com.quartzodev.buddybook.R;

import net.danlew.android.joda.JodaTimeAndroid;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by victoraldir on 21/05/2017.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/baskvl.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }
}
