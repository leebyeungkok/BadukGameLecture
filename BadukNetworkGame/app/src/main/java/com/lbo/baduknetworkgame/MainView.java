package com.lbo.baduknetworkgame;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Enumeration;

import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.widget.Toast;
import android.graphics.Canvas;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.os.*;

public class MainView extends SurfaceView 
	implements SurfaceHolder.Callback{
	MainActivity mainActivity;
	MainThread mainThread;
	Handler handler;
	Context mainContext;
	Resources m_res;
	boolean drawCls = false;
	PosInfo posInfo;
	StoneInfo stoneInfo;
	GameButton badukPanel;
	GameButton createServer;
	GameButton connectServer;
	GameButton openStone;
	GameButton openStoneEnd;
	GameButton endGame;
	GameButton[][] stoneButton;	
	int [][] stone;
	boolean myTurn;
	int myStone;
	String myCommand = "진행";
	float start_x;
	float start_y;
	float end_x;
	float end_y;
	boolean isTap=false;
	boolean isMove=false;
	int turnCls = 1;

	public MainView(Context r){
		super(r);
		getHolder().addCallback(this);
		mainThread = new MainThread(getHolder(),this);
		setFocusable(true);
		mainContext = r;
	}
	public void init(int width, int height, MainActivity mainActivity){
		this.mainActivity = mainActivity;
		drawCls = true;
		// 가상 크기를 300, 200으로 설정함.
		posInfo = new PosInfo(width,height, 300, 200);
		m_res = getResources();
		badukPanel = new GameButton( 1, 
			posInfo.getX(600),  posInfo.getY(600), 
			posInfo, m_res, R.drawable.baduk );
		badukPanel.move(posInfo.getX(0), posInfo.getY(0));
		createServer = new GameButton( 1, 
			posInfo.getX(50),  posInfo.getY(20), 
			posInfo, m_res, R.drawable.create );
		createServer.move(posInfo.getX(5), posInfo.getY(5));
		connectServer = new GameButton( 1, 
			posInfo.getX(50),  posInfo.getY(20), 
			posInfo, m_res, R.drawable.connect );
		connectServer.move(posInfo.getX(65), posInfo.getY(5));
		openStone = new GameButton( 1, 
			posInfo.getX(50),  posInfo.getY(20), 
			posInfo, m_res, R.drawable.openstone );
		openStone.move(posInfo.getX(125), posInfo.getY(5));
		openStoneEnd = new GameButton( 1, 
			posInfo.getX(50),  posInfo.getY(20), 
			posInfo, m_res, R.drawable.openstoneend );
		openStoneEnd.move(posInfo.getX(185), posInfo.getY(5));
		endGame = new GameButton( 1, 
			posInfo.getX(50),  posInfo.getY(20), 
			posInfo, m_res, R.drawable.end );
		endGame.move(posInfo.getX(245), posInfo.getY(5));
		stone = new int [20][20];
		stoneInfo = new StoneInfo(mainContext, m_res);
		stoneInfo.init(posInfo);
	}
	@Override
	public  void onDraw(Canvas canvas){
		if(drawCls == false)
			return;
		badukPanel.draw(canvas);
		stoneInfo.draw(canvas);
		createServer.draw(canvas);
		connectServer.draw(canvas);
		openStone.draw(canvas);
		openStoneEnd.draw(canvas);
		endGame.draw(canvas);		
	}
    @Override
    public boolean onTouchEvent(MotionEvent event){
		final float x = event.getX();
		final float y = event.getY();
    	if ( event.getAction() == MotionEvent.ACTION_DOWN){
    		isTap = true;
    		isMove = false;
    		start_x = x;
    		start_y = y;
    	}
    	if ( event.getAction() == MotionEvent.ACTION_MOVE){
    		if(isTap== true){
	    		end_x = x;
	    		end_y = y;
	    		if(Math.abs(end_x - start_x ) > posInfo.getX(20) || 
	    				Math.abs(end_y - start_y ) > posInfo.getY(20)){
	    			isMove = true;
	    		}
	    		moveScreenX((int)(end_x - start_x));
	    		moveScreenY((int)(end_y - start_y));
	    		if(isMove == true){
	    			start_x = end_x;
	    			start_y = end_y;
	    		}
    		}
    	}
    	if ( event.getAction() == MotionEvent.ACTION_UP){
    		if(createServer.isSelected((int)x,(int)y)){
    			this.mainActivity.createSocket();
    			//getLocalIpAddr();
    		}else if(connectServer.isSelected((int)x,(int)y)){
    			this.mainActivity.connectView();
    		}else if(openStone.isSelected((int)x,(int)y)){
    			// 개가요청
    			if(myStone == 1){
    				this.mainActivity.sendFromClient("210000");
    			}else if(myStone == 2){
    				this.mainActivity.sendFromServer("220000");
    			}
    		}else if(openStoneEnd.isSelected((int)x,(int)y)){
    			// 개가종료요청
    			if(myStone == 1){
    				this.mainActivity.sendFromClient("410000");
    			}else if(myStone == 2){
    				this.mainActivity.sendFromServer("420000");
    			}       			
    		}else if(endGame.isSelected((int)x,(int)y)){
    			// 종료
    			this.mainActivity.closeSocket();
    			this.mainActivity.finish();
    		}else if(isTap == true){
	    		end_x = x;
	    		end_y = y;
	    		if(isMove==false){
	    			if(myTurn == true){
		    		if( Math.abs(end_x - start_x ) < posInfo.getX(5) &&
		    		    Math.abs(end_y - start_y ) < posInfo.getY(5)){
		    			// 선택
		    			int numX = (int)(x - badukPanel.mX- posInfo.getX(15))/
		    				posInfo.getX(30) + 1;
		    			int numY = (int)(y - badukPanel.mY- posInfo.getY(15))/
		    				posInfo.getY(30) + 1;
		    			String strSendData= "";
		    			if(myCommand.equals("사석제거")){
			    			strSendData= "3" + myStone + 
			    					getFormat(numY,2) +	 
			    					getFormat(numX,2);		// 가로, 세로이므로 행은 Y 열은 X
		    			}else if(myCommand.equals("진행")){
			    			strSendData= "1" + myStone + 
			    					getFormat(numY,2) +	 
			    					getFormat(numX,2);		// 가로, 세로이므로 행은 Y 열은 X
		    			}
		    			if(myStone == 1){
		    				this.mainActivity.sendFromClient(strSendData);
		    			}else if(myStone == 2){
		    				this.mainActivity.sendFromServer(strSendData);
		    			}
		    		}    	
	    			}
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
    public void getLocalIpAddr() {
        try {
            Enumeration<NetworkInterface> en =
            NetworkInterface.getNetworkInterfaces(); 
            while(en.hasMoreElements()) {
                NetworkInterface interf = en.nextElement();
                Enumeration<InetAddress> ips = interf.getInetAddresses();
                while (ips.hasMoreElements()) {
                    InetAddress inetAddress = ips.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                		Toast toast = Toast.makeText(mainContext, 
        					"주소찾기 실패" +  inetAddress.getHostAddress(), Toast.LENGTH_LONG);
                		toast.show();
                    }
                }
            }
        } catch (Exception ex) {
        	Toast toast = Toast.makeText(mainContext, 
					"에러" + ex.toString(), Toast.LENGTH_LONG);
				toast.show();
        }        
    }      
    public void moveScreenX(float input){
    	badukPanel.mX += input;
    	stoneInfo.moveTop(input , 0);
    	if(badukPanel.mX > 0){
    		badukPanel.mX = 0;
    		stoneInfo.top_x = 0;
    	}else if( badukPanel.mX < 
    		posInfo.screenWidth -badukPanel.mWidth ){
    		badukPanel.mX = posInfo.screenWidth - badukPanel.mWidth;
    		stoneInfo.top_x = badukPanel.mX ;
    	}
    }
    public void moveScreenY(float input){
    	badukPanel.mY += input;
    	stoneInfo.moveTop(0, input);
    	if(badukPanel.mY > 0){
    		badukPanel.mY = 0;
    		stoneInfo.top_y = 0;
    	}else if( badukPanel.mY < 
    			posInfo.screenHeight - badukPanel.mHeight  ){
    		badukPanel.mY = posInfo.screenHeight -badukPanel.mHeight;
    		stoneInfo.top_y = badukPanel.mY;
    	}
    }
    public void receiveData(String paramData){
    	Log.i("receiveData", paramData);
		String strCommand = "";
		String strTurn = "";
		String strX = "";
		String strY = "";
		strCommand = paramData.substring(0,2);
		strTurn = paramData.substring(1,2);
		strX = paramData.substring(2,4);
		strY = paramData.substring(4,6);
		if(strCommand.equals("00")){
		}else if(strCommand.equals("11")){
			if(this.stoneInfo.setDol(
				Integer.parseInt(strX),Integer.parseInt(strY),1) == false){
				return;
			}
		}else if(strCommand.equals("12")){
			if(this.stoneInfo.setDol(
				Integer.parseInt(strX),Integer.parseInt(strY),2) == false){
				return;
			}
		}else if(strCommand.equals("21")){
			if(myStone==2){
				new AlertDialog.Builder(this.mainActivity).setTitle(
					"확인").setMessage("계가를 승낙하시겠습니까?").setPositiveButton(
					"Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						String strSendData= "230000";
						mainActivity.sendFromServer(strSendData);
					}
				}).setNegativeButton("NO", null).show();
			}
			return;		
		}else if(strCommand.equals("22")){
			if(myStone==1){
				new AlertDialog.Builder(this.mainActivity).setTitle(
					"확인").setMessage("계가를 승낙하시겠습니까?").setPositiveButton(
					"Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						String strSendData= "230000";
						mainActivity.sendFromClient(strSendData);
					}
				}).setNegativeButton("NO", null).show();
			}
			return;			
		}else if(strCommand.equals("23") || strCommand.equals("24")){
			Toast toast = Toast.makeText(mainContext, 
				"사석제거모드입니다. 사석을 제거하십시오", Toast.LENGTH_LONG);
			toast.show();			
			myCommand = "사석제거";
		}else if(strCommand.equals("31") || strCommand.equals("32")){
			this.stoneInfo.setDolChgDead(
				Integer.parseInt(strX),Integer.parseInt(strY));	
		}else if(strCommand.equals("41")){
			if(myStone==2){
				new AlertDialog.Builder(this.mainActivity).setTitle(
					"확인").setMessage("사석제거를 마치겠습니까?").setPositiveButton(
					"Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						String strSendData= "5" + myStone + "0000";							
							mainActivity.sendFromServer(strSendData);
					}
				}).setNegativeButton("NO", null).show();
			}
			return;			
		}else if(strCommand.equals("42")){
			if(myStone==1){
				new AlertDialog.Builder(
					this.mainActivity).setTitle("확인").setMessage(
						"사석제거를 마치겠습니까?").setPositiveButton(
						"Yes", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which){
						String strSendData= "5" + myStone + "0000";							
							mainActivity.sendFromClient(strSendData);
					}
				}).setNegativeButton("NO", null).show();
			}
			return;				
		}else if(strCommand.equals("51") || // 개가
				strCommand.equals("52")){
			this.stoneInfo.calcEnd();
			stone = this.stoneInfo.getStoneState();
			long blackcount = 
				this.stoneInfo.blackStoneEnd + 
				this.stoneInfo.whiteStoneDead;
			long whitecount = 
				this.stoneInfo.whiteStoneEnd + 
				this.stoneInfo.blackStoneDead;
			if(blackcount > whitecount){
				Toast toast = Toast.makeText(mainContext, 
					"흑 승입니다.\n 흑 : " + blackcount +
					" 백 : " + whitecount + "\n  " +
					(blackcount - whitecount) + "차 ", Toast.LENGTH_LONG);
				toast.show();
			}else if(blackcount < whitecount){
				Toast toast = Toast.makeText(mainContext,
					"백 승입니다.\n 흑 : " + blackcount +
					" 백 : " + whitecount + "\n  " +
					(whitecount - blackcount) + "차 ", Toast.LENGTH_LONG);
				toast.show();
			}else{
				Toast toast = Toast.makeText(mainContext,
					"무승부 입니다. \n 흑  : " + blackcount +
					" 백 : " + whitecount, Toast.LENGTH_LONG);
				toast.show();
			}
			return;
		}		

		stone = this.stoneInfo.getStoneState();
		if(strTurn.equals("1")){
			if( myStone == 1 ){
				myTurn = false;
			}else{
				myTurn = true;
			}
		}else if(strTurn.equals("2")){
			if( myStone == 1 ){
				myTurn = true;
			}else{
				myTurn = false;
			}
		}
		if(myTurn == true){
			Toast toast = Toast.makeText(this.mainContext, 
				"돌을 놓을 차례입니다.", Toast.LENGTH_LONG);
			toast.show();
		}else{
			Toast toast = Toast.makeText(this.mainContext, 
				"상대가 놓을 차례입니다.", Toast.LENGTH_LONG);
			toast.show();
		}     	
    }
    public void setStone(int val){
    	if(val==1){
	    	this.stoneInfo.myStone = 1;
	    	this.stoneInfo.yourStone = 2;
    	}
    	if(val == 2){
    		this.stoneInfo.myStone = 2;
    		this.stoneInfo.yourStone = 1;    		
    	}
    }
    public void surfaceChanged(SurfaceHolder holder, int format, 
    	int width, int height){
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
			Log.i("MainView", "ex:" + ex.toString());
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
    			Log.i("MainView", "surfaceDestoryed ex" + e.toString());
    		}
    	}
    }  
    public String getFormat(int input, int len){
    	String tempFormat = "";
    	for(int i=0; i< len;i++){
    		tempFormat = tempFormat + "0";
    	}    	
    	DecimalFormat df = new DecimalFormat(tempFormat);
    	return df.format(input);
    }
    public String getFormat(String input, int len){
    	if(input.length() < len){
    		input = input + " ";
    	}
    	else if(input.length() > len){
    		input = input.substring(0,len);
    	}
    	return input;
    }    
}
