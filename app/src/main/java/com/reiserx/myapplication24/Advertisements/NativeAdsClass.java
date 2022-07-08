package com.reiserx.myapplication24.Advertisements;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.google.android.ads.nativetemplates.NativeTemplateStyle;
import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdLoader;
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

    public void setDesign (ConstraintLayout constraintLayout) {
        int nightModeFlags =
                context.getResources().getConfiguration().uiMode &
                        Configuration.UI_MODE_NIGHT_MASK;
        switch (nightModeFlags) {
            case Configuration.UI_MODE_NIGHT_YES:
                colorDrawable = new ColorDrawable(ContextCompat.getColor(context, R.color.dark));
                break;

            case Configuration.UI_MODE_NIGHT_NO:
                colorDrawable = (ColorDrawable) constraintLayout.getBackground();
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                colorDrawable = (ColorDrawable) constraintLayout.getBackground();
                break;
        }
    }
}
