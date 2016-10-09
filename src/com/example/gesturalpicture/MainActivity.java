package com.example.gesturalpicture;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class MainActivity extends Activity {
	ArrayList<String> arrayList = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		GridView picGrid = (GridView) findViewById(R.id.picGrid);
		arrayList = new ArrayList<String>();
		arrayList.add("http://wl.xiyilang.cc/images/viewpager/pic11.png?v=15");
		arrayList.add("http://wl.xiyilang.cc/images/viewpager/pic13.png?v=14");
		arrayList.add("http://wl.xiyilang.cc/images/viewpager/pic14.png?v=15");
		arrayList.add("http://wl.xiyilang.cc/images/viewpager/pic15.png?v=14");
		arrayList.add("http://wl.xiyilang.cc/images/viewpager/pic16.png?v=14");
		MyAdapter myAdapter = new MyAdapter();
		picGrid.setAdapter(myAdapter);
		
		picGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Log.e("xiyilang", "arg2=="+arg2);
				Intent intent = new Intent(MainActivity.this,PictureShowActivity.class);
				intent.putStringArrayListExtra("list", arrayList);
				intent.putExtra("position", arg2);
				startActivity(intent);
			}
		});
	}

	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(arrayList==null){
				return 0;
			}
			return arrayList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return arrayList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			ImageView imageView = new ImageView(getApplicationContext());
			imageView.setScaleType(ScaleType.FIT_XY);
			new NormalLoadPictrue().getPicture(arrayList.get(arg0), imageView);
//			image.bind(imageView, arrayList.get(arg0), ImageOptions.DEFAULT);
			return imageView;
		}
		
	}
}
