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
28.4.18
This is also possible with images and video:
Each node can hold 1 image see https://www.tutorialspoint.com/javafx/javafx_images.htm
https://docs.oracle.com/javase/8/javafx/media-tutorial/overview.htm

30.4.18
Provided user choice of views e.g. (a) node text/input, child nodes, output area (b) node text only (c) child nodes only 
Keys to cycle through that for any chosen node.  [Every node is an app, the app is flexible]
Easy to achieve through a MVC model.
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
//
import javafx.scene.shape.Rectangle;
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
import javafx.scene.control.Labeled;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.Cursor;
// event handlers
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
//Paint
import javafx.scene.paint.Color;
//Menus
import javafx.scene.control.MenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;



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
Group spriteGroup;
Pane spritePane;
Scene localScene;
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
//MenuBar
MenuBar localmenubar;
//User choice of view
String userNodeView;

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
static ClauseContainer currentTarget; //any Box(no?) or StageManager can set this to its display node

//constructor
public StageManager() {
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
}

//temporary constructor for old windows (toolbars, output etc)
public StageManager(StageManager parent, String myTitle) {
    Stage myStage = new Stage();
    setStage(myStage);
    setTitle(myTitle);
    setDragBox(DragBox);
    setJavaFXStageParent(parent);
    this.outputTextArea.setWrapText(true);
    this.inputTextArea.setWrapText(true);  //default
    //cycleUserView();
}

//standard open node viewer constructor, with only category and no content passed on.  Title?
public StageManager(StageManager parent, NodeCategory myCat, EventHandler PressBox, EventHandler DragBox) {
    //view
    setJavaFXStageParent(parent);
    setPressBox(PressBox);
    setDragBox(DragBox);
    setKeyPress(NodeKeyHandler); //this can be different for workspace
    //associate this Node Category with this StageManager (used with views etc)
    myCat.setCatViewer(StageManager.this);
    //data: new node based on category alone
    setDisplayNode(new ClauseContainer(myCat,"The holding area for all nodes of this category",myCat.getCategory()));
    //focus
    currentFocus=StageManager.this; //set focus on creation
    parent.setCurrentFocus(StageManager.this);//this duplicated previous line since class variable?
    //
    updateOpenNodeView(); //updates contents but doesn't show stage unless requested
    //showStage(); //to do: put default view in constructor
}

//standard open node viewer constructor using an existing Spritebox with node 
public StageManager(StageManager parent, SpriteBox myBox, EventHandler PressBox, EventHandler DragBox) {
    setJavaFXStageParent(parent);
    setParentBox(myBox); //data 
    //
    myBox.setChildStage(StageManager.this);
    setPressBox(PressBox);
    setDragBox(DragBox);
    setKeyPress(NodeKeyHandler); //this can be different for workspace
    //
    currentFocus=StageManager.this; //set focus on creation
    parent.setCurrentFocus(StageManager.this);//this duplicated previous line since class variable?
    updateOpenNodeView();
    showStage();
}

//workspace constructor.  Filename details will be inherited from loaded node.
//Passes MenuBar from main application for now
//Passes general eventhandlers from Main (at present, also uses these for the boxes)
public StageManager(String title, NodeCategory myCategory, MenuBar myMenu, EventHandler PressBox, EventHandler DragBox) {
    //view
    setTitle(title);
    setMenuBar(myMenu);
    setPressBox(PressBox);
    setDragBox(DragBox);
    newWorkstageFromGroup();
    currentFocus=StageManager.this; //set focus on creation  
    //data
    //ClauseContainer WorkspaceNode = ;
    setWSNode(new ClauseContainer(myCategory,"The workspace is base node of project.","myWorkspace")); //data
}

//GLOBAL view setting.  Make switch.
private void cycleUserView() {
    //handle null case
    if (userNodeView==null) {
        userNodeView="all";
    }
    if (userNodeView.equals("all")) {
        userNodeView="textonly";
        updateOpenNodeView();
        return;
    }
    if (userNodeView.equals("textonly")) {
        userNodeView="nodeboxesonly";
        updateOpenNodeView();
        return;
    }
    if (userNodeView.equals("nodeboxesonly")) {
        userNodeView="all";
        updateOpenNodeView();
        return;
    }
}

