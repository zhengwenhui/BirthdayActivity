/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��24��     ֣�Ļ�	   ������ũ���Ļ���ת�����Լ�������һ��ũ������������
 */
package com.birthday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;
/** 
 * ��ʾ��ϵ�������б��������Activity
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��24��
 */ 
public class BirthdayActivity extends TabActivity implements TabHost.TabContentFactory{
	/** Called when the activity is first created. */
	private BirthdaySQLite sqliteHelper = new BirthdaySQLite(this);
	private Cursor cursorSolar = null,cursorLundar = null,cursor = null;
	private final int SET_SHOW_ITEM = Menu.FIRST; 
	private final int SET_ITEM = Menu.FIRST+1;
	private final int INPUT_CONTACTS_ITEM = Menu.FIRST+2;
	private final int HELP_ITEM = Menu.FIRST+3;
	private ProgressDialog pDialog = null;
	private AutoCompleteTextView aTextView = null;
	public int showType = 2;
	//���ղ�������Ϣ
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case 0x111:
				TabHost th = getTabHost();
				addTab(th);//���Tab
				break;
			default:
				break;
			}
			
			/*int[] h;
			h = new int[]{
				1,2,3	
			};*/
			
		}
	};
	//�½��߳�ִ�в���͸�����ϵ�˵��������ĵĲ���
	private Thread thread = new Thread(){
		public void run(){
			CheckinContacts();//������ϵ��
			sqliteHelper.update();//������ϵ�˵�������Ϣ
			Message msg = new Message();
			msg.what = 0x111;
			pDialog.cancel();
			handler.sendMessage(msg );
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//���OptionsMenu����
		menu.add(0,SET_SHOW_ITEM,0,getString(R.string.birthdayList)).setIcon(R.drawable.ic_menu_allfriends);
		menu.add(0,SET_ITEM,0,getString(R.string.warnningSet)).setIcon(R.drawable.ic_menu_manage);
		menu.add(0,INPUT_CONTACTS_ITEM,0,getString(R.string.input)).setIcon(R.drawable.ic_menu_invite);
		menu.add(0,HELP_ITEM,0,getString(R.string.help)).setIcon(R.drawable.ic_menu_help);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case SET_SHOW_ITEM :
			setShowTypeDialog().show();//������ʾѡ��
			break;
		case SET_ITEM :
			//�������ѵĲ���
			Intent intent1 = new Intent(BirthdayActivity.this,SetActivity.class);
			startActivityForResult(intent1,1);	
			break;
		case INPUT_CONTACTS_ITEM :
			//������ϵ��
			int count = CheckinContacts();
			Toast.makeText(this, getString(R.string.string01)+count+getString(R.string.string02), Toast.LENGTH_SHORT).show();
			break;
		case HELP_ITEM :
			Intent intent3 = new Intent(BirthdayActivity.this,HelpActivity.class);
			startActivity(intent3);
			break;
		default :
			break;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pDialog = new ProgressDialog(this);
		pDialog.setTitle(getResources().getString(R.string.pdmessage));
		pDialog.show();
		thread.start();
	}  

	@Override
	public View createTabContent(String tag){
		showType = new BirthdayPreferences(this).getPreferences("type", 2);

		ListView listview = new ListView(this);
		listview.setCacheColorHint(getResources().getColor(R.color.listview_color));
		listview.setBackgroundDrawable(getResources().getDrawable(R.drawable.wallpaper));

		LinearLayout layout = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.search, null);
		aTextView = (AutoCompleteTextView)layout.findViewById(R.id.aTextView);
		ImageButton button = (ImageButton)layout.findViewById(R.id.aClear);
		button.setOnClickListener(clearClickListener);
		listview.addHeaderView(layout);
		listview.setOnItemClickListener(onItemClickListener);
		//    	listview.smoothScrollToPosition(2);
		if(tag.equals("Solar")){
			//���������б�
			if(null!=cursorSolar){
				cursorSolar.close();
			}
			cursorSolar = sqliteHelper.queryBirthdaySolar(showType+1);
			if(cursorSolar==null||cursorSolar.getCount()==0){
				Toast.makeText(BirthdayActivity.this, getString(R.string.warnning05), Toast.LENGTH_LONG).show();
			}
			SimpleCursorAdapter adapter = new CheckCursorAdapter(
					getApplicationContext(),
					R.layout.birthdaylist, 
					cursorSolar, 
					new String[]{"name","birthdaySolar","_id","bornLundarDate"}, 
					new int[]{R.id.name,R.id.birthday,R.id.dayslost,R.id.animalSign},
					0);
			aTextView.setAdapter(adapter);
			listview.setAdapter(adapter);
		}
		else{
			//ũ�������б�
			if(null!=cursorLundar){
				cursorLundar.close();
			}
			cursorLundar = sqliteHelper.queryBirthdayLundar(showType+1);
			if(cursorLundar==null||cursorLundar.getCount()==0){
				Toast.makeText(BirthdayActivity.this, getString(R.string.warnning04), Toast.LENGTH_LONG).show();
			}
			SimpleCursorAdapter adapter = new CheckCursorAdapter(
					getApplicationContext(),
					R.layout.birthdaylist, 
					cursorLundar, 
					new String[]{"name","birthdayLundar","_id","bornLundarDate"}, 
					new int[]{R.id.name,R.id.birthday,R.id.dayslost,R.id.animalSign},
					1);
			listview.setAdapter(adapter);
			aTextView.setAdapter(adapter);
		}
		return listview;
	} 
	/** 
	 * ����ѡ����ʾѡ���AlertDialog
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private AlertDialog setShowTypeDialog(){
		final AlertDialog.Builder builder3 = new AlertDialog.Builder(this);
		builder3.setIcon(R.drawable.ic_dialog_menu_generic)
		.setTitle(getString(R.string.birthdayList)).setSingleChoiceItems(
				BirthdayPreferences.TypeString, 
				showType,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						new BirthdayPreferences(BirthdayActivity.this).putPreferences("type" , which);
						dialog.cancel();
						updateTabHost();
					}
				});
		return builder3.create();
	}
	/** 
	 * ��ѯ���button�ĵ����ص�����
	 */
	OnClickListener clearClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			aTextView.setText("");
			updateTabHost();
		}
	};
	/** 
	 * �б���ĵ����Ļص�����
	 * Interface definition for a callback to be invoked when an item in this AdapterView has been clicked.
	 */
	OnItemClickListener onItemClickListener= new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			Bundle bundle = new Bundle();
			bundle.putString("_id", String.valueOf(id));
			Intent intentSet = new Intent(BirthdayActivity.this,SetBirthActivity.class);
			intentSet.putExtras(bundle);
			//����������ϵ��������ũ�����������գ��Ƿ��������������ѣ��Ƿ���ũ���������ѵ�Activity
			startActivityForResult(intentSet, 5);
		}
	};

	/** 
	 * ����������պ�ũ�����յ�Tab
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void addTab(TabHost th){
		TabWidget tabWidget1 = (TabWidget)getTabHost().getTabWidget().getChildAt(0);
		//ʵ����Tab��xml�����ļ�
		LinearLayout linearLayout1 = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.tabset,tabWidget1, false);
		th.addTab(
				th
				.newTabSpec("Solar")
				.setIndicator(linearLayout1)
				.setContent(this));

		TabWidget tabWidget2 = (TabWidget)getTabHost().getTabWidget().getChildAt(1);
		LinearLayout linearLayout2 = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.tabset,tabWidget2, false);
		//����Tab��icon
		ImageView iv2 = (ImageView)linearLayout2.getChildAt(0);
		iv2.setImageDrawable(getResources().getDrawable(R.drawable.ic_dialog_time2));
		//����Tab�� title
		TextView tx2 = (TextView)linearLayout2.getChildAt(1);
		tx2.setText(getString(R.string.birthdayLundar));
		th.addTab(
				th
				.newTabSpec("Lundar")
				.setIndicator(linearLayout2)
				.setContent(this));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		sqliteHelper.close();
		if(null!=cursorLundar){
			cursorLundar.close();
		}
		if(null!=cursorSolar){
			cursorSolar.close();
		}
	}
	@Override
	protected void onActivityResult(int requestCode,int resultCode,Intent data){
		switch(resultCode){  	
		case 1:{
			//�޸Ĳ�Ӱ�������������ӣ�ֻ����TabHost��
			updateTabHost();//����TabHost��
			break;
		}
		case 2:{
			//�޸�Ӱ�������������ӣ��������������������Ӳ�����TabHost��
			updateTabHost();
			new SetBirthdayAlarm(this).setBirthdayAlarm();//��������������������
			break;
		}
		default:
			break;
		}
	}
	/** 
	 * ����TabHost
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void updateTabHost() {
		TabHost th = getTabHost();
		int tab = th.getCurrentTab();
		th.setCurrentTab(0);
		th.clearAllTabs();
		addTab(th);
		if(1==tab){
			th.setCurrentTab(1);
		}
	}
	/** 
	 * ��Contacts������ϵ��������¼�������ݿ����û�е���ϵ������insert��
	 * @param
	 * @return ���ز������ݿ������ϵ�˵���Ŀ
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int CheckinContacts(){
		if(sqliteHelper.query().getCount()<=0){
			//���ݿ���û����Ϣ���״�ʹ�ã�����������Ϣ��
			Intent intent4 = new Intent(BirthdayActivity.this,HelpActivity.class);
			startActivity(intent4);
		}
		ContentResolver resolver = getContentResolver();
		if(null!=cursor){
			cursor.close();
		}
		//��ѯ��ϵ����Ϣ
		cursor = resolver.query(
				ContactsContract.Contacts.CONTENT_URI,
				new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME,"sort_key"}, 
				null,
				null, 
				null);
		
		int count = 0;
		String name;
		String _id;
		String sort_key;
		while(cursor.moveToNext()){
			name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
			sort_key = cursor.getString(cursor.getColumnIndex("sort_key"));
			if(sqliteHelper.insert(_id,name,sort_key)>=0){
				count++;
			}
		}
		cursor.close();
		synchronizationContacts();
		return count;
	} 

	/** 
	 * �������ڱ������У�����������ͨѶ¼�е���ϵ�˼�¼ɾ��
	 * @param
	 * @return
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int synchronizationContacts(){
		int counts = 0;
		String idInBirth;
		Cursor cursorContacts = null;
		Cursor cursorBirth = sqliteHelper.query();
		ContentResolver resolver = getContentResolver();
		while(cursorBirth.moveToNext()){
			idInBirth = cursorBirth.getString(cursorBirth.getColumnIndex("_id"));
			cursorContacts = resolver.query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[]{ContactsContract.Contacts._ID}, 
					"_id = ?",
					new String[]{idInBirth}, 
					null);
			if(null == cursorContacts ||0 == cursorContacts.getCount()){
				sqliteHelper.delete(idInBirth);//ɾ����¼
				counts++;
			}
			cursorContacts.close();
		}
		cursorBirth.close();
		return counts;
	} 

	/** 
	 * �̳���SimpleCursorAdapter���ࡣ
	 * @param
	 * @author ֣�Ļ�
	 * @Date 2011��10��24��
	 */
	public class CheckCursorAdapter extends SimpleCursorAdapter{
		private final Context context;
		private Cursor cursor;
		private QuickContactBadge badge;
		private TextView textView;
		private int flag = 0;
		public CheckCursorAdapter(Context context,int layout,Cursor c,String[] from, int[] to,int flag) {
			super(context, layout, c, from, to);
			cursor = c;
			this.context = context;
			this.flag = flag;
		}
		@Override 
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) { 
			//��ѯ�����к���������constraint����ϵ��
			if (constraint != null&&!String.valueOf(constraint).isEmpty()) { 
				StringBuilder sBuilder = new StringBuilder();
				sBuilder.append("sort_key like \'");
				for(int i = 0;i<constraint.length();i++){
					sBuilder.append("%");
					sBuilder.append(constraint.charAt(i));
				}
				sBuilder.append("%\'");
				cursor = sqliteHelper.querySearch(sBuilder.toString());  
			} 
			return cursor;
		}
		//    @Override 
		//    public String convertToString(Cursor cursor) { 
		//        return cursor.getString(cursor.getColumnIndex("name")); 
		//    } 
		@Override
		public void bindView(View view, Context context, Cursor cursor){
			super.bindView(view, context, cursor);
			textView = (TextView)view.findViewById(R.id.birthday);
			//��������ʣ����������Ϣ��
			String string = getDaysLostByBirthday(textView.getText().toString());
			textView  = (TextView)view.findViewById(R.id.dayslost);
			textView.setText(string);

			badge = (QuickContactBadge)view.findViewById(R.id.animalSign);
			
			Uri uri = getLookupUriById(cursor.getString(cursor.getColumnIndex("_id")));

			badge.assignContactUri(uri);
			Drawable drawable = getDrawableByDateForBadge(cursor,flag);
			badge.setImageDrawable(drawable);
		}
		/** 
		 * ������ϵ�˵�_id�õ�LookupUri��
		 * @param _id ��ϵ�����ݿ��е�_id,��ContactsContract.Contacts._ID
		 * @return LookupUri
		 * @exception  
		 * @see        
		 * @since     
		 */
		private Uri getLookupUriById(String _id){
			Uri uri = null;
			//����_id��ϵ�˵�LOOKUP_KEY
			Cursor cursor = context.getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[]{ContactsContract.Contacts._ID,ContactsContract.Contacts.LOOKUP_KEY}, 
					ContactsContract.Contacts._ID+"=?", 
					new String[]{_id}, 
					null);     
			if(cursor.moveToNext()){    
				String lookupKey = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
				//Build a CONTENT_LOOKUP_URI lookup Uri using the given _ID and LOOKUP_KEY. 
				uri = ContactsContract.Contacts.getLookupUri(Long.valueOf(_id), lookupKey);
			}
			cursor.close();
			return uri;
		}
		/** 
		 * ������ϵ�˵ĳ������ڵõ���ʾ��ͼƬ������Ϊ����ͼƬ��ũ��Ϊ�����ͼƬ����
		 * @param flag Ϊ0��������Ϊ1��ũ����
		 * @return 
		 * @exception  
		 * @see        
		 * @since     
		 */
		private Drawable getDrawableByDateForBadge(Cursor cursor,int flag) {
			Drawable drawable = null;
			int asIndex = 12;
			String date;
			switch (flag) {
			case 0:
				//�������õ�������ͼƬ
				date = cursor.getString(cursor.getColumnIndex("bornSolar"));
				if(date!=null&&11==date.length()){
					int monthAndDay = Integer.valueOf(date.substring(5, 7)+date.substring(8, 10));
					int cc[] ={119,218,320,420,520,621,722,822,922,1023,1121,1221};//ÿ�������ĵ�һ������ں���λ��ʾ�գ�ǰ��ı�ʾ�£�
					int i;
					for(i= 0;i<12&&monthAndDay>cc[i];i++){}
					drawable = getResources().getDrawable(BirthdayPreferences.CONSTELLATION_SIGN[i%12]);
				}
				else{
					//��ʾĬ�ϵ�ͼƬ
					drawable = getResources().getDrawable(R.drawable.default_sign);
				}
				break;
			case 1:
				//ũ�����õ������ͼƬ
				date = cursor.getString(cursor.getColumnIndex("bornLundarDate"));
				if(date!=null&&date.length()>=7){
					asIndex = Integer.valueOf(date.substring(0, 4));
					asIndex = (asIndex - 4) % 12;
					drawable = getResources().getDrawable(BirthdayPreferences.ANIMAL_SIGN[asIndex]);
				}
				else{
					//��ʾĬ�ϵ�ͼƬ
					drawable = getResources().getDrawable(R.drawable.default_sign);
				}
				break;
			default:
				break;
			}
			return drawable;
		}
		/** 
		 * �������յ����ں͵�ǰ�����ڱȽϣ��õ�ʣ��������Ϣ��
		 * @param birthday ��������
		 * @return ʣ��������Ϣ
		 * @exception  
		 * @see        
		 * @since     
		 */
		private String getDaysLostByBirthday(String birthday) {
			String string = "";
			if(birthday.length()>0){
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy��MM��dd��");
				try {
					Date birthdayDate = sDateFormat.parse(String.valueOf(birthday));
					Date currentDate = new Date();//��ǰ������
					long millisecondsOfOneDay = 1000*60*60*24;//һ��ĺ�����
					long daysLost = ((birthdayDate.getTime()-currentDate.getTime()+millisecondsOfOneDay)/(millisecondsOfOneDay));
					if(0==daysLost){
						//����
						string = getString(R.string.today);
					}
					else if(1==daysLost){
						//����
						string = getString(R.string.tomorrow);
					}
					else if(daysLost>1){
						//day���
						string = daysLost+getString(R.string.string12);
					}

				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return string;
		}
	}
}
