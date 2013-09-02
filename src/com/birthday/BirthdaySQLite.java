/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月19日     郑文辉	             数据库的帮助类
 */
package com.birthday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
/** 
 * Class description goes here. 
 * 
 * @param
 * @author 郑文辉
 * @Date   2011年10月19日
 */ 
public class BirthdaySQLite extends SQLiteOpenHelper{
	private SQLiteDatabase database = null;
	private static final String DATABASE_NAME = "birthday.db";//数据库名
	private static final String TABLE_NAME = "BirthdayTbl";//数据库中记录联系人表名
	public BirthdaySQLite(Context context) {
		super(context,DATABASE_NAME,null,2); 
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("create table ");
		sBuilder.append(TABLE_NAME);
		sBuilder.append("(_id integer primary key,");//主键 autoincrement
		sBuilder.append("name text,");                    //联系人姓名
		sBuilder.append("sort_key text,");                //联系人sort_key
		sBuilder.append("bornSolar text,");				  //阳历出生年月日，yyyy年MM月dd日
		sBuilder.append("bornLundarDate text,");    	  //农历出生年月日，中间以"/"间隔
		sBuilder.append("bornLundar text,");			  //农历出生年月日。天干地支记年，大写记月日
		sBuilder.append("warningLundar integer,");		  //是否提醒农历生日 。不提醒为0，提醒为1
		sBuilder.append("birthdayLundar text,");		  //农历生日的时间，yyyy年MM月dd日
		sBuilder.append("warningSolar integer,");	 	  //是否提醒阳历生日 。不提醒为0，提醒为1
		sBuilder.append("birthdaySolar text)");			  //阳历生日的时间，yyyy年MM月dd日
		//创建表的SQL语句
		String CREATE_TABLE_SQL = sBuilder.toString();
		try{
			//执行创建表
			db.execSQL(CREATE_TABLE_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	@Override
	public void close(){
		//关闭数据库
		super.close();
		if(null != database){
			database.close();
			database = null;
		}
	}
	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
	}
	/** 
	 * 给数据库表中插入一条联系人记录，若这个联系人已经存在，则不插入
	 * ，每个联系人根据_id唯一区分
	 * @param      
	 * @return     the row ID of the newly inserted row, or -1 if an error occurred
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public long insert(String _id,String name,String sort_key){
		long result = -1;
		Cursor cursor = database.query(TABLE_NAME, new String[]{"_id"}, "_id=?", new String[]{_id}, null, null, null);
		if(cursor.getCount()<=0){
			ContentValues values = new ContentValues();
			values.put("_id", _id);
			values.put("name", name);
			values.put("sort_key", sort_key);
			values.put("bornSolar", "");
			values.put("bornLundar", "");
			values.put("bornLundarDate", "");
			values.put("warningLundar", 0);
			values.put("birthdayLundar", "");
			values.put("warningSolar", 0);
			values.put("birthdaySolar", "");
			result = database.insert(TABLE_NAME, null, values);	
		}
		return result;
	}

	/** 
	 * 将数据库表 _id 记录的 ColumnName 更新为 value；
	 * @param      
	 * @return     the number of rows affected 
	 * @exception  
	 * @see        
	 * @since      
	 */
	public int update(String _id,String ColumnName,int value){
		if(null==database){
			database = getWritableDatabase();
		}
		ContentValues values = new ContentValues();
		values.put(ColumnName, value);
		return database.update(TABLE_NAME, values, "_id=?", new String[]{_id});
	}
	//创建生成指定格式的日期
	/** 
	 * 格式化给定的年月日
	 * @param      
	 * @return   
	 * @exception  
	 * @see        
	 * @since      例如year=2011，month=1，day=19，
	 * 				tag=1时  返回 2011年01月19日
	 * 				tag=2时  返回 2011/1/19/
	 * 				其他返回null;
	 */
	public static String dateStringBuilder(int year,int month,int day,int tag){
		String result = null;
		switch (tag) {
		case 1:
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
			Date date = new Date();
			date.setYear(year-1900);
			date.setMonth(month-1);
			date.setDate(day);
			result = sDateFormat.format(date);
			break;
		case 2:
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(year);
			stringBuilder.append("/");
			stringBuilder.append(month);
			stringBuilder.append("/");
			stringBuilder.append(day);
			stringBuilder.append("/");
			result = stringBuilder.toString();
			break;
		default:
			break;
		}
		return result;
	}
	/** 
	 * 解析字符串"yyyy年MM月dd日"
	 * @param      
	 * @return    返回解析出来的Date
	 * @exception  
	 * @see        
	 * @since      
	 */
	public static Date dateParse(String datestring) throws ParseException{
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
		return sDateFormat.parse(datestring);
	}
	/** 
	 * 更新表中过期的生日
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */
	public int update(){
		int count = 0;
		Calendar mCalendar = Calendar.getInstance();
		String string = dateStringBuilder(
				mCalendar.get(Calendar.YEAR), 
				mCalendar.get(Calendar.MONTH)+1, 
				mCalendar.get(Calendar.DAY_OF_MONTH),
				1);
		Cursor mCursor = query(string);
		String m_id ;
		while(mCursor.moveToNext()){
			m_id = mCursor.getString(mCursor.getColumnIndex("_id"));
			update(m_id);
		}
		count = mCursor.getCount();
		mCursor.close();
		return count;
	}
	/** 
	 * 更新_id的联系人的生日
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */
	public boolean update(String _id){
		boolean result = true;
		Cursor cursor = queryById( _id);
		if(cursor.moveToFirst()){
			String string ;
			Calendar calendarTemp;
			SolarAndLundar solarlundar = new SolarAndLundar();
			ContentValues values = new ContentValues();

			string = cursor.getString(cursor.getColumnIndex("bornSolar"));
			try {
				Date date = dateParse(string);
				calendarTemp  = solarlundar.getNextSolarBirthday(
						date.getYear(),
						date.getMonth(),
						date.getDate());
				string = dateStringBuilder(
						calendarTemp.get(Calendar.YEAR), 
						calendarTemp.get(Calendar.MONTH)+1, 
						calendarTemp.get(Calendar.DAY_OF_MONTH),
						1);
				values.put("birthdaySolar", string);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				result = false;
				e.printStackTrace();
			}

			string = cursor.getString(cursor.getColumnIndex("bornLundarDate"));
			String temp2[] = string.split("/");
			if(!(temp2.length<3)){
				//计算下个农历生日
				calendarTemp = solarlundar.getNextLundarBirthday(
						Integer.valueOf(temp2[0]),
						Integer.valueOf(temp2[1]),
						Integer.valueOf(temp2[2]));
				if(null==calendarTemp){
					values.put("birthdayLundar","");
					result = false;
				}
				else{
					string = dateStringBuilder(
							calendarTemp.get(Calendar.YEAR), 
							calendarTemp.get(Calendar.MONTH)+1, 
							calendarTemp.get(Calendar.DAY_OF_MONTH),
							1);
					values.put("birthdayLundar", string);
				}
			}
			if(values.size()>0){
				database.update(TABLE_NAME, values, "_id=?", new String[]{_id});
			}
		}
		return result;
	}
	/** 
	 * 修改 _id联系人的出生日期；
	 * @param      参数year，month，day是农历出生日期；
	 * @return     the number of rows affected； 
	 * @exception  
	 * @see        
	 * @since      
	 */
	public int updateLundarBirth(String _id,int year,int month,int day ){
		int result = -1;
		String string = null;
		ContentValues values = new ContentValues();
		SolarAndLundar solarlundar = new SolarAndLundar();
		Calendar calendarTemp = Calendar.getInstance();
		
		string = solarlundar.lunarYear(year, month, day);
		values.put("bornLundar", string);
		string = dateStringBuilder(year, month, day, 2);
		values.put("bornLundarDate", string);
		calendarTemp = solarlundar.getNextLundarBirthday(year, month, day);
		if(null!=calendarTemp){
			string = dateStringBuilder(
					calendarTemp.get(Calendar.YEAR), 
					calendarTemp.get(Calendar.MONTH)+1, 
					calendarTemp.get(Calendar.DAY_OF_MONTH),
					1);
			values.put("birthdayLundar", string);
		}
		else{
			values.put("birthdayLundar", "");
		}
		
		calendarTemp = solarlundar.sCalendarLundarToSolar(year, month, day);
		
		values.put("bornSolar", dateStringBuilder(
				calendarTemp.get(Calendar.YEAR), 
				calendarTemp.get(Calendar.MONTH)+1, 
				calendarTemp.get(Calendar.DAY_OF_MONTH),
				1));//阳历出生日期
		
		calendarTemp = solarlundar.getNextSolarBirthday(year,month, day);
		string = dateStringBuilder(
				calendarTemp.get(Calendar.YEAR), 
				calendarTemp.get(Calendar.MONTH)+1, 
				calendarTemp.get(Calendar.DAY_OF_MONTH),
				1);
		values.put("birthdaySolar", string);//阳历生日
		
		if(null==database){
			database = getWritableDatabase();
		}
		result = database.update(TABLE_NAME, values, "_id=?", new String[]{_id});
		return result;
	}
	/** 
	 * 修改 _id联系人的出生日期；
	 * @param      参数year，month，day是阳历出生日期；
	 * @return     the number of rows affected； 
	 * @exception  
	 * @see        
	 * @since      
	 */
	public int updateSolarBirth(String _id,int year,int month,int day ){
		int result = -1;
		String string = null;
		ContentValues values = new ContentValues();
		SolarAndLundar solarlundar = new SolarAndLundar();
		Calendar calendarTemp = Calendar.getInstance();
		
		calendarTemp.set(year, month-1, day);
		values.put("bornSolar", dateStringBuilder(year, month, day, 1));//阳历出生日期
		
		calendarTemp = solarlundar.getNextSolarBirthday(year,month, day);
		string = dateStringBuilder(
				calendarTemp.get(Calendar.YEAR), 
				calendarTemp.get(Calendar.MONTH)+1, 
				calendarTemp.get(Calendar.DAY_OF_MONTH),
				1);
		values.put("birthdaySolar", string);//阳历生日
		
		calendarTemp.set(year, month-1, day);
		
		Bundle bundle = solarlundar.sCalendarSolarToLundar(calendarTemp);
		if(null!=bundle){
			string = solarlundar.lunarYear(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));
			values.put("bornLundar", string);
			string = dateStringBuilder(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"), 2);
			values.put("bornLundarDate", string);
			calendarTemp = solarlundar.getNextLundarBirthday(bundle.getInt("year"), bundle.getInt("month"), bundle.getInt("day"));
			if(null!=calendarTemp){
				string = dateStringBuilder(
						calendarTemp.get(Calendar.YEAR), 
						calendarTemp.get(Calendar.MONTH)+1, 
						calendarTemp.get(Calendar.DAY_OF_MONTH),
						1);
				values.put("birthdayLundar", string);
			}
			else{
				values.put("birthdayLundar", "");
			}
		}
		else{
			values.put("bornLundar","");
			values.put("bornLundarDate","");
			values.put("birthdayLundar", "");
		}
		if(null==database){
			database = getWritableDatabase();
		}
		result = database.update(TABLE_NAME, values, "_id=?", new String[]{_id});
		return result;
	}
	/** 
	 * 返回数据库表中所有记录；
	 * @param      
	 * @return     
	 * @exception  
	 * @see        
	 * @since      
	 */
	public Cursor query() {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.query(TABLE_NAME,
				null,//new String[]{"_id","name","bornSolar","bornLundar","birthdaySolar","birthdayLundar","warningSolar","warningLundar"}, 
				null, null, null, null, null);
	}
	/** 
	 * 返回数据库中生日日期小于参数的记录
	 * @param      
	 * @return     
	 * @exception  
	 * @see        
	 * @since      
	 */
	public Cursor query(String date) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.query(TABLE_NAME,
				new String[]{"_id","name","birthdaySolar"},
				"(birthdaySolar<? AND birthdaySolar!=?) OR (birthdayLundar<? AND birthdayLundar!=?)", 
				new String[]{date,"",date,""},
				null,
				null, 
				null);
	}
	/** 
	 * 查找返回阳历相关信息的记录
	 * @param      type=1  查找设置为提醒且阳历生日不为空的记录；
	 *     		   type=2  查找阳历生日不为空的记录，包括设置为提醒的，和设置为不提醒的；
	 *		       type=3  查找所有
	 *			       其他返回null。
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Cursor queryBirthdaySolar(int type) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		Cursor mCursor = null;
		switch(type){
		case 1:
			//type=1  查找设置为提醒的阳历生日不为空的记录；
			mCursor= database.query(TABLE_NAME,
					null,
					"warningSolar=? AND birthdaySolar!=?", 
					new String[]{"1",""},
					null,
					null, 
			"birthdaySolar");
			break;
		case 2:
			//type=2  查找阳历生日不为空的记录，包括设置为提醒的，和设置为不提醒的；
			mCursor= database.query(TABLE_NAME,
					null,
					"birthdaySolar!=?", 
					new String[]{""},
					null,
					null, 
			"birthdaySolar");
			break;
		case 3:
			//type=3  查找所有
			mCursor= database.query(TABLE_NAME,
					null,
					null, 
					null,
					null,
					null, 
			"sort_key");
			break;
		default:
			//其他返回null。
			break;
		}
		return mCursor;
	}
	/** 
	 * @param      type=1  查找设置为提醒且农历生日不为空的记录；
	 *     		   type=2  查找农历生日不为空的记录，包括设置为提醒的，和设置为不提醒的；
	 *		       type=3  查找所有
	 *			       其他返回null。
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Cursor queryBirthdayLundar(int type) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		Cursor mCursor = null;
		switch(type){
		case 1:
			//type=1  查找设置为提醒的农历生日不为空的记录；
			mCursor = database.query(TABLE_NAME,
					null,
					"warningLundar=? AND birthdayLundar!=?", 
					new String[]{"1",""},
					null,
					null, 
			"birthdayLundar");
			break;
		case 2:
			//type=2  查找农历生日不为空的记录，包括设置为提醒的，和设置为不提醒的；
			mCursor = database.query(TABLE_NAME,
					null,
					"birthdayLundar!=?", 
					new String[]{""},
					null,
					null, 
			"birthdayLundar");
			break;
		case 3:
			//type=3  查找所有
			mCursor = database.query(TABLE_NAME,
					null,
					null, 
					null,
					null,
					null, 
			"sort_key");
			break;
		default:
			//其他返回null。
			break;
		}
		return mCursor;
	}
	/** 
	 * 按参数给定的_id查找记录
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Cursor queryById(String _id) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.query(TABLE_NAME,null,"_id=?", new String[]{_id},null,null,null);
	}
	/** 
	 * 按selection查找记录
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public Cursor querySearch(String selection) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		Cursor cursor = database.query(TABLE_NAME,null,selection, null,null,null,null);
		return cursor;
	}
	/** 
	 * 按Id删除记录
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public int delete(String ID) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.delete(TABLE_NAME, "_id=?", new String[]{ID});
	}
	/** 
	 * 得到大于参数date给定的日期的最近的生日日期
	 * @param      
	 * @return 如果没有，返回null  
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public String queryNearestBirthday(String date) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		
		Cursor lundarCursor = database.query(TABLE_NAME,
				new String[]{"min(birthdayLundar)","_id"},
				"warningLundar=? AND birthdayLundar!=? AND birthdayLundar>=?", 
				new String[]{"1","",date},
				null,
				null,
				null);	
		
		Cursor solarCursor = database.query(TABLE_NAME,
				new String[]{"min(birthdaySolar)","_id"},
				"warningSolar=? AND birthdaySolar!=? AND birthdaySolar>=?", 
				new String[]{"1","",date},
				null,
				null,
				null);
		
		String result = null;
		
		if(null!=lundarCursor && lundarCursor.moveToFirst()){
			result = lundarCursor.getString(0);
		}
		
		if(null!=solarCursor && solarCursor.moveToFirst()){
			String temp = solarCursor.getString(0);
			if(null == result || result.compareTo(temp)>0){
				result = temp;
			}
		}
		
		lundarCursor.close();
		solarCursor.close();
		
		return result;
	}
	/** 
	 * 查找阳历生日设置为提醒 ，并且阳历生日日期等于参数date给定的日期 的记录
	 * @param      
	 * @return  
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public Cursor querySolarBirthdayByDate(String date) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.query(TABLE_NAME,
				new String[]{"_id","name"},
				"warningSolar=? AND birthdaySolar=?", 
				new String[]{"1",date},
				null,
				null,
				null);
		
	}
	/** 
	 * 查找农历生日设置为提醒 ，并且农历生日日期等于参数date给定的日期 的记录
	 * @param      
	 * @return
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	public Cursor queryLundarBirthdayByDate(String date) {
		// TODO Auto-generated method stub
		if(null==database){
			database = getWritableDatabase();
		}
		return database.query(TABLE_NAME,
				new String[]{"_id","name"},
				"warningLundar=? AND birthdayLundar=?", 
				new String[]{"1",date},
				null,
				null,
				null);	
	}
}

