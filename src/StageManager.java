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

26.4.18
Most of the functions are intended to be used with a stage that displays a 'node'.
In effect, this class helps make a GUI: to create a Stage that will display a node, its text and its child nodes, and allow editing
It also performs tracking of the stage (open node window) with current focus.
The Workspace is an instance of this class but uses far fewer helper functions.

The stages are iterative: in creating new child node boxes, each box can open a new node editing window
Therefore, the StageManager is like a visual tree navigator for the node data.
A node or Stage does not require opening up a separate 'edit' window because each node viewer's design is informative and functional.
(To do: Consider if "NodeViewer" is a better class name.  Nodes represent abstract 'frames'
A display option is to have background colour of node editor change for different types/levels)

The stage manager will provide its own GUI functions for updating the node's text.

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
//Scene - general appearance & layout of Background Fills, Stages, nodes
import javafx.scene.layout.Region;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane; //these still have individual positions (like Sprites)
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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
SpriteBox parentBox;//to hold the calling box for this viewer.  
//Do not create new object here or circular constructors! Do in constructor

String filename = ""; //current filename for saving this stage's contents
//STAGE IDS
int location = 0;
String category="";
//Displayed ClauseContainer (i.e. Node).  Will be updated through GUI.
ClauseContainer displayNode = new ClauseContainer();
int doccount=0; //document counter for this stage

//NODE VIEWER DIMENSIONS
int nodeViewWidth = 600;
int nodeViewHeight = 600;

//NODE'S TEXT CONTENT
//For storing main text output area for this Stage (if any)
//As of 26.4.2018: make this the default area to hold the node's own text (for stages that display a frame that is also an open node).  Always editable.

//This TextArea is the GUI display object for the nodes' docnotes String.  Edit button will update the node's (ClauseContainer) actual data
TextArea shortnameTextArea = new TextArea();
TextArea headingTextArea = new TextArea();
TextArea inputTextArea = new TextArea();
TextArea outputTextArea = new TextArea();
Text parentBoxText;
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


//Track current stage that is open.
static StageManager currentFocus; //any StageManager can set this to itself

//constructor
public StageManager() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//constructor with category etc
/*public StageManager(String category) {
    //this.globalTracker=tracker;
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
    setCategory(category);
}
*/

//any instance can return the global variable with focus stage
public StageManager getCurrentFocus() {
    return currentFocus; //notice not a 'this' as not an instance
}

