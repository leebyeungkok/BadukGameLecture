package com.lbo.baduknetworkgame;

public class PosInfo {
	public   int screenWidth;
	public   int screenHeight;

	public   int virtualWidth;
	public   int virtualHeight;

	public   int[][] stones = new int[19][19];

	public PosInfo(int screenWidth , int screenHeight, int virtualWidth, int virtualHeight){
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.virtualWidth = virtualWidth;
		this.virtualHeight = virtualHeight;
	}
	public void setSize(int width, int height){

	}
	public int getX(int x){
		return (int)( x * screenWidth/virtualWidth);
	}
	public int getY(int y){
		return (int)( y * screenHeight/virtualHeight);
	}
	public int getVirtualX(int x){
		return (int)( x * virtualWidth/screenWidth);
	}
	public int getVirtualY(int y){
		return (int)( y * virtualHeight/screenHeight);
	}
	public void init(){
		for(int i=0; i<19;i++) {
			for(int j=0; j<19;j++) {
				stones[i][j] = 0;
			}
		}
	}
}
