package com.example.news;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import android.util.Log;

public class NewsList {
	private Element tableElement;
	private Element trElement;
	//private Element tbodyElement;
	/**接口List定义表*/
	public List<NewsEntity> newsList;
	public void newsListAnalysis(String url) throws IOException{
		/**用ArrayList构造newsList*/
		newsList=new ArrayList<NewsEntity>();
		/**传入相应的url来解析*/

		Document document=Jsoup.connect(url).timeout(5000).get();
		//Log.v("--childcode",document.childNodes().get(0).nodeName()); 

		
		Element element = document.body();
		Elements eles = element.getElementsByClass("winstyle58488");
		for(Element el : eles){		
			int i=0;
			int total=el.getElementsByClass("c58488").size();
			while(i<total){
			Element title = el.getElementsByClass("c58488").get(i);
			Element date = el.getElementsByClass("timestyle58488").get(i);
			
			NewsEntity news=new NewsEntity(); 
			news.setTitle(title.text());
			/**abs:将相对路径转换为绝对路径*/
			news.setUrl(title.attr("abs:href"));
			news.setDate(date.text());
			newsList.add(news);
			i=i+1;
			}
			
		
		}		 
	}

	public List<NewsEntity> getNewsList() {
		return newsList;
	}

}
