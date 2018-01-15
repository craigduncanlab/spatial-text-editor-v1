//FX and events
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//text
//Scene - Text as text
import javafx.scene.text.Text; 
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;
//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
//don't use "extends DefBox" at this stage.  Might have to...in order to extend Rectangle - a Node for FX purposes

//This class creates a Pane made from a rectangular box and text, for make ease of sync'd positioning


public class StackBox extends StackPane {         
    //instance variables are the graphic box and its text, and location
    StackPane boxPane = new StackPane(); 
    DefBox myBox = new DefBox();
    Text myText = new Text();
    int XPos = 0;
    int YPos = 0;
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    public StackBox(String myText) {
    	myText.setText(myText);
        myBox.setCursor(Cursor.HAND); 
        //myText.setTextAlignment(TextAlignment.CENTER);
        boxPane.getChildren().addAll(myBox,myText);
     }    
}
