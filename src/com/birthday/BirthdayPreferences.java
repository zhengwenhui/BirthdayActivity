/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   ������ũ���Ļ���ת�����Լ�������һ��ũ������������
 */
package com.birthday;

import android.content.Context;
import android.content.SharedPreferences;
/** 
 * �������ѵĲ�����
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */ 
public class BirthdayPreferences {
	private final String TEMP_SET = "temp_set";
	private final Context context ;
	public static final String [] TypeString = {
		"��ʾ�������������ѵ���ϵ��",
		"��ʾ���������յ���ϵ��",
		"��ʾ������ϵ��"
	};
	public static final String [] DAY_STRING = {
		"����",
		"1��",
		"2��",
		"3��",
		"4��",
		"5��",
		"6��",
		"7��",
		"30��"
	};
	/** 
	 * ������
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
	 * ����key�õ�int�Ͳ��������SharedPreferences�е�����
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
	 * ����key�õ�String�Ͳ��������SharedPreferences�е�����
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
	 * ���޸ĵĲ������浽SharedPreferences
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
	 * ���޸ĵĲ������浽SharedPreferences
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
	//�����ͼ��
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
	//������ͼ��
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
	 * �õ�����������ǰ��������ʾ����Ϣ
	 * @param ����������ǰ���ѵ�������Ϣ
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
	 * �õ�����������ǰ������int����
	 * @param ����������ǰ���ѵ�������Ϣ
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
	 * �õ�����������ǰ�����Ĳ�������
	 * @param ����������ǰ���ѵ�������Ϣ
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
	 * �õ�����������ǰ�����Ĳ�������
	 * @param ����������ǰ���ѵ�������Ϣ
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
	 * �ж�����boolean�����Ƿ���ȣ������������boolean���鳤����ȣ���ÿ����Ӧ��ȫ����ȣ���ô������boolean�������
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
	 * �жϸ�����boolean�Ƿ�ȫ��Ϊfalse
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
	
//	//������ͼ��
//	public static final String[]PREFERENCES_ITEM = {
//		"hourOfDay",		//�������ѵ�ʱ
//		"minute",			//�������ѵķ���
//		"day",				//����������ǰ������
//		"type",				//�б�ѡ������
//		"alarmStatus",		//�Ƿ�������
//		"mCustomRingtone",	//���� 
//		"vibrate",			//��
//	};
}