//any instance can return the global variable with focus stage
public StageManager getCurrentFocus() {
    return currentFocus; //notice not a 'this' as not an instance
}

//setter: should generally only set it to current instance
public void setCurrentFocus(StageManager mySM) {
    currentFocus = mySM; //notice not a 'this' as not an instance
}

public void setTargetByViewer(StageManager mySM) {
    currentTarget = mySM.getDisplayNode();
}

/* TENTATIVE - NOT USED? */
public void setTargetByBox(StageManager mySM) {
    currentTarget = mySM.getDisplayNode();
}

public ClauseContainer getCurrentTarget() {
    return currentTarget;
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
    storeSceneAndStage(textOutputScene, textOutputStage);
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

//Set key handler at level of stage in node editor
private void setKeyPress(EventHandler<KeyEvent> myKE) {
    getStage().addEventFilter(KeyEvent.KEY_PRESSED, NodeKeyHandler);
}

EventHandler myMouseLambda = new EventHandler<MouseEvent>() {
 @Override
 public void handle(MouseEvent mouseEvent) {
    System.out.println("Mouse click detected for text output window! " + mouseEvent.getSource());
     }
 };

 EventHandler<KeyEvent> NodeKeyHandler = new EventHandler<KeyEvent>() {
 @Override
 public void handle(KeyEvent ke) {
    System.out.println("Key Event on current Stage:"+StageManager.this.toString());
    System.out.println("Key Press (keycode):"+ke.getCode());
    //System.out.println("Key Press (keycode textual):"+ke.getCode().getKeyCode());
    System.out.println("Key Press (keycode name):"+ke.getCode().getName());
    System.out.println("Key Press (as string):"+ke.getCode().toString());
    System.out.println("KeyPress (as source): " + ke.getSource());
    System.out.println("KeyPress (as higher-level event type): " + ke.getEventType());
    System.out.println("KeyPress (unicode): " + ke.getCharacter());
    System.out.println("is Control Down: " + ke.isControlDown());
    System.out.println("is Meta(Command) Down: " + ke.isMetaDown());
    if (ke.isMetaDown() && ke.getCode().getName().equals("Z")) {
         System.out.println("CMND-Z pressed");
         cycleUserView();
    }
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

/* Text Area in JavaFX inherits selected text method from
javafx.scene.control.TextInputControl
*/

private String getSelectedInputText() {
    return inputTextArea.getSelectedText();
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

public void storeSceneAndStage (Scene myScene, Stage myStage) {
    setStage(myStage);
    updateScene(myScene);
}

private Scene getSceneLocal() {
    return this.localScene;
}

private Scene getSceneGUI () {
     return getStage().getScene(); //JavaFX
}

private void updateScene (Scene myScene) {
     getStage().setScene(myScene); //JavaFX in the GUI
     this.localScene = myScene; //local copy/reference
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
}

public String getTitle() {
    return this.stageTitle;
}

private void refreshTitle() {
    this.localStage.setTitle(getTitle());
}

public void setCategory(String myCat) {
    this.category=myCat;
}

public String getCategory() {
    return this.category;
}

//for passing in a menubar from main (for now: 29.4.18)
public void setMenuBar(MenuBar myMenu) {
    this.localmenubar = myMenu;
}

public MenuBar getMenuBar() {
    return this.localmenubar;
}

/* --- BASIC GUI SETUP FOR OPEN NODE VIEWERS --- */
private void updateOpenNodeView() {
    makeSceneForNodeEdit();
    resetSpriteOrigin();
    //title bar
    refreshTitle();
    //provide information about path of current open node in tree
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
    String pathText = parentSTR+"-->"+displayNode.getDocName()+"(viewing)"; 
    parentBoxText.setText(pathText);
    //REFRESHES ALL GUI DATA - EVEN IF NOT CURRENTLY VISIBLE
    
        inputTextArea.setText(displayNode.getNotes());

        shortnameTextArea.setText(displayNode.getDocName());
        headingTextArea.setText(displayNode.getHeading());
        
        //output node contents
        outputTextArea.setText(displayNode.getOutputText());
    
        displayChildNodeBoxes();

    }
/* ----- DATA (DISPLAY) NODE FUNCTIONS ----- */

/* 
This method sets the node that is used for the display in this stage.
All other nodes added to this node are considered child nodes of this node:
they are added as child nodes to the data node; they are display in the section of the stage for this

*/

private void setDisplayNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
}

public ClauseContainer getDisplayNode() {
    return this.displayNode;
}

public void addWSNode(ClauseContainer myNode) {

}

//Method to update workspace appearance based on current node setting (usually root of project)
public void setWSNode(ClauseContainer myNode) {
    this.displayNode = myNode;
    String myFileLabel = myNode.getDocName();
    setFilename(myFileLabel+".ser"); //default
    Group newGroup = new Group(); //new GUI node to show only new content.
    swapSpriteGroup(newGroup); //store the new GUI node for later use
    displayChildNodeBoxes(); //update WS view with new child boxes only
}

public void openNodeInViewer(ClauseContainer myNode) {

    setDisplayNode(myNode);
    updateOpenNodeView();
}

public ClauseContainer Node() {
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

/* Box up a container of Sprites and place on Stage 
Refreshes stage from display node, but doesn't show if invisible*/

 private void displayChildNodeBoxes() {
    
        ClauseContainer parentNode = displayNode;
        //SpriteBox lastBox = new SpriteBox();
        ArrayList<ClauseContainer> childNodes = parentNode.getChildNodes();
        Iterator<ClauseContainer> myiterator = childNodes.iterator();

        //only operates if there are Child Nodes to add
        while (myiterator.hasNext()) {
            ClauseContainer thisNode = myiterator.next(); 
            System.out.println("Current child node to be added: "+thisNode.toString());
            System.out.println("WS Viewer :"+StageManager.this);
            addNodeToView(thisNode);
        }
        //return getFocusBox();
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

/*
setter for the Group sprite boxes will be added to
*/
public void setSpriteGroup(Group myGroup) {
    this.spriteGroup = myGroup;
}

public Group getSpriteGroup() {
    return this.spriteGroup;
}

public void setSpritePane(Pane myPane) {
    this.spritePane = myPane;
}

public Pane getSpritePane() {
    return this.spritePane;
}

public void swapSpriteGroup(Group myGroup) {
    Pane myPane = getSpritePane();
    myPane.getChildren().remove(getSpriteGroup());
    setSpriteGroup(myGroup);
    myPane.getChildren().addAll(myGroup);
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
private void setJavaFXStageParent(StageManager ParentSM) {
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

//TO DO: set position based on NodeCat.
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
   setJavaFXStageParent(myParentSM);
   setPosition(); 
   setSpriteGroup(myGroup);
   setTitle(myTitle);
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
        updateScene(tempScene);
        return tempScene;
}

/* Method to build the viewer for the current open node.
Capable of showing a text area, a pane to display sprite boxes and an Edit/Update button
User can choose to see less (i.e. only work with some of what a node can contain)
i.e. can resemble a text editor, or graphical tree, or functional text processor with all three areas

State variable (userNodeView) defines which version of UI to display.
User can cycle through states of UI display through key press (CMD-Z)

*/

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
        //set view option
        VBox customView;
        //handle null case
        if (userNodeView==null) {
            userNodeView="all";
        }
        if (userNodeView.equals("textonly")) {
            System.out.println("Make Scene. User Node View: "+userNodeView);
            customView = new VBox(0,headingTextArea,inputTextArea,hboxButtons);
            setTitle(getDisplayNode().getDocName()+" - Text View");
        }
        else if(userNodeView.equals("nodeboxesonly")) {
            customView = new VBox(0,shortnameTextArea,hboxButtons,tempPane);
            System.out.println("Make Scene. User Node View: "+userNodeView);
            setTitle(getDisplayNode().getDocName()+" - Container View");
        }
            else {
            customView = new VBox(0,parentBoxText,shortnameTextArea,headingTextArea,inputTextArea,hboxButtons,tempPane,outputTextArea);
            System.out.println("Make Scene. User Node View: "+userNodeView);
            setTitle(getDisplayNode().getDocName()+" - Full View");
        }
        //vboxAll.setPrefWidth(200);
        //
        Pane largePane = new Pane();
        largePane.getChildren().add(customView); 
        Scene tempScene = new Scene (largePane,nodeViewWidth,nodeViewHeight); //default width x height (px)
        //add event handler for mouse event
        tempScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
         @Override
         public void handle(MouseEvent mouseEvent) {
         System.out.println("Mouse click on a node (StageManager scene) detected! " + mouseEvent.getSource());
         //setStageFocus("document");
         currentFocus=StageManager.this;
         //error checking i.e. like jUnit assert
         if (getCurrentFocus()==StageManager.this) {
            System.out.println("Change of Viewer Focus OK in Viewer!");
             System.out.println("makescene Viewer :"+StageManager.this);
         }
         else {
            System.out.println("Problem with change Viewer Focus");
            System.out.println("makescene Present Viewer :"+StageManager.this);
            System.out.println("Current Focus :"+getCurrentFocus());
         }
         }
        });
        updateScene(tempScene);
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
            //parentBox - should we insist on one?
            SpriteBox pntBox = getParentBox();
            if (pntBox!=null) {
                pntBox.setLabel(editedName);
            }
            displayNode.setHeading(editedHeading);
            displayNode.setNotes(editedText);
            displayNode.setOutputText(editedOutput);
            //error checking - log
            if (displayNode.getNotes().equals(editedText)) {
                System.out.println("Node updated OK!");
            }
            else {
                 System.out.println("Problem with node update.");
            }
            }
        };


private void newWorkstageFromGroup() {
    Group myGroup = makeWorkspaceTree();
    Scene myScene = makeWorkspaceScene(myGroup);
    Stage myStage = new Stage();
    setStage(myStage);
    updateScene(myScene);
    setPosition();
    showStage();
}

/* 

Java FX View setup:
Create root node and branches that is ready for placing in a Scene.

Sets up workspace stage with 2 subgroups for vertical separation:
(a) menu bar
(b) sprite display area, which is inside a border pane and group for layout reasons.

This method does not update content of the Sprite-display GUI node.

*/

private Group makeWorkspaceTree() {

        Group myGroup_root = new Group(); //for root node of Scene
        BorderPane myBP = new BorderPane(); //holds the menubar, spritegroup
        Group menubarGroup = new Group(); //subgroup
        MenuBar myMenu = getMenuBar();
        menubarGroup.getChildren().addAll(myMenu);
        
        //the Pane holding the group allows movement of SpriteBoxes independently, without relative movement
        
        Pane workspacePane = new Pane(); //to hold a group, holding a spritegroup
        Group displayAreaGroup = new Group(); //subgroup of Pane; where Sprites located
        
        workspacePane.getChildren().addAll(displayAreaGroup);
        setSpritePane(workspacePane); //store for later use
        setSpriteGroup(displayAreaGroup); //store for later use

        myBP.setTop(menubarGroup);
        myBP.setMargin(workspacePane, new Insets(50,50,50,50));
        myBP.setCenter(workspacePane);
        //workspacePane.setPadding(new Insets(150,150,150,150));
        
        //add the Border Pane and branches to root Group 
        myGroup_root.getChildren().addAll(myBP);
        //store the root node for future use
        setSceneRoot(myGroup_root); //store 
        //for box placement within the Scene - attach them to the correct Node.
        return myGroup_root;  
    }

private Scene makeWorkspaceScene(Group myGroup) {
        
        //construct scene with its root node
        Scene workspaceScene = new Scene (myGroup,getBigX(),getBigY(), Color.BEIGE);
        
        //nb do not change focus unless click on sprite group
        //Nodes etc inherit Event Target so you can check it in the event chain.
        
        //filter for capture, handler for sorting through the bubbling
        workspaceScene.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
             @Override
             public void handle(MouseEvent mouseEvent) {
             //System.out.println("Workspace Stage Mouse click detected! " + mouseEvent.getSource());
             //System.out.println("Workspace is "+StageManager.this.toString());
             //System.out.println("Here is the target: "+mouseEvent.getTarget());
             //System.out.println("Target class: "+mouseEvent.getTarget().getClass());
             if (getSceneGUI()!=getSceneLocal()) {
                  System.out.println("Problem with storing Scene");
             }
            /*
            areas and targets differ depending on objects on stage (invisible stretch)
            Ignore the MenuBar here
            JavaFX has ability to detect Text, Rectangle, ColBox (all components of a SpriteBox)
            Better to force it to detect a SpriteBox?
            Although clicking on text could be useful for updating headings/filenames
            It is possible to change focus with a click, but exclude MenuBar targets
            (these seemed to be instances of LabeledText 
            i.e. class com.sun.javafx.scene.control.LabeledText)
             */
            //if (mouseEvent.getTarget()==getSceneGUI()) {
            if (mouseEvent.getTarget() instanceof Scene) {
                System.out.println("Clicked on scene; updated focus");
                System.out.println("ws Viewer :"+StageManager.this);
                currentFocus=StageManager.this;
                mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof BorderPane) {
                 System.out.println("Clicked on Border Pane ; updated focus");
                 System.out.println("ws Viewer :"+StageManager.this);
                 currentFocus=StageManager.this;
                 mouseEvent.consume(); //to stop the bubbling?
            }
            else if (mouseEvent.getTarget() instanceof Pane) {
                 System.out.println("ws Clicked on Pane ; updated focus");
                 currentFocus=StageManager.this;
                 mouseEvent.consume(); //to stop the bubbling?
            }
            //to distinguish Text on Menu from Text on boxes you can interrogate what the Text is to see if it's a menu
            else if (mouseEvent.getTarget() instanceof Text) {
                 System.out.println("ws Clicked on Text ; no change to focus");
                 //currentFocus=StageManager.this;
            }
            else if (mouseEvent.getTarget() instanceof ColBox) {
                 System.out.println("ws Clicked on box ; updated focus");
                 System.out.println("ws Viewer :"+StageManager.this);
                 currentFocus=StageManager.this;
            }
            else if (mouseEvent.getTarget() instanceof Rectangle) {
                 System.out.println("ws Clicked on box ; updated focus");
                 currentFocus=StageManager.this;
            }
            else if (mouseEvent.getTarget() instanceof Labeled) {
                 System.out.println("ws Clicked on Labeled ; no change to focus");
                 //currentFocus=StageManager.this;
            }
            else {
                System.out.println("ws Click not identified : no change to focus");
            }

            }
        });

        workspaceScene.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
             @Override
             public void handle(KeyEvent keyEvent) {
             System.out.println("Key pressed on workspace stage " + keyEvent.getSource());
             //if source = ... only then change focus 
            }
        });
        
        return workspaceScene;
    }

