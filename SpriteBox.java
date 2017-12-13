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

Purpose is to create the principal object for GUI windows.
It holds data structures (Clauses) and those internal properties (e.g. category field of Clause) can determine appearance of SpriteBox.
Its instance 'objects' include a Rectangle (extended: ColBox), Text and Colour objects (for GUI appearance).  These are Shapes/Text objects that JavaFX recognises as Nodes.

Within JavaFX this object is also a "Node"/Layout Object called "StackPane".
The adjustment of the position of this object will adjust position of all visible Nodes contained within it.
to visualise concepts within a GUI (therefore extends StackPane) but also as a container for content/data that can be controller through the object itself.

TO DO: distinguish private/public methods and write javadocs info

*/

public class SpriteBox extends StackPane {         
    //instance variables are contained Nodes/Objects.
    //Not class variables as they are not 'static'
    ColBox myBox;
    Clause myClause;
    String Category=""; //will be Clause, Definition etc
    Text boxtext = new Text ("empty box");//Default label text for every SpriteBox
    String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
    double Xpos = 0;
    double Ypos = 0;
    Boolean isAlert=false;
    String defaultColour="";
    String alertColour="red";


    //basic default constructor
    public SpriteBox() {
        this.setup();
    }

    //default constructor with initial Clause included
    public SpriteBox(Clause inputClause) {
        this.setup();
        this.setClause(inputClause);
    }

    //default constructor with label
    public SpriteBox(String startLabel) {
    	this.setup();
        this.boxtext = new Text (startLabel);//myBox.getLabel();
        
     }  
    // constructor with colour
    public SpriteBox(String startLabel, String mycolour) {
        this.setup();
        this.myBox = new ColBox(mycolour);
        this.boxtext = new Text (startLabel);//myBox.getLabel();
     }
    
    /* General setup */

    public void setup() {
        myBox = new ColBox();   //Uses defaults.
        myClause = new Clause(); //no details in clause yet; Null.
        Font boxfont=Font.font ("Verdana", 10);
        boxtext.setFont(boxfont);
        this.setCursor(Cursor.HAND);
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

    /*Appearance based on Clause properties */

    private void updateAppearance() {
        //set Sprite label to Clause label
        this.boxtext.setText(this.myClause.getLabel());

        //set colour of SpriteBox based on Clause category
        switch(this.myClause.getCategory()){
            case "definition":
                this.SetColour("green");
                //
                //box setup to show freq of definitions as well
                String FreqCnt = Integer.toString(myClause.getFreq());
                this.boxtext.setText(this.myClause.getLabel()+"("+FreqCnt+")");
                break;
            case "clause":
                this.SetColour("yellow");
                break;
            case "legalrole":
                this.SetColour("orange");
                break;
            default:
                this.SetColour("blue");
                break;
            }
        //to do : set shape based on category too
        }

    /* ----  INTERNAL OBJECT DATA --- */

    /** Method to set individual parameters of internal Clause in SpriteBox */

    public void setInternalClause(String myLabel, String myHeading, String myText, String myCategory){
        this.myClause.setClausetext(myText);
        this.myClause.setClauselabel(myLabel);
        this.myClause.setHeading(myHeading);
        this.myClause.setCategory(myCategory);

        //sync relevant Spritebox appearance based on Clause variables
        this.setContent(myText);
        this.setCategory(myCategory);
        this.boxtext.setText(myLabel);
    }

    /* Add or remove internal Clause object 
    I could use the 'Category' property of this to make settings for the appearance of the SpriteBox.
    If categories of Clauses change, I can make a single change here.

    */
    public void setClause(Clause inputClause){
        this.myClause = inputClause;
        this.updateAppearance();
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
