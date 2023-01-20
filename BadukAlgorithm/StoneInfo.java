package com.lbo.badukalgorithm;

import java.io.BufferedReader;
import java.io.InputStreamReader;



public class StoneInfo {
	public   int screen_width;
	public   int screen_height;
	public   int virtual_width;
	public   int virtual_height;
	public   int[] fieldValue = new int[400];
	public int myStone;
	public int yourStone;
	public int whiteStoneDead;
	public int blackStoneDead;
	public int deadStoneCount;
	public int whiteStoneEnd;	// ������ ����
	public int blackStoneEnd;	// ������ ����
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

	public StoneInfo(){
	
	}
	public void init()
	{
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
        // ���� ����ϴ� �缮 ���� 0���� �ʱ�ȭ�Ѵ�.
		deadStoneCount = 0;
		// x,y �ֺ� 4���� mem0�� �����Ѵ�.
		int fieldIndex = (x -1) * 20 + y;
		int fieldTop = fieldIndex - 20;
		int fieldBottom = fieldIndex + 20;
		int fieldLeft = fieldIndex -1;
		int fieldRight = fieldIndex + 1;		
		// ����� ���� ����� üũ
		try{
			// �ʱ�ȭ�Ѵ�.
			mem0[0] = 0;
			mem0[1] = 0;
			mem0[2] = 0;
			mem0[3] = 0;
			// ����� ���� �о�´�.
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
			// �ֺ��� ���� �缮�� �ִ��� ����Ѵ�.
			calcDeadOther();
			mem0[0] = fieldIndex;
			// ���� ���� �� �ִ� ��쿡�� üũ�Ѵ�.
			if(calcDeadUs() == true){
				if(inputValue == 1)
					whiteStoneDead += deadStoneCount;
				else
					blackStoneDead += deadStoneCount;
			}else{
				System.out.println("���� ���� �� �����ϴ�.");				
				return false;
			}
		}
		catch(Exception e){
		}
		return true;
	}
	// ����� ���� ����Ѵ�.
	public void calcDeadOther(){
		// �׹����� ��� üũ�Ѵ�.
		for(int i=0; i< 4; i++){
			// �缮�� �ִٰ� ���� �����Ѵ�.
			boolean isDeadTot = true;
			// �ش� ���� ����� ���� ��츸 ����Ѵ�.
			if( fieldValue[mem0[i]] == myStone ||  
				fieldValue[mem0[i]] == 0 ){
				// ������ ������ ��뵹�� �ƴϸ� ������� �ʴ´�.
			}else{
				// �缮�� ����ϱ� ���� ���Ƿ� mem1�� �迭�� ����Ѵ�.
				int memIndex1 = 0;
				int[] mem1 = new int[400];			
				mem1[memIndex1++] = mem0[i];
				for(int j = 0; j < 400; j++){
					boolean isDead = true;
					int tempCenter = mem1[j];
					if( isDead == false)
						break;
					// �缮����� ������ ���� ���� ���ٸ� �缮�� �ƴϴ�.
					if( tempCenter == 0  || 
						fieldValue[tempCenter] == 0 ){
						break;
					}
					int tempTop = getTop(mem1[j]);
					int tempBottom = getBottom(mem1[j]);
					int tempLeft = getLeft(mem1[j]);
					int tempRight= getRight(mem1[j]);
					// ����� ���
					if(tempTop <= 0 ){
					}else{			
						int tempTopStone = fieldValue[tempTop];
						boolean tempPass = true;
						if(tempTopStone == 0){
							// ��ܿ� ���� ����.
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
					// �ϴ��� ��� 
					if(tempBottom <= 0){
					}else{				
						int tempBottomStone = fieldValue[tempBottom];
						boolean tempPass = true;
						if(tempBottomStone <= 0 || tempTop >=380){
							// �ϴܿ� ���� ����.
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
					// ������ ��� 
					if(tempLeft == 0){
					}else{
						int tempLeftStone = fieldValue[tempLeft];
						boolean tempPass = true;
						if(tempLeftStone == 0){
							// ������ ���̾���.
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
					// ������ ��� 
					if(tempRight == 0){
					}else{
						int tempRightStone = fieldValue[tempRight];
						boolean tempPass = true;
						if(tempRightStone == 0){
							// ������ ���� ����.
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
					// �缮�� �ƴҰ��					
					if(isDead == false){					
						isDeadTot = false;	
					}else{	
						// �缮�� ��� 
					}
				}	// for				
				// �缮�� ��� �缮���� ���� mem1 �迭�� ���� ��� �����Ѵ�.
				// deadStoneCount �� ����ŭ ������Ų��. 
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
	// ���� ������ �ִ����� üũ�Ѵ�. 
	public boolean calcDeadUs(){
		boolean isDeadTot = true;
		// mem1�� �������� ��ȸ�Ѵ�. 
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
			// ����� ����Ѵ�.
			if(tempTop <= 0 ){
			}else{			
				int tempTopStone = fieldValue[tempTop];
				boolean tempPass = true;
				if(tempTopStone == 0){
					// ��ܿ� ���� ����.
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
			// �ϴ� ���� ����Ѵ�. 
			if(tempBottom <= 0){
			}else{				
				int tempBottomStone = fieldValue[tempBottom];
				boolean tempPass = true;
				if(tempBottomStone <= 0 || tempTop >=380){
					// �ϴܿ� ���� ����.
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
			// ������ ���� ����Ѵ�.
			if(tempLeft == 0){
			}else{
				int tempLeftStone = fieldValue[tempLeft];
				boolean tempPass = true;
				if(tempLeftStone == 0){
					// ������ ���̾���.
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
			// ���� ���� ����Ѵ�.
			if(tempRight == 0){
			}else{
				int tempRightStone = fieldValue[tempRight];
				boolean tempPass = true;
				if(tempRightStone == 0){
					// ������ ���� ����.
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
			
			// ��ΰ���Ѵ�.					
			if(isDead == false){									
				// �缮�� �ƴ�
				isDeadTot = false;	
			}else{	
				// �缮��
			}
				
		}	// for
		// �缮�� �ְ� �ڽ��� ���̶�� ���� ���� �� ����.
		if(isDeadTot == true){
			for(int j = 0; j < 400; j++){
				if( mem1[j] == 0){
					continue;
				}else{
					// ���� ���� �� ����.
					fieldValue[mem1[j]] = 0;
					return false;
				}
			}
		}
		return true;
	}

	// ���� ���� ����Ѵ�.
	public void calcEnd(){
		for(int x = 1; x <= 19; x++){
			for(int y = 1; y <=19; y++){
				int fieldIndex = (x - 1) * 20 + y;
				// �缮�� 2������ ���ȴ� (���� ������)
				if(fieldValue[fieldIndex] == 3 ){
					whiteStoneEnd +=2;
				}else if(fieldValue[fieldIndex] == 4 ){
					blackStoneEnd +=2;
				}else if(fieldValue[fieldIndex] == 0 ){
					boolean findStoneKind = false;
					if(findStoneKind == false){
						// �������� �̵��ϸ� �鵹�� �浹�� �ִ��� �˻��Ѵ�.
						for(int i = x; i >= 1; i--){
							if( fieldValue[(i - 1) * 20 + y] == 1 || 
								fieldValue[(i - 1) * 20 + y] == 2 ){
								// �����ִٸ� 5, ���� �ִٸ� 6�� �����Ѵ�.
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
						// ������ ������ ���ߴٸ� ���������� �̵��ϸ� ���� �ִ��� �˻��Ѵ�.
						for(int i = x; i <= 19; i++){
							if( fieldValue[(i - 1) * 20 + y]== 1 || 
								fieldValue[(i - 1) * 20 + y] == 2 ){
								// �����ִٸ� �浹�� ��� ���� 5, ������� �ִٸ� ����6���� �����Ѵ�.
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
						// ���� ���ٸ� ���� ���ʺ��� ���� ã�´�.
						for(int i = 1; i <= 19; i++){
							// ���� �࿡�� ����� ���� ã�´�. 
							for(int j = y; j >= 1 ; j--){
								if( fieldValue[(i - 1) * 20 + j] == 1 || 
									fieldValue[(i - 1) * 20 + j] == 2 ){
									// ���� �ִٸ� ���� ��� ����5, ���ϰ�� ���� 6���� �����Ѵ�. 
									findStoneKind = true;
									if( fieldValue[(i - 1) * 20 + j] == 1){
										fieldValue[fieldIndex] = 5;
										blackStoneEnd++;
									}else{
										fieldValue[fieldIndex] = 6;
										whiteStoneEnd++;
									}
									// ����������. 
									i = 20;
									j = 0;											
								}
							}
							if(findStoneKind == false){
								// ���� ã�� ���ߴٸ� �Ʒ��� ����ϸ� ��������. 
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
										// ����������.
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
	// ����� ���� �д´�.
	public int getTop(int inputValue){
		int tempValue = inputValue - 20;
		if(tempValue < 0)
			tempValue = 0;
		else if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// �ϴ��� ���� �д´�.
	public int getBottom(int inputValue){
		int tempValue = inputValue + 20;
		if(tempValue > 379)
			tempValue = 0;
		return tempValue;
	}
	// �������� �д´�.
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
	// �������� �д´�.		
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
	// �缮���Ÿ�忡�� ���� ������. 
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
	//���� ���´�. 
	public boolean setDol(int x,int y, int inputValue){
		if(inputValue == 1){
			myStone = 1;
			yourStone = 2;
		}else{
			myStone = 2;
			yourStone = 1;
		}
		if( fieldValue[(x -1) * 20 + y ] != 0){
			System.out.println("���� �ֽ��ϴ�.");
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
	// �缮���
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
	// �ٵ��ǿ� ��ġ�� ���� ��ü������ ��ȯ�Ѵ�.
	public int[][] getStoneState(){
		int[][] returnStone = new int[20][20];
		for(int x = 1; x <= 19; x++){
			for(int y = 1; y <=19; y++){
				returnStone[x][y] = fieldValue[ (x - 1) * 20 + y ];
			}
		}
		return returnStone;
	}
	
	public static void main(String[] arg)
	{
		StoneInfo baduk = new StoneInfo();

		String inputX = "";
		String inputY = "";
		String inputValue = "";
		
		/*
		// �׽�Ʈ
		baduk.setDol(2,2,2);
		baduk.setDol(2,1,1);
		baduk.setDol(1,2,1);
		baduk.setDol(3,2,1);
		*/
		// �׽�Ʈ�� ���� ���� ���´�.
		baduk.setDol(1,3,1);
		baduk.setDol(2,3,1);
		baduk.setDol(3,3,1);
		baduk.setDol(4,3,1);
		baduk.setDol(5,3,1);
		baduk.setDol(5,2,1);
		baduk.setDol(5,1,1);
		
		baduk.setDol(1,4,2);
		baduk.setDol(2,4,2);
		baduk.setDol(3,4,2);
		baduk.setDol(4,4,2);
		baduk.setDol(5,4,2);
		baduk.setDol(6,4,2);
		baduk.setDol(6,3,2);
		baduk.setDol(6,2,2);
		baduk.setDol(6,1,2);
		
		baduk.setDol(1,1,2);
		
		
		BufferedReader in = 
			new BufferedReader(new InputStreamReader(System.in));
		int[][] StoneState = new int[20][20];
		while(true)
		{
			try
			{		
				StoneState = baduk.getStoneState();
				for(int i = 1; i<=19; i++)
				{
					for(int j = 1; j<= 19; j++)
					{
						System.out.print(StoneState[i][j] + " ");
					}
					System.out.println("");
				}	
				System.out.print("X��ǥ:");
				inputX = in.readLine();
				System.out.print("Y��ǥ:");
				inputY = in.readLine();
				System.out.print("�浹(1), �鵹(2):");
				inputValue= in.readLine();
				if(inputX == "0" && inputY == "0")
				{
					System.out.println("�Է°�����");
					return;
				}
				if(inputX.equals("end"))				
				{
					baduk.calcEnd();
				}				
				else if(inputValue.equals("1") || inputValue.equals("2"))
				{
				    baduk.setDol(inputX,inputY,inputValue);
				}
				else if(inputValue.equals("3") || inputValue.equals("4"))
				{
				    baduk.setDolChgDead(inputX,inputY);
				}

							
			}
			catch(Exception e)
			{
				System.out.println("Error: " + e.toString());
			}			
		}			
		
	}
}