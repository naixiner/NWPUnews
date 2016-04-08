package com.example.news;

import java.io.IOException;

import com.example.news.MainListView.OnHeadRefreshListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private  MainListView homepageLv;
	private RelativeLayout homepageMainViewRl;
	private NewsAnalysisAsyncTask newsAnalysisAsyncTask;
	private NewsList  newsList;
	private NewsSql newsSql;
	private ImageView ComprehensiveNewsIv;
	private ImageView EducationalNewsIv;
	private ImageView AcademicNewsIv;
	private ImageView StudyAbroadNewsIv;
	private ImageView DocumentNewsIv;
	private ImageView NetworkNewsIv;
	private ImageView homepageBufferIv;
	private ImageView schoolfellowIv;
	public static String nowPlateUrl;
	
	private static int mPlateId;  
	public static final String[] ARRAY_PLATED={"ComprehensiveNews","EducationalNews","AcademicNews","StudyAbroadNews","DocumentNews","NetworkNews"};
	private  int[] mClickPlatedTime={0,0,0,0,0,0,0,0};//记录每个模块被点击数       
	private ConnectivityManager cm;
	private boolean flag=false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏,一定要写在 setContentView（）前
		setContentView(R.layout.newshomepage);
		homepageMainViewRl=(RelativeLayout)findViewById(R.id.rl_main_view);
		
		homepageLv=(MainListView)findViewById(R.id.lv_homepage_text);
		homepageLv.setOnHeadRefreshListener(new HeadRefresh());
		cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);   //获取网络通讯类的实例 ConnectivityManager 
   	   	newsList=new NewsList();
		mPlateId=0;//初始化为工大要闻界面
		nowPlateUrl=NewsUrl.ComprehensiveNews_url;
		newsSql=new NewsSql(MainActivity.this);
		 initItemOnClick();
		initHomepagePlate();//含有要闻按钮模拟点击功能，所以放在数据库声明对象后面
		
	}

	

	@Override
	protected void onStart() {
		super.onStart();
		homepageLv.setAdapter(NewsAdapter.ListView(MainActivity.this,
                newsSql.getNewsData(ARRAY_PLATED[mPlateId])));
	
	}
	
	/*
	 * 初始化item点击的监听
	 */
	public void initItemOnClick(){
		homepageLv.setOnItemClickListener(new OnItemClickListener(){

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				arg2--;//由于加了个头文件
				
				if (cm.getActiveNetworkInfo() != null) {
			         flag = cm.getActiveNetworkInfo().isAvailable();   
			         }
			     if(!flag){
				    Toast.makeText(MainActivity.this,"请检查网络连接" , Toast.LENGTH_SHORT ).show();
				    flag=false;//重新置于false
			      System.out.println("请检查网络连接");
				 }else{
						Intent mIntent=new Intent();
						mIntent.putExtra("mItemUrl", newsSql.getNewsData(ARRAY_PLATED[mPlateId]).get(arg2).getUrl());
						mIntent.putExtra("mItemTitle", newsSql.getNewsData(ARRAY_PLATED[mPlateId]).get(arg2).getTitle());
						
						//从数据库中读取数据
						
						mIntent.setClass(MainActivity.this,NewsItemActivity.class);//用WEbView显示单个新闻
						MainActivity.this.startActivity(mIntent);
				 }
			}
		});		
	}
	public class HeadRefresh implements OnHeadRefreshListener{

		public void onHeadRefrend() {
			System.out.println("刷新接口已经建立");
			
			newsAnalysisAsyncTask=new NewsAnalysisAsyncTask();
			newsAnalysisAsyncTask.execute();
			//判断网络是否连接
			if (cm.getActiveNetworkInfo() != null) {
		         flag = cm.getActiveNetworkInfo().isAvailable();   
		         }
		     if(!flag){
			    Toast.makeText(MainActivity.this,"请检查网络连接" , Toast.LENGTH_SHORT ).show();
			    flag=false;//重新置于false
		      System.out.println("请检查网络连接");
			 }
		}
	}

	/*
	 * 开启新的线程下载新闻内容
	 */
	private  class NewsAnalysisAsyncTask extends AsyncTask<String, Integer, String>{
		
		@Override
		protected void onPreExecute() {
			
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) { //因为新闻解析需要阻塞线程，所以不能再主线程中操作
			System.out.println("开启下载News"+ARRAY_PLATED[mPlateId]);
			 try {
				newsList.newsListAnalysis(nowPlateUrl);
			} catch (IOException e) {
				e.printStackTrace();
			}
			 return null;
		}
		
		@Override  
	    protected void onPostExecute(String result) {  

			System.out.println("下载News结束"+ARRAY_PLATED[mPlateId]);
			homepageLv.onRefreshComplete();//刷新结束
	        // 返回HTML页面的内容  
			newsSql.setNewsData(newsList.getNewsList(),ARRAY_PLATED[mPlateId]);
			mClickPlatedTime[mPlateId]++;//说明这个模块的数据已经成功加载了一次
			homepageLv.setAdapter(NewsAdapter.ListView(MainActivity.this,
	                newsSql.getNewsData(ARRAY_PLATED[mPlateId])));
			Log.v("--OnPostExecute--", "done");
		}
	}
	


 /*
  *  更换新闻版块按键初始化  加监听
  */
	public void initHomepagePlate(){
		
		 ComprehensiveNewsIv=(ImageView)findViewById(R.id.iv_main_news);
		 ComprehensiveNewsIv.setOnClickListener(this);
		 EducationalNewsIv=(ImageView)findViewById(R.id.iv_inform);
		 EducationalNewsIv.setOnClickListener(this);
		 AcademicNewsIv=(ImageView)findViewById(R.id.iv_purchase);
		 AcademicNewsIv.setOnClickListener(this);
		 StudyAbroadNewsIv=(ImageView)findViewById(R.id.iv_chair);
		 StudyAbroadNewsIv.setOnClickListener(this);
		 DocumentNewsIv=(ImageView)findViewById(R.id.iv_media);
		 DocumentNewsIv.setOnClickListener(this);
		 NetworkNewsIv=(ImageView)findViewById(R.id.iv_all_news);
		 NetworkNewsIv.setOnClickListener(this);

		 
		 initPlateSrc();
		/**初始化*/
		 ComprehensiveNewsIv.performClick();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		switch(v.getId()){
		case R.id.iv_main_news:
			mPlateId=0;
			break;
		case R.id.iv_inform:
			mPlateId=1;
			break;
		case R.id.iv_purchase:
			mPlateId=2;
			break;
		case R.id.iv_chair:
			mPlateId=3;
			break;
		case R.id.iv_media:
			mPlateId=4;
			break;
		case R.id.iv_all_news:
			mPlateId=5;
			break;

		}
		toChangePlate();
	}
	/*
	 * 用于模块的图片显示
	 */
