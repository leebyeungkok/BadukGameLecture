package com.lbo.badukscreen;

import android.content.Context;
import android.graphics.*;
import android.widget.Toast;
import android.content.res.*;

public class GameButton{
	public  int m_num;
	private Context context;	
	private Bitmap bitmap;
	private Resources m_res;
	private int m_bitmap_id;
	public int mX;
	public int mY;
	public int mWidth;
	public int mHeight;
	private boolean mIsDraw = true;
	private boolean mIsAble = true;

	public GameButton( int num, int width, int height, 
		PosInfo pos_info, Resources res, int bitmap_id){		
		m_res = res;
		m_bitmap_id = bitmap_id;
		m_num = num;
		mWidth = width;
		mHeight = height;				
		Bitmap bitmaporg = BitmapFactory.decodeResource(m_res, m_bitmap_id);
        bitmap= Bitmap.createScaledBitmap(bitmaporg, mWidth, mHeight, false);	
	}	
	public void destory(){ 
		try{
			if(bitmap != null)
				bitmap.recycle();
		}
		catch(Exception ex){}
	}
	public void setIsDraw(boolean is_draw){
		mIsDraw = is_draw;
	}
	public void move(int x, int y){
		mX = x;
		mY = y;
	}
	public boolean isSelected(int x, int y){
		boolean is_selected = false;
		if(mIsAble == true){
			if( (x > mX && x < mX + mWidth) &&
				(y > mY && y < mY + mHeight)){
				is_selected = true;
			}
		}
		return is_selected;
	}
    public void onShowMsg(String pParam){
	    Toast toast = Toast.makeText(context,pParam, Toast.LENGTH_LONG);
	    toast.show();
	}	
	public void draw(Canvas canvas){		
		if(mIsDraw == true) 
			canvas.drawBitmap(bitmap,mX, mY, null);
	}	
}
