/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��19��     ֣�Ļ�	             ���ݿ�İ�����
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
 * @author ֣�Ļ�
 * @Date   2011��10��19��
 */ 
public class BirthdaySQLite extends SQLiteOpenHelper{
	private SQLiteDatabase database = null;
	private static final String DATABASE_NAME = "birthday.db";//���ݿ���
	private static final String TABLE_NAME = "BirthdayTbl";//���ݿ��м�¼��ϵ�˱���
	public BirthdaySQLite(Context context) {
		super(context,DATABASE_NAME,null,2); 
		// TODO Auto-generated constructor stub
	}
	@Override
	public void onCreate(SQLiteDatabase db){
		StringBuilder sBuilder = new StringBuilder();
		sBuilder.append("create table ");
		sBuilder.append(TABLE_NAME);
		sBuilder.append("(_id integer primary key,");//���� autoincrement
		sBuilder.append("name text,");                    //��ϵ������
		sBuilder.append("sort_key text,");                //��ϵ��sort_key
		sBuilder.append("bornSolar text,");				  //�������������գ�yyyy��MM��dd��
		sBuilder.append("bornLundarDate text,");    	  //ũ�����������գ��м���"/"���
		sBuilder.append("bornLundar text,");			  //ũ�����������ա���ɵ�֧���꣬��д������
		sBuilder.append("warningLundar integer,");		  //�Ƿ�����ũ������ ��������Ϊ0������Ϊ1
		sBuilder.append("birthdayLundar text,");		  //ũ�����յ�ʱ�䣬yyyy��MM��dd��
		sBuilder.append("warningSolar integer,");	 	  //�Ƿ������������� ��������Ϊ0������Ϊ1
		sBuilder.append("birthdaySolar text)");			  //�������յ�ʱ�䣬yyyy��MM��dd��
		//�������SQL���
		String CREATE_TABLE_SQL = sBuilder.toString();
		try{
			//ִ�д�����
			db.execSQL(CREATE_TABLE_SQL);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	@Override
	public void close(){
		//�ر����ݿ�
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
	 * �����ݿ���в���һ����ϵ�˼�¼���������ϵ���Ѿ����ڣ��򲻲���
	 * ��ÿ����ϵ�˸���_idΨһ����
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
	 * �����ݿ�� _id ��¼�� ColumnName ����Ϊ value��
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
	//��������ָ����ʽ������
	/** 
	 * ��ʽ��������������
	 * @param      
	 * @return   
	 * @exception  
	 * @see        
	 * @since      ����year=2011��month=1��day=19��
	 * 				tag=1ʱ  ���� 2011��01��19��
	 * 				tag=2ʱ  ���� 2011/1/19/
	 * 				��������null;
	 */
	public static String dateStringBuilder(int year,int month,int day,int tag){
		String result = null;
		switch (tag) {
		case 1:
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
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
	 * �����ַ���"yyyy��MM��dd��"
	 * @param      
	 * @return    ���ؽ���������Date
	 * @exception  
	 * @see        
	 * @since      
	 */
	public static Date dateParse(String datestring) throws ParseException{
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
		return sDateFormat.parse(datestring);
	}
	/** 
	 * ���±��й��ڵ�����
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
	 * ����_id����ϵ�˵�����
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
				//�����¸�ũ������
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
	 * �޸� _id��ϵ�˵ĳ������ڣ�
	 * @param      ����year��month��day��ũ���������ڣ�
	 * @return     the number of rows affected�� 
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
				1));//������������
		
		calendarTemp = solarlundar.getNextSolarBirthday(year,month, day);
		string = dateStringBuilder(
				calendarTemp.get(Calendar.YEAR), 
				calendarTemp.get(Calendar.MONTH)+1, 
				calendarTemp.get(Calendar.DAY_OF_MONTH),
				1);
		values.put("birthdaySolar", string);//��������
		
		if(null==database){
			database = getWritableDatabase();
		}
		result = database.update(TABLE_NAME, values, "_id=?", new String[]{_id});
		return result;
	}
	/** 
	 * �޸� _id��ϵ�˵ĳ������ڣ�
	 * @param      ����year��month��day�������������ڣ�
	 * @return     the number of rows affected�� 
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
		values.put("bornSolar", dateStringBuilder(year, month, day, 1));//������������
		
		calendarTemp = solarlundar.getNextSolarBirthday(year,month, day);
		string = dateStringBuilder(
				calendarTemp.get(Calendar.YEAR), 
				calendarTemp.get(Calendar.MONTH)+1, 
				calendarTemp.get(Calendar.DAY_OF_MONTH),
				1);
		values.put("birthdaySolar", string);//��������
		
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
	 * �������ݿ�������м�¼��
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
	 * �������ݿ�����������С�ڲ����ļ�¼
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
	 * ���ҷ������������Ϣ�ļ�¼
	 * @param      type=1  ��������Ϊ�������������ղ�Ϊ�յļ�¼��
	 *     		   type=2  �����������ղ�Ϊ�յļ�¼����������Ϊ���ѵģ�������Ϊ�����ѵģ�
	 *		       type=3  ��������
	 *			       ��������null��
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
			//type=1  ��������Ϊ���ѵ��������ղ�Ϊ�յļ�¼��
			mCursor= database.query(TABLE_NAME,
					null,
					"warningSolar=? AND birthdaySolar!=?", 
					new String[]{"1",""},
					null,
					null, 
			"birthdaySolar");
			break;
		case 2:
			//type=2  �����������ղ�Ϊ�յļ�¼����������Ϊ���ѵģ�������Ϊ�����ѵģ�
			mCursor= database.query(TABLE_NAME,
					null,
					"birthdaySolar!=?", 
					new String[]{""},
					null,
					null, 
			"birthdaySolar");
			break;
		case 3:
			//type=3  ��������
			mCursor= database.query(TABLE_NAME,
					null,
					null, 
					null,
					null,
					null, 
			"sort_key");
			break;
		default:
			//��������null��
			break;
		}
		return mCursor;
	}
	/** 
	 * @param      type=1  ��������Ϊ������ũ�����ղ�Ϊ�յļ�¼��
	 *     		   type=2  ����ũ�����ղ�Ϊ�յļ�¼����������Ϊ���ѵģ�������Ϊ�����ѵģ�
	 *		       type=3  ��������
	 *			       ��������null��
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
			//type=1  ��������Ϊ���ѵ�ũ�����ղ�Ϊ�յļ�¼��
			mCursor = database.query(TABLE_NAME,
					null,
					"warningLundar=? AND birthdayLundar!=?", 
					new String[]{"1",""},
					null,
					null, 
			"birthdayLundar");
			break;
		case 2:
			//type=2  ����ũ�����ղ�Ϊ�յļ�¼����������Ϊ���ѵģ�������Ϊ�����ѵģ�
			mCursor = database.query(TABLE_NAME,
					null,
					"birthdayLundar!=?", 
					new String[]{""},
					null,
					null, 
			"birthdayLundar");
			break;
		case 3:
			//type=3  ��������
			mCursor = database.query(TABLE_NAME,
					null,
					null, 
					null,
					null,
					null, 
			"sort_key");
			break;
		default:
			//��������null��
			break;
		}
		return mCursor;
	}
	/** 
	 * ������������_id���Ҽ�¼
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
	 * ��selection���Ҽ�¼
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
	 * ��Idɾ����¼
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
	 * �õ����ڲ���date���������ڵ��������������
	 * @param      
	 * @return ���û�У�����null  
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
	 * ����������������Ϊ���� �����������������ڵ��ڲ���date���������� �ļ�¼
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
	 * ����ũ����������Ϊ���� ������ũ���������ڵ��ڲ���date���������� �ļ�¼
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

