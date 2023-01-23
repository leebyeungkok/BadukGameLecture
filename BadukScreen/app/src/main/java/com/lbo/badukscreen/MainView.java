package com.lbo.badukscreen;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.graphics.Canvas;
import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.os.*;
public class MainView extends SurfaceView 
	implements SurfaceHolder.Callback{
	MainActivity mainActivity;
	MainThread mainThread;
	Context mainContext;
	Resources mRes;
	boolean drawCls = false;
	PosInfo posInfo;
	GameButton badukPanel;
	float startX;
	float startY;
	float endX;
	float endY;
	boolean isTap=false;
	boolean isMove=false;
	public MainView(Context r){
		super(r);
		getHolder().addCallback(this);
		mainThread = new MainThread(getHolder(),this);
		setFocusable(true);
		mainContext = r;
	}
	public void init(int width, int height, MainActivity mainActivity){
		this.mainActivity = mainActivity;

		posInfo = new PosInfo(width,height, 300 ,200);
		mRes = getResources();
		badukPanel = new GameButton( 1,
			posInfo.getX(600),  posInfo.getY(600), 
			posInfo, mRes, R.drawable.baduk );
		badukPanel.move(posInfo.getX(0), posInfo.getY(0));
		drawCls = true;
	}
	@Override
	public  void onDraw(Canvas canvas){
		if(drawCls == false)
			return;
		badukPanel.draw(canvas)	;
	}
    @Override
    public boolean onTouchEvent(MotionEvent event){
		final float x = event.getX();
		final float y = event.getY();
    	if ( event.getAction() == MotionEvent.ACTION_DOWN){
    		isTap = true;
    		isMove = false;
    		startX = x;
    		startY = y;
    	}
    	if ( event.getAction() == MotionEvent.ACTION_MOVE){
    		if(isTap== true){
	    		endX = x;
	    		endY = y;
	    		if(Math.abs(endX - startX ) > posInfo.getX(20) || 
	    			Math.abs(endY - startY ) > posInfo.getY(20)){
	    			isMove = true;
	    		}
	    		moveScreenX((int)(endX - startX));
	    		moveScreenY((int)(endY - startY));
	    		if(isMove == true){
	    			startX = endX;
	    			startY = endY;
	    		}
    		}
    	}
    	if ( event.getAction() == MotionEvent.ACTION_UP){
            if(isTap == true){
	    		endX = x;
	    		endY = y;
	    		if(isMove==false){
	    		}
    		}
    		isTap = false;
    		isMove = false;
    	}
    	if ( event.getAction() == MotionEvent.ACTION_CANCEL){    	
        	isTap = false;
        	isMove = false;
    	}
	    return true;
    } 
    public void moveScreenX(float input){
    	badukPanel.mX += input;
    	if(badukPanel.mX > 0){
    		badukPanel.mX = 0;
    	}else if( badukPanel.mX <
    		posInfo.screenWidth -badukPanel.mWidth ){
    		badukPanel.mX =
    			posInfo.screenWidth - badukPanel.mWidth;
    	}
    }
    public void moveScreenY(float input){
    	badukPanel.mY += input;
    	if(badukPanel.mY > 0){
    		badukPanel.mY = 0;
    	}else if( badukPanel.mY < 
    		posInfo.screenHeight - badukPanel.mHeight  ){
    		badukPanel.mY = 
    			posInfo.screenHeight -badukPanel.mHeight;
    	}
    }
    public void surfaceChanged(SurfaceHolder holder, 
    	int format, int width, int height){
    }
    public void surfaceCreated(SurfaceHolder holder){
		mainThread.setRunning(true);
		try{
	    	if(mainThread.getState() == Thread.State.TERMINATED ){
	    		mainThread = new MainThread(getHolder(),this);
	    		mainThread.setRunning(true);
	    		setFocusable(true);
	    		mainThread.start();
	    	}else{
	    		mainThread.start();
	    	}
		}catch(Exception ex){
		}
    }
    public void surfaceDestroyed(SurfaceHolder holder){
    	boolean retry= true;
    	mainThread.setRunning(false);
    	while(retry){
    		try{
    			mainThread.join();
    			retry= false;
    		}catch(Exception e){
    		}
    	}
    }  
}
