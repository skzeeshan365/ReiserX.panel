package com.reiserx.myapplication24.Advertisements;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.reiserx.myapplication24.BuildConfig;
import com.reiserx.myapplication24.R;

public class NativeAdsClass {
    Context context;
    TemplateView template;
    ColorDrawable colorDrawable;
    String ADID;

    public NativeAdsClass(Context context, TemplateView template, ConstraintLayout constraintLayout) {
        this.context = context;
        this.template = template;
        setDesign(constraintLayout);
        if (BuildConfig.DEBUG) {
            ADID = "/6499/example/native";
        } else {
            ADID = "ca-app-pub-1588658066763563/3393249593";
        }
        template.setVisibility(View.GONE);
    }

    public void loadAd () {
        MobileAds.initialize(context);
        AdLoader adLoader = new AdLoader.Builder(context, ADID)
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                }).withAdListener(new AdListener() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                        template.setVisibility(View.GONE);
                        Log.d("NativeAdsClass", loadAdError.toString());
                    }

                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        template.setVisibility(View.VISIBLE);
                        Log.d("NativeAdsClass", "loaded");
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                    }
                })
                .build();
        adLoader.loadAd(new AdManagerAdRequest.Builder().build());
    }

    public void setDesign (ConstraintLayout constraintLayout) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.dark));
                template.setBackgroundColor(context.getColor(R.color.dark));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                colorDrawable = (ColorDrawable) constraintLayout.getBackground();
                template.setBackgroundColor(context.getColor(R.color.dark));
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                colorDrawable = (ColorDrawable) constraintLayout.getBackground();
                template.setBackgroundColor(context.getColor(R.color.dark));
                break;
        }
    }
}