public void initPlateSrc(){
	 ComprehensiveNewsIv.setImageResource(R.drawable.comprehensivenews);
	 EducationalNewsIv.setImageResource(R.drawable.educationalnews);
	 AcademicNewsIv.setImageResource(R.drawable.academicnews);
	 StudyAbroadNewsIv.setImageResource(R.drawable.studyabroadnews);
	 DocumentNewsIv.setImageResource(R.drawable.documentnews);
	 NetworkNewsIv.setImageResource(R.drawable.networknews);

	
} 

	/*
	 * 用于模块改变时的显示
	 */
public void toChangePlate(){
	initPlateSrc();
	switch(mPlateId){
	case 0:
		/**综合通知*/
		nowPlateUrl=NewsUrl.ComprehensiveNews_url;
		ComprehensiveNewsIv.setImageResource(R.drawable.comprehensive_down);
		break;
	case 1:
		/**教务教学*/
		nowPlateUrl=NewsUrl.EducationalNews_url;
		EducationalNewsIv.setImageResource(R.drawable.educational_down);
		break;
	case 2:
		/**学术交流*/
		nowPlateUrl=NewsUrl.AcademicNews_url;
		AcademicNewsIv.setImageResource(R.drawable.academic_down);
		break;
	case 3:
		/**留学信息*/
		nowPlateUrl=NewsUrl.StudyAbroadNews_url;
		StudyAbroadNewsIv.setImageResource(R.drawable.studyabroad_down);
		break;
	case 4:
		/**文献服务*/
		nowPlateUrl=NewsUrl.DocumentNews_url;
		DocumentNewsIv.setImageResource(R.drawable.document_down);
		break;
	case 5:
		/**网络服务*/
		nowPlateUrl=NewsUrl.NetworkNews_url;
		NetworkNewsIv.setImageResource(R.drawable.network_down);
		break;

	}
	System.out.println(mClickPlatedTime[mPlateId]);
	if(mClickPlatedTime[mPlateId]==0){
		
	 // 检测是否联网
	 if (cm.getActiveNetworkInfo() != null) {
         flag = cm.getActiveNetworkInfo().isAvailable();   
         }
     if(!flag){
	    Toast.makeText(MainActivity.this,"请检查网络连接" , Toast.LENGTH_SHORT).show();
	    flag=false;//重新置于false
      System.out.println("请检查网络连接");
	 }else{
				homepageLv.state=2;
				homepageLv.chargeHeadState();//ListView Head为刷新界面
			newsAnalysisAsyncTask=new NewsAnalysisAsyncTask();
			newsAnalysisAsyncTask.execute();
			 }
	 }
		homepageLv.setAdapter(NewsAdapter.ListView(MainActivity.this,
                newsSql.getNewsData(ARRAY_PLATED[mPlateId])));
	
}

protected void dialog() {
	  AlertDialog.Builder builder = new Builder(MainActivity.this);
	  builder.setMessage("确认退出吗？");

	  builder.setTitle("提示");

	  builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

	   @Override
	   public void onClick(DialogInterface dialog, int which) {
	    dialog.dismiss();
	  //  HomePageActivity.this.finish();
	  android.os.Process.killProcess(android.os.Process.myPid());//退出程序
	   }
	  });

	  builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	   @Override
	   public void onClick(DialogInterface dialog, int which) {
	    dialog.dismiss();
	   }
	  });

	  builder.create().show();
	 } 
	 /// 按键按下事件监听
	@Override                                                
public boolean onKeyDown(int keyCode, KeyEvent event) {     
		//处理back返回按键
	if (keyCode == KeyEvent.KEYCODE_BACK) {
		//退出
		dialog();
			//System.exit(0);
		//表示此按键已处理，不再交给系统处理，
	//从而避免游戏被切入后台
		return true;
	}
	return super.onKeyDown(keyCode, event);//不是Back键就交给父类解决
}
}
