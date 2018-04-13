package com.example.zhangchong.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by zhangchong on 2018/4/13.
 */

public class DialogCaptureLayout extends FrameLayout {
    private View mContentView;
    private ImageView mCaptureView;
    private Bitmap mCaputreBMP;

    public DialogCaptureLayout(@NonNull Context context) {
        super(context);
    }

    private void initCaptureView(){
        mCaptureView= new ImageView(getContext());
        mCaptureView.setScaleType(ImageView.ScaleType.FIT_XY);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(mCaptureView, params);
        mCaptureView.setVisibility(View.GONE);
    }

    public void setContentView(View contentView) {
        if(mContentView != null)
            return;
        initCaptureView();
        this.mContentView = contentView;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.addView(mContentView, params);
    }

    public void captureChacheBitmap(){
        if(mContentView == null)
            return;

        long startTime = System.currentTimeMillis();
        Log.v("msg", "start time:" +  startTime);
        //mContentView.setDrawingCacheEnabled(true);
        //mContentView.buildDrawingCache();
        //final Bitmap bitmap  = mContentView.getDrawingCache();
        //if (bitmap != null) {
        //    mCaputreBMP = Bitmap.createBitmap(bitmap);
        //    mContentView.setDrawingCacheEnabled(false);
        //} else {
        //    mCaputreBMP = null;
        //}

        mCaputreBMP = Bitmap.createBitmap(mContentView.getWidth(), mContentView.getHeight(), Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(mCaputreBMP);
        mContentView.draw(canvas);

        mCaptureView.setImageBitmap(mCaputreBMP);

        Log.v("msg", "duration time:" +  (System.currentTimeMillis() - startTime)
        + ", bitmp size:" + mCaputreBMP.getWidth() * mCaputreBMP.getHeight() * 2/1024 + "kb");
    }

    public void onDestroy(){

    }

    public void showContentView(){
        mContentView.setVisibility(View.VISIBLE);
        mCaptureView.setVisibility(View.GONE);
    }

    public void showCaptureView(){
        mCaptureView.setVisibility(View.VISIBLE);
        mContentView.setVisibility(View.GONE);
    }
}
