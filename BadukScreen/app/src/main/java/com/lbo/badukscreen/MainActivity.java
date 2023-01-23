package com.lbo.badukscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    Context mContext;
    MainView mainView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        int screenWidth =
                getWindowManager().getDefaultDisplay().getWidth();
        int screenHeight =
                getWindowManager().getDefaultDisplay().getHeight();
        mainView = new MainView(this);
        setContentView(mainView);
        mainView.init(screenWidth, screenHeight, this);
        mContext = this;
    }
}