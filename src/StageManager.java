/* 
Class to manage each Stage
30.3.2018
Until now, Stage Manager class was used as a singleton.
However, by creating a 'StageManager' object for each stage, it can keep Stage-specific information
and enormously reduce the complexity of stage position, current sprite location etc.

This is required because the Stage object in JavaFX defined for the GUI.
This class is a conceptual object that will hold not only the javaFX Stage object, but associated data

Requires stageID to be set at start of app.
The Group that is part of the JavaFX node tree to which SpriteBoxes are to be added can be stored here.
(i.e. this saves having to navigate through the GUI node instances to find it each time)

*/

//import utilities needed for Arrays lists etc
import java.util.*;
//JavaFX
import javafx.stage.Stage;
import javafx.stage.Screen;
//Screen positioning
import javafx.geometry.Rectangle2D;
import javafx.geometry.Insets;
//Scene graph (nodes) and traversal
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.Node; 
import javafx.scene.Parent;
//Scene - Text as text
import javafx.scene.text.Text;  //nb you can't stack textarea and shape controls but this works
//Scene - Text controls 
import javafx.scene.control.ScrollPane; // This is still not considered 'layout' i.e. it's content
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//for UI and Mouse Click and Drag
import javafx.scene.input.MouseEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;



/* Stages will always be on top of the parent window.  This is important for layout
Make sure the smaller windows are owned by the larger window that is always visible
The owner must be initialized before the stage is made visible.
*/

