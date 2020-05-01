package com.ailiwean.core.view;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public interface LifeOwner extends LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();


    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause();


    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();

}
