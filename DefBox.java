import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class DefBox extends Rectangle{         

    public DefBox() {
    	Color myColour = Color.BLUE; //constructor using constants (prebuilt)
        setWidth(150);
        setHeight(60);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);
     }    
}
