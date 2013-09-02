/* 
 * Copyright (C) 2006 ����¡�����ͿƼ����޹�˾. 
 * 
 * ��ϵͳ���������,δ����Ȩ���Ը��ƻ򴫲�������Ĳ��ֻ�ȫ�����ǷǷ���. 
 * 
 * Date          Author      Description 
 * 2011��10��19��     ֣�Ļ�	             ���ݿ�İ�����
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
 * ũ�����ڵ�������Dialog�࣬������DatePicker 
 * ��������һ����һ�굽����ž��������
 * @param
 * @author ֣�Ļ�
 * @Date   2011��10��21��
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

	private boolean longClicking = false;  //�Ƿ����ڳ���״̬��
	private View longClickView = null; 	   
	private Thread longClickThread = null;
	/** 
	 * ��������Ĭ�ϵ�����Ϊ1990��1��1��
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
	 * ������ ��
	 * @param ���ó�ʼ������Ϊ year��month��day�� �� 
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
	 * ��������ʾһ�������������ʾũ�����ڵĶԻ���
	 * @param 
	 * @exception  
	 * @see        
	 * @since     
	 */
	public void Create() {
		@SuppressWarnings("static-access")
		LayoutInflater inflater = ((SetBirthActivity)mContext).getLayoutInflater().from(mContext);
		final View dialogView = inflater.inflate(R.layout.lundardate, null);
		//����id����view
		plusYearButton  = (Button) dialogView.findViewById(R.id.plusYear);
		minusYearButton = (Button) dialogView.findViewById(R.id.minusYear);
		plusMonthButton  = (Button) dialogView.findViewById(R.id.plusMonth);
		minusMonthButton = (Button) dialogView.findViewById(R.id.minusMonth);	
		plusDayButton  = (Button) dialogView.findViewById(R.id.plusDay);
		minusDayButton = (Button) dialogView.findViewById(R.id.minusDay);
		//ΪButton��Ӽ�����
		setButtonListeners(plusYearButton);
		setButtonListeners(minusYearButton);
		setButtonListeners(plusMonthButton);
		setButtonListeners(minusMonthButton);
		setButtonListeners(plusDayButton);
		setButtonListeners(minusDayButton);

		yearEditText = (EditText)dialogView.findViewById(R.id.year);
		monthEditText = (EditText)dialogView.findViewById(R.id.month);
		dayEditText = (EditText)dialogView.findViewById(R.id.day);
		//Ϊ���ڸ�ֵ
		yearEditText.setText(String.valueOf(mYear));
		monthEditText.setText(decimalformat.format(mMonth+adjustment(mYear,mMonth))); 
		dayEditText.setText(decimalformat.format(mDay));
		//��ӽ���仯����Ӧ
		yearEditText.setOnFocusChangeListener(foucusChangeListener);
		monthEditText.setOnFocusChangeListener(foucusChangeListener);
		dayEditText.setOnFocusChangeListener(foucusChangeListener);
		//���EditText���ݱ仯����Ӧ
		yearEditText.addTextChangedListener(yearTextWatcher);
		monthEditText.addTextChangedListener(monthTextWatcher);
		dayEditText.addTextChangedListener(dayTextWatcher);

		//�����Ի�����ʾ
		dialog = createLunarDateDialog(dialogView);
		dialog.show();
	}
	/** 
	 * Ϊ�����Button��Ӽ�����������onLongClickListener��onTouchListener��onKeyListener��
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

	//�õ�һ���µ��߳�
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
	//��Ӧ�������¼�
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
	//��Ӧ�����¼�
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
	//����任ʱ�Ĵ���
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
	 *  �ı�EditText�Ľ���
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
	 *  �жϸ�����ũ��y��m���ǲ�������
	 * @param      
	 * @return    ��������·���true�������������false;
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
	 * @return    ũ������y����������m�´������£�����-1����������0��
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
	 *  ��ݵ�TextWatcher
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
	 *  �·ݵ�TextWatcher
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
	 * day��TextWatcher
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
	 * ����ũ�����ڵ������Ի���
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