//SPRITE BOX ASSIST FUNCTIONS

/* public function to add a box (as a child node) to this Viewer.
Add node to view will also call the addsprite to stage to complete this.
*/
public void addNewSpriteToStage(SpriteBox mySprite) {
        //mySprite.getBoxNode()
        addChildNodeToDisplayNode(mySprite.getBoxNode()); //data
        //addSpriteToStage(mySprite); //view 
        addNodeToView(mySprite.getBoxNode());
    }

/*
Internal method to add sprite to the Group/Pane of this Node Viewer 
This is to add an existing GUI 'box/node' to the Child Node section of this Viewer.
i.e. this adds a specific object, rather than updating the view from whole underlying data set.
*/

private void addSpriteToStage(SpriteBox mySprite) {
    getSpriteGroup().getChildren().add(mySprite); //GUI tree 
    System.out.println("Current sprite group is "+getSpriteGroup().toString()); 
    positionSpriteOnStage(mySprite);
    setFocusBox(mySprite); //local information
    mySprite.setStageLocation(StageManager.this); //give Sprite the object for use later.
}

//Method to add child node based on the contents of an identified NodeBox in GUI.
//also sets parent node of the node in the sprite box to this Stage Manager
/*private void addChildBoxToDisplayNode(SpriteBox mySprite) {
    getDisplayNode().addChildNode(mySprite.getBoxNode());
    mySprite.getBoxNode().setParentNode(getDisplayNode());
}
*/
//public method to allow Main controller to initiate child node creation in viewer

