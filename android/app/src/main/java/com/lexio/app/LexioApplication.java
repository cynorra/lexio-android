package com.lexio.app;

import android.app.Application;
import com.facebook.soloader.SoLoader;
import com.facebook.drawee.backends.pipeline.Fresco;

import com.google.android.gms.ads.MobileAds;

public class LexioApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Initialize SoLoader for Litho layout components
        SoLoader.init(this, false);
        
        // Initialize Fresco for image loading
        Fresco.initialize(this);

        // Initialize AdMob SDK
        MobileAds.initialize(this, initializationStatus -> {});
    }
}
