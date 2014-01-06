package com.example.rxsubject;

import android.app.Application;

public class BaseApplication extends Application {
  public static LocationProvider locationProvider;

  @Override
  public void onCreate() {
    super.onCreate();

    locationProvider = new LocationProvider(this);
    locationProvider.start();
  }
}
