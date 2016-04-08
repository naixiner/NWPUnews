package com.example.news;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.widget.SimpleAdapter;


public class NewsAdapter {
	/**ListView简单适配器*/
	/**context　　SimpleAdapter关联的View的运行环境*/
	public static SimpleAdapter ListView(Context context,List<NewsEntity> nowList){
	ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
	Iterator<NewsEntity> mIteratorNews=nowList.iterator();	
	while(mIteratorNews.hasNext()){
		   HashMap<String, Object> map = new HashMap<String, Object>();
		   NewsEntity news=mIteratorNews.next();
		   map.put("ItemText", news.getTitle());
		   map.put("ItemDate", news.getDate());
		   listItem.add(map);
	   }
		 //生成适配器的Item和动态数组对应的元素
    SimpleAdapter listItemAdapter =  new SimpleAdapter(context,listItem, R.layout.newslistview, 
 		   new String[] { "ItemText","ItemDate"},
 		   new int[] {R.id.tv_items_text,R.id.tv_items_date});
    
    return listItemAdapter ;
	}
}
