package com.example.news;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;


public class NewsItemActivity extends Activity{
	private String mItemUrl;
	private String mItemTitle;
	private ImageView mItemBackIv;
	private WebView mItemWebView;
	private TextView mItemTitleTv;
	private NewsItemView mNewsItemWebView;
	private NewsItemWebViewAsyncTask mNewsItemWebViewAsyncTask;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏,一定要写在 setContentView（）前
		setContentView(R.layout.newsitemwebview);
		mItemWebView=(WebView)findViewById(R.id.wb_news_item);
		mItemTitleTv=(TextView)findViewById(R.id.tv_wv_item_title);
		mItemBackIv=(ImageView)findViewById(R.id.iv_item_back);
		mItemBackIv.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		//设置支持JavaScript脚本
		WebSettings webSettings = mItemWebView.getSettings();
		webSettings.setJavaScriptEnabled(true); 
		webSettings.setBuiltInZoomControls(true);
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		getIntentData();
		mItemTitleTv.setText(mItemTitle);
        mNewsItemWebView=new NewsItemView();
        mNewsItemWebViewAsyncTask=new NewsItemWebViewAsyncTask();
        mNewsItemWebViewAsyncTask.execute();
	}
	/*
	 * 开启新的线程下载新闻内容
	 */
	private  class NewsItemWebViewAsyncTask extends AsyncTask<String, Integer, String>{

		protected String doInBackground(String... params) {
			mNewsItemWebView.newsItemWebView(mItemUrl);
			//mItemWebView.loadUrl(mItemUrl);
			return null;
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mItemWebView.loadDataWithBaseURL(null, mNewsItemWebView.getItemData(), "text/html", "utf-8", null);
			//mItemWebView.loadUrl(mItemUrl);
		}	
	}
	
	/*
	 * 获取主页Activity传来的数据
	 */
	public void getIntentData(){
		 //取得从上一个Activity当中传递过来的Intent对象  
        Intent mIntent = getIntent();  
        //从Intent当中根据key取得value  
        mItemUrl= mIntent.getStringExtra("mItemUrl"); 
        mItemTitle=mIntent.getStringExtra("mItemTitle");
	}
    
}




