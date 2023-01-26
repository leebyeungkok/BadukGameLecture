package com.lbo.baduknetworkgame;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;



public class StoneInfo {
	public   int[] fieldValue = new int[400];
	public   PosInfo  posInfo;
	public int myStone;
	public int yourStone;
	public int whiteStoneDead;
	public int blackStoneDead;
	public int deadStoneCount;
	public int whiteStoneEnd;	// 개가후 점수
	public int blackStoneEnd;	// 개가후 점수
	public int[] mem0 = new int[400];
	public final static int NONE  = 0;
	public final static int BLACK = 1;
	public final static int WHITE = 2;
	public final static int DEADB = 3;
	public final static int DEADW = 4;
	int m_width;
	int m_height;
	int m_widths;
	int m_heights;
	int top_x;
	int top_y;
	int delay_time = 500;
	Context m_context;
	Resources m_res;
	Bitmap bmBlack;
	Bitmap bmWhite;
	Bitmap bmSpot;

	public StoneInfo(Context context, Resources res){
		m_context = context;
		m_res = res;
	}


	public void init(PosInfo posInfo){
		this.posInfo = posInfo;
		m_width = posInfo.getX(30-2);
		m_height = posInfo.getY(30-2);
		m_widths = posInfo.getX(16-2);
		m_heights = posInfo.getY(16-2);
		Bitmap bitmapblackorg =
				BitmapFactory.decodeResource(m_res, R.drawable.black);
		bmBlack= Bitmap.createScaledBitmap(
				bitmapblackorg, m_width, m_height, false);
		Bitmap bitmapwhiteorg =
				BitmapFactory.decodeResource(m_res, R.drawable.white);
		bmWhite= Bitmap.createScaledBitmap(
				bitmapwhiteorg, m_width, m_height, false);
		Bitmap bitmapspotorg =
				BitmapFactory.decodeResource(m_res, R.drawable.spot);
		bmSpot= Bitmap.createScaledBitmap(
				bitmapspotorg, m_widths, m_heights, false);
		for(int i=1; i<=19;i++){
			for(int j=1; j<=19;j++){
				fieldValue[(i-1)*20 + j] = NONE;
			}
		}
	}
	public void moveTop(float x, float y){
		this.top_x += x;
		this.top_y += y;
	}
	public void setState(int i, int j, int state){
		fieldValue[(i-1)*20 + j] = state;
	}
	public boolean calcDead(int x,int y, int inputValue){
		// 현재 계산하는 사석 수는 0으로 초기화한다.
		deadStoneCount = 0;
		// x,y 주변 4곳을 mem0에 저장한다.
		int fieldIndex = (x -1) * 20 + y;
		int fieldTop = fieldIndex - 20;
		int fieldBottom = fieldIndex + 20;
		int fieldLeft = fieldIndex -1;
		int fieldRight = fieldIndex + 1;
		// 사방의 돌이 어떤건지 체크
		try{
			// 초기화한다.
			mem0[0] = 0;
			mem0[1] = 0;
			mem0[2] = 0;
			mem0[3] = 0;
			// 사방의 값을 읽어온다.
			if(fieldTop > 0)
				mem0[0] = fieldTop;
			else
				mem0[0] = 0;
			if(fieldBottom < 380)
				mem0[1] = fieldBottom;
			else
				mem0[1] = 0;
			if(fieldRight % 20 !=0)
				mem0[2] = fieldRight;
			else
				mem0[2] = 0;
			if(fieldLeft % 20 != 0)
				mem0[3] = fieldLeft;
			else
				mem0[3] = 0;
			// 주변의 돌중 사석이 있는지 계산한다.
			calcDeadOther();
			mem0[0] = fieldIndex;
			// 돌을 놓을 수 있는 경우에만 체크한다.
			if(calcDeadUs() == true){
				if(inputValue == 1)
					whiteStoneDead += deadStoneCount;
				else
					blackStoneDead += deadStoneCount;
			}else{
				System.out.println("돌을 놓을 수 없습니다.");
				return false;
			}
		}
		catch(Exception e){
		}
		return true;
	}
	// 상대의 돌을 계산한다.
	public void calcDeadOther(){
		// 네방향을 모두 체크한다.
		for(int i=0; i< 4; i++){
			// 사석이 있다고 먼저 설정한다.
			boolean isDeadTot = true;
			// 해당 돌이 상대의 돌일 경우만 계산한다.
			if( fieldValue[mem0[i]] == myStone ||
					fieldValue[mem0[i]] == 0 ){
				// 근접한 ㄷ로이 상대돌이 아니면 계산하지 않는다.
			}else{
				// 사석을 계산하기 위해 임의로 mem1의 배열을 사용한다.
				int memIndex1 = 0;
				int[] mem1 = new int[400];
				mem1[memIndex1++] = mem0[i];
				for(int j = 0; j < 400; j++){
					boolean isDead = true;
					int tempCenter = mem1[j];
					if( isDead == false)
						break;
					// 사석계산중 인접한 곳에 돌이 없다면 사석이 아니다.
					if( tempCenter == 0  ||
							fieldValue[tempCenter] == 0 ){
						break;
					}
					int tempTop = getTop(mem1[j]);
					int tempBottom = getBottom(mem1[j]);
					int tempLeft = getLeft(mem1[j]);
					int tempRight= getRight(mem1[j]);
					// 상단의 경우
					if(tempTop <= 0 ){
					}else{
						int tempTopStone = fieldValue[tempTop];
						boolean tempPass = true;
						if(tempTopStone == 0){
							// 상단에 돌이 없다.
							isDead = false;
						}else if(tempTopStone == yourStone){
							for(int k=0; k < 400 ; k++){
								if(mem1[k] == tempTop)
									tempPass = false;
							}
						}else{
							tempPass = false;
						}
						if(tempPass == true)
							mem1[memIndex1++] = tempTop;
					}
					// 하단일 경우
					if(tempBottom <= 0){
					}else{
						int tempBottomStone = fieldValue[tempBottom];
						boolean tempPass = true;
						if(tempBottomStone <= 0 || tempTop >=380){
							// 하단에 돌이 없다.
							isDead = false;
						}else if(tempBottomStone == yourStone){
							for(int k=0; k < 400 ; k++){
								if(mem1[k] == tempBottom)
									tempPass = false;
							}
						}else{
							tempPass = false;
						}
						if(tempPass == true)
							mem1[memIndex1++] = tempBottom;
					}
					// 좌측일 경우
					if(tempLeft == 0){
					}else{
						int tempLeftStone = fieldValue[tempLeft];
						boolean tempPass = true;
						if(tempLeftStone == 0){
							// 좌측에 돌이없다.
							isDead = false;
						}else if(tempLeftStone == yourStone){
							for(int k=0; k < 400 ; k++){
								if(mem1[k] == tempLeft)
									tempPass = false;
							}
						}else{
							tempPass = false;
						}
						if(tempPass == true)
							mem1[memIndex1++] = tempLeft;
					}
					// 우측일 경우
					if(tempRight == 0){
					}else{
						int tempRightStone = fieldValue[tempRight];
						boolean tempPass = true;
						if(tempRightStone == 0){
							// 우측에 돌이 없다.
							isDead = false;
						}
						if(tempRightStone == yourStone){
							for(int k=0; k < 400 ; k++){
								if(mem1[k] == tempRight)
									tempPass = false;
							}
						}else{
							tempPass = false;
						}
						if(tempPass == true)
							mem1[memIndex1++] = tempRight;
					}
					// 사석이 아닐경우
					if(isDead == false){
						isDeadTot = false;
					}else{
						// 사석일 경우
					}
				}	// for
				// 사석일 경우 사석으로 계산된 mem1 배열의 돌을 모두 제거한다.
				// deadStoneCount 를 수만큼 증가시킨다.
				if(isDeadTot == true){
					for(int j = 0; j < 400; j++){
						if( mem1[j] == 0){
							continue;
						}else{
							fieldValue[mem1[j]] = 0;
							deadStoneCount++;
						}
					}
				}
			} // else
		} // for
	}
	// 돌을 놓을수 있는지를 체크한다.
	public boolean calcDeadUs(){
		boolean isDeadTot = true;
		// mem1을 기준으로 조회한다.
		int memIndex1 = 0;
		int[] mem1 = new int[400];
		mem1[memIndex1++] = mem0[0];
		for(int j = 0; j < 400; j++){
			boolean isDead = true;
			int tempCenter = mem1[j];
			if( isDead == false)
				break;
			if( tempCenter == 0  || fieldValue[tempCenter] == 0 ){
				break;
			}
			int tempTop = getTop(mem1[j]);
			int tempBottom = getBottom(mem1[j]);
			int tempLeft = getLeft(mem1[j]);
			int tempRight= getRight(mem1[j]);
			// 상단을 계산한다.
			if(tempTop <= 0 ){
			}else{
				int tempTopStone = fieldValue[tempTop];
				boolean tempPass = true;
				if(tempTopStone == 0){
					// 상단에 돌이 없다.
					isDead = false;
				}else if(tempTopStone == myStone){
					for(int k=0; k < 400 ; k++){
						if(mem1[k] == tempTop)
							tempPass = false;
					}
				}else{
					tempPass = false;
				}
				if(tempPass == true)
					mem1[memIndex1++] = tempTop;
			}
			// 하단 돌을 계산한다.
			if(tempBottom <= 0){
			}else{
				int tempBottomStone = fieldValue[tempBottom];
				boolean tempPass = true;
				if(tempBottomStone <= 0 || tempTop >=380){
					// 하단에 돌이 없다.
					isDead = false;
				}else if(tempBottomStone == myStone){
					for(int k=0; k < 400 ; k++){
						if(mem1[k] == tempBottom)
							tempPass = false;
					}
				}else{
					tempPass = false;
				}
				if(tempPass == true)
					mem1[memIndex1++] = tempBottom;
			}
			// 좌측의 돌을 계산한다.
			if(tempLeft == 0){
			}else{
				int tempLeftStone = fieldValue[tempLeft];
				boolean tempPass = true;
				if(tempLeftStone == 0){
					// 좌측에 돌이없다.
					isDead = false;
				}else if(tempLeftStone == myStone){
					for(int k=0; k < 400 ; k++){
						if(mem1[k] == tempLeft)
							tempPass = false;
					}
				}else{
					tempPass = false;
				}
				if(tempPass == true)
					mem1[memIndex1++] = tempLeft;
			}
			// 우측 돌을 계산한다.
			if(tempRight == 0){
			}else{
				int tempRightStone = fieldValue[tempRight];
				boolean tempPass = true;
				if(tempRightStone == 0){
					// 우측에 돌이 없다.
					isDead = false;
				}
				if(tempRightStone == myStone){
					for(int k=0; k < 400 ; k++){
						if(mem1[k] == tempRight)
							tempPass = false;
					}
				}else{
					tempPass = false;
				}
				if(tempPass == true)
					mem1[memIndex1++] = tempRight;
			}

			// 모두계산한다.
			if(isDead == false){
				// 사석이 아님
				isDeadTot = false;
			}else{
				// 사석임
			}

		}	// for
		// 사석이 있고 자신의 돌이라면 돌을 놓을 수 없다.
		if(isDeadTot == true){
			for(int j = 0; j < 400; j++){
				if( mem1[j] == 0){
					continue;
				}else{
					// 돌을 놓을 수 없다.
					fieldValue[mem1[j]] = 0;
					return false;
				}
			}
		}
		return true;
	}

