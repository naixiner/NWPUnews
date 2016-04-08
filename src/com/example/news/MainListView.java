package com.example.news;

import android.content.Context;
import android.content.res.Resources;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;


/**动态加载就是把放入adapter中的数据分好几次加载。在用户拖动 listview 时再加载一定的数据，和微博的客户端类似。 */
public class MainListView extends ListView implements OnScrollListener{
	private static final String TAG = "listview";
	private int  headHeight;
	/**相对布局*/
	private RelativeLayout LsitViewHeadRl;
	private TextView headPromptTv;
	private TextView headTimeTv;
	private ImageView headPicIv;
	/**进度条*/
	private ProgressBar headPicPb;
	private boolean isLastRow = false;    
	private OnHeadRefreshListener onHeadRefreshListener;
	private boolean isRefresh=false;//是否建立刷新
	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;
	//释放刷新
	private final static int RELEASE_To_REFRESH = 0;
	//下拉刷新
	private final static int PULL_To_REFRESH = 1;
	//正在刷新
	private final static int REFRESHING = 2;
	//刷新完成
	private final static int DONE = 3;
    //缓冲
	private final static int LOADING = 4;
	public  int state;//记录当前所处状态
	private int startY ;//触摸位置的纵轴位置
	private int endY ;
	private int firstItem;//第一个可见的Item，记录ListView是否到顶
	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean isRecored;
	private boolean isBack;
	
	private Time t;
	public MainListView(Context context) {
		super(context);
		init(context);
	}

	public MainListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public MainListView(Context context,AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		init(context);
		
	}
	/*
	 * head的初始化
	 */
	public void init(Context context){
		LsitViewHeadRl=(RelativeLayout)LayoutInflater.from(context).inflate(R.layout.news_listview_head, null);
		
		 headPromptTv=(TextView)LsitViewHeadRl.findViewById(R.id.tv_head_prompt);
		 headTimeTv=(TextView)LsitViewHeadRl.findViewById(R.id.tv_head_time);
		 headPicIv=(ImageView)LsitViewHeadRl.findViewById(R.id.iv_head_pic);
		 headPicPb=(ProgressBar)LsitViewHeadRl.findViewById(R.id.pb_head_pic);
		// 获得Resources 实例 
		 Resources r = getResources(); 
		 // 通过getDimension方法获得尺寸值 
		 headHeight=(int)r.getDimension(R.dimen.head_rl_height);
		 System.out.println("控件 高度"+headHeight);
	    //measureView(LsitViewHeadRl);
	     LsitViewHeadRl.setPadding(0, -1*headHeight, 0, 0);
	     LsitViewHeadRl.invalidate();//刷新
		 addHeaderView(LsitViewHeadRl);
		 
		 //加载时间
		 t=new Time(); 
		 // or Time t=new Time("GMT+8"); 加上Time Zone资料。
         
		
	}
	
	public boolean onTouchEvent(MotionEvent ev) {
		if (isRefresh) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (firstItem == 0 && !isRecored) {
					isRecored = true;
					startY = (int) ev.getY();
				}
				break;

			case MotionEvent.ACTION_UP:

				if (state != REFRESHING && state != LOADING) {
					if (state == DONE) {
						// 什么都不做
					}
					if (state == PULL_To_REFRESH) {
						state = DONE;
						chargeHeadState();

					}
					if (state == RELEASE_To_REFRESH) {
						state = REFRESHING;
						chargeHeadState();
						onHeadRefrend();
						Log.v(TAG, "由松开刷新状态，到done状态");
					}
				}

				isRecored = false;
				isBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) ev.getY();

				//				System.out.println("tempY="+tempY);
				if (!isRecored && firstItem == 0) {
					Log.v(TAG, "在move时候记录下位置");
					isRecored = true;
					startY = tempY;
				}

				if (state != REFRESHING && isRecored && state != LOADING) {

					// 保证在设置padding的过程中，当前的位置一直是在head，否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动

					// 可以松手去刷新了
					if (state == RELEASE_To_REFRESH) {

						setSelection(0);

						// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
						if (((tempY - startY) / RATIO < headHeight)
								&& (tempY - startY) > 0) {
							state = PULL_To_REFRESH;
							System.out.println("状态转换 松开--〉 下拉");
							chargeHeadState();

							Log.v(TAG, "由松开刷新状态转变到下拉刷新状态");
						}
						// 一下子推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							chargeHeadState();

							Log.v(TAG, "由松开刷新状态转变到done状态");
						}
						// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
						else {
							// 不用进行特别的操作，只用更新paddingTop的值就行了
						}
					}
					// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
					if (state == PULL_To_REFRESH) {

						setSelection(0);//回到ListView顶部

						// 下拉到可以进入RELEASE_TO_REFRESH的状态
						if ((tempY - startY) / RATIO >= headHeight) {
							state = RELEASE_To_REFRESH;
							isBack = true;
							chargeHeadState();

							Log.v(TAG, "由done或者下拉刷新状态转变到松开刷新");
						}
						// 上推到顶了
						else if (tempY - startY <= 0) {
							state = DONE;
							chargeHeadState();

							Log.v(TAG, "由DOne或者下拉刷新状态转变到done状态");
						}
					}