public class StageManager {

//hold default Stage variables. TO DO: position relative to screen and then increment.
double latestX = 300;
double latestY = 3000;
String StageFocus = "";
Rectangle2D ScreenBounds = Screen.getPrimary().getVisualBounds();
double myBigX = ScreenBounds.getWidth();
double myBigY = ScreenBounds.getHeight();
ArrayList<Stage> myStageList = new ArrayList<Stage>();
int spriteX = 0;
int spriteY = 0;
String stageName = "";
String stageTitle = "";

ClauseContainer reference_ParentNode = new ClauseContainer();
Stage localStage = new Stage();
Node rootNode; //Use Javafx object type
Group spriteGroup = new Group();
Scene spriteScene;
SpriteBox focusbox; //for holding active sprite in this scene.  Pass to app.
//SpriteTracker globalTracker;

String filename = ""; //current filename for saving this stage's contents
//STAGE IDS
int location = 0;
String category="";
//Displayed ClauseContainer (i.e. Node).  Will be updated through GUI.
ClauseContainer displayNode = new ClauseContainer();
int doccount=0; //document counter for this stage


//For storing main text input area for this Stage (if any)

//For storing main text output area for this Stage (if any)
TextArea inputTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
//Store the common event handlers here for use
EventHandler<MouseEvent> PressBox;
EventHandler<MouseEvent> DragBox;


/*
Data collection will parallel GUI display of boxes. Provided stage manager can be serialised?
Can GUI info be transient or should it be serialised?
StageManager should store GUI objects in one way, data in another?  separation of concerns
Some kind of content manager for each stage?
Consider if subclasses of StageManager could deal with flavours of StageManager (e.g. position?
*/
ArrayList<Object> BoxContentsArray = new ArrayList<Object>(); //generic store of contents of boxes

//constructor
public StageManager() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//constructor with category etc
public StageManager(String category) {
    //this.globalTracker=tracker;
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
    setCategory(category);
}

//JAVAFX SCENE GRAPH GUI INFO (THIS IS NOT THE DATA NODE!)
public void setSceneRoot(Node myNode) {
    this.rootNode = myNode;
}

public Node getSceneRoot() {
    return this.rootNode;
}


//FILE I/O DATA
public String getFilename() {
    return this.filename;
}

public void setFilename(String myFile) {
    this.filename = myFile;
}

public int getDocCount() {
    return this.doccount;
}

public void resetDocCount() {
    this.doccount=0;
}

public int advanceDocCount() {
    return this.doccount++;
}

//JAVAFX SCROLLERS FOR TEXT OUTPUT - DEFAULT
//Method to operate on external object passed to function (does not return)
//to DO - separate JavaFX objects wrapper functions class?

//add scene to stage
public void putTextScrollerOnStage() {
    ScrollPane rootnode_scroll = new ScrollPane();
    configDefaultScroller(rootnode_scroll); //scroller with text
    Scene textOutputScene = makeSceneScrollerAsRoot(rootnode_scroll);
    Stage textOutputStage = new Stage();
    setSceneOnStage(textOutputScene, textOutputStage);
}

//make new scene with Scroller
private Scene makeSceneScrollerAsRoot (ScrollPane myRootNode) {

int setWidth=500;
int setHeight=250;
Scene myScene = new Scene (myRootNode,setWidth,setHeight); //width x height (px)
//this operates as a lambda - i.e events still detected by Main?
myScene.addEventFilter(MouseEvent.MOUSE_PRESSED, myMouseLambda);
return myScene;
}
 
public void setPressBox(EventHandler<MouseEvent> myEvent) {
    this.PressBox=myEvent;
}

public void setDragBox(EventHandler<MouseEvent> myEvent) {
    this.DragBox=myEvent;
}


EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
 System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

private void configDefaultScroller(ScrollPane myScroll) {
    myScroll.setFitToHeight(true);
    myScroll.setFitToWidth(true);
    //setup text scroll node
    double width = 600; 
    double height = 500; 
    myScroll.setPrefHeight(height);  
    myScroll.setPrefWidth(width);
    //set to this object's outputtext area
    myScroll.setContent(getOutputTextNode()); 
    setSceneRoot(myScroll);
}

//JAVA FX TEXT AREAS - GETTERS AND SETTERS
public void setOutputText(String myText) {
    outputTextArea.setText(myText);
}

public String getOutputText() {
    return outputTextArea.getText();
}

//Return the JavaFX object (Node) 
public TextArea getOutputTextNode() {
    return this.outputTextArea;
}

//Input text area e.g. importer
public void setInputText(String myText) {
    inputTextArea.setText(myText);
}

public String getInputText() {
    return inputTextArea.getText();
}

//Return the JavaFX object (Node) 
public TextArea getInputTextNode() {
    return this.inputTextArea;
}

//SIMPLE SCENE GETTERS AND SETTERS AS JAVA FX WRAPPER

public void setSceneOnStage (Scene myScene, Stage myStage) {
    setStage(myStage);
    addSceneToStage(myScene);
}

private void addSceneToStage (Scene myScene) {
     getStage().setScene(myScene); //JavaFX
     setSceneInStage(myScene);
}

//SIMPLE STAGE GETTERS AND SETTERS FOR CUSTOM GUI.  WRAPPER FOR JAVAFX SETTERS

public void setStageName(String myName) {
    this.stageName = myName;
    this.localStage.setTitle(myName);
}

public String getStageName() {
    return this.stageName;
}

//probably redundant - keep name or title
public void setTitle(String myTitle) {
    this.stageTitle = myTitle;
    this.localStage.setTitle(myTitle);
}

public String getTitle() {
    return this.stageTitle;
}

public void setCategory(String myCat) {
    this.category=myCat;
}

public String getCategory() {
    return this.category;
}

/* ----- DATA NODE FUNCTIONS ----- */

public void setDisplayNode(ClauseContainer myNode) {
    this.displayNode = myNode;
}

public ClauseContainer getDisplayNode() {
    return this.displayNode;
}

//set the parent node for Nodes enclosed in boxes (i.e. level above)
public void setRefParentNode(ClauseContainer myParentID) {
    this.reference_ParentNode = myParentID;
}

public ClauseContainer getRefParentNode() {
    return this.reference_ParentNode;
}

/* GUI FUNCTIONS FOR WORKING WITH BOXES, NODES */

public SpriteBox openBoxesOnStage(ClauseContainer myNode) {

    resetSpriteOrigin();
    defaultConfigStage();
    setTitle(myNode.getDocName());
    setDisplayNode(myNode);
    SpriteBox b = displayBoxesOnStage(myNode);
    return b;
}

/* Altenative: open default Node that is stored here */

public SpriteBox openDisplayNodeOnStage() {

    ClauseContainer myNode = getDisplayNode();
    resetSpriteOrigin();
    defaultConfigStage();
    setTitle(myNode.getDocName());
    //setDisplayNode(myNode);
    SpriteBox b = displayBoxesOnStage(myNode);
    return b;
}

/* Box up a container of Sprites and place on Stage */

