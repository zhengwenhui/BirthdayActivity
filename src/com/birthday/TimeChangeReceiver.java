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
/** 
 * ���ղ�����ʱ�����ã����ڸı䣬ʱ���ı�Ĺ㲥�����������������ѵ����ӡ�
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */ 
public class TimeChangeReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		new SetBirthdayAlarm(context).setBirthdayAlarm();
	}
}
