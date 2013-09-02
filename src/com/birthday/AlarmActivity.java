/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   阳历和农历的互相转换，以及计算下一个农历和阳历生日
 */
package com.birthday;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
/** 
 * 显示闹钟提醒的Activity类
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class AlarmActivity extends Activity {
	private MediaPlayer mediaPlayer = null;
	private ListView testlistview; 
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.alarmactivity);
		
		testlistview = (ListView)findViewById(R.id.testzheng);
		updateListView();
		
		Button okBtn = (Button)findViewById(R.id.okBtn);
		okBtn.setOnClickListener(onClickListener);
		
		Button openBtn = (Button)findViewById(R.id.openBtn);
		openBtn.setOnClickListener(onClickListener);
		
//		messageTextView.setText(getDisplayMessage());//设置提醒的信息
		BirthdayPreferences preferences = new BirthdayPreferences(this);
		playerRing(preferences.getPreferences("mCustomRingtone", null));
		vibrate(preferences.getPreferences("vibrate", 1));
		new SetBirthdayAlarm(this).setBirthdayAlarm();
	}
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.okBtn:
				if(null!=mediaPlayer){
					mediaPlayer.stop();//停止铃声播放
				}
				AlarmActivity.this.finish();
				break;
			case R.id.openBtn:
				Intent intent = new Intent(AlarmActivity.this,BirthdayActivity.class);
				startActivity(intent);
				AlarmActivity.this.finish();
				break;
			default:
				break;
			}
		}
	};
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent keyEvent){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			if(null!=mediaPlayer){
				mediaPlayer.stop();//停止铃声播放
			}
			AlarmActivity.this.finish();
			break;
		}
		return false;
	}
	/** 
	 * 得到需要提醒的信息
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	private String getDisplayMessage(){
		Intent intent = getIntent();
		String message = intent.getStringExtra("message");
		return message;
	}
	/** 
	 * 震动
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	private void vibrate(int open){
		if(open==1){
			Vibrator vibrator=(Vibrator)this.getSystemService(AlarmActivity.VIBRATOR_SERVICE);
			vibrator.vibrate(new long[]{2500,500,2500,1500}, -1);
		}
	}
	/** 
	 * 播放铃声
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since      
	 */ 
	private void playerRing(String ring){
		if(null!=ring && !ring.isEmpty()){
			mediaPlayer = MediaPlayer.create(this, Uri.parse(ring));
			mediaPlayer.start();
		}
	}
	
	/** 
	 * 更新listview列表内容
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void updateListView(){
		String message = getDisplayMessage();
		String messageArrary[] = message.split("\n");
		
		int length = messageArrary.length/3;
		
		if(length>0){
			String name[] =  new String[length];
			String lundarOrSolar[] = new String[length];
			String birthday[] = new String[length];
			ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
			
			
			
			for(int i = 0 ,j = 0; i < (messageArrary.length -2) ; j++){
				name[j] = messageArrary[i++];
				lundarOrSolar[j] = messageArrary[i++];
				birthday[j] = messageArrary[i++];
			}
			
			for(int i = 0;i<lundarOrSolar.length;i++){
				HashMap<String, Object> user = new HashMap<String, Object>();
				user.put("name", name[i]);
				user.put("lundarOrSolar", lundarOrSolar[i]);
				user.put("birthday", birthday[i]);
				users.add(user);
			}
			SimpleAdapter saImageItems = new SimpleAdapter(this,
					users,              // 数据来源
					R.layout.alarmlist,  //相当ListView的一个组件 
					new String[] { "name", "lundarOrSolar","birthday" },
					// 分别对应view 的id
					new int[] {  R.id.name, R.id.lundarOrSolar,R.id.birthday });
			testlistview.setAdapter(saImageItems);
		}
	}
}
