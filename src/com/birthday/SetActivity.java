/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   
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
 * �����������Ѳ�����Activity�� �������Ƿ����������ӡ�����ʱ�䡢��ǰ���������������񶯡���ʾѡ�
 * 
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */ 
public class SetActivity extends Activity {
	private int showHourOfDay = 0,showMinute = 0, alarmStatus = 1;//showType = 0,
	private String mCustomRingtone = null;//����
	private String showDay;//���ѵ�����
	private int vibrate;//��
	private int change = 0;
	ListView setlistview = null;
	boolean daysBooleanArray[];
	boolean daysBooleanArrayCompare[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��������ı�Ĵ���״̬
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.setbirthday);
		//�����������ò��֡�
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title); 

		setlistview = (ListView)findViewById(R.id.ListViewSetBirtyday);
		setlistview.setCacheColorHint(getResources().getColor(R.color.listview_color));

		updateListView();//ˢ�½���
		setlistview.setOnItemClickListener(onItemClickListener);
	}
	/** 
	 * �õ�SharedPreferences���������Ѳ���������
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void getSharedPreferences(){
		BirthdayPreferences preferences = new BirthdayPreferences(this);
		showHourOfDay = preferences.getPreferences("hourOfDay", 9);				//�������ѵ�ʱ
		showMinute = preferences.getPreferences("minute", 0);					//�������ѵķ���
		showDay = preferences.getPreferences("day", "1");						//����������ǰ��������Ĭ��1��
		alarmStatus = preferences.getPreferences("alarmStatus", 1);				//�Ƿ������ӣ�Ĭ�Ͽ���
		mCustomRingtone = preferences.getPreferences("mCustomRingtone", null);  //���� 
		vibrate = preferences.getPreferences("vibrate", 1);						//�񶯣�Ĭ�Ͽ���
	}
	/** 
	 * ���޸ĵĲ������浽SharedPreferences
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
	 * ���޸ĵĲ������浽SharedPreferences
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
	 * ����listview�б�����
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
				alarmStatus==0?"�ѹر�":"�ѿ���",
						strDate,
						BirthdayPreferences.getTypeShowMessage(showDay),
						temp,
						vibrate==0?"�ѹر�":"�ѿ���",
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
				users,              // ������Դ
				R.layout.listview,  //�൱ListView��һ����� 
				new String[] { "name", "bornSolar","image" },
				// �ֱ��Ӧview ��id
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
			//����ʱ�䣬����TimePickerDialog
			resultDialog = new TimePickerDialog(this,callback, showHourOfDay, showMinute, true);
			break;
		case 2:
			//ѡ����ǰ��������������ѡ��AlertDialog
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
	 * ѡ������������ǰ���������ɶ�ѡ��������ѡ��AlertDialog
	 * @param  
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void setAlarmDays(){
		daysBooleanArray = BirthdayPreferences.getTypeBooleanArray(showDay);
		daysBooleanArrayCompare = daysBooleanArray.clone();
		//ѡ����ǰ��������������ѡ��AlertDialog
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
	 * TimePickerDialog�Ļص�����
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
	 * ������ѡ���ѡ������
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
			ringtoneUri = null;//����
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
			// ѡ��������֮����ѡ��������URI,����ֵ�������ݿ�   //EXTRA_RINGTONE_PICKED_URI
			Uri pickedUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
			handleRingtonePicked(pickedUri);
			break;
		}
	}
	/** 
	 * ��������Uri�������������Ѳ�����
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
	 * @return ������Ƶ�ļ������֣����Ҳ������ؿ��ַ���
	 * @exception  
	 * @see        
	 * @since     
	 */
	private String getMediaDisplayNameViaUriString(String uriString){
		String string = "";
		if(null == uriString || uriString.isEmpty()){
			//����
			string = getResources().getString(R.string.silent);
		}
		else{
			Uri uri = Uri.parse(uriString);
			if(RingtoneManager.isDefault(uri)){
				//Ĭ������
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
