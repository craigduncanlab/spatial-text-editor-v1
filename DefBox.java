import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class DefBox extends Rectangle{   
    //set default or current colour here?
    Color myColour; //colour for this instance
//default constructor
    public DefBox() {
    	this.setColour("blue");
        setWidth(150);
        setHeight(60);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        //-->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);

     }  

    //constructor with colour
    public DefBox(String mycol) {
        this.setColour("blue");
        this.setColour(mycol);
        setWidth(150);
        setHeight(60);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        //--->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);
     } 
     public void setColour (String mycol) {
        if (mycol.equals("blue")) {
            myColour = Color.BLUE;
        }
        if (mycol.equals("green")) {
            myColour = Color.GREEN;
        }
         if (mycol.equals("yellow")) {
            myColour = Color.YELLOW;
        }
        if (mycol.equals("red")) {
            myColour = Color.RED;
        }
        //update the Colour for display; allows for transparency
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));
     }

}
