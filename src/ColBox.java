import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//for displaying images
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.ImagePattern;


public class ColBox extends Rectangle{   
    //set default or current colour here?
    Color myColour; //colour object for this instance
    String boxcolour; //to hold String with colour description.

    /* Some sample colours:
    "chocolate", "salmon", "gold", "coral", "darkorchid",
            "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue",
            "blueviolet", "brown");
    */

//default constructor
    public ColBox() {
    	this.setColour("salmon"); //default
        this.setWidth(150);
        this.setHeight(40);
        //setArcWidth(60);  //do this enough you get a circle.  option
        //setArcHeight(60);                
        //-->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);

     }  

    //constructor with colour
    public ColBox(String mycol) {
        this.setColour("salmon"); //default just in case
        this.setColour(mycol);
        setWidth(150);
        setHeight(40);
        /* image too.  
        //This works but proportions must be correct for rectangle if image used as a fill
        Image img = new Image("paper.png");
        this.setFill(new ImagePattern(img));
        */
        //setArcWidth(60);  //do this enough you get a circle
        //setArcHeight(60);                
        //--->setFill(myColour.deriveColor(0, 1.2, 1, 0.6));  //allows for transparency so order not so important
        //setFill(myColour);
        setStroke(Color.BLACK);
     } 

     //TO DO: Use key, value pairs
     //See https://docs.oracle.com/javase/8/javafx/api/javafx/scene/paint/Color.html

     public void setColour (String mycol) {
        if (mycol.equals("blue")) {
            myColour = Color.BLUE;
            this.boxcolour = mycol;
        }
        if (mycol.equals("darkblue")) {
            myColour = Color.DARKBLUE;
            this.boxcolour = mycol;
        }
        if (mycol.equals("lemon")) {
            myColour = Color.LEMONCHIFFON;
            this.boxcolour = mycol;
        }
        if (mycol.equals("lightblue")) {
            myColour = Color.LIGHTBLUE;
            this.boxcolour = mycol;
        }
        if (mycol.equals("green")) {
            myColour = Color.GREEN;
            this.boxcolour = mycol;
        }
         if (mycol.equals("yellow")) {
            myColour = Color.YELLOW;
            this.boxcolour = mycol;
        }
        if (mycol.equals("red")) {
            myColour = Color.RED;
            this.boxcolour = mycol;
        }
        if (mycol.equals("pink")) {
            myColour = Color.DEEPPINK;
            this.boxcolour = mycol;
        }
        if (mycol.equals("maroon")) {
            myColour = Color.MAROON;
            this.boxcolour = mycol;
        }
        if (mycol.equals("darkgold")) {
            myColour = Color.DARKGOLDENROD;
            this.boxcolour = mycol;
        }
        if (mycol.equals("khaki")) {
            myColour = Color.DARKKHAKI;
            this.boxcolour = mycol;
        }
        if (mycol.equals("orange")) {
            myColour = Color.ORANGE;
            this.boxcolour = mycol;
        }
        if (mycol.equals("salmon")) {
            myColour = Color.SALMON;
            this.boxcolour = mycol;
        }
        if (mycol.equals("gold")) {
            myColour = Color.GOLD;
            this.boxcolour = mycol;
        }
        if (mycol.equals("white")) {
            myColour = Color.WHITE;
            this.boxcolour = mycol;
        }

        //update the Colour for display; allows for transparency
        setFill(myColour.deriveColor(0, 1.2, 1, 0.6));
     }
     public String getColour() {
        return this.boxcolour;
    }
}
