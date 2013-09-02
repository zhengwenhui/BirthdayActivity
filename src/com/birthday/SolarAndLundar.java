/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��19��     ֣�Ļ�	   ������ũ���Ļ���ת�����Լ�������һ��ũ������������
 */ 
package com.birthday;

import java.util.Calendar;
import java.lang.Math;

import android.os.Bundle;

/** 
 * ������ũ���Ļ���ת�����Լ�������һ��ũ�����������ա�
 * 
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��19��
 */ 
public class SolarAndLundar {
	public static final int MIN_YEAR = 1901;
	public static final int MAX_YEAR = 2099;
	/*
	 * �������Ӧ��ũ������,ÿ�����ֽ�,��1901�굽2099��
	 * ��һ�ֽ�BIT7-4 λ��ʾ�����·�,ֵΪ0 Ϊ������,BIT3-0 ��Ӧũ����1-4 �µĴ�С
	 * �ڶ��ֽ�BIT7-0 ��Ӧũ����5-12 �´�С,
	 * �����ֽ�BIT7 ��ʾũ����13 ���´�С�·ݶ�Ӧ��λΪ1 ��ʾ��ũ���´�(30 ��),Ϊ0 ��ʾС(29 ��)
	 * �����ֽ�BIT6-5 ��ʾ���ڵĹ����·�,BIT4-0 ��ʾ���ڵĹ�������
	 */
	private static final char[] YEAR_CODE = {
		0x04,0xAe,0x53,    //1901 0
		0x0A,0x57,0x48,    //1902 3
		0x55,0x26,0xBd,    //1903 6
		0x0d,0x26,0x50,    //1904 9
		0x0d,0x95,0x44,    //1905 12
		0x46,0xAA,0xB9,    //1906 15
		0x05,0x6A,0x4d,    //1907 18
		0x09,0xAd,0x42,    //1908 21
		0x24,0xAe,0xB6,    //1909
		0x04,0xAe,0x4A,    //1910
		0x6A,0x4d,0xBe,    //1911
		0x0A,0x4d,0x52,    //1912
		0x0d,0x25,0x46,    //1913
		0x5d,0x52,0xBA,    //1914
		0x0B,0x54,0x4e,    //1915
		0x0d,0x6A,0x43,    //1916
		0x29,0x6d,0x37,    //1917
		0x09,0x5B,0x4B,    //1918
		0x74,0x9B,0xC1,    //1919
		0x04,0x97,0x54,    //1920
		0x0A,0x4B,0x48,    //1921
		0x5B,0x25,0xBC,    //1922
		0x06,0xA5,0x50,    //1923
		0x06,0xd4,0x45,    //1924
		0x4A,0xdA,0xB8,    //1925
		0x02,0xB6,0x4d,    //1926
		0x09,0x57,0x42,    //1927
		0x24,0x97,0xB7,    //1928
		0x04,0x97,0x4A,    //1929
		0x66,0x4B,0x3e,    //1930
		0x0d,0x4A,0x51,    //1931
		0x0e,0xA5,0x46,    //1932
		0x56,0xd4,0xBA,    //1933
		0x05,0xAd,0x4e,    //1934
		0x02,0xB6,0x44,    //1935
		0x39,0x37,0x38,    //1936
		0x09,0x2e,0x4B,    //1937
		0x7C,0x96,0xBf,    //1938
		0x0C,0x95,0x53,    //1939
		0x0d,0x4A,0x48,    //1940
		0x6d,0xA5,0x3B,    //1941
		0x0B,0x55,0x4f,    //1942
		0x05,0x6A,0x45,    //1943
		0x4A,0xAd,0xB9,    //1944
		0x02,0x5d,0x4d,    //1945
		0x09,0x2d,0x42,    //1946
		0x2C,0x95,0xB6,    //1947
		0x0A,0x95,0x4A,    //1948
		0x7B,0x4A,0xBd,    //1949
		0x06,0xCA,0x51,    //1950
		0x0B,0x55,0x46,    //1951
		0x55,0x5A,0xBB,    //1952
		0x04,0xdA,0x4e,    //1953
		0x0A,0x5B,0x43,    //1954
		0x35,0x2B,0xB8,    //1955
		0x05,0x2B,0x4C,    //1956
		0x8A,0x95,0x3f,    //1957
		0x0e,0x95,0x52,    //1958
		0x06,0xAA,0x48,    //1959
		0x7A,0xd5,0x3C,    //1960
		0x0A,0xB5,0x4f,    //1961
		0x04,0xB6,0x45,    //1962
		0x4A,0x57,0x39,    //1963
		0x0A,0x57,0x4d,    //1964
		0x05,0x26,0x42,    //1965
		0x3e,0x93,0x35,    //1966
		0x0d,0x95,0x49,    //1967
		0x75,0xAA,0xBe,    //1968
		0x05,0x6A,0x51,    //1969
		0x09,0x6d,0x46,    //1970
		0x54,0xAe,0xBB,    //1971
		0x04,0xAd,0x4f,    //1972
		0x0A,0x4d,0x43,    //1973
		0x4d,0x26,0xB7,    //1974
		0x0d,0x25,0x4B,    //1975
		0x8d,0x52,0xBf,    //1976
		0x0B,0x54,0x52,    //1977
		0x0B,0x6A,0x47,    //1978
		0x69,0x6d,0x3C,    //1979
		0x09,0x5B,0x50,    //1980
		0x04,0x9B,0x45,    //1981
		0x4A,0x4B,0xB9,    //1982
		0x0A,0x4B,0x4d,    //1983
		0xAB,0x25,0xC2,    //1984
		0x06,0xA5,0x54,    //1985
		0x06,0xd4,0x49,    //1986
		0x6A,0xdA,0x3d,    //1987
		0x0A,0xB6,0x51,    //1988
		0x09,0x37,0x46,    //1989
		0x54,0x97,0xBB,    //1990
		0x04,0x97,0x4f,    //1991
		0x06,0x4B,0x44,    //1992
		0x36,0xA5,0x37,    //1993
		0x0e,0xA5,0x4A,    //1994
		0x86,0xB2,0xBf,    //1995
		0x05,0xAC,0x53,    //1996
		0x0A,0xB6,0x47,    //1997
		0x59,0x36,0xBC,    //1998
		0x09,0x2e,0x50,    //1999 294
		0x0C,0x96,0x45,    //2000 297
		0x4d,0x4A,0xB8,    //2001
		0x0d,0x4A,0x4C,    //2002
		0x0d,0xA5,0x41,    //2003
		0x25,0xAA,0xB6,    //2004
		0x05,0x6A,0x49,    //2005
		0x7A,0xAd,0xBd,    //2006
		0x02,0x5d,0x52,    //2007
		0x09,0x2d,0x47,    //2008
		0x5C,0x95,0xBA,    //2009
		0x0A,0x95,0x4e,    //2010
		0x0B,0x4A,0x43,    //2011
		0x4B,0x55,0x37,    //2012
		0x0A,0xd5,0x4A,    //2013
		0x95,0x5A,0xBf,    //2014
		0x04,0xBA,0x53,    //2015
		0x0A,0x5B,0x48,    //2016
		0x65,0x2B,0xBC,    //2017
		0x05,0x2B,0x50,    //2018
		0x0A,0x93,0x45,    //2019
		0x47,0x4A,0xB9,    //2020
		0x06,0xAA,0x4C,    //2021
		0x0A,0xd5,0x41,    //2022
		0x24,0xdA,0xB6,    //2023
		0x04,0xB6,0x4A,    //2024
		0x69,0x57,0x3d,    //2025
		0x0A,0x4e,0x51,    //2026
		0x0d,0x26,0x46,    //2027
		0x5e,0x93,0x3A,    //2028
		0x0d,0x53,0x4d,    //2029
		0x05,0xAA,0x43,    //2030
		0x36,0xB5,0x37,    //2031
		0x09,0x6d,0x4B,    //2032
		0xB4,0xAe,0xBf,    //2033
		0x04,0xAd,0x53,    //2034
		0x0A,0x4d,0x48,    //2035
		0x6d,0x25,0xBC,    //2036
		0x0d,0x25,0x4f,    //2037
		0x0d,0x52,0x44,    //2038
		0x5d,0xAA,0x38,    //2039
		0x0B,0x5A,0x4C,    //2040
		0x05,0x6d,0x41,    //2041
		0x24,0xAd,0xB6,    //2042
		0x04,0x9B,0x4A,    //2043
		0x7A,0x4B,0xBe,    //2044
		0x0A,0x4B,0x51,    //2045
		0x0A,0xA5,0x46,    //2046
		0x5B,0x52,0xBA,    //2047
		0x06,0xd2,0x4e,    //2048
		0x0A,0xdA,0x42,    //2049
		0x35,0x5B,0x37,    //2050
		0x09,0x37,0x4B,    //2051
		0x84,0x97,0xC1,    //2052
		0x04,0x97,0x53,    //2053
		0x06,0x4B,0x48,    //2054
		0x66,0xA5,0x3C,    //2055
		0x0e,0xA5,0x4f,    //2056
		0x06,0xB2,0x44,    //2057
		0x4A,0xB6,0x38,    //2058
		0x0A,0xAe,0x4C,    //2059
		0x09,0x2e,0x42,    //2060
		0x3C,0x97,0x35,    //2061
		0x0C,0x96,0x49,    //2062
		0x7d,0x4A,0xBd,    //2063
		0x0d,0x4A,0x51,    //2064
		0x0d,0xA5,0x45,    //2065
		0x55,0xAA,0xBA,    //2066
		0x05,0x6A,0x4e,    //2067
		0x0A,0x6d,0x43,    //2068
		0x45,0x2e,0xB7,    //2069
		0x05,0x2d,0x4B,    //2070
		0x8A,0x95,0xBf,    //2071
		0x0A,0x95,0x53,    //2072
		0x0B,0x4A,0x47,    //2073
		0x6B,0x55,0x3B,    //2074
		0x0A,0xd5,0x4f,    //2075
		0x05,0x5A,0x45,    //2076
		0x4A,0x5d,0x38,    //2077
		0x0A,0x5B,0x4C,    //2078
		0x05,0x2B,0x42,    //2079
		0x3A,0x93,0xB6,    //2080
		0x06,0x93,0x49,    //2081
		0x77,0x29,0xBd,    //2082
		0x06,0xAA,0x51,    //2083
		0x0A,0xd5,0x46,    //2084
		0x54,0xdA,0xBA,    //2085
		0x04,0xB6,0x4e,    //2086
		0x0A,0x57,0x43,    //2087
		0x45,0x27,0x38,    //2088
		0x0d,0x26,0x4A,    //2089
		0x8e,0x93,0x3e,    //2090
		0x0d,0x52,0x52,    //2091
		0x0d,0xAA,0x47,    //2092
		0x66,0xB5,0x3B,    //2093
		0x05,0x6d,0x4f,    //2094
		0x04,0xAe,0x45,    //2095
		0x4A,0x4e,0xB9,    //2096
		0x0A,0x4d,0x4C,    //2097
		0x0d,0x15,0x41,    //2098
		0x2d,0x92,0xB5,    //2099
	};
	/** 
	 * ����ũ��lunarYear��������·�������û�з���0
	 * @param      ũ����ݣ���2011
	 * @return     ũ��year��������·�������lunarYear�Ƿ�������-1
	 * @exception  
	 * @see        
	 * @since      ����1990���������£�����ֵΪ5
	 */ 
	public int getLeapMonthOfLunarYear(int lunarYear){
		int leapMonthOfLunarYear = 0;
		if(check(lunarYear)<0){
			leapMonthOfLunarYear = -1;
		}
		else{
			leapMonthOfLunarYear = (YEAR_CODE[(lunarYear-MIN_YEAR)*3]&0xf0)>>4;
		}
		return leapMonthOfLunarYear;
	}
	/** 
	 * ����year���ũ��һ��һ��������1��1����������
	 * @param      ���
	 * @return     ����year���ũ��һ��һ��������1��1��������������year�Ƿ�������-1
	 * @exception  
	 * @see        
	 * @since      ����2011��ũ��һ��һ��������2��3�գ�����33(31+3-1)
	 */ 
	public int getLunarSolarOffset(int year){
		int offset = 0;
		if(check(year)<0){
			offset = -1;
		}
		else{
			int month = (YEAR_CODE[(year-1901)*3+2]&0x60)>>5;
			int day = YEAR_CODE[(year-1901)*3+2]&0x1f;
			offset = day;
			switch(month){
			case 1 : break;
			case 2 : offset+=31;break;
			//case 3 : {
			//if(isSolarLeapYear(year)){
			//	offset = 60;
			//}
			//else
			//	offset = 59;
			//}
			default : break;
			}
			offset--;
		}
		return offset;
	}
	/** 
	 * ����ũ��lunarYear��lunarMonth�µ�����
	 * @param    
	 * @return     ����ũ��lunarYear��lunarMonth�µ�����,���򷵻�-1;
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public int getDaysOfLunarMonth(int lunarYear,int lunarMonth){
		int DaysOfLunarMonth = 0;
		if(check(lunarYear,lunarMonth)<0){
			DaysOfLunarMonth = -1;
		}
		else{
			int bigOrSmallMonth = 0;
			if(13 == lunarMonth){
				bigOrSmallMonth = (YEAR_CODE[(lunarYear-MIN_YEAR)*3+2]&0x80)>>7;
			}
			else if(lunarMonth>=5){
				bigOrSmallMonth = (YEAR_CODE[(lunarYear-MIN_YEAR)*3+1]&0x80>>(lunarMonth-5))>>(12-lunarMonth);
			}
			else{
				bigOrSmallMonth = (YEAR_CODE[(lunarYear-MIN_YEAR)*3]&0x80>>(lunarMonth+3))>>(4-lunarMonth);
			}
			DaysOfLunarMonth = (1==bigOrSmallMonth)?30:29;
		}
		return DaysOfLunarMonth;
	}
	/** 
	 * ���ũ��year���Ƿ�Ƿ�
	 * @param    
	 * @return     ��ȷ����1,year��������������ʱ����-1
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int check(int year){
		int result = 1;
		if(year>MAX_YEAR||year<MIN_YEAR)
			result = -1;
		return result;
	}
	/** 
	 * ���ũ��year��month���Ƿ�Ƿ�
	 * @param    
	 * @return     ��ȷ����1,�Ƿ�����һ������
	 * 			   year��������������ʱ����-1��
	 * 			   month������Χ����-2��1��13����
	 * 			   month����13�����Ǹ���û�����£�ֻ��12����ʱ����-3��
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int check(int year,int month){
		int result = 1;
		result = check(year);
		if(1==result){
			if(month>13||month<1){
				result = -2;
			}	
			else if(month==13&&getLeapMonthOfLunarYear(year)<=0){
				result = -3;
			}
		}
		return result;
	}
	/** 
	 * ���ũ��year��month��day���Ƿ�Ƿ�
	 * @param    
	 * @return     ��ȷ����1,�Ƿ�����һ��������
	 * 			   year��������������ʱ����-1��
	 * 			   month������Χ����-2��1��13����
	 * 			   month����13�����Ǹ���û�����£�ֻ��12����ʱ����-3��
	 * 			   day������Χ����-4��1��30����
	 * 			        ��year��month��û��day��ʱ����-5��
	 * @exception  
	 * @see        
	 * @since     
	 */
	public int check(int year,int month,int day){
		int result = 1;
		result = check(year,month);
		if(1==result){
			if(day>30||day<1){
				result = -4;
			}
			else if(day>getDaysOfLunarMonth(year,month)){
				result = -5;
			}
		}
		return result;
	}
	/** 
	 * ����ũ��year��month��day����year��һ��һ����������
	 * @param    	����������ũ�����ڷǷ�ʱ����һ��������
	 * @return     ��ȷ��������,�Ƿ�����һ������
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int getDaysOfLunarDate(int year,int month,int day) {
		int days = 0;
		if(check(year,month,day)<0){
			days = -1;
		}
		else{
			for(int i = 1; i<month ;i++){
				days+=getDaysOfLunarMonth(year,i);
			}
			days = days+day;
		}
		return days;
	}
	/** 
	 * �жϸ���������year���ǲ������꣨����2��29�죬ƽ��2��28�죩
	 * �����ܱ�4�����Ҳ��ܱ�100�����������ܱ�400��������������ꡣ
	 * @param    	
	 * @return     �����귵��true�����Ƿ���false
	 * @exception  
	 * @see        
	 * @since     
	 */
	private boolean isSolarLeapYear(int iYear) {
		return ((iYear % 4 == 0) && (iYear % 100 != 0) || iYear % 400 == 0);
	}
	/** 
	 * ����������������ת��Ϊũ������
	 * @param      Calendar calendar ��������
	 * @return     ����Bundle�����к���int��year��month��day��leapMonth
	 * 				ת��ʧ�ܣ�����null
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Bundle sCalendarSolarToLundar(Calendar calendar) {
		int day,lunarDay, lunarMonth, lunarYear = calendar.get(Calendar.YEAR);
		//�õ������������ھ��뵱��1��1�յ�����
		Bundle bundle = null;
		lunarDay = calendar.get(Calendar.DAY_OF_YEAR);
		//���������������뵱��1��1����������
		if (lunarDay <= getLunarSolarOffset(lunarYear)) {
			//������������������뵱��1��1����������С�ڵ���ũ��һ��һ��������1��1����������
			lunarYear--;
			lunarDay += isSolarLeapYear(lunarYear)?366:365;
		}

		lunarDay -= getLunarSolarOffset(lunarYear);
		//�õ���ǰ���ھ���ũ��lunarYear��һ��һ����������

		for ( lunarMonth = 1; lunarMonth <= 13; lunarMonth++) {
			//����ũ�����·�
			day = getDaysOfLunarMonth(lunarYear,lunarMonth);
			if(day<0||lunarDay<=day){
				break;
			}
			lunarDay -= day;
		}

		if(check(lunarYear, lunarMonth, lunarDay)>0){
			bundle = new Bundle();
			bundle.putInt("year", lunarYear);
			bundle.putInt("month", lunarMonth);
			bundle.putInt("day", lunarDay);
			bundle.putInt("leapMonth", getLeapMonthOfLunarYear(lunarYear));
		}
		return bundle;
	}
	/** 
	 * ��������ũ������ת��Ϊ��Ӧ����������
	 * @param      
	 * @return     Calendar,���������ũ�����ڷǷ����򷵻�null;
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Calendar sCalendarLundarToSolar(int year,int month,int day){
		Calendar calendar = null ;
		if(check(year, month, day)>0){
			int offset = getDaysOfLunarDate( year,month,day)+getLunarSolarOffset(year);
			calendar = Calendar.getInstance();
			calendar.set(year, 0, offset,0,0,0);
		}
		return calendar;
	}
	/** 
	 * ������������year��month��day�գ����شӵ�ǰ�����ڿ�ʼ����һ������month��day�յ����ڣ�
	 * 			���ڸ��������������ڣ�������һ���������ڣ�
	 * @param      
	 * @return     ����������ڲ�С�ڵ�ǰ�����ڣ����ظ��������ڣ�
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Calendar getNextSolarBirthday(int year,int month,int day){
		Calendar calendar = Calendar.getInstance();
		Calendar next = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		next.set(year, month-1, day);
		if(next.before(calendar)){
			//��������Ϊ2��29��
			if(2==month&&29==day){
				int BirthdayYear = calendar.get(Calendar.YEAR);
				//�����һ�����ꡣ
				while(!isSolarLeapYear(BirthdayYear)){
					BirthdayYear++;
				}
				next.set(BirthdayYear, month-1, day,0,0,0);
				//�������ѹ�ȥ��
				if(next.before(calendar)){
					//�����һ�����ꡣ
					//do{
					BirthdayYear+=4;
					//}while(!isSolarLeapYear(year));
					next.set(Calendar.YEAR, BirthdayYear);
				}
			}else{
				next.set(calendar.get(Calendar.YEAR), month-1, day,0,0,0);
				if(next.before(calendar)){
					next.add(Calendar.YEAR, 1);
				}			
			}
		}
		return next;
	}
	/** 
	 * ����ũ������year��month��day�գ����شӵ�ǰ��ũ�����ڿ�ʼ����һ������month��day�յ��������ڣ�
	 * ���¿�����ǰһ���൱(���磺�����¿�����������ͬ)�����ڸ���ũ���������ڣ�������һ��ũ�����յ��������ڣ�
	 * @param      
	 * @return     ����������ڲ�С�ڵ�ǰ�����ڣ����ظ���������Ӧ���������ڣ�����������ڳ��ַǷ�������null��
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	public Calendar getNextLundarBirthday(int year,int month,int day){
		Calendar calendar = Calendar.getInstance();
		int curYear = calendar.get(Calendar.YEAR);
		int BirthdayYear = curYear-1;
		int BirthdayMonth = 0;
		int leapmonthBirthday = 0;
		int leapmonthBorn = getLeapMonthOfLunarYear(year);
		Calendar Birthday = Calendar.getInstance();

		if(year>curYear){
			BirthdayYear = year;
		}
		if(leapmonthBorn>0&&leapmonthBorn<month){
			//������������month��������
			month--;
		}

		BirthdayMonth= month;
		leapmonthBirthday = getLeapMonthOfLunarYear(BirthdayYear);
		if(leapmonthBirthday>0&&leapmonthBirthday<month){
			BirthdayMonth++;
		}
		calendar.add(Calendar.DAY_OF_MONTH, -1);
		do{
			if(day<=getDaysOfLunarMonth(BirthdayYear,BirthdayMonth))
			{
				//�ж�day�Ƿ��ڸ�����µ�������
				if(check(BirthdayYear, BirthdayMonth, day)<0){
					Birthday = null;
					break;
				}
				Birthday = sCalendarLundarToSolar(BirthdayYear,BirthdayMonth,day);
			}
			if(leapmonthBirthday==BirthdayMonth){
				BirthdayMonth = month+1;
			}
			else{
				BirthdayMonth = month;
				BirthdayYear++;
				leapmonthBirthday = getLeapMonthOfLunarYear(BirthdayYear);
				if(leapmonthBirthday>0&&leapmonthBirthday<month){
					BirthdayMonth++;
				}
			}
		}while(Birthday.before(calendar));
		//�жϵ�ǰ��ũ�����ڣ��뵱���ũ�����գ����һ�����Ĵ�С���������������ũ�������Ѿ���ȥ�ˣ�����������ũ�����ա�
		return Birthday;  
	}
	/** 
	 * ���ظ���ũ����ݵ�����
	 * @param      
	 * @return     �Ƿ�����ݷ���һ�����ַ���
	 * @exception  
	 * @see        
	 * @since     
	 */
	@SuppressWarnings("unused")
	private String animalsYear(int year) {
		final String[] Animals = new String[]{"��", "ţ", "��", "��", "��", "��", "��", "��", "��", "��", "��", "��"};
		String result = "";
		if(check(year)>0){
			result = Animals[(year - 4) % 12];
		}
		return result;
	}
	/** 
	 * ���ظ���ũ����ݵ���ɵ�֧��ʾ
	 * @param      
	 * @return     �Ƿ�����ݷ���һ�����ַ���
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String ChineseYearEra(int year) {
		final String[] sHeavenlyStems = { "��", "��", "��", "��", "��", "��", "��", "��", "��", "��" };
		final String[] sEarthlyBranches = { "��", "��", "��", "î", "��", "��", "��", "δ", "��", "��", "��", "��" };
		String result = "";
		if(check(year)>0){
			int temp;
			temp = Math.abs(year - 1924);
			result = sHeavenlyStems[temp % 10] + sEarthlyBranches[temp % 12];
		}
		return result;
	}
	/** 
	 * ���ظ���ũ���·ݵ����ı�ʾ
	 * @param      
	 * @return     �Ƿ����·ݷ���һ�����ַ���
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String LunarMonthEra(int year,int month) {
		final String[] LunarMonth = { "��", "��", "��", "��", "��", "��", "��", "��","��", "ʮ", "ʮһ", "��" };
		String result = "";
		if(check(year, month)>0){
			int leapMonth = getLeapMonthOfLunarYear(year);
			if(leapMonth>0){
				if(leapMonth == month-1){
					result = "��";
				}
				if(leapMonth < month){
					month--;
				}
			}
			result += LunarMonth[month-1]; 
		}
		return result;
	}
	/** 
	 * ���ظ���ũ�����������ı�ʾ
	 * @param      
	 * @return     �Ƿ�����������һ�����ַ���
	 * @exception  
	 * @see        
	 * @since      ����28�ձ�ʾΪ��ʮ��
	 */ 
	private String lunarDayEra(int day) {
		final String[] LunarDay = { "","һ","��", "��", "��", "��", "��", "��", "��","��","��"};
		String temp;
		if(day<1||day>30){
			temp = "";
		}
		else{
			temp = LunarDay[day%10];
			switch(day/10){
			case 0: 
				temp="��"+temp; 
				break;
			case 1:
				temp="ʮ"+temp; 
				break;
			case 2:
				temp="إ"+temp;
				break;
			case 3: 
				temp="��ʮ"+temp; 
				break;
			}
			if(10==day){
				temp = "��ʮ";
			}
		}
		return temp;
	}
	/** 
	 * ���ظ���ũ����ݵ���ɵ�֧����
	 * @param      
	 * @return     ���ظ���ũ�����ڶ�Ӧ����ɵ�֧���꣬�Ƿ���ũ�����ڷ��ؿ��ַ���
	 * @exception  
	 * @see        
	 * @since      ����ũ��1924��1��1��Ϊ������һ��һ��
	 */ 
	public String lunarYear(int year,int month,int day){
		String result;

		if(check(year, month, day)<0){
			result = "";
		}
		else{
			StringBuilder chineseDate = new StringBuilder();
			chineseDate.append(ChineseYearEra(year));
			chineseDate.append("��");
			chineseDate.append(LunarMonthEra(year,month));
			chineseDate.append("��");
			chineseDate.append(lunarDayEra(day));
//			chineseDate.append("��");
			result = chineseDate.toString();
		}
		return result;
	}
}
