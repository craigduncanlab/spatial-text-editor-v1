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

//This class creates a StackPane comprising a Rectangle (box) and a Text node, for ease of sync'd positioning
//It also includes basic contents and positino data structure, but this may be extended.
//Event handlers will move the StackPane inclusive of box and text.



public class StackBox extends StackPane {         
    //instance variables are the graphic box and its text, and location
    //StackPane boxPane = new StackPane(); 
    DefBox myBox;
    Text myLabel;
    String contents;
    int Xpos = 0;
    int Ypos = 0;
    Boolean isAlert=false;
    String defaultColour="";
    String alertColour="red";

    //default constructor
    public StackBox(String myLabel) {
    	myBox = new DefBox();
        defaultColour="blue";
        this.setCursor(Cursor.HAND); 
        Text boxtext = new Text (myLabel);//myBox.getLabel();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }  
    // constructor with colour
    public StackBox(String myLabel, String mycolour) {
        //myBox = new DefBox(myText,mycolour);
        myBox = new DefBox(mycolour);
        defaultColour=mycolour;
        this.setCursor(Cursor.HAND); 
        Text boxtext = new Text (myLabel);//myBox.getLabel();
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }
    
    public int[] getXY() {
        return new int[]{this.Xpos,this.Ypos};
    }

    public void setXY(int x, int y) {
        this.Xpos=x;
        this.Ypos=y;
    }
     
    public Text getLabel() {
        return myLabel;
    }

    public void setLabel(String myString) {
        this.myLabel.setText(myString);
    }

    public void setContent(String myString) {
        this.contents=myString;
    }

    public String getContent() {
        return this.contents;
    }

    public void SetColour(String mycol) {
        myBox.setColour(mycol);
    }

    public String getColour() {
        return myBox.getColour();
    }

    public Boolean isAlert() {
        return this.isAlert;
    }

    public void doAlert() {
        this.isAlert=true;
        myBox.setColour(alertColour);
    }
     public void endAlert() {
        this.isAlert=false;
        myBox.setColour(defaultColour);
    }

}
