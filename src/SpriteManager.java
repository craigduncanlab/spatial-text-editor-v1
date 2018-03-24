/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

public class SpriteManager {

//Stackbox boxWithFocus = null;
SpriteBox currentSprite = null;
SpriteBox targetSprite = null;
double stageX = 0;
double stageY = 0;
double LibX = 0;
double LibY = 0;
String StageFocus = "";


//constructor
public SpriteManager() {

}


public void setCurrentSprite(SpriteBox mySprite) {

	this.currentSprite=mySprite;
}

/* Set target sprite.  The Target Sprite differs from 'current sprite' in that it is set 
here, in the Sprite Manager and isn't necessarily the 'current sprite' triggered by a mouse event.
Target sprite is the last tagged sprite e.g. the last sprite that had its red alert triggered?
e.g. current sprite could be a box that is merely dragged, but not 'selected'.
No need for this distintion?  Just change focus when touched?
*/

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

//reset library window base coordinates 

public void resetLibXY() {
        this.LibX = 0;
        this.LibY = 0;
}

/* 
    Method to vary position of latest Sprite added to Stage.
    Sprite is added separately via adding child node to Workgroup
    TO DO: add to most recent Sprite
*/

public void placeOnMainStage(SpriteBox mySprite) {
        
         if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        this.stageX=this.stageX+50;
        this.stageY=this.stageY+50;
        mySprite.setTranslateX(this.stageX);
        mySprite.setTranslateY(this.stageY); 
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        this.currentSprite.setOnStage(true);
    }

// This can be used for both new boxes and a box being moved

public void placeInLibrary(SpriteBox mySprite) {
        
        if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        //set position relative to Library Window
        this.LibX=this.LibX+50;
        this.LibY=this.LibY+50;
        mySprite.setTranslateX(this.LibX);
        mySprite.setTranslateY(this.LibY); 
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        //TO DO: if this is a move then this might be needed : this.currentSprite.setOnStage(false);
        this.currentSprite.setInLibrary(true);
    }

public void setStageFocus(String myFocus) {
    this.StageFocus=myFocus;
}

public String getStageFocus() {
    return this.StageFocus;
}
 
}