public void selectedAsChildNode() {
    String sampleText = getSelectedInputText();
    //construct new node using available inputs (i.e. suitable constructor)
    NodeCategory NC_clause = new NodeCategory ("clause",0,"blue"); //mirror main
    ClauseContainer myNode = new ClauseContainer(NC_clause,sampleText,sampleText.substring(0,8));
    //
    newNodeAsChildNode(myNode); //data and view for node viewer
}

//method to box up node as shape and add to GUI in node viewer

private void addNodeToView (ClauseContainer myNode) {
    //SpriteBox b = makeBoxWithNode(myNode); //relies on Main, event handlers x
    SpriteBox b = new SpriteBox(PressBox,DragBox,myNode);
    addSpriteToStage(b); //differs from Main 
    setFocusBox(b); 
}

//General method to add AND open a node if not on ws; otherwise place on workspace
//The StageManager arg passed in as myWS should be 'Stage_WS' for all calls 

public void OpenNewNodeNow(ClauseContainer targetNode, StageManager myWS) {
    System.out.println("OpenNewNode now...");
     if (StageManager.this.equals(myWS)) { 
     newNodeForWorkspace(targetNode);
     System.out.println("Adding new node to Workspace");
}
        else {
             newNodeAsChildNode(targetNode);
             System.out.println("Adding new node to stage (not WS)");
             System.out.println("sm.this in opennew, Viewer :"+StageManager.this.toString());
             System.out.println("myWS in opennew, Viewer :"+myWS.toString());
        }
}

