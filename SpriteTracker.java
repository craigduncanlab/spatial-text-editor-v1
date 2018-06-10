/* Class to handle global active sprite tracking 

Operates as a controller class; listens for mouse clicks on Sprites and updates current sprite

*/

public class SpriteTracker {

SpriteBox activeSprite;

//constructor
public SpriteTracker() {

}

public void setCurrentSprite(SpriteBox myBox) {
	this.activeSprite=myBox;
}

public SpriteBox getCurrentSprite() {
	return this.activeSprite;
}

}