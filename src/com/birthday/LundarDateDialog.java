/* 
 * Copyright (C) 2006 深圳隆宇世纪科技有限公司. 
 * 
 * 本系统是商用软件,未经授权擅自复制或传播本程序的部分或全部将是非法的. 
 * 
 * Date          Author      Description 
 * 2011年10月19日     郑文辉	             数据库的帮助类
 */
package com.birthday;

import java.text.DecimalFormat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
/** 
 * 农历日期的输入框的Dialog类，类似与DatePicker 
 * 允许输入一九零一年到二零九九年的日期
 * @param
 * @author 郑文辉
 * @Date   2011年10月21日
 */ 
public class LundarDateDialog{

	private static final int MIN_YEAR = SolarAndLundar.MIN_YEAR;
	private static final int MAX_YEAR = SolarAndLundar.MAX_YEAR;
	private static final int MILLISECONDS_THREAD = 200;
	private static final int MESSAGE_WHAT_LONGCLICK = 0x121;

	private int mYear,mMonth,mDay;
	private AlertDialog dialog;

	private EditText yearEditText = null;
	private EditText monthEditText = null;
	private EditText dayEditText = null;

	private Button minusDayButton = null;
	private Button plusDayButton = null;
	private Button minusMonthButton = null;
	private Button plusMonthButton = null;
	private Button minusYearButton = null;
	private Button plusYearButton = null;

	private DecimalFormat decimalformat = new DecimalFormat("00");
	private SolarAndLundar sl = new SolarAndLundar();
	private BirthdaySQLite sqlitebirthday;
	private final Context mContext;
	private int change = 0;
	private boolean leapmonth = false;

