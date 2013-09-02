/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   阳历和农历的互相转换，以及计算下一个农历和阳历生日
 */
package com.birthday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
/** 
 * 设置生日提醒闹钟的类. 
 * 
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class SetBirthdayAlarm {
	private static final String BC_ACTION = "com.sharpAndroid.test.BC_ACTION_3";
	private final static int TIME = 60000;//1分钟
	private final BirthdayPreferences preferences;
	private final Context context;
	private final BirthdaySQLite sqliteHelper;
	private int adjust = 0;

	private String alarmDate;

	/** 
	 * SetBirthdayAlarm 构造器
	 * @param birthday 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public SetBirthdayAlarm(Context arg0) {
		context =arg0;
		preferences = new BirthdayPreferences(arg0);
		sqliteHelper = new BirthdaySQLite(context);
	}
	/** 
	 * 查找最近需要提醒的生日并设置闹钟提醒。
	 * @param birthday 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void setBirthdayAlarm(){	
		int alarmStatus = preferences.getPreferences("alarmStatus", 1);
		if( 0==alarmStatus ){
			//闹钟关闭
			cancelAlarm();
		}
		else{
			//闹钟开启
			String message = findAlarmBirthday();          //得到提醒信息
			long millis = getAlarmTimeInMillis(alarmDate); //计算提醒的时间
			long currentmillis = new Date().getTime();     //当前的时间

			if(null!=message && !message.isEmpty() && millis>currentmillis){
				if((millis-currentmillis)>TIME){           //生日提醒的时间在从现在起的1分钟之后。
					setAlarm(millis,message);
//					setAlarm(Calendar.getInstance().getTimeInMillis()+10000,message);
				}
				else{
					adjust = 1;							   //设置adjust为1(相当于提前的天数加1)，重新设置闹钟。
					setBirthdayAlarm();
				}
			}
		}
		sqliteHelper.close();
	}
	/** 
	 * 将当前的日期增加days天。 以 yyyy年MM月dd日 的形式返回
	 * @param days 天数 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String getDateString(int days){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, days+adjust);
		//当前日期
		String stringDate = BirthdaySQLite.dateStringBuilder(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH)+1,
				calendar.get(Calendar.DAY_OF_MONTH),
				1);
		return stringDate;
	}
	/** 
	 * 将给定的日期增加days天。 以 yyyy年MM月dd日 的形式返回
	 * @param
	 * 		dateString 以 yyyy年MM月dd日 的形式 给定的日期
	 * 		days 天数 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String getDateString(String dateString,int days){
		String stringDate = null;
		try {
			Date date = BirthdaySQLite.dateParse(dateString);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, days);
			stringDate = BirthdaySQLite.dateStringBuilder(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH)+1,
					calendar.get(Calendar.DAY_OF_MONTH),
					1);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringDate;
	}
	/** 
	 * 获得最近要提醒的生日
	 * @param 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String findAlarmBirthday(){
		sqliteHelper.update();//更新联系人生日。
		
		String showDay = preferences.getPreferences("day", "1");//生日提醒提前的天数，默认1天
		int daysArray[] = BirthdayPreferences.getTypeStringArray(showDay);
		int days;

		String startDate = null;
		String nearBirthDate = null;
		alarmDate = null;
		
		for(int i =0;i<daysArray.length;i++){
			days = daysArray[i];
			if(days<0){
				continue;
			}
			startDate = getDateString(days);
			nearBirthDate = sqliteHelper.queryNearestBirthday(startDate);
			if(null!=nearBirthDate){
				String str = getDateString(nearBirthDate,-days);
				if(null == alarmDate || alarmDate.compareTo(str)>0){
					alarmDate = str;
				}
			}
			else{
				break;
			}
		}
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if(null!=alarmDate && !alarmDate.isEmpty()){
			for(int i =0;i<daysArray.length;i++){
				days = daysArray[i];
				if(days<0){
					continue;
				}
				String birthDate = getDateString(alarmDate,days);
				Cursor cursor = sqliteHelper.queryLundarBirthdayByDate(birthDate);
				if(null!=cursor){
					while(cursor.moveToNext()){
						stringBuilder.append(cursor.getString(cursor.getColumnIndex("name")));
						stringBuilder.append("\n");
						stringBuilder.append("农历生日");
						stringBuilder.append("\n");
						stringBuilder.append(birthDate);
						stringBuilder.append("\n");
					}
				}
				cursor.close();

				cursor = sqliteHelper.querySolarBirthdayByDate(birthDate);
				if(null!=cursor){
					while(cursor.moveToNext()){
						stringBuilder.append(cursor.getString(cursor.getColumnIndex("name")));
						stringBuilder.append("\n");
						stringBuilder.append("阳历生日");
						stringBuilder.append("\n");
						stringBuilder.append(birthDate);
						stringBuilder.append("\n");
					}
				}
				cursor.close();
			}
		}
		return stringBuilder.toString();
	}

	/** 
	 * 根据生日日期以及提醒的时间参数返回具体生日提醒闹钟时间的毫秒数.
	 * @param birthday 生日日期 
	 * @return millis the number of milliseconds since Jan. 1, 1970, midnight GMT. 
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private long getAlarmTimeInMillis(String birthday){
		long millis = -1;
		if(null!=birthday && !birthday.isEmpty()){
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
			try {
				Date date = sDateFormat.parse(birthday);
				//得到保存在SharedPreferences中的提醒设置参数
				date.setDate(date.getDate());
				date.setHours(preferences.getPreferences("hourOfDay", 9));
				date.setMinutes(preferences.getPreferences("minute", 0));
				date.setSeconds(0);
				millis = date.getTime();
			}
			catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return millis;
	}
	/** 
	 * 设置一个生日提醒闹钟。
	 * @param millis the number of milliseconds since Jan. 1, 1970, midnight GMT. 响闹钟的时间。
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private void setAlarm(long millis,String message){
		Intent intent = new Intent();
		intent.setAction(BC_ACTION); 
//		intent.putExtras(mbundle);
		intent.putExtra("message",message);
		final PendingIntent pendingintent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, millis, pendingintent);
	}
	/** 
	 * 取消生日提醒的闹钟。
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private void cancelAlarm(){
		Intent intent = new Intent();
		intent.setAction(BC_ACTION); 
		final PendingIntent pendingintent = PendingIntent.getBroadcast(context,0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarm = (AlarmManager)context.getSystemService(Activity.ALARM_SERVICE);
		alarm.cancel(pendingintent);
	}
}