//any instance can return the global variable with focus stage
public void setCurrentFocus(StageManager mySM) {
    currentFocus = mySM; //notice not a 'this' as not an instance
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
public void setTextAreaLayout() {
    headingTextArea.setPrefRowCount(1);
    shortnameTextArea.setPrefRowCount(1);
    //inputTextArea  = makeTextArea();
}

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

//set the identified JavaFX object (TextArea) for the Stage
public void setStageTextArea(TextArea myTA) {
    this.inputTextArea = myTA;
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

/* --- BASIC GUI SETUP --- */
public void updateView() {
    makeSceneForNodeEdit();
    resetSpriteOrigin();
    //title bar
    setTitle(displayNode.getType());
    //path
    //Set text to identify Stage containing parent box
    SpriteBox parBX = getParentBox();
    String parentSTR="";
    if (parBX==null) {
        parentSTR="[WS]";
    }
    else {
        if (parBX.getStageLocation()!=null) {
            parentSTR=parBX.getStageLocation().getTitle();
        }
        /*
        if(parBX.getBoxDocName()!=null) {
            parentSTR=parBX.getBoxDocName();

        }
        */
    }
    String pathText = parentSTR+"-->"+displayNode.getDocName()+"(contents)"; 
    parentBoxText.setText(pathText);
    //main node contents (text)
    shortnameTextArea.setText(displayNode.getDocName());
    headingTextArea.setText(displayNode.getHeading());
    inputTextArea.setText(displayNode.getNotes());
    //output node contents
    outputTextArea.setText(displayNode.getOutputText());
    //child nodes
    displayBoxesOnStage();
}

/* ----- DATA NODE FUNCTIONS ----- */

/* 
This method sets the node that is used for the display in this stage.
All other nodes added to this node are considered child nodes of this node:
they are added as child nodes to the data node; they are display in the section of the stage for this

*/

private void setDisplayNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    updateView();
}

//specific method for creating initial workspace view
public void setWSNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    //String myFileLabel = myNode.getDocName();
    String myFileLabel = "workspace";
    setFilename(myFileLabel+".ser"); //default
    //updateView();
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

public void setParentBox (SpriteBox myPB) {
    this.parentBox = myPB;
    ClauseContainer myNode = myPB.getBoxNode();
    setDisplayNode(myNode);
}

public SpriteBox getParentBox () {
    return this.parentBox;
}

public void openBoxesOnStage(ClauseContainer myNode) {

    setDisplayNode(myNode);
}

/* Altenative: open default Node that is stored here */
/*
public SpriteBox openDisplayNodeOnStage() {

    ClauseContainer myNode = getDisplayNode();
    resetSpriteOrigin();
    defaultConfigStage();
    setTitle(myNode.getDocName());
    //setDisplayNode(myNode);
    SpriteBox b = displayBoxesOnStage(myNode);
    return b;
}
*/
/* Box up a container of Sprites and place on Stage */

 private void displayBoxesOnStage() {
    
        ClauseContainer myNode = displayNode;
        //SpriteBox lastBox = new SpriteBox();
        ArrayList<ClauseContainer> myNodes = myNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = myNodes.iterator();

        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            SpriteBox b = makeBoxWithNode(thisNode); //relies on Main, event handlers x
            addSpriteToStage(b); //differs from Main 
            setFocusBox(b); 
        }
        showStage();
        //return getFocusBox();
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

/*Method to set parent stage.  Call this before showing stage 

This is for GUI relationships, not data tree relationships.

nb If the stage has been called from a SpriteBox, the tree parent is the box, but
that box lies within a stage that can be used as parent stage here
(or make all stages the child of Stage_WS)
*/
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

            case "workspace":
                setStagePosition(0,0);
                stageBack();
                break;

            case "editor":
                //myStage.initOwner(Parent);  //this must be called before '.show()' on child
                setStagePosition(850,0);
                stageFront();
                break;

            case "project":
                setStagePosition(800,300);
                stageFront();
                break;

            case "project library":
                setStagePosition(800,300);
                stageFront();
                break;

            case "library":
                setStagePosition(1000,300);
                stageFront();
                break;

            case "collection":
                setStagePosition(800,100);
                stageFront();
                break;
                
            case "document":
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

//public interface setter helper - currently not used

public void setInitStage(StageManager myParentSM, Stage myStage, Group myGroup, String myTitle) {
   setStageName(myTitle);
   setStage(myStage);
   setStageParent(myParentSM);
   setPosition(); 
   setSpriteGroup(myGroup);
   setTitle(myTitle);
}

//method replaces scene in current stage with the default node editing screen
//does not create the stage in this method as the localStage is a StageManager instance variable
private void defaultConfigStage() {
    //Scene myScene = makeSceneForBoxes(makeScrollGroup());
    
    //Stage myStage = setupNewSpriteStage(myScene);
}

/* Method to make new Scene with known Group for Sprite display */
public ScrollPane makeScrollGroup () {
    Group myGroup = new Group();
    setSpriteGroup(myGroup); 
    ScrollPane outerScroll = new ScrollPane();
    outerScroll.setContent(myGroup);
    return outerScroll;
}

/* Method to make new TextArea that has associated functions in this class */
public TextArea makeTextArea() {
    TextArea tempTextArea = new TextArea();
    setStageTextArea(tempTextArea); 
    return tempTextArea;
}

//The scene only contains a pane to display sprite boxes
private Scene makeSceneForBoxes(ScrollPane myPane) {
        
        Scene tempScene = new Scene (myPane,650,400); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on SM scene detected! " + mouseEvent.getSource());
         //setStageFocus("document");
             }
        });
        setSceneInStage(tempScene);
        return tempScene;
}