	private boolean longClicking = false;  //是否正在长按状态下
	private View longClickView = null; 	   
	private Thread longClickThread = null;
	/** 
	 * 构造器，默认的日期为1990年1月1日
	 * @param      
	 * @exception  
	 * @see        
	 * @since     
	 */
	public LundarDateDialog(Context context) {
		// TODO Auto-generated constructor stub
		mContext = context;
		sqlitebirthday = new BirthdaySQLite(context);
		mYear = 1990;
		mMonth = 1;
		mDay = 1;
	}
	/** 
	 * 构造器 ；
	 * @param 设置初始的日期为 year年month月day日 ； 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public LundarDateDialog(Context context,int year,int month ,int day) {
		// TODO Auto-generated constructor stub
		mContext = context;
		sqlitebirthday = new BirthdaySQLite(context);
		mYear = year;
		mMonth = month;
		mDay = day;
	}
	/** 
	 * 创建并显示一个用于输入和显示农历日期的对话框；
	 * @param 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void Create() {
		@SuppressWarnings("static-access")
		LayoutInflater inflater = ((SetBirthActivity)mContext).getLayoutInflater().from(mContext);
		final View dialogView = inflater.inflate(R.layout.lundardate, null);
		//根据id查找view
		plusYearButton  = (Button) dialogView.findViewById(R.id.plusYear);
		minusYearButton = (Button) dialogView.findViewById(R.id.minusYear);
		plusMonthButton  = (Button) dialogView.findViewById(R.id.plusMonth);
		minusMonthButton = (Button) dialogView.findViewById(R.id.minusMonth);	
		plusDayButton  = (Button) dialogView.findViewById(R.id.plusDay);
		minusDayButton = (Button) dialogView.findViewById(R.id.minusDay);
		//为Button添加监听器
		setButtonListeners(plusYearButton);
		setButtonListeners(minusYearButton);
		setButtonListeners(plusMonthButton);
		setButtonListeners(minusMonthButton);
		setButtonListeners(plusDayButton);
		setButtonListeners(minusDayButton);

		yearEditText = (EditText)dialogView.findViewById(R.id.year);
		monthEditText = (EditText)dialogView.findViewById(R.id.month);
		dayEditText = (EditText)dialogView.findViewById(R.id.day);
		//为日期赋值
		yearEditText.setText(String.valueOf(mYear));
		monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth))); 
		dayEditText.setText(decimalformat.format(mDay));
		//添加焦点变化的响应
		yearEditText.setOnFocusChangeListener(foucusChangeListener);
		monthEditText.setOnFocusChangeListener(foucusChangeListener);
		dayEditText.setOnFocusChangeListener(foucusChangeListener);
		//添加EditText内容变化的响应
		yearEditText.addTextChangedListener(yearTextWatcher);
		monthEditText.addTextChangedListener(monthTextWatcher);
		dayEditText.addTextChangedListener(dayTextWatcher);

		//创建对话框并显示
		dialog = createLunarDateDialog(dialogView);
		dialog.show();
	}
	/** 
	 * 为传入的Button添加监听器，包括onLongClickListener，onTouchListener，onKeyListener。
	 * @param 
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void setButtonListeners(Button button){
		button.setOnLongClickListener(onLongClickListener);
		button.setOnTouchListener(onTouchListener);
		button.setOnKeyListener(onKeyListener);
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			// TODO Auto-generated method stub
			switch (message.what) {
			case MESSAGE_WHAT_LONGCLICK:
				click.onClick(longClickView);
				break;
			default:
				break;
			}
		}
	};

	//得到一个新的线程
	private Thread getNewThread(){
		Thread thread = new Thread(){
			public void run(){
				while(longClicking){
					try {
						Message message = new Message();
						message.what = MESSAGE_WHAT_LONGCLICK;
						handler.sendMessage(message);
						Thread.sleep(MILLISECONDS_THREAD);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		return thread;
	}

	//a callback to be invoked when a view has been clicked and held.
	private OnLongClickListener onLongClickListener = new OnLongClickListener() {
		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			longClicking = true;
			v.requestFocusFromTouch();
			longClickThread = getNewThread();
			longClickThread.start();
			longClickView = v;
			return true;
		}
	};
	//响应物理按键事件
	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			boolean result = false;
			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_CENTER:
			case KeyEvent.KEYCODE_ENTER:
				if (KeyEvent.ACTION_UP == event.getAction()) {
					if(!longClicking){
						requestEditTextFocus(v);
						click.onClick(v);
					}
					else{
						longClicking = false;
						v.clearFocus();
						v.removeCallbacks(longClickThread);  
					}
					result = true;
				}
			}
			return result;
		}
	};
	//响应触摸事件
	private OnTouchListener onTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				if(!longClicking){
					requestEditTextFocus(v);
					click.onClick(v);
				}
				else{
					longClicking = false;
					v.clearFocus();
					v.removeCallbacks(longClickThread);  
				}
				break;
			default:
				break;
			}
			return false;
		}
	};
	//焦点变换时的处理
	private OnFocusChangeListener foucusChangeListener = new OnFocusChangeListener() {
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			int temp = 0;
			if(!hasFocus){
				switch (v.getId()) {
				case R.id.year:
					if(yearEditText.getText().toString().length()>=4){
						temp = Integer.valueOf((yearEditText.getText().toString()));

						mMonth += adjustment(mYear, mMonth);

						switch(sl.check(temp, mMonth, mDay)){
						case -1:
							break;
						case -3:
							mYear = temp;
							mMonth=12;
							monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
							break;
						case -5:
							mYear = temp;
							mDay=29;
							dayEditText .setText(decimalformat.format(mDay));
							break;
						default:
							mYear = temp;
							break;
						}
						mMonth -= adjustment(mYear, mMonth);
					}
					yearEditText.setText(String.valueOf(mYear));
					dialog.setTitle(sl.lunarYear(mYear, mMonth, mDay));
					break;
				case R.id.month:
					if(monthEditText.getText().toString().length()>=1){
						temp = Integer.valueOf((monthEditText.getText().toString()));//////////////////
						if(0!=temp){
							if(leapmonth){
								temp +=1;
							}else{
								temp -= adjustment(mYear, temp);
							}	
						}
						switch(sl.check(mYear, temp, mDay)){
						case -2: break;
						case -5:
							mMonth = temp;
							mDay=29;
							dayEditText .setText(decimalformat.format(mDay));
							break;
						default:
							mMonth = temp;
							break;
						}
					}
					monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
					dialog.setTitle(sl.lunarYear(mYear, mMonth, mDay));
					break;
				case R.id.day:
					if(dayEditText.getText().toString().length()>=1){
						temp = Integer.valueOf((dayEditText.getText().toString()));
						if(0!=temp){
							mDay = temp;
						}
					}	
					dayEditText.setText(decimalformat.format(mDay));
					dialog.setTitle(sl.lunarYear(mYear, mMonth, mDay));
					break;
				default:
					break;
				}
			}
		}
	};
	/** 
	 *  改变EditText的焦点
	 * @param      
	 * @return   
	 * @exception  
	 * @see        
	 * @since     
	 */
	private void requestEditTextFocus(View v){
		switch (v.getId()) {
		case R.id.plusYear:
			if(yearEditText.hasFocus()){
				String temp = yearEditText.getText().toString();
				if(temp.length()==4){
					int m = Integer.valueOf(temp);
					if(m>=MIN_YEAR&&m<=MAX_YEAR){
						mYear = m;
					}
				}
			}
			else{
				yearEditText.requestFocus();
			}
			break;
		case R.id.minusYear:
			if(yearEditText.hasFocus()){
				String temp = yearEditText.getText().toString();
				if(temp.length()==4){
					int m = Integer.valueOf(temp);
					if(m>=MIN_YEAR&&m<=MAX_YEAR){
						mYear = m;
					}
				}
			}
			else{
				yearEditText.requestFocus();
			}
			break;
		case R.id.plusMonth:
			if(monthEditText.hasFocus()){
				String temp = monthEditText.getText().toString();
				if(temp.length()>0){
					int m = Integer.valueOf(temp);
					if(0!=m&&m!=(mMonth+adjustment(mYear,mMonth))){
						mMonth = m;
					}
				}
			}
			else{
				monthEditText.requestFocus();
			}
			break;
		case R.id.minusMonth:
			if(monthEditText.hasFocus()){
				String temp = monthEditText.getText().toString();
				if(temp.length()>0){
					int m = Integer.valueOf(temp);
					if(0!=m&&m!=(mMonth+adjustment(mYear,mMonth))){
						mMonth = m;
					}
				}
			}
			else{
				monthEditText.requestFocus();
			}
			break;
		case R.id.plusDay:
			if(dayEditText.hasFocus()){
				String temp = dayEditText.getText().toString();
				if(temp.length()>0){
					int m = Integer.valueOf(temp);
					if(0!=m){
						mDay = m;
					}
				}
			}
			else{
				dayEditText.requestFocus();
			}
			break;
		case R.id.minusDay:
			if(dayEditText.hasFocus()){
				String temp = dayEditText.getText().toString();
				if(temp.length()>0){
					int m = Integer.valueOf(temp);
					if(0!=m){
						mDay = m;
					}
				}
			}
			else{
				dayEditText.requestFocus();
			}
			break;
		default:
			break;
		}
	}
	/** 
	 *  判断给定的农历y年m月是不是闰月
	 * @param      
	 * @return    如果是闰月返回true，其他情况返回false;
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	boolean isLeapMonth(int y,int m){
		boolean result = false;
		int lm = sl.getLeapMonthOfLunarYear(y);
		if( lm>0&&lm==(m-1)){
			result = true;
		}
		return result;
	}
	/** 
	 * @param      
	 * @return    农历日期y年有闰月且m月大于闰月，返回-1，其他返回0；
	 * @exception  
	 * @see        
	 * @since     
	 */
	int adjustment(int y,int m){
		leapmonth = isLeapMonth(y, m);
		int result = 0;
		int temp = sl.getLeapMonthOfLunarYear(mYear);
		if(temp>0&&temp<m){
			result = -1;
		}
		return result;
	}

