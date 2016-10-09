package com.example.gesturalpicture;


import java.util.ArrayList;

import touchgallery.GalleryWidget.GalleryViewPager;
import touchgallery.GalleryWidget.UrlPagerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by Sinaan on 2016/3/2.
 */
public class PictureShowActivity extends Activity {
    private GalleryViewPager mViewPager;
    private ArrayList<String> picList = new ArrayList<>();
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture_show);

        initView();

        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, picList);

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(position);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView(){
        mViewPager = (GalleryViewPager)findViewById(R.id.dialog_show_viewPager);
        picList = getIntent().getStringArrayListExtra("list");
        position = getIntent().getIntExtra("position", 0);
    }

}
