/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月24日     郑文辉	   阳历和农历的互相转换，以及计算下一个农历和阳历生日
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
 * 显示联系人生日列表，程序的主Activity
 * @param
 * @author 郑文辉
 * @Date   2011年10月24日
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
	//接收并处理消息
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case 0x111:
				TabHost th = getTabHost();
				addTab(th);//添加Tab
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
	//新建线程执行插入和更新联系人的生日信心的操作
	private Thread thread = new Thread(){
		public void run(){
			CheckinContacts();//插入联系人
			sqliteHelper.update();//更新联系人的生日信息
			Message msg = new Message();
			msg.what = 0x111;
			pDialog.cancel();
			handler.sendMessage(msg );
		}
	};
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		//添加OptionsMenu的项
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
			setShowTypeDialog().show();//设置显示选项
			break;
		case SET_ITEM :
			//设置提醒的参数
			Intent intent1 = new Intent(BirthdayActivity.this,SetActivity.class);
			startActivityForResult(intent1,1);	
			break;
		case INPUT_CONTACTS_ITEM :
			//导入联系人
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
			//阳历生日列表
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
			//农历生日列表
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
	 * 创建选择显示选项的AlertDialog
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
	 * 查询清空button的单击回调函数
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
	 * 列表项的单击的回调函数
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
			//开启设置联系人阳历和农历出生年月日，是否开启阳历生日提醒，是否开启农历生日提醒的Activity
			startActivityForResult(intentSet, 5);
		}
	};

	/** 
	 * 添加阳历生日和农历生日的Tab
	 * @param
	 * @return 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void addTab(TabHost th){
		TabWidget tabWidget1 = (TabWidget)getTabHost().getTabWidget().getChildAt(0);
		//实例化Tab的xml布局文件
		LinearLayout linearLayout1 = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.tabset,tabWidget1, false);
		th.addTab(
				th
				.newTabSpec("Solar")
				.setIndicator(linearLayout1)
				.setContent(this));

		TabWidget tabWidget2 = (TabWidget)getTabHost().getTabWidget().getChildAt(1);
		LinearLayout linearLayout2 = (LinearLayout)LayoutInflater.from(this).inflate(R.layout.tabset,tabWidget2, false);
		//设置Tab的icon
		ImageView iv2 = (ImageView)linearLayout2.getChildAt(0);
		iv2.setImageDrawable(getResources().getDrawable(R.drawable.ic_dialog_time2));
		//设置Tab的 title
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
			//修改不影响生日提醒闹钟，只更新TabHost；
			updateTabHost();//更新TabHost；
			break;
		}
		case 2:{
			//修改影响生日提醒闹钟，重新设置生日提醒闹钟并更新TabHost；
			updateTabHost();
			new SetBirthdayAlarm(this).setBirthdayAlarm();//重新设置生日提醒闹钟
			break;
		}
		default:
			break;
		}
	}
	/** 
	 * 更新TabHost
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
	 * 从Contacts导出联系人姓名记录，将数据库表中没有的联系人姓名insert。
	 * @param
	 * @return 返回插入数据库表中联系人的数目
	 * @exception  
	 * @see        
	 * @since     
	 */
	private int CheckinContacts(){
		if(sqliteHelper.query().getCount()<=0){
			//数据库中没有信息，首次使用，弹出帮助信息。
			Intent intent4 = new Intent(BirthdayActivity.this,HelpActivity.class);
			startActivity(intent4);
		}
		ContentResolver resolver = getContentResolver();
		if(null!=cursor){
			cursor.close();
		}
		//查询联系人信息
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
	 * 将存在于本程序中，但不存在于通讯录中的联系人记录删除
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
				sqliteHelper.delete(idInBirth);//删除记录
				counts++;
			}
			cursorContacts.close();
		}
		cursorBirth.close();
		return counts;
	} 

	/** 
	 * 继承于SimpleCursorAdapter的类。
	 * @param
	 * @author 郑文辉
	 * @Date 2011年10月24日
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
			//查询姓名中含有子序列constraint的联系人
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
			//计算生日剩余天数的信息；
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
		 * 根据联系人的_id得到LookupUri。
		 * @param _id 联系人数据库中的_id,即ContactsContract.Contacts._ID
		 * @return LookupUri
		 * @exception  
		 * @see        
		 * @since     
		 */
		private Uri getLookupUriById(String _id){
			Uri uri = null;
			//查找_id联系人的LOOKUP_KEY
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
		 * 根据联系人的出生日期得到显示的图片（阳历为星座图片，农历为属相的图片）。
		 * @param flag 为0：阳历；为1：农历；
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
				//阳历，得到星座的图片
				date = cursor.getString(cursor.getColumnIndex("bornSolar"));
				if(date!=null&&11==date.length()){
					int monthAndDay = Integer.valueOf(date.substring(5, 7)+date.substring(8, 10));
					int cc[] ={119,218,320,420,520,621,722,822,922,1023,1121,1221};//每种星座的第一天的日期后两位表示日，前面的表示月；
					int i;
					for(i= 0;i<12&&monthAndDay>cc[i];i++){}
					drawable = getResources().getDrawable(BirthdayPreferences.CONSTELLATION_SIGN[i%12]);
				}
				else{
					//显示默认的图片
					drawable = getResources().getDrawable(R.drawable.default_sign);
				}
				break;
			case 1:
				//农历，得到属相的图片
				date = cursor.getString(cursor.getColumnIndex("bornLundarDate"));
				if(date!=null&&date.length()>=7){
					asIndex = Integer.valueOf(date.substring(0, 4));
					asIndex = (asIndex - 4) % 12;
					drawable = getResources().getDrawable(BirthdayPreferences.ANIMAL_SIGN[asIndex]);
				}
				else{
					//显示默认的图片
					drawable = getResources().getDrawable(R.drawable.default_sign);
				}
				break;
			default:
				break;
			}
			return drawable;
		}
		/** 
		 * 根据生日的日期和当前的日期比较，得到剩余天数信息。
		 * @param birthday 生日日期
		 * @return 剩余天数信息
		 * @exception  
		 * @see        
		 * @since     
		 */
		private String getDaysLostByBirthday(String birthday) {
			String string = "";
			if(birthday.length()>0){
				SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
				try {
					Date birthdayDate = sDateFormat.parse(String.valueOf(birthday));
					Date currentDate = new Date();//当前的日期
					long millisecondsOfOneDay = 1000*60*60*24;//一天的毫秒数
					long daysLost = ((birthdayDate.getTime()-currentDate.getTime()+millisecondsOfOneDay)/(millisecondsOfOneDay));
					if(0==daysLost){
						//今天
						string = getString(R.string.today);
					}
					else if(1==daysLost){
						//明天
						string = getString(R.string.tomorrow);
					}
					else if(daysLost>1){
						//day天后
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