/* This method adds the child nodes of the parentNode passed as arg 
to the Open Node, as its child nodes and then updates the view.
*/

public void addOpenNodeChildren (ClauseContainer parentNode) {
    getDisplayNode().addNodeChildren(parentNode);
    updateOpenNodeView();
}

/* This method adds a single node to workspace without refreshing entire view */

private void newNodeForWorkspace(ClauseContainer myNode) {
    addChildNodeToDisplayNode(myNode); //data
    addNodeToView(myNode); //view
}

//Method to add a single child node to the open node in this view and update parent node (data)

private void addChildNodeToDisplayNode(ClauseContainer myChildNode) {
        getDisplayNode().addChildNode(myChildNode);
        myChildNode.setParentNode(getDisplayNode());
}

/* Method to add node as child node of parent AND update/display all nodes */

private void newNodeAsChildNode(ClauseContainer myNode) {
    addChildNodeToDisplayNode(myNode); //data
    updateOpenNodeView(); //view
}

public void removeSpriteFromStage(SpriteBox thisSprite) {
    thisSprite.unsetParentNode(); //data
    //TO DO: remove Node (data) ? is it cleaned up by GUI object removal?
    this.spriteGroup.getChildren().remove(thisSprite); //view/GUI
    thisSprite.resetLocation(); //??
    getStage().show(); //refresh GUI
    
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
        mySprite.setTranslateX(spriteX);
        mySprite.setTranslateY(spriteY); 
        mySprite.setStageLocation(StageManager.this); //needed if stage is not o/w tracked
        if (mySprite.getStageLocation()!=StageManager.this) {
            System.out.println("Problem with adding sprite:"+mySprite.toString());
        }
        else {
            System.out.println("Positioned sprite at:"+mySprite.toString()+" ("+spriteX+","+spriteY+")");
        }
}

public void resetSpriteOrigin() {
    this.spriteY=0;
    this.spriteX=0;
}

//TO DO: Reset sprite positions when re-loading display.  To match a Grid Layout.
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

//SPECIFIC TEXT OUTPUT WINDOW OPTION

//Function to setup independent output window
//This is only called for the Stage_Output instance.
//TO DO: discard or put into StageManager constructor

public void setupTextOutputWindow() {

    putTextScrollerOnStage();
    setOutputText("Some future contents");
    hideStage();
    setPosition();
}


}