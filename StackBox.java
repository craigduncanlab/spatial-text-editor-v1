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
//Event handlers will move the StackPane inclusive of box and text.


public class StackBox extends StackPane {         
    //instance variables are the graphic box and its text, and location
    //StackPane boxPane = new StackPane(); 
    DefBox myBox;
    Text myText;
    int XPos = 0;
    int YPos = 0;
    //variables for mouse events TO DO : RENAME
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;

    //default constructor
    public StackBox(String myText) {
    	myBox = new DefBox(myText);
        this.setCursor(Cursor.HAND); 
        Text boxtext = myBox.getText();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }  
    // constructor with colour
    public StackBox(String myText, String mycolour) {
        myBox = new DefBox(myText,mycolour);
        this.setCursor(Cursor.HAND); 
        Text boxtext = myBox.getText();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }

}
