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
import android.os.Bundle;
/** 
 * 接收闹钟提醒广播的BroadcastReceiver类
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class AlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context arg0, Intent intent) {
		// TODO Auto-generated method stub	
		Bundle bundle = intent.getExtras();
//		Intent intent1 = new Intent(arg0,CheckBirthdayListActivity.class);
		Intent intent1 = new Intent(arg0,AlarmActivity.class);//
		intent1.putExtras(bundle);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//启动生日提醒Activity
		arg0.startActivity(intent1);	
	}
}
