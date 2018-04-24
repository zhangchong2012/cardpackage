package com.example.zhangchong.myapplication.cardbag;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.example.zhangchong.myapplication.CardViewTag;
import com.example.zhangchong.myapplication.cardbag.cardanimation.DefaultBagLayoutListener;
import java.util.List;
import java.util.Stack;

/**
 * Created by zhangchong on 2018/4/19.
 */

public class CardBagControllerImpl
    implements ICardBagController, DefaultBagLayoutListener.IDefaultControllerLayoutListener {
    //卡片view的序列和队列的下标不一样.
    private ICardBagView mViewBag;
    private Stack<View> mViewList;

    public CardBagControllerImpl(Context context) {
        mViewList = new Stack<>();
        mViewBag = new CardBagViewImpl(context, this);
    }

    @Override
    public final void addView(int index, View view, boolean animation) {
        if (index > mViewList.size()) {
            return;
        } else if (index == mViewList.size()) {
            addLastView(view, animation);
        } else {
            mViewList.add(index, view);
            int displayIndex = shiftDisplayIndex(index);
            if (displayIndex < 0) {
                return;
            }
            mViewBag.addView(displayIndex, view, animation);
        }
    }

    @Override
    public void removeView(int index, boolean animation) {
        if (index >= mViewList.size()) {
            return;
        } else if (index == mViewList.size() - 1) {
            removeLastView(animation);
        } else {
            int displayIndex = shiftDisplayIndex(index);
            if (displayIndex >= 0) {
                mViewBag.removeView(displayIndex, animation);
            }
            mViewList.remove(index);
        }
    }

    @Override
    public void removeViewById(String id, boolean animation) {
        int index = mViewList.size() - 1;
        while (index >= 0) {
            View view = mViewList.get(index);
            CardViewTag tag = (CardViewTag) view.getTag();
            if (tag == null) continue;
            if (TextUtils.equals(tag.getViewId(), id)) {
                break;
            }
            --index;
        }
        removeView(index, animation);
    }

    @Override
    public void updateView(int index, View view, boolean animation) {
        if (index >= mViewList.size()) {
            return;
        } else if (index == mViewList.size() - 1) {
            updateLastView(view, animation);
        } else {
            int displayIndex = shiftDisplayIndex(index);
            if (displayIndex >= 0) {
                mViewBag.updateView(displayIndex, view, animation);
            }
            mViewList.set(index, view);
        }
    }

    @Override
    public View getCardView(int index) {
        if (mViewList.isEmpty()) {
            return null;
        }

        if (index < 0 || index >= mViewList.size()) {
            return null;
        }

        int displayIndex = shiftDisplayIndex(index);
        if (displayIndex > 0) {
            return mViewBag.getViewCard(displayIndex);
        } else {
            return mViewList.get(index);
        }
    }

    @Override
    public void addLastView(View view, boolean animation) {
        mViewList.push(view);
        mViewBag.addNewCard(view, animation);
    }

    @Override
    public void addLastViews(List<View> views, boolean animation) {
        mViewList.addAll(views);
        mViewBag.clearViews();

        int disPlayIndex = 0;
        int index = shiftDisplayIndex(disPlayIndex);
        index = index < 0 ? 0 : index;
        while (index < mViewList.size()){
            View view = mViewList.get(index);
            if(index == mViewList.size() - 1){
                mViewBag.addNewCard(view, true);
            }else{
                mViewBag.addView(disPlayIndex,view, false);
            }

            index ++;
            disPlayIndex++;
        }
    }

    @Override
    public void removeLastView(boolean animation) {
        if (mViewList.isEmpty()) {
            return;
        }
        mViewList.pop();
        mViewBag.removeNewCard(animation);
    }

    @Override
    public void updateLastView(View view, boolean animation) {
        if (mViewList.isEmpty()) {
            addLastView(view, animation);
            return;
        }

        mViewList.set(mViewList.size() - 1, view);
        mViewBag.updateNewCard(view, animation);
    }

    @Override
    //更新卡片包动画
    public void updateCardBagLayout(boolean animation) {
        if (mViewList.isEmpty()) {
            return;
        }
        mViewBag.updateCardBag(animation);
    }

    @Override
    public View getLastCardView() {
        if (mViewList.isEmpty()) {
            return null;
        }
        return mViewBag.getNewCard();
    }

    @Override
    public void syncCardView() {
        int count = mViewBag.getViewCount();
        for (int displayIndex = 0; displayIndex < count; displayIndex++) {
            View view = mViewBag.getViewCard(displayIndex);
            CardViewTag viewTag = (CardViewTag) view.getTag();
            int index = shiftIndex(displayIndex);
            View diff = getCardView(index);
            CardViewTag diffTag = (CardViewTag) view.getTag();

            if (!TextUtils.equals(viewTag.getViewId(), diffTag.getViewId())) {
                mViewBag.updateView(displayIndex, diff, false);
            }
        }
    }

    @Override
    public ViewGroup getCarBagView() {
        return mViewBag.getBagView();
    }

    @Override
    public int shiftDisplayIndex(int index) {
        //index必须在0~size-1之间
        if (index < 0 || index > mViewList.size() - 1) {
            return -1;
        }

        if (mViewList.size() < MAX_CARD) {
            return index;
        } else {
            return index + (mViewList.size() - MAX_CARD);
        }
    }

    @Override
    public int shiftIndex(int displayIndex) {
        if (displayIndex > MAX_CARD || displayIndex < 0) {
            return -1;
        }

        int index = displayIndex + (mViewList.size() - MAX_CARD);
        if (index >= 0 && index < mViewList.size()) {
            return index;
        }
        return -1;
    }

    @Override
    public int size() {
        return mViewList.size();
    }

    @Override
    public void controllerDisplay() {
        if (mViewBag.getViewCount() > ICardBagController.MAX_CARD) {
            mViewBag.removeView(0, false);
        }
    }

    @Override
    public void controllerDismiss() {
        int displayIndex = 0;
        int index = shiftIndex(displayIndex);
        if (index >= 0 && mViewList.get(index) != null) {
            View view = mViewList.get(index);
            if(view.getParent() != null){
                return;
            }
            mViewBag.addView(0, view, false);
        }
    }
}
