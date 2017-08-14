
package com.view.progresscircleview;


public class XChartCalc {
	
	//Positionλ��  
    private float posX = 0.0f;  
    private float posY = 0.0f;  
      
    public XChartCalc()  
    {  
          
    }  
              
      

    public void CalcArcEndPointXY(float cirX, float cirY, float radius, float cirAngle){  
  
        //���Ƕ�ת��Ϊ����        
        float arcAngle = (float) (Math.PI * cirAngle / 180.0);
        if (cirAngle < 90)  
        {  
            posX = cirX + (float)(Math.cos(arcAngle)) * radius;
            posY = cirY + (float)(Math.sin(arcAngle)) * radius;
        }  
        else if (cirAngle == 90)  
        {  
            posX = cirX;  
            posY = cirY + radius;  
        }  
        else if (cirAngle > 90 && cirAngle < 180)  
        {  
            arcAngle = (float) (Math.PI * (180 - cirAngle) / 180.0);
            posX = cirX - (float)(Math.cos(arcAngle)) * radius;
            posY = cirY + (float)(Math.sin(arcAngle)) * radius;
        }  
        else if (cirAngle == 180)  
        {  
            posX = cirX - radius;  
            posY = cirY;  
        }  
        else if (cirAngle > 180 && cirAngle < 270)  
        {  
            arcAngle = (float) (Math.PI * (cirAngle - 180) / 180.0);
            posX = cirX - (float)(Math.cos(arcAngle)) * radius;
            posY = cirY - (float)(Math.sin(arcAngle)) * radius;
        }  
        else if (cirAngle == 270)  
        {  
            posX = cirX;  
            posY = cirY - radius;  
        }  
        else  
        {  
            arcAngle = (float) (Math.PI * (360 - cirAngle) / 180.0);
            posX = cirX + (float)(Math.cos(arcAngle)) * radius;
            posY = cirY - (float)(Math.sin(arcAngle)) * radius;
        }  
                  
    }  
  
  
    //////////////  
    public float getPosX() {  
        return posX;  
    }  
  
  
    public float getPosY() {  
        return posY;  
    }

}
