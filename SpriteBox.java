//FX and events
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
//text
//Scene - Text as text, with font option
import javafx.scene.text.Text; 
import javafx.scene.text.Font;
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;
//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;

/* This class started Nov 2017 
Purpose is to create the principal object to visualise concepts within a GUI (therefore extends StackPane) but also as a container for content/data that can be controller through the object itself.

In many ways it acts as a Controller, and the Graphics Window/Scene is a Controller environment.

In the GUI, the position of instances of this class that are Java FX Nodes (Rectangle/Box and Label) can be sync'ed by changing the position of this object.
Event handlers will move the StackPane inclusive of box and text.

TO DO: 
Graphic window will allow new scene in which opening a SpriteBox reveals contents like clauses, defs, data, text.
in a similar graphic viewer.
Consider if 'contents' should be a more complex object, rather than text string.
Should the data structure be an array of say Clause (obj), DefsObjs (Array).  Each DefsObj has 'Item'?
Perhaps clauses shouldn't wrap Defs because Defs often have direct relationship to Items/Data.

*/

public class SpriteBox extends StackPane {         
    //instance variables are the graphic box and its text, and location.
    //Not shared with class unless 'static' applied.
    //StackPane boxPane = new StackPane(); 
    //These are the data objects held internally:
    ColBox myBox;
    Clause myClause;
    String Category=""; //will be Clause, Definition etc
    //These are the superficial sprite values - can sync with internal objects vice versa
    Text boxtext;
    String contents;  //This may not be needed - this is additional text to the Clause object etc
    double Xpos = 0;
    double Ypos = 0;
    Boolean isAlert=false;
    String defaultColour="";
    String alertColour="red";

    //default constructor
    public SpriteBox(String startLabel) {
    	myBox = new ColBox();
        myClause = new Clause();
        defaultColour="blue";
        this.setCursor(Cursor.HAND); 
        //set GUI text to passed string
        this.boxtext = new Text (startLabel);//myBox.getLabel();
        //just set size and use default font
        Font boxfont=Font.font ("Verdana", 10);
        boxtext.setFont(boxfont);
        //boxtext.setFont(new Font(10));
        /* set specific font and size
        double fontsize=10;
        Font boxfont = new Font ("Arial", fontsize);
        boxtext.setFont(boxfont);
        */
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }  
    // constructor with colour
    public SpriteBox(String startLabel, String mycolour) {
        //myBox = new ColBox(myText,mycolour);
        myClause = new Clause();
        myBox = new ColBox(mycolour);
        defaultColour=mycolour;
        this.setCursor(Cursor.HAND); 
        this.boxtext = new Text (startLabel);//myBox.getLabel();
        double fontsize=12;
        Font boxfont = new Font ("Arial", fontsize);
        boxtext.setFont(boxfont);
        //boxtext.setTextAlignment(TextAlignment.CENTER);
        this.getChildren().addAll(myBox,boxtext);
     }
    

     /* SUPERFICIAL SPRITE APPEARANCE */

    public double[] getXY() {
        return new double[]{this.Xpos,this.Ypos};
    }

    public double getX() {
        return this.Xpos;
    }

    public double getY() {
        return this.Ypos;
    }

    public void setXY(double x, double y) {
        this.Xpos=x;
        this.Ypos=y;
    }
     
    public Text getLabel() {
        return boxtext;
    }

    public void setLabel(String myString) {
        if (!myString.equals("")) {
            this.boxtext.setText(myString);
        }
    }

    public void setContent(String myString) {
        this.contents=myString;
    }

    public String getContent() {
        return this.contents;
    }

    public void SetColour(String mycol) {
        myBox.setColour(mycol);
        this.defaultColour=myBox.getColour();
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

    /* ----  INTERNAL OBJECT DATA --- */

    public void setInternalClause(String myLabel, String myHeading, String myText, String myCategory){
        this.myClause.setClausetext(myText);
        this.myClause.setClauselabel(myLabel);
        this.myClause.setHeading(myHeading);
        this.myClause.setCategory(myCategory);
        System.out.println("set internal clause category:"+myCategory);

        //sync relevant Spritebox appearance based on Clause variables
        this.setContent(myText);
        this.setCategory(myCategory);
        this.boxtext.setText(myLabel);
    }

    /* Add or remove internal Clause object */
    public void setClause(Clause inputClause){
        this.myClause = inputClause;
    }

    public Clause getClause() {
        return this.myClause;
    }

    /* Sync or obtain text from internal clause container */    

    public String getClauseText() {
        return this.myClause.getClause();
    }

    /* Sync internal clause container text with external data */  

    public void setClauseText(String myString) {
        this.myClause.setClausetext(myString);
        //sync the content of this spritebox too i.e. displayed in inspector
        //TO DO: inspector should look straight to clause text?  mirror for definitions? defs are stripped down clauses?
        this.setContent(myString);
    }

    /* Set label and sync internal clause container label with spritebox label */  

    public void setClauseLabel(String myString) {
        this.myClause.setClauselabel(myString);
        //this.setLabel(myString);  //in case you want box to have freq count on face of it etc leave off
    }

    /* Set / sync internal clause container heading */  

    public void setClauseHeading(String myString) {
        this.myClause.setHeading(myString);
    }

    public String getClauseHeading() {
        return this.myClause.getHeading();
    }

    /* Set / sync internal clause category */  

    public void setCategory(String myString) {
        this.myClause.setCategory(myString);  //sets internal clause category
        this.setCategory(myString); //sets sprite category to same.  needed?
    }


}