	// 최종 집을 계산한다.
	public void calcEnd(){
		for(int x = 1; x <= 19; x++){
			for(int y = 1; y <=19; y++){
				int fieldIndex = (x - 1) * 20 + y;
				// 사석은 2점으로 계산된다 (집과 따낸돌)
				if(fieldValue[fieldIndex] == 3 ){
					whiteStoneEnd +=2;
				}else if(fieldValue[fieldIndex] == 4 ){
					blackStoneEnd +=2;
				}else if(fieldValue[fieldIndex] == 0 ){
					boolean findStoneKind = false;
					if(findStoneKind == false){
						// 왼쪽으로 이동하며 백돌과 흑돌이 있는지 검색한다.
						for(int i = x; i >= 1; i--){
							if( fieldValue[(i - 1) * 20 + y] == 1 ||
									fieldValue[(i - 1) * 20 + y] == 2 ){
								// 흑이있다면 5, 백이 있다면 6을 설정한다.
								findStoneKind = true;
								if( fieldValue[(i - 1) * 20 + y]== 1 ){
									fieldValue[fieldIndex] = 5;
									blackStoneEnd++;
								}else{
									fieldValue[fieldIndex] = 6;
									whiteStoneEnd++;
								}
								i=0;
							}
						}
					}
					if(findStoneKind == false){
						// 위에서 구하지 못했다면 오른쪽으로 이동하며 돌이 있는지 검색한다.
						for(int i = x; i <= 19; i++){
							if( fieldValue[(i - 1) * 20 + y]== 1 ||
									fieldValue[(i - 1) * 20 + y] == 2 ){
								// 돌이있다면 흑돌의 경우 흑집 5, 흰색돌이 있다면 백집6으로 설정한다.
								findStoneKind = true;
								if( fieldValue[(i - 1) * 20 + y] == 1 ){
									fieldValue[fieldIndex] = 5;
									blackStoneEnd++;
								}else{
									fieldValue[fieldIndex] = 6;
									whiteStoneEnd++;
								}
								i = 20;
							}
						}
					}
					if(findStoneKind == false){
						// 돌이 없다면 제일 왼쪽부터 열로 찾는다.
						for(int i = 1; i <= 19; i++){
							// 현재 행에서 상단의 돌을 찾는다.
							for(int j = y; j >= 1 ; j--){
								if( fieldValue[(i - 1) * 20 + j] == 1 ||
										fieldValue[(i - 1) * 20 + j] == 2 ){
									// 돌이 있다면 흑일 경우 흑집5, 백일경우 백집 6으로 설정한다.
									findStoneKind = true;
									if( fieldValue[(i - 1) * 20 + j] == 1){
										fieldValue[fieldIndex] = 5;
										blackStoneEnd++;
									}else{
										fieldValue[fieldIndex] = 6;
										whiteStoneEnd++;
									}
									// 빠져나간다.
									i = 20;
									j = 0;
								}
							}
							if(findStoneKind == false){
								// 돌을 찾지 못했다면 아래로 계산하며 내려간다.
								for(int j = y; j <=19 ;j++){
									if( fieldValue[(i - 1) * 20 + j]== 1 ||
											fieldValue[(i - 1) * 20 + j] == 2){
										findStoneKind = true;
										if( fieldValue[(i - 1) * 20 + j] == 1){
											fieldValue[fieldIndex] = 5;
											blackStoneEnd++;
										}else{
											fieldValue[fieldIndex] = 6;
											whiteStoneEnd++;
										}
										// 빠져나간다.
										i = 20;
										j = 20;
									}
								}
							}
						}
					}
				}
			} //y
		} // x
	}
	// 상단의 값을 읽는다.
	public int getTop(int inputValue){
		int tempValue = inputValue - 20;
		if(tempValue < 0)
			tempValue = 0;
		else if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// 하단의 값을 읽는다.
	public int getBottom(int inputValue){
		int tempValue = inputValue + 20;
		if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// 우측값을 읽는다.
	public int getRight(int inputValue){
		int tempValue= inputValue + 1;
		if( (tempValue % 20) == 0)
			tempValue = 0;
		if(tempValue < 0)
			tempValue = 0;
		else if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// 좌측값을 읽는다.
	public int getLeft(int inputValue){
		int tempValue= inputValue - 1;
		if((tempValue % 20) == 0)
			tempValue = 0;
		if(tempValue < 0)
			tempValue = 0;
		else if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// 사석제거모드에서 돌을 따낸다.
	public void pickDol(int x, int y){
		if(fieldValue[(x-1) * 20 + y] == BLACK){
			fieldValue[(x-1) * 20 + y] = DEADB;
		}else if(fieldValue[(x-1) * 20 + y] == WHITE){
			fieldValue[(x-1) * 20 + y] = DEADW;
		}else if(fieldValue[(x-1) * 20 + y] == DEADB){
			fieldValue[(x-1) * 20 + y] = BLACK;
		}else if(fieldValue[(x-1) * 20 + y] == DEADW){
			fieldValue[(x-1) * 20 + y] = WHITE;
		}
	}
	//돌을 놓는다.
	public boolean setDol(int x,int y, int inputValue){
		if(inputValue == 1){
			myStone = 1;
			yourStone = 2;
		}else{
			myStone = 2;
			yourStone = 1;
		}
		if( fieldValue[(x -1) * 20 + y ] != 0){
			System.out.println("돌이 있습니다.");
			return false;
		}else{
			fieldValue[(x -1) * 20 + y ] = inputValue;
			return calcDead(x,y,inputValue);
		}
	}
	public boolean setDol( String x, String y, String inputValue){
		return setDol(Integer.parseInt(x), Integer.parseInt(y),
				Integer.parseInt(inputValue));
	}
	// 사석계산
	public void setDolChgDead( int x, int y){
		if(fieldValue[(x -1) * 20 + y ] == 1){
			fieldValue[(x -1) * 20 + y ] = 3;
		}else if(fieldValue[(x -1) * 20 + y ] == 3){
			fieldValue[(x -1) * 20 + y ] = 1;
		}else if(fieldValue[(x -1) * 20 + y ] == 2){
			fieldValue[(x -1) * 20 + y ] = 4;
		}else if(fieldValue[(x -1) * 20 + y ] == 4){
			fieldValue[(x -1) * 20 + y ] = 2;
		}
	}

	public void setDolChgDead( String x, String y){
		setDolChgDead(Integer.parseInt(x), Integer.parseInt(y));
	}
	// 바둑판에 위치한 돌의 전체정보를 반환한다.
	public int[][] getStoneState(){
		int[][] returnStone = new int[20][20];
		for(int x = 1; x <= 19; x++){
			for(int y = 1; y <=19; y++){
				returnStone[x][y] = fieldValue[ (x - 1) * 20 + y ];
			}
		}
		return returnStone;
	}
	public void draw(Canvas canvas){
		for(int i=1;i<=19;i++){
			for(int j=1;j<=19;j++){
				if(fieldValue[(i-1)*20 +j] == NONE) {
				}else if(fieldValue[(i-1)*20 + j] == BLACK){
					canvas.drawBitmap(	bmBlack,
							top_x + posInfo.getX(15) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(15) + posInfo.getY(30)* (i-1), null);
				}else if(fieldValue[(i-1)*20 + j]== WHITE){
					canvas.drawBitmap(	bmWhite,
							top_x + posInfo.getX(15) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(15) + posInfo.getY(30)* (i-1), null);
				}else if(fieldValue[(i-1)*20 + j] == DEADB){
					canvas.drawBitmap(	bmBlack,
							top_x + posInfo.getX(15) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(15) + posInfo.getY(30)* (i-1), null);
					canvas.drawBitmap(	bmSpot,
							top_x + posInfo.getX(22) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(22) + posInfo.getY(30)* (i-1), null);
				}else if(fieldValue[(i-1)*20 + j]== DEADW){
					canvas.drawBitmap(	bmWhite,
							top_x + posInfo.getX(15) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(15) + posInfo.getY(30)* (i-1), null);
					canvas.drawBitmap(	bmSpot,
							top_x + posInfo.getX(22) + posInfo.getX(30)* (j-1),
							top_y + posInfo.getY(22) + posInfo.getY(30)* (i-1), null);
				}
			}
		}
	}
}