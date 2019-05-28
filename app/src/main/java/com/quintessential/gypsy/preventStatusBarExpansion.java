package com.quintessential.gypsy;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;

public class preventStatusBarExpansion {
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public preventStatusBarExpansion(Context context) {
        ////////////////////////////////////////////////////////////////////////////////////////////////
        WindowManager manager = ((WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE));
        Activity activity = (Activity) context;
        WindowManager.LayoutParams localLayoutParams = new WindowManager.LayoutParams();
        localLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        localLayoutParams.gravity = Gravity.TOP;
        localLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        localLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        int resId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int result = 0;
        if (resId > 0) {
            result = activity.getResources().getDimensionPixelSize(resId);
        }
        localLayoutParams.height = result;
        localLayoutParams.format = PixelFormat.TRANSPARENT;
        customViewGroup view = new customViewGroup(context);
        manager.addView(view, localLayoutParams);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    public static class customViewGroup extends ViewGroup {
        public customViewGroup(Context context) {
            super(context);
        }
        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) { }
        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            return true;
        }
    }
}