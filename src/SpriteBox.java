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
import javafx.scene.text.*;
//Layout - use StackPane for now
import javafx.scene.layout.StackPane;
//Events
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
//Serializable
import java.io.Serializable;

/* This class started Nov 2017 

Purpose is to create the principal object for GUI windows.
It holds data structures (Clauses) and those internal properties (e.g. category field of Clause) can determine appearance of SpriteBox.
Its instance 'objects' include a Rectangle (extended: ColBox), Text and Colour objects (for GUI appearance).  These are Shapes/Text objects that JavaFX recognises as Nodes.

Within JavaFX this object is also a "Node"/Layout Object called "StackPane".
The adjustment of the position of this object will adjust position of all visible Nodes contained within it.
to visualise concepts within a GUI (therefore extends StackPane) but also as a container for content/data that can be controller through the object itself.

TO DO: distinguish private/public methods and write javadocs info

13.3.18 Consider making Spritebox capable of holding some basic object types:
e.g. Clauses, Definitions, Facts

13.3.18 modifications to implement serializable class, to assist with saving IDE layout & content
The basic concept is that the boxes and their contents will be saved.  Other functions can be run on the contents of the boxes (e.g. export)
If the location of each Spritebox can be saved (e.g. the parent window), this may assist with retrieval.

14.3.18 Add Library status
Query if stage and library status should also be in Clause instances

27.3.18 Consider if SpriteBox can also hold a Document (ClauseContainer)

*/

public class SpriteBox extends StackPane implements java.io.Serializable {         
    //instance variables are contained Nodes/Objects.
    //Not class variables as they are not 'static'
    ColBox myBox;
    Object BoxContent; //generic holder of content
    Clause myClause;
    ClauseContainer myDocument;
    String Category=""; //will be Clause, Definition etc
    Text boxtext = new Text ("empty box");//Default label text for every SpriteBox
    String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
    double Xpos = 0;
    double Ypos = 0;
    Boolean isAlert=false;
    Boolean OnStage=false;
    Boolean InLibrary=false;
    Boolean InCollection=false;
    Boolean InDocumentStage=false;
    Boolean OtherStage=false;
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
    
     /*Place an Object inside (e.g. handles subclasses Clause or ClauseContainer) */

    public void setBoxContent (Object myObject) {
        this.BoxContent = myObject;
        this.updateAppearance();
    }

     /*Return the Object inside (e.g. handles subclasses Clause or ClauseContainer) */

    public Object getBoxContent() {
        return this.BoxContent;
    }

    /*Return the Object inside (if ClauseContainer) */

    public ClauseContainer getCC() {
        if(this.BoxContent instanceof ClauseContainer) {
            return (ClauseContainer)this.BoxContent;
        }
        else {
            return new ClauseContainer(); //error?
        }
    }

    /* General setup with Clause inside */

    public void setup() {
        myBox = new ColBox();   //Uses defaults.
        myClause = new Clause(); //no details in clause yet; Null.
        Font boxfont=Font.font ("Verdana", 10);
        boxtext.setFont(boxfont);
        boxtext.setWrappingWidth(130);
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

    /* Status in workspace at any time */

    public Boolean isOnStage() {
        return this.OnStage;
    }

    /* Status in library at any time */
    //To do: make this generic windows?

    public Boolean isInLibrary() {
        return this.InLibrary;
    }

    public Boolean isInCollection() {
        return this.InCollection;
    }

    public Boolean isInDocumentStage() {
        return this.InDocumentStage;
    }

    public Boolean isInOtherStage() {
        return this.OtherStage;
    }

    /* This is set to true when Sprite/Clause is on Main Stage */ 

    public void setOnStage(Boolean myBool) {
        this.OnStage = myBool;
    }

    /* This is set to true when Sprite/Clause is in Library Window */ 

    public void setInLibrary(Boolean myBool) {
        this.InLibrary = myBool;
    }

    /* This is set to true when Sprite/Clause is in Collection Window */ 

    public void setInCollection(Boolean myBool) {
        this.InCollection = myBool;
    }

    /* This is set to true when Sprite/Clause is in Document Window */ 

    public void setInDocumentStage(Boolean myBool) {
        this.InDocumentStage = myBool;
    }

     public void setInOtherStage(Boolean myBool) {
        this.OtherStage = myBool;
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

    /*
    Appearance based on Clause properties/contents 
    */

    private void updateAppearance() {
        
        //if it happens to hold a ClauseContainer set it to dark blue
        Object testContent = this.getBoxContent();
        if (testContent instanceof ClauseContainer) {
            String cctype = ((ClauseContainer)testContent).getType();
            if (cctype.equals("library")) {
                this.SetColour("lemon");
            }
            else {
                this.SetColour("darkblue");
            }
            this.setLabel(((ClauseContainer)testContent).getDocName());
            /* TO DO: change shape for docs
            myBox.setWidth(90);
            myBox.setHeight(100);
            this.boxtext.setWrappingWidth(130);
            this.boxtext.setTextAlignment(TextAlignment.JUSTIFY);
            */
            return;
        }   
        else {
        //set Sprite label to Clause label
        this.boxtext.setText(this.myClause.getLabel());

        //otherwise, for now, set colour of SpriteBox based on Clause category
        // TO DO: Make this use 'instanceof' as the case
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
            case "event":
                this.SetColour("lightblue");
                break;
            default:
                this.SetColour("blue");
                break;
            }
        //to do : set shape based on category too
        }
    }

    /* ----  INTERNAL OBJECT DATA --- */

    /** Method to set individual parameters of internal Clause in SpriteBox */

    public void setInternalClause(String myLabel, String myHeading, String myText, String myCategory){
        this.myClause.setClausetext(myText);
        this.myClause.setClauselabel(myLabel);
        this.myClause.setHeading(myHeading);
        this.myClause.setCategory(myCategory);
        this.setBoxContent(myClause);

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
        if (inputClause.getLabel().length()>=50) {
            myClause.setClauselabel(inputClause.getLabel().substring(0,47)+"...");
        }
        this.setBoxContent(inputClause);
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