 public SpriteBox displayBoxesOnStage(ClauseContainer myNode) {
    
        SpriteBox lastBox = new SpriteBox();
        ArrayList<ClauseContainer> myNodes = myNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = myNodes.iterator();

        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            SpriteBox b = makeBoxWithNode(thisNode); //relies on Main, event handlers x
            addSpriteToStage(b); //differs from Main 
            setFocusBox(b); 
        }
        showStage();
        return getFocusBox();
        }

private SpriteBox makeBoxWithNode(ClauseContainer node) {
    
    SpriteBox b = new SpriteBox();
    b.setOnMousePressed(PressBox); 
    b.setOnMouseDragged(DragBox);
    b.setBoxNode(node);
    return b;
}

/* ----- GENERAL GUI FUNCTIONS ----- */

//setter for the Stage
public void setStage(Stage myStage) {
    this.localStage = myStage;
}

//getter for the Stage
public Stage getStage() {
    return this.localStage;
}


//setter for the Scene
public void setSceneInStage(Scene myScene) {
    this.spriteScene = myScene;
    getStage().setScene(myScene);
}

//getter for the Scene
public Scene getSceneInStage() {
    return this.spriteScene;
}


//setter for the Group sprite boxes will be added to
public void setSpriteGroup(Group myGroup) {
    this.spriteGroup = myGroup;
}

//getter for the Group sprite boxes are added to
public Group getSpriteGroup() {
    return this.spriteGroup;
}

private void setStagePosition(double x, double y) {
    this.localStage.setX(x);
    this.localStage.setY(y);
}

private void stageBack() {
    this.localStage.toBack();
}

private void stageFront() {
    this.localStage.toFront();
}

//getters and setters
public void setCurrentXY(double x, double y) {

	this.latestX=x;
    this.latestY=y;
}

/*Method to set parents.  Call this before showing stage*/
public void setStageParent(StageManager ParentSM) {
    Stage myStage = getStage(); 
    Stage Parent = ParentSM.getStage();
    myStage.initOwner(Parent);
}


/* 

The order in which the Stages are created and set will determine initial z order for display
Earliest z is toward back
The workspace (WS) is, in effect, a large window placed at back.
TO DO: Make the MenuBar etc attach to a group that is at back,
then add WIP spritexboxes to a 'Document Group' that replaces Workspace with 'Document' menu

*/

//TO DO: set position based on StageManager category.
public void setPosition() {

    switch(this.stageName){

            case "Workspace":
                setStagePosition(0,0);
                stageBack();
                break;

            case "Editor":
                //myStage.initOwner(Parent);  //this must be called before '.show()' on child
                setStagePosition(850,0);
                stageFront();
                break;

            case "Project":
                setStagePosition(800,300);
                stageFront();
                break;

            case "Project Library":
                setStagePosition(800,300);
                stageFront();
                break;

            case "Library":
                setStagePosition(1000,300);
                stageFront();
                break;

            case "Collection":
                setStagePosition(800,100);
                stageFront();
                break;
                
            case "Document":
                setStagePosition(400,200);
                stageFront();
                break;

            case "Toolbar":
                setStagePosition(1000,50);
                stageFront();
                break;

            case "Output":  
                setStagePosition(150,550);
                stageFront();
                break;

            case "Import":
                setStagePosition(800,200);
                stageFront();
                break;
            
            default:
                setStagePosition(200,200);
                stageFront();
                break;
    }
}

//STAGE MANAGEMENT FUNCTIONS


public void showStage() {
    this.localStage.show(); 
}

public void hideStage() {
    this.localStage.hide(); 
}

