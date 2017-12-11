/* A Class to hold details for management of the main Clause Sandbox Window */

public class SpriteManager {

//Stackbox boxWithFocus = null;
int currentBoxX=0;
int currentBoxY=0;
SpriteBox currentSprite = null;


//constructor
public SpriteManager() {

}


public void setCurrentSprite(SpriteBox mySprite) {

	this.currentSprite=mySprite;
}

public SpriteBox getCurrentSprite(){

	return this.currentSprite;
}

public int[] getXY() {
        return new int[]{this.currentBoxX,this.currentBoxY};
    }

public void setXY(int x, int y) {
        this.currentBoxX=x;
        this.currentBoxY=y;
    }
  
/* Increment XY position for next box to be added
TO DO:  keep track of last box as it moves and add new box relative to last box
*/

public int[] incrementXY() {
        /*OLD - tiled

        if (currentBoxX>440) {
                currentBoxY=currentBoxY+65;
                currentBoxX=0;
            }
            else {
                currentBoxX = currentBoxX+160;
            }
		*/
        currentBoxY=currentBoxY+65;

        return new int[]{this.currentBoxX,this.currentBoxY};
    }
 
}