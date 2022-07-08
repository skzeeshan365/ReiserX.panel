package com.reiserx.myapplication24.Advertisements;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.reiserx.myapplication24.BuildConfig;

import java.util.Random;

public class InterstitialAdsClass {

    private AdManagerInterstitialAd mAdManagerInterstitialAd;
    Context context;
    private final String ADID;
    String TAG = "uyghfhgih";

    public InterstitialAdsClass(Context context) {
        this.context = context;
        if (BuildConfig.DEBUG) {
            ADID = "/6499/example/interstitial";
        } else {
            ADID = "ca-app-pub-1588658066763563/3270509402";
        }
    }

    public void loadAds () {
        if (getRandom(0, 1)==1) {
            AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

            AdManagerInterstitialAd.load(context, ADID, adRequest,
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
    }

    private void shows () {
        if (mAdManagerInterstitialAd != null) {
            mAdManagerInterstitialAd.show((Activity) context);
            mAdManagerInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                @Override
                public void onAdClicked() {
                    // Called when a click is recorded for an ad.
                    Log.d(TAG, "Ad was clicked.");
                }

                @Override
                public void onAdDismissedFullScreenContent() {
                    // Called when ad is dismissed.
                    // Set the ad reference to null so you don't show the ad a second time.
                    Log.d(TAG, "Ad dismissed fullscreen content.");
                    mAdManagerInterstitialAd = null;
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when ad fails to show.
                    Log.e(TAG, "Ad failed to show fullscreen content.");
                    mAdManagerInterstitialAd = null;
                }

                @Override
                public void onAdImpression() {
                    // Called when an impression is recorded for an ad.
                    Log.d(TAG, "Ad recorded an impression.");
                }

                @Override
                public void onAdShowedFullScreenContent() {
                    // Called when ad is shown.
                    Log.d(TAG, "Ad showed fullscreen content.");
                }
            });
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.");
        }
    }

    public int getRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
}
