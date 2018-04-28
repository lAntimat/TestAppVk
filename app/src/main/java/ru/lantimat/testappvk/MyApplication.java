package ru.lantimat.testappvk;

import android.app.Application;

import com.vk.sdk.VKSdk;

import ru.lantimat.testappvk.utils.ImagesCache;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VKSdk.initialize(getApplicationContext());

        ImagesCache.getInstance().initializeCache();
    }
}
