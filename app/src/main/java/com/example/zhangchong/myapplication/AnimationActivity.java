package com.example.zhangchong.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import com.example.zhangchong.myapplication.cardbag.CardBagControllerImpl;
import com.example.zhangchong.myapplication.cardbag.ICardBagController;
import java.util.ArrayList;
import java.util.List;

public class AnimationActivity extends AppCompatActivity {
    public static final String TYPE_VIEW = "view";
    private FrameLayout mLayout;
    private int mRandom = 0;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams param;
    ICardBagController mCardBagController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        mCardBagController = new CardBagControllerImpl(this);
        ViewGroup viewGroup = mCardBagController.getCarBagView();
        mLayout = findViewById(R.id.animation_layout);

        mLayout.addView(viewGroup,  new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void addAnimation(View view) {
        addNewView();
    }

    public void removeAnimation(View view) {
        removeView();
    }

    public void randomRemove(View view){
        remove();
    }
    public void showWindow(View view){
        addWindowView();
    }


    private void showCustomWindow(){
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getApplication(). getSystemService(Context.WINDOW_SERVICE);
        }

        if (param == null) {
            param = new WindowManager.LayoutParams();

            param.type = WindowManager.LayoutParams.TYPE_TOAST;// 系统提示类型,重要
            param.format = PixelFormat.RGBA_8888;
            param.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE; // 不能抢占聚焦点
            param.flags = param.flags | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            param.flags = param.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS; // 排版不受限制

            param.alpha = 1.0f;

            param.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;   //调整悬浮窗口至左上角
            //以屏幕左上角为原点，设置x、y初始值
            param.x = 0;
            //param.y = UIUtils.dp2px(mContext, 12);
            param.y = 80;

            //设置悬浮窗口长宽数据
            param.width = 980;
            param.height = WindowManager.LayoutParams.WRAP_CONTENT;

        }
        View view = new View(this);
        view.setBackgroundColor(Color.BLUE);
        mWindowManager.addView(view, param);
    }


    private void addWindowView(){
        int max = 2;
        List<View> views = new ArrayList<>();
        for (int j = 0; j < max; j++) {

            FrameLayout frameLayout  = new FrameLayout(this);
            View view = new View(this);
            int i = (int) (Math.random() * 4);
            while (i == mRandom){
                i = (int) (Math.random() * 4);
            }
            switch (i) {
                case 0:
                    view.setBackgroundColor(Color.RED);
                    break;
                case 1:
                    view.setBackgroundColor(Color.BLACK);
                    break;
                case 2:
                    view.setBackgroundColor(Color.GREEN);
                    break;
                case 3:
                    view.setBackgroundColor(Color.BLUE);
                    break;
            }
            frameLayout.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            frameLayout.setTag(new CardViewTag("index"+ i));
            views.add(frameLayout);
            mRandom = i;
        }
        mCardBagController.addLastViews(views, true);

    }

    private void addNewView() {
        FrameLayout frameLayout  = new FrameLayout(this);
        View view = new View(this);
        int i = (int) (Math.random() * 4);
        while (i == mRandom){
            i = (int) (Math.random() * 4);
        }
        switch (i) {
            case 0:
                view.setBackgroundColor(Color.RED);
                break;
            case 1:
                view.setBackgroundColor(Color.BLACK);
                break;
            case 2:
                view.setBackgroundColor(Color.GREEN);
                break;
            case 3:
                view.setBackgroundColor(Color.BLUE);
                break;
        }
        frameLayout.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        frameLayout.setTag(new CardViewTag("index"+ i));
        mCardBagController.addLastView(frameLayout, true);
        mRandom = i;
    }

    private void removeView(){
        mCardBagController.removeLastView(true);
    }

    private void remove(){
        int index = (int) (Math.random() * mCardBagController.size());
        Log.e("msg", " remove index:" + index);

        mCardBagController.getLastCardView();
        mCardBagController.removeView(index, true);
    }
}
