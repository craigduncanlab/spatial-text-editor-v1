/* A Class to hold details for management of the main Clause Sandbox Window */

public class SpriteManager {

//Stackbox boxWithFocus = null;
SpriteBox currentSprite = null;
SpriteBox targetSprite = null;
String StageFocus = "";


//constructor
public SpriteManager() {

}


public void setCurrentSprite(SpriteBox mySprite) {

	this.currentSprite=mySprite;
}

public void setTargetSprite(SpriteBox mySprite) {
    this.targetSprite=mySprite;
}

public SpriteBox getCurrentSprite(){

	return this.currentSprite;
}

public double[] getXY() {
        double[] output = this.currentSprite.getXY();
        return output;
    }

public void setXY(double x, double y) {
        //sync
         this.currentSprite.setXY(x,y);

    }
  
/* Increment XY position for next box to be added but not current box.
TO DO:  keep track of last box as it moves and add new box relative to last box.
At moment, this is only the XY position of the sprite boxes in the Clause WIP area - so be careful.
It is only used when adding new clauses to that Stage.  For a more general management of Sprites in ANY stage,
more sophisticated tracking is needed.
*/

public void setAsTarget(SpriteBox mySprite) {
        /*OLD - tiled

        if (currentBoxX>440) {
                currentBoxY=currentBoxY+65;
                currentBoxX=0;
            }
            else {
                currentBoxX = currentBoxX+160;
            }
		*/
        double x=0;
        double y=0;
        double[] c;
        if (targetSprite!=null) {
            c = this.targetSprite.getXY();
            x = c[0]+15;
            y = c[1]+65;
        }
        //
        this.currentSprite=mySprite;
        this.targetSprite=mySprite; 
        this.setXY(x,y);
        this.currentSprite.setTranslateX(x);
        this.currentSprite.setTranslateY(y);  
    }

public void setStageFocus(String myFocus) {
    this.StageFocus=myFocus;
}

public String getStageFocus() {
    return this.StageFocus;
}
 
}