package com.example.zhangchong.myapplication.cardbag;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhangchong on 2018/4/19.
 */

public interface ICardBagView {
    ViewGroup getBagView();

    void addView(int index, View view, boolean animation);
    void removeView(int index, boolean animation);
    void updateView(int index,View view, boolean animation);
    View getViewCard(int index);

    void addNewCard(View view, boolean animation);
    void removeNewCard(boolean animation);
    void updateNewCard(View view, boolean animation);
    View getNewCard();

    int getViewCount();

    //更新卡片包动画
    void updateCardBag(boolean animation);
    void clearViews();
}
