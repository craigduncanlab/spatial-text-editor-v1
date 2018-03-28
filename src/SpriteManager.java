/* A Class to hold details for management of the main Clause Sandbox Window 
TO DO: Hold position information for saves? */

public class SpriteManager {

//Stackbox boxWithFocus = null;
SpriteBox currentSprite = null;
SpriteBox targetSprite = null;
double stageX = 0;
double stageY = 0;
double CollX = 0;
double CollY = 0;
double LibX = 0;
double LibY = 0;
double DocX = 0;
double DocY = 0;
double otherX=0;
double otherY=0;
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
No need for this distinction?  Just change focus when touched?
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

public void resetCollXY() {
        this.CollX = 0;
        this.CollY = 0;
}

public void resetOtherXY() {
        this.otherX = 0;
        this.otherY = 0;
}

public void resetDocXY() {
        this.DocX = 0;
        this.DocY = 0;
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

public void placeInCollection(SpriteBox mySprite) {
        
        if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        //set position relative to Library Window
        /*this.CollX=this.CollX+50;
        this.CollY=this.CollY+50;
        
        */
        mySprite.setTranslateX(this.CollX);
        mySprite.setTranslateY(this.CollY); 
        //
            if (this.CollX>440) {
                this.CollY=CollY+65;
                this.CollX=0;
            }
            else {
                this.CollX = CollX+160;
            }
        
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        //TO DO: if this is a move then this might be needed : this.currentSprite.setOnStage(false);
        this.currentSprite.setInCollection(true);
    }

public void placeInLibrary(SpriteBox mySprite) {
        
        if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        //set position relative to Library Window
        //this.LibX=this.LibX+50;
        //this.LibY=this.LibY+50;
        mySprite.setTranslateX(this.LibX);
        mySprite.setTranslateY(this.LibY); 
          if (this.LibX>440) {
                this.LibY=LibY+65;
                this.LibX=0;
            }
            else {
                this.LibX = LibX+160;
            }
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        //TO DO: if this is a move then this might be needed : this.currentSprite.setOnStage(false);
        this.currentSprite.setInLibrary(true);
    }

public void placeInDocument(SpriteBox mySprite) {
        
        if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        //set position relative to Library Window
        //this.DocX=this.DocX+50;
        //this.DocY=this.DocY+50;
        mySprite.setTranslateX(this.DocX);
        mySprite.setTranslateY(this.DocY);
        if (this.DocX>440) {
                this.DocY=DocY+65;
                this.DocX=0;
            }
            else {
                this.DocX = DocX+160;
            } 
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        //TO DO: if this is a move then this might be needed : this.currentSprite.setOnStage(false);
        this.currentSprite.setInDocumentStage(true);
    }

public void placeInOtherStage(SpriteBox mySprite) {
        
        if (this.currentSprite!=null) {  //might be no current sprite if not dbl clicked
                this.currentSprite.endAlert();
        }
        //set position relative to Library Window
        //this.otherX=this.otherX+50;
        //this.otherY=this.otherY+50;
        mySprite.setTranslateX(this.otherX);
        mySprite.setTranslateY(this.otherY); 
        if (this.otherX>440) {
                this.otherY=otherY+65;
                this.otherX=0;
            }
            else {
                this.otherX = otherX+160;
            } 
        //
        this.setCurrentSprite(mySprite); 
        this.currentSprite.doAlert();
        //TO DO: if this is a move then this might be needed : this.currentSprite.setOnStage(false);
        this.currentSprite.setInOtherStage(true);
    }

public void setStageFocus(String myFocus) {
    this.StageFocus=myFocus;
}

public String getStageFocus() {
    return this.StageFocus;
}
 
}