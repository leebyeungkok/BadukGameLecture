package com.lbo.sockettest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.*;
import java.net.*;
import java.text.DecimalFormat;
public class MainActivity extends Activity {
    Context mContext;
    // 서버 IP
    String strServerIp = "";
    // 서버에서보낼 Text
    String strServerSendText = "";
    // 클라이언트에서 보낼 텍스트
    String strClientSendText = "";
    // 응답받은 텍스트
    String strReceiveText = "";
    // 응답받은 모든 텍스트
    String strTotReceiveText= "";
    // 서버포트
    int strServerPort= 10876;
    // 서버소켓
    ServerSocket serverSocket = null;
    // 소켓
    Socket socket = null;
    // 클라이언트소켓
    Socket clientSocket = null;
    // 이벤트 핸들러
    Handler mHandler = null;
    // 서버로부터 응답 핸들러
    final static int RECEIVE_SERVER = 1000;
    // 클라이언트로부터 응답 핸들러
    final static int RECEIVE_CLIENT = 1001;

    // 컴포넌트
    EditText EdtServerIp;
    EditText EdtServerSendText;
    EditText EdtClientSendText;
    TextView txtReceiveText;
    // 서버가 준비가 되었다면
    boolean isReadySever=false;
    // 클라이언트가 준비가 되었다면
    boolean isReadyClient =false;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        Button BtnServerCreate =
                (Button)findViewById(R.id.btnServerCreate);
        Button BtnConnect =
                (Button)findViewById(R.id.btnConnect);
        Button BtnServerSend =
                (Button)findViewById(R.id.btnServerSend);
        Button BtnClientSend =
                (Button)findViewById(R.id.btnClientSend);
        Button BtnClose =
                (Button)findViewById(R.id.btnClose);

        EdtServerIp =
                (EditText)findViewById(R.id.edtServerIp);
        EdtServerSendText =
                (EditText)findViewById(R.id.edtServerSendText);
        EdtClientSendText =
                (EditText)findViewById(R.id.edtClientSendText);
        txtReceiveText =
                (TextView)findViewById(R.id.txtReceive);
        EdtServerIp.setText("192.168.0.2");
        // 서버생성
        BtnServerCreate.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        createSocket();
                    }
                }
        );

        // 연결
        BtnConnect.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View v){
                        connectSocket();
                    }
                }
        );
        // 서버에서 보내기
        BtnServerSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                strServerSendText =
                        EdtServerSendText.getText().toString();
                byte[] temp = strServerSendText.getBytes();
                int len = temp.length;
                DecimalFormat df = new DecimalFormat("000");
                String strLen = df.format(len);
                sendServer(strLen + strServerSendText);
            }
        });
        // 클라이언트에서 보내기
        BtnClientSend.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                strClientSendText =
                        EdtClientSendText.getText().toString();
                byte[] temp = strClientSendText.getBytes();
                int len = temp.length;
                DecimalFormat df = new DecimalFormat("000");
                String strLen = df.format(len);
                sendClient(strLen + strClientSendText);
            }
        });
        // 소캣닫기
        BtnClose.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                try {
                    clientSocket.close();
                    socket.close();
                    serverSocket.close();
                }
                catch(Exception ex) {
                    Log.e("", ex.toString());
                }
            }
        });
        //이벤트 헨들러
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case RECEIVE_SERVER:
                        receiveServer();
                        break;
                    case RECEIVE_CLIENT:
                        receiveClient();
                        break;

                }
            }
        };



        // 서버에서 쓰레드로 응답을 기다림
        Thread receiveThread = new Thread(){
            public void run(){
                while(true)
                {
                    try
                    {
                        try
                        {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}
                        if( isReadySever == true)
                        {
                            InputStream in = socket.getInputStream();
                            byte arrlen[] = new byte[3];
                            in.read(arrlen);
                            String strLen = new String(arrlen);
                            int len = Integer.parseInt(strLen);
                            byte arrcont[] = new byte[len];
                            in.read(arrcont);
                            String strArrCont = new String(arrcont);
                            strReceiveText = strLen + strArrCont;
                            mHandler.sendMessage(Message.obtain(
                                    mHandler, RECEIVE_SERVER));
                        }
                    }
                    catch(Exception exTemp)
                    {
                        Log.e("", exTemp.toString());
                    }
                }
            }
        };
        receiveThread.start();
        // 클라이언트 에서 쓰레드로 응답을 기다림
        Thread receiveClientThread = new Thread(){
            public void run(){
                while(true) {
                    try {
                        try {
                            Thread.sleep(2000);
                        }
                        catch(Exception ex){}

                        if( isReadyClient == true) {
                            InputStream in =
                                    clientSocket.getInputStream();
                            byte arrlen[] = new byte[3];
                            in.read(arrlen);
                            String strLen = new String(arrlen);
                            int len = Integer.parseInt(strLen);
                            byte arrcont[] = new byte[len];
                            in.read(arrcont);
                            String strArrCont = new String(arrcont);
                            strReceiveText = strLen + strArrCont;
                            mHandler.sendMessage(Message.obtain(
                                    mHandler, RECEIVE_CLIENT));
                        }
                    }
                    catch(Exception exTemp) {
                        Log.e("", exTemp.toString());
                    }
                }
            }
        };
        receiveClientThread.start();
    }
    public void createSocket()
    {
        Toast toast = Toast.makeText(mContext,"소켓연결",Toast.LENGTH_LONG);
        toast.show();
        Thread acceptThread = new Thread(){
            public void run(){
                try {
                    serverSocket = new ServerSocket( strServerPort );
                    socket = serverSocket.accept();
                    Log.i("","Accepted");
                    isReadySever= true;
                }
                catch(Exception exSocket) {
                    Log.e("", exSocket.toString());
                }
            }
        };
        acceptThread.start();
    }
    public void connectSocket() {
        Thread thread = new Thread() {
            public void run() {
                try {
                    strServerIp = EdtServerIp.getText().toString();
                    clientSocket = new Socket(strServerIp, strServerPort);
                    isReadyClient = true;
                } catch (Exception exSocket) {
                    Log.i("socket error", exSocket.toString());
                }
            }
        };
        thread.start();
    }
    public void sendServer(String input) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Log.i("socket", "서버에서보냄:" + input);
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream()));
                    out.write(input);
                    out.flush();
                } catch (Exception exTemp) {
                    Log.e("", "error" + exTemp.toString());
                }
            }
        };
        thread.start();
    }
    public void sendClient(String input) {
        Thread thread = new Thread() {
            public void run() {
                try {
                    Log.i("socket", "클라이언트에서보냄:" + input);
                    BufferedWriter out = new BufferedWriter(
                            new OutputStreamWriter(clientSocket.getOutputStream()));
                    out.write(input);
                    out.flush();
                } catch (Exception exTemp) {
                    Log.e("", "error" + exTemp.toString());
                }
            }
        };
        thread.start();
    }
    public void receiveServer() {
        Log.i("", "receive Server");
        strTotReceiveText = strTotReceiveText +
                "\nCLINET:" + strReceiveText;
        txtReceiveText.setText(strTotReceiveText);

    }
    public void receiveClient() {
        Log.i("", "receive Client");
        strTotReceiveText = strTotReceiveText +
                "\nSERVER:" + strReceiveText;
        txtReceiveText.setText(strTotReceiveText);
    }
}