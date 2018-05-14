package com.why94.glide.drawable;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_URL = "https://img.picbling.cn/4071fffb-5870-4f40-a412-665e468fa7cb.jpg";

    private CheckBox mCbxUseGlidePlaceholderDrawable;
    private ImageView mIvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCbxUseGlidePlaceholderDrawable = findViewById(R.id.cbx_use_glide_placeholder_drawable);
        mIvShow = findViewById(R.id.iv_show);
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btn_refresh_image) {
            refreshImage();
        } else if (v instanceof RadioButton) {
            mIvShow.setScaleType(ImageView.ScaleType.valueOf(((RadioButton) v).getText().toString()));
        }
    }

    private void refreshImage() {
        Glide.with(this)
                .load(IMAGE_URL)
                .apply(new RequestOptions()
                        .placeholder(mCbxUseGlidePlaceholderDrawable.isChecked() ? new GlidePlaceholderDrawable(getResources(), R.drawable.placeholder) : getDrawable(R.drawable.placeholder))
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .skipMemoryCache(true)
                )
                .transition(new DrawableTransitionOptions().crossFade(1000))
                .into(new DrawableImageViewTarget(mIvShow) {
                    @Override
                    @SuppressLint("MissingSuperCall")
                    public void getSize(@NonNull final SizeReadyCallback cb) {
                        //由于图片比较小,加载特别快,可能看不到异常效果,这里延迟调用超类方法可以延迟开始加载图片
                        mIvShow.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                callSuperGetSize(cb);
                            }
                        }, 1000);
                    }

                    private void callSuperGetSize(SizeReadyCallback cb) {
                        super.getSize(cb);
                    }
                });
    }
}
