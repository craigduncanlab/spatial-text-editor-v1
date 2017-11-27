import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class DefBox extends Rectangle{   
    Text textInBox = new Text();   //can we just use string?  At moment, returns a JavaFX Node (Text) 

//default constructor
    public DefBox(String myText) {
        textInBox.setText(myText);
    	Color myColour = Color.BLUE; //constructor using constants (prebuilt)
        setWidth(150);
        setHeight(60);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);

     }  

    //constructor with colour
    public DefBox(String myText, String mycol) {
        textInBox.setText(myText);
        Color myColour = Color.BLUE; //constructor using constants (prebuilt)
        if (mycol.equals("green")) {
            myColour = Color.GREEN;
        }
         if (mycol.equals("yellow")) {
            myColour = Color.YELLOW;
        }
        setWidth(150);
        setHeight(60);
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);

     } 

    public Text getText() {
        return textInBox;
    }
}
