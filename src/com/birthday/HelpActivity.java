/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   
 */
package com.birthday;

import android.R.style;
import android.app.Activity;
import android.os.Bundle;
/** 
 * ��ʾ������Ϣ��Activity�ࡣ
 * 
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */
public class HelpActivity extends Activity {
	String mCustomRingtone = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getWindow().requestFeature(android.R.drawable.btn_minus);
		setTheme(style.Theme_Dialog);
		setTitle("����");//����title
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
}