					// done状态下
					if (state == DONE) {
						if (tempY - startY > 0) {
							state = PULL_To_REFRESH;
							chargeHeadState();
						}
					}

					// 更新headView的size
					if (state == PULL_To_REFRESH) {
						LsitViewHeadRl.setPadding(0, -1 * headHeight
								+ (tempY - startY) / RATIO, 0, 0);

					}

					// 更新headView的paddingTop
					if (state == RELEASE_To_REFRESH) {
						LsitViewHeadRl.setPadding(0, (tempY - startY) / RATIO
								- headHeight, 0, 0);
					}

				}

				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
		//滚动时一直回调，直到停止滚动时才停止回调。单击时回调一次。    
        //firstVisibleItem：当前能看见的第一个列表项ID（从0开始）    
        //visibleItemCount：当前能看见的列表项个数（小半个也算）    
        //totalItemCount：列表项共数    
    
        //判断是否滚到最后一行    
		firstItem= firstVisibleItem;
		 
	}

	

    //当滚到最后一行且停止滚动时，执行加载    
	 public void onScrollStateChanged(AbsListView view, int scrollState) {    
        //正在滚动时回调，回调2-3次，手指没抛则回调2次。scrollState = 2的这次不回调    
        //回调顺序如下    
        //第1次：scrollState = SCROLL_STATE_TOUCH_SCROLL(1) 正在滚动    
        //第2次：scrollState = SCROLL_STATE_FLING(2) 手指做了抛的动作（手指离开屏幕前，用力滑了一下）    
        //第3次：scrollState = SCROLL_STATE_IDLE(0) 停止滚动             
        //当屏幕停止滚动时为0；当屏幕滚动且用户使用的触碰或手指还在屏幕上时为1；  
        //由于用户的操作，屏幕产生惯性滑动时为2  
    
        //当滚到最后一行且停止滚动时，执行加载    
        
	}

	public void chargeHeadState(){
		switch(state){
		case RELEASE_To_REFRESH ://释放刷新
			 headPromptTv.setText("松开可以刷新");
			 t.setToNow(); // 取得系统时间。
			 headTimeTv.setText("当前刷新时间为："+ t.year+"年"+ t.month+"月"+ t.monthDay+"日"+  t.hour+"时"
					                            +  t.minute+"分");
			 headPicIv.setVisibility(View.VISIBLE);//显示
			 headPicPb.setVisibility(View.GONE);//隐藏
			 System.out.println("当前状态，松开可以刷新");
			break;

		case  PULL_To_REFRESH:	//下拉刷新
			 headPromptTv.setText("下拉可以刷新");
			 t.setToNow(); // 取得系统时间。
			 headTimeTv.setText("当前刷新时间为："+ t.year+"年"+ t.month+"月"+ t.monthDay+"日"+  t.hour+"时"
					                            +  t.minute+"分");
			 headPicIv.setVisibility(View.VISIBLE);//显示
			 headPicPb.setVisibility(View.GONE);//隐藏
			 System.out.println("当前状态，下拉可以刷新");
			break;
		case  REFRESHING://正在刷新
			 LsitViewHeadRl.setPadding(0,0, 0, 0);
			headPromptTv.setText("正在刷新...");
			t.setToNow(); // 取得系统时间。
			 headTimeTv.setText("当前刷新时间为："+ t.year+"年"+ t.month+"月"+ t.monthDay+"日"+  t.hour+"时"
					                            +  t.minute+"分");
			 headPicIv.setVisibility(View.GONE);//隐藏
			 headPicPb.setVisibility(View.VISIBLE);//显示
			 System.out.println("当前状态，正在刷新...");
			break;
		case  DONE://刷新完成
			LsitViewHeadRl.setPadding(0, -1*headHeight, 0, 0);
			headPromptTv.setText("下拉可以刷新");
			t.setToNow(); // 取得系统时间。
			 headTimeTv.setText("当前刷新时间为："+ t.year+"年"+ t.month+"月"+ t.monthDay+"日"+  t.hour+"时"
					                            +  t.minute+"分");
			 headPicIv.setVisibility(View.VISIBLE);//显示
			 headPicPb.setVisibility(View.GONE);//隐藏
			 System.out.println("当前状态，刷新已经完成");
			break;
		}
	}
	/*
	 * 下拉刷新接口，用于和外部相连，刷新时调用	
	 */
	public interface OnHeadRefreshListener{
		public void onHeadRefrend();
	}

	/*
	 * 设置下拉刷新监听接口
	 */
	public void setOnHeadRefreshListener(OnHeadRefreshListener onHeadRefreshListener){
		this.onHeadRefreshListener=onHeadRefreshListener;
		isRefresh=true;
	}

	/*
	 * 下拉刷新调用函数
	 */
	public void onHeadRefrend(){
		if(isRefresh==true){
			onHeadRefreshListener.onHeadRefrend();
		}
	}

	/**
	 * 当数据加载完成后调用已更新界面
	 */
	public void onRefreshComplete() {
		state = DONE;
		chargeHeadState();
	}
	
	



}
