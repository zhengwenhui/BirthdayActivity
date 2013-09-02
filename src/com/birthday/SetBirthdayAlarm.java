/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   ������ũ���Ļ���ת�����Լ�������һ��ũ������������
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
 * ���������������ӵ���. 
 * 
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */ 
public class SetBirthdayAlarm {
	private static final String BC_ACTION = "com.sharpAndroid.test.BC_ACTION_3";
	private final static int TIME = 60000;//1����
	private final BirthdayPreferences preferences;
	private final Context context;
	private final BirthdaySQLite sqliteHelper;
	private int adjust = 0;

	private String alarmDate;

	/** 
	 * SetBirthdayAlarm ������
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
	 * ���������Ҫ���ѵ����ղ������������ѡ�
	 * @param birthday 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void setBirthdayAlarm(){	
		int alarmStatus = preferences.getPreferences("alarmStatus", 1);
		if( 0==alarmStatus ){
			//���ӹر�
			cancelAlarm();
		}
		else{
			//���ӿ���
			String message = findAlarmBirthday();          //�õ�������Ϣ
			long millis = getAlarmTimeInMillis(alarmDate); //�������ѵ�ʱ��
			long currentmillis = new Date().getTime();     //��ǰ��ʱ��

			if(null!=message && !message.isEmpty() && millis>currentmillis){
				if((millis-currentmillis)>TIME){           //�������ѵ�ʱ���ڴ��������1����֮��
					setAlarm(millis,message);
//					setAlarm(Calendar.getInstance().getTimeInMillis()+10000,message);
				}
				else{
					adjust = 1;							   //����adjustΪ1(�൱����ǰ��������1)�������������ӡ�
					setBirthdayAlarm();
				}
			}
		}
		sqliteHelper.close();
	}
	/** 
	 * ����ǰ����������days�졣 �� yyyy��MM��dd�� ����ʽ����
	 * @param days ���� 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String getDateString(int days){
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, days+adjust);
		//��ǰ����
		String stringDate = BirthdaySQLite.dateStringBuilder(calendar.get(Calendar.YEAR),
				calendar.get(Calendar.MONTH)+1,
				calendar.get(Calendar.DAY_OF_MONTH),
				1);
		return stringDate;
	}
	/** 
	 * ����������������days�졣 �� yyyy��MM��dd�� ����ʽ����
	 * @param
	 * 		dateString �� yyyy��MM��dd�� ����ʽ ����������
	 * 		days ���� 
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
	 * ������Ҫ���ѵ�����
	 * @param 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String findAlarmBirthday(){
		sqliteHelper.update();//������ϵ�����ա�
		
		String showDay = preferences.getPreferences("day", "1");//����������ǰ��������Ĭ��1��
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
						stringBuilder.append("ũ������");
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
						stringBuilder.append("��������");
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
	 * �������������Լ����ѵ�ʱ��������ؾ���������������ʱ��ĺ�����.
	 * @param birthday �������� 
	 * @return millis the number of milliseconds since Jan. 1, 1970, midnight GMT. 
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private long getAlarmTimeInMillis(String birthday){
		long millis = -1;
		if(null!=birthday && !birthday.isEmpty()){
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
			try {
				Date date = sDateFormat.parse(birthday);
				//�õ�������SharedPreferences�е��������ò���
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
	 * ����һ�������������ӡ�
	 * @param millis the number of milliseconds since Jan. 1, 1970, midnight GMT. �����ӵ�ʱ�䡣
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
	 * ȡ���������ѵ����ӡ�
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