//The scene contains a text area, a pane to display sprite boxes and an Edit/Update button

private void makeSceneForNodeEdit() {
        
        ScrollPane tempPane = makeScrollGroup();
        setTextAreaLayout();
        //Button for saving clauses
        Button btnUpdate = new Button();
        btnUpdate.setText("Update");
        btnUpdate.setTooltip(new Tooltip ("Press to Save current edits"));
        btnUpdate.setOnAction(UpdateNodeText);
        //Button for cancel
        Button btnEditCancel = new Button();
        btnEditCancel.setText("Cancel Edits");
        btnEditCancel.setTooltip(new Tooltip ("Press to Cancel current edits"));
        //TO DO: set on action
      
        HBox hboxButtons = new HBox(0,btnUpdate,btnEditCancel);
        //
        parentBoxText = new Text();
        VBox allContent = new VBox(0,parentBoxText,shortnameTextArea,headingTextArea,inputTextArea,hboxButtons,tempPane,outputTextArea);
        //vboxAll.setPrefWidth(200);
        //
        Pane largePane = new Pane();
        largePane.getChildren().add(allContent); 
        Scene tempScene = new Scene (largePane,nodeViewWidth,nodeViewHeight); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on a node (StageManager scene) detected! " + mouseEvent.getSource());
         //setStageFocus("document");
         currentFocus=StageManager.this;
         }
        });
        addSceneToStage(tempScene);
        //setSceneInStage(tempScene);
        //return tempScene;
}

//Create Eventhandler to use with stages that allow edit button

EventHandler<ActionEvent> UpdateNodeText = 
        new EventHandler<ActionEvent>() {
        @Override 
        public void handle(ActionEvent event) {
            //main node contents (text)
            String editedName=shortnameTextArea.getText();
            String editedHeading=headingTextArea.getText();
            String editedText=inputTextArea.getText();
            String editedOutput=outputTextArea.getText();
            //
            displayNode.setDocName(editedName);
            //parentBox
            SpriteBox pntBox = getParentBox();
            pntBox.setLabel(editedName);
            displayNode.setHeading(editedHeading);
            displayNode.setNotes(editedText);
            displayNode.setOutputText(editedOutput);
            //
            System.out.println("Text in the update:"+editedText);
            System.out.println("Node updated!");
            System.out.println("Check update:"+displayNode.getNotes());
            /* myEditCC.setDocName(docnameEdit.getText());
            myEditCC.setAuthorName(authorEdit.getText());
            myEditCC.setNotes(notesEdit.getText());
            myEditCC.setDate(CCdateEdit.getText());
            System.out.println("Container updated!");
            //update the SpriteBox content with updated CC, which will update GUI
            SpriteBox focusSprite = getCurrentSprite();
            focusSprite.setBoxNode(myEditCC);
            */
            }
        };

/*
Method to setup single stage using Scene and Group - currently not used internally
obsolete external call
*/


public void setupNewSpriteStage(Scene myScene) {

    //Configure the Stage and its position/visibility
    Stage tempStage = new Stage();
    setStage(tempStage);
    //setStageParent(myParentSM);
    setSceneInStage(myScene);
    setPosition();
    tempStage.show();
}

/*
Method to setup single stage using Scene and Group - used only by WS stage
*/

public Stage makeWorkspaceStage(Scene myScene) {

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
    addChildNodeToDisplayNode(mySprite);
    positionSpriteOnStage(mySprite);
    setFocusBox(mySprite); //local information
    //TO DO: add Node as child to Parent
    mySprite.setStageLocation(StageManager.this); //give Sprite the object for use later.
}

public void addChildNodeToDisplayNode(SpriteBox mySprite) {
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
        mySprite.setStageLocation(StageManager.this); //needed if stage is not o/w tracked
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