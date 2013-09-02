/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   
 */
package com.birthday;

import android.R.style;
import android.app.Activity;
import android.os.Bundle;
/** 
 * 显示帮助信息的Activity类。
 * 
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */
public class HelpActivity extends Activity {
	String mCustomRingtone = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		//getWindow().requestFeature(android.R.drawable.btn_minus);
		setTheme(style.Theme_Dialog);
		setTitle("帮助");//设置title
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
}
