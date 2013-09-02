/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   
 */
package com.birthday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
/** 
 * ������ϵ�˵������������ڡ�ũ���������ڡ��Ƿ����������������Ѻ��Ƿ���ũ���������ѵ��ࡣ
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */
public class SetBirthActivity extends Activity {
	public BirthdaySQLite sqlitebirthday = new BirthdaySQLite(this);
	private String bornSolar = null;
	private String bornLundar = null;
	private int warningSolar = 0;
	private int warningLundar = 0;
	private int change = 0;
	private Cursor cursor = null;
	public String _id  = null;
	private ListView setlistview = null;
	private TextView title  = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Ԥ����������ı�Ĵ���״̬������ setContentView ֮ǰ���ã��������ñ���ʱ������ʱ����
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE); 
		setContentView(R.layout.setbirthday);
		//����title�Ĳ���
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title);
		title = (TextView)getWindow().findViewById(R.id.title_text);

		setlistview = (ListView)findViewById(R.id.ListViewSetBirtyday);
		setlistview.setCacheColorHint(getResources().getColor(R.color.listview_color));
		updateListView();
		//Register a callback to be invoked when an item in this AdapterView has been clicked.
		setlistview.setOnItemClickListener(onItemClickListener);	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sqlitebirthday.close();
		if(null!=cursor){
			cursor.close();
			//�ر�cursor
		}
	}
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent keyEvent){
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			if(change>0){
				//��������������Ҫ�������á�
				SetBirthActivity.this.setResult(2);
			}
			SetBirthActivity.this.finish();
			break;
		}
		return false;
	}

	@Override
	protected Dialog onCreateDialog(int id){
		Dialog resultDialog = null;
		switch(id){
		case 0:{
			//����������������������
			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
			try {
				Date date;
				date = sDateFormat.parse(bornSolar);
				resultDialog = new DatePickerDialog(this, 
						callback,
						date.getYear()+1900,
						date.getMonth(),
						date.getDate());

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				resultDialog = new DatePickerDialog(this, callback,1990,0,1);
				e.printStackTrace();
			}

			break;
		}
		case 1:{
			//����ũ����������������
			String[] str = bornLundar.split("/");
			LundarDateDialog lundarDateDialog;
			if(str.length<3){
				lundarDateDialog = new LundarDateDialog(this,1990, 1, 1);
			}
			else{
				lundarDateDialog = new LundarDateDialog(this,
						Integer.valueOf(str[0]),
						Integer.valueOf(str[1]),
						Integer.valueOf(str[2]));
			}			
			lundarDateDialog.Create();
			change++;
			break;
		}
		case 2:{
			//�������������Ƿ���ʾ
			sqlitebirthday.update(_id, "warningSolar", (warningSolar+1)%2);
			change++;
			updateListView();//ˢ�½���
			break;
		}
		case 3:{
			//����ũ�������Ƿ���ʾ
			sqlitebirthday.update(_id, "warningLundar", (warningLundar+1)%2);
			change++;
			updateListView();//ˢ�½���
			break;
		}
		}
		return resultDialog;
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		switch(resultCode){  	
		case 7:{
			//����ũ����������
			Bundle bundle = data.getExtras();
			sqlitebirthday.updateLundarBirth(_id, 
					Integer.valueOf(bundle.getString("year")),
					Integer.valueOf(bundle.getString("month")), 
					Integer.valueOf(bundle.getString("day")));
			change++;
			updateListView();//ˢ�½���
			break;
		}
		default:
			break;
		}
	}
	/** 
	 * DatePickerDialog�Ļص�����
	 * @param birthday 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private OnDateSetListener callback = new OnDateSetListener(){
		public void onDateSet(DatePicker view,int year ,int monthOfYear ,int dayOfMonth){
			//�������ݿ�
			sqlitebirthday.updateSolarBirth(_id, year, monthOfYear+1, dayOfMonth);
			change++;
			updateListView();//ˢ�½���
		}
	};
	/** 
	 * ����listview�б�
	 * @param birthday 
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void updateListView(){
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		if(cursor!=null){
			cursor.close();
		}
		//�������ݿ��и���ϵ�˵ļ�¼
		cursor = sqlitebirthday.queryById(bundle.get("_id").toString());
		if(!cursor.moveToFirst()){
			Toast.makeText(this, getString(R.string.warnning03), Toast.LENGTH_SHORT).show();
			return;
		}

		bornSolar = cursor.getString(cursor.getColumnIndex("bornSolar"));
		bornLundar = cursor.getString(cursor.getColumnIndex("bornLundarDate"));

		_id = cursor.getString(cursor.getColumnIndex("_id"));
		warningSolar = cursor.getInt(cursor.getColumnIndex("warningSolar"));
		warningLundar = cursor.getInt(cursor.getColumnIndex("warningLundar"));

		String from[] = new String[]{
				cursor.getString(cursor.getColumnIndex("bornSolar")),
				cursor.getString(cursor.getColumnIndex("bornLundar")),
				cursor.getString(cursor.getColumnIndex("birthdaySolar")),
				cursor.getString(cursor.getColumnIndex("birthdayLundar"))
				};

		String to[] = new String[]{
				getString(R.string.string07),
				getString(R.string.string08),
				getString(R.string.string09),
				getString(R.string.string10)
				};

		int image[] = new int[]{
				R.drawable.expander_ic_minimized,
				R.drawable.expander_ic_minimized,
				0==warningSolar?R.drawable.btn_check_on_disable:R.drawable.btn_check_on,
				0==warningLundar?R.drawable.btn_check_on_disable:R.drawable.btn_check_on,
				};
		
		ArrayList<HashMap<String, Object>> users = new ArrayList<HashMap<String, Object>>();
		
		for(int i = 0;i<to.length;i++){
			HashMap<String, Object> user = new HashMap<String, Object>();
			user.put("name", to[i]);
			user.put("bornSolar", from[i]);
			user.put("image", image[i]);
			users.add(user);
		}
		
		SimpleAdapter saImageItems = new SimpleAdapter(this,
				users,             // ������Դ
				R.layout.listview, //ÿһ��user xml �൱ListView��һ����� 
				new String[] { "name", "bornSolar" ,"image"},       // �ֱ��Ӧview ��id
				new int[] {  R.id.name, R.id.bornSolar,R.id.image});// R.id.image

		title.setText(cursor.getString(cursor.getColumnIndex("name")));
		setlistview.setAdapter(saImageItems);

//		if(cursor.getString(cursor.getColumnIndex("bornSolar")).length()<2){
//			Toast.makeText(this, getString(R.string.warnning02), Toast.LENGTH_LONG).show();
//		}
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
