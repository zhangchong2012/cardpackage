package com.example.zhangchong.myapplication.cardbag.cardanimation;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangchong on 2018/4/20.
 */

public interface ICardBagViewAnimation {
    //快速结束动画.
    void endAnimation();

    //缩小卡片
    void dismissAnimation(ViewGroup container, View view);

    //放大卡片
    void displayAnimation(ViewGroup container, View view);

    //同步所有的view
    void syncAllView(ViewGroup container, boolean isAnimation);
}
