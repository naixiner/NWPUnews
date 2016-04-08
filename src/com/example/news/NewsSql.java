package com.example.news;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class NewsSql extends SQLiteOpenHelper{
	private static final int DATABASE_VERSION=1;
	private static final String  DATABASE_NAME="homepageSQLite.db";

	public NewsSql(Context context) {
		super(context, DATABASE_NAME,null,DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase db) {
		for(String typePlated:MainActivity.ARRAY_PLATED){
		
			db.execSQL("create table newsListView_"+typePlated+"( id int," +
                    "title varchar(200), date varchar(50), url varchar(50))");// INTEGER PRIMARY KEY AUTOINCREMENT
		}
	}

	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("db updated");
	}
	/*
	 * 向数据库中插入数据
	 */
	public  void setNewsData( List<NewsEntity> newsList,String typePlate){
		// 得到一个可写的SQLiteDatabase对象  
		int i=0;
        SQLiteDatabase sqliteDatabase =this.getWritableDatabase();  
        
   sqliteDatabase.delete("newsListView_"+typePlate, null, null);//清空表中数据
        
		//sqliteDatabase.execSQL("update sqlite_sequence set id=1 where name="+"newsListView_"+typePlate);
		for(NewsEntity news:newsList){
		 // 创建ContentValues对象  
			i++;
        ContentValues values = new ContentValues();  
        // 向该对象中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致  
        values.put("id", i);  
        values.put("title", news.getTitle());  
        values.put("date", news.getDate());  
        values.put("url", news.getUrl());
        // 调用insert方法，就可以将数据插入到数据库当中  
        // 第一个参数:表名称  
        // 第二个参数：SQl不允许一个空列，如果ContentValues是空的，那么这一列被明确的指明为NULL值  
        // 第三个参数：ContentValues对象  
        sqliteDatabase.insert("newsListView_"+typePlate, null, values);
		}
		sqliteDatabase.close();
	}
	
	/*
	 * 向数据库中查询数据
	 */
	public List<NewsEntity> getNewsData(String typePlate){
		List<NewsEntity> newsList= new ArrayList<NewsEntity>();
		//mNewsList.clear();
        //创建DatabaseHelper对象  
        // 得到一个只读的SQLiteDatabase对象  
        SQLiteDatabase sqliteDatabase =this.getReadableDatabase();  
        // 调用SQLiteDatabase对象的query方法进行查询，返回一个Cursor对象：由数据库查询返回的结果集对象  
        
        // 第一个参数String：表名  
        // 第二个参数String[]:要查询的列名  
        // 第三个参数String：查询条件  
        // 第四个参数String[]：查询条件的参数  
        // 第五个参数String:对查询的结果进行分组  
        // 第六个参数String：对分组的结果进行限制  
        // 第七个参数String：对查询的结果进行排序  
        
        Cursor cursor = sqliteDatabase.query("newsListView_"+typePlate, new String[] { "id",  
                "title" ,"date", "url"}, null,null, null, null, null);  
        // 将光标移动到下一行，从而判断该结果集是否还有下一条数据，如果有则返回true，没有则返回false  
        while (cursor.moveToNext()) {  
        	NewsEntity news=new NewsEntity();
        	news.setTitle( cursor.getString(cursor.getColumnIndex("title"))) ; 
        	Log.v("-OnSql-","Runing");
        	news.setDate( cursor.getString(cursor.getColumnIndex("date"))) ;  
        	news.setUrl( cursor.getString(cursor.getColumnIndex("url"))) ;  
        	newsList.add(news);
        }   
        sqliteDatabase.close();
		return newsList;
		
	}
}

	

