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
//For storing current Stage location
import javafx.stage.Stage;

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

3.4.18 By passing in the node GUI location, the SpriteBox can retrieve the appropriate StageManager
If we make the location an object, then the GUILocation object 
can also have the StageManager for that Location already packaged by the Node?.

*/

public class SpriteBox extends StackPane implements java.io.Serializable {         
    //instance variables are contained Nodes/Objects.
    //Not class variables as they are not 'static'
    ColBox myBox;
    ClauseContainer BoxNode; //generic holder of content
    Clause myClause;
    ClauseContainer myDocument;
    String Category=""; //will be Clause, Definition etc
    Text boxlabel = new Text ("new box");//Default label text for every SpriteBox
    String contents;  // Text for the SpriteBox outside of Clause objects.  Currently unused.
    double Xpos = 0;
    double Ypos = 0;
    Boolean isAlert=false;
    Boolean OnStage=false;
    Boolean InProject=false;
    Boolean InProjectLib=false;
    Boolean InLibrary=false;
    Boolean InCollection=false;
    Boolean InDocumentStage=false;
    Boolean OtherStage=false;
    String defaultColour="";
    String alertColour="red";
    StageManager StageLocation;
    StageManager childStage; //i.e. the nodeviewer stage
    //using alternate states representation for open window
    int location = 0;
    //mouse
    double orgSceneX, orgSceneY;
    double orgTranslateX, orgTranslateY;
    //
    String boxcategory=""; 
    //Track if this box has been opened to edit node or not.
    Boolean viewingNode=false;



    //basic default constructor
    public SpriteBox() {
        this.setup();
    }

    //Box constructor with Node .  To do.  Pass in info for event handlers needed.

    public SpriteBox(ClauseContainer node, StageManager mySM) {
    
    this.setup();
    location = node.getNodeLocation();
    String myCat = node.getNodeCategory(); //works with clausecontainer method
    SpriteBox.this.setStageLocation(mySM);
    SpriteBox.this.setOnMousePressed(PressBoxEventHandler);  // JavaFX - inherited from Rectangle 
    SpriteBox.this.setOnMouseDragged(DragBoxEventHandler);   //ditto
    SpriteBox.this.setBoxCategory(myCat); 
    SpriteBox.this.setBoxNode(node); //sets and updates appearance
    //return b;
}

//Box constructor that takes just Node

    public SpriteBox(ClauseContainer node) {
    
    this.setup();
    location = node.getNodeLocation();
    String myCat = node.getNodeCategory(); 
    SpriteBox.this.setBoxNode(node); //sets and updates appearance//works with clausecontainer method
    //SpriteBox.this.setStageLocation(mySM);
    SpriteBox.this.setOnMousePressed(PressBoxEventHandler);  // JavaFX - inherited from Rectangle 
    SpriteBox.this.setOnMouseDragged(DragBoxEventHandler);   //ditto
    SpriteBox.this.setBoxCategory(myCat); 
    
    //return b;
}



    //default constructor with initial Clause included _ DEPRECATE
    public SpriteBox(Clause inputClause) {
        this.setup();
        this.setClauseInLeafNode(inputClause);
    }

    //default constructor with label
    public SpriteBox(String startLabel) {
    	this.setup();
        this.boxlabel = new Text (startLabel);//myBox.getLabel();
        
     }  
    // constructor with colour
    public SpriteBox(String startLabel, String mycolour) {
        this.setup();
        this.myBox = new ColBox(mycolour);
        this.boxlabel = new Text (startLabel);//myBox.getLabel();
     }

     //SPRITEBOX STATUS: NODE OPEN OR NOT
     public Boolean isOpen() {
        return this.viewingNode;
     }

     public void setOpen() {
        this.viewingNode=true;
     }
    
    /*
     public StageManager getChildStage() {
        return this.childStage;
     }

     public void setChildStage(StageManager myCSM) {
        this.childStage = myCSM;
     }
     */

     //EVENT HANDLERS THAT PROVIDE CONTEXT

     EventHandler<MouseEvent> PressBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
         //current position of mouse
        orgSceneX = t.getSceneX();
        orgSceneY = t.getSceneY();

