
package com.example.news;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class NewsItemView {

	private Document doc;
	private Elements allItemElement;
	
	public String getItemData() {
		return allItemElement.text();
	}
	public void newsItemWebView(String url){
		try {
			/**超时时间是五秒*/
			doc=Jsoup.connect(url).timeout(5000).get();
			Element element = doc.body();
			allItemElement=element.getElementsByClass("winstyle58490");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			
		}
	}

}