	private OnClickListener click = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch(v.getId()){
			case R.id.plusYear :
				mMonth += adjustment(mYear, mMonth);
				switch(sl.check(mYear+1, mMonth, mDay)){
				case 1:mYear++;break;
				case -3:
					mYear++;
					mMonth=12;
					monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
					break;
				case -5:
					mYear++;
					mDay=29;
					dayEditText .setText(decimalformat.format(mDay));
					break;
				default:
					mYear = MIN_YEAR;
					break;
				}
				mMonth -= adjustment(mYear, mMonth);
				yearEditText.setText(String.valueOf(mYear));
				break;
			case R.id.plusMonth :
				switch(sl.check(mYear, mMonth+1, mDay)){
				case 1:mMonth++;break;
				case -5:
					mMonth++;
					mDay=29;
					dayEditText .setText(decimalformat.format(mDay));
					break;
				default:
					mMonth = 1;
					break;
				}
				monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
				break;
			case R.id.plusDay :
				if(1==sl.check(mYear, mMonth, mDay+1)){
					mDay++;
				}
				else mDay = 1;
				dayEditText .setText(decimalformat.format(mDay));
				break;
			case R.id.minusYear :
				mMonth += adjustment(mYear, mMonth);
				switch(sl.check(mYear-1, mMonth, mDay)){
				case 1:mYear--;break;
				case -3:
					mYear--;
					mMonth=12;
					monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
					break;
				case -5:
					mYear--;
					mDay=29;
					dayEditText .setText(decimalformat.format(mDay));
					break;
				default:
					mYear = MAX_YEAR;
					break;
				}
				mMonth -= adjustment(mYear, mMonth);
				yearEditText.setText(String.valueOf(mYear));
				break;
			case R.id.minusMonth :
				switch(sl.check(mYear, mMonth-1, mDay)){
				case 1:mMonth--;break;
				case -5:
					mMonth--;
					mDay=29;
					dayEditText .setText(decimalformat.format(mDay));
					break;
				default:
					mMonth = sl.getLeapMonthOfLunarYear(mYear)==0?12:13;
					break;
				}
				monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth)));
				break;
			case R.id.minusDay :
				if(1==sl.check(mYear, mMonth, mDay-1)){  
					mDay--;
				}
				else mDay = sl.getDaysOfLunarMonth(mYear, mMonth);
				dayEditText .setText(decimalformat.format(mDay));
				break;

			default:
				break;
			}
			dialog.setTitle(sl.lunarYear(mYear, mMonth, mDay));
		}
	};

	/** 
	 *  年份的TextWatcher
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private TextWatcher yearTextWatcher = new TextWatcher() {
		int mStart;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			mStart = start;
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if((s.toString().length())>=4&&(Integer.valueOf(s.toString())>MAX_YEAR||Integer.valueOf(s.toString())<MIN_YEAR)){
				s.delete(mStart, mStart+1);
			}
		}
	};
	/** 
	 *  月份的TextWatcher
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private TextWatcher monthTextWatcher = new TextWatcher() {
		int mStart;

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			mStart = start;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			//months = sl.getLunarLeapMonth(mYear)>0?13:12;
			if((s.toString().length())>=2&&Integer.valueOf(s.toString())>12){
				s.delete(mStart, mStart+1);
			}
		}
	};
	/** 
	 * day的TextWatcher
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private TextWatcher dayTextWatcher = new TextWatcher() {
		int mStart;
		int days;
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			mStart = start;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
		}
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			days = sl.getDaysOfLunarMonth(mYear, mMonth);
			if((s.toString().length())>=2&&Integer.valueOf(s.toString())>days){
				s.delete(mStart, mStart+1);
			}
		}
	};
	/** 
	 * 创建农历日期的输入框对话框
	 * @param      
	 * @return    
	 * @exception  
	 * @see        
	 * @since     
	 */ 
	private AlertDialog createLunarDateDialog(View dialogView){
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(dialogView)
		.setTitle(sl.lunarYear(mYear, mMonth, mDay))
		.setIcon(R.drawable.ic_dialog_time)
		.setPositiveButton(mContext.getString(R.string.warnningSet), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				LundarDateDialog.this.dialog.getCurrentFocus().clearFocus();
				sqlitebirthday.updateLundarBirth(((SetBirthActivity) mContext)._id, mYear, mMonth, mDay);
				change++;
				((SetBirthActivity) mContext).updateListView();
				sqlitebirthday.close();
			}
		})
		.setNegativeButton(mContext.getString(R.string.warnningCancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				sqlitebirthday.close();
			}
		});
		return builder.create();
	}
}