        //update the origin point to this click/press
        orgTranslateX = SpriteBox.this.getTranslateX(); //references this instance at Runtime
        orgTranslateY = SpriteBox.this.getTranslateY();
        t.consume();

        }
    };

    EventHandler<MouseEvent> DragBoxEventHandler = 
        new EventHandler<MouseEvent>() {
 
        @Override
        public void handle(MouseEvent t) {
            //TO DO: tell stage manager etc this box is active
            double offsetX = t.getSceneX() - orgSceneX;
            double offsetY = t.getSceneY() - orgSceneY;
            double newTranslateX = orgTranslateX + offsetX;
            double newTranslateY = orgTranslateY + offsetY;
            System.out.println("The local (#) Box handler for drag box is acting");
            //updates to sprite that triggered event
            SpriteBox.this.setTranslateX(newTranslateX);
            SpriteBox.this.setTranslateY(newTranslateY);
            SpriteBox.this.doAlert(); //in case single click event doesn't detect
            t.consume();//check
        }
    };


    /* GETTERS AND SETTERS FOR BOX ITSELF */

    public void setBoxCategory(String category) {
        this.boxcategory = category;
    }

    public String getBoxCategory() {
        return this.boxcategory;
    }

     /*Place an ClauseContainer inside (e.g. handles subclasses Clause or ClauseContainer) */

    public void setBoxNode (ClauseContainer myNode) {
        this.BoxNode = myNode;
        this.updateAppearance();
    }

     /*Return the ClauseContainer inside (e.g. handles subclasses Clause or ClauseContainer) */

    public ClauseContainer getBoxNode() {
        return this.BoxNode;
    }

    /*Return the ClauseContainer inside (if ClauseContainer) REDUNDANT */

    public ClauseContainer getCC() {
        if(this.BoxNode instanceof ClauseContainer) {
            return (ClauseContainer)this.BoxNode;
        }
        else {
            return new ClauseContainer(); //error?
        }
    }

    /* General setup with Clause inside */

    public void setup() {
        myBox = new ColBox();   //Uses defaults.
        myClause = new Clause(); //TO DO: remove this data item
        Font boxfont=Font.font ("Verdana", 10);
        boxlabel.setFont(boxfont);
        boxlabel.setWrappingWidth(130);
        this.setCursor(Cursor.HAND);
        this.getChildren().addAll(myBox,boxlabel); 
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
        return boxlabel;
    }

    public void setLabel(String myString) {
        if (!myString.equals("")) {
            this.boxlabel.setText(myString);
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
    }

    public void SetDefaultColour (String mycol) {
        this.defaultColour=mycol;
    }

    public String getColour() {
        return myBox.getColour();
    }

    //--- LOCATION SETTING/TESTING ---//

    public void setStageLocation(StageManager currentSM) {
        this.StageLocation = currentSM;
    }

    public StageManager getStageLocation() {
        return this.StageLocation;
    }

     /* Set index for location of spritebox (i.e. an open window or workspace) */
    public void resetLocation() {
        this.StageLocation = null;
        //
        this.location=0;
        //delete these when no longer needed:
        this.OnStage=false;
        this.InProject=false;
        this.InLibrary=false;
        this.InCollection=false;
        this.InDocumentStage=false;
        this.OtherStage=false;
    }


    public SpriteBox clone() {
        SpriteBox clone = new SpriteBox();
        clone.setup();
        clone.setStageID(getStageID());
        //do not use setClause first - will update BoxNode
        clone.setBoxNode(this.getBoxNode());
        return clone;
    } 

    //LOCATION SETTERS
     /* Simple setter to store stageID */
    public void setStageID(int myLoc) {
        this.location = myLoc;
    }

     /* Get currentStageID for this box */
    public int getStageID() {
        return this.location;
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

    private void updateboxlabel(ClauseContainer thisNode) {
    
        String thisboxlabel=thisNode.getDocName();
         //set label with Node DocName?
        if (thisboxlabel=="") {
            thisboxlabel="newobject";
        }
        this.setLabel(thisboxlabel); 
        
    }

    //get category from enclosed node
    private String getNodeCategory() {
        ClauseContainer thisNode = this.getBoxNode();
        return thisNode.getNodeCategory();
    }

    //get colour from enclosed nodecat instance in node
     private String getNodeColour() {
        ClauseContainer thisNode = this.getBoxNode();
        return thisNode.getNodeColour();
    }

    /*
    Appearance based on Clause properties/contents 
    */

    private void updateAppearance() {
        
        ClauseContainer thisNode = this.getBoxNode();
        updateboxlabel(thisNode);
        //String colour = getNodeCategory(thisNode);
        String thisboxcol = getNodeColour();
        this.SetColour(thisboxcol);
        this.SetDefaultColour(thisboxcol);
        //to do : set shape based on category too

        /*
        String thisboxcol="";
        switch(category){
            case "definition":
                thisboxcol="green";
                break;
            case "clause":
                thisboxcol="blue";
                break;
            case "library":
                thisboxcol="lemon";
                break;
            case "legalrole":
                thisboxcol="orange";
                break;
            case "event":
                thisboxcol="lightblue";
                break;
            default:
                thisboxcol="darkblue";
                break;
            }
            */
        
        }

    /* ----  INTERNAL OBJECT DATA --- */

    /** Method to set individual parameters of internal Clause in SpriteBox */

    public void setInternalClause(String myLabel, String myHeading, String myText, String myCategory){
        Clause tempClause = new Clause();
        this.myClause.setClauseText(myText);
        this.myClause.setClauselabel(myLabel);
        this.myClause.setHeading(myHeading);
        this.myClause.setCategory(myCategory);

        setClauseInLeafNode(tempClause);
    }

    /* Add or remove internal Clause of contained node.
    TO DO: remove duplicate clause object that is also in this SpriteBox.

    */
    public void setClauseInLeafNode(Clause inputClause){
        this.myClause = inputClause;
        if (inputClause.getLabel().length()>=50) {
            myClause.setClauselabel(inputClause.getLabel().substring(0,47)+"...");
        }
        ClauseContainer thisNode = this.getBoxNode();
        thisNode.addNodeClause(this.myClause);
        this.setBoxNode(thisNode);
        this.updateAppearance();
        }

    public Clause getClause() {
        return this.myClause;
    }

    /* Sync or obtain text from internal clause container */    

    public String getClauseText() {
        return this.myClause.getClauseText();
    }

    /* Sync internal clause container text with external data */  

    public void setClauseText(String myString) {
        this.myClause.setClauseText(myString);
        //sync the content of this spritebox too i.e. displayed in inspector
        //TO DO: inspector should look straight to clause text?  mirror for definitions? defs are stripped down clauses?
        this.setContent(myString);
    }

    /* Set label and sync internal clause container label with spritebox label */  

    public void setClauseLabel(String myString) {
        this.myClause.setClauselabel(myString);
        //this.setLabel(myString);  //in case you want box to have freq count on face of it etc leave off
    }

    public void setBoxLabel(String myString) {
        this.setLabel(myString); 
        //this.myClause.setClauselabel(myString);
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
