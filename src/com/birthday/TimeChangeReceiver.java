/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   阳历和农历的互相转换，以及计算下一个农历和阳历生日
 */
package com.birthday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/** 
 * 接收并处理时间设置，日期改变，时区改变的广播并重新设置生日提醒的闹钟。
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class TimeChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		new SetBirthdayAlarm(context).setBirthdayAlarm();
	}
}
