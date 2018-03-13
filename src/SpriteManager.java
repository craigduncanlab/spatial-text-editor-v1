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