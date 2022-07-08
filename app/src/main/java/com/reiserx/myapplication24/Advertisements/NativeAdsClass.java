package com.reiserx.myapplication24.Advertisements;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.reiserx.myapplication24.BuildConfig;

public class NativeAdsClass {
    Context context;
    TemplateView template;
    ColorDrawable colorDrawable;
    String ADID;

    public NativeAdsClass(Context context, TemplateView template, ColorDrawable colorDrawable) {
        this.context = context;
        this.template = template;
        this.colorDrawable = colorDrawable;
        if (BuildConfig.DEBUG) {
            ADID = "/6499/example/native";
        } else {
            ADID = "ca-app-pub-1588658066763563/3393249593";
        }
    }

    public void loadAd () {
        MobileAds.initialize(context);
        AdLoader adLoader = new AdLoader.Builder(context, ADID)
                .forNativeAd(nativeAd -> {
                    NativeTemplateStyle styles = new
                            NativeTemplateStyle.Builder().withMainBackgroundColor(colorDrawable).build();
                    template.setStyles(styles);
                    template.setNativeAd(nativeAd);
                })
                .build();

        adLoader.loadAd(new AdManagerAdRequest.Builder().build());
    }
}
