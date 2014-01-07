package com.example.rxsubject;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import rx.concurrency.Schedulers;
import rx.subjects.BehaviorSubject;

public class LocationProvider {
  protected final BehaviorSubject<Location> behaviorSubject;
  protected final LocationClient locationClient;

  public LocationProvider(final Context context) {
    final LocationRequest locationRequest = LocationRequest.create()
        .setInterval(5000)
        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    behaviorSubject = BehaviorSubject.create(new Location(""));
    behaviorSubject.subscribeOn(Schedulers.threadPoolForIO());

    locationClient = new LocationClient(context, new GooglePlayServicesClient.ConnectionCallbacks() {
      @Override
      public void onConnected(Bundle bundle) {
        behaviorSubject.onNext(locationClient.getLastLocation());

        locationClient.requestLocationUpdates(locationRequest, new LocationListener() {
          @Override
          public void onLocationChanged(Location location) {
            behaviorSubject.onNext(location);
          }
        });
      }

      @Override
      public void onDisconnected() {
        behaviorSubject.onCompleted();
      }
    }, new GooglePlayServicesClient.OnConnectionFailedListener() {
      @Override
      public void onConnectionFailed(ConnectionResult connectionResult) {
        behaviorSubject.onError(new GooglePlayServicesNotAvailableException(connectionResult.getErrorCode()));
      }
    }
    );
  }

  public void start() {
    if (!locationClient.isConnected() || !locationClient.isConnecting()) {
      locationClient.connect();
    }
  }

  public void stop() {
    if (!locationClient.isConnected()) {
      locationClient.disconnect();
    }
  }

  public BehaviorSubject<Location> locationObserver() {
    return behaviorSubject;
  }
}
