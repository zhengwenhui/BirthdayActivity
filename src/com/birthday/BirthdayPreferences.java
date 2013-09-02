/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   阳历和农历的互相转换，以及计算下一个农历和阳历生日
 */
package com.birthday;

import android.content.Context;
import android.content.SharedPreferences;
/** 
 * 生日提醒的参数类
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class BirthdayPreferences {
	private final String TEMP_SET = "temp_set";
	private final Context context ;
	public static final String [] TypeString = {
		"显示已设置生日提醒的联系人",
		"显示已设置生日的联系人",
		"显示所有联系人"
	};
	public static final String [] DAY_STRING = {
		"当天",
		"1天",
		"2天",
		"3天",
		"4天",
		"5天",
		"6天",
		"7天",
		"30天"
	};
	/** 
	 * 构造器
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public BirthdayPreferences(Context context){
		// TODO Auto-generated constructor stub
		this.context = context;
	}
	/** 
	 * 根据key得到int型参数，获得SharedPreferences中的数据
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public int getPreferences(String key,int defValue){
		SharedPreferences preferences = context.getSharedPreferences(TEMP_SET, Context.MODE_WORLD_READABLE);
		return preferences.getInt(key, defValue);
	}
	/** 
	 * 根据key得到String型参数，获得SharedPreferences中的数据
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public String getPreferences(String key,String defValue){
		SharedPreferences preferences = context.getSharedPreferences(TEMP_SET, Context.MODE_WORLD_READABLE);
		return preferences.getString(key, defValue);
	}
	/** 
	 * 将修改的参数保存到SharedPreferences
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void putPreferences(String key,int value){
		SharedPreferences.Editor mPreEditor = 
			context.getSharedPreferences(TEMP_SET, Context.MODE_WORLD_WRITEABLE).edit();    	
		mPreEditor.putInt(key, value);
		mPreEditor.commit();
	}
	/** 
	 * 将修改的参数保存到SharedPreferences
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void putPreferences(String key,String value){
		SharedPreferences.Editor mPreEditor = 
			context.getSharedPreferences(TEMP_SET, Context.MODE_WORLD_WRITEABLE).edit();    	
		mPreEditor.putString(key, value);
		mPreEditor.commit();
	}
	//属相的图标
	public static final int[]ANIMAL_SIGN = {
		R.drawable.animal_sign00,
		R.drawable.animal_sign01,
		R.drawable.animal_sign02,
		R.drawable.animal_sign03,
		R.drawable.animal_sign04,
		R.drawable.animal_sign05,
		R.drawable.animal_sign06,
		R.drawable.animal_sign07,
		R.drawable.animal_sign08,
		R.drawable.animal_sign09,
		R.drawable.animal_sign10,
		R.drawable.animal_sign11,
	};
	//星座的图标
	public static final int[]CONSTELLATION_SIGN = {
		R.drawable.constellation00,
		R.drawable.constellation01,
		R.drawable.constellation02,
		R.drawable.constellation03,
		R.drawable.constellation04,
		R.drawable.constellation05,
		R.drawable.constellation06,
		R.drawable.constellation07,
		R.drawable.constellation08,
		R.drawable.constellation09,
		R.drawable.constellation10,
		R.drawable.constellation11,
	};
	
	/** 
	 * 得到生日提醒提前天数的显示的信息
	 * @param 生日提醒提前提醒的设置信息
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static String getTypeShowMessage(String type){
		StringBuilder stringBuilder = new StringBuilder();
		for(int i =0;i<DAY_STRING.length && i<type.length();i++){
			if(type.charAt(i)=='1'){
				stringBuilder.append(DAY_STRING[i]);
				stringBuilder.append(",");
			}
		}
		if(stringBuilder.length()>0){
			stringBuilder.deleteCharAt(stringBuilder.length()-1);
		}
		return stringBuilder.toString();
	}

	/** 
	 * 得到生日提醒提前天数的int数组
	 * @param 生日提醒提前提醒的设置信息
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static int[] getTypeStringArray(String type){
		
		int []typeIntArray = new int[DAY_STRING.length];
		
		for(int i =0;i<DAY_STRING.length;i++){
			if(i<type.length() && type.charAt(i)=='1'){
				if(i==DAY_STRING.length-1){
					typeIntArray[i] = 30;
				}
				else{
					typeIntArray[i] = i;
				}
			}
			else{
				typeIntArray[i] = -1;
			}
		}
		return typeIntArray;
	}
	
	/** 
	 * 得到生日提醒提前天数的布尔数组
	 * @param 生日提醒提前提醒的设置信息
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static boolean[] getTypeBooleanArray(String type){
		boolean []typeBooleanArray = new boolean[DAY_STRING.length];
		for(int i =0;i<DAY_STRING.length;i++){
			if(i<type.length() && type.charAt(i)=='1'){
				typeBooleanArray[i]=true;
			}
			else{
				typeBooleanArray[i]=false;
			}
		}
		return typeBooleanArray;
	}
	/** 
	 * 得到生日提醒提前天数的布尔数组
	 * @param 生日提醒提前提醒的设置信息
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static String getTypeString(boolean[] daysBooleanArray){
		char []showday = new char[DAY_STRING.length];
		for( int i = 0; i < DAY_STRING.length; i++){
			if( i < daysBooleanArray.length && daysBooleanArray[i]){
				showday[i] = '1';
			}
			else{
				showday[i] = '0';
			}
		}
		return String.valueOf(showday);
	}
	
	/** 
	 * 判断两个boolean数组是否相等，规则：如果两个boolean数组长度相等，且每个对应项全部相等，那么这两个boolean数组相等
	 * @param 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static boolean booleanEquals(boolean[] from ,boolean []to){
		boolean result = false;
		if( from.length == to.length ){
			int i = 0;
			for( ; i < from.length; i++ ){
				if( from[i] != to[i] ){
					break;
				}
			}
			if( i == from.length ){
				result = true;
			}
		}
		return result;
	}
	/** 
	 * 判断给定的boolean是否全部为false
	 * @param 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public static boolean isAllFalse(boolean[] from){
		boolean result = false;
		int i;
		for( i = 0; i < from.length; i++ ){
			if( true == from[i]){
				break;
			}
		}
		if( i == from.length ){
			result = true;
		}
		return result;
	}
	
//	//星座的图标
//	public static final String[]PREFERENCES_ITEM = {
//		"hourOfDay",		//生日提醒的时
//		"minute",			//生日提醒的分钟
//		"day",				//生日提醒提前的天数
//		"type",				//列表选项的类别
//		"alarmStatus",		//是否开启闹钟
//		"mCustomRingtone",	//铃声 
//		"vibrate",			//振动
//	};
}
