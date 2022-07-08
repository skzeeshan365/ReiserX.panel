package com.reiserx.myapplication24.Advertisements;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.reiserx.myapplication24.BuildConfig;

public class bannerAdsClass {
    Context context;
    LinearLayout view;
    String ADID;

    public bannerAdsClass(Context context, LinearLayout view) {
        this.context = context;
        this.view = view;
        if (BuildConfig.DEBUG) {
            ADID = "ca-app-pub-3940256099942544/6300978111";
        } else {
            ADID = "ca-app-pub-1588658066763563/7181834258";
        }
    }

    public void adsCode () {
        MobileAds.initialize(context, initializationStatus -> loadBannerAds());
    }
    private void loadBannerAds () {
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(ADID);
        view.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
