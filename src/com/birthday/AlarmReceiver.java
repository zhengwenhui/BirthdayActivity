/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   ������ũ���Ļ���ת�����Լ�������һ��ũ������������
 */
package com.birthday;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
/** 
 * �����������ѹ㲥��BroadcastReceiver��
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
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
		//������������Activity
		arg0.startActivity(intent1);	
	}
}
