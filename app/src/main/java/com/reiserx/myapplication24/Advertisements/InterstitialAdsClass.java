package com.reiserx.myapplication24.Advertisements;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.reiserx.myapplication24.Activities.Operations.operation;
import com.reiserx.myapplication24.BuildConfig;

public class InterstitialAdsClass {

    private AdManagerInterstitialAd mAdManagerInterstitialAd;
    Context context;
    private final String ADID;

    public InterstitialAdsClass(Context context) {
        this.context = context;
        if (BuildConfig.DEBUG) {
            ADID = "/6499/example/interstitial";
        } else {
            ADID = "ca-app-pub-1588658066763563/3270509402";
        }
    }

    public void loadAds () {
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

        AdManagerInterstitialAd.load(context,ADID, adRequest,
                new AdManagerInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
                        // The mAdManagerInterstitialAd reference will be null until
                        // an ad is loaded.
                        mAdManagerInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        shows();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mAdManagerInterstitialAd = null;
                    }
                });
    }

    private void shows () {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd.show((Activity) context);
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }
}
