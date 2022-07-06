package com.reiserx.myapplication24.Methods;

import android.graphics.Color;
import android.view.View;

public class DesignLayout {
    public void Apply (final View view, final double radius, final double shadow, final String color, final boolean ripple) {
        if (ripple) {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(Color.parseColor(color));
            gd.setCornerRadius((int)radius);
            view.setElevation((int)shadow);
            android.content.res.ColorStateList clrb = new android.content.res.ColorStateList(new int[][]{new int[]{}}, new int[]{Color.parseColor("#9e9e9e")});
            android.graphics.drawable.RippleDrawable ripdrb = new android.graphics.drawable.RippleDrawable(clrb , gd, null);
            view.setClickable(true);
            view.setBackground(ripdrb);
        }
        else {
            android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
            gd.setColor(Color.parseColor(color));
            gd.setCornerRadius((int)radius);
            view.setBackground(gd);
            view.setElevation((int)shadow);
        }
    }

}
