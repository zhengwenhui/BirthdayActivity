/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   
 */
package com.birthday;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TimePicker;
import android.widget.Toast;
/** 
 * 设置生日提醒参数的Activity类 。包括是否开启提醒闹钟、提醒时间、提前的天数、铃声、振动、显示选项。
 * 
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
 */ 
public class SetActivity extends Activity {
	private int showHourOfDay = 0,showMinute = 0, alarmStatus = 1;//showType = 0,
	private String mCustomRingtone = null;//铃声
	private String showDay;//提醒的天数
	private int vibrate;//振动
	private int change = 0;
	ListView setlistview = null;
	boolean daysBooleanArray[];
	boolean daysBooleanArrayCompare[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设置允许改变的窗口状态
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.setbirthday);
		//标题区域设置布局。
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 

		setlistview = (ListView)findViewById(R.id.ListViewSetBirtyday);
		setlistview.setCacheColorHint(getResources().getColor(R.color.listview_color));

		updateListView();//刷新界面
		setlistview.setOnItemClickListener(onItemClickListener);
	}
	/** 
	 * 得到SharedPreferences中生日提醒参数的数据
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void getSharedPreferences(){
		BirthdayPreferences preferences = new BirthdayPreferences(this);
		showHourOfDay = preferences.getPreferences("hourOfDay", 9);				//生日提醒的时
		showMinute = preferences.getPreferences("minute", 0);					//生日提醒的分钟
		showDay = preferences.getPreferences("day", "1");						//生日提醒提前的天数，默认1天
		alarmStatus = preferences.getPreferences("alarmStatus", 1);				//是否开启闹钟，默认开启
		mCustomRingtone = preferences.getPreferences("mCustomRingtone", null);  //铃声 
		vibrate = preferences.getPreferences("vibrate", 1);						//振动，默认开启
	}
	/** 
	 * 将修改的参数保存到SharedPreferences
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void putSharedPreferences(String key,int value){
		BirthdayPreferences preferences = new BirthdayPreferences(this);
		preferences.putPreferences(key, value);
	}
	/** 
	 * 将修改的参数保存到SharedPreferences
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void putSharedPreferences(String key,String value){
		BirthdayPreferences preferences = new BirthdayPreferences(this);
		preferences.putPreferences(key, value);
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
		getSharedPreferences();
		ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();

		DecimalFormat decimalformat = new DecimalFormat("00");
		String strDate = decimalformat.format(showHourOfDay)+":"+decimalformat.format(showMinute);

		String temp = "";
		temp = getMediaDisplayNameViaUriString(mCustomRingtone);

		String from[] = new String[]{
				alarmStatus==0?"已关闭":"已开启",
						strDate,
						BirthdayPreferences.getTypeShowMessage(showDay),
						temp,
						vibrate==0?"已关闭":"已开启",
								//								showTypeStr
		};
		String to[] = new String[]{
				getString(R.string.alarm),
				getString(R.string.string04),
				getString(R.string.string03),
				getString(R.string.ring),
				getString(R.string.vibrate),
				//				getString(R.string.birthdayList)
		};
		int image[] = new int[]{
				alarmStatus==0?R.drawable.btn_check_on_disable:R.drawable.btn_check_on,
						R.drawable.expander_ic_minimized,
						R.drawable.expander_ic_minimized,
						R.drawable.expander_ic_minimized,
						vibrate==0?R.drawable.btn_check_on_disable:R.drawable.btn_check_on,
								//						R.drawable.expander_ic_minimized
		};

		for(int i = 0;i<to.length;i++){
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("name", to[i]);
			user.put("bornSolar", from[i]);
			user.put("image", image[i]);
			users.add(user);
		}
		SimpleAdapter saImageItems = new SimpleAdapter(this,
				users,              // 数据来源
				R.layout.listview,  //相当ListView的一个组件 
				new String[] { "name", "bornSolar","image" },
				// 分别对应view 的id
				new int[] {  R.id.name, R.id.bornSolar,R.id.image });
		setlistview.setAdapter(saImageItems);
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent keyEvent){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			if(change>0){
				this.setResult(2);
			}
			this.finish();
			break;
		}
		return false;
	}
	@Override
	protected Dialog onCreateDialog(int id){
		Dialog resultDialog = null;
		switch(id){
		case 0:
			alarmStatus = (++alarmStatus)%2;
			putSharedPreferences("alarmStatus" , alarmStatus);
			updateListView();
			change++;
			break;
		case 1:
			//设置时间，创建TimePickerDialog
			resultDialog = new TimePickerDialog(this,callback, showHourOfDay, showMinute, true);
			break;
		case 2:
			//选择提前的天数，创建单选的AlertDialog
			setAlarmDays();
			break;
		case 3:
			doPickRingtone();
			break;
		case 4:
			vibrate = (++vibrate)%2;
			putSharedPreferences("vibrate" , vibrate);
			updateListView();
			break;
		}
		return resultDialog;

	}
	/** 
	 * 选择生日提醒提前的天数，可多选，创建多选的AlertDialog
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void setAlarmDays(){
		daysBooleanArray = BirthdayPreferences.getTypeBooleanArray(showDay);
		daysBooleanArrayCompare = daysBooleanArray.clone();
		//选择提前的天数，创建多选的AlertDialog
		final AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
		builder2.setIcon(R.drawable.ic_dialog_time)
		.setTitle(getString(R.string.string03))
		.setNegativeButton(getResources().getString(android.R.string.cancel), null)
		.setPositiveButton(getResources().getString(android.R.string.ok), 
				new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(BirthdayPreferences.isAllFalse(daysBooleanArray)){
					Toast.makeText(SetActivity.this, 
							getResources().getString(R.string.warnning06),
							Toast.LENGTH_LONG)
							.show();
				}
				if(!BirthdayPreferences.booleanEquals(daysBooleanArray,daysBooleanArrayCompare)){
					showDay = BirthdayPreferences.getTypeString(daysBooleanArray);
					putSharedPreferences("day" , showDay);
					change++;
					updateListView();	
				}
			}
		})
		.setMultiChoiceItems(BirthdayPreferences.DAY_STRING,
				daysBooleanArray,
				new OnMultiChoiceClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which, boolean isChecked) {
				// TODO Auto-generated method stub
				daysBooleanArray[which]= isChecked;
			}
		}
		);
		builder2.create().show();
	}


	/** 
	 * TimePickerDialog的回调函数
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private OnTimeSetListener callback = new OnTimeSetListener(){
		public void onTimeSet(TimePicker view ,int hourOfDay ,int minute){
			putSharedPreferences("hourOfDay", hourOfDay);
			putSharedPreferences("minute", minute);	
			change++;
			updateListView();
		}
	}; 
	/** 
	 * 打开铃声选择框选择铃声
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void doPickRingtone() {
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		// Allow user to pick 'Default'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
		// show 'Silent'
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
		// Show only ringtones
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
		Uri ringtoneUri = null;
		if ( null == mCustomRingtone || mCustomRingtone.isEmpty()) {
			ringtoneUri = null;//静音
		}
		else
		{
			ringtoneUri = Uri.parse(mCustomRingtone);
		}
		// Put checkmark next to the current ringtone for this contact
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, ringtoneUri);
		// Launch!
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 1: 
			// 选择完铃声之后获得选中铃音的URI,将其值存入数据库   //EXTRA_RINGTONE_PICKED_URI
			Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			handleRingtonePicked(pickedUri);
			break;
		}
	}
	/** 
	 * 将铃声的Uri保存在生日提醒参数中
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void handleRingtonePicked(Uri pickedUri) {
		if (pickedUri == null ) {
			mCustomRingtone = "";
		} else {
			mCustomRingtone = pickedUri.toString();
		}
		putSharedPreferences("mCustomRingtone", mCustomRingtone);
		updateListView();
	}
	/** 
	 * get Media's diaplay name via uri 
	 * @param  
	 * @return 返回音频文件的名字，若找不到返回空字符串
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String getMediaDisplayNameViaUriString(String uriString){
		String string = "";
		if(null == uriString || uriString.isEmpty()){
			//静音
			string = getResources().getString(R.string.silent);
		}
		else{
			Uri uri = Uri.parse(uriString);
			if(RingtoneManager.isDefault(uri)){
				//默认铃声
				string = getResources().getString(R.string.defaultRington);
			}
			else{
				Cursor c = managedQuery(uri,
						new String[] { MediaStore.Audio.Playlists._ID,MediaStore.Audio.Media.DISPLAY_NAME},
						null,
						null, 
						MediaStore.Audio.Playlists._ID);
				if (c!=null&&c.moveToFirst())
				{
					int cid=c.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
					string = c.getString(cid);
				}
			}
		}
		return string;
	}
	/** 
	 * Register a callback to be invoked when an item in this AdapterView has been clicked.
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			// TODO Auto-generated method stub
			Dialog tempDialog = onCreateDialog(position);
			if(null!=tempDialog){
				tempDialog.show();
			}
		}
	};
}
