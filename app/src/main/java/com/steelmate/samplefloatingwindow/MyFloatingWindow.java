package com.steelmate.samplefloatingwindow;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * @author xt on 2019/10/16 9:47
 */
public class MyFloatingWindow {
    private static final int REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION = 999;

    private static final Application   sApplication;
    public static final  int           sDisplayWidthPixels;
    public static        int           sDisplayHeightPixels;
    private static final WindowManager sWindowManager;

    private final View                       mView;
    private final WindowManager.LayoutParams mLayoutParams;

    private boolean mIsViewAdded;


    static {
        sApplication = MyApp.sMyApp;
        sWindowManager = (WindowManager) sApplication.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        sWindowManager.getDefaultDisplay().getMetrics(dm);
        sDisplayWidthPixels = dm.widthPixels;
        sDisplayHeightPixels = dm.heightPixels;
    }

    /**
     * @param layoutRes 布局
     */
    public MyFloatingWindow(int layoutRes, int width, int height) {
        mView = View.inflate(sApplication, layoutRes, null);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mLayoutParams = getLayoutParams(width, height);
    }

    public boolean isViewAdded() {
        return mIsViewAdded;
    }

    public void setViewAdded(boolean viewAdded) {
        mIsViewAdded = viewAdded;
    }

    public View getView() {
        return mView;
    }

    public WindowManager.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    public boolean showFloatingWindow() {
        if (!hasPermission()) {
            return false;
        }
        if (isViewAdded()) {
            return false;
        }

        sWindowManager.addView(mView, mLayoutParams);
        setViewAdded(true);
        return true;
    }

    public boolean removeView() {
        if (!isViewAdded()) {
            return false;
        }
        sWindowManager.removeViewImmediate(mView);
        setViewAdded(false);
        return true;
    }

    public void setXy(int x, int y) {
        mLayoutParams.x = x;
        mLayoutParams.y = y;
        updateView();
    }

    public void updateSize(int width,int height){
        mLayoutParams.width = width;
        mLayoutParams.height = height;
        sWindowManager.updateViewLayout(mView,mLayoutParams);
    }

    public void updateView() {
        if (mLayoutParams.x + getView().getWidth() > sDisplayWidthPixels) {
            mLayoutParams.x = sDisplayWidthPixels - getView().getWidth();
        }
        if (mLayoutParams.x < 0) {
            mLayoutParams.x = 0;
        }
        if (mLayoutParams.y + getView().getHeight() > sDisplayHeightPixels) {
            mLayoutParams.y = sDisplayHeightPixels - getView().getHeight();
        }
        if (mLayoutParams.y < 0) {
            mLayoutParams.y = 0;
        }
        if (isViewAdded()) {
            sWindowManager.updateViewLayout(mView, mLayoutParams);
        }
    }

    /**
     * @param view            要处理事件的view
     * @param onTouchListener 透传touch事件
     */
    public void setViewMove(View view, final View.OnTouchListener onTouchListener) {
        view.setOnTouchListener(new View.OnTouchListener() {
            private int rawX;
            private int rawY;
            private int downX;
            private int downY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (onTouchListener != null) {
                    onTouchListener.onTouch(v, event);
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        rawX = (int) event.getRawX();
                        rawY = (int) event.getRawY();
                        downX = rawX;
                        downY = rawY;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int movedX = (int) event.getRawX() - rawX;
                        Log.d("movedX", "movedX=" + movedX);
                        int movedY = (int) event.getRawY() - rawY;
                        rawX = (int) event.getRawX();
                        rawY = (int) event.getRawY();
                        mLayoutParams.x = mLayoutParams.x + movedX;
                        mLayoutParams.y = mLayoutParams.y + movedY;

                        updateView();
                        break;
                    case MotionEvent.ACTION_UP:
                        if (((int) event.getRawX() == downX) && ((int) event.getRawY() == downY)) {
                            v.performClick();
                        }
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
    }

    public static WindowManager.LayoutParams getLayoutParams(int width, int height) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.gravity = Gravity.START | Gravity.TOP;
//        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.width = width;
        layoutParams.height = height;

        return layoutParams;
    }

    public static boolean hasPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(sApplication);
        }
        return true;
    }

    public static void requestPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + activity.getPackageName())), REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION);
        }
    }

    public static boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ACTION_MANAGE_OVERLAY_PERMISSION) {
            Context applicationContext = sApplication.getApplicationContext();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(applicationContext)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            sApplication.startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + sApplication.getPackageName())));
        }
    }
}
