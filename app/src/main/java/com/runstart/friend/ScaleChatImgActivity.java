package com.runstart.friend;

import android.app.Activity;



/**
 * Created by g on 2017/10/12.
 */

public class ScaleChatImgActivity extends Activity {
//    private RelativeLayout MainView;
//    private ImageView showImageView;
//    private ImageInfoObj imageInfoObj;
//    private ImageWidgetInfoObj imageWidgetInfoObj;
//    Button button;
//
//    // 屏幕宽度
//    public float Width;
//    //原图高
//    private float y_img_h;
//    // 屏幕高度
//    public float Height;
//    private float size, size_h, img_w, img_h;
//    protected float to_x = 0;
//    protected float to_y = 0;
//    private float tx;
//    private float ty;
//
//
//    private final Spring spring = SpringSystem
//            .create()
//            .createSpring()
//            .addListener(new ExampleSpringListener());
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.friend_chat_image);
//        LogUtils.d("--->ShowImageActivity onCreate");
//        findId();
//        init();
//        Listener();
//    }
//
//    private void findId() {
//        MainView = (RelativeLayout) findViewById(R.id.chat_img_rl);
//        button = (Button) findViewById(R.id.chat_img_button);
//    }
//
//    private void init() {
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        Width = dm.widthPixels;
//        Height = dm.heightPixels;
//
//        imageInfoObj = (ImageInfoObj) getIntent().getSerializableExtra("imageInfoObj");
//        imageWidgetInfoObj = (ImageWidgetInfoObj) getIntent().getSerializableExtra("imageWidgetInfoObj");
//        if (imageInfoObj == null) {
//            LogUtils.d("--->imageInfoObj==null");
//        }
//        if (imageWidgetInfoObj == null) {
//            LogUtils.d("--->imageWidgetInfoObj==null");
//        }
//
//        showImageView = new ImageView(this);
//        showImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//
//        Glide.with(ScaleChatImgActivity.this).load(imageInfoObj.imageUrl).into(showImageView);
//
//        img_w = imageWidgetInfoObj.width;
//        img_h = imageWidgetInfoObj.height - 300;
//        size = Width / img_w;
//        y_img_h = imageInfoObj.imageHeight * Width / imageInfoObj.imageWidth;
//        size_h = y_img_h / img_h;
//
//        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams((int) imageWidgetInfoObj.width,
//                (int) imageWidgetInfoObj.height);
//        p.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//        p.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
//        showImageView.setLayoutParams(p);
//        p.setMargins((int) imageWidgetInfoObj.x,
//                (int) imageWidgetInfoObj.y, (int) (Width - (imageWidgetInfoObj.x + imageWidgetInfoObj.width)),
//                (int) (Height - (imageWidgetInfoObj.y + imageWidgetInfoObj.height)));
//        MainView.addView(showImageView);
//
//        new Handler().post(new Runnable() {
//            public void run() {
//                ShowImageView();
//            }
//        });
//    }
//
//    private void Listener() {
//        showImageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ShowImageView();
//            }
//        });
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               // ShowImageView();
//            }
//        });
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        LogUtils.d("--->ShowImageActivity onResume");
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        LogUtils.d("--->ShowImageActivity onPause");
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        LogUtils.d("--->ShowImageActivity onDestroy");
//    }
//
//    private class ExampleSpringListener implements SpringListener {
//
//        @Override
//        public void onSpringUpdate(Spring spring) {
//            double CurrentValue = spring.getCurrentValue();
//            float mappedValue = (float) SpringUtil.mapValueFromRangeToRange(CurrentValue, 0, 1, 1, size);
//            float mapy = (float) SpringUtil.mapValueFromRangeToRange(CurrentValue, 0, 1, 1, size_h);
//            showImageView.setScaleX(mappedValue);
//            showImageView.setScaleY(mapy);
//            if (CurrentValue == 1) {
////    showImageView.setVisibility(View.GONE);
//            }
//        }
//
//        @Override
//        public void onSpringAtRest(Spring spring) {
//
//        }
//
//        @Override
//        public void onSpringActivate(Spring spring) {
//
//        }
//
//        @Override
//        public void onSpringEndStateChange(Spring spring) {
//
//        }
//    }
//
//    //实现效果
//    private void MoveView() {
//
//        ObjectAnimator.ofFloat(MainView, "alpha", 0.8f).setDuration(0).start();
//        MainView.setVisibility(View.VISIBLE);
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(
//                ObjectAnimator.ofFloat(showImageView, "translationX", tx).setDuration(200),
//                ObjectAnimator.ofFloat(showImageView, "translationY", ty).setDuration(200),
//                ObjectAnimator.ofFloat(MainView, "alpha", 1).setDuration(200)
//
//        );
//        set.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                showImageView.setScaleType(ImageView.ScaleType.FIT_XY);
//                spring.setEndValue(1);
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        set.start();
//
//    }
//
//    //关闭页面
//    private void MoveBackView() {
//        AnimatorSet set = new AnimatorSet();
//        set.playTogether(
//                ObjectAnimator.ofFloat(showImageView, "translationX", to_x).setDuration(200),
//                ObjectAnimator.ofFloat(showImageView, "translationY", to_y).setDuration(200)
//        );
//        set.addListener(new Animator.AnimatorListener() {
//            @Override
//            public void onAnimationStart(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animator animator) {
//                finish();
//            }
//
//            @Override
//            public void onAnimationCancel(Animator animator) {
//
//            }
//
//            @Override
//            public void onAnimationRepeat(Animator animator) {
//
//            }
//        });
//        set.start();
//    }
//
//    //具体动画处理类
//    private void ShowImageView() {
//        if (spring.getEndValue() == 0) {
//            //弹动摩擦力
//            spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(300, 5));
//            //动画结束后出现的位置
//            tx = 0;
//            ty = Height / 2 - (imageWidgetInfoObj.y + img_h + 600);
//            MoveView();
//            return;
//        }
//        spring.setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(1, 5));
//        spring.setEndValue(0);
//        new Handler().post(new Runnable() {
//            public void run() {
//                MoveBackView();
//            }
//        });
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//
//            showImageView.setVisibility(View.VISIBLE);
//            ShowImageView();
//
//        }
//        return true;
//    }
}
