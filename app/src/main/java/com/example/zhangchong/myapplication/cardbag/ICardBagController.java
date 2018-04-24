package com.example.zhangchong.myapplication.cardbag;

import android.view.View;
import android.view.ViewGroup;
import java.util.List;

/**
 * Created by zhangchong on 2018/4/19.
 */

public interface ICardBagController {
    int MAX_CARD = 3;

    void addView(int index, View view, boolean animation);
    void removeView(int index, boolean animation);
    void removeViewById(String id, boolean animation);
    void updateView(int index, View view, boolean animation);
    View getCardView(int index);

    void addLastView(View view, boolean animation);
    void removeLastView(boolean animation);
    void updateLastView(View view, boolean animation);
    View getLastCardView();
    void addLastViews(List<View> views, boolean animation);


    void syncCardView();

    int shiftDisplayIndex(int index);
    int shiftIndex(int displayIndex);

    int size();

    //更新卡片包布局
    void updateCardBagLayout(boolean animation);
    ViewGroup getCarBagView();
}