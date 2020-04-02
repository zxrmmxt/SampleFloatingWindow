package com.steelmate.samplefloatingwindow;

import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author xt on 2019/10/15 16:43
 */
public class TpmsFloatingWindowModel {
    private       Handler                            mTpmsWindowShowHideHandler = new Handler();
    public        MyFloatingWindow                   myFloatingButton;
    private       TextView                           mTextViewButton;
    public        MyFloatingWindow                   myFloatingWindowTpmsInfo;
    private       FloatingWindowTpmsInfoViewHolder   mViewHolderLeftTop;
    private       FloatingWindowTpmsInfoViewHolder   mViewHolderRightTop;
    private       FloatingWindowTpmsInfoViewHolder   mViewHolderLeftBottom;
    private       FloatingWindowTpmsInfoViewHolder   mViewHolderRightBottom;
    private final FloatingWindowTpmsInfoViewHolder[] mViewHolderArray;
    private       ObjectAnimator                     mButtonAplhaAnimator;

    public TpmsFloatingWindowModel() {
        {
            myFloatingButton = new MyFloatingWindow(R.layout.layout_floating_button, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
            myFloatingButton.getView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    myFloatingButton.getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    myFloatingButton.setXy(MyFloatingWindow.sDisplayWidthPixels - myFloatingButton.getView().getWidth(), MyFloatingWindow.sDisplayHeightPixels / 2 - myFloatingButton.getView().getHeight() / 2);
                }
            });
            myFloatingButton.setViewMove(myFloatingButton.getView(), new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            resetButtonAplha();
                            break;
                        case MotionEvent.ACTION_UP:
                            startButtonAlphaAnimator();
                            break;
                        default:
                            break;
                    }
                    return false;
                }
            });
            myFloatingButton.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFloatingWindowTpms();
                    myFloatingButton.removeView();
                }
            });
            mTextViewButton = myFloatingButton.getView().findViewById(R.id.textViewButton);
        }
        {
            myFloatingWindowTpmsInfo = new MyFloatingWindow(R.layout.layout_floating_window, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            View rootView = myFloatingWindowTpmsInfo.getView();
            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFloatingWindowButton();
                    myFloatingWindowTpmsInfo.removeView();
                }
            });
            mViewHolderLeftTop = new FloatingWindowTpmsInfoViewHolder((ImageView) rootView.findViewById(R.id.imageViewTireLeftTop), rootView.findViewById(R.id.layoutTpmsValueLeftTop));
            mViewHolderRightTop = new FloatingWindowTpmsInfoViewHolder((ImageView) rootView.findViewById(R.id.imageViewTireRightTop), rootView.findViewById(R.id.layoutTpmsValueRightTop));
            mViewHolderLeftBottom = new FloatingWindowTpmsInfoViewHolder((ImageView) rootView.findViewById(R.id.imageViewTireLeftBottom), rootView.findViewById(R.id.layoutTpmsValueLeftBottom));
            mViewHolderRightBottom = new FloatingWindowTpmsInfoViewHolder((ImageView) rootView.findViewById(R.id.imageViewTireRightBottom), rootView.findViewById(R.id.layoutTpmsValueRightBottom));
            mViewHolderArray = new FloatingWindowTpmsInfoViewHolder[]{
                    mViewHolderRightTop,
                    mViewHolderLeftTop,
                    mViewHolderRightBottom,
                    mViewHolderLeftBottom
            };
        }
    }

    private boolean showFloatingWindow(MyFloatingWindow myFloatingWindow) {
        return myFloatingWindow.showFloatingWindow();
    }

    public boolean showFloatingWindowTpms() {
        boolean showFloatingWindow = showFloatingWindow(myFloatingWindowTpmsInfo);
        if (showFloatingWindow) {
            autoHideTpmsInfoWindow();
        }
        return showFloatingWindow;
    }

    public boolean showFloatingWindowButton() {
        boolean showFloatingWindow = showFloatingWindow(myFloatingButton);
        if (showFloatingWindow) {
            startButtonAlphaAnimator();
        }
        return showFloatingWindow;
    }

    public void startButtonAlphaAnimator() {
        resetButtonAplha();
        mButtonAplhaAnimator = ObjectAnimator.ofFloat(mTextViewButton, "alpha", 0.8f, 0.4f);
        mButtonAplhaAnimator.setStartDelay(5000);
        mButtonAplhaAnimator.setDuration(1000);
        mButtonAplhaAnimator.start();
    }

    private void resetButtonAplha() {
        if (mButtonAplhaAnimator != null) {
            mButtonAplhaAnimator.cancel();
        }
        mTextViewButton.setAlpha(0.8f);
    }

    public void showAlarm(String[] pressArray, String[] tempArray, boolean[] isPressAlarmArray, boolean[] isTempAlarmArray) {
        for (int i = 0; i < mViewHolderArray.length; i++) {
            mViewHolderArray[i].showTpms(pressArray[i], tempArray[i], isPressAlarmArray[i], isTempAlarmArray[i]);
        }

        if (isAlarm()) {
            showFloatingWindowTpms();
            myFloatingButton.removeView();
        } else {
            if (myFloatingWindowTpmsInfo.isViewAdded()) {
                autoHideTpmsInfoWindow();
            }
        }
    }

    private void autoHideTpmsInfoWindow() {
        mTpmsWindowShowHideHandler.removeCallbacksAndMessages(null);
        mTpmsWindowShowHideHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAlarm()) {
                    showFloatingWindowButton();
                    myFloatingWindowTpmsInfo.removeView();
                }
            }
        }, 5000);
    }

    private boolean isAlarm() {
        return mViewHolderLeftTop.mIsAlarm || mViewHolderRightTop.mIsAlarm || mViewHolderLeftBottom.mIsAlarm || mViewHolderRightBottom.mIsAlarm;
    }

    private class FloatingWindowTpmsInfoViewHolder {
        private ImageView mImageViewTire;
        private TextView  mTextViewPress;
        private TextView  mTextViewTemp;
        private View      mViewUnderline;

        private boolean mIsAlarm;

        public FloatingWindowTpmsInfoViewHolder(ImageView imageViewTire, View contentView) {
            mImageViewTire = imageViewTire;
            mTextViewPress = contentView.findViewById(R.id.textViewPress);
            mTextViewTemp = contentView.findViewById(R.id.textViewTemp);
            mViewUnderline = contentView.findViewById(R.id.viewUnderline);

            showTpms("--", "--", false, false);
        }

        public void showTpms(String press, String temp, boolean isPressAlarm, boolean isTempAlarm) {
            mIsAlarm = isPressAlarm || isTempAlarm;
            {
                mTextViewPress.setText(press);
                mTextViewTemp.setText(temp);
            }
            {
                if (mIsAlarm) {
                    mTextViewButton.setBackgroundResource(R.mipmap.icon_wheel_alarmban_red);
                    mImageViewTire.setBackgroundResource(R.mipmap.icon_wheelaram_red);
                } else {
                    mTextViewButton.setBackgroundResource(R.mipmap.icon_wheel_alarmban_white);
                    mImageViewTire.setBackgroundResource(R.mipmap.icon_wheel_gray);
                }
            }
            int colorAlarm = 0xFFFF0000;
            if (isPressAlarm) {
                mTextViewPress.setTextColor(colorAlarm);
            } else {
                mTextViewPress.setTextColor(Color.WHITE);
            }
            if (isTempAlarm) {
                mTextViewTemp.setTextColor(colorAlarm);
                mViewUnderline.setBackgroundColor(colorAlarm);
            } else {
                mTextViewTemp.setTextColor(Color.WHITE);
                mViewUnderline.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
