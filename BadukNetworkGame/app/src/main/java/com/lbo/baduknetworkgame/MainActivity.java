package com.lbo.baduknetworkgame;


 
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.*;
import java.net.*;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

	Context mContext;
	String strServerIp = "192.168.0.2";
	String strServerSendText = "";
	String strClientSendText = "";
	String strReceiveText = "";
	String strTotReceiveText= "";
	int strServerPort= 10876;
	
	ServerSocket serverSocket = null;
	Socket socket = null;
	Socket clientSocket = null;
	
	Handler mHandler = null;
	final static int RECEIVE_FROM_SERVER = 1000;
	final static int RECEIVE_FROM_CLIENT = 1001;
	final static int TOAST_MESSAGE = 2000;
	static String toastMessage = "";
	
	final static int RESULT_CONNECT_CODE = 1000;
	final static int RESULT_CANCEL = 1001;

	final static int RESULT_OPENSTONE_YES = 1010;
	final static int RESULT_OPENSTONE_CANCEL = 1011;
		

	EditText EdtServerIp;	
	EditText EdtServerSendText;
	EditText EdtClientSendText;
	TextView txtReceiveText;
	
	boolean isReadySever=false;
	boolean isReadyClient =false;
	
    Thread receiveThread = null;
    Thread receiveClientThread = null;
    
	MainView mainView;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getSupportActionBar().hide();

        super.onCreate(savedInstanceState);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        int ScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
        int ScreenHeight = getWindowManager().getDefaultDisplay().getHeight();
		mainView = new MainView(this);
		setContentView(mainView);
        mainView.init(ScreenWidth, ScreenHeight, this);
        mContext = this;
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case RECEIVE_FROM_SERVER:
					receiveFromServer();
					break;
				case RECEIVE_FROM_CLIENT:
					receiveFromClient();
					break;
				case TOAST_MESSAGE:
					Toast toast = Toast.makeText(mContext,toastMessage,Toast.LENGTH_LONG);
					toast.show();

				}
			}
		};		

		receiveThread = new Thread(){ 
			public void run(){
				InputStream in = null;
		        try{
		        	in= socket.getInputStream();
		        }
		        catch(Exception exSocket){
		        	Log.i("", exSocket.toString());
		        }				
				while(true){
					try{
						try{
							Thread.sleep(2000);
						}
						catch(Exception ex){}
						if( isReadySever == true)
						{
					        byte arrlen[] = new byte[3];
					        in.read(arrlen);					        
					        String strLen = new String(arrlen);
					        int len = Integer.parseInt(strLen);
					        byte arrcont[] = new byte[len];
					        in.read(arrcont);		
					        String strArrCont = new String(arrcont);
					        strReceiveText = strLen + strArrCont;
					        mHandler.sendMessage(Message.obtain(mHandler, RECEIVE_FROM_SERVER));
						}
					}
					catch(Exception exTemp){
						Log.e(">>>", exTemp.toString());
					}
				}
			}
		};
		receiveClientThread = new Thread(){ 
			public void run(){
		        InputStream in = null;
		        try{
		        	in= clientSocket.getInputStream();
		        }
		        catch(Exception exSocket){
		        	Log.i("", exSocket.toString());
		        }
				while(true){
					try{
						try{
							Thread.sleep(2000);
						}
						catch(Exception ex){}

						if( isReadyClient == true){
					        byte arrlen[] = new byte[3];
					        in.read(arrlen);
					        String strLen = new String(arrlen);
					        int len = Integer.parseInt(strLen);
					        byte arrcont[] = new byte[len];
					        in.read(arrcont);		
				        
					        String strArrCont = new String(arrcont);
					        strReceiveText = strLen + strArrCont;
					        mHandler.sendMessage(Message.obtain(mHandler, RECEIVE_FROM_CLIENT));
						}
					}
					catch(Exception exTemp){
						Log.e("", exTemp.toString());
					}
				}
			}
		};
    }
	public void ToastMessage(String message) {
		toastMessage = message;
		mHandler.sendMessage(Message.obtain(mHandler, TOAST_MESSAGE));
	}
    public void createSocket(){
    	Toast toast = Toast.makeText(mContext,"소켓을 생성합니다.",Toast.LENGTH_LONG);
    	toast.show();
		Thread acceptThread = new Thread(){
			public void run(){
		    	try{
			    	serverSocket = new ServerSocket( strServerPort );
		        	socket = serverSocket.accept();
		        	mainView.myStone = 2;	// 백돌
		        	mainView.setStone(2);
		        	mainView.myTurn = false;
		        	isReadySever= true;
		        	receiveThread.start();
		    	}
		    	catch(Exception exSocket){
		    		Log.e("socketCreate error", exSocket.toString());
		    	}					
			}
		};    
		acceptThread.start();
    }
    public void connectView(){
		connectSocket(strServerIp);
		// IP를 받아서 처리하도록 수정.
    }
    public void openStoneView(){
		if(this.mainView.myStone == 1){
			this.sendFromClient("410000");
		}
		else if(this.mainView.myStone == 2){
			this.sendFromServer("420000");
		}
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	super.onActivityResult(requestCode,  resultCode, data);
    	if(resultCode == RESULT_CONNECT_CODE){
    		strServerIp = data.getStringExtra("strServerIp");
    		connectSocket(strServerIp);
    	}else if(resultCode == RESULT_CANCEL){
    	}
    	else if(resultCode == RESULT_OPENSTONE_YES){
    		if(this.mainView.myStone == 1){
    			this.sendFromClient("410000");
    		}
    		else if(this.mainView.myStone == 2){
    			this.sendFromServer("420000");
    		}    		
    	}else if(resultCode == RESULT_OPENSTONE_CANCEL){
    	}    	
    }
    public void connectSocket(String input){
		Thread thread = new Thread() {
			public void run() {
				try {
					clientSocket = new Socket(input, strServerPort);
					isReadyClient = true;
					mainView.myStone = 1;
					mainView.setStone(1);
					mainView.myTurn = true;
					ToastMessage("소캣이 연결됨");
					receiveClientThread.start();
				} catch (Exception exSocket) {
					ToastMessage("소캣이 연결오류" + exSocket.toString());
				}
			}
		};
        thread.start();
    }
	public void closeSocket(){		
	   	ToastMessage("소켓을 종료합니다.");
		try{
			clientSocket.close();
			socket.close();
			serverSocket.close();
		}
		catch(Exception ex){
			Log.e("", ex.toString());
		}
    }
    public void sendFromServer(String input){
		Thread thread = new Thread() {
			public void run() {
				try {
					byte[] temp = input.getBytes();
					int len = temp.length;
					DecimalFormat df = new DecimalFormat("000");
					String strLen = df.format(len);
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					out.write(strLen + input);
					out.flush();
					ToastMessage("응답이 왔습니다.");
					mainView.receiveData(input);
				} catch (Exception exTemp) {
					Log.e("", "error222" + exTemp.toString());
				}
			}
		};
		thread.start();
    }
    public void sendFromClient(String input){
		Thread thread = new Thread() {
			public void run() {
				try {
					byte[] temp = input.getBytes();
					int len = temp.length;
					DecimalFormat df = new DecimalFormat("000");
					String strLen = df.format(len);
					BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
					out.write(strLen + input);
					out.flush();
					ToastMessage("응답이 왔습니다.");
					mainView.receiveData(input);
				} catch (Exception exTemp) {
					Log.e("", "error1111" + exTemp.toString());
				}
			}
		};
		thread.start();
    }
    public void receiveFromServer() {
    	this.mainView.receiveData(strReceiveText.substring(3));    	
    }
    public void receiveFromClient() {
    	this.mainView.receiveData(strReceiveText.substring(3));
   	
    }     
    
}