public void toggleStage() {
    Stage myStage = getStage();         
    if (myStage==null) {
        System.out.println("Problem with Stage setup +"+myStage.toString());
    }
    if (myStage.isShowing()==false) {
        showStage();
        return;
    }
    if (myStage.isShowing()==true) {
        hideStage();
        return;
    }
}

//public interface setter helper
public void setInitStage(StageManager myParentSM, Stage myStage, Group myGroup, String myTitle) {
   setStageName(myTitle);
   setPosition();
   setStage(myStage);
   setStageParent(myParentSM);
   setSpriteGroup(myGroup);
   setTitle(myTitle);
}

//
public void defaultConfigStage() {
    Scene myScene = makeSceneForBoxes(makeScrollGroup());
    setupNewSpriteStage(myScene);
    setPosition();
    //tempStage.setTitle(getTitle());
}

/* Method to make new Scene with known Group for Sprite display */
public ScrollPane makeScrollGroup () {
    Group myGroup = new Group();
    setSpriteGroup(myGroup); 
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(myGroup);
    return outerScroll;
}

private Scene makeSceneForBoxes(ScrollPane myPane) {
        
        Scene tempScene = new Scene (myPane,650,400); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on scene detected! " + mouseEvent.getSource());
         //setStageFocus("document");
             }
        });
        setSceneInStage(tempScene);
        return tempScene;
}

/*
Method to setup single stage using Scene and Group
*/


public void setupNewSpriteStage(Scene myScene) {

    //Configure the Stage and its position/visibility
    Stage tempStage = new Stage();
    setSceneInStage(myScene);
    setPosition();
}

public Stage makeNewSpriteStage(Scene myScene) {

    Stage tempStage = new Stage();
    tempStage.setScene(myScene); //JavaFX: set current scene for the Stage
    setStage(tempStage);
    setSceneInStage(myScene);
    setPosition();
    tempStage.show();
    return tempStage;
}


//SPRITE BOX ASSIST FUNCTIONS

//Method to add sprite to the Group fo this Stage, and position it
public void addSpriteToStage(SpriteBox mySprite) {
    getSpriteGroup().getChildren().add(mySprite); 
    addNodeToDisplayNode(mySprite);
    positionSpriteOnStage(mySprite);
    setFocusBox(mySprite); //local information
    //TO DO: add Node as child to Parent
    mySprite.setStageLocation(StageManager.this); //give Sprite the object for use later.
}

public void addNodeToDisplayNode(SpriteBox mySprite) {
    getDisplayNode().addChildNode(mySprite.getBoxNode());
}

public void removeSpriteFromStage(SpriteBox thisSprite) {
    this.spriteGroup.getChildren().remove(thisSprite); 
    thisSprite.resetLocation();
     //TO DO: remove Node
}

public void setContentsArray(ArrayList<Object> inputObject) {
    this.BoxContentsArray = inputObject;
}

public ArrayList<Object> getContentsArray() {
    return this.BoxContentsArray;
}

public void positionSpriteOnStage(SpriteBox mySprite) {
        
        if (mySprite!=null) {  //might be no current sprite if not dbl clicked
                mySprite.endAlert();
        }
        advanceSpritePosition();
        System.out.println("advanced pos on sprite:"+mySprite.toString());
        mySprite.setTranslateX(spriteX);
        mySprite.setTranslateY(spriteY); 
        mySprite.setStageLocation(StageManager.this); //needed?
}

public void resetSpriteOrigin() {
    this.spriteY=0;
    this.spriteX=0;
}

private void advanceSpritePosition() {
        if (this.spriteX>440) {
                this.spriteY=spriteY+65;
                this.spriteX=0;
            }
            else {
                spriteX = spriteX+160;
            }
}

public void setFocusBox(SpriteBox myBox) {
    this.focusbox = myBox;
}

public SpriteBox getFocusBox() {
    return this.focusbox;
}

//max screen dimensions
public double getBigX() {
    return this.myBigX;
}


public double getBigY() {
    return this.myBigY;
}